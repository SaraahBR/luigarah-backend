package com.luigarah.dto.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StringListFlexDeserializer extends JsonDeserializer<List<String>> {
    @Override
    public List<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t = p.currentToken();
        List<String> out = new ArrayList<>();

        if (t == JsonToken.START_ARRAY) {
            while (p.nextToken() != JsonToken.END_ARRAY) {
                String s = clean(p.getValueAsString());
                if (s != null && !s.isEmpty()) out.add(s);
            }
            return out;
        }
        if (t == JsonToken.VALUE_STRING) {
            String raw = p.getValueAsString();
            if (raw == null) return null;
            String s = raw.trim();
            if (s.contains(",") || s.contains(";")) {
                for (String part : s.split("[,;]")) {
                    String v = clean(part);
                    if (v != null && !v.isEmpty()) out.add(v);
                }
            } else {
                String v = clean(s);
                if (v != null && !v.isEmpty()) out.add(v);
            }
            return out;
        }
        return null;
    }

    private static String clean(String v){
        if (v == null) return null;
        String s = v.replace("\r","").replace("\n","").trim();
        if (s.startsWith("http")) s = s.replaceAll("\\s+","");
        return s;
    }
}
