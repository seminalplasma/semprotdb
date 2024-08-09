import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import RecursoService from './recurso.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import ProteinaService from '@/entities/proteina/proteina.service';
import { type IProteina } from '@/shared/model/proteina.model';
import { type IRecurso, Recurso } from '@/shared/model/recurso.model';
import { BioDB } from '@/shared/model/enumerations/bio-db.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'RecursoUpdate',
  setup() {
    const recursoService = inject('recursoService', () => new RecursoService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const recurso: Ref<IRecurso> = ref(new Recurso());

    const proteinaService = inject('proteinaService', () => new ProteinaService());

    const proteinas: Ref<IProteina[]> = ref([]);
    const bioDBValues: Ref<string[]> = ref(Object.keys(BioDB));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

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
      uid: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      db: {},
      link: {},
      proteinas: {},
    };
    const v$ = useVuelidate(validationRules, recurso as any);
    v$.value.$validate();

    return {
      recursoService,
      alertService,
      recurso,
      previousState,
      bioDBValues,
      isSaving,
      currentLanguage,
      proteinas,
      v$,
      t$,
    };
  },
  created(): void {
    this.recurso.proteinas = [];
  },
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.recurso.id) {
        this.recursoService()
          .update(this.recurso)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('semprotdbApp.recurso.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.recursoService()
          .create(this.recurso)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('semprotdbApp.recurso.created', { param: param.id }).toString());
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
