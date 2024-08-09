import { Authority } from '@/shared/security/authority';
/* tslint:disable */
// prettier-ignore
const Entities = () => import('@/entities/entities.vue');

const Curadoria = () => import('@/entities/curadoria/curadoria.vue');
const CuradoriaUpdate = () => import('@/entities/curadoria/curadoria-update.vue');
const CuradoriaDetails = () => import('@/entities/curadoria/curadoria-details.vue');

const Carga = () => import('@/entities/carga/carga.vue');
const CargaUpdate = () => import('@/entities/carga/carga-update.vue');
const CargaDetails = () => import('@/entities/carga/carga-details.vue');

const Versao = () => import('@/entities/versao/versao.vue');
const VersaoUpdate = () => import('@/entities/versao/versao-update.vue');
const VersaoDetails = () => import('@/entities/versao/versao-details.vue');

const Referencia = () => import('@/entities/referencia/referencia.vue');
const ReferenciaUpdate = () => import('@/entities/referencia/referencia-update.vue');
const ReferenciaDetails = () => import('@/entities/referencia/referencia-details.vue');

const Organismo = () => import('@/entities/organismo/organismo.vue');
const OrganismoUpdate = () => import('@/entities/organismo/organismo-update.vue');
const OrganismoDetails = () => import('@/entities/organismo/organismo-details.vue');

const Gene = () => import('@/entities/gene/gene.vue');
const GeneUpdate = () => import('@/entities/gene/gene-update.vue');
const GeneDetails = () => import('@/entities/gene/gene-details.vue');

const Proteina = () => import('@/entities/proteina/proteina.vue');
const ProteinaUpdate = () => import('@/entities/proteina/proteina-update.vue');
const ProteinaDetails = () => import('@/entities/proteina/proteina-details.vue');

const Recurso = () => import('@/entities/recurso/recurso.vue');
const RecursoUpdate = () => import('@/entities/recurso/recurso-update.vue');
const RecursoDetails = () => import('@/entities/recurso/recurso-details.vue');

const DBConfig = () => import('@/entities/db-config/db-config.vue');
const DBConfigUpdate = () => import('@/entities/db-config/db-config-update.vue');
const DBConfigDetails = () => import('@/entities/db-config/db-config-details.vue');

// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default {
  path: '/',
  component: Entities,
  children: [
    {
      path: 'curadoria',
      name: 'Curadoria',
      component: Curadoria,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'curadoria/new',
      name: 'CuradoriaCreate',
      component: CuradoriaUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'curadoria/:curadoriaId/edit',
      name: 'CuradoriaEdit',
      component: CuradoriaUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'curadoria/:curadoriaId/view',
      name: 'CuradoriaView',
      component: CuradoriaDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'carga',
      name: 'Carga',
      component: Carga,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'carga/new',
      name: 'CargaCreate',
      component: CargaUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'carga/:cargaId/edit',
      name: 'CargaEdit',
      component: CargaUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'carga/:cargaId/view',
      name: 'CargaView',
      component: CargaDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'versao',
      name: 'Versao',
      component: Versao,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'versao/new',
      name: 'VersaoCreate',
      component: VersaoUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'versao/:versaoId/edit',
      name: 'VersaoEdit',
      component: VersaoUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'versao/:versaoId/view',
      name: 'VersaoView',
      component: VersaoDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'referencia',
      name: 'Referencia',
      component: Referencia,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'referencia/new',
      name: 'ReferenciaCreate',
      component: ReferenciaUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'referencia/:referenciaId/edit',
      name: 'ReferenciaEdit',
      component: ReferenciaUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'referencia/:referenciaId/view',
      name: 'ReferenciaView',
      component: ReferenciaDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'organismo',
      name: 'Organismo',
      component: Organismo,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'organismo/new',
      name: 'OrganismoCreate',
      component: OrganismoUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'organismo/:organismoId/edit',
      name: 'OrganismoEdit',
      component: OrganismoUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'organismo/:organismoId/view',
      name: 'OrganismoView',
      component: OrganismoDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'gene',
      name: 'Gene',
      component: Gene,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'gene/new',
      name: 'GeneCreate',
      component: GeneUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'gene/:geneId/edit',
      name: 'GeneEdit',
      component: GeneUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'gene/:geneId/view',
      name: 'GeneView',
      component: GeneDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'proteina',
      name: 'Proteina',
      component: Proteina,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'proteina/new',
      name: 'ProteinaCreate',
      component: ProteinaUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'proteina/:proteinaId/edit',
      name: 'ProteinaEdit',
      component: ProteinaUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'proteina/:proteinaId/view',
      name: 'ProteinaView',
      component: ProteinaDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'recurso',
      name: 'Recurso',
      component: Recurso,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'recurso/new',
      name: 'RecursoCreate',
      component: RecursoUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'recurso/:recursoId/edit',
      name: 'RecursoEdit',
      component: RecursoUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'recurso/:recursoId/view',
      name: 'RecursoView',
      component: RecursoDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'db-config',
      name: 'DBConfig',
      component: DBConfig,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'db-config/new',
      name: 'DBConfigCreate',
      component: DBConfigUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'db-config/:dBConfigId/edit',
      name: 'DBConfigEdit',
      component: DBConfigUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'db-config/:dBConfigId/view',
      name: 'DBConfigView',
      component: DBConfigDetails,
      meta: { authorities: [Authority.USER] },
    },
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};
