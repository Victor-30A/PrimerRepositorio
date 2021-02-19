package com.bancoazteca.bdm.cotizador.BDMCotizacion.entity;

public class SobrePrecio {
	private String identificador;
	private int idProducto;
	private int plazo;
	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}
	public int getIdProducto() {
		return idProducto;
	}
	public void setIdProducto(int idProducto) {
		this.idProducto = idProducto;
	}
	public int getPlazo() {
		return plazo;
	}
	public void setPlazo(int plazo) {
		this.plazo = plazo;
	}

}
