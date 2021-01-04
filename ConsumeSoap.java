package com.bancoazteca.bdm.cotizador.BDMCotizacion.business;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.CreditRunCotizadorRequest;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.CreditRunResponseCotizador;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.RecompraEntrada;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.RecompraRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Service
public class ConsumeSoap {
	private static final String CODIGO_RESPUESTA_COT = "codigo";
	private static final String DESCRIPCION = "descripcion";
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String APPLICATION_JSON = "application/json";
	private static final String DESCONOCIDO = "DESCONOCIDO";
	private static final String BASIC = "Basic ";
	private static final String AUTHORIZATION = "Authorization";
	private static final String ACCEPT = "Accept";
	private static final String OK = "OK";

	private static String SEPARATOR_URL = "/";
	private static final Logger logger = LoggerFactory.getLogger(ConsumeSoap.class);

	
	public ResponseEntity<Object> consumeSoap(RecompraRequest request, String requestCifrado) {
		CreditRunResponseCotizador responseTO = new CreditRunResponseCotizador();
		RecompraEntrada recEnt = new RecompraEntrada();
		int seguro;
		float valorSeguro;
		
		recEnt.setCanal(9);			
		recEnt.setIcu("565656565");
		recEnt.setCu("6565454545466545654541564");
		recEnt.setCuenta("20304060");
		recEnt.setIdProducto(24);
		recEnt.setMonto(25000);
		recEnt.setInteres(100);
		recEnt.setMontoTotal(25000);
		recEnt.setPagoNormal(35);
		recEnt.setPagoPuntual(55);
		recEnt.setPais(1);
		recEnt.setPeriodicidad(200);
		recEnt.setPlazo(1);
		recEnt.setSucursal(1);
		recEnt.setToken("request.getFirma()");
		recEnt.setUs("usuarioAln");
		recEnt.setLatitud("request.getLatitud()");
		recEnt.setLongitud("");
		recEnt.setIdEmpleado("cadenaAuxIdEmpl");
		recEnt.setOpcionSeguro("1");
		
		
		if(request.isSeguros()) {
			seguro = 30;
			valorSeguro = (float) seguro;
			logger.info("ENVIANDO EL VALOR DE SEGURO CON " + valorSeguro);
			recEnt.setAbonoSeguro(String.valueOf(valorSeguro));

		}
		else {
			logger.info("No entra con seguros y se va con el flujo Normal");
			//ParametrosCreditoEntity parametroBase = paramCre.findParametro("6");
			recEnt.setAbonoSeguro("20");
		}
		
		
		String jsonRecompra = convertObjectToJson(recEnt);
		String resp = recompraCons(jsonRecompra); //recompraCifrado(jsonRecompra);			
		
		
		
		
		 return new ResponseEntity<>(responseTO, HttpStatus.OK);
	}
	
	
	
	
	public String recompraCons(String jsonRec) {
		logger.info("Consultando el metodo [ RecompraDaoImpl::recompraCons ]");
		String response = "";
		try {
			

			//String urlWS = PropertiesManager.getinstance().getProperty(ConstantesCredito.CREDITO_PROPERTIES,URL_SERVICIO_RECOMPRA);
			String urlWS = "";
			logger.info(urlWS);

			response = postSOAP(urlWS, jsonRec);

		} catch (RestClientException e) {
			logger.info("Incidencia dao recompraCons");
			return response;

		} catch (RuntimeException e) {
			logger.info("Incidencia dao recompraCons: {}", e.getMessage());
		} catch (Exception e) {
			logger.info("Se provoco un contratiempo en el servicio de la MOC");
		}
		return response;
	

}
	
	public String postSOAP(String urlWS, String objectRequest) {

		
		StringBuilder stringBuilder = new StringBuilder();
		HttpURLConnection httpURLConnection = null;
		BufferedReader bufferedReader = null;
		OutputStream outputStream = null;
		String output;
		String respuesta = null;
		InputStream inputStream = null;
		try {
			URL url = new URL(urlWS);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty(ACCEPT, APPLICATION_JSON);
			httpURLConnection.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON);

			outputStream = httpURLConnection.getOutputStream();
			if (!Validations.isNullOrEmpty(objectRequest))
				outputStream.write(objectRequest.getBytes());
			outputStream.flush();

			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				new Exception("HTTP error code : " );
			}
			inputStream = httpURLConnection.getInputStream();
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

			while ((output = bufferedReader.readLine()) != null) {
				stringBuilder.append(output);
			}
			respuesta = stringBuilder.toString();
		} catch (IOException e) {
			logger.info("{}", e.getMessage());
		} finally {
			try {
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (Exception e) {
				bufferedReader = null;
			}
			try {
				if (outputStream != null)
					outputStream.close();
			} catch (Exception e) {
				outputStream = null;
			}
			if (httpURLConnection != null)
				httpURLConnection.disconnect();
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (IOException e) {
				inputStream = null;
			}
		}

		return respuesta;
	}
	
	public String convertObjectToJson(Object object) {
		String json = "";
		Gson gson = new GsonBuilder().serializeNulls().create();
		try {
			json = gson.toJson(object);
		} catch (Exception e) {
			logger.info("Incidencia en metodo convertObjectToJson ", e);
			json = null;
		}
		return json;
	}
	
}
