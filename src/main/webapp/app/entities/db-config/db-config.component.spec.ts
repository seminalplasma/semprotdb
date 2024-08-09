/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import DBConfig from './db-config.vue';
import DBConfigService from './db-config.service';
import AlertService from '@/shared/alert/alert.service';

type DBConfigComponentType = InstanceType<typeof DBConfig>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('DBConfig Management Component', () => {
    let dBConfigServiceStub: SinonStubbedInstance<DBConfigService>;
    let mountOptions: MountingOptions<DBConfigComponentType>['global'];

    beforeEach(() => {
      dBConfigServiceStub = sinon.createStubInstance<DBConfigService>(DBConfigService);
      dBConfigServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          dBConfigService: () => dBConfigServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        dBConfigServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(DBConfig, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(dBConfigServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.dBConfigs[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: DBConfigComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(DBConfig, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        dBConfigServiceStub.retrieve.reset();
        dBConfigServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        dBConfigServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeDBConfig();
        await comp.$nextTick(); // clear components

        // THEN
        expect(dBConfigServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(dBConfigServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
