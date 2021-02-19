package com.bancoazteca.bdm.cotizador.BDMCotizacion.business;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.CreditRunCotizadorRequest;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.CreditRunResponseCotizador;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ConsumeServicioPOST {
	private static Logger logger = LoggerFactory.getLogger(ConsumeServicioPOST.class);

	@Autowired
	SeguroBusinessImpl seguroBusinessImpl;
	
	@Autowired
	UtilServicio utilServicio;
	
	ObjectMapper map;
	private static final String STATUSCODE="statusCode";

	public ResponseEntity<Object> consumeServicioPost(CreditRunCotizadorRequest request, String requestCifrado) {

		CreditRunResponseCotizador responseTO = new CreditRunResponseCotizador();
		
		logger.info("MENSAJE DE ENTRADA");
		
		String peticion ="{\r\n" + 
				"	\"tipoOferta\": 1,\r\n" + 
				"	\"origen\": 1,\r\n" + 
				"	\"cliente\": {\r\n" + 
				"		\"pais\": 1,\r\n" + 
				"		\"canal\": 1,\r\n" + 
				"		\"sucursal\": 8624,\r\n" + 
				"		\"folio\": 7,\r\n" + 
				"		\"capacidadPagoDisponible\": 30\r\n" + 
				"	},\r\n" + 
				"	\"productoCredito\": {\r\n" + 
				"		\"montoVenta\": 0,\r\n" + 
				"		\"productoId\": 0,\r\n" + 
				"		\"periodo\": 1,\r\n" + 
				"		\"plazo\": 0\r\n" + 
				"	},\r\n" + 
				"	\"seguro\": {\r\n" + 
				"		\"esPromocion\": false,\r\n" + 
				"		\"iva\": 0.16\r\n" + 
				"	},\r\n" + 
				"	\"informacionBase\": {\r\n" + 
				"		\"ws\": \"BAZ1337242\",\r\n" + 
				"		\"usuario\": \"111111\",\r\n" + 
				"		\"sucursal\": 1\r\n" + 
				"	}\r\n" + 
				"}";
		
		//String respuesta = utilServicio.invocaServicioPostHeaderJson("http://10.50.53.239:8080/SegurosServicios/VidamaxUnificada", peticion, 20000, 20000);
		
		logger.info("LA RESPUESTA DE ESTE SERVIVIO FUE " + ejecutaEnvioEdoCuenta());
		
		return new ResponseEntity<>(responseTO, HttpStatus.OK);
	}
	
	private String ejecutaEnvioEdoCuenta() {
		String response = "";
		String peticion ="{\r\n" + 
				"	\"tipoOferta\": 1,\r\n" + 
				"	\"origen\": 1,\r\n" + 
				"	\"cliente\": {\r\n" + 
				"		\"pais\": 1,\r\n" + 
				"		\"canal\": 1,\r\n" + 
				"		\"sucursal\": 8624,\r\n" + 
				"		\"folio\": 7,\r\n" + 
				"		\"capacidadPagoDisponible\": 30\r\n" + 
				"	},\r\n" + 
				"	\"productoCredito\": {\r\n" + 
				"		\"montoVenta\": 0,\r\n" + 
				"		\"productoId\": 0,\r\n" + 
				"		\"periodo\": 1,\r\n" + 
				"		\"plazo\": 0\r\n" + 
				"	},\r\n" + 
				"	\"seguro\": {\r\n" + 
				"		\"esPromocion\": false,\r\n" + 
				"		\"iva\": 0.16\r\n" + 
				"	},\r\n" + 
				"	\"informacionBase\": {\r\n" + 
				"		\"ws\": \"BAZ1337242\",\r\n" + 
				"		\"usuario\": \"111111\",\r\n" + 
				"		\"sucursal\": 1\r\n" + 
				"	}\r\n" + 
				"}";
		try {
			JsonNode root = null;
			
			String url = "http://10.50.53.239:8080/SegurosServicios/VidamaxUnificada";
			logger.info("URL " + url);
			//String peticion = utilServicio.convertObjectToJson("");
			//logger.info(" Peticion envioEdoCuenta"  + peticion);
			if(url != null) {
				map = new ObjectMapper();
				String respuesta = utilServicio.invocaServicioPostHeaderJson(url, peticion, 1000, 1000);
				root = map.readTree(respuesta);
				response = root.path(STATUSCODE).asText();
			}
		}catch(IOException e) {
			logger.info("No se pudo obtener la respuesta "+ e.getMessage());
			//throw new MessageException(CodigoErrorEnum.ERROR_ENVIO_ESTADO_CUENTA.getCodigo());
		}catch(NullPointerException e) {
			logger.info("No se pudo ejecutar la peticion" + e.getMessage());
			//throw new MessageException(CodigoErrorEnum.ERROR_ENVIO_ESTADO_CUENTA.getCodigo());
		}
		return response;
	}
	
	
	
	
	
}