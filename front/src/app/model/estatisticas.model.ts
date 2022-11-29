import { Estatistica } from './estatistica.model';
export class Estatisticas {
    constructor(
        public informacoesDezenas: Array<Estatistica>,
        public totalDeDezenasSorteadas: number
    ) {}
}
