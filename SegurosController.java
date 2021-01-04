package com.bancoazteca.bdm.cotizador.BDMCotizacion.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bancoazteca.bdm.cotizador.BDMCotizacion.business.ObtenListaSeguros;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.business.OperacionTransferir;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.business.SegurosNoLigados.SegurosNoLigados;

import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.ProductoSeguroRequest;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SeguroRequest;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.TransferenciaRequestTO;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.Request.ListaSegurosRequestTO;

@RestController
@RequestMapping(value = "api/bdm/seguros/")
public class SegurosController {

	@Autowired SegurosNoLigados seguroNoLigado;
	@Autowired OperacionTransferir operacionTransferir;
	@Autowired ObtenListaSeguros obtenListaSeguros;
	
	/*
	 * Se obtendra la cookie para consumir los demas servicios de Seguros No Ligados
	 */
	@RequestMapping(value = "/obtenSeguro", method = RequestMethod.POST)
	public ResponseEntity<Object> obtenCookie(@RequestBody String requestCifrado,
			HttpServletRequest httpServletRequest) {
		return (ResponseEntity<Object>) seguroNoLigado.obtenCookie(new SeguroRequest(), requestCifrado);

	}

	/*
	 * Se obtiene los posibles productos de seguros para ofertarlos a los clientes
	 */
	@RequestMapping(value = "/obtenSeguros", method = RequestMethod.POST)
	public ResponseEntity<Object> obtenSeguros(@RequestBody String requestCifrado,
			HttpServletRequest httpservletRequest) {
		return (ResponseEntity<Object>) seguroNoLigado.obtenSeguros(new ProductoSeguroRequest(), requestCifrado);
	}

	/* Se realizara conexion como DaaP */
	@RequestMapping(value = "realtimeejecutaenvio", method = RequestMethod.POST)
	public ResponseEntity<Object> realTimeEjecutaEnvio(@RequestBody String requestCifrado,
			HttpServletRequest httpServletRequest) {
		return (ResponseEntity<Object>) operacionTransferir.operacionTransferir(new TransferenciaRequestTO(),
				requestCifrado);

	}
	
	
	/**
	 * Se obtendra la lista se seguros.
	 */
	
	@RequestMapping (value =  "obtenListaSeguros" , method = RequestMethod.POST)
	public ResponseEntity<Object> obtenListaSeguros (@RequestBody String requestCifrado, HttpServletRequest httpServletRequest){
		return (ResponseEntity  <Object>) obtenListaSeguros.obtenListaSeguros(new ListaSegurosRequestTO(), requestCifrado);
	}
}