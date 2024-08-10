<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="semprotdbApp.carga.home.createOrEditLabel"
          data-cy="CargaCreateUpdateHeading"
          v-text="t$('semprotdbApp.carga.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="carga.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="carga.id" readonly />
          </div>
          <div class="form-group my-4">
            <b-form-checkbox @change="carga.tipo = modoRemoto ? 'REMOTO' : 'ARQUIVO'" v-model="modoRemoto" name="check-button" switch>
              Baixar dados remoto
            </b-form-checkbox>
          </div>

          <!--          TIPO-->
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.carga.tipo')" for="carga-tipo"></label>
            <select
              :disabled="!!carga.validado"
              class="form-control"
              name="tipo"
              :class="{ valid: !v$.tipo.$invalid, invalid: v$.tipo.$invalid }"
              v-model="v$.tipo.$model"
              id="carga-tipo"
              data-cy="tipo"
              required
            >
              <option v-for="tipo in tipoValues" :key="tipo" v-bind:value="tipo" v-bind:label="t$('semprotdbApp.Tipo.' + tipo)">
                {{ tipo }}
              </option>
            </select>
            <div v-if="v$.tipo.$anyDirty && v$.tipo.$invalid">
              <small class="form-text text-danger" v-for="error of v$.tipo.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>

          <!--          CAMINHO-->
          <div v-if="modoRemoto" class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.carga.caminho')" for="carga-caminho"></label>
            <input
              type="text"
              class="form-control"
              name="caminho"
              id="carga-caminho"
              data-cy="caminho"
              :class="{ valid: !v$.caminho.$invalid, invalid: v$.caminho.$invalid }"
              v-model="v$.caminho.$model"
            />
          </div>

          <!--          PLANILHA-->
          <div v-else class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.carga.planilha')" for="carga-planilha"></label>
            <div>
              <div v-if="carga.planilha" class="form-text text-danger clearfix">
                <a class="pull-left" v-on:click="openFile(carga.planilhaContentType, carga.planilha)" v-text="t$('entity.action.open')"></a
                ><br />
                <span class="pull-left">{{ carga.planilhaContentType }}, {{ byteSize(carga.planilha) }}</span>
                <button
                  type="button"
                  v-on:click="
                    carga.planilha = null;
                    carga.planilhaContentType = null;
                  "
                  class="btn btn-secondary btn-xs pull-right"
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                </button>
              </div>
              <label for="file_planilha" v-text="t$('entity.action.addblob')" class="btn btn-primary pull-right"></label>
              <input
                type="file"
                ref="file_planilha"
                id="file_planilha"
                style="display: none"
                data-cy="planilha"
                v-on:change="
                  setFileData(
                    $event,
                    carga,
                    'planilha',
                    false,
                    'nome',
                    (f, t) => (carga.formato = /.*sheet.*/.test(t || '') ? 'XLSX' : 'TSV'),
                  );
                  carga.tipo = 'ARQUIVO';
                "
              />
            </div>
            <input
              type="hidden"
              class="form-control"
              name="planilha"
              id="carga-planilha"
              data-cy="planilha"
              :class="{ valid: !v$.planilha.$invalid, invalid: v$.planilha.$invalid }"
              v-model="v$.planilha.$model"
            />
            <input
              type="hidden"
              class="form-control"
              name="planilhaContentType"
              id="carga-planilhaContentType"
              v-model="carga.planilhaContentType"
            />
          </div>

          <!--          NOME-->
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.carga.nome')" for="carga-nome"></label>
            <input
              type="text"
              class="form-control"
              name="nome"
              id="carga-nome"
              data-cy="nome"
              :class="{ valid: !v$.nome.$invalid, invalid: v$.nome.$invalid }"
              v-model="v$.nome.$model"
              required
            />
            <div v-if="v$.nome.$anyDirty && v$.nome.$invalid">
              <small class="form-text text-danger" v-for="error of v$.nome.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>

          <!--          STATUS-->
          <div :hidden="isNew" class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.carga.status')" for="carga-status"></label>
            <input
              type="text"
              class="form-control"
              name="status"
              id="carga-status"
              data-cy="status"
              :class="{ valid: !v$.status.$invalid, invalid: v$.status.$invalid }"
              v-model="v$.status.$model"
            />
          </div>

          <!--          ORDEM-->
          <div :hidden="isNew" class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.carga.ordem')" for="carga-ordem"></label>
            <input
              type="number"
              class="form-control"
              name="ordem"
              id="carga-ordem"
              data-cy="ordem"
              :class="{ valid: !v$.ordem.$invalid, invalid: v$.ordem.$invalid }"
              v-model.number="v$.ordem.$model"
            />
          </div>

          <!--          VALIDADO-->
          <div hidden class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.carga.validado')" for="carga-validado"></label>
            <input
              type="checkbox"
              class="form-check"
              name="validado"
              id="carga-validado"
              data-cy="validado"
              :class="{ valid: !v$.validado.$invalid, invalid: v$.validado.$invalid }"
              v-model="v$.validado.$model"
            />
          </div>

          <!--          FORMATO-->
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.carga.formato')" for="carga-formato"></label>
            <select
              class="form-control"
              name="formato"
              :class="{ valid: !v$.formato.$invalid, invalid: v$.formato.$invalid }"
              v-model="v$.formato.$model"
              id="carga-formato"
              data-cy="formato"
              required
            >
              <option
                v-for="formato in formatoValues"
                :key="formato"
                v-bind:value="formato"
                v-bind:label="t$('semprotdbApp.Formato.' + formato)"
              >
                {{ formato }}
              </option>
            </select>
            <div v-if="v$.formato.$anyDirty && v$.formato.$invalid">
              <small class="form-text text-danger" v-for="error of v$.formato.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>

          <!--          DESTINO-->
          <div :hidden="isNew" class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.carga.destino')" for="carga-destino"></label>
            <select
              disabled
              class="form-control"
              name="destino"
              :class="{ valid: !v$.destino.$invalid, invalid: v$.destino.$invalid }"
              v-model="v$.destino.$model"
              id="carga-destino"
              data-cy="destino"
            >
              <option
                v-for="destino in destinoValues"
                :key="destino"
                v-bind:value="destino"
                v-bind:label="t$('semprotdbApp.Destino.' + destino)"
              >
                {{ destino }}
              </option>
            </select>
          </div>

          <!--          LINHAS-->
          <div hidden class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.carga.linhas')" for="carga-linhas"></label>
            <input
              type="number"
              class="form-control"
              name="linhas"
              id="carga-linhas"
              data-cy="linhas"
              :class="{ valid: !v$.linhas.$invalid, invalid: v$.linhas.$invalid }"
              v-model.number="v$.linhas.$model"
            />
          </div>

          <!--          CHECKSUM-->
          <div hidden class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.carga.checksum')" for="carga-checksum"></label>
            <input
              type="text"
              class="form-control"
              name="checksum"
              id="carga-checksum"
              data-cy="checksum"
              :class="{ valid: !v$.checksum.$invalid, invalid: v$.checksum.$invalid }"
              v-model="v$.checksum.$model"
            />
          </div>

          <!--          VERSAO-->
          <div class="form-group">
            <label class="form-control-label" v-text="t$('semprotdbApp.carga.versao')" for="carga-versao"></label>
            <select class="form-control" id="carga-versao" data-cy="versao" name="versao" v-model="carga.versao">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="carga.versao && versaoOption.id === carga.versao.id ? carga.versao : versaoOption"
                v-for="versaoOption in versaos"
                :key="versaoOption.id"
              >
                {{ versaoOption.id }}
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
<script lang="ts" src="./carga-update.component.ts"></script>
