package com.bancoazteca.bdm.cotizador.BDMCotizacion.entity;

import java.io.Serializable;

public class CotizadorRequest {
	private static final long serialVersionUID = 1L;
	
	private int idProducto;
	private int monto;
	private int origen;
	private String idSolicitud;
	private String fechaNacimiento;
	private int plataforma;
	private String icu;
	private String clienteUnico;
	private int mensajeOrigen;
	//Hugo
	private String pais;
	private String canal;
	private String sucursal;
	private String folio;
	
	
	public int getMensajeOrigen() {
		return mensajeOrigen;
	}
	public void setMensajeOrigen(int mensajeOrigen) {
		this.mensajeOrigen = mensajeOrigen;
	}
	
	public int getPlataforma() {
		return plataforma;
	}
	public void setPlataforma(int plataforma) {
		this.plataforma = plataforma;
	}
	public String getIcu() {
		return icu;
	}
	public void setIcu(String icu) {
		this.icu = icu;
	}

	public String getClienteUnico() {
		return clienteUnico;
	}
	public void setClienteUnico(String clienteUnico) {
		this.clienteUnico = clienteUnico;
	}
	
	public String getFechaNacimiento() {
		return fechaNacimiento;
	}
	public void setFechaNacimiento(String fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}
	
	public String getIdSolicitud() {
		return idSolicitud;
	}
	public void setIdSolicitud(String idSolicitud) {
		this.idSolicitud = idSolicitud;
	}
	
	public int getIdProducto() {
		return idProducto;
	}
	public void setIdProducto(int producto) {
		this.idProducto = producto;
	}
	public int getMonto() {
		return monto;
	}
	public void setMonto(int monto) {
		this.monto = monto;
	}
	public int getOrigen() {
		return origen;
	}
	public void setOrigen(int origen) {
		this.origen = origen;
	}
	public String getPais() {
		return pais;
	}
	public void setPais(String pais) {
		this.pais = pais;
	}
	public String getCanal() {
		return canal;
	}
	public void setCanal(String canal) {
		this.canal = canal;
	}
	public String getSucursal() {
		return sucursal;
	}
	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}
	public String getFolio() {
		return folio;
	}
	public void setFolio(String folio) {
		this.folio = folio;
	}


}
