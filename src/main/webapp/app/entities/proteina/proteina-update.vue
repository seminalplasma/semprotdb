<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="semprotdbApp.proteina.home.createOrEditLabel"
          data-cy="ProteinaCreateUpdateHeading"
          v-text="t$('semprotdbApp.proteina.home.createOrEditLabel')"
        ></h2>
        <div>
          <!--ID-->
          <div class="form-group" v-if="proteina.id">
            <label for="id" class="font-weight-bold h6" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="proteina.id" readonly />
          </div>

          <!--VERSAO-->
          <div class="form-group">
            <label class="form-control-label font-weight-bold h6" v-text="t$('semprotdbApp.proteina.versao')" for="proteina-versao"></label>
            <select disabled class="form-control" id="proteina-versao" data-cy="versao" name="versao" v-model="proteina.versao">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="proteina.versao && versaoOption.id === proteina.versao.id ? proteina.versao : versaoOption"
                v-for="versaoOption in versaos"
                :key="versaoOption.id"
              >
                {{ versaoOption.id }} [{{ versaoOption.numero }}] {{ versaoOption.nome }}
              </option>
            </select>
          </div>

          <!--CURADORIA-->
          <div class="form-group" v-if="proteina.curadoria">
            <label
              class="form-control-label font-weight-bold h6"
              v-text="t$('semprotdbApp.proteina.curadoria')"
              for="proteina-curadoria"
            ></label>
            <select class="form-control" id="proteina-curadoria" data-cy="curadoria" name="curadoria" v-model="proteina.curadoria" disabled>
              <option v-bind:value="null"></option>
              <option
                v-bind:value="proteina.curadoria && curadoriaOption.id === proteina.curadoria.id ? proteina.curadoria : curadoriaOption"
                v-for="curadoriaOption in curadorias"
                :key="curadoriaOption.id"
              >
                {{ curadoriaOption.id }} - {{ curadoriaOption.email }}
              </option>
            </select>
          </div>

          <b-alert variant="warning" show
            >Nao recomendo <b>alterar o nome</b>, use o campo <code>DESCRICAO</code> para ajustar o nome da proteina.</b-alert
          >

          <!--NOME-->
          <div class="form-group">
            <label class="form-control-label font-weight-bold h6" v-text="t$('semprotdbApp.proteina.nome')" for="proteina-nome"></label>
            <input
              type="text"
              class="form-control"
              name="nome"
              id="proteina-nome"
              data-cy="nome"
              :class="{ valid: !v$.nome.$invalid, invalid: v$.nome.$invalid }"
              v-model="v$.nome.$model"
              required
            />
            <div v-if="v$.nome.$anyDirty && v$.nome.$invalid">
              <small class="form-text text-danger" v-for="error of v$.nome.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>

          <!-- TAMANHO         -->
          <div class="form-group">
            <label
              class="form-control-label font-weight-bold h6"
              v-text="t$('semprotdbApp.proteina.tamanho')"
              for="proteina-tamanho"
            ></label>
            <input
              type="number"
              class="form-control"
              name="tamanho"
              id="proteina-tamanho"
              data-cy="tamanho"
              :class="{ valid: !v$.tamanho.$invalid, invalid: v$.tamanho.$invalid }"
              v-model.number="v$.tamanho.$model"
            />
          </div>

          <!--MASSA          -->
          <div class="form-group">
            <label class="form-control-label font-weight-bold h6" v-text="t$('semprotdbApp.proteina.massa')" for="proteina-massa"></label>
            <input
              type="text"
              class="form-control"
              name="massa"
              id="proteina-massa"
              data-cy="massa"
              :class="{ valid: !v$.massa.$invalid, invalid: v$.massa.$invalid }"
              v-model="v$.massa.$model"
            />
          </div>

          <!--DESCRICAO          -->
          <div class="form-group">
            <label
              class="form-control-label font-weight-bold h6"
              v-text="t$('semprotdbApp.proteina.descricao')"
              for="proteina-descricao"
            ></label>
            <input
              type="text"
              class="form-control"
              name="descricao"
              id="proteina-descricao"
              data-cy="descricao"
              :class="{ valid: !v$.descricao.$invalid, invalid: v$.descricao.$invalid }"
              v-model="v$.descricao.$model"
            />
          </div>

          <!--GENE-->

          <b-input-group :prepend="t$('semprotdbApp.proteina.gene')">
            <input
              type="text"
              class="form-control"
              name="gene"
              id="proteina-gene"
              data-cy="gene"
              :class="{ valid: !v$.gene.$invalid, invalid: v$.gene.$invalid }"
              v-model="geneQ"
            />
            <b-input-group-append>
              <b-button @click="setGene" :disabled="isSaving">
                <font-awesome-icon icon="search"></font-awesome-icon>
              </b-button>
            </b-input-group-append>
          </b-input-group>

          <div class="my-2">
            <template v-for="gene in genes">
              <b-button
                v-if="gene.descricao"
                class="m-1"
                size="sm"
                :variant="`${geneQ === gene.descricao ? '' : 'outline-'}primary`"
                :pressed="geneQ === gene.descricao"
                @click="
                  proteina.gene = gene;
                  geneQ = gene.descricao;
                "
                @dblclick="toGene(gene)"
              >
                {{ gene.descricao }}
              </b-button>
            </template>
          </div>

          <!--          Referencia-->
          <div class="form-group">
            <label v-text="t$('semprotdbApp.proteina.referencia')" for="proteina-referencia" class="font-weight-bold h6"></label>
            <select
              class="form-control"
              id="proteina-referencias"
              data-cy="referencia"
              multiple
              name="referencia"
              v-if="proteina.referencias !== undefined"
              v-model="proteina.referencias"
            >
              <option
                v-bind:value="getSelected(proteina.referencias, referenciaOption, 'id')"
                v-for="referenciaOption in referencias"
                :key="referenciaOption.id"
              >
                {{ referenciaOption.id }} - {{ referenciaOption.citacao }}
              </option>
            </select>
          </div>

          <!--          Links-->
          <div class="form-group">
            <label for="proteina-link" class="font-weight-bold h6">Links</label>

            <b-input-group prepend="Link">
              <input type="text" class="form-control" name="link" id="proteina-nlink" v-model="linkQ" />
              <b-input-group-append>
                <b-button @click="setLink" :disabled="isSaving">
                  <font-awesome-icon icon="bolt"></font-awesome-icon>
                </b-button>
              </b-input-group-append>
            </b-input-group>

            <ul id="proteina-link">
              <li v-for="db in new Set(proteina.recursos?.map(r => r.db))">
                {{ db }}
                <ul>
                  <li v-for="rec in proteina.recursos?.filter(r => r.db === db)">
                    <a :href="rec.link" target="_blank">[{{ rec.id }}] - {{ rec.uid }}</a>
                    <b-button size="sm" variant="warning" pill class="mx-2" @click="toLink(rec)">
                      <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    </b-button>
                    <b-button size="sm" variant="danger" pill @click="proteina.recursos = proteina.recursos?.filter(r => r !== rec)">
                      <font-awesome-icon icon="trash"></font-awesome-icon>
                    </b-button>
                  </li>
                </ul>
              </li>
            </ul>
          </div>
        </div>

        <div class="mt-5 mb-3">
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary mx-2"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
          <b-button
            @click="(isCurar = !isCurar) ? (proteina.curadoria = curadorias[0]) : (proteina.curadoria = null)"
            :disabled="isSaving || !curadorias || curadorias.length < 1"
            :variant="isCurar ? 'success' : 'outline-success'"
          >
            <font-awesome-icon icon="shield"></font-awesome-icon>
            Curar
          </b-button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./proteina-update.component.ts"></script>
