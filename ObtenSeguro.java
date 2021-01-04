package com.bancoazteca.bdm.cotizador.BDMCotizacion.business;

import java.io.ByteArrayOutputStream;
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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.bancoazteca.bdm.cotizador.BDMCotizacion.dao.encryptJavaCode1;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.Cliente;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.InformacionBase;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.ProductoCredito;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.Seguro;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SeguroDeVida;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SeguroVidamax;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


@Component
public class ObtenSeguro {

	
	private static final Logger logger = LoggerFactory.getLogger(ObtenSeguro.class);
	List<SeguroDeVida> listaOfertas = new ArrayList<>();
	final String uri = "http://10.81.27.220/SegurosServicios/VidamaxUnificada";
	private static final String CHARSET = "UTF-8";
	
	public ResponseEntity<?> obtenSeguroVidamax(SeguroVidamax svmx, String request){
		
		svmx =  new SeguroVidamax ();
		svmx.setTipoOferta(1);
		svmx.setOrigen(10);
		svmx.setCliente(new Cliente(1,1,8624,72525,15,"1987-12-24",false));
		svmx.setProductoCredito(new ProductoCredito(0,0,1,0));  
		svmx.setSeguro(new Seguro(0.16,false,0));
		svmx.setInformacionBase(new InformacionBase(1,"926811","WS_SFIN04"));
		
		
		
		try {
			logger.info("EL REQUEST ES " + request);
			//obtenerOfertasSeguros(svmx);
			obtenerTOKEN(request);
			
			
			
		}catch(Exception er) {
			System.out.println("Error en el metodo obtenSeguroVidmax en " + er);
		}
		return new ResponseEntity<Object>(listaOfertas, HttpStatus.OK);
	}
	
	public void obtenerOfertasSeguros(SeguroVidamax svmx) {

		//////////logger.info("Inicia proceso consulta de seguros");
		try {
	//////////logger.info("consumir servicio de seguros");
			extraeLista(obtenJson(transmiteXml(svmx)));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error al obtener la oferta de seguros " + e);
		}
	}
	
	
	public SOAPMessage transmiteXml(SeguroVidamax svmx) {
		SOAPMessage soapResponse = null;
//////////logger.info("Proceso para consumir servicio SOAP de seguros");
		try {
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			//soapResponse = soapConnection.call(generaXml(obj),uri);
			//soapResponse = soapConnection.call(generaXmlEncriptado(svmx), uri);
			//soapResponse = soapConnection.call(generaTOKEN(), "http://10.50.53.239:8080/SegurosServicios/WSTokenServicesGen?wsdl");
			
			if (soapResponse != null){
				 String mensajin = toString(soapResponse);
				 logger.info("El mensaje de la generacion del token es  " + mensajin);
				
				//logger.info("Respuesta del servicio SOAP Valida con respuesta: " + soapResponse);
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
		//logger.info("Obtener JSON de la respuesta XML del servicio SOAP");

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

		//logger.info("Cadena JSON:::" + cadenajson);
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
	 
	 
	 /*Encriptados*/
	 
	 public SOAPMessage generaXmlEncriptados(SeguroVidamax obj) {
		 logger.info("ENTRA AL generaXmlEncriptado");
			SOAPMessage soapMessage = null;
			encryptJavaCode1 enc = new encryptJavaCode1();
			String encriptado = null;
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
				//toString(soapMessage);
				String mensajin = toString(soapMessage);
				logger.info("El mensajin debe ser " + mensajin);
				
				//generaTOKEN();
				
				try {
					encriptado = enc.invoke("5arRQBILSsSg0SuDOX327kHKVmZjjJp/jfBMo0FBtAY=", mensajin);
				}catch(Exception er) {
					er.printStackTrace();	
				}
				if (soapMessage != null){
					logger.info("Cuerpo XML de la peticion SOAP construido exitosamente");
					/*PODRIA IR AQUI*/
					logger.info("PODRIA IR AQUI");
					logger.info(soapMessage.toString());
					
				} 
				else {
					logger.info("No se construyo el cuerpo XML de la peticion SOAP");
				}

			} catch (SOAPException e) {
				logger.error("Error al generar el cuerpo XML de la peticion SOAP");
			}
			return soapMessage;
		}
	 
	 
	  
	 
	 /**********************************************************************METODOS PARA GENERAR EL TOKEN************************************************************************************************************************/
	 public void obtenerTOKEN(String user) {

			//////////logger.info("Inicia proceso consulta de seguros");
			try {
		//////////logger.info("consumir servicio de seguros");
				extraeLista(obtenJson(transmiteXmlTOKEN(user)));
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error al obtener la oferta de seguros " + e);
			}
		}
	 
	 public SOAPMessage transmiteXmlTOKEN(String user) {
			SOAPMessage soapResponse = null;
			try {
				SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
				SOAPConnection soapConnection = soapConnectionFactory.createConnection();

				soapResponse = soapConnection.call(generaTOKEN(user), "http://10.50.53.239:8080/SegurosServicios/WSTokenServicesGen?wsdl");
				
				if (soapResponse != null){
					 String mensajin = toString(soapResponse);
					 logger.info("El mensaje de la generacion del token es  " + mensajin);
					
					//logger.info("Respuesta del servicio SOAP Valida con respuesta: " + soapResponse);
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
	 
	 public SOAPMessage generaTOKEN(String nombre) {
		 logger.info("METODO  PARA OBTENER PRIMER TOKEN");
		 String pass = "PASSWORDBAZ";
		 logger.info("ENTRA AL generaXmlEncriptado");
			SOAPMessage soapMessage = null;
			encryptJavaCode1 enc = new encryptJavaCode1();
			String encriptado = null;
			try {
				MessageFactory messageFactory =  MessageFactory.newInstance();
				 soapMessage = messageFactory.createMessage();
				 SOAPPart soapPart = soapMessage.getSOAPPart();
				 SOAPEnvelope envelope = soapPart.getEnvelope();
				 envelope.addNamespaceDeclaration("ser", "http://www.afore.com/schemas/token/services");
				 SOAPBody soapBody = envelope.getBody();
				 SOAPElement soapBodyElem = soapBody.addChildElement("credentials", "ser");
				 SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("userName", "ser");
				 SOAPElement soapBodyElem3 = soapBodyElem.addChildElement("password", "ser");
				 soapBodyElem2.addTextNode(nombre);
				 soapBodyElem3.addTextNode(pass);
				 MimeHeaders headers = soapMessage.getMimeHeaders();
				 headers.addHeader("SOAPAction", String.valueOf("http://www.afore.com/schemas/token/services") + "credentials");
				String mensajin = toString(soapMessage);
				logger.info("El mensajin debe ser " + mensajin);
				
				try {
					encriptado = enc.invoke("5arRQBILSsSg0SuDOX327kHKVmZjjJp/jfBMo0FBtAY=", mensajin);
				}catch(Exception er) {
					er.printStackTrace();	
				}
				if (soapMessage != null){
					logger.info("Cuerpo XML de la peticion SOAP construido exitosamente");
					/*PODRIA IR AQUI*/
					logger.info("PODRIA IR AQUI");
					logger.info(soapMessage.toString());
					
				} 
				else {
					logger.info("No se construyo el cuerpo XML de la peticion SOAP");
				}

			} catch (SOAPException e) {
				logger.error("Error al generar el cuerpo XML de la peticion SOAP");
			}
			return soapMessage;
		}
	 
	 public static String toString(SOAPMessage message) {
		    try {
		        ByteArrayOutputStream out = new ByteArrayOutputStream();
		        message.writeTo(out);
		        //logger.info("Se tuvo que imprimir mensaje==========" + message);
		        return new String(out.toByteArray(),CHARSET);
		    } catch (Exception ex) {
		        logger.error(ex.getMessage(), ex);
		        return "";
		    }
		}

}
