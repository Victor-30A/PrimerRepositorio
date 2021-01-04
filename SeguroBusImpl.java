package com.bancoazteca.bdm.cotizador.BDMCotizacion.business;



import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;

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

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.ClienteUnicoTazTO;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.RootLineaLC;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SeguroDeVida;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SeguroVidamax;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


@Component
public class SeguroBusImpl {
	
	private static final Logger logger = LoggerFactory.getLogger(SeguroBusImpl.class);
	public static final String PATH_SEGURO_VIDAMAX = "http://10.50.53.239:8080/SegurosServicios/VidamaxUnificada";
	//public static final String PATH_SEGURO_VIDAMAX = "";
	public static final String ENCABEZADO_VIDAMAX ="http://www.segurosazteca.com.mx/WSOfertaVidamaxUnificada";
	private static final String ENCODING_UTF_8 = "UTF-8";
	private List<SeguroDeVida> listaOfertas = new ArrayList<>();
	
	
	public Object consultaLineaCredito(ClienteUnicoTazTO clienteUnicoTazTO, boolean ambienteProductivo) {
		logger.info("Entra al consultaLineaCredito");
		Object[] responseArray = new Object[2];
		try {
			String urlWs = "http://10.51.210.58:80/servlet/adn.EjeqQuery";//--Original
			//String urlWs = "http://10.51.210.580:80/servlet/adn.EjeqQuery";
			
			if (ambienteProductivo) {
				System.out.println("Entra ambiente productivo");
				urlWs = pathProductivo(urlWs, clienteUnicoTazTO);
				
			} else {
				System.out.println("No Entra ambiente productivo");
				urlWs = pathDesaLineaCredito(urlWs, clienteUnicoTazTO);

			}
			urlWs = parametrizarDatosFNATPRLS0160(urlWs, clienteUnicoTazTO);

			logger.info("Path linea de credito  " + urlWs);

			responseArray = invocaServicioXML(urlWs, RootLineaLC.class, 5000,5000);

		} catch (RestClientException e) {
			logger.info("Incidencia dao de la consulta de linea ");

		} catch (RuntimeException e) {
			logger.info("Incidencia dao consultaLineaCredito ");
		}
		return responseArray[0];
	}
	
	public Object[] invocaServicioXML(String urlWs, Class<?> entity, int conTimeOut, int redTimeOut) {
		Object[] response = new Object[2];
		RestTemplate rest = new RestTemplate();
		rest.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName(ENCODING_UTF_8)));
		SimpleClientHttpRequestFactory requestFactory = (SimpleClientHttpRequestFactory) rest.getRequestFactory();
		requestFactory.setConnectTimeout(conTimeOut);
		requestFactory.setReadTimeout(redTimeOut);

		rest.setRequestFactory(requestFactory);
		UriTemplate expanded = new UriTemplate(urlWs);
		try {
			String url = URLDecoder.decode(expanded.toString(), ENCODING_UTF_8);
			String responseCad = new String(rest.getForObject(url, String.class).getBytes(), StandardCharsets.UTF_8).replace("&", "&amp;");
			ObjectMapper objectMapper = new ObjectMapper();
			//ObjectMapper objectMapper = new ObjectMapper();
			responseCad.replace("&", "&amp;");
			response[0] = responseCad;
			response[1] = objectMapper.readValue(responseCad, entity);
		} catch (UnsupportedEncodingException e) {
			logger.info("Incidencia UTF8 al codificar url");
		} catch (JsonParseException e) {
			logger.info("Incidencia al parcear el XML " + e);

		} catch (JsonMappingException e) {
			logger.info("Incidencia al mapear el XML");
		} catch (IOException e) {
			logger.info("Incidencia en IO");
		}


		return response;
	}
	public String pathDesaLineaCredito(String path, ClienteUnicoTazTO clienteUnicoTazTO) {
		logger.info("Construyendo query");
		StringBuilder query = new StringBuilder();
		query.append("Query=call%20")
				.append("RCREDITO.FNATPRLS0160(pais,canal,sucursal,folio");
		if (Validations.isNullOrEmpty(clienteUnicoTazTO.getDs()))
			path = path.concat("&").concat(query.toString());
		else
			path = path.concat("?").concat("ds=").concat(clienteUnicoTazTO.getDs()).concat("&").concat("usr=")
					.concat(clienteUnicoTazTO.getUsuario()).concat("&").concat("pwd=")
					.concat(clienteUnicoTazTO.getPwd()).concat("&").concat(query.toString());
		return path;
	}
	public String pathProductivo(String path, ClienteUnicoTazTO clienteUnicoTazTO) {
		logger.info(" Armando path productivo ");
		String usr = "usr=".concat(clienteUnicoTazTO.getUsuario()), pws = "pwd=".concat(clienteUnicoTazTO.getPwd()),
				ds = "ds=".concat(clienteUnicoTazTO.getDs());
		path = path.concat("?").concat(ds).concat("&").concat(usr).concat("&").concat(pws);
		logger.info("Se path productivo->".concat(path));
		return this.pathDesarrolloLineaCredito(path, null);
	}
	public String pathDesarrolloLineaCredito(String path, String ds) {
		logger.info("Armando path query");
		String query = "Query=call%20"
				.concat("RCREDITO.FNATPRLS0160(pais,canal,sucursal,folio)"),dsTarjeta = "ds=";
		if (Validations.isNullOrEmpty(ds))
			path = path.concat("&").concat(query);
		else
			path = path.concat("?").concat(dsTarjeta).concat(ds).concat("&").concat(query);

		logger.info("Path query -> ".concat(path));
		return path;
	}
	public String parametrizarDatosFNATPRLS0160(String path, ClienteUnicoTazTO clienteUnicoTazTO) {
		try {
			logger.info("Path  ".concat(path));

			path = path.replaceAll("pais", clienteUnicoTazTO.getPais())
					.replaceAll("canal", clienteUnicoTazTO.getCanal())
					.replaceAll("sucursal", clienteUnicoTazTO.getSucursal())
					.replaceAll("folio", clienteUnicoTazTO.getFolio());
		} catch (Exception e) {
			logger.info("ocurrio un problema parametrizarDatosFNATPRLS0160 ".concat(e.getMessage()));
		}

		return path;
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
	
	/*
	public List<SeguroDeVida> cargaListaSeguros(int val, int pagoNormal, int pagoPuntual, int pagoNormalPromo,int pagoPuntualPromo) {
      List<SeguroDeVida> lista = new ArrayList<>();
      for (SeguroDeVida seguro : listaOfertas) {
          if (seguro.getPrecio() <= val &&  seguro.getPrecio() >= 10) {
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
          else if((seguro.getPrecio()>=5 && seguro.getPrecio() <10) && val  ) {
        	  
          }
      }
      return lista;
  }
	*/
	
	public Document convierteStringAXML(String xmlStr){
	  logger.info("Convirtiendo string a XML" + xmlStr);
		if (xmlStr != null) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			try {
				builder = factory.newDocumentBuilder();
				Document doc = builder.parse(new InputSource(new StringReader(xmlStr)));
				return doc;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error al obtener la oferta de seguros Causa del error: " + e.getCause().toString() + e.getMessage().toString() + "*******" + e.toString());
			}
		}
		return null;
	}
	
	public void obtenerOfertasSeguros(SeguroVidamax obj) {
		try {
			logger.info("consumir servicio de seguros");
			extraeLista(obtenJson(transmiteXml(obj)));
		} catch (Exception e) {
			logger.error("Error al obtener la oferta de seguros " + ", Causa del error: " + e.getCause().toString() + "*******" + e.toString());
		}
	}
	
	public SOAPMessage transmiteXml(SeguroVidamax obj) {
	    logger.info("objeto segurovidamax: " + obj.toString());
		SOAPMessage soapResponse = null;
		try {
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			soapResponse = soapConnection.call(generaXml(obj), PATH_SEGURO_VIDAMAX);
			if (soapResponse != null){
				logger.info("Respuesta del servicio SOAP Valida");
			} else {
				logger.info("Respuesta del servicio SOAP Invalida");
			}

			soapConnection.close();
		} catch (SOAPException e) {
			logger.error("Error en el metodo TransmiteXml de la clase GeneraCuerpoXml:" + ", Causa del error: " + e.getCause().toString() + "*******" + e.toString());
		}
		return soapResponse;
	}
	
	public SOAPMessage generaXml(SeguroVidamax obj) {
		SOAPMessage soapMessage = null;
		try {
			MessageFactory messageFactory = MessageFactory.newInstance();
			soapMessage = messageFactory.createMessage();
			SOAPPart soapPart = soapMessage.getSOAPPart();

			SOAPEnvelope envelope = soapPart.getEnvelope();
			envelope.addNamespaceDeclaration("wsof", ENCABEZADO_VIDAMAX);

			SOAPBody soapBody = envelope.getBody();
			SOAPElement soapBodyElem = soapBody.addChildElement("ofertaVidamaxUnificadaRequest", "wsof");
			SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("peticionJSON", "wsof");

			String cuerpojson = convertObjectToJson(obj);

			logger.info("Cuerpojson::: " + cuerpojson);

			soapBodyElem1.addTextNode(cuerpojson);

			MimeHeaders headers = soapMessage.getMimeHeaders();
			headers.addHeader("SOAPAction", ENCABEZADO_VIDAMAX + "ofertaVidamaxUnificadaRequest");

			soapMessage.saveChanges();
			if (soapMessage != null){logger.info("Cuerpo XML de la peticion SOAP construido exitosamente");	} 
			else {logger.info("No se construyo el cuerpo XML de la peticion SOAP");	}

		} catch (SOAPException e) {
			logger.error("Error al generar el cuerpo XML de la peticion SOAP");
		}
		return soapMessage;
	}
	
	public String obtenJson(SOAPMessage soapMessage) {
		SOAPPart soapPart = soapMessage.getSOAPPart();
		String cadenajson = null;
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
							if (cadenajson != null){
								logger.info("Cuerpo del JSON contenido en la respuesta XML extraido con exito con cadena JSON " + cadenajson );
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
			logger.error("Error XML de respuesta invalido" + e.getStackTrace().toString() + ", Causa del error: " + e.getCause().toString() + "*******" + e.toString());
		}
		logger.info("Cadena JSON::: " + cadenajson);
		return cadenajson;
	}
	
	public void extraeLista(String json) {
		if (json != null) {
			try {
				JSONObject jsonObject = new JSONObject(json);
				if(!listaOfertas.isEmpty()) {listaOfertas.clear();}
				if (jsonObject.has("seguros")) {	
					listaOfertas = new Gson().fromJson(jsonObject.getJSONObject("seguros").getJSONArray("lista").toString(), new TypeToken<List<SeguroDeVida>>() {}.getType());
					logger.info("listaOfertas::: " + listaOfertas);
					if (listaOfertas.isEmpty()) {
						new Exception();
					} 
				} 
			} catch (Exception e) {
				logger.error("Error al momento de extraer JSON de la respuesta XML" + e.getStackTrace().toString() + ", Causa del error: " + e.getCause().toString() + "*******" + e.toString());
				//throw new MessageException(CodigoErrorEnum.ERROR_GENERICO.getCodigo());
			}
		}
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
	
	public boolean validaRespuesta(Object obj) {
		try {
			Class<? extends Object> codigo = obj.getClass();
			if (Validations.isNullOrEmpty(codigo))
				return false;
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
