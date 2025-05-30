<template>
  <div class="home-page">
    <!-- Hero Section -->
    <section class="hero-section text-center py-2">
      <div class="container">
        <h1 class="display-4 text-primary">SemProtDB</h1>
        <p class="lead text-muted mb-5">{{ t$('home.hero.tagline') }}</p>

        <div class="organism-circle-container my-4">
          <img class="central-image" v-if="organismos.length > 0" src="/content/images/semprot-logo3.svg" alt="Imagem da Versão" />
          <router-link
            v-for="(item, index) in organismos"
            :key="item.id"
            :to="{ path: '/tabela', query: { organismId: item.id } }"
            class="organism-item shadow-sm"
            :style="getOrganismStyle(index, organismos.length, 220, 100)"
            :title="item.apelido || item.nome"
          >
            <div class="organism-icon-wrapper">
              <img :src="item.silhueta || ''" :alt="item.nome" class="organism-icon" />
              <span class="organism-name">{{ item.apelido || item.nome }}</span>
            </div>
          </router-link>
          <div
            v-if="organismos.length === 0 && versaoImagemSrc"
            class="text-center w-100"
            style="position: absolute; top: 45%; left: 50%; transform: translate(-50%, -50%)"
          >
            <img class="central-image-placeholder" src="/content/images/semprot-logo2.svg" alt="Imagem da Versão" />
            <p class="text-muted mt-3">{{ t$('home.hero.loadingOrganisms') }}</p>
          </div>
        </div>
        <p v-if="versao?.texto" class="col-lg-8 mx-auto fs-6 text-muted mt-5" v-html="versao.texto"></p>
        <p v-else class="col-lg-8 mx-auto fs-6 text-muted mt-5">{{ t$('home.hero.currentVersionInfo') }}</p>
      </div>
    </section>

    <!-- Content Sections -->
    <section class="content-section py-5 scroll-reveal" ref="section1">
      <div class="container">
        <div class="row align-items-center">
          <div class="col-md-6">
            <h2 class="mb-3">{{ t$('home.sections.whatIs.title') }}</h2>
            <p class="text-muted">
              {{ t$('home.sections.whatIs.text') }}
            </p>
          </div>
          <div class="col-md-6 text-center">
            <!-- Placeholder for an illustrative image or icon -->
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="120"
              height="120"
              fill="currentColor"
              class="bi bi-archive text-primary"
              viewBox="0 0 16 16"
            >
              <path
                d="M0 2a1 1 0 0 1 1-1h14a1 1 0 0 1 1 1v2a1 1 0 0 1-1 1v7.5a2.5 2.5 0 0 1-2.5 2.5h-9A2.5 2.5 0 0 1 1 12.5V5a1 1 0 0 1-1-1V2zm2 3v7.5A1.5 1.5 0 0 0 3.5 14h9a1.5 1.5 0 0 0 1.5-1.5V5H2zm13-3H1v2h14V2zM5 7.5a.5.5 0 0 1 .5-.5h5a.5.5 0 0 1 0 1h-5a.5.5 0 0 1-.5-.5z"
              />
            </svg>
          </div>
        </div>
      </div>
    </section>

    <section class="content-section bg-light py-5 scroll-reveal" ref="section2">
      <div class="container">
        <div class="row align-items-center">
          <div class="col-md-6 order-md-2">
            <h2 class="mb-3">{{ t$('home.sections.importance.title') }}</h2>
            <p class="text-muted">
              {{ t$('home.sections.importance.text') }}
            </p>
          </div>
          <div class="col-md-6 order-md-1 text-center">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="120"
              height="120"
              fill="currentColor"
              class="bi bi-lightbulb text-primary"
              viewBox="0 0 16 16"
            >
              <path
                d="M2 6a6 6 0 1 1 10.174 4.31c-.203.196-.359.4-.453.619l-.762 1.769A.5.5 0 0 1 10.5 13a.5.5 0 0 1 0 1 .5.5 0 0 1 0 1l-.224.447a1 1 0 0 1-.894.553H6.618a1 1 0 0 1-.894-.553L5.5 15a.5.5 0 0 1 0-1 .5.5 0 0 1 0-1 .5.5 0 0 1-.46-.302l-.761-1.77a1.964 1.964 0 0 0-.453-.618A6 6 0 0 1 2 6zm6 8.5a.5.5 0 0 0 .5-.5v-.5a.5.5 0 0 0-1 0v.5a.5.5 0 0 0 .5.5zM8 1a4 4 0 1 0 0 8 4 4 0 0 0 0-8z"
              />
            </svg>
          </div>
        </div>
      </div>
    </section>

    <section class="content-section py-5 scroll-reveal" ref="section3">
      <div class="container">
        <div class="row align-items-center">
          <div class="col-md-6">
            <h2 class="mb-3">{{ t$('home.sections.whatYouWillFind.title') }}</h2>
            <p class="text-muted">
              {{ t$('home.sections.whatYouWillFind.text') }}
            </p>
          </div>
          <div class="col-md-6 text-center">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="120"
              height="120"
              fill="currentColor"
              class="bi bi-card-list text-primary"
              viewBox="0 0 16 16"
            >
              <path
                d="M14.5 3a.5.5 0 0 1 .5.5v9a.5.5 0 0 1-.5.5h-13a.5.5 0 0 1-.5-.5v-9a.5.5 0 0 1 .5-.5h13zm-13-1A1.5 1.5 0 0 0 0 3.5v9A1.5 1.5 0 0 0 1.5 14h13a1.5 1.5 0 0 0 1.5-1.5v-9A1.5 1.5 0 0 0 14.5 2h-13z"
              />
              <path
                d="M5 8a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7A.5.5 0 0 1 5 8zm0-2.5a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5zm0 5a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5zm-1-5a.5.5 0 1 1-1 0 .5.5 0 0 1 1 0zM4 8a.5.5 0 1 1-1 0 .5.5 0 0 1 1 0zm0 2.5a.5.5 0 1 1-1 0 .5.5 0 0 1 1 0z"
              />
            </svg>
          </div>
        </div>
      </div>
    </section>

    <section class="content-section bg-light py-5 scroll-reveal" ref="section4">
      <div class="container">
        <div class="row align-items-center">
          <div class="col-md-6 order-md-2">
            <h2 class="mb-3">{{ t$('home.sections.howItHelps.title') }}</h2>
            <p class="text-muted">
              {{ t$('home.sections.howItHelps.text') }}
            </p>
          </div>
          <div class="col-md-6 order-md-1 text-center">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="120"
              height="120"
              fill="currentColor"
              class="bi bi-tools text-primary"
              viewBox="0 0 16 16"
            >
              <path
                d="M1 0 0 1l2.2 3.081a1 1 0 0 0 .815.419h.07a1 1 0 0 1 .708.293l2.675 2.675-2.617 2.654A3.003 3.003 0 0 0 0 13a3 3 0 1 0 5.878-.851l2.654-2.617.968.968-.305.914a1 1 0 0 0 .242 1.023l3.356 3.356a1 1 0 0 0 1.414 0l1.586-1.586a1 1 0 0 0 0-1.414l-3.356-3.356a1 1 0 0 0-1.023-.242L10.5 9.5l-.96-.96 2.68-2.66L15 3l-1-1-3.081 2.2a1 1 0 0 1-.419.815h-.07a1 1 0 0 0-.293.708L10.25 5.69 7.586 3.025A3.003 3.003 0 0 0 6 0H1zm1.586 3L4.293 4.293a1 1 0 0 1 0 1.414L1.414 8.586l-.707-.707L3.586 5H5.5L4 3.5 3 2.5 2.586 3zm4.414.586L8.707 5.293a1 1 0 0 1 0 1.414L5.828 9.586l-.707-.707L8.414 6H10L8.5 4.5 7.5 3.5 7 4.086z"
              />
            </svg>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script lang="ts" src="./home.component.ts"></script>

<style scoped>
.home-page {
  --primary-color: #3e73d9;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.hero-section {
  background-color: #f8f9fa; /* Light gray background */
  min-height: 70vh; /* Ensure it takes up significant space */
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-section .display-3 {
  color: var(--primary-color);
}

.organism-circle-container {
  position: relative;
  width: 475px; /* Adjust as needed, must be large enough for circle + items */
  height: 475px; /* Adjust as needed */
  margin-left: auto;
  margin-right: auto;
  display: flex; /* For centering placeholder content if no organisms */
  align-items: center;
  justify-content: center;
}

.central-image {
  width: 250px; /* Adjust size as needed */
  height: 250px;
  border-radius: 50%;
  object-fit: cover;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.2);
}
.central-image-placeholder {
  width: 250px;
  height: 250px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.2);
}

.organism-item {
  position: absolute;
  /* left and top are set by inline style from getOrganismStyle */
  width: 100px; /* Diameter of the circle */
  height: 100px; /* Diameter of the circle */
  background-color: white;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  text-decoration: none;
  color: #333;
  /* Base transform now includes scale and translate for centering */
  transform: translate(-50%, -50%) scale(1);
  transition:
    transform 0.3s ease,
    box-shadow 0.3s ease;
  padding: 5px;
  box-sizing: border-box; /* Include padding and border in the element's total width and height */
}

.organism-item:hover {
  /* On hover, only scale changes in the transform property */
  transform: translate(-50%, -50%) scale(1.2);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
  z-index: 10;
}

.scroll-reveal {
  opacity: 0;
  transform: translateY(30px);
  transition:
    opacity 0.6s ease-out,
    transform 0.6s ease-out;
}

.scroll-reveal.is-visible {
  opacity: 1;
  transform: translateY(0);
}

.organism-icon-wrapper {
  width: 80px; /* Adjust icon container size */
  height: 80px; /* Adjust icon container size */
  padding: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  margin-bottom: 5px;
}

.organism-icon {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.organism-name {
  font-size: 0.75rem; /* Small text for the name */
  color: #555;
  display: block;
  line-height: 1.2;
}

.content-section {
  padding-top: 4rem;
  padding-bottom: 4rem;
}

.content-section h2 {
  color: var(--primary-color);
  font-weight: 600;
}

.content-section.bg-light {
  background-color: #f8f9fa;
}

.text-primary {
  color: var(--primary-color) !important;
}

/* Responsive adjustments for the circle container if needed */
@media (max-width: 768px) {
  .organism-circle-container {
    width: 350px; /* Smaller circle on smaller screens */
    height: 350px;
  }
  .central-image {
    width: 150px;
    height: 150px;
  }
  .central-image-placeholder {
    width: 150px;
    height: 150px;
  }
  .organism-item {
    width: 80px;
    height: 80px;
  }
  .organism-icon-wrapper {
    width: 35px;
    height: 35px;
  }
  .organism-name {
    font-size: 0.65rem;
  }
  .hero-section {
    min-height: auto;
    padding-top: 3rem;
    padding-bottom: 3rem;
  }
  .content-section .col-md-6 {
    text-align: center; /* Center text content on small screens */
    margin-bottom: 2rem;
  }
  .content-section .col-md-6:last-child {
    margin-bottom: 0;
  }
  .content-section .row {
    flex-direction: column-reverse; /* Stack image/icon below text on mobile */
  }
  .content-section .row .order-md-1 {
    order: 1 !important;
  }
  .content-section .row .order-md-2 {
    order: 2 !important;
  }
}
</style>
