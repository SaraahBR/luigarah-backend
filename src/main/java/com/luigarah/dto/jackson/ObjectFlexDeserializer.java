package com.luigarah.dto.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ObjectFlexDeserializer extends JsonDeserializer<Map<String,Object>> {
    @Override
    public Map<String, Object> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t = p.currentToken();

        if (t == JsonToken.START_OBJECT) {
            return p.readValueAs(Map.class);
        }
        if (t == JsonToken.VALUE_STRING) {
            String s = p.getValueAsString();
            if (s == null) return null;
            s = s.replace("\r","").replace("\n","").trim();
            if (s.isEmpty()) return null;

            Map<String,Object> map = new LinkedHashMap<>();
            String[] pairs = s.split("[;]");
            if (pairs.length == 1) pairs = s.split(",");

            for (String pair : pairs) {
                String kv = pair.trim();
                if (kv.isEmpty()) continue;
                int idx = kv.indexOf(':');
                if (idx < 0) continue;
                String k = kv.substring(0, idx).trim().replaceAll("^\"|\"$", "");
                String v = kv.substring(idx+1).trim().replaceAll("^\"|\"$", "");
                map.put(k, coerce(v));
            }
            return map.isEmpty() ? null : map;
        }
        return null;
    }

    private static Object coerce(String v){
        try { return Integer.valueOf(v); } catch (Exception ignored) {}
        try { return Double.valueOf(v); } catch (Exception ignored) {}
        if ("true".equalsIgnoreCase(v) || "false".equalsIgnoreCase(v)) return Boolean.valueOf(v);
        return v;
    }
}
