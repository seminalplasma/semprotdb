import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import GeneService from './gene.service';
import { type IGene } from '@/shared/model/gene.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'GeneDetails',
  setup() {
    const geneService = inject('geneService', () => new GeneService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const gene: Ref<IGene> = ref({});

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

    return {
      alertService,
      gene,

      previousState,
      t$: useI18n().t,
    };
  },
});
