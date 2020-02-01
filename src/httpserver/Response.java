package httpserver;

import java.io.*;
import java.net.Socket;
import java.util.Date;

public class Response {
    private String CRLF = "\r\n";
    private String BLANK = " ";
    private int statusCode;
    private int len;
    private Date date;
    //流
    private PrintStream printStream;
    //正文输入流
    private FileInputStream fis = null;
    //存储头信息
    private StringBuilder responseInfo;

    private String redirectPath;


    public Response() {
        statusCode = 200;

        responseInfo = new StringBuilder();
        len = 0;
    }

    public Response(OutputStream outputStream) {
        this();
        this.printStream = new PrintStream(outputStream);

    }

    public Response(Socket client) {
        this();
        try {
            printStream = new PrintStream(client.getOutputStream());
        } catch (IOException e) {
            statusCode = 404;
            responseInfo = null;

        }
    }


    public Response println(String info) {
        printStream.print(info);
        len = len + info.getBytes().length;
        len = len + CRLF.getBytes().length;
        return this;
    }

    public void createHeadInfo(String contentType) {
        responseInfo.append("HTTP/1.1").append(BLANK).append(statusCode).append(BLANK);
        switch (statusCode) {
            case 200:
                responseInfo.append("OK").append(CRLF);
                break;
            case 404:
                responseInfo.append("NOT FOUND").append(CRLF);
                break;
            case 505:
                responseInfo.append("HTTP Version not supported").append(CRLF);
                break;
            case 302:
                responseInfo.append("Found").append(CRLF);
                break;
            case 500:
                responseInfo.append("Internal Server Error").append(CRLF);
        }

        date = new Date();
        responseInfo.append("Date: ").append(date).append(CRLF);
        responseInfo.append("Content-Type:").append(contentType).append(CRLF);
     //   responseInfo.append("Content-Length:").append(len).append(CRLF);
        responseInfo.append("Transfer-Encoding:").append("chunked").append(CRLF);
        if (statusCode == 302) {
            System.out.println("The redirectPath:" + redirectPath);
            responseInfo.append("Location: ").append(redirectPath).append(CRLF);
        }

    }

    /*
     *获取响应内容类型
     */
    private String getContentType(String suffixName) {
        String contentType = "text/html";

        switch (suffixName) {
            case "html":
            case "htm":
            case "shm":
                contentType = "text/html";
                break;
            case "css":
                contentType = "text/css";
                break;
            case "js":
                contentType = "application/x-javascript";
                break;
            case "png":
                contentType = "image/png";
                break;
            case "bmp":
                contentType = "image/bmp";
                break;
            case "jpg":
            case "jpeg":
                contentType = "image/jpeg";
                break;
            case "gif":
                contentType = "image/gif";
                break;
            default:
                contentType = "text/plain";// 其他文件都当成纯文本文件处理
                break;
        }
        return contentType;

    }

    /*
     *把响应内容输出到输出流
     */
    private void getContent(FileInputStream fis) {

        String info;
        try {
            int length = -1;
            String size="";
            byte[] buffer = new byte[1024];
            while ((length = fis.read(buffer)) > 0) {
                size=Integer.toHexString(length);
                printStream.write((size+CRLF).getBytes());
                printStream.write(buffer, 0, length);
                printStream.write(CRLF.getBytes());
                printStream.flush();
            }
            size=Integer.toHexString(0);
            printStream.write((size+CRLF+CRLF).getBytes());
            printStream.flush();
        } catch (Exception e) {


            e.printStackTrace();
        }
    }

    public void pushToClient(String suffixName, String realPath) {
        try {
            if(realPath!=null)
            fis = new FileInputStream(realPath);
            if (fis != null) {
                len = fis.available();
            }
        } catch (Exception e) {
            if (statusCode != 302)
                DealStatusUtil.do500(this);
        }
        String contentType = getContentType(suffixName);
        createHeadInfo(contentType);
        try {
            printStream.print(responseInfo.toString());
            printStream.print(CRLF);
            if (fis != null)
                getContent(fis);
            printStream.flush();
            if (printStream != null)
                printStream.close();
        } catch (Exception e) {

        }

    }

    public StringBuilder getResponseInfo() {
        return responseInfo;
    }

    /*
     *用于调试输出
     */
    private String getThreadName() {
        return Thread.currentThread().getName();
    }

    public void setRedirectPath(String redirectPath) {
        this.redirectPath = redirectPath;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
