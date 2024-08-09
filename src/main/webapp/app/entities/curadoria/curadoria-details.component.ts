import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import CuradoriaService from './curadoria.service';
import { useDateFormat } from '@/shared/composables';
import { type ICuradoria } from '@/shared/model/curadoria.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CuradoriaDetails',
  setup() {
    const dateFormat = useDateFormat();
    const curadoriaService = inject('curadoriaService', () => new CuradoriaService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const curadoria: Ref<ICuradoria> = ref({});

    const retrieveCuradoria = async curadoriaId => {
      try {
        const res = await curadoriaService().find(curadoriaId);
        curadoria.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.curadoriaId) {
      retrieveCuradoria(route.params.curadoriaId);
    }

    return {
      ...dateFormat,
      alertService,
      curadoria,

      previousState,
      t$: useI18n().t,
    };
  },
});
