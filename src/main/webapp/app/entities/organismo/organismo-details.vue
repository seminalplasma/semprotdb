<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="organismo">
        <h2 class="jh-entity-heading" data-cy="organismoDetailsHeading">
          <span v-text="t$('semprotdbApp.organismo.detail.title')"></span> {{ organismo.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('semprotdbApp.organismo.nome')"></span>
          </dt>
          <dd>
            <span>{{ organismo.nome }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.organismo.sigla')"></span>
          </dt>
          <dd>
            <span>{{ organismo.sigla }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.organismo.apelido')"></span>
          </dt>
          <dd>
            <span>{{ organismo.apelido }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.organismo.silhueta')"></span>
          </dt>
          <dd>
            <div v-if="organismo.silhueta">
              <a v-on:click="openFile(organismo.silhuetaContentType, organismo.silhueta)">
                <img
                  v-bind:src="'data:' + organismo.silhuetaContentType + ';base64,' + organismo.silhueta"
                  style="max-width: 100%"
                  alt="organismo"
                />
              </a>
              {{ organismo.silhuetaContentType }}, {{ byteSize(organismo.silhueta) }}
            </div>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.organismo.icone')"></span>
          </dt>
          <dd>
            <span>{{ organismo.icone }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.organismo.pos')"></span>
          </dt>
          <dd>
            <span>{{ organismo.pos }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.organismo.imagem')"></span>
          </dt>
          <dd>
            <span>{{ organismo.imagem }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.organismo.descricao')"></span>
          </dt>
          <dd>
            <span>{{ organismo.descricao }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="organismo.id"
          :to="{ name: 'OrganismoEdit', params: { organismoId: organismo.id } }"
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

<script lang="ts" src="./organismo-details.component.ts"></script>
