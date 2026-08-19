package com.ju.mcmv.regras;

import java.math.BigDecimal;

public class McmvRegras {

    private McmvRegras() {
    }

    // de acordo com as regras do programa, somente 30% da renda familiar pode ser
    // comprometida com as parcelas do financiamento

    public static final BigDecimal COMPROMETIMENTO_MAXIMO =
            new BigDecimal("0.30");
}