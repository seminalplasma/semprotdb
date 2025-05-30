<template>
  <div id="app">
    <ribbon></ribbon>
    <div id="app-header">
      <jhi-navbar></jhi-navbar>
    </div>
    <div class="container-fluid">
      <div class="card jh-card conteudo shadow">
        <router-view></router-view>
      </div>
      <b-modal id="login-page" hide-footer lazy>
        <template #modal-title>
          <span data-cy="loginTitle" id="login-title" v-text="t$('login.title')"></span>
        </template>
        <login-form></login-form>
      </b-modal>

      <jhi-footer></jhi-footer>
    </div>
  </div>
</template>

<script lang="ts" src="./app.component.ts"></script>

<style scoped>
.contanier {
  background-color: rgba(95, 50, 205, 0.02);
}

.conteudo {
  background-color: #f8f8f8;
  background-repeat: no-repeat;
  margin-left: 8px;
  margin-right: 8px;
}

@media (max-width: 768px) {
  .conteudo {
    margin-left: 0;
    margin-right: 0;
  }
  .jh-card {
    margin-top: 0;
  }
  .container-fluid {
    padding-left: 0;
    padding-right: 0;
  }
}
</style>
