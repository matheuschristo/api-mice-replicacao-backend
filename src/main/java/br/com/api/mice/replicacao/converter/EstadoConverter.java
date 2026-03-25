package br.com.api.mice.replicacao.converter;

import br.com.api.mice.replicacao.entity.EstadoEntity;
import java.util.Map;

public class EstadoConverter {

    private EstadoConverter() {}

    public static void apply(Map<String, Object> data, EstadoEntity entity) {
        entity.setNome(ConverterUtils.getString(data, "name", "nome"));
        entity.setSigla(ConverterUtils.getString(data, "code", "sigla"));
    }
}