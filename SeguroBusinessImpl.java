package com.bancoazteca.bdm.cotizador.BDMCotizacion.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.soap.SOAPMessage;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.Cliente;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.CodigoSeguro;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.InformacionBase;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.ProductoCredito;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.RespuestaBase;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.RespuestaSeguro;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.Seguro;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SeguroDeVida;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SeguroDeVidaRenovacion;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SeguroVidamax;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


@Component
public class SeguroBusinessImpl {
	
	private static final Logger logger = LoggerFactory.getLogger(SeguroBusinessImpl.class);
	
	//private SeguroDeVidaRenovacion ofertaRenovacion = new SeguroDeVidaRenovacion();
	private List<SeguroDeVida> listaOfertasRenovacion = new ArrayList<>();
	private List<SeguroDeVida> listaOfertas = new ArrayList<>();
	
	RespuestaSeguro respuestaSeguro = new RespuestaSeguro(); 
	RespuestaBase respuestaBase =  new RespuestaBase();
	
	
	 
	
	
	private static final int SEGURO = 30;
	private static final int MONTO_VENTA = 0;
	private static final int PRODUCTO_ID = 0;
	private static final int PERIODO = 1;
	private static final int PLAZO = 0;
	private static final int TIPO_OFERTA = 1;
	private static final int TIPO_OFERTA_RENOVACION = 2;
	private static final int ORIGEN = 10;
	private static final int CLIENTE_NUEVO = 0;
	private static final int CLIENTE_RECOMPRA = 1;
	private static final int CLIENTE_RENOVACION = 2;
	private static final float IVA = (float) 0.16;
	private static final int ID_COTIZADOR = 0;

	
	@Autowired private UtilSeguro util;
	
	public void llenaSegurosNuevo(int origen) {	
		
		logger.info("Empieza a llenar los seguros de cliente nuevo");
		
		//llenaClienteSeguros(origen);	
	}
	
	
	public void llenaSegurosRecompra(int origen,Cliente cliente) {
		llenaClienteSeguros(origen,cliente);	
	}
	
	public void llenaSegurosRenovacion(int origen,  int capacidadPagoForzada, int pedidoRenovar, int paisPedido, int canalPedido, int sucursalPedido,String proceso,Cliente cliente) {
	    logger.info("Los datos recibidos del cliente de renovacion  son  origen: " + origen + " capacidadPagoForzada: " + capacidadPagoForzada + " pedidoRenovar: " + " paisPedido:" + paisPedido + "canalPedido "+ canalPedido + "sucursalPedido " + sucursalPedido);
	    llenaClienteSegurosRenovacion(origen,  capacidadPagoForzada, pedidoRenovar, paisPedido, canalPedido, sucursalPedido,proceso,cliente);
	  }
	
	public void llenaSegurosRenovacionPersonalizada(int origen,  int capacidadPagoForzada, int pedidoRenovar, int paisPedido, int canalPedido, int sucursalPedido,String proceso, Cliente cliente) {
	   // logger.info("Los datos recibidos del cliente de renovacion personalizada  son  origen: " + origen + " capacidadPagoForzada: " + capacidadPagoForzada + " pedidoRenovar: " + " paisPedido:" + paisPedido + "canalPedido "+ canalPedido + "sucursalPedido " + sucursalPedido);
	    llenaClienteSegurosRenovacion(origen,  capacidadPagoForzada, pedidoRenovar, paisPedido, canalPedido, sucursalPedido,proceso ,cliente);
	  }
	
	
	
	
	public void llenaClienteSeguros(int origen,Cliente cliente) {
		SeguroVidamax svmx = new SeguroVidamax();
		/* Validaremos Origen del cliente */
		if (origen == CLIENTE_NUEVO) {
			svmx.setTipoOferta(TIPO_OFERTA);
			svmx.setOrigen(ORIGEN);
			svmx.setCliente(new Cliente(1,1, 2244, 25966, SEGURO,"1982-11-24",true));
			svmx.setProductoCredito(new ProductoCredito(MONTO_VENTA,PRODUCTO_ID,PERIODO,PLAZO));
			svmx.setSeguro(new Seguro(false,IVA));
			svmx.setInformacionBase(new InformacionBase("SYS-BAZDIGITAL","SYS-BAZDIGITAL",2244));

			obtenerOfertasSeguros(svmx , origen ,"");

		} 
		else if (origen == CLIENTE_RECOMPRA) {
			svmx.setTipoOferta(TIPO_OFERTA);
			svmx.setOrigen(ORIGEN);
			//svmx.setCliente(new Cliente(1,1, 8624, 72525, SEGURO,"1982-11-24",false));//30 PESOS EN SEGUROS
			//svmx.setCliente(new Cliente(1,1, 2244, 22794, SEGURO,"1982-11-24",false));//5PESOS EN SEGUROS
			//svmx.setCliente(new Cliente(1,1, 2244, 57349, SEGURO,"1982-11-24",false));//35 PESOS EN SEGUROS
			svmx.setCliente(new Cliente(cliente.getPais(),cliente.getCanal(),cliente.getSucursal(),cliente.getFolio(),30,cliente.getFechaNacimiento(),cliente.isClienteNuevo()));
			
			svmx.setProductoCredito(new ProductoCredito(MONTO_VENTA,PRODUCTO_ID,PERIODO,PLAZO));
			svmx.setSeguro(new Seguro(false,IVA));
			svmx.setInformacionBase(new InformacionBase("SYS-BAZDIGITAL","SYS-BAZDIGITAL",2244));

			obtenerOfertasSeguros(svmx , origen ,"");
		}	
	}
	
	public void llenaClienteSegurosRenovacion(int origen,  int capacidadPagoForzada, int pedidoRenovar, int paisPedido, int canalPedido, int sucursalPedido,String proceso,Cliente cliente) {
	    SeguroVidamax svmx = new SeguroVidamax();
	    if (origen == TIPO_OFERTA_RENOVACION) {
	    	svmx.setTipoOferta(TIPO_OFERTA_RENOVACION);
    		svmx.setOrigen(ORIGEN);
    		svmx.setCliente(new Cliente(capacidadPagoForzada, cliente.getSucursal(), cliente.getFolio(), cliente.getPais(), cliente.getCanal(), capacidadPagoForzada));
    		svmx.setProductoCredito(new ProductoCredito(canalPedido, 120, sucursalPedido, pedidoRenovar, paisPedido));
    		svmx.setSeguro(new Seguro(IVA, false, ID_COTIZADOR));
    		svmx.setInformacionBase(new InformacionBase(2244,ConstantesSeguro.IDENTIFICADOR_DATOS_BASICOS, ConstantesSeguro.IDENTIFICADOR_DATOS_BASICOS));
	        obtenerOfertasSeguros(svmx, origen,proceso);
	    } 
	  }


	public void obtenerOfertasSeguros(SeguroVidamax svmx ,int flujo, String proceso) {
		try {
			if(flujo == CLIENTE_NUEVO || flujo == CLIENTE_RECOMPRA) {
				logger.info("consumir servicio de seguros sea recompra o cliente nuevo");
				extraeLista(util.obtenJson(util.transmiteXml(svmx)));
			}
			
			else if(flujo == CLIENTE_RENOVACION && proceso =="RENUEVA") {
				logger.info("Consume servicio que renueva un seguro que ya trae un cliente");
				SOAPMessage soapMessage = util.transmiteXml(svmx);
			//	util.llenaCodigosSeguro(util.obtenJson(soapMessage));
				extraeSeguroRenovado(util.obtenJson(soapMessage));	
				extraeListaSegurosRenovado(util.obtenJson(soapMessage));
				
				
			}
			else if(flujo == CLIENTE_RENOVACION && proceso == "") {
				logger.info("Extrae la lista del seguro que solo se va a renovar con flujo de 1ra fase");
				extraeListaRenovacion(util.obtenJson(util.transmiteXml(svmx)));	
			}
		} catch (Exception e) {
			logger.info("Incidencia al momento de extraer JSON  de la respuesta de renovacion del  XML: {}", e.getMessage());
			
			
		}
	}
	
	
	
	/*Carga la lista de seguros para clientes nuevos y de recompra*/
	public List<SeguroDeVida> cargaListaSeguros(int val, int pagoPuntual, int pagoNormal) {
		List<SeguroDeVida> lista = new ArrayList<>();
		for (SeguroDeVida l : listaOfertas) {
			if (l.getPrecio() <= val) {
				lista.add(new SeguroDeVida(
						l.getPrecioCalculado(),
						l.getPrecio(),
						l.getSobrePrecio(),
						l.getMontoFinal(),
						l.getProductoId(),
						l.getNombre(),
						(l.getPrecio() + pagoPuntual),
						(l.getPrecio() + pagoNormal),
						(l.getMontoFinal() * 2), 
						"")
					);
			}
		}
		return lista;
	}
	
	
	/*Carga la lista de seguros de recompra con la primera version que se subio de credit run y no se afectara las versiones que tenian en el modulo de credito*/
	public List<SeguroDeVida> cargaListaSegurosCredit(int val, int pagoPuntual, int pagoNormal,int pagoPuntualPromo, int pagoNormalPromo) {
		List<SeguroDeVida> lista = new ArrayList<>();
		for (SeguroDeVida listaSeguro : listaOfertas) {
			if (listaSeguro.getPrecio() <= val) {
				lista.add(new SeguroDeVida(
						listaSeguro.getPrecioCalculado(),
						listaSeguro.getPrecio(),
						listaSeguro.getSobrePrecio(),
						listaSeguro.getMontoFinal(),
						listaSeguro.getProductoId(),
						listaSeguro.getNombre(),
						(listaSeguro.getPrecio() + pagoPuntual),
						(listaSeguro.getPrecio() + pagoNormal),
						(pagoPuntualPromo),
						(pagoNormalPromo),
						(listaSeguro.getMontoFinal() * 2)
						)
					);
			}
		}
		return lista;
	}
	
	
	public List<SeguroDeVida> cargaListaSegurosCreditoLimitado(int val, int pagoPuntual, int pagoNormal,int pagoPuntualPromo, int pagoNormalPromo) {
		
		int maximaOfertaDeSeguros = 0;
		ArrayList<Integer> listaPreciosSeguro = new ArrayList<Integer>();
		List<SeguroDeVida> lista = new ArrayList<>();

		for (SeguroDeVida listaSeguro : listaOfertas) {
			if(listaOfertas.isEmpty()) {}
			else {
				listaPreciosSeguro.add(listaSeguro.getPrecio());
				maximaOfertaDeSeguros = Collections.max(listaPreciosSeguro);
			}
		}
		
		//logger.info("El monto maximo encontrado en la lista de seguros es " + maximaOfertaDeSeguros);
		
		if(maximaOfertaDeSeguros >= 10) {
			//logger.info("ENTRA AL LAS OFERTAS MAYORES DE $5 Y NO LOS OFERTA PORQUE EL CLIENTE TIENE CAPACIDAD DE PAGO Y LA LISTA DE SEGUROS RETORNA QUE SE PUEDE LLEVAR ARRIBA DE $10 EN SEGUROS");
			for(SeguroDeVida listaSeguro :listaOfertas) {
				if(listaSeguro.getPrecio() <= val  && listaSeguro.getPrecio()>5  ) {
					lista.add(new SeguroDeVida(
							listaSeguro.getPrecioCalculado(),
							listaSeguro.getPrecio(),
							listaSeguro.getSobrePrecio(),
							listaSeguro.getMontoFinal(),
							listaSeguro.getProductoId(),
							listaSeguro.getNombre(),
							(listaSeguro.getPrecio() + pagoPuntual),
							(listaSeguro.getPrecio() + pagoNormal),
							(pagoPuntualPromo),
							(pagoNormalPromo),
							(listaSeguro.getMontoFinal() * 2)
						));
					
					}
			}
		}
		else if(maximaOfertaDeSeguros == 5) {
			//logger.info("ENTRA A LAS OFERTAS DE SOLO $5 PORQUE LA REGLA DE LOS $35 CONTRATADOS");
			for(SeguroDeVida listaSeguro :listaOfertas) {
				if(listaSeguro.getPrecio() <= val) {
					lista.add(new SeguroDeVida(
							listaSeguro.getPrecioCalculado(),
							listaSeguro.getPrecio(),
							listaSeguro.getSobrePrecio(),
							listaSeguro.getMontoFinal(),
							listaSeguro.getProductoId(),
							listaSeguro.getNombre(),
							(listaSeguro.getPrecio() + pagoPuntual),
							(listaSeguro.getPrecio() + pagoNormal),
							(pagoPuntualPromo),
							(pagoNormalPromo),
							(listaSeguro.getMontoFinal() * 2)
						));
					
					}
			}
		}
		return lista;
	}
	
	
	/*Carga la lista de seguros de $5*/
	public List<SeguroDeVida> cargaListaSegurosCreditoCincoPesos(int val, int pagoPuntual, int pagoNormal,int pagoPuntualPromo, int pagoNormalPromo) {
		List<SeguroDeVida> lista = new ArrayList<>();
		if(val == 9) {
			//logger.info("Entra con la capacidad limitada hasta 9 pesos");
			for (SeguroDeVida l : listaOfertas) {
				if (l.getPrecio() <= val && l.getPrecio() >= 5) {
					lista.add(new SeguroDeVida(
							l.getPrecioCalculado(),
							l.getPrecio(),
							l.getSobrePrecio(),
							l.getMontoFinal(),
							l.getProductoId(),
							l.getNombre(),
							(l.getPrecio() + pagoPuntual),
							(l.getPrecio() + pagoNormal),
							(l.getMontoFinal() * 2), 
							"")
						);
				}
			}
		}
		
		return lista;
	}
	
	/*Carga la lista de seguros de $5*/
	public List<SeguroDeVida> cargaListaSegurosCreditFiltroCincoPesos(int val, int pagoPuntual, int pagoNormal,int pagoPuntualPromo, int pagoNormalPromo) {
		List<SeguroDeVida> lista = new ArrayList<>();
		if(val == 9) {
			//logger.info("Entra con la capacidad limitada hasta 9 pesos");
			for (SeguroDeVida l : listaOfertas) {
				if (l.getPrecio() <= val && l.getPrecio() >= 5) {
					lista.add(new SeguroDeVida(
							l.getPrecioCalculado(),
							l.getPrecio(),
							l.getSobrePrecio(),
							l.getMontoFinal(),
							l.getProductoId(),
							l.getNombre(),
							(l.getPrecio() + pagoPuntual),
							(l.getPrecio() + pagoNormal),
							(l.getMontoFinal() * 2), 
							"")
						);
				}
			}
		}
		
		return lista;
	}
				
	
	

	
	
	
	
	/*Carga lista de renovacion con el flujo de seguros*/
	public List<SeguroDeVida> cargaListaSegurosRenovacion(int pagoPuntual ,int pagoNormal) {
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
			    seguroDeVidaRenovacion.setSumaMuerteAccidental(seguro.getMontoFinal() * 2);
			    seguroDeVidaRenovacion.setBeneficio("");
			   
				lista.add(seguroDeVidaRenovacion);
			}
			else {
				//logger.info("El precio del seguro ahora  es " + seguro.getPrecio());
			}
			
		}
		return lista;
	}
	
	/*Carga lista de renovacion_Personalizada con el flujo de seguros*/
	public List<SeguroDeVida> cargaListaSegurosRenovacionPersonalizada(int pagoPuntual ,int pagoNormal, int pagoPuntualPromo , int pagoNormalPromo) {
		List<SeguroDeVida> lista = new ArrayList<>();
		for (SeguroDeVida seguro : listaOfertasRenovacion) {
			if (seguro.getPrecio()> 0) {
				SeguroDeVida seguroDeVida = new SeguroDeVida();
			    int precioSeguro = seguro.getPrecio();
			    seguroDeVida.setPrecioCalculado(seguro.getPrecioCalculado());
			    seguroDeVida.setSobrePrecio(seguro.getSobrePrecio());
			    seguroDeVida.setNombre(seguro.getNombre());
			    seguroDeVida.setPrecio(precioSeguro);
			    seguroDeVida.setProductoId(seguro.getProductoId());
			    seguroDeVida.setMontoFinal(seguro.getMontoFinal());
			    seguroDeVida.setPagoPuntualSeguro(precioSeguro + pagoPuntual);
			    seguroDeVida.setPagoNormalSeguro(precioSeguro + pagoNormal);
			    seguroDeVida.setPagoPuntualSeguroCreditRun(precioSeguro + pagoPuntualPromo);
			    seguroDeVida.setPagoNormalSeguroCreditRun(precioSeguro + pagoNormalPromo);
			    seguroDeVida.setSumaMuerteAccidental(seguro.getMontoFinal() * 2);
			    seguroDeVida.setBeneficio("");
				lista.add(seguroDeVida);
				obtenSeguroARenovar();
				
			}
			else {
				//logger.info("El precio del seguro ahora  es " + seguro.getPrecio());
			}
			
		}
		return lista;
	}
	
	public int obtenSeguroARenovar() {
		for(SeguroDeVida seguro:listaOfertasRenovacion) {
			if(seguro.getPrecio()>0) {
				int precioSeguro = seguro.getPrecio();
				return precioSeguro;
			}
			else {
				return 0;
			}
		}
		return 0;
	}
	
	
	/*Carga lista de renovacion con el flujo de seguros*/
	public List<SeguroDeVida> cargaListaSegurosRenovacionOfertasEspeciales(int pagoPuntual,int pagoNormal,int pagoPuntualPromo, int pagoNormalPromo,int pagoPuntualPromoEspecial, int pagoNormalPromoEspecial ) {
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
			    seguroDeVidaRenovacion.setPagoPuntualSeguroCreditRun(precioSeguro + pagoPuntualPromo);
			    seguroDeVidaRenovacion.setPagoNormalSeguroCreditRun(precioSeguro + pagoNormalPromo);
			    seguroDeVidaRenovacion.setPagoPuntualPromoEspecial(precioSeguro + pagoPuntualPromoEspecial);
			    seguroDeVidaRenovacion.setPagoNormalPromoEspecial(precioSeguro + pagoNormalPromoEspecial);
			    seguroDeVidaRenovacion.setSumaMuerteAccidental(seguro.getMontoFinal() * 2);
			    seguroDeVidaRenovacion.setBeneficio("");
				lista.add(seguroDeVidaRenovacion);
				
			}
			else {
				logger.info("El precio del seguro ahora  es " + seguro.getPrecio());
			}
			
		}
		return lista;
	}
	
	
	/*Carga lista de recompra con nuevas promociones especiales */
	public List<SeguroDeVida> cargaListaSegurosPromoEspecial(int val, int pagoPuntual,int pagoNormal,int pagoPuntualPromo, int pagoNormalPromo,int pagoPuntualPromoEspecial, int pagoNormalPromoEspecial){
		List<SeguroDeVida> lista = new ArrayList<>();
		for (SeguroDeVida l : listaOfertas) {
			if (l.getPrecio() <= val) {
				lista.add(new SeguroDeVida(
						l.getPrecioCalculado(),
						l.getPrecio(),
						l.getSobrePrecio(),
						l.getMontoFinal(),
						l.getProductoId(),
						l.getNombre(),
						(l.getPrecio() + pagoPuntual),
						(l.getPrecio() + pagoNormal),
						(l.getPrecio() + pagoPuntualPromo),
						(l.getPrecio() + pagoNormalPromo),
						(l.getPrecio() + pagoPuntualPromoEspecial),
						(l.getPrecio() + pagoNormalPromoEspecial),
						(l.getMontoFinal() * 2))
					);
			}
		}
		
		return lista;
	}
	
	
	

	
	public void extraeLista(String json) {
		if (json != null) {
			try {
				JSONObject jsonObject = new JSONObject(json);
				if(!listaOfertas.isEmpty()) {listaOfertas.clear();}
				if (jsonObject.has("seguros")) {	
					listaOfertas = new Gson().fromJson(jsonObject.getJSONObject("seguros").getJSONArray("lista").toString(), new TypeToken<List<SeguroDeVida>>() {}.getType());
					//logger.info("Response JSON::: " + listaOfertas);
					if (listaOfertas.isEmpty()) {
						new Exception();
					} 
				} 
			} catch (Exception e) {
				logger.info("Se genero una incidencia al momento de extraer JSON de la respuesta XML:{}", e.getMessage());
				
			}
		}
	}
	
	public void extraeListaRenovacion(String json) {
		SeguroDeVida seguroDeVidaRenovacion = new SeguroDeVida();
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
								seguroDeVidaRenovacion.setNombre(seguroDeVidaRenovacion.getNombre());
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
				logger.info("Se genero una incidencia al momento de extraer JSON de renovacion de  la respuesta XML:{}", e.getMessage());
				
				
			}
		}
	}
	
	
	public void extraeSeguroRenovado(String json) {
		SeguroDeVida seguroDeVida = new SeguroDeVida();
		if(json != null) {
			try {
				JSONObject jsonObject = new JSONObject(json);
				logger.info("el jsonObject es :::::" + jsonObject);
				if(jsonObject.has("seguro")) {
					if(!jsonObject.isNull("seguro")) {
						if(jsonObject.getJSONObject("seguro").has("informacion")) {
							seguroDeVida = new SeguroDeVida();
							seguroDeVida = new Gson().fromJson(jsonObject.getJSONObject("seguro").getJSONObject("informacion").toString(), new TypeToken<SeguroDeVida>() {}.getType());
							
							if(seguroDeVida.getPrecio()>0) {
								seguroDeVida.setNombre(seguroDeVida.getNombre());
								seguroDeVida.setPrecio(seguroDeVida.getPrecio());
						    }
					    }
					}
				}
				else {
					logger.info("EL CLIENTE NO TRAE UN SEGURO PARA RENOVAR");
				}
				if(listaOfertasRenovacion.isEmpty()==false) {
					listaOfertasRenovacion.clear();
				}
				
				if(seguroDeVida != null) {
					listaOfertasRenovacion.add(seguroDeVida);
				}
				
			}catch(Exception e) {
				logger.info("Se genero una incidencia al momento de extraer JSON de renovacion de  la respuesta XML:{}", e.getMessage());
				
				
			}
		}
	}
	
	/*Metodo para extraer la lista de los seguros que se pueden ofertar al renovar un pedido*/
	public void extraeListaSegurosRenovado(String json) {
		if (json != null) {
			try {
				JSONObject jsonObject = new JSONObject(json);
				if(!listaOfertas.isEmpty()) {listaOfertas.clear();}
				if (jsonObject.has("seguros")) {	
					listaOfertas = new Gson().fromJson(jsonObject.getJSONObject("seguros").getJSONArray("lista").toString(), new TypeToken<List<SeguroDeVida>>() {}.getType());
					//logger.info("Response JSON::: " + listaOfertas);
					if (listaOfertas.isEmpty()) {
						new Exception();
					} 
				}
				
			} catch (Exception e) {
				logger.info("Se genero una incidencia al momento de extraer JSON de la respuesta XML:{}", e.getMessage());
				
			}
		}
	}
	
	
	
	/*Carga la lista de seguros de recompra para filtrar seguros de 0*/
	public List<SeguroDeVida> cargaListaSegurosCreditFiltro(int val, int pagoPuntual, int pagoNormal,int pagoPuntualPromo, int pagoNormalPromo) {
		List<SeguroDeVida> lista = new ArrayList<>();
		try {
			int maximaOfertaDeSeguros = 0;
			ArrayList<Integer> listaPreciosSeguro =  new ArrayList<Integer>();
			if(listaOfertas.isEmpty()) {}
			else {
				for(SeguroDeVida listaSeguro : listaOfertas) {
					listaPreciosSeguro.add(listaSeguro.getPrecio());
				}
			}
		
			maximaOfertaDeSeguros = Collections.max(listaPreciosSeguro);
			//logger.info("LA MAXIMA DE PRECIOLISTASEGURO ES " + maximaOfertaDeSeguros);
		
			if(maximaOfertaDeSeguros >= 10) {
				//logger.info("ENTRA AL LAS OFERTAS MAYORES DE $5 Y NO LOS OFERTA PORQUE EL CLIENTE TIENE CAPACIDAD DE PAGO Y LA LISTA DE SEGUROS RETORNA QUE SE PUEDE LLEVAR ARRIBA DE $10 EN SEGUROS");
				for(SeguroDeVida listaSeguro :listaOfertas) {
					if(listaSeguro.getPrecio() <= val  && listaSeguro.getPrecio()>5  ) {
						lista.add(new SeguroDeVida(
							listaSeguro.getPrecioCalculado(),
							listaSeguro.getPrecio(),
							listaSeguro.getSobrePrecio(),
							listaSeguro.getMontoFinal(),
							listaSeguro.getProductoId(),
							listaSeguro.getNombre(),
							(listaSeguro.getPrecio() + pagoPuntual),
							(listaSeguro.getPrecio() + pagoNormal),
							(pagoPuntualPromo),
							(pagoNormalPromo),
							(listaSeguro.getMontoFinal() * 2)
						));
					
					}
				}
			}
		
			else if(maximaOfertaDeSeguros == 5) {
				//logger.info("ENTRA A LAS OFERTAS DE SOLO $5 PORQUE LA REGLA DE LOS $35 CONTRATADOS");
				for(SeguroDeVida listaSeguro :listaOfertas) {
					if(listaSeguro.getPrecio() <= val   && listaSeguro.getPrecio()==5  ) {
						lista.add(new SeguroDeVida(
							listaSeguro.getPrecioCalculado(),
							listaSeguro.getPrecio(),
							listaSeguro.getSobrePrecio(),
							listaSeguro.getMontoFinal(),
							listaSeguro.getProductoId(),
							listaSeguro.getNombre(),
							(listaSeguro.getPrecio() + pagoPuntual),
							(listaSeguro.getPrecio() + pagoNormal),
							(pagoPuntualPromo),
							(pagoNormalPromo),
							(listaSeguro.getMontoFinal() * 2)
						));
					
					}
				}
			}
		
		}catch(Exception e) {return lista;}
		
		return lista;
		
	}
	
	
	
	
	/*Carga la lista de seguros de recompra para filtrar seguros de 0*/
	public List<SeguroDeVida> cargaListaSegurosCreditFiltroNPAM(int val, int pagoPuntual, int pagoNormal,int pagoPuntualPromo, int pagoNormalPromo,int pagoPuntualPromoEspecial, int pagoNormalPromoEspecial) {
		List<SeguroDeVida> lista = new ArrayList<>();
		
		try {
			int maximaOfertaDeSeguros = 0;
			ArrayList<Integer> listaPreciosSeguro =  new ArrayList<Integer>();
			
			if(listaOfertas.isEmpty()) {}
			else {
				for(SeguroDeVida listaSeguro : listaOfertas) {					
					listaPreciosSeguro.add(listaSeguro.getPrecio());
				}
			}
			maximaOfertaDeSeguros = Collections.max(listaPreciosSeguro);
			//logger.info("LA MAXIMA DE PRECIOLISTASEGURO ES " + maximaOfertaDeSeguros);
			
			if(maximaOfertaDeSeguros >= 10) {
				//logger.info("ENTRA AL LAS OFERTAS MAYORES DE $5 Y NO LOS OFERTA PORQUE EL CLIENTE TIENE CAPACIDAD DE PAGO Y LA LISTA DE SEGUROS RETORNA QUE SE PUEDE LLEVAR ARRIBA DE $10 EN SEGUROS");
				for(SeguroDeVida listaSeguro :listaOfertas) {
					if(listaSeguro.getPrecio() <= val  && listaSeguro.getPrecio()>5  ) {
						lista.add(new SeguroDeVida(
								listaSeguro.getPrecioCalculado(),
								listaSeguro.getPrecio(),
								listaSeguro.getSobrePrecio(),
								listaSeguro.getMontoFinal(),
								listaSeguro.getProductoId(),
								listaSeguro.getNombre(),
								(listaSeguro.getPrecio() + pagoPuntual),
								(listaSeguro.getPrecio() + pagoNormal),
								(listaSeguro.getPrecio() + pagoPuntualPromo),
								(listaSeguro.getPrecio() + pagoNormalPromo),
								(listaSeguro.getPrecio() + pagoPuntualPromoEspecial),
								(listaSeguro.getPrecio() + pagoNormalPromoEspecial),
								(listaSeguro.getMontoFinal() * 2)
							));
						
						}
				}
			}
			
			else if(maximaOfertaDeSeguros == 5) {
				//logger.info("ENTRA A LAS OFERTAS DE SOLO $5 PORQUE LA REGLA DE LOS $35 CONTRATADOS");
				for(SeguroDeVida listaSeguro :listaOfertas) {
					if(listaSeguro.getPrecio() <= val   && listaSeguro.getPrecio()==5) {
						lista.add(new SeguroDeVida(
								listaSeguro.getPrecioCalculado(),
								listaSeguro.getPrecio(),
								listaSeguro.getSobrePrecio(),
								listaSeguro.getMontoFinal(),
								listaSeguro.getProductoId(),
								listaSeguro.getNombre(),
								(listaSeguro.getPrecio() + pagoPuntual),
								(listaSeguro.getPrecio() + pagoNormal),
								(listaSeguro.getPrecio() + pagoPuntualPromo),
								(listaSeguro.getPrecio() + pagoNormalPromo),
								(listaSeguro.getPrecio() + pagoPuntualPromoEspecial),
								(listaSeguro.getPrecio() + pagoNormalPromoEspecial),
								(listaSeguro.getMontoFinal() * 2)
							));
						
						}
				}
			}
			
		}catch (Exception er) {return lista;}
		
		
		return lista;
		
		
		
	}
		
		
	
	/*Carga lista de renovacion_Personalizada con el flujo de seguros NPAM*/
	public List<SeguroDeVida> cargaListaSegurosRenovacionPersonalizadaNPAM(int pagoPuntual ,int pagoNormal,int pagoPuntualPromo, int pagoNormalPromo , int pagoPuntualPromoEspecial , int pagoNormalPromoEspecial) {
		List<SeguroDeVida> lista = new ArrayList<>();
		for (SeguroDeVida seguro : listaOfertasRenovacion) {
			if (seguro.getPrecio()> 0) {
				SeguroDeVida seguroDeVida = new SeguroDeVida();
			    int precioSeguro = seguro.getPrecio();
			    seguroDeVida.setPrecioCalculado(seguro.getPrecioCalculado());
			    seguroDeVida.setSobrePrecio(seguro.getSobrePrecio());
			    seguroDeVida.setNombre(seguro.getNombre());
			    seguroDeVida.setPrecio(precioSeguro);
			    seguroDeVida.setProductoId(seguro.getProductoId());
			    seguroDeVida.setMontoFinal(seguro.getMontoFinal());
			    seguroDeVida.setPagoPuntualSeguro(precioSeguro + pagoPuntual);
			    seguroDeVida.setPagoNormalSeguro(precioSeguro + pagoNormal);
			    seguroDeVida.setPagoPuntualSeguroCreditRun(precioSeguro + pagoPuntualPromo);
			    seguroDeVida.setPagoNormalSeguroCreditRun(precioSeguro +pagoNormalPromo);
			    seguroDeVida.setPagoPuntualPromoEspecial(precioSeguro + pagoPuntualPromoEspecial);
			    seguroDeVida.setPagoNormalPromoEspecial(precioSeguro + pagoNormalPromoEspecial);
			    seguroDeVida.setSumaMuerteAccidental(seguro.getMontoFinal() * 2);
			    seguroDeVida.setBeneficio("");
				lista.add(seguroDeVida);
				obtenSeguroARenovar();
				
			}
			else {
				//logger.info("El precio del seguro ahora  es " + seguro.getPrecio());
			}
			
		}
		return lista;
	}
	

	/*Se meten validaciones de recompra de $5 para las listas de credito NPAM*/
	public List<SeguroDeVida> cargaListaSegurosCreditoLimitadoNPAM(int val, int pagoPuntual,int pagoNormal,int pagoPuntualPromo, int pagoNormalPromo,int pagoPuntualPromoEspecial, int pagoNormalPromoEspecial) {
		int maximaOfertaDeSeguros = 0;
		ArrayList<Integer> listaPreciosSeguro = new ArrayList<Integer>();
		List<SeguroDeVida> lista = new ArrayList<>();

		for (SeguroDeVida listaSeguro : listaOfertas) {
			if(listaOfertas.isEmpty()) {}
			else {
				listaPreciosSeguro.add(listaSeguro.getPrecio());
				maximaOfertaDeSeguros = Collections.max(listaPreciosSeguro);
			}
		}
		
		//logger.info("El monto maximo encontrado en la lista de seguros es " + maximaOfertaDeSeguros);
		
		if(maximaOfertaDeSeguros >= 10) {
			//logger.info("ENTRA AL LAS OFERTAS MAYORES DE $5 Y NO LOS OFERTA PORQUE EL CLIENTE TIENE CAPACIDAD DE PAGO Y LA LISTA DE SEGUROS RETORNA QUE SE PUEDE LLEVAR ARRIBA DE $10 EN SEGUROS");
			for(SeguroDeVida listaSeguro :listaOfertas) {
				if(listaSeguro.getPrecio() <= val  && listaSeguro.getPrecio()>5  ) {
					lista.add(new SeguroDeVida(
								listaSeguro.getPrecioCalculado(),
								listaSeguro.getPrecio(),
								listaSeguro.getSobrePrecio(),
								listaSeguro.getMontoFinal(),
								listaSeguro.getProductoId(),
								listaSeguro.getNombre(),
								(listaSeguro.getPrecio() + pagoPuntual),
								(listaSeguro.getPrecio() + pagoNormal),
								(listaSeguro.getPrecio() + pagoPuntualPromo),
								(listaSeguro.getPrecio() + pagoNormalPromo),
								(listaSeguro.getPrecio() + pagoPuntualPromoEspecial),
								(listaSeguro.getPrecio() + pagoNormalPromoEspecial),
								(listaSeguro.getMontoFinal() * 2)
					));
				}
			}
		}
		else if(maximaOfertaDeSeguros == 5) {
		//	logger.info("ENTRA A LAS OFERTAS DE SOLO $5 PORQUE LA REGLA DE LOS $35 CONTRATADOS");
			for(SeguroDeVida listaSeguro :listaOfertas) {
				if(listaSeguro.getPrecio() <= val) {
					lista.add(new SeguroDeVida(
								listaSeguro.getPrecioCalculado(),
								listaSeguro.getPrecio(),
								listaSeguro.getSobrePrecio(),
								listaSeguro.getMontoFinal(),
								listaSeguro.getProductoId(),
								listaSeguro.getNombre(),
								(listaSeguro.getPrecio() + pagoPuntual),
								(listaSeguro.getPrecio() + pagoNormal),
								(listaSeguro.getPrecio() + pagoPuntualPromo),
								(listaSeguro.getPrecio() + pagoNormalPromo),
								(listaSeguro.getPrecio() + pagoPuntualPromoEspecial),
								(listaSeguro.getPrecio() + pagoNormalPromoEspecial),
								(listaSeguro.getMontoFinal() * 2)
					));	
				}
			}
		}
		return lista;
	}
	
	/*Carga la lista de seguros de $5*/
	public List<SeguroDeVida> cargaListaSegurosCreditoCincoPesosNPAM(int val, int pagoPuntual, int pagoNormal,int pagoPuntualPromo, int pagoNormalPromo,int pagoPuntualPromoEspecial, int pagoNormalPromoEspecial) {
		List<SeguroDeVida> lista = new ArrayList<>();
		if(val == 9) {
			//logger.info("Entra con la capacidad limitada hasta 9 pesos");
			for (SeguroDeVida listaSeguro : listaOfertas) {
				if (listaSeguro.getPrecio() <= val) {
					lista.add(new SeguroDeVida(
							listaSeguro.getPrecioCalculado(),
							listaSeguro.getPrecio(),
							listaSeguro.getSobrePrecio(),
							listaSeguro.getMontoFinal(),
							listaSeguro.getProductoId(),
							listaSeguro.getNombre(),
							(listaSeguro.getPrecio() + pagoPuntual),
							(listaSeguro.getPrecio() + pagoNormal),
							(listaSeguro.getPrecio() + pagoPuntualPromo),
							(listaSeguro.getPrecio() + pagoNormalPromo),
							(listaSeguro.getPrecio() + pagoPuntualPromoEspecial),
							(listaSeguro.getPrecio() + pagoNormalPromoEspecial),
							(listaSeguro.getMontoFinal() * 2)
				));	
				}
			}
		}
		
		return lista;
	}
	
	/*Carga la lista de seguros de $5*/
	public List<SeguroDeVida> cargaListaSegurosCreditFiltroCincoPesosNPAM(int val, int pagoPuntual, int pagoNormal,int pagoPuntualPromo, int pagoNormalPromo,int pagoPuntualPromoEspecial, int pagoNormalPromoEspecial) {
		List<SeguroDeVida> lista = new ArrayList<>();
		if(val == 9) {
			//logger.info("Entra con la capacidad limitada hasta 9 pesos");
			for (SeguroDeVida l : listaOfertas) {
				if (l.getPrecio() <= val && l.getPrecio() >= 5) {
					lista.add(new SeguroDeVida(
							l.getPrecioCalculado(),
							l.getPrecio(),
							l.getSobrePrecio(),
							l.getMontoFinal(),
							l.getProductoId(),
							l.getNombre(),
							(l.getPrecio() + pagoPuntual),
							(l.getPrecio() + pagoNormal),
							(l.getPrecio() + pagoPuntualPromo),
							(l.getPrecio() + pagoNormalPromo),
							(l.getPrecio() + pagoPuntualPromoEspecial),
							(l.getPrecio() + pagoNormalPromoEspecial),
							(l.getMontoFinal() * 2)
							)
						);
				}
			}
		}
		
		return lista;
	}	
	
	
	
	/*SE METEN VALIDACIONES PARA LAS RESPUESTAS DE CODIGOS DE SEGUROS*/
	
	public RespuestaSeguro llenaCodigosSeguro(String json) {
		Integer tipoOfertaRespuesta;
		if(json != null) {
			try {
				JSONObject jsonObject = new JSONObject(json);
				if(jsonObject.has("respuestaBase")) {
					
					respuestaBase = new Gson().fromJson(jsonObject.getJSONObject("respuestaBase").toString(), new TypeToken<RespuestaBase>() {}.getType());
					tipoOfertaRespuesta =  jsonObject.getInt("tipoOfertaRespuesta");
					
					respuestaSeguro.setRespuestaBase(respuestaBase);
					respuestaSeguro.setTipoOfertaRespuesta(tipoOfertaRespuesta);
						
				}
				else {
					logger.info("No se pudo obtener el objeto respuestaBase del servicio de seguros");
				}
				
				return respuestaSeguro;
				
				
			}catch(JSONException e) {
				logger.info("Se genero una incidencia al momento de extraer JSON del seguro de la respuesta XML:{}", e.getMessage());
				
			}
			return respuestaSeguro;
		}
		else {
			logger.info("EL SERVICIO DE SEGUROS CONTESTO SIN UN OBJETO Y SE GENERAUNA INCIDENCIA");
			
			respuestaBase.setCodigo(ConstantesSeguro.CODIGO_RESPUESTA);
			respuestaBase.setDescripcion(ConstantesSeguro.INCIDENCIA_EN_RESPUESTA);
			tipoOfertaRespuesta =  ConstantesSeguro.INCIDENCIA_RESPUESTA_CODIGO;
			
			respuestaSeguro.setRespuestaBase(respuestaBase);
			respuestaSeguro.setTipoOfertaRespuesta(tipoOfertaRespuesta);
			
			return respuestaSeguro;
			
		
		}
	}
	
	public RespuestaSeguro obtenCodigosSeguros() {
		RespuestaSeguro respSeguro = new RespuestaSeguro();
		RespuestaBase respBase =  new RespuestaBase();
		Integer tipoOfertaRespuesta;
		
		if(respuestaSeguro.getRespuestaBase()!=null) {
			return respSeguro = respuestaSeguro;
		}
		else {
			respBase.setCodigo(ConstantesSeguro.CODIGO_INCIDENCIA);
			respBase.setDescripcion(ConstantesSeguro.INCIDENCIA_SERVICIO_SEGUROS);
			tipoOfertaRespuesta = ConstantesSeguro.INCIDENCIA_SERVICIO_SEGURO;
			
			respSeguro.setRespuestaBase(respBase);
			respSeguro.setTipoOfertaRespuesta(tipoOfertaRespuesta);
			
			return respSeguro;
		}
	}
}