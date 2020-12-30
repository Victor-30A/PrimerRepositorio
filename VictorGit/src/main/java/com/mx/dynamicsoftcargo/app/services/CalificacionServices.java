package com.mx.dynamicsoftcargo.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mx.dynamicsoftcargo.app.business.CalificacionesBusiness;

@RestController


public class CalificacionServices {
	@Autowired CalificacionesBusiness calificaciones ; 
	@GetMapping ("/mostrarCalificaciones")
	public String mostrarCalificaciones (@RequestParam int calificacion ) {
		return calificaciones.calculoCalificaciones(calificacion);
	}
	
	

}
