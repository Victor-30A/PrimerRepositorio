package com.mx.dynamicsoftcargo.app.business;

import org.springframework.stereotype.Component;

@Component

public class CalificacionesBusiness {
	public String calculoCalificaciones ( int calificacion) {
		if (calificacion < 6 ) {
			return "El alumno repeobo";
		}
		else if (calificacion == 6) {
			return "El alumno panzo";
		}
		else {
			return "K CHIDOTE";
		}
		
	}
	

}
