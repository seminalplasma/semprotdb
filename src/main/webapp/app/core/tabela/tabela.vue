<template>
  <div class="row justify-content-between">
    <div class="px- mx-2 bg-light p-2 m-2 border-left border-secondary" style="border-width: 12px !important ; max-height: 8rem">
      <h1 class="display-3 text-primary">
        <font-awesome-icon icon="dna"></font-awesome-icon>
        <span v-text="t$('semprotdbApp.tabela.title')"></span>
      </h1>
    </div>

    <div class="col-5 mx-2" v-if="versao_loaded">
      <!--      <div class="row">-->
      <b-form-group class="w-100" id="fieldset-2" :disabled="isFetching || queryPT2e.length < 1">
        <b-input-group class="mt-3" size="sm">
          <template #prepend>
            <b-button variant="info" v-if="queryes.length > 0" @click="queryPT1 = queryPT1 === 'AND' ? 'OR' : 'AND'">
              {{ queryPT1 }}
            </b-button>
            <b-dropdown :text="queryPT2" variant="secondary" size="sm">
              <template v-for="entity of queryPT2e">
                <b-dropdown-item
                  @click="
                    queryPT2 = entity;
                    queryPT3 = (queryPT2n.includes(queryPT2) ? fOps2 : fOps)[0];
                  "
                  >{{ entity }}
                </b-dropdown-item>
              </template>
            </b-dropdown>
            <b-dropdown :text="queryPT3" variant="secondary" size="sm" v-model="queryPT3">
              <b-dropdown-item v-for="filtOp of queryPT2n.includes(queryPT2) ? fOps2 : fOps" @click="queryPT3 = filtOp">
                {{ filtOp }}
              </b-dropdown-item>
            </b-dropdown>
          </template>

          <b-form-input
            id="search"
            disabled
            :placeholder="t$('semprotdbApp.tabela.filterBellow')"
            type="text"
            v-model="query"
          ></b-form-input>

          <b-input-group-append>
            <b-button
              variant="primary"
              :disabled="query.length < 1"
              @click="
                queryes.push(`${queryes.length > 0 ? queryPT1 : ''} ${queryPT2} ${queryPT3} ${query}`);
                query = '';
                queryPT2e = queryPT2e.filter(x => x !== queryPT2);
                queryPT2 = queryPT2e.length > 0 ? queryPT2e[0] : 'Protein';
                queryPT3 = (queryPT2n.includes(queryPT2) ? fOps2 : fOps)[0];
              "
            >
              <font-awesome-icon icon="filter"></font-awesome-icon>
            </b-button>
          </b-input-group-append>
        </b-input-group>
      </b-form-group>
      <!--      </div>-->
      <!--      <div class="row">-->
      <b-form-group class="w-100" id="fieldset-1" label-for="search">
        <b-input-group class="mt-3">
          <b-form-input
            id="search"
            :disabled="isFetching || queryPT2e.length < 1"
            :placeholder="queryes.length > 0 ? '...' : t$('semprotdbApp.tabela.proteinOrGeneName')"
            type="text"
            v-model="query"
          ></b-form-input>

          <template #append>
            <b-button v-if="queryes.length > 0" variant="danger" @click="reset()" :disabled="isFetching">
              <font-awesome-icon icon="x"></font-awesome-icon>
            </b-button>
            <b-button variant="primary" @click="search()" :disabled="isFetching">
              <b-spinner v-if="isFetching" small label="Small Spinner"></b-spinner>
              <font-awesome-icon v-else-if="queryes.length > 0" icon="check"></font-awesome-icon>
              <font-awesome-icon v-else icon="search"></font-awesome-icon>
            </b-button>
          </template>
        </b-input-group>
      </b-form-group>
      <!--      </div>-->

      <b-form-group class="w-100" id="fieldset-3" v-if="queryes.length > 0">
        <b-form-tags disabled placeholder="" input-id="tags-basic" v-model="queryes"></b-form-tags>
      </b-form-group>
    </div>
  </div>

  <div v-if="versao && versao_loaded && versao.id && versao.id > 0">
    <div class="table-responsive-lg" v-if="proteinas && proteinas.length > 0">
      <table class="table table-sm table-striped table-hover" aria-describedby="proteinas">
        <thead class="bg-dark text-light">
          <tr>
            <th scope="row" class="text-success align-middle text-center">
              <b-button variant="outline-success" size="sm" pill :pressed="curado" @click="changeCur">
                <font-awesome-icon class="m-0 p-0" icon="shield"></font-awesome-icon>
              </b-button>
            </th>
            <th class="align-middle text-center" scope="row" v-on:click="changeOrder('GeneOrganismoApelido')">
              <span v-text="t$('semprotdbApp.tabela.organismo')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'GeneOrganismoApelido'"></jhi-sort-indicator>
            </th>
            <th class="align-middle" scope="row" v-on:click="changeOrder('GeneNome')">
              <span v-text="t$('semprotdbApp.tabela.gene')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'GeneNome'"></jhi-sort-indicator>
            </th>
            <th class="align-middle" scope="row" v-on:click="changeOrder('descricao')">
              <span v-text="t$('semprotdbApp.tabela.protein')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'descricao'"></jhi-sort-indicator>
            </th>
            <th class="align-middle text-center" scope="row" v-on:click="changeOrder('tamanho')">
              <span v-text="t$('semprotdbApp.proteina.tamanho')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'tamanho'"></jhi-sort-indicator>
            </th>
            <th class="align-middle text-center" scope="row" v-on:click="changeOrder('massa')">
              <span v-text="t$('semprotdbApp.proteina.massa')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'massa'"></jhi-sort-indicator>
            </th>
            <th class="align-middle text-center" scope="row">
              <span v-text="t$('semprotdbApp.tabela.recurso')"></span>
            </th>
            <th class="align-middle text-center" scope="row">
              <span v-text="t$('semprotdbApp.tabela.referencia')"></span>
            </th>
          </tr>
        </thead>
        <tbody class="table-group-divider">
          <tr class="align-middle" v-for="proteina in proteinas" :key="proteina.id" data-cy="entityTable">
            <td class="align-middle text-center">
              <template v-if="authenticated">
                <router-link :to="{ name: 'ProteinaEdit', params: { proteinaId: proteina.id } }" custom v-slot="{ navigate }">
                  <b-button
                    @click="navigate"
                    size="sm"
                    :variant="proteina.curadoria?.id ? 'success' : 'secondary'"
                    :id="`${proteina.id}.${proteina.versao?.numero}`"
                  >
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                  </b-button>
                </router-link>
              </template>
              <template v-else>
                <font-awesome-icon
                  :id="`${proteina.id}.${proteina.versao?.numero}`"
                  :icon="proteina.curadoria?.id ? 'shield' : 'tag'"
                  :class="(proteina.curadoria?.id ? 'text-success' : 'text-primary') + ' mr-2'"
                ></font-awesome-icon>
              </template>
              <b-tooltip
                placement="right"
                :target="`${proteina.id}.${proteina.versao?.numero}`"
                :title="`${proteina.id}.${proteina.versao?.numero}`"
              ></b-tooltip>
            </td>
            <td class="align-middle text-center text-dark">
              <em> {{ proteina.gene?.organismo?.apelido }}</em>
            </td>
            <td class="align-middle text-dark">
              <u>
                <ins
                  ><small style="letter-spacing: -0.05rem">{{ proteina.gene?.descricao ?? proteina.gene?.nome }}</small></ins
                >
              </u>
            </td>
            <td class="align-middle text-truncate text-dark" style="max-width: 20rem">
              <strong>{{ proteina.descricao }}</strong>
            </td>
            <td class="align-middle text-right text-dark">{{ proteina.tamanho ? proteina.tamanho + 'aa' : 'Ø' }}</td>
            <td class="align-middle text-center text-dark">{{ proteina.massa === 'UNDEFINED' ? 'Ø' : proteina.massa }}</td>

            <td class="align-middle text-center">
              <template v-if="proteina.recursos && proteina.recursos.length > 0">
                <b-button v-if="proteina.recursos.length < 2" size="sm" :href="proteina.recursos[0].link || '#'" target="_blank"
                  >{{ proteina.recursos[0].uid }}
                </b-button>

                <b-dropdown v-else id="dropdown-1" :text="`${proteina.recursos.length} Links`" size="sm" text="Small">
                  <b-dropdown-item v-for="r in proteina.recursos" :href="r.link || '#'" target="_blank">{{ r.uid }} </b-dropdown-item>
                </b-dropdown>
              </template>
            </td>

            <td class="align-middle text-center">
              <template v-if="proteina.referencias && proteina.referencias.length > 0">
                <a
                  v-for="(referencia, idx) in proteina.referencias.sort(r => r.ano)"
                  :href="referencia.link + '?'"
                  class="autores text-black-50"
                  target="_blank"
                >
                  {{ referencia.autores ?? referencia.citacao }} {{ referencia.ano }}
                  {{ proteina.referencias.length > 1 && idx + 1 < proteina.referencias.length ? ' • ' : '' }}
                </a>

                <!--                <a -->
                <!--                  class="text-capitalize" -->
                <!--                  :href="proteina.referencias[0].link + '?'">{{ proteina.referencias[0].citacao }}</a>-->
                <!--                <span v-if="proteina.referencias.length > 1" class="badge bg-info float-md-right">-->
                <!--                  +{{ proteina.referencias.length - 1 }}-->
                <!--                </span>-->
              </template>
            </td>
          </tr>
        </tbody>
        <span ref="infiniteScrollEl"></span>
      </table>
    </div>

    <div class="d-flex align-items-center justify-content-center w-100" v-if="isFetching">
      <strong role="status">
        <template v-if="proteinas && proteinas.length > 0 && totalItems > 0">
          {{ t$('semprotdbApp.tabela.showing', { count: proteinas.length, total: totalItems }) }}
        </template>
        <template v-else>{{ t$('semprotdbApp.tabela.loading', { count: itemsPerPage }) }}</template>
      </strong>
      <div class="spinner-border ms-auto m-4" aria-hidden="true"></div>
      <div class="btn-group">
        <button class="btn btn-info btn-sm" :disabled="itemsPerPage === 20" @click="setPageSize(20)">20</button>
        <button class="btn btn-info btn-sm" :disabled="itemsPerPage === 200" @click="setPageSize(200)">200</button>
        <button class="btn btn-info btn-sm" :disabled="itemsPerPage === 2000" @click="setPageSize(2000)">2000</button>
      </div>
    </div>
    <div v-else class="m-4">
      <b-alert v-if="proteinas && proteinas.length > 0" variant="success" show>
        <font-awesome-icon icon="check"></font-awesome-icon>
        <span v-html="t$('semprotdbApp.tabela.totalFound', { count: proteinas.length })"></span>
      </b-alert>
      <b-alert v-else variant="warning" show>
        <span>
          <font-awesome-icon icon="triangle-exclamation"></font-awesome-icon>
          <strong><span v-text="t$('semprotdbApp.tabela.notFound')"></span></strong>
        </span>

        <b-button class="mx-4" @click="reset()">
          <font-awesome-icon icon="eraser"></font-awesome-icon>
          <span v-text="t$('semprotdbApp.tabela.removeAllFilters')"></span>
        </b-button>
      </b-alert>
    </div>
    <b-button class="float-right" pill @click="toTop" v-if="proteinas.length > 20">
      <font-awesome-icon icon="up-long"></font-awesome-icon>
    </b-button>
  </div>
  <div v-else><span v-text="t$('semprotdbApp.tabela.loadingVersion')"></span></div>
</template>

<style scoped>
.autores {
  font-size: xx-small;
}
</style>

<script lang="ts" src="./tabela.component.ts"></script>
