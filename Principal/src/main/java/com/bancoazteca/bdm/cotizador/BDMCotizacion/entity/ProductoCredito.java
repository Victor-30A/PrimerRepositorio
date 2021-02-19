package com.bancoazteca.bdm.cotizador.BDMCotizacion.entity;

public class ProductoCredito {
	private int montoVenta;
	private int productoId;
	private int periodo;
	private int plazo;
	
	/*Parametros de renovacion*/
	private int pedidoARenovar;
	private int paisPedido;
	private int canalPedido;
	private int sucursalPedido;
	
	

	public int getMontoVenta() {
		return this.montoVenta;
	}

	public void setMontoVenta(int montoVenta) {
		this.montoVenta = montoVenta;
	}

	public int getProductoId() {
		return this.productoId;
	}

	public void setProductoId(int productoId) {
		this.productoId = productoId;
	}

	public int getPeriodo() {
		return this.periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public int getPlazo() {
		return this.plazo;
	}

	public void setPlazo(int plazo) {
		this.plazo = plazo;
	}
	
	public int getPedidoARenovar() {
		return pedidoARenovar;
	}

	public void setPedidoARenovar(int pedidoARenovar) {
		this.pedidoARenovar = pedidoARenovar;
	}

	public int getPaisPedido() {
		return paisPedido;
	}

	public void setPaisPedido(int paisPedido) {
		this.paisPedido = paisPedido;
	}

	public int getCanalPedido() {
		return canalPedido;
	}

	public void setCanalPedido(int canalPedido) {
		this.canalPedido = canalPedido;
	}

	public int getSucursalPedido() {
		return sucursalPedido;
	}

	public void setSucursalPedido(int sucursalPedido) {
		this.sucursalPedido = sucursalPedido;
	}

	@Override
	public String toString() {
		return "ProductoCredito [montoVenta=" + montoVenta + ", productoId=" + productoId + ", periodo=" + periodo
				+ ", plazo=" + plazo + ",pedidoARenovar="+pedidoARenovar+" ,paisPedido="+paisPedido+",canalPedido="+canalPedido+",sucursalPedido="+sucursalPedido+"]";
	}
	
}
