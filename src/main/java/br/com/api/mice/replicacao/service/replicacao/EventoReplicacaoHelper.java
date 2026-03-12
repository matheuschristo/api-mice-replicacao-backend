package br.com.api.mice.replicacao.service.replicacao;

import br.com.api.mice.replicacao.dto.EventoReplicacaoDTO;
import java.time.LocalDateTime;
import java.util.Map;

final class EventoReplicacaoHelper {

    private EventoReplicacaoHelper() {
    }

    static Long sourceId(EventoReplicacaoDTO evento) {
        return parseLong(evento.getSourceId(), "sourceId");
    }

    static String getString(Map<String, Object> data, String... keys) {
        if (data == null) {
            return null;
        }
        for (String key : keys) {
            Object value = data.get(key);
            if (value != null) {
                return String.valueOf(value);
            }
        }
        return null;
    }

    static Long getLong(Map<String, Object> data, String... keys) {
        String value = getString(data, keys);
        return value == null || value.isBlank() ? null : parseLong(value, keys[0]);
    }

    static Boolean getBoolean(Map<String, Object> data, String... keys) {
        String value = getString(data, keys);
        return value == null ? null : Boolean.parseBoolean(value);
    }

    static LocalDateTime updatedAt(EventoReplicacaoDTO evento) {
        return evento.getUpdatedAt() != null ? evento.getUpdatedAt() : LocalDateTime.now();
    }

    static Long parseLong(String value, String fieldName) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Campo invalido para conversao numerica: " + fieldName);
        }
    }
}
