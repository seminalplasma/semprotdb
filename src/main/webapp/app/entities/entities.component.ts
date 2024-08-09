import { defineComponent, provide } from 'vue';

import CuradoriaService from './curadoria/curadoria.service';
import CargaService from './carga/carga.service';
import VersaoService from './versao/versao.service';
import ReferenciaService from './referencia/referencia.service';
import OrganismoService from './organismo/organismo.service';
import GeneService from './gene/gene.service';
import ProteinaService from './proteina/proteina.service';
import RecursoService from './recurso/recurso.service';
import DBConfigService from './db-config/db-config.service';
import UserService from '@/entities/user/user.service';
// jhipster-needle-add-entity-service-to-entities-component-import - JHipster will import entities services here

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Entities',
  setup() {
    provide('userService', () => new UserService());
    provide('curadoriaService', () => new CuradoriaService());
    provide('cargaService', () => new CargaService());
    provide('versaoService', () => new VersaoService());
    provide('referenciaService', () => new ReferenciaService());
    provide('organismoService', () => new OrganismoService());
    provide('geneService', () => new GeneService());
    provide('proteinaService', () => new ProteinaService());
    provide('recursoService', () => new RecursoService());
    provide('dBConfigService', () => new DBConfigService());
    // jhipster-needle-add-entity-service-to-entities-component - JHipster will import entities services here
  },
});
