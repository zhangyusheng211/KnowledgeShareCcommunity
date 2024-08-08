<template>
  <div class="index-page">
    <mavon-editor style="z-index: 0" ref="md" class="item-editor" :style="{'height': windowHeight + 'px'}" v-model="textContent"
                  @imgAdd="$imgAdd" @imgDel="$imgDel" @change="contentChange" @fullScreen="fullScreen">
    </mavon-editor>
  </div>
</template>

<script>
import { getToken } from '@/utils/auth'
const axios = require('axios');
export default {
  name: 'Mavon',
  props: ["context", 'height'],
  data () {
    return {
      form: {},
      img_file: {},
      textContent: this.context,
      windowHeight: this.height,
      imageClick: function (file) {
        console.log(file);
      },
      imgName: '',
    }
  },
  created () {
    setTimeout(()=> {
      // this.form.content = this.context;
      this.$set(this.form, 'content', this.textContent)
    },200)
  },
  components: {

  },
  mounted() {

  },
  methods: {
    initData: function () {
      this.textContent = ""
    },
    fullScreen: function () {
      console.log("全屏回调")
      let windowHeight = this.windowHeight
      let height = this.height
      if (windowHeight > height) {
        this.windowHeight = height
      } else {
        this.windowHeight = document.documentElement.clientHeight - 20
      }
    },
    //获取data
    getData: function() {
      console.log("获取内容", this.textContent)
      return this.$commonUtil.markdownToHtml(this.textContent);
    },
    setData: function(data) {
      this.textContent = this.$commonUtil.htmlToMarkdown(data)
    },
    contentChange: function(val, render) {
      this.$emit("contentChange", val);
    },
    $imgAdd(pos, $file) {
      console.log('imgAdd', pos, $file);
      this.img_file[pos] = $file;
      var formdata = new FormData();
      formdata.append(pos, this.img_file[pos]);
      axios({
        url: process.env.PICTURE_API + '/file/ckeditorUploadFile?token=' +  getToken(),
        method: 'post',
        data: formdata,
        headers: {'Content-Type': 'multipart/form-data'},
      }).then((res) => {
        this.$refs.md.$img2Url(pos, res.data.url)
      })
    },
    $imgDel(pos) {
      console.log('imgDel', pos);
      delete this.img_file[pos];
    },
    saveone (val, render) {
    },
  }
}
</script>

<style scoped>

</style>
