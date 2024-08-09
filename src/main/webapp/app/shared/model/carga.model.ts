import { type IVersao } from '@/shared/model/versao.model';

import { type Tipo } from '@/shared/model/enumerations/tipo.model';
import { type Formato } from '@/shared/model/enumerations/formato.model';
import { type Destino } from '@/shared/model/enumerations/destino.model';
export interface ICarga {
  id?: number;
  status?: string | null;
  ordem?: number | null;
  planilhaContentType?: string | null;
  planilha?: string | null;
  nome?: string;
  caminho?: string | null;
  validado?: boolean | null;
  tipo?: keyof typeof Tipo;
  formato?: keyof typeof Formato;
  destino?: keyof typeof Destino | null;
  linhas?: number | null;
  checksum?: string | null;
  versao?: IVersao | null;
}

export class Carga implements ICarga {
  constructor(
    public id?: number,
    public status?: string | null,
    public ordem?: number | null,
    public planilhaContentType?: string | null,
    public planilha?: string | null,
    public nome?: string,
    public caminho?: string | null,
    public validado?: boolean | null,
    public tipo?: keyof typeof Tipo,
    public formato?: keyof typeof Formato,
    public destino?: keyof typeof Destino | null,
    public linhas?: number | null,
    public checksum?: string | null,
    public versao?: IVersao | null,
  ) {
    this.validado = this.validado ?? false;
  }
}
