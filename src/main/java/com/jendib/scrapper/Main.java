package com.jendib.scrapper;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static Pattern GLOSSARY_URL_PATTERN = Pattern.compile("<a href=\"(/glossary/users.*?)\"");
    private static Pattern PROFILE_URL_PATTERN = Pattern.compile("<a href=\"(/p/.*?)\"");
    private static Pattern NAME_PATTERN = Pattern.compile("<h1>(.*?)</h1>");

    public static void main(String[] args) throws Exception {
        // Download letter pages
        Downloader downloader = new Downloader();
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        for (char letter : alphabet) {
            downloader.add("http://copainsdavant.linternaute.com/glossary/users/" + letter);
        }
        List<Future<String>> futureList = downloader.awaitTermination(1, TimeUnit.MINUTES);

        // Download glossary pages (2 levels)
        futureList = downloadPages(GLOSSARY_URL_PATTERN, futureList);
        futureList = downloadPages(GLOSSARY_URL_PATTERN, futureList);

        // Download profile page
        futureList = downloadPages(PROFILE_URL_PATTERN, futureList);

        // Parse profiles
        for (Future<String> future : futureList) {
            String content = future.get();
            Matcher matcher = NAME_PATTERN.matcher(content);
            if (matcher.find()) {
                System.out.println(matcher.group(1));
            }
        }
    }

    private static List<Future<String>> downloadPages(Pattern pattern, List<Future<String>> futureList) throws Exception {
        Downloader downloader = new Downloader();
        for (Future<String> future : futureList) {
            String content = future.get();
            Matcher matcher = pattern.matcher(content);
            while (matcher.find()) {
                downloader.add("http://copainsdavant.linternaute.com" + matcher.group(1));
            }
        }
        return downloader.awaitTermination(7, TimeUnit.DAYS);
    }
}