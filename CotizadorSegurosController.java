package com.bancoazteca.bdm.cotizador.BDMCotizacion.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bancoazteca.bdm.cotizador.BDMCotizacion.business.ConsumeServicioPOST;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.business.CotizadorCreditRun;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.business.CotizadorCreditRunCinco;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.business.GeneraCreditRun;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.business.GeneraCreditRunRenovacion;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.business.GeneraCuerpoXml;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.business.GeneraTransmisionSeguros;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.business.GeneraTransmisionSegurosRenovacion;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.business.ListaSegurosCreditRun;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.business.ObtenSeguro;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.business.RenovacionPersonalizado;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.business.GeneraSobrePrecio;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.CotizadorRequest;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.CreditRunCotizadorRequest;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SeguroVidamax;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.SobrePrecio;




@RestController
@RequestMapping(value = "/cotizador")
public class CotizadorSegurosController {
	
	@Autowired private GeneraCuerpoXml cuerpoXml;
	
	@Autowired private GeneraSobrePrecio sobrePrecio;
	
	@Autowired private GeneraTransmisionSeguros generaSeguro;
	
	@Autowired private GeneraCreditRun cotizador;
	
	@Autowired private GeneraCreditRunRenovacion cotizadorRenovacion;
	
	@Autowired GeneraTransmisionSegurosRenovacion  generaSeguroRenovacion;
	
	@Autowired 	ObtenSeguro obtenSeguro;
	
	 @Autowired CotizadorCreditRun cotizadorCreditRun;
	 
	@Autowired ListaSegurosCreditRun listaSegurosCreditRun;
	
	@Autowired CotizadorCreditRunCinco cotizadorCreditRunCinco;
	
	@Autowired RenovacionPersonalizado renovacionPersonalizado;
	
	@Autowired ConsumeServicioPOST consumeServicioPost;
	
	
	 
	 
	        
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
	
	
	/*Se obtienen seguros vidamax con token */
	@SuppressWarnings("unchecked")//Lista que trae Tokens
	@RequestMapping (value = "/obtenSegurosVidamaxToken" , method = RequestMethod.POST)
	public ResponseEntity<Object> segurosVidamax(@RequestBody String requestCifrado , HttpServletRequest httpServletRequest){
		return(ResponseEntity<Object>) obtenSeguro.obtenSeguroVidamax(new SeguroVidamax(), requestCifrado);
	}
	
	
	/*Se obtiene lista cotizador de Renovacion Credit Run*/
	@RequestMapping(value = "/listaSegurosCreditRun", method = RequestMethod.POST)
	public ResponseEntity<Object> listaSegurosCreditRun(@RequestBody String requestCifrado, HttpServletRequest httpServletRequest) {
		return (ResponseEntity<Object>)  listaSegurosCreditRun.listaSegurosCreditRun(new CotizadorRequest(), requestCifrado);
		
	}
	
	/*Se obtiene lista cotizador de Recompra Credit Run*/
	@RequestMapping(value = "/cotizadorcreditrun", method = RequestMethod.POST)
	public ResponseEntity<Object> flowCreditRun(@RequestBody String requestCifrado, HttpServletRequest httpServletRequest) {
		return (ResponseEntity<Object>)  cotizadorCreditRun.cotizador(new CreditRunCotizadorRequest(), requestCifrado);
		
	}
	

	/*Se obtiene lista cotizador de Renovacion Credit Run*/
	@RequestMapping(value = "/listaSegurosCreditRunCinco", method = RequestMethod.POST)
	public ResponseEntity<Object> listaSegurosCreditRunCinco(@RequestBody String requestCifrado, HttpServletRequest httpServletRequest) {
		return (ResponseEntity<Object>)  cotizadorCreditRunCinco.cotizador(new CreditRunCotizadorRequest(), requestCifrado);
		
	}
	
	
	/*Se obtendra las listas de cotizacion de renovacion para personalizar */
	@RequestMapping(value = "/renovacionPersonalizado" , method = RequestMethod.POST)
		public ResponseEntity<Object> renovacionPersonalizado(@RequestBody String requestCifrado, HttpServletRequest httpServletRequest){
			return (ResponseEntity<Object>) renovacionPersonalizado.renovacionPersonalizado(new CreditRunCotizadorRequest(),requestCifrado);
			
		}
	
	/*Se lee un Archivo*/
	@RequestMapping (value ="/leeArchivo" , method = RequestMethod.POST)
	public ResponseEntity<String> leeArchivo (@RequestBody String requestCifrado,HttpServletRequest httpServletRequest){
		String mensaje = "hola";
		return renovacionPersonalizado.leeArchivo();
	}
}