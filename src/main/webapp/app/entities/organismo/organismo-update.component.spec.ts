/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import OrganismoUpdate from './organismo-update.vue';
import OrganismoService from './organismo.service';
import AlertService from '@/shared/alert/alert.service';

type OrganismoUpdateComponentType = InstanceType<typeof OrganismoUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const organismoSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<OrganismoUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Organismo Management Update Component', () => {
    let comp: OrganismoUpdateComponentType;
    let organismoServiceStub: SinonStubbedInstance<OrganismoService>;

    beforeEach(() => {
      route = {};
      organismoServiceStub = sinon.createStubInstance<OrganismoService>(OrganismoService);
      organismoServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          organismoService: () => organismoServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(OrganismoUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.organismo = organismoSample;
        organismoServiceStub.update.resolves(organismoSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(organismoServiceStub.update.calledWith(organismoSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        organismoServiceStub.create.resolves(entity);
        const wrapper = shallowMount(OrganismoUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.organismo = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(organismoServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        organismoServiceStub.find.resolves(organismoSample);
        organismoServiceStub.retrieve.resolves([organismoSample]);

        // WHEN
        route = {
          params: {
            organismoId: '' + organismoSample.id,
          },
        };
        const wrapper = shallowMount(OrganismoUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.organismo).toMatchObject(organismoSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        organismoServiceStub.find.resolves(organismoSample);
        const wrapper = shallowMount(OrganismoUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
