import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import ReferenciaService from './referencia.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import ProteinaService from '@/entities/proteina/proteina.service';
import { type IProteina } from '@/shared/model/proteina.model';
import { type IReferencia, Referencia } from '@/shared/model/referencia.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ReferenciaUpdate',
  setup() {
    const referenciaService = inject('referenciaService', () => new ReferenciaService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const referencia: Ref<IReferencia> = ref(new Referencia());

    const proteinaService = inject('proteinaService', () => new ProteinaService());

    const proteinas: Ref<IProteina[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

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

    const initRelationships = () => {
      proteinaService()
        .retrieve()
        .then(res => {
          proteinas.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      citacao: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      link: {},
      ano: {},
      autores: {},
      proteinas: {},
    };
    const v$ = useVuelidate(validationRules, referencia as any);
    v$.value.$validate();

    return {
      referenciaService,
      alertService,
      referencia,
      previousState,
      isSaving,
      currentLanguage,
      proteinas,
      v$,
      t$,
    };
  },
  created(): void {
    this.referencia.proteinas = [];
  },
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.referencia.id) {
        this.referenciaService()
          .update(this.referencia)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('semprotdbApp.referencia.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.referenciaService()
          .create(this.referencia)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('semprotdbApp.referencia.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },

    getSelected(selectedVals, option, pkField = 'id'): any {
      if (selectedVals) {
        return selectedVals.find(value => option[pkField] === value[pkField]) ?? option;
      }
      return option;
    },
  },
});
