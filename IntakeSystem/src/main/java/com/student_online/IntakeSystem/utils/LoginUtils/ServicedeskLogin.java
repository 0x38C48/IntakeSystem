package com.student_online.IntakeSystem.utils.LoginUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.student_online.IntakeSystem.utils.LoginUtils.Des;
import com.student_online.IntakeSystem.utils.LoginUtils.HttpUtil;
import org.springframework.http.HttpMethod;

import java.net.URISyntaxException;
import java.util.Map;

/**
 * 数据中心
 */
public class ServicedeskLogin {
    public static final String GATE_WAY = "https://servicedesk.sdu.edu.cn/idc/index.jsp";

    public static String fetchServicedeskCookie(String servicedeskTicket) throws URISyntaxException {

        //--------------------ticket入口：https://servicedesk.sdu.edu.cn/idc/index.jsp?ticket=XXX --------------------//

        System.out.println("开始进入数据中心");

        HttpUtil.Response<String> response = HttpUtil.connect(servicedeskTicket)
                .method(HttpMethod.GET)
                .config().followRedirects(false).set()
                .execute();

        String JSESSIONID = response.cookie("JSESSIONID");
        String cookie_adx = response.cookie("cookie-adx");
        assert JSESSIONID != null && cookie_adx != null;
        return String.format("JSESSIONID=%s;cookie-adx=%s", JSESSIONID, cookie_adx);
    }

    public static Map<String, String> fetchStudentInfo(String cookie, String casId) throws URISyntaxException, JsonProcessingException {
        String BE_OPT_ID = Des.strEnc(casId, "tp", "des", "param");
        HttpUtil.Response<String> response = HttpUtil.connect("https://servicedesk.sdu.edu.cn/idc/sys/uacm/profile/getUserInfo")
                .method(HttpMethod.POST)
                .header("Content-Type","application/json;charset=UTF-8")
                .header("Accept","application/json")
                .cookies(cookie)
                .jsonBody("{\"BE_OPT_ID\":\"" + BE_OPT_ID +"\"}")
                .execute();

        System.out.println(response.body());

        return response.convert(new TypeReference<>() {});
    }

}
