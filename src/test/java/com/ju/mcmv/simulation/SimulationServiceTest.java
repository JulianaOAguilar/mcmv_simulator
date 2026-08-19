package com.ju.mcmv.simulation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

import com.ju.mcmv.regras.McmvFaixas;
import com.ju.mcmv.regras.TipoImovel;
import com.ju.mcmv.regras.TipoUnidade;
import com.ju.mcmv.simulation.DTO.SimulationRequest;
import com.ju.mcmv.simulation.DTO.SimulationResponse;
import com.ju.mcmv.simulation.calculator.FinancingCalculator;
import com.ju.mcmv.simulation.calculator.SubsidyCalculator;

// vamos fazer alguns testes para praticar heheheheh

@ExtendWith(MockitoExtension.class) // fala para o o Junit usar o mockito tbm
// mocks criam objetos falsos que representam tal dependência durante o teste.
public class SimulationServiceTest {

    @Mock
    private SimulationRepository repository;

    @Mock
    private FinancingCalculator financingCalculator;

    @Mock
    private SubsidyCalculator subsidyCalculator;

    private SimulationService service;


    @BeforeEach // antes dos testes, inicializar usando os mocks
    void setUp() {
        service = new SimulationService(
                repository,
                financingCalculator,
                subsidyCalculator);
    }

    @Test
    void deveSalvarUmaSimulacao() {

        SimulationRequest request = new SimulationRequest( // Cria um request com alguns dados
    new BigDecimal("3000"),
    new BigDecimal("250000"),
    new BigDecimal("30000"),
    420,
    "Itu",
    "SP",
    TipoImovel.NOVO,
    TipoUnidade.CASA,
    new BigDecimal("50")
);


when(subsidyCalculator.calcularSubsidio(
    any(),
    any(),
    any(),
    any(),
    any(),
    any(),
    any(),
    any(),
    any()
)).thenReturn(new BigDecimal("20000"));

when(financingCalculator.calcularPrimeiraParcela(
    any(), any(), any()
)).thenReturn(new BigDecimal("1200"));

when(financingCalculator.calcularParcelaMaxima(
    any()
)).thenReturn(new BigDecimal("900"));


when(financingCalculator.parcelaCabeNaRenda(
    any(), any()
)).thenReturn(true);

when(repository.save(any(Simulation.class)))
    .thenAnswer(invocation -> invocation.getArgument(0));

    SimulationResponse response = service.save(request); // realiza a ação save


    assertThat(response.faixaMcmv())
    .isEqualTo(McmvFaixas.FAIXA_1);




    }

}