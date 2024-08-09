import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import CuradoriaService from './curadoria.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type ICuradoria, Curadoria } from '@/shared/model/curadoria.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CuradoriaUpdate',
  setup() {
    const curadoriaService = inject('curadoriaService', () => new CuradoriaService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const curadoria: Ref<ICuradoria> = ref(new Curadoria());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveCuradoria = async curadoriaId => {
      try {
        const res = await curadoriaService().find(curadoriaId);
        res.data = new Date(res.data);
        curadoria.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.curadoriaId) {
      retrieveCuradoria(route.params.curadoriaId);
    }

    const initRelationships = () => {};

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      email: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      data: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      anotacoes: {},
      proteinas: {},
      genes: {},
    };
    const v$ = useVuelidate(validationRules, curadoria as any);
    v$.value.$validate();

    return {
      curadoriaService,
      alertService,
      curadoria,
      previousState,
      isSaving,
      currentLanguage,
      v$,
      ...useDateFormat({ entityRef: curadoria }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.curadoria.id) {
        this.curadoriaService()
          .update(this.curadoria)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('semprotdbApp.curadoria.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.curadoriaService()
          .create(this.curadoria)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('semprotdbApp.curadoria.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
