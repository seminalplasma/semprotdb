<template>
  <div class="p-5 text-center main px-4">
    <span class="mt-5" style="position: absolute">
      <img width="400px" style="margin-left: -200px; margin-top: -4rem" src="/content/images/semprot-logo2.svg" />
    </span>

    <div class="d-none d-md-block w-100 container text-center pt-5">
      <template v-for="i in [80, 60, 40]">
        <div class="row justify-content-between" :style="'min-height: 120px; width: ' + i + '% ; margin-left: ' + (100 - i) / 2 + '%;'">
          <template v-for="j in [1, 2]">
            <template v-for="o in organismos">
              <router-link :to="{ path: '/tabela', query: { organismId: o.id } }" v-if="o?.pos?.includes(`${i}.${j}`)">
                <img
                  v-if="o.silhueta"
                  t
                  :class="{ imgorg: 1, 'shadow img-fluid rounded-circle': o?.pos?.includes('R') }"
                  v-bind:src="'data:' + o.silhuetaContentType + ';base64,' + o.silhueta"
                  :alt="o.nome"
                  width="80px"
                />
                <br />
                <i>{{ o.apelido || o.nome }}</i>
              </router-link>
            </template>
          </template>
        </div>
      </template>
    </div>

    <div class="separador my-sm-5 my-md-0"></div>

    <div class="px-5 mx-5 bg-light shadow-lg py-5 rounded-sm">
      <h1 class="display-3 text-dark d-none d-md-block">Semprotdb</h1>
      <p class="col-lg-8 mx-auto fs-5 text-muted" v-html="versao?.texto" v-if="versao?.texto"></p>
      <p v-else class="col-lg-8 mx-auto fs-5 text-muted lead">
        This is a custom jumbotron featuring an SVG image at the top, some longer text that wraps early thanks to a responsive
        <code>.col-*</code> class, and a customized call to action.
      </p>
      <!--    <div class="d-inline-flex gap-2 mb-5 w-100">-->
      <!--      <button class="d-inline-flex align-items-center btn btn-primary btn-lg px-4 rounded-pill" type="button">-->
      <!--        Call to action-->
      <!--      </button>-->
      <!--      <button class="btn btn-outline-secondary btn-lg px-4 rounded-pill" type="button">-->
      <!--        Secondary link-->
      <!--      </button>-->
      <!--    </div>-->
    </div>
  </div>

  <div class="row content-center mt-4">
    <img width="300px" v-if="versao && versao.imagem" v-bind:src="'data:' + versao.imagemContentType + ';base64,' + versao.imagem" />
  </div>

  <div class="home row">
    <div class="col py-4 px-5">
      <!--      <h1 class="display-4" v-text="t$('home.title')"></h1>-->

      <!--      <h1 class="display-4 text-center mb-4 titulo">Semprotdb</h1>-->

      <!--      <p class="lead my-8">colocar a descricao geral do semprot, quantas ref, rog, datas, link, recursos, ...</p>-->

      <!--      box com possibilidade do usuário  pesquisar por nome, ID ou espécie.-->

      <!--      Na nossa imagem, gostaríamos do título “The seminal-->
      <!--      plasma” e a silhueta das espécies em volta. O círculo seria o número total de proteínas e as-->
      <!--      cores correspondentes a cada espécie. Seria interessante que o usuário conseguisse clica na-->
      <!--      espécie de interesse (seria uma outra maneira de pedir as proteínas da espécie desejada).-->

      <!--      <hr style="margin-top: 8rem" />-->

      <p>
        Imagine um catálogo detalhado de todas as proteínas presentes no fluido seminal de diferentes espécies. Essa é a essência de um
        banco de dados de proteínas seminais. Esses repositórios digitais armazenam e organizam uma vasta quantidade de informações sobre as
        proteínas encontradas no sêmen, desde a sua sequência de aminoácidos até sua função biológica. Por que eles são importantes?
        Reprodução animal: Esses bancos de dados são cruciais para a pesquisa em reprodução animal, permitindo a identificação de
        biomarcadores de fertilidade, a compreensão dos mecanismos de fertilização e o desenvolvimento de novas tecnologias de reprodução
        assistida. Doenças sexualmente transmissíveis: Ao comparar os perfis proteicos de indivíduos saudáveis e infectados, é possível
        identificar proteínas associadas a doenças sexualmente transmissíveis, auxiliando no diagnóstico e desenvolvimento de novas
        terapias. Infertilidade masculina: O estudo das proteínas seminais pode fornecer insights sobre as causas da infertilidade
        masculina, permitindo o desenvolvimento de novos tratamentos. Biomarcadores: As proteínas seminais podem servir como biomarcadores
        para avaliar a qualidade do sêmen, a saúde reprodutiva e o potencial de fertilização. Quais informações são armazenadas? Os bancos
        de dados de proteínas seminais armazenam uma variedade de informações, incluindo: Sequência de aminoácidos: A ordem exata dos
        aminoácidos que compõem cada proteína. Estrutura tridimensional: A forma tridimensional da proteína, que determina sua função.
        Função biológica: O papel da proteína no processo de fertilização, como a capacitação espermática, a reação acrossômica e a fusão
        com o óvulo. Abundância: A quantidade de cada proteína presente no sêmen. Modificações pós-traducionais: Alterações químicas que
        ocorrem nas proteínas após a sua síntese e que podem afetar sua função. Interações moleculares: Quais outras moléculas a proteína
        interage no fluido seminal. Exemplos de aplicações: Desenvolvimento de testes de diagnóstico: A identificação de proteínas
        específicas pode levar ao desenvolvimento de testes rápidos e precisos para avaliar a fertilidade masculina. Desenvolvimento de
        contraceptivos masculinos não hormonais: O estudo das proteínas envolvidas na fertilização pode levar ao desenvolvimento de novas
        formas de contracepção masculina. Melhoria da criopreservação do sêmen: A compreensão das proteínas envolvidas na proteção do
        espermatozoide durante o congelamento pode levar ao desenvolvimento de melhores métodos de criopreservação. Em resumo: Os bancos de
        dados de proteínas seminais representam uma ferramenta poderosa para a pesquisa em reprodução e saúde masculina. Ao fornecer um
        repositório abrangente de informações sobre as proteínas presentes no sêmen, esses bancos de dados permitem aos pesquisadores
        desvendar os mecanismos moleculares que governam a fertilidade e desenvolver novas estratégias para tratar a infertilidade e outras
        doenças relacionadas. Gostaria de saber mais sobre algum aspecto específico dos bancos de dados de proteínas seminais? Por exemplo,
        posso explicar como as proteínas são identificadas no sêmen, como os dados são armazenados e organizados em um banco de dados ou
        como esses dados podem ser utilizados para desenvolver novas tecnologias reprodutivas.
      </p>
    </div>
  </div>
</template>

<script lang="ts" src="./home.component.ts"></script>

<style scoped>
.main {
  background-color: rgba(255, 255, 255, 0.81);
  border-radius: 1rem;
}

.separador {
  width: 100%;
  display: block;
  height: 4rem;
}

.imgorg:hover {
  scale: 1.1;
}
</style>
