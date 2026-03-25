package br.com.api.mice.replicacao.converter;

import br.com.api.mice.replicacao.entity.UsuarioEntity;
import java.util.Map;

public class UsuarioConverter {

    private UsuarioConverter() {}

    public static void apply(Map<String, Object> data, UsuarioEntity entity) {
        entity.setNome(ConverterUtils.getString(data, "full_name", "nome"));
        entity.setEmail(ConverterUtils.getString(data, "email"));
        entity.setAtivo(Boolean.TRUE.equals(ConverterUtils.getBoolean(data, "active", "ativo")));
    }
}