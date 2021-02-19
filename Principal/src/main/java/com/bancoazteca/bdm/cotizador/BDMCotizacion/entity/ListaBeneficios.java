package com.bancoazteca.bdm.cotizador.BDMCotizacion.entity;

public class ListaBeneficios {

	private String descripcion;
    private int idElemento;
    
	public ListaBeneficios() {
		
	}

	public ListaBeneficios(String descripcion, int idElemento) {
		this.descripcion = descripcion;
		this.idElemento = idElemento;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public int getIdElemento() {
		return idElemento;
	}

	public void setIdElemento(int idElemento) {
		this.idElemento = idElemento;
	}

	@Override
	public String toString() {
		return "descripcion=" + descripcion + ", idElemento=" + idElemento;
	}

}
