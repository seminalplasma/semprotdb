import { type ICuradoria } from '@/shared/model/curadoria.model';
import { type IVersao } from '@/shared/model/versao.model';
import { type IGene } from '@/shared/model/gene.model';
import { type IReferencia } from '@/shared/model/referencia.model';
import { type IRecurso } from '@/shared/model/recurso.model';

export interface IProteina {
  id?: number;
  nome?: string;
  tamanho?: number | null;
  massa?: string | null;
  descricao?: string | null;
  curadoria?: ICuradoria | null;
  versao?: IVersao | null;
  gene?: IGene | null;
  referencias?: IReferencia[] | null;
  recursos?: IRecurso[] | null;
}

export class Proteina implements IProteina {
  constructor(
    public id?: number,
    public nome?: string,
    public tamanho?: number | null,
    public massa?: string | null,
    public descricao?: string | null,
    public curadoria?: ICuradoria | null,
    public versao?: IVersao | null,
    public gene?: IGene | null,
    public referencias?: IReferencia[] | null,
    public recursos?: IRecurso[] | null,
  ) {}
}
