/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import CuradoriaDetails from './curadoria-details.vue';
import CuradoriaService from './curadoria.service';
import AlertService from '@/shared/alert/alert.service';

type CuradoriaDetailsComponentType = InstanceType<typeof CuradoriaDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const curadoriaSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Curadoria Management Detail Component', () => {
    let curadoriaServiceStub: SinonStubbedInstance<CuradoriaService>;
    let mountOptions: MountingOptions<CuradoriaDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      curadoriaServiceStub = sinon.createStubInstance<CuradoriaService>(CuradoriaService);

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
          curadoriaService: () => curadoriaServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        curadoriaServiceStub.find.resolves(curadoriaSample);
        route = {
          params: {
            curadoriaId: '' + 123,
          },
        };
        const wrapper = shallowMount(CuradoriaDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.curadoria).toMatchObject(curadoriaSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        curadoriaServiceStub.find.resolves(curadoriaSample);
        const wrapper = shallowMount(CuradoriaDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
