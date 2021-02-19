package com.bancoazteca.bdm.cotizador.BDMCotizacion.business;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Validations {
	
private static Logger log = LoggerFactory.getLogger(Validations.class);
	
	public static boolean isDecimal(String data) {
		boolean flag = false;
		if (!isNullOrEmpty(data)) {
			flag = data.trim().matches("^((\\d+)|(\\.(\\d+))|(\\d+\\.\\d+))$");

		}
		return flag;
	}

	public static boolean isNullOrEmpty(Object data) {
		boolean flag = false;
		if (isNull(data)) {
			flag = true;
		} else {
			if (String.valueOf(data).trim().isEmpty()) {
				flag = true;
			}
		}
		return flag;
	}

	public static boolean isNullOrEmptyArray(Object data) {
		boolean flag = false;
		if (isNull(data)) {
			flag = true;
		} else {
			String[] stringData = (String[]) data;
			if (stringData.length == 0) {
				flag = true;
			} else if (stringData[0].trim().isEmpty()) {
				flag = true;
			}
		}
		return flag;
	}
	
	public static boolean isNullOrEmptyGeo(Object data){
		boolean flag = false;
		if (isNull(data)) {
			flag = true;
		}else{
			if( data instanceof double[] ){
				//Tratar el dato como un double[]
				double[] doubleData = (double[]) data;
				if(doubleData.length == 2){
					if(doubleData[0] != 0 && doubleData[1] != 0){
						if( validarRangoCoordenada(doubleData[0],doubleData[1]) ) {
							flag = false;
						}else{
							flag = true;
						}
					}else{
						flag = true;
					}
				}else{
					flag = true;
				}
			}
			if( data instanceof Double[] ){
				//Tratar el dato como un Double[]
				Double[] doubleData = (Double[]) data;
				if(doubleData.length == 2){
					if(doubleData[0] != null && doubleData[1] != null && doubleData[0] != 0 && doubleData[1] != 0){
						if( validarRangoCoordenada(doubleData[0],doubleData[1]) ) {
							flag = false;
						}else{
							flag = true;
						}
					}else{
						flag = true;
					}
				}else{
					flag = true;
				}
			}
			
		}
		return flag;
	}
	
	public static boolean esIncorrectaLatitud(Object data){
		boolean flag = false;
		if (isNull(data)) {
			flag = true;
		}else{
			try{
				double latitud = (double)data;
				if((latitud > 90 || latitud  < -90) || latitud == 0.0)
					flag =  true;
			}catch(NumberFormatException e){
				flag = true;
			}
		}
		return flag;
	}
	
	public static boolean esIncorrectaLongitud(Object data){
		boolean flag = false;
		if (isNull(data)) {
			flag = true;
		}else{
			try{
				double longitud = (double)data;
				if((longitud > 180 || longitud < -180) || longitud == 0)
					flag =  true;
			}catch(NumberFormatException e){
				flag = true;
			}
		}
		return flag;
	}

	public static boolean isNull(Object data) {
		boolean flag = false;
		if (data == null) {
			flag = true;
		}
		return flag;
	}

	public static boolean textoNombre(Object attribute) {
		boolean flag = false;
		if (!isNullOrEmpty(attribute)) {
			String stringData = String.valueOf(attribute);
			flag = stringData.trim().matches("^([A-Za-z ��������������]+)$");

		}
		return !flag;
	}

	public static boolean maxSizeText(Object attribute, int maxSize) {
		boolean flag = false;
		if (!isNullOrEmpty(attribute)) {
			String stringData = String.valueOf(attribute);
			flag = stringData.trim().length() <= maxSize;

		}
		return !flag;
	}

	public static boolean exactSizeText(Object attribute, int exactSize) {
		boolean flag = false;
		if (!isNullOrEmpty(attribute)) {
			String stringData = String.valueOf(attribute);
			flag = stringData.trim().length() == exactSize;

		}
		return flag;
	}

	public static boolean startWith(String data, String inicioCadena) {
		boolean flag = false;
		if(!isNullOrEmpty(data)){
			if(data.startsWith(inicioCadena))
				flag = true;
		}
		return flag;
	}

	public static boolean validaFecha(Date fecha) {
		Calendar fechaRecibida = Calendar.getInstance();
		boolean flag = false;
		if(!isNullOrEmpty(fecha)){
			fechaRecibida.setTime(fecha);
			if(fechaRecibida.before(Calendar.getInstance()))
				flag = true;
		}
		return flag;
	}

	public static boolean validateIP(Object attribute) {
		boolean flag = false;
		try {
			flag = Pattern.matches(
					"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$",
					attribute.toString());
		} catch (PatternSyntaxException e) {
			log.error(LogUtils.printStackTrace(e));
		}
		return flag;
	}

	public static boolean isNumerico(Object data) {
		try {
			return Pattern.matches("\\d*", data.toString());
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean validateRegExpression(String regularExpression, Object value) {

		boolean flag = false;
		try {
			flag = Pattern.matches(regularExpression, value.toString());

		} catch (PatternSyntaxException e) {
			log.error(LogUtils.printStackTrace(e));
		}

		return flag;

	}
	
	public static boolean validaFormatoFecha(Object fecha, String format)
	{
		SimpleDateFormat formato=new SimpleDateFormat(format);
	
		try
		{
			formato.setLenient(false);
			formato.parse(fecha.toString());
		}catch(ParseException e){
			return false;
		}
	
		return true;
	}
	
	public static boolean validateEmail(Object correoElectronico)
	{
		boolean flag = false;
		
		if (correoElectronico != null && correoElectronico.toString().trim().length()!=0)
		{
			try 
			{
				flag = Pattern.matches("^[a-zA-Z0-9\\._](-|[a-zA-Z0-9\\._])*[a-zA-Z0-9\\._]@[a-zA-Z0-9](-|[a-zA-Z0-9\\._])*[a-zA-Z0-9][\\._][a-zA-Z](-|[a-zA-Z0-9\\._])*[a-zA-Z0-9]$", correoElectronico.toString().trim());
			} catch (PatternSyntaxException e) {
				return false;
			}
		}
		return flag;
	}
	
	private static boolean validarRangoCoordenada(double longitud, double latitud){
		if(longitud > 180 || longitud < -180)
			return false;
		if(latitud > 90 || latitud  < -90)
			return false;
		return true;
	}

}
