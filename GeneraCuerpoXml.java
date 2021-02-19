package com.bancoazteca.bdm.cotizador.BDMCotizacion.business;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

import org.json.JSONException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SeguroVidamax;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SobrePrecio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



@Component
public class GeneraCuerpoXml {
	private static String result;
	SOAPMessage soapMessage = null;
	private static final Logger LOGGER = LoggerFactory.getLogger(GeneraCuerpoXml.class);

	public ResponseEntity<String> ObtenerOfertasSeguros(SeguroVidamax obj, String request) {

		ResponseEntity<String> response = null;
		ResponseEntity<SOAPMessage> response2 =  null;
		
		ObjectMapper mapper = new ObjectMapper();

		LOGGER.info("Inicia proceso consulta de seguros");
		try {

			obj = (SeguroVidamax) mapper.readValue(request, SeguroVidamax.class);
			LOGGER.info("consumir servicio de seguros");
			//generaXml(obj);
			
			//response = new ResponseEntity<String>(result, HttpStatus.OK);
			response2 = new ResponseEntity<SOAPMessage>(generaXml(obj), HttpStatus.OK);
			
			
			LOGGER.info("Extraccion exitosa, resultado: " + response2);
		} catch (IOException e) {
			LOGGER.error("Error al obtener la oferta de seguros " + e);
		} catch (JSONException e) {

			LOGGER.error("Error JSON");
		}
		return response;
	}

	public ResponseEntity<String> ObtenerOfertasSeguras(SobrePrecio obj, String request) {

		ResponseEntity<String> response = null;
		ObjectMapper mapper = new ObjectMapper();

		String oferta = "";
		LOGGER.info("Inicia proceso consulta de sobreprecio");
		try {

			obj = (SobrePrecio) mapper.readValue(request, SobrePrecio.class);
			LOGGER.info("consumir servicio de seguros");

			response = new ResponseEntity<String>(oferta, HttpStatus.OK);
			LOGGER.info("Extraccion del sobreprecio exitosa, resultado: " + response);
		} catch (IOException e) {
			LOGGER.error("Error al obtener el sobreprecio " + e);
		} catch (JSONException e) {

			LOGGER.error("Error JSON");
		}

		return response;
	}

	public SOAPMessage TransmiteXml(SeguroVidamax obj) {
		SOAPMessage soapResponse = null;
		LOGGER.info("Proceso para consumir servicio SOAP de seguros");
		try {
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			String url = "http://10.51.25.211:8080/vidamaxPrestamoPersonal/ofertaNormal?wsdl";
			soapResponse = soapConnection.call(generaXml(obj), url);
			if (soapResponse != null)

			{
				LOGGER.info("Respuesta del servicio SOAP Valida");
			} else {
				LOGGER.info("Respuesta del servicio SOAP Invalida");
			}

			soapConnection.close();
		} catch (SOAPException e) {
			LOGGER.error("Error en el metodo TransmiteXml de la clase GeneraCuerpoXml:");
		}
		return soapResponse;
	}

	public SOAPMessage generaXml(SeguroVidamax obj) {
		SOAPMessage soapMessage = null;

		String serverURI = "http://www.segurosazteca.com.mx/WSOfertaVidamaxUnificada";
		LOGGER.info("Genera el XML de la peticion SOAP");

		try {
			MessageFactory messageFactory = MessageFactory.newInstance();
			soapMessage = messageFactory.createMessage();
			SOAPPart soapPart = soapMessage.getSOAPPart();

			SOAPEnvelope envelope = soapPart.getEnvelope();
			envelope.addNamespaceDeclaration("wsof", serverURI);

			SOAPBody soapBody = envelope.getBody();
			SOAPElement soapBodyElem = soapBody.addChildElement("ofertaVidamaxUnificadaRequest", "wsof");
			SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("peticionJSON", "wsof");
			String objeto = convertObjectToJson(obj); 

			LOGGER.info("Cuerpojson::: " + objeto);

			soapBodyElem1.addTextNode(objeto);

			MimeHeaders headers = soapMessage.getMimeHeaders();
			headers.addHeader("SOAPAction", String.valueOf(serverURI) + "ofertaVidamaxUnificadaRequest");

			soapMessage.saveChanges();
			if (soapMessage != null)

			{
				LOGGER.info("Cuerpo XML de la peticion SOAP construido exitosamente");
			} else {
				LOGGER.info("No se construyo el cuerpo XML de la peticion SOAP");
			}

		} catch (SOAPException e) {
			LOGGER.error("Error al generar el cuerpo XML de la peticion SOAP");
		}
		return soapMessage;
	}
	
	 @SuppressWarnings({ "unchecked", "rawtypes" })
		public Object convertJsonToObject(String json, Class clazz) {
			Object objeto = null;
			Gson gson = new GsonBuilder().serializeNulls().create();
			try {
				objeto = gson.fromJson(json, clazz);
			} catch (Exception e) {
				LOGGER.info("Incidencia en metodo convertJsonToObject", e);
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
				LOGGER.info("Incidencia en metodo convertObjectToJson ", e);
				json = null;
			}
			return json;
		}
}