import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import GeneService from './gene.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import CuradoriaService from '@/entities/curadoria/curadoria.service';
import { type ICuradoria } from '@/shared/model/curadoria.model';
import OrganismoService from '@/entities/organismo/organismo.service';
import { type IOrganismo } from '@/shared/model/organismo.model';
import { type IGene, Gene } from '@/shared/model/gene.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'GeneUpdate',
  setup() {
    const geneService = inject('geneService', () => new GeneService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const gene: Ref<IGene> = ref(new Gene());

    const curadoriaService = inject('curadoriaService', () => new CuradoriaService());

    const curadorias: Ref<ICuradoria[]> = ref([]);

    const organismoService = inject('organismoService', () => new OrganismoService());

    const organismos: Ref<IOrganismo[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveGene = async geneId => {
      try {
        const res = await geneService().find(geneId);
        gene.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.geneId) {
      retrieveGene(route.params.geneId);
    }

    const initRelationships = () => {
      curadoriaService()
        .retrieve()
        .then(res => {
          curadorias.value = res.data;
        });
      organismoService()
        .retrieve()
        .then(res => {
          organismos.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      nome: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      descricao: {},
      curadoria: {},
      organismo: {},
      proteinas: {},
    };
    const v$ = useVuelidate(validationRules, gene as any);
    v$.value.$validate();

    return {
      geneService,
      alertService,
      gene,
      previousState,
      isSaving,
      currentLanguage,
      curadorias,
      organismos,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.gene.id) {
        this.geneService()
          .update(this.gene)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('semprotdbApp.gene.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.geneService()
          .create(this.gene)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('semprotdbApp.gene.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
