package com.bancoazteca.bdm.cotizador.BDMCotizacion.entity;

public class DatosEntrada {
		
		private int tipoOferta;
		private int origen;
		private int pais;
		private int canal;
		private int sucursal;
		private int folio;
		private int capacidadPagoDisponible;
		private int montoVenta;
		private int productoId;
		private int periodo;
		private int plazo;
		private boolean esPromocion;
		private double iva;
		private String ws;
		private String usuario;
		private int sucursal_two;

		public DatosEntrada() {
		}

		public DatosEntrada(int tipoOferta, int origen, int pais, int canal, int sucursal, int folio,
				int capacidadPagoDisponible, int montoVenta, int productoId, int periodo, int plazo, boolean esPromocion,
				double iva, String ws, String usuario, int sucursal_two) {
			this.tipoOferta = tipoOferta;
			this.origen = origen;
			this.pais = pais;
			this.canal = canal;
			this.sucursal = sucursal;
			this.folio = folio;
			this.capacidadPagoDisponible = capacidadPagoDisponible;
			this.montoVenta = montoVenta;
			this.productoId = productoId;
			this.periodo = periodo;
			this.plazo = plazo;
			this.esPromocion = esPromocion;
			this.iva = iva;
			this.ws = ws;
			this.usuario = usuario;
			this.sucursal_two = sucursal_two;
		}

		public int getTipoOferta() {
			return this.tipoOferta;
		}

		public void setTipoOferta(int tipoOferta) {
			this.tipoOferta = tipoOferta;
		}

		public int getOrigen() {
			return this.origen;
		}

		public void setOrigen(int origen) {
			this.origen = origen;
		}

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

		public int getSucursal_two() {
			return this.sucursal_two;
		}

		public void setSucursal_two(int sucursal_two) {
			this.sucursal_two = sucursal_two;
		}

		public String toString() {
			return "DatosEntrada [tipoOferta=" + this.tipoOferta + ", origen=" + this.origen + ", pais=" + this.pais
					+ ", canal=" + this.canal + ", sucursal=" + this.sucursal + ", folio=" + this.folio
					+ ", capacidadPagoDisponible=" + this.capacidadPagoDisponible + ", montoVenta=" + this.montoVenta
					+ ", productoId=" + this.productoId + ", periodo=" + this.periodo + ", plazo=" + this.plazo
					+ ", esPromocion=" + this.esPromocion + ", iva=" + this.iva + ", ws=" + this.ws + ", usuario="
					+ this.usuario + ", sucursal_two=" + this.sucursal_two + "]";
		}

}
