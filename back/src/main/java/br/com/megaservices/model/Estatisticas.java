package br.com.megaservices.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Estatisticas {

	private List<Estatistica> informacoesDezenas;
	private Double totalDeDezenasSorteadas;	
	private Map<String, Double> percMedioBaixo;
	@JsonIgnore
	private List<String> listaSorteadosMaisDeUmaVez = new ArrayList<String>();
	@JsonIgnore
	private List<String> listaDeJogosJaSorteados;
	@JsonIgnore
	private Double[] perc;
	private String rangeJogos;
}
