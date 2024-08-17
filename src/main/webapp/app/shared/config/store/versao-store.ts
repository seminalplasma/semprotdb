import { defineStore } from 'pinia';
import { Versao } from '@/shared/model/versao.model';
import { Status } from '@/shared/model/enumerations/status.model';

export interface IVersaoStoreState {
  selecionada: Versao;
  carregado: boolean;
  disponiveis: Array<Versao>;
  liberadas: Array<Versao>;
  carregadas: Array<Versao>;
}

export const initialState: IVersaoStoreState = {
  selecionada: undefined,
  carregado: false,
  disponiveis: new Array<Versao>(),
  liberadas: new Array<Versao>(),
  carregadas: new Array<Versao>(),
};

export const useVersaoStore = defineStore('versaoStore', {
  state: (): IVersaoStoreState => ({ ...initialState }),

  getters: {
    versao: state => state.selecionada,
    versoes: state => state.disponiveis,
    visiveis: state => state.disponiveis.filter(v => v.status === Status.DISPONIVEL),
    esta_carregada: state => state.carregadas.includes(state.selecionada),
  },

  actions: {
    setList(versoes) {
      this.disponiveis = new Array(...versoes);
      const nomev = localStorage.getItem('currentVersion');
      const v = this.disponiveis.find(v => v.nome === nomev);
      if (v === undefined) this.chooseVersao(this.disponiveis.length > 0 ? versoes[0] : null);
      else this.chooseVersao(v);
    },

    chooseVersao(versao: Versao) {
      if (!versao || !versao.id) {
        this.carregado = true;
        return;
      }
      this.selecionada = versao;
      this.carregado = this.esta_carregada;
      localStorage.setItem('currentVersion', versao.nome);
    },

    loadVersao(versao: Versao) {
      this.carregado = true;

      if (this.carregadas.includes(versao)) return;

      this.carregadas.push(versao);
      this.disponiveis = this.disponiveis.filter(x => x.id !== versao.id);
      this.disponiveis.push(versao);
      this.selecionada = versao;
    },
  },
});
