package com.bancoazteca.bdm.cotizador.BDMCotizacion.business;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.business.SegurosNoLigados.CryptographyUtil;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.AuditHeaderBean;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.AutenticacionSeguros;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.DireccionTercero;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.EventInsuredObject;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.EventRiskUnit;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.InformacionDappSesion;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.ListInsuranceObject;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.ListParticipation;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.ListParticipationInsuredObject;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.ListRiskUnit;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.ListRiskUnitPlanMedico;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.PaymentModeInput;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.PolicyEvent;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.PolicyFinancialPlan;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.PolicyProperties;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.PolicyValues;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.PolicyValuesPlanMedico;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.PolizaPlanMedico_Input;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.PolizaSegurosInfo;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.Poliza_Input;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.PropertiesTemplate;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.RespuestaServicios;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.Rol_Input;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.ThirdPartyList;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.TransactionResponse;

@Service
public class SegurosAztecaDapp {
	

	
	private static final Logger log = LoggerFactory.getLogger(SegurosAztecaDapp.class);
	private static final Integer ERROR_GENERAL = -1;
	private static final String CODIGO_EXISTENCIA = "302";
	
	@Autowired
	private SegurosAztecaDao segurosAztecaDao;
	
	public static final String CODIGOACEPTACION="200";
	public static final String CODIGOACEPTACIONCONTENIDO="0";
	
	public void emitirPolizaSegurosAzteca(String rfc, String curp,InformacionDappSesion informacionDapp) throws Exception {
		log.info("Emitiendo Poliza");
		//String clienteUnico=objetoCliente.getExt().getCu().getCuPrimario().getPais().concat(objetoCliente.getExt().getCu().getCuPrimario().getCanal()).concat(objetoCliente.getExt().getCu().getCuPrimario().getSucursal()).concat(objetoCliente.getExt().getCu().getCuPrimario().getFolio());

		//segurosAztecaDao.guardarInfoPoliza(clienteUnico, informacionDapp);
		
		String tokenSeguros=obtenerCodigoToken();
		//String tokenObtenido=obtenerValorToken(tokenSeguros);
		log.info("Token obtenido:{}",tokenSeguros);
		
		String thirdParty;
		try {
			thirdParty = crearThirdParty(rfc,curp, tokenSeguros);
			TransactionResponse transaccion= jsonAObjeto(thirdParty,TransactionResponse.class);
			if(transaccion.getCodeMessage().equals(CODIGOACEPTACIONCONTENIDO))
				crearFolioTerceroNuevo(transaccion,tokenSeguros,informacionDapp);
			else if(transaccion.getCodeMessage().equals(CODIGO_EXISTENCIA))
				crearFolioTerceroExistente(transaccion,tokenSeguros,informacionDapp);
			else
				log.error("SE genero un error linea 81 ");
				//LogUtils.printStackTrace();
		
		} catch (Exception e) {
			log.info("Incidencia al ejecutar el emitirPolizaSegurosAzteca {}", e.getStackTrace(),e.getMessage());
		}
		
	}
	
	
	
	/**
	 * Metodo para un nuevo tercero
	 * 
	 */
	public void crearFolioTerceroNuevo(TransactionResponse transaccion,String tokenObtenido,InformacionDappSesion informacionDapp)throws Exception {
		InformacionDappSesion informacionDapps = new InformacionDappSesion();
		informacionDapps.setDapp_code("1ce01efe-af7c-4c11-88fd-848cf95f4143");
		
		if(AsignarRollTerceros(transaccion.getThirdPartyList(),tokenObtenido)==true) {
			if(obtenerDireccionTercero(transaccion.getThirdPartyList().get(0).getThirdPartyId(),tokenObtenido).equals(CODIGOACEPTACION)) {
				if(callAsignarModoCobro(transaccion.getThirdPartyList().get(0).getThirdPartyId(),tokenObtenido).equals(CODIGOACEPTACION)) {
					log.info("Codigo dapp:{}",informacionDapp.getDapp_code());
					if(informacionDapp.getDapp_code().equals("1ce01efe-af7c-4c11-88fd-848cf95f4143"))
						log.info("Respuesta Json Seguros Azteca:{}",callEventPolicyVidaTranquilidad(tokenObtenido,informacionDapp.getReference_num(),transaccion.getThirdPartyList().get(0).getThirdPartyId()));
					else
						//throw new MessageException(ERROR_GENERAL);
						log.error("SE genero un error linea 104 ");
				}
				else {
					//throw new MessageException(ERROR_GENERAL);
					log.error("SE genero un error linea 108 ");
				}
			}
			else
				//throw new MessageException(ERROR_GENERAL);
				log.error("SE genero un error linea 113 ");
		}
		else 
			//throw new MessageException(ERROR_GENERAL);
			log.error("SE genero un error linea 117 ");
	}
	
	/**
	 * Metodo si existe el tercero
	 * 
	 */
	public void crearFolioTerceroExistente(TransactionResponse transaccion,String tokenObtenido,InformacionDappSesion informacionDapp) throws Exception {
		informacionDapp.setDapp_code("1ce01efe-af7c-4c11-88fd-848cf95f4143");
		
		if(AsignarRollTerceros(transaccion.getThirdPartyList(),tokenObtenido)==true){
			
			if(transaccion.getThirdPartyList().get(0).isHasAddress()==false){
				if(obtenerDireccionTercero(transaccion.getThirdPartyList().get(0).getThirdPartyId(),tokenObtenido).equals(CODIGOACEPTACION)) {
					if(transaccion.getThirdPartyList().get(0).getListPaymentMode().size()==0){
						if(callAsignarModoCobro(transaccion.getThirdPartyList().get(0).getThirdPartyId(),tokenObtenido).equals(CODIGOACEPTACION)) {
							log.info("Codigo dapp:{}",informacionDapp.getDapp_code());
							if(informacionDapp.getDapp_code().equals("1ce01efe-af7c-4c11-88fd-848cf95f4143"))
								log.info("Respuesta Json Seguros Azteca:{}",callEventPolicyVidaTranquilidad(tokenObtenido,informacionDapp.getReference_num(),transaccion.getThirdPartyList().get(0).getThirdPartyId()));
							else
								log.error("SE genero un error linea 136 ");
						}
						else
							log.error("SE genero un error linea 139 ");
					}else {
						log.info("Codigo dapp:{}",informacionDapp.getDapp_code());
						if(informacionDapp.getDapp_code().equals("1ce01efe-af7c-4c11-88fd-848cf95f4143"))
							log.info("Respuesta Json Seguros Azteca:{}",callEventPolicyVidaTranquilidad(tokenObtenido,informacionDapp.getReference_num(),transaccion.getThirdPartyList().get(0).getThirdPartyId()));
						else if(informacionDapp.getDapp_code().equals("2aae8657-ca0d-41e7-afed-154c576424ff"))
							log.info("Respuesta Json Seguros Azteca:{}",callEventPolicyPlanMedico(tokenObtenido,informacionDapp.getReference_num(),transaccion.getThirdPartyList().get(0).getThirdPartyId()));
						else
							log.error("SE genero un error linea 147 ");
					}
				}
				else
					log.error("SE genero un error linea 151 ");
			}
			else {
				if(transaccion.getThirdPartyList().get(0).getListPaymentMode().size()==0){
					if(callAsignarModoCobro(transaccion.getThirdPartyList().get(0).getThirdPartyId(),tokenObtenido).equals(CODIGOACEPTACION)) {
						log.info("Codigo dapp:{}",informacionDapp.getDapp_code());
						if(informacionDapp.getDapp_code().equals("1ce01efe-af7c-4c11-88fd-848cf95f4143"))
							log.info("Respuesta Json Seguros Azteca:{}",callEventPolicyVidaTranquilidad(tokenObtenido,informacionDapp.getReference_num(),transaccion.getThirdPartyList().get(0).getThirdPartyId()));
						else if(informacionDapp.getDapp_code().equals("2aae8657-ca0d-41e7-afed-154c576424ff"))
							log.info("Respuesta Json Seguros Azteca:{}",callEventPolicyPlanMedico(tokenObtenido,informacionDapp.getReference_num(),transaccion.getThirdPartyList().get(0).getThirdPartyId()));
						else
							log.error("SE genero un error linea 162 ");
					}
					else
						log.error("SE genero un error linea 165 ");
				}else {
					log.info("Codigo dapp:{}",informacionDapp.getDapp_code());
					if(informacionDapp.getDapp_code().equals("1ce01efe-af7c-4c11-88fd-848cf95f4143"))
						log.info("Respuesta Json Seguros Azteca:{}",callEventPolicyVidaTranquilidad(tokenObtenido,informacionDapp.getReference_num(),transaccion.getThirdPartyList().get(0).getThirdPartyId()));
					else if(informacionDapp.getDapp_code().equals("2aae8657-ca0d-41e7-afed-154c576424ff"))
						log.info("Respuesta Json Seguros Azteca:{}",callEventPolicyPlanMedico(tokenObtenido,informacionDapp.getReference_num(),transaccion.getThirdPartyList().get(0).getThirdPartyId()));
					else
						log.error("SE genero un error linea 173 ");
				}
			}
		}
		else 
			log.error("SE genero un error linea 178 ");
	}
	
	/**
	 * Metodo para obtener el token
	 * @param tokenSeguros
	 * 
	 */
	public String obtenerValorToken(String tokenSeguros){
		String tokenObtenido = null;
		
		try {
			if(tokenSeguros!=null) {
				String[] temporal=tokenSeguros.split(";");
				String[] temporalB=temporal[0].split("=");
				tokenObtenido=temporalB[1];
			}else {
				log.error("SE genero un error linea 198 ");
			}
		}catch(Exception e){
		    //log.info("Error al realizar la operacion {}" , e);
		   log.error("SE genero un error linea 202 " ,e);
	    }
		//return tokenObtenido;
		return tokenObtenido;
	}
	
	/**
	 * Metodo para obtener el codigo de la respuesta de los servicios
	 * @param respuesta
	 * 
	 */
	public String obtenerRespuesta(String respuesta) {
		String codigoObtenido="";
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		try {
			RespuestaServicios codigo = mapper.readValue(respuesta, RespuestaServicios.class);
			codigoObtenido = codigo.getCodeMessage();
		} catch (JsonParseException e) {
			log.error("SE genero un error linea 219 ");
		} catch (JsonMappingException e) {
			log.error("SE genero un error linea 221 ");
		} catch (IOException e) {
			log.error("SE genero un error linea 223 ");
		}
		return codigoObtenido;
	}
	
	/*
	 * CREAR NUEVO TOKEN
	 * 
	 * */
	public String obtenerCodigoToken() throws Exception{
		AutenticacionSeguros autenticacionSeguros = new AutenticacionSeguros();
		
		try {
		autenticacionSeguros.setUserName("BAZDigital");
		autenticacionSeguros.setPassword("Azteca10");
		autenticacionSeguros.setCountry("MX");
		autenticacionSeguros.setInstance("Azteca");
		}catch(Exception e)
	    {
			log.error("SE genero un error linea 243 ");
	    }
		return segurosAztecaDao.CrearTokenSegurosAzteca(autenticacionSeguros);
	}
	
	public String crearThirdParty(String rfc,String curp, String cookie) throws Exception {
		
		//CryptographyUtil cryptographyUtil = new CryptographyUtil();
		AuditHeaderBean auditHeaderBean = new AuditHeaderBean();
		PolizaSegurosInfo entradaTercero = new PolizaSegurosInfo();
		//String clienteUnico=objetoCliente.getExt().getCu().getCuPrimario().getPais().concat(objetoCliente.getExt().getCu().getCuPrimario().getCanal()).concat(objetoCliente.getExt().getCu().getCuPrimario().getSucursal()).concat(objetoCliente.getExt().getCu().getCuPrimario().getFolio());
		String clienteUnico = "010987990000001968";
		PolizaSegurosInfo entradaTerceroMongo = new PolizaSegurosInfo();
		
		try {
		auditHeaderBean.setIdApplication("BAZ01");
		auditHeaderBean.setIdUser("BAZ");
		auditHeaderBean.setNameApplication("BAZ QR");
		List<PropertiesTemplate> listaPropertiesTemplate = new ArrayList<>();
		List<PropertiesTemplate> listaPropertiesTemplateMongo = new ArrayList<>();
				
		for (int i = 1; i <= 13; i++) {
			PropertiesTemplate propertiesTemplate = new PropertiesTemplate();
			PropertiesTemplate propertiesTemplateMongo = new PropertiesTemplate();
			switch (i) {

			case 1:
				propertiesTemplate.setNameProperty("ClienteUnicoBAZ");
				//propertiesTemplate.setInput(cryptographyUtil.encrypt(clienteUnico));
				propertiesTemplate.setInput(clienteUnico);
				propertiesTemplate.setEncrypted(false);
				listaPropertiesTemplate.add(propertiesTemplate);
				
				propertiesTemplateMongo.setNameProperty("ClienteUnicoBAZ");
				propertiesTemplateMongo.setInput(clienteUnico);
				propertiesTemplateMongo.setEncrypted(false);
				listaPropertiesTemplateMongo.add(propertiesTemplateMongo);
				break;
			case 2:
				/////propertiesTemplate.setNameProperty("CURP");
				//propertiesTemplate.setInput(cryptographyUtil.encrypt(curp));
				/////propertiesTemplate.setInput(curp);
				/////propertiesTemplate.setEncrypted(false);
				/////listaPropertiesTemplate.add(propertiesTemplate);
				
				/////propertiesTemplateMongo.setNameProperty("CURP");
				/////propertiesTemplateMongo.setInput(curp);
				/////propertiesTemplateMongo.setEncrypted(false);
				/////listaPropertiesTemplateMongo.add(propertiesTemplateMongo);

				break;
			case 3:
				propertiesTemplate.setNameProperty("RFC");
				//propertiesTemplate.setInput(cryptographyUtil.encrypt(rfc));
				propertiesTemplate.setInput(rfc);
				propertiesTemplate.setEncrypted(false);
				listaPropertiesTemplate.add(propertiesTemplate);
				
				propertiesTemplateMongo.setNameProperty("RFC");
				propertiesTemplateMongo.setInput(rfc);
				propertiesTemplateMongo.setEncrypted(false);
				listaPropertiesTemplateMongo.add(propertiesTemplateMongo);
				break;
			case 4:
				propertiesTemplate.setNameProperty("Nombre");
				//propertiesTemplate.setInput(cryptographyUtil.encrypt("HUGO"));
				propertiesTemplate.setInput("HUGO");
				propertiesTemplate.setEncrypted(false);
				listaPropertiesTemplate.add(propertiesTemplate);
				
				propertiesTemplateMongo.setNameProperty("Nombre");
				propertiesTemplateMongo.setInput("HUGO");
				propertiesTemplateMongo.setEncrypted(false);
				listaPropertiesTemplateMongo.add(propertiesTemplateMongo);
				break;
			case 5:
				propertiesTemplate.setNameProperty("ApellidoPaterno");
				//propertiesTemplate.setInput(cryptographyUtil.encrypt("AMARO"));
				propertiesTemplate.setInput("AMARO");
				propertiesTemplate.setEncrypted(false);
				listaPropertiesTemplate.add(propertiesTemplate);
				
				propertiesTemplateMongo.setNameProperty("ApellidoPaterno");
				propertiesTemplateMongo.setInput("AMARO");
				propertiesTemplateMongo.setEncrypted(false);
				listaPropertiesTemplateMongo.add(propertiesTemplateMongo);
				break;
			case 6:
				propertiesTemplate.setNameProperty("ApellidoMaterno");
				//String ApellidoMaterno = !Validations.isNullOrEmpty(objetoCliente.getCliente().getApellidos().get(1))?objetoCliente.getCliente().getApellidos().get(1):"";
				String ApellidoMaterno = "OLGUIN";
				//propertiesTemplate.setInput(cryptographyUtil.encrypt(ApellidoMaterno));
				propertiesTemplate.setInput(ApellidoMaterno);
				propertiesTemplate.setEncrypted(false);
				listaPropertiesTemplate.add(propertiesTemplate);
				
				propertiesTemplateMongo.setNameProperty("ApellidoMaterno");
				propertiesTemplateMongo.setInput(ApellidoMaterno);
				propertiesTemplateMongo.setEncrypted(false);
				listaPropertiesTemplateMongo.add(propertiesTemplateMongo);
				break;
			case 7:
				propertiesTemplate.setNameProperty("BirthDate");
				SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date fechaNac = newDateFormat.parse("31-01-1990");
				newDateFormat.applyPattern("dd-MM-yyyy");
				//propertiesTemplate.setInput(cryptographyUtil.encrypt(newDateFormat.format(fechaNac)));
				propertiesTemplate.setInput(newDateFormat.format(fechaNac));
				propertiesTemplate.setEncrypted(false);
				listaPropertiesTemplate.add(propertiesTemplate);
				
				propertiesTemplateMongo.setNameProperty("BirthDate");
				propertiesTemplateMongo.setInput(newDateFormat.format(fechaNac));
				propertiesTemplateMongo.setEncrypted(false);
				listaPropertiesTemplateMongo.add(propertiesTemplateMongo);
				break;
			case 8:
				propertiesTemplate.setNameProperty("Sex");
				//log.info(objetoCliente.toString());
				//String genero = (objetoCliente.getCliente().getGenero_cu().equalsIgnoreCase("M"))?"2.0":(objetoCliente.getCliente().getGenero_cu().equalsIgnoreCase("F"))?"1.0":null;
				String genero = "M";
				if(genero==null) {
					//throw new MessageException(ERROR_GENERAL);
					log.error("SE genero un error linea 360 ");
				}
				//propertiesTemplate.setInput(cryptographyUtil.encrypt(genero));
				propertiesTemplate.setInput(genero);
				propertiesTemplate.setEncrypted(false);
				listaPropertiesTemplate.add(propertiesTemplate);
				
				propertiesTemplateMongo.setNameProperty("Sex");
				propertiesTemplateMongo.setInput(genero);
				propertiesTemplateMongo.setEncrypted(false);
				listaPropertiesTemplateMongo.add(propertiesTemplateMongo);
				break;
			case 9:
				propertiesTemplate.setNameProperty("TelefonoFijo");
				//String movil = !Validations.isNullOrEmpty(objetoCliente.getExt().getBdm().getTelefono_asociado())?objetoCliente.getExt().getBdm().getTelefono_asociado():"";
				String movil = "55 57 75 04 99";
				//propertiesTemplate.setInput(cryptographyUtil.encrypt(movil));
				propertiesTemplate.setInput(movil);
				propertiesTemplate.setEncrypted(false);
				listaPropertiesTemplate.add(propertiesTemplate);
				
				propertiesTemplateMongo.setNameProperty("TelefonoFijo");
				propertiesTemplateMongo.setInput(movil);
				propertiesTemplateMongo.setEncrypted(false);
				listaPropertiesTemplateMongo.add(propertiesTemplateMongo);
				break;
			case 10:
				propertiesTemplate.setNameProperty("TelefonoCelular");
				//String celular =!Validations.isNullOrEmpty(objetoCliente.getExt().getBdm().getTelefono_asociado())?objetoCliente.getExt().getBdm().getTelefono_asociado():"";
				String celular = "55 57 75 04 99"; 
				//propertiesTemplate.setInput(cryptographyUtil.encrypt(celular));
				propertiesTemplate.setInput(celular);
				propertiesTemplate.setEncrypted(false);
				listaPropertiesTemplate.add(propertiesTemplate);
				
				propertiesTemplateMongo.setNameProperty("TelefonoCelular");
				propertiesTemplateMongo.setInput(celular);
				propertiesTemplateMongo.setEncrypted(false);
				listaPropertiesTemplateMongo.add(propertiesTemplateMongo);
				break;
			case 11:
				propertiesTemplate.setNameProperty("Email");
				//String email = !Validations.isNullOrEmpty(objetoCliente.getCliente().getCorreos_electronicos().get(0).getDireccion())?objetoCliente.getCliente().getCorreos_electronicos().get(0).getDireccion():"";
				String email =  "hugo.amaro@bancoazteca.com";
				//propertiesTemplate.setInput(cryptographyUtil.encrypt(email));
				propertiesTemplate.setInput(email);
				propertiesTemplate.setEncrypted(false);
				listaPropertiesTemplate.add(propertiesTemplate);
				
				propertiesTemplateMongo.setNameProperty("Email");
				propertiesTemplateMongo.setInput(email);
				propertiesTemplateMongo.setEncrypted(false);
				listaPropertiesTemplateMongo.add(propertiesTemplateMongo);
				break;
			case 12:
				propertiesTemplate.setNameProperty("Intercompanias");
				propertiesTemplate.setInput("1.0");
				listaPropertiesTemplate.add(propertiesTemplate);

				propertiesTemplateMongo.setNameProperty("Intercompanias");
				propertiesTemplateMongo.setInput("1.0");
				listaPropertiesTemplateMongo.add(propertiesTemplateMongo);
				break;
			case 13:
				propertiesTemplate.setNameProperty("GL");
				propertiesTemplate.setInput("0000");
				listaPropertiesTemplate.add(propertiesTemplate);
				
				propertiesTemplateMongo.setNameProperty("GL");
				propertiesTemplateMongo.setInput("0000");
				listaPropertiesTemplateMongo.add(propertiesTemplateMongo);
				break;
			}

		}
		entradaTercero.setAuditHeaderBean(auditHeaderBean);
		entradaTercero.setThirdPartyType("NaturalPerson");
		entradaTercero.setPropertiesTemplate(listaPropertiesTemplate);
	
		entradaTerceroMongo.setAuditHeaderBean(entradaTercero.getAuditHeaderBean());
		entradaTerceroMongo.setThirdPartyType(entradaTercero.getThirdPartyType());
		entradaTerceroMongo.setPropertiesTemplate(listaPropertiesTemplateMongo);
		}catch(Exception e)
	    {
		    log.info("Problema: {}" , e);
		    LogUtils.printStackTrace(e);
	    }
		
		return segurosAztecaDao.createThirdParty(entradaTercero, cookie);
	}
	
	public boolean AsignarRollTerceros(List<ThirdPartyList> listaThirdParty, String cookie) throws Exception {
		List<String> listRol=listaThirdParty.get(0).getListRol();
		log.info("Tamano de la lista:{}",listRol.size());
			if(listRol.size()<2) {			
				if(listRol.size()!=0) {
					if(listRol.get(0).equals("Asegurado"))
						return callAsignarRolTercerosContratante(listaThirdParty,cookie).equals(CODIGOACEPTACION)?true:false;
					else if(listRol.get(0).equals("Contratante")) 
						return callAsignarRolTercerosAsegurado(listaThirdParty,cookie).equals(CODIGOACEPTACION)?true:false;
				}else
					return (callAsignarRolTercerosAsegurado(listaThirdParty,cookie).equals(CODIGOACEPTACION)&&callAsignarRolTercerosContratante(listaThirdParty,cookie).equals(CODIGOACEPTACION))?true:false;	
			}
			return true;
	}
	
	public String callAsignarRolTercerosAsegurado(List<ThirdPartyList> listaThirdParty,String cookie)throws Exception {
		
		Rol_Input rol_Input = new Rol_Input();
		//String clienteUnico=objetoCliente.getExt().getCu().getCuPrimario().getPais().concat(objetoCliente.getExt().getCu().getCuPrimario().getCanal()).concat(objetoCliente.getExt().getCu().getCuPrimario().getSucursal()).concat(objetoCliente.getExt().getCu().getCuPrimario().getFolio());
		
		try{
			AuditHeaderBean auditHeaderBean = new AuditHeaderBean();
			auditHeaderBean.setIdApplication("BAZ01");
			auditHeaderBean.setIdUser("BAZ");
			auditHeaderBean.setNameApplication("BAZ QR");
			
			List<PropertiesTemplate> listaPropertiesTemplateRol = new ArrayList<>();
			PropertiesTemplate propertiesTemplate = new PropertiesTemplate();
			
			Date date = new Date();  
		    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");  
		    String strDate = formatter.format(date);  
			
			propertiesTemplate.setNameProperty("InclusionDate");
			propertiesTemplate.setInput(strDate);
			propertiesTemplate.setEncrypted(false);
			listaPropertiesTemplateRol.add(propertiesTemplate);
			
			rol_Input.setAuditHeaderBean(auditHeaderBean);
			rol_Input.setnameRolTemplate("Asegurado");
			rol_Input.setPropertiesTemplateRol(listaPropertiesTemplateRol);
		} catch (Exception e) {
			LogUtils.printStackTrace(e);
		}
		return segurosAztecaDao.addRolThirdParty(rol_Input,cookie,listaThirdParty);
		
	}

	public String callAsignarRolTercerosContratante(List<ThirdPartyList> listaThirdParty, String cookie)throws Exception {
		Rol_Input rol_Input = new Rol_Input();
		//String clienteUnico=objetoCliente.getExt().getCu().getCuPrimario().getPais().concat(objetoCliente.getExt().getCu().getCuPrimario().getCanal()).concat(objetoCliente.getExt().getCu().getCuPrimario().getSucursal()).concat(objetoCliente.getExt().getCu().getCuPrimario().getFolio());
		
		try {
				
			AuditHeaderBean auditHeaderBean = new AuditHeaderBean();
			auditHeaderBean.setIdApplication("BAZ01");
			auditHeaderBean.setIdUser("BAZ");
			auditHeaderBean.setNameApplication("BAZ QR");
			
			List<PropertiesTemplate> listaPropertiesTemplateRol = new ArrayList<>();
			PropertiesTemplate propertiesTemplate = new PropertiesTemplate();
			
			Date date = new Date();  
		    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");  
		    String strDate = formatter.format(date);  
			
			propertiesTemplate.setNameProperty("InclusionDate");
			propertiesTemplate.setInput(strDate);
			propertiesTemplate.setEncrypted(false);
			listaPropertiesTemplateRol.add(propertiesTemplate);
			
			rol_Input.setAuditHeaderBean(auditHeaderBean);
			rol_Input.setnameRolTemplate("Contratante");
			rol_Input.setPropertiesTemplateRol(listaPropertiesTemplateRol);
		} catch (Exception e) {
			LogUtils.printStackTrace(e);
		}
		return segurosAztecaDao.addRolThirdParty(rol_Input,cookie,listaThirdParty);
	}
	
	public String obtenerDireccionTercero(String thirdPartyId,String cookie) throws Exception{
		DireccionTercero direccionTercero = new DireccionTercero();
		//String clienteUnico=objetoCliente.getExt().getCu().getCuPrimario().getPais().concat(objetoCliente.getExt().getCu().getCuPrimario().getCanal()).concat(objetoCliente.getExt().getCu().getCuPrimario().getSucursal()).concat(objetoCliente.getExt().getCu().getCuPrimario().getFolio());

		try{
		AuditHeaderBean auditHeaderBean = new AuditHeaderBean();
		auditHeaderBean.setIdApplication("BAZ01");
		auditHeaderBean.setIdUser("BAZ");
		auditHeaderBean.setNameApplication("BAZ QR");
		direccionTercero.setAuditHeaderBean(auditHeaderBean);
		direccionTercero.setIdTemplateAddress(Integer.parseInt("2819"));
		String[] datosDireccionTerceros = ("Pais;DescripcionEstado;DescripcionPoblacion;Colonia;Calle;CodigoPostal;NumeroExterior;NumeroInterior;Direccion").split(";");
		log.info(datosDireccionTerceros.toString());
		List<PropertiesTemplate> listaPropertiesTemplateAddress = new ArrayList<PropertiesTemplate>();
		for(String propiedad:datosDireccionTerceros) {
			PropertiesTemplate propertiesTemplateAddress = new PropertiesTemplate();
			propertiesTemplateAddress.setNameProperty(propiedad);
			propertiesTemplateAddress.setInput(("1.0").concat(propiedad));
			listaPropertiesTemplateAddress.add(propertiesTemplateAddress);
		}
		direccionTercero.setPropertiesTemplateAddress(listaPropertiesTemplateAddress);
		}catch(Exception e)
	    {
	    	log.info("Problema en operacion {}" , e);
	    	//throw new MessageException(ERROR_GENERAL);
	    	log.error("SE genero un error linea 553 ");
	    }
		return segurosAztecaDao.asignarDireccionTerceros(thirdPartyId,direccionTercero,cookie);
	}
	
	public String callAsignarModoCobro(String thirdPartyId,String cookie) throws Exception{
		PaymentModeInput paymentModeInput = new PaymentModeInput();
		//String clienteUnico=objetoCliente.getExt().getCu().getCuPrimario().getPais().concat(objetoCliente.getExt().getCu().getCuPrimario().getCanal()).concat(objetoCliente.getExt().getCu().getCuPrimario().getSucursal()).concat(objetoCliente.getExt().getCu().getCuPrimario().getFolio());

		AuditHeaderBean auditHeaderBean = new AuditHeaderBean();
		auditHeaderBean.setIdApplication("BAZ01");
		auditHeaderBean.setIdUser("BAZ");
		auditHeaderBean.setNameApplication("BAZ QR");
		paymentModeInput.setAuditHeaderBean(auditHeaderBean);
		paymentModeInput.setIdCollector("1246");
		paymentModeInput.setIdPaymentModes("49539884");
		
		return segurosAztecaDao.asignarModoCobro(paymentModeInput,thirdPartyId,cookie);

	}
	
	public String callEventPolicyVidaTranquilidad(String cookie, String numeroReferencia, String thirdPartyId) throws Exception{
		AuditHeaderBean auditHeaderBean = new AuditHeaderBean();
		Poliza_Input poliza_Input = new Poliza_Input();
		//String clienteUnico=objetoCliente.getExt().getCu().getCuPrimario().getPais().concat(objetoCliente.getExt().getCu().getCuPrimario().getCanal()).concat(objetoCliente.getExt().getCu().getCuPrimario().getSucursal()).concat(objetoCliente.getExt().getCu().getCuPrimario().getFolio());

		try {
			auditHeaderBean.setIdApplication("BAZ01");
			auditHeaderBean.setIdUser("BAZ");
			auditHeaderBean.setNameApplication("BAZ QR");
		
		PolicyValues policyValues = policyValuesVidaTranquilidad(cookie, numeroReferencia, thirdPartyId);
		
		poliza_Input.setAuditHeaderBean(auditHeaderBean);
		poliza_Input.setCreatePolicy(true);
		poliza_Input.setPolicyValues(policyValues);
		poliza_Input.setGenerateLetter(true);
		poliza_Input.setIgnoreValidations(false);
		poliza_Input.setOnline(false);
		}catch(Exception e)
	    {
			log.error("SE genero un error linea 594 ");
	    }
		return segurosAztecaDao.createPolicy(poliza_Input,cookie);
	}
	
	public PolicyValues policyValuesVidaTranquilidad(String cookie, String numeroReferencia, String thirdPartyId) throws Exception{
		
		PolicyValues policyValues = new PolicyValues();
		
		try {
			numeroReferencia = "20304060";
			final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm:ss");
			
			Date date = new Date();  
		    SimpleDateFormat formatterDateTime = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");  
		    String strDateTimeInitial =formatterDateTime.format(date); 
		    
		    String strDateTimeInitialF =formatterDateTime.format(date); 
		    LocalDateTime dateTimeFinish = LocalDateTime.parse(strDateTimeInitialF, formatter);
			dateTimeFinish = dateTimeFinish.plusYears(1);				
			String strDateTimeFinish = dateTimeFinish.format(formatter);
			
			SimpleDateFormat formatterDate = new SimpleDateFormat("dd-MM-yyyy");  
		    String strDateInitial = formatterDate.format(date);  
			//---
			PolicyEvent policyEvent = new PolicyEvent();
			List<PropertiesTemplate> listPropertyEventPolicyEvent = new ArrayList<>();
			PropertiesTemplate propertiesTemplatePropertyEventPolicyEvent = new PropertiesTemplate();
			propertiesTemplatePropertyEventPolicyEvent.setNameProperty("FechaMovimiento");
			propertiesTemplatePropertyEventPolicyEvent.setInput(strDateInitial);
			propertiesTemplatePropertyEventPolicyEvent.setEncrypted(false);
			listPropertyEventPolicyEvent.add(propertiesTemplatePropertyEventPolicyEvent);
			policyEvent.setEventName("Emitir");
			policyEvent.setListPropertyEvent(listPropertyEventPolicyEvent);
			//---
			PolicyProperties policyProperties = new PolicyProperties();
			
			String codigoProductoAzteca = "VTC1";
			String codigoProducto = "207";
			String noRef = numeroReferencia;
			String numeroPoliza = codigoProductoAzteca.concat(codigoProducto).concat(noRef);
			
			String referenciaPago = numeroReferencia;
			
			List<PropertiesTemplate> listPropertyBean = new ArrayList<>();
			PropertiesTemplate propertiesTemplatePropertyBeanNumeroPoliza = new PropertiesTemplate();
			PropertiesTemplate propertiesTemplatePropertyBeanReferenciaPago = new PropertiesTemplate();
			PropertiesTemplate propertiesTemplatePropertyBeanFechaEmision = new PropertiesTemplate();
			
			propertiesTemplatePropertyBeanNumeroPoliza.setNameProperty("NumeroPoliza");
			propertiesTemplatePropertyBeanNumeroPoliza.setInput(numeroPoliza);
			propertiesTemplatePropertyBeanNumeroPoliza.setEncrypted(false);
			listPropertyBean.add(propertiesTemplatePropertyBeanNumeroPoliza);
			
			propertiesTemplatePropertyBeanReferenciaPago.setNameProperty("ReferenciaPago");
			propertiesTemplatePropertyBeanReferenciaPago.setInput(referenciaPago);
			propertiesTemplatePropertyBeanReferenciaPago.setEncrypted(false);
			listPropertyBean.add(propertiesTemplatePropertyBeanReferenciaPago);
			
			propertiesTemplatePropertyBeanFechaEmision.setNameProperty("FechaEmision");
			propertiesTemplatePropertyBeanFechaEmision.setInput(strDateInitial);
			propertiesTemplatePropertyBeanFechaEmision.setEncrypted(false);
			listPropertyBean.add(propertiesTemplatePropertyBeanFechaEmision);
			
			String[] urlPropertyBean = ("Medio;SubArea;TipoVenta;MonedaCNSF;RelacionLaboral;HoraInicio;HoraFin;TipoDescuento").split(";");
			for(String propiedad:urlPropertyBean) {
				PropertiesTemplate propertiesTemplatePropertyBean = new PropertiesTemplate();
				propertiesTemplatePropertyBean.setNameProperty(propiedad);
				propertiesTemplatePropertyBean.setInput(("3.0").concat(propiedad));
				listPropertyBean.add(propertiesTemplatePropertyBean);
			}
			policyProperties.setNameTemplate("POLVidaTranquilidad");
			policyProperties.setListPropertyBean(listPropertyBean);
			//---
			PolicyFinancialPlan policyFinancialPlan = new PolicyFinancialPlan();
			policyFinancialPlan.setName("UnPago");
			policyFinancialPlan.setCurrency("Pesos Mexicanos");
			//---
			ListParticipation listParticipationElem = new ListParticipation();
			List<ListParticipation> listParticipation = new ArrayList<>();
			listParticipationElem.setRoleName("Contratante");
			listParticipationElem.setThirdPartyId(thirdPartyId);
			listParticipationElem.setEvent("Include");
			listParticipationElem.setPaymentModeTemplateName("PAGODIRECTO");
			listParticipationElem.setCollectorID(1246);
			listParticipationElem.setPercentage("100");
			listParticipation.add(listParticipationElem);
			//---
			ListRiskUnit listRiskUnitElem = new ListRiskUnit();
			List<ListRiskUnit> listRiskUnit = new ArrayList<>();
			//-----
			EventRiskUnit eventRiskUnit = new EventRiskUnit();
			List<PropertiesTemplate> listPropertyEventEventRiskUnit = new ArrayList<>();
			PropertiesTemplate propertiesTemplatePropertyEventEventRiskUnit = new PropertiesTemplate();
			propertiesTemplatePropertyEventEventRiskUnit.setNameProperty("FechaMovimiento");
			propertiesTemplatePropertyEventEventRiskUnit.setInput(strDateInitial);
			propertiesTemplatePropertyEventEventRiskUnit.setEncrypted(false);
			listPropertyEventEventRiskUnit.add(propertiesTemplatePropertyEventEventRiskUnit);
			eventRiskUnit.setEventName("Emitir");
			eventRiskUnit.setListPropertyEvent(listPropertyEventEventRiskUnit);
			//-----
			//String[] urlPropertyRiskUnit = ("NumUnidRiesgo;ClienteBig;EstructuraFamiliar;IVAInterfaz;ComisionInterfaz;PrimaTotalInterfaz;SAVTC;PNetaBasicaFalle;SAFalleEnfEpi;PNetaFalleEnfEpi;PNetaAsistenciaVida;PNetaIndHospEpi").split(";");
			String[] urlPropertyRiskUnit = ("NumUnidRiesgo").split(";");
			
			List<PropertiesTemplate> listPropertyRiskUnit = new ArrayList<>();
			for(String propiedad:urlPropertyRiskUnit) {
				PropertiesTemplate propertiesTemplatePropertyRiskUnit = new PropertiesTemplate();
				propertiesTemplatePropertyRiskUnit.setNameProperty(propiedad);
				propertiesTemplatePropertyRiskUnit.setInput(("1").concat(propiedad));
				listPropertyRiskUnit.add(propertiesTemplatePropertyRiskUnit);
			}	
			//-----
			ListInsuranceObject listInsuranceObjectElem = new ListInsuranceObject();
			List<ListInsuranceObject> listInsuranceObject = new ArrayList<>();
			//-------
			EventInsuredObject eventInsuredObject = new EventInsuredObject();
			List<PropertiesTemplate> listPropertyEventEventInsuredObject = new ArrayList<>();
			PropertiesTemplate propertiesTemplatePropertyEventEventInsuredObject = new PropertiesTemplate();
			propertiesTemplatePropertyEventEventInsuredObject.setNameProperty("FechaMovimiento");
			propertiesTemplatePropertyEventEventInsuredObject.setInput(strDateInitial);
			propertiesTemplatePropertyEventEventInsuredObject.setEncrypted(false);
			listPropertyEventEventInsuredObject.add(propertiesTemplatePropertyEventEventInsuredObject);
			eventInsuredObject.setEventName("Emitir");
			eventInsuredObject.setListPropertyEvent(listPropertyEventEventInsuredObject);
			//-------
			String[] urlPropertyInsuredObject = ("NumeroObjetoAseg;TipoAsegurado").split(";");
			List<PropertiesTemplate> listPropertyInsuredObject = new ArrayList<>();
			for(String propiedad:urlPropertyInsuredObject) {
				PropertiesTemplate propertiesTemplatePropertyInsuredObject = new PropertiesTemplate();
				propertiesTemplatePropertyInsuredObject.setNameProperty(propiedad);
				propertiesTemplatePropertyInsuredObject.setInput("1".concat(propiedad));
				listPropertyInsuredObject.add(propertiesTemplatePropertyInsuredObject);
			}
			//-------
			ListParticipationInsuredObject listParticipationInsuredObjectElem = new ListParticipationInsuredObject();
			List<ListParticipationInsuredObject> listParticipationInsuredObject = new ArrayList<>();
			listParticipationInsuredObjectElem.setRoleName("Asegurado");
			listParticipationInsuredObjectElem.setThirdPartyId(thirdPartyId);
			listParticipationInsuredObjectElem.setEvent("Include");
			listParticipationInsuredObjectElem.setPercentage(Integer.valueOf("100"));
			listParticipationInsuredObject.add(listParticipationInsuredObjectElem);
			//-------
			listInsuranceObjectElem.setTemplateType("OAVidaTranquilidad");
			listInsuranceObjectElem.setPlan("PLANBasicoVTC");
			listInsuranceObjectElem.setInitialDate(strDateTimeInitial);
			listInsuranceObjectElem.setFinalDate(strDateTimeFinish);
			listInsuranceObjectElem.setEventInsuredObject(eventInsuredObject);
			listInsuranceObjectElem.setListPropertyInsuredObject(listPropertyInsuredObject);
			listInsuranceObjectElem.setListParticipationInsuredObject(listParticipationInsuredObject);
			listInsuranceObject.add(listInsuranceObjectElem);
			//-----
			listRiskUnitElem.setNameTemplate("URVidaTranquilidad");
			listRiskUnitElem.setInitialDate(strDateTimeInitial);
			listRiskUnitElem.setFinalDate(strDateTimeFinish);
			listRiskUnitElem.setEventRiskUnit(eventRiskUnit);
			listRiskUnitElem.setListPropertyRiskUnit(listPropertyRiskUnit);
			listRiskUnitElem.setListInsuranceObject(listInsuranceObject);
			listRiskUnit.add(listRiskUnitElem);
			//---
			policyValues.setProductName("ProteccionVidaTranquilidad");
			policyValues.setValidity(0);
			policyValues.setInitialDate(strDateTimeInitial);
			policyValues.setFinishDate(strDateTimeFinish);
			policyValues.setPolicyEvent(policyEvent);
			policyValues.setPolicyProperties(policyProperties);
			policyValues.setPolicyFinancialPlan(policyFinancialPlan);
			policyValues.setListParticipation(listParticipation);
			policyValues.setListRiskUnit(listRiskUnit);
		
		}catch(Exception e) {
			log.error("SE genero un error linea 762" , e);
		}
		
		return policyValues;
	}
	
	public String callEventPolicyPlanMedico(String cookie, String numeroReferencia, String thirdPartyId)throws Exception {
		AuditHeaderBean auditHeaderBean = new AuditHeaderBean();
		PolizaPlanMedico_Input poliza_Input = new PolizaPlanMedico_Input();
		//String clienteUnico=objetoCliente.getExt().getCu().getCuPrimario().getPais().concat(objetoCliente.getExt().getCu().getCuPrimario().getCanal()).concat(objetoCliente.getExt().getCu().getCuPrimario().getSucursal()).concat(objetoCliente.getExt().getCu().getCuPrimario().getFolio());

		try {
		auditHeaderBean.setIdApplication("BAZ01");
		auditHeaderBean.setIdUser("BAZ");
		auditHeaderBean.setNameApplication("BAZ QR");
		
		PolicyValuesPlanMedico policyValues = policyValuesPlanMedico(cookie, numeroReferencia, thirdPartyId);

		poliza_Input.setAuditHeaderBean(auditHeaderBean);
		poliza_Input.setCreatePolicy(true);
		poliza_Input.setPolicyValues(policyValues);
		poliza_Input.setGenerateLetter(true);
		poliza_Input.setIgnoreValidations(false);
		poliza_Input.setOnline(false);
		}catch(Exception e)
	    {
		    log.info("No se pudo registrar en la base de datos el intento {}" , e);
		    //LogUtils.printST(e);
	    }
		return segurosAztecaDao.createPolicyPlanMedico(poliza_Input,cookie);
	}
	
	public PolicyValuesPlanMedico policyValuesPlanMedico(String cookie, String numeroReferencia, String thirdPartyId) throws Exception{
		
		PolicyValuesPlanMedico policyValues = new PolicyValuesPlanMedico();
		try {
			
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm:ss");
		
		Date date = new Date();  
	    SimpleDateFormat formatterDateTime = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");  
	    String strDateTimeInitial = formatterDateTime.format(date); 
	    
	    String strDateTimeInitialF =formatterDateTime.format(date); 
	    LocalDateTime dateTimeFinish = LocalDateTime.parse(strDateTimeInitialF, formatter);
		dateTimeFinish = dateTimeFinish.plusYears(1);
		String strDateTimeFinish = dateTimeFinish.format(formatter);
		
		SimpleDateFormat formatterDate = new SimpleDateFormat("dd-MM-yyyy");  
	    String strDateInitial = formatterDate.format(date);  
		//---
		PolicyEvent policyEvent = new PolicyEvent();
		List<PropertiesTemplate> listPropertyEventPolicyEvent = new ArrayList<>();
		PropertiesTemplate propertiesTemplatePropertyEventPolicyEvent = new PropertiesTemplate();
		propertiesTemplatePropertyEventPolicyEvent.setNameProperty("FechaMovimiento");
		propertiesTemplatePropertyEventPolicyEvent.setInput(strDateInitial);
		propertiesTemplatePropertyEventPolicyEvent.setEncrypted(false);
		listPropertyEventPolicyEvent.add(propertiesTemplatePropertyEventPolicyEvent);
		policyEvent.setEventName("Emitir");
		policyEvent.setListPropertyEvent(listPropertyEventPolicyEvent);
		//---
		PolicyProperties policyProperties = new PolicyProperties();
		
		String codigoProductoAzteca = "PCAR";
		String codigoProducto = "211";
		String noRef = numeroReferencia;
		String numeroPoliza = codigoProductoAzteca.concat(codigoProducto).concat(noRef);
		
		String referenciaPago = numeroReferencia;
		
		List<PropertiesTemplate> listPropertyBean = new ArrayList<>();
		PropertiesTemplate propertiesTemplatePropertyBeanNumeroPoliza = new PropertiesTemplate();
		PropertiesTemplate propertiesTemplatePropertyBeanReferenciaPago = new PropertiesTemplate();
		PropertiesTemplate propertiesTemplatePropertyBeanFechaEmision = new PropertiesTemplate();
		propertiesTemplatePropertyBeanNumeroPoliza.setNameProperty("NumeroPoliza");
		propertiesTemplatePropertyBeanNumeroPoliza.setInput(numeroPoliza);
		propertiesTemplatePropertyBeanNumeroPoliza.setEncrypted(false);
		listPropertyBean.add(propertiesTemplatePropertyBeanNumeroPoliza);
		propertiesTemplatePropertyBeanReferenciaPago.setNameProperty("ReferenciaPago");
		propertiesTemplatePropertyBeanReferenciaPago.setInput(referenciaPago);
		propertiesTemplatePropertyBeanReferenciaPago.setEncrypted(false);
		listPropertyBean.add(propertiesTemplatePropertyBeanReferenciaPago);
		propertiesTemplatePropertyBeanFechaEmision.setNameProperty("FechaEmision");
		propertiesTemplatePropertyBeanFechaEmision.setInput(strDateInitial);
		propertiesTemplatePropertyBeanFechaEmision.setEncrypted(false);
		listPropertyBean.add(propertiesTemplatePropertyBeanFechaEmision);
		String[] urlPropertyBean = ("Medio;SubArea;TipoVenta;MonedaCNSF;RelacionLaboral;HoraInicio;HoraFin;TipoDescuento").split(";");
		for(String propiedad:urlPropertyBean) {
			PropertiesTemplate propertiesTemplatePropertyBean = new PropertiesTemplate();
			propertiesTemplatePropertyBean.setNameProperty(propiedad);
			propertiesTemplatePropertyBean.setInput(("3.0").concat(propiedad));
			listPropertyBean.add(propertiesTemplatePropertyBean);
		}

		policyProperties.setNameTemplate("POLPlanMedicoOcho");
		policyProperties.setListPropertyBean(listPropertyBean);
		//---
		PolicyFinancialPlan policyFinancialPlan = new PolicyFinancialPlan();
		policyFinancialPlan.setName("UnPago");
		policyFinancialPlan.setCurrency("Pesos Mexicanos");
		//---
		ListParticipation listParticipationElem = new ListParticipation();
		List<ListParticipation> listParticipation = new ArrayList<>();
		listParticipationElem.setRoleName("Contratante");
		listParticipationElem.setThirdPartyId(thirdPartyId);
		listParticipationElem.setEvent("Include");
		listParticipationElem.setPaymentModeTemplateName("PAGODIRECTO");
		listParticipationElem.setCollectorID(1246);
		listParticipationElem.setPercentage("100");
		listParticipation.add(listParticipationElem);
		//---
		ListRiskUnitPlanMedico listRiskUnitElem = new ListRiskUnitPlanMedico();
		List<ListRiskUnitPlanMedico> listRiskUnit = new ArrayList<>();
		//-----
		EventRiskUnit eventRiskUnit = new EventRiskUnit();
		List<PropertiesTemplate> listPropertyEventEventRiskUnit = new ArrayList<>();
		PropertiesTemplate propertiesTemplatePropertyEventEventRiskUnit = new PropertiesTemplate();
		propertiesTemplatePropertyEventEventRiskUnit.setNameProperty("FechaMovimiento");
		propertiesTemplatePropertyEventEventRiskUnit.setInput(strDateInitial);
		propertiesTemplatePropertyEventEventRiskUnit.setEncrypted(false);
		listPropertyEventEventRiskUnit.add(propertiesTemplatePropertyEventEventRiskUnit);
		eventRiskUnit.setEventName("Emitir");
		eventRiskUnit.setListPropertyEvent(listPropertyEventEventRiskUnit);
		ListInsuranceObject listInsuranceObjectElem = new ListInsuranceObject();
		List<ListInsuranceObject> listInsuranceObject = new ArrayList<>();
		//-------
		EventInsuredObject eventInsuredObject = new EventInsuredObject();
		List<PropertiesTemplate> listPropertyEventEventInsuredObject = new ArrayList<>();
		PropertiesTemplate propertiesTemplatePropertyEventEventInsuredObject = new PropertiesTemplate();
		propertiesTemplatePropertyEventEventInsuredObject.setNameProperty("FechaMovimiento");
		propertiesTemplatePropertyEventEventInsuredObject.setInput(strDateInitial);
		propertiesTemplatePropertyEventEventInsuredObject.setEncrypted(true);
		listPropertyEventEventInsuredObject.add(propertiesTemplatePropertyEventEventInsuredObject);
		eventInsuredObject.setEventName("Emitir");
		eventInsuredObject.setListPropertyEvent(listPropertyEventEventInsuredObject);
		//-------
		String[] urlPropertyInsuredObject = ("NumeroObjetoAseg;SAPlanMedicoOcho;PNetaOchoEnfermedades;PNetaAsistPCAR;SAFalleEnfEpi;PNetaFalleEnfEpi;PNetaIndHospEpi;IVAInterfaz;PrimaTotalInterfaz;ComisionInterfaz").split(";");
		List<PropertiesTemplate> listPropertyInsuredObject = new ArrayList<>();
		for(String propiedad:urlPropertyInsuredObject) {
			PropertiesTemplate propertiesTemplatePropertyInsuredObject = new PropertiesTemplate();
			propertiesTemplatePropertyInsuredObject.setNameProperty(propiedad);
			propertiesTemplatePropertyInsuredObject.setInput("1".concat(propiedad));
			listPropertyInsuredObject.add(propertiesTemplatePropertyInsuredObject);
		}			
		//-------
		ListParticipationInsuredObject listParticipationInsuredObjectElem = new ListParticipationInsuredObject();
		List<ListParticipationInsuredObject> listParticipationInsuredObject = new ArrayList<>();
		listParticipationInsuredObjectElem.setRoleName("Asegurado");
		listParticipationInsuredObjectElem.setThirdPartyId(thirdPartyId);
		listParticipationInsuredObjectElem.setEvent("Include");
		listParticipationInsuredObjectElem.setPercentage(Integer.valueOf("100"));
		listParticipationInsuredObject.add(listParticipationInsuredObjectElem);
		//-------
		listInsuranceObjectElem.setTemplateType("OAPlanMedicoOcho");
		listInsuranceObjectElem.setPlan("PLANTiendaPCAR");
		listInsuranceObjectElem.setInitialDate(strDateTimeInitial);
		listInsuranceObjectElem.setFinalDate(strDateTimeFinish);
		listInsuranceObjectElem.setEventInsuredObject(eventInsuredObject);
		listInsuranceObjectElem.setListPropertyInsuredObject(listPropertyInsuredObject);
		listInsuranceObjectElem.setListParticipationInsuredObject(listParticipationInsuredObject);
		listInsuranceObject.add(listInsuranceObjectElem);
		//-----
		listRiskUnitElem.setNameTemplate("URPlanMedicoOcho");
		listRiskUnitElem.setInitialDate(strDateTimeInitial);
		listRiskUnitElem.setFinalDate(strDateTimeFinish);
		listRiskUnitElem.setEventRiskUnit(eventRiskUnit);
		listRiskUnitElem.setListInsuranceObject(listInsuranceObject);
		listRiskUnit.add(listRiskUnitElem);
		//---
		policyValues.setProductName("PlanMedicoOcho");
		policyValues.setValidity(12);
		policyValues.setInitialDate(strDateTimeInitial);
		policyValues.setFinishDate(strDateTimeFinish);
		policyValues.setPolicyEvent(policyEvent);
		policyValues.setPolicyProperties(policyProperties);
		policyValues.setPolicyFinancialPlan(policyFinancialPlan);
		policyValues.setListParticipation(listParticipation);
		policyValues.setListRiskUnit(listRiskUnit);
		
		}catch(Exception e) {
			LogUtils.printStackTrace(e);
		}
		
		return policyValues;
	}
	
	public static <T> T jsonAObjeto(String resultado, Class<T> objeto) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(resultado, objeto);
		} catch (Exception e) {
			LogUtils.printStackTrace(e);
		}
		return null;
	}
	

}
