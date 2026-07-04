<template>
  <div class="empty-state">
    <!-- 内嵌 SVG 插画 -->
    <div class="empty-illustration" :class="illustrationType">
      <component :is="iconComponent" />
    </div>
    <div class="empty-title">{{ title }}</div>
    <div class="empty-desc">{{ description }}</div>
    <slot name="action">
      <button v-if="actionText" class="empty-action" @click="$emit('action')">
        {{ actionText }}
      </button>
    </slot>
  </div>
</template>

<script setup>
import { computed, markRaw } from 'vue'
import { Box, Document, Search, Warning, Van, Tickets } from '@element-plus/icons-vue'

const props = defineProps({
  type: { type: String, default: 'empty' },  // empty | search | error | tickets | train
  title: { type: String, default: '暂无数据' },
  description: { type: String, default: '这里空空如也，去添加一条新数据吧' },
  actionText: { type: String, default: '' }
})

defineEmits(['action'])

const iconMap = {
  empty: markRaw(Box),
  search: markRaw(Search),
  error: markRaw(Warning),
  tickets: markRaw(Tickets),
  train: markRaw(Van),
  document: markRaw(Document)
}

const iconComponent = computed(() => iconMap[props.type] || iconMap.empty)
const illustrationType = computed(() => `type-${props.type}`)
</script>

<style scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--space-12) var(--space-6);
  text-align: center;
  min-height: 240px;
}
.empty-illustration {
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: var(--space-4);
  border-radius: 50%;
  background: linear-gradient(135deg,
    var(--primitive-cyan-50) 0%,
    var(--primitive-cyan-100) 100%);
  position: relative;
  animation: floatY 3s ease-in-out infinite;
}
.empty-illustration::before {
  content: '';
  position: absolute;
  inset: -8px;
  border-radius: 50%;
  border: 2px dashed var(--primitive-cyan-200);
  animation: spin 20s linear infinite;
  opacity: 0.5;
}
.empty-illustration :deep(svg) {
  width: 60px;
  height: 60px;
  color: var(--color-primary);
  z-index: 1;
}
.empty-illustration.type-search { background: linear-gradient(135deg, #FEF3C7, #FDE68A); }
.empty-illustration.type-search::before { border-color: #FCD34D; }
.empty-illustration.type-search :deep(svg) { color: #D97706; }
.empty-illustration.type-error { background: linear-gradient(135deg, #FEE2E2, #FECACA); }
.empty-illustration.type-error::before { border-color: #FCA5A5; }
.empty-illustration.type-error :deep(svg) { color: #DC2626; }

.empty-title {
  font-size: var(--text-lg);
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: var(--space-2);
}
.empty-desc {
  font-size: var(--text-md);
  color: var(--color-text-muted);
  max-width: 320px;
  line-height: 1.5;
  margin-bottom: var(--space-4);
}
.empty-action {
  padding: var(--space-2) var(--space-5);
  background: var(--btn-primary-gradient);
  color: white;
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--text-md);
  font-weight: 500;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(8, 145, 178, 0.3);
  transition: all var(--duration-normal) var(--ease-default);
}
.empty-action:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(8, 145, 178, 0.4);
}

@keyframes floatY {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-6px); }
}
@keyframes spin {
  from { transform: rotate(0deg); }
  to   { transform: rotate(360deg); }
}

[data-theme="dark"] .empty-illustration {
  background: linear-gradient(135deg,
    rgba(8, 145, 178, 0.15) 0%,
    rgba(8, 145, 178, 0.25) 100%);
}
</style>
