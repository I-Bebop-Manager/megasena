package br.com.megaservices.service;

import static br.com.megaservices.utils.MegaSenaUtils.ordenaJogo;
import static br.com.megaservices.utils.MegaSenaUtils.retornaNumeroFormatado;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import br.com.megaservices.model.Estatistica;
import br.com.megaservices.model.Estatisticas;
import br.com.megaservices.model.Jogos;
import br.com.megaservices.model.ParametrosJogo;

@Service
public class MegaSenaService {
		
	List<String> linhas;
		
	public Estatisticas calculaEstatisticas(ParametrosJogo parametros) throws IOException, URISyntaxException {
				
		List<String> dezenas = new ArrayList<String>();
		
		List<String> listaDeJogosJaSorteados = new ArrayList<String>();
		
		Integer[] quantidade = new Integer[60];
		
		Double totalDeDezenasSorteadas = 0.0;
		
		Double[] perc = new Double[60];
				
		/**
		 * Le o arquivo e coloca num List de Strings, cada string da lista � uma linha
		 * do arquivo
		 */
		URL resource = getClass().getResource("/resultados-anteriores.txt");
		
		if(linhas == null) {
			linhas = Files.readAllLines(Paths.get(resource.toURI()));			
		}
		
		linhas = linhas.stream().filter(linha -> linha.length() >= 7 && StringUtils.isNumeric(linha.substring(0, 1))).collect(Collectors.toList());		

		/**
		 * recupera as dezenas sorteadas anteriormente
		 */		
		int count = 0;
		int range = parametros.getRangeAmostras();
		parametros.setRangeAmostras(linhas.size()-parametros.getRangeAmostras());		
		for (String linha : linhas) {
			if(range == 0 || parametros.getRangeAmostras() < count) {				
				String[] l = linha.split("\t");
				
				String dezena1 = retornaNumeroFormatado(l[2]);
				dezenas.add(dezena1);
				String dezena2 = retornaNumeroFormatado(l[3]);
				dezenas.add(dezena2);
				String dezena3 = retornaNumeroFormatado(l[4]);
				dezenas.add(dezena3);
				String dezena4 = retornaNumeroFormatado(l[5]);
				dezenas.add(dezena4);
				String dezena5 = retornaNumeroFormatado(l[6]);
				dezenas.add(dezena5);
				String dezena6 = retornaNumeroFormatado(l[7]);
				dezenas.add(dezena6);
				listaDeJogosJaSorteados.add(dezena1 + dezena2 + dezena3 + dezena4 + dezena5 + dezena6);
			}
			count++;
		}
		System.out.println("Range de jogos: Concurso "
				+ (parametros.getRangeAmostras() == linhas.size() ? 1 : linhas.size())  
				+" ao concurso "+linhas.size());
		
		/**
		 * Array de quantidades que contara quantas vezes cada numero ja saiu.
		 * Iniciado com Zero
		 */

		int i = 0;
		while (i < quantidade.length) {
			quantidade[i] = 0;
			i++;
		}
		

		/**
		 * sumariza o numero total que cada dezena ja foi sorteada
		 */
		int num;
		for (String d : dezenas) {
			num = Integer.parseInt(d);
			quantidade[num - 1] = quantidade[num - 1] + 1;
			totalDeDezenasSorteadas++;
		}

		/**
		 * calcula as porcentagens de cada dezena sorteada
		 */
		Estatisticas estatisticas = new Estatisticas();
		
		List<Estatistica> lista = new ArrayList<Estatistica>();
		Estatistica e;
		for (i = 0; i < 60; i++) {
			e = new Estatistica();
			perc[i] = quantidade[i] * 100 / totalDeDezenasSorteadas;
			e.setDezena(StringUtils.leftPad(String.valueOf((i + 1)), 2, "0"));
			e.setPercentual(perc[i] != 0.0 ? perc[i].toString().substring(0,4) : "0.0");
			e.setVezesSorteadas(String.valueOf(quantidade[i]));
			lista.add(e);
		}
		
		estatisticas.setInformacoesDezenas(lista);
		estatisticas.setTotalDeDezenasSorteadas(totalDeDezenasSorteadas);
		estatisticas.setListaDeJogosJaSorteados(listaDeJogosJaSorteados);
		estatisticas.setPerc(perc);
		estatisticas.setRangeJogos("Range de jogos: Concurso "+parametros.getRangeAmostras()+" ao concurso "+linhas.size());		
		return estatisticas;
	}	

	public Map<String, Double> calculaPercMedioBaixo(Estatisticas estatisticas) {
		List<Double> p = new ArrayList<Double>();
		estatisticas.getInformacoesDezenas().forEach(e -> p.add(Double.parseDouble(e.getPercentual())) );
		Stream<Double> sMin = p.parallelStream();
		Stream<Double> sMax = p.parallelStream();

		Double max = sMax.max((p1, p2) -> Double.compare(p1, p2)).get();
		Double min = sMin.min((p1, p2) -> Double.compare(p1, p2)).get();

		Double diferencaMaxMin = max - min;
		Double percBaixo = (diferencaMaxMin * 0.15) + min;
		Double percMedio = (diferencaMaxMin * 0.66) + min;
		Double percAlto = (diferencaMaxMin * 0.85) + min;

		Map<String, Double> map = new HashMap<String, Double>();
		map.put("baixo", percBaixo);
		map.put("medio", percMedio);
		map.put("alto", percAlto);
		return map;
		
	}
	
	public String run(Jogos jogos, ParametrosJogo parametros) throws IOException {

		String jogo = "";
		Estatisticas estatisticas = jogos.getEstatisticas();
		/**
		 * preenche as dezenas para o jogo
		 */
		jogo = randomizaJogo(jogos, parametros);

		/**
		 * rola novamente caso o jogo ja tenha saido anteriormente
		 */
		//TODO implementar logica para verificar todas as possibilidades mesmo com jogos de mais de 6 dezenas
		while (verificaJogoJaSorteado(jogo, estatisticas)) {
		
			System.out.println("JOGO SORTEADO ANTERIORMENTE:"+jogo);
			estatisticas.getListaSorteadosMaisDeUmaVez().add(jogo);
			jogo = randomizaJogo(jogos, parametros);			
		}
		

		/**
		 * ordena posicao dos numeros
		 */
		jogo = ordenaJogo(jogo);
		
		return jogo;
	}
	
	private static boolean verificaJogoJaSorteado(String jogo, Estatisticas estatisticas) {

		for(String s : estatisticas.getListaDeJogosJaSorteados()) {
			s = ordenaJogo(s);
			jogo = ordenaJogo(jogo);
			if(jogo.contains(s)) {
				return true;
			}
		}
		return false;
	}
	
	public static String randomizaJogo(Jogos jogos, ParametrosJogo parametros) {
		double percentualBaixo = 999.0;
		double percentualMedio = 999.0;
		double percentualAlto = 999.0;
		
		Estatisticas estatisticas = jogos.getEstatisticas();

		Double[] perc = estatisticas.getPerc();
		
		
		if(parametros.getUsaPorcentagens()) {
			percentualBaixo = estatisticas.getPercMedioBaixo().get("baixo");
			percentualMedio = estatisticas.getPercMedioBaixo().get("medio");
			percentualAlto = estatisticas.getPercMedioBaixo().get("alto");
		}
		

		HashSet<Integer> lista = new HashSet<Integer>();
		String modo = parametros.getModoPreferenciaJogo();

		while (lista.size() < parametros.getNumeroDeDezenasPorJogo()) {
			Integer number = new Random().nextInt(60) + 1;
			
			if("RANDOM".equals(parametros.getModoPreferenciaJogo())) {
				if(new Random().nextInt()%2==0) {
					modo = "ALTO";
				}else {
					modo = "BAIXO";
				}
			}
			
			if("ALTO".equals(modo)) {
				if (parametros.getUsaPorcentagens()) {
					if (perc[number - 1] <= percentualMedio) {
						number = new Random().nextInt(60) + 1;
					}
					if(parametros.getAutoNaoQuero()) {
						if (perc[number - 1] <= percentualBaixo) {
							jogos.getNaoQuero().add(number);
						}
					}
				}				
			} else if("BAIXO".equals(modo)) {				
				if (parametros.getUsaPorcentagens()) {
					if (perc[number - 1] >= percentualMedio) {
						number = new Random().nextInt(60) + 1;
					}
					if(parametros.getAutoNaoQuero()) {
						if (perc[number - 1] >= percentualAlto) {
							jogos.getNaoQuero().add(number);
						}						
					}
				}
			} else if("NORMAL".equals(modo)) {									
				if(parametros.getAutoNaoQuero()) {
					if (perc[number - 1] >= percentualAlto) {
						jogos.getNaoQuero().add(number);
					}						
				}
			}
			
			if(!jogos.getNaoQuero().contains(number)) {
				lista.add(number);				
			} else {
				lista.remove(number);
			}
		}

		String jogo = "";
		for (Integer i : lista) {
			jogo = jogo + retornaNumeroFormatado(String.valueOf(i));
		}
		return jogo;
	}	
}
