<template>
  <div id="app" :data-theme="theme">
    <!-- 动态渐变背景 -->
    <div class="bg-gradient">
      <div class="bg-blob bg-blob-1"></div>
      <div class="bg-blob bg-blob-2"></div>
      <div class="bg-blob bg-blob-3"></div>
    </div>

    <!-- 玻璃态顶部导航 -->
    <header class="glass-nav">
      <div class="nav-brand">
        <!-- 火车头SVG图标 -->
        <svg class="brand-icon" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
          <defs>
            <linearGradient id="trainGrad" x1="0" y1="0" x2="48" y2="48" gradientUnits="userSpaceOnUse">
              <stop offset="0%" stop-color="#22D3EE"/>
              <stop offset="100%" stop-color="#0891B2"/>
            </linearGradient>
          </defs>
          <path d="M8 36 L8 14 C8 11 10 9 13 9 L35 9 C38 9 40 11 40 14 L40 36 L36 36 L36 40 L32 40 L32 36 L16 36 L16 40 L12 40 L12 36 Z" fill="url(#trainGrad)"/>
          <rect x="11" y="13" width="26" height="14" rx="2" fill="white" opacity="0.92"/>
          <circle cx="14" cy="29" r="2.5" fill="#1E293B"/>
          <circle cx="34" cy="29" r="2.5" fill="#1E293B"/>
          <line x1="24" y1="13" x2="24" y2="27" stroke="#94A3B8" stroke-width="1.2"/>
          <rect x="13" y="14" width="9" height="6" rx="1" fill="#E0F2FE"/>
          <rect x="26" y="14" width="9" height="6" rx="1" fill="#E0F2FE"/>
        </svg>
        <div class="brand-text">
          <div class="brand-title">铁路票务</div>
          <div class="brand-subtitle">TICKET MANAGEMENT</div>
        </div>
      </div>

      <nav class="nav-menu">
        <router-link
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          class="nav-link"
          :class="{ active: isActive(item.path) }"
        >
          <el-icon class="nav-icon" v-if="item.icon">
            <component :is="item.icon" />
          </el-icon>
          <span>{{ item.label }}</span>
        </router-link>
      </nav>

      <div class="nav-meta">
        <!-- 深色模式切换 -->
        <button class="theme-toggle" @click="toggleTheme" :title="theme === 'dark' ? '切换亮色模式' : '切换深色模式'">
          <el-icon v-if="theme === 'dark'">< Sunny /></el-icon>
          <el-icon v-else>< Moon /></el-icon>
        </button>

        <div class="meta-pill">
          <span class="meta-dot"></span>
          <span>系统运行中</span>
        </div>
        <div class="meta-time">{{ currentTime }}</div>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="page" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <!-- 底部信息 -->
    <footer class="footer">
      <span>© 2026 火车站票务管理系统 · 数据库课程设计</span>
      <span class="footer-tag">v2.0 · Spring Boot 3 + Vue 3 + MySQL</span>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, markRaw } from 'vue'
import { useRoute } from 'vue-router'
import {
  Van, OfficeBuilding, User, Tickets, Refresh, DataAnalysis, FolderOpened,
  Sunny, Moon
} from '@element-plus/icons-vue'

const route = useRoute()
const currentTime = ref('')
const theme = ref('light')
let timer = null

// 从 localStorage 恢复主题
const savedTheme = localStorage.getItem('theme') || 'light'
theme.value = savedTheme
document.documentElement.setAttribute('data-theme', savedTheme)

const toggleTheme = () => {
  theme.value = theme.value === 'dark' ? 'light' : 'dark'
  document.documentElement.setAttribute('data-theme', theme.value)
  localStorage.setItem('theme', theme.value)
}

const navItems = [
  { path: '/trains', label: '车次管理', icon: markRaw(Van) },
  { path: '/stations', label: '站点管理', icon: markRaw(OfficeBuilding) },
  { path: '/salespeople', label: '业务员', icon: markRaw(User) },
  { path: '/tickets', label: '售票', icon: markRaw(Tickets) },
  { path: '/tickets/refund', label: '退票', icon: markRaw(Refresh) },
  { path: '/statistics', label: '统计报表', icon: markRaw(DataAnalysis) },
  { path: '/backup', label: '备份恢复', icon: markRaw(FolderOpened) }
]

const isActive = (path) => {
  if (path === '/tickets/refund') return route.path === path
  return route.path === path || route.path.startsWith(path + '/')
}

const updateTime = () => {
  const now = new Date()
  const pad = (n) => String(n).padStart(2, '0')
  currentTime.value = `${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
})
onUnmounted(() => clearInterval(timer))
</script>

<style>
/* ================================================================
 * App.vue 全局样式
 * 使用 design-tokens.css 中的 CSS 变量，不再重复定义颜色
 * ================================================================ */

/* ── 背景渐变 + 装饰球 ─────────────────────────────── */
.bg-gradient {
  position: fixed;
  inset: 0;
  z-index: -1;
  background: linear-gradient(135deg,
    var(--color-bg-base) 0%,
    var(--color-bg-surface) 50%,
    var(--primitive-cyan-50) 100%);
  overflow: hidden;
}
.bg-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(var(--blob-blur));
  opacity: var(--blob-opacity);
  animation: float 20s ease-in-out infinite;
}
.bg-blob-1 {
  width: 400px; height: 400px;
  background: radial-gradient(circle, var(--blob-1-color), transparent);
  top: -100px; left: -100px;
}
.bg-blob-2 {
  width: 500px; height: 500px;
  background: radial-gradient(circle, var(--blob-2-color), transparent);
  bottom: -150px; right: -150px;
  animation-delay: -7s;
}
.bg-blob-3 {
  width: 350px; height: 350px;
  background: radial-gradient(circle, var(--blob-3-color), transparent);
  top: 40%; right: 20%;
  animation-delay: -14s;
}
@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(40px, -30px) scale(1.1); }
  66% { transform: translate(-30px, 40px) scale(0.95); }
}

/* ── 玻璃态导航 ─────────────────────────────────── */
.glass-nav {
  position: sticky; top: 0; z-index: 100;
  display: flex; align-items: center; gap: var(--space-6);
  padding: var(--space-3) var(--space-8);
  background: var(--color-glass-bg);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border-bottom: 1px solid var(--color-glass-border);
  box-shadow: var(--nav-shadow);
}

.nav-brand {
  display: flex; align-items: center; gap: var(--space-3);
  user-select: none;
}
.brand-icon {
  width: 38px; height: 38px;
  filter: drop-shadow(0 4px 8px rgba(8, 145, 178, 0.25));
}
.brand-text { display: flex; flex-direction: column; line-height: 1.1; }
.brand-title {
  font-size: var(--text-xl); font-weight: 700;
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-light));
  -webkit-background-clip: text; -webkit-text-fill-color: transparent;
  background-clip: text;
}
.brand-subtitle {
  font-size: var(--text-xs); font-weight: 600;
  letter-spacing: 1.5px;
  color: var(--color-text-muted);
  font-family: var(--font-mono);
}

/* ── 导航菜单 ───────────────────────────────────── */
.nav-menu {
  display: flex; align-items: center; gap: var(--space-1);
  flex: 1; margin-left: var(--space-4);
}
.nav-link {
  display: flex; align-items: center; gap: 6px;
  padding: var(--space-2) var(--space-4);
  border-radius: var(--nav-link-radius);
  color: var(--color-text-secondary);
  text-decoration: none;
  font-size: var(--text-md); font-weight: 500;
  transition: all var(--duration-normal) var(--ease-default);
  position: relative;
}
.nav-link:hover {
  color: var(--color-primary);
  background: rgba(8, 145, 178, 0.06);
}
.nav-link.active {
  color: var(--color-text-inverse);
  background: var(--nav-active-bg);
  box-shadow: var(--nav-active-shadow);
  font-weight: 600;
}
.nav-link .nav-icon { font-size: 16px; }

/* ── 导航右侧信息 ───────────────────────────────── */
.nav-meta {
  display: flex; align-items: center; gap: var(--space-4);
}
.theme-toggle {
  display: flex; align-items: center; justify-content: center;
  width: 34px; height: 34px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: var(--color-glass-bg-deep);
  color: var(--color-text-secondary);
  cursor: pointer;
  font-size: var(--text-lg);
  transition: all var(--duration-normal) var(--ease-default);
}
.theme-toggle:hover {
  color: var(--color-primary);
  border-color: var(--color-primary);
  background: rgba(8, 145, 178, 0.06);
}
.meta-pill {
  display: flex; align-items: center; gap: 6px;
  padding: 6px var(--space-3);
  background: var(--color-success-bg);
  color: var(--color-success-text);
  border-radius: var(--radius-full);
  font-size: var(--text-sm); font-weight: 500;
}
.meta-dot {
  width: 6px; height: 6px;
  background: var(--color-success);
  border-radius: 50%;
  box-shadow: 0 0 8px var(--color-success);
  animation: pulse 2s ease-in-out infinite;
}
@keyframes pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.6; transform: scale(1.2); }
}
.meta-time {
  font-family: var(--font-mono);
  font-size: var(--text-md); font-weight: 600;
  color: var(--color-text-primary);
  letter-spacing: 1px;
}

/* ── 主内容 ─────────────────────────────────────── */
.main-content {
  max-width: 1400px; margin: 0 auto;
  padding: var(--space-8);
}

/* ── 页面过渡 ───────────────────────────────────── */
.page-enter-active, .page-leave-active {
  transition: all var(--duration-slow) var(--ease-default);
}
.page-enter-from { opacity: 0; transform: translateY(8px); }
.page-leave-to   { opacity: 0; transform: translateY(-8px); }

/* ── Element Plus 覆盖 ──────────────────────────── */
.el-card {
  background: var(--card-bg) !important;
  backdrop-filter: blur(10px);
  border: 1px solid var(--card-border) !important;
  border-radius: var(--card-radius) !important;
  box-shadow: var(--card-shadow) !important;
  transition: box-shadow var(--duration-normal) var(--ease-default),
              transform var(--duration-normal) var(--ease-default) !important;
}
.el-card:hover {
  box-shadow: var(--card-shadow-hover) !important;
  transform: translateY(-2px);
}
.el-card h2, .el-card h3 {
  margin: 0 0 var(--space-5) 0;
  font-size: var(--text-2xl); font-weight: 700;
  color: var(--color-text-primary);
  display: flex; align-items: center; gap: var(--space-2);
}
.el-card h2::before {
  content: '';
  display: inline-block;
  width: 4px; height: 18px;
  background: var(--btn-primary-gradient);
  border-radius: 2px;
  flex-shrink: 0;
}

.el-button--primary {
  background: var(--btn-primary-gradient) !important;
  border-color: transparent !important;
  box-shadow: var(--btn-primary-shadow) !important;
  transition: all var(--duration-normal) var(--ease-default) !important;
  font-weight: 500;
}
.el-button--primary:hover {
  transform: translateY(-1px);
  box-shadow: var(--btn-primary-shadow-hover) !important;
}
.el-button--success {
  background: var(--btn-success-gradient) !important;
  border-color: transparent !important;
  transition: all var(--duration-normal) var(--ease-default) !important;
}
.el-button--danger {
  background: var(--btn-danger-gradient) !important;
  border-color: transparent !important;
  transition: all var(--duration-normal) var(--ease-default) !important;
}
.el-button {
  transition: all var(--duration-normal) var(--ease-default) !important;
  font-weight: 500;
  border-radius: var(--radius-md) !important;
}

.el-form-item .el-input__wrapper, .el-form-item .el-textarea__inner, .el-form-item .el-select__wrapper {
  background: var(--input-bg) !important;
  border-radius: var(--input-radius) !important;
  box-shadow: 0 0 0 1px var(--input-border) !important;
  transition: box-shadow var(--duration-normal) var(--ease-default) !important;
  min-height: 38px;
  padding-left: var(--space-3) !important;
  padding-right: var(--space-3) !important;
}
.el-form-item .el-input__wrapper:hover, .el-form-item .el-textarea__inner:hover,
.el-form-item .el-select__wrapper:hover {
  box-shadow: var(--input-shadow-hover) !important;
}
.el-form-item .el-input__wrapper.is-focus, .el-form-item .el-textarea__inner:focus,
.el-form-item .el-select__wrapper.is-focus {
  box-shadow: var(--input-shadow-focus) !important;
}
.el-input__inner, .el-textarea__inner {
  color: var(--input-text-color) !important;
  font-size: var(--text-md);
}
.el-input__inner::placeholder, .el-textarea__inner::placeholder {
  color: var(--input-placeholder-color) !important;
  font-weight: 400;
}
.el-input__wrapper:hover, .el-textarea__inner:hover,
.el-select__wrapper:hover {
  box-shadow: var(--input-shadow-hover) !important;
}
.el-input__wrapper.is-focus, .el-textarea__inner:focus,
.el-select__wrapper.is-focus {
  box-shadow: var(--input-shadow-focus) !important;
}
.el-select__placeholder, .el-input__placeholder {
  color: var(--input-placeholder-color) !important;
  font-weight: 400;
}

/* 日期选择器 / 时间选择器 内部 input */
.el-date-editor.el-input, .el-date-editor.el-input__wrapper {
  background: var(--input-bg) !important;
  box-shadow: 0 0 0 1px var(--input-border) !important;
}
.el-date-editor.el-input:hover {
  box-shadow: var(--input-shadow-hover) !important;
}
.el-date-editor.el-input.is-focus {
  box-shadow: var(--input-shadow-focus) !important;
}

.el-table {
  background: transparent !important;
  border-radius: var(--table-radius) !important;
  overflow: hidden;
}
.el-table th.el-table__cell {
  background: var(--table-header-bg) !important;
  color: var(--table-header-fg) !important;
  font-weight: 600 !important;
  border-bottom: 2px solid var(--color-border-strong) !important;
  font-size: var(--text-sm);
}
.el-table tr {
  background: var(--table-row-bg) !important;
  transition: background var(--duration-fast) var(--ease-default);
}
.el-table tr:hover > td.el-table__cell {
  background: var(--table-row-bg-hover) !important;
}
.el-table td.el-table__cell {
  border-bottom: 1px solid var(--table-row-border) !important;
  color: var(--color-text-primary);
  font-size: var(--text-base);
}

.el-tag { font-weight: 500; border-radius: var(--tag-radius); }

/* 表单 label 加深（对比 12306） */
.el-form-item__label {
  color: var(--color-text-primary) !important;
  font-weight: 500 !important;
  font-size: var(--text-md) !important;
}

.el-dialog {
  border-radius: var(--dialog-radius) !important;
  overflow: hidden;
}
.el-dialog__header {
  background: var(--dialog-header-bg);
  padding: var(--space-5) var(--space-6) !important;
  border-bottom: 1px solid var(--color-border);
}
.el-dialog__title {
  font-weight: 700 !important;
  color: var(--dialog-header-fg) !important;
  font-size: var(--text-lg);
}
.el-dialog__body {
  background: var(--dialog-body-bg);
  padding: var(--space-6);
}

.el-pagination .el-pager li.is-active {
  background: var(--pagination-active-bg) !important;
  color: var(--pagination-active-fg) !important;
  font-weight: 600;
}
.el-pagination .el-pager li {
  border-radius: var(--radius-sm);
  transition: all var(--duration-fast) var(--ease-default);
}

/* ── 底部 ───────────────────────────────────────── */
.footer {
  text-align: center;
  padding: var(--space-6) var(--space-8);
  color: var(--color-text-muted);
  font-size: var(--text-sm);
  display: flex; justify-content: center;
  align-items: center; gap: var(--space-4);
  flex-wrap: wrap;
}
.footer-tag {
  padding: var(--space-1) var(--space-3);
  background: rgba(8, 145, 178, 0.08);
  border-radius: var(--radius-full);
  font-family: var(--font-mono);
  font-size: var(--text-xs);
  color: var(--color-text-secondary);
}

/* ── 响应式 ─────────────────────────────────────── */
@media (max-width: 1024px) { .nav-menu { display: none; } }
@media (max-width: 768px) {
  .glass-nav { padding: var(--space-3) var(--space-4); flex-wrap: wrap; }
  .nav-meta { display: none; }
  .main-content { padding: var(--space-4); }
}
</style>
