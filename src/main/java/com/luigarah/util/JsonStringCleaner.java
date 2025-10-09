package com.luigarah.util;

public final class JsonStringCleaner {

    private JsonStringCleaner() {}

    /** Remove quebras de linha/tabs do início e fim e normaliza aspas. */
    public static String clean(String raw) {
        if (raw == null) return null;
        // trim seguro para JSON gravado como texto
        String s = raw.replace("\r", "").replace("\n", "").trim();
        // nenhum parse aqui para não arriscar null por JSON mal fechado
        return s.isEmpty() ? null : s;
    }
}