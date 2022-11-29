package br.com.megaservices.model;

import java.util.HashSet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ParametrosJogo {

	private Integer numeroDeDezenasPorJogo;
	private Integer numeroDeJogos;
	private Integer rangeAmostras;
	private Boolean usaPorcentagens;
	private String modoPreferenciaJogo;
	private HashSet<Integer> naoQuero;
	private Boolean autoNaoQuero;
	
}
