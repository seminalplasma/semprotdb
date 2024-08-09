import { type IProteina } from '@/shared/model/proteina.model';

import { type BioDB } from '@/shared/model/enumerations/bio-db.model';
export interface IRecurso {
  id?: number;
  uid?: string;
  db?: keyof typeof BioDB | null;
  link?: string | null;
  proteinas?: IProteina[] | null;
}

export class Recurso implements IRecurso {
  constructor(
    public id?: number,
    public uid?: string,
    public db?: keyof typeof BioDB | null,
    public link?: string | null,
    public proteinas?: IProteina[] | null,
  ) {}
}
