/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import VersaoDetails from './versao-details.vue';
import VersaoService from './versao.service';
import AlertService from '@/shared/alert/alert.service';

type VersaoDetailsComponentType = InstanceType<typeof VersaoDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const versaoSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Versao Management Detail Component', () => {
    let versaoServiceStub: SinonStubbedInstance<VersaoService>;
    let mountOptions: MountingOptions<VersaoDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      versaoServiceStub = sinon.createStubInstance<VersaoService>(VersaoService);

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
          versaoService: () => versaoServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        versaoServiceStub.find.resolves(versaoSample);
        route = {
          params: {
            versaoId: '' + 123,
          },
        };
        const wrapper = shallowMount(VersaoDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.versao).toMatchObject(versaoSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        versaoServiceStub.find.resolves(versaoSample);
        const wrapper = shallowMount(VersaoDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
