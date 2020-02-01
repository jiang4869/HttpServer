package httpserver;

/*
 *处理各种状态码
 */
public class DealStatusUtil {

    public static void do200(Request request, Response response) {
        response.setStatusCode(200);
        String suffixName = request.getRealUrl().substring(request.getRealUrl().lastIndexOf(".") + 1);
        response.pushToClient(suffixName, request.getRealUrl());
    }

    public static void do404(Request request, Response response) {
        response.setStatusCode(404);
        request.setRealUrl(System.getProperty("user.dir")+"\\wwwroot\\404\\index.html");
        response.pushToClient("html", request.getRealUrl());
    }

    public static void do302(Request request, Response response) {
        response.setStatusCode(302);
        response.setRedirectPath(request.getRedirectPath());
        response.pushToClient("html", null);
    }

    public static void do500(Response response) {
        response.println("Server ERROR");
        response.setStatusCode(500);
    }

}
