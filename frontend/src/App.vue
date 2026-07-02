<template>
  <div id="app">
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
      <span class="footer-tag">v1.0 · Spring Boot 3 + Vue 3 + MySQL</span>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, markRaw } from 'vue'
import { useRoute } from 'vue-router'
import {
  Van, OfficeBuilding, User, Tickets, Refresh, DataAnalysis, FolderOpened
} from '@element-plus/icons-vue'

const route = useRoute()
const currentTime = ref('')
let timer = null

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
/* ===== 全局设计令牌 ===== */
:root {
  --color-primary: #0891B2;
  --color-primary-light: #06B6D4;
  --color-primary-dark: #0E7490;
  --color-accent: #22D3EE;
  --color-success: #10B981;
  --color-warning: #F59E0B;
  --color-danger: #EF4444;
  --color-text: #0F172A;
  --color-text-muted: #475569;
  --color-text-light: #94A3B8;
  --color-bg: #F0F9FF;
  --color-bg-end: #E0F2FE;
  --color-glass: rgba(255, 255, 255, 0.72);
  --color-glass-border: rgba(255, 255, 255, 0.5);
  --color-card: rgba(255, 255, 255, 0.85);
  --shadow-glass: 0 8px 32px rgba(8, 145, 178, 0.08);
  --shadow-elevated: 0 12px 40px rgba(15, 23, 42, 0.12);
  --radius-lg: 16px;
  --radius-md: 12px;
  --radius-sm: 8px;
}

* {
  box-sizing: border-box;
}

html, body, #app {
  margin: 0;
  padding: 0;
  min-height: 100vh;
  font-family: 'PingFang SC', 'Helvetica Neue', 'Microsoft YaHei', sans-serif;
  color: var(--color-text);
  background: var(--color-bg);
  -webkit-font-smoothing: antialiased;
}

/* ===== 背景渐变 + 装饰球 ===== */
.bg-gradient {
  position: fixed;
  inset: 0;
  z-index: -1;
  background: linear-gradient(135deg, #F0F9FF 0%, #E0F2FE 50%, #CFFAFE 100%);
  overflow: hidden;
}
.bg-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.4;
  animation: float 20s ease-in-out infinite;
}
.bg-blob-1 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, #22D3EE, transparent);
  top: -100px;
  left: -100px;
}
.bg-blob-2 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, #67E8F9, transparent);
  bottom: -150px;
  right: -150px;
  animation-delay: -7s;
}
.bg-blob-3 {
  width: 350px;
  height: 350px;
  background: radial-gradient(circle, #A5F3FC, transparent);
  top: 40%;
  right: 20%;
  animation-delay: -14s;
}
@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(40px, -30px) scale(1.1); }
  66% { transform: translate(-30px, 40px) scale(0.95); }
}

/* ===== 玻璃态导航 ===== */
.glass-nav {
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 14px 32px;
  background: rgba(255, 255, 255, 0.72);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.5);
  box-shadow: 0 4px 24px rgba(8, 145, 178, 0.06);
}

.nav-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  user-select: none;
}
.brand-icon {
  width: 38px;
  height: 38px;
  filter: drop-shadow(0 4px 8px rgba(8, 145, 178, 0.25));
}
.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.1;
}
.brand-title {
  font-size: 18px;
  font-weight: 700;
  background: linear-gradient(135deg, #0891B2, #06B6D4);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.brand-subtitle {
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 1.5px;
  color: var(--color-text-light);
  font-family: 'JetBrains Mono', 'SF Mono', monospace;
}

.nav-menu {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
  margin-left: 16px;
}
.nav-link {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: var(--radius-md);
  color: var(--color-text-muted);
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}
.nav-link:hover {
  color: var(--color-primary);
  background: rgba(8, 145, 178, 0.06);
}
.nav-link.active {
  color: white;
  background: linear-gradient(135deg, #0891B2, #06B6D4);
  box-shadow: 0 4px 12px rgba(8, 145, 178, 0.3);
  font-weight: 600;
}
.nav-link .nav-icon {
  font-size: 16px;
}

.nav-meta {
  display: flex;
  align-items: center;
  gap: 16px;
}
.meta-pill {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: rgba(16, 185, 129, 0.1);
  color: #047857;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 500;
}
.meta-dot {
  width: 6px;
  height: 6px;
  background: #10B981;
  border-radius: 50%;
  box-shadow: 0 0 8px rgba(16, 185, 129, 0.6);
  animation: pulse 2s ease-in-out infinite;
}
@keyframes pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.6; transform: scale(1.2); }
}
.meta-time {
  font-family: 'JetBrains Mono', 'SF Mono', monospace;
  font-size: 14px;
  color: var(--color-text);
  font-weight: 600;
  letter-spacing: 1px;
}

/* ===== 主内容 ===== */
.main-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 32px;
}

/* ===== 页面过渡 ===== */
.page-enter-active, .page-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.page-enter-from {
  opacity: 0;
  transform: translateY(8px);
}
.page-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

/* ===== 全局 Element Plus 风格覆盖 ===== */
.el-card {
  background: var(--color-card) !important;
  backdrop-filter: blur(10px);
  border: 1px solid var(--color-glass-border) !important;
  border-radius: var(--radius-lg) !important;
  box-shadow: var(--shadow-glass) !important;
}
.el-card h2, .el-card h3 {
  margin: 0 0 20px 0;
  font-size: 20px;
  font-weight: 700;
  color: var(--color-text);
  display: flex;
  align-items: center;
  gap: 8px;
}
.el-card h2::before {
  content: '';
  display: inline-block;
  width: 4px;
  height: 18px;
  background: linear-gradient(180deg, #0891B2, #06B6D4);
  border-radius: 2px;
}

.el-button--primary {
  background: linear-gradient(135deg, #0891B2, #06B6D4) !important;
  border-color: transparent !important;
  box-shadow: 0 2px 8px rgba(8, 145, 178, 0.3) !important;
}
.el-button--primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(8, 145, 178, 0.4) !important;
}
.el-button--success {
  background: linear-gradient(135deg, #10B981, #34D399) !important;
  border-color: transparent !important;
}
.el-button--danger {
  background: linear-gradient(135deg, #EF4444, #F87171) !important;
  border-color: transparent !important;
}
.el-button {
  transition: all 0.2s ease;
  font-weight: 500;
}

.el-input__wrapper, .el-textarea__inner, .el-select__wrapper {
  background: rgba(255, 255, 255, 0.85) !important;
  border-radius: var(--radius-sm) !important;
  transition: all 0.2s ease;
}
.el-input__wrapper:hover, .el-textarea__inner:hover {
  box-shadow: 0 0 0 1px #06B6D4 inset !important;
}
.el-input__wrapper.is-focus {
  box-shadow: 0 0 0 2px #0891B2 inset !important;
}

.el-table {
  background: transparent !important;
  border-radius: var(--radius-md) !important;
  overflow: hidden;
}
.el-table th.el-table__cell {
  background: rgba(8, 145, 178, 0.08) !important;
  color: var(--color-primary-dark) !important;
  font-weight: 600 !important;
  border-bottom: 2px solid rgba(8, 145, 178, 0.2) !important;
}
.el-table tr {
  background: rgba(255, 255, 255, 0.6) !important;
  transition: background 0.2s ease;
}
.el-table tr:hover > td.el-table__cell {
  background: rgba(8, 145, 178, 0.04) !important;
}
.el-table td.el-table__cell {
  border-bottom: 1px solid rgba(8, 145, 178, 0.06) !important;
}

.el-tag {
  font-weight: 500;
  border-radius: 6px;
}

.el-dialog {
  border-radius: var(--radius-lg) !important;
  overflow: hidden;
}
.el-dialog__header {
  background: linear-gradient(135deg, #F0F9FF, #E0F2FE);
  padding: 20px 24px !important;
}
.el-dialog__title {
  font-weight: 700 !important;
  color: var(--color-primary-dark) !important;
}

.el-pagination .el-pager li.is-active {
  background: linear-gradient(135deg, #0891B2, #06B6D4) !important;
  color: white !important;
}

/* ===== 底部 ===== */
.footer {
  text-align: center;
  padding: 24px 32px;
  color: var(--color-text-light);
  font-size: 12px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}
.footer-tag {
  padding: 4px 10px;
  background: rgba(8, 145, 178, 0.08);
  border-radius: 999px;
  font-family: 'JetBrains Mono', monospace;
}

/* ===== 响应式 ===== */
@media (max-width: 1024px) {
  .nav-menu { display: none; }
}
@media (max-width: 768px) {
  .glass-nav { padding: 12px 16px; flex-wrap: wrap; }
  .nav-meta { display: none; }
  .main-content { padding: 16px; }
}
</style>