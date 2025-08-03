package com.student_online.IntakeSystem.utils.LoginUtils;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.http.HttpMethod;

import java.net.URISyntaxException;
import java.util.Map;

/**
 * 本科智慧教学平台
 */
public class BkzhjxLogin {
    public static final String GATE_WAY = "http://bkzhjx.wh.sdu.edu.cn/sso.jsp";

    public static String fetchBkzhjxCookie(String bkzhjxTicket) throws URISyntaxException {


        //--------------------ticket入口：http://bkzhjx.wh.sdu.edu.cn/sso.jsp?ticket=1 --------------------//

        System.out.println("开始进入本科智慧教学平台");

        HttpUtil.Response<String> redirectedResponse = HttpUtil.connect(bkzhjxTicket)
                .method(HttpMethod.GET)
                .config().followRedirects(false).set()
                .execute();


        //--------------------重定向：https://bkzhjx.wh.sdu.edu.cn/sso.jsp?ticket=1 --------------------//

        String redirectUrl = redirectedResponse.header("Location");
        System.out.println("重定向 URL: " + redirectUrl);

        assert redirectUrl != null;
        HttpUtil.Response<String> redirectedResponse2 = HttpUtil.connect(redirectUrl)
                .header("Cookie", "SERVERID=1;bzb_njw=1")   // 不知道为什么，这里一定要填cookie，但内容随意
                .method(HttpMethod.GET)
                .config().followRedirects(false).set()
                .execute();

        String SERVERID = redirectedResponse2.cookie("SERVERID");
        String bzb_njw = redirectedResponse2.cookie("bzb_njw");


        //--------------------重定向：http://bkzhjx.wh.sdu.edu.cn/sso.jsp --------------------//

        redirectUrl = redirectedResponse2.header("Location");
        System.out.println("重定向 URL: " + redirectUrl);

        assert redirectUrl != null;
        HttpUtil.Response<String> redirectedResponse3 = HttpUtil.connect(redirectUrl)
                .method(HttpMethod.GET)
                .config().followRedirects(false).set()
                .execute();


        //--------------------重定向：https://bkzhjx.wh.sdu.edu.cn/sso.jsp --------------------//

        redirectUrl = redirectedResponse3.header("Location");
        System.out.println("重定向 URL: " + redirectUrl);

        assert redirectUrl != null;
        assert SERVERID != null;
        assert bzb_njw != null;
        HttpUtil.Response<String> redirectedResponse4 = HttpUtil.connect(redirectUrl)
                .cookie("SERVERID", SERVERID)
                .cookie("bzb_njw", bzb_njw)
                .method(HttpMethod.GET)
                .config().followRedirects(false).set()
                .execute();


        //--------------------重定向：https://bkzhjx.wh.sdu.edu.cn/jsxsd/xk/LoginToXk?method=1&ticket1=2 --------------------//

        redirectUrl = redirectedResponse4.header("Location");
        System.out.println("重定向 URL: " + redirectUrl);

        assert redirectUrl != null;
        HttpUtil.Response<String> redirectedResponse5 = HttpUtil.connect(redirectUrl)
                .cookie("SERVERID", SERVERID)
                .cookie("bzb_njw", bzb_njw)
                .header("host", "bkzhjx.wh.sdu.edu.cn")
                .method(HttpMethod.GET)
                .config().followRedirects(false).set()
                .execute();

        String bzb_jsxsd = redirectedResponse5.cookie("bzb_jsxsd");

        return String.format("bzb_jsxsd=%s;SERVERID=%s;bzb_njw=%s", bzb_jsxsd, SERVERID, bzb_njw);
    }

    public static Map<String, String> getUserInfo(String cookie) throws URISyntaxException {
        HttpUtil.Response<String> res = HttpUtil.connect("https://bkzhjx.wh.sdu.edu.cn/jsxsd/grxx/xsxx")
                .cookies(cookie)
                .execute();

        Document document = res.parse();
        Elements trs = document.select("#xjkpTable tbody tr");

        // 注意若教务网站上学籍卡片结构变化，这里也要变化

        //<td align="center" style="border:0px solid black;vertical-align: top;" colspan="2">学号：XXXXXXXXXXXX</td>
        String casId = trs.get(2).child(4).text().substring(3);

        //<td align="center" style="border:1px solid black">&nbsp;XXX</td>
        String name = trs.get(3).child(1).text();

        //<td align="center" style="border:1px solid black">&nbsp;男</td>
        String gender = trs.get(3).child(3).text();

        //<td align="left" style="border:0px solid black;vertical-align: top;" colspan="2">院系：软件学院</td>
        String depart = trs.get(2).child(0).text().substring(3);

        //<td align="center" style="border:0px solid black;vertical-align: top;" colspan="2">专业：软件工程</td>
        String major = trs.get(2).child(1).text().substring(3);

        return Map.of("casId", casId, "name", name, "gender", gender, "depart", depart, "major",  major);
    }

}
