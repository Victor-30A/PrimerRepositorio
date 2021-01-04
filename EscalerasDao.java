package com.bancoazteca.bdm.cotizador.BDMCotizacion.dao;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class EscalerasDao {
	
	//  @Autowired  UtilCredito util;
	  
	  private static Logger LOGGER = LoggerFactory.getLogger(EscalerasDao.class);
	  
	 // private final String URL = PropertiesManager.getinstance().getProperty(ConstantesCredito.CREDITO_PROPERTIES,"credito.npam.valor.url.escaleras");
	  //private final String URL = "https://10.51.251.151:10504/campanasCobranza/v1/escaleras";
	  private final String URL  = "";
	  
	  public String getEscaleras(Map<String, String> headersServ, String request) {

	    String respuesta = "";
	    LOGGER.info("Consumiendo escaleras en: " + URL);
	    LOGGER.info("Consumiendo escaleras con request: " + request);
	    
	    try {
	    	respuesta = "";
	      //respuesta = util.invocaServicioPostVariosHeaders(URL, request, 5000, 5000, headersServ);
	    } catch (Exception exc) {
	      LOGGER.info("Ocurrío una incidencia llamando el servicio de campanasCobranza/v1/escaleras, " + "Mensaje: " + exc.getMessage().toString()
	          + "Causa: " + exc.getCause().toString());
	     // throw new MessageException(CodigoErrorEnum.ERROR_GENERICO.getCodigo());
	    }
	    
	    return respuesta;
	  }


}
