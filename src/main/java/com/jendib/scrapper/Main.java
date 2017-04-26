package com.jendib.scrapper;

import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static ExecutorService executorService = Executors.newFixedThreadPool(50);

    public static void main(String[] args) throws Exception {
        // Download letter pages
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        for (char letter : alphabet) {
            executorService.submit(() -> {
                String url = "http://copainsdavant.linternaute.com/glossary/users/" + letter;
                return HttpUtil.readUrlIntoString(url, Charset.forName("UTF-8"));
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
    }
}
