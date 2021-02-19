package com.bancoazteca.bdm.cotizador.BDMCotizacion.dao;


import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.bancoazteca.bdm.cotizador.BDMCotizacion.business.Validations;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.AutenticacionSeguros;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.DescripcionPolizas;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.DescripcionSeguro;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.DetallePolizas;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.ListaPolizas;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.PolizaSegurosInfo;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.Polizas;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.Request.ListaSegurosRequestTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@Component
public class UtilNoLigados {
	private static final Logger logger = LoggerFactory.getLogger(UtilNoLigados.class);
	
	private static final String ACSEL_CONTENT_TYPE = "application/vdn.acsele.api.v1+json";
	private static final Integer ERROR_GENERAL = -1;
	public static final String URL_CONSULT_POLICY = "http://10.81.24.41:8080/ws/rest/azteca/consultPolicy/policyThirdPartyRol";
	
	
	/**
	 * Se obtiene el Token de la session de accele 
	 */
	
	public String obtenerCodigoToken() throws Exception{
		AutenticacionSeguros autenticacionSeguros = new AutenticacionSeguros();
		try {
			autenticacionSeguros.setUserName("BAZDigital");
			autenticacionSeguros.setPassword("Azteca10");
			autenticacionSeguros.setCountry("MX");
			autenticacionSeguros.setInstance("Azteca");
		}
		catch(Exception e)
	    {
			//LogUtils.printST(e);
		    //throw new MessageException(ERROR_GENERAL);
			logger.error("NO SE PUDO OBTENER EL TOKEN");
	    }
		return crearTokenAccele(autenticacionSeguros);
	}
	
	
	/**
	 * Metodo para obtener el token
	 * @param tokenSeguros
	 * 
	 */
	public String obtenerValorToken(String tokenSeguros){
		String tokenObtenido = null;
		
		try {
			if(tokenSeguros!=null) {
				String[] temporal=tokenSeguros.split(";");
				String[] temporalB=temporal[0].split("=");
				tokenObtenido=temporalB[1];
			}else {
				//throw new MessageException(ERROR_GENERAL);
			}
		}catch(Exception e){
		    logger.info("Error al realizar la operacion {}" , e);
		    //throw new MessageException(ERROR_GENERAL);
	    }
		return tokenObtenido;
	}
	

	/**
	 * metodo /ws/rest/login
	 */
	public String crearTokenAccele (AutenticacionSeguros autenticacion) throws Exception{
		ResponseEntity <String> response;
		String endpoint ="http://10.81.24.41:8080/ws/rest/login";
		String queryParameterURL = null;
		String queryParameterValue = null;
		
		logger.info("acsel_Input {}", autenticacion.toString());
		logger.info("endpoint {}", endpoint);
		logger.info("HttpMethod {}", HttpMethod.POST);
		
		response = llamarServicio(HttpMethod.POST, autenticacion, endpoint, queryParameterURL, queryParameterValue);
		if (response != null) {
			return response.getHeaders().getValuesAsList("Set-Cookie").toString();
		}
		//throw new MessageException(ERROR_GENERAL);	
		return null;
	}
	
	
	/**
	 * Metodo para consumir el servicio rest de seguros azteca con cookie
	 */
	public ResponseEntity<String> llamarServicio(HttpMethod method, Object objeto, String endpoint, String queryParameterURL, String queryParameterValue) throws Exception{
		ResponseEntity<String> response = null;
		String urlRecurso = endpoint;
		URI uri;

		try {
			RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
			restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			
				if(!Validations.isNullOrEmpty(queryParameterURL) && !Validations.isNullOrEmpty(queryParameterValue)) {
					UriComponents uriComponent = UriComponentsBuilder.fromUriString(endpoint)
							.queryParam(queryParameterURL, queryParameterValue).build();
					urlRecurso = uriComponent.toString();
					uri = new URI(urlRecurso);
				} else {
					uri = new URI(urlRecurso);
				}	
			
			// URL para llamar service
				logger.info("Service URL {} ", uri);
			response = restTemplate.exchange(uri, method, getHttpEntity(objeto, urlRecurso),String.class);
			return response;
		} catch (HttpClientErrorException e) {
			logger.info("Response Body:: {} StatusCode:: {}  StackTrace:: {}", e.getResponseBodyAsString(),e.getStatusCode(), e.getStackTrace().toString());
			throw e;
		} catch (URISyntaxException e) {
			logger.info("Mensaje {}", e.getMessage());
		} catch (ResourceAccessException ex) {
			logger.info("Timeout: {}", ex.getMessage());
		}catch(HttpServerErrorException e ) {
			logger.info("Error por parte del servidor: {}", e.getMessage());
		}
		//throw new MessageException(ERROR_GENERAL);
		return response;
	}
	

	/**
	 * metodo creacion de la entidad
	 */
	public static HttpEntity<Object> getHttpEntity(Object objeto, String endpoint) {
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.parseMediaType(ACSEL_CONTENT_TYPE));
		header.add("onlyToken", "1");
		logger.info("header {}", header);
		return new HttpEntity<>(objeto, header);
	}
	
	/**
	 * metodo /ws/rest/azteca/thirdParties/createThirdParty
	 * @throws Exception 
	 */
	public String createThirdParty(PolizaSegurosInfo entradaTercero, String cookie) throws Exception {
		ResponseEntity<String> response;
		String endpoint = "http://10.81.24.41:8080//ws/rest/azteca/thirdParties/createThirdParty";
		String queryParameterURL = "withThirdPartyUpdate";
		String queryParameterValue = "2";

		logger.info("acsel_Input {}", entradaTercero.toString());
		logger.info("endpoint {}", endpoint);
		logger.info("HttpMethod {}", HttpMethod.POST);

		response = llamarServicio(HttpMethod.POST, entradaTercero, endpoint, queryParameterURL,queryParameterValue,cookie);
		if (response != null) {
			logger.info("response {}", response.getStatusCode());
			return response.getBody();
		}
		//throw new MessageException(ERROR_GENERAL);
		return null;
	}
	
	/**
	 * metodo /ws/rest/azteca/thirdParties/createThirdParty
	 * @throws Exception 
	 */
	public String queryPolizas(PolizaSegurosInfo entradaTercero, String cookie) throws Exception {
		ResponseEntity<String> response;

		String endpoint = URL_CONSULT_POLICY;
		String queryParameterURL = null;
		String queryParameterValue = null;
		logger.info("acsel_Input {}", entradaTercero.toString());
		logger.info("endpoint {}", endpoint);
		logger.info("HttpMethod {}", HttpMethod.GET);

		response = llamarServicio(HttpMethod.GET, entradaTercero, endpoint, queryParameterURL,queryParameterValue,cookie);
		if (response != null) {
			logger.info("response {}", response.getStatusCode());
			return response.getBody();
		}
		//throw new MessageException(ERROR_GENERAL);
		return null;
	}
	
	/**
	 * metodo /ws/rest/azteca/thirdParties/createThirdParty
	 * @throws Exception 
	 */
	public List<DescripcionPolizas> queryPolizasGET(ListaPolizas entradaTercero, String cookie) throws Exception {
		//String response;
		List<DescripcionPolizas> response = null;
		
		String endpoint = URL_CONSULT_POLICY+"?thirdPartyType="+entradaTercero.getThirdPartyType()+"&propertiesThirdParty="+URLEncoder.encode(entradaTercero.getListaPropertiesThirdParty().toString()).concat("&productIdList=205,203,142&idRole=1015");
		//DetallePolizas response;
		logger.info("acsel_Input {}", entradaTercero.toString());
		logger.info("endpoint {}",endpoint);
		logger.info("HttpMethod {}", HttpMethod.GET);
		
		


		response = llamarServicioGET(HttpMethod.GET, entradaTercero, endpoint,cookie);
		//response = (DetallePolizas) getApi(endpoint,HttpMethod.GET,cookie,entradaTercero);
		
		
		if (response != null) {
			logger.info("IMPRESION DE POLIZAS CON API");
			return response;
		}
		
		//throw new MessageException(ERROR_GENERAL);
		return null;
	}
	
	/**
	 * Metodo para consumir el servicio rest de seguros azteca sin cookie
	 */
	public ResponseEntity<String> llamarServicio(HttpMethod method, Object objeto, String endpoint, String queryParameterURL, String queryParameterValue, String cookie) throws Exception{
		ResponseEntity<String> response = null;
		String urlRecurso = endpoint;
		URI uri;

		try {
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			
			if((queryParameterURL=="withThirdPartyUpdate")&&Validations.isNullOrEmpty(queryParameterURL)) {
				//throw new MessageException(ERROR_GENERAL);
			}else {
				if(!Validations.isNullOrEmpty(queryParameterURL) && !Validations.isNullOrEmpty(queryParameterValue)) {
					UriComponents uriComponent = UriComponentsBuilder.fromUriString(endpoint)
							.queryParam(queryParameterURL, queryParameterValue).build();
					urlRecurso = uriComponent.toString();
					uri = new URI(urlRecurso);
				} else {
					uri = new URI(urlRecurso);
				}	
			
			// URL para llamar service
			logger.info("Service URL {} ", uri);
			
			response = restTemplate.exchange(uri, method, getHttpEntity(objeto, urlRecurso, cookie),String.class);
			if (response != null) {
				logger.info("Response {} ",response.getBody());
				return response;
			}
			}
		} catch (HttpClientErrorException e) {
			logger.info("Response Body:: {} StatusCode:: {}  StackTrace:: {}", e.getResponseBodyAsString(),e.getStatusCode(), e.getStackTrace().toString());
			
			throw e;
		} catch (URISyntaxException e) {
			logger.info("Mensaje {}", e.getMessage());
		} catch (ResourceAccessException ex) {
			logger.info("Timeout: {}", ex.getMessage());
		}
		//throw new MessageException(ERROR_GENERAL);
		return null;
		
	}
	
	
	/**
	 * Metodo para consumir el servicio rest de seguros azteca sin cookie
	 */
	public List<DescripcionPolizas> llamarServicioGET(HttpMethod method, Object objeto, String endpoint, String cookie) throws Exception{
		ResponseEntity<String> response = null;
		List<DescripcionPolizas> descList = null;
		String respuesta;
		String urlRecurso = endpoint;
		URI uri;
		
		
		
		uri = new URI(urlRecurso);

		try {
			RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
			restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			
			// URL para llamar service
			logger.info("Service URL {} ", uri);
			logger.info("EL objeto a enviar " + objeto.toString());
			logger.info("EL urlRecurso a enviar " + urlRecurso);
			
			response = restTemplate.exchange(uri, method, getHttpEntity(objeto, urlRecurso, cookie),String.class);
			
			respuesta = response.getBody();
			
			final ObjectMapper objectMapper = new ObjectMapper();
			DescripcionPolizas [] desc  =  objectMapper.readValue(respuesta,DescripcionPolizas [].class);

			descList = new ArrayList(Arrays.asList(desc));
			logger.info("Se estara imprimiento esta madre");
			
			descList.forEach(x -> System.out.println(x.toString()));

			if (response != null) {
				logger.info("Entrando al if del RATERESPONSE NORMAL");
				logger.info("code {} ",response.getStatusCode());
				logger.info("Response {} ",response.getBody());
				logger.info("RESPONSE {} ",response.toString());
			}

		} 
		catch (HttpClientErrorException e) {
			logger.error("Error en  " + e );
			logger.info("Response Body:: {} StatusCode:: {}  StackTrace:: {}", e.getResponseBodyAsString(),e.getStatusCode(), e.getStackTrace().toString());
			throw e;
		} catch (ResourceAccessException ex) {
			logger.info("Timeout: {}", ex.getMessage());
		}
		catch(Exception e) {
			logger.error("UN ERRORSASO HORRIBLE " + e);
		}
		return descList;
		
	}
	
	private SimpleClientHttpRequestFactory getClientHttpRequestFactory() 
	{
	    SimpleClientHttpRequestFactory clientHttpRequestFactory
	                      = new SimpleClientHttpRequestFactory();
	    //Connect timeout
	    clientHttpRequestFactory.setConnectTimeout(10000);
	     
	    //Read timeout
	    clientHttpRequestFactory.setReadTimeout(10000);
	    
	    return clientHttpRequestFactory;
	}
	/**
	 * metodo creacion de la entidad con la cookie
	 */
	public static HttpEntity<Object> getHttpEntity(Object objeto, String endpoint, String cookie) {
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.parseMediaType(ACSEL_CONTENT_TYPE));
		header.add("onlyToken", "1");
		header.add("cookie", "token=".concat(cookie));
		header.add("idUser", "BAZ");
		header.add("idApplication",  "BAZ01");
		
		logger.info("header {}", header);
		logger.info("objeto {}", objeto);
		return new HttpEntity<>(objeto, header);
	}
}
