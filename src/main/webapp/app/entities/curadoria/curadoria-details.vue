<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="curadoria">
        <h2 class="jh-entity-heading" data-cy="curadoriaDetailsHeading">
          <span v-text="t$('semprotdbApp.curadoria.detail.title')"></span> {{ curadoria.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('semprotdbApp.curadoria.email')"></span>
          </dt>
          <dd>
            <span>{{ curadoria.email }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.curadoria.data')"></span>
          </dt>
          <dd>
            <span v-if="curadoria.data">{{ formatDateLong(curadoria.data) }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.curadoria.anotacoes')"></span>
          </dt>
          <dd>
            <span>{{ curadoria.anotacoes }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="curadoria.id"
          :to="{ name: 'CuradoriaEdit', params: { curadoriaId: curadoria.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./curadoria-details.component.ts"></script>
