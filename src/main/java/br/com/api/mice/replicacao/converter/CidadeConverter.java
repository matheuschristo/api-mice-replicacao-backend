package br.com.api.mice.replicacao.converter;

import br.com.api.mice.replicacao.entity.CidadeEntity;
import java.util.Map;

public class CidadeConverter {

    private CidadeConverter() {}

    public static void apply(Map<String, Object> data, CidadeEntity entity) {
        entity.setNome(ConverterUtils.getString(data, "name", "nome"));
        entity.setCodigoIbge(ConverterUtils.getString(data, "ibge_code", "codigoIbge"));
    }
}