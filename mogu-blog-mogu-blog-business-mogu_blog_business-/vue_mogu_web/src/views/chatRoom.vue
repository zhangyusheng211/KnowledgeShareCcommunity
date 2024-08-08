<template>
  <div class="main">
    <div class="pagebg sh"></div>
    <div class="container" style="min-height: 810px;">
      <div>
        <ChatRoom style=""></ChatRoom>
      </div>
    </div>
  </div>

</template>

<script>
import ChatRoom from '@/components/ChatRoom'
import {recorderBehavior} from "../api";
export default {
  name: "chat",
  components: {
    ChatRoom
  },
  created() {
    // 5S后埋点信息上报
    setTimeout(()=>{
      let params = {}
      params.otherData = "私信"
      params.behavior = "VISIT_PAGE"
      recorderBehavior(params).then(response => {})
    }, 5000)
  }
}
</script>

<style scoped>

</style>
