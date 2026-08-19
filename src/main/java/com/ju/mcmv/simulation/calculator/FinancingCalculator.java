package com.ju.mcmv.simulation.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

import com.ju.mcmv.regras.McmvRegras;


// calculadora do financiamento
@Component
public class FinancingCalculator {

    public BigDecimal calcularPrimeiraParcela(
            BigDecimal valorFinanciado,
            Integer mesesFinanciamento,
            BigDecimal taxaJurosMensal) {

        // Amortização constante
        BigDecimal amortizacao =
                valorFinanciado.divide(
                        BigDecimal.valueOf(mesesFinanciamento),
                        10,
                        RoundingMode.HALF_UP
                );

        // Juros sobre o saldo inicial
        BigDecimal juros =
                valorFinanciado.multiply(taxaJurosMensal);

        // Primeira parcela do SAC
        return amortizacao
                .add(juros)
                .setScale(2, RoundingMode.HALF_UP);
    }

    // esse atributo é a maior parcela que voc~e poderia pagar
    public BigDecimal calcularParcelaMaxima(
            BigDecimal rendaFamiliar) {

        return rendaFamiliar
                .multiply(McmvRegras.COMPROMETIMENTO_MAXIMO)
                .setScale(2, RoundingMode.HALF_UP);
    }


    // se sua parcela está dentro do parcelaMaxima ou não
    public Boolean parcelaCabeNaRenda(
            BigDecimal parcela,
            BigDecimal rendaFamiliar) {

        BigDecimal parcelaMaxima =
                calcularParcelaMaxima(rendaFamiliar);

        return parcela.compareTo(parcelaMaxima) <= 0;
    }

    // entrada estimada para que a parcela seja cabível no seu bolso
    public BigDecimal calcularEntradaEstimada(
        BigDecimal valorImovel,
        BigDecimal valorSubsidio,
        BigDecimal rendaFamiliar,
        Integer mesesFinanciamento,
        BigDecimal taxaJurosMensal) {

    // Quanto a pessoa pode pagar por mês
    BigDecimal parcelaMaxima =
            calcularParcelaMaxima(rendaFamiliar);


    BigDecimal meses =
            BigDecimal.valueOf(mesesFinanciamento);

    BigDecimal umSobreMeses =
            BigDecimal.ONE.divide(
                    meses,
                    10,
                    RoundingMode.HALF_UP
            );

    BigDecimal fator =
            umSobreMeses.add(taxaJurosMensal);

    BigDecimal valorFinanciadoMaximo =
            parcelaMaxima.divide(
                    fator,
                    10,
                    RoundingMode.HALF_UP
            );

    // Entrada = imóvel - subsídio - financiamento máximo
    BigDecimal entradaEstimada =
            valorImovel
                    .subtract(valorSubsidio)
                    .subtract(valorFinanciadoMaximo);

    // Se der negativo, não precisa de entrada adicional
    if (entradaEstimada.compareTo(BigDecimal.ZERO) < 0) {
        entradaEstimada = BigDecimal.ZERO;
    }

    return entradaEstimada.setScale(
            2,
            RoundingMode.HALF_UP
    );
}
}