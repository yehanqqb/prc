package prc.service.common.utils;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import sun.misc.BASE64Encoder;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @version: 1.0
 */
public class HttpRequestUtil {
    public static String imgToBase64(String url, Map<String, String> headerMap, Proxy proxy) {
        InputStream in = null;
        HttpsURLConnection connection = null;
        ByteArrayOutputStream outputStream = null;

        try {
            URL realUrl = new URL(url);
            if (proxy == null) {
                connection = (HttpsURLConnection) realUrl.openConnection();
            } else {
                connection = (HttpsURLConnection) realUrl.openConnection(proxy);
            }
            try {
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.setRequestMethod("POST");
                SSLContext ctx = MyX509TrustManagerUtils();
                connection.setSSLSocketFactory(ctx.getSocketFactory());
                connection.setHostnameVerifier(new HostnameVerifier() {
                    //在握手期间，如果 URL 的主机名和服务器的标识主机名不匹配，则验证机制可以回调此接口的实现程序来确定是否应该允许此连接。
                    @Override
                    public boolean verify(String arg0, SSLSession arg1) {
                        return true;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                connection = (HttpsURLConnection) realUrl.openConnection();
            }

            connection.setInstanceFollowRedirects(false);
            for (String key : headerMap.keySet()) {
                connection.setRequestProperty(key, headerMap.get(key));
            }
            PrintWriter out = new PrintWriter(connection.getOutputStream());
            // flush输出流的缓冲
            out.flush();
            connection.setConnectTimeout(10 * 1000);
            connection.setReadTimeout(10 * 1000);
            // 定义BufferedReader输入流来读取URL的响应
            in = connection.getInputStream();
            outputStream = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int len = 0;
            while ((len = in.read(b)) != -1) {
                outputStream.write(b, 0, len);
            }
            BASE64Encoder base = new BASE64Encoder();
            return base.encode(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static String sendHttpGet(String url, String agent) {
        String result = "";
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            if (StringUtils.isNotEmpty(agent)) {
                connection.setRequestProperty("user-agent", agent);
            } else {
                connection.setRequestProperty("user-agent",
                        "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1");
            }

            connection.setConnectTimeout(1000 * 20);
            connection.setReadTimeout(1000 * 20);
            // 建立实际的连接
            connection.connect();
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    public static Map<String, List<String>> sendHttpNoRedirectHeader(String url, Map<String, String> headerMap, Proxy proxy) {
        BufferedReader in = null;
        HttpURLConnection connection = null;
        try {
            URL realUrl = new URL(url);
            if (proxy == null) {
                connection = (HttpURLConnection) realUrl.openConnection();
            } else {
                try {
                    connection = (HttpURLConnection) realUrl.openConnection(proxy);
                } catch (Exception e) {
                    e.printStackTrace();
                    connection = (HttpURLConnection) realUrl.openConnection();
                }
            }
            connection.setInstanceFollowRedirects(false);
            connection.setRequestProperty("Proxy-Connection", "keep-alive");
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            for (String key : headerMap.keySet()) {
                connection.setRequestProperty(key, headerMap.get(key));
            }
            connection.setConnectTimeout(10 * 1000);
            connection.setReadTimeout(10 * 1000);
            connection.connect();

            return connection.getHeaderFields();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static Map<String, List<String>> sendHttpsNoRedirectHeader(String url, Map<String, String> headerMap, Proxy proxy) {
        BufferedReader in = null;
        HttpsURLConnection connection = null;
        try {
            URL realUrl = new URL(url);
            if (proxy == null) {
                connection = (HttpsURLConnection) realUrl.openConnection();
            } else {
                connection = (HttpsURLConnection) realUrl.openConnection(proxy);
            }
            try {
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.setRequestMethod("GET");
                SSLContext ctx = MyX509TrustManagerUtils();
                connection.setSSLSocketFactory(ctx.getSocketFactory());
                connection.setHostnameVerifier(new HostnameVerifier() {
                    //在握手期间，如果 URL 的主机名和服务器的标识主机名不匹配，则验证机制可以回调此接口的实现程序来确定是否应该允许此连接。
                    @Override
                    public boolean verify(String arg0, SSLSession arg1) {
                        return true;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                connection = (HttpsURLConnection) realUrl.openConnection();
            }
            connection.setInstanceFollowRedirects(false);
            connection.setRequestProperty("Proxy-Connection", "keep-alive");
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            for (String key : headerMap.keySet()) {
                connection.setRequestProperty(key, headerMap.get(key));
            }
            connection.setConnectTimeout(10 * 1000);
            connection.setReadTimeout(10 * 1000);
            connection.connect();
            return connection.getHeaderFields();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static JSONObject sendHttpsNoRedirectCookie(String url, Map<String, String> headerMap, Proxy proxy, String body) {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        JSONObject ret = new JSONObject();
        try {
            URL realUrl = new URL(url);
            HttpsURLConnection connection = null;
            if (proxy == null) {
                connection = (HttpsURLConnection) realUrl.openConnection();
            } else {
                connection = (HttpsURLConnection) realUrl.openConnection(proxy);
            }
            try {
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.setRequestMethod("GET");
                SSLContext ctx = MyX509TrustManagerUtils();
                connection.setSSLSocketFactory(ctx.getSocketFactory());
                connection.setHostnameVerifier(new HostnameVerifier() {
                    //在握手期间，如果 URL 的主机名和服务器的标识主机名不匹配，则验证机制可以回调此接口的实现程序来确定是否应该允许此连接。
                    @Override
                    public boolean verify(String arg0, SSLSession arg1) {
                        return true;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                connection = (HttpsURLConnection) realUrl.openConnection();
            }

            connection.setInstanceFollowRedirects(false);
            connection.setRequestProperty("Proxy-Connection", "keep-alive");
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            for (String key : headerMap.keySet()) {
                connection.setRequestProperty(key, headerMap.get(key));
            }

            PrintWriter out = new PrintWriter(connection.getOutputStream());
            // 发送请求参数
            out.print(body);
            // flush输出流的缓冲
            out.flush();
            connection.setConnectTimeout(10 * 1000);
            connection.setReadTimeout(10 * 1000);
            connection.connect();
            result.append(connection.getHeaderField("Location"));
            String cookieVal = "";
            for (int i = 0; i < connection.getHeaderFields().get("Set-Cookie").size(); i++) {
                cookieVal += connection.getHeaderFields().get("Set-Cookie").get(i);
            }
            cookieVal = cookieVal.replaceAll("path=//;", "");
            cookieVal = cookieVal.replaceAll("secure;", "");
            cookieVal = cookieVal.replaceAll("Domain=.alipay.com;", "");
            cookieVal = cookieVal.replaceAll("Secure;", "");
            cookieVal = cookieVal.replaceAll("SameSite=None", "");
            cookieVal = cookieVal.replaceAll("HttpOnly", "");
            cookieVal = cookieVal.replaceAll("path=/;", "");
            cookieVal = cookieVal.replaceAll("Path=/;", "");
            cookieVal = cookieVal.replaceAll("Path=;", "");
            cookieVal = cookieVal.replaceAll("Path=/", "");
            ret.put("cookie", cookieVal);
            ret.put("url", result.toString());
            String[] s = cookieVal.split(";");
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < s.length - 1; i++) {
                String[] w = s[i].split("=");
                map.put(w[0], w[1]);
            }
            ret.put("map", map);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return ret;
    }

    public static String sendHttpBody(String url, Map<String, String> headerMap, Proxy proxy) {
        BufferedReader in = null;
        HttpURLConnection connection = null;
        try {
            URL realUrl = new URL(url);
            if (proxy == null) {
                connection = (HttpURLConnection) realUrl.openConnection();
            } else {
                connection = (HttpURLConnection) realUrl.openConnection(proxy);
            }
            try {
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestMethod("GET");
                connection.setUseCaches(false);
                SSLContext ctx = MyX509TrustManagerUtils();
            } catch (Exception e) {
                e.printStackTrace();
                connection = (HttpsURLConnection) realUrl.openConnection();
            }
            connection.setInstanceFollowRedirects(false);
            connection.setRequestProperty("Proxy-Connection", "keep-alive");
            connection.setRequestProperty("connection", "Keep-Alive");
            for (String key : headerMap.keySet()) {
                connection.setRequestProperty(key, headerMap.get(key));
            }
            connection.setConnectTimeout(10 * 1000);
            connection.setReadTimeout(10 * 1000);
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK
                    || connection.getResponseCode() == HttpURLConnection.HTTP_CREATED
                    || connection.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED) {
                in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            } else {
                in = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "UTF-8"));
            }
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static String sendHttpsBody(String url, Map<String, String> headerMap, Proxy proxy) {
        BufferedReader in = null;
        HttpsURLConnection connection = null;
        try {
            URL realUrl = new URL(url);
            if (proxy == null) {
                connection = (HttpsURLConnection) realUrl.openConnection();
            } else {
                connection = (HttpsURLConnection) realUrl.openConnection(proxy);
            }
            try {
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestMethod("GET");
                connection.setUseCaches(false);
                SSLContext ctx = MyX509TrustManagerUtils();
                connection.setSSLSocketFactory(ctx.getSocketFactory());
                connection.setHostnameVerifier(new HostnameVerifier() {
                    //在握手期间，如果 URL 的主机名和服务器的标识主机名不匹配，则验证机制可以回调此接口的实现程序来确定是否应该允许此连接。
                    @Override
                    public boolean verify(String arg0, SSLSession arg1) {
                        return true;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                connection = (HttpsURLConnection) realUrl.openConnection();
            }
            connection.setInstanceFollowRedirects(false);
            connection.setRequestProperty("Proxy-Connection", "keep-alive");
            connection.setRequestProperty("connection", "Keep-Alive");
            for (String key : headerMap.keySet()) {
                connection.setRequestProperty(key, headerMap.get(key));
            }
            connection.setConnectTimeout(15 * 1000);
            connection.setReadTimeout(15 * 1000);
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK
                    || connection.getResponseCode() == HttpURLConnection.HTTP_CREATED
                    || connection.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED) {
                in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            } else {
                in = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "UTF-8"));
            }
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static String sendHttpPostBodys(String url, Map<String, String> headerMap, Proxy proxy, String body) {
        BufferedReader in = null;
        HttpURLConnection connection = null;
        try {
            URL realUrl = new URL(url);
            if (proxy == null) {
                connection = (HttpURLConnection) realUrl.openConnection();
            } else {
                connection = (HttpURLConnection) realUrl.openConnection(proxy);
            }
            try {
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.setRequestMethod("POST");
            } catch (Exception e) {
                e.printStackTrace();
                connection = (HttpsURLConnection) realUrl.openConnection();
            }

            connection.setInstanceFollowRedirects(false);
            for (String key : headerMap.keySet()) {
                connection.setRequestProperty(key, headerMap.get(key));
            }
            PrintWriter out = new PrintWriter(connection.getOutputStream());
            // 发送请求参数
            out.print(body);
            // flush输出流的缓冲
            out.flush();
            connection.setConnectTimeout(10 * 1000);
            connection.setReadTimeout(10 * 1000);
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static Map<String, String> sendHttpsBodyAndCookie(String url, Map<String, String> headerMap, Proxy proxy, String body) {
        BufferedReader in = null;
        HttpsURLConnection connection = null;
        try {
            URL realUrl = new URL(url);
            if (proxy == null) {
                connection = (HttpsURLConnection) realUrl.openConnection();
            } else {
                connection = (HttpsURLConnection) realUrl.openConnection(proxy);
            }
            try {
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestMethod("GET");
                connection.setUseCaches(false);
                SSLContext ctx = MyX509TrustManagerUtils();
                connection.setSSLSocketFactory(ctx.getSocketFactory());
                connection.setHostnameVerifier(new HostnameVerifier() {
                    //在握手期间，如果 URL 的主机名和服务器的标识主机名不匹配，则验证机制可以回调此接口的实现程序来确定是否应该允许此连接。
                    @Override
                    public boolean verify(String arg0, SSLSession arg1) {
                        return true;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                connection = (HttpsURLConnection) realUrl.openConnection();
            }
            connection.setInstanceFollowRedirects(false);
            connection.setRequestProperty("Proxy-Connection", "keep-alive");
            connection.setRequestProperty("connection", "Keep-Alive");
            for (String key : headerMap.keySet()) {
                connection.setRequestProperty(key, headerMap.get(key));
            }

            PrintWriter out = new PrintWriter(connection.getOutputStream());
            // 发送请求参数
            out.print(body);
            // flush输出流的缓冲
            out.flush();
            connection.setConnectTimeout(30 * 1000);
            connection.setReadTimeout(30 * 1000);
            Map<String, String> ret = new HashMap<>();
            try {
                connection.connect();
            } catch (Exception e) {
                ret.put("code", "1");
                return ret;
            }
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK
                    || connection.getResponseCode() == HttpURLConnection.HTTP_CREATED
                    || connection.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED) {
                in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            } else {
                in = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "UTF-8"));
            }
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            System.out.println(result.toString());

            String cookieVal = "";
            for (int i = 0; i < connection.getHeaderFields().get("Set-Cookie").size(); i++) {
                cookieVal += connection.getHeaderFields().get("Set-Cookie").get(i);
            }
            cookieVal = cookieVal.replaceAll("path=//;", "");
            cookieVal = cookieVal.replaceAll("secure;", "");
            cookieVal = cookieVal.replaceAll("Domain=.alipay.com;", "");
            cookieVal = cookieVal.replaceAll("Secure;", "");
            cookieVal = cookieVal.replaceAll("SameSite=None", "");
            cookieVal = cookieVal.replaceAll("HttpOnly", "");
            cookieVal = cookieVal.replaceAll("path=/;", "");
            cookieVal = cookieVal.replaceAll("Path=/;", "");
            cookieVal = cookieVal.replaceAll("Path=;", "");
            cookieVal = cookieVal.replaceAll("Path=/", "");

            ret.put("body", result.toString());
            ret.put("code", "0");
            ret.put("cookie", cookieVal);
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> ret = new HashMap<>();
            ret.put("code", "2");
            return ret;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static Map<String, String> sendHttpsBodyAndRest(String url, Map<String, String> headerMap, Proxy proxy) {
        BufferedReader in = null;
        HttpsURLConnection connection = null;
        try {
            URL realUrl = new URL(url);
            if (proxy == null) {
                connection = (HttpsURLConnection) realUrl.openConnection();
            } else {
                connection = (HttpsURLConnection) realUrl.openConnection(proxy);
            }
            try {
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestMethod("GET");
                connection.setUseCaches(false);
                SSLContext ctx = MyX509TrustManagerUtils();
                connection.setSSLSocketFactory(ctx.getSocketFactory());
                connection.setHostnameVerifier(new HostnameVerifier() {
                    //在握手期间，如果 URL 的主机名和服务器的标识主机名不匹配，则验证机制可以回调此接口的实现程序来确定是否应该允许此连接。
                    @Override
                    public boolean verify(String arg0, SSLSession arg1) {
                        return true;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                connection = (HttpsURLConnection) realUrl.openConnection();
            }
            connection.setInstanceFollowRedirects(false);
            connection.setRequestProperty("Proxy-Connection", "keep-alive");
            connection.setRequestProperty("connection", "Keep-Alive");
            for (String key : headerMap.keySet()) {
                connection.setRequestProperty(key, headerMap.get(key));
            }
            connection.setConnectTimeout(10 * 1000);
            connection.setReadTimeout(10 * 1000);
            Map<String, String> ret = new HashMap<>();
            try {
                connection.connect();
            } catch (Exception e) {
                ret.put("code", "1");
                return ret;
            }
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK
                    || connection.getResponseCode() == HttpURLConnection.HTTP_CREATED
                    || connection.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED) {
                in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            } else {
                in = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "UTF-8"));
            }
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            ret.put("body", result.toString());
            ret.put("code", "0");
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> ret = new HashMap<>();
            ret.put("code", "2");
            return ret;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static Map<String, List<String>> sendHttpsPostBody(String url, Map<String, String> headerMap, Proxy proxy) {
        BufferedReader in = null;
        HttpsURLConnection connection = null;
        try {
            URL realUrl = new URL(url);
            if (proxy == null) {
                connection = (HttpsURLConnection) realUrl.openConnection();
            } else {
                connection = (HttpsURLConnection) realUrl.openConnection(proxy);
            }
            try {

                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.setRequestMethod("POST");
                SSLContext ctx = MyX509TrustManagerUtils();
                connection.setSSLSocketFactory(ctx.getSocketFactory());
                connection.setHostnameVerifier(new HostnameVerifier() {
                    //在握手期间，如果 URL 的主机名和服务器的标识主机名不匹配，则验证机制可以回调此接口的实现程序来确定是否应该允许此连接。
                    @Override
                    public boolean verify(String arg0, SSLSession arg1) {
                        return true;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                connection = (HttpsURLConnection) realUrl.openConnection();
            }
            connection.setInstanceFollowRedirects(false);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Proxy-Connection", "keep-alive");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("accept-language", "zh-CN,zh;q=0.9");
            for (String key : headerMap.keySet()) {
                connection.setRequestProperty(key, headerMap.get(key));
            }
            connection.setConnectTimeout(10 * 1000);
            connection.setReadTimeout(10 * 1000);
            connection.connect();
            return connection.getHeaderFields();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static String sendHttpsPostBodys(String url, Map<String, String> headerMap, Proxy proxy, String body) {
        BufferedReader in = null;
        HttpsURLConnection connection = null;
        try {
            URL realUrl = new URL(url);
            if (proxy == null) {
                connection = (HttpsURLConnection) realUrl.openConnection();
            } else {
                connection = (HttpsURLConnection) realUrl.openConnection(proxy);
            }
            try {
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.setRequestMethod("POST");
                SSLContext ctx = MyX509TrustManagerUtils();
                connection.setSSLSocketFactory(ctx.getSocketFactory());
                connection.setHostnameVerifier(new HostnameVerifier() {
                    //在握手期间，如果 URL 的主机名和服务器的标识主机名不匹配，则验证机制可以回调此接口的实现程序来确定是否应该允许此连接。
                    @Override
                    public boolean verify(String arg0, SSLSession arg1) {
                        return true;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                connection = (HttpsURLConnection) realUrl.openConnection();
            }

            connection.setInstanceFollowRedirects(false);
            connection.setRequestProperty("Proxy-Connection", "keep-alive");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("accept-language", "zh-CN,zh;q=0.9");
            for (String key : headerMap.keySet()) {
                connection.setRequestProperty(key, headerMap.get(key));
            }
            PrintWriter out = new PrintWriter(connection.getOutputStream());
            // 发送请求参数
            out.print(body);
            // flush输出流的缓冲
            out.flush();
            connection.setConnectTimeout(10 * 1000);
            connection.setReadTimeout(10 * 1000);
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static String sendHttpsPostBodysNoHeader(String url, Map<String, String> headerMap, Proxy proxy, String body) {
        BufferedReader in = null;
        HttpsURLConnection connection = null;
        try {
            URL realUrl = new URL(url);
            if (proxy == null) {
                connection = (HttpsURLConnection) realUrl.openConnection();
            } else {
                connection = (HttpsURLConnection) realUrl.openConnection(proxy);
            }
            try {
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.setRequestMethod("POST");
                SSLContext ctx = MyX509TrustManagerUtils();
                connection.setSSLSocketFactory(ctx.getSocketFactory());
                connection.setHostnameVerifier(new HostnameVerifier() {
                    //在握手期间，如果 URL 的主机名和服务器的标识主机名不匹配，则验证机制可以回调此接口的实现程序来确定是否应该允许此连接。
                    @Override
                    public boolean verify(String arg0, SSLSession arg1) {
                        return true;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                connection = (HttpsURLConnection) realUrl.openConnection();
            }

            connection.setInstanceFollowRedirects(false);

            for (String key : headerMap.keySet()) {
                connection.setRequestProperty(key, headerMap.get(key));
            }
            PrintWriter out = new PrintWriter(connection.getOutputStream());
            // 发送请求参数
            out.print(body);
            // flush输出流的缓冲
            out.flush();
            connection.setConnectTimeout(10 * 1000);
            connection.setReadTimeout(10 * 1000);
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static Map<String, List<String>> sendHttpsPostNoRedirectBodys(String url, Map<String, String> headerMap, Proxy proxy, String body) {
        BufferedReader in = null;
        HttpsURLConnection connection = null;
        try {
            URL realUrl = new URL(url);
            if (proxy == null) {
                connection = (HttpsURLConnection) realUrl.openConnection();
            } else {
                connection = (HttpsURLConnection) realUrl.openConnection(proxy);
            }
            try {
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.setRequestMethod("POST");
                SSLContext ctx = MyX509TrustManagerUtils();
                connection.setSSLSocketFactory(ctx.getSocketFactory());
                connection.setHostnameVerifier(new HostnameVerifier() {
                    //在握手期间，如果 URL 的主机名和服务器的标识主机名不匹配，则验证机制可以回调此接口的实现程序来确定是否应该允许此连接。
                    @Override
                    public boolean verify(String arg0, SSLSession arg1) {
                        return true;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                connection = (HttpsURLConnection) realUrl.openConnection();
            }

            connection.setInstanceFollowRedirects(false);
            connection.setRequestProperty("Proxy-Connection", "keep-alive");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("accept-language", "zh-CN,zh;q=0.9");
            for (String key : headerMap.keySet()) {
                connection.setRequestProperty(key, headerMap.get(key));
            }
            PrintWriter out = new PrintWriter(connection.getOutputStream());
            // 发送请求参数
            out.print(body);
            // flush输出流的缓冲
            out.flush();
            connection.setConnectTimeout(10 * 1000);
            connection.setReadTimeout(10 * 1000);
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            return connection.getHeaderFields();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static String sendSSLHttpsAliGet(String url, Map<String, String> ckMap, Proxy proxy) {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        OutputStream os = null;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            HttpsURLConnection connection = null;
            if (proxy == null) {
                connection = (HttpsURLConnection) realUrl.openConnection();
            } else {
                try {
                    //fix for not find proxy
                    connection = (HttpsURLConnection) realUrl.openConnection(proxy);
                } catch (Exception e) {
                    e.printStackTrace();
                    connection = (HttpsURLConnection) realUrl.openConnection();
                }
            }

            // 设置通用的请求属性
            connection.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml");
            connection.setRequestProperty("accept-language", "zh-CN,zh;q=0.9");
            connection.setRequestProperty("sec-ch-ua-platform", "Android");
            connection.setRequestProperty("user-agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Mobile Safari/537.36");

            //connection.setRequestProperty("content-type", "text/html;charset=UTF-8");
            connection.setRequestProperty("x-forwarded-for", ckMap.get("ip"));
            connection.setConnectTimeout(10 * 1000);
            connection.setReadTimeout(10 * 1000);
            connection.setDoOutput(true);
            connection.setDoInput(true);

            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line + "\n");
            }
        } catch (Exception e) {
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result.toString();
    }

    public static String sendSSLPostAliParam(String url, String param, Proxy proxy) {
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try {
            String urlNameString = url;
            URL realUrl = new URL(urlNameString);

            URLConnection conn = null;
            if (proxy == null) {
                conn = (HttpsURLConnection) realUrl.openConnection();
            } else {
                try {
                    //fix for not find proxy
                    conn = (HttpsURLConnection) realUrl.openConnection(proxy);
                } catch (Exception e) {
                    e.printStackTrace();
                    conn = (HttpsURLConnection) realUrl.openConnection();
                }
            }

            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("Connection", "keep-alive");

            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Host", "mclient.alipay.com");
            conn.setRequestProperty("Origin", "https://mclient.alipay.com");

            conn.setRequestProperty("Sec-Fetch-Site", "same-site");
            conn.setRequestProperty("Sec-Fetch-Mode", "cors");
            conn.setRequestProperty("Sec-Fetch-Dest", "empty");
            conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");

            conn.setConnectTimeout(10 * 1000);
            conn.setReadTimeout(10 * 1000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (ConnectException e) {
        } catch (SocketTimeoutException e) {
        } catch (IOException e) {
        } catch (Exception e) {
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
            }
        }
        return result.toString();
    }

    public static SSLContext MyX509TrustManagerUtils() {
        TrustManager[] tm = {new MyX509TrustManager()};
        SSLContext ctx = null;
        try {
            ctx = SSLContext.getInstance("TLS");
            ctx.init(null, tm, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ctx;
    }
}

class MyX509TrustManager extends X509ExtendedTrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        // TODO Auto-generated method stub

    }

    @Override
    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        // TODO Auto-generated method stub

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] arg0, String arg1, Socket arg2) throws CertificateException {
        // TODO Auto-generated method stub

    }

    @Override
    public void checkClientTrusted(X509Certificate[] arg0, String arg1, SSLEngine arg2)
            throws CertificateException {
        // TODO Auto-generated method stub

    }

    @Override
    public void checkServerTrusted(X509Certificate[] arg0, String arg1, Socket arg2) throws CertificateException {
        // TODO Auto-generated method stub

    }

    @Override
    public void checkServerTrusted(X509Certificate[] arg0, String arg1, SSLEngine arg2)
            throws CertificateException {
        // TODO Auto-generated method stub

    }

}

