package com.student_online.IntakeSystem.utils.LoginUtils;


import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.hash.Hashing;
import com.student_online.IntakeSystem.config.exception.CommonErr;
import com.student_online.IntakeSystem.config.exception.CommonErrException;
import lombok.Getter;
import org.apache.commons.codec.digest.DigestUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 统一认证登入爬虫 示例代码
 */

public class SduLogin {
    // 统一认证登入网页
    private static final String CAS_LOGIN_PAGE = "https://pass.sdu.edu.cn/cas/login";
    
    // 统一认证语言
    private static final String Language = "zh_CN";
    
    // 统一认证登入 设备验证components（该值可替换）
    public String components = "java-spider-for-sdu-components";
    // 统一认证登入 设备验证details
    public String details = "java-spider-for-sdu-details";
    
    private String murmur;
    private String murmur_s;
    private String murmur_md5;
    
    /**
     * 统一认证账号
     */
    public String sdu_id;
    
    /**
     * 统一认证密码
     */
    public String sdu_password;
    
    public String captcha;
    
    public String JSESSIONID;
    public String cookie_adx;
    
    public boolean checked = false;
    
    /**
     * 统一认证登入页面Cookie
     */
    @Getter
    Map<String, String> casLoginCookie = new HashMap<>();
    
    /**
     * 统一认证登入页面隐藏字段
     */
    private String lt;
    
    public void initFingerprint() {
        murmur = Hashing.murmur3_128(31).hashString(components, StandardCharsets.UTF_8).toString();
        murmur_s = Hashing.murmur3_128(31).hashString(details, StandardCharsets.UTF_8).toString();
        murmur_md5 = DigestUtils.md5Hex(details);
    }
    
    /**
     * 标记先前是否经过统一认证密码验证
     */
//    @Setter
//    private boolean checked = false;
    public SduLogin(String sdu_id, String sdu_password) {
        this.sdu_id = sdu_id;
        this.sdu_password = sdu_password;
    }
    
    /**
     * 进入统一认证登入页面提取信息
     * 打开浏览器后第一次登入统一认证页面时，该页面会返回一个JSESSIONID的cookie
     * 此时这个JSESSIONID还未经过设备验证绑定，因此我们无法使用它来进行登入校验
     * JSESSIONID的expire time被设置为“会话”，也就是说除非浏览器关闭，这个JSESSIONID就不会被删除。
     * 我们可以将JSESSIONID的生命周期设定为我们程序的生命周期，也可以将JSESSIONID持久化到redis以供它在多个程序的生命周期间使用
     * 但无法永远使用它，因为JSESSIONID在统一认证后台保存超时后会被自动删除，这也是我们应该保持JSESSIONID的时间上限
     * 现在尚未测试过一个JSESSIONID能在统一认证后台保存多久，需要进一步测试
     */
    private void enterCasLoginPage(String casLoginURL) throws URISyntaxException {
        
        // 获取登录页面，提取隐藏字段和统一认证界面cookie
        HttpUtil.Response<String> loginFormResponse = HttpUtil.connect(casLoginURL)
                .method(HttpMethod.GET)
                .execute();
        
        // 获取页面会话（可以保持一段时间）
        if (JSESSIONID == null) {
            System.out.println(loginFormResponse.cookies());
            JSESSIONID = loginFormResponse.cookie("JSESSIONID");
            cookie_adx = loginFormResponse.cookie("cookie-adx");
        }
        proCookie();
        
        // 提取页面隐藏字段
        lt = loginFormResponse.parse().select("input[name=lt]").attr("value");
    }
    
    public void proCookie() {
        casLoginCookie.put("JSESSIONID", JSESSIONID);
        casLoginCookie.put("cookie-adx", cookie_adx);
        casLoginCookie.put("Language", Language);
    }
    
    /**
     * 设备验证
     * 设备验证和JSESSIONID绑定，带上JSESSIONID经过设备验证后，JSESSIONID会被统一认证登入标记为已经验证过
     * 只有已经验证过的JSESSIONID才能参与登入接口
     */
    private String deviceCheck() throws URISyntaxException {
        String u = Des.strEnc(sdu_id, "1", "2", "3");
        String p = Des.strEnc(sdu_password, "1", "2", "3");
        // 发送设备验证请求
        HttpUtil.Response<Map<String, String>> deviceResponse = HttpUtil.connect("https://pass.sdu.edu.cn/cas/device")
                .cookies(casLoginCookie)
                .formData()
                .data("d", murmur)
                .data("d_s", murmur_s)
                .data("d_md5", murmur_md5)
                .data("m", "1")
                .data("u", u)
                .data("p", p)
                .set()
                .method(HttpMethod.POST)
                .execute(new TypeReference<>() {
                });
        
        // 解析设备验证结果
        Map<String, String> deviceInfo = deviceResponse.body();
//        System.out.println(deviceInfo.get("info"));
        
        switch (deviceInfo.get("info")) {
            case "binded", "pass" -> {
//                System.out.println("设备验证通过，准备登入");
            }
            case "bind" -> {
                if (captcha == null) {
                    
                    HttpUtil.Response<String> response = HttpUtil.connect("https://pass.sdu.edu.cn/cas/device")
                            .cookies(casLoginCookie)
                            .formData().data("m", "2").set()
                            .method(HttpMethod.POST)
                            .execute();
                    System.out.println(response.body());
                    return "need captcha";
                }
                twiceDeviceCheck();
            }
            case "validErr", "notFound" -> {
//                System.out.println("密码错误或用户不存在");
                throw new CommonErrException(CommonErr.USER_WRONG);
            }
            case "mobileErr" -> {
//                System.out.println("未绑定手机");
                throw new CommonErrException(CommonErr.PARAM_WRONG);
            }
        }
        return null;
    }
    
    /**
     * 二次设备验证
     * 对于陌生的components以及details，统一认证登入平台将要求用户进行二次验证
     * 经过二次验证后，如果选择信任设备，components以及details将被记录到学校后台
     * 于是下次使用components以及details进行设备验证时，统一认证登入平台会先和数据库中的components以及details比较，如果数据库中存在，则无需二次验证
     * 注意components以及details的存储是有上限的，超出这个上限就会自动清除最早记录的components以及details数据
     */
    public void twiceDeviceCheck() throws URISyntaxException {
//        String c = (new Scanner(System.in)).next();
//        System.out.println("是否信任该设备？(true or false)");
//        boolean s = (new Scanner(System.in)).nextBoolean();
        
        // 进行二次设备验证
        HttpUtil.Response<Map<String, String>> checkResponse = HttpUtil.connect("https://pass.sdu.edu.cn/cas/device")
                .cookies(casLoginCookie)
                .formData()
                .data("d", murmur_s)
                .data("i", details)
                .data("m", "3")
                .data("u", sdu_id)
                .data("c", captcha)
                .data("s", "0")
                .set()
                .method(HttpMethod.POST)
                .execute(new TypeReference<>() {
                });
        
        Map<String, String> checkInfo = checkResponse.body();
//        System.out.println(checkInfo.get("info"));
        
        switch (checkInfo.get("info")) {
            case "ok" -> {
//                System.out.println("已授权!");
            }
            case "most" -> {
//                System.out.println("设备已经超过最大数量，已自动解除最早一台设备。");
            }
            case "codeErr" -> {
//                System.out.println("验证码有误");
                throw new CommonErrException(CommonErr.CAPTCHA_WRONG);
            }
            case "timeout" -> {
//                System.out.println("验证码超时");
                throw new RuntimeException();
            }
            default -> {
//                System.out.println("验证失败");
                throw new RuntimeException();
            }
        }
        
    }
    
    /**
     * 密码验证
     * 通过密码验证后，统一认证登入页面会返回一个名为CASTGC的cookie，该cookie的expire time也是“会话”。
     * 只要持有CASTGC以及对应的JSESSIONID，那么下次登入就无需使用用户名密码
     * 注意，一个JSESSIONID经过用户名密码验证过后就无法再使用用户名密码登入！若后续还想用该JSESSIONID，则必须使用CASTGC验证登入！
     */
    public String passwordVerification(String casLoginURL) throws URISyntaxException {
        // 构建表单数据
        Map<String, String> formData = Map.of(
                "rsa", Des.strEnc(sdu_id + sdu_password + lt, "1", "2", "3"),
                "ul", String.valueOf(sdu_id.length()),
                "pl", String.valueOf(sdu_password.length()),
                "lt", lt,
                "execution", "e1s1",
                "_eventId", "submit");
        
        // 提交表单
        HttpUtil.Response<String> loginResponse = HttpUtil.connect(casLoginURL)
                .method(HttpMethod.POST)
                .formData(formData)
                .cookies(casLoginCookie)
                .config().followRedirects(false).set()
                .execute();
        
        // 检查登录是否成功
        if (loginResponse.statusCode() != 302) {
            if (loginResponse.statusCode() == 200) {
//                System.out.println("登录失败！");
                Document loggedInPage = loginResponse.parse();
                Element element = loggedInPage.getElementById("errormsg");
//                System.out.println(element);
            } else {
//                System.out.println("登录失败，状态码：" + loginResponse.statusCode());
            }
            throw new RuntimeException();
        }
        
        // 提取用于下次无需用户名密码登入的CASTGC
        casLoginCookie.put("CASTGC", loginResponse.cookie("CASTGC"));
        
        // 标记为验证过
//        checked = true;
        
        return loginResponse.location();
    }
    
    /**
     * CASTGC验证
     * 使用CASTGC省去了用户名密码。经过密码验证的JSESSIONID后续再登入统一认证平台都必须使用此接口
     */
    public String casTgcVerification(String casLoginURL) throws URISyntaxException {
        // 验证登入
        HttpUtil.Response<String> loginResponse = HttpUtil.connect(casLoginURL)
                .method(HttpMethod.POST)
                .cookies(casLoginCookie)
                .config().followRedirects(false).set()
                .execute();
        
        // 检查登录是否成功
        if (loginResponse.statusCode() != 302) {
            // 标记为未验证过
//            checked = false;
            
            if (loginResponse.statusCode() == 200) {
//                System.out.println("使用CASTGC验证失败，尝试使用用户名密码重新登入");
                return null;
            } else {
//                System.out.println("登录失败，状态码：" + loginResponse.statusCode());
                return null;
            }
        }
        
        return loginResponse.location();
    }
    
    
    /**
     * 统一认证登入
     */
    public String login(String loginService) throws IOException, URISyntaxException {
        
        String casLoginURL = CAS_LOGIN_PAGE + "?service=" + URLEncoder.encode(loginService, StandardCharsets.UTF_8);
        
        String redirectUrl = null;
        
        
        if (checked) {
//            System.out.println("执行CASTGC验证...");
            redirectUrl = casTgcVerification(casLoginURL);
        }
        
        if (!checked) {
            System.out.println("进入统一认证登入页面提取信息...");
            enterCasLoginPage(casLoginURL);
//        System.out.println("执行设备验证...");
            String result = deviceCheck();
            if (Objects.equals(result, "need captcha")) {
                return result;
            }
//            System.out.println("进行用户名密码验证...");
            redirectUrl = passwordVerification(casLoginURL);
            checked = true;
        }

//        System.out.println("统一认证登入验证通过！");
//        System.out.println("统一认证登入重定向 URL: " + redirectUrl);
        return redirectUrl;
    }
    
}
