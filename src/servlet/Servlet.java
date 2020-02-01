package servlet;

import httpserver.Request;
import httpserver.Response;

public interface Servlet {
    void service(Request request,Response response);
//     void doGet(Request request, Response response);
//
//     void doPost(Request request, Response response);

}
