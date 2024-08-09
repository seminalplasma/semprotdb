import { type ICuradoria } from '@/shared/model/curadoria.model';
import { type IOrganismo } from '@/shared/model/organismo.model';

export interface IGene {
  id?: number;
  nome?: string;
  descricao?: string | null;
  curadoria?: ICuradoria | null;
  organismo?: IOrganismo | null;
}

export class Gene implements IGene {
  constructor(
    public id?: number,
    public nome?: string,
    public descricao?: string | null,
    public curadoria?: ICuradoria | null,
    public organismo?: IOrganismo | null,
  ) {}
}
