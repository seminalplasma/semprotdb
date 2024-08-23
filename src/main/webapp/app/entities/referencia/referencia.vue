<template>
  <div>
    <h2 id="page-heading" data-cy="ReferenciaHeading">
      <span v-text="t$('semprotdbApp.referencia.home.title')" id="referencia-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('semprotdbApp.referencia.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'ReferenciaCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-referencia"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('semprotdbApp.referencia.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && referencias && referencias.length === 0">
      <span v-text="t$('semprotdbApp.referencia.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="referencias && referencias.length > 0">
      <table class="table table-striped" aria-describedby="referencias">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('citacao')">
              <span v-text="t$('semprotdbApp.referencia.citacao')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'citacao'"></jhi-sort-indicator>
            </th>
            <!--            <th scope="row" v-on:click="changeOrder('link')">-->
            <!--              <span v-text="t$('semprotdbApp.referencia.link')"></span>-->
            <!--              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'link'"></jhi-sort-indicator>-->
            <!--            </th>-->
            <th scope="row" v-on:click="changeOrder('ano')">
              <span v-text="t$('semprotdbApp.referencia.ano')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'ano'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('autores')">
              <span v-text="t$('semprotdbApp.referencia.autores')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'autores'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="referencia in referencias" :key="referencia.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'ReferenciaView', params: { referenciaId: referencia.id } }">{{ referencia.id }}</router-link>
            </td>
            <td>
              <a :href="referencia.link ?? '?'" target="_blank">{{ referencia.citacao }}</a>
            </td>
            <!--            <td>{{ referencia.link }}</td>-->
            <td>{{ referencia.ano }}</td>
            <td>{{ referencia.autores }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'ReferenciaView', params: { referenciaId: referencia.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'ReferenciaEdit', params: { referenciaId: referencia.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(referencia)"
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
        <span
          id="semprotdbApp.referencia.delete.question"
          data-cy="referenciaDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-referencia-heading" v-text="t$('semprotdbApp.referencia.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" v-on:click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-referencia"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            v-on:click="removeReferencia()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="referencias && referencias.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./referencia.component.ts"></script>
