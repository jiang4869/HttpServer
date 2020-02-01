package servlet;

import httpserver.DealStatusUtil;
import httpserver.Request;
import httpserver.Response;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class RegisterServlet implements Servlet {
    private Set<String> users = new HashSet<>();

    @Override
    public void service(Request request, Response response) {
        FileWriter fw = null;
        String filePath;
        getUser();
        try {
            File f = new File(System.getProperty("user.dir")+"\\src\\userInfo\\pwd.txt");
            fw = new FileWriter(f, true);
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(fw);
        if (users.contains(request.getParameter("username")) == false&&request.getParameter("username")!=null&&request.getParameter("pwd")!=null) {
            filePath = System.getProperty("user.dir")+"\\wwwroot\\servlettest\\home\\register.html";
            pw.println(request.getParameter("username") + " " + request.getParameter("pwd"));
            pw.flush();
        } else {
            filePath = System.getProperty("user.dir")+"\\wwwroot\\servlettest\\home\\regError.html";
        }
        try {
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
        request.setRealUrl(filePath);
        DealStatusUtil.do200(request, response);
    }

    /*
     *如果有配置数据库可以从数据库获取用户信息。
     *目前只做成用文件中获取信息
     */
    public void getUser() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(System.getProperty("user.dir")+"\\src\\userInfo\\pwd.txt")));
            System.out.println(bufferedReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {

                String[] strs = line.split(" ");
                String userName = strs[0];
                users.add(userName);
            }
        } catch (Exception e) {

        }
    }

}
