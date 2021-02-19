package com.bancoazteca.bdm.cotizador.BDMCotizacion.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import com.bancoazteca.bdm.cotizador.BDMCotizacion.business.GeneraCreditRun;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.business.GeneraCreditRunRenovacion;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.business.GeneraCuerpoXml;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.business.GeneraTransmisionSeguros;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.business.GeneraTransmisionSegurosRenovacion;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.business.GeneraSobrePrecio;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.CotizadorRequest;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SeguroVidamax;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SobrePrecio;



@RestController
@RequestMapping(value = "/cotizador")
public class CotizadorSegurosController {
	@Autowired
	private GeneraCuerpoXml cuerpoXml;
	
	@Autowired 
	private GeneraSobrePrecio sobrePrecio;
	
	@Autowired
	private GeneraTransmisionSeguros generaSeguro;
	
	
	
	@Autowired 
	private GeneraCreditRun cotizador;
	
	@Autowired
	private GeneraCreditRunRenovacion cotizadorRenovacion;
	
	@Autowired
	GeneraTransmisionSegurosRenovacion  generaSeguroRenovacion;
	
	        
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/seguras", method = RequestMethod.POST)
	public ResponseEntity<String> SobrePrecio(@RequestBody String request){
		return (ResponseEntity<String>) cuerpoXml.ObtenerOfertasSeguras(new SobrePrecio(), request);
	}
	
	//consultaSobrePrecio
	@RequestMapping(value = "/consultaSobrePrecio", method = RequestMethod.POST)
	public ResponseEntity<String> SobrePrecios(@RequestBody String request){
		return (ResponseEntity<String>) sobrePrecio.consultaSobrePrecio(new SobrePrecio(), request);
	}
	

	@RequestMapping(value = "/seguros", method = RequestMethod.POST)
	public ResponseEntity<String> OfertasSeguros(@RequestBody String request){
		return (ResponseEntity<String>) cuerpoXml.ObtenerOfertasSeguros(new SeguroVidamax(), request);	
	}
	
	@SuppressWarnings("unchecked")//Lista que trae Seguros-Recompra  (Se metio Metodo de credit Run Basico)
	@RequestMapping(value = "/obtenListaSeguros", method = RequestMethod.POST)
	public ResponseEntity<Object> recompraCredito(@RequestBody String requestCifrado, HttpServletRequest httpServletRequest){
		return (ResponseEntity<Object>) generaSeguro.ObtenListaSeguros(new SeguroVidamax(), requestCifrado);
	}
	@SuppressWarnings("unchecked")//Lista que trae Seguros-Renovacion
	@RequestMapping (value = "/obtenListaSeguosRenovacion" , method = RequestMethod.POST)
	public ResponseEntity<Object> renovacionCredito(@RequestBody String requestCifrado , HttpServletRequest httpServletRequest){
		return(ResponseEntity<Object>) generaSeguroRenovacion.ObtenListaSegurosRenovacion(new SeguroVidamax(), requestCifrado);
	}
	
	/*Se obtiene cotizador Credit Run con seguros en recompra*/
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/obtenerCotizadorRun" , method = RequestMethod.POST)
	public ResponseEntity<Object> obtenerCotizador(@RequestBody String requestCifrado, HttpServletRequest httpServletRequest){
	  return (ResponseEntity<Object>) cotizador.cotizador(new CotizadorRequest(),  requestCifrado);
	}
	
	/*Se obtiene lista cotizador de Renovacion Credit Run*/
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/obtenerCotizadorRenovacionCreditRun" , method = RequestMethod.POST)
	public ResponseEntity<Object> obtenerCotizadorRenovacion(@RequestBody String requestCifrado, HttpServletRequest httpServletRequest){
	  return (ResponseEntity<Object>) cotizadorRenovacion.cotizador(new CotizadorRequest(),  requestCifrado);
	}
	
}