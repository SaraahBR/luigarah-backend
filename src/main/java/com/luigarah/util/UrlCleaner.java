package com.luigarah.util;

public final class UrlCleaner {
    private UrlCleaner() {}

    /** Remove todos os whitespaces (inclui \r \n \t e espaços) e poda pontas. */
    public static String cleanUrl(String raw) {
        if (raw == null) return null;
        String s = raw.replaceAll("\\s+", ""); // tira qualquer whitespace
        return s.isEmpty() ? null : s;
    }
}
