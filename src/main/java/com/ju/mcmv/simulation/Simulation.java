package com.ju.mcmv.simulation;

import java.math.BigDecimal;

import com.ju.mcmv.regras.McmvFaixas;
import com.ju.mcmv.regras.TipoImovel;
import com.ju.mcmv.regras.TipoUnidade;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "TBL_MCMV_SIMULACAO")
public class Simulation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal parcelaMaxima;
    private Boolean parcelaCabeNaRenda;

    @NotNull(message = "A renda é obrigatória")
    @Positive(message = "A renda deve ser maior que zero")
    private BigDecimal rendaFamiliar;

    @NotNull(message = "O valor do imóvel é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor do imóvel deve ser maior que zero")
    private BigDecimal valorImovel;

    @NotNull(message = "O valor da entrada é obrigatório")
    @PositiveOrZero(message = "A entrada não pode ser negativa")
    private BigDecimal valorEntrada;

    @NotNull(message = "O prazo de financiamento é obrigatório")
    @Min(value = 1, message = "O prazo deve ser de pelo menos 1 mês")
    @Max(value = 420, message = "O prazo máximo é de 420 meses (35 anos)")
    private Integer mesesFinanciamento;

    @NotNull(message = "A cidade é obrigatória")
    private String cidade;

    @NotNull(message = "O estado é obrigatório")
    private String estado;

    @NotNull(message = "O tipo do imóvel é obrigatório")
    @Enumerated(EnumType.STRING)
    private TipoImovel tipoImovel;

    @Enumerated(EnumType.STRING)
    private McmvFaixas faixaMcmv;

    private BigDecimal entradaEstimada;
    private Boolean imovelElegivel;
    private BigDecimal valorSubsidio;
    private BigDecimal valorFinanciado;
    private BigDecimal taxaJurosAnual;
    private BigDecimal valorParcela;
    private BigDecimal areaImovel;


     public BigDecimal getParcelaMaxima() {
        return parcelaMaxima;
    }

    public void setParcelaMaxima(BigDecimal parcelaMaxima) {
        this.parcelaMaxima = parcelaMaxima;
    }

    public Boolean getParcelaCabeNaRenda() {
        return parcelaCabeNaRenda;
    }

    public void setParcelaCabeNaRenda(Boolean parcelaCabeNaRenda) {
        this.parcelaCabeNaRenda = parcelaCabeNaRenda;
    }

    public BigDecimal getAreaImovel() {
        return areaImovel;
    }

     public BigDecimal getEntradaEstimada() {
        return entradaEstimada;
    }

    public void setEntradaEstimada(BigDecimal entradaEstimada) {
        this.entradaEstimada = entradaEstimada;
    }

    public void setAreaImovel(BigDecimal areaImovel) {
        this.areaImovel = areaImovel;
    }

    public TipoUnidade getTipoUnidade() {
        return tipoUnidade;
    }

    public void setTipoUnidade(TipoUnidade tipoUnidade) {
        this.tipoUnidade = tipoUnidade;
    }

    @Enumerated(EnumType.STRING)
    private TipoUnidade tipoUnidade;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getRendaFamiliar() {
        return rendaFamiliar;
    }

    public void setRendaFamiliar(BigDecimal rendaFamiliar) {
        this.rendaFamiliar = rendaFamiliar;
    }

    public BigDecimal getValorImovel() {
        return valorImovel;
    }

    public void setValorImovel(BigDecimal valorImovel) {
        this.valorImovel = valorImovel;
    }

    public BigDecimal getValorEntrada() {
        return valorEntrada;
    }

    public void setValorEntrada(BigDecimal valorEntrada) {
        this.valorEntrada = valorEntrada;
    }

    public Integer getMesesFinanciamento() {
        return mesesFinanciamento;
    }

    public void setMesesFinanciamento(Integer mesesFinanciamento) {
        this.mesesFinanciamento = mesesFinanciamento;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public TipoImovel getTipoImovel() {
        return tipoImovel;
    }

    public void setTipoImovel(TipoImovel tipoImovel) {
        this.tipoImovel = tipoImovel;
    }

    public McmvFaixas getFaixaMcmv() {
        return faixaMcmv;
    }

    public void setFaixaMcmv(McmvFaixas faixaMcmv) {
        this.faixaMcmv = faixaMcmv;
    }

    public Boolean getImovelElegivel() {
        return imovelElegivel;
    }

    public void setImovelElegivel(Boolean imovelElegivel) {
        this.imovelElegivel = imovelElegivel;
    }

    public BigDecimal getValorSubsidio() {
        return valorSubsidio;
    }

    public void setValorSubsidio(BigDecimal valorSubsidio) {
        this.valorSubsidio = valorSubsidio;
    }

    public BigDecimal getValorFinanciado() {
        return valorFinanciado;
    }

    public void setValorFinanciado(BigDecimal valorFinanciado) {
        this.valorFinanciado = valorFinanciado;
    }

    public BigDecimal getTaxaJurosAnual() {
        return taxaJurosAnual;
    }

    public void setTaxaJurosAnual(BigDecimal taxaJurosAnual) {
        this.taxaJurosAnual = taxaJurosAnual;
    }

    public BigDecimal getValorParcela() {
        return valorParcela;
    }

    public void setValorParcela(BigDecimal valorParcela) {
        this.valorParcela = valorParcela;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result +
                ((id == null) ? 0 : id.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        Simulation other = (Simulation) obj;

        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id)) {
            return false;
        }

        return true;
    }
}