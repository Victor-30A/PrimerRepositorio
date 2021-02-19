package com.bancoazteca.bdm.cotizador.BDMCotizacion.business;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bancoazteca.bdm.cotizador.BDMCotizacion.dao.TasasCentralizadasDao;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.Abono;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.CreditRunRequest;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.LstDTOAbono;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.RequestTasasCentralizadas;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.TasasCentralizadas;

@Service
public class CreditRunBusiness {
	
	private String tasasBaseUrl2 = "https://dev-api.bancoazteca.com.mx:8080/cobranza_credito/creditos/cotizaciones/v1/abonos";

		  private static final int CASOHAPPY = 0;

		  private static final int CASONOEXISTECREDITRUN = 1;

		  private static final int CASONOEXISTENORMAL = 2;

		  private static final int CASONORMALMENORACREDITRUN = 3;

		  private static final Logger LOG = LoggerFactory.getLogger(CreditRunBusiness.class);


		  /**
		   * Obtiene todas las tasas y plazos posibles.
		   * 
		   * @param requestTasasCentralizadas - el request para llamar tasas centralizadas
		   * @return
		   */
		  public TasasCentralizadas getTasas(RequestTasasCentralizadas requestTasasCentralizadas) {

		    TasasCentralizadasDao restTempTasas = new TasasCentralizadasDao();
		    TasasCentralizadas tasas = new TasasCentralizadas();

		    try {
		      tasas = restTempTasas.getTasas(tasasBaseUrl2, requestTasasCentralizadas);
		      //LOG.info(tasas.toString());
		      if (tasas.getCargaAbonosResult() == null) {
		        LOG.error("El resultado de consumir el API abonos fue null");
		        //throw new MessageException(CodigoErrorEnum.ERROR_GENERICO.getCodigo());
		      } else if (tasas.getCargaAbonosResult().getLstDTOAbonos().isEmpty()
		          || tasas.getCargaAbonosResult().getLstDTOAbonos() == null) {
		       // LOG.error("El resultado de la lista de abonos fue null");
		       // throw new MessageException(CodigoErrorEnum.ERROR_GENERICO.getCodigo());
		      }
		    } catch (Exception exc) {
		      LOG.error("Ocurrio un problema consumiendo el servicio de tasas centralizadas. "
		          + "Causa del error: " + exc.getCause().toString() + "*******" + exc.toString());
		     // throw new MessageException(CodigoErrorEnum.ERROR_GENERICO.getCodigo());
		    }

		    return tasas;
		  }

		  /**
		   * Crea un objeto de tipo Abono y lo regresa fusionando el match. Valida cual de los dos
		   * LstDTOAbono tiene la promocion
		   * 
		   * @param normal - el abono normal sin crditrun
		   * @param promo - el abono creditrun
		   * @return
		   */
		  public Abono setAbono(LstDTOAbono normal, LstDTOAbono promo, String tasaBase) {

		    LstDTOAbono normalLocal;
		    LstDTOAbono promoLocal;

		    // El abono normal es el que tiene tipo cliente B
		    if (normal.getTipoCliente().contentEquals(tasaBase)) {
		      normalLocal = normal;
		      promoLocal = promo;
		    } else {
		      normalLocal = promo;
		      promoLocal = normal;
		    }

		    // Creo un objeto de tipo abono que requiere el front
		    Abono abonoTemp = new Abono();
		    // El idproducto es el sku de LstDTOAbono
		    abonoTemp.setIdProducto(normalLocal.getSku());
		    // El monto es el precio de LstDTOAbono
		    abonoTemp.setMonto(Double.valueOf(normalLocal.getPrecio()).intValue());
		    // El IdPeriodicidad es el periodo de LstDTOAbono
		    int periodo = Integer.parseInt(normalLocal.getPeriodo());
		    abonoTemp.setIdPeriodicidad(periodo);

		    // Se obtiene el pago total normal de tasa CREDITrUN con ((plazo-1)* abonoNormal) + ultimopago
		    int pagoTotalNormal = ((Integer.parseInt(promoLocal.getPlazo()) - 1)
		        * Double.valueOf(promoLocal.getNormal()).intValue())
		        + Double.valueOf(promoLocal.getUltimo()).intValue();

		    // Se obtiene el pago total puntual de tasa creditRun con plazo * pagopuntual tasa creditRun
		    int pagoTotalPuntual = Integer.parseInt(promoLocal.getPlazo())
		        * Double.valueOf(promoLocal.getPuntual()).intValue();

		    // se pone la Periodicidad dependiendo del Periodo
		    if (normalLocal.getPeriodo().equals("13")) {
		      abonoTemp.setPeriodicidad("QUINCENAL");
		    } else if (normalLocal.getPeriodo().equals("14")) {
		      abonoTemp.setPeriodicidad("MENSUAL");
		    } else {
		      abonoTemp.setPeriodicidad("SEMANAL");
		    }

		    // El IdPlazo es el Plazo de LstDTOAbono
		    abonoTemp.setIdPlazo(normalLocal.getPlazo());
		    abonoTemp.setPlazo(normalLocal.getPlazo());

		    // Caso el pago normal es menor al pago creditrun
		    if (Double.valueOf(normalLocal.getPuntual()).intValue() <= Double
		        .valueOf(promoLocal.getPuntual()).intValue()) {

		      abonoTemp.setCodigo(CASONORMALMENORACREDITRUN);
		      // Si este mensaje viene vacio quiere decir que el pago normal es menor al pago creditRun esto
		      abonoTemp.setMensajeCR("");

		      abonoTemp.setPagoNormal(Double.valueOf(normalLocal.getNormal()).intValue());
		      abonoTemp.setPagoPuntual(Double.valueOf(normalLocal.getPuntual()).intValue());
		      abonoTemp.setPagoNormalPromo(Double.valueOf(promoLocal.getNormal()).intValue());
		      abonoTemp.setPagoPuntualPromo(Double.valueOf(promoLocal.getPuntual()).intValue());

		      abonoTemp.setIntereses(Double.valueOf(normalLocal.getSobre()).intValue());

		      // INTERES MAS MONTO ES IGUAL A MONTO TOTAL CALCULADO SOBRE EL PAGO NORMAL
		      abonoTemp.setMontoTotal(Double.valueOf(normalLocal.getSobre()).intValue()
		          + Double.valueOf(normalLocal.getPrecio()).intValue());

		    } else {
		      abonoTemp.setCodigo(CASOHAPPY);
		      abonoTemp.setMensajeCR("Felicidades por tu beneficio creditRun");

		      abonoTemp.setPagoNormal(Double.valueOf(normalLocal.getNormal()).intValue());
		      abonoTemp.setPagoPuntual(Double.valueOf(normalLocal.getPuntual()).intValue());
		      abonoTemp.setPagoNormalPromo(Double.valueOf(promoLocal.getNormal()).intValue());
		      abonoTemp.setPagoPuntualPromo(Double.valueOf(promoLocal.getPuntual()).intValue());

		      // INTERES MAS MONTO ES IGUAL A MONTO TOTAL CALCULADO SOBRE EL PAGO NORMAL
		      abonoTemp.setMontoTotal(Double.valueOf(normalLocal.getSobre()).intValue()
		          + Double.valueOf(normalLocal.getPrecio()).intValue());

		      abonoTemp.setIntereses(Double.valueOf(promoLocal.getSobre()).intValue());

		      abonoTemp.setAhorro(pagoTotalNormal - pagoTotalPuntual);
		      // abonoTemp.setAhorro(Double.valueOf(normalLocal.getPrecio()).intValue()
		      // + Double.valueOf(normalLocal.getSobre()).intValue() - abonoTemp.getMontoTotal());

		    }

		    return abonoTemp;
		  }

		  /**
		   * Crea un objeto de tipo Abono y lo regresa fusionando el match.
		   * 
		   * @param normal - el abono que se encontro ya sea creditrun o normal
		   * @return
		   */
		  public Abono setAbono(LstDTOAbono normal, String tasaBase) {

		    Abono abonoTemp = new Abono();
		    abonoTemp.setIdProducto(normal.getSku());
		    abonoTemp.setMonto(Double.valueOf(normal.getPrecio()).intValue());
		    int periodo = Integer.parseInt(normal.getPeriodo());
		    abonoTemp.setIdPeriodicidad(periodo);

		    if (normal.getPeriodo().equals("13")) {
		      abonoTemp.setPeriodicidad("QUINCENAL");
		    } else if (normal.getPeriodo().equals("14")) {
		      abonoTemp.setPeriodicidad("MENSUAL");
		    } else {
		      abonoTemp.setPeriodicidad("SEMANAL");
		    }

		    abonoTemp.setIdPlazo(normal.getPlazo());
		    abonoTemp.setPlazo(normal.getPlazo());

		    // Caso no existe pago tasa creditRun
		    if (normal.getTipoCliente().equals(tasaBase)) {
		      abonoTemp.setCodigo(CASONOEXISTECREDITRUN);
		      abonoTemp.setMensajeCR("No aplica creditRun");

		      abonoTemp.setPagoNormal(Double.valueOf(normal.getNormal()).intValue());
		      abonoTemp.setPagoPuntual(Double.valueOf(normal.getPuntual()).intValue());

		      // Caso no existe pago tasa base
		    } else {
		      abonoTemp.setCodigo(CASONOEXISTENORMAL);
		      abonoTemp.setMensajeCR("Gracias a creditRun puedes acceder a este prestamo");

		      abonoTemp.setPagoNormalPromo(Double.valueOf(normal.getNormal()).intValue());
		      abonoTemp.setPagoPuntualPromo(Double.valueOf(normal.getPuntual()).intValue());
		      abonoTemp.setAhorro((abonoTemp.getPagoNormalPromo() - abonoTemp.getPagoPuntualPromo())
		          * Integer.parseInt(normal.plazo));
		    }

		    abonoTemp.setIntereses(Double.valueOf(normal.getSobre()).intValue());

		    // INTERES MAS MONTO ES IGUAL A MONTO TOTAL CALCULADO SOBRE EL PAGO NORMAL
		    abonoTemp.setMontoTotal(Double.valueOf(normal.getSobre()).intValue()
		        + Double.valueOf(normal.getPrecio()).intValue());

		    return abonoTemp;
		  }

		  /**
		   * Validates that the req its valid.
		   */
		  public void validateReq(CreditRunRequest req) {
		    if (req.getMin() > req.getMax()) {
		     // LOG.info("El minimo no puede ser mayor al maximo");
		      //throw new MessageException(CodigoErrorEnum.ERROR_GENERICO.getCodigo());
		    }
		    if (req.getPlazoFin() < req.getPlazoIni()) {
		    //  LOG.info("El plazo maximo no puede ser menor al minimo");
		     // throw new MessageException(CodigoErrorEnum.ERROR_GENERICO.getCodigo());
		    }
		  }

		  /**
		   * Flujo principal de creditRun.
		   * 
		   * @param creditRunRequest - el objeto con los datos para el flujo
		   * @return
		   */
		  public List<Abono> creditRunFlow(CreditRunRequest creditRunRequest) {

		    List<Abono> abonos = new ArrayList<>();

		    // flujo creditRun
		    if (creditRunRequest.isAplicaCreditRun()) {
		      abonos = llenarListaTasaCreditRun(creditRunRequest);
		      // Cualquier otro flujo
		    } else {
		      abonos = llenarListaTasaUnica(creditRunRequest);
		    }

		    return abonos;
		  }

		  /**
		   * Cuando no aplica creditRun.
		   * 
		   * @param creditRunRequest - CreditRunRequest
		   */
		  public List<Abono> llenarListaTasaUnica(CreditRunRequest creditRunRequest) {

		    int capacidadDePago = creditRunRequest.getCapacidadDePago();

		    // Se hace el request con una sola tasa
		    RequestTasasCentralizadas requestTasas =
		        new RequestTasasCentralizadas(creditRunRequest.getPeriodo(), creditRunRequest.getMin(),
		            creditRunRequest.getMax(), creditRunRequest.getTasa().toUpperCase(),
		            creditRunRequest.getCanal(), creditRunRequest.getSucursal(), creditRunRequest.getPais(),
		            creditRunRequest.getPlazoIni(), creditRunRequest.getPlazoFin());

		  //  LOG.info("Request tasas: {}", requestTasas);
		    // se manda a llamar el servicio de tasas centralizadas
		    TasasCentralizadas tasas = getTasas(requestTasas);

		    // Agarro la lista de todos los abonos
		    List<LstDTOAbono> listaAbonosCompletos = tasas.getCargaAbonosResult().getLstDTOAbonos();
		   // LOG.info("listaAbonosCompletos:" + listaAbonosCompletos.toString());
		    // Esta es la nueva lista con el formato que el front debe recibir
		    List<Abono> abonosFiltrados = new ArrayList<>();

		    // Tomo la lista filtrada por capacidad de pago y la voy iterando haciendo pares de los que
		    // tengan el mismo precio y plazo
		    for (int i = 0; i < listaAbonosCompletos.size(); i++) {
		      Abono abonoTemp = setAbonoUnico(listaAbonosCompletos.get(i));

		      if (abonoTemp.getPagoPuntual() <= capacidadDePago) {
		        abonosFiltrados.add(abonoTemp);
		      }
		    }

		    if (abonosFiltrados.isEmpty()) {
		     // throw new MessageException(CodigoErrorEnum.ERROR_GENERICO.getCodigo());
		    }


		    return abonosFiltrados;
		  }

		  /**
		   * Pone los datos de los abonos cuando no aplica creditRun y no hay tasa para comparar.
		   * 
		   * @param normal abono a convertirse a respuesta para el front
		   * @return Abono
		   */
		  public Abono setAbonoUnico(LstDTOAbono normal) {

		    Abono abonoTemp = new Abono();
		    abonoTemp.setIdProducto(normal.getSku());
		    abonoTemp.setMonto(Double.valueOf(normal.getPrecio()).intValue());
		    int periodo = Integer.parseInt(normal.getPeriodo());
		    abonoTemp.setIdPeriodicidad(periodo);

		    if (normal.getPeriodo().equals("13")) {
		      abonoTemp.setPeriodicidad("QUINCENAL");
		    } else if (normal.getPeriodo().equals("14")) {
		      abonoTemp.setPeriodicidad("MENSUAL");
		    } else {
		      abonoTemp.setPeriodicidad("SEMANAL");
		    }

		    abonoTemp.setIdPlazo(normal.getPlazo());
		    abonoTemp.setPlazo(normal.getPlazo());

		    abonoTemp.setCodigo(CASONOEXISTECREDITRUN);
		    abonoTemp.setMensajeCR("No aplica creditRun");
		    abonoTemp.setPagoNormal(Double.valueOf(normal.getNormal()).intValue());
		    abonoTemp.setPagoPuntual(Double.valueOf(normal.getPuntual()).intValue());

		    abonoTemp.setIntereses(Double.valueOf(normal.getSobre()).intValue());

		    // INTERES MAS MONTO ES IGUAL A MONTO TOTAL CALCULADO SOBRE EL PAGO NORMAL
		    abonoTemp.setMontoTotal(Double.valueOf(normal.getSobre()).intValue()
		        + Double.valueOf(normal.getPrecio()).intValue());

		    return abonoTemp;
		  }

		  /**
		   * Para llenar la lista de abonos cuando aplica creditRun. 
		   * @param creditRunRequest - CreditRunRequest
		   * @return - List de Abono
		   */
		  public List<Abono> llenarListaTasaCreditRun(CreditRunRequest creditRunRequest) {
		    //LOG.info("Llenando lista con tasa unica");
		    int capacidadDePago = creditRunRequest.getCapacidadDePago();

		    // ir por las tasas con los datos que me da recompra requiero el minimo en caso de que sea
		    // renovacion
		    RequestTasasCentralizadas requestTasas = new RequestTasasCentralizadas(
		        creditRunRequest.getPeriodo(), creditRunRequest.getMin(), creditRunRequest.getMax(),
		        creditRunRequest.getTasaBase() + "," + creditRunRequest.getTasa().toUpperCase(),
		        creditRunRequest.getCanal(), creditRunRequest.getSucursal(), creditRunRequest.getPais(),
		        creditRunRequest.getPlazoIni(), creditRunRequest.getPlazoFin());
		    //LOG.info("Request tasas: {}", requestTasas);
		    TasasCentralizadas tasas = getTasas(requestTasas);

		    // Agarro la lista de todos los abonos
		    List<LstDTOAbono> listaAbonosCompletos = tasas.getCargaAbonosResult().getLstDTOAbonos();
		    //LOG.info("listaAbonosCompletos:" + listaAbonosCompletos.toString());
		    // Esta es la nueva lista con el formato que el front debe recibir
		    List<Abono> abonosFiltrados = new ArrayList<>();

		    // Tomo la lista filtrada por capacidad de pago y la voy iterando haciendo pares de los que
		    // tengan el mismo precio y plazo
		    for (int i = 0; i < listaAbonosCompletos.size(); i++) {
		      String plazo = listaAbonosCompletos.get(i).getPlazo();
		      String precio = listaAbonosCompletos.get(i).getPrecio();

		      // Creo una lista temporal con los items que tengan el mismo plazo y precio
		      List<LstDTOAbono> abonosNuevos = listaAbonosCompletos.stream().filter(s -> s.getPlazo().equals(plazo) && s.getPrecio().equals(precio)).collect(Collectors.toList());

		      // Si contiene mas de un elemento significa que encontro un match
		      if (abonosNuevos.size() > 1) {
		        // mando ambos abonos para unirlos
		        Abono abonoTemp =
		            setAbono(abonosNuevos.get(0), abonosNuevos.get(1), creditRunRequest.getTasaBase());

		        boolean alreadyExist = false;
		        // aqui la estoy cagando
		        for (int j = 0; j < abonosFiltrados.size(); j++) {
		          if (abonosFiltrados.get(j).getPlazo().equals(abonoTemp.getPlazo())
		              && abonosFiltrados.get(j).getMonto() == abonoTemp.getMonto()) {
		            alreadyExist = true;
		            break;
		          }
		        }

		        // Si no esta en la lista lo agrego
		        if (alreadyExist == false && abonoTemp.getPagoPuntualPromo() <= capacidadDePago) {
		          abonosFiltrados.add(abonoTemp);
		        }

		      } else if (abonosNuevos.size() == 1) {

		        Abono abonoTemp = setAbono(abonosNuevos.get(0), creditRunRequest.getTasaBase());

		        boolean alreadyExist = false;
		        for (int j = 0; j < abonosFiltrados.size(); j++) {
		          if (abonosFiltrados.get(j).getPlazo().equals(abonoTemp.getPlazo())
		              && abonosFiltrados.get(j).getMonto() == abonoTemp.getMonto()) {
		            alreadyExist = true;
		            break;
		          }
		        }
		        // El pago es el que no se encuentre en 0 ya sea caso que exista normal y creditRun no o
		        // viceversa
		        // EL pago se compara con la capacidad de pago para ver si se agrega o no a la lista
		        int pago = abonoTemp.getPagoPuntual() == 0 ? abonoTemp.getPagoPuntualPromo()
		            : abonoTemp.getPagoPuntual();
		        if (!alreadyExist && pago <= capacidadDePago) {
		          abonosFiltrados.add(abonoTemp);
		        }
		      }

		    }

		    if (abonosFiltrados.isEmpty()) {
		    //  LOG.info("abonosFiltrados its empty");
		     // throw new MessageException(CodigoErrorEnum.ERROR_GENERICO.getCodigo());
		    }

		    return abonosFiltrados;
		  }

}
