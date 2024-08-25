<template>
  <div>
    <h2 id="page-heading" data-cy="DBConfigHeading">
      <span>Feedbacks</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('semprotdbApp.dBConfig.home.refreshListLabel')"></span>
        </button>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && dBConfigs && dBConfigs.length === 0">Nenhum feedback encontrado.</div>

    <b-card-group class="m-3" v-if="dBConfigs && dBConfigs.length > 0" columns>
      <b-card v-for="feedback in dBConfigs" :key="feedback.id" class="shadow-sm my-3" header-tag="header" footer-tag="footer">
        <template #header>
          <span class="mb-0"
            ><em>{{ formatDateShort(feedback.vdate) || '' }}</em></span
          >
        </template>

        <h6>
          <router-link class="text-monospace opacity-25" :to="{ name: 'DBConfigView', params: { dBConfigId: feedback.id } }"
            >Cód.
            {{ feedback.id }}
          </router-link>
        </h6>

        <b-card-text>
          <p>
            {{ feedback.vtext }}
          </p>
        </b-card-text>
        <template #footer>
          <b-button :disabled="isFetching" v-on:click="readFeedback(feedback.id)" variant="success" class="btn btn-sm mr-2">
            <font-awesome-icon icon="check"></font-awesome-icon>
            Lido
          </b-button>

          <b-button
            :disabled="isFetching"
            v-on:click="removeFeedback(feedback.id)"
            variant="danger"
            class="btn btn-sm"
            data-cy="entityDeleteButton"
          >
            <font-awesome-icon icon="times"></font-awesome-icon>
            <span class="d-none d-md-inline" v-text="t$('entity.action.delete')"></span>
          </b-button>
        </template>
      </b-card>
    </b-card-group>
  </div>
</template>

<script lang="ts" src="./feedback.ts"></script>
