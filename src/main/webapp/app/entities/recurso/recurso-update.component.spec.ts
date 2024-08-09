/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import RecursoUpdate from './recurso-update.vue';
import RecursoService from './recurso.service';
import AlertService from '@/shared/alert/alert.service';

import ProteinaService from '@/entities/proteina/proteina.service';

type RecursoUpdateComponentType = InstanceType<typeof RecursoUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const recursoSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<RecursoUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Recurso Management Update Component', () => {
    let comp: RecursoUpdateComponentType;
    let recursoServiceStub: SinonStubbedInstance<RecursoService>;

    beforeEach(() => {
      route = {};
      recursoServiceStub = sinon.createStubInstance<RecursoService>(RecursoService);
      recursoServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          recursoService: () => recursoServiceStub,
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
        const wrapper = shallowMount(RecursoUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.recurso = recursoSample;
        recursoServiceStub.update.resolves(recursoSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(recursoServiceStub.update.calledWith(recursoSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        recursoServiceStub.create.resolves(entity);
        const wrapper = shallowMount(RecursoUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.recurso = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(recursoServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        recursoServiceStub.find.resolves(recursoSample);
        recursoServiceStub.retrieve.resolves([recursoSample]);

        // WHEN
        route = {
          params: {
            recursoId: '' + recursoSample.id,
          },
        };
        const wrapper = shallowMount(RecursoUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.recurso).toMatchObject(recursoSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        recursoServiceStub.find.resolves(recursoSample);
        const wrapper = shallowMount(RecursoUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
