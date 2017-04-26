package com.jendib.scrapper;

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
            downloader.add("http://copainsdavant.linternaute.com/glossary/users/" + letter,
                    (url, content) -> downloadGlossaryPages(downloader, GLOSSARY_URL_PATTERN, content));
        }

        while (true) {
            Thread.sleep(5000);
        }

        // Download profile page
        /*futureList = downloadPages(PROFILE_URL_PATTERN, futureList);

        // Parse profiles
        for (Future<String> future : futureList) {
            String content = future.get();
            Matcher matcher = NAME_PATTERN.matcher(content);
            if (matcher.find()) {
                System.out.println(matcher.group(1));
            }
        }*/
    }

    private static void downloadGlossaryPages(Downloader downloader, Pattern pattern, String content) {
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            downloader.add("http://copainsdavant.linternaute.com" + matcher.group(1), (url0, content0) -> {
                if (url0.contains("/p/")) {
                    // TODO Parse profile page
                    System.out.println("Profile page: " + url0);
                } else {
                    downloadGlossaryPages(downloader, pattern, content0);
                }
            });
        }
    }
}