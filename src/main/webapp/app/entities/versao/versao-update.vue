<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="semprotdbApp.versao.home.createOrEditLabel"
          data-cy="VersaoCreateUpdateHeading"
          v-text="t$('semprotdbApp.versao.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="versao.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="versao.id" readonly />
          </div>

          <b-alert variant="warning" show
            >Nao recomendo <b>alterar o nome ou numero</b> apos a versao estar disponivel para o usuario.
          </b-alert>

          <!--          NOME-->
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.versao.nome')" for="versao-nome"></label>
            <input
              type="text"
              class="form-control"
              name="nome"
              placeholder="EdgeZoo  AgTech  GreenFields  Ultimate"
              id="versao-nome"
              data-cy="nome"
              :class="{ valid: !v$.nome.$invalid, invalid: v$.nome.$invalid }"
              v-model="v$.nome.$model"
              required
            />
            <div v-if="v$.nome.$anyDirty && v$.nome.$invalid">
              <small class="form-text text-danger" v-for="error of v$.nome.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>

          <!--          NUMERO-->
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.versao.numero')" for="versao-numero"></label>
            <input
              type="number"
              class="form-control"
              name="numero"
              id="versao-numero"
              data-cy="numero"
              :class="{ valid: !v$.numero.$invalid, invalid: v$.numero.$invalid }"
              v-model.number="v$.numero.$model"
              required
            />
            <div v-if="v$.numero.$anyDirty && v$.numero.$invalid">
              <small class="form-text text-danger" v-for="error of v$.numero.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>

          <!--          RELEASE-->
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.versao.release')" for="versao-release"></label>
            <div class="d-flex">
              <input
                id="versao-release"
                data-cy="release"
                type="datetime-local"
                class="form-control"
                name="release"
                :class="{ valid: !v$.release.$invalid, invalid: v$.release.$invalid }"
                :value="convertDateTimeFromServer(v$.release.$model)"
                @change="updateInstantField('release', $event)"
              />
            </div>
          </div>

          <!--          STATUS-->
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.versao.status')" for="versao-status"></label>
            <select
              class="form-control"
              name="status"
              :class="{ valid: !v$.status.$invalid, invalid: v$.status.$invalid }"
              v-model="v$.status.$model"
              id="versao-status"
              data-cy="status"
              required
            >
              <option v-for="status in statusValues" :key="status" v-bind:value="status" v-bind:label="t$('semprotdbApp.Status.' + status)">
                {{ status }}
              </option>
            </select>
            <div v-if="v$.status.$anyDirty && v$.status.$invalid">
              <small class="form-text text-danger" v-for="error of v$.status.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>

          <!--          DESCRIÇÃO-->
          <div class="form-group">
            <!--            <label class="form-control-label" v-text="t$('semprotdbApp.versao.texto')" for="versao-texto"></label>-->
            <label class="form-control-label" for="versao-texto">Descrição</label>
            <textarea
              class="form-control"
              name="texto"
              id="versao-texto"
              data-cy="texto"
              :class="{ valid: !v$.texto.$invalid, invalid: v$.texto.$invalid }"
              v-model="v$.texto.$model"
            ></textarea>
          </div>

          <!--          IMAGEM-->
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.versao.imagem')" for="versao-imagem"></label>
            <div>
              <img
                v-bind:src="'data:' + versao.imagemContentType + ';base64,' + versao.imagem"
                style="max-height: 100px"
                v-if="versao.imagem"
                alt="versao"
              />
              <div v-if="versao.imagem" class="form-text text-danger clearfix">
                <span class="pull-left">{{ versao.imagemContentType }}, {{ byteSize(versao.imagem) }}</span>
                <button
                  type="button"
                  v-on:click="clearInputImage2('imagem', 'imagemContentType', 'file_imagem')"
                  class="btn btn-secondary btn-xs pull-right"
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                </button>
              </div>
              <label for="file_imagem" v-text="t$('entity.action.addimage')" class="btn btn-primary pull-right"></label>
              <input
                type="file"
                ref="file_imagem"
                id="file_imagem"
                style="display: none"
                data-cy="imagem"
                v-on:change="setFileData($event, versao, 'imagem', true)"
                accept="image/*"
              />
            </div>
            <input
              type="hidden"
              class="form-control"
              name="imagem"
              id="versao-imagem"
              data-cy="imagem"
              :class="{ valid: !v$.imagem.$invalid, invalid: v$.imagem.$invalid }"
              v-model="v$.imagem.$model"
            />
            <input
              type="hidden"
              class="form-control"
              name="imagemContentType"
              id="versao-imagemContentType"
              v-model="versao.imagemContentType"
            />
          </div>
          <!--          LOG-->
          <div class="form-group" v-if="isNew">
            <b-form-checkbox
              id="checkbox-1"
              name="checkbox-1"
              v-model="reaproveitar"
              @change="versao.log = reaproveitar ? 'REAPROVEITAR' : ''"
            >
              Reaproveitar todas cargas de dados anteriores
            </b-form-checkbox>
          </div>
          <div class="form-group" v-else>
            <label class="form-control-label" v-text="t$('semprotdbApp.versao.log')" for="versao-log"></label>
            <textarea
              class="form-control"
              name="log"
              id="versao-log"
              data-cy="log"
              :class="{ valid: !v$.log.$invalid, invalid: v$.log.$invalid }"
              v-model="v$.log.$model"
            ></textarea>
          </div>

          <div class="form-group" hidden>
            <label class="form-control-label" v-text="t$('semprotdbApp.versao.detalhes')" for="versao-detalhes"></label>
            <input
              type="text"
              class="form-control"
              name="detalhes"
              id="versao-detalhes"
              data-cy="detalhes"
              :class="{ valid: !v$.detalhes.$invalid, invalid: v$.detalhes.$invalid }"
              v-model="v$.detalhes.$model"
            />
          </div>
          <div class="form-group" hidden>
            <label class="form-control-label" v-text="t$('semprotdbApp.versao.label')" for="versao-label"></label>
            <input
              type="text"
              class="form-control"
              name="label"
              id="versao-label"
              data-cy="label"
              :class="{ valid: !v$.label.$invalid, invalid: v$.label.$invalid }"
              v-model="v$.label.$model"
            />
          </div>
          <div class="form-group" hidden>
            <label class="form-control-label" v-text="t$('semprotdbApp.versao.logo')" for="versao-logo"></label>
            <input
              type="text"
              class="form-control"
              name="logo"
              id="versao-logo"
              data-cy="logo"
              :class="{ valid: !v$.logo.$invalid, invalid: v$.logo.$invalid }"
              v-model="v$.logo.$model"
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
<script lang="ts" src="./versao-update.component.ts"></script>
