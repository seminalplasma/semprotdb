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
          <div class="form-group" v-if="proteina.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="proteina.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.proteina.nome')" for="proteina-nome"></label>
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
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.proteina.tamanho')" for="proteina-tamanho"></label>
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
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.proteina.massa')" for="proteina-massa"></label>
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
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.proteina.descricao')" for="proteina-descricao"></label>
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
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.proteina.curadoria')" for="proteina-curadoria"></label>
            <select class="form-control" id="proteina-curadoria" data-cy="curadoria" name="curadoria" v-model="proteina.curadoria">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="proteina.curadoria && curadoriaOption.id === proteina.curadoria.id ? proteina.curadoria : curadoriaOption"
                v-for="curadoriaOption in curadorias"
                :key="curadoriaOption.id"
              >
                {{ curadoriaOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.proteina.versao')" for="proteina-versao"></label>
            <select class="form-control" id="proteina-versao" data-cy="versao" name="versao" v-model="proteina.versao">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="proteina.versao && versaoOption.id === proteina.versao.id ? proteina.versao : versaoOption"
                v-for="versaoOption in versaos"
                :key="versaoOption.id"
              >
                {{ versaoOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.proteina.gene')" for="proteina-gene"></label>
            <select class="form-control" id="proteina-gene" data-cy="gene" name="gene" v-model="proteina.gene">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="proteina.gene && geneOption.id === proteina.gene.id ? proteina.gene : geneOption"
                v-for="geneOption in genes"
                :key="geneOption.id"
              >
                {{ geneOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label v-text="t$('semprotdbApp.proteina.referencia')" for="proteina-referencia"></label>
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
                {{ referenciaOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label v-text="t$('semprotdbApp.proteina.recurso')" for="proteina-recurso"></label>
            <select
              class="form-control"
              id="proteina-recursos"
              data-cy="recurso"
              multiple
              name="recurso"
              v-if="proteina.recursos !== undefined"
              v-model="proteina.recursos"
            >
              <option
                v-bind:value="getSelected(proteina.recursos, recursoOption, 'id')"
                v-for="recursoOption in recursos"
                :key="recursoOption.id"
              >
                {{ recursoOption.id }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./proteina-update.component.ts"></script>
