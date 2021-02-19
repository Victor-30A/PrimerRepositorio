package com.bancoazteca.bdm.cotizador.BDMCotizacion.business;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.Abono;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.AbonoNormal;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.CreditRunCotizadorRequest;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.CreditRunResponseCotizador;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.EscalerasResponseTo;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.RespuestaBase;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.RespuestaSeguro;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SeguroAsociadoRenovacionDao;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SeguroAsociadoRequest;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SeguroAsociadoResponse;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SeguroDeVida;

@Component
public class UtilSeguroCredito {
	private static Logger logger = LoggerFactory.getLogger(UtilSeguroCredito.class);

	private final int SEGURORENOVACION = 20;

	@Autowired
	private SeguroBusinessImpl seguroImple;
	
	@Autowired 
	private UtilSeguro utilSeguro;

	/* Metodos para tasas Centralizadas */

	public void llenarListaSegurosCredit(CreditRunResponseCotizador responseTO, int capPagoDisp) {
		for (Abono abono : responseTO.getListaCotizador()) {
			int result = abono.getPagoPuntualPromo() > 0 ? capPagoDisp - (abono.getPagoPuntualPromo())
					: capPagoDisp - (abono.getPagoPuntual());

			if (result >= 20) {
				abono.setListaOfertas(seguroImple.cargaListaSegurosCredit(20, abono.getPagoPuntual(),
						abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo()));
				List<SeguroDeVida> seguroFiltrado = abono.getListaOfertas().stream().filter(s -> s.getPrecio() == 20)
						.collect(Collectors.toList());
				abono.setListaOfertas(seguroFiltrado);
				abono.setAplicaSeguro(true);
			}
			if (abono.isAplicaSeguro() == false
					|| (abono.getListaOfertas() == null || abono.getListaOfertas().size() == 0)) {
				abono.setAplicaSeguro(false);
			}
		}
	}

	public void llenarListaSegurosRecompra(CreditRunResponseCotizador responseTO, int capPagoDisp) {
		logger.info("LA CAPACIDAD DE PAGO DISPONIBLE ES ::::: " + capPagoDisp);

		for (Abono abono : responseTO.getListaCotizador()) {
			int result = capPagoDisp - (abono.getPagoPuntual());
			if (result >= 10 & result <= 14) {
				abono.setListaOfertas(seguroImple.cargaListaSeguros(10, abono.getPagoPuntual(), abono.getPagoNormal()));
			} else if (result >= 15 & result <= 19) {
				abono.setListaOfertas(seguroImple.cargaListaSeguros(15, abono.getPagoPuntual(), abono.getPagoNormal()));
			} else if (result >= 20 & result <= 24) {
				abono.setListaOfertas(seguroImple.cargaListaSeguros(20, abono.getPagoPuntual(), abono.getPagoNormal()));
			} else if (result >= 25 & result <= 29) {
				abono.setListaOfertas(seguroImple.cargaListaSeguros(25, abono.getPagoPuntual(), abono.getPagoNormal()));
			} else if (result >= 30) {
				abono.setListaOfertas(seguroImple.cargaListaSeguros(30, abono.getPagoPuntual(), abono.getPagoNormal()));
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

	/**
	 * Esto es para llenar la lista de seguros con 20 siempre porque no hay otro
	 * seguro.
	 * 
	 * @param responseTO  - CreditRunResponseCotizador
	 * @param capPagoDisp - capacidad de pago
	 */
	public void llenarSegurosRenovacion(CreditRunResponseCotizador responseTO, int capacidadPago) {

		for (Abono abono : responseTO.getListaCotizador()) {
			int puntual = 0;
			int normal = 0;
			int puntualPromo = 0;
			int normalPromo = 0;
			int pagoMasSeguro = 0;
			if (abono.getCodigo() == 0) {
				pagoMasSeguro = abono.getPagoPuntualPromo();
				puntualPromo = abono.getPagoPuntualPromo();
				normalPromo = abono.getPagoNormalPromo();
				puntual = abono.getPagoPuntual();
				normal = abono.getPagoNormal();
			} else if (abono.getCodigo() == 1) {
				pagoMasSeguro = abono.getPagoPuntual();
				puntual = abono.getPagoPuntual();
				normal = abono.getPagoNormal();
			} else if (abono.getCodigo() == 2) {
				pagoMasSeguro = abono.getPagoPuntualPromo();
				puntualPromo = abono.getPagoPuntualPromo();
				normalPromo = abono.getPagoNormalPromo();
			} else {
				pagoMasSeguro = abono.getPagoPuntual();
				puntualPromo = abono.getPagoPuntualPromo();
				normalPromo = abono.getPagoNormalPromo();
				puntual = abono.getPagoPuntual();
				normal = abono.getPagoNormal();
			}

			if (pagoMasSeguro <= capacidadPago) {
				abono.setAplicaSeguro(true);
				List<SeguroDeVida> lista = new ArrayList<>();
				lista.add(new SeguroDeVida(0, SEGURORENOVACION, 0, 30000, 529232, "SEGURO VIDAMAX $20 SEMANAL",
						puntual + SEGURORENOVACION, normal + SEGURORENOVACION, puntualPromo + SEGURORENOVACION,
						normalPromo + SEGURORENOVACION, 60000));
				abono.setListaOfertas(lista);
			}
		}
	}

	/**
	 * Este metodo es para llenar la lista de renovacion con seguros y en diferentes
	 * planes.
	 * 
	 * @param responseTO - CreditRunResponseCotizador
	 */
	public void llenarSegurosRenovacionModulo(CreditRunResponseCotizador responseTO) {
		logger.info("Entrando al metodo llenarSegurosRenovacionModulo");
		for (Abono abono : responseTO.getListaCotizador()) {
			abono.setListaOfertas(
					seguroImple.cargaListaSegurosRenovacion(abono.getPagoPuntual(), abono.getPagoNormal()));

			if ((abono.isAplicaSeguro() == false || abono.isAplicaSeguro() == true)
					&& (abono.getListaOfertas() == null || abono.getListaOfertas().size() == 0)) {
				abono.setAplicaSeguro(false);
			} else {
				responseTO.setAptoSeguro(true);
				abono.setAplicaSeguro(true);
			}
		}
	}

	/* Metodos para Escaleras NPAM */

	public void llenarListaSegurosRecompraOfertasEspeciales(EscalerasResponseTo responseTO, int capPagoDisp) {
		logger.info("LA CAPACIDAD DE PAGO DISPONIBLE ES ::::: " + capPagoDisp);

		for (AbonoNormal abono : responseTO.getListaCotizador()) {
			int result = capPagoDisp - (abono.getPagoPuntualPromo());
			if (result >= 10 & result <= 14) {
				abono.setListaOfertas(seguroImple.cargaListaSegurosPromoEspecial(10, abono.getPagoPuntual(),
						abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo(),
						abono.getPagoPuntualPromoEspecial(), abono.getPagoNormalPromoEspecial()));
			} else if (result >= 15 & result <= 19) {
				abono.setListaOfertas(seguroImple.cargaListaSegurosPromoEspecial(15, abono.getPagoPuntual(),
						abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo(),
						abono.getPagoPuntualPromoEspecial(), abono.getPagoNormalPromoEspecial()));
			} else if (result >= 20 & result <= 24) {
				abono.setListaOfertas(seguroImple.cargaListaSegurosPromoEspecial(20, abono.getPagoPuntual(),
						abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo(),
						abono.getPagoPuntualPromoEspecial(), abono.getPagoNormalPromoEspecial()));
			} else if (result >= 25 & result <= 29) {
				abono.setListaOfertas(seguroImple.cargaListaSegurosPromoEspecial(25, abono.getPagoPuntual(),
						abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo(),
						abono.getPagoPuntualPromoEspecial(), abono.getPagoNormalPromoEspecial()));
			} else if (result >= 30) {
				abono.setListaOfertas(seguroImple.cargaListaSegurosPromoEspecial(30, abono.getPagoPuntual(),
						abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo(),
						abono.getPagoPuntualPromoEspecial(), abono.getPagoNormalPromoEspecial()));
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

	public void seguroRenovacion(CreditRunCotizadorRequest request, EscalerasResponseTo responseTO) {

		SeguroAsociadoResponse seguroAsociado = new SeguroAsociadoResponse();
		SeguroAsociadoRenovacionDao daoSeguroAsociado = new SeguroAsociadoRenovacionDao();
		SeguroAsociadoRequest requestSeguroAsociado = new SeguroAsociadoRequest();
		//DataSeguroAsociado objSeguro = new DataSeguroAsociado();

		try {

			requestSeguroAsociado.setCanal(request.getCanalRen());
			requestSeguroAsociado.setPais(request.getPaisRen());
			requestSeguroAsociado.setPedido(request.getPedido());
			requestSeguroAsociado.setSucursal(request.getSucursalRen());

			if (!request.getTasa().equalsIgnoreCase("F")) {
				seguroAsociado = daoSeguroAsociado.getSeguroAsociado(requestSeguroAsociado);
				logger.info("seguroAsociado: " + seguroAsociado);

				if (seguroAsociado.getCodigo() == 0) {
					Gson g = new Gson();
					//objSeguro = g.fromJson(seguroAsociado.getData(), DataSeguroAsociado.class);
				}

			}

		} catch (Exception exc) {
			logger.error("Incidencia con seguro asociado renovacion: " + "Causa del incidencia: "
					+ exc.getCause().toString() + "*******" + exc.getMessage().toString());
			//throw new MessageException(CodigoErrorEnum.ERROR_GENERICO.getCodigo());
		}

		if (!request.getTasa().equalsIgnoreCase("F")) {
			//if (seguroAsociado.getCodigo() == 0 && objSeguro.getdatoRsp().get(0).getImpSeguroVida() >= 20) {
			//	llenarSegurosRenovacionEspecial(responseTO, request.getCapacidadDePago());
			//}
		}

	}

	

	/**
	 * Esto es para llenar la lista de renovacion de seguros con ofertas especiales
	 * y diferentes planes de seguro.
	 * 
	 * @param responseTO - CreditRunResponseCotizador
	 */
	public void llenarSegurosRenovacionModuloOfertasEspeciales(EscalerasResponseTo responseTO) {
		logger.info("Entrando al metodo llenarSegurosRenovacionModulo de Ofertas Especiales");
		for (AbonoNormal abonoNormal : responseTO.getListaCotizador()) {
			abonoNormal.setListaOfertas(seguroImple.cargaListaSegurosRenovacionOfertasEspeciales(
					abonoNormal.getPagoPuntual(), abonoNormal.getPagoNormal(), abonoNormal.getPagoPuntualPromo(),
					abonoNormal.getPagoNormalPromo(), abonoNormal.getPagoPuntualPromoEspecial(),
					abonoNormal.getPagoNormalPromoEspecial()));

			if ((abonoNormal.isAplicaSeguro() == false || abonoNormal.isAplicaSeguro() == true)
					&& (abonoNormal.getListaOfertas() == null || abonoNormal.getListaOfertas().size() == 0)) {
				abonoNormal.setAplicaSeguro(false);
			} else {
				responseTO.setAptoSeguro(true);
				abonoNormal.setAplicaSeguro(true);
			}
		}
	}

	/* Se hace logica para la evaluacion de los seguros de $5 de Credito tradicional*/
	public void evaluaListaSegurosCredito(CreditRunResponseCotizador responseTO, int capPagoDisp) {
		int resultado;
		int maxResultado;
		ArrayList<Integer> listaResultados = new ArrayList<Integer>();
		
	//	logger.info("La capacidad de pago recibido en el metodo es " + capPagoDisp);
		for (Abono abono : responseTO.getListaCotizador()) {
			resultado = capPagoDisp - (abono.getPagoPuntual());
			listaResultados.add(resultado);
		}
		maxResultado = Collections.max(listaResultados);
		logger.info("El maximo resultado para el metodo de $5 de CPD - PP es:   " + maxResultado);

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
				abono.setListaOfertas(seguroImple.cargaListaSegurosCreditoLimitado(9, abono.getPagoPuntual(),
						abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo()));
			}
			if (result >= 10 & result <= 14) {
				abono.setListaOfertas(seguroImple.cargaListaSegurosCreditoLimitado(10, abono.getPagoPuntual(),
						abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo()));
			} else if (result >= 15 & result <= 19) {
				abono.setListaOfertas(seguroImple.cargaListaSegurosCreditoLimitado(15, abono.getPagoPuntual(),
						abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo()));
			} else if (result >= 20 & result <= 24) {

				abono.setListaOfertas(seguroImple.cargaListaSegurosCreditoLimitado(20, abono.getPagoPuntual(),
						abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo()));
			} else if (result >= 25 & result <= 29) {

				abono.setListaOfertas(seguroImple.cargaListaSegurosCreditoLimitado(25, abono.getPagoPuntual(),
						abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo()));
			} else if (result >= 30) {
				abono.setListaOfertas(seguroImple.cargaListaSegurosCreditoLimitado(30, abono.getPagoPuntual(),
						abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo()));
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
				abono.setListaOfertas(seguroImple.cargaListaSegurosCreditoCincoPesos(9, abono.getPagoPuntual(),
						abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo()));
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
	
	
	
	
	/* Se hace logica para la evaluacion de los seguros de $5 de Credito Npam*/
	public void evaluaListaSegurosCreditoNpam(EscalerasResponseTo responseTO, int capPagoDisp) {
		//logger.info("La capacidad de pago recibido en el metodo es " + capPagoDisp);
		int resultado;
		int maxResultado;
		ArrayList<Integer> listaResultados = new ArrayList<Integer>();
		for (AbonoNormal abono : responseTO.getListaCotizador()) {
			resultado = capPagoDisp - (abono.getPagoPuntualPromo());
			listaResultados.add(resultado);
		}
		maxResultado = Collections.max(listaResultados);

		logger.info("El maximo resultado de CPD - PPP es:   " + maxResultado);

		if (maxResultado >= 10) {
			logger.info("Entra a llenar listas con capacidad mayor de  10");
			llenarListaSegurosCincoNpam(responseTO, capPagoDisp);
		}
		if (maxResultado >= 5 && maxResultado <= 9) {
			logger.info("Entra a llenar  listas con capacidad menor= 9");
			llenarListaSegurosSoloCincoNpam(responseTO, capPagoDisp);
		}

	}
	
	private void llenarListaSegurosCincoNpam(EscalerasResponseTo responseTO, int capPagoDisp) {
		logger.info("LA CPD que se recibe para este primer metodo es " + capPagoDisp);

		for (AbonoNormal abono : responseTO.getListaCotizador()) {
			int result = capPagoDisp - (abono.getPagoPuntualPromo());
			if (result >= 5 & result < 10) {
				abono.setListaOfertas(seguroImple.cargaListaSegurosCreditoLimitadoNPAM(9, abono.getPagoPuntual(),abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo(),abono.getPagoPuntualPromoEspecial(), abono.getPagoNormalPromoEspecial()));
			}
			if (result >= 10 & result <= 14) {
				abono.setListaOfertas(seguroImple.cargaListaSegurosCreditoLimitadoNPAM(10, abono.getPagoPuntual(),
						abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo(),abono.getPagoPuntualPromoEspecial(), abono.getPagoNormalPromoEspecial()));
			} else if (result >= 15 & result <= 19) {
				abono.setListaOfertas(seguroImple.cargaListaSegurosCreditoLimitadoNPAM(15, abono.getPagoPuntual(),
						abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo(),abono.getPagoPuntualPromoEspecial(), abono.getPagoNormalPromoEspecial()));
			} else if (result >= 20 & result <= 24) {

				abono.setListaOfertas(seguroImple.cargaListaSegurosCreditoLimitadoNPAM(20, abono.getPagoPuntual(),
						abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo(),abono.getPagoPuntualPromoEspecial(), abono.getPagoNormalPromoEspecial()));
			} else if (result >= 25 & result <= 29) {

				abono.setListaOfertas(seguroImple.cargaListaSegurosCreditoLimitadoNPAM(25, abono.getPagoPuntual(),
						abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo(),abono.getPagoPuntualPromoEspecial(), abono.getPagoNormalPromoEspecial()));
			} else if (result >= 30) {
				abono.setListaOfertas(seguroImple.cargaListaSegurosCreditoLimitadoNPAM(30, abono.getPagoPuntual(),
						abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo(),abono.getPagoPuntualPromoEspecial(), abono.getPagoNormalPromoEspecial()));
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

	public void llenarListaSegurosSoloCincoNpam(EscalerasResponseTo responseTO, int capPagoDisp) {
		for (AbonoNormal abono : responseTO.getListaCotizador()) {
			int result = capPagoDisp - (abono.getPagoPuntualPromo());
			if (result >= 5 & result < 10) {
				abono.setListaOfertas(seguroImple.cargaListaSegurosCreditoCincoPesosNPAM(9, abono.getPagoPuntual(),
						abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo(),abono.getPagoPuntualPromoEspecial(), abono.getPagoNormalPromoEspecial()));
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
	
	
	
	
	
	
	
	
	
	/*Se meten validaciones de credito_renovacion personalizado */
	public void evaluaListaSegurosCreditoRenovacion(CreditRunResponseCotizador responseTO, int capPagoDisp) {
		//logger.info("La capacidad de pago recibido en el metodo es  " + capPagoDisp);
		int resultado;
		int maxResultado;
		int resultadoSeguro = seguroImple.obtenSeguroARenovar();
		ArrayList<Integer> listaResultados = new ArrayList<Integer>();

		for (Abono abono : responseTO.getListaCotizador()) {
			resultado = (capPagoDisp + resultadoSeguro) - (abono.getPagoPuntual());
			listaResultados.add(resultado);
		}
		maxResultado = Collections.max(listaResultados);
		logger.info("EL VALORSISIMO DE CREDITO NORMAL es " + maxResultado);
		

		if (maxResultado >= 10) {
			logger.info("Vamos a llenar los seguros de renovacion si vienen en la lista pero descartamos el de $5");
			llenalistaSegurosAOfertar(responseTO, capPagoDisp);

		}
		if (maxResultado >= 5 && maxResultado < 10) {
			logger.info("Vamos a llenar los seguros de renovacion si vienen en la lista pero por CPD es menor de $10");
			llenarListaSegurosSoloCincoAOfertar(responseTO, capPagoDisp);
		}
		if (maxResultado < 5) {
			logger.info("Vamos a llenar los seguros de renovacion si vienen en la lista pero por CPD es menor de $5");
			llenarListaSegurosSoloCincoAOfertar(responseTO, capPagoDisp);
		}
	}
	
	/*Se mete la logica para seguros a renovar personalizados*/
	private void llenalistaSegurosAOfertar(CreditRunResponseCotizador responseTO, int capPagoDisp) {
		//logger.info("Entramos al metodo llenalistaSegurosAOfertar");
		
		RespuestaSeguro respuestaSeguro = seguroImple.obtenCodigosSeguros();
		
		
		int resultadoSeguro = seguroImple.obtenSeguroARenovar();
		logger.info("Se obtubo el resultado de resultadoSeguro " + resultadoSeguro);

		for (Abono abono : responseTO.getListaCotizador()) {

			int result = (capPagoDisp + resultadoSeguro) - (abono.getPagoPuntual());

			if (result >= 5 & result < 10) {
				abono.setListaOfertas(seguroImple.cargaListaSegurosCreditFiltro(9, abono.getPagoPuntual(),abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo()));
			}	
			else if(result >= 10 & result <= 14) {
				abono.setListaOfertas(seguroImple.cargaListaSegurosCreditFiltro(10, abono.getPagoPuntual(),abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo()));
			} else if (result >= 15 & result <= 19) {
				abono.setListaOfertas(seguroImple.cargaListaSegurosCreditFiltro(15, abono.getPagoPuntual(),abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo()));
			} else if (result >= 20 & result <= 24) {
				abono.setListaOfertas(seguroImple.cargaListaSegurosCreditFiltro(20, abono.getPagoPuntual(),abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo()));
			} else if (result >= 25 & result <= 29) {
				abono.setListaOfertas(seguroImple.cargaListaSegurosCreditFiltro(25, abono.getPagoPuntual(),abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo()));
			} else if (result >= 30) {
				abono.setListaOfertas(seguroImple.cargaListaSegurosCreditFiltro(30, abono.getPagoPuntual(),abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo()));
			}
			abono.setSeguroRenovar(seguroImple.cargaListaSegurosRenovacionPersonalizada(abono.getPagoPuntual(),abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo()));

			if ((abono.isAplicaSeguro() == false || abono.isAplicaSeguro() == true)	&& (abono.getListaOfertas() == null || abono.getListaOfertas().size() == 0)) {
				abono.setAplicaSeguro(false);
			} else {
				responseTO.setAptoSeguro(true);
				abono.setAplicaSeguro(true);
			}
			if ((abono.getSeguroRenovar() == null || abono.getSeguroRenovar().size() == 0)) {
				abono.setSeguroRenovacion(false);
			} else {
				abono.setSeguroRenovacion(true);
				abono.setAplicaSeguro(true);
				responseTO.setAptoSeguro(true);
			}
			responseTO.setRespuestaSeguro(respuestaSeguro);
			
		}
	}
	
	private void llenarListaSegurosSoloCincoAOfertar(CreditRunResponseCotizador responseTO, int capPagoDisp) {
		
		logger.info("Se obtubo el resultado de resultadoSeguro " + capPagoDisp);
		
		
		RespuestaSeguro respuestaSeguro = seguroImple.obtenCodigosSeguros();
		RespuestaBase respuestaBase = new RespuestaBase();
		
		int maxResultado;
		ArrayList<Integer> listaResultados =  new ArrayList<Integer>();
		
		for (Abono abono : responseTO.getListaCotizador()) {
			int result = capPagoDisp - (abono.getPagoPuntual());
			listaResultados.add(result);
		}
		maxResultado = Collections.max(listaResultados);

		
		if (maxResultado < 5) {
			for (Abono abono : responseTO.getListaCotizador()) {
				int result = capPagoDisp - (abono.getPagoPuntual());
				
				
				if ((abono.isAplicaSeguro() == false || abono.isAplicaSeguro() == true)&& (abono.getListaOfertas() == null || abono.getListaOfertas().size() == 0)) {
					abono.setAplicaSeguro(false);

				} else {
					responseTO.setAptoSeguro(true);
					abono.setAplicaSeguro(true);
				}
				if ((abono.getSeguroRenovar() == null || abono.getSeguroRenovar().size() == 0)) {
					abono.setSeguroRenovacion(false);
				} else {
					abono.setSeguroRenovacion(true);
					abono.setAplicaSeguro(true);
					responseTO.setAptoSeguro(true);
				}
				respuestaBase.setCodigo("-3");
				respuestaBase.setDescripcion("No se ofertaron Seguros por la capacidad de pago disponible");
				
				respuestaSeguro.setRespuestaBase(respuestaBase);
				respuestaSeguro.setTipoOfertaRespuesta(-3);
				
				responseTO.setRespuestaSeguro(respuestaSeguro);
				
			}	
			
		}
		else {
			for (Abono abono : responseTO.getListaCotizador()) {
				int result = capPagoDisp - (abono.getPagoPuntual());
				logger.info("RSUL DE MENOR DEL METODO  " + result );
				
				//MAXIMO
				if (result >= 5 & result < 10) {
					abono.setListaOfertas(seguroImple.cargaListaSegurosCreditFiltroCincoPesos(9, abono.getPagoPuntual(),abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo()));
				}
				abono.setSeguroRenovar(seguroImple.cargaListaSegurosRenovacionPersonalizada(abono.getPagoPuntual(),abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo()));
				if ((abono.isAplicaSeguro() == false || abono.isAplicaSeguro() == true)&& (abono.getListaOfertas() == null || abono.getListaOfertas().size() == 0)) {
					abono.setAplicaSeguro(false);

				} else {
					responseTO.setAptoSeguro(true);
					abono.setAplicaSeguro(true);
				}
				if ((abono.getSeguroRenovar() == null || abono.getSeguroRenovar().size() == 0)) {
					abono.setSeguroRenovacion(false);
				} else {
					abono.setSeguroRenovacion(true);
					abono.setAplicaSeguro(true);
					responseTO.setAptoSeguro(true);
				}
				responseTO.setRespuestaSeguro(respuestaSeguro);
			}	
		}
	}
	
	
	
	/*Se mete la logica para validar seguros de renovacion personalizada NPAM*/
	public void evaluaListaSegurosCreditoRenovacionNPAM(EscalerasResponseTo responseTO, int capPagoDisp) {
		//logger.info("La capacidad de pago recibido en el metodo es  " + capPagoDisp);
		int resultado;
		int maxResultado;
		int resultadoSeguro = seguroImple.obtenSeguroARenovar();
		ArrayList<Integer> listaResultados = new ArrayList<Integer>();

		for (AbonoNormal abono : responseTO.getListaCotizador()) {
			//resultado = capPagoDisp - (abono.getPagoPuntualPromo());
			resultado = (capPagoDisp + resultadoSeguro) - (abono.getPagoPuntualPromo());
			listaResultados.add(resultado);
		}
		maxResultado = Collections.max(listaResultados);
		//logger.info("EL VALORSISIMO DE NAPAM es " + maxResultado);

		if (maxResultado >= 10) {
		//	logger.info("Vamos a llenar los seguros de renovacion NPAM si vienen en la lista pero descartamos el de $5");
			llenalistaSegurosAOfertarNPAM(responseTO, capPagoDisp);

		}
		if (maxResultado >= 5 && maxResultado < 10) {
			logger.info("Vamos a llenar los seguros de renovacionNPAM si vienen en la lista pero por CPD es menor de $10");
			llenarListaSegurosSoloCincoAOfertarNPAM(responseTO, capPagoDisp);
		}
	}
	
	/*Se mete la logica para seguros a renovar personalizados*/
	public void llenalistaSegurosAOfertarNPAM(EscalerasResponseTo responseTO, int capPagoDisp) {
		//logger.info("Entramos al metodo " + "llenalistaSegurosAOfertarNPAM");

		int resultadoSeguro = seguroImple.obtenSeguroARenovar();
		//logger.info("Se obtubo el resultado de resultadoSeguro " + resultadoSeguro);

		for (AbonoNormal abono : responseTO.getListaCotizador()) {

			int result = (capPagoDisp + resultadoSeguro) - (abono.getPagoPuntualPromo());

			if (result >= 10 & result <= 14) {
				abono.setListaOfertas(seguroImple.cargaListaSegurosCreditFiltroNPAM(10, abono.getPagoPuntual(),abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo(),abono.getPagoPuntualPromoEspecial(),abono.getPagoNormalPromoEspecial()));
			} else if (result >= 15 & result <= 19) {
				abono.setListaOfertas(seguroImple.cargaListaSegurosCreditFiltroNPAM(15, abono.getPagoPuntual(),abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo(),abono.getPagoPuntualPromoEspecial(),abono.getPagoNormalPromoEspecial()));
			} else if (result >= 20 & result <= 24) {
				abono.setListaOfertas(seguroImple.cargaListaSegurosCreditFiltroNPAM(20, abono.getPagoPuntual(),abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo(),abono.getPagoPuntualPromoEspecial(),abono.getPagoNormalPromoEspecial()));
			} else if (result >= 25 & result <= 29) {
				abono.setListaOfertas(seguroImple.cargaListaSegurosCreditFiltroNPAM(25, abono.getPagoPuntual(),abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo(),abono.getPagoPuntualPromoEspecial(),abono.getPagoNormalPromoEspecial()));
			} else if (result >= 30) {
				abono.setListaOfertas(seguroImple.cargaListaSegurosCreditFiltroNPAM(30, abono.getPagoPuntual(),abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo(),abono.getPagoPuntualPromoEspecial(),abono.getPagoNormalPromoEspecial()));
			}
			abono.setSeguroRenovar(seguroImple.cargaListaSegurosRenovacionPersonalizadaNPAM(abono.getPagoPuntual(),abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo(),abono.getPagoPuntualPromoEspecial(),abono.getPagoNormalPromoEspecial()));

			if ((abono.isAplicaSeguro() == false || abono.isAplicaSeguro() == true)	&& (abono.getListaOfertas() == null || abono.getListaOfertas().size() == 0)) {
				abono.setAplicaSeguro(false);
			} else {
				responseTO.setAptoSeguro(true);
				abono.setAplicaSeguro(true);
			}
			if ((abono.getSeguroRenovar() == null || abono.getSeguroRenovar().size() == 0)) {
				abono.setSeguroRenovacion(false);
			} else {
				abono.setSeguroRenovacion(true);
				abono.setAplicaSeguro(true);
				responseTO.setAptoSeguro(true);
			}
		}
	}
	
	private void llenarListaSegurosSoloCincoAOfertarNPAM(EscalerasResponseTo responseTO, int capPagoDisp) {
		
		//logger.info("Se obtubo el resultado de resultadoSeguro " + resultadoSeguro);

		for (AbonoNormal abono : responseTO.getListaCotizador()) {
			int result = capPagoDisp - (abono.getPagoPuntualPromo());
			
			if (result >= 5 & result < 10) {
				abono.setListaOfertas(seguroImple.cargaListaSegurosCreditFiltroCincoPesosNPAM(9, abono.getPagoPuntual(),abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo(),abono.getPagoPuntualPromoEspecial(),abono.getPagoNormalPromoEspecial()));
			
			}
			abono.setSeguroRenovar(seguroImple.cargaListaSegurosRenovacionPersonalizadaNPAM(abono.getPagoPuntual(),abono.getPagoNormal(), abono.getPagoPuntualPromo(), abono.getPagoNormalPromo(),abono.getPagoPuntualPromoEspecial(),abono.getPagoNormalPromoEspecial()));
			if ((abono.isAplicaSeguro() == false || abono.isAplicaSeguro() == true)&& (abono.getListaOfertas() == null || abono.getListaOfertas().size() == 0)) {
				abono.setAplicaSeguro(false);

			} else {
				responseTO.setAptoSeguro(true);
				abono.setAplicaSeguro(true);
			}
			if ((abono.getSeguroRenovar() == null || abono.getSeguroRenovar().size() == 0)) {
				abono.setSeguroRenovacion(false);
			} else {
				abono.setSeguroRenovacion(true);
				abono.setAplicaSeguro(true);
				responseTO.setAptoSeguro(true);
			}
		}
	}

}