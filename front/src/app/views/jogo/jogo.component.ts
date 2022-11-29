import { Jogos } from './../../model/jogos.model';
import { ResponseApi } from './../../model/response.api';
import { JogoService } from './jogo.service';
import { JogoFilter } from './../../model/jogo-filter.model';
import { Component, OnInit, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Estatistica } from 'src/app/model';


@Component({
  selector: 'app-jogo',
  templateUrl: './jogo.component.html',
  styleUrls: ['./jogo.component.css']
})
export class JogoComponent implements OnInit {

  jogoFilter: JogoFilter = new JogoFilter('', '', '', new Array<string>());
  jogos: Jogos = new Jogos();
  data: any[];
  public informacoesDezenas: Array<Estatistica> = new Array<Estatistica>();
  displayedColumns: string[] = ['dezena','percentual','vezesSorteadas'];

  @ViewChild("form", {static: true})
  form: NgForm;

  constructor(
    private jogoService: JogoService
  ) {
    this.jogoFilter.usaPorcentagens = 'true';
   }

  ngOnInit() {
  }

  public carregaJogo() {
    console.log(this.jogoFilter.numeroDeDezenasPorJogo);
    console.log(this.jogoFilter.numeroDeJogos);
    console.log(this.jogoFilter.usaPorcentagens);

    this.jogoService.geraJogo(this.jogoFilter).subscribe((responseApi: ResponseApi) => {
      this.jogos = responseApi.data;
      this.informacoesDezenas = this.jogos.estatisticas.informacoesDezenas;

      console.log('--' + JSON.stringify(this.data));
    }, err => {
      console.log('erro');
    });
  }
}
