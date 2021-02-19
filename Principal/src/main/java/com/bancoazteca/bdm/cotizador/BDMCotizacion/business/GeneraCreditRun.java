package com.bancoazteca.bdm.cotizador.BDMCotizacion.business;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.Cliente;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.CotizadorRequest;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.CotizadorResponse;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.DatosCotizacion;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.InformacionBase;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.ProductoCredito;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.Seguro;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SeguroVidamax;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SeguroDeVida;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

//@Component
@Service
public class GeneraCreditRun {
	private static Logger logger = LoggerFactory.getLogger(GeneraCreditRun.class);

	private int CPD = 220;
	// private static final String PATH_TARJETA_AZTECA =
	// "credito.properties.url.servicio.consulta.cu.tarjeta.azteca";
	private static final String PATH_SEGURO_VIDAMAX = "http://10.81.27.220/SegurosServicios/VidamaxUnificada";
	public static final String PATH_SEGURO_VIDAMAX2 = "http://www.segurosazteca.com.mx/WSOfertaVidamaxUnificada";
	// private static final String URL_SERVICIO_COTIZADOR =
	// "http://10.51.82.220:8081/OriginacionCentralizada/originacion/BazDigital/consultaTermometro";

	private List<SeguroDeVida> listaOfertas = new ArrayList<>();

	public ResponseEntity<Object> cotizador(CotizadorRequest request, String requestCifrado) {
		logger.info("Ya entro");
		// logger.info("CotizadorBusinessImpl [cotizador]");

		CotizadorResponse responseTO = new CotizadorResponse();

		try {
			JSONObject obj = new JSONObject(requestCifrado);

			SeguroVidamax svmx = new SeguroVidamax();
			Cliente cliente = new Cliente();
			ProductoCredito productoCredito = new ProductoCredito();
			Seguro seguro = new Seguro();
			InformacionBase informacion = new InformacionBase();

			svmx.setTipoOferta(1);
			svmx.setOrigen(10);

			cliente.setPais(Integer.parseInt(obj.get("pais").toString()));
			cliente.setCanal(Integer.parseInt(obj.get("canal").toString()));
			cliente.setSucursal(Integer.parseInt(obj.get("sucursal").toString()));
			cliente.setFolio(Integer.parseInt(obj.get("folio").toString()));
			cliente.setCapacidadPagoDisponible(Integer.parseInt(obj.get("capacidadPagoDisponible").toString()));
			svmx.setCliente(cliente);

			productoCredito.setMontoVenta(0);
			productoCredito.setProductoId(0);
			productoCredito.setPeriodo(1);
			productoCredito.setPlazo(0);
			svmx.setProductoCredito(productoCredito);

			seguro.setEsPromocion(false);
			seguro.setIva(0.16);
			svmx.setSeguro(seguro);

			informacion.setWs("BAZ13377242");
			informacion.setUsuario("1111111");
			informacion.setSucursal(10);
			svmx.setInformacionBase(informacion);

			obtenerOfertasSeguros(svmx);
			obtenerListaCotizador(responseTO, request);
			
		} catch (Exception e) {

		}

		return new ResponseEntity<>((Object) responseTO, HttpStatus.OK);

	}

	public Document convierteStringAXML(String xmlStr) {
		if (xmlStr != null) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			try {
				builder = factory.newDocumentBuilder();
				Document doc = builder.parse(new InputSource(new StringReader(xmlStr)));
				return doc;
			} catch (Exception e) {
				logger.error("Error al obtener la oferta de seguros " + e);
				e.printStackTrace();

			}
		}
		return null;
	}

	

	public void obtenerOfertasSeguros(SeguroVidamax obj) {

		logger.info("Inicia proceso consulta de seguros");
		try {
			logger.info("consumir servicio de seguros");
			extraeLista(obtenJson(transmiteXml(obj)));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error al obtener la oferta de seguros " + e);
		}

	}

	public SOAPMessage transmiteXml(SeguroVidamax obj) {
		SOAPMessage soapResponse = null;
		logger.info("Proceso para consumir servicio SOAP de seguros");
		try {
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			soapResponse = soapConnection.call(generaXml(obj), PATH_SEGURO_VIDAMAX);
			if (soapResponse != null) {
				logger.info("Respuesta del servicio SOAP Valida");
			} else {
				logger.info("Respuesta del servicio SOAP Invalida");
			}

			soapConnection.close();
		} catch (SOAPException e) {
			e.printStackTrace();
			logger.error("Error en el metodo TransmiteXml de la clase GeneraCuerpoXml:");
		}
		return soapResponse;
	}

	public SOAPMessage generaXml(SeguroVidamax obj) {
		SOAPMessage soapMessage = null;
		logger.info("Genera el XML de la peticion SOAP");
		try {
			MessageFactory messageFactory = MessageFactory.newInstance();
			soapMessage = messageFactory.createMessage();
			SOAPPart soapPart = soapMessage.getSOAPPart();

			SOAPEnvelope envelope = soapPart.getEnvelope();
			envelope.addNamespaceDeclaration("wsof", PATH_SEGURO_VIDAMAX2);

			SOAPBody soapBody = envelope.getBody();
			SOAPElement soapBodyElem = soapBody.addChildElement("ofertaVidamaxUnificadaRequest", "wsof");
			SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("peticionJSON", "wsof");

			String cuerpojson = convertObjectToJson(obj);

			logger.info("Cuerpojson::: " + cuerpojson);

			soapBodyElem1.addTextNode(cuerpojson);

			MimeHeaders headers = soapMessage.getMimeHeaders();
			headers.addHeader("SOAPAction", String.valueOf(PATH_SEGURO_VIDAMAX2) + "ofertaVidamaxUnificadaRequest");

			soapMessage.saveChanges();
			if (soapMessage != null)

			{
				logger.info("Cuerpo XML de la peticion SOAP construido exitosamente");
			} else {
				logger.info("No se construyo el cuerpo XML de la peticion SOAP");
			}

		} catch (SOAPException e) {
			logger.error("Error al generar el cuerpo XML de la peticion SOAP");
		}
		return soapMessage;
	}

	public String obtenJson(SOAPMessage soapMessage) {
		SOAPPart soapPart = soapMessage.getSOAPPart();
		String cadenajson = null;
		logger.info("Obtener JSON de la respuesta XML del servicio SOAP");

		try {
			SOAPEnvelope envelope = soapPart.getEnvelope();
			SOAPBody soapBody = envelope.getBody();

			Iterator<Node> itr = soapBody.getChildElements();
			while (itr.hasNext()) {
				Node node = (Node) itr.next();
				if (node.getNodeType() == 1) {

					Element ele = (Element) node;

					NodeList statusNodeList = ele.getChildNodes();
					for (int i = 0; i < statusNodeList.getLength(); i++) {
						Element elementResult = (Element) statusNodeList.item(i);
						String str;
						switch ((str = elementResult.getNodeName()).hashCode()) {
						case 1346564264:
							if (!str.equals("respuestaJSON"))
								break;
							cadenajson = elementResult.getTextContent();
							if (cadenajson != null) {
								logger.info(
										"Cuerpo del JSON contenido en la respuesta XML extraido con exito con cadena JSON "
												+ cadenajson);
							} else {
								logger.info("Cuerpo del JSON contenido en la respuesta XML extraido sin exito");
							}

							break;
						}

					}
					continue;
				}
				node.getNodeType();
			}

		} catch (SOAPException e) {
			e.printStackTrace();
			logger.error("XML de respuesta invalido");
		}

		logger.info("Cadena JSON:::" + cadenajson);
		return cadenajson;
	}

	public void extraeLista(String json) {
		if (json != null) {
			logger.info("Extrayendo del JSON lista de ofertas y beneficios de seguros");
			try {
				JSONObject jsonObject = new JSONObject(json);

				if (jsonObject.has("seguros")) {

					if (!listaOfertas.isEmpty()) {
						listaOfertas.clear();
					}

					listaOfertas = new Gson().fromJson(
							jsonObject.getJSONObject("seguros").getJSONArray("lista").toString(),
							new TypeToken<List<SeguroDeVida>>() {
							}.getType());

					logger.info("listaOfertas:::" + listaOfertas);

					if (listaOfertas.isEmpty()) {
						new Exception();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error al momento de extraer JSON de la respuesta XML: ");
				// throw new MessageException(CodigoErrorEnum.ERROR_GENERICO.getCodigo());
			}
		}
	}

	private void obtenerListaCotizador(CotizadorResponse responseTO, CotizadorRequest request) {
		//float montoSeguro = 30;
		

		String respuesta = getCotizadorDao();

		JSONObject obj = new JSONObject(respuesta);
		Gson gson = new Gson();

		// if (util.validarCodRespOriginacion(obj) && util.validaCodExito2(obj)) {
		boolean aptoSeguro = false;
		JSONArray jsonArray = null;
		if (obj.get("abonos") instanceof JSONArray) {
			logger.info("Entra a Es un array");
			jsonArray = obj.getJSONArray("abonos");
			aptoSeguro = true;
		} 
		else {
			logger.info("Entra a Es objeto");
			// JSONObject obj2 = new JSONObject(obj.get("data").toString());
			jsonArray = obj.getJSONArray("abonos");// si lo parsea es un objeto

			// aptoSeguro = obj2.getBoolean("aptoSeguro");
			aptoSeguro = true;

		}

		String json = jsonArray.toString();

		Type type = new TypeToken<List<DatosCotizacion>>() {
		}.getType();
		List<DatosCotizacion> listaCotizacion = gson.fromJson(json, type);

		for(DatosCotizacion dato : listaCotizacion) {
	    	int result = CPD -(dato.getPagoPuntual());
	        if(result >=10 & result <= 14){		
	    		dato.setListaOfertas(cargaListaSeguros(10, dato.getPagoNormal(), dato.getPagoPuntual() , dato.getPagoNormalPromo(), dato.getPagoPuntualPromo()));
	    	}else if(result >=15 & result <= 19){
	    		dato.setListaOfertas(cargaListaSeguros(15, dato.getPagoNormal(), dato.getPagoPuntual() , dato.getPagoNormalPromo(), dato.getPagoPuntualPromo()));
	    	}else if(result >=20 & result <= 24){
	    		dato.setListaOfertas(cargaListaSeguros(20, dato.getPagoNormal(), dato.getPagoPuntual() , dato.getPagoNormalPromo(), dato.getPagoPuntualPromo()));
	    	}else if(result >=25 & result <= 29){
	    		dato.setListaOfertas(cargaListaSeguros(25, dato.getPagoNormal(), dato.getPagoPuntual() , dato.getPagoNormalPromo(), dato.getPagoPuntualPromo()));
	    	}else if(result >= 30){
	    		dato.setListaOfertas(cargaListaSeguros(30, dato.getPagoNormal(), dato.getPagoPuntual() , dato.getPagoNormalPromo(), dato.getPagoPuntualPromo()));
	    	}
	        
	       // logger.info(" ::::: "+dato.isAplicaSeguro()+" ---  "+(dato.getListaOfertas()!=null)+" ----- "+dato.getListaOfertas().size());
	        
	        if(dato.isAplicaSeguro()==false && (dato.getListaOfertas()==null || dato.getListaOfertas().size()==0)) {
	    		dato.setAplicaSeguro(false);	
	    	}
	        else {
	        	dato.setAplicaSeguro(true);
	        }
	    }
	    

		responseTO.setAptoSeguro(aptoSeguro);
		responseTO.setListaCotizador(listaCotizacion);

	}

	private String getCotizadorDao() {
		String resp = "";

		try {
			resp = obtenerCotizador();
		} catch (Exception e) {
			resp = obtenerCotizador();
		}

		return resp;
	}

	public String obtenerCotizador() {
		logger.info("Consultando el metodo [ CotizadorDaoImpl::obtenerCotizador ]");
		String response = "";
		try {

			// String urlWS = URL_SERVICIO_COTIZADOR;
			String urlWS = "http://10.95.69.171:8085/creditrun?canal=1&pais=1&periodo=1&min=2000&max=12000&tasa=J&sucursal=100&nivelCR=7&descuento=35.00&capacidadDePago=787&plazoIni=13&plazoFin=100";
			logger.info(urlWS);

			response = invocaServicioGet(urlWS);

		} catch (RestClientException e) {
			logger.info("Incidencia dao obtenerCotizador");
			return response;

		} catch (RuntimeException e) {
			logger.info("Incidencia dao obtenerCotizador: {}", e.getMessage());
			// throw new BDMCreditoException(e);
		}
		return response;
	}

	public String invocaServicioGet(String urlWs) {

		String response = null;
		RestTemplate rest = new RestTemplate();
		rest.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
		SimpleClientHttpRequestFactory requestFactory = (SimpleClientHttpRequestFactory) rest.getRequestFactory();
		rest.setRequestFactory(requestFactory);
		response = rest.getForObject(urlWs, String.class);

		return response;
	}

	public List<SeguroDeVida> cargaListaSeguros(int val, int pagoNormal, int pagoPuntual, int pagoNormalPromo,int pagoPuntualPromo) {
		List<SeguroDeVida> lista = new ArrayList<>();
		for (SeguroDeVida seguro : listaOfertas) {
			if (seguro.getPrecio() <= val) {
				lista.add(
						new SeguroDeVida(
								seguro.getPrecioCalculado(), 
								seguro.getPrecio(), 
								seguro.getSobrePrecio(), 
								seguro.getMontoFinal(),
								seguro.getProductoId(), 
								seguro.getNombre(), 
								(seguro.getPrecio() + pagoNormal), 
								(seguro.getPrecio() + pagoPuntual),
								(seguro.getPrecio() + pagoNormalPromo),
								(seguro.getPrecio() + pagoPuntualPromo),
								(seguro.getMontoFinal() * 2)));
			}
		}
		return lista;
	}

	public Object convertJsonToObject(String json, Class classe) {
		Object objeto = null;
		Gson gson = new GsonBuilder().serializeNulls().create();
		try {
			objeto = gson.fromJson(json, classe);
		} catch (Exception e) {
			logger.info("Incidencia en metodo convertJsonToObject", e);
			objeto = null;
		}
		return objeto;
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
