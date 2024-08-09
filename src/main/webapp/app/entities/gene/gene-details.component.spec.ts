/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import GeneDetails from './gene-details.vue';
import GeneService from './gene.service';
import AlertService from '@/shared/alert/alert.service';

type GeneDetailsComponentType = InstanceType<typeof GeneDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const geneSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Gene Management Detail Component', () => {
    let geneServiceStub: SinonStubbedInstance<GeneService>;
    let mountOptions: MountingOptions<GeneDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      geneServiceStub = sinon.createStubInstance<GeneService>(GeneService);

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
          geneService: () => geneServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        geneServiceStub.find.resolves(geneSample);
        route = {
          params: {
            geneId: '' + 123,
          },
        };
        const wrapper = shallowMount(GeneDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.gene).toMatchObject(geneSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        geneServiceStub.find.resolves(geneSample);
        const wrapper = shallowMount(GeneDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
