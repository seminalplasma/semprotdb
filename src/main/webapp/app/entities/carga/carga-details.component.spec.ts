/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import CargaDetails from './carga-details.vue';
import CargaService from './carga.service';
import AlertService from '@/shared/alert/alert.service';

type CargaDetailsComponentType = InstanceType<typeof CargaDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const cargaSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Carga Management Detail Component', () => {
    let cargaServiceStub: SinonStubbedInstance<CargaService>;
    let mountOptions: MountingOptions<CargaDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      cargaServiceStub = sinon.createStubInstance<CargaService>(CargaService);

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
          cargaService: () => cargaServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        cargaServiceStub.find.resolves(cargaSample);
        route = {
          params: {
            cargaId: '' + 123,
          },
        };
        const wrapper = shallowMount(CargaDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.carga).toMatchObject(cargaSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        cargaServiceStub.find.resolves(cargaSample);
        const wrapper = shallowMount(CargaDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
