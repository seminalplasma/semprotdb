<template>
  <div>
    <h2 id="page-heading" data-cy="OrganismoHeading">
      <span v-text="t$('semprotdbApp.organismo.home.title')" id="organismo-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('semprotdbApp.organismo.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'OrganismoCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-organismo"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('semprotdbApp.organismo.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && organismos && organismos.length === 0">
      <span v-text="t$('semprotdbApp.organismo.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="organismos && organismos.length > 0">
      <table class="table table-striped" aria-describedby="organismos">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('nome')">
              <span v-text="t$('semprotdbApp.organismo.nome')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'nome'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('sigla')">
              <span v-text="t$('semprotdbApp.organismo.sigla')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'sigla'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('apelido')">
              <span v-text="t$('semprotdbApp.organismo.apelido')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'apelido'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('silhueta')">
              <span v-text="t$('semprotdbApp.organismo.silhueta')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'silhueta'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('icone')">
              <span v-text="t$('semprotdbApp.organismo.icone')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'icone'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('pos')">
              <span v-text="t$('semprotdbApp.organismo.pos')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'pos'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('imagem')">
              <span v-text="t$('semprotdbApp.organismo.imagem')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'imagem'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('descricao')">
              <span v-text="t$('semprotdbApp.organismo.descricao')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'descricao'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="organismo in organismos" :key="organismo.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'OrganismoView', params: { organismoId: organismo.id } }">{{ organismo.id }}</router-link>
            </td>
            <td>{{ organismo.nome }}</td>
            <td>{{ organismo.sigla }}</td>
            <td>{{ organismo.apelido }}</td>
            <td>
              <a v-if="organismo.silhueta" v-on:click="openFile(organismo.silhuetaContentType, organismo.silhueta)">
                <img
                  v-bind:src="'data:' + organismo.silhuetaContentType + ';base64,' + organismo.silhueta"
                  style="max-height: 30px"
                  alt="organismo"
                />
              </a>
              <span v-if="organismo.silhueta">{{ organismo.silhuetaContentType }}, {{ byteSize(organismo.silhueta) }}</span>
            </td>
            <td>{{ organismo.icone }}</td>
            <td>{{ organismo.pos }}</td>
            <td>{{ organismo.imagem }}</td>
            <td>{{ organismo.descricao }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'OrganismoView', params: { organismoId: organismo.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'OrganismoEdit', params: { organismoId: organismo.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(organismo)"
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
        <span id="semprotdbApp.organismo.delete.question" data-cy="organismoDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-organismo-heading" v-text="t$('semprotdbApp.organismo.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" v-on:click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-organismo"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            v-on:click="removeOrganismo()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="organismos && organismos.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./organismo.component.ts"></script>
