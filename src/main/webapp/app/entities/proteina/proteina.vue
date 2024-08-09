<template>
  <div>
    <h2 id="page-heading" data-cy="ProteinaHeading">
      <span v-text="t$('semprotdbApp.proteina.home.title')" id="proteina-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('semprotdbApp.proteina.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'ProteinaCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-proteina"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('semprotdbApp.proteina.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && proteinas && proteinas.length === 0">
      <span v-text="t$('semprotdbApp.proteina.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="proteinas && proteinas.length > 0">
      <table class="table table-striped" aria-describedby="proteinas">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('nome')">
              <span v-text="t$('semprotdbApp.proteina.nome')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'nome'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('tamanho')">
              <span v-text="t$('semprotdbApp.proteina.tamanho')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'tamanho'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('massa')">
              <span v-text="t$('semprotdbApp.proteina.massa')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'massa'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('descricao')">
              <span v-text="t$('semprotdbApp.proteina.descricao')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'descricao'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('curadoria.id')">
              <span v-text="t$('semprotdbApp.proteina.curadoria')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'curadoria.id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('versao.id')">
              <span v-text="t$('semprotdbApp.proteina.versao')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'versao.id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('gene.id')">
              <span v-text="t$('semprotdbApp.proteina.gene')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'gene.id'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="proteina in proteinas" :key="proteina.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'ProteinaView', params: { proteinaId: proteina.id } }">{{ proteina.id }}</router-link>
            </td>
            <td>{{ proteina.nome }}</td>
            <td>{{ proteina.tamanho }}</td>
            <td>{{ proteina.massa }}</td>
            <td>{{ proteina.descricao }}</td>
            <td>
              <div v-if="proteina.curadoria">
                <router-link :to="{ name: 'CuradoriaView', params: { curadoriaId: proteina.curadoria.id } }">{{
                  proteina.curadoria.id
                }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="proteina.versao">
                <router-link :to="{ name: 'VersaoView', params: { versaoId: proteina.versao.id } }">{{ proteina.versao.id }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="proteina.gene">
                <router-link :to="{ name: 'GeneView', params: { geneId: proteina.gene.id } }">{{ proteina.gene.id }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'ProteinaView', params: { proteinaId: proteina.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'ProteinaEdit', params: { proteinaId: proteina.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(proteina)"
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
        <span ref="infiniteScrollEl"></span>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span id="semprotdbApp.proteina.delete.question" data-cy="proteinaDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-proteina-heading" v-text="t$('semprotdbApp.proteina.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" v-on:click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-proteina"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            v-on:click="removeProteina()"
          ></button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./proteina.component.ts"></script>
