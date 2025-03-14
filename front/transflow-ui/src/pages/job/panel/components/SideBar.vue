<script setup>
import useDragAndDrop from './useDnD'
const { onDragStart } = useDragAndDrop()
</script>

<template>
  <div class="nodes q-pa-md">
    <div class="vue-flow__node-input" :draggable="true" @dragstart="onDragStart($event, 'input')">Input Node</div>

    <div class="vue-flow__node-default" :draggable="true" @dragstart="onDragStart($event, 'default')">Default Node</div>

    <div class="vue-flow__node-output" :draggable="true" @dragstart="onDragStart($event, 'output')">Output Node</div>
  </div>
</template>

<style scoped>

</style>
