/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import GeneUpdate from './gene-update.vue';
import GeneService from './gene.service';
import AlertService from '@/shared/alert/alert.service';

import CuradoriaService from '@/entities/curadoria/curadoria.service';
import OrganismoService from '@/entities/organismo/organismo.service';

type GeneUpdateComponentType = InstanceType<typeof GeneUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const geneSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<GeneUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Gene Management Update Component', () => {
    let comp: GeneUpdateComponentType;
    let geneServiceStub: SinonStubbedInstance<GeneService>;

    beforeEach(() => {
      route = {};
      geneServiceStub = sinon.createStubInstance<GeneService>(GeneService);
      geneServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          geneService: () => geneServiceStub,
          curadoriaService: () =>
            sinon.createStubInstance<CuradoriaService>(CuradoriaService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          organismoService: () =>
            sinon.createStubInstance<OrganismoService>(OrganismoService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(GeneUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.gene = geneSample;
        geneServiceStub.update.resolves(geneSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(geneServiceStub.update.calledWith(geneSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        geneServiceStub.create.resolves(entity);
        const wrapper = shallowMount(GeneUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.gene = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(geneServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        geneServiceStub.find.resolves(geneSample);
        geneServiceStub.retrieve.resolves([geneSample]);

        // WHEN
        route = {
          params: {
            geneId: '' + geneSample.id,
          },
        };
        const wrapper = shallowMount(GeneUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.gene).toMatchObject(geneSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        geneServiceStub.find.resolves(geneSample);
        const wrapper = shallowMount(GeneUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
