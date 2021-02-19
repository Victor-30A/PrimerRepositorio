package com.bancoazteca.bdm.cotizador.BDMCotizacion.entity;

import java.io.Serializable;
import java.util.List;


public class CotizadorResponse implements Serializable{
	private static final long serialVersionUID = 1L;
	private boolean aptoSeguro;
	private List<DatosCotizacion> listaCotizador;
	
	public boolean isAptoSeguro() {
		return aptoSeguro;
	}

	public void setAptoSeguro(boolean aptoSeguro) {
		this.aptoSeguro = aptoSeguro;
	}
	
	public List<DatosCotizacion> getListaCotizador() {
		return listaCotizador;
	}

	public void setListaCotizador(List<DatosCotizacion> listaCotizador) {
		this.listaCotizador = listaCotizador;
	}
}