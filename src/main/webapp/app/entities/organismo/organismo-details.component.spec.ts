/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import OrganismoDetails from './organismo-details.vue';
import OrganismoService from './organismo.service';
import AlertService from '@/shared/alert/alert.service';

type OrganismoDetailsComponentType = InstanceType<typeof OrganismoDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const organismoSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Organismo Management Detail Component', () => {
    let organismoServiceStub: SinonStubbedInstance<OrganismoService>;
    let mountOptions: MountingOptions<OrganismoDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      organismoServiceStub = sinon.createStubInstance<OrganismoService>(OrganismoService);

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'router-link': true,
        },
        provide: {
          alertService,
          organismoService: () => organismoServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        organismoServiceStub.find.resolves(organismoSample);
        route = {
          params: {
            organismoId: '' + 123,
          },
        };
        const wrapper = shallowMount(OrganismoDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.organismo).toMatchObject(organismoSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        organismoServiceStub.find.resolves(organismoSample);
        const wrapper = shallowMount(OrganismoDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
