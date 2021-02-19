package com.bancoazteca.bdm.cotizador.BDMCotizacion.business;


import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.CodigoSeguro;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.CreditRunCotizadorRequest;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.CreditRunRequest;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.CreditRunResponseCotizador;
import com.google.gson.Gson;

@Service
public class RenovacionPersonalizado {
	private static Logger logger = LoggerFactory.getLogger(RenovacionPersonalizado.class);
	
	@Autowired
	private UtilSeguroCredito utilSeguroCredito;

	@Autowired
	SeguroBusinessImpl seguroImple;

	@Autowired
	NpamBusiness npam;

	private final int FLUJORENOVACION = 2;

	public ResponseEntity<Object> renovacionPersonalizado(CreditRunCotizadorRequest request, String requestCifrado) {
		CreditRunResponseCotizador responseTO = new CreditRunResponseCotizador();

		Gson gson = new Gson();
		CreditRunCotizadorRequest crcr = gson.fromJson(requestCifrado, CreditRunCotizadorRequest.class);
		//logger.info("Los datos del request son " + crcr.toString());
		
		request = new CreditRunCotizadorRequest();
		request.setMax(crcr.getMax());
		request.setMin(crcr.getMin());
		request.setTasa(crcr.getTasa());
		request.setCanal(crcr.getCanal());
		request.setSucursal(crcr.getSucursal());	
		request.setPais(crcr.getPais());
		request.setPeriodo(crcr.getPeriodo());
		request.setCapacidadDePago(crcr.getCapacidadDePago());
		request.setPlazoIni(crcr.getPlazoIni());
		request.setPlazoFin(crcr.getPlazoFin());
		request.setTipoFlujo(crcr.getTipoFlujo());
		request.setPaisRen(crcr.getPaisRen());
		request.setCanalRen(crcr.getCanalRen());
		request.setSucursalRen(crcr.getSucursalRen());
		request.setPedido(crcr.getPedido());
		request.setAplicaCreditrun(crcr.isAplicaCreditrun());
		request.setTasaBase(crcr.getTasaBase());
		
		request.setCliente(crcr.getCliente());
		
		//logger.info("Se lleno el request con los datos " + request.toString());

		if (Validations.isNullOrEmpty(request)) {
			logger.info("El Request tiene un campo vacio");
		}

		CreditRunBusiness creditRunBusiness = new CreditRunBusiness();
		CodigoSeguro codigoSeguro = new CodigoSeguro();
		

		if (request.getTipoFlujo() == FLUJORENOVACION) {
			//logger.info("Entrando al modulo de renovacion mas modulo de seguros");
			try {
			//	logger.info("Consultando lista de abonos con tasa unica de renovacion + seguros");
				responseTO.setListaCotizador(creditRunBusiness.creditRunFlow(new CreditRunRequest(request.getMin(),
						request.getMax(), request.getTasa(), request.getCanal(), request.getSucursal(),
						request.getPais(), request.getPeriodo(), request.getCapacidadDePago(), request.getPlazoIni(),
						request.getPlazoFin(), request.isAplicaCreditrun(), request.getTasaBase())));
			} 
			catch (Exception exc) {
				logger.error("Error con tasas centralizadas recompra: " + exc.getStackTrace().toString()+ "Causa del error: " + exc.getCause().toString() + "*******" + exc.toString());
			}

			if (!request.getTasa().equalsIgnoreCase("F")) {
				/* Aqui llenamos el objeto del cliente de renovacion para seguros */
				//seguroImple.llenaSegurosRenovacionPersonalizada(request.getTipoFlujo(), request.getCapacidadDePago(),request.getPedido(), request.getPaisRen(), request.getCanalRen(), request.getSucursalRen(),"");
				seguroImple.llenaSegurosRenovacionPersonalizada(request.getTipoFlujo(), request.getCapacidadDePago(),request.getPedido(), request.getPaisRen(), request.getCanalRen(), request.getSucursalRen(),"RENUEVA",crcr.getCliente());
				utilSeguroCredito.evaluaListaSegurosCreditoRenovacion(responseTO, crcr.getCapacidadDePago());
				
			}
		}

		try {
			responseTO.setEscaleras(npam.getEscaleras(request));

		} catch (Exception exc) {

			logger.error("Incidencia en invocacion de Escaleras: " + exc.getStackTrace());
		}
		return new ResponseEntity<>(responseTO, HttpStatus.OK);
	}
	
	public ResponseEntity<String> leeArchivo() {
		
		String respuesta = null;
		try {
			//respuesta = read("E:/listaEscalera_OLD.json");
			respuesta = read("E:/fincadoClienteNuevo.json");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return new ResponseEntity<>(respuesta,HttpStatus.OK);
	
	}
	
	 public static String read(String file) throws IOException {
		    StringBuilder content = new StringBuilder();
		    try (BufferedReader reader = Files.newBufferedReader(Paths.get(file, new String[0]), 
		          Charset.defaultCharset())) {
		      String line = null;
		      while ((line = reader.readLine()) != null) {
		        content.append(line).append("\n");
		      }
		      return content.toString();
		    } 
		  }
}