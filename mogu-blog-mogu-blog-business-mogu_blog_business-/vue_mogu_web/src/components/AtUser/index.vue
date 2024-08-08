<template>
  <div>
    <el-card class="box-card" :body-style="{ padding: '5px' }" id="atUser" :style="{marginTop: topIndex}"
             style="position: absolute; width: 200px; height: 200px; overflow-y: scroll; z-index: 200" v-show="showAt">
      <ul class="infinite-list">

        <ul v-for="(item, index) in userList" @click="selectUser(item)">

          <el-card :body-style="{ padding: '0px' }" shadow="hover">
            <li style=" padding-right: 6px">
              <el-row>
                <el-col :span="5" style="text-align: center">
                  <span :class="item.userTag > 0 ?'vip-avatar':''">
                    <el-avatar :class="item.userTag > 0 ?'vip-color':''"
                               style="cursor: pointer;" shape="circle" :size="35" fit="fill" :src="item.photoUrl">
                      <img :src="defaultAvatar"/>
                      </el-avatar>
                      <span style="font-size: 10px; right: -4px; bottom: -3px;" class="vip-text pointer"
                            v-if="item.userTag > 0">v
                      </span>
                  </span>
                </el-col>

                <el-col :span="17">
                  <div>
                  <span class="blackFont"
                        style="cursor: pointer; font-size: 13px; margin-left: 5px ;font-weight: 600; white-space: nowrap;overflow: hidden;text-overflow: ellipsis; line-height: 35px;">
                    {{ item.nickName }}
                  </span>
                  </div>
                </el-col>

              </el-row>
            </li>
          </el-card>
        </ul>

      </ul>
    </el-card>
  </div>
</template>

<script>
import {getUserList} from "../../api/about";

export default {
  name: "atUser",
  props: ["inputContent", "topIndex"],
  data() {
    return {
      showAt: false,
      count: 10,
      oldText: "",
      searchUserName: " ", // 搜索名称
      userList: [], // 搜索的用户列表
      VUE_MOGU_WEB: process.env.VUE_MOGU_WEB,
      timer: null,
      defaultAvatar: process.env.defaultAvatar
    }
  },
  watch: {
    searchUserName(newVal) {
      let that = this
      clearTimeout(that.timer);
      this.timer = setTimeout(() => {
        // 在这里发送请求获取内容，并将结果存储到this.result中
        console.log("请求变化", newVal)
        that.querySearchAsync(newVal)
      }, 300);
    }
  },
  mounted() {
    this.$nextTick(() => {
      document.addEventListener('click', this.handleClickOutside);
    });
  },
  created() {
    let that = this
    document.addEventListener("keydown", function (event) {
      if (event.key === "@") { // 输入字符为a
        that.showAt = true
      }
      if (event.key === " ") { // 输入字符为a
        that.showAt = false
      }
      that.onKeyDown(event.key)
    });
  },
  methods: {
    handleClickOutside(event) {
      const contentEl = document.querySelector('#atUser');
      if (contentEl && !contentEl.contains(event.target)) {
        this.showAt = false;
      }
    },
    replaceFromEnd(str, oldStr, newStr) {
      let index = str.lastIndexOf(oldStr);
      if (index === -1) {
        return str;
      }
      let before = str.slice(0, index);
      let after = str.slice(index).replace(oldStr, newStr);
      return before.concat(after);
    },
    selectUser(user) {
      console.log("被替换的字符", this.inputContent)
      let messageHtml = "<a href='" + this.VUE_MOGU_WEB + "/userCenter?userUid=" + user.uid + "' target='_blank'>&nbsp;" + "@" + user.nickName + "&nbsp;</a>"
      let inputContent = this.replaceFromEnd(this.inputContent, "@" + this.searchUserName, messageHtml)
      this.$emit("selectUser", user, inputContent)
      this.searchUserName = ""
      this.showAt = false
    },
    querySearchAsync(queryString) {
      let params = {}
      params.currentPage = 1;
      params.pageSize = 11;
      params.keyword = queryString
      params.orderByDescColumn = "credits"
      getUserList(params).then(resp => {
        if (resp.code === this.$ECode.SUCCESS) {
          console.log("获取用户信息", resp)
          this.userList = resp.data.records
        }
      })
    },
    onKeyDown(char) {
      let that = this
      if (!that.showAt) {
        return
      }
      // 当输入@那一刻的字符
      if (char === "@") {
        setTimeout(() => {
          that.oldText = that.inputContent
        }, 100)
      }

      // 如果输入的字符是目标字符
      if (char === "@" || this.oldText.indexOf("@") !== -1) {
        setTimeout(() => {
          that.searchUserName = that.inputContent.replace(this.oldText, "")
        }, 100)

      }
    },
  },
}
</script>

<style scoped>

</style>
