export interface IDBConfig {
  id?: number;
  key?: string;
  habilitado?: boolean | null;
  vstring?: string | null;
  vbol?: boolean | null;
  vdate?: Date | null;
  vint?: number | null;
  vtext?: string | null;
  vimgContentType?: string | null;
  vimg?: string | null;
}

export class DBConfig implements IDBConfig {
  constructor(
    public id?: number,
    public key?: string,
    public habilitado?: boolean | null,
    public vstring?: string | null,
    public vbol?: boolean | null,
    public vdate?: Date | null,
    public vint?: number | null,
    public vtext?: string | null,
    public vimgContentType?: string | null,
    public vimg?: string | null,
  ) {
    this.habilitado = this.habilitado ?? false;
    this.vbol = this.vbol ?? false;
  }
}
