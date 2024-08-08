<template>
  <div class="main">
    <div class="pagebg sh"></div>
    <div class="container" style="min-height: 810px;">
      <Chat style="margin-top: 30px"></Chat>
    </div>
  </div>

</template>

<script>
import Chat from '@/components/Chat'
export default {
  name: 'chat',
  components: {
    Chat
  }
}
</script>

<style scoped>
  .main {
    color: #4d4d4d;
    font: 14px/1.4em 'Helvetica Neue', Helvetica, 'Microsoft Yahei', Arial, sans-serif;
    background: #f5f5f5 url('http://localhost:8080/dist/images/bg.jpg') no-repeat center;
    background-size: cover;
  }
</style>
