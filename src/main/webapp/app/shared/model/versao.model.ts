import { type Status } from '@/shared/model/enumerations/status.model';
export interface IVersao {
  id?: number;
  nome?: string;
  detalhes?: string | null;
  release?: Date | null;
  label?: string | null;
  status?: keyof typeof Status;
  numero?: number;
  logo?: string | null;
  log?: string | null;
  texto?: string | null;
  imagemContentType?: string | null;
  imagem?: string | null;
}

export class Versao implements IVersao {
  constructor(
    public id?: number,
    public nome?: string,
    public detalhes?: string | null,
    public release?: Date | null,
    public label?: string | null,
    public status?: keyof typeof Status,
    public numero?: number,
    public logo?: string | null,
    public log?: string | null,
    public texto?: string | null,
    public imagemContentType?: string | null,
    public imagem?: string | null,
  ) {}

  with_status(s: Status): IVersao {
    this.status = s;
    return this;
  }

  with_log(l: string): IVersao {
    this.log = l;
    return this;
  }
}
