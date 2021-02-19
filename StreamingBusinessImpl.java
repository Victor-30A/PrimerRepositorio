package com.bancoazteca.bdm.cotizador.BDMCotizacion.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.StreamingRequest;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.StreamingResponse;
import com.bancoazteca.bdm.cotizador.BDMCotizacion.entity.programacionFuncional.Formula;
import com.google.gson.Gson;

@Service
public class StreamingBusinessImpl {
	private static Logger logger = LoggerFactory.getLogger(StreamingBusinessImpl.class);
	
	
	
	public ResponseEntity<Object> ejecutaStreamings(StreamingRequest request, String objetoJson){
		
		StreamingResponse response = new StreamingResponse();
		Gson gson = new Gson();
		
		StreamingRequest strmRequest = gson.fromJson(objetoJson, StreamingRequest.class);
		try {
			request = new StreamingRequest();
			request.setVariable(strmRequest.getVariable());
			
			response.setVariable(strmRequest.getVariable());
			
			
			logger.info("Entramos a los Streamings "  + response.getVariable().toString());
			
		}catch(Exception er) {
			logger.error("Error al intentar generar la peticion en  " + er);
			
		}
		
		
		/*Metodo Normal para calcular la raiz cuadrada de un numero sqrt*/
		Formula formula = new Formula() {
			@Override
			public double calcular (int a) {
				return Math.sqrt(a * 100);
			}
		};
		
		
		logger.info("Metodo calcular " + formula.calcular(100)); // Llamamos el metodo calcular que solo acepta el valor que vamos a imprimir
		logger.info("Metodo sqrt " + formula.sqrt(16));// Se hace el calculo de la raiz del numero 
		
		logger.info("************ Expresiones lambda 1*************");
		List <String> stringCollection = Arrays.asList("luis","juan","hugo","erika","nelly","rox","nico","ara");
		
	
		//Collections.sort(names, (String a, String b) -> {
		//    return b.compareTo(a);
		//});
		
		
		
		

		
		
		
		
		

		
		//List<String> stringCollection = new ArrayList<>();
		/*stringCollection.add("ddd2");
		stringCollection.add("aaa2");
		stringCollection.add("bbb1");
		stringCollection.add("aaa1");
		stringCollection.add("bbb3");
		stringCollection.add("ccc");
		stringCollection.add("bbb2");
		stringCollection.add("ddd1");*/
		
		stringCollection
	    .stream()
	    .sorted()
	    .filter((s) -> s.startsWith("a"))
	    .forEach(System.out::println);
		
		
		
		
		
		
		
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}