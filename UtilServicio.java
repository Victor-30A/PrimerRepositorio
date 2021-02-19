package com.bancoazteca.bdm.cotizador.BDMCotizacion.business;

import java.util.Calendar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Locale;

import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.UrlProperties;


@Component
public class UtilServicio {
	private static Logger logger = LoggerFactory.getLogger(UtilServicio.class);
	
	private static final String ACCEPT = "Accept";
	private static final String APPLICATION_JSON = "application/json";
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String DESCONOCIDO = "DESCONOCIDO";
	private static final String ENCODING_UTF_8 = "UTF-8";
	
	public String postSOAP(String urlWS, String objectRequest, int conTimeOut, int redTimeOut) {

		long tiempoInicio = obtenerTiempoInMillis();
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
			httpURLConnection.setConnectTimeout(conTimeOut);
			httpURLConnection.setReadTimeout(redTimeOut);

			outputStream = httpURLConnection.getOutputStream();
			if (!Validations.isNullOrEmpty(objectRequest))
				outputStream.write(objectRequest.getBytes());
			outputStream.flush();

			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				//throw new BDMRuntimeException("HTTP error code : " + httpURLConnection.getResponseCode());
				logger.error("ERROR EN NO MA " + httpURLConnection.getResponseCode());
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

		UrlProperties urlProp = getInformacionURL(urlWS);
		String sistema = obtieneOrigenServicio(urlWS);
		//this.printLog(urlProp.getNameServico(), sistema, respuesta);
		LogUtils.agregaTiempoEjecucion(sistema, restarTiempoMilisegundos(tiempoInicio, obtenerTiempoInMillis()),urlProp.getNameServico(), isThread());
		//LogUtils.agregaTiempoEjecucion(sistema, tiempo, metodo, thread);

		return respuesta;
	}
	
	/**
	 * @return tiempo en milisegundos
	 */
	public long obtenerTiempoInMillis() {
		return Calendar.getInstance().getTimeInMillis();
	}
	
	
	public UrlProperties getInformacionURL(String urlPath) {
		UrlProperties urlP = new UrlProperties();
		try {

			URL url = obtieneObjURL(urlPath);

			String path = (url != null ? url.getPath() : "");
			String[] datosURL = path.split("/");

			urlP.setArea(datosURL[1]);
			urlP.setIp(url != null ? url.getHost() : "");
			
			String origenServicio = "";
			//String origenServicio = PropertiesManager.getinstance().getProperty(ConstantesCredito.CREDITO_PROPERTIES,urlP.getIp());

			if (!Validations.isNull(origenServicio) && origenServicio.equals("COBRANZA")) {

				urlP.setNameServico(datosURL[3]);

			} else {

				urlP.setNameServico(datosURL[datosURL.length - 1]);
			}

		} catch (Exception e) {

			urlP.setIp(DESCONOCIDO);
			urlP.setArea(DESCONOCIDO);
			urlP.setNameServico(DESCONOCIDO);

			logger.info("Hubo problemas para obtener informacion de url servicio: {}", urlPath);

		}

		return urlP;
	}
	
	public String obtieneOrigenServicio(String urlWS) {
		String origenServicio = "http://10.50.53.239:8080/SegurosServicios/VidamaxUnificada";

		try {

			String[] cadenas = urlWS.split("/");
			String ip = cadenas[2];
			cadenas = ip.split(":");

			String ipServicio = cadenas[0];

			origenServicio = (ipServicio);

			if (Validations.isNullOrEmpty(origenServicio)) {

				origenServicio = DESCONOCIDO;
			}

		} catch (Exception e) {

			origenServicio = DESCONOCIDO;
			logger.info("Origen del servicio Desconocido");

		}

		return origenServicio;

	}
	
	public URL obtieneObjURL(String urlPath) {

		URL url = null;

		try {

			url = new URL(urlPath);

		} catch (MalformedURLException e) {
			logger.info("Hubo un problema al obtener el objeto URL: {}", urlPath);

		}

		return url;
	}
	
	public void printLog(String servicio, String sistema, String descripcion) {

		if (!Validations.isNullOrEmpty(descripcion) && !isValidJson(descripcion)) {
			logger.info("cadena cifrada moc");
		}

		descripcion = descripcion.replaceAll("rror", "rr").replaceAll("RROR", "RR");
		logger.info("Servicio: {}, Sistema: {}, Descripcion: {}", servicio, sistema, descripcion);

	}
	
	private boolean isValidJson(String cadenaJson) {

		try {

			new JSONObject(cadenaJson);

		} catch (JSONException e) {
			return false;
		}

		return true;

	}
	
	public String restarTiempoMilisegundos(long tiempoInicio, long tiempoFinal) {

		long tiempoTotal = tiempoFinal - tiempoInicio;

		return String.valueOf(tiempoTotal);

	}
	public static boolean isThread() {
		return Thread.currentThread().getName().toLowerCase(Locale.ROOT).contains("thread");
	}
	
	
	public String invocaServicioPostHeaderJson(String urlWs, String json, int conTimeOut, int redTimeOut) {
		long tiempoInicio = obtenerTiempoInMillis();
		String response;
		
		RestTemplate rest = new RestTemplate();
		rest.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName(ENCODING_UTF_8)));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<String> entity = new HttpEntity<>(json);

		SimpleClientHttpRequestFactory requestFactory = (SimpleClientHttpRequestFactory) rest.getRequestFactory();
		requestFactory.setConnectTimeout(conTimeOut);
		requestFactory.setReadTimeout(redTimeOut);
		rest.setRequestFactory(requestFactory);
		response = rest.postForObject(urlWs, entity, String.class);
		logger.info("ESTE REPONSOTOTOTOTE ES " +  response);

		UrlProperties urlProp = getInformacionURL(urlWs);
		String sistema = obtieneOrigenServicio(urlWs);
		this.printLog(urlProp.getNameServico(), sistema, response);
		LogUtils.agregaTiempoEjecucion(sistema, restarTiempoMilisegundos(tiempoInicio, obtenerTiempoInMillis()),
				urlProp.getNameServico(), isThread());
		return response;
	}


}
