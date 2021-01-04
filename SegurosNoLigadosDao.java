package com.bancoazteca.bdm.cotizador.BDMCotizacion.dao;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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

import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.AutenticacionSeguros;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.DireccionTercero;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.PaymentModeInput;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.PolizaPlanMedico_Input;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.PolizaSegurosInfo;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.Poliza_Input;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.Rol_Input;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.ThirdPartyList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;



@Component
public class SegurosNoLigadosDao {
	

	
	//@Autowired private UtilNoLigados utilNoLigados;
	
	private static final Logger LOG = LoggerFactory.getLogger(SegurosNoLigadosDao.class);
	private static final String ACSEL_CONTENT_TYPE = "application/vdn.acsele.api.v1+json";
	private static final Integer ERROR_GENERAL = -1;
	

	/**
	 * metodo /ws/rest/login
	 * @throws Exception 
	 */
	public String CrearTokenSegurosAzteca(AutenticacionSeguros autenticacionSeguros) throws Exception
	{
		ResponseEntity<String> response;
		String endpoint ="http://10.81.24.41:8080/ws/rest/login";
		String queryParameterURL = null;
		String queryParameterValue = null;
		
		LOG.info("acsel_Input {}", autenticacionSeguros.toString());
		LOG.info("endpoint {}", endpoint);
		LOG.info("HttpMethod {}", HttpMethod.POST);
		
		response = llamarServicio(HttpMethod.POST, autenticacionSeguros, endpoint, queryParameterURL, queryParameterValue);
		if (response != null) {
			return response.getHeaders().getValuesAsList("Set-Cookie").toString();
		}
		//throw new MessageException(ERROR_GENERAL);
		return null;
	}
	
	/**
	 * metodo /ws/rest/azteca/thirdParties/createThirdParty
	 * @throws Exception 
	 */
	public String createThirdParty(PolizaSegurosInfo entradaTercero, String cookie) throws Exception {
		ResponseEntity<String> response;
		String endpoint ="http://10.81.24.41:8080//ws/rest/azteca/thirdParties/createThirdParty";
		String queryParameterURL = "withThirdPartyUpdate";
		String queryParameterValue = "2";

		LOG.info("acsel_Input {}", entradaTercero.toString());
		LOG.info("endpoint {}", endpoint);
		LOG.info("HttpMethod {}", HttpMethod.POST);

		response = llamarServicio(HttpMethod.POST, entradaTercero, endpoint, queryParameterURL,queryParameterValue,cookie);
		if (response != null) {
			LOG.info("response {}", response.getStatusCode());
			return response.getBody();
		}
		//throw new MessageException(ERROR_GENERAL);
		return null;

	}
	
	/**
	 * /ws/rest/azteca/thirdParties/addRolThirdParty
	 * @throws Exception 
	 */
	public String addRolThirdParty(Rol_Input rol_Input, String cookie, List<ThirdPartyList> listaThirdParty) throws Exception {
		ResponseEntity<String> response;
		String endpoint = "http://10.81.24.41:8080/ws/rest/azteca/thirdParties/addRolThirdParty";
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
	 * /ws/rest/azteca/thirdParties/addRolThirdParty
	 * @param rol_Input
	 * @param cookie
	 * @param thirdPartyId
	 * @return
	 * @throws Exception
	 */
	public String addRolThirdParty(Rol_Input rol_Input, String cookie, String thirdPartyId) throws Exception {
		ResponseEntity<String> response;
		String endpoint = "http://10.81.24.41:8080/ws/rest/azteca/thirdParties/addRolThirdParty";
		String queryParameterURL = "thirdPartyId";
		String queryParameterValue = thirdPartyId;
		generarCadena(rol_Input);
		LOG.info("endpoint {}", endpoint);
		LOG.info("HttpMethod {}", HttpMethod.POST);

		response = llamarServicio(HttpMethod.POST, rol_Input, endpoint, queryParameterURL,queryParameterValue, cookie);
		LOG.info("Response {} ",response.getStatusCode());
		return response.getStatusCode().toString();
	}
	
	/**
	 * metodo /ws/rest/azteca/thirdParties/addAddressThirdParty
	 */
	public String asignarDireccionTerceros(String thirdPartyId,DireccionTercero direccionTercero, String cookie) throws Exception{	
		ResponseEntity<String> response;
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
			
			if(queryParameterURL=="withThirdPartyUpdate" && (!queryParameterURL.isEmpty())) {
				//throw new MessageException(ERROR_GENERAL);
			}else {
				if((!queryParameterURL.isEmpty()) && (!queryParameterValue.isEmpty())) {
					UriComponents uriComponent = UriComponentsBuilder.fromUriString(endpoint)
							.queryParam(queryParameterURL, queryParameterValue).build();
					urlRecurso = uriComponent.toString();
					uri = new URI(urlRecurso);
				} else {
					uri = new URI(urlRecurso);
				}	
			
			// URL para llamar service
			LOG.info("Service URL {} ", uri);
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
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			
				if((!queryParameterURL.isEmpty()) || (!queryParameterValue.isEmpty())) {
					UriComponents uriComponent = UriComponentsBuilder.fromUriString(endpoint).queryParam(queryParameterURL, queryParameterValue).build();
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
		//throw new MessageException(ERROR_GENERAL);
		return null;
		
	}
	
	/**
	 * metodo /ws/rest/azteca/thirdParties/addThirdPartyPaymentMode
	 */
	public String asignarModoCobro(PaymentModeInput paymentModeInput,String thirdPartyId, String cookie) {

		ResponseEntity<String> response;
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
		return null;
	}
	
	/**
	 * metodo /ws/rest/azteca/applyEventPolicy para tranquilidad
	 */
	public String createPolicy(Poliza_Input poliza_Input, String cookie) throws Exception {
		ResponseEntity<String> response;
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
		return null;
	}
	
	/**
	 * metodo /ws/rest/azteca/applyEventPolicy para el plan medico
	 */
	public String createPolicyPlanMedico(PolizaPlanMedico_Input poliza_Input, String cookie) throws Exception {
		ResponseEntity<String> response;
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
		return null;
	}
	
	/**
	 * metodo creacion de la entidad con la cookie
	 */
	public static HttpEntity<Object> getHttpEntity(Object objeto, String endpoint, String cookie) {
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.parseMediaType(ACSEL_CONTENT_TYPE));
		header.add("onlyToken",  "1");
		header.add("cookie", "token=".concat(cookie));
		
		LOG.info("header {}", header);
		return new HttpEntity<>(objeto, header);
	}
	
	/**
	 * metodo creacion de la entidad
	 */
	public static HttpEntity<Object> getHttpEntity(Object objeto, String endpoint) {
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.parseMediaType(ACSEL_CONTENT_TYPE));
		header.add("onlyToken", "1");
		LOG.info("header {}", header);
		return new HttpEntity<>(objeto, header);
	}
	
	/**
	 * Metodo para almacenar la info
	 */
	/*public void guardarInfoPoliza(String icu, String cu, String nombre, String apellidoPaterno, String apellidoMaterno, String genero, String fechaNacimiento, String rfc, String curp, String celular, String correoElectronico, InformacionDappSesion informacionDapp) {
		LOG.info("Comienza almacenamiento de respaldo");
		PolizaAztecaMongo respaldoPoliza = new PolizaAztecaMongo();
		respaldoPoliza.setIcu(icu);
		respaldoPoliza.setClienteUnico(cu);
		respaldoPoliza.setCodigoDapp(informacionDapp.getDapp_code());
		respaldoPoliza.setFecha(new Date());
		respaldoPoliza.setNombre(nombre);
		respaldoPoliza.setApellidoPaterno(apellidoPaterno);
		respaldoPoliza.setApellidoMaterno(apellidoMaterno);
		respaldoPoliza.setGenero(genero);
		respaldoPoliza.setFechaNacimiento(fechaNacimiento);
		respaldoPoliza.setRfc(rfc);
		respaldoPoliza.setCurp(curp);
		respaldoPoliza.setCelular(celular);
		respaldoPoliza.setCorreoElectronico(correoElectronico);
		respaldoPoliza.setDescripcionCodeDapp(informacionDapp.getDescription());
		respaldoPoliza.setMontoCodeDapp(informacionDapp.getAmount());
		mongoTemplate.save(respaldoPoliza);
	}*/
	

	/**
	 * 
	 * // guardar la informacion del seguro del usuario en DB
	 */
/*	public void guardarPeticionMongo(DappSegurosMongo dappSegurosMongo) {
		try {
			Update update = new Update();
			Query query = new Query(Criteria.where("icu").is(dappSegurosMongo.getIcu()));

			DappSegurosMongo request = new DappSegurosMongo();
			request = mongoTemplate.findOne(query, DappSegurosMongo.class);

			if (Validations.isNullOrEmpty(request)) {
				LOG.info("No existe Obj en base de datos, se creara uno nuevo");
				mongoTemplate.insert(dappSegurosMongo);
			} else {
				LOG.info("Existe Obj en base de datos, se actualiza");
				update.set("seguros", dappSegurosMongo.getSeguros());
				mongoTemplate.upsert(query, update, DappSegurosMongo.class);
			}

		} catch (Exception e) {
			LOG.info("Incidente al consultar en la Base de Datos {} ", e);
		}
	}
*/
	/**
	 * 
	 * Obtiene los detalles de un seguro por dapp_code
	 */
	/*public DappSeguroDetalles obtenerSeguro(DappSegurosMongo objeto, String dapp_code) {
		DappSeguroDetalles request = new DappSeguroDetalles();
		if(!Validations.isNullOrEmpty(objeto) && !Validations.isNullOrEmpty(objeto.getIcu())) {
			request = objeto.getSeguros().stream().filter(seguro -> dapp_code.equals(seguro.getDapp_code()!=null?seguro.getDapp_code():"")).findAny()
					.orElse(null);
			return request;
		}
		return request;
	}*/
	
	/**
	 * 
	 * //Obtener la informacion  completo de los seguros del cliente
	 */
	/*public  DappSegurosMongo obtenerSegurosCliente(String icu) {
		Query query = Query.query(Criteria.where(ConstantesSegurosAzteca.ICU).is(icu));
		DappSegurosMongo objeto = (DappSegurosMongo) mongoTemplate.findOne(query, DappSegurosMongo.class) != null ? (DappSegurosMongo) mongoTemplate.findOne(query, DappSegurosMongo.class) : new DappSegurosMongo();
		return objeto;
	}
	*/

	/**
	 * Metodo para consumir el servicio rest(Tabla dinamica)
	 */
	public ResponseEntity<String> llamarServicioRest(HttpMethod method, HttpEntity<Object> headers, URI uri) {
		ResponseEntity<String> response = null;
		try {
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
			LOG.info("PETICION A");
			LOG.info("endpoint {} ", uri);
			LOG.info("HttpMethod {} ", method);
			LOG.info("Headers {} ", headers.getHeaders());
			response = restTemplate.exchange(uri, method, headers, String.class);
			if (response.getStatusCode().equals(HttpStatus.OK)) {
				LOG.info("Response Body:: {} StatusCode:: {} ", response.getStatusCode(),response.getBody());
				return response;
			} else {
				LOG.info("Response Body:: {} StatusCode:: {} ",  response.getBody(),response.getStatusCode());
			}
		} catch (HttpClientErrorException e) {
			LOG.info("Response Body:: {} StatusCode:: {}  StackTrace:: {}", e.getResponseBodyAsString(),
					e.getStatusCode(), e.getStackTrace());
		} catch (ResourceAccessException ex) {
			LOG.info("Timeout: {}", ex.getMessage());
		}
		//throw new MessageException(ERROR_GENERAL);
		return null;
	}
	
	public static void generarCadena(Object objeto) {
		ObjectMapper mapper = new ObjectMapper();
		StringBuilder cadena = new StringBuilder();
		try {
			cadena = cadena.append("").append(mapper.writeValueAsString(objeto)).append(" ] ");
			LOG.info("Request/Response [ -> {} " , cadena);
		} catch (JsonProcessingException e) {
			LOG.info("Problema al generarCadenaEntrada()... ", e);
		}
	}

}

