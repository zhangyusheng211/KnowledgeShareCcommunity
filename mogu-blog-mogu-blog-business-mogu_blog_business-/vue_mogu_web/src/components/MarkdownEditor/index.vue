<template>
  <div class="index-page" v-loading="isLoading">
    <div id="vditor" class="vditor" :style="vditorClass" />
  </div>
</template>

<script>
  import Vditor from 'vditor'
  import {getCookie} from "@/utils/cookieUtils";
  export default {
    name: 'MarkdownEditor',
    props: ["height"],
    data() {
      return {
        isLoading: true,
        isMobile: window.innerWidth <= 960,
        vditor: null
      }
    },
    created() {

    },
    components: {

    },
    computed:{
      vditorClass: function(){
        return {
          height: this.height + 'px'
        }
      }
    },
    mounted() {
      this.initVditor()
      this.$nextTick(() => {
        this.isLoading = false
      })
    },
    methods: {
      initVditor() {
        const that = this
        const options = {
          width: this.isMobile ? '100%' : '100%',
          height: this.height,
          tab: '\t',
          counter: '999999',
          typewriterMode: true,
          mode: 'ir', // wysiwyg 所见即所得 ， ir 即时渲染模式
          comment: true,
          preview: {
            delay: 100,
            show: !this.isMobile
          },
          theme: "classic",
          outline: true,
          input (text) {
            that.$emit("contentChange", "");
          },
          upload: {
            max: 5 * 1024 * 1024,
            // linkToImgUrl: 'https://sm.ms/api/upload',
            handler(file) {
              let formData = new FormData()
              for (let i in file) {
                formData.append('smfile', file[i])
              }
              let request = new XMLHttpRequest()
              // 图片上传路径
              request.open('POST', process.env.PICTURE_API + '/file/ckeditorUploadFile?token=' +  getCookie("token"))
              request.onload = that.onloadCallback
              request.send(formData)
            }
          }
        }
        this.vditor = new Vditor('vditor', options)
        this.vditor.input = this.inputHandle
        return this.vditor
      },
      onloadCallback(oEvent) {
        const currentTarget = oEvent.currentTarget
        if (currentTarget.status !== 200) {
          return this.$message({
            type: 'error',
            message: currentTarget.status + ' ' + currentTarget.statusText
          })
        }
        let resp = JSON.parse(currentTarget.response)
        let imgMdStr = ''
        if (resp.uploaded !== 1) {
          return this.$message({
            type: 'error',
            message: resp.error.message
          })
        }
        if (resp.uploaded === 1) {
          imgMdStr = `![${resp.fileName}](${resp.url})`
        }
        this.vditor.insertValue(imgMdStr)
      },
      //获取data
      getData: function() {
        return this.vditor.getHTML();
      },
      setData: function(data) {
        let that = this;
        window.localStorage.setItem('vditorvditor', "")
        this.$nextTick(() => {
          //DOM现在更新了
          let vditor = that.initVditor()
          let markdownText = that.$commonUtil.htmlToMarkdown(data)
          window.localStorage.setItem('vditorvditor', markdownText)
        });
      },
      initData: function () {
        let that = this
        window.localStorage.removeItem('vditorvditor')
        this.$nextTick(() => {
          if(that.vditor.vditor.lute) {
            that.vditor.setValue("")
            window.localStorage.setItem('vditorvditor', "")
          }
        });
      }
    }
  }
</script>

<style>
  .vditor-panel {
    line-height: 0px;
  }
  .index-page {
    width: 100%;
    height: 100%;
    background-color: #FFFFFF;
  }
  .vditor {
    width: 100%;
    /*height: calc(100vh - 100px);*/
    /*top: 20px;*/
    z-index: 99999;
    /*margin: 20px auto;*/
    text-align: left;
  }
  .vditor-reset {
    font-size: 14px;
  }
  .vditor-textarea {
    font-size: 14px;
    height: 100% !important;
  }
</style>


