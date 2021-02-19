package com.bancoazteca.bdm.cotizador.BDMCotizacion.business;

import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.Cliente;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.Sucursal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EscalerasRequest {


    private Cliente cliente;
    private Sucursal sucursal;
    private int idProducto;
    private String plataforma;
    private String tipoCliente;
    private int periodo;
    private int tipoPromocion;
    private int cdpDisponible;

    @JsonProperty("cliente")
    public Cliente getCliente() {
        return cliente;
    }

    @JsonProperty("cliente")
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    @JsonProperty("sucursal")
    public Sucursal getSucursal() {
        return sucursal;
    }

    @JsonProperty("sucursal")
    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

    @JsonProperty("idProducto")
    public int getIdProducto() {
        return idProducto;
    }

    @JsonProperty("idProducto")
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    @JsonProperty("plataforma")
    public String getPlataforma() {
        return plataforma;
    }

    @JsonProperty("plataforma")
    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }

    @JsonProperty("tipoCliente")
    public String getTipoCliente() {
        return tipoCliente;
    }

    @JsonProperty("tipoCliente")
    public void setTipoCliente(String tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

    @JsonProperty("periodo")
    public int getPeriodo() {
        return periodo;
    }

    @JsonProperty("periodo")
    public void setPeriodo(int periodo) {
        this.periodo = periodo;
    }

    @JsonProperty("tipoPromocion")
    public int getTipoPromocion() {
        return tipoPromocion;
    }

    @JsonProperty("tipoPromocion")
    public void setTipoPromocion(int tipoPromocion) {
        this.tipoPromocion = tipoPromocion;
    }

    @JsonProperty("cdpDisponible")
    public int getCdpDisponible() {
        return cdpDisponible;
    }

    @JsonProperty("cdpDisponible")
    public void setCdpDisponible(int cdpDisponible) {
        this.cdpDisponible = cdpDisponible;
    }

    @Override
    public String toString() {
      return "{cliente: " + cliente + ", sucursal: " + sucursal + ", idProducto: " + idProducto
          + ", plataforma: " + plataforma + ", tipoCliente: " + tipoCliente + ", periodo: "
          + periodo + ", tipoPromocion: " + tipoPromocion + ", cdpDisponible: " + cdpDisponible
          + "}";
    }

}
