package com.bancoazteca.bdm.cotizador.BDMCotizacion.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bancoazteca.bdm.cotizador.BDMCotizacion.business.SegurosNoLigados.CryptographyUtil;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.dao.SegurosNoLigadosDao;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.dao.UtilNoLigados;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.Request.ListaSegurosRequestTO;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.Response.ListaSegurosResponseTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.AuditHeaderBean;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.DescripcionPolizas;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.DescripcionSeguros;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.ListaPolizas;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.PolizaSegurosInfo;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.PropertiesTemplate;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.PropertiesThirdParty;
import com.google.gson.Gson;

import java.time.LocalDate;
import java.time.Period;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




@Service
public class ObtenListaSeguros {
	private static Logger logger = LoggerFactory.getLogger(ObtenListaSeguros.class);
	
	@Autowired SegurosNoLigadosDao dao;
	@Autowired private UtilNoLigados utilNoLigados;
	
	private final String Infarto_Card = "InfartoCard";
	private final String Mujer_Card = "MujerCard";
	private final String Vida_Card = "VidaCard";
	
	public ResponseEntity<Object> obtenListaSeguros(ListaSegurosRequestTO request, String requestCifrado) {
		// create instance of Random class 
        Random rand = new Random(); 
        
     // Generate random integers in range 0 to 999 
    /*    int rand_int1 = rand.nextInt(3); 
        int rand_int2 = rand.nextInt(3);
        
        // Print random integers 
        System.out.println("Random Integers: "+rand_int1); 
        System.out.println("Random Integers: "+rand_int2); 
      */  
        
		
		ListaSegurosResponseTO responseTO   =  new ListaSegurosResponseTO();
		
		DescripcionSeguros descripcionSeguro = new DescripcionSeguros();
		ArrayList<DescripcionSeguros> listaDescripcionSeguro = new ArrayList<>();
		
		Gson gson = new Gson();
		ListaSegurosRequestTO requestTO = gson.fromJson(requestCifrado, ListaSegurosRequestTO.class);
		
		request = new ListaSegurosRequestTO();
		request.setGenero(requestTO.getGenero());
		request.setEdad(requestTO.getEdad());
		
		String genero = request.getGenero();
		int edad = request.getEdad();
		
		
		List<DescripcionPolizas> listaProductosSeguros = new ArrayList<DescripcionPolizas>();
		
		listaProductosSeguros = obteniendoProductosSeguros();
		//listaProductosSeguros = listaDescripcionSeguro();
		
		if(listaProductosSeguros == null) {
			responseTO.setRespuesta("EL GENERO INGRESADO ES:  " + request.getGenero());
			responseTO.setDescripcionListaSeguros(listaDescripcionSeguro);
			return new ResponseEntity<>(responseTO, HttpStatus.OK);	
			
		}
		else {
		
		logger.info("Tamanio de la lista es " ,listaProductosSeguros.size());

		int numeroPolizas_infarto = 0;
		int numeroPolizas_mujer = 0;
		int numeroPolizas_vida = 0;
		
		//Plna Mujer
		int j;
		
		
		if(genero.equals("Femenino")) {
			for (j = 0; j <= 1; j++) {
				if(edad >=18 && edad <=64) {
					if (listaProductosSeguros!=null) {
						for (DescripcionPolizas descPol : listaProductosSeguros) {
							String nombreProducto = ((DescripcionPolizas) descPol).getDescProduct();
							if (nombreProducto.equals(Mujer_Card)) {
								numeroPolizas_mujer = ((DescripcionPolizas) descPol).getCountPolicies();
								logger.info("numeroPolizas_mujer " + numeroPolizas_mujer);
								if(numeroPolizas_mujer>=2) {
									descripcionSeguro = new DescripcionSeguros();
									descripcionSeguro.setNombre("Plan Mujer");
									descripcionSeguro.setAptoPrimeraPersona(false);
									descripcionSeguro.setAptoTerceraPersona(true);
								}
								if(numeroPolizas_mujer<2) {
									descripcionSeguro = new DescripcionSeguros();
									descripcionSeguro.setNombre("Plan Mujer");
									descripcionSeguro.setAptoPrimeraPersona(true);
									descripcionSeguro.setAptoTerceraPersona(true);
								}
							}
						}
					}
					else {
						logger.info("El cliente no trajo ninguna poliza de mujer");
						descripcionSeguro.setNombre("Plan Mujer");
						descripcionSeguro.setAptoPrimeraPersona(true);
						descripcionSeguro.setAptoTerceraPersona(true);
					}
				}
				else if(edad>64) {
					descripcionSeguro.setNombre("Plan Mujer");
					descripcionSeguro.setAptoPrimeraPersona(false);
					descripcionSeguro.setAptoTerceraPersona(true);
				}
				else if(edad<18) {
					logger.info("El cliente es menor de edad, no se le oferta seguros de plan mujer");
				}
				
				//METEREMOS EL IF DEL ACCIDENTE
				if (j == 1) {
					if(edad >=18 && edad <=70) {
						if (listaProductosSeguros!=null) {
							for (DescripcionPolizas descPol : listaProductosSeguros) {
								String nombreProducto = ((DescripcionPolizas) descPol).getDescProduct();
								if (nombreProducto.equals(Vida_Card)) {
									numeroPolizas_mujer = ((DescripcionPolizas) descPol).getCountPolicies();
									logger.info("numeroPolizas_vida " + numeroPolizas_mujer);
									if(numeroPolizas_mujer>=2) {
										descripcionSeguro = new DescripcionSeguros();
										descripcionSeguro.setNombre("Plan Accidente");
										descripcionSeguro.setAptoPrimeraPersona(false);
										descripcionSeguro.setAptoTerceraPersona(true);
									}
									if(numeroPolizas_mujer<2) {
										descripcionSeguro = new DescripcionSeguros();
										descripcionSeguro.setNombre("Plan Accidente");
										descripcionSeguro.setAptoPrimeraPersona(true);
										descripcionSeguro.setAptoTerceraPersona(true);
									}
								}
							}
						}
						else {
							logger.info("El cliente no trajo ninguna poliza de accidente");
							descripcionSeguro.setNombre("Plan Accidente");
							descripcionSeguro.setAptoPrimeraPersona(true);
							descripcionSeguro.setAptoTerceraPersona(true);
						}
					}
					else if(edad>70) {
						descripcionSeguro.setNombre("Plan Accidente");
						descripcionSeguro.setAptoPrimeraPersona(false);
						descripcionSeguro.setAptoTerceraPersona(true);
					}
					if(edad<18) {
						logger.info("El cliente es menor de edad, no se le oferta seguros de plan accidente");
					}
				}
				listaDescripcionSeguro.add(descripcionSeguro);	
			}
		}	
		
		
		
		if(genero.equals("Masculino")) {
			for (j = 0; j <= 1; j++) {
				if(edad >=18 && edad <=64) {
					if (listaProductosSeguros!=null) {
						for (DescripcionPolizas descPol : listaProductosSeguros) {
							String nombreProducto = ((DescripcionPolizas) descPol).getDescProduct();
							if (nombreProducto.equals(Infarto_Card)) {
								numeroPolizas_mujer = ((DescripcionPolizas) descPol).getCountPolicies();
								logger.info("numeroPolizas_infaro " + numeroPolizas_mujer);
								if(numeroPolizas_mujer>=2) {
									descripcionSeguro = new DescripcionSeguros();
									descripcionSeguro.setNombre("Plan Infarto");
									descripcionSeguro.setAptoPrimeraPersona(false);
									descripcionSeguro.setAptoTerceraPersona(true);
								}
								if(numeroPolizas_mujer<2) {
									descripcionSeguro = new DescripcionSeguros();
									descripcionSeguro.setNombre("Plan Infarto");
									descripcionSeguro.setAptoPrimeraPersona(true);
									descripcionSeguro.setAptoTerceraPersona(true);
								}
							}
						}
					}
					else {
						logger.info("El cliente no trajo ninguna poliza de infarto");
						descripcionSeguro.setNombre("Plan Infarto");
						descripcionSeguro.setAptoPrimeraPersona(true);
						descripcionSeguro.setAptoTerceraPersona(true);
					}
				}
				else if(edad>64) {
					descripcionSeguro.setNombre("Plan Infarto");
					descripcionSeguro.setAptoPrimeraPersona(false);
					descripcionSeguro.setAptoTerceraPersona(true);
				}
				else if(edad<18) {
					logger.info("El cliente es menor de edad, no se le oferta seguros de plan infarto");
				}
				
				//METEREMOS EL IF DEL ACCIDENTE
				if (j == 1) {
					if(edad >=18 && edad <=70) {
						if (listaProductosSeguros!=null) {
							for (DescripcionPolizas descPol : listaProductosSeguros) {
								String nombreProducto = ((DescripcionPolizas) descPol).getDescProduct();
								if (nombreProducto.equals(Vida_Card)) {
									numeroPolizas_mujer = ((DescripcionPolizas) descPol).getCountPolicies();
									logger.info("numeroPolizas_vida " + numeroPolizas_mujer);
									if(numeroPolizas_mujer>=2) {
										descripcionSeguro = new DescripcionSeguros();
										descripcionSeguro.setNombre("Plan Accidente");
										descripcionSeguro.setAptoPrimeraPersona(false);
										descripcionSeguro.setAptoTerceraPersona(true);
									}
									if(numeroPolizas_mujer<2) {
										descripcionSeguro = new DescripcionSeguros();
										descripcionSeguro.setNombre("Plan Accidente");
										descripcionSeguro.setAptoPrimeraPersona(true);
										descripcionSeguro.setAptoTerceraPersona(true);
									}
								}
							}
						}
						else {
							logger.info("El cliente no trajo ninguna poliza de accidente");
							descripcionSeguro.setNombre("Plan Accidente");
							descripcionSeguro.setAptoPrimeraPersona(true);
							descripcionSeguro.setAptoTerceraPersona(true);
						}
					}
					else if(edad>70) {
						descripcionSeguro.setNombre("Plan Accidente");
						descripcionSeguro.setAptoPrimeraPersona(false);
						descripcionSeguro.setAptoTerceraPersona(true);
					}
					if(edad<18) {
						logger.info("El cliente es menor de edad, no se le oferta seguros de plan accidente");
					}
				}
				listaDescripcionSeguro.add(descripcionSeguro);	
			}
		}
		

		int edades;
		
		LocalDate date = LocalDate.parse("1990-11-27", DateTimeFormatter.ofPattern("uuuu-MM-dd"));
		LocalDate hoy = LocalDate.now();
		
		
		
		
		edades = calculateAge(date,hoy);
		
		logger.info("LAS EDADES SON " + edades);
		
		}

		
		responseTO.setRespuesta("EL GENERO INGRESADO ES:  " + request.getGenero());
		responseTO.setDescripcionListaSeguros(listaDescripcionSeguro);
		

		return new ResponseEntity<>(responseTO, HttpStatus.OK);	
	}
	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-uuuu");
	
	public List<DescripcionPolizas> obteniendoProductosSeguros() {
		List<DescripcionPolizas> lista = null;
		lista = listaDescripcionSeguro();
		logger.info("Se obtubo esta lista vizarra" + lista);
		

		return lista;
	}
	
	
	

	public int calculateAge(LocalDate birthDate, LocalDate currentDate) {
		// validate inputs ...
		return Period.between(birthDate, currentDate).getYears();
	}
	
	public List<DescripcionPolizas> listaDescripcionSeguro (){
		List<DescripcionPolizas> queryPolizas = null;
		try {
			String tokenAccele = utilNoLigados.obtenerCodigoToken();
			String tokenObtenido = utilNoLigados.obtenerValorToken(tokenAccele);
			logger.info("Token Obtenido es {}",tokenObtenido);
			queryPolizas = callQueryPolizas(tokenObtenido);
			
			//logger.info("el valor de queryPolizas esH " + queryPolizas);
			
			
			
		
			
		}catch(Exception ex) {
			logger.info("No se obtubo ninguna lista de seguros ", ex.getMessage());	
		}
		
		return queryPolizas;	
	}
	
	
	
	
	public List<DescripcionPolizas> callQueryPolizas(String cookie) throws Exception {
		//String respuesta = "";
		List<DescripcionPolizas> respuesta = null;
		CryptographyUtil cryptographyUtil = new CryptographyUtil();
		AuditHeaderBean auditHeaderBean = new AuditHeaderBean();
		ListaPolizas entradaTercero = new ListaPolizas();
		//String clienteUnico=consultado.getExt().getCu().getCuPrimario().getPais().concat(consultado.getExt().getCu().getCuPrimario().getCanal()).concat(consultado.getExt().getCu().getCuPrimario().getSucursal()).concat(consultado.getExt().getCu().getCuPrimario().getFolio());
		try {
			auditHeaderBean.setIdApplication("BAZ03");
			auditHeaderBean.setIdUser("BAZ");
			auditHeaderBean.setNameApplication("BAZDigital Seguros");
			List<PropertiesTemplate> listaPropertiesTemplate = new ArrayList<>();
			
			List<PropertiesThirdParty> listaPropertiesThirdParty = new ArrayList<>();
			PropertiesThirdParty propertiesThirdParty;
			
			
			 
			
			for (int i = 1 ; i <=1 ; i++) {
				switch(i) {
				case 1 :
					propertiesThirdParty = new PropertiesThirdParty();
					propertiesThirdParty.setNameProperty("ClienteUnicoBAZ");
					//String ApellidoMaterno = !Validations.isNullOrEmpty(consultado.getCliente().getApellidos().get(1))?consultado.getCliente().getApellidos().get(1):"";
					//propertiesThirdParty.setInput(cryptographyUtil.encrypt(ApellidoMaterno));
					//String ApellidoMaterno = !Validations.isNullOrEmpty(consultado.getCliente().getApellidos().get(1))?consultado.getCliente().getApellidos().get(1):"";
					propertiesThirdParty.setInput(cryptographyUtil.encrypt("JOGEMONOORON23051960M2"));
					//propertiesThirdParty.setInput(cryptographyUtil.encrypt("NATHALY DEL CARMEN"));
					propertiesThirdParty.setEncrypted(true);
					listaPropertiesThirdParty.add(propertiesThirdParty);
					break;
							
				}
			}
			
			logger.info("Segun esta lista es " + listaPropertiesThirdParty);
			
		
			entradaTercero.setAuditHeaderBean(auditHeaderBean);
			entradaTercero.setThirdPartyType("NaturalPerson");
			entradaTercero.setPropertiesTemplate(listaPropertiesTemplate);
			entradaTercero.setListaPropertiesThirdParty(listaPropertiesThirdParty);
			
			respuesta =  utilNoLigados.queryPolizasGET(entradaTercero, cookie);
			
			
	
			
			

			return respuesta;
			
			
		}catch(Exception er) {
			logger.error("Se genero un error al intentar traer la informacion de las polizas del cliente " + er);
			
		}
		
		return respuesta;
	}
}
