<template>
  <div style="margin: 0 auto; background: #fff;">

    <AtUser v-if="showAtUser" :input-content="inputContent" @selectUser="selectUser" topIndex="40px"></AtUser>

    <div class="commentBox">
    <span class="left" v-if="isShowAvatar">
      <img v-if="isShowAvatar" :src="getUserPhoto" onerror="onerror=null;src=defaultAvatar"/>
    </span>
      <span class="right" :class="isShowAvatar?'rightWidth':'rightMaxWidth'">
        <div class="chat-input-box-container">
          <div @paste.capture.prevent="handlePaste" v-on="listeners" v-focus @focus="atUserFocus" v-model="inputContent"
               class="chat-input-box" ref="inputBox" placeholder="既然来了，那就留下些动态吧~ 【支持Markdown语法】"
               :contenteditable="contenteditable"></div>
        </div>
    </span>
    </div>
    <div class="bottom">
      <el-tooltip
        effect="dark"
        :disabled="authCode.ADD_MOMENT"
        content="权限不足，无法操作"
        placement="top-end"
      >
        <span class="submit p2">
          <el-button type="primary" @click="handleSubmit" :disabled="!authCode.ADD_MOMENT">发布动态</el-button>
        </span>
      </el-tooltip>

      <el-button class="cancel p2" type="info" @click="handleCancle">取消</el-button>
      <emoji class="cancel p2" @selected="emojiSelected" @sendPic="sendPic" bottomIndex="15px"></emoji>

      <span class="pointer" style="margin-left: 10px; font-size: 17px; color: #909090;" @click="clickType('1')"><i
        class="el-icon-picture-outline-round"></i>图片</span>

      <el-popover
        placement="bottom-start"
        width="450"
        style="height: 50px"
        v-model="visible">
        <div class="url-container">

          <el-form label-position="left" :model="urlForm" label-width="100px" ref="blogLink" :rules="rules">
            <el-form-item label="网站地址" :label-width="labelWidth" prop="url">
              <el-input v-model="urlForm.url" style="width: 270px;" type="text" autocomplete="off"
                        placeholder="eg:http://www.example.com"></el-input>
            </el-form-item>

            <el-form-item label="网站信息" :label-width="labelWidth" prop="info">
              <el-input v-model="urlForm.info" style="width: 270px;" type="text" autocomplete="off"
                        placeholder="解析失败，可以手动输入网站信息"></el-input>
              <el-button type="primary" style="margin-left: 5px;" @click="submitUrl">解析</el-button>
            </el-form-item>
          </el-form>
        </div>

        <span slot="reference" class="pointer" style="margin-left: 10px; font-size: 17px; color: #909090;"
              @click="clickType('2')"><i class="el-icon-link"></i>链接</span>

      </el-popover>

      <el-popover
        placement="bottom-start"
        width="350"
        style="height: 50px"
        v-model="topVisible">
        <div class="tip">
          <strong>提示</strong>
          <span>
            请选择讨论的话题
          </span>
        </div>
        <el-select @change="topicChange" multiple :multiple-limit="3" v-model="topicValue"
                   placeholder="请选择讨论的话题">
          <el-option
            v-for="item in momentTopicList"
            :key="item.uid"
            :label="item.content"
            :value="item.uid">
          </el-option>
        </el-select>

        <span slot="reference" class="pointer" style="margin-left: 10px; font-size: 17px; color: #909090;"
              @click="clickType('3')"><i class="el-icon-chat-line-round"></i>话题</span>
      </el-popover>

      <el-tag
        style="margin-left: 3px"
        :key="index"
        v-for="(item, index) in topicValue"
        :type="typeList[index%5]"
        hit
        size="mini"
        effect="light"
      >{{ momentTopicMap[item].content }}
      </el-tag>
    </div>

    <div v-if="isShowUrl">
      <div class="link-container">
        <div class="zone-link-part">
          <span class="el-icon-link zone-link-bg"></span>
          <div class="link-right-part"><span class="title">{{ urlInfo.info }}</span>
            <a :href="urlInfo.url" target="_blank" class="url">{{ urlInfo.url }}</a>
          </div>
        </div>
        <i class="el-icon-circle-close link-close-btn"></i>
      </div>
    </div>

    <div style="padding: 10px;" v-if="showType == '1' || changeFileList.length > 0">
      <el-upload
        :file-list="changeFileList"
        :on-change="handleChange"
        ref="uploadPicture"
        name="filedatas"
        :data="otherData"
        :action="uploadPictureHost"
        :auto-upload="false"
        list-type="picture-card"
        :on-preview="handlePictureCardPreview"
        :before-upload="beforeUpload"
        :on-remove="handleRemove">
        <i class="el-icon-plus"></i>
      </el-upload>

      <el-dialog :visible.sync="dialogVisible">
        <img width="100%" :src="dialogImageUrl" alt="">
      </el-dialog>
    </div>
  </div>

</template>

<script>
import {getCookie} from "@/utils/cookieUtils";
import {mapGetters, mapMutations} from 'vuex';
import EmojiPanel from "@/components/EmojiPanel/EmojiPanel.vue";
import emoji from "@/components/ChatRoom/input-box/emoji.vue";
import {Loading} from "element-ui";
import {addUserMoment, getUserMomentTopicList, parseUrl} from "@/api/moment"
import AtUser from "@/components/AtUser";

export default {
  name: 'ZoneBox',
  props: {
    // 是否显示头像
    showAvatar: {
      type: Boolean,
      default: true
    },
    userInfo: {
      type: Object
    },
    // 回复的对象
    toInfo: {
      type: Object
    },
    // 博客信息
    commentInfo: {
      type: Object
    },
    showCancel: {
      type: Boolean,
      default: true
    }
  },
  components: {
    EmojiPanel,
    emoji,
    AtUser,
  },
  data() {
    return {
      showAtUser: false, // 展示AT用户
      comments: [],
      submitting: false,
      value: '',
      user: {},
      imgCount: 0,
      imgSrcMap: {},
      replaceMap: null,
      labelWidth: "80px",
      isShowEmojiPanel: false, // 是否显示表情面板
      isShowAvatar: true,
      defaultAvatar: process.env.defaultAvatar,
      dialogImageUrl: '',
      dialogVisible: false,
      showType: '0',
      visible: false,
      topVisible: false,
      isShowUrl: false,
      urlInfo: {
        url: "",
        info: ""
      },
      contenteditable: true,
      urlForm: {
        urlValue: '',
        urlInfoValue: '',
      },
      pasteType: ["image/jpeg", "image/png", "image/jpg", "image/gif"],
      topicValue: [],
      uploadPictureHost: process.env.PICTURE_API + "/file/pictures",
      uploadAdminHost: process.env.WEB_API + "/createBlog/uploadLocalBlog",
      importHeaders: {
        Authorization: getCookie("token")
      },
      localUploadVisible: false,
      uploadLoading: null,
      pictureList: [], // 上传的图片列表
      otherData: {
        source: "picture",
        userUid: this.$store.state.user.userInfo.uid,
        projectName: "blog",
        sortName: "moment",
        token: getCookie("token")
      },
      momentTopicMap: {},
      momentTopicList: [],
      typeList: ['warning', 'danger', 'success', 'info', 'primary'],
      changeFileList: [], // 待上传的文件列表
      inputContent: "",
      rules: {
        info: [
          {min: 1, max: 50, message: '长度在1到50个字符'},
        ],
        url: [
          {required: true, message: '网站地址不能为空', trigger: 'blur'},
          {pattern: /^((https|http|ftp|rtsp|mms)?:\/\/)[^\s]+/, message: '请输入有效的URL'},
        ],
      },
      authCode: {},
    }
  },
  computed: {
    ...mapGetters(['getUserPhoto']),
    listeners() {
      return Object.assign({}, this.$listeners, {
        input: function (e) {
          const inputContent =
            this.contentType === "plain"
              ? e.target.textContent
              : e.target.innerHTML;
          this.inputContent = inputContent;
        }.bind(this),
      });
    },
  },
  watch: {
    // 判断登录状态位是否改变（用于控制弹框）
    '$store.state.app.momentTopicList': function (newFlag, oldFlag) {
      this.topicValue = newFlag
    },
    '$store.state.user.authCode': function (newFlag, oldFlag) {
      this.authCode = this.$store.state.user.authCode
      console.log("获取用户权限", this.authCode)
    }
  },
  beforeDestroy() {
    document.removeEventListener('click', this.handleClickOutside)
  },
  mounted() {
    // 页面加载的时候调用监听
    this.hideEmojiPanel()
    // 获取宽高
    window.onresize = () => {
      return (() => {
        this.resizeWin();
      })();
    };
    document.addEventListener('click', this.handleClickOutside)
    this.resizeWin();
  },
  created() {
    this.authCode = this.$store.state.user.authCode
    this.isShowAvatar = this.showAvatar
    this.userMomentTopicList()
  },
  methods: {
    //拿到vuex中的写的方法
    ...mapMutations(['setLoginMessage']),
    handleClickOutside(event) {
      if (this.$refs.inputBox && !this.$refs.inputBox.contains(event.target)) {
        this.showAtUser = false
      } else {
        this.showAtUser = true
      }
    },
    // 选择用户回调
    selectUser(user, newText) {
      this.inputContent = newText
      this.$refs.inputBox.innerHTML = newText
      this.$commonUtil.moveCursorToEnd(this.$refs.inputBox, false)
      this.atUserUid = user.uid
    },
    atUserFocus() {
      this.showAtUser = true
    },
    emojiSelected(e, src, imageUrl) {
      // let imgSrc = require(`@/assets/emoji/${src}`);
      let imgTag = `<img src="${imageUrl}" class="chat-msg-emoji" width="20" height="20" style="width:20px; height: 20px; display: inline-block; margin-left: 2px; vertical-align: middle; margin-right: 2px;">`;
      this.inputContent = this.inputContent + imgTag
      this.$refs.inputBox.innerHTML = this.inputContent
      this.$commonUtil.moveCursorToEnd(this.$refs.inputBox, false)
    },
    sendPic(url, fileUid) {
      let imgTag = `<img class="chat-msg-img"  src="${url}" style="max-height: 160px"  uid="${fileUid}">`;
      this.inputContent = this.inputContent + imgTag
      this.$refs.inputBox.innerHTML = this.inputContent
      this.$commonUtil.moveCursorToEnd(this.$refs.inputBox, false)
    },
    userMomentTopicList: function () {
      let params = {}
      params.currentPage = 1;
      params.pageSize = 10;
      getUserMomentTopicList(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let momentTopicList = response.data.records;
          let momentTopicMap = {}
          for (let i = 0; i < momentTopicList.length; i++) {
            momentTopicMap[momentTopicList[i].uid] = momentTopicList[i]
          }
          this.momentTopicMap = momentTopicMap
          this.momentTopicList = momentTopicList
        }
      });
    },
    handlePaste(e) {
      let _this = this;
      let pastedText = undefined;
      // 兼容IE
      if (window.clipboardData && window.clipboardData.getData) {
        // 获取拷贝进剪切板指定格式的数据 (此处用的Text格式)
        pastedText = window.clipboardData.getData("Text");
        document.execCommand("insertText", false, pastedText);
      } else if (
        (e.clipboardData || e.originalEvent.clipboardData) &&
        (e.clipboardData || e.originalEvent.clipboardData).getData
      ) {
        let clip = (e.originalEvent || e).clipboardData;
        if (clip.types.indexOf("Files") >= 0) {
          let file = clip.files[0];
          if (_this.pasteType.indexOf(file.type) > 0) {
            // 图片粘贴
            let imgSrc = URL.createObjectURL(file);
            // let imgTag = `<img src="${imgSrc}" a="${_this.imgCount}" height="100px" style="display: inline-block; margin-left: 2px; vertical-align: middle; margin-right: 2px;">`;
            // document.execCommand("insertHTML", false, imgTag);

            let fileData = {
              "name": "1675951572392.jpg",
              "percentage": 0,
              "raw": file,
              "size": file.size,
              "status": "ready",
              "uid": file.lastModified,
              "url": imgSrc,
            }
            this.changeFileList.push(fileData)
            // 记录file点击时上传
            _this.imgSrcMap[_this.imgCount] = file;
            _this.imgCount++;
          }
        } else {
          pastedText = (e.originalEvent || e).clipboardData.getData(
            "text/plain"
          );
          document.execCommand("insertText", false, pastedText);
        }
      }
      return false;
    },
    topicChange: function () {
      console.log("topicChange", this.topicValue)
    },
    submitUrl: function () {
      let url = this.urlForm.url
      let info = this.urlForm.info
      if (info) {
        this.urlInfo.info = info
        this.urlInfo.url = url
        this.visible = false
        this.isShowUrl = true
        this.showType = '0'
      } else {
        let params = {}
        params.url = url;
        parseUrl(params).then(response => {
          if (response.code == this.$ECode.SUCCESS) {
            this.$message.success("解析成功");
            this.urlInfo.info = response.data
            this.urlInfo.url = url
            this.visible = false
            this.isShowUrl = true
            this.showType = '0'
          } else {
            this.$message.error("解析失败");
          }
        })
      }

    },
    clickType: function (type) {
      console.log("点击type", type)
      switch (type) {
        case '1': {
          if (this.showType == '1') {
            this.showType = '0'
          } else {
            this.showType = '1'
          }
        }
          break;
        case '2': {
          if (this.showType == '2') {
            this.showType = '0'
          } else {
            this.showType = '2'
          }
        }
          break;
        case '3': {
          if (this.showType == '3') {
            this.showType = '0'
          } else {
            this.showType = '3'
          }
        }
          break;
      }
    },
    handleChange: function (file, fileList) {
      this.changeFileList = fileList
    },
    toParseUrl: function () {
      let params = {}
      params.url = this.urlValue;
      parseUrl(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.$message.success("解析成功");
          this.urlInfo = response.data
        } else {
          this.$message.error("解析失败");
        }
      })
    },
    isAssetTypeAnImage(ext) {
      return [
        'png', 'jpg', 'jpeg', 'bmp', 'gif', 'webp', 'psd', 'svg', 'tiff'].indexOf(ext.toLowerCase()) !== -1;
    },
    beforeUpload(file) {
      console.log("开始上传文件", this.isAssetTypeAnImage(file.type))
      const isJPG = file.type === 'image/jpeg';
      const isLt2M = file.size / 1024 / 1024 < 5;
      if (!isJPG) {
        this.$message.error('上传头像图片只能是 JPG 格式!');
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 5MB!');
      }
      return isJPG && isLt2M;
    },
    /**
     * 自定义上传文件
     * @param fileList 文件列表
     * @param data 上传时附带的额外参数
     * @param url 上传的URL地址
     * @param success 成功回调
     * @param error 失败回调
     */
    uploadFiles({uploadFiles, headers, data, action, success, error}) {
      let form = new FormData()
      // 文件对象
      uploadFiles.map(file => form.append("filedatas", file.raw))
      // 附件参数
      for (let key in data) {
        form.append(key, data[key])
      }
      let xhr = new XMLHttpRequest()
      // 异步请求
      xhr.open("post", action, true)
      // 设置请求头
      xhr.setRequestHeader("Authorization", getCookie("token"));
      xhr.onreadystatechange = function () {
        if (xhr.readyState == 4) {
          if ((xhr.status >= 200 && xhr.status < 300) || xhr.status == 304) {
            success && success(xhr.responseText)
          } else {
            error && error(xhr.status)
          }
        }
      }
      xhr.send(form)
    },
    handleRemove(file, fileList) {
      this.changeFileList = fileList
    },
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url;
      this.dialogVisible = true;
    },
    openLoading() {
      this.uploadLoading = Loading.service({
        lock: true,
        text: '正在努力上传中……'
      })
    },
    closeLoading() {
      this.uploadLoading.close()
    },
    handleSubmit() {
      let info = this.$store.state.user.userInfo
      let isLogin = this.$store.state.user.isLogin
      if (!isLogin) {
        this.$notify.error({
          title: '警告',
          message: '登录后才可以发表动态~',
          offset: 100
        });
        // 未登录，自动弹出登录框
        this.setLoginMessage(Math.random())
        return;
      }
      this.otherData.userUid = info.uid

      if (this.inputContent === "") {
        this.$notify.error({
          title: '警告',
          message: '动态内容不能为空哦~',
          offset: 100
        });
        return;
      }

      // this.inputContent = this.inputContent.replace(/```(.+?)\n((.|\n)*?)\n```/g, function (match, p1, p2) {
      //   // 将代码块内的HTML标签和空格字符替换为空字符串
      //   return '```' + p1 + '\n' + p2.replace(/<.+?>/, '').replace(/\s+/g, '') + '\n```';
      // });

      // 计算移除标签后的字符数
      let count = this.$commonUtil.getValueCount(this.inputContent)
      if (count > 2048) {
        this.$notify.error({
          title: '警告',
          message: '动态字符过多，已超过2048个字符',
          offset: 100
        });
        return
      }

      // 开始上传图片
      if (this.$refs.uploadPicture && this.changeFileList.length > 0) {
        let {uploadFiles, action, data} = this.$refs.uploadPicture
        if (uploadFiles.length > 3) {
          this.$message.error('每条动态仅支持上传三张图片');
          return;
        }

        for (let i = 0; i < uploadFiles.length; i++) {
          //获取最后一个.的位置
          let filePath = uploadFiles[i].name
          let index = filePath.lastIndexOf(".");
          //获取后缀
          let ext = filePath.substr(index + 1);
          let isCanUpload = this.isAssetTypeAnImage(ext)
          if (!isCanUpload) {
            this.$message.error('请上传正确格式的图片!');
            return;
          }

          const isLt2M = uploadFiles[i].size / 1024 / 1024 < 10;
          if (!isLt2M) {
            this.$message.error('单个图片大小不能超过 5MB!');
            return;
          }
        }
        this.openLoading()
        this.uploadFiles({
          uploadFiles,
          data,
          action,
          success: (response) => {
            let res = JSON.parse(response)
            if (res.code == this.$ECode.SUCCESS) {
              this.$commonUtil.message.success("图片上传成功")
              let pictureList = res.data
              let list = []
              for (let a = 0; a < pictureList.length; a++) {
                let picture = {}
                picture.uid = pictureList[a].uid
                picture.fileOldName = pictureList[a].fileOldName
                picture.picUrl = pictureList[a].picUrl
                picture.qiNiuUrl = pictureList[a].qiNiuUrl
                picture.minioUrl = pictureList[a].minioUrl
                list.push(picture)
              }
              this.pictureList = list
              this.submitUserMoment()
            } else {
              this.$commonUtil.message.error(res.data)
            }
            this.closeLoading()
          },
          error: (error) => {
            this.closeLoading()
            this.$commonUtil.message.error("图片上传失败")
          }
        })
      } else {
        this.submitUserMoment()
      }
    },
    clearUserMoment: function () {
      // location.reload();
      let url = window.location.href;
      url = url.split('?')
      window.location.href = url[0]
      location.reload();
    },
    submitUserMoment() {
      // 开始提交动态
      let photoList = this.pictureList
      let fileUids = "";
      for (let i = 0; i < photoList.length; i++) {
        fileUids += photoList[i].uid + ","
      }
      let topicList = this.topicValue
      let topicUids = "";
      for (let i = 0; i < topicList.length; i++) {
        topicUids += topicList[i] + ","
      }
      let params = {}
      params.content = this.inputContent
      params.fileUids = fileUids
      params.topicUids = topicUids
      params.url = this.urlInfo.url;
      params.urlInfo = this.urlInfo.info;
      addUserMoment(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.$message.success(response.message);
          this.$emit("submit-box", response)
          // 发表成功，清空内容
          this.clearUserMoment()
        } else {
          this.$message.error(response.message);
        }
      })
    },
    emoji(word) {
      // 生成html
      const type = word.substring(1, word.length - 1);
      return `<span class="emoji-item-common emoji-${type} emoji-size-small"></span>`;
    },
    handleCancle() {
      this.inputContent = '';
      this.$refs.inputBox.innerHTML = this.inputContent
      this.isShowEmojiPanel = false
      if (this.toInfo) {
        this.$emit("cancel-box", this.toInfo.commentUid)
      }
      this.hideEmojiPanel()
    },
    showEmojiPanel() {
      this.isShowEmojiPanel = !this.isShowEmojiPanel;
    },
    hideEmojiPanel() {
      this.isShowEmojiPanel = false
    },
    appendEmoji(text) {
      this.inputContent = this.inputContent + ":" + text + ":"
      this.$refs.inputBox.innerHTML = this.inputContent
    },
    resizeWin() {
      //当前window 宽
      let centerWidth = document.documentElement.scrollWidth;
      if (centerWidth > 800) {
        this.isShowAvatar = true && this.showAvatar
      } else {
        // 是否显示头像框
        this.isShowAvatar = false && this.showAvatar
      }
    },
  },
};
</script>

<style>
@import "../../assets/css/emoji.css";

.el-upload-list--picture-card .el-upload-list__item {
  width: 100px !important;
  height: 100px !important;
  line-height: 100px !important;
}

.el-upload--picture-card {
  width: 100px !important;
  height: 100px !important;
  line-height: 100px !important;
}

.el-popover {
  height: 135px;
  width: 420px;
}


.emoji-size-small {
  zoom: 0.3;
  margin: 5px;
  vertical-align: middle;
}

.emoji-size-large {
  zoom: 0.5;
  margin: 5px;
}

.emoji-item-common {
  background: url("../../assets/img/emoji_sprite.png");
  display: inline-block;
}

.emoji-item-common:hover {
  cursor: pointer;
}

.emoji-size-small {
  zoom: 0.3;
}


@media only screen and (min-width: 300px) and (max-width: 767px) {
  .emoji-panel-wrap {
    box-sizing: border-box;
    border: 1px solid #cccccc;
    border-radius: 5px;
    background-color: #ffffff;
    width: 385px;
    height: 179px;
    position: absolute;
    z-index: 99;
    top: 10px;
  }

  .el-popover {
    height: 185px;
    width: 385px;
  }
}
</style>

<style scoped>

.chat-input-box-container {
  width: 100%;
  min-height: 126px;
  padding: 5px;
  position: relative;
  background-color: #f6f7fb;
  border: 0.2px solid #e5eff8;
  overflow-y: scroll;
  border-radius: 3px;
}

.chat-input-box {
  width: 100%;
  height: 100%;
}

.chat-input-box:empty::before {
  content: attr(placeholder);
  color: #ccc;
}

.chat-input-box[contenteditable] {
  outline: none;
  height: 150px;
}

.tip {
  padding: 10px;
  margin: 20px auto 15px auto;
  background-color: #ecf8ff;
  border-radius: 4px;
  border-left: 5px solid #50bfff;
  color: #888888;
}

.tip strong {
  color: #38485A;
  font-weight: 400 !important;
  font-size: 13px;
  padding-right: 8px;
}

.link-container {
  position: relative;
  padding: 10px;
}

.zone-link-part {
  width: 98%;
  display: flex;
  border: 1px solid #ebebeb;
  padding: 5px;
  border-radius: 4px;
  margin-top: 10px;
  justify-content: flex-start;
}

.zone-link-bg {
  padding: 20px;
  background: #0084ff;
  color: #fff;
}

.link-right-part {
  margin-left: 10px;
}

.link-right-part .title {
  line-height: 26px;
  font-size: 16px;
  color: #8a93a0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  padding-right: 10px;
}

.link-right-part .url {
  color: #0084ff;
  font-size: 14px;
  margin-top: 6px;
  opacity: .6;
  line-height: 24px;
}

.link-close-btn {
  position: absolute;
  right: 1px;
  font-size: 24px;
  color: #ebebeb;
  cursor: pointer;
  bottom: 1px;
}

.commentBox {
  /*min-width: 700px;*/
  width: 100%;
  height: 170px;
  margin: 0 auto;
}

.commentBox .left {
  display: inline-block;
  width: 4%;
  height: 100%;
  padding-top: 3px;
}

.commentBox .left img {
  cursor: pointer;
  margin: 0 auto;
  width: 90%;
  border-radius: 50%;
}

.commentBox .right {
  display: inline-block;
  margin: 5px 2px 0 0;
  height: 100%;
}

.rightWidth {
  width: 95%;
}

.rightMaxWidth {
  width: 99%;
}

textarea::-webkit-input-placeholder {
  color: #909399;
}

.commentBox .right textarea {
  color: #606266;
  padding: 10px 5px 5px 10px;
  resize: none;
  width: 98%;
  height: 100%;
}

.bottom {
  position: relative;
  /*width: 98%;*/
  height: 40px;
  line-height: 40px;
  margin-top: 1px;
}

.bottom .p2 {
  float: right;
  margin-top: 5px;
  margin-right: 10px;
}

.emoji-panel-btn img {
  height: 35px;
  width: 35px;
}

.emoji-panel-btn:hover {
  cursor: pointer;
  opacity: 0.8;
}


@media only screen and (min-width: 300px) and (max-width: 767px) {
  .commentBox .right {
    display: inline-block;
    margin: 5px 2px 0 0;
    width: 99%;
    height: 100%;
  }
}
</style>
