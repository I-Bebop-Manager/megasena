package br.com.megaservices.utils;

import java.util.ArrayList;
import java.util.List;

public class MegaSenaUtils {

	public static String retornaNumeroFormatado(String num) {
		return String.format("%02d", Integer.parseInt(num));
	}
	
	public static String ordenaJogo(String jogo) {

		List<Integer> lista = new ArrayList<Integer>();
		for (int i = 0; i < jogo.length(); i++) {
			if (i % 2 == 0) {
				lista.add(Integer.parseInt(jogo.substring(i, i + 2)));
			}
		}

		Integer dezenas[] = lista.toArray(new Integer[lista.size()]);

		Integer aux = 999;

		for (int j = 0; j < dezenas.length; j++) {
			for (int i = 0; i < dezenas.length; i++) {
				if (dezenas[j] < dezenas[i]) {
					aux = dezenas[j];
					dezenas[j] = dezenas[i];
					dezenas[i] = aux;
				}
			}
		}

		jogo = "";
		for (int i = 0; i < dezenas.length; i++) {
			jogo = jogo + retornaNumeroFormatado(String.valueOf(dezenas[i]));
		}

		return jogo;
	}
	
	public static List<String> exibeJogoFormatado(List<String> jogosFinais) {
		String saida = "";
		List<String> lista = new ArrayList<String>();

		for (String s : jogosFinais) {
			for (int i = 0; i < s.length(); i++) {
				if (i % 2 == 0) {
					saida = saida + s.substring(i, i + 2);
					if(i < s.length()-2) {
						saida = saida + "-";
					}
				}
			}
//			System.out.println(saida);
			
			lista.add(saida);
			saida="";
		}
		return lista;
	}
}
