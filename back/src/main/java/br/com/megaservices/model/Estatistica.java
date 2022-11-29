package br.com.megaservices.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Estatistica {

	private String dezena;
	private String percentual;
	private String vezesSorteadas;
	
}
