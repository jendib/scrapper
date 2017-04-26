package com.jendib.scrapper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;

public class HttpUtil {
    public static String readUrlIntoString(String url, Charset charset) {
        try {
            CloseableHttpClient httpClient = HttpClientBuilder.create()
                    .setRetryHandler((exception, executionCount, context) -> executionCount < 5)
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setConnectTimeout(10000)
                            .setConnectionRequestTimeout(15000)
                            .setSocketTimeout(10000)
                    .build())
                    .build();

            HttpGet httpGet = new HttpGet(url);
            long start = System.currentTimeMillis();
            HttpResponse response = httpClient.execute(httpGet);
            System.out.println("Downloaded " + url + " in " + (System.currentTimeMillis() - start) + "ms");
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, charset);
        } catch (IOException e) {
            return null;
        }
    }
}