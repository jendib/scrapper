package com.jendib.scrapper;

import com.google.common.collect.Lists;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static Pattern GLOSSARY_URL_PATTERN = Pattern.compile("<a href=\"(/glossary/users.*?)\"");

    public static void main(String[] args) throws Exception {
        // Download letter pages
        Downloader downloader = new Downloader();
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        for (char letter : alphabet) {
            downloader.add("http://copainsdavant.linternaute.com/glossary/users/" + letter);
        }

        // Download first level pages
        List<Future<String>> futureList = downloader.awaitTermination(1, TimeUnit.MINUTES);
        downloader = new Downloader();
        for (Future<String> future : futureList) {
            String content = future.get();
            Matcher matcher = GLOSSARY_URL_PATTERN.matcher(content);
            while (matcher.find()) {
                downloader.add("http://copainsdavant.linternaute.com" + matcher.group(1));
            }
        }

        // Download second level pages
        futureList = downloader.awaitTermination(5, TimeUnit.MINUTES);
        downloader = new Downloader();
        for (Future<String> future : futureList) {
            String content = future.get();
            Matcher matcher = GLOSSARY_URL_PATTERN.matcher(content);
            while (matcher.find()) {
                downloader.add("http://copainsdavant.linternaute.com" + matcher.group(1));
            }
        }
    }

    static class Downloader {
        private ExecutorService executorService = Executors.newFixedThreadPool(100);
        private List<Future<String>> futureList = Lists.newArrayList();

        private void add(String url) {
            futureList.add(executorService.submit(() -> HttpUtil.readUrlIntoString(url, Charset.forName("UTF-8"))));
        }

        List<Future<String>> awaitTermination(long timeout, TimeUnit unit) throws Exception {
            executorService.shutdown();
            executorService.awaitTermination(timeout, unit);
            return futureList;
        }
    }
}