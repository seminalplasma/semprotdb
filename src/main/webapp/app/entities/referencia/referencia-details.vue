<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="referencia">
        <h2 class="jh-entity-heading" data-cy="referenciaDetailsHeading">
          <span v-text="t$('semprotdbApp.referencia.detail.title')"></span> {{ referencia.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('semprotdbApp.referencia.citacao')"></span>
          </dt>
          <dd>
            <span>{{ referencia.citacao }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.referencia.link')"></span>
          </dt>
          <dd>
            <span>{{ referencia.link }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.referencia.ano')"></span>
          </dt>
          <dd>
            <span>{{ referencia.ano }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.referencia.autores')"></span>
          </dt>
          <dd>
            <span>{{ referencia.autores }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.referencia.proteina')"></span>
          </dt>
          <dd>
            <span v-for="(proteina, i) in referencia.proteinas" :key="proteina.id"
              >{{ i > 0 ? ', ' : '' }}
              <router-link :to="{ name: 'ProteinaView', params: { proteinaId: proteina.id } }">{{ proteina.id }}</router-link>
            </span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="referencia.id"
          :to="{ name: 'ReferenciaEdit', params: { referenciaId: referencia.id } }"
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

<script lang="ts" src="./referencia-details.component.ts"></script>
