package com.bancoazteca.bdm.cotizador.BDMCotizacion.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.InformacionDappSesion;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.ProductoSeguroResponse;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.TransferenciaRequestTO;


@Service
public class OperacionTransferir {
	private static Logger logger = LoggerFactory.getLogger(OperacionTransferir.class);
	
	@Autowired private SegurosAztecaDapp segurosAztecaDapp;

	
	public ResponseEntity <Object> operacionTransferir(TransferenciaRequestTO request, String requestCifrado){
		ProductoSeguroResponse responseTO = new ProductoSeguroResponse();
		logger.info("Comienza emision de poliza Seguros Azteca para empleado");
		InformacionDappSesion dappSession = new InformacionDappSesion();
		dappSession.setDapp_code("2aae8657-ca0d-41e7-afed-154c576424ff");
		
		String curp =  "AAOH900131HDFMLG00";
		String rfc = "AAOH9001311k3";
		String nombreCliente = "Hugo Amaro";
		
		
		emitirPolizasSegurosAzteca(nombreCliente,curp,rfc,segurosAztecaDapp);
		return new ResponseEntity<>(responseTO,HttpStatus.OK);	
	}
	
	public void emitirPolizasSegurosAzteca(String cliente,String rfc, String curp, SegurosAztecaDapp segurosAztecaDapp) {
		
		
		InformacionDappSesion informacionDapp = new InformacionDappSesion();
		informacionDapp.setAccount("00000");
		informacionDapp.setAddress("Direccion");
		informacionDapp.setAmount(200.0);
		informacionDapp.setDapp_code("CODE");
		informacionDapp.setDescription("Descripcion");
		
		try {
			segurosAztecaDapp.emitirPolizaSegurosAzteca(rfc,curp,informacionDapp);
		} catch (Exception e) {
			logger.info("Error al realizar la operacion {}" , e);
		}
	}
}