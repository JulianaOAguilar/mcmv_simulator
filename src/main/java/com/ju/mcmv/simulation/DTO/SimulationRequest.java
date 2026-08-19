package com.ju.mcmv.simulation.DTO;

import java.math.BigDecimal;

import com.ju.mcmv.regras.TipoImovel;
import com.ju.mcmv.regras.TipoUnidade;

public record SimulationRequest(

        BigDecimal rendaFamiliar,
        BigDecimal valorImovel,
        BigDecimal valorEntrada,
        Integer mesesFinanciamento,
        String cidade,
        String estado,
        TipoImovel tipoImovel,
        TipoUnidade tipoUnidade,
        BigDecimal areaImovel

) {

}