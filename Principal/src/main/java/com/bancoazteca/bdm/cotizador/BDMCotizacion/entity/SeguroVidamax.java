package com.bancoazteca.bdm.cotizador.BDMCotizacion.entity;

public class SeguroVidamax {
	
	private int tipoOferta;
	private int origen;
	private Cliente cliente;
	private ProductoCredito productoCredito;
	private Seguro seguro;
	private InformacionBase informacionBase;
	
	public SeguroVidamax() {
		super();
	}

	public int getTipoOferta() {
		return this.tipoOferta;
	}

	public void setTipoOferta(int tipoOferta) {
		this.tipoOferta = tipoOferta;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public ProductoCredito getProductoCredito() {
		return productoCredito;
	}

	public void setProductoCredito(ProductoCredito productoCredito) {
		this.productoCredito = productoCredito;
	}

	public Seguro getSeguro() {
		return seguro;
	}

	public void setSeguro(Seguro seguro) {
		this.seguro = seguro;
	}

	public InformacionBase getInformacionBase() {
		return informacionBase;
	}

	public void setInformacionBase(InformacionBase informacionBase) {
		this.informacionBase = informacionBase;
	}

	public int getOrigen() {
		return this.origen;
	}

	public void setOrigen(int origen) {
		this.origen = origen;
	}

	@Override
	public String toString() {
		return "DatosEntrada [tipoOferta=" + tipoOferta + ", origen=" + origen + ", cliente=" + cliente
				+ ", productoCredito=" + productoCredito + ", seguro=" + seguro + ", informacionBase=" + informacionBase
				+ "]";
	}
	

}
