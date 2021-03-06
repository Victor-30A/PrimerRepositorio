package com.bancoazteca.bdm.cotizador.BDMCotizacion.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
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

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
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
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SeguroDeVida;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SeguroDeVidaRenovacion;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SeguroVidamax;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

@Service
public class GeneraCreditRunRenovacion {
	
	private static Logger logger = LoggerFactory.getLogger(GeneraCreditRunRenovacion.class);
	
	private int CPD = 220;
	private static  String CADENA = null;

	private static final int SEGURO = 30;
	private static final int MONTO_VENTA = 0;
	private static final int PRODUCTO_ID = 0;
	private static final int PERIODO = 1;
	private static final int PLAZO = 0;
	private static final int TIPO_OFERTA = 2;
	private static final int ORIGEN = 10;
	
	
	private static final String CODIGO_RESPUESTA = "codigo";
	private static final String STATUS = "status";
	private static final String CODIGO = "codigo";
	private static final String EXITO = "EXITO";
	private static final String ERROR = "error";
	private static final int CODIGO_EXITO = 2;
	public static final String DATA_NOTNULL_JSON = "data";
	
	private static final String VIDAMAXUNIFICADA = "http://10.81.27.220/SegurosServicios/VidamaxUnificada";
	public static final String WSOFERTAVIDAMAXUNIFICADA = "http://www.segurosazteca.com.mx/WSOfertaVidamaxUnificada";
	
	
    private List<SeguroDeVida> listaOfertas = new ArrayList<>();
	//private SeguroDeVidaRenovacion listaOfertas = new SeguroDeVidaRenovacion();
	public ResponseEntity<Object> cotizador (CotizadorRequest request, String requestCifrado){
		logger.info("Ya entro a la renovacion");
		CotizadorResponse responseTO = new CotizadorResponse();
		
		try {
					
			JSONObject obj = new JSONObject(requestCifrado);
			
			SeguroVidamax  svmx =  new SeguroVidamax (); 
			svmx.setTipoOferta(2); //Se obtiene tipo de oferta de seguros
         
			svmx.setOrigen(3);
         
			svmx.setCliente(new Cliente(99,7236,7710,1,1,545));
			
			svmx.setProductoCredito(new ProductoCredito(1,120,7236,944826,1));  
				 
         
			svmx.setSeguro(new Seguro(0.16,false,0));
      
			svmx.setInformacionBase(new InformacionBase(7236,"926811","WS_SFIN04"));
			
			
			obtenerOfertasSegurosRenovacion(svmx);
			obtenerListaCotizador(responseTO, request);
			
			
		}catch(Exception er) {
			logger.error("El error es " + er);
			
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
	
	
	
	public void obtenerOfertasSegurosRenovacion(SeguroVidamax obj) {
		try {
			extraeListaRenovacion(obtenJSON(transmiteXML(obj)));
		}catch(Exception er) {
			logger.error("Error al generar el XML::: ");
			er.printStackTrace();
			
		}
	}
	
	
	
	public SOAPMessage transmiteXML(SeguroVidamax obj) {
		SOAPMessage soapResponse = null;
		try {
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			soapResponse = soapConnection.call(generaXML(obj),"http://10.81.27.220/SegurosServicios/VidamaxUnificada" );
			if (soapResponse != null){
				logger.info("Respuesta del servicio SOAP Valida");
			} else {
				logger.info("Respuesta del servicio SOAP Invalida");
			}

			soapConnection.close();
		} catch (SOAPException e) {
			e.printStackTrace();
			logger.error("Error en el metodo TransmiteXml de la clase GeneraCuerpoXml: " + e);
		}
		return soapResponse;
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
							if(cadenaJSON != null) {
								logger.info("Cadena JSON::: " + cadenaJSON);
							}
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
	
	
	public void extraeListaRenovacion(String json) {
		SeguroDeVida seguroDeVidaRenovacion = null;
		if(json != null) {
			try {
				JSONObject jsonObject = new JSONObject(json);
				logger.info("el jsonObject es :::::" + jsonObject);
				if(jsonObject.has("seguro")) {
					if(!jsonObject.isNull("seguro")) {
						if(jsonObject.getJSONObject("seguro").has("informacion")) {
							seguroDeVidaRenovacion = new SeguroDeVida();
							seguroDeVidaRenovacion = new Gson().fromJson(jsonObject.getJSONObject("seguro").getJSONObject("informacion").toString(), new TypeToken<SeguroDeVida>() {}.getType());
							
							if(seguroDeVidaRenovacion.getPrecio()>0) {
								seguroDeVidaRenovacion.setNombre("SEGURO VIDAMAX SD $"+seguroDeVidaRenovacion.getPrecio());
								seguroDeVidaRenovacion.setPrecio(seguroDeVidaRenovacion.getPrecio());
						    }
					    }
					}
				}
				else {
					logger.info("EL CLIENTE NO TRAE UN SEGURO PARA RENOVAR");
				}
				if(listaOfertas.isEmpty()==false) {
					listaOfertas.clear();
					}
				
				if(seguroDeVidaRenovacion != null) {
					listaOfertas.add(seguroDeVidaRenovacion);
				}
				
			}catch(Exception e) {
				logger.error("Error al momento de extraer el JSON de la respuesta XML");
				e.printStackTrace();
				
			}
		}
	}
	
	
	
	private void obtenerListaCotizador(CotizadorResponse responseTO, CotizadorRequest request) {
		String respuesta = getCotizadorDao();

		JSONObject obj = new JSONObject(respuesta);
		Gson gson = new Gson();

		boolean aptoSeguro = false;
		JSONArray jsonArray = null;
		if (obj.get("data") instanceof JSONArray) {
			logger.info("Es un array");
			jsonArray = obj.getJSONArray("data");
		} else {
			logger.info("Es objeto");
			JSONObject obj2 = new JSONObject(obj.get("data").toString());
			if(obj2.getJSONArray("listTermometro").isEmpty()) {}
			else {
				jsonArray = obj2.getJSONArray("listTermometro");
			}
			
			//jsonArray = obj2.getJSONArray("listTermometro");// si lo parsea es un objeto

			//aptoSeguro = obj2.getBoolean("aptoSeguro");
			aptoSeguro = true;
		}

		String json = jsonArray.toString();

		Type type = new TypeToken<List<DatosCotizacion>>() {}.getType();
		List<DatosCotizacion> listaCotizacion = gson.fromJson(json, type);
		
		llenaSegurosRenovacion(listaCotizacion,true);
		
		
		responseTO.setAptoSeguro(aptoSeguro);
		responseTO.setListaCotizador(listaCotizacion);

	}
	
	
	public void llenaSegurosRenovacion (List<DatosCotizacion> listaCotizacion , boolean valorCPD) {
		logger.info("si llega a llenaSegurosRenovacion  ");
		for(DatosCotizacion dato : listaCotizacion) {
			dato.setListaOfertas(
					cargaListaSeguros(dato.getPagoPuntual(),dato.getPagoNormal()));
	        if(dato.isAplicaSeguro()==false && (dato.getListaOfertas()==null || dato.getListaOfertas().size()==0)) {
	    		dato.setAplicaSeguro(false);	
	    	}
	        else {
	        	dato.setAplicaSeguro(true);
	        }
	    }
	}
	
	
	
	private String getCotizadorDao() {
		String resp = "";
		try {
			resp = obtenerCotizador();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return resp;
	}



	public String obtenerCotizador()
	{ 
		logger.info("Consultando el metodo [ CotizadorDaoImpl::obtenerCotizador ]" );
		String response = ""; 
	    try
	    {
		    
			//String urlWS = PropertiesManager.getinstance().getProperty(ConstantesCredito.CREDITO_PROPERTIES, URL_SERVICIO_COTIZADOR);
			//logger.info(urlWS);

			MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
			map.add("idProducto", String.valueOf(24));
			map.add("intervalo", "");
			map.add("bdmID", "b876b50a78d44b69ac06cf344978008a");
			map.add("monto", String.valueOf(20));
			map.add("idSolicitud", "0.0");
			map.add("montoSeguro", String.valueOf("20"));
			map.add("fechaNacimiento", "08/10/1988");
			map.add("clienteUnico", "1-1-2244-48778");
			map.add("carrito", "");

			//response = util.invocaServicioPostCotizador(urlWS, map, conTimeOut, redTimeOut);
			try {
				response = read("E:/Users/hamaro/listaCredito.json");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			    
	    }catch(RestClientException e)
	    {		
			logger.info("Incidencia dao obtenerCotizador");
			return response;
		
	    } catch (RuntimeException e) 
	    {
			logger.info("Incidencia dao obtenerCotizador: {}",e.getMessage());
	    	//throw new BDMCreditoException(e);
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

	public List<SeguroDeVida> cargaListaSeguros(int pagoPuntual ,int pagoNormal) {
		List<SeguroDeVida> lista = new ArrayList<>();
		
		for (SeguroDeVida seguro : listaOfertas) {
			if (seguro.getPrecio()> 0) {
			
				SeguroDeVida seguroDeVidaRenovacion = new SeguroDeVida();
			    int precioSeguro = seguro.getPrecio();
			   
			    seguroDeVidaRenovacion.setPrecioCalculado(seguro.getPrecioCalculado());
			    seguroDeVidaRenovacion.setSobrePrecio(seguro.getSobrePrecio());
			    seguroDeVidaRenovacion.setNombre(seguro.getNombre());
			    seguroDeVidaRenovacion.setPrecio(precioSeguro);
			    seguroDeVidaRenovacion.setProductoId(seguro.getProductoId());
			    seguroDeVidaRenovacion.setMontoFinal(seguro.getMontoFinal());
			    seguroDeVidaRenovacion.setPagoPuntualSeguro(precioSeguro + pagoPuntual);
			    seguroDeVidaRenovacion.setPagoNormalSeguro(precioSeguro + pagoNormal);
			    seguroDeVidaRenovacion.setBeneficio("");
			   
				lista.add(seguroDeVidaRenovacion);
			}
			else {
				logger.info("El precio del seguro ahora  es " + seguro.getPrecio());
			}
			
		}
		return lista;
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
	public static String read(String file) throws IOException {
		StringBuilder content = new StringBuilder();
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(file), 
				Charset.defaultCharset())) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				content.append(line).append("\n");
			}
			return content.toString();
		}
	}
}