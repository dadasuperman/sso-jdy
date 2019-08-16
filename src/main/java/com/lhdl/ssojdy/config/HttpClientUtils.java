package com.lhdl.ssojdy.config;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * HttpClient4.3工具类
 *
 */
@Component
@PropertySource("classpath:myproperties.properties")
public class HttpClientUtils
{
    private static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class); // 日志记


    @Value("${sso.apiKey}")
    private static String apiKey;


    public static String  getHttpPost(String url,String param,Map<String,String> header){
        // 获取连接客户端工具
        CloseableHttpClient httpClient = HttpClients.createDefault();

        String entityStr = null;
        CloseableHttpResponse response = null;

        try {

            URIBuilder uriBuilder = new URIBuilder(url);
            /** 第一种添加参数的形式 */
        /*uriBuilder.addParameter("name", "root");
        uriBuilder.addParameter("password", "123456");*/
            /** 第二种添加参数的形式 */
           /* List<NameValuePair> list = new LinkedList<>();
            BasicNameValuePair param1 = new BasicNameValuePair("username", "lkdl_zd");
            list.add(param1);
            uriBuilder.setParameters(list)*/;

            // 根据带参数的URI对象构建GET请求对象
            HttpPost httpPost = new HttpPost(uriBuilder.build());

            /*
             * 添加请求头信息
             */
            // 浏览器表示
            for (Map.Entry<String,String> entry : header.entrySet()) {
                httpPost.addHeader(entry.getKey(),entry.getValue());
            }
            httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.6)");
            // 传输的类型
            //httpPost.addHeader("Content-Type", "application/json;charset=utf-8");
           //httpPost.addHeader("Authorization", "Bearer "+apiKey);

            // 执行请求
            response = httpClient.execute(httpPost);
            // 获得响应的实体对象
            HttpEntity entity = response.getEntity();
            // 使用Apache提供的工具类进行转换成字符串
            entityStr = EntityUtils.toString(entity, "UTF-8");
        } catch (ClientProtocolException e) {
            System.err.println("Http协议出现问题");
            e.printStackTrace();
        } catch (ParseException e) {
            System.err.println("解析错误");
            e.printStackTrace();
        } catch (URISyntaxException e) {
            System.err.println("URI解析异常");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IO异常");
            e.printStackTrace();
        } finally {
            // 释放连接
            if (null != response) {
                try {
                    response.close();
                    httpClient.close();
                } catch (IOException e) {
                    System.err.println("释放连接出错");
                    e.printStackTrace();
                }
            }
        }

        // 打印响应内容
        return entityStr;

    }


//    public static void main(String[] args) {
//        new HttpClientUtils().getHttpPost();
//        //d1d04016-4d61-455c-9302-48676b0791e1
//    }

}