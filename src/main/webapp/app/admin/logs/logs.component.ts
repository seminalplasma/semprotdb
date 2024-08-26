import { computed, defineComponent, inject, onMounted, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';

import LogsService from './logs.service';
import { orderAndFilterBy } from '@/shared/computables';
import DBConfigService from '@/entities/db-config/db-config.service';
import type { IDBConfig } from '@/shared/model/db-config.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'JhiLogs',
  setup() {
    const logsService = inject('logsService', () => new LogsService(), true);

    const dBConfigService = inject('dBConfigService', () => new DBConfigService());
    const dBConfig: Ref<IDBConfig> = ref({});
    const isFetching = ref(false);
    const alertService = inject('alertService', () => useAlertService(), true);

    const retrieveDBConfigs = async () => {
      console.log('carregando.....');
      isFetching.value = true;
      try {
        const res = await dBConfigService().retrieve(false, true);
        dBConfig.value = res.data[0];
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const loggers: Ref<any[]> = ref([]);
    const filtered = ref('');
    const orderProp = ref('name');
    const reverse = ref(false);
    const filteredLoggers = computed(() =>
      orderAndFilterBy(loggers.value, {
        filterByTerm: filtered.value,
        orderByProp: orderProp.value,
        reverse: reverse.value,
      }),
    );

    return {
      logsService,
      loggers,
      filtered,
      orderProp,
      reverse,
      filteredLoggers,
      isFetching,
      dBConfig,
      retrieveDBConfigs,
      t$: useI18n().t,
    };
  },
  mounted() {
    this.init();
  },
  methods: {
    init(): void {
      this.logsService.findAll().then(response => {
        this.extractLoggers(response);
      });
      this.retrieveDBConfigs().then(r => {
        console.log(r);
      });
    },
    updateLevel(name: string, level: string): void {
      this.logsService.changeLevel(name, level).then(() => {
        this.init();
      });
    },
    changeOrder(orderProp: string): void {
      this.orderProp = orderProp;
      this.reverse = !this.reverse;
    },
    extractLoggers(response) {
      this.loggers = [];
      if (response.data) {
        for (const key of Object.keys(response.data.loggers)) {
          const logger = response.data.loggers[key];
          this.loggers.push({ name: key, level: logger.effectiveLevel });
        }
      }
    },
  },
});
