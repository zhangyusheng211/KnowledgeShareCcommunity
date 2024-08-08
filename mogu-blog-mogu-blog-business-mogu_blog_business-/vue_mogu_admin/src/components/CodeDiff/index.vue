<template>
    <!-- 添加或修改对话框 -->
    <el-dialog
        :title="title"
        fullscreen
        :visible.sync="dialogCodeDiffVisible"
        v-if="dialogCodeDiffVisible"
        :before-close="beforeClose"
    >
    <div>
        <code-diff :old-string="oldContent" :new-string="content" :context="context" :outputFormat="outputFormat" :drawFileList="drawFileList"  />
    </div>
    </el-dialog>

</template>

<script>
  import CodeDiff from 'vue-code-diff'
  import { getToken } from '@/utils/auth'
  import { Loading } from 'element-ui';
  export default {
    name: 'index',
    props: ["visible", "oldContentData" , "contentData", "loaded"],
    data() {
      return {
          drawFileList: false,
          context: 100,
          outputFormat: "side-by-side",
          title: "修正对比",
          oldContent: "",
          content: "",
          dialogCodeDiffVisible: this.visible,
          fullscreenLoading: this.loaded,
      }
    },
    created() {
        this.dialogCodeDiffVisible = this.visible;
        var tmpOldContent = this.$commonUtil.htmlToMarkdown(this.oldContentData);
        this.oldContent = tmpOldContent;
        var tmpContent = this.$commonUtil.htmlToMarkdown(this.contentData);
        this.content = tmpContent;
        this.fullscreenLoading = false;
    },
    components: {
        CodeDiff,
    },
    watch: {
      visible: function() {
         this.dialogCodeDiffVisible = this.visible;
      },
      oldContentData: function() {
          this.oldContent = this.oldContentData;
      },
      contentData: function() {
          this.content = this.contentData;
      },
    },
    computed:{

    },
    mounted() {
        console.log("节点数", document.getElementsByClassName("d2h-code-side-line"))
        document.getElementsByClassName("d2h-code-side-line").innerHTML = "";
        this.$emit("getLoading",true)
    },
    methods: {
        cancel: function () {
            this.$emit("beforeClose", "");
        },
        beforeClose(done) {
            this.$emit("beforeClose", "");
            done();
        },
    }
  }
</script>

<style>

    .d2h-info {
        display: none;
    }

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
    top: 20px;
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


