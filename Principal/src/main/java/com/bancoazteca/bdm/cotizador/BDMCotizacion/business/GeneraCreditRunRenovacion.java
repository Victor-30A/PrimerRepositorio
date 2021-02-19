package com.bancoazteca.bdm.cotizador.BDMCotizacion.business;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
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
	
	private static final String VIDAMAXUNIFICADA = "http://10.81.27.220/SegurosServicios/VidamaxUnificada";
	public static final String WSOFERTAVIDAMAXUNIFICADA = "http://www.segurosazteca.com.mx/WSOfertaVidamaxUnificada";
	
	
    private List<SeguroDeVidaRenovacion> listaOfertas = new ArrayList<>();
	//private SeguroDeVidaRenovacion listaOfertas = new SeguroDeVidaRenovacion();
	public ResponseEntity<Object> cotizador (CotizadorRequest request, String requestCifrado){
		logger.info("Ya entro a la renovacion");
		CotizadorResponse responseTO = new CotizadorResponse();
		
		try {
			JSONObject obj = new JSONObject(requestCifrado);
			
			SeguroVidamax svmx = new SeguroVidamax();
			Cliente cliente = new Cliente();
			ProductoCredito productoCredito = new ProductoCredito();
			Seguro seguro = new Seguro();
			InformacionBase informacionBase = new InformacionBase();
			
			
			
			svmx.setTipoOferta(2);
			svmx.setOrigen(3);
			
			
			cliente.setPais(1);
			
			cliente.setCanal(1);
			
			cliente.setSucursal(7236);
			
			cliente.setFolio(7710);
			
			cliente.setCapacidadPagoDisponible(99);
		
			cliente.setCapacidadPagoForzada(545);
			svmx.setCliente(cliente);
			
			
			productoCredito.setPlazo(128);
			productoCredito.setPedidoARenovar(868153);
			productoCredito.setPaisPedido(1);
			productoCredito.setCanalPedido(1);
			productoCredito.setSucursalPedido(987);
			svmx.setProductoCredito(productoCredito);
			
			seguro.setEsPromocion(true);
			seguro.setProductoIDCotizador("529232");
			seguro.setIva(0.16);
			svmx.setSeguro(seguro);
			
			
			informacionBase.setWs("WS_SFIN07");
			informacionBase.setUsuario("954638");
			informacionBase.setSucursal(987);
			svmx.setInformacionBase(informacionBase);
			
			obtenerOfertasSegurosRenovacion(svmx);
			obtenerListaCotizador(responseTO, request);
			
			
		}catch(Exception er) {
			
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
	
	
	public void extraeLista(String json) {
		
		SeguroDeVidaRenovacion seguroDeVidaRenovacion = null;
		if(json != null) {
			try {
				JSONObject jsonObject = new JSONObject(json);
				if(jsonObject.has("seguro")) {
					if(!jsonObject.isNull("seguro")) {
						if(jsonObject.getJSONObject("seguro").has("Informacion")) {
							
							seguroDeVidaRenovacion = new SeguroDeVidaRenovacion();
							seguroDeVidaRenovacion = new Gson().fromJson(jsonObject.getJSONObject("seguro").getJSONObject("Informacion").toString(), new TypeToken<SeguroDeVidaRenovacion>() {}.getType());
							
							if(seguroDeVidaRenovacion.getPrecio()>=20) {
								seguroDeVidaRenovacion.setNombre("SEGURO VIDAMAX SD $20");
								seguroDeVidaRenovacion.setPrecio(20);
						    }
							
					    } else {
					    	seguroDeVidaRenovacion.setPrecioCalculado(0);
					    	seguroDeVidaRenovacion.setSobrePrecio(0);
					    	seguroDeVidaRenovacion.setNombre("");
					    	seguroDeVidaRenovacion.setPrecio(0);
					    	seguroDeVidaRenovacion.setProductoId(0);
					    	seguroDeVidaRenovacion.setMontoFinal("");
					    }
					}
				}
				
				if(listaOfertas.isEmpty()==false) {listaOfertas.clear();}
				
				if(seguroDeVidaRenovacion != null) {listaOfertas.add(seguroDeVidaRenovacion);}
				
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
		if (obj.get("abonos") instanceof JSONArray) {
			logger.info("Entra a Es un array");
			jsonArray = obj.getJSONArray("abonos");
			aptoSeguro = true;
		} 
		else {
			logger.info("Entra a Es objeto");
			jsonArray = obj.getJSONArray("abonos");// si lo parsea es un objeto
			aptoSeguro = true;
		}

		String json = jsonArray.toString();

		Type type = new TypeToken<List<DatosCotizacion>>() {
		}.getType();
		List<DatosCotizacion> listaCotizacion = gson.fromJson(json, type);
		
		

		for(DatosCotizacion dato : listaCotizacion) {
	    	dato.setListasOfertaRenovacion(cargaListaSeguros(dato.getPagoNormal(), dato.getPagoPuntual() , dato.getPagoNormalPromo(), dato.getPagoPuntualPromo()));
	        if(dato.isAplicaSeguro()==false && (dato.getListasOfertaRenovacion()==null || dato.getListasOfertaRenovacion().size()==0)) {
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
			String urlWS = "http://10.95.69.171:8085/creditrun?canal=1&pais=1&periodo=1&min=2000&max=12000&tasa=J&sucursal=100&nivelCR=7&descuento=35.00&capacidadDePago=787&plazoIni=13&plazoFin=100";
			logger.info(urlWS);

			response = invocaServicioGet(urlWS);

		} catch (RestClientException e) {
			logger.info("Incidencia dao obtenerCotizador");
			return response;

		} catch (RuntimeException e) {
			logger.info("Incidencia dao obtenerCotizador: {}", e.getMessage());
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

	public List<SeguroDeVidaRenovacion> cargaListaSeguros(int pagoNormal, int pagoPuntual, int pagoNormalPromo,int pagoPuntualPromo) {
		List<SeguroDeVidaRenovacion> lista = new ArrayList<>();
		for (SeguroDeVidaRenovacion seguro : listaOfertas) {
			
			SeguroDeVidaRenovacion seguroDeVidaRenovacion = new SeguroDeVidaRenovacion();
			    int precio = seguro.getPrecio();
			    seguroDeVidaRenovacion.setPrecioCalculado(seguro.getPrecioCalculado());
			    seguroDeVidaRenovacion.setSobrePrecio(seguro.getSobrePrecio());
			    seguroDeVidaRenovacion.setNombre(seguro.getNombre());
			    seguroDeVidaRenovacion.setPrecio(precio);
			    seguroDeVidaRenovacion.setProductoId(seguro.getProductoId());
			    seguroDeVidaRenovacion.setMontoFinal(seguro.getMontoFinal());
			    seguroDeVidaRenovacion.setPagoNormalSeguro(precio + pagoNormal);
			    seguroDeVidaRenovacion.setPagoPuntualSeguro(precio + pagoPuntual);
			    seguroDeVidaRenovacion.setPagoPuntualSeguroCreditRun(precio + pagoPuntualPromo);
			    seguroDeVidaRenovacion.setPagoNormalSeguroCreditRun(precio + pagoNormalPromo);
                
				lista.add(seguroDeVidaRenovacion);	
			
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
}