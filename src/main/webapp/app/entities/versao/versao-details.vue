<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="versao">
        <h2 class="jh-entity-heading" data-cy="versaoDetailsHeading">
          <span v-text="t$('semprotdbApp.versao.detail.title')"></span> {{ versao.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('semprotdbApp.versao.nome')"></span>
          </dt>
          <dd>
            <span>{{ versao.nome }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.versao.detalhes')"></span>
          </dt>
          <dd>
            <span>{{ versao.detalhes }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.versao.release')"></span>
          </dt>
          <dd>
            <span v-if="versao.release">{{ formatDateLong(versao.release) }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.versao.label')"></span>
          </dt>
          <dd>
            <span>{{ versao.label }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.versao.status')"></span>
          </dt>
          <dd>
            <span v-text="t$('semprotdbApp.Status.' + versao.status)"></span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.versao.numero')"></span>
          </dt>
          <dd>
            <span>{{ versao.numero }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.versao.logo')"></span>
          </dt>
          <dd>
            <span>{{ versao.logo }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.versao.log')"></span>
          </dt>
          <dd>
            <span>{{ versao.log }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.versao.texto')"></span>
          </dt>
          <dd>
            <span>{{ versao.texto }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.versao.imagem')"></span>
          </dt>
          <dd>
            <div v-if="versao.imagem">
              <a v-on:click="openFile(versao.imagemContentType, versao.imagem)">
                <img v-bind:src="'data:' + versao.imagemContentType + ';base64,' + versao.imagem" style="max-width: 100%" alt="versao" />
              </a>
              {{ versao.imagemContentType }}, {{ byteSize(versao.imagem) }}
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="versao.id" :to="{ name: 'VersaoEdit', params: { versaoId: versao.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary mx-2">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./versao-details.component.ts"></script>
