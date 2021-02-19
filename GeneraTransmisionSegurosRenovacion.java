package com.bancoazteca.bdm.cotizador.BDMCotizacion.business;

import org.slf4j.LoggerFactory;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SeguroDeVida;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SeguroDeVidaRenovacion;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SeguroVidamax;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

@Component
public class GeneraTransmisionSegurosRenovacion {
	private static final Logger logger = LoggerFactory.getLogger(GeneraTransmisionSegurosRenovacion.class);
	final String uri = "http://10.81.27.220/SegurosServicios/VidamaxUnificada";
	//private List<SeguroDeVidaRenovacion> listaOfertas = new ArrayList<>();
	private SeguroDeVidaRenovacion listaOfertas = new SeguroDeVidaRenovacion();
	
	public ResponseEntity<?> ObtenListaSegurosRenovacion(SeguroVidamax svmx, String request){
		ObjectMapper mapper = new ObjectMapper();
		try {
			svmx = (SeguroVidamax) mapper.readValue(request,SeguroVidamax.class);
			obtenerOfertasSegurosRenovacion(svmx);
		}catch(Exception er) {
			er.printStackTrace();
		}
		return new ResponseEntity<>(listaOfertas,HttpStatus.OK);
	}
	
	public void obtenerOfertasSegurosRenovacion(SeguroVidamax obj) {
		try {
			extraeLista(obtenJSON(transmiteXML(obj)));
		}catch(Exception er) {
			logger.error("Error al generar el XML::: ");
			er.printStackTrace();
			
		}
	}
	
	public SOAPMessage transmiteXML (SeguroVidamax obj) {
		SOAPMessage soapResponse =  null;
		try {
			SOAPConnectionFactory soapConnectionFactory  =  SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			soapResponse = soapConnection.call(generaXML(obj), "http://10.81.27.220/SegurosServicios/VidamaxUnificada");
			if(soapResponse != null) {logger.info("Respuesta SOAP::: " + soapResponse);}
			else {logger.error("NO HAY  RESPUESTA SOAP");}
			soapConnection.close();
		}catch(SOAPException e) {
			e.printStackTrace();
			logger.error("Error en el metodo para transmitir el XML");
		}
		return soapResponse;
	}
	
	public String obtenJSON (SOAPMessage soapMessage) {
		String cadenaJSON = null;
		SOAPPart soapPart = soapMessage.getSOAPPart();
		try {
			SOAPEnvelope envelope = soapPart.getEnvelope();
			SOAPBody soapBody = envelope.getBody();
			
			Iterator<Node> itr = soapBody.getChildElements();
			while(itr.hasNext()) {
				Node node = (Node) itr.next();
				if(node.getNodeType()==1) {
					Element element = (Element) node;
					NodeList statusNodeList = element.getChildNodes();
					for(int i = 0; i<statusNodeList.getLength();i++) {
						Element elementResult = (Element) statusNodeList.item(i);
						String str;
						switch((str = elementResult.getNodeName()).hashCode()) {
						case 1346564264:
							if(!str.equals("respuestaJSON"))
								break;
							cadenaJSON = elementResult.getTextContent();
							if(cadenaJSON != null) {logger.info("Cadena JSON::: " + cadenaJSON);}
							else {logger.error("No se pudo extraer la  cadenaJSON");}
							break;
						}
					}
					continue;
				}
				node.getNodeType();
			}
		}catch(Exception e) {
			logger.error("XML de la respuesta invalido::: ");
			e.printStackTrace();	
		}
		
		return cadenaJSON;
	}
	
	public SOAPMessage generaXML (SeguroVidamax obj) {
		SOAPMessage soapMessage = null;
		try {
			MessageFactory messageFactory = MessageFactory.newInstance();
			soapMessage = messageFactory.createMessage();
			SOAPPart soapPart = soapMessage.getSOAPPart();
			
			SOAPEnvelope envelope = soapPart.getEnvelope();
			envelope.addNamespaceDeclaration("wsof", "http://www.segurosazteca.com.mx/WSOfertaVidamaxUnificada");
			SOAPBody soapBody = envelope.getBody();
			SOAPElement soapBodyElemen = soapBody.addChildElement("ofertaVidamaxUnificadaRequest", "wsof");
			SOAPElement soapBodyElemen1 = soapBodyElemen.addChildElement("peticionJSON","wsof");
			
			String objeto = convertObjectToJson(obj);
			soapBodyElemen1.addTextNode(objeto);
			
			MimeHeaders headers = soapMessage.getMimeHeaders();
			headers.addHeader("SOAPAction", String.valueOf("http://www.segurosazteca.com.mx/WSOfertaVidamaxUnificada") + "ofertaVidamaxUnificadaRequest");
			soapMessage.saveChanges();
			
			if(soapMessage != null) {logger.info("Se construyo el cuerpo del XML SOAP");}
			else {logger.error("NOOOO se construyo el cuerpo del XML SOAP");}
		}catch(SOAPException e) {
			logger.error("Error al gemerar el cuerpo XML en la peticion SOAP");
			e.printStackTrace();
		}
		return soapMessage;
	}
	
	public void extraeLista(String json) {
		if(json != null) {
			try {
				JSONObject jsonObject = new JSONObject(json);
				if(jsonObject.has("seguro")) {
					if(!jsonObject.isNull("seguro")) {
						if(jsonObject.getJSONObject("seguro").has("Informacion")) {
							listaOfertas = new Gson().fromJson(jsonObject.getJSONObject("seguro").getJSONObject("Informacion").toString(), new TypeToken<SeguroDeVidaRenovacion>() {}.getType());
					    }else {
					    	listaOfertas.setPrecioCalculado(0);
					    	listaOfertas.setSobrePrecio(0);
					    	listaOfertas.setNombre("");
					    	listaOfertas.setPrecio(0);
					    	listaOfertas.setProductoId(0);
					    	//listaOfertas.setMontoFinal("");
					    }
					}
				}
			}catch(Exception e) {
				logger.error("Error al momento de extraer el JSON de la respuesta XML");
				e.printStackTrace();
				
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
}