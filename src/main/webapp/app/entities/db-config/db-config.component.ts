import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';

import DBConfigService from './db-config.service';
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

    const clear = () => {};

    const retrieveDBConfigs = async () => {
      isFetching.value = true;
      try {
        const res = await dBConfigService().retrieve();
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

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IDBConfig) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeDBConfig = async () => {
      try {
        await dBConfigService().delete(removeId.value);
        const message = t$('semprotdbApp.dBConfig.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveDBConfigs();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      dBConfigs,
      handleSyncList,
      isFetching,
      retrieveDBConfigs,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeDBConfig,
      t$,
      ...dataUtils,
    };
  },
});
