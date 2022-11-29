import { MEGASENA_API } from './../../megasena.api';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { JogoFilter } from 'src/app/model';

@Injectable({
  providedIn: 'root'
})
export class JogoService {

  constructor(
    private http: HttpClient
  ) { }

  geraJogo(jogoFilter: JogoFilter) {
    return this.http.post(`${MEGASENA_API}/api/megasena`, jogoFilter);
  }
}
