<template>
  <div class="webNotice" v-if="showNotice">
    <span style="float: right; cursor: pointer; margin-right: 10px;" @click="closeBox">关闭×</span>
    <span v-html="webNotice.content"></span>
  </div>
</template>

<script>

import {getWebNotice} from "../../api/messagePush";
import {getCookie, setCookie} from "../../utils/cookieUtils";

export default {
  name: "WebNotice",
  data() {
    return {
      webNotice: {},
      showNotice: false
    };
  },
  components: {},
  created() {
    this.getWebNoticeInfo()
  },
  mounted() {

  },
  watch: {
  },
  methods: {
    getWebNoticeInfo() {
      getWebNotice().then(res => {
        if (res.code == this.$ECode.SUCCESS) {
          this.webNotice = res.data
          let webNoticeUid = getCookie('webNotice')
          // 判断是否点击过公告
          if (webNoticeUid == this.webNotice.uid) {
            this.showNotice = false
          } else {
            this.showNotice = true
          }
        }
      })
    },
    closeBox: function () {
      let webNotice = this.webNotice
      this.showNotice = false
      if (!webNotice) {
        return
      }
      setCookie("webNotice", webNotice.uid, 100)
    },
  }
};
</script>


<style scoped>

</style>
