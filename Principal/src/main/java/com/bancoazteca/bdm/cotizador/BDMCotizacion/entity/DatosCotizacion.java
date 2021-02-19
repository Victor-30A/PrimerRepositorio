package com.bancoazteca.bdm.cotizador.BDMCotizacion.entity;

import java.util.ArrayList;
import java.util.List;

public class DatosCotizacion {
	private String idProducto;
	private int monto;
	private int idPeriodicidad;
	private String periodicidad;
	private String idPlazo;
	private String plazo;
	private int pagoNormal;
	private int pagoPuntual;
	private int pagoNormalPromo;
	private int pagoPuntualPromo;
	private int montoTotal;
	private int interes;
	private boolean aplicaSeguro;
	
    private List<SeguroDeVida> listaOfertas = new ArrayList<>();
    
    private List<SeguroDeVidaRenovacion> listasOfertaRenovacion = new ArrayList<>();
	
	public boolean isAplicaSeguro() {
		return aplicaSeguro;
	}
	public void setAplicaSeguro(boolean aplicaSeguro) {
		this.aplicaSeguro = aplicaSeguro;
	}

	public String getIdProducto() {
		return idProducto;
	}
	public void setIdProducto(String idProducto) {
		this.idProducto = idProducto;
	}
	public int getMonto() {
		return monto;
	}
	public void setMonto(int monto) {
		this.monto = monto;
	}
	public int getIdPeriodicidad() {
		return idPeriodicidad;
	}
	public void setIdPeriodicidad(int idPeriodicidad) {
		this.idPeriodicidad = idPeriodicidad;
	}
	public String getPeriodicidad() {
		return periodicidad;
	}
	public void setPeriodicidad(String periodicidad) {
		this.periodicidad = periodicidad;
	}
	public String getIdPlazo() {
		return idPlazo;
	}
	public void setIdPlazo(String idPlazo) {
		this.idPlazo = idPlazo;
	}
	public String getPlazo() {
		return plazo;
	}
	public void setPlazo(String plazo) {
		this.plazo = plazo;
	}
	public int getPagoNormal() {
		return pagoNormal;
	}
	public void setPagoNormal(int pagoNormal) {
		this.pagoNormal = pagoNormal;
	}
	public int getPagoPuntual() {
		return pagoPuntual;
	}
	public void setPagoPuntual(int pagoPuntual) {
		this.pagoPuntual = pagoPuntual;
	}
	public int getMontoTotal() {
		return montoTotal;
	}
	public void setMontoTotal(int montoTotal) {
		this.montoTotal = montoTotal;
	}
	public int getIntereses() {
		return interes;
	}
	public void setIntereses(int interes) {
		this.interes = interes;
	}
	public List<SeguroDeVida> getListaOfertas() {
		return listaOfertas;
	}
	public void setListaOfertas(List<SeguroDeVida> listaOfertas) {
		this.listaOfertas = listaOfertas;
	}
	

	public int getPagoNormalPromo() {
		return pagoNormalPromo;
	}
	public void setPagoNormalPromo(int pagoNormalPromo) {
		this.pagoNormalPromo = pagoNormalPromo;
	}
	public int getPagoPuntualPromo() {
		return pagoPuntualPromo;
	}
	public void setPagoPuntualPromo(int pagoPuntualPromo) {
		this.pagoPuntualPromo = pagoPuntualPromo;
	}
	
	public List<SeguroDeVidaRenovacion> getListasOfertaRenovacion() {
		return listasOfertaRenovacion;
	}
	public void setListasOfertaRenovacion(List<SeguroDeVidaRenovacion> listasOfertaRenovacion) {
		this.listasOfertaRenovacion = listasOfertaRenovacion;
	}
	@Override
	public String toString() {
		return "idProducto=" + idProducto + ", monto=" + monto + ", idPeriodicidad=" + idPeriodicidad
				+ ", periodicidad=" + periodicidad + ", idPlazo=" + idPlazo + ", plazo=" + plazo + ", pagoNormal="
				+ pagoNormal + ", pagoPuntual=" + pagoPuntual + ", pagoNormalPromo=" + pagoNormalPromo
				+ ", pagoPuntualPromo=" + pagoPuntualPromo + ", montoTotal=" + montoTotal + ", interes=" + interes
				+ ", aplicaSeguro=" + aplicaSeguro + ", listaOfertas=" + listaOfertas + "";
	}
	

	

}
