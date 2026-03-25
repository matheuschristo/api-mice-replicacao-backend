package br.com.api.mice.replicacao.converter;

import br.com.api.mice.replicacao.entity.EmpresaEntity;
import java.util.Map;

public class EmpresaConverter {

    private EmpresaConverter() {}

    public static void apply(Map<String, Object> data, EmpresaEntity entity) {
        entity.setRazaoSocial(ConverterUtils.getString(data, "legal_name", "razaoSocial"));
        entity.setNomeFantasia(ConverterUtils.getString(data, "trade_name", "nomeFantasia"));
        entity.setCnpj(ConverterUtils.getString(data, "cnpj"));
    }
}