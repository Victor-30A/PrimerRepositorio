package com.bancoazteca.bdm.cotizador.BDMCotizacion.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.InformacionDappSesion;

public class ThreadDappSeguro implements Runnable {
	private String rfc;
	private String curp;
	private SegurosAztecaDapp segurosAztecaDapp;
	//private InformacionDappSesion informacionDapp;
	//private Consultado objetoCliente;
	private static final Logger log = LoggerFactory.getLogger(SegurosAztecaDapp.class);
	
	public ThreadDappSeguro(String rfc, String curp, SegurosAztecaDapp segurosAztecaDapp) {
		super();
		this.rfc = rfc;
		this.curp = curp;
		this.segurosAztecaDapp = segurosAztecaDapp;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		InformacionDappSesion informacionDapp = new InformacionDappSesion();
		informacionDapp.setAccount("00000");
		informacionDapp.setAddress("Direccion");
		informacionDapp.setAmount(200.0);
		informacionDapp.setDapp_code("CODE");
		informacionDapp.setDescription("Descripcion");
		
		try {
			segurosAztecaDapp.emitirPolizaSegurosAzteca(rfc,curp,informacionDapp);
		} catch (Exception e) {
			log.info("Error al realizar la operacion {}" , e);
		}
		
	}
	

}
