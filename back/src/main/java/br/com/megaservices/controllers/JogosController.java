package br.com.megaservices.controllers;

import static br.com.megaservices.utils.MegaSenaUtils.ordenaJogo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
			
			novoVerificaJogoAnterior(estatisticas);
							
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
	
	/**
	 * @param estatisticas
	 */
	public void novoVerificaJogoAnterior(Estatisticas estatisticas) {
		List<String> jogosVerifAnteriores = new ArrayList<>();
		int valProximoJogo = 1;
				
		for(String jogo : estatisticas.getListaDeJogosJaSorteados()) {
			String jogoFormatado = ordenaJogo(jogo);				
			jogosVerifAnteriores.add(jogoFormatado);
		}
		
		jogosVerifAnteriores = MegaSenaUtils.exibeJogoFormatado(jogosVerifAnteriores);
		
		Map<Integer, String> sorteios = new HashMap<Integer, String>();
		for(int i = jogosVerifAnteriores.size(); i > 0; i--) {
			//System.out.println("jogos: " + jogosVerifAnteriores.get(i-1));
			sorteios.put(i, jogosVerifAnteriores.get(i-1));
		}
		
		for(Integer key : sorteios.keySet()) {			
			if(key>=3) {
				List<String> ultimoJogo = Arrays.asList(sorteios.get(key).split("-"));
				List<String> penultimoJogo = Arrays.asList(sorteios.get(key-1).split("-"));
				List<String> antepenultimoJogo = Arrays.asList(sorteios.get(key-2).split("-"));
				
				StringBuilder sb = new StringBuilder();			
				
				System.out.println(" *******************************");
				System.out.println("* JOGO "+key+" : "+sorteios.get(key) +" *");
				System.out.println(" *******************************");
				System.out.println("--- JOGO Numero "+(key-1)+": "+sorteios.get(key-1));
				System.out.println("--- JOGO Numero "+(key-2)+": "+sorteios.get(key-2));
				boolean naoSaiu = true;
				for(String s : ultimoJogo) {					
					if(penultimoJogo.contains(s)) {						
						System.out.println("Numero "+s+" saiu no penultimo jogo.");
						naoSaiu = false;
					}
					if(antepenultimoJogo.contains(s)) {						
						System.out.println("Numero "+s+" saiu no antepenultimo jogo.");
						naoSaiu = false;
					}					
				}
				if(naoSaiu) {
					System.out.println("####################################################################");
					System.out.println("###  JOGO "+key+" NÃO TEVE NUMERO REPETIDO NOS JOGOS ANTERIORES ### ");
					System.out.println("####################################################################");
				}
			}
			System.out.println("---------------------------------------");
		}
	}
}
