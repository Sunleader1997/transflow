<script setup>
import { BaseEdge, EdgeLabelRenderer, getBezierPath } from '@vue-flow/core'
import { computed, onMounted, ref } from 'vue'

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
const dots = ref([])
const edgePathRef = ref()

function animateDot() {
  const dot = ref({
    x: -100,
    y: -100,
    progress: 0,
    count: 0,
    startTime: performance.now(),
  })
  dots.value.push(dot)
}
// 相当于每个线段都要维护一个循环动画，线段多不得爆炸
function startDotsLoop() {
  console.log('开始执行循环')
  const duration = 2000 // 动画持续时间
  const edgeElement = edgePathRef.value
  if (!edgeElement) return
  const path = edgeElement // 获取第一个边的路径
  const pathLength = path.getTotalLength()
  const animation = (currentTime) => {
    dots.value.forEach((dot) => {
      const elapsed = currentTime - dot.value.startTime
      dot.value.progress = Math.min(elapsed / duration, 1)
      const point = path.getPointAtLength(dot.value.progress * pathLength)
      dot.value.x = point.x
      dot.value.y = point.y
    })
    dots.value = dots.value.filter((d) => d.value.progress < 1)
    requestAnimationFrame(animation)
  }
  requestAnimationFrame(animation)
}

const path = computed(() => getBezierPath(props))
defineExpose({ animateDot })

onMounted(() => {
  startDotsLoop()
})
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
  <circle v-for="dot in dots" :key="dot" r="8" fill="#4a90e2" :cx="dot.value.x" :cy="dot.value.y" />
  <!-- Use the `EdgeLabelRenderer` to escape the SVG world of edges and render your own custom label in a `<div>` ctx -->
  <EdgeLabelRenderer>
    <div
      :style="{
        pointerEvents: 'all',
        position: 'absolute',
        transform: `translate(-50%, -50%) translate(${path[1]}px,${path[2]}px)`,
      }"
      class="nodrag nopan"
    ></div>
  </EdgeLabelRenderer>
</template>
