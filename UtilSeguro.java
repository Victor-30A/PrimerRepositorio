package com.bancoazteca.bdm.cotizador.BDMCotizacion.business;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Iterator;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.RespuestaBase;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.RespuestaSeguro;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SeguroVidamax;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;



@SuppressWarnings("deprecation")
@Component
public class UtilSeguro {
	private static Logger logger = LoggerFactory.getLogger(UtilSeguro.class);
	@Autowired private SeguroBusinessImpl seguroBusiness;
	
	RespuestaSeguro respuestaSeguro = new RespuestaSeguro(); 
	RespuestaBase respuestaBase =  new RespuestaBase();
	


	//String urlSeguros = "http://10.81.27.220/SegurosServicios/VidamaxUnificada";
	//String urlSeguros = "http://10.50.53.239:8080/SegurosServicios/VidamaxUnificada";   http://10.81.27.220/SegurosServiciosQA/VidamaxUnificada?wsdl
	String urlSeguros = "http://10.81.27.220/SegurosServiciosQA/VidamaxUnificada";
	

	
	public SOAPMessage transmiteXml(SeguroVidamax sgvmx) {
		logger.info("SOLO DEBE DE ENTRAR UNA VEZ");
		SOAPMessage soapMessage = null;
		try {
			SOAPConnection connection = SOAPConnectionFactory.newInstance().createConnection();
			URL url = new URL(new URL(urlSeguros),"",new URLStreamHandler() {
				@Override
				protected URLConnection openConnection(URL url) throws IOException {
					URL target = new URL(url.toString());
					URLConnection connection = target.openConnection();
					// Tiempos de consumo
					connection.setConnectTimeout(1500); 
					connection.setReadTimeout(1500); 
					return(connection);
				}
				
			});
			soapMessage = connection.call(generaXml(sgvmx), url);
			if (soapMessage != null) {
				logger.info("Respuesta del servicio SOAP Valida " );
				
				//////////String RESPUESTA;
				//////////RESPUESTA = soapMessageToString(soapMessage);
				
				//////////logger.info("ESTA FUCK RESPUEST ES : "  + RESPUESTA) ;
				
				
				//seguroBusiness.llenaCodigosSeguro(obtenJson(soapMessage));
			} 
			else {
				logger.info("Respuesta del servicio SOAP Invalida");
			}
			connection.close();
		}catch(SOAPException | MalformedURLException e ) {
			logger.error("Hubo una incidencia en obtener seguro vidamax en: " +e.getMessage());
			
		}
		return soapMessage;
	}
	
	
	public String soapMessageToString(SOAPMessage message) 
    {
        String result = null;

        if (message != null) 
        {
            ByteArrayOutputStream baos = null;
            try 
            {
                baos = new ByteArrayOutputStream();
                message.writeTo(baos); 
                result = baos.toString();
            } 
            catch (Exception e) 
            {
            } 
            finally 
            {
                if (baos != null) 
                {
                    try 
                    {
                        baos.close();
                    } 
                    catch (IOException ioe) 
                    {
                    }
                }
            }
        }
        return result;
    }   

	
	
	
	
	
	
	

	public SOAPMessage generaXml(SeguroVidamax obj) {
		SOAPMessage soapMessage = null;
		
		 //AttachmentPart a = soapMessage.createAttachmentPart();
		 
		try {
			//a.setContentType("application/soap+xml");
			MessageFactory messageFactory = MessageFactory.newInstance();
			
			soapMessage = messageFactory.createMessage();
			SOAPPart soapPart = soapMessage.getSOAPPart();

			SOAPEnvelope envelope = soapPart.getEnvelope();
			envelope.addNamespaceDeclaration("wsof", "http://www.segurosazteca.com.mx/WSOfertaVidamaxUnificada");

			SOAPBody soapBody = envelope.getBody();
			SOAPElement soapBodyElem = soapBody.addChildElement(ConstantesSeguro.REQUEST_VIDAMAX,ConstantesSeguro.WSOF);
			SOAPElement soapBodyElem1 = soapBodyElem.addChildElement(ConstantesSeguro.PETICION_JSON,ConstantesSeguro.WSOF);

			String cuerpojson = convertObjectToJson(obj);

			logger.info("Request a seguros ::: " + cuerpojson);

			soapBodyElem1.addTextNode(cuerpojson);

			MimeHeaders headers = soapMessage.getMimeHeaders();
			headers.addHeader("SOAPAction", String.valueOf("http://www.segurosazteca.com.mx/WSOfertaVidamaxUnificada") + "ofertaVidamaxUnificadaRequest");

			soapMessage.saveChanges();

		} catch (SOAPException e) {
			logger.info("Incidencia al generar el cuerpo XML de la peticion SOAP: {}", e.getMessage());
		}
		
		StringBuffer sbuf = new StringBuffer();
		try {
			 sbuf.append(soapMessage.toString());
			 ByteArrayOutputStream baos = new ByteArrayOutputStream();
			 soapMessage.writeTo(baos); 
			 
			
		}catch(Exception e) {
			logger.error("No se pudo imprimir XML por : " + e );
			
		}
		logger.info("La salida del XML es " + sbuf.toString());
		
		
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
							if (!str.equals(ConstantesSeguro.RESPUESTA_JSON))
								break;
							cadenajson = elementResult.getTextContent();
							if (cadenajson != null) {
								logger.info("Cuerpo del JSON  generado: "+ cadenajson);
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
			logger.info("XML de la respuesta invalido: {}", e.getMessage());
			
			
		}
		logger.info("Cadena JSON::: " + cadenajson);
		return cadenajson;
	}
	
	
	/**
	 * @param object
	 * @return
	 */
	public String convertObjectToJson(Object object) {
		String json = "";
		Gson gson = new GsonBuilder().serializeNulls().create();
		try {
			json = gson.toJson(object);
		} catch (Exception e) {
			logger.info("Incidencia en metodo convertObjectToJson: {} ", e.getMessage());
			json = null;
		}
		return json;
	}
}