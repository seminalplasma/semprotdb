<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="proteina">
        <h2 class="jh-entity-heading" data-cy="proteinaDetailsHeading">
          <span v-text="t$('semprotdbApp.proteina.detail.title')"></span> {{ proteina.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('semprotdbApp.proteina.nome')"></span>
          </dt>
          <dd>
            <span>{{ proteina.nome }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.proteina.tamanho')"></span>
          </dt>
          <dd>
            <span>{{ proteina.tamanho }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.proteina.massa')"></span>
          </dt>
          <dd>
            <span>{{ proteina.massa }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.proteina.descricao')"></span>
          </dt>
          <dd>
            <span>{{ proteina.descricao }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.proteina.curadoria')"></span>
          </dt>
          <dd>
            <div v-if="proteina.curadoria">
              <router-link :to="{ name: 'CuradoriaView', params: { curadoriaId: proteina.curadoria.id } }">{{
                proteina.curadoria.id
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.proteina.versao')"></span>
          </dt>
          <dd>
            <div v-if="proteina.versao">
              <router-link :to="{ name: 'VersaoView', params: { versaoId: proteina.versao.id } }">{{ proteina.versao.id }}</router-link>
            </div>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.proteina.gene')"></span>
          </dt>
          <dd>
            <div v-if="proteina.gene">
              <router-link :to="{ name: 'GeneView', params: { geneId: proteina.gene.id } }">{{ proteina.gene.id }}</router-link>
            </div>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.proteina.referencia')"></span>
          </dt>
          <dd>
            <span v-for="(referencia, i) in proteina.referencias" :key="referencia.id"
              >{{ i > 0 ? ', ' : '' }}
              <router-link :to="{ name: 'ReferenciaView', params: { referenciaId: referencia.id } }">{{ referencia.id }}</router-link>
            </span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.proteina.recurso')"></span>
          </dt>
          <dd>
            <span v-for="(recurso, i) in proteina.recursos" :key="recurso.id"
              >{{ i > 0 ? ', ' : '' }}
              <router-link :to="{ name: 'RecursoView', params: { recursoId: recurso.id } }">{{ recurso.id }}</router-link>
            </span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="proteina.id" :to="{ name: 'ProteinaEdit', params: { proteinaId: proteina.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./proteina-details.component.ts"></script>
