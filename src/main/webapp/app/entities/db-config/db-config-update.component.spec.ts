/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import DBConfigUpdate from './db-config-update.vue';
import DBConfigService from './db-config.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

type DBConfigUpdateComponentType = InstanceType<typeof DBConfigUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const dBConfigSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<DBConfigUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('DBConfig Management Update Component', () => {
    let comp: DBConfigUpdateComponentType;
    let dBConfigServiceStub: SinonStubbedInstance<DBConfigService>;

    beforeEach(() => {
      route = {};
      dBConfigServiceStub = sinon.createStubInstance<DBConfigService>(DBConfigService);
      dBConfigServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          dBConfigService: () => dBConfigServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(DBConfigUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(DBConfigUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.dBConfig = dBConfigSample;
        dBConfigServiceStub.update.resolves(dBConfigSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(dBConfigServiceStub.update.calledWith(dBConfigSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        dBConfigServiceStub.create.resolves(entity);
        const wrapper = shallowMount(DBConfigUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.dBConfig = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(dBConfigServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        dBConfigServiceStub.find.resolves(dBConfigSample);
        dBConfigServiceStub.retrieve.resolves([dBConfigSample]);

        // WHEN
        route = {
          params: {
            dBConfigId: '' + dBConfigSample.id,
          },
        };
        const wrapper = shallowMount(DBConfigUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.dBConfig).toMatchObject(dBConfigSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        dBConfigServiceStub.find.resolves(dBConfigSample);
        const wrapper = shallowMount(DBConfigUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
