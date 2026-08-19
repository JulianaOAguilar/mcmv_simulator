package com.ju.mcmv.simulation.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

import com.ju.mcmv.regras.TipoImovel;
import com.ju.mcmv.regras.TipoUnidade;

@Component
public class SubsidyCalculator {

    public BigDecimal calcularSubsidio(
            BigDecimal rendaFamiliar,
            BigDecimal valorImovel,
            String estado,
            TipoImovel tipoImovel,
            TipoUnidade tipoUnidade,
            BigDecimal areaImovel,
            BigDecimal taxaJurosMensal,
            BigDecimal limiteMunicipio,
            BigDecimal fatorPopulacional) {

        /*
         * O desconto é destinado às famílias dentro do limite
         * de renda definido pelas regras do programa.
         */
        if (rendaFamiliar.compareTo(new BigDecimal("4000")) > 0) {
            return BigDecimal.ZERO;
        }

        /*
         * 1. Fator relacionado à renda familiar
         */
        BigDecimal frenda =
                calcularFrenda(rendaFamiliar);

        /*
         * 2. Fator relacionado ao estado
         */
        BigDecimal fdr =
                calcularFdr(estado);

        /*
         * 3. Fator relacionado à demanda de recursos
         * em relação ao valor do imóvel.
         */
        BigDecimal fdFin =
                calcularFdFin(
                        rendaFamiliar,
                        taxaJurosMensal,
                        valorImovel,
                        limiteMunicipio
                );

        /*
         * 4. Fator relacionado às características
         * da unidade habitacional.
         */
        BigDecimal fuh =
                calcularFuh(
                        tipoUnidade,
                        areaImovel
                );

        /*
         * Soma dos fatores.
         */
        BigDecimal fatores =
                fdr
                        .add(fdFin)
                        .add(fuh);

        /*
         * Fórmula do desconto:
         *
         * D = Frenda ×
         *     (1 + (FDR + FdFin + FUH) / 100)
         *     × Fpop
         */
        BigDecimal desconto =
                frenda
                        .multiply(
                                BigDecimal.ONE.add(
                                        fatores.divide(
                                                new BigDecimal("100"),
                                                10,
                                                RoundingMode.HALF_UP
                                        )
                                )
                        )
                        .multiply(fatorPopulacional);

        /*
         * Para imóvel usado, aplicamos a redução
         * definida na regra utilizada pelo simulador.
         */
        if (tipoImovel == TipoImovel.USADO) {

            desconto =
                    desconto.multiply(
                            new BigDecimal("0.50")
                    );
        }

        /*
         * Limite máximo do desconto.
         *
         * Norte: até R$ 65.000
         * Demais regiões: até R$ 55.000
         */
        BigDecimal limiteMaximo;

        if (ehRegiaoNorte(estado)) {

            limiteMaximo =
                    new BigDecimal("65000");

        } else {

            limiteMaximo =
                    new BigDecimal("55000");
        }

        if (desconto.compareTo(limiteMaximo) > 0) {

            desconto = limiteMaximo;
        }

        /*
         * Se o desconto calculado for inferior ao
         * mínimo permitido, não concedemos desconto.
         */
        BigDecimal descontoMinimo =
                new BigDecimal("1500");

        if (desconto.compareTo(descontoMinimo) < 0) {

            return BigDecimal.ZERO;
        }

        return desconto.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }


    /*
     * =========================================================
     * FRENDA
     * =========================================================
     *
     * Quanto menor a renda, maior tende a ser o desconto.
     */
    private BigDecimal calcularFrenda(
            BigDecimal renda) {

        BigDecimal dMax =
                new BigDecimal("50000");

        BigDecimal dMin =
                new BigDecimal("1900");

        BigDecimal rdMax =
                new BigDecimal("1750");

        BigDecimal rdMin =
                new BigDecimal("3700");

        BigDecimal dois =
                new BigDecimal("2");

        BigDecimal b =
                dois
                        .multiply(dMax)
                        .multiply(
                                dMin
                                        .divide(
                                                dMax,
                                                10,
                                                RoundingMode.HALF_UP
                                        )
                                        .subtract(
                                                BigDecimal.ONE
                                        )
                        )
                        .divide(
                                rdMin.subtract(rdMax),
                                10,
                                RoundingMode.HALF_UP
                        );

        BigDecimal a =
                b.negate()
                        .divide(
                                dois.multiply(
                                        rdMin.subtract(rdMax)
                                ),
                                10,
                                RoundingMode.HALF_UP
                        );

        BigDecimal diferenca =
                renda.subtract(rdMax);

        return a
                .multiply(
                        diferenca.pow(2)
                )
                .add(
                        b.multiply(diferenca)
                )
                .add(dMax);
    }


    /*
     * =========================================================
     * FDR
     * =========================================================
     *
     * Fator relacionado ao comprometimento médio
     * de despesa/renda da unidade federativa.
     */
    private BigDecimal calcularFdr(
            String estado) {

        return switch (estado.toUpperCase()) {

            case "SP" -> new BigDecimal("2.07");

            case "ES" -> new BigDecimal("-5.10");
            case "MG" -> new BigDecimal("-5.68");
            case "RJ" -> new BigDecimal("-1.78");

            case "PR" -> new BigDecimal("-5.13");
            case "RS" -> new BigDecimal("-1.09");
            case "SC" -> new BigDecimal("4.58");

            case "DF" -> new BigDecimal("7.17");
            case "GO" -> new BigDecimal("-0.68");
            case "MS" -> new BigDecimal("-3.49");
            case "MT" -> new BigDecimal("-0.13");

            case "AC" -> new BigDecimal("3.44");
            case "AM" -> new BigDecimal("0.56");
            case "AP" -> new BigDecimal("10.00");
            case "PA" -> new BigDecimal("5.45");
            case "RO" -> new BigDecimal("-10.00");
            case "RR" -> new BigDecimal("-4.83");
            case "TO" -> new BigDecimal("-3.17");

            case "AL" -> new BigDecimal("-8.14");
            case "BA" -> new BigDecimal("-5.03");
            case "CE" -> new BigDecimal("-7.87");
            case "MA" -> new BigDecimal("0.69");
            case "PB" -> new BigDecimal("-6.78");
            case "PE" -> new BigDecimal("-5.31");
            case "PI" -> new BigDecimal("-8.13");
            case "RN" -> new BigDecimal("0.58");
            case "SE" -> new BigDecimal("-6.18");

            default -> throw new IllegalArgumentException(
                    "Estado inválido: " + estado
            );
        };
    }


    /*
     * =========================================================
     * FUH
     * =========================================================
     *
     * Fator relacionado às características da unidade.
     *
     * No seu modelo:
     * - apartamento: considera 39m² como referência
     * - demais unidades: considera 46m²
     */
    private BigDecimal calcularFuh(
            TipoUnidade tipoUnidade,
            BigDecimal area) {

        if (tipoUnidade == TipoUnidade.APARTAMENTO) {

            return new BigDecimal("10")
                    .multiply(
                            area.subtract(
                                    new BigDecimal("39")
                            )
                    )
                    .divide(
                            new BigDecimal("20"),
                            10,
                            RoundingMode.HALF_UP
                    );
        }

        return new BigDecimal("10")
                .multiply(
                        area.subtract(
                                new BigDecimal("46")
                        )
                )
                .divide(
                        new BigDecimal("20"),
                        10,
                        RoundingMode.HALF_UP
                );
    }


    /*
     * =========================================================
     * FDFIN
     * =========================================================
     *
     * Fator relacionado à demanda de recursos da família
     * em relação ao valor do imóvel.
     *
     * A ideia é:
     *
     * quanto maior a necessidade de financiamento,
     * maior pode ser o fator.
     *
     * Para o simulador, usamos:
     *
     * valor do imóvel
     * -------------------------------
     * limite máximo do município
     *
     * e também consideramos a capacidade de pagamento
     * aproximada da família.
     */
    private BigDecimal calcularFdFin(
            BigDecimal renda,
            BigDecimal taxaMensal,
            BigDecimal valorImovel,
            BigDecimal limiteMunicipio) {

        /*
         * Evita divisão por zero.
         */
        if (limiteMunicipio.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        /*
         * Relação entre o valor do imóvel e o limite
         * permitido para o município.
         *
         * Exemplo:
         *
         * imóvel = 230.000
         * limite = 275.000
         *
         * relação ≈ 0,836
         */
        BigDecimal proporcaoImovel =
                valorImovel.divide(
                        limiteMunicipio,
                        10,
                        RoundingMode.HALF_UP
                );

        /*
         * Quanto maior o valor do imóvel em relação
         * ao limite municipal, maior a demanda.
         *
         * Transformamos isso em uma pontuação.
         */
        BigDecimal fatorImovel =
                proporcaoImovel
                        .multiply(new BigDecimal("10"));

        /*
         * Capacidade aproximada de comprometimento
         * da renda.
         *
         * Aqui usamos 30% como referência para o simulador.
         */
        BigDecimal parcelaMaxima =
                renda.multiply(
                        new BigDecimal("0.30")
                );

        /*
         * Se a renda for muito baixa, aumentamos a
         * pontuação de necessidade de recursos.
         */
        BigDecimal fatorRenda;

        if (parcelaMaxima.compareTo(
                BigDecimal.ZERO) <= 0) {

            fatorRenda =
                    BigDecimal.ZERO;

        } else {

            /*
             * Uma referência simples baseada na relação
             * entre a renda e o valor do imóvel.
             */
            BigDecimal rendaAnual =
                    renda.multiply(
                            new BigDecimal("12")
                    );

            BigDecimal relacao =
                    valorImovel.divide(
                            rendaAnual,
                            10,
                            RoundingMode.HALF_UP
                    );

            fatorRenda =
                    relacao
                            .divide(
                                    new BigDecimal("100"),
                                    10,
                                    RoundingMode.HALF_UP
                            );
        }

        /*
         * Soma dos componentes.
         *
         * Limitamos o resultado para evitar que o fator
         * ultrapasse uma faixa absurda.
         */
        BigDecimal resultado =
                fatorImovel.add(fatorRenda);

        BigDecimal limite =
                new BigDecimal("10");

        if (resultado.compareTo(limite) > 0) {
            resultado = limite;
        }

        if (resultado.compareTo(BigDecimal.ZERO) < 0) {
            resultado = BigDecimal.ZERO;
        }

        return resultado.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }


    /*
     * =========================================================
     * REGIÃO NORTE
     * =========================================================
     */
    private boolean ehRegiaoNorte(
            String estado) {

        return switch (estado.toUpperCase()) {

            case "AC",
                 "AP",
                 "AM",
                 "PA",
                 "RO",
                 "RR",
                 "TO" -> true;

            default -> false;
        };
    }
}