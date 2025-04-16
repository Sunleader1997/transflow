<script setup>
import { Background } from '@vue-flow/background'
</script>

<template>
  <div class="dropzone-background">
    <Background pattern-color="#C6AB6B" :size="1" :gap="40" />
    <div class="overlay">
      <slot />
    </div>
  </div>
</template>
