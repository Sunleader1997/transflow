<script setup>
import { computed } from 'vue'
import { Handle, Position } from '@vue-flow/core'

const props = defineProps({
  position: {
    type: Object,
    required: true,
  },
  data: {
    type: Object,
  },
})

const x = computed(() => `${Math.round(props.position.x)}px`)
const y = computed(() => `${Math.round(props.position.y)}px`)
</script>

<template>
  <div class="vue-flow__node-default">
    <div>{{ data.label }}</div>

    <div>{{ x }} {{ y }}</div>

    <Handle type="source" :position="Position.Bottom" />
  </div>
</template>
