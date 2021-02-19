package com.bancoazteca.bdm.cotizador.BDMCotizacion.entity;

public class Cliente {
	private int pais;
	private int canal;
	private int sucursal;
	private int folio;
	private int capacidadPagoDisponible;
	private int capacidadPagoForzada;

	
	/*public Cliente(int pais, int canal, int sucursal, int folio, int capacidadPagoDisponible) {
		super();
		this.pais = pais;
		this.canal = canal;
		this.sucursal = sucursal;
		this.folio = folio;
		this.capacidadPagoDisponible = capacidadPagoDisponible;
	}*/

	public int getPais() {
		return this.pais;
	}

	public void setPais(int pais) {
		this.pais = pais;
	}

	public int getCanal() {
		return this.canal;
	}

	public void setCanal(int canal) {
		this.canal = canal;
	}

	public int getSucursal() {
		return this.sucursal;
	}

	public void setSucursal(int sucursal) {
		this.sucursal = sucursal;
	}

	public int getFolio() {
		return this.folio;
	}

	public void setFolio(int folio) {
		this.folio = folio;
	}

	public int getCapacidadPagoDisponible() {
		return this.capacidadPagoDisponible;
	}

	public void setCapacidadPagoDisponible(int capacidadPagoDisponible) {
		this.capacidadPagoDisponible = capacidadPagoDisponible;
	}
	
	

	public int getCapacidadPagoForzada() {
		return capacidadPagoForzada;
	}

	public void setCapacidadPagoForzada(int capacidadPagoForzada) {
		this.capacidadPagoForzada = capacidadPagoForzada;
	}

	@Override
	public String toString() {
		return "Cliente [pais=" + pais + ", canal=" + canal + ", sucursal=" + sucursal + ", folio=" + folio
				+ ", capacidadPagoDisponible=" + capacidadPagoDisponible + ",capacidadPagoForzada="+capacidadPagoForzada+ "]";
	}
	
	
}
