package br.com.api.mice.replicacao.converter;

import java.util.Map;

final class ConverterUtils {

    private ConverterUtils() {}

    static String getString(Map<String, Object> data, String... keys) {
        if (data == null) return null;
        for (String key : keys) {
            Object value = data.get(key);
            if (value != null) return String.valueOf(value);
        }
        return null;
    }

    static Boolean getBoolean(Map<String, Object> data, String... keys) {
        String value = getString(data, keys);
        return value == null ? null : Boolean.parseBoolean(value);
    }
}