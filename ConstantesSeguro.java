package com.bancoazteca.bdm.cotizador.BDMCotizacion.business;

public class ConstantesSeguro {
private ConstantesSeguro() {}
	
	public static final String SEGUROS_PROPERTIES = "credito.properties";
	
	public static final int TIMEOUT_5_SEG = 5000;
	public static final int TIMEOUT_10_SEG = 10000;
	public static final int TIMEOUT_15_SEG = 15000;
	public static final int TIMEOUT_30_SEG = 30000;
	public static final int TIMEOUT_50_SEG = 50000;
	public static final int TIMEOUT_110_SEG = 110000;
	public static final int TIMEOUT_120_SEG = 120000;
	
	public static final String FECHA_FORMATO_D_M_Y = "dd/MM/yyyy";
	
	//Codigos de exito
	public static final int CODIGO_EXITO_JSON = 2;
	public static final String DATA_NOTNULL_JSON = "data";
		
	//CONSTANTES QUE SOLO FUNCIONARIAN AL ENCONTRAR LA BANDERA DE AMBIENTE DESARROLLO
	public static final String BANDERA_DESARROLLO_O_PRODUCCION = "envio_correo_produccion";
	
	//Bandera ambiente desarrollo
	public static final String AMBIENTE_DESARROLLO = "2";
	
	public static final String DS_TARJETA = "credito.properties.url.servicio.consulta.cu.tarjeta.azteca.ds.tarjeta";
	public static final String USER = "credito.properties.url.servicio.consulta.cu.tarjeta.azteca.user" ;
	public static final String PASS = "credito.properties.url.servicio.consulta.cu.tarjeta.azteca.pws" ;
	public static final String PATH_TARJETA_AZTECA = "credito.properties.url.servicio.consulta.cu.tarjeta.azteca";
	//public static final String PATH_SEGURO_VIDAMAX = "credito.properties.url.servicio.obtiene.seguro.vidamax";
	public static final String PATH_SEGURO_VIDAMAX = "http://10.81.27.220/SegurosServicios/VidamaxUnificada";
	//public static final String ENCABEZADO_VIDAMAX ="credito.properties.url.servicio.obtiene.seguro.vidamax.encabezado";
	public static final String ENCABEZADO_VIDAMAX ="http://www.segurosazteca.com.mx/WSOfertaVidamaxUnificada";
	
	public static final String WSOF = "wsof";
	public static final String REQUEST_VIDAMAX = "ofertaVidamaxUnificadaRequest";
	public static final String PETICION_JSON = "peticionJSON";
	public static final String SOAP_ACTION = "SOAPAction";
	public static final String RESPUESTA_JSON = "respuestaJSON";
	
	public static final String IDENTIFICADOR_DATOS_BASICOS = "SYS-BAZDIGITAL";
	
	/*Codigos de respuesta en las peticiones de seguros*/
	public static final String CODIGO_INCIDENCIA = "-1";
	public static final String INCIDENCIA_SERVICIO_SEGUROS = "El servicio de seguros tuvo alguna incidencia";
	public static final Integer INCIDENCIA_SERVICIO_SEGURO = -1;
	
	
	public static final String CODIGO_RESPUESTA = "-2";
	public static final String INCIDENCIA_EN_RESPUESTA = "El servicio de seguros si contesto pero sin informacion";
	public static final Integer INCIDENCIA_RESPUESTA_CODIGO = -2;
	
	

	
	



}
