<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="semprotdbApp.recurso.home.createOrEditLabel"
          data-cy="RecursoCreateUpdateHeading"
          v-text="t$('semprotdbApp.recurso.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="recurso.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="recurso.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.recurso.uid')" for="recurso-uid"></label>
            <input
              type="text"
              class="form-control"
              name="uid"
              id="recurso-uid"
              data-cy="uid"
              :class="{ valid: !v$.uid.$invalid, invalid: v$.uid.$invalid }"
              v-model="v$.uid.$model"
              required
            />
            <div v-if="v$.uid.$anyDirty && v$.uid.$invalid">
              <small class="form-text text-danger" v-for="error of v$.uid.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.recurso.db')" for="recurso-db"></label>
            <select
              class="form-control"
              name="db"
              :class="{ valid: !v$.db.$invalid, invalid: v$.db.$invalid }"
              v-model="v$.db.$model"
              id="recurso-db"
              data-cy="db"
            >
              <option v-for="bioDB in bioDBValues" :key="bioDB" v-bind:value="bioDB" v-bind:label="t$('semprotdbApp.BioDB.' + bioDB)">
                {{ bioDB }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.recurso.link')" for="recurso-link"></label>
            <input
              type="text"
              class="form-control"
              name="link"
              id="recurso-link"
              data-cy="link"
              :class="{ valid: !v$.link.$invalid, invalid: v$.link.$invalid }"
              v-model="v$.link.$model"
            />
          </div>
          <div class="form-group">
            <label v-text="t$('semprotdbApp.recurso.proteina')" for="recurso-proteina"></label>
            <select
              class="form-control"
              id="recurso-proteinas"
              data-cy="proteina"
              multiple
              name="proteina"
              v-if="recurso.proteinas !== undefined"
              v-model="recurso.proteinas"
            >
              <option
                v-bind:value="getSelected(recurso.proteinas, proteinaOption, 'id')"
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
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./recurso-update.component.ts"></script>
