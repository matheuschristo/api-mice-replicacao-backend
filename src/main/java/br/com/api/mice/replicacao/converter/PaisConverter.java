package br.com.api.mice.replicacao.converter;

import br.com.api.mice.replicacao.entity.PaisEntity;
import java.util.Map;

public class PaisConverter {

    private PaisConverter() {}

    public static void apply(Map<String, Object> data, PaisEntity entity) {
        entity.setNome(ConverterUtils.getString(data, "name", "nome"));
        entity.setSigla(ConverterUtils.getString(data, "code", "sigla"));
    }
}
