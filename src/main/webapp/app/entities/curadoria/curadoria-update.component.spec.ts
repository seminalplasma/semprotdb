/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import CuradoriaUpdate from './curadoria-update.vue';
import CuradoriaService from './curadoria.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

type CuradoriaUpdateComponentType = InstanceType<typeof CuradoriaUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const curadoriaSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<CuradoriaUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Curadoria Management Update Component', () => {
    let comp: CuradoriaUpdateComponentType;
    let curadoriaServiceStub: SinonStubbedInstance<CuradoriaService>;

    beforeEach(() => {
      route = {};
      curadoriaServiceStub = sinon.createStubInstance<CuradoriaService>(CuradoriaService);
      curadoriaServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          curadoriaService: () => curadoriaServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(CuradoriaUpdate, { global: mountOptions });
        comp = wrapper.vm;
      });
      it('Should convert date from string', () => {
        // GIVEN
        const date = new Date('2019-10-15T11:42:02Z');

        // WHEN
        const convertedDate = comp.convertDateTimeFromServer(date);

        // THEN
        expect(convertedDate).toEqual(dayjs(date).format(DATE_TIME_LONG_FORMAT));
      });

      it('Should not convert date if date is not present', () => {
        expect(comp.convertDateTimeFromServer(null)).toBeNull();
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(CuradoriaUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.curadoria = curadoriaSample;
        curadoriaServiceStub.update.resolves(curadoriaSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(curadoriaServiceStub.update.calledWith(curadoriaSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        curadoriaServiceStub.create.resolves(entity);
        const wrapper = shallowMount(CuradoriaUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.curadoria = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(curadoriaServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        curadoriaServiceStub.find.resolves(curadoriaSample);
        curadoriaServiceStub.retrieve.resolves([curadoriaSample]);

        // WHEN
        route = {
          params: {
            curadoriaId: '' + curadoriaSample.id,
          },
        };
        const wrapper = shallowMount(CuradoriaUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.curadoria).toMatchObject(curadoriaSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        curadoriaServiceStub.find.resolves(curadoriaSample);
        const wrapper = shallowMount(CuradoriaUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
