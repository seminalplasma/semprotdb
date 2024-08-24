import { defineComponent, inject } from 'vue';
import { useI18n } from 'vue-i18n';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import DBConfigService from '@/entities/db-config/db-config.service';
import { DBConfig } from '@/shared/model/db-config.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  components: { FontAwesomeIcon },
  compatConfig: { MODE: 3 },
  name: 'JhiFooter',
  setup() {
    const alertService = inject('alertService', () => useAlertService(), true);
    const dBConfigService = inject('dBConfigService', () => new DBConfigService());
    const dbc = new DBConfig();

    return {
      t$: useI18n().t,
      dBConfigService,
      dbc,
      alertService,
    };
  },

  methods: {
    feedback() {
      const sucesso = () => {
        this.alertService.showInfo('Feedback registrado com suesso!');
      };
      const erro = () => {
        this.alertService.showError('Falhou ao registrar o feedback! tente novamente mais tarde');
      };
      this.dbc.key = 'feedbacks';
      this.dBConfigService()
        .create(this.dbc, true)
        .then(x => {
          if (x && x.habilitado) sucesso();
          else erro();
        })
        .catch(erro);
    },
  },
});
