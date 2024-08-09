/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import CargaUpdate from './carga-update.vue';
import CargaService from './carga.service';
import AlertService from '@/shared/alert/alert.service';

import VersaoService from '@/entities/versao/versao.service';

type CargaUpdateComponentType = InstanceType<typeof CargaUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const cargaSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<CargaUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Carga Management Update Component', () => {
    let comp: CargaUpdateComponentType;
    let cargaServiceStub: SinonStubbedInstance<CargaService>;

    beforeEach(() => {
      route = {};
      cargaServiceStub = sinon.createStubInstance<CargaService>(CargaService);
      cargaServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          cargaService: () => cargaServiceStub,
          versaoService: () =>
            sinon.createStubInstance<VersaoService>(VersaoService, {
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
        const wrapper = shallowMount(CargaUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.carga = cargaSample;
        cargaServiceStub.update.resolves(cargaSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(cargaServiceStub.update.calledWith(cargaSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        cargaServiceStub.create.resolves(entity);
        const wrapper = shallowMount(CargaUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.carga = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(cargaServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        cargaServiceStub.find.resolves(cargaSample);
        cargaServiceStub.retrieve.resolves([cargaSample]);

        // WHEN
        route = {
          params: {
            cargaId: '' + cargaSample.id,
          },
        };
        const wrapper = shallowMount(CargaUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.carga).toMatchObject(cargaSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        cargaServiceStub.find.resolves(cargaSample);
        const wrapper = shallowMount(CargaUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
