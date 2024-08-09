import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import ReferenciaService from './referencia.service';
import { type IReferencia } from '@/shared/model/referencia.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ReferenciaDetails',
  setup() {
    const referenciaService = inject('referenciaService', () => new ReferenciaService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const referencia: Ref<IReferencia> = ref({});

    const retrieveReferencia = async referenciaId => {
      try {
        const res = await referenciaService().find(referenciaId);
        referencia.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.referenciaId) {
      retrieveReferencia(route.params.referenciaId);
    }

    return {
      alertService,
      referencia,

      previousState,
      t$: useI18n().t,
    };
  },
});
