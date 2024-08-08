<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container" style="margin: 10px 0 0 0;">
      <el-button class= "button" type="success"  @click="handInit('BLOG')" icon="el-icon-refresh" v-permission="'/search/batchInitElasticIndex'">初始化博客索引</el-button>
      <el-button class= "button" type="success"  @click="handInit('Question')" icon="el-icon-refresh" v-permission="'/search/batchInitElasticIndex'">初始化问答索引</el-button>
        <el-button class= "button" type="success"  @click="handInit('MEDIA')" icon="el-icon-refresh" v-permission="'/search/batchInitElasticIndex'">初始化课程索引</el-button>
      <el-button class= "button" type="success"  @click="handInit('MOMENT')" icon="el-icon-refresh" v-permission="'/search/batchInitElasticIndex'">初始化动态索引</el-button>
      <el-button class= "button" type="success"  @click="handInit('PROBLEM')" icon="el-icon-refresh" v-permission="'/search/batchInitElasticIndex'">初始化面经索引</el-button>
        <el-button class= "button" type="success"  @click="handInit('RESOURCE')" icon="el-icon-refresh" v-permission="'/search/batchInitElasticIndex'">初始化资源索引</el-button>
      <el-button class= "button" type="danger"  @click="handleCleanIndex" icon="el-icon-delete" v-permission="'/search/batchInitElasticIndex'">清空全部索引</el-button>
    </div>
    <iframe style="margin: 10px 0 0 0;" :style="{'height': minHeight+ 'px'}" id="myIframe" :src="elasticSearchServiceUrl" width="100%" ></iframe>
  </div>
</template>

<script>
  import { batchInitElasticIndex } from "@/api/searchIndex";
  import {Loading} from "element-ui";
  import {cleanIndex} from "../../api/searchIndex";
  export default {
    data() {
      return {
        elasticSearchServiceUrl:  process.env.ELASTIC_SEARCH,
        minHeight: window.screen.height - 310,
      };
    },
    mounted() {
      let iframe = document.getElementById("myIframe")
      let uploadLoading = Loading.service({
        lock: true,
        text: '正在努力加载中……'
      })
      if (iframe.attachEvent) {
        iframe.attachEvent("onload", function () {
          uploadLoading.close();
        });
      } else {
        iframe.onload = function () {
          uploadLoading.close();
        };
      }
    },
    created() {

    },
    methods: {
      handInit: function(resourceType) {
        this.$confirm("此操作将初始化索引, 是否继续?", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        })
          .then(() => {
              let params = new URLSearchParams()
              params.append("resourceType", resourceType)
              batchInitElasticIndex(params).then(response => {
              if(response.code === this.$ECode.SUCCESS) {
                this.$commonUtil.message.success(response.message);
              } else {
                this.$commonUtil.message.error(response.message);
              }
            })
          })
          .catch(() => {
            this.$commonUtil.message.info("已取消");
          });
      },
      handleCleanIndex: function() {
        this.$confirm("此操作将清空ES索引, 是否继续?", "提示", {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning"
        })
            .then(() => {
                cleanIndex().then(response => {
                    if(response.code === this.$ECode.SUCCESS) {
                        this.$commonUtil.message.success(response.message);
                    } else {
                        this.$commonUtil.message.error(response.message);
                    }
                })
            })
            .catch(() => {
                this.$commonUtil.message.info("已取消");
            });
        },

    }
  };
</script>
