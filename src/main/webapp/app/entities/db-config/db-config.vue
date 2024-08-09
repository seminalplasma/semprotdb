<template>
  <div>
    <h2 id="page-heading" data-cy="DBConfigHeading">
      <span v-text="t$('semprotdbApp.dBConfig.home.title')" id="db-config-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('semprotdbApp.dBConfig.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'DBConfigCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-db-config"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('semprotdbApp.dBConfig.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && dBConfigs && dBConfigs.length === 0">
      <span v-text="t$('semprotdbApp.dBConfig.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="dBConfigs && dBConfigs.length > 0">
      <table class="table table-striped" aria-describedby="dBConfigs">
        <thead>
          <tr>
            <th scope="row"><span v-text="t$('global.field.id')"></span></th>
            <th scope="row"><span v-text="t$('semprotdbApp.dBConfig.key')"></span></th>
            <th scope="row"><span v-text="t$('semprotdbApp.dBConfig.habilitado')"></span></th>
            <th scope="row"><span v-text="t$('semprotdbApp.dBConfig.vstring')"></span></th>
            <th scope="row"><span v-text="t$('semprotdbApp.dBConfig.vbol')"></span></th>
            <th scope="row"><span v-text="t$('semprotdbApp.dBConfig.vdate')"></span></th>
            <th scope="row"><span v-text="t$('semprotdbApp.dBConfig.vint')"></span></th>
            <th scope="row"><span v-text="t$('semprotdbApp.dBConfig.vtext')"></span></th>
            <th scope="row"><span v-text="t$('semprotdbApp.dBConfig.vimg')"></span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="dBConfig in dBConfigs" :key="dBConfig.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'DBConfigView', params: { dBConfigId: dBConfig.id } }">{{ dBConfig.id }}</router-link>
            </td>
            <td>{{ dBConfig.key }}</td>
            <td>{{ dBConfig.habilitado }}</td>
            <td>{{ dBConfig.vstring }}</td>
            <td>{{ dBConfig.vbol }}</td>
            <td>{{ formatDateShort(dBConfig.vdate) || '' }}</td>
            <td>{{ dBConfig.vint }}</td>
            <td>{{ dBConfig.vtext }}</td>
            <td>
              <a v-if="dBConfig.vimg" v-on:click="openFile(dBConfig.vimgContentType, dBConfig.vimg)">
                <img v-bind:src="'data:' + dBConfig.vimgContentType + ';base64,' + dBConfig.vimg" style="max-height: 30px" alt="dBConfig" />
              </a>
              <span v-if="dBConfig.vimg">{{ dBConfig.vimgContentType }}, {{ byteSize(dBConfig.vimg) }}</span>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'DBConfigView', params: { dBConfigId: dBConfig.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'DBConfigEdit', params: { dBConfigId: dBConfig.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(dBConfig)"
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
        <span id="semprotdbApp.dBConfig.delete.question" data-cy="dBConfigDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-dBConfig-heading" v-text="t$('semprotdbApp.dBConfig.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" v-on:click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-dBConfig"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            v-on:click="removeDBConfig()"
          ></button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./db-config.component.ts"></script>
