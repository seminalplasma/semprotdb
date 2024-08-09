<template>
  <div>
    <h2 id="page-heading" data-cy="CuradoriaHeading">
      <span v-text="t$('semprotdbApp.curadoria.home.title')" id="curadoria-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('semprotdbApp.curadoria.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'CuradoriaCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-curadoria"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('semprotdbApp.curadoria.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && curadorias && curadorias.length === 0">
      <span v-text="t$('semprotdbApp.curadoria.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="curadorias && curadorias.length > 0">
      <table class="table table-striped" aria-describedby="curadorias">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('email')">
              <span v-text="t$('semprotdbApp.curadoria.email')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'email'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('data')">
              <span v-text="t$('semprotdbApp.curadoria.data')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'data'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('anotacoes')">
              <span v-text="t$('semprotdbApp.curadoria.anotacoes')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'anotacoes'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="curadoria in curadorias" :key="curadoria.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'CuradoriaView', params: { curadoriaId: curadoria.id } }">{{ curadoria.id }}</router-link>
            </td>
            <td>{{ curadoria.email }}</td>
            <td>{{ formatDateShort(curadoria.data) || '' }}</td>
            <td>{{ curadoria.anotacoes }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'CuradoriaView', params: { curadoriaId: curadoria.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'CuradoriaEdit', params: { curadoriaId: curadoria.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(curadoria)"
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
        <span id="semprotdbApp.curadoria.delete.question" data-cy="curadoriaDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-curadoria-heading" v-text="t$('semprotdbApp.curadoria.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" v-on:click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-curadoria"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            v-on:click="removeCuradoria()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="curadorias && curadorias.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./curadoria.component.ts"></script>
