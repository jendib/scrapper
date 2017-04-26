package com.jendib.scrapper;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;

class HttpUtil {
    private static CloseableHttpClient httpClient;
    static {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(100);

        httpClient = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setRetryHandler((exception, executionCount, context) -> executionCount < 5)
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(10000)
                        .setConnectionRequestTimeout(15000)
                        .setSocketTimeout(10000)
                        .build())
                .build();
    }

    static String readUrlIntoString(String url, Charset charset) {
        HttpGet httpGet = new HttpGet(url);
        long start = System.currentTimeMillis();
        try (CloseableHttpResponse response = httpClient.execute(httpGet, HttpClientContext.create())) {
            System.out.println("Downloaded " + url + " in " + (System.currentTimeMillis() - start) + "ms");
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, charset);
        } catch (IOException e) {
            return null;
        }
    }
}