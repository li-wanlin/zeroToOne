package com.jnl.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpLocalUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpLocalUtils.class);

    private static final int DEFAULT_CONNECT_TIMEOUT = 5000;

    private static final int DEFAULT_READ_TIMEOUT = 5000;


    public static String sendGetRequest(String urlStr){
        try{
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
            connection.setReadTimeout(DEFAULT_READ_TIMEOUT);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                return response.toString();
            }
        }catch (Exception e){
            logger.error("get请求异常，请检查",e);
        }
        return null;
    }


    public static String sendGetRequest(String urlStr,String token){
        try {
            // 创建信任所有证书的 SSLContext
            SSLContext sslContext = SSLContextBuilder.create()
                    .loadTrustMaterial(null, TrustAllStrategy.INSTANCE)
                    .build();

            // 创建 HttpClient 并设置 SSLContext 和主机名验证器
            HttpClient httpClient = HttpClients.custom()
                    .setSSLContext(sslContext)
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .build();

            //普通方法创建请求客户端
            //HttpClient aDefault = HttpClients.createDefault();

            // 创建 HttpGet 请求
            HttpGet httpGet = new HttpGet(urlStr);
            // 设置请求头
            httpGet.addHeader("token", token);

            // 执行请求并获取响应
            HttpResponse response = httpClient.execute(httpGet);
            // 获取响应实体内容
            String responseBody = EntityUtils.toString(response.getEntity());
            return responseBody;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String sendUnverifiedGet(String url){
        try {
            // 创建信任所有证书的 SSLContext
            SSLContext sslContext = SSLContextBuilder.create()
                    .loadTrustMaterial(null, TrustAllStrategy.INSTANCE)
                    .build();

            // 创建 HttpClient 并设置 SSLContext 和主机名验证器
            HttpClient httpClient = HttpClients.custom()
                    .setSSLContext(sslContext)
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .build();

            //普通方法创建请求客户端
            //HttpClient aDefault = HttpClients.createDefault();

            // 创建 HttpGet 请求
            HttpGet httpGet = new HttpGet(url);


            // 执行请求并获取响应
            HttpResponse response = httpClient.execute(httpGet);
            // 获取响应实体内容
            String responseBody = EntityUtils.toString(response.getEntity());
            return responseBody;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }



    public static String sendUnverifiedPost(String url){
        try {
            // 创建信任所有证书的 SSLContext
            SSLContext sslContext = SSLContextBuilder.create()
                    .loadTrustMaterial(null, TrustAllStrategy.INSTANCE)
                    .build();

            // 创建 HttpClient 并设置 SSLContext 和主机名验证器
            HttpClient httpClient = HttpClients.custom()
                    .setSSLContext(sslContext)
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .build();

            //普通方法创建请求客户端
            //HttpClient aDefault = HttpClients.createDefault();

            // 创建 HttpPost 请求
            HttpPost httpPost = new HttpPost(url);


            // 执行请求并获取响应
            HttpResponse response = httpClient.execute(httpPost);
            // 获取响应实体内容
            String responseBody = EntityUtils.toString(response.getEntity());
            return responseBody;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
