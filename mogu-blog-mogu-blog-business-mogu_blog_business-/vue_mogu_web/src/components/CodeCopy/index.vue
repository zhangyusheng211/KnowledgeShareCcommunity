<template>
  <div class="copy-content">
    <!-- 复制按钮 -->
    <div
      class="copy-btn code-data-copy"
      @click="copyMessage"
      data-clipboard-action="copy"
      :data-clipboard-text="code"
    >
      <i class="el-icon-document-copy"></i>
    </div>
<!--    <div v-if="success" class="copy-success-text">复制成功!</div>-->
  </div>
</template>

<script>
// 通过cdn的方式导入
// import clipboard from 'clipboard' //复制插件
export default {
  data() {
    return {
      code: null,
    }
  },
  methods: {
    copyMessage(value) {
      let _this = this
      let clipboard = new ClipboardJS('.code-data-copy')
      clipboard.on('success', function(e) {
        console.log('复制成功')
        _this.$message.success("复制成功")
        clipboard.destroy() // 销毁,避免多次点击重复出现
      })
      clipboard.on('error', function() {
        _this.$message.success("复制失败")
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.copy-content {
  height: 0;
}
.icon {
  width: 0.4rem;
  height: 0.4rem;
  fill: white;
}
.copy-btn {
  user-select: none;
  opacity: 0;
  position: absolute;
  right: 5px;
  top: 5px;
  cursor: pointer;
  padding: 5px;
  border-radius: 3px;
  transition: 0.3s;
  background: rgba(255, 255, 255, 0.2);
  &:active {
    background: rgba(253, 253, 253, 0.575);
  }
}
.copy-success-text {
  color: white;
  position: absolute;
  font-size: 12px;
  top: 8px;
  right: 2.5rem;
  font-weight: 200;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen,
  Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
  animation: successCopy 0.5s ease both 1;
}
@keyframes successCopy {
  70% {
    opacity: 1;
    transform: scale(1);
  }
  100% {
    opacity: 0;
    transform: scale(0.5);
  }
}
</style>
