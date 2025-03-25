<script setup>
import { BaseEdge, EdgeLabelRenderer, getBezierPath } from '@vue-flow/core'
import { computed } from 'vue'

const props = defineProps({
  id: String,
  sourceX: {
    type: Number,
    required: true,
  },
  sourceY: {
    type: Number,
    required: true,
  },
  targetX: {
    type: Number,
    required: true,
  },
  targetY: {
    type: Number,
    required: true,
  },
  sourcePosition: {
    type: String,
    required: true,
  },
  targetPosition: {
    type: String,
    required: true,
  },
  data: {
    type: Object,
    required: true,
  },
})
const path = computed(() => getBezierPath(props))
</script>

<script>
export default {
  inheritAttrs: false,
}
</script>

<template>
  <!-- You can use the `BaseEdge` component to create your own custom edge more easily -->
  <BaseEdge :path="path[0]" />
<!--  <path :d="path[0]" fill="none" stroke="transparent" ref="edgePathRef" />-->
<!--  <svg v-if="isAnimating" class="dot-container">-->
<!--    <circle :cx="dataPoint.x" :cy="dataPoint.y" r="8" fill="#4a90e2" />-->
<!--  </svg>-->
  <!-- Use the `EdgeLabelRenderer` to escape the SVG world of edges and render your own custom label in a `<div>` ctx -->
  <EdgeLabelRenderer>
    <div
      :style="{
        pointerEvents: 'all',
        position: 'absolute',
        transform: `translate(-50%, -50%) translate(${path[1]}px,${path[2]}px)`,
      }"
      class="nodrag nopan"
    >
    </div>
  </EdgeLabelRenderer>
</template>
