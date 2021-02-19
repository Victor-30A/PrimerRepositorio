package com.bancoazteca.bdm.cotizador.BDMCotizacion.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.RequestTasasCentralizadas;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.TasasCentralizadas;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;


public class TasasCentralizadasDao {

  private static final Logger LOG = LoggerFactory.getLogger(TasasCentralizadasDao.class);

  /**
   * Consulta el servicio de tasas centralizadas.
   * 
   * @param baseUrl - la url base ya sea de desarrollo o produccion
   * @param req - objeto RequestTasasCentralizadas
   * @return objeto tipo TasasCentralizadas
   */
  public TasasCentralizadas getTasas(String baseUrl, RequestTasasCentralizadas req) {

   // LOG.info("Llendo por tasas centralizadas con: " + req.toString());

    DaoTokenAbonos daoToken = new DaoTokenAbonos();
    String token = "";
    HttpHeaders httpHeaders = new HttpHeaders();
    try {
      //token = daoToken.obtenerToken();
      token = "12345";
      
    } catch (Exception exc) {
      LOG.error("Error consumiendo token: " + exc.getMessage().toString() + ", " + exc.getCause().toString());
    }

    if (token == null || token.isEmpty()) {
      LOG.error("No se pudo obtener el token para consumir al API de abonos ");
      //throw new MessageException(CodigoErrorEnum.ERROR_GENERICO.getCodigo());
    } else {
      httpHeaders.set("Authorization", "Bearer " + token);
    }

    RestTemplate restTemplate = new RestTemplate();

    HttpEntity<String> entity = new HttpEntity<String>(httpHeaders);



    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl)
        .queryParam("filtro", "rangos")
        .queryParam("origenPeticion", req.getOrigenPeticion())
        .queryParam("idTipoProducto", req.getTipoProducto())
        .queryParam("periodo", req.getPeriodo())
        .queryParam("precio", req.getPrecio())
        .queryParam("plazoMinimo", req.getPlazoIni())
        .queryParam("plazoMaximo", req.getPlazoFin())
        .queryParam("precioMinimo", req.getPrecioDe())
        .queryParam("precioMaximo", req.getPrecioA())
        .queryParam("idTipoCliente", req.getTipoCliente())
        .queryParam("idCanal", req.getCanal())
        .queryParam("idSucursal", req.getSucursal())
        .queryParam("idPais", req.getPais());

    TasasCentralizadas res = new TasasCentralizadas();
    String resp = "";
    
    try {
    	// res = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity,TasasCentralizadas.class).getBody();
        //resp = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity,String.class).getBody();
      	
      	/*Hardcodeo para traer listas de credit RUN*/
      	ObjectMapper mapper = new ObjectMapper();
      	TasasCentralizadas obj = mapper.readValue(new File("E:/Users/hamaro/listaCreditRun.json"), TasasCentralizadas.class);
      	res = obj;
    } catch (Exception exc) {
    	//LOG.error("PERRO ERROR EN " + exc);
     LOG.error("Error consumienddo tasas centralizadas: " + exc.getMessage().toString() + ", " + exc.getCause().toString());
    }
   // LOG.info("RESPUESTA STRING TASAS: " + resp);
    return res;


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
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
	public Object convertJsonToObject(String json, Class clazz) {
		Object objeto = null;
		Gson gson = new GsonBuilder().serializeNulls().create();
		try {
			objeto = gson.fromJson(json, clazz);
		} catch (Exception e) {
			LOG.info("Incidencia en metodo convertJsonToObject", e);
			objeto = null;
		}
		return objeto;
	}
  
}

