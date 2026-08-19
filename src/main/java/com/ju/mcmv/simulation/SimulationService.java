package com.ju.mcmv.simulation;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ju.mcmv.regras.McmvFaixas;
import com.ju.mcmv.simulation.DTO.SimulationRequest;
import com.ju.mcmv.simulation.DTO.SimulationResponse;
import com.ju.mcmv.simulation.calculator.FinancingCalculator;
import com.ju.mcmv.simulation.calculator.SubsidyCalculator;

@Service
public class SimulationService {

    private final SimulationRepository repository;
    private final FinancingCalculator financingCalculator;
    private final SubsidyCalculator subsidyCalculator;

    public SimulationService(
            SimulationRepository repository,
            FinancingCalculator financingCalculator,
            SubsidyCalculator subsidyCalculator) {

        this.repository = repository;
        this.financingCalculator = financingCalculator;
        this.subsidyCalculator = subsidyCalculator;
    }

    public SimulationResponse save(SimulationRequest request) {

        Simulation simulation = new Simulation();

        // Dados informados pelo usuário
        simulation.setRendaFamiliar(request.rendaFamiliar());
        simulation.setValorImovel(request.valorImovel());
        simulation.setValorEntrada(request.valorEntrada());
        simulation.setMesesFinanciamento(request.mesesFinanciamento());
        simulation.setCidade(request.cidade());
        simulation.setEstado(request.estado());
        simulation.setTipoImovel(request.tipoImovel());
        simulation.setTipoUnidade(request.tipoUnidade());
        simulation.setAreaImovel(request.areaImovel());
        

        // 1. Identifica a faixa do MCMV
        McmvFaixas faixa = identificarFaixa(
                request.rendaFamiliar()
        );

        // 2. Define a taxa de juros
        // Temporária
        BigDecimal taxaJurosMensal =
                new BigDecimal("0.005");

        BigDecimal taxaJurosAnual =
                new BigDecimal("6.00");

        // 3. Calcula o subsídio
        BigDecimal valorSubsidio =
                subsidyCalculator.calcularSubsidio(
                        request.rendaFamiliar(),
                        request.valorImovel(),
                        request.estado(),
                        request.tipoImovel(),
                        request.tipoUnidade(),
                        request.areaImovel(),
                        taxaJurosMensal,
                        new BigDecimal("275000"),
                        BigDecimal.ONE
                );

        // 4. Calcula o valor efetivamente financiado
        BigDecimal valorFinanciado =
                request.valorImovel()
                        .subtract(request.valorEntrada())
                        .subtract(valorSubsidio);

        // 5. Calcula a primeira parcela pelo SAC
        BigDecimal parcela =
                financingCalculator.calcularPrimeiraParcela(
                        valorFinanciado,
                        request.mesesFinanciamento(),
                        taxaJurosMensal
                );

        // 6. Calcula o máximo que pode comprometer da renda
        BigDecimal parcelaMaxima =
                financingCalculator.calcularParcelaMaxima(
                        request.rendaFamiliar()
                );

        // 7. Verifica se a parcela cabe na renda
        Boolean parcelaCabeNaRenda =
                financingCalculator.parcelaCabeNaRenda(
                        parcela,
                        request.rendaFamiliar()
                );

                
                BigDecimal entradaEstimada =
        financingCalculator.calcularEntradaEstimada(
                request.valorImovel(),
                valorSubsidio,
                request.rendaFamiliar(),
                request.mesesFinanciamento(),
                taxaJurosMensal
        );

        // 8. Verifica se o imóvel é elegível
        Boolean imovelElegivel =
                verificarElegibilidade(
                        faixa,
                        request.valorImovel()
                );

        // 9. Salva os resultados calculados
        simulation.setFaixaMcmv(faixa);
        simulation.setImovelElegivel(imovelElegivel);
        simulation.setValorSubsidio(valorSubsidio);
        simulation.setValorFinanciado(valorFinanciado);
        simulation.setValorParcela(parcela);
        simulation.setTaxaJurosAnual(taxaJurosAnual);
simulation.setEntradaEstimada(entradaEstimada);
        simulation.setParcelaMaxima(parcelaMaxima);
        simulation.setParcelaCabeNaRenda(parcelaCabeNaRenda);

        Simulation saved = repository.save(simulation);

        // 10. Retorna a resposta
        return new SimulationResponse(
                saved.getId(),
                saved.getRendaFamiliar(),
                saved.getValorImovel(),
                saved.getValorEntrada(),
                saved.getMesesFinanciamento(),
                saved.getCidade(),
                saved.getEstado(),
                saved.getTipoImovel(),
                saved.getTipoUnidade(),
                saved.getAreaImovel(),
                saved.getFaixaMcmv(),
                saved.getImovelElegivel(),
                saved.getValorSubsidio(),
                saved.getValorFinanciado(),
                saved.getTaxaJurosAnual(),
                saved.getValorParcela(),
                saved.getParcelaMaxima(),
                saved.getParcelaCabeNaRenda(),
                saved.getEntradaEstimada()
        );
    }

    public List<SimulationResponse> findAll() {

        return repository.findAll()
                .stream()
                .map(simulation -> new SimulationResponse(
                        simulation.getId(),
                        simulation.getRendaFamiliar(),
                        simulation.getValorImovel(),
                        simulation.getValorEntrada(),
                        simulation.getMesesFinanciamento(),
                        simulation.getCidade(),
                        simulation.getEstado(),
                        simulation.getTipoImovel(),
                        simulation.getTipoUnidade(),
                        simulation.getAreaImovel(),
                        simulation.getFaixaMcmv(),
                        simulation.getImovelElegivel(),
                        simulation.getValorSubsidio(),
                        simulation.getValorFinanciado(),
                        simulation.getTaxaJurosAnual(),
                        simulation.getValorParcela(),
                        simulation.getParcelaMaxima(),
                        simulation.getParcelaCabeNaRenda(),
                        simulation.getEntradaEstimada()
                ))
                .toList();
    }

    private Boolean verificarElegibilidade(
            McmvFaixas faixa,
            BigDecimal valorImovel) {

        BigDecimal teto;

        if (faixa == McmvFaixas.FAIXA_1) {

            teto = new BigDecimal("275000");

        } else if (faixa == McmvFaixas.FAIXA_2) {

            teto = new BigDecimal("275000");

        } else if (faixa == McmvFaixas.FAIXA_3) {

            teto = new BigDecimal("400000");

        } else if (faixa == McmvFaixas.FAIXA_4) {

            teto = new BigDecimal("600000");

        } else {

            return false;
        }

        return valorImovel.compareTo(teto) <= 0;
    }

    private McmvFaixas identificarFaixa(
            BigDecimal rendaFamiliar) {

        if (rendaFamiliar.compareTo(
                new BigDecimal("3200")) <= 0) {

            return McmvFaixas.FAIXA_1;

        } else if (rendaFamiliar.compareTo(
                new BigDecimal("5000")) <= 0) {

            return McmvFaixas.FAIXA_2;

        } else if (rendaFamiliar.compareTo(
                new BigDecimal("9600")) <= 0) {

            return McmvFaixas.FAIXA_3;

        } else if (rendaFamiliar.compareTo(
                new BigDecimal("13000")) <= 0) {

            return McmvFaixas.FAIXA_4;

        } else {

            return McmvFaixas.FORA_DO_PROGRAMA;
        }
    }
}