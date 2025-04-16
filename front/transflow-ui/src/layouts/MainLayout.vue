<template>
  <q-layout view="lHh Lpr lFf">
    <q-header
      elevated
      bordered
      style="border-color: #aaaaaa; background-color: #2b2d30; border-bottom-color: #ffffff47"
    >
      <q-bar style="border-color: #aaaaaa; background-color: #2b2d30; color: #cbcbcb">
        <q-icon name="laptop_chromebook" />
        <div>transflow</div>
        <q-space />
        <div>v0.1.0</div>
      </q-bar>
    </q-header>

    <q-page-container style="padding-top: 32px">
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup></script>
