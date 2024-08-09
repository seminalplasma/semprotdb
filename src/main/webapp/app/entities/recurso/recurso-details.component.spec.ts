/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import RecursoDetails from './recurso-details.vue';
import RecursoService from './recurso.service';
import AlertService from '@/shared/alert/alert.service';

type RecursoDetailsComponentType = InstanceType<typeof RecursoDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const recursoSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Recurso Management Detail Component', () => {
    let recursoServiceStub: SinonStubbedInstance<RecursoService>;
    let mountOptions: MountingOptions<RecursoDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      recursoServiceStub = sinon.createStubInstance<RecursoService>(RecursoService);

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
          recursoService: () => recursoServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        recursoServiceStub.find.resolves(recursoSample);
        route = {
          params: {
            recursoId: '' + 123,
          },
        };
        const wrapper = shallowMount(RecursoDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.recurso).toMatchObject(recursoSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        recursoServiceStub.find.resolves(recursoSample);
        const wrapper = shallowMount(RecursoDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
