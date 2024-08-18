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
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import { useStore } from '@/store';

export default defineComponent({
  components: { FontAwesomeIcon },
  compatConfig: { MODE: 3 },
  name: 'ProteinaUpdate',
  setup() {
    const store = useStore();
    const account = computed(() => store.account);

    const proteinaService = inject('proteinaService', () => new ProteinaService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const proteina: Ref<IProteina> = ref(new Proteina());

    const curadoriaService = inject('curadoriaService', () => new CuradoriaService());

    const curadorias: Ref<ICuradoria[]> = ref([]);

    const versaoService = inject('versaoService', () => new VersaoService());

    const versaos: Ref<IVersao[]> = ref([]);

    const geneService = inject('geneService', () => new GeneService());

    const genes: Ref<IGene[]> = ref([]);

    const geneQ: Ref<string> = ref('');
    const linkQ: Ref<string> = ref('');

    const referenciaService = inject('referenciaService', () => new ReferenciaService());

    const referencias: Ref<IReferencia[]> = ref([]);

    const recursoService = inject('recursoService', () => new RecursoService());

    const recursos: Ref<IRecurso[]> = ref([]);
    const isSaving = ref(false);
    const isCurar = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const toGene = g => router.push({ path: `/gene/${g.id}/view` });
    const toLink = l => router.push({ path: `/recurso/${l.id}/view` });

    const retrieveProteina = async proteinaId => {
      try {
        const res = await proteinaService().find(proteinaId);
        proteina.value = res;
        if (res.gene) {
          res.gene.descricao = geneQ.value = res.gene?.id + ' - ' + res.gene?.nome;
        }
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.proteinaId) {
      retrieveProteina(route.params.proteinaId);
    }

    const initRelationships = () => {
      curadoriaService()
        .retrieve({ size: 100 })
        .then(res => {
          curadorias.value = res.data.filter(c => c.email === account.value.email);
        });
      versaoService()
        .retrieve()
        .then(res => {
          versaos.value = res.data;
        });
      geneService()
        .retrieve()
        .then(res => {
          genes.value = [proteina.value.gene]
            .concat(res.data)
            .filter(g => g && g.id && g.nome)
            .map(g => {
              g.descricao = g.id + ' - ' + g.nome;
              return g;
            });
        });
      referenciaService()
        .retrieve({ size: 80 })
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
      tamanho: {
        required: validations.required(t$('entity.validation.required').toString()),
        // integer: validations.integer(t$('entity.validation.integer').toString()),
        // minValue: validations.minValue("Digite um valor maior que zero", 0),
      },
      massa: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      descricao: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      curadoria: {},
      versao: {},
      gene: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      referencias: {
        required: validations.required(t$('entity.validation.required').toString()),
        // minLength: validations.minLength('Insira pelo menos uma referencia', 1),
      },
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
      geneService,
      geneQ,
      toGene,
      linkQ,
      recursoService,
      toLink,
      isCurar,
    };
  },
  created(): void {
    this.proteina.referencias = [];
    this.proteina.recursos = [];
  },
  methods: {
    setGene() {
      const idg = (this.geneQ + '').trim();
      console.log(idg);
      if (idg.length < 2) return;
      this.proteina.gene = null;
      const q = { 'versaoId.equals': this.proteina.versao?.id };
      if (idg?.match(/^\d+$/)) q['id.equals'] = parseInt(idg);
      else q['nome.contains'] = idg;
      this.isSaving = true;
      this.geneService()
        .retrieve(q)
        .then(gs => {
          this.genes = gs.data.map(g => {
            g.descricao = g.id + ' - ' + g.nome;
            return g;
          });
          this.proteina.gene = this.genes[0];
        })
        .finally(() => (this.isSaving = false));
    },

    setLink() {
      this.isSaving = true;
      this.recursoService()
        .find(this.linkQ, true)
        .then(link => {
          this.proteina.recursos?.push(link);
        })
        .finally(() => (this.isSaving = false));
    },

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
