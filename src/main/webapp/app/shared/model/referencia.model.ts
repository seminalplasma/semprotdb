import { type IProteina } from '@/shared/model/proteina.model';

export interface IReferencia {
  id?: number;
  citacao?: string;
  link?: string | null;
  ano?: number | null;
  autores?: string | null;
  proteinas?: IProteina[] | null;
}

export class Referencia implements IReferencia {
  constructor(
    public id?: number,
    public citacao?: string,
    public link?: string | null,
    public ano?: number | null,
    public autores?: string | null,
    public proteinas?: IProteina[] | null,
  ) {}
}
