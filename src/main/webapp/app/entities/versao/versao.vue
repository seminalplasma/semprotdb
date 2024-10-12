<template>
  <div>
    <h2 id="page-heading" data-cy="VersaoHeading">
      <span v-text="t$('semprotdbApp.versao.home.title')" id="versao-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('semprotdbApp.versao.home.refreshListLabel')"></span>
        </button>

        <button
          v-if="ctxt?.includes(' de ') && !ctxt?.includes('?')"
          class="btn btn-info mr-2"
          v-on:click="curar"
          :disabled="ctxt.includes('?') || isFetching"
        >
          <font-awesome-icon icon="shield"></font-awesome-icon>
          <span>Curar {{ ctxt }}</span>
        </button>

        <router-link :to="{ name: 'VersaoCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-versao"
            :disabled="!hasAnyAuthority('ROLE_ADMIN')"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('semprotdbApp.versao.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && versaos && versaos.length === 0">
      <span v-text="t$('semprotdbApp.versao.home.notFound')"></span>
    </div>

    <b-alert variant="warning" dismissible :show="versaos && versaos.some(v => [Status.CARREGADO, Status.INVALIDO].includes(v.status))"
      >Processar ou remover uma versao pode <b>levar horas</b> dependendo da quantidade de registros. Consulte os logs.</b-alert
    >

    <div class="table-responsive" v-if="versaos && versaos.length > 0">
      <table class="table table-striped" aria-describedby="versaos">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('nome')">
              <span v-text="t$('semprotdbApp.versao.nome')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'nome'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('numero')">
              <span v-text="t$('semprotdbApp.versao.numero')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'numero'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('status')" class="text-center">
              <span v-text="t$('semprotdbApp.versao.status')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'status'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('release')">
              <span v-text="t$('semprotdbApp.versao.release')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'release'"></jhi-sort-indicator>
            </th>
            <!--            <th scope="row" v-on:click="changeOrder('detalhes')">-->
            <!--              <span v-text="t$('semprotdbApp.versao.detalhes')"></span>-->
            <!--              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'detalhes'"></jhi-sort-indicator>-->
            <!--            </th>-->
            <!--            <th scope="row" v-on:click="changeOrder('label')">-->
            <!--              <span v-text="t$('semprotdbApp.versao.label')"></span>-->
            <!--              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'label'"></jhi-sort-indicator>-->
            <!--            </th>-->
            <!--            <th scope="row" v-on:click="changeOrder('logo')">-->
            <!--              <span v-text="t$('semprotdbApp.versao.logo')"></span>-->
            <!--              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'logo'"></jhi-sort-indicator>-->
            <!--            </th>-->
            <!--            <th scope="row" v-on:click="changeOrder('log')">-->
            <!--              <span v-text="t$('semprotdbApp.versao.log')"></span>-->
            <!--              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'log'"></jhi-sort-indicator>-->
            <!--            </th>-->
            <!--            <th scope="row" v-on:click="changeOrder('texto')">-->
            <!--              <span v-text="t$('semprotdbApp.versao.texto')"></span>-->
            <!--              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'texto'"></jhi-sort-indicator>-->
            <!--            </th>-->
            <!--            <th scope="row" v-on:click="changeOrder('imagem')">-->
            <!--              <span v-text="t$('semprotdbApp.versao.imagem')"></span>-->
            <!--              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'imagem'"></jhi-sort-indicator>-->
            <!--            </th>-->
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="versao in versaos" :key="versao.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'VersaoView', params: { versaoId: versao.id } }">{{ versao.id }}</router-link>
            </td>
            <td>{{ versao.nome }}</td>
            <td>{{ versao.numero }}</td>

            <td class="text-center">
              <!--            criado =>  <i class="fa-solid fa-volleyball fa-bounce animation-iteration-count:3;"></i>-->
              <!--            invalido => <i class="fa-solid fa-square-exclamation fa-beat-fade"></i>-->
              <!--          carrgegado =>  <i class="fa-solid fa-cog fa-spin" style="&#45;&#45;fa-animation-duration: 15s;"></i>-->

              <template v-if="versao.status === Status.CRIADO">
                <font-awesome-icon icon="check" bounce class="text-success"></font-awesome-icon>
              </template>
              <template v-else-if="versao.status === Status.CARREGADO">
                <font-awesome-icon icon="cog" spin></font-awesome-icon>
              </template>
              <template v-else-if="versao.status === Status.INVALIDO">
                <font-awesome-icon icon="triangle-exclamation" beatFade class="text-danger"></font-awesome-icon>
              </template>
              <template v-else>
                <span class="badge badge-primary" v-text="t$('semprotdbApp.Status.' + versao.status)"></span>
              </template>
            </td>

            <!--            <td v-text="t$('semprotdbApp.Status.' + versao.status)"></td>-->
            <td>{{ formatDateShort(versao.release) || '' }}</td>
            <!--            <td>{{ versao.detalhes }}</td>-->
            <!--            <td>{{ versao.label }}</td>-->
            <!--            <td>{{ versao.logo }}</td>-->
            <!--            <td>{{ versao.log }}</td>-->
            <!--            <td>{{ versao.texto }}</td>-->
            <!--            <td>-->
            <!--              <a v-if="versao.imagem" v-on:click="openFile(versao.imagemContentType, versao.imagem)">-->
            <!--                <img v-bind:src="'data:' + versao.imagemContentType + ';base64,' + versao.imagem" style="max-height: 30px" alt="versao" />-->
            <!--              </a>-->
            <!--              <span v-if="versao.imagem">{{ versao.imagemContentType }}, {{ byteSize(versao.imagem) }}</span>-->
            <!--            </td>-->
            <td class="text-right">
              <div class="btn-group">
                <!--              <router-link :to="{ name: 'VersaoView', params: { versaoId: versao.id } }" custom v-slot="{ navigate }">-->
                <!--                <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">-->
                <!--                  <font-awesome-icon icon="eye"></font-awesome-icon>-->
                <!--                  <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>-->
                <!--                </button>-->
                <!--              </router-link>-->

                <!--PROCESSAR VERSAO-->
                <b-button
                  v-if="versao.status !== Status.INVALIDO && versao.status !== Status.CARREGADO"
                  :disabled="!hasAnyAuthority('ROLE_ADMIN')"
                  v-on:click="changeStatus(versao, Status.PROCESSADO)"
                  variant="warning"
                  class="btn btn-sm"
                >
                  <font-awesome-icon icon="hammer"></font-awesome-icon>
                  <span class="d-none d-md-inline">Processar</span>
                </b-button>

                <!--LIBERAR VERSAO-->
                <button
                  v-if="versao.status === Status.PROCESSADO || versao.status === Status.OCULTO"
                  @click="changeStatus(versao, Status.DISPONIVEL)"
                  class="btn btn-info btn-sm details"
                  data-cy="entityDetailsButton"
                >
                  <font-awesome-icon icon="eye"></font-awesome-icon>
                  <span class="d-none d-md-inline">Liberar</span>
                </button>

                <!--OCULTAR VERSAO-->
                <button
                  v-if="versao.status === Status.DISPONIVEL || versao.status === Status.INVALIDO"
                  @click="changeStatus(versao, Status.OCULTO)"
                  class="btn btn-info btn-sm details"
                  data-cy="entityDetailsButton"
                >
                  <font-awesome-icon icon="eye-slash"></font-awesome-icon>
                  <span class="d-none d-md-inline">Ocultar</span>
                </button>

                <!--EDITAR VERSAO-->
                <router-link :to="{ name: 'VersaoEdit', params: { versaoId: versao.id } }" custom v-slot="{ navigate }">
                  <button
                    :disabled="!hasAnyAuthority('ROLE_ADMIN')"
                    @click="navigate"
                    class="btn btn-primary btn-sm edit"
                    data-cy="entityEditButton"
                  >
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>

                <!--REMOVER VERSAO-->
                <b-button
                  v-if="versao.status === Status.OCULTO"
                  :disabled="!hasAnyAuthority('ROLE_ADMIN')"
                  v-on:click="prepareRemove(versao)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline" v-text="t$('entity.action.delete')"></span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span id="semprotdbApp.versao.delete.question" data-cy="versaoDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-versao-heading" v-text="t$('semprotdbApp.versao.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" v-on:click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-versao"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            v-on:click="removeVersao()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="versaos && versaos.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./versao.component.ts"></script>
