import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import CargaService from './carga.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import VersaoService from '@/entities/versao/versao.service';
import { type IVersao } from '@/shared/model/versao.model';
import { type ICarga, Carga } from '@/shared/model/carga.model';
import { Tipo } from '@/shared/model/enumerations/tipo.model';
import { Formato } from '@/shared/model/enumerations/formato.model';
import { Destino } from '@/shared/model/enumerations/destino.model';
import { Status } from '@/shared/model/enumerations/status.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CargaUpdate',
  setup() {
    const cargaService = inject('cargaService', () => new CargaService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const carga: Ref<ICarga> = ref(new Carga());

    const versaoService = inject('versaoService', () => new VersaoService());

    const versaos: Ref<IVersao[]> = ref([]);
    const tipoValues: Ref<string[]> = ref(Object.keys(Tipo));
    const formatoValues: Ref<string[]> = ref(Object.keys(Formato));
    const destinoValues: Ref<string[]> = ref(Object.keys(Destino));
    const isSaving = ref(false);
    const isNew = ref(false);
    const modoRemoto = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveCarga = async cargaId => {
      try {
        const res = await cargaService().find(cargaId);
        carga.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.cargaId) {
      retrieveCarga(route.params.cargaId);
    } else {
      isNew.value = true;
    }

    const initRelationships = () => {
      versaoService()
        .retrieve()
        .then(res => {
          versaos.value = res.data;
          const criados = versaos.value.filter(v => v.status === Status.CRIADO);
          if (isNew.value && criados.length < 2) {
            carga.value.versao = criados[0];
          }
        });
    };

    initRelationships();

    const dataUtils = useDataUtils();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      status: {},
      ordem: {},
      planilha: {},
      nome: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      caminho: {},
      validado: {},
      tipo: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      formato: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      destino: {},
      linhas: {},
      checksum: {},
      versao: {},
    };
    const v$ = useVuelidate(validationRules, carga as any);
    v$.value.$validate();

    return {
      cargaService,
      alertService,
      carga,
      previousState,
      tipoValues,
      formatoValues,
      destinoValues,
      isSaving,
      currentLanguage,
      versaos,
      ...dataUtils,
      v$,
      t$,
      isNew,
      modoRemoto,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.carga.id) {
        this.cargaService()
          .update(this.carga)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('semprotdbApp.carga.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.cargaService()
          .create(this.carga)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('semprotdbApp.carga.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
