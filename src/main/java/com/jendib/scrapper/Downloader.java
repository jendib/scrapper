package com.jendib.scrapper;

import com.google.common.collect.Lists;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class Downloader {
    private ExecutorService executorService = Executors.newFixedThreadPool(30);
    private List<Future<Void>> futureList = Lists.newArrayList();

    void add(String url, OnDownload onDownload) {
        futureList.add(executorService.submit(() -> {
            onDownload.run(url, HttpUtil.readUrlIntoString(url, Charset.forName("UTF-8")));
            return null;
        }));
    }

    @FunctionalInterface
    interface OnDownload {
        void run(String url, String content);
    }
}