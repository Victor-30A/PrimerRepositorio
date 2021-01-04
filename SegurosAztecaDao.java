package com.bancoazteca.bdm.cotizador.BDMCotizacion.business;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.autoconfigure.security.SecurityProperties.Headers;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;


import com.bancoazteca.bdm.cotizador.BDMCotizacion.dao.ConstantesSegurosAzteca;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.AutenticacionSeguros;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.DireccionTercero;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.PaymentModeInput;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.PolizaPlanMedico_Input;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.PolizaSegurosInfo;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.Poliza_Input;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.Rol_Input;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.ThirdPartyList;


@Component
public class SegurosAztecaDao {
	
	private static final Logger LOG = LoggerFactory.getLogger(SegurosAztecaDao.class);
	
	private static final String ACSEL_CONTENT_TYPE = "application/vdn.acsele.api.v1+json";
	private static final Integer ERROR_GENERAL = -1;
	
	
	
	/**
	 * metodo /ws/rest/login
	 * @throws Exception 
	 */
	public String CrearTokenSegurosAzteca(AutenticacionSeguros autenticacionSeguros) throws Exception
	{
		ResponseEntity<String> response;
		//String endpoint =PropertiesManager.getinstance().getProperty(ConstantesSegurosAzteca.ARCHIVO_PROPERTY_RETIROS, ConstantesSegurosAzteca.URL_TOKEN_SEGUROS_AZTECA);
		String endpoint = "http://10.81.24.41:8080/ws/rest/login";
		String queryParameterURL = null;
		String queryParameterValue = null;
		
		LOG.info("acsel_Input {}", autenticacionSeguros.toString());
		LOG.info("endpoint {}", endpoint);
		LOG.info("HttpMethod {}", HttpMethod.POST);
		
		response = llamarServicio(HttpMethod.POST, autenticacionSeguros, endpoint, queryParameterURL, queryParameterValue);
		LOG.info(""+response);
		String cadena = response.toString();
		String recortada;
		recortada = cadena.substring(162,210);
		LOG.info("El valor de la recortada es " + recortada);
		if (response != null) {
			//return response.getHeaders().getValuesAsList("Set-Cookie").toString();
			//return response.toString();
			return recortada;
		}
		throw new Exception();
	}
	

	/**
	 * metodo /ws/rest/azteca/thirdParties/createThirdParty
	 * @throws Exception 
	 */
	public String createThirdParty(PolizaSegurosInfo entradaTercero, String cookie) throws Exception {
		ResponseEntity<String> response;
		//String endpoint = PropertiesManager.getinstance().getProperty(ConstantesSegurosAzteca.ARCHIVO_PROPERTY_RETIROS, ConstantesSegurosAzteca.URL_THIRD_SEGUROS_AZTECA);
		String endpoint =  "http://10.81.24.41:8080//ws/rest/azteca/thirdParties/createThirdParty";
		//String queryParameterURL = PropertiesManager.getinstance().getProperty(ConstantesSegurosAzteca.ARCHIVO_PROPERTY_RETIROS, ConstantesSegurosAzteca.THIRD_PARTY_UPDATE);
		String queryParameterURL = "withThirdPartyUpdate";
		//String queryParameterValue = PropertiesManager.getinstance().getProperty(ConstantesSegurosAzteca.ARCHIVO_PROPERTY_RETIROS, ConstantesSegurosAzteca.THIRD_PARTY_INPUT);
		String queryParameterValue = "1";
		

		LOG.info("acsel_Input {}", entradaTercero.toString());
		LOG.info("endpoint {}", endpoint);
		LOG.info("HttpMethod {}", HttpMethod.POST);

		response = llamarServicio(HttpMethod.POST, entradaTercero, endpoint, queryParameterURL,queryParameterValue,cookie);
		if (response != null) {
			LOG.info("response {}", response.getStatusCode());
			return response.getBody();
		}
		throw new Exception();

	}
	
	/**
	 * /ws/rest/azteca/thirdParties/addRolThirdParty
	 * @throws Exception 
	 */
	public String addRolThirdParty(Rol_Input rol_Input, String cookie, List<ThirdPartyList> listaThirdParty) throws Exception {
		ResponseEntity<String> response;
		//String endpoint = PropertiesManager.getinstance().getProperty(ConstantesSegurosAzteca.ARCHIVO_PROPERTY_RETIROS, ConstantesSegurosAzteca.URL_ROL_SEGUROS_AZTECA);
		String endpoint =  "http://10.81.24.41:8080/ws/rest/azteca/thirdParties/addRolThirdParty";
		String queryParameterURL = "thirdPartyId";
		String queryParameterValue = listaThirdParty.get(0).getThirdPartyId();

		LOG.info("rol_Input {}", rol_Input.toString());
		LOG.info("endpoint {}", endpoint);
		LOG.info("HttpMethod {}", HttpMethod.POST);

		response = llamarServicio(HttpMethod.POST, rol_Input, endpoint, queryParameterURL,queryParameterValue, cookie);
		LOG.info("Response {} ",response.getStatusCode().toString());
		return response.getStatusCode().toString();
	}
	
	/**
	 * metodo /ws/rest/azteca/thirdParties/addAddressThirdParty
	 */
	public String asignarDireccionTerceros(String thirdPartyId,DireccionTercero direccionTercero, String cookie) throws Exception{	
		ResponseEntity<String> response;
		//String endpoint = PropertiesManager.getinstance().getProperty(ConstantesSegurosAzteca.ARCHIVO_PROPERTY_RETIROS, ConstantesSegurosAzteca.URL_DIRECCION_TERCEROS);
		String endpoint = "http://10.81.24.41:8080/ws/rest/azteca/thirdParties/addAddressThirdParty";
		String queryParameterURL = "thirdPartyId";
		String queryParameterValue = thirdPartyId;
		
		LOG.info("AddAddress imput {}", direccionTercero.toString());
		LOG.info("endpoint {}", endpoint);
		LOG.info("HttpMethod {}", HttpMethod.POST);
		
		response = llamarServicio(HttpMethod.POST, direccionTercero, endpoint, queryParameterURL,queryParameterValue, cookie);
		LOG.info("response {}", response.getStatusCode().toString());
		return response.getStatusCode().toString();
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
			
			//if(queryParameterURL==PropertiesManager.getinstance().getProperty(ConstantesSegurosAzteca.ARCHIVO_PROPERTY_RETIROS, ConstantesSegurosAzteca.THIRD_PARTY_UPDATE)&&Validations.isNullOrEmpty(queryParameterURL)) {
			if(queryParameterURL=="withThirdPartyUpdate"&&Validations.isNullOrEmpty(queryParameterURL)) {
				throw new Exception();
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
			LOG.info("Service URL 173 {} ", uri);
			response = restTemplate.exchange(uri, method, getHttpEntity(objeto, urlRecurso, cookie),String.class);
			if (response != null) {
				LOG.info("Response {} ",response.getBody());
				return response;
			}
			}
		} catch (HttpClientErrorException e) {
			LOG.info("Response Body:: {} StatusCode:: {}  StackTrace:: {}", e.getResponseBodyAsString(),e.getStatusCode(), e.getStackTrace().toString());
			throw e;
		} catch (URISyntaxException e) {
			LOG.info("Mensaje {}", e.getMessage());
		} catch (ResourceAccessException ex) {
			LOG.info("Timeout: {}", ex.getMessage());
		}
		throw new Exception();
		
	}
	
	/**
	 * Metodo para consumir el servicio rest de seguros azteca con cookie
	 */
	public ResponseEntity<String> llamarServicio(HttpMethod method, Object objeto, String endpoint, String queryParameterURL, String queryParameterValue) throws Exception{
		ResponseEntity<String> response = null;
		String urlRecurso = endpoint;
		URI uri;

		try {
			RestTemplate restTemplate = new RestTemplate();
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
			LOG.info("Service URL {} ", uri);
			response = restTemplate.exchange(uri, method, getHttpEntity(objeto, urlRecurso),String.class);
			return response;
		} catch (HttpClientErrorException e) {
			LOG.info("Response Body:: {} StatusCode:: {}  StackTrace:: {}", e.getResponseBodyAsString(),e.getStatusCode(), e.getStackTrace().toString());
			throw e;
		} catch (URISyntaxException e) {
			LOG.info("Mensaje {}", e.getMessage());
		} catch (ResourceAccessException ex) {
			LOG.info("Timeout: {}", ex.getMessage());
		}catch(HttpServerErrorException e ) {
			LOG.info("Error por parte del servidor: {}", e.getMessage());
		}
		throw new Exception();
		
	}
	
	/**
	 * metodo /ws/rest/azteca/thirdParties/addThirdPartyPaymentMode
	 * @throws Exception 
	 */
	public String asignarModoCobro(PaymentModeInput paymentModeInput,String thirdPartyId, String cookie) throws Exception {

		ResponseEntity<String> response;
		//String endpoint = PropertiesManager.getinstance().getProperty(ConstantesSegurosAzteca.ARCHIVO_PROPERTY_RETIROS, ConstantesSegurosAzteca.URL_PAYMENT_MODE);
		String endpoint = "http://10.81.24.41:8080/ws/rest/azteca/thirdParties/addThirdPartyPaymentMode";
		String queryParameterURL = "thirdPartyId";
		String queryParameterValue = thirdPartyId;


		LOG.info("Entrada modo de cobro {}", paymentModeInput.toString());
		LOG.info("endpoint {}", endpoint);
		LOG.info("HttpMethod {}", HttpMethod.POST);


		try {
			response = llamarServicio(HttpMethod.POST, paymentModeInput, endpoint, queryParameterURL,queryParameterValue,cookie);
	
			if (response != null) {
				LOG.info("response {}", response.getStatusCode().toString());
				return response.getStatusCode().toString();
			}
		 } catch (Exception e) {
			 LOG.error("Error al realizar la operacion");
		}
		//throw new MessageException(ERROR_GENERAL);
		throw new Exception();
	}
	
	/**
	 * metodo /ws/rest/azteca/applyEventPolicy para tranquilidad
	 */
	public String createPolicy(Poliza_Input poliza_Input, String cookie) throws Exception {
		ResponseEntity<String> response;
		//String endpoint = PropertiesManager.getinstance().getProperty(ConstantesSegurosAzteca.ARCHIVO_PROPERTY_RETIROS, ConstantesSegurosAzteca.URL_POLIZAS_SEGUROS);
		String endpoint = "http://10.81.24.41:8080/ws/rest/azteca/applyEventPolicy";
		String queryParameterURL = null;
		String queryParameterValue = null;

		LOG.info("policy input {}", poliza_Input);
		LOG.info("endpoint {}", endpoint);
		LOG.info("HttpMethod {}", HttpMethod.POST);

		response = llamarServicio(HttpMethod.POST, poliza_Input, endpoint, queryParameterURL,queryParameterValue,cookie);
		if (response != null) {
			LOG.info("response {}", response.getBody());
			return response.getBody();
		}
		//throw new MessageException(ERROR_GENERAL);
		throw new Exception();
	}
	
	/**
	 * metodo /ws/rest/azteca/applyEventPolicy para el plan medico
	 */
	public String createPolicyPlanMedico(PolizaPlanMedico_Input poliza_Input, String cookie) throws Exception {
		ResponseEntity<String> response;
		//String endpoint = PropertiesManager.getinstance().getProperty(ConstantesSegurosAzteca.ARCHIVO_PROPERTY_RETIROS, ConstantesSegurosAzteca.URL_POLIZAS_SEGUROS);
		String endpoint = "http://10.81.24.41:8080/ws/rest/azteca/applyEventPolicy";
		String queryParameterURL = null;
		String queryParameterValue = null;

		LOG.info("policy input {}", poliza_Input.toString());
		LOG.info("endpoint {}", endpoint);
		LOG.info("HttpMethod {}", HttpMethod.POST);

		response = llamarServicio(HttpMethod.POST, poliza_Input, endpoint,queryParameterURL,queryParameterValue,cookie);
		if (response != null) {
			LOG.info("response {}", response.getBody());
			return response.getBody();
		}
		//throw new MessageException(ERROR_GENERAL);
		throw new Exception();
	}
	
	/**
	 * metodo creacion de la entidad con la cookie
	 */
	public static HttpEntity<Object> getHttpEntity(Object objeto, String endpoint, String cookie) {
		LOG.info("el objeto es**** " + objeto.toString());
		HttpHeaders header = new HttpHeaders();
		try {
			
			header.setContentType(MediaType.parseMediaType(ACSEL_CONTENT_TYPE));
			//header.add("onlyToken", PropertiesManager.getinstance().getProperty(ConstantesSegurosAzteca.ARCHIVO_PROPERTY_RETIROS, ConstantesSegurosAzteca.ONLYTOKEN));
			header.add("onlyToken", "1");
			header.add("cookie", "token=".concat(cookie));
			
			LOG.info("header {}", header);
			return new HttpEntity<>(objeto, header);
		}catch (Exception e) {
			LOG.error("MAMADa");
			e.getStackTrace();
			
		}
		return new HttpEntity<>(objeto, header);
	}
	
	/**
	 * metodo creacion de la entidad
	 */
	public static HttpEntity<Object> getHttpEntity(Object objeto, String endpoint) {
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.parseMediaType(ACSEL_CONTENT_TYPE));
		//header.add("onlyToken", PropertiesManager.getinstance().getProperty(ConstantesSegurosAzteca.ARCHIVO_PROPERTY_RETIROS, ConstantesSegurosAzteca.ONLYTOKEN));
		header.add("onlyToken", "1");
		LOG.info("header {}", header);
		return new HttpEntity<>(objeto, header);
	}

}
