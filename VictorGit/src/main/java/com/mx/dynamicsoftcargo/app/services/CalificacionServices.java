package com.mx.dynamicsoftcargo.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mx.dynamicsoftcargo.app.business.CalificacionesBusiness;
import com.mx.dynamicsoftcargo.entity.Calificaciones;

@RestController


public class CalificacionServices {
	@Autowired CalificacionesBusiness calificacioness ; 
	@GetMapping ("/mostrarCalificaciones")
	public String mostrarCalificaciones (@RequestParam int calificacion ) {
		int calihhjk;
		Calificaciones calificaciones = new Calificaciones();
		calificaciones.setCalificacion(calificacion);
		calihhjk = calificaciones.getCalificacion();
		
		return calificacioness.calculoCalificaciones(calihhjk);
	}
	
	

} 
