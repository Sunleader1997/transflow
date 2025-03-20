<script setup>
import { BaseEdge, EdgeLabelRenderer, getBezierPath } from '@vue-flow/core'
import { computed, ref } from 'vue'

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
const isAnimating = ref(false)
const edgePathRef = ref()
const dataPoint = ref({ x: 0, y: 0 })

function animateDot() {
  if (isAnimating.value) {
    return
  }
  isAnimating.value = true
  const edgeElement = edgePathRef.value
  if (!edgeElement) return

  const path = edgeElement // 获取第一个边的路径
  const pathLength = path.getTotalLength()
  let progress = 0
  const duration = 2000 // 动画持续时间
  const startTime = performance.now()

  const animate = (currentTime) => {
    if (!isAnimating.value) return

    const elapsed = currentTime - startTime
    progress = Math.min(elapsed / duration, 1)
    const point = path.getPointAtLength(progress * pathLength)
    dataPoint.value = { x: point.x, y: point.y }
    if (progress < 1) {
      requestAnimationFrame(animate)
    } else {
      isAnimating.value = false
    }
  }
  requestAnimationFrame(animate)
}
const path = computed(() => getBezierPath(props))
defineExpose({animateDot})
</script>

<script>
export default {
  inheritAttrs: false,
}
</script>

<template>
  <!-- You can use the `BaseEdge` component to create your own custom edge more easily -->
  <BaseEdge :path="path[0]" />
  <path :d="path[0]" fill="none" stroke="transparent" ref="edgePathRef" />
  <svg v-if="isAnimating" class="dot-container">
    <circle :cx="dataPoint.x" :cy="dataPoint.y" r="8" fill="#4a90e2" />
  </svg>
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
      <q-btn @click="animateDot()"></q-btn>
    </div>
  </EdgeLabelRenderer>
</template>
