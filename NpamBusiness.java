package com.bancoazteca.bdm.cotizador.BDMCotizacion.business;

import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.AbonoNormal;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.AbonosR;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.Cliente;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.CreditRunCotizadorRequest;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.EscalerasResponse;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.EscalerasResponseTo;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.Plazo;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.Precio;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.Sucursal;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NpamBusiness {
	private static final Logger LOG = LoggerFactory.getLogger(NpamBusiness.class);

	@Autowired
	private SeguroBusinessImpl seguroImple;

	@Autowired
	private UtilSeguroCredito utilSeguro;

	final int TIPOFLUJORENOVACIONESPECIAL = 20;
	final int FLUJORENOVACION = 2;
	final int TIPOFLUJORECOMPRAESPECIAL = 10;
	final int FLUJORECOMPRA = 1;

	public EscalerasResponseTo getEscaleras(CreditRunCotizadorRequest request) {

		Map<String, String> headers = new HashMap<String, String>();

		// Se hace log del request recibido
		//LOG.info("Request: " + request.toString());

		// Se logean los headers para ver que se encuentren correctos
		//LOG.info("headers: " + headers.toString());

		// Se crea un objeto con el request
		EscalerasRequest reqEscaleras = setRequestEscalera(request);

		String reqEsc = convertObjectToJson(reqEscaleras);

		// String resEscDao = escalerasDao.getEscaleras(headers, reqEsc);DESCOMENTAR
		// HUGO

		// HUGO
		String resEscDao = "";

		try {
			resEscDao = read("E:/Users/hamaro/listaEscalera.json");
			//LOG.info("Si trajo escaleras");

		} catch (Exception er) {
			LOG.error("NO TRAJO ESCALERAS " + er);
			er.printStackTrace();
		}
		// TERMINA HUGO

		EscalerasResponseTo respon = new EscalerasResponseTo();

		if (resEscDao.contains("abonos")) {
			//LOG.info("Si contiene tag de abonos");
			// continuar flujo
			respon = crearRespuesta(resEscDao, request);
		} else {
			LOG.info("No contiene abonos");
			respon.setCodigo(2);
			return respon;
		}
		return respon;
	}

	public static String read(String file) throws IOException {
		StringBuilder content = new StringBuilder();
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(file, new String[0]),
				Charset.defaultCharset())) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				content.append(line).append("\n");
			}
			return content.toString();
		}
	}

	public EscalerasResponseTo crearRespuesta(String resEscDao, CreditRunCotizadorRequest request) {
		EscalerasResponse responseEsc = new EscalerasResponse();

		//LOG.info("Creando respuesta...");
		try {

			Gson g = new Gson();
			responseEsc = g.fromJson(resEscDao, EscalerasResponse.class);
		} catch (Exception exc) {
			LOG.info("Ocurrio un problema tratando de convertir la respuesta a objeto, Causa: {}, Mensaje: {}",
					exc.getCause().toString(), exc.getMessage().toString());
		}

		EscalerasResponseTo respon = new EscalerasResponseTo();

		if (null == responseEsc.getRespuesta().getAbonos().getPrecios()
				|| responseEsc.getRespuesta().getAbonos().getPrecios().isEmpty()) {
			LOG.info("Este cliente no cuenta con ofertas, " + respon.toString());
			respon.setCodigo(1);
			return respon;
		}

		// TODO validar tasas y datos necesarios que no vengan null o vacios
		List<AbonoNormal> lisResp = new ArrayList<AbonoNormal>();
		try {
			lisResp = convierteAbono(responseEsc, String.valueOf(request.getPeriodo()), request.getTipoFlujo(),
					request.getMin(), request.getPlazoIni());

			respon.setListaCotizador(lisResp);
		} catch (Exception exc) {
			LOG.info("Ocurrio un problema tratando de convertir la respuesta a abonosnormales",
					exc.getCause().toString(), exc.getMessage().toString());
			// throw new MessageException(CodigoErrorEnum.ERROR_GENERICO.getCodigo());
		}

		try {
			if (request.isRenovacionSeguros() == false && request.getTipoFlujo() == FLUJORENOVACION) {
				//LOG.info("Llenando seguros renovacion");
				//LOG.info("Llenando seguros renovacion personalizada en NPAM");
				
				utilSeguro.evaluaListaSegurosCreditoRenovacionNPAM(respon, request.getCapacidadDePago());
				
			}
			else {
				LOG.info("No hay seguros");
			}

		} catch (Exception exc) {
			LOG.info("Ocurrio un problema agregando seguros", exc.getCause().toString(), exc.getMessage().toString());
			// throw new MessageException(CodigoErrorEnum.ERROR_GENERICO.getCodigo());
		}

		return respon;
	}
	
	

	public EscalerasRequest setRequestEscalera(CreditRunCotizadorRequest request) {

		//LOG.info("Creando request escaleras");

		// Consultado consultado = (Consultado)
		// session.getAttribute(ConstantesCredito.DETAIL_OBJETO_CLIENTE_360);

		// Se obtiene cliente unico de credito
		String pais = "1";
		String canal = "1";
		String sucursal = "2244";
		String folio = "108065";

		EscalerasRequest req = new EscalerasRequest();

		// Se setea la parte de cliente del request
		Cliente cliente = new Cliente(FLUJORECOMPRA, FLUJORECOMPRA, FLUJORECOMPRA, FLUJORECOMPRA, FLUJORECOMPRA,
				FLUJORECOMPRA);
		cliente.setPais(Integer.parseInt(pais));
		cliente.setCanal(Integer.parseInt(canal));
		cliente.setSucursal(Integer.parseInt(sucursal));
		cliente.setFolio(Integer.parseInt(folio));
		req.setCliente(cliente);

		// Se setea la parte de sucursal del request
		Sucursal sucursalReq = new Sucursal();
		sucursalReq.setIdCanal(request.getCanalRen() != 0 ? request.getCanalRen() : request.getCanal());
		sucursalReq.setIdSucursal(request.getSucursalRen() != 0 ? request.getSucursalRen() : request.getSucursal());
		req.setSucursal(sucursalReq);

		req.setIdProducto(24);// 24 es prestamos
		req.setPlataforma("B"); // hace referencia a banca digital
		req.setTipoCliente(request.getTasa());
		req.setPeriodo(request.getPeriodo());
		req.setTipoPromocion(0); // siempre va en 0
		req.setCdpDisponible(request.getCapacidadDePago());

		//LOG.info("Request: " + req.toString());

		return req;
	}

	public List<AbonoNormal> convierteAbono(EscalerasResponse responseEsc, String periodo, int flujo, int precioMin,
			int plazoIni) {

		//LOG.info("Convirtiendo respuesta a abonos normales");
		List<AbonoNormal> abonosNormales = new ArrayList<AbonoNormal>();
		//LOG.info("Escaleras size: " + responseEsc.getRespuesta().getAbonos().getPrecios().size());
		List<Precio> precios = responseEsc.getRespuesta().getAbonos().getPrecios();

		for (int i = 0; i < precios.size(); i++) {

			Precio precioTemp = precios.get(i);
			List<Plazo> plazosDeprecioI = precioTemp.getPlazos();

			for (int j = 0; j < plazosDeprecioI.size(); j++) {
				Plazo plazoTemp = plazosDeprecioI.get(j);
				AbonoNormal temp = new AbonoNormal();

				temp.setIdProducto(precioTemp.getSku());
				temp.setMonto(precioTemp.getPrecio());

				temp.setIdPeriodicidad(Integer.parseInt(periodo));

				if (periodo.equals("13")) {
					temp.setPeriodicidad("QUINCENAL");
				} else if (periodo.equals("14")) {
					temp.setPeriodicidad("MENSUAL");
				} else {
					temp.setPeriodicidad("SEMANAL");
				}

				temp.setIdPlazo(String.valueOf(plazoTemp.getPlazo()));
				temp.setPlazo(String.valueOf(plazoTemp.getPlazo()));

				temp.setIntereses(plazoTemp.getSobre());
				temp.setMontoTotal(precioTemp.getPrecio() + plazoTemp.getSobre());
				temp.setMontoTotalR(precioTemp.getPrecio() + plazoTemp.getSobreR());
				temp.setInteresesR(plazoTemp.getSobreR());

				AbonosR tempReducido = plazoTemp.getAbonosR().get(0);
				temp.setPagoNormal(plazoTemp.getNormal());
				temp.setPagoNormalPromo(plazoTemp.getNormalR());
				temp.setPagoNormalPromoEspecial(Integer.parseInt(tempReducido.getEspecialNormalR()));

				temp.setPagoPuntual(plazoTemp.getPuntual());
				temp.setPagoPuntualPromo(plazoTemp.getPuntualR());
				temp.setPagoPuntualPromoEspecial(Integer.parseInt(tempReducido.getEspecialPuntualR()));

				temp.setNumeroSemanasPromocionales(tempReducido.getNumeroSemanasPromocionales());

				temp.setPorcentajeLiquidacion(plazoTemp.getPorcentajeLiquidacion());

				// Si es flujo de renovacion y el min es igual o mayor al monto del plazo y el
				// plazo es
				// mayor o igual al plazo ini
				// Se agrega a la lista
				if (flujo == 2 && precioMin >= temp.getMonto()
						&& plazoIni >= Integer.parseUnsignedInt(temp.getPlazo())) {
					abonosNormales.add(temp);
				} else {
					abonosNormales.add(temp);
				}
			}

		}

		return abonosNormales;
	}

	/**
	 * @param object
	 * @return
	 */
	public String convertObjectToJson(Object object) {
		String json = "";
		Gson gson = new GsonBuilder().serializeNulls().create();
		try {
			json = gson.toJson(object);
		} catch (Exception e) {
			LOG.info("Incidencia en metodo convertObjectToJson: {} ", e.getMessage());
			json = null;
		}
		return json;
	}

}
