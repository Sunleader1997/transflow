<script setup>
import useDragAndDrop from './useDnD.js'
defineProps(["plugins","inputs","filters","gateways","outputs"])
const { onDragStart } = useDragAndDrop()
</script>

<template>
  <q-list class="nodes q-px-md">
    <q-item-label header>inputs</q-item-label>
    <q-item
      dark
      v-for="plugin in inputs"
      :key="plugin.id"
      :class="`q-my-md node-` + plugin.type"
      :draggable="plugin.state === 'STARTED'"
      @dragstart="onDragStart($event, plugin)"
    >
      <q-item-section>
        <q-item-label>{{ plugin.id }}</q-item-label>
        <q-item-label caption>
          {{ plugin.version }}
        </q-item-label>
      </q-item-section>
    </q-item>
    <q-item-label header>filters</q-item-label>
    <q-item
      dark
      v-for="plugin in filters"
      :key="plugin.id"
      :class="`q-my-md node-` + plugin.type"
      :draggable="plugin.state === 'STARTED'"
      @dragstart="onDragStart($event, plugin)"
    >
      <q-item-section>
        <q-item-label>{{ plugin.id }}</q-item-label>
        <q-item-label caption>
          {{ plugin.version }}
        </q-item-label>
      </q-item-section>
    </q-item>
    <q-item-label header>gateways</q-item-label>
    <q-item
      dark
      v-for="plugin in gateways"
      :key="plugin.id"
      :class="`q-my-md node-` + plugin.type"
      :draggable="plugin.state === 'STARTED'"
      @dragstart="onDragStart($event, plugin)"
    >
      <q-item-section>
        <q-item-label>{{ plugin.id }}</q-item-label>
        <q-item-label caption>
          {{ plugin.version }}
        </q-item-label>
      </q-item-section>
    </q-item>
    <q-item-label header>outputs</q-item-label>
    <q-item
      dark
      v-for="plugin in outputs"
      :key="plugin.id"
      :class="`q-my-md node-` + plugin.type"
      :draggable="plugin.state === 'STARTED'"
      @dragstart="onDragStart($event, plugin)"
    >
      <q-item-section>
        <q-item-label>{{ plugin.id }}</q-item-label>
        <q-item-label caption>
          {{ plugin.version }}
        </q-item-label>
      </q-item-section>
    </q-item>
  </q-list>
</template>

<style scoped></style>
