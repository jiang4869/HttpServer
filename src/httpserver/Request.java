package httpserver;

import com.sun.istack.internal.NotNull;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class Request {
    final private String CRLF = "\r\n";
    final private int SIZE = 1024 * 1024;
    private final String wwwRoot = System.getProperty("user.dir") + File.separator + "wwwroot";

    private String requestInfo;
    private String method;
    private String realUrl;
    private String url;
    private String host;

    private String paramInfo;
    private Map<String, ArrayList<String>> parameterMapValue;
    private InputStream inputStream;
    private int len;

    private boolean isRedirect;
    private String redirectPath;

    public Request() {
        requestInfo = "";
        method = "";
        url = null;
        realUrl = null;
        paramInfo = "";
        isRedirect = false;
        redirectPath = "";
        parameterMapValue = new HashMap<>();
    }

    public Request(InputStream inputStream) {
        this();
        this.inputStream = inputStream;
        receive();
    }


    public Request(Socket socket) {
        this();
        try {
            this.inputStream = socket.getInputStream();
            receive();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     *请求信息
     *
     */
    private void receive() {
        if (inputStream == null) {
            return;
        }
        byte[] datas = new byte[SIZE];
        try {
            len = inputStream.read(datas);
            if (len <= 0)
                return;
            requestInfo = new String(datas, 0, len);
            if (requestInfo == null || (requestInfo = requestInfo.trim()).equalsIgnoreCase("")) {
                return;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }



        String firstLine = requestInfo.substring(0, requestInfo.indexOf(CRLF)).trim();
        method = firstLine.substring(0, firstLine.indexOf("/")).trim();
        url = firstLine.substring(firstLine.indexOf("/") + 1, firstLine.indexOf("HTTP/")).trim();

        host = requestInfo.substring(requestInfo.indexOf("Host:") + "Host".length() + 2, requestInfo.indexOf(CRLF, requestInfo.indexOf("Host:")));

        String realResourcePath = "";
        if (method.equalsIgnoreCase("get")) {
            if (url.indexOf("?") != -1) {
                paramInfo = url.substring(url.indexOf("?") + 1).trim();
                realResourcePath = wwwRoot + File.separator + url.substring(0, url.indexOf("?")).trim();
                url = url.substring(0, url.indexOf("?"));
            } else {
                realResourcePath = wwwRoot + File.separator + url;
            }
        } else if (method.equalsIgnoreCase("post")) {
            paramInfo = requestInfo.substring(requestInfo.lastIndexOf(CRLF));
            realResourcePath = wwwRoot + File.separator + url;
        }
        String tmp = wwwRoot + File.separator;
        int index = realResourcePath.indexOf(tmp) + tmp.length();
        isRedirect = checkRedirect(realResourcePath.substring(index));
        if (isRedirect)
            return;

        checkResource(realResourcePath);

        paramInfo = paramInfo.trim();
        if (paramInfo == null || paramInfo.equalsIgnoreCase("")) {
            return;
        }
        parseParam(paramInfo);
    }

    /*
     *判断是否需要重定向
     *参数为requestInfo中resourcePath
     */
    private boolean checkRedirect(String resourcePath) {
        File file = new File(wwwRoot + File.separator + resourcePath);
        if (resourcePath.length() != 0 && file.isDirectory() && resourcePath.charAt((resourcePath.length() - 1)) != '/') {
            redirectPath = "http://" + host + "/" + resourcePath + "/";
            return true;
        }
        return false;
    }

    /*
     *
     *解析资源路径
     */
    private void checkResource(String realResourcePath) {
        realResourcePath = realResourcePath.replace("/", File.separator);
        realResourcePath = realResourcePath.replace("\\", "\\\\");
        File file = new File(realResourcePath);

        if (file.isDirectory()) {

            realUrl = new File(realResourcePath, "index.html").getAbsolutePath();
            realUrl = realUrl.replace("\\", "\\\\");

        } else if (file.isFile()) {
            realUrl = realResourcePath;
        } else {
            realUrl = null;
        }
    }

    /*
     *
     *解析参数
     */
    private void parseParam(String paramString) {
        if (paramString == null || (paramString = paramString.trim()).equalsIgnoreCase("")) {
            return;
        }
        StringTokenizer tokenizer = new StringTokenizer(paramString, "&");
        while (tokenizer.hasMoreTokens()) {
            String[] keyValue = tokenizer.nextToken().split("=");
            if (keyValue.length == 1) {
                keyValue = Arrays.copyOf(keyValue, 2);
                keyValue[1] = null;
            }
            String key = keyValue[0].trim();
            String value = null == keyValue[1] ? null : decode(keyValue[1].trim(), "gbk");
            if (parameterMapValue.get(key) == null) {
                parameterMapValue.put(key, new ArrayList<>());
            }
            ArrayList<String> list = parameterMapValue.get(key);
            list.add(value);
        }

    }

    /*
     *处理get中有中文问题
     */
    private String decode(String value, String code) {
        try {
            return java.net.URLDecoder.decode(value, code);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     *获得一组参数
     */
    public String[] getParameterValues(String name) {
        List<String> values = null;
        if ((values = parameterMapValue.get(name)) == null) {
            return null;
        } else {
            return values.toArray(new String[0]);
        }
    }

    /*
     *获得单个参数
     */
    public String getParameter(String name) {
        String[] values = getParameterValues(name);
        if (null == values) {
            return null;
        }
        return values[0];
    }

    public String getUrl() {
        return url;
    }

    public boolean isRedirect() {
        return isRedirect;
    }

    public void setRedirect(boolean redirect) {
        isRedirect = redirect;
    }

    public String getRedirectPath() {
        return redirectPath;
    }

    public void setRedirectPath(String redirectPath) {
        this.redirectPath = redirectPath;
    }

    public String getRealUrl() {
        return realUrl;
    }

    public void setRealUrl(String realUrl) {
        this.realUrl=realUrl;
    }
//    public Map<String, ArrayList<String>> getParameterMapValue() {
//        return parameterMapValue;
//    }

    public void close() throws Exception {
        inputStream.close();
    }
}
