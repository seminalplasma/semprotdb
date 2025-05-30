import { computed, defineComponent, inject, onMounted, ref, type Ref, watch, watchEffect } from 'vue';
import { useI18n } from 'vue-i18n';
import { useIntersectionObserver } from '@vueuse/core';
import { useRoute } from 'vue-router';

import ProteinaService from '@/entities/proteina/proteina.service';
import { type IProteina } from '@/shared/model/proteina.model';
import useDataUtils from '@/shared/data/data-utils.service';
import { useAlertService } from '@/shared/alert/alert.service';
import { useStore, useVersaoStore } from '@/store';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Tabela',
  setup() {
    const store = useStore();

    const { t: t$ } = useI18n();
    const dataUtils = useDataUtils();
    const proteinaService = inject('proteinaService', () => new ProteinaService());
    const alertService = inject('alertService', () => useAlertService(), true);
    const authenticated = computed(() => store.authenticated);
    const route = useRoute();
    const versaoStore = useVersaoStore();
    const versao = computed(() => versaoStore.versao);
    const versao_loaded = computed(() => versaoStore.esta_carregada);

    const curado = ref(false);

    const queryPT1: Ref<string> = ref('AND');
    const queryPT2: Ref<string> = ref('Protein');
    const queryPT2e: Ref<string[]> = ref(['Organism', 'Gene', 'Protein', 'Tamanho']);
    const queryPT2n: Ref<string[]> = ref(['Tamanho']);

    const fOps = ['contains', 'doesNotContain', 'equals', 'notEquals', 'in', 'notIn'];
    const fOps2 = ['>', '=', '>=', '<=', '<'];
    const queryPT3: Ref<string> = ref('contains');

    const query: Ref<string> = ref('');
    const queryes: Ref<Array<string>> = ref([]);
    const filters: Ref<{}> = ref({});

    const itemsPerPage = ref(20);
    const queryCount: Ref<number> = ref(null);
    const page: Ref<number> = ref(1);
    const propOrder = ref('id');
    const reverse = ref(false);
    const totalItems = ref(0);
    const links: Ref<any> = ref({});

    const proteinas: Ref<IProteina[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {
      page.value = 1;
      links.value = {};
      proteinas.value = [];
    };

    const sort = (): Array<any> => {
      const result = [propOrder.value + ',' + (reverse.value ? 'desc' : 'asc')];
      if (propOrder.value !== 'id') {
        result.push('id');
      }
      return result;
    };

    watch(
      () => versaoStore.selecionada,
      async value => {
        if (versaoStore.esta_carregada && value.id) {
          if (proteinas.value.length > 0) location.reload();
          clear();
        }
      },
    );

    const retrieveProteinas = async () => {
      if (!versao.value || !versao.value || !versao.value.id || versao.value.id < 1) {
        return;
      }

      isFetching.value = true;
      const page_size = itemsPerPage.value;

      try {
        const paginationQuery = Object.assign(filters.value, {
          page: page.value - 1,
          size: itemsPerPage.value,
          sort: sort(),
          'versaoId.equals': versao.value.id,
        });

        if (route.query?.organismId) paginationQuery['organismoId.equals'] = parseInt(route.query.organismId);

        if (!paginationQuery['qfirst'] || !paginationQuery['qfirst'].includes('versaoId'))
          paginationQuery['qfirst'] = (filters.value['qfirst'] ? filters.value['qfirst'] + ',' : '') + 'versaoId';

        if (curado.value) {
          paginationQuery['qfirst'] += ',' + 'curadoriaId';
          paginationQuery['curadoriaId.specified'] = true;
        } else {
          paginationQuery['curadoriaId.specified'] = false;
        }

        const res = await proteinaService().retrieve(paginationQuery);
        console.log(res);
        if (page_size !== itemsPerPage.value) return;
        totalItems.value = Number(res.headers['x-total-count']);
        queryCount.value = totalItems.value;
        links.value = dataUtils.parseLinks(res.headers?.['link']);
        proteinas.value.push(...(res.data ?? []));
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = page_size !== itemsPerPage.value;
      }
    };

    const handleSyncList = () => {
      clear();
    };

    onMounted(async () => {
      await retrieveProteinas();
    });

    const changeOrder = (newOrder: string) => {
      if (propOrder.value === newOrder) {
        reverse.value = !reverse.value;
      } else {
        reverse.value = false;
      }
      propOrder.value = newOrder;
    };

    // Whenever order changes, reset the pagination
    watch([propOrder, reverse], () => {
      clear();
    });

    // Whenever the data resets or page changes, switch to the new page.
    watch([proteinas, page], async ([data, page], [_prevData, prevPage]) => {
      if (data.length === 0 || page !== prevPage) {
        await retrieveProteinas();
      }
    });

    const infiniteScrollEl = ref<HTMLElement>();
    const intersectionObserver = useIntersectionObserver(
      infiniteScrollEl,
      intersection => {
        if (intersection[0].isIntersecting && !isFetching.value) {
          page.value++;
        }
      },
      {
        threshold: 0.5,
        immediate: false,
      },
    );
    watchEffect(() => {
      if (links.value.next) {
        intersectionObserver.resume();
      } else if (intersectionObserver.isActive) {
        intersectionObserver.pause();
      }
    });

    return {
      proteinas,
      handleSyncList,
      isFetching,
      retrieveProteinas,
      clear,
      itemsPerPage,
      queryCount,
      page,
      propOrder,
      reverse,
      totalItems,
      changeOrder,
      infiniteScrollEl,
      t$,
      versao,
      versao_loaded,
      ...dataUtils,
      queryPT1,
      queryPT2,
      queryPT3,
      queryPT2e,
      fOps,
      fOps2,
      query,
      queryes,
      filters,
      queryPT2n,
      alertService,
      curado,
      authenticated,
      route,
    };
  },

  methods: {
    changeCur() {
      this.curado = !this.curado;
      this.clear();
    },
    reset() {
      if (this.route.query?.organismId) {
        this.route.query.organismId = null;
      }
      this.curado = false;
      this.queryes = [];
      this.queryPT2 = 'Protein';
      this.queryPT2e = ['Organism', 'Gene', 'Protein', 'Tamanho'];
      if (Object.keys(this.filters).some(x => x.includes('.'))) {
        this.filters = {};
        this.clear();
      }
    },
    setPageSize(size: number) {
      this.itemsPerPage = size;
      this.clear();
    },
    toTop: () => {
      window.scroll({ top: 0, behavior: 'smooth' });
    },
    search() {
      // this.isFetching = true;
      if (this.queryes.length > 0) {
        /// modo avancado

        const parts = this.queryes
          .map(q => q.split(' '))
          .map((x, i) => x.slice(0, i > 0 ? 3 : 2).concat([x.slice(i > 0 ? 3 : 2).join(' ')]));
        const qfirst = [];
        const qors = [];

        this.filters = Object.fromEntries(
          parts.map((p, i) => {
            const q = i > 0 ? p[3] : p[2];
            let f: string = i > 0 ? p[2] : p[1];
            let ent = i > 0 ? p[1] : p[0];

            if (this.queryPT2n.includes(ent))
              f = {
                '>': 'greaterThan',
                '>=': 'greaterThanOrEqual',
                '<': 'lessThan',
                lessThanOrEqual: '<=',
                '=': 'equals',
              }[f];

            /// "organism" => organismonome ou organismosigla
            ent = ent.startsWith('Org') ? (q.split(',')[0].trim().length < 4 ? 'organismoSigla' : 'organismoNome') : ent;
            // "gene", => genenome
            ent = ent.startsWith('Gen') ? 'geneNome' : ent;
            // "protein" => nome
            ent = ent.startsWith('Prot') ? 'descricao' : ent;
            // "Tamanho" => tamanho
            ent = ent.startsWith('Tam') ? 'tamanho' : ent;

            /// xyz.contains=something
            /// xyz.doesNotContain=something
            /// xyz.equals=someValue
            /// xyz.notEquals=someValue
            /// xyz.in=someValue,otherValue
            /// xyz.dotIn=something

            qfirst.push(ent);
            if (p[0] === 'OR') qors.push(ent);

            return [`${ent}.${f}`, q.trim().toUpperCase()];
          }),
        );

        if (qors.length > 0) {
          this.filters['qors'] = qors.join(',');
        }
        this.filters['qfirst'] = qfirst.join(',');
      } else {
        /// modo ptna ou gene  ptna.nome contain or gene nome contais
        ///curl '... api/proteinas?nome.contains=AQN&geneNome.contains=AQN&qors=nome,geneNome'
        this.filters = {
          'nome.contains': this.query,
          'descricao.contains': this.query,
          'geneNome.contains': this.query,
          qors: 'nome,descricao,geneNome',
          qfirst: 'nome,descricao,geneNome',
        };
      }

      this.clear();
    },
  },
});
