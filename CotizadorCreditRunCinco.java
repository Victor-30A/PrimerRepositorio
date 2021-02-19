package com.bancoazteca.bdm.cotizador.BDMCotizacion.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
import com.google.gson.Gson;

@Service
public class CotizadorCreditRunCinco {
	private static Logger logger = LoggerFactory.getLogger(CotizadorCreditRunCinco.class);

	@Autowired
	SeguroBusinessImpl seguroBusinessImpl;

	private final int FLUJORECOMPRA = 2;

	public ResponseEntity<Object> cotizador(CreditRunCotizadorRequest request, String requestCifrado) {
		
		

		CreditRunResponseCotizador responseTO = new CreditRunResponseCotizador();
		Gson gson = new Gson();

		CreditRunCotizadorRequest crcr = gson.fromJson(requestCifrado, CreditRunCotizadorRequest.class);
	//	logger.info("Los datos del request son " + crcr.toString());

		request = new CreditRunCotizadorRequest();
		request.setMax(crcr.getMax());
		request.setMin(crcr.getMin());
		request.setTasa(crcr.getTasa());
		request.setCanal(crcr.getCanal());
		request.setSucursal(crcr.getSucursal());
		request.setPais(crcr.getPais());
		request.setPeriodo(crcr.getPeriodo());
		request.setCapacidadDePago(crcr.getCapacidadDePago());
		request.setPlazoIni(crcr.getPlazoIni());
		request.setPlazoFin(crcr.getPlazoFin());
		request.setTipoFlujo(crcr.getTipoFlujo());
		request.setPaisRen(crcr.getPaisRen());
		request.setCanalRen(crcr.getCanalRen());
		request.setSucursalRen(crcr.getSucursalRen());
		request.setPedido(crcr.getPedido());
		request.setAplicaCreditrun(crcr.isAplicaCreditrun());
		request.setTasaBase(crcr.getTasaBase());
		request.setCliente(crcr.getCliente());
		
		//logger.info("El objeto Cliente es " + crcr.getCliente().toString());
		
		//logger.info("Se lleno el request con los datos: " + request.toString());


		if (Validations.isNullOrEmpty(request)) {
			logger.info("El request es nulo");
		}

		CreditRunBusiness creditRunBusiness = new CreditRunBusiness();

		// para poner la lista de seguros
		if (request.getTipoFlujo() == FLUJORECOMPRA) {
			logger.info("seguros recompra $5");
			try {
				// Aqui voy por la lista del cotizador
				responseTO.setListaCotizador(creditRunBusiness.creditRunFlow(new CreditRunRequest(request.getMin(),
						request.getMax(), request.getTasa(), request.getCanal(), request.getSucursal(),
						request.getPais(), request.getPeriodo(), request.getCapacidadDePago(), request.getPlazoIni(),
						request.getPlazoFin(), request.isAplicaCreditrun(), request.getTasaBase())));
			} catch (Exception exc) {
				logger.error("Error con tasas centralizadas recompra: " + exc.getStackTrace().toString()
						+ "Causa del error: " + exc.getCause().toString() + "*******" + exc.toString());
			}

			logger.info("Lista abonos: " + responseTO.getListaCotizador().toString());

			try {
				if (!request.getTasa().equalsIgnoreCase("F")) {
				//	logger.info("Se aplica regla para clientes de rescate: Recompra");
				//	logger.info("Consultando seguros recompra");

				//	logger.info("Llenando lista de seguros");
					seguroBusinessImpl.llenaSegurosRecompra(1,crcr.getCliente());
					evaluaListaSegurosCredito(responseTO, request.getCapacidadDePago());
				}
			} catch (Exception exc) {
				//logger.error("Error con seguro asociado recompra: " + exc.getStackTrace().toString()+ "Causa del error: " + exc.getCause().toString() + "*******" + exc.toString());
			}
		}
		return new ResponseEntity<>(responseTO, HttpStatus.OK);
	}

	private void evaluaListaSegurosCredito(CreditRunResponseCotizador responseTO, int capPagoDisp) {
		//logger.info("La capacidad de pago recibido en el metodo es " + capPagoDisp);
		int resultado;
		int maxResultado;
		ArrayList<Integer> listaResultados = new ArrayList<Integer>();
		for (Abono abono : responseTO.getListaCotizador()) {
			resultado = capPagoDisp - (abono.getPagoPuntual());
			listaResultados.add(resultado);
		}
		maxResultado = Collections.max(listaResultados);
		
		logger.info("El maximo resultado de CPD - PP es:   " + maxResultado);

		if (maxResultado >= 10) {
			logger.info("Entra a llenar listas con capacidad mayor de  10");
			llenarListaSegurosCinco(responseTO, capPagoDisp);
		}
		if (maxResultado >= 5 && maxResultado <= 9) {
			logger.info("Entra a llenar  listas con capacidad menor= 9");
			llenarListaSegurosSoloCinco(responseTO, capPagoDisp);
		}

	}

	private void llenarListaSegurosCinco(CreditRunResponseCotizador responseTO, int capPagoDisp) {
		logger.info("LA CPD que se recibe para este primer metodo es " + capPagoDisp);
		
			for (Abono abono : responseTO.getListaCotizador()) {
				int result = capPagoDisp - (abono.getPagoPuntual());
				if (result >= 5 & result < 10) {
					abono.setListaOfertas(seguroBusinessImpl.cargaListaSegurosCreditoLimitado(9, abono.getPagoPuntual(),abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo()));
				}
				if (result >= 10 & result <= 14) {
					abono.setListaOfertas(seguroBusinessImpl.cargaListaSegurosCreditoLimitado(10, abono.getPagoPuntual(),abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo()));
				} else if (result >= 15 & result <= 19) {
					abono.setListaOfertas(seguroBusinessImpl.cargaListaSegurosCreditoLimitado(15, abono.getPagoPuntual(),abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo()));
				} else if (result >= 20 & result <= 24) {
					
					abono.setListaOfertas(seguroBusinessImpl.cargaListaSegurosCreditoLimitado(20, abono.getPagoPuntual(),abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo()));
				} else if (result >= 25 & result <= 29) {
					
					abono.setListaOfertas(seguroBusinessImpl.cargaListaSegurosCreditoLimitado(25, abono.getPagoPuntual(),abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo()));
				} else if (result >= 30) {
					abono.setListaOfertas(seguroBusinessImpl.cargaListaSegurosCreditoLimitado(30, abono.getPagoPuntual(),abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo()));
				}
				if ((abono.isAplicaSeguro() == false || abono.isAplicaSeguro() == true)
						&& (abono.getListaOfertas() == null || abono.getListaOfertas().size() == 0)) {
					abono.setAplicaSeguro(false);

				} else {
					responseTO.setAptoSeguro(true);
					abono.setAplicaSeguro(true);
				}
			}	
		}

	private void llenarListaSegurosSoloCinco(CreditRunResponseCotizador responseTO, int capPagoDisp) {
		for (Abono abono : responseTO.getListaCotizador()) {
			int result = capPagoDisp - (abono.getPagoPuntual());
			if (result >= 5 & result < 10) {
				abono.setListaOfertas(seguroBusinessImpl.cargaListaSegurosCreditoCincoPesos(9, abono.getPagoPuntual(),abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo()));
			}

			if ((abono.isAplicaSeguro() == false || abono.isAplicaSeguro() == true)
					&& (abono.getListaOfertas() == null || abono.getListaOfertas().size() == 0)) {
				abono.setAplicaSeguro(false);

			} else {
				responseTO.setAptoSeguro(true);
				abono.setAplicaSeguro(true);
			}
		}
	}
}