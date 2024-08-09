/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ProteinaDetails from './proteina-details.vue';
import ProteinaService from './proteina.service';
import AlertService from '@/shared/alert/alert.service';

type ProteinaDetailsComponentType = InstanceType<typeof ProteinaDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const proteinaSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Proteina Management Detail Component', () => {
    let proteinaServiceStub: SinonStubbedInstance<ProteinaService>;
    let mountOptions: MountingOptions<ProteinaDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      proteinaServiceStub = sinon.createStubInstance<ProteinaService>(ProteinaService);

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
          proteinaService: () => proteinaServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        proteinaServiceStub.find.resolves(proteinaSample);
        route = {
          params: {
            proteinaId: '' + 123,
          },
        };
        const wrapper = shallowMount(ProteinaDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.proteina).toMatchObject(proteinaSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        proteinaServiceStub.find.resolves(proteinaSample);
        const wrapper = shallowMount(ProteinaDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
