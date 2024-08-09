export interface IOrganismo {
  id?: number;
  nome?: string;
  sigla?: string | null;
  apelido?: string | null;
  silhuetaContentType?: string | null;
  silhueta?: string | null;
  icone?: string | null;
  pos?: string | null;
  imagem?: string | null;
  descricao?: string | null;
}

export class Organismo implements IOrganismo {
  constructor(
    public id?: number,
    public nome?: string,
    public sigla?: string | null,
    public apelido?: string | null,
    public silhuetaContentType?: string | null,
    public silhueta?: string | null,
    public icone?: string | null,
    public pos?: string | null,
    public imagem?: string | null,
    public descricao?: string | null,
  ) {}
}
