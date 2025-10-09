package com.luigarah.util;

public final class JsonStringCleaner {
    private JsonStringCleaner() {}
    /** Remove quebras de linha/tabs e espaços extras nas pontas; mantém espaços internos. */
    public static String clean(String raw) {
        if (raw == null) return null;
        // remove CR/LF em qualquer posição e poda pontas
        String s = raw.replace("\r", "").replace("\n", "").trim();
        return s.isEmpty() ? null : s;
    }
}
