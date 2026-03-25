package br.com.api.mice.replicacao.converter;

import br.com.api.mice.replicacao.entity.FilialEntity;
import java.util.Map;

public class FilialConverter {

    private FilialConverter() {}

    public static void apply(Map<String, Object> data, FilialEntity entity) {
        entity.setNome(ConverterUtils.getString(data, "name", "nome"));
        entity.setCodigo(ConverterUtils.getString(data, "code", "codigo"));
    }
}