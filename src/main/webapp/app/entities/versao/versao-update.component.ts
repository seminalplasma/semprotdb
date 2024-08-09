import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import VersaoService from './versao.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type IVersao, Versao } from '@/shared/model/versao.model';
import { Status } from '@/shared/model/enumerations/status.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'VersaoUpdate',
  setup() {
    const versaoService = inject('versaoService', () => new VersaoService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const versao: Ref<IVersao> = ref(new Versao());
    const statusValues: Ref<string[]> = ref(Object.keys(Status));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveVersao = async versaoId => {
      try {
        const res = await versaoService().find(versaoId);
        res.release = new Date(res.release);
        versao.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.versaoId) {
      retrieveVersao(route.params.versaoId);
    }

    const initRelationships = () => {};

    initRelationships();

    const dataUtils = useDataUtils();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      nome: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      detalhes: {},
      release: {},
      label: {},
      status: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      numero: {
        required: validations.required(t$('entity.validation.required').toString()),
        integer: validations.integer(t$('entity.validation.number').toString()),
      },
      logo: {},
      log: {},
      texto: {},
      imagem: {},
      proteinas: {},
      cargas: {},
    };
    const v$ = useVuelidate(validationRules, versao as any);
    v$.value.$validate();

    return {
      versaoService,
      alertService,
      versao,
      previousState,
      statusValues,
      isSaving,
      currentLanguage,
      ...dataUtils,
      v$,
      ...useDateFormat({ entityRef: versao }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.versao.id) {
        this.versaoService()
          .update(this.versao)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('semprotdbApp.versao.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.versaoService()
          .create(this.versao)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('semprotdbApp.versao.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },

    clearInputImage(field, fieldContentType, idInput): void {
      if (this.versao && field && fieldContentType) {
        if (Object.prototype.hasOwnProperty.call(this.versao, field)) {
          this.versao[field] = null;
        }
        if (Object.prototype.hasOwnProperty.call(this.versao, fieldContentType)) {
          this.versao[fieldContentType] = null;
        }
        if (idInput) {
          (<any>this).$refs[idInput] = null;
        }
      }
    },
  },
});
