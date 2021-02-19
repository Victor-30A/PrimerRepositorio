package com.bancoazteca.bdm.cotizador.BDMCotizacion.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bancoazteca.bdm.cotizador.BDMCotizacion.business.StreamingBusinessImpl;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.StreamingRequest;


@RestController
@RequestMapping (value = "/programacionFuncional")
public class StreamingController {
	
	@Autowired StreamingBusinessImpl streamings;
	
	/*Pruebas de Streaming  PROGRAMACION FUNCIONAL*/
	@RequestMapping (value = "/streamings", method = RequestMethod.POST)
	public ResponseEntity<Object> streamings (@RequestBody String objetoJson , HttpServletRequest httpServletRequest){
		return(ResponseEntity<Object>) streamings.ejecutaStreamings (new StreamingRequest(),objetoJson);
	}

}
