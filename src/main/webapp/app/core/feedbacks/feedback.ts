import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';

import DBConfigService from '../../entities/db-config/db-config.service';
import { type IDBConfig } from '@/shared/model/db-config.model';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'DBConfig',
  setup() {
    const { t: t$ } = useI18n();
    const dateFormat = useDateFormat();
    const dataUtils = useDataUtils();
    const dBConfigService = inject('dBConfigService', () => new DBConfigService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dBConfigs: Ref<IDBConfig[]> = ref([]);

    const isFetching = ref(false);

    const retrieveDBConfigs = async () => {
      isFetching.value = true;
      try {
        const res = await dBConfigService().retrieve(true);
        dBConfigs.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveDBConfigs();
    };

    onMounted(async () => {
      await retrieveDBConfigs();
    });

    const readFeedback = async (id: number) => {
      try {
        await dBConfigService().partialUpdate({ id: id, habilitado: false });
        handleSyncList();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    const removeFeedback = async (id: number) => {
      try {
        await dBConfigService().delete(id);
        const message = t$('semprotdbApp.dBConfig.deleted', { param: id }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        retrieveDBConfigs();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      dBConfigs,
      isFetching,
      retrieveDBConfigs,
      ...dateFormat,
      t$,
      ...dataUtils,
      handleSyncList,
      removeFeedback,
      readFeedback,
    };
  },
});
