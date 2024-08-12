import { type ComputedRef, defineComponent, inject, ref, type Ref, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import OrganismoService from '@/entities/organismo/organismo.service';
import type { IOrganismo } from '@/shared/model/organismo.model';
import type { IVersao } from '@/shared/model/versao.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  setup() {
    const versao = inject<ComputedRef<IVersao>>('versao');
    const organismoService = inject('organismoService', () => new OrganismoService());
    const organismos: Ref<IOrganismo[]> = ref([]);

    onMounted(async () => {
      const res2 = await organismoService().retrieve();
      for (const o of res2.data.map(o => o.id)) {
        const res3 = await organismoService().find(o);
        organismos.value.push(res3);
      }
    });

    // '12' '52' '94' '97' '69' '19'

    return {
      t$: useI18n().t,
      organismos,
      versao,
    };
  },
});
