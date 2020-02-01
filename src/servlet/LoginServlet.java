package servlet;

import httpserver.DealStatusUtil;
import httpserver.Request;
import httpserver.Response;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class LoginServlet implements Servlet {
    private Map<String, String> users = new HashMap<>();

    @Override
    public void service(Request request, Response response) {
        getUsers();
        String userName = request.getParameter("username");
        String pwd = request.getParameter("pwd");
        String pwd0 = null;
        pwd0 = users.get(userName);
        String filePath;
        if (pwd0 != null && pwd0.equals(pwd)) {
            filePath = System.getProperty("user.dir")+"\\wwwroot\\servlettest\\home\\index.html";

        } else {
            filePath = System.getProperty("user.dir")+"\\wwwroot\\servlettest\\home\\error.html";
        }
        request.setRealUrl(filePath);
        String info;
        DealStatusUtil.do200(request, response);
    }

    /*
     *如果有配置数据库可以从数据库获取用户信息。
     *目前只做成用文件中获取信息
     */
    private void getUsers() {
        try {

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(System.getProperty("user.dir")+"\\src\\userInfo\\pwd.txt")));
            System.out.println(bufferedReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] strs = line.split(" ");
                String userName = strs[0];
                String pwd = strs[1];
                users.put(userName, pwd);
            }
        } catch (Exception e) {

        }
    }

}
