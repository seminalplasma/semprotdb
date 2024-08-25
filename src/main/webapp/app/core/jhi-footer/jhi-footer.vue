<template>
  <div class="container">
    <footer class="py-3 my-4">
      <div class="row justify-content-around my-4">
        <a href="https://ufmg.br" target="_blank"><img src="/content/images/bioin.png" height="50" /></a>
        <a href="https://ufmg.br" target="_blank"><img src="/content/images/ufmg.png" height="50" /></a>
        <a href="https://ufmg.br" target="_blank"><img src="/content/images/bioin.png" height="50" /></a>
        <a href="https://ufmg.br" target="_blank"><img src="/content/images/ufmg.png" height="50" /></a>
      </div>

      <ul class="nav justify-content-center border-bottom pb-3 mb-3">
        <!--        <li class="nav-item"><a href="#" class="nav-link px-2 text-body-secondary">Home</a></li>-->
        <!--        <li class="nav-item"><a href="#" class="nav-link px-2 text-body-secondary">Features</a></li>-->
        <!--        <li class="nav-item"><a href="#" class="nav-link px-2 text-body-secondary">Pricing</a></li>-->
        <!--        <li class="nav-item"><a href="#" class="nav-link px-2 text-body-secondary">FAQs</a></li>-->
        <!--        <li class="nav-item"><a href="#" class="nav-link px-2 text-body-secondary">About</a></li>-->
      </ul>
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
