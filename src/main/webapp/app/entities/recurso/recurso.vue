<template>
  <div>
    <h2 id="page-heading" data-cy="RecursoHeading">
      <span v-text="t$('semprotdbApp.recurso.home.title')" id="recurso-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('semprotdbApp.recurso.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'RecursoCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-recurso"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('semprotdbApp.recurso.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && recursos && recursos.length === 0">
      <span v-text="t$('semprotdbApp.recurso.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="recursos && recursos.length > 0">
      <table class="table table-striped" aria-describedby="recursos">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('uid')">
              <span v-text="t$('semprotdbApp.recurso.uid')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'uid'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('db')">
              <span v-text="t$('semprotdbApp.recurso.db')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'db'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('link')">
              <span v-text="t$('semprotdbApp.recurso.link')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'link'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="recurso in recursos" :key="recurso.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'RecursoView', params: { recursoId: recurso.id } }">{{ recurso.id }}</router-link>
            </td>
            <td>{{ recurso.uid }}</td>
            <td v-text="t$('semprotdbApp.BioDB.' + recurso.db)"></td>
            <td>{{ recurso.link }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'RecursoView', params: { recursoId: recurso.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'RecursoEdit', params: { recursoId: recurso.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(recurso)"
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
        <span id="semprotdbApp.recurso.delete.question" data-cy="recursoDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-recurso-heading" v-text="t$('semprotdbApp.recurso.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" v-on:click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-recurso"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            v-on:click="removeRecurso()"
          ></button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./recurso.component.ts"></script>
