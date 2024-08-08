<template>
  <div class="emoji-wrapper">
    <!--        <span id="emoji-span" class="input-toolbar-icon fa fa-smile-o"></span>-->
    <el-tooltip placement="top" effect="light">
      <div slot="content">表情</div>
      <span id="emoji-span" class="iconfont pointer" style="font-size: 20px;" @click="showEmojiPanel">&#xe63d;</span>
    </el-tooltip>


    <div v-if="showImagePanel" class="emoji-ul" ref="emojiUl">

      <ul id="emoji-ul" v-if="showImagePanelStatus == 1" class="emoji-picker" :style="{bottom: bottomIndex}">
        <li v-for="(item, key) in files" :key="key" class="emoji-picker-item">
          <img class="emoji-icon" @click="insertImage($event, item.src)" width="30" height="30"
               :src="require(`@/assets/emoji/${item.src}`)" alt/>
        </li>
      </ul>

      <ul id="emoji-ul" v-if="showImagePanelStatus == 2" class="emoji-picker" :style="{bottom: bottomIndex}">
        <li class="uploadImgBody" id="emoji-upload1"  @click="localUploadVisible = true">
          <i id="emoji-upload2" class="el-icon-plus avatar-uploader-icon"/>
        </li>

        <li v-for="(item, key) in emoticonList" :key="key" class="emoji-picker-item" @contextmenu.prevent="onContextImageMenu">
          <img class="emoji-icon"
               :uid="item.uid"
               @click="sendEmoticon(item.photoUrl, item.fileUid)"
               width="75" height="75" :src="item.photoUrl" alt/>
        </li>

        <li id="emoji-select3"  v-if="total > emoticonList.length"  @click="emoticonSelectMore" class="uploadImgBody">加载更多</li>

      </ul>

      <div class="bottomSelect">

        <el-tooltip placement="top" effect="light">
          <div slot="content">表情</div>
          <span id="emoji-select1" class="iconfont pointer" @click="emojiSelect" :class=" showImagePanelStatus == 1 ? 'selectEmoji': ''"
                style="font-size: 30px; margin-left: 10px;">&#xe63d;</span>
        </el-tooltip>

        <el-tooltip placement="top" effect="light">
          <div slot="content">表情包</div>
          <span id="emoji-select2" class="iconfont pointer" @click="emoticonSelect" :class=" showImagePanelStatus == 2 ? 'selectEmoji': ''"
                style="font-size: 30px; margin-left: 10px;">&#xe614;</span>
        </el-tooltip>


      </div>

    </div>

    <el-dialog
      :visible.sync="localUploadVisible"
      title="上传表情包"
      width="40%">
      <el-upload
        name="filedatas"
        :data="otherData"
        :action="uploadPictureHost"
        :on-success="handleAvatarSuccess"
        :before-upload="beforeAvatarUpload"
        class="upload-demo"
        drag
        multiple>
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将图片文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">只能上传图片文件，且不超过2MB</div>
      </el-upload>
    </el-dialog>

  </div>
</template>
<script>
import {addEmoticon, getEmoticonList, deleteEmoticon, stickyEmoticon} from "../../../api/emoticon";

const path = require("path");

// const requireEmoji = require.context("@/assets/emoji")
const requireEmoji = require.context("../../../../static/images/emoji");
let emojiKeys = requireEmoji.keys();
import {getCookie} from "@/utils/cookieUtils";
export default {
  props: ["bottomIndex"],
  data() {
    return {
      showImagePanelStatus: 1,
      showImagePanel: false,
      uploadPictureHost: process.env.PICTURE_API + "/file/pictures",
      VUE_MOGU_WEB: process.env.VUE_MOGU_WEB,
      importHeaders: {
        Authorization: getCookie("token"),
        contentType: 'application/x-www-form-urlencoded;charset=UTF-8',
      },
      localUploadVisible: false,
      uploadLoading: null,
      pictureList: [], // 上传的图片列表
      currentPage: 1,
      pageSize: 13,
      total: 0,
      emoticonList: [],
      otherData: {
        source: "picture",
        userUid: "uid00000000000000000000000000000000",
        adminUid: "uid00000000000000000000000000000000",
        projectName: "blog",
        sortName: "chat",
        token: getCookie("token")
      },

      // files: emojiKeys.map((url) => require(`@/assets/emoji/${url.slice(2)}`)),
      files: emojiKeys.map(url => {
        return {
          src: `${url.slice(2)}`,
        }
      })
    };
  },
  mounted() {
    document.addEventListener('click', this.handleClickOutside)
  },
  beforeDestroy() {
    document.removeEventListener('click', this.handleClickOutside)
  },
  created() {
    console.log("初始化实例: comment")
    // window.addEventListener("click", this.clickEvent, true);
  },
  methods: {
    sendEmoticon(img, fileUid) {
      this.showImagePanel = false
      this.$emit("sendPic", img, fileUid);
    },
    getEmoticonList() {
      let params = {}
      params.currentPage = this.currentPage
      params.pageSize = this.pageSize
      getEmoticonList(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let emoticonList = response.data.records
          this.total = response.data.total
          if (emoticonList.length > 0) {
            this.emoticonList = this.emoticonList.concat(emoticonList)
          }
        } else {
          this.$message.error(response.message)
        }
      })
    },
    handleAvatarSuccess(res, fileList) {
      let resp = fileList.response
      if (resp.code == this.$ECode.SUCCESS ) {
        console.log("file", resp.data)
        let file = resp.data[0]
        let params = {}
        params.fileUid = file.uid
        params.isSelection = 0
        addEmoticon(params).then(response => {
          if (response.code == this.$ECode.SUCCESS) {
              this.$message.success(response.message)
          } else {
            this.$message.error(response.message)
          }
          this.localUploadVisible = false
          this.showImagePanel = true
          this.emoticonSelect()
        })
      } else {
        this.$message.error(resp.message)
      }
    },
    onContextImageMenu(event) {
      console.log("图片上右击", event)
      // 右键操作的文本
      let uid = event.target.getAttribute("uid")
      console.log("获取uid", uid)
      this.$contextmenu({
        items: [
          {
            label: "置顶",
            icon: "el-icon-star-off",
            divided: true,
            onClick: () => {
                // 置顶表情包
              let params = {
                "uid": uid
              }
              stickyEmoticon(params).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                  this.currentPage = 1
                  this.emoticonList = []
                  this.getEmoticonList()
                  this.$message.success(response.message)
                } else {
                  this.$message.error(response.message)
                }
              })
            }
          },
          {
            label: "删除",
            icon: "el-icon-delete",
            onClick: () => {
              let params = [
                {
                  "uid": uid
                }
              ]
              deleteEmoticon(params).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                  this.currentPage = 1
                  this.emoticonList = []
                  this.getEmoticonList()
                  this.$message.success(response.message)
                } else {
                  this.$message.error(response.message)
                }
              })
            }
          },
        ],
        event, // 鼠标事件信息
        customClass: "custom-class", // 自定义菜单 class
        zIndex: 2001, // 菜单样式 z-index
        minWidth: 100 // 主菜单最小宽度
      });
      return false;
    },
    beforeAvatarUpload(file) {
      console.log("开始上传表情")
      const isLt2M = file.size / 1024 / 1024 < 2;
      if (
        file.type !== "image/jpeg" &&
        file.type !== "image/jpg" &&
        file.type !== "image/png" &&
        file.type !== "image/gif"
      ) {
        this.$message.error('不是有效的图片文件!');
        return false;
      }

      if (!isLt2M) {
        this.$message.error('上传表情包大小不能超过 2MB!');
      }
      return isLt2M;
    },
    handleClickOutside(event) {
      if (this.$refs.emojiUl && !this.$refs.emojiUl.contains(event.target)) {
        this.showImagePanel = false
      }
    },
    showEmojiPanel() {
      if (this.showImagePanelStatus === 2) {
        this.currentPage = 1
        this.emoticonList = []
        this.getEmoticonList()
      }
      this.showImagePanel = !this.showImagePanel;
    },
    emoticonSelect() {
      // 重置次数
      this.emoticonList = []
      this.currentPage = 1
      this.getEmoticonList()
      this.showImagePanelStatus = 2
    },
    emoticonSelectMore() {
      // 加载更多
      this.currentPage = this.currentPage + 1
      this.getEmoticonList()
    },
    emojiSelect() {
      this.showImagePanelStatus = 1
    },
    insertImage(e, src) {
      this.showImagePanel = false;
      let emojiUrl =  this.VUE_MOGU_WEB + "/static/images/emoji/" + src
      this.$emit("selected", e, src, emojiUrl);
    },
  }
}
;
</script>

<style scoped>
.uploadImgBody {
  margin: 4px;
  cursor: pointer;
  width: 75px;
  height: 75px;
  line-height: 75px;
  text-align: center;
  border: dashed 1px #c0c0c0;
}

.uploadImgBody :hover {
  width: 75px;
  height: 75px;
  line-height: 75px;
  border: dashed 1px #00ccff;
}
</style>
<style scoped lang="less">

.selectEmoji {
  width: 40px;
  height: 40px;
  background: #d8d8d9;
  //color: red;
}

.emoji-picker {
  display: flex;
  flex-wrap: wrap;
  width: 428px;
  list-style: none;
  //bottom: 210px;
  left: 5px;
  z-index: 1000;
  height: 178px;
  overflow: scroll;
}

.bottomSelect {
  text-align: center;
  width: 446px;
  height: 40px;
  z-index: 1000;
  background: #fff;
  box-shadow: #ccc 1px 1px 7px;
  border-radius: 5px;
  left: 5px;
  display: flex;
  position: absolute;
  top: 3px;

  &.left {
    right: 0;
  }

  &.middle {
    left: 50%;
    transform: translateX(-50%);
  }
}

.emoji-picker-item {
  margin: 4px;
  cursor: pointer;

  img {
    user-select: none;
  }
}

.emoji-picker {
  background: #fff;
  box-shadow: #ccc 1px 1px 7px;
  border-radius: 5px;
  padding: 10px;

  display: flex;
  position: absolute;

  &.left {
    right: 0;
  }

  &.middle {
    left: 50%;
    transform: translateX(-50%);
  }
}

.emoji-icon {
  transition: transform .2s, box-shadow .2s;
}
.emoji-icon:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 20px 0 rgba(95,101,105,.05);
}
</style>
