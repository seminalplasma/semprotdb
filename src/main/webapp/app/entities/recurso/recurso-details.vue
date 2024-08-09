<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="recurso">
        <h2 class="jh-entity-heading" data-cy="recursoDetailsHeading">
          <span v-text="t$('semprotdbApp.recurso.detail.title')"></span> {{ recurso.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('semprotdbApp.recurso.uid')"></span>
          </dt>
          <dd>
            <span>{{ recurso.uid }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.recurso.db')"></span>
          </dt>
          <dd>
            <span v-text="t$('semprotdbApp.BioDB.' + recurso.db)"></span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.recurso.link')"></span>
          </dt>
          <dd>
            <span>{{ recurso.link }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.recurso.proteina')"></span>
          </dt>
          <dd>
            <span v-for="(proteina, i) in recurso.proteinas" :key="proteina.id"
              >{{ i > 0 ? ', ' : '' }}
              <router-link :to="{ name: 'ProteinaView', params: { proteinaId: proteina.id } }">{{ proteina.id }}</router-link>
            </span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="recurso.id" :to="{ name: 'RecursoEdit', params: { recursoId: recurso.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./recurso-details.component.ts"></script>
