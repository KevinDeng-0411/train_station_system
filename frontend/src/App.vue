<template>
  <div id="app">
    <el-container>
      <el-header style="background: #409eff; color: white; display: flex; align-items: center;">
        <h2 style="margin: 0;">火车站票务管理系统</h2>
        <el-menu
          :default-active="$route.path"
          mode="horizontal"
          background-color="#409eff"
          text-color="white"
          active-text-color="#fff"
          :ellipsis="false"
          style="flex: 1; border: 0; margin-left: 50px;"
          :router="true"
        >
          <el-menu-item index="/trains">车次管理</el-menu-item>
          <el-menu-item index="/stations">站点管理</el-menu-item>
          <el-menu-item index="/salespeople">业务员管理</el-menu-item>
          <el-menu-item index="/tickets">售票</el-menu-item>
          <el-menu-item index="/tickets/refund">退票</el-menu-item>
          <el-menu-item index="/statistics">统计报表</el-menu-item>
          <el-menu-item index="/backup">备份恢复</el-menu-item>
        </el-menu>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
</script>

<style>
#app {
  font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
}
.el-header {
  padding: 0 20px;
}
</style>
