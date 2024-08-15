<template>
  <div>
    <b-dropdown-item to="/versao" v-if="hasAnyAuthority('ROLE_ADMIN')">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.versao')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/carga" v-if="hasAnyAuthority('ROLE_ADMIN')">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.carga')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/referencia">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.referencia')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/organismo">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.organismo')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/gene">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.gene')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/proteina">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.proteina')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/recurso">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.recurso')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/curadoria">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.curadoria')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/db-config" v-if="hasAnyAuthority('ROLE_ADMIN')">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.dbConfig')"></span>
    </b-dropdown-item>
    <!-- jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here -->
  </div>
</template>

<script lang="ts" src="./entities-menu.component.ts"></script>
