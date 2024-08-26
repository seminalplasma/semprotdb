<template>
  <div class="container mt-5">
    <footer class="py-3 my-4">
      <div class="container text-center">
        <div class="row">
          <div class="col">
            <a href="http://www.fapemig.br" target="_blank"><img src="/content/images/logo_FAPEMIG.jpg" height="70" /></a>
          </div>
          <div class="col mx-5">
            <a href="https://www.ufv.br" target="_blank"><img src="/content/images/Logo_UFV.png" height="70" /></a>
          </div>
          <div class="col">
            <a href="https://www.instagram.com/laratufv/" target="_blank"><img src="/content/images/LARAT.jfif" height="70" /></a>
          </div>
          <div class="col">
            <a href="#"><img src="/content/images/Logo_LBM.png" height="70" /></a>
          </div>
          <div class="col">
            <a href="https://www.lbsmsb.ufv.br" target="_blank"
              ><img src="/content/images/0ce78c9e-e24d-4cb8-9c45-5f5f8a201c96.jfif" height="70"
            /></a>
          </div>
        </div>

        <div class="row mt-2">
          <div class="col d-flex align-self-center align-items-center" style="justify-content: space-evenly">
            <a href="https://www.gov.br/cnpq" target="_blank"><img src="/content/images/Logo_CNPq.jpg" height="25" /></a>
            <a href="https://www.gov.br/capes" target="_blank"><img src="/content/images/Logo_CAPES.png" height="50" /></a>
          </div>
          <div class="col align-self-center mx-5">
            <a href="https://www.ufc.br" target="_blank"><img src="/content/images/Logo_UFC.png" height="60" /></a>
          </div>
          <div class="col align-self-center">
            <a href="#"><img src="/content/images/Logo_Arlindo_UFC.jfif" height="60" /></a>
          </div>
          <div class="col align-self-center"></div>
          <div class="col align-self-center"></div>
        </div>
      </div>

      <ul class="nav justify-content-center border-bottom pb-3 mb-3"></ul>
      <p class="text-center text-body-secondary">
        &copy; 2024 Semprotdb
        <b-button :disabled="!disp" pill size="sm" variant="light" class="mx-4" v-b-modal.modal-1>
          <font-awesome-icon icon="comment-dots"></font-awesome-icon>
        </b-button>
      </p>
    </footer>

    <b-modal id="modal-1" title="Feedbacks" @ok="feedback">
      <div class="row m-2">
        <label for="exampleFormControlTextarea1" class="form-label">Digite aqui sua sugestão</label>
        <textarea
          v-model="dbc.vtext"
          class="form-control"
          id="exampleFormControlTextarea1"
          rows="3"
          placeholder="limite 1000 caracteres"
        ></textarea>
      </div>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./jhi-footer.component.ts"></script>
