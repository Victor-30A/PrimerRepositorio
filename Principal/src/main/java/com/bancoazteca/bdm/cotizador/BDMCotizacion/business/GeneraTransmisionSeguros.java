package com.bancoazteca.bdm.cotizador.BDMCotizacion.business;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SeguroDeVida;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SeguroVidamax;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


@Component
public class GeneraTransmisionSeguros {
	private static final Logger logger = LoggerFactory.getLogger(GeneraTransmisionSeguros.class);
	private List<SeguroDeVida> listaOfertas = new ArrayList<>();
	final String uri = "http://10.81.27.220/SegurosServicios/VidamaxUnificada";
	RestTemplate restTemplate = new RestTemplate();
	
	public ResponseEntity<?> ObtenListaSeguros(SeguroVidamax svmx, String request) {

		ObjectMapper mapper = new ObjectMapper();
		try {
			svmx = (SeguroVidamax) mapper.readValue(request, SeguroVidamax.class);
			obtenerOfertasSeguros(svmx);
			
		} catch (Exception er) {
			logger.error("Error al traer la respuesta " + er);
			er.printStackTrace();
		}

		return new ResponseEntity<>(listaOfertas, HttpStatus.OK);
	}
	
	
	 public void obtenerOfertasSeguros(SeguroVidamax obj) {

			//////////logger.info("Inicia proceso consulta de seguros");
			try {
		//////////logger.info("consumir servicio de seguros");
				extraeLista(obtenJson(transmiteXml(obj)));
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error al obtener la oferta de seguros " + e);
			}
		}
	 public SOAPMessage transmiteXml(SeguroVidamax obj) {
			SOAPMessage soapResponse = null;
	//////////logger.info("Proceso para consumir servicio SOAP de seguros");
			try {
				SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
				SOAPConnection soapConnection = soapConnectionFactory.createConnection();
				soapResponse = soapConnection.call(generaXml(obj), "http://10.81.27.220/SegurosServicios/VidamaxUnificada");
				if (soapResponse != null){
			//////////logger.info("Respuesta del servicio SOAP Valida con respuesta: " + soapResponse);
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
				e.printStackTrace();
				logger.error("XML de respuesta invalido");
			}

			logger.info("Cadena JSON:::" + cadenajson);
			return cadenajson;
		}
	 public SOAPMessage generaXml(SeguroVidamax obj) {
			SOAPMessage soapMessage = null;
	///////logger.info("Genera el XML de la peticion SOAP");
			try {
				MessageFactory messageFactory = MessageFactory.newInstance();
				soapMessage = messageFactory.createMessage();
				SOAPPart soapPart = soapMessage.getSOAPPart();

				SOAPEnvelope envelope = soapPart.getEnvelope();
				envelope.addNamespaceDeclaration("wsof", "http://www.segurosazteca.com.mx/WSOfertaVidamaxUnificada");

				SOAPBody soapBody = envelope.getBody();
				SOAPElement soapBodyElem = soapBody.addChildElement("ofertaVidamaxUnificadaRequest", "wsof");
				SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("peticionJSON", "wsof");
				String objeto = convertObjectToJson(obj); 

				logger.info("Cuerpojson::: " + objeto);

				soapBodyElem1.addTextNode(objeto);

				MimeHeaders headers = soapMessage.getMimeHeaders();
				headers.addHeader("SOAPAction", String.valueOf("http://www.segurosazteca.com.mx/WSOfertaVidamaxUnificada") + "ofertaVidamaxUnificadaRequest");

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
	 
	 public void extraeLista(String json) {
			if (json != null) {
				//////////logger.info("Extrayendo del JSON lista de ofertas y beneficios de seguros");
				try {
					JSONObject jsonObject = new JSONObject(json);

					if (jsonObject.has("seguros")) {
						
						if(!listaOfertas.isEmpty()) {listaOfertas.clear();}
						
						if(!jsonObject.isNull("seguros")) {
						
						listaOfertas = new Gson().fromJson(jsonObject.getJSONObject("seguros").getJSONArray("lista").toString(), new TypeToken<List<SeguroDeVida>>() {}.getType());
						
						logger.info("listaOfertas:::"+listaOfertas);
						
						if (listaOfertas.isEmpty()) {
							new Exception();
						} 
						
						}
					} 
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("Error al momento de extraer JSON de la respuesta XML: ");
					
				}
			}
		}
	 
	 
	  
	  @SuppressWarnings({ "unchecked", "rawtypes" })
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