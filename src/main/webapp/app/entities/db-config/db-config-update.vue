<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="semprotdbApp.dBConfig.home.createOrEditLabel"
          data-cy="DBConfigCreateUpdateHeading"
          v-text="t$('semprotdbApp.dBConfig.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="dBConfig.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="dBConfig.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.dBConfig.key')" for="db-config-key"></label>
            <input
              type="text"
              class="form-control"
              name="key"
              id="db-config-key"
              data-cy="key"
              :class="{ valid: !v$.key.$invalid, invalid: v$.key.$invalid }"
              v-model="v$.key.$model"
              required
            />
            <div v-if="v$.key.$anyDirty && v$.key.$invalid">
              <small class="form-text text-danger" v-for="error of v$.key.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.dBConfig.habilitado')" for="db-config-habilitado"></label>
            <input
              type="checkbox"
              class="form-check"
              name="habilitado"
              id="db-config-habilitado"
              data-cy="habilitado"
              :class="{ valid: !v$.habilitado.$invalid, invalid: v$.habilitado.$invalid }"
              v-model="v$.habilitado.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.dBConfig.vstring')" for="db-config-vstring"></label>
            <input
              type="text"
              class="form-control"
              name="vstring"
              id="db-config-vstring"
              data-cy="vstring"
              :class="{ valid: !v$.vstring.$invalid, invalid: v$.vstring.$invalid }"
              v-model="v$.vstring.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.dBConfig.vbol')" for="db-config-vbol"></label>
            <input
              type="checkbox"
              class="form-check"
              name="vbol"
              id="db-config-vbol"
              data-cy="vbol"
              :class="{ valid: !v$.vbol.$invalid, invalid: v$.vbol.$invalid }"
              v-model="v$.vbol.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.dBConfig.vdate')" for="db-config-vdate"></label>
            <div class="d-flex">
              <input
                id="db-config-vdate"
                data-cy="vdate"
                type="datetime-local"
                class="form-control"
                name="vdate"
                :class="{ valid: !v$.vdate.$invalid, invalid: v$.vdate.$invalid }"
                :value="convertDateTimeFromServer(v$.vdate.$model)"
                @change="updateInstantField('vdate', $event)"
              />
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.dBConfig.vint')" for="db-config-vint"></label>
            <input
              type="number"
              class="form-control"
              name="vint"
              id="db-config-vint"
              data-cy="vint"
              :class="{ valid: !v$.vint.$invalid, invalid: v$.vint.$invalid }"
              v-model.number="v$.vint.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.dBConfig.vtext')" for="db-config-vtext"></label>
            <textarea
              class="form-control"
              name="vtext"
              id="db-config-vtext"
              data-cy="vtext"
              :class="{ valid: !v$.vtext.$invalid, invalid: v$.vtext.$invalid }"
              v-model="v$.vtext.$model"
            ></textarea>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.dBConfig.vimg')" for="db-config-vimg"></label>
            <div>
              <img
                v-bind:src="'data:' + dBConfig.vimgContentType + ';base64,' + dBConfig.vimg"
                style="max-height: 100px"
                v-if="dBConfig.vimg"
                alt="dBConfig"
              />
              <div v-if="dBConfig.vimg" class="form-text text-danger clearfix">
                <span class="pull-left">{{ dBConfig.vimgContentType }}, {{ byteSize(dBConfig.vimg) }}</span>
                <button
                  type="button"
                  v-on:click="clearInputImage('vimg', 'vimgContentType', 'file_vimg')"
                  class="btn btn-secondary btn-xs pull-right"
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                </button>
              </div>
              <label for="file_vimg" v-text="t$('entity.action.addimage')" class="btn btn-primary pull-right"></label>
              <input
                type="file"
                ref="file_vimg"
                id="file_vimg"
                style="display: none"
                data-cy="vimg"
                v-on:change="setFileData($event, dBConfig, 'vimg', true)"
                accept="image/*"
              />
            </div>
            <input
              type="hidden"
              class="form-control"
              name="vimg"
              id="db-config-vimg"
              data-cy="vimg"
              :class="{ valid: !v$.vimg.$invalid, invalid: v$.vimg.$invalid }"
              v-model="v$.vimg.$model"
            />
            <input
              type="hidden"
              class="form-control"
              name="vimgContentType"
              id="db-config-vimgContentType"
              v-model="dBConfig.vimgContentType"
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
            class="btn btn-primary mx-2"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./db-config-update.component.ts"></script>
