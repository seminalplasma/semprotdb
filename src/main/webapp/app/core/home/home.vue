<template>
  <div class="p-5 text-center main px-4 gota">
    <div class="d-none d-md-block w-100 container text-center">
      <template v-for="i in poss">
        <div
          :class="`row justify-content-${organismos.some(z => z.pos?.startsWith(i) && z.pos?.includes('L')) ? 'end' : organismos.some(z => z.pos?.startsWith(i) && z.pos?.includes('C')) ? 'center' : 'between'} my-${organismos
            .filter(z => z.pos?.startsWith(i))
            .map(z => (z.pos + '').split('Y').length - 1)
            .concat([0])
            .reduce((a, b) => a + b)}`"
          :style="' width: ' + i.replace('-', '') + '% ; margin-left: ' + (100 - parseInt(i.replace('-', ''))) / 2 + '%;'"
        >
          <template v-for="j in [1, 2]">
            <template v-for="o in organismos">
              <router-link :to="{ path: '/tabela', query: { organismId: o.id } }" v-if="o?.pos?.startsWith(`${i}.${j}`)">
                <img
                  v-if="o.silhueta"
                  :class="{ imgorg: 1, 'shadow img-fluid rounded-circle': o?.pos?.includes('R') }"
                  v-bind:src="'data:' + o.silhuetaContentType + ';base64,' + o.silhueta"
                  :alt="o.nome"
                  width="80px"
                />
                <br />
                <i>{{ o.pos.includes('X') ? o.pos : '' }} {{ o.apelido || o.nome }}</i>
              </router-link>
            </template>
          </template>
        </div>
      </template>
    </div>

    <div class="separador my-sm-5 my-md-0"></div>

    <div class="px-5 mx-5 bg-light shadow-lg py-5 rounded-sm">
      <h1 class="display-3 text-dark d-none d-md-block">SemProtDB</h1>
      <p class="col-lg-8 mx-auto fs-5 text-muted" v-html="versao?.texto" v-if="versao?.texto"></p>
      <p v-else class="col-lg-8 mx-auto fs-5 text-muted lead">Versão ?</p>
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

  <div class="row justify-content-center mt-4">
    <img
      width="300px"
      v-if="versao && versao.imagem"
      v-bind:src="'data:' + versao.imagemContentType + ';base64,' + versao.imagem"
      alt="imagem-da-versao"
    />
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
        O estudo do proteoma do plasma seminal de mamíferos vem da necessidade em compreender a função de proteínas envolvidas no complexo
        processo de fertilização, desde a proteção dos espermatozoides até a fecundação, e no desenvolvimento embrionário inicial. A
        identificação de proteínas seminais e espermáticas conservadas entre espécies pode contribuir para informações evolutivas, bem como
        apontar proteínas que sejam potenciais biomarcadores de fertilidade do macho e congelabilidade do sêmen. SemProtDB é um banco de
        dados que tem como objetivo reunir e organizar os dados disponíveis de proteoma de plasma seminal de espécies mamíferas presentes na
        literatura, auxiliando a comunidade científica na análise das informações existentes. Sabe-se que as proteínas ou famílias de
        proteínas seminais exercem papeis importantes na fisiologia espermática, com impacto direto na habilidade reprodutiva da espécie.
        Além disso, cada espécie possui um perfil proteico bem definido para o plasma seminal. Portanto, o SemProtDB visar ser uma
        ferramenta facilitadora na tomada de decisões para futuros desenhos experimentais, evitando desperdício de tempo e recursos de
        pesquisa. Aqui o usuário encontrará a reunião de todas as proteínas descritas em plasma seminal das principais espécies mamíferas,
        bem como informações sobre seu peso molecular, tamanho, gene e suas referências. O SemProtDB oferece a opção de link clicável para
        direcionamento do usuário que direcionará para mais informações sobre a proteína escolhida em bancos de dados de proteínas.
      </p>
    </div>
  </div>
</template>

<script lang="ts" src="./home.component.ts"></script>

<style scoped>
.gota {
  background-image: url('/content/images/semprot-logo2.svg');
  background-size: 35%;
  background-position: top;
  background-repeat: no-repeat;
}

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
