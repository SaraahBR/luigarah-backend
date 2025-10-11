package com.luigarah.model.enums;

public enum PadraoTamanho {
    USA("usa"),
    BR("br"),
    SAPATOS("sapatos"); // mantido para compatibilidade

    private final String value;
    PadraoTamanho(String v) { this.value = v; }
    public String value() { return value; }

    public static boolean isValid(String s) {
        if (s == null) return true; // null = indefinido
        String x = s.toLowerCase();
        return "usa".equals(x) || "br".equals(x) || "sapatos".equals(x);
    }

    public static String normalize(String s) {
        return s == null ? null : s.toLowerCase().trim();
    }
}