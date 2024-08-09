import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import DBConfigService from './db-config.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type IDBConfig, DBConfig } from '@/shared/model/db-config.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'DBConfigUpdate',
  setup() {
    const dBConfigService = inject('dBConfigService', () => new DBConfigService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dBConfig: Ref<IDBConfig> = ref(new DBConfig());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveDBConfig = async dBConfigId => {
      try {
        const res = await dBConfigService().find(dBConfigId);
        res.vdate = new Date(res.vdate);
        dBConfig.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.dBConfigId) {
      retrieveDBConfig(route.params.dBConfigId);
    }

    const dataUtils = useDataUtils();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      key: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      habilitado: {},
      vstring: {},
      vbol: {},
      vdate: {},
      vint: {},
      vtext: {},
      vimg: {},
    };
    const v$ = useVuelidate(validationRules, dBConfig as any);
    v$.value.$validate();

    return {
      dBConfigService,
      alertService,
      dBConfig,
      previousState,
      isSaving,
      currentLanguage,
      ...dataUtils,
      v$,
      ...useDateFormat({ entityRef: dBConfig }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.dBConfig.id) {
        this.dBConfigService()
          .update(this.dBConfig)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('semprotdbApp.dBConfig.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.dBConfigService()
          .create(this.dBConfig)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('semprotdbApp.dBConfig.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },

    clearInputImage(field, fieldContentType, idInput): void {
      if (this.dBConfig && field && fieldContentType) {
        if (Object.prototype.hasOwnProperty.call(this.dBConfig, field)) {
          this.dBConfig[field] = null;
        }
        if (Object.prototype.hasOwnProperty.call(this.dBConfig, fieldContentType)) {
          this.dBConfig[fieldContentType] = null;
        }
        if (idInput) {
          (<any>this).$refs[idInput] = null;
        }
      }
    },
  },
});
