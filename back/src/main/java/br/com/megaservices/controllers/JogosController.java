package br.com.megaservices.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.megaservices.controllers.response.Response;
import br.com.megaservices.model.Estatisticas;
import br.com.megaservices.model.Jogos;
import br.com.megaservices.model.ParametrosJogo;
import br.com.megaservices.service.MegaSenaService;
import br.com.megaservices.utils.MegaSenaUtils;

@RestController("jogos")
@RequestMapping("/api/megasena")
@CrossOrigin("*")
public class JogosController {
	
	@Autowired
	private MegaSenaService service;

	@GetMapping
	public String jogo() {
		return "teste";
	}
	
	@PostMapping
	public ResponseEntity<Response<Jogos>> geraJogo(HttpServletRequest request ,@RequestBody ParametrosJogo parametros, BindingResult result) throws Exception{
		Response<Jogos> response = new Response<Jogos>();
		List<String> jogosFinais = new ArrayList<String>();
				
		Estatisticas estatisticas = null;
		try {
			estatisticas = service.calculaEstatisticas(parametros);
			estatisticas.setPercMedioBaixo(service.calculaPercMedioBaixo(estatisticas));
			
			Jogos jogos = new Jogos();
			jogos.setEstatisticas(estatisticas);
			jogos.setNaoQuero(parametros.getNaoQuero());

			for (int z = 1; z <= parametros.getNumeroDeJogos(); z++) {
				jogosFinais.add(service.run(jogos, parametros));
			}
			
			jogos.setJogos(MegaSenaUtils.exibeJogoFormatado(jogosFinais));			
			
			response.setData(jogos);
		} catch (IOException e) {			
			response.setErrors(Arrays.asList(e.getMessage()));
		}
		
		return ResponseEntity.ok(response);
	}
}
