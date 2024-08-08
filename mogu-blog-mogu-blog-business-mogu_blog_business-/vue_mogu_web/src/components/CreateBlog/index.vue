<template>

  <div>
    <!-- 添加或修改对话框 -->
    <el-dialog
      :modal-append-to-body="false"
      :title="title"
      :visible.sync="dialogFormVisible"
      :before-close="beforeClose"
      fullscreen
    >
      <el-form :model="form" :rules="rules" ref="form">
        <el-row>
          <el-col :span="16">
            <el-form-item label="标题" :label-width="formLabelWidth" prop="title">
              <el-input v-model="form.title" auto-complete="off" @input="contentChange"></el-input>
            </el-form-item>

            <el-form-item label="简介" :label-width="formLabelWidth">
              <el-input v-model="form.summary" auto-complete="off" @input="contentChange"></el-input>
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item label="标题图" :label-width="formLabelWidth">
              <div class="imgBody" v-if="form.photoList">
                <i
                  class="el-icon-error inputClass"
                  v-show="icon"
                  @click="deletePhoto()"
                  @mouseover="icon = true"
                ></i>
                <img
                  @mouseover="icon = true"
                  @mouseout="icon = false"
                  v-bind:src="form.photoList[0]"
                  style="display:inline; width: 195px;height: 105px;"
                >
              </div>

              <div v-else class="uploadImgBody" @click="checkPhoto">
                <i class="el-icon-plus avatar-uploader-icon"></i>
              </div>

            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :lg="5" :sm="8" :xs="8">
            <el-form-item label="分类" :label-width="formLabelWidth" prop="blogSortUid">
              <el-select
                @input="contentChange"
                v-model="form.blogSortUid"
                size="small"
                placeholder="请选择"
                style="width:150px"
              >
                <el-option
                  v-for="item in blogSortData"
                  :key="item.uid"
                  :label="item.sortName"
                  :value="item.uid"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :lg="5" :sm="8" :xs="8">
            <el-form-item label="标签" label-width="80px" prop="tagUid">
              <el-select
                @input="contentChange"
                v-model="tagValue"
                multiple
                :multiple-limit="3"
                size="small"
                placeholder="请选择"
                style="width:250px"
                filterable
              >
                <el-option
                  v-for="item in tagData"
                  :key="item.uid"
                  :label="item.content"
                  :value="item.uid"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :lg="7" :sm="8" :xs="8">
            <el-form-item label="是否原创" :label-width="formLabelWidth" prop="isOriginal">
              <el-radio-group v-model="form.isOriginal" size="small">
                <el-radio v-for="item in blogOriginalDictList" :key="item.uid" :label="item.dictValue" border>{{item.dictLabel}}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>

<!--          <el-col :span="5">-->
<!--            <el-form-item :label-width="formLabelWidth" prop="isPublish">-->
<!--              <template slot="label">-->
<!--                是否发布-->
<!--                <el-popover-->
<!--                  placement="top-start"-->
<!--                  width="200"-->
<!--                  trigger="hover"-->
<!--                  content="选中发布后,当后台审核通过后,文章将会直接展示给大家查阅">-->
<!--                  <i slot="reference" style="cursor: pointer;margin-left: 2px" class="el-icon-question"></i>-->
<!--                </el-popover>-->
<!--              </template>-->
<!--              <el-radio-group v-model="form.isPublish" size="small">-->
<!--                <el-radio v-for="item in blogPublishDictList" :key="item.uid" :label="item.dictValue" border>{{item.dictLabel}}</el-radio>-->
<!--              </el-radio-group>-->
<!--            </el-form-item>-->
<!--          </el-col>-->
        </el-row>

        <el-form-item label="作者" :label-width="formLabelWidth" v-if="form.isOriginal==0" prop="author">
          <el-input v-model="form.author" auto-complete="off"></el-input>
        </el-form-item>

        <el-form-item label="文章出处" :label-width="formLabelWidth" v-if="form.isOriginal==0">
          <el-input v-model="form.articlesPart" auto-complete="off"></el-input>
        </el-form-item>

        <el-form-item label="外链" :label-width="formLabelWidth" v-if="form.type == 1" prop="outsideLink">
          <el-input v-model="form.outsideLink" auto-complete="off"></el-input>
        </el-form-item>


        <el-form-item label="内容" :label-width="formLabelWidth" prop="content">
<!--          <MarkdownEditor ref="editor" v-if="editorModel == '1'" @contentChange="contentChange" :content="form.content"  :height="560"></MarkdownEditor>-->
          <mavon ref="editor" v-if="editorModel === '1'" :context="form.content" @contentChange= "contentChange" :height="560"></mavon>
          <CKEditor ref="editor" v-else :content="form.content" @contentChange="contentChange" :height="465"></CKEditor>
        </el-form-item>

      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="beforeClose">取 消</el-button>
        <el-button type="info" @click="submitDraft">保存草稿</el-button>
        <el-button type="primary" @click="submitForm">提交审核</el-button>
      </span>

    </el-dialog>

    <!--头像裁剪-->
    <avatar-cropper
      v-show="imagecropperShow"
      :key="imagecropperKey"
      :width="400"
      :height="200"
      :url="url"
      lang-type="zh"
      :noCircle="true"
      :noSquare="true"
      @close="close"
      @crop-upload-success="cropSuccess"
    />

    <PhotoGallery v-if="showPhotoGallery" @uploadCallback="uploadCallback" @close="photoGalleryClose" @chooseCallback="chooseCallback"></PhotoGallery>

  </div>

</template>

<script>
import CKEditor from "../CKEditor";
import MarkdownEditor from "../MarkdownEditor";
import AvatarCropper from '@/components/AvatarCropper'
import {getListByDictTypeList} from "@/api/sysDictData"
import {addBlog, editBlog, getBlogSortList, getBlogTagList} from "@/api/createBlog"
import {formatData} from "@/utils/webUtils";
import Mavon from "../Mavon";
import PhotoGallery from "../PhotoGallery";
export default {
  props: ["visible", "isEdit", "formData", "editorModel"],
  created() {
    this.blogSortList()
    this.tagList()
    window.addEventListener('beforeunload', e => this.beforeUnload(e))
  },
  components: {
    CKEditor,
    MarkdownEditor,
    AvatarCropper,
    Mavon,
    PhotoGallery,
  },
  watch: {
    visible: function() {
      this.dialogFormVisible = this.visible;
    },
  },
  mounted() {
    this.getDictList()
    console.log("编辑器模式", this.editorModel)
  },
  data() {
    return {
      form: {},
      icon: false,
      isSubmitOp: false,
      formLabelWidth: "120px",
      lineLabelWidth: "120px", //一行的间隔数
      maxLineLabelWidth: "100px",
      blogOriginalDictList: [], //存储区域字典
      blogPublishDictList:[],//是否字典
      openDictList: [], // 是否启动字典
      blogOriginalDefault: null, //博客原创默认值
      blogPublishDefault: null, //博客发布默认值
      openDefault: null, // 是否开启评论默认值
      blogSortData: [],
      tagData: [], //标签数据
      tagValue: [], //保存选中标签id(编辑时)
      dialogFormVisible: this.visible,
      title: "写文章",
      imagecropperShow: false,
      imagecropperKey: 0,
      url: process.env.PICTURE_API + "/file/cropperPicture",
      isChange: false, // 表单内容是否改变
      changeCount: 0, // 改变计数器
      showPhotoGallery: false,
      rules: {
        title: [
          {required: true, message: '标题不能为空', trigger: 'blur'},
          {min: 2, max: 50, message: '长度在2到50个字符'},
        ],
        blogSortUid: [
          {required: true, message: '分类不能为空', trigger: 'blur'}
        ],
        tagUid: [
          {required: true, message: '标签不能为空', trigger: 'blur'}
        ],
        isOriginal: [
          {required: true, message: '原创字段不能为空', trigger: 'blur'},
          {pattern: /^[0-9]\d*$/, message: '原创字段只能为自然数'},
        ],
        isPublish: [
          {required: true, message: '发布字段不能为空', trigger: 'blur'},
          {pattern: /^[0-9]\d*$/, message: '发布字段只能为自然数'},
        ],
        content: [
          {required: true, message: '内容不能为空', trigger: 'blur'}
        ],
        outsideLink: [
          {required: true, message: '外链地址不能为空', trigger: 'blur'},
          {pattern:  /^((https|http|ftp|rtsp|mms)?:\/\/)[^\s]+/, message: '请输入有效的URL'},
        ],
      }
    }
  },
  methods: {
    /**
     * 字典查询
     */
    getDictList: function () {
      // 判断是否处于编辑模式
      let that = this
      let dictTypeList =  [ 'sys_original_status', 'sys_normal_disable','sys_publish_status']
      getListByDictTypeList(dictTypeList).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let dictMap = response.data;
          console.log(dictMap.sys_publish_status.list)
          this.blogOriginalDictList = dictMap.sys_original_status.list
          this.blogPublishDictList = dictMap.sys_publish_status.list
          this.openDictList = dictMap.sys_normal_disable.list

          if(dictMap.sys_original_status.defaultValue) {
            this.blogOriginalDefault = dictMap.sys_original_status.defaultValue;
          }
          if(dictMap.sys_publish_status.defaultValue) {
            this.blogPublishDefault = dictMap.sys_publish_status.defaultValue;
          }
          if(dictMap.sys_normal_disable.defaultValue) {
            this.openDefault = dictMap.sys_normal_disable.defaultValue;
          }

          if(this.isEdit) {
            this.form = this.formData
            setTimeout(()=>{
              that.$refs.editor.setData(that.form.content); //设置富文本内容
            },100)
            that.tagValue = [];
            if (this.form.tagList) {
              let json = this.form.tagList;
              for (let i = 0, l = json.length; i < l; i++) {
                if (json[i] != null) {
                  that.tagValue.push(json[i]["uid"]);
                }
              }
            }
          } else {
            let tempForm = null;
            console.log("判断缓存中是否有内容", window.localStorage)
            if(window.localStorage && window.localStorage.getItem("form")) {
              tempForm = JSON.parse(window.localStorage.getItem("form"));
            }
            this.$refs.editor.initData(); //设置富文本内容
            if (tempForm != null && tempForm.title != null && tempForm.title != "") {
              this.$confirm("还有上次未完成的文章编辑，是否继续编辑?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
              })
                .then(() => {
                  that.dialogFormVisible = true;
                  that.tagValue = [];
                  that.form = tempForm;
                  let tagValue = that.form.tagUid.split(",");
                  for (let a = 0; a < tagValue.length; a++) {
                    if (tagValue[a] != null && tagValue[a] != "") {
                      that.tagValue.push(tagValue[a]);
                    }
                  }
                  that.$nextTick(() => {
                    //DOM现在更新了
                    that.$refs.editor.setData(that.form.content); //设置富文本内容
                  });
                  if(that.form.uid) {
                    that.title = "编辑博客";
                    that.isEditForm = true;
                  } else {
                    that.title = "新增博客";
                    that.isEditForm = false;
                  }
                })
                .catch(() => {
                  window.localStorage.clear()
                  that.dialogFormVisible = true;
                  that.form = that.getFormObject();
                  that.$nextTick(() => {
                    //DOM现在更新了
                    that.$refs.editor.initData(); //设置富文本内容
                  });
                  that.tagValue = [];
                  that.isEditForm = false;
                  that.title = "新增博客";
                });
            } else {
              that.dialogFormVisible = true;
              that.form = that.getFormObject();
              that.$nextTick(() => {
                //初始化内容
                that.$refs.editor.initData();
              });
              that.tagValue = [];
              that.isEditForm = false;
            }
          }
        }
      });
    },
    getFormObject: function() {
      let formObject = {
        uid: null,
        title: null,
        summary: null,
        content: null,
        tagUid: null,
        fileUid: null,
        isOriginal: this.blogOriginalDefault, //是否原创
        isPublish: this.blogPublishDefault,
        author: null, //作者
        articlesPart: null //文章出处，默认蘑菇博客
      };
      return formObject;
    },
    photoGalleryClose() {
      this.showPhotoGallery = false
    },
    uploadCallback() {
      this.showPhotoGallery = false
      this.imagecropperShow = true
    },
    beforeUnload: function (e) {
      console.log("刷新页面的回调", this.isSubmitOp)
      if (this.isSubmitOp) {
        return
      }
      e = e || window.event;
      // 兼容IE8和Firefox 4之前的版本
      if (e) {
        e.returnValue = '关闭提示';
      }
      // Chrome, Safari, Firefox 4+, Opera 12+ , IE 9+
      return '关闭提示';
    },
    submitDraft: function () {
      this.form.isPublish = "0"
      this.submit();
    },
    submitForm: function () {
      this.form.isPublish = "1"
      this.submit();
    },
    submit: function() {
      let url = window.location.href
      if(this.tagValue.length <= 0) {
        this.$commonUtil.message.error("标签不能为空!")
        return;
      }
      this.isSubmitOp = true
      this.form.content = this.$refs.editor.getData();
      this.form.tagUid = this.tagValue.join(",");
      this.$refs.form.validate((valid) => {
        if(!valid) {
          console.log("校验出错")
        } else {
          if (this.isEdit) {
            editBlog(this.form).then(response => {
              if (response.code == this.$ECode.SUCCESS) {
                this.$commonUtil.message.success(response.message)
                // 清空缓存
                window.localStorage.clear()
                // this.$emit("beforeClose", "");
                setTimeout(()=> {
                  // 跳转到我的文章
                  window.location.href = this.$commonUtil.setUrlParams("action", "MyArticle")
                }, 500)
              } else {
                this.$commonUtil.message.error(response.message)
              }
            });

          } else {
            addBlog(this.form).then(response => {
              if (response.code == this.$ECode.SUCCESS) {
                this.$commonUtil.message.success(response.message)
                // 清空缓存
                window.localStorage.clear()
                setTimeout(()=> {
                  // 跳转到我的文章
                  window.location.href = this.$commonUtil.setUrlParams("action", "MyArticle")
                }, 500)
              } else {
                this.$commonUtil.message.error(response.message)
              }
            });
          }
        }
      })
    },
    blogSortList: function() {
      let blogSortParams = {};
      blogSortParams.pageSize = 100;
      blogSortParams.currentPage = 1;
      getBlogSortList(blogSortParams).then(response => {
        if(response.code == this.$ECode.SUCCESS) {
          this.blogSortData = response.data.records;
          this.sortOptions = response.data.records;
        }
      });
    },
    tagList: function() {
      let tagParams = {};
      tagParams.pageSize = 100;
      tagParams.currentPage = 1;
      getBlogTagList(tagParams).then(response => {
        this.tagData = response.data.records;
        this.tagOptions = response.data.records;
      });
    },
    checkPhoto: function () {
      // this.imagecropperShow = true
      this.showPhotoGallery = true
    },
    // 内容改变，触发监听
    contentChange: function() {
      console.log("内容改变")
      let that = this;
      if(that.changeCount > 1) {
        that.isChange = true;
        that.form.content = that.$refs.editor.getData(); //获取文本中的内容
        that.form.tagUid = that.tagValue.join(",");
        // 将内容设置到 WebStorage中
        console.log("备份的内容", that.form)
        // 提交的时候，不允许备份了
        if(!this.isSubmitOp) {
          window.localStorage.setItem("form", JSON.stringify(that.form))
        }
      }
      this.changeCount = this.changeCount + 1;
    },
    // 关闭时的回调
    beforeClose(done) {
      this.$confirm("是否关闭博客编辑窗口", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
      .then(() => {
        this.isChange = false;
        this.changeCount = 0
        //取消时，开始状态
        this.$emit("beforeClose", "");
        done();
      })
      .catch(() => {
        // this.$commonUtil.message.info("已取消")
      });
    },
    cancel: function () {
      this.$emit("beforeClose", "");
    },
    // 头像裁剪关闭回调
    close() {
      this.imagecropperShow = false
    },
    cropSuccess(resData) {
      this.imagecropperShow = false
      this.imagecropperKey = this.imagecropperKey + 1
      console.log("裁剪后的数据", resData)
      let array = new Array()
      array.push(resData.url)
      this.form.photoList = array
      this.form.fileUid = resData.uid
      this.contentChange()
    },
    chooseCallback(file) {
      console.log("回调", file)
      let array = new Array()
      array.push(file.pictureUrl)
      this.form.photoList = array
      this.form.fileUid = file.uid
      this.contentChange()
      this.showPhotoGallery = false
    },
    deletePhoto: function() {
      console.log("删除")
      let bakFrom = this.form
      this.form = {}
      bakFrom.photoList = null;
      bakFrom.fileUid = "";
      this.form = bakFrom
    },

  }
}
</script>

<style scoped>
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  margin: 0, 0, 0, 10px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width:  195px;
  height: 105px;
  line-height: 105px;
  text-align: center;
}
.imgBody {
  width:  195px;
  height: 105px;
  border: solid 2px #ffffff;
  float: left;
  position: relative;
}
.uploadImgBody {
  margin-left: 5px;
  width:  195px;
  height: 105px;
  border: dashed 1px #c0c0c0;
  float: left;
  position: relative;
}
.uploadImgBody :hover {
  border: dashed 1px #00ccff;
}
.inputClass {
  position: absolute;
}
</style>
