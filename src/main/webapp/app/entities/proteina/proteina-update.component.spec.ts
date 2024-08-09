/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ProteinaUpdate from './proteina-update.vue';
import ProteinaService from './proteina.service';
import AlertService from '@/shared/alert/alert.service';

import CuradoriaService from '@/entities/curadoria/curadoria.service';
import VersaoService from '@/entities/versao/versao.service';
import GeneService from '@/entities/gene/gene.service';
import ReferenciaService from '@/entities/referencia/referencia.service';
import RecursoService from '@/entities/recurso/recurso.service';

type ProteinaUpdateComponentType = InstanceType<typeof ProteinaUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const proteinaSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ProteinaUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Proteina Management Update Component', () => {
    let comp: ProteinaUpdateComponentType;
    let proteinaServiceStub: SinonStubbedInstance<ProteinaService>;

    beforeEach(() => {
      route = {};
      proteinaServiceStub = sinon.createStubInstance<ProteinaService>(ProteinaService);
      proteinaServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          proteinaService: () => proteinaServiceStub,
          curadoriaService: () =>
            sinon.createStubInstance<CuradoriaService>(CuradoriaService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          versaoService: () =>
            sinon.createStubInstance<VersaoService>(VersaoService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          geneService: () =>
            sinon.createStubInstance<GeneService>(GeneService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          referenciaService: () =>
            sinon.createStubInstance<ReferenciaService>(ReferenciaService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          recursoService: () =>
            sinon.createStubInstance<RecursoService>(RecursoService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(ProteinaUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.proteina = proteinaSample;
        proteinaServiceStub.update.resolves(proteinaSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(proteinaServiceStub.update.calledWith(proteinaSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        proteinaServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ProteinaUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.proteina = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(proteinaServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        proteinaServiceStub.find.resolves(proteinaSample);
        proteinaServiceStub.retrieve.resolves([proteinaSample]);

        // WHEN
        route = {
          params: {
            proteinaId: '' + proteinaSample.id,
          },
        };
        const wrapper = shallowMount(ProteinaUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.proteina).toMatchObject(proteinaSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        proteinaServiceStub.find.resolves(proteinaSample);
        const wrapper = shallowMount(ProteinaUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
