import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import OrganismoService from './organismo.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type IOrganismo, Organismo } from '@/shared/model/organismo.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'OrganismoUpdate',
  setup() {
    const organismoService = inject('organismoService', () => new OrganismoService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const organismo: Ref<IOrganismo> = ref(new Organismo());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveOrganismo = async organismoId => {
      try {
        const res = await organismoService().find(organismoId);
        organismo.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.organismoId) {
      retrieveOrganismo(route.params.organismoId);
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
      sigla: {},
      apelido: {},
      silhueta: {},
      icone: {},
      pos: {},
      imagem: {},
      descricao: {},
      genes: {},
    };
    const v$ = useVuelidate(validationRules, organismo as any);
    v$.value.$validate();

    return {
      organismoService,
      alertService,
      organismo,
      previousState,
      isSaving,
      currentLanguage,
      ...dataUtils,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.organismo.id) {
        this.organismoService()
          .update(this.organismo)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('semprotdbApp.organismo.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.organismoService()
          .create(this.organismo)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('semprotdbApp.organismo.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },

    clearInputImage2(field, fieldContentType, idInput): void {
      if (this.organismo && field && fieldContentType) {
        if (Object.prototype.hasOwnProperty.call(this.organismo, field)) {
          this.organismo[field] = null;
        }
        if (Object.prototype.hasOwnProperty.call(this.organismo, fieldContentType)) {
          this.organismo[fieldContentType] = null;
        }
        if (idInput) {
          (<any>this).$refs[idInput] = null;
        }
      }
    },
  },
});
