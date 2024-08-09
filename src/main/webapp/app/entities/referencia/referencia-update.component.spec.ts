/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ReferenciaUpdate from './referencia-update.vue';
import ReferenciaService from './referencia.service';
import AlertService from '@/shared/alert/alert.service';

import ProteinaService from '@/entities/proteina/proteina.service';

type ReferenciaUpdateComponentType = InstanceType<typeof ReferenciaUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const referenciaSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ReferenciaUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Referencia Management Update Component', () => {
    let comp: ReferenciaUpdateComponentType;
    let referenciaServiceStub: SinonStubbedInstance<ReferenciaService>;

    beforeEach(() => {
      route = {};
      referenciaServiceStub = sinon.createStubInstance<ReferenciaService>(ReferenciaService);
      referenciaServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          referenciaService: () => referenciaServiceStub,
          proteinaService: () =>
            sinon.createStubInstance<ProteinaService>(ProteinaService, {
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
        const wrapper = shallowMount(ReferenciaUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.referencia = referenciaSample;
        referenciaServiceStub.update.resolves(referenciaSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(referenciaServiceStub.update.calledWith(referenciaSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        referenciaServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ReferenciaUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.referencia = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(referenciaServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        referenciaServiceStub.find.resolves(referenciaSample);
        referenciaServiceStub.retrieve.resolves([referenciaSample]);

        // WHEN
        route = {
          params: {
            referenciaId: '' + referenciaSample.id,
          },
        };
        const wrapper = shallowMount(ReferenciaUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.referencia).toMatchObject(referenciaSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        referenciaServiceStub.find.resolves(referenciaSample);
        const wrapper = shallowMount(ReferenciaUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
