import { computed, type ComputedRef, defineComponent, inject, onMounted, ref, type Ref } from 'vue';
import { useVersaoStore } from '@/shared/config/store/versao-store';
import type { IVersao } from '@/shared/model/versao.model';
import VersaoService from '@/entities/versao/versao.service';
import CargaService from '@/entities/carga/carga.service';
import type { ICarga } from '@/shared/model/carga.model';
import useDataUtils from '@/shared/data/data-utils.service';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Downloads',
  setup() {
    const versaoStore = useVersaoStore();
    const versao: ComputedRef<IVersao> = computed(() => versaoStore.versao);
    const versao_loaded = computed(() => versaoStore.esta_carregada);

    const versoes: Ref<IVersao[]> = ref([]);
    const versaoService = inject('versaoService', () => new VersaoService());

    const cargas: Ref<ICarga[]> = ref([]);
    const cargaService = inject('cargaService', () => new CargaService());

    const alertService = inject('alertService', () => useAlertService(), true);
    const dataUtils = useDataUtils();

    const downloading = ref(false);

    onMounted(() => {
      versaoService()
        .retrieve({ sort: 'numero,desc' })
        .then(vs => {
          versoes.value = vs.data;
        });
      cargaService()
        .retrieve({ size: 100, sort: 'nome,asc' })
        .then(cs => {
          cargas.value = cs.data;
        });
    });

    return {
      versao,
      versao_loaded,
      versoes,
      cargas,
      cargaService,
      ...dataUtils,
      downloading,
      alertService,
    };
  },

  methods: {
    download(carga: ICarga): void {
      if (carga && carga.id) {
        this.downloading = true;
        this.cargaService()
          .find(carga.id)
          .then(carga => this.downloadFile(carga.planilhaContentType, carga.planilha, carga.nome))
          .catch(() => this.alertService.showError('Houve uma falha ao tentar baixar o arquivo solicitado'))
          .finally(() => (this.downloading = false));
      }
    },
  },
});
