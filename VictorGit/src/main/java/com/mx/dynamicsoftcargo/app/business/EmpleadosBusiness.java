package com.mx.dynamicsoftcargo.app.business;

import org.springframework.boot.logging.Slf4JLoggingSystem;
import org.springframework.stereotype.Component;

import com.mx.dynamicsoftcargo.entity.Antiguedad;
import com.mx.dynamicsoftcargo.entity.Clave;
import com.mx.dynamicsoftcargo.entity.Empleado;

@Component

public class EmpleadosBusiness {
	public Empleado verificandoEmpleados (String nombre,int antiguedad, int clave) {
		Empleado empleado = new Empleado();
		 Clave claves = new Clave();
		 Antiguedad antiguedades = new Antiguedad();
		 ConstantesCoca constantes = new ConstantesCoca ();
		 int claveUno = 1;
		 int claveDos = 2;
		 int claveTres = 3;
		 System.out.println(constantes.nombreTrabajador);
		 empleado.setNombre(nombre);
		 System.out.println(constantes.claveDepartamento);
		 empleado.setClave(clave);
		 System.out.println(constantes.añosAntiguedad);
		 empleado.setAntiguedad(antiguedad);
		 
		// if (empleado.getClave())
		 
		 return empleado;
		 
		 
		 
		 
		 
				 
	}
	

}
