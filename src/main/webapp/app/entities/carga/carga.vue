<template>
  <div>
    <h2 id="page-heading" data-cy="CargaHeading">
      <span v-text="t$('semprotdbApp.carga.home.title')" id="carga-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('semprotdbApp.carga.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'CargaCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-carga"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('semprotdbApp.carga.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && cargas && cargas.length === 0">
      <span v-text="t$('semprotdbApp.carga.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="cargas && cargas.length > 0">
      <table class="table table-striped" aria-describedby="cargas">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <!--            <th scope="row" v-on:click="changeOrder('status')">-->
            <!--              <span v-text="t$('semprotdbApp.carga.status')"></span>-->
            <!--              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'status'"></jhi-sort-indicator>-->
            <!--            </th>-->
            <th scope="row" v-on:click="changeOrder('ordem')">
              <span v-text="t$('semprotdbApp.carga.ordem')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'ordem'"></jhi-sort-indicator>
            </th>
            <!--            <th scope="row" v-on:click="changeOrder('planilha')">-->
            <!--              <span v-text="t$('semprotdbApp.carga.planilha')"></span>-->
            <!--              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'planilha'"></jhi-sort-indicator>-->
            <!--            </th>-->
            <th scope="row" v-on:click="changeOrder('nome')">
              <span v-text="t$('semprotdbApp.carga.nome')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'nome'"></jhi-sort-indicator>
            </th>
            <!--            <th scope="row" v-on:click="changeOrder('caminho')">-->
            <!--              <span v-text="t$('semprotdbApp.carga.caminho')"></span>-->
            <!--              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'caminho'"></jhi-sort-indicator>-->
            <!--            </th>-->
            <th scope="row" v-on:click="changeOrder('validado')">
              <span v-text="t$('semprotdbApp.carga.validado')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'validado'"></jhi-sort-indicator>
            </th>
            <!--            <th scope="row" v-on:click="changeOrder('tipo')">-->
            <!--              <span v-text="t$('semprotdbApp.carga.tipo')"></span>-->
            <!--              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'tipo'"></jhi-sort-indicator>-->
            <!--            </th>-->
            <th scope="row" v-on:click="changeOrder('formato')">
              <span v-text="t$('semprotdbApp.carga.formato')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'formato'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('destino')">
              <span v-text="t$('semprotdbApp.carga.destino')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'destino'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('linhas')">
              <span v-text="t$('semprotdbApp.carga.linhas')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'linhas'"></jhi-sort-indicator>
            </th>
            <!--            <th scope="row" v-on:click="changeOrder('checksum')">-->
            <!--              <span v-text="t$('semprotdbApp.carga.checksum')"></span>-->
            <!--              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'checksum'"></jhi-sort-indicator>-->
            <!--            </th>-->
            <th scope="row" v-on:click="changeOrder('versao.nome')">
              <span v-text="t$('semprotdbApp.carga.versao')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'versao.nome'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="carga in cargas" :key="carga.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'CargaView', params: { cargaId: carga.id } }">{{ carga.id }}</router-link>
            </td>
            <!--            <td>{{ carga.status }}</td>-->
            <td class="text-center">
              <span class="badge badge-dark">{{ carga.ordem }}</span>
            </td>
            <!--            <td>-->
            <!--              <a-->
            <!--                v-if="carga.planilha"-->
            <!--                v-on:click="openFile(carga.planilhaContentType, carga.planilha)"-->
            <!--                v-text="t$('entity.action.open')"-->
            <!--              ></a>-->
            <!--              <span v-if="carga.planilha">{{ carga.planilhaContentType }}, {{ byteSize(carga.planilha) }}</span>-->
            <!--            </td>-->
            <td>{{ carga.nome }}</td>
            <!--            <td>{{ carga.caminho }}</td>-->
            <td class="text-center">
              <font-awesome-icon v-if="carga.validado" icon="file-circle-check" class="text-success"></font-awesome-icon>
            </td>
            <!--            <td v-text="t$('semprotdbApp.Tipo.' + carga.tipo)"></td>-->
            <td class="text-center">
              <font-awesome-icon
                :icon="carga.formato === Formato.XLSX ? 'file-excel' : carga.formato === Formato.TSV ? 'file-csv' : 'file'"
              ></font-awesome-icon>
            </td>
            <td v-text="t$('semprotdbApp.Destino.' + carga.destino)"></td>
            <td class="text-right text-monospace">{{ carga.linhas }}</td>
            <!--            <td>{{ carga.checksum }}</td>-->
            <td>
              <div v-if="carga.versao">
                <router-link :to="{ name: 'VersaoView', params: { versaoId: carga.versao.id } }">{{ carga.versao.nome }} </router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'CargaView', params: { cargaId: carga.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'CargaEdit', params: { cargaId: carga.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(carga)"
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
        <span id="semprotdbApp.carga.delete.question" data-cy="cargaDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-carga-heading" v-text="t$('semprotdbApp.carga.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" v-on:click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-carga"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            v-on:click="removeCarga()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="cargas && cargas.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./carga.component.ts"></script>
