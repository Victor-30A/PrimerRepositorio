package com.bancoazteca.bdm.cotizador.BDMCotizacion.business;

import java.io.IOException;
import java.util.Iterator;

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
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.DatosEntrada;
import com.fasterxml.jackson.databind.ObjectMapper;


@Component
public class GeneraSeguritos {
	
	private static String listafinal;
	  private static final Logger LOGGER = LoggerFactory.getLogger(GeneraCuerpoXml.class);

	  
	  public ResponseEntity<String> ObtenerOfertasSeguros(String request) {
	    ResponseEntity<String> response = null;
	    String oferta = "";
	    LOGGER.info("Inicia proceso consulta de seguros");
	    try {
	      ObjectMapper mapper = new ObjectMapper();
	      DatosEntrada obj = (DatosEntrada)mapper.readValue(request, DatosEntrada.class);
	      LOGGER.info("consumir servicio de seguros");
	      TransmiteXml(obj);
	      oferta = listafinal.replace("[", "");
	      oferta = oferta.replace("]", "");
	      oferta = "[" + oferta + "]";
	      response = new ResponseEntity<String>(oferta, HttpStatus.OK);
	      LOGGER.info("Extraccion exitosa, resultado: " + response);
	    }
	    catch (IOException e) {
	      LOGGER.error("Error al obtener la oferta de seguros");
	    } 
	    return response;
	  }

	  
	  public SOAPMessage TransmiteXml(DatosEntrada obj) {
	    SOAPMessage soapResponse = null;
	    LOGGER.info("Proceso para consumir servicio SOAP de seguros");
	    try {
	      SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
	      SOAPConnection soapConnection = soapConnectionFactory.createConnection();

	      
	      String url = "http://10.81.27.220/SegurosServicios/VidamaxUnificada?wsdl";
	      soapResponse = soapConnection.call(generaXml(obj), url);
	      if (soapResponse != null)
	      
	      { LOGGER.info("Respuesta del servicio SOAP Valida"); }
	      else { LOGGER.info("Respuesta del servicio SOAP Invalida"); }
	      
	      obtenJson(soapResponse);
	      soapConnection.close();
	    }
	    catch (SOAPException e) {
	      LOGGER.error("Error en el metodo TransmiteXml de la clase GeneraCuerpoXml:");
	    } 
	    return soapResponse;
	  }


	  
	  public SOAPMessage generaXml(DatosEntrada obj) {
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
	      
	      String cuerpojson = "{\"tipoOferta\":" + obj.getTipoOferta() + ",\"origen\":" + obj.getOrigen() + 
	        ";\"cliente\":{" + "\"pais\":" + obj.getPais() + ",\"canal\":" + obj.getCanal() + ",\"sucursal\":" + 
	        obj.getSucursal() + ",\"folio\":" + obj.getFolio() + ",\"capacidadPagoDisponible\":" + 
	        obj.getCapacidadPagoDisponible() + "}," + "\"productoCredito\":{" + "\"montoVenta\":" + 
	        obj.getMontoVenta() + ",\"productoId\":" + obj.getProductoId() + ",\"periodo\":" + 
	        obj.getPeriodo() + ",\"plazo\":" + obj.getPlazo() + "}," + "\"seguro\":{" + "\"esPromocion\":" + 
	        obj.isEsPromocion() + ",\"iva\":" + obj.getIva() + "}," + "\"informacionBase\":{" + "\"ws\":\"" + 
	        obj.getWs() + "\"" + ",\"usuario\":\"" + obj.getUsuario() + "\"" + ",\"sucursal\":" + 
	        obj.getSucursal_two() + "}" + "}";
	      
	      soapBodyElem1.addTextNode(cuerpojson);
	      
	      MimeHeaders headers = soapMessage.getMimeHeaders();
	      headers.addHeader("SOAPAction", String.valueOf(serverURI) + "ofertaVidamaxUnificadaRequest");
	      
	      soapMessage.saveChanges();
	      if (soapMessage != null)
	      
	      { LOGGER.info("Cuerpo XML de la peticion SOAP construido exitosamente"); }
	      else { LOGGER.info("No se construyo el cuerpo XML de la peticion SOAP"); }
	    
	    } catch (SOAPException e) {
	      LOGGER.error("Error al generar el cuerpo XML de la peticion SOAP");
	    } 
	    return soapMessage;
	  }

	  
	  public String obtenJson(SOAPMessage soapMessage) {
	    SOAPPart soapPart = soapMessage.getSOAPPart();
	    String cadenajson = null;
	    LOGGER.info("Obtener JSON de la respuesta XML del servicio SOAP");
	    
	    try {
	      SOAPEnvelope envelope = soapPart.getEnvelope();
	      SOAPBody soapBody = envelope.getBody();

	      
	      Iterator<Node> itr = soapBody.getChildElements();
	      while (itr.hasNext()) {
	        Node node = (Node)itr.next();
	        if (node.getNodeType() == 1) {
	          
	          Element ele = (Element)node;
	          
	          NodeList statusNodeList = ele.getChildNodes();
	          for (int i = 0; i < statusNodeList.getLength(); i++) {
	            Element emailResult = (Element)statusNodeList.item(i);
	            String str;
	            switch ((str = emailResult.getNodeName()).hashCode()) { case 1346564264: if (!str.equals("respuestaJSON"))
	                  break; 
	                cadenajson = emailResult.getTextContent();
	                if (cadenajson != null)
	                
	                { LOGGER.info("Cuerpo del JSON contenido en la respuesta XML extraido con exito"); }
	                else { LOGGER.info("Cuerpo del JSON contenido en la respuesta XML extraido sin exito"); }
	                 ExtraeLista(cadenajson);
	                break; }
	          
	          } 
	          continue;
	        } 
	        node.getNodeType();
	      }
	    
	    }
	    catch (SOAPException e) {
	      LOGGER.error("XML de respuesta invalido");
	    } 
	    return cadenajson;
	  }

	  
	  public String ExtraeLista(String json) {
	    String listaofertas = null;
	    String listabeneficios = null;
	    String lista = null;
	    LOGGER.info("Extrayendo del JSON lista de ofertas y beneficios de seguros");
	    try {
	      JSONObject jsonObject = new JSONObject(json);
	      listaofertas = jsonObject.getJSONObject("seguros").getJSONArray("lista").toString();
	      listabeneficios = jsonObject.getJSONObject("seguros").getJSONArray("listaBeneficios").toString();
	      
	      lista = String.valueOf(listaofertas) + "," + listabeneficios;
	      listafinal = lista;
	      
	      if (listafinal != null)
	      
	      { LOGGER.info("oferta de seguros y beneficios extraida con exito"); }
	      else { LOGGER.info("oferta de seguros y beneficios extraida sin exito"); }
	    
	    } catch (JSONException e) {
	      LOGGER.error("Error al momento de extraer JSON de la respuesta XML: ");
	    } 
	    return lista;
	  }
	
	

}
