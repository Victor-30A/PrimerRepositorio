package com.bancoazteca.bdm.cotizador.BDMCotizacion.business;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.Abono;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.Cliente;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.ClienteUnicoTazTO;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.CreditRunCotizadorRequest;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.CreditRunRequest;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.CreditRunResponseCotizador;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.InformacionBase;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.ProductoCredito;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.Seguro;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SeguroAsociadoRenovacionDao;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SeguroAsociadoRequest;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SeguroAsociadoResponse;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SeguroDeVida;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SeguroVidamax;

@Service
public class CotizadorCreditRun {
	
	private static Logger logger = LoggerFactory.getLogger(CotizadorCreditRun.class);

	  private static final int SEGURO = 30;
	  private static final int MONTO_VENTA = 0;
	  private static final int PRODUCTO_ID = 0;
	  private static final int PERIODO = 1;
	  private static final int PLAZO = 0;
	  private static final int TIPO_OFERTA = 1;
	  private static final int ORIGEN = 10;
	  
	  private static final int TIPO_OFERTA_RENOVACION = 2;
	  private static final int CLIENTE_NUEVO = 0;
	  private static final int CLIENTE_RECOMPRA = 1;
	  private static final int CLIENTE_RENOVACION = 2;
	  private static final float IVA = (float) 0.16;
	  private static final int ID_COTIZADOR = 0;


	  
	  @Autowired
	  SeguroBusImpl seguroImpl;

	  private final int FLUJORENOVACION = 1;
	  private final int SEGURORENOVACION = 20;

	  /**
	   * Metodo para ir por la lista de abonos del cotizador y armar la respuesta del cotizador con lo.
	   * necesario para el surtimiento
	   * 
	   * @param request - CreditRunCotizadorRequest
	   * @param session - sesion para obtener el icu
	   * @param requestCifrado - CreditRunCotizadorRequest
	   * @return
	   */
	
	  public ResponseEntity<Object> cotizador(CreditRunCotizadorRequest request, String requestCifrado) {
		  
		  request = new CreditRunCotizadorRequest();
		  request.setMax(10400);
		  request.setMin(2000);
		  request.setTasa("");
		  request.setCanal(3);
		  request.setSucursal(4111);
		  request.setPais(1);
		  request.setPeriodo(1);
		  request.setCapacidadDePago(500);
		  request.setPlazoIni(13);
		  request.setPlazoFin(100);
		  request.setTipoFlujo(2);
		  request.setPaisRen(1);
		  request.setCanalRen(1);
		  request.setSucursalRen(1);
		  request.setPedido(20304060);
		  request.setAplicaCreditrun(false);
		  request.setTasaBase("");
		  

	    logger.info("CotizadorCreditRun [cotizador]");
	    logger.info("Request :" + request.toString());
	    
	    if (Validations.isNullOrEmpty(request)) {
		      logger.info("El request es nulo");
		      //throw new MessageException(CodigoErrorEnum.ERROR_GENERICO.getCodigo());
		    }

	    CreditRunResponseCotizador responseTO = new CreditRunResponseCotizador();
	    CreditRunBusiness creditRunBusiness = new CreditRunBusiness();


	    // para poner la lista de seguros
	    if (request.getTipoFlujo() == FLUJORENOVACION) {
	      logger.info("seguros renovacion");

	      SeguroAsociadoResponse seguroAsociado = new SeguroAsociadoResponse();
	      SeguroAsociadoRenovacionDao daoSeguroAsociado = new SeguroAsociadoRenovacionDao();
	      SeguroAsociadoRequest requestSeguroAsociado = new SeguroAsociadoRequest();

	      try {
	        requestSeguroAsociado.setCanal(request.getCanalRen());
	        requestSeguroAsociado.setPais(request.getPaisRen());
	        requestSeguroAsociado.setPedido(request.getPedido());
	        requestSeguroAsociado.setSucursal(request.getSucursalRen());

	        if(!request.getTasa().equalsIgnoreCase("F")) {
	          logger.info("Se aplica regla para clientes de rescate: Renovacion");
	          logger.info("Consultando lista de abonos con tasa unica");
	          seguroAsociado = daoSeguroAsociado.getSeguroAsociado(requestSeguroAsociado);
	          logger.info("seguroAsociado: " + seguroAsociado);
	        }
	          
	      } catch (Exception exc) {
	        logger.error("Error con seguro asociado renovacion: " + exc.getStackTrace().toString()
	            + "Causa del error: " + exc.getCause().toString() + "*******" + exc.toString());
	       // throw new MessageException(CodigoErrorEnum.ERROR_GENERICO.getCodigo());
	      }

	      try {
	        logger.info("Consultando lista de abonos con tasa unica");
	        responseTO.setListaCotizador(
	            creditRunBusiness.creditRunFlow(new CreditRunRequest(request.getMin(), request.getMax(),
	                request.getTasa(), request.getCanal(), request.getSucursal(), request.getPais(),
	                request.getPeriodo(), request.getCapacidadDePago(), request.getPlazoIni(),
	                request.getPlazoFin(), request.isAplicaCreditrun(), request.getTasaBase())));
	      } catch (Exception exc) {
	        logger.error("Error con tasas centralizadas recompra: " + exc.getStackTrace().toString()
	            + "Causa del error: " + exc.getCause().toString() + "*******" + exc.toString());
	      }

	      if(!request.getTasa().equalsIgnoreCase("F")) {
	        if (seguroAsociado.getCodigo() == 0) {
	          llenarSegurosRenovacion(responseTO, request.getCapacidadDePago());
	        }
	      }

	    }
	    else {

	      try {
	        // Aqui voy por la lista del cotizador
	        responseTO.setListaCotizador(
	        		creditRunBusiness.creditRunFlow(new CreditRunRequest(
	        				request.getMin(),
	        				request.getMax(),
	        				request.getTasa(),
	        				request.getCanal(),
	        				request.getSucursal(),
	        				request.getPais(),
	        				request.getPeriodo(),
	        				request.getCapacidadDePago(),
	        				request.getPlazoIni(),
	        				request.getPlazoFin(),
	        				request.isAplicaCreditrun(),
	        				request.getTasaBase())));
	      } catch (Exception exc) {
	        logger.error("Error con tasas centralizadas recompra: " + exc.getStackTrace().toString()
	            + "Causa del error: " + exc.getCause().toString() + "*******" + exc.toString());
	      }


	      logger.info("Lista abonos: " + responseTO.getListaCotizador().toString());

	      try {
	    	  if(!request.getTasa().equalsIgnoreCase("F")) {
	    		  logger.info("Se aplica regla para clientes de rescate: Recompra");
	    		  logger.info("Consultando seguros recompra");
	    		  getListaSeguroRecompra(request.getCapacidadDePago());
	    		  logger.info("Llenando lista de seguros");
	            
	    		  llenarListaSegurosCredit(responseTO, request.getCapacidadDePago());
	    	  }
	      } 
	      catch (Exception exc) {
	        logger.error("Error con seguro asociado recompra: " + exc.getStackTrace().toString() + "Causa del error: " + exc.getCause().toString() + "*******" + exc.toString());
	       // throw new MessageException(CodigoErrorEnum.ERROR_GENERICO.getCodigo());
	      }
	    }
	   // messageManager.managerSuccess(responseTO);

	    return new ResponseEntity<>(responseTO, HttpStatus.OK);

	  }

	  /**
	   * Esto es de seguros.
	   * 
	   * @param consultado - consultado
	   * @param capPagoDisp - capacidad de pago disponible
	   */
	  private void getListaSeguroRecompra(int capPagoDisp) {
		  
		  boolean entra;
	    logger.info("obteniendo lista de seguros recompra");
	    ClienteUnicoTazTO clienteUnicoTazTO = new ClienteUnicoTazTO();
	    
	    clienteUnicoTazTO.setCanal("1");
		clienteUnicoTazTO.setPais("1");
		clienteUnicoTazTO.setSucursal("2244");
		clienteUnicoTazTO.setFolio("25966");

		clienteUnicoTazTO.setDs("dsTarjeta");
		clienteUnicoTazTO.setUsuario("usrcapta");
		clienteUnicoTazTO.setPwd("usrcapta*01234");

	    logger.info(
	        "cliente para la lista de seguros: \ncanal: " + clienteUnicoTazTO.getCanal() + ", pais: "
	            + clienteUnicoTazTO.getPais() + ", sucursal: " + clienteUnicoTazTO.getSucursal()
	            + ", folio: " + clienteUnicoTazTO.getFolio() + ", DS: " + clienteUnicoTazTO.getDs());

	    String cadena = "";
	
	    String  BANDERA_DESARROLLO_O_PRODUCCION = "1";
	    if (BANDERA_DESARROLLO_O_PRODUCCION == "1") { 
	    	System.out.println("Entra 1"); 
	    	cadena = (String)seguroImpl.consultaLineaCredito(clienteUnicoTazTO, true);
	    	//validaRespuesta(CADENA);
	    } 
	    else {
	    	 System.out.println("Entra 2");
			 cadena = (String)seguroImpl.consultaLineaCredito(clienteUnicoTazTO, false);
	    }
	    entra = seguroImpl.validaRespuesta(cadena);
	    
	    if (entra == true) {
			
			Document doc = seguroImpl.convierteStringAXML(cadena);
			if(doc!=null){
				obtieneValor(doc.getDocumentElement(),capPagoDisp);
				if(capPagoDisp < 1){}
				else{   
					SeguroVidamax  svmx =  new SeguroVidamax (); 
					svmx.setTipoOferta(TIPO_OFERTA); //Se obtiene tipo de oferta de seguros
	             
					svmx.setOrigen(ORIGEN);
	             
					svmx.setCliente(
	            		 //new Cliente(1,1, 8624, 72525, SEGURO,"1982-11-24"
							new Cliente(1,1, 2244, 25966, SEGURO,"1982-11-24",false
	            	));
	             
					svmx.setProductoCredito(new ProductoCredito(MONTO_VENTA,PRODUCTO_ID,PERIODO,PLAZO));  
	             
					svmx.setSeguro(new Seguro(false,IVA));
	          
					svmx.setInformacionBase(new InformacionBase(4624,"SYS-BAZDIGITAL","486c952e05824414a496f093ebea2deb"));
	 
					seguroImpl.obtenerOfertasSeguros(svmx); 
	        
					//obtenerListaCotizador(responseTO, request, bdmId, idSol,fechaNacimiento,cuCre);  
	           }
			}
			else{}	
		}
	    else {
			System.out.println("SI ENTRA A LA VALIDACION DE LA CADENA");
			//obtenerListaCotizador(responseTO, request, bdmId, idSol,fechaNacimiento,cuCre);
		}
	  }

	  /**
	   * Esto es de seguros.
	   * 
	   * @param responseTO - la respuesta del cotizador
	   * @param capPagoDisp - capacidad de pago disp
	   */
	  private void llenarListaSeguros(CreditRunResponseCotizador responseTO, int capPagoDisp) {
	    for (Abono abono : responseTO.getListaCotizador()) {

	      int result = abono.getPagoPuntualPromo() > 0 ? capPagoDisp - (abono.getPagoPuntualPromo())
	          : capPagoDisp - (abono.getPagoPuntual());

	      if (result >= 20) {
	        abono.setListaOfertas(seguroImpl.cargaListaSeguros(20, abono.getPagoNormal(),
	            abono.getPagoPuntual(), abono.getPagoNormalPromo(), abono.getPagoPuntualPromo()));
	        List<SeguroDeVida> seguroFiltrado = abono.getListaOfertas().stream()
	            .filter(s -> s.getPrecio() == 20).collect(Collectors.toList());
	        abono.setListaOfertas(seguroFiltrado);
	        abono.setAplicaSeguro(true);
	      }

	      if (abono.isAplicaSeguro() == false
	          || (abono.getListaOfertas() == null || abono.getListaOfertas().size() == 0)) {
	        abono.setAplicaSeguro(false);
	      }
	    }

	  }
	  private void llenarListaSegurosCredit(CreditRunResponseCotizador responseTO, int capPagoDisp) {
		    for (Abono abono : responseTO.getListaCotizador()) {

		    //  int result = abono.getPagoPuntualPromo() > 0 ? capPagoDisp - (abono.getPagoPuntualPromo())
		      //    : capPagoDisp - (abono.getPagoPuntual());
		      
		      int result = capPagoDisp - (abono.getPagoPuntual());

		      if(result >= 10 & result <=14) {
		    	  abono.setListaOfertas(seguroImpl.cargaListaSeguros(10, abono.getPagoNormal(), abono.getPagoPuntual(), abono.getPagoNormalPromo(), abono.getPagoPuntualPromo()));  
		      }else if(result >= 15 & result <= 19) {
		    	  abono.setListaOfertas(seguroImpl.cargaListaSeguros(15, abono.getPagoNormal(), abono.getPagoPuntual(), abono.getPagoNormalPromo(), abono.getPagoPuntualPromo()));  
		      }else if(result >= 20 & result <= 24) {
		    	  abono.setListaOfertas(seguroImpl.cargaListaSeguros(20, abono.getPagoNormal(), abono.getPagoPuntual(), abono.getPagoNormalPromo(), abono.getPagoPuntualPromo()));  
		      }else if(result >= 25 & result <= 29) {
		    	  abono.setListaOfertas(seguroImpl.cargaListaSeguros(25, abono.getPagoNormal(), abono.getPagoPuntual(), abono.getPagoNormalPromo(), abono.getPagoPuntualPromo()));  
		      }else if(result >= 30) {
		    	  abono.setListaOfertas(seguroImpl.cargaListaSeguros(30, abono.getPagoNormal(), abono.getPagoPuntual(), abono.getPagoNormalPromo(), abono.getPagoPuntualPromo()));  
		      }  
		      //if ((abono.isAplicaSeguro() == false || (abono.getListaOfertas() == null || abono.getListaOfertas().size() == 0)) {
		      if ((abono.isAplicaSeguro() == false || abono.isAplicaSeguro() == true)&& (abono.getListaOfertas() == null || abono.getListaOfertas().size() == 0)) {  
		    	  	abono.setAplicaSeguro(false);
		    	  
		      }
		      else {
		    	  responseTO.setAptoSeguro(true);
					abono.setAplicaSeguro(true);
					
				}
		    }

		  }

	  /**
	   * Esto es de seguros.
	   * 
	   * @param node - nodo
	   * @param capPagoDisp - capacidad de pago disp
	   */
	  private void obtieneValor(Node node, int capPagoDisp) {
	    String nodeName = node.getNodeName();

	    if (nodeName.equals("REGISTRO1")) {
	      NamedNodeMap attributes = node.getAttributes();
	      Node n = attributes.getNamedItem("CAPACIDADDISPONIBLE");
	      if (n != null) {
	        if (capPagoDisp != 0) {
	          capPagoDisp = 0;
	        }
	        capPagoDisp = Integer.parseInt(n.getTextContent().toString());
	      }
	    }
	    NodeList list = node.getChildNodes();
	    if (list.getLength() > 0) {
	      for (int i = 0; i < list.getLength(); i++) {
	        obtieneValor(list.item(i), capPagoDisp);
	      }
	    }
	  }

	  /**
	   * Esto es para llenar la lista de seguros con 20 siempre porque no hay otro seguro.
	   * 
	   * @param responseTO - CreditRunResponseCotizador
	   * @param capPagoDisp - capacidad de pago
	   */
	  private void llenarSegurosRenovacion(CreditRunResponseCotizador responseTO, int capacidadPago) {

	    for (Abono abono : responseTO.getListaCotizador()) {
	      int puntual = 0;
	      int normal = 0;
	      int puntualPromo = 0;
	      int normalPromo = 0;
	      int pagoMasSeguro = 0;
	      if (abono.getCodigo() == 0) {
	        pagoMasSeguro = abono.getPagoPuntualPromo() + SEGURORENOVACION;
	        puntualPromo = abono.getPagoPuntualPromo();
	        normalPromo = abono.getPagoNormalPromo();
	        puntual = abono.getPagoPuntual();
	        normal = abono.getPagoNormal();
	      } else if (abono.getCodigo() == 1) {
	        pagoMasSeguro = abono.getPagoPuntual() + SEGURORENOVACION;
	        puntual = abono.getPagoPuntual();
	        normal = abono.getPagoNormal();
	      } else if (abono.getCodigo() == 2) {
	        pagoMasSeguro = abono.getPagoPuntualPromo() + SEGURORENOVACION;
	        puntualPromo = abono.getPagoPuntualPromo();
	        normalPromo = abono.getPagoNormalPromo();
	      } else {
	        pagoMasSeguro = abono.getPagoPuntual() + SEGURORENOVACION;
	        puntualPromo = abono.getPagoPuntualPromo();
	        normalPromo = abono.getPagoNormalPromo();
	        puntual = abono.getPagoPuntual();
	        normal = abono.getPagoNormal();
	      }

	      if (pagoMasSeguro <= capacidadPago) {
	        abono.setAplicaSeguro(true);
	        List<SeguroDeVida> lista = new ArrayList<>();
	        lista.add(new SeguroDeVida(0, SEGURORENOVACION, 0, 40000, 529232,
	            "SEGURO VIDAMAX $20 SEMANAL", normal + SEGURORENOVACION, puntual + SEGURORENOVACION,
	            normalPromo + SEGURORENOVACION, puntualPromo + SEGURORENOVACION, 80000));
	        abono.setListaOfertas(lista);
	      }
	    }
	  }


}
