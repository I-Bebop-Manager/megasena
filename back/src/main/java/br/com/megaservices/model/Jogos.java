package br.com.megaservices.model;

import java.util.HashSet;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Jogos {

	private Estatisticas estatisticas;
	private List<String> jogos;
	private HashSet<Integer> naoQuero;
}
