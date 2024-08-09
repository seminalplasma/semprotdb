import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import OrganismoService from './organismo.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { type IOrganismo } from '@/shared/model/organismo.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'OrganismoDetails',
  setup() {
    const organismoService = inject('organismoService', () => new OrganismoService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const organismo: Ref<IOrganismo> = ref({});

    const retrieveOrganismo = async organismoId => {
      try {
        const res = await organismoService().find(organismoId);
        organismo.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.organismoId) {
      retrieveOrganismo(route.params.organismoId);
    }

    return {
      alertService,
      organismo,

      ...dataUtils,

      previousState,
      t$: useI18n().t,
    };
  },
});
