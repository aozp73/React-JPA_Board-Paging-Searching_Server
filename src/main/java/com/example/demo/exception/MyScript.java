package com.example.demo.exception;

public class MyScript {
    public static String back(String msg) {
        StringBuffer sb = new StringBuffer();
        sb.append("<script>");
        sb.append("alert('" + msg + "');");
        sb.append("history.back();");
        sb.append("</script>");
        return sb.toString();
    }

    public static String href(String path) {
        StringBuffer sb = new StringBuffer();
        sb.append("<script>");
        sb.append("location.href = '" + path + "';");
        sb.append("</script>");
        return sb.toString();
    }

    public static String href(String msg, String path) {
        StringBuffer sb = new StringBuffer();
        sb.append("<script>");
        sb.append("alert('" + msg + "');");
        sb.append("location.href = '" + path + "';");
        sb.append("</script>");
        return sb.toString();
    }
}
