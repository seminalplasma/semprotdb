<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="semprotdbApp.gene.home.createOrEditLabel"
          data-cy="GeneCreateUpdateHeading"
          v-text="t$('semprotdbApp.gene.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="gene.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="gene.id" readonly />
          </div>

          <b-alert variant="warning" show>Nao recomendo <b>alterar o nome</b>, use o campo DESCRICAO para ajustar o nome do gene.</b-alert>

          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.gene.nome')" for="gene-nome"></label>
            <input
              type="text"
              class="form-control"
              name="nome"
              id="gene-nome"
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
            <label class="form-control-label" v-text="t$('semprotdbApp.gene.descricao')" for="gene-descricao"></label>
            <input
              type="text"
              class="form-control"
              name="descricao"
              id="gene-descricao"
              data-cy="descricao"
              :class="{ valid: !v$.descricao.$invalid, invalid: v$.descricao.$invalid }"
              v-model="v$.descricao.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.gene.curadoria')" for="gene-curadoria"></label>
            <select class="form-control" id="gene-curadoria" data-cy="curadoria" name="curadoria" v-model="gene.curadoria">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="gene.curadoria && curadoriaOption.id === gene.curadoria.id ? gene.curadoria : curadoriaOption"
                v-for="curadoriaOption in curadorias"
                :key="curadoriaOption.id"
              >
                {{ curadoriaOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.gene.organismo')" for="gene-organismo"></label>
            <select class="form-control" id="gene-organismo" data-cy="organismo" name="organismo" v-model="gene.organismo">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="gene.organismo && organismoOption.id === gene.organismo.id ? gene.organismo : organismoOption"
                v-for="organismoOption in organismos"
                :key="organismoOption.id"
              >
                {{ organismoOption.id }}
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
            class="btn btn-primary mx-2"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./gene-update.component.ts"></script>
