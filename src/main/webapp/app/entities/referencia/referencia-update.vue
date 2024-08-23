<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="semprotdbApp.referencia.home.createOrEditLabel"
          data-cy="ReferenciaCreateUpdateHeading"
          v-text="t$('semprotdbApp.referencia.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="referencia.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="referencia.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.referencia.citacao')" for="referencia-citacao"></label>
            <input
              type="text"
              class="form-control"
              name="citacao"
              id="referencia-citacao"
              data-cy="citacao"
              :class="{ valid: !v$.citacao.$invalid, invalid: v$.citacao.$invalid }"
              v-model="v$.citacao.$model"
              required
            />
            <div v-if="v$.citacao.$anyDirty && v$.citacao.$invalid">
              <small class="form-text text-danger" v-for="error of v$.citacao.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.referencia.link')" for="referencia-link"></label>
            <input
              type="text"
              class="form-control"
              name="link"
              id="referencia-link"
              data-cy="link"
              :class="{ valid: !v$.link.$invalid, invalid: v$.link.$invalid }"
              v-model="v$.link.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.referencia.ano')" for="referencia-ano"></label>
            <input
              type="number"
              class="form-control"
              name="ano"
              id="referencia-ano"
              data-cy="ano"
              :class="{ valid: !v$.ano.$invalid, invalid: v$.ano.$invalid }"
              v-model.number="v$.ano.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.referencia.autores')" for="referencia-autores"></label>
            <input
              type="text"
              class="form-control"
              name="autores"
              id="referencia-autores"
              data-cy="autores"
              :class="{ valid: !v$.autores.$invalid, invalid: v$.autores.$invalid }"
              v-model="v$.autores.$model"
            />
          </div>
          <div class="form-group">
            <label v-text="t$('semprotdbApp.referencia.proteina')" for="referencia-proteina"></label>
            <select
              class="form-control"
              id="referencia-proteinas"
              data-cy="proteina"
              multiple
              name="proteina"
              v-if="referencia.proteinas !== undefined"
              v-model="referencia.proteinas"
            >
              <option
                v-bind:value="getSelected(referencia.proteinas, proteinaOption, 'id')"
                v-for="proteinaOption in proteinas"
                :key="proteinaOption.id"
              >
                {{ proteinaOption.id }}
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
<script lang="ts" src="./referencia-update.component.ts"></script>
