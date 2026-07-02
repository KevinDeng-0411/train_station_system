<template>
  <v-chart
    :option="option"
    :loading="loading"
    :autoresize="autoresize"
    class="base-chart"
  />
</template>

<script setup>
import { computed } from 'vue'
import VChart from 'vue-echarts'

defineProps({
  option: {
    type: Object,
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  },
  autoresize: {
    type: Boolean,
    default: true
  }
})
</script>

<style scoped>
.base-chart {
  width: 100%;
  height: 100%;
  min-height: 240px;
}
</style>
