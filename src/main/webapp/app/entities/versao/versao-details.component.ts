import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import VersaoService from './versao.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat } from '@/shared/composables';
import { type IVersao } from '@/shared/model/versao.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'VersaoDetails',
  setup() {
    const dateFormat = useDateFormat();
    const versaoService = inject('versaoService', () => new VersaoService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const versao: Ref<IVersao> = ref({});

    const retrieveVersao = async versaoId => {
      try {
        const res = await versaoService().find(versaoId);
        versao.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.versaoId) {
      retrieveVersao(route.params.versaoId);
    }

    return {
      ...dateFormat,
      alertService,
      versao,

      ...dataUtils,

      previousState,
      t$: useI18n().t,
    };
  },
});
