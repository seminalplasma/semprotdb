export interface ICuradoria {
  id?: number;
  email?: string;
  data?: Date;
  anotacoes?: string | null;
}

export class Curadoria implements ICuradoria {
  constructor(
    public id?: number,
    public email?: string,
    public data?: Date,
    public anotacoes?: string | null,
  ) {}
}
