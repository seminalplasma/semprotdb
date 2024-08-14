<template>
  <div class="px-5 bg-light p-2 m-2 border-left border-secondary" style="border-width: 12px !important">
    <h1 class="display-3 text-primary">
      <font-awesome-icon icon="cloud-arrow-down"></font-awesome-icon>
      Downloads
    </h1>
  </div>

  <b-skeleton-wrapper :loading="!versao || !versoes || !cargas || versoes.length < 1 || cargas.length < 1">
    <template #loading>
      <div class="my-5 mx-5">
        <b-skeleton width="85%" height="60px"></b-skeleton>
        <b-skeleton width="55%"></b-skeleton>
        <b-skeleton width="70%"></b-skeleton>
      </div>
    </template>

    <div v-if="versao_loaded" class="container mt-4">
      <ul v-if="versoes && versoes.length > 0 && cargas && cargas.length > 0">
        <li v-for="v in versoes" class="mb-5 mt-2">
          <h1 class="display-6">
            <font-awesome-icon icon="database"></font-awesome-icon>
            Version <strong>{{ v.nome }}</strong>
          </h1>
          <ul>
            <li v-for="carga in cargas.filter(c => c.versao?.id === v.id)" class="my-2">
              <b>File {{ carga.id }}</b>
              <code class="mx-1">{{ carga.nome }}</code>
              <template v-if="carga.status?.includes('|')">
                <br />
                <b>MD5</b>
                <span class="badge badge-light">{{ carga.status.split('|')[1] }}</span>
              </template>
              <template v-else>{{ carga.status }}</template>
              <button :disabled="downloading" @click="download(carga)" class="btn btn-success btn-sm mx-2">
                <font-awesome-icon icon="cloud-arrow-down" v-if="!downloading" />
                <b-spinner small v-if="downloading"></b-spinner>
              </button>
            </li>
          </ul>
        </li>
      </ul>
    </div>
  </b-skeleton-wrapper>
</template>

<script lang="ts" src="./downloads.component.ts"></script>

<style scoped></style>
