package com.bancoazteca.bdm.cotizador.BDMCotizacion.dao;

import java.nio.charset.Charset;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.Credenciales;


@Component
public class UtilSeguros {
private static Logger logger = LoggerFactory.getLogger(UtilSeguros.class);
	
	private static final String ENCODING_UTF_8 = "UTF-8";
	
	/**
	 * Metodo que invoca el servicio pára hacer login con acsel-e
	 */
	
	public String incovaServicioPostHeader(String urlWs, String requestJson, int connectTimeout, int readTimeout) {
		long tiempoInicio =  obtenerTiempoInMillis();
		String response = null;
		
		HttpHeaders headers = new HttpHeaders();
		RestTemplate restTemplate = new RestTemplate();
		Credenciales credenciales = new Credenciales();
		try {
			credenciales.setUserName("BAZDigital");
			credenciales.setPassword("Azteca10");
			credenciales.setCountry("MX");
			credenciales.setInstance("Azteca");
		
			headers.set("Content-Type", "application/vdn.acsele.api.v1+json");
			HttpEntity<Credenciales> request = new HttpEntity<>(credenciales,headers);
			HttpEntity<String> httpResponse = restTemplate.exchange(urlWs,HttpMethod.POST,request,String.class);
		
			headers = httpResponse.getHeaders();

			response = headers.getFirst(HttpHeaders.SET_COOKIE);
		
		
			System.out.println("Set-Cookie Mamalona : " + response + "\n");
		
		}catch(Exception e) {
			logger.error("ERROR EN ESTA MAMADA " + e);
		}
		/*Agregaremos despues los servicios para la lectura de los servicios*/
		
		return response;
	}
	
	/**
	 * @return tiempo en milisegundos
	 */
	public long obtenerTiempoInMillis() {
		return Calendar.getInstance().getTimeInMillis();
	}

}
