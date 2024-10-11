import { defineComponent, inject, onMounted, ref, type Ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';

import VersaoService from './versao.service';
import { type IVersao, Versao } from '@/shared/model/versao.model';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';
import type AccountService from '@/account/account.service';
import { Status } from '@/shared/model/enumerations/status.model';

export default defineComponent({
  computed: {
    Status() {
      return Status;
    },
  },
  compatConfig: { MODE: 3 },
  name: 'Versao',
  setup() {
    const accountService = inject<AccountService>('accountService');
    const hasAnyAuthorityValues: Ref<any> = ref({});

    const { t: t$ } = useI18n();
    const dateFormat = useDateFormat();
    const dataUtils = useDataUtils();
    const versaoService = inject('versaoService', () => new VersaoService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const itemsPerPage = ref(20);
    const queryCount: Ref<number> = ref(null);
    const page: Ref<number> = ref(1);
    const propOrder = ref('id');
    const ctxt = ref('? de ?');
    const reverse = ref(false);
    const totalItems = ref(0);

    const versaos: Ref<IVersao[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {
      page.value = 1;
    };

    const sort = (): Array<any> => {
      const result = [propOrder.value + ',' + (reverse.value ? 'desc' : 'asc')];
      if (propOrder.value !== 'id') {
        result.push('id');
      }
      return result;
    };

    const retrieveVersaos = async () => {
      isFetching.value = true;
      try {
        const paginationQuery = {
          page: page.value - 1,
          size: itemsPerPage.value,
          sort: sort(),
        };
        const res = await versaoService().retrieve(paginationQuery);
        totalItems.value = Number(res.headers['x-total-count']);
        queryCount.value = totalItems.value;
        versaos.value = res.data;
        ctxt.value =
          queryCount.value > 1
            ? versaos.value
                .filter(v => v.status === 'CRIADO' || v.status === 'DISPONIVEL')
                .map(v => v.id)
                .sort((a, b) => a - b)
                .reverse()
                .slice(0, 2)
                .join(' de ')
            : '? de ?';
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveVersaos();
    };

    onMounted(async () => {
      await retrieveVersaos();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IVersao) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeVersao = async () => {
      try {
        await versaoService().delete(removeId.value);
        const message = t$('semprotdbApp.versao.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveVersaos();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    const changeStatus = async (v: IVersao, s: Status) => {
      await versaoService().partialUpdate(new Versao(v.id).with_status(s));
      await retrieveVersaos();
    };

    const curar = async () => {
      const vs = ctxt.value.split('de').map(x => parseInt(x.trim()));
      if (vs.length < 2) return;
      await versaoService().partialUpdate(new Versao(vs[0]).with_log('RECUPERAR_CURADORIA:' + vs[1] + ':'));
      await retrieveVersaos();
    };

    const changeOrder = (newOrder: string) => {
      if (propOrder.value === newOrder) {
        reverse.value = !reverse.value;
      } else {
        reverse.value = false;
      }
      propOrder.value = newOrder;
    };

    // Whenever order changes, reset the pagination
    watch([propOrder, reverse], async () => {
      if (page.value === 1) {
        // first page, retrieve new data
        await retrieveVersaos();
      } else {
        // reset the pagination
        clear();
      }
    });

    // Whenever page changes, switch to the new page.
    watch(page, async () => {
      await retrieveVersaos();
    });

    return {
      versaos,
      handleSyncList,
      isFetching,
      retrieveVersaos,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeVersao,
      itemsPerPage,
      queryCount,
      page,
      propOrder,
      reverse,
      totalItems,
      changeOrder,
      t$,
      ...dataUtils,
      accountService,
      hasAnyAuthorityValues,
      changeStatus,
      ctxt,
      curar,
    };
  },
  methods: {
    hasAnyAuthority(authorities: any): boolean {
      this.accountService.hasAnyAuthorityAndCheckAuth(authorities).then(value => {
        if (this.hasAnyAuthorityValues[authorities] !== value) {
          this.hasAnyAuthorityValues = { ...this.hasAnyAuthorityValues, [authorities]: value };
        }
      });
      return this.hasAnyAuthorityValues[authorities] ?? false;
    },
  },
});
