<template>
    <div class="sidebarDiv guanzhu" id="follow-us" ref="follow" v-if="isShow">
      <h2 class="hometitle">关注我们</h2>
      <ul>
        <!-- <li class="sina"><a href="/" target="_blank"><span>新浪微博</span>蘑菇博客</a></li>         -->
        <li class="qqGroup" v-if="contact.qqGroup"><a href="javascript:void(0);"><span>QQ群</span>{{contact.qqGroup}}</a></li>
        <li class="qq" v-if="contact.qqNumber"><a :href="'tencent://AddContact/?fromId=50&fromSubId=1&subcmd=all&uin=' + contact.qqNumber" target="_blank"><span>QQ号</span>{{contact.qqNumber}}</a></li>
        <li class="email" v-if="contact.email"><a href="javascript:void(0);"><span>邮箱帐号</span>{{contact.email}}</a></li>
        <li class="wxgzh" v-if="contact.weChat"><a href="javascript:void(0);"><span>微信号</span>{{contact.weChat}}</a></li>
        <li class="github" v-if="contact.github"><a :href="contact.github" target="_blank"><span>Github</span>{{contact.github}}</a></li>
        <li class="gitee" v-if="contact.gitee"><a :href="contact.gitee" target="_blank"><span>Gitee</span>{{contact.gitee}}</a></li>
        <!-- <li class="wx"><img src="../../../static/images/wx.jpg"></li> -->
      </ul>
    </div>
</template>

<script>
export default {
  name: "FollowUs",
  data() {
    return {
      contact: {},
      isShow: false
    };
  },
  watch: {
    // 判断配置
    '$store.state.app.webConfigData': function (newFlag, oldFlag) {
      this.getContactData()
    },
  },
  created() {
    this.getContactData()
  },
  methods: {
    getContactData: function() {
      let webConfigData = this.$store.state.app.webConfigData;
      if(webConfigData.createTime) {
        this.contact = webConfigData;
        let showList = this.contact.showList
        if(showList.length > 2) {
          this.isShow = true;
        }
      }
    },
  }
};
</script>

<style>
</style>
