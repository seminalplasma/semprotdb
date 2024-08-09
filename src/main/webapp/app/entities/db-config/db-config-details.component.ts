import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import DBConfigService from './db-config.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat } from '@/shared/composables';
import { type IDBConfig } from '@/shared/model/db-config.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'DBConfigDetails',
  setup() {
    const dateFormat = useDateFormat();
    const dBConfigService = inject('dBConfigService', () => new DBConfigService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const dBConfig: Ref<IDBConfig> = ref({});

    const retrieveDBConfig = async dBConfigId => {
      try {
        const res = await dBConfigService().find(dBConfigId);
        dBConfig.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.dBConfigId) {
      retrieveDBConfig(route.params.dBConfigId);
    }

    return {
      ...dateFormat,
      alertService,
      dBConfig,

      ...dataUtils,

      previousState,
      t$: useI18n().t,
    };
  },
});
