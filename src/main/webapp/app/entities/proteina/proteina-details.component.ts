import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import ProteinaService from './proteina.service';
import { type IProteina } from '@/shared/model/proteina.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ProteinaDetails',
  setup() {
    const proteinaService = inject('proteinaService', () => new ProteinaService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const proteina: Ref<IProteina> = ref({});

    const retrieveProteina = async proteinaId => {
      try {
        const res = await proteinaService().find(proteinaId);
        proteina.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.proteinaId) {
      retrieveProteina(route.params.proteinaId);
    }

    return {
      alertService,
      proteina,

      previousState,
      t$: useI18n().t,
    };
  },
});
