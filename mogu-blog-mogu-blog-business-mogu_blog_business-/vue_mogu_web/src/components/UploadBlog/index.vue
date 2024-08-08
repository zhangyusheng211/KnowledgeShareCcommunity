<template>
  <el-dialog
    :modal-append-to-body="false"
    title="本地文章上传【仅支持Markdown】"
    width="40%"
    :visible.sync="localUploadVisible"
    :before-close="beforeClose"
  >
    <div class="tipBox">
      <div class="tip">导入须知</div>
      <div class="tipItem">
        1）如果你的Markdown文档里面的图片是本地，需要选择本地图片，然后提交到图片服务器
      </div>
      <div class="tipItem">
        2）含有本地图片一定需要提前上传图片，否者会出现图片无法替换的问题
      </div>
      <div class="tipItem">
        3）如果你的Markdown文档里面的图片不是本地，直接选择博客文件上传即可
      </div>
      <div class="tipItem">
        4）目前支持Markdown文件单个上传，步骤是先提交所有图片，在提交博客文件
      </div>
      <div class="tipItem">
        5）因为网络或者服务器性能等不可抗拒的原因，因此建议不要上传较大的图片
      </div>
    </div>

    <el-upload
      style="margin-top: 10px"
      class="upload-demo2"
      ref="uploadPicture"
      name="filedatas"
      :data="otherData"
      :action="uploadPictureHost"
      :auto-upload="false"
      multiple
    >
      <el-button slot="trigger" size="small" type="primary"
        >选择本地图片</el-button
      >
      <el-button
        style="margin-left: 10px"
        size="small"
        type="success"
        @click="submitPictureUpload"
        >提交到图片服务器</el-button
      >
    </el-upload>

    <el-upload
      style="margin-top: 40px"
      class="upload-demo"
      ref="uploadFile"
      name="filedatas"
      :headers="importHeaders"
      :action="uploadAdminHost"
      :auto-upload="false"
      multiple
    >
      <el-button slot="trigger" size="small" type="primary"
        >选择博客文件</el-button
      >
      <el-button
        style="margin-left: 10px"
        size="small"
        type="success"
        @click="submitUpload"
        >上传文章</el-button
      >
    </el-upload>
  </el-dialog>
</template>

<script>
import { getCookie } from "@/utils/cookieUtils";
import { Loading } from 'element-ui';
import { mapMutations } from "vuex";
export default {
  props: ["visible"],
  created () {
    this.localUploadVisible = this.visible
  },
  components: {

  },
  watch: {
    visible: function () {
      this.localUploadVisible = this.visible;
    },
  },
  mounted () {

  },
  data () {
    return {
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
        userUid: "uid00000000000000000000000000000000",
        adminUid: "uid00000000000000000000000000000000",
        projectName: "blog",
        sortName: "web",
        token: getCookie("token")
      },
    }
  },
  methods: {
    ...mapMutations(["setDomainEventMessage"]),
    // 文件上传
    submitUpload () {
      let { uploadFiles, action } = this.$refs.uploadFile
      let data = {}
      data.pictureList = JSON.stringify(this.pictureList)
      this.openLoading()
      this.uploadFiles({
        uploadFiles,
        data,
        action,
        success: (response) => {
          let res = JSON.parse(response)
          if (res.code == this.$ECode.SUCCESS) {
            this.$commonUtil.message.success(res.message)
            //取消时，开始状态
            this.$emit("beforeClose", "");
          } else {
            this.$commonUtil.message.error(res.message)
          }
          this.localUploadVisible = false
          this.closeLoading()
          // 上传成功后，将里面的内容删除
          this.$refs.uploadFile.clearFiles();
          this.$refs.uploadPicture.clearFiles();

          // 上传成功后，打开我的文章
          // 发送个人中心领域事件
          let event = {
            "type": "personCenter",
            "action": "MyArticle",
            "time": new Date(),
          }
          this.setDomainEventMessage(event)

        },
        error: (error) => {
          this.closeLoading()
          console.log('失败了', error)
        }
      })
    },
    // 图片上传
    submitPictureUpload () {
      let { uploadFiles, action, data } = this.$refs.uploadPicture
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
          } else {
            this.$commonUtil.message.error("图片上传失败")
          }
          this.closeLoading()
        },
        error: (error) => {
          this.closeLoading()
          this.$commonUtil.message.error("图片上传失败")
          console.log('失败了', error)
        }
      })
    },
    /**
     * 自定义上传文件
     * @param fileList 文件列表
     * @param data 上传时附带的额外参数
     * @param url 上传的URL地址
     * @param success 成功回调
     * @param error 失败回调
     */
    uploadFiles ({ uploadFiles, headers, data, action, success, error }) {
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
    // 关闭时的回调
    beforeClose (done) {
      //取消时，开始状态
      this.$emit("beforeClose", "");
      done();
    },
    openLoading () {
      this.uploadLoading = Loading.service({
        lock: true,
        text: '正在努力上传中……'
      })
    },
    closeLoading () {
      this.uploadLoading.close()
    },
  }
}
</script>

<style scoped>
</style>
