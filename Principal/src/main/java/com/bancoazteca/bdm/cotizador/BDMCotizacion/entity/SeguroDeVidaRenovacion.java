package com.bancoazteca.bdm.cotizador.BDMCotizacion.entity;

public class SeguroDeVidaRenovacion {
	private int precioCalculado;
	private int sobrePrecio;
	private String nombre;
	private int precio;
	private int productoId;
	private String montoFinal;
	
	private int pagoNormalSeguro;
	private int pagoPuntualSeguro;
	private int pagoNormalSeguroCreditRun;
	private int pagoPuntualSeguroCreditRun;
	
	public SeguroDeVidaRenovacion() {
		super();
	}
	public SeguroDeVidaRenovacion(int precioCalculado
			/*, int precio, int sobrePrecio, int montoFinal, int productoId, String nombre,
			int pagoNormalSeguro, int pagoPuntualSeguro , int pagoNormalSeguroCreditRun ,int pagoPuntualSeguroCreditRun,  
			int sumaMuerteAccidental*/
			) {
		super();
		this.precioCalculado = precioCalculado;
		/*this.precio = precio;
		this.sobrePrecio = sobrePrecio;
		this.montoFinal = montoFinal;
		this.productoId = productoId;
		this.nombre = nombre;
		this.pagoPuntualSeguro = pagoPuntualSeguro;
		this.pagoNormalSeguro = pagoNormalSeguro;
		this.pagoNormalSeguroCreditRun = pagoNormalSeguroCreditRun;
		this.pagoPuntualSeguroCreditRun = pagoPuntualSeguroCreditRun;
		this.sumaMuerteAccidental = sumaMuerteAccidental;
		*/
	}
	
	public int getPrecioCalculado() {
		return precioCalculado;
	}
	public void setPrecioCalculado(int precioCalculado) {
		this.precioCalculado = precioCalculado;
	}
	public int getSobrePrecio() {
		return sobrePrecio;
	}
	public void setSobrePrecio(int sobrePrecio) {
		this.sobrePrecio = sobrePrecio;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getPrecio() {
		return precio;
	}
	public void setPrecio(int precio) {
		this.precio = precio;
	}
	
	public int getProductoId() {
		return productoId;
	}
	public void setProductoId(int productoId) {
		this.productoId = productoId;
	}
	public String getMontoFinal() {
		return montoFinal;
	}
	public void setMontoFinal(String montoFinal) {
		this.montoFinal = montoFinal;
	}
	public int getPagoNormalSeguro() {
		return pagoNormalSeguro;
	}
	public void setPagoNormalSeguro(int pagoNormalSeguro) {
		this.pagoNormalSeguro = pagoNormalSeguro;
	}
	public int getPagoPuntualSeguro() {
		return pagoPuntualSeguro;
	}
	public void setPagoPuntualSeguro(int pagoPuntualSeguro) {
		this.pagoPuntualSeguro = pagoPuntualSeguro;
	}
	public int getPagoNormalSeguroCreditRun() {
		return pagoNormalSeguroCreditRun;
	}
	public void setPagoNormalSeguroCreditRun(int pagoNormalSeguroCreditRun) {
		this.pagoNormalSeguroCreditRun = pagoNormalSeguroCreditRun;
	}
	public int getPagoPuntualSeguroCreditRun() {
		return pagoPuntualSeguroCreditRun;
	}
	public void setPagoPuntualSeguroCreditRun(int pagoPuntualSeguroCreditRun) {
		this.pagoPuntualSeguroCreditRun = pagoPuntualSeguroCreditRun;
	}
	
	
	
	
	
	

}
