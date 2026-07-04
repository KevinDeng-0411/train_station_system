<template>
  <div class="skeleton-block" :class="{ animated: !static }">
    <div
      v-for="(line, i) in lines"
      :key="i"
      class="skeleton-line"
      :style="{
        width: typeof line === 'object' ? line.width : line,
        height: typeof line === 'object' ? (line.height || '14px') : '14px',
        marginTop: i === 0 ? '0' : (typeof line === 'object' ? (line.gap || '8px') : '8px')
      }"
    ></div>
  </div>
</template>

<script setup>
defineProps({
  lines: { type: Array, default: () => ['100%', '80%', '60%'] },
  static: { type: Boolean, default: false }
})
</script>

<style scoped>
.skeleton-block {
  padding: var(--space-3) 0;
}
.skeleton-line {
  background: linear-gradient(90deg,
    var(--primitive-slate-100) 0%,
    var(--primitive-slate-200) 50%,
    var(--primitive-slate-100) 100%);
  background-size: 200% 100%;
  border-radius: var(--radius-sm);
  display: block;
}
.animated .skeleton-line {
  animation: shimmer 1.5s ease-in-out infinite;
}
@keyframes shimmer {
  0%   { background-position: 100% 0; }
  100% { background-position: -100% 0; }
}

[data-theme="dark"] .skeleton-line {
  background: linear-gradient(90deg,
    rgba(71, 85, 105, 0.3) 0%,
    rgba(71, 85, 105, 0.5) 50%,
    rgba(71, 85, 105, 0.3) 100%);
  background-size: 200% 100%;
}
</style>
