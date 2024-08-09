import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import RecursoService from './recurso.service';
import { type IRecurso } from '@/shared/model/recurso.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'RecursoDetails',
  setup() {
    const recursoService = inject('recursoService', () => new RecursoService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const recurso: Ref<IRecurso> = ref({});

    const retrieveRecurso = async recursoId => {
      try {
        const res = await recursoService().find(recursoId);
        recurso.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.recursoId) {
      retrieveRecurso(route.params.recursoId);
    }

    return {
      alertService,
      recurso,

      previousState,
      t$: useI18n().t,
    };
  },
});
