<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="carga">
        <h2 class="jh-entity-heading" data-cy="cargaDetailsHeading">
          <span v-text="t$('semprotdbApp.carga.detail.title')"></span> {{ carga.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('semprotdbApp.carga.status')"></span>
          </dt>
          <dd>
            <span>{{ carga.status }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.carga.ordem')"></span>
          </dt>
          <dd>
            <span>{{ carga.ordem }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.carga.planilha')"></span>
          </dt>
          <dd>
            <div v-if="carga.planilha">
              <a v-on:click="openFile(carga.planilhaContentType, carga.planilha)" v-text="t$('entity.action.open')"></a>
              {{ carga.planilhaContentType }}, {{ byteSize(carga.planilha) }}
            </div>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.carga.nome')"></span>
          </dt>
          <dd>
            <span>{{ carga.nome }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.carga.caminho')"></span>
          </dt>
          <dd>
            <span>{{ carga.caminho }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.carga.validado')"></span>
          </dt>
          <dd>
            <span>{{ carga.validado }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.carga.tipo')"></span>
          </dt>
          <dd>
            <span v-text="t$('semprotdbApp.Tipo.' + carga.tipo)"></span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.carga.formato')"></span>
          </dt>
          <dd>
            <span v-text="t$('semprotdbApp.Formato.' + carga.formato)"></span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.carga.destino')"></span>
          </dt>
          <dd>
            <span v-text="t$('semprotdbApp.Destino.' + carga.destino)"></span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.carga.linhas')"></span>
          </dt>
          <dd>
            <span>{{ carga.linhas }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.carga.checksum')"></span>
          </dt>
          <dd>
            <span>{{ carga.checksum }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.carga.versao')"></span>
          </dt>
          <dd>
            <div v-if="carga.versao">
              <router-link :to="{ name: 'VersaoView', params: { versaoId: carga.versao.id } }">{{ carga.versao.id }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="carga.id" :to="{ name: 'CargaEdit', params: { cargaId: carga.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./carga-details.component.ts"></script>
