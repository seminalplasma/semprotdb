<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="gene">
        <h2 class="jh-entity-heading" data-cy="geneDetailsHeading">
          <span v-text="t$('semprotdbApp.gene.detail.title')"></span> {{ gene.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('semprotdbApp.gene.nome')"></span>
          </dt>
          <dd>
            <span>{{ gene.nome }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.gene.descricao')"></span>
          </dt>
          <dd>
            <span>{{ gene.descricao }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.gene.curadoria')"></span>
          </dt>
          <dd>
            <div v-if="gene.curadoria">
              <router-link :to="{ name: 'CuradoriaView', params: { curadoriaId: gene.curadoria.id } }">{{ gene.curadoria.id }}</router-link>
            </div>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.gene.organismo')"></span>
          </dt>
          <dd>
            <div v-if="gene.organismo">
              <router-link :to="{ name: 'OrganismoView', params: { organismoId: gene.organismo.id } }">{{ gene.organismo.id }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="gene.id" :to="{ name: 'GeneEdit', params: { geneId: gene.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary mx-2">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./gene-details.component.ts"></script>
