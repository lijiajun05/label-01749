<template>
  <svg class="svg-icon" :style="{ width: size + 'px', height: size + 'px' }">
    <use :xlink:href="'#icon-' + name" />
  </svg>
</template>

<script setup>
defineProps({
  name: {
    type: String,
    required: true
  },
  size: {
    type: Number,
    default: 18
  }
})
</script>

<style scoped>
.svg-icon {
  fill: currentColor;
  vertical-align: middle;
}
</style>
