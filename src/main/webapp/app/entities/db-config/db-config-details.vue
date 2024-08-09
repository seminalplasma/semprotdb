<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="dBConfig">
        <h2 class="jh-entity-heading" data-cy="dBConfigDetailsHeading">
          <span v-text="t$('semprotdbApp.dBConfig.detail.title')"></span> {{ dBConfig.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('semprotdbApp.dBConfig.key')"></span>
          </dt>
          <dd>
            <span>{{ dBConfig.key }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.dBConfig.habilitado')"></span>
          </dt>
          <dd>
            <span>{{ dBConfig.habilitado }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.dBConfig.vstring')"></span>
          </dt>
          <dd>
            <span>{{ dBConfig.vstring }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.dBConfig.vbol')"></span>
          </dt>
          <dd>
            <span>{{ dBConfig.vbol }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.dBConfig.vdate')"></span>
          </dt>
          <dd>
            <span v-if="dBConfig.vdate">{{ formatDateLong(dBConfig.vdate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.dBConfig.vint')"></span>
          </dt>
          <dd>
            <span>{{ dBConfig.vint }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.dBConfig.vtext')"></span>
          </dt>
          <dd>
            <span>{{ dBConfig.vtext }}</span>
          </dd>
          <dt>
            <span v-text="t$('semprotdbApp.dBConfig.vimg')"></span>
          </dt>
          <dd>
            <div v-if="dBConfig.vimg">
              <a v-on:click="openFile(dBConfig.vimgContentType, dBConfig.vimg)">
                <img v-bind:src="'data:' + dBConfig.vimgContentType + ';base64,' + dBConfig.vimg" style="max-width: 100%" alt="dBConfig" />
              </a>
              {{ dBConfig.vimgContentType }}, {{ byteSize(dBConfig.vimg) }}
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="dBConfig.id" :to="{ name: 'DBConfigEdit', params: { dBConfigId: dBConfig.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./db-config-details.component.ts"></script>
