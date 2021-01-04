package com.bancoazteca.bdm.cotizador.BDMCotizacion.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

@Component
public class SegurosDaoImpl {
	
private static final Logger logger = LoggerFactory.getLogger(SegurosDaoImpl.class);
	
	
	@Autowired
	private UtilSeguros utilSeguros;
	
	public String obtenerCookieLogin(String jsonRequest , int connectTimeout, int readTimeout) {
		logger.info ("Consultando el metodo dao [obtener cookie de loggin Accel-e]");
		String respuesta = null;
		
		try {
			String urlWs = "http://10.81.24.41:8080/ws/rest/login";
			respuesta =  utilSeguros.incovaServicioPostHeader(urlWs, jsonRequest, connectTimeout, readTimeout);
			
		}catch(RestClientException e) {
			logger.info("Incidencia Dao en obtener la cookien en login Accel-e en " + e);
			
		}	
		return respuesta;
	}
}