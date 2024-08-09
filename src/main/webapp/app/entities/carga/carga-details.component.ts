import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import CargaService from './carga.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { type ICarga } from '@/shared/model/carga.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CargaDetails',
  setup() {
    const cargaService = inject('cargaService', () => new CargaService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const carga: Ref<ICarga> = ref({});

    const retrieveCarga = async cargaId => {
      try {
        const res = await cargaService().find(cargaId);
        carga.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.cargaId) {
      retrieveCarga(route.params.cargaId);
    }

    return {
      alertService,
      carga,

      ...dataUtils,

      previousState,
      t$: useI18n().t,
    };
  },
});
