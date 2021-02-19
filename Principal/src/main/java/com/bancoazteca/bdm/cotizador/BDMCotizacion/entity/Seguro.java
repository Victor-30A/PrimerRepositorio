package com.bancoazteca.bdm.cotizador.BDMCotizacion.entity;

public class Seguro {
	private boolean esPromocion;
	private double iva;
	private String productoIDCotizador;

	/*
	public Seguro(boolean esPromocion, double iva) {
		super();
		this.esPromocion = esPromocion;
		this.iva = iva;
	}
*/
	public boolean isEsPromocion() {
		return this.esPromocion;
	}

	public void setEsPromocion(boolean esPromocion) {
		this.esPromocion = esPromocion;
	}

	public double getIva() {
		return this.iva;
	}

	public void setIva(double iva) {
		this.iva = iva;
	}
	
	

	public String getProductoIDCotizador() {
		return productoIDCotizador;
	}

	public void setProductoIDCotizador(String productoIDCotizador) {
		this.productoIDCotizador = productoIDCotizador;
	}

	@Override
	public String toString() {
		return "Seguro [esPromocion=" + esPromocion + ", iva=" + iva + ",productoIDCotizador="+productoIDCotizador+"]";
	}

}
