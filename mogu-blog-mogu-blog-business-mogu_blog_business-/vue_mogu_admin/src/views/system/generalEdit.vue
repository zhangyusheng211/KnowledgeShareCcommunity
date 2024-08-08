<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
   <div class="filter-container" style="margin: 10px 0 10px 0;" v-permission="'/generalEdit/getGeneralEditList'">

      <el-form :inline="true" v-show="showSearch" label-width="68px" style="margin-bottom: 8px;">
        <el-input
          clearable
          class="filter-item"
          style="width: 150px;"
          v-model="queryParams.keyword"
          placeholder="请输入问题内容"
          size="small"
          @keyup.enter.native="handleFind"
        ></el-input>

        <el-select v-model="queryParams.auditStatus" clearable placeholder="是否审核" size="small" style="width:110px">
          <el-option
            v-for="item in auditDictList"
            :key="item.uid"
            :label="item.dictLabel"
            :value="item.dictValue"
          ></el-option>
        </el-select>

        <el-button style="margin-left: 10px;" class="filter-item" type="primary" icon="el-icon-search" @click="handleFind" size="small" v-permission="'/generalEdit/getGeneralEditList'">查找</el-button>

      </el-form>

      <el-row :gutter="10" style="margin-bottom: 8px;">

        <el-col :span="1.5">
          <el-button class="filter-item" type="danger" @click="handleDeleteBatch" icon="el-icon-delete" size="small" v-permission="'/generalEdit/deleteBatch'">删除选中</el-button>
        </el-col>

        <right-toolbar :showSearch.sync="showSearch" @queryTable="resetBlogList"></right-toolbar>
      </el-row>

    </div>

    <el-table :data="tableData"
              ref="articleTable"
              style="width: 100% "
              @selection-change="handleSelectionChange"
              @sort-change="changeSort"
              :default-sort="{prop: 'createTime', order: 'descending'}">
      <el-table-column type="selection"></el-table-column>

      <el-table-column label="序号" width="60" align="center">
        <template slot-scope="scope">
          <span>{{scope.$index + 1}}</span>
        </template>
      </el-table-column>


        <el-table-column label="用户" width="200" align="center">
            <template slot-scope="scope">
                <UserAvatar :user="scope.row.user"></UserAvatar>
            </template>
        </el-table-column>

      <el-table-column label="标题" width="200" align="left">
         <template slot-scope="scope">
             <span style="margin-top: -10px" v-html="scope.row.summary" v-if="scope.row.summary">{{scope.row.summary}}</span>
         </template>
      </el-table-column>

      <el-table-column label="类型" width="100" align="center" prop="bizType" sortable="custom" :sort-by="['bizType']">
        <template slot-scope="scope">
          <template>
            <el-tag v-for="item in bizTypeDictList" :key="item.uid" :type="item.listClass" v-if="scope.row.bizType == item.dictValue">{{item.dictLabel}}</el-tag>
          </template>
        </template>
      </el-table-column>

      <el-table-column label="修改原因" width="250" align="left">
        <template slot-scope="scope">
          <span style="margin-top: -10px" v-if="scope.row.reason">{{scope.row.reason}}</span>
        </template>
      </el-table-column>

      <el-table-column label="审批状态" width="100" align="center" prop="level" sortable="custom" :sort-by="['auditStatus']">
        <template slot-scope="scope">

          <el-tag v-for="item in auditStatusDictList" :key="item.uid" v-if="scope.row.auditStatus == item.dictValue" :type="item.listClass">
            <el-tooltip v-if="scope.row.auditStatus == 1"  class="item" placement="top">
              <div slot="content">{{scope.row.rejectReason}}</div>
              <div>{{item.dictLabel}}</div>
            </el-tooltip>
            <span v-else>
              {{item.dictLabel}}
            </span>
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="审核人" width="90" align="center" prop="auditName" >
        <template slot-scope="scope">
          <span>{{ scope.row.auditName }}</span>
        </template>
      </el-table-column>

      <el-table-column label="审核时间" width="160" align="center" prop="auditTime" sortable="custom" :sort-by="['auditTime']">
        <template slot-scope="scope">
          <span>{{ scope.row.auditTime }}</span>
        </template>
      </el-table-column>

      <el-table-column label="创建时间" width="160" align="center" prop="createTime" sortable="custom" :sort-orders="['ascending', 'descending']">
        <template slot-scope="scope">
          <span>{{ scope.row.createTime }}</span>
        </template>
      </el-table-column>

      <el-table-column label="操作" fixed="right" min-width="250">
        <template slot-scope="scope">
          <el-button v-if="(scope.row.auditStatus == 0 || scope.row.auditStatus == 1)" @click="handleAudit(scope.row)" type="success" size="mini" v-permission="'/generalEdit/audit'">审核</el-button>
          <el-button @click="openCodeDiff(scope.row.oldContent,scope.row.content)" type ="warning" size="mini">对比</el-button>
          <el-button @click="handleDelete(scope.row)" type="danger" size="mini" v-permission="'/generalEdit/deleteBatch'">删除</el-button>
        </template>
      </el-table-column>

    </el-table>

    <!--分页-->
    <div class="block">
      <el-pagination
        @current-change="handleCurrentChange"
        :current-page.sync="currentPage"
        :page-size="pageSize"
        layout="total, prev, pager, next, jumper"
        :total="total"
      ></el-pagination>
    </div>


    <!--  审批弹出框  -->
    <el-dialog
      title="请选择审批结果"
      :visible.sync="auditDialogVisible"
      width="30%"
      center>
      <div style="text-align: center">
        <el-form>
          <el-form-item label="审批状态" :label-width="lineLabelWidth" prop="auditStatus">
            <el-radio-group v-model="auditForm.auditStatus" size="small">
              <el-radio v-for="item in auditStatusDictList" v-if="item.dictValue != 0"  :key="item.uid" :label="item.dictValue" border>{{item.dictLabel}}</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="拒绝原因" :label-width="lineLabelWidth" v-if="auditForm.auditStatus == 1" prop="rejectReason">
            <el-input v-model="auditForm.rejectReason" maxlength="50"  placeholder="拒绝的原因" clearable></el-input>
          </el-form-item>
        </el-form>


      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="auditDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitAudit">确 定</el-button>
      </span>
    </el-dialog>

    <CodeDiff class="item-editor" v-if="dialogCodeVisible"  :visible="dialogCodeVisible" :loaded="fullscreenLoading" :oldContentData="oldContentData" :contentData="contentData" @beforeClose="beforeClose" @getLoading="getLoading" ></CodeDiff>
  </div>
</template>

<script>
import { getGeneralEditList ,auditGeneralEdit,deleteBatchGeneralEdit} from "@/api/generalEdit";
import { getSystemConfig} from "@/api/systemConfig";
import { getCookie } from "@/utils/cookieUtils";
import {getListByDictTypeList} from "@/api/sysDictData"
let querystring = require("querystring");
import { Loading } from 'element-ui';
import CodeDiff from "../../components/CodeDiff";
import UserAvatar from "../../components/UserAvatar"

export default {
  components: {
      CodeDiff,
      UserAvatar,
  },
  data() {
    return {
      fullscreen: null, //codeDiff dialig加载中
      fullscreenLoading:false,
      dialogCodeVisible:false,
      contentData: "",
      oldContentData: "",
      systemConfig: {}, // 系统配置
      auditForm: {},
      auditDialogVisible: false, // 审批结果
      auditStatusDictList: [], // 审批状态字典
      bizTypeDictList: [],
      orderByDescColumn: "", // 降序字段
      orderByAscColumn: "", // 升序字段
      tableData: [], //问题数据
      multipleSelection: [], //多选，用于批量删除
      auditDictList: [], //是否通过审核
      showSearch: null, // 显示搜索条件
      currentPage: 1,
      pageSize: 10,
      total: 0, //总数量
      queryParams:{
        keyword: "",
        auditStatus: "", // 是否审核通过搜索
      }, // 搜索条件
      loading: false, //搜索框加载状态
      uploadLoading: null, //文件上传loading
      title: "增加问题",
      icon: false, //控制删除图标的显示
      lineLabelWidth: "120px", //一行的间隔数
    };
  },
  created() {
    // 获取系统配置
    this.getSystemConfigList()
    // 获取字典
    this.getDictList()
    // 获取修改列表
    this.generalEditList()
    // 判断是否需要展开条件查询
    this.getShowSearch()
  },
  methods: {
    getLoading: function (data) {
        this.closeLoading();
    },
    openCodeDiff: function (oldContent, content) {
        this.fullscreenLoading = true
        this.uploadLoading = Loading.service({
            lock: true,
            text: '正在努力加载中……'
        })
        setTimeout(()=> {
            this.oldContentData = oldContent
            this.contentData = content
            this.dialogCodeVisible = true
        }, 300)

    },
    // 判断是否需要展开条件查询
    getShowSearch: function () {
      let showSearch = getCookie("showSearch")
      if(showSearch == "false"){ //此时的hasAuth是true
        this.showSearch = false
      } else {
        this.showSearch = true
      }
    },
    handleCurrentChange: function(val) {
      this.currentPage = val;
      this.generalEditList();
    },
    handleDelete: function(row) {
      let that = this;
      this.$confirm("此操作将把本条数据删除, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          let params = {};
          params.uid = row.uid;
          let data = [params]
          deleteBatchGeneralEdit(data).then(response => {
            that.$commonUtil.message.success(response.message)
            that.generalEditList();
          });
        })
        .catch(() => {
          that.$commonUtil.message.info("已取消删除")
        });
    },
    // 点击审核
    handleAudit: function (row) {
      this.auditForm = this.getAuditFormObject();
      this.auditForm.uid = row.uid
      this.auditForm.bizUid = row.bizUid
      this.auditForm.bizType = row.bizType
      this.auditForm.content = row.content
      this.auditDialogVisible = true
    },
    getAuditFormObject: function () {
      let auditForm = {
        uid: "",
        bizType: "",
        bizUid: "",
        auditStatus: this.auditStatusDefault, // 审批状态默认值
        rejectReason: null, // 审批拒绝原因
        content: "",
      }
      return auditForm
    },
    submitAudit: function () {
      let params = {}
      params.uid = this.auditForm.uid
      params.bizUid = this.auditForm.bizUid
      params.bizType = this.auditForm.bizType
      params.auditStatus = this.auditForm.auditStatus
      params.rejectReason = this.auditForm.rejectReason
      params.content = this.auditForm.content
      auditGeneralEdit(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.$commonUtil.message.success(response.message)
          this.auditDialogVisible = false
          this.generalEditList();
        } else {
          this.$commonUtil.message.error(response.message)
        }
      });
    },
    /**
     * 字典查询
     */
    getDictList: function () {

      let dictTypeList =  ['sys_audit_status', 'sys_resource_type']
      getListByDictTypeList(dictTypeList).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let dictMap = response.data;
          this.auditDictList = dictMap.sys_audit_status.list
          this.bizTypeDictList = dictMap.sys_resource_type.list
          this.auditStatusDictList = dictMap.sys_audit_status.list
        }
      });
    },
    // 从后台获取数据,重新排序
    changeSort (val) {
      // 根据当前排序重新获取后台数据,一般后台会需要一个排序的参数
      if(val.order == "ascending") {
        this.orderByAscColumn = val.prop
        this.orderByDescColumn = ""
      } else {
        this.orderByAscColumn = ""
        this.orderByDescColumn = val.prop
      }
      this.generalEditList()
    },
    // 改变多选
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    resetBlogList: function (){
      this.queryParams = {}
      this.generalEditList();
    },
    handleDeleteBatch: function(row) {
      let that = this;
      if(that.multipleSelection.length <= 0 ) {
        that.$commonUtil.message.error("请先选中需要删除的问题")
        return;
      }
      this.$confirm("此操作将把选中问题删除, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          deleteBatchGeneralEdit(that.multipleSelection).then(response => {
            if (response.code == this.$ECode.SUCCESS) {
              that.$commonUtil.message.success(response.message)
              that.generalEditList();
            } else {
              that.$commonUtil.message.error(response.message)
            }
          });
        })
        .catch(() => {
          that.$commonUtil.message.info("已取消删除")
        });
    },
    handleFind: function() {
      this.currentPage = 1
      this.generalEditList();
    },
    generalEditList: function() {
      let params = {};
      params.keyword = this.queryParams.keyword;
      params.auditStatus = this.queryParams.auditStatus;;
      params.currentPage = this.currentPage;
      params.pageSize = this.pageSize;
      params.orderByDescColumn = this.orderByDescColumn
      params.orderByAscColumn = this.orderByAscColumn
      getGeneralEditList(params).then(response => {
        if(response.code == this.$ECode.SUCCESS) {
          let tableData = response.data.records;
          this.currentPage = response.data.current;
          this.pageSize = response.data.size;
          this.total = response.data.total;
          this.tableData = tableData
        }
      });
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
    // 获取系统配置
    getSystemConfigList: function() {
      getSystemConfig().then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          if (response.data) {
            this.systemConfig = response.data;
          }
        }
      });
    },
    beforeClose: function () {
        this.dialogCodeVisible = false
    },
  }
};
</script>
<style scoped>
.oldContentStyle {
    display: block;
    overflow:hidden;
    text-overflow:ellipsis;
    display:-webkit-box;
    -webkit-line-clamp:2;    /*定义的数字为显示的行数*/
    -webkit-box-orient:vertical;
}
.contentStyle {
    display: block;
    overflow:hidden;
    text-overflow:ellipsis;
    display:-webkit-box;
    -webkit-line-clamp:2;    /*定义的数字为显示的行数*/
    -webkit-box-orient:vertical;
}
</style>
