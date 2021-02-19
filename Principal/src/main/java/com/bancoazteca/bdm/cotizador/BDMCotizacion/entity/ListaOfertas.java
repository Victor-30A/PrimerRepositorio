package com.bancoazteca.bdm.cotizador.BDMCotizacion.entity;

public class ListaOfertas {
	
private int precioCalculado;
private int precio;
private int sobrePrecio;
private int montoFinal;
private int productoId;
private String nombre;


public ListaOfertas(int precioCalculado, int precio, int sobrePrecio, int montoFinal, int productoId, String nombre) {
	
	this.precioCalculado = precioCalculado;
	this.precio = precio;
	this.sobrePrecio = sobrePrecio;
	this.montoFinal = montoFinal;
	this.productoId = productoId;
	this.nombre = nombre;
	
}

public int getPrecioCalculado() {
	return precioCalculado;
}
public void setPrecioCalculado(int precioCalculado) {
	this.precioCalculado = precioCalculado;
}
public int getPrecio() {
	return precio;
}
public void setPrecio(int precio) {
	this.precio = precio;
}
public int getSobrePrecio() {
	return sobrePrecio;
}
public void setSobrePrecio(int sobrePrecio) {
	this.sobrePrecio = sobrePrecio;
}
public int getMontoFinal() {
	return montoFinal;
}
public void setMontoFinal(int montoFinal) {
	this.montoFinal = montoFinal;
}
public int getProductoId() {
	return productoId;
}
public void setProductoId(int productoId) {
	this.productoId = productoId;
}
public String getNombre() {
	return nombre;
}
public void setNombre(String nombre) {
	this.nombre = nombre;
}

@Override
public String toString() {
	return "precioCalculado=" + precioCalculado + ", precio=" + precio + ", sobrePrecio=" + sobrePrecio
			+ ", montoFinal=" + montoFinal + ", productoId=" + productoId + ", nombre=" + nombre;
}

}
