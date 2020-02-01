package servlet;

import httpserver.DealStatusUtil;
import httpserver.Request;
import httpserver.Response;

import java.io.*;

public class DefaultServlet implements Servlet {
    @Override
    public void service(Request request, Response response) {
        String realUrl = request.getRealUrl();
        if (realUrl == null || realUrl.equalsIgnoreCase("")) {
            DealStatusUtil.do404(request, response);
            return;
        }
        File file = new File(realUrl);
        if (file.exists() == false) {
            DealStatusUtil.do404(request, response);
            return;
        }
        DealStatusUtil.do200(request, response);
    }
}
