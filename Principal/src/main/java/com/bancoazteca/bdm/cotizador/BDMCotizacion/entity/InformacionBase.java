package com.bancoazteca.bdm.cotizador.BDMCotizacion.entity;

public class InformacionBase {

	private String ws;
	private String usuario;
	private int sucursal;
	
	/*public InformacionBase(String ws, String usuario, int sucursal_two) {
		super();
		this.ws = ws;
		this.usuario = usuario;
		this.sucursal_two = sucursal_two;
	}
*/
	public String getWs() {
		return this.ws;
	}

	public void setWs(String ws) {
		this.ws = ws;
	}

	public String getUsuario() {
		return this.usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public int getSucursal() {
		return this.sucursal;
	}

	public void setSucursal(int sucursal) {
		this.sucursal = sucursal;
	}
	@Override
	public String toString() {
		return "InformacionBase [ws=" + ws + ", usuario=" + usuario + ", sucursal=" + sucursal + "]";
	}
	
}
