import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import ProteinaService from './proteina.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import CuradoriaService from '@/entities/curadoria/curadoria.service';
import { type ICuradoria } from '@/shared/model/curadoria.model';
import VersaoService from '@/entities/versao/versao.service';
import { type IVersao } from '@/shared/model/versao.model';
import GeneService from '@/entities/gene/gene.service';
import { type IGene } from '@/shared/model/gene.model';
import ReferenciaService from '@/entities/referencia/referencia.service';
import { type IReferencia } from '@/shared/model/referencia.model';
import RecursoService from '@/entities/recurso/recurso.service';
import { type IRecurso } from '@/shared/model/recurso.model';
import { type IProteina, Proteina } from '@/shared/model/proteina.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ProteinaUpdate',
  setup() {
    const proteinaService = inject('proteinaService', () => new ProteinaService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const proteina: Ref<IProteina> = ref(new Proteina());

    const curadoriaService = inject('curadoriaService', () => new CuradoriaService());

    const curadorias: Ref<ICuradoria[]> = ref([]);

    const versaoService = inject('versaoService', () => new VersaoService());

    const versaos: Ref<IVersao[]> = ref([]);

    const geneService = inject('geneService', () => new GeneService());

    const genes: Ref<IGene[]> = ref([]);

    const referenciaService = inject('referenciaService', () => new ReferenciaService());

    const referencias: Ref<IReferencia[]> = ref([]);

    const recursoService = inject('recursoService', () => new RecursoService());

    const recursos: Ref<IRecurso[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveProteina = async proteinaId => {
      try {
        const res = await proteinaService().find(proteinaId);
        proteina.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.proteinaId) {
      retrieveProteina(route.params.proteinaId);
    }

    const initRelationships = () => {
      curadoriaService()
        .retrieve()
        .then(res => {
          curadorias.value = res.data;
        });
      versaoService()
        .retrieve()
        .then(res => {
          versaos.value = res.data;
        });
      geneService()
        .retrieve()
        .then(res => {
          genes.value = res.data;
        });
      referenciaService()
        .retrieve()
        .then(res => {
          referencias.value = res.data;
        });
      recursoService()
        .retrieve()
        .then(res => {
          recursos.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      nome: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      tamanho: {},
      massa: {},
      descricao: {},
      curadoria: {},
      versao: {},
      gene: {},
      referencias: {},
      recursos: {},
    };
    const v$ = useVuelidate(validationRules, proteina as any);
    v$.value.$validate();

    return {
      proteinaService,
      alertService,
      proteina,
      previousState,
      isSaving,
      currentLanguage,
      curadorias,
      versaos,
      genes,
      referencias,
      recursos,
      v$,
      t$,
    };
  },
  created(): void {
    this.proteina.referencias = [];
    this.proteina.recursos = [];
  },
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.proteina.id) {
        this.proteinaService()
          .update(this.proteina)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('semprotdbApp.proteina.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.proteinaService()
          .create(this.proteina)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('semprotdbApp.proteina.created', { param: param.id }).toString());
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
