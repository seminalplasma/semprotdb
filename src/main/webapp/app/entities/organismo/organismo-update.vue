<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="semprotdbApp.organismo.home.createOrEditLabel"
          data-cy="OrganismoCreateUpdateHeading"
          v-text="t$('semprotdbApp.organismo.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="organismo.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="organismo.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.organismo.nome')" for="organismo-nome"></label>
            <input
              type="text"
              class="form-control"
              name="nome"
              id="organismo-nome"
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
            <label class="form-control-label" v-text="t$('semprotdbApp.organismo.sigla')" for="organismo-sigla"></label>
            <input
              type="text"
              class="form-control"
              name="sigla"
              id="organismo-sigla"
              data-cy="sigla"
              :class="{ valid: !v$.sigla.$invalid, invalid: v$.sigla.$invalid }"
              v-model="v$.sigla.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.organismo.apelido')" for="organismo-apelido"></label>
            <input
              type="text"
              class="form-control"
              name="apelido"
              id="organismo-apelido"
              data-cy="apelido"
              :class="{ valid: !v$.apelido.$invalid, invalid: v$.apelido.$invalid }"
              v-model="v$.apelido.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.organismo.silhueta')" for="organismo-silhueta"></label>
            <div>
              <img
                v-bind:src="'data:' + organismo.silhuetaContentType + ';base64,' + organismo.silhueta"
                style="max-height: 100px"
                v-if="organismo.silhueta"
                alt="organismo"
              />
              <div v-if="organismo.silhueta" class="form-text text-danger clearfix">
                <span class="pull-left">{{ organismo.silhuetaContentType }}, {{ byteSize(organismo.silhueta) }}</span>
                <button
                  type="button"
                  v-on:click="clearInputImage2('silhueta', 'silhuetaContentType', 'file_silhueta')"
                  class="btn btn-secondary btn-xs pull-right"
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                </button>
              </div>
              <label for="file_silhueta" v-text="t$('entity.action.addimage')" class="btn btn-primary pull-right"></label>
              <input
                type="file"
                ref="file_silhueta"
                id="file_silhueta"
                style="display: none"
                data-cy="silhueta"
                v-on:change="setFileData($event, organismo, 'silhueta', true)"
                accept="image/*"
              />
            </div>
            <input
              type="hidden"
              class="form-control"
              name="silhueta"
              id="organismo-silhueta"
              data-cy="silhueta"
              :class="{ valid: !v$.silhueta.$invalid, invalid: v$.silhueta.$invalid }"
              v-model="v$.silhueta.$model"
            />
            <input
              type="hidden"
              class="form-control"
              name="silhuetaContentType"
              id="organismo-silhuetaContentType"
              v-model="organismo.silhuetaContentType"
            />
          </div>
          <div class="form-group d-none">
            <label class="form-control-label" v-text="t$('semprotdbApp.organismo.icone')" for="organismo-icone"></label>
            <input
              type="text"
              class="form-control"
              name="icone"
              id="organismo-icone"
              data-cy="icone"
              :class="{ valid: !v$.icone.$invalid, invalid: v$.icone.$invalid }"
              v-model="v$.icone.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.organismo.pos')" for="organismo-pos"></label>
            <input
              type="text"
              class="form-control"
              name="pos"
              id="organismo-pos"
              data-cy="pos"
              :class="{ valid: !v$.pos.$invalid, invalid: v$.pos.$invalid }"
              v-model="v$.pos.$model"
            />
          </div>
          <div class="form-group d-none">
            <label class="form-control-label" v-text="t$('semprotdbApp.organismo.imagem')" for="organismo-imagem"></label>
            <input
              type="text"
              class="form-control"
              name="imagem"
              id="organismo-imagem"
              data-cy="imagem"
              :class="{ valid: !v$.imagem.$invalid, invalid: v$.imagem.$invalid }"
              v-model="v$.imagem.$model"
            />
          </div>
          <div class="form-group d-none">
            <label class="form-control-label" v-text="t$('semprotdbApp.organismo.descricao')" for="organismo-descricao"></label>
            <input
              type="text"
              class="form-control"
              name="descricao"
              id="organismo-descricao"
              data-cy="descricao"
              :class="{ valid: !v$.descricao.$invalid, invalid: v$.descricao.$invalid }"
              v-model="v$.descricao.$model"
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
<script lang="ts" src="./organismo-update.component.ts"></script>
