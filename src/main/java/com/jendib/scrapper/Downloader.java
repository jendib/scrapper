package com.jendib.scrapper;

import com.google.common.collect.Lists;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

class Downloader {
    private ExecutorService executorService = Executors.newFixedThreadPool(20);
    private List<Future<String>> futureList = Lists.newArrayList();

    void add(String url) {
        futureList.add(executorService.submit(() -> HttpUtil.readUrlIntoString(url, Charset.forName("UTF-8"))));
    }

    List<Future<String>> awaitTermination(long timeout, TimeUnit unit) throws Exception {
        executorService.shutdown();
        executorService.awaitTermination(timeout, unit);
        return futureList;
    }
}