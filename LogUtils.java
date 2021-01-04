package com.bancoazteca.bdm.cotizador.BDMCotizacion.business;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class LogUtils {
	private static final String tiempoRespuesta="tiempoRespuesta";
	private static Logger log = LoggerFactory.getLogger(LogUtils.class);
	
	/**
	 * Metodo que devuelve el StackTrace formateado para imprimir en log.
	 * @param e Throwable del cual se obtendra el stackTrace a imprimir.
	 * @return String de mensaje formateado para imprimir en log.
	 */
	public static String printStackTrace(Throwable e){
		String message = "";
		if(!Validations.isNull(e)){
			message = messageError(e);
		}
		return message;
	}
	
	public static void agregaTiempoEjecucion(String nombreServicio, String tiempo, String metodo, boolean thread) {
		nombreServicio = (nombreServicio != null) ? nombreServicio.toUpperCase() : "";
		metodo = (metodo != null) ? metodo.toUpperCase().trim() : "";
		if (thread) {
			log.info(metodo+", "+nombreServicio + ": Tiempo: " + tiempo + " Milisegundos ");
			return;
		}

		try {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(metodo);
			stringBuilder.append(", ");
			stringBuilder.append(nombreServicio).append(":");
			stringBuilder.append(" Tiempo: ");
			stringBuilder.append(tiempo);
			stringBuilder.append(" Milisegundos ");

			HttpServletRequest request = null;
			if (RequestContextHolder.getRequestAttributes() != null) {
				request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			}
			if (request != null) {
				if (request.getAttribute(tiempoRespuesta) == null) {
					request.setAttribute(tiempoRespuesta, stringBuilder);
				} else {
					StringBuilder stringBuilder2 = (StringBuilder) request.getAttribute(tiempoRespuesta);
					stringBuilder2.append(stringBuilder);
					request.setAttribute(tiempoRespuesta, stringBuilder2);
				}
			}else{
				log.info("Tiempo de respuesta Servicio:.. "+ metodo + ", " + nombreServicio + ": Tiempo: " + tiempo + " Milisegundos ");
			}
		} catch (Exception e) {
			log.info("No fue posible agregar el tiempo del servicio: " + metodo + ", " + nombreServicio + ": Tiempo: " + tiempo + " Milisegundos ");
			e.getMessage();
		}
	}
	@Deprecated
	public static void agregaTiempoEjecucion(String nombreServicio, String tiempo){
		agregaTiempoEjecucion(nombreServicio, tiempo, "", false);
	}
	@Deprecated
	public static void agregaTiempoEjecucion(String nombreServicio, long tiempoInicio,long tiempoFin){
		agregaTiempoEjecucion(nombreServicio, String.valueOf(tiempoFin-tiempoInicio));
	}
	
	/**
	 * @param e
	 * @return String
	 */
	private static String messageError(Throwable e) {
		
	    if(e == null) {
	      return "No hay informacion en la excepcion";
		}
	    StringBuilder sb = new StringBuilder();
		for (StackTraceElement element : e.getStackTrace()) {
			sb.append("\t at " + element.toString());
			sb.append("\n");
		}
		Throwable exTemp = e;
		while(exTemp.getCause() != null) {
			exTemp = exTemp.getCause();
			sb.append(" Caused by " );
			sb.append(exTemp.toString());
			sb.append("\n");
			for(StackTraceElement element : exTemp.getStackTrace()) {
				sb.append("\tat " );
				sb.append(element.toString());
				sb.append("\n");
			}
		}
		return e.toString()  + "\n" + sb.toString();
	}

}
