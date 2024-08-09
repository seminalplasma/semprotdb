<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="semprotdbApp.curadoria.home.createOrEditLabel"
          data-cy="CuradoriaCreateUpdateHeading"
          v-text="t$('semprotdbApp.curadoria.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="curadoria.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="curadoria.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.curadoria.email')" for="curadoria-email"></label>
            <input
              type="text"
              class="form-control"
              name="email"
              id="curadoria-email"
              data-cy="email"
              :class="{ valid: !v$.email.$invalid, invalid: v$.email.$invalid }"
              v-model="v$.email.$model"
              required
            />
            <div v-if="v$.email.$anyDirty && v$.email.$invalid">
              <small class="form-text text-danger" v-for="error of v$.email.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.curadoria.data')" for="curadoria-data"></label>
            <div class="d-flex">
              <input
                id="curadoria-data"
                data-cy="data"
                type="datetime-local"
                class="form-control"
                name="data"
                :class="{ valid: !v$.data.$invalid, invalid: v$.data.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.data.$model)"
                @change="updateInstantField('data', $event)"
              />
            </div>
            <div v-if="v$.data.$anyDirty && v$.data.$invalid">
              <small class="form-text text-danger" v-for="error of v$.data.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.curadoria.anotacoes')" for="curadoria-anotacoes"></label>
            <input
              type="text"
              class="form-control"
              name="anotacoes"
              id="curadoria-anotacoes"
              data-cy="anotacoes"
              :class="{ valid: !v$.anotacoes.$invalid, invalid: v$.anotacoes.$invalid }"
              v-model="v$.anotacoes.$model"
            />
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
<script lang="ts" src="./curadoria-update.component.ts"></script>
