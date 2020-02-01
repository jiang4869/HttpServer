package httpserver;


import servlet.Servlet;
import servlet.WebApp;
import util.CloseUtil;

import java.io.*;
import java.net.Socket;

public class ServerFactory implements Runnable {
    private Socket client;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Request request;
    private Response response;
    private Servlet servlet;

    public ServerFactory(Socket client) {
        servlet = null;
        this.client = client;
        try {
            inputStream = this.client.getInputStream();
            outputStream = this.client.getOutputStream();
            request = new Request(inputStream);
            response = new Response(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run() {
        if (request.isRedirect()) {
            DealStatusUtil.do302(request,response);
            CloseUtil.close(client);
            return;
        }
        try {
            servlet = WebApp.getServlet(request.getUrl());
            servlet.service(request, response);
        } catch (Exception e) {
            DealStatusUtil.do404(request,response);
        }
        CloseUtil.close(client);

    }
}
