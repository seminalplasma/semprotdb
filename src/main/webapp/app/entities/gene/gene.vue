<template>
  <div>
    <h2 id="page-heading" data-cy="GeneHeading">
      <span v-text="t$('semprotdbApp.gene.home.title')" id="gene-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('semprotdbApp.gene.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'GeneCreate' }" custom v-slot="{ navigate }">
          <button @click="navigate" id="jh-create-entity" data-cy="entityCreateButton" class="btn btn-primary jh-create-entity create-gene">
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('semprotdbApp.gene.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && genes && genes.length === 0">
      <span v-text="t$('semprotdbApp.gene.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="genes && genes.length > 0">
      <table class="table table-striped" aria-describedby="genes">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('nome')">
              <span v-text="t$('semprotdbApp.gene.nome')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'nome'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('descricao')">
              <span v-text="t$('semprotdbApp.gene.descricao')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'descricao'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('curadoria.id')">
              <span v-text="t$('semprotdbApp.gene.curadoria')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'curadoria.id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('organismo.id')">
              <span v-text="t$('semprotdbApp.gene.organismo')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'organismo.id'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="gene in genes" :key="gene.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'GeneView', params: { geneId: gene.id } }">{{ gene.id }}</router-link>
            </td>
            <td>{{ gene.nome }}</td>
            <td>{{ gene.descricao }}</td>
            <td>
              <div v-if="gene.curadoria?.id">
                <router-link :to="{ name: 'CuradoriaView', params: { curadoriaId: gene.curadoria.id } }">{{
                  gene.curadoria.id
                }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="gene.organismo">
                <router-link :to="{ name: 'OrganismoView', params: { organismoId: gene.organismo.id } }">{{
                  gene.organismo.id
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'GeneView', params: { geneId: gene.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'GeneEdit', params: { geneId: gene.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(gene)"
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
        <span id="semprotdbApp.gene.delete.question" data-cy="geneDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-gene-heading" v-text="t$('semprotdbApp.gene.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" v-on:click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-gene"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            v-on:click="removeGene()"
          ></button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./gene.component.ts"></script>
