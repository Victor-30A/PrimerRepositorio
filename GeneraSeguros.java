package com.bancoazteca.bdm.cotizador.BDMCotizacion.business;

import java.io.IOException;
import java.nio.charset.Charset;
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
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SeguroVidamax;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SobrePrecio;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class GeneraSeguros {
	private static String listafinal;
	  private static String listabenef;
	  private static String listaofer;
	  private static String result;
	  private static final Logger LOGGER = LoggerFactory.getLogger(GeneraCuerpoXml.class);

	  
	  public ResponseEntity<String> ObtenerOfertasSeguros(SeguroVidamax obj,String request) {
	   
	    ResponseEntity<String> response = null;
	    ObjectMapper mapper = new ObjectMapper();
	    
	  
	    LOGGER.info("Inicia proceso consulta de seguros");
	    try {
	      
	    	obj = (SeguroVidamax)mapper.readValue(request, SeguroVidamax.class);
	        LOGGER.info("consumir servicio de seguros");
	      
	        //llamadaJson(obj);
	        generaXml(obj);
	        response = new ResponseEntity<String>(result, HttpStatus.OK);
	        LOGGER.info("Extraccion exitosa, resultado: " + response);
	        
	        LOGGER.info("Se consumira servicio de preAutorizador  ");
	        preautorizarGenerico("bb165c937c3d4a5da262cffeb5f09b4f", 10100.0, 1, 1);
	        
	    }
	    catch (IOException e) {
	      LOGGER.error("Error al obtener la oferta de seguros " + e);
	    } catch (JSONException e) {
	    	
	      LOGGER.error("Error JSON");
		}     
	  
	    
	    return response;
	  }
	  
	  public ResponseEntity<String> ObtenerOfertasSeguras(SobrePrecio obj,String request) {
		   
		    ResponseEntity<String> response = null;
		    ObjectMapper mapper = new ObjectMapper();
		    
		    String oferta = "";
		    LOGGER.info("Inicia proceso consulta de sobreprecio");
		    try {
		      
		    	obj = (SobrePrecio)mapper.readValue(request, SobrePrecio.class);
		        LOGGER.info("consumir servicio de seguros");
		        llamdaSobrePrecio(obj);
		        
		        response = new ResponseEntity<String>(oferta, HttpStatus.OK);
		        LOGGER.info("Extraccion del sobreprecio exitosa, resultado: " + response);
		    }
		    catch (IOException e) {
		      LOGGER.error("Error al obtener el sobreprecio " + e);
		    } catch (JSONException e) {
		    	
		      LOGGER.error("Error JSON");
			}     
		  
		    
		    return response;
		  }
	  
	  

	  
	 /* public String llamadaJson(SeguroVidamax obj) {
	      String json=null;
	      try {
	        String cuerpojson = "{\"tipoOferta\":" + obj.getTipoOferta() + ",\"origen\":" + obj.getOrigen() + 
	          ";\"cliente\":{" + "\"pais\":" + obj.getCliente().getPais() + ",\"canal\":" + obj.getCliente().getCanal() + ",\"sucursal\":" + 
	          obj.getCliente().getSucursal() + ",\"folio\":" + obj.getCliente().getFolio() + ",\"capacidadPagoDisponible\":" + 
	          obj.getCliente().getCapacidadPagoDisponible() + "}," + "\"productoCredito\":{" + "\"montoVenta\":" + 
	          obj.getProductoCredito().getMontoVenta() + ",\"productoId\":" + obj.getProductoCredito().getProductoId() + ",\"periodo\":" + 
	          obj.getProductoCredito().getPeriodo() + ",\"plazo\":" + obj.getProductoCredito().getPlazo() + "}," + "\"seguro\":{" + "\"esPromocion\":" + 
	          obj.getSeguro().isEsPromocion() + ",\"iva\":" + obj.getSeguro().getIva() + "}," + "\"informacionBase\":{" + "\"ws\":\"" + 
	          obj.getInformacionBase().getWs() + "\"" + ",\"usuario\":\"" + obj.getInformacionBase().getUsuario() + "\"" + ",\"sucursal\":" + 
	          obj.getInformacionBase().getSucursal() + "}" + "}";
	        
	        LOGGER.info("Cuerpojson::: "+cuerpojson);
	        
	      //final String uri = "http://10.51.25.211:8080/vidamaxPrestamoPersonal/ofertaNormal";
	      
	       final String uri = "http://10.81.27.220/SegurosServicios/VidamaxUnificada";
	        RestTemplate restTemplate = new RestTemplate();
	        result = restTemplate.postForObject(uri, obj, String.class);
	        LOGGER.info("RESULTADO:::: "+result);
	        
	      
	      } catch (Exception e) {
	    	  LOGGER.error("Error al generar el cuerpo XML de la peticion SOAP " + e);
	    	  e.printStackTrace();
	      } 
	      return json;
	    }*/
	  
	  
	  public void preautorizarGenerico(String icu, double monto, int idFamilia, int plazo) 
		{
		
			String respuesta = preautorizadorRec(icu, monto, idFamilia, 0, 0,plazo);
			

			if (respuesta.isEmpty()){
				LOGGER.info("la respuesta del preautorizador es nula o vacia");
				
			}		
			
			JSONObject obj = new JSONObject(respuesta);
			
			if (obj.isNull("codigo") || obj.isNull("status")){
				LOGGER.info("la respuesta del servicio preautorizador viene con los valores nulos");
				
			}
			
			LOGGER.info("puede continuar con el flujo");
			
		}

	  
	  public String preautorizadorRec(String icu, double monto, int idFamilia, int canalVendedor,int sucursalVendedora,int plazo) {
			LOGGER.info("Consultando el metodo [ RecompraDaoImpl:preautorizadorRec ]" );
			String resp = "";
			try {		
				LOGGER.info("{}    icu:{}, monto:{}, idFamilia:{}, canalVendedor:{}, sucursalVendedora:{}, plazo:{}",icu,monto,idFamilia,canalVendedor,sucursalVendedora,plazo);
						

				String urlWS = "http://10.51.82.220:8081/OriginacionCentralizada/originacion/BazDigital/preautorizadorBazDigital";
				LOGGER.info(urlWS);

				MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
				map.add("icu",icu);
				map.add("monto", String.valueOf(monto));	
				map.add("idFamilia", String.valueOf(idFamilia));
				map.add("canalVendedor", String.valueOf(canalVendedor));
				map.add("sucursalVendedora", String.valueOf(sucursalVendedora));
				map.add("plazo", String.valueOf(plazo));

				resp = invocaServicioPost(urlWS, map);
				
				
			}catch(RestClientException e){		
				LOGGER.info("Incidencia dao preautorizadorRec");
				return resp;
			} 
			return resp;
		}
	  public String invocaServicioPost(String urlWs, MultiValueMap<String, String> map) {
	  	
			//long tiempoInicio = obtenerTiempoInMillis();
			String response;
			RestTemplate rest = new RestTemplate();
			rest.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			SimpleClientHttpRequestFactory requestFactory = (SimpleClientHttpRequestFactory) rest.getRequestFactory();
			rest.setRequestFactory(requestFactory);
			response = rest.postForObject(urlWs, map, String.class);

			return response;
		}
	  

	  
	  public String llamdaSobrePrecio(SobrePrecio spc) {
		  String json = null;
		  try {
			  String cuerpojson ="{\"identificador\":"+spc.getIdentificador()+",\"idProducto\":"+spc.getIdProducto()+",\"identificador\":"+spc.getPlazo()+"}";
			  LOGGER.info("Cuerpo del Sobreprecio " + cuerpojson);
			  final String uris ="http://10.51.25.211:8080/vidamaxPrestamoPersonal/precioSobreprecio";
			  RestTemplate restTemplatex = new RestTemplate();
			  String resultadoXX = restTemplatex.postForObject(uris, spc, String.class);
			  LOGGER.info("RESULTADOXXX:::: "+resultadoXX);
			  
			  
		  }catch(Exception er) {
			  LOGGER.error("Error al traer los datos del sobre precio");
		  }
		  
		  return json;
	  }
	  
	  public SOAPMessage TransmiteXml(SeguroVidamax obj) {
	    SOAPMessage soapResponse = null;
	    LOGGER.info("Proceso para consumir servicio SOAP de seguros");
	    try {
	      SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
	      SOAPConnection soapConnection = soapConnectionFactory.createConnection();

	      
	     String url = "http://10.51.25.211:8080/vidamaxPrestamoPersonal/ofertaNormal?wsdl";
	      //String url ="http://10.81.27.220/SegurosServicios/VidamaxUnificada?wsdl";
	      soapResponse = soapConnection.call(generaXml(obj), url);
	      if (soapResponse != null)
	      
	      { LOGGER.info("Respuesta del servicio SOAP Valida"); }
	      else { LOGGER.info("Respuesta del servicio SOAP Invalida"); }
	      
	      soapConnection.close();
	    }
	    catch (SOAPException e) {
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
	      
	      
	      String cuerpojson = "";
	      
	      LOGGER.info("Cuerpojson::: "+cuerpojson);
	      
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
	    
	    LOGGER.error("Cadena JSON:::"+cadenajson);
	    return cadenajson;
	  }

	  
	  public String ExtraeLista(String json) {
	    String listaofertas = null;
	    String listabeneficios = null;
	    String lista = null;
	    LOGGER.info("Extrayendo del JSON lista de ofertas y beneficios de seguros");
	    try {
	      JSONObject jsonObject = new JSONObject(json);
	      
	      if(jsonObject.has("seguros")) {
	    	  
	      listaofertas = jsonObject.getJSONObject("seguros").getJSONArray("lista").toString();
	      listabeneficios = jsonObject.getJSONObject("seguros").getJSONArray("listaBeneficios").toString();
	      
	      LOGGER.info(listaofertas);
	      LOGGER.info(listabeneficios);
	      
	      lista = String.valueOf(listaofertas) + "," + listabeneficios;
	      listafinal = lista;
	      listabenef = listabeneficios;
	      listaofer = listaofertas; 
	      
	      if (listafinal != null)
	      
	      { LOGGER.info("oferta de seguros y beneficios extraida con exito"); }
	      else { LOGGER.info("oferta de seguros y beneficios extraida sin exito"); }
	      
	      } else {
	    	  
	    	  listafinal = jsonObject.getJSONObject("respuestaBase").toString();
	    
	      }
	    
	    } catch (JSONException e) {
	    	e.printStackTrace();
	      LOGGER.error("Error al momento de extraer JSON de la respuesta XML: ");
	    } 
	    return lista;
	  }

}
