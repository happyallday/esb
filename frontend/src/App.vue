<template>
  <div id="app">
    <el-container>
      <el-header>
        <div class="header">
          <h1>ESB Gateway - 接口中转服务</h1>
        </div>
      </el-header>
      <el-container>
        <el-aside width="200px">
          <el-menu
            :default-active="$route.path"
            router
            class="el-menu-vertical"
          >
            <el-menu-item index="/">
              <span>仪表盘</span>
            </el-menu-item>
            <el-menu-item index="/logs">
              <span>调用日志</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  width: 100vw;
  height: 100vh;
}

.el-container {
  height: 100%;
}

.el-header {
  background-color: #409EFF;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header h1 {
  font-size: 24px;
  font-weight: 500;
}

.el-aside {
  background-color: #f5f7fa;
  height: calc(100vh - 60px);
}

.el-menu {
  border-right: none;
}

.el-main {
  background-color: #fff;
  padding: 20px;
}
</style>