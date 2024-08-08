<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container" style="margin: 10px 0 10px 0;" v-permission="'/tag/getList'">
      <el-input
        clearable
        @keyup.enter.native="handleFind"
        class="filter-item"
        style="width: 200px;"
        v-model="queryParams.keyword"
        placeholder="请输入标签名"
        size="small"
      ></el-input>

      <el-select v-model="queryParams.tagLevel" clearable placeholder="标签等级" size="small" style="width:150px">
        <el-option
          v-for="item in tagLevelDictList"
          :key="item.uid"
          :label="item.dictLabel"
          :value="item.dictValue"
        ></el-option>
      </el-select>

      <el-select v-model="queryParams.tagType" clearable placeholder="标签类型" size="small" style="width:150px">
        <el-option
          v-for="item in problemTagTypeList"
          :key="item.uid"
          :label="item.dictLabel"
          :value="item.dictValue"
        ></el-option>
      </el-select>

      <el-select v-model="queryParams.isSelection" clearable placeholder="是否精选" size="small" style="width:120px">
        <el-option
          v-for="item in yesNoDictList"
          :key="item.uid"
          :label="item.dictLabel"
          :value="item.dictValue"
        ></el-option>
      </el-select>

      <el-select v-model="queryParams.isPublish" clearable placeholder="是否上架" size="small" style="width:120px">
        <el-option
          v-for="item in publishDictList"
          :key="item.uid"
          :label="item.dictLabel"
          :value="item.dictValue"
        ></el-option>
      </el-select>

      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFind" size="small" v-permission="'/problemTag/getList'">查找</el-button>
      <el-button class="filter-item" type="primary" @click="handleAdd" icon="el-icon-edit" size="small" v-permission="'/problemTag/add'">添加标签</el-button>
      <el-button class="filter-item" type="danger" @click="handleDeleteBatch" icon="el-icon-delete" size="small" v-permission="'/problemTag/deleteBatch'">删除选中</el-button>
    </div>

    <el-table :data="tableData"
              style="width: 100%"
              @selection-change="handleSelectionChange"
              @sort-change="changeSort"
              :default-sort="{prop: 'sort', order: 'descending'}">
      <el-table-column type="selection"></el-table-column>

      <el-table-column label="序号" width="60" align="center">
        <template slot-scope="scope">
          <span>{{scope.$index + 1}}</span>
        </template>
      </el-table-column>

      <el-table-column label="标签名" width="100" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.name }}</span>
        </template>
      </el-table-column>

        <el-table-column label="标签简介" width="200" align="center">
            <template slot-scope="scope">
                <span>{{ scope.row.summary }}</span>
            </template>
        </el-table-column>

      <el-table-column label="标签类型" width="100" align="center">
        <template slot-scope="scope">
          <el-tag v-for="item in problemTagTypeList" :key="item.uid" v-if="scope.row.tagType == item.dictValue" :type="item.listClass">{{item.dictLabel}}</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="标签等级" width="100" align="center" prop="level" sortable="custom" :sort-by="['tagLevel']">
        <template slot-scope="scope">
          <el-tag v-for="item in tagLevelDictList" :key="item.uid" v-if="scope.row.tagLevel == item.dictValue" :type="item.listClass">{{item.dictLabel}}</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="父标签" width="100" align="center" prop="level" sortable="custom" :sort-by="['level']">
        <template slot-scope="scope">
          <el-tag v-for="item in menuOptions" :key="item.uid" v-if="scope.row.parentUid == item.uid" >{{item.name}}</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="图标" width="50" align="center">
        <template slot-scope="scope">
          <i :class="scope.row.icon" />
        </template>
      </el-table-column>

      <el-table-column label="是否跳转外链" width="120" align="center">
        <template slot-scope="scope">
          <el-tag v-for="item in jumpExternalDictList" :key="item.uid" v-if="scope.row.isJumpExternalUrl == item.dictValue" :type="item.listClass">{{item.dictLabel}}</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="链接" width="120" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.url }}</span>
        </template>
      </el-table-column>

      <el-table-column label="精选" width="100" align="center" prop="openComment" sortable="custom" :sort-by="['openComment']">
        <template slot-scope="scope">
          <template>
            <el-switch v-model="scope.row.selectionStatus" active-color="#F5DEB3" @change="handChange(scope.row)"></el-switch>
          </template>
        </template>
      </el-table-column>

      <el-table-column label="上架" width="100" align="center" prop="openComment" sortable="custom" :sort-by="['openComment']">
        <template slot-scope="scope">
          <template>
            <el-switch v-model="scope.row.publishStatus" active-color="#13ce66" inactive-color="#ff4949" @change="handChange(scope.row)"></el-switch>
          </template>
        </template>
      </el-table-column>

      <el-table-column label="点击数" width="100" align="center" prop="clickCount" sortable="custom" :sort-by="['clickCount']">
        <template slot-scope="scope">
          <span>{{ scope.row.clickCount }}</span>
        </template>
      </el-table-column>

      <el-table-column label="排序" width="100" align="center" prop="sort" sortable="custom" :sort-orders="['ascending', 'descending']">
        <template slot-scope="scope">
          <el-tag type="warning">{{ scope.row.sort }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="创建时间" width="160" align="center" prop="createTime" sortable="custom" :sort-by="['createTime']">
        <template slot-scope="scope">
          <span>{{ scope.row.createTime }}</span>
        </template>
      </el-table-column>

      <el-table-column label="更新时间" width="160" align="center" prop="updateTime" sortable="custom" :sort-by="['updateTime']">
        <template slot-scope="scope">
          <span>{{ scope.row.updateTime }}</span>
        </template>
      </el-table-column>


      <el-table-column label="操作" fixed="right" min-width="230">
        <template slot-scope="scope">
          <el-button @click="handleEdit(scope.row)" type="primary" size="mini" v-permission="'/problemTag/edit'">编辑</el-button>
          <el-button @click="handleDelete(scope.row)" type="danger" size="mini" v-permission="'/problemTag/deleteBatch'">删除</el-button>
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

    <!-- 添加或修改对话框 -->
    <el-dialog :title="title" :visible.sync="dialogFormVisible">
      <el-form :model="form" :rules="rules" ref="form">

        <el-form-item label="标签名" :label-width="formLabelWidth" prop="name">
          <el-input v-model="form.name" auto-complete="off"></el-input>
        </el-form-item>

        <el-form-item label="标签等级" :label-width="formLabelWidth" prop="tagLevel">
          <el-select v-model="form.tagLevel" placeholder="请选择">
            <el-option
              v-for="item in tagLevelDictList"
              :key="item.uid"
              :label="item.dictLabel"
              :value="parseInt(item.dictValue)"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item
          v-if="form.tagLevel == 2"
          label="父标签名"
          :label-width="formLabelWidth"
          prop="parentUid"
        >
          <el-select
            v-model="form.parentUid"
            filterable
            clearable
            remote
            reserve-keyword
            placeholder="请输入父标签名"
            :remote-method="remoteMethod"
          >
            <el-option
              v-for="item in menuOptions"
              :key="item.uid"
              :label="item.name"
              :value="item.uid"
            ></el-option>
          </el-select>
        </el-form-item>


        <el-form-item label="简介" :label-width="formLabelWidth" prop="summary">
          <el-input v-model="form.summary" auto-complete="off"></el-input>
        </el-form-item>

        <el-row>
          <el-col :span="8">
            <el-form-item label="标签类型" :label-width="formLabelWidth" prop="tagType">
              <el-select v-model="form.tagType"  placeholder="请选择" style="width:200px">
                <el-option
                  v-for="item in problemTagTypeList"
                  :key="item.uid"
                  :label="item.dictLabel"
                  :value="parseInt(item.dictValue)"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>

        </el-row>

        <el-form-item label="图标" :label-width="formLabelWidth" prop="icon">
          <el-input v-model="form.icon" placeholder="请输入前图标名称" style="width: 350px">
            <el-button slot="append" icon="el-icon-setting" @click="openIconsDialog('prefix-icon')">
              选择
            </el-button>
          </el-input>
        </el-form-item>

        <el-form-item label="是否跳转外链" :label-width="formLabelWidth" prop="isShow">
          <el-radio-group v-model="form.isJumpExternalUrl" size="small">
            <el-radio v-for="item in jumpExternalDictList" :key="item.uid" :label="parseInt(item.dictValue)" border>{{item.dictLabel}}</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item
          v-if="form.isJumpExternalUrl == 1"
          label="链接"
          :label-width="formLabelWidth"
          prop="parentUid"
        >
          <el-input v-model="form.url" auto-complete="off"></el-input>
        </el-form-item>


        <el-form-item label="排序" :label-width="formLabelWidth" prop="sort">
          <el-input v-model="form.sort" auto-complete="off"></el-input>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm(0)">确 定</el-button>
      </div>
    </el-dialog>
    <icons-dialog :visible.sync="iconsVisible" :current="form.icon" @select="setIcon" />
  </div>
</template>

<script>
import {
  getProblemTagList,
  addProblemTag,
  editProblemTag,
  deleteBatchProblemTag
} from "@/api/problemTag";
import {getListByDictTypeList} from "@/api/sysDictData"
import { formatData } from "@/utils/webUtils";
import IconsDialog from "../../components/IconsDialog";
export default {
  data() {
    return {
      queryParams:{
        keyword: "",
        tagLevel: null,
      },
      iconsVisible: false, // 是否显示icon选择器
      activeData: '', // 激活的图标
      multipleSelection: [], //多选，用于批量删除
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0, //总数量
      title: "增加标签",
      dialogFormVisible: false, //控制弹出框
      formLabelWidth: "120px",
      isEditForm: false,
      orderByDescColumn: "", // 降序字段
      orderByAscColumn: "", // 升序字段
      form: {
        name: "",
        summary: "",
        sort: 0,
      },
      menuOptions: [], // 一级标签候选项
      problemTagTypeList: [], // 问题标签类型列表
      openDictList: [], // 是否开启评论
      problemSelectionDictList: [], // 是否精选
      tagLevelDictList: [], //菜单等级字典
      yesNoDictList: [], // 是否字典
      publishDictList: [], // 上架
      jumpExternalDictList: [], // 是否跳转外链
      problemTagTypeDefault: null,
      openDefault: null,
      problemSelectionDefault: null,
      publishDefault: null,
      tagLevelDefault: null,
      jumpExternalDefault: null,
      rules: {
        name: [
          {required: true, message: '标签名称不能为空', trigger: 'blur'},
          {min: 1, max: 100, message: '长度在1到10个字符'},
        ],
        tagType: [
          {required: true, message: '标签类型不能为空', trigger: 'blur'}
        ],
        isSelection: [
          {required: true, message: '是否精选不能为空', trigger: 'blur'}
        ],
        sort: [
          {required: true, message: '排序字段不能为空', trigger: 'blur'},
          {pattern: /^[0-9]\d*$/, message: '排序字段只能为自然数'},
        ]
      },
    };
  },
  components: {
    IconsDialog
  },
  created() {
    // 获取一遍一级标签
    this.remoteMethod()
    this.getDictList()
    this.tagList();
  },
  methods: {
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
      this.tagList()
    },
    openIconsDialog(model) {
      this.iconsVisible = true
      this.currentIconModel = model
    },
    // 选择图标
    setIcon(val) {
      this.form.icon = val
    },
    //菜单远程搜索函数
    remoteMethod: function(query) {
      //这里只搜索一级菜单出来
      let params = {};
      params.keyword = query
      params.tagLevel = 1
      params.pageSize = 100
      params.currentPage = 1
      getProblemTagList(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.menuOptions = response.data.records;
        } else {
          this.menuOptions = []
        }
      });
    },
    /**
     * 字典查询
     */
    getDictList: function () {
      let dictTypeList =  ['sys_problem_tag_type', 'sys_normal_disable', 'sys_yes_no', 'sys_publish_status', 'sys_tag_level', 'sys_yes_no', 'sys_jump_external']
      getListByDictTypeList(dictTypeList).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let dictMap = response.data;
          this.problemTagTypeList = dictMap.sys_problem_tag_type.list
          this.openDictList = dictMap.sys_normal_disable.list
          this.problemSelectionDictList = dictMap.sys_yes_no.list
          this.publishDictList = dictMap.sys_publish_status.list
          this.tagLevelDictList = dictMap.sys_tag_level.list
          this.yesNoDictList = dictMap.sys_yes_no.list
          this.jumpExternalDictList = dictMap.sys_jump_external.list
          if(dictMap.sys_problem_tag_type.defaultValue) {
            this.problemTagTypeDefault = dictMap.sys_problem_tag_type.defaultValue;
          }
          if(dictMap.sys_normal_disable.defaultValue) {
            this.openDefault = dictMap.sys_normal_disable.defaultValue;
          }
          if(dictMap.sys_yes_no.defaultValue) {
            this.problemSelectionDefault = dictMap.sys_yes_no.defaultValue;
          }
          if(dictMap.sys_publish_status.defaultValue) {
            this.publishDefault = dictMap.sys_publish_status.defaultValue;
          }
          if(dictMap.sys_tag_level.defaultValue) {
            this.tagLevelDefault = dictMap.sys_tag_level.defaultValue;
          }
          if(dictMap.sys_yes_no.defaultValue) {
            this.yesNoDefault = parseInt(dictMap.sys_yes_no.defaultValue);
          }
          if(dictMap.sys_jump_external.defaultValue) {
            this.jumpExternalDefault = parseInt(dictMap.sys_jump_external.defaultValue);
          }
        }
      });
    },
    handChange(row) {
      if (row.selectionStatus) {
        row.isSelection = "1"
      } else {
        row.isSelection = "0"
      }
      if (row.publishStatus) {
        row.isPublish = "1"
      } else {
        row.isPublish = "0"
      }
      this.form = row;
      this.submitForm(1)
    },
    tagList: function() {
      let params = {};
      params.keyword = this.queryParams.keyword;
      params.tagLevel = this.queryParams.tagLevel;
      params.tagType = this.queryParams.tagType
      params.isSelection = this.queryParams.isSelection
      params.isPublish = this.queryParams.isPublish
      params.currentPage = this.currentPage;
      params.pageSize = this.pageSize;
      params.orderByDescColumn = this.orderByDescColumn
      params.orderByAscColumn = this.orderByAscColumn
      getProblemTagList(params).then(response => {
        let tableData = response.data.records;
        for (let i = 0; i < tableData.length; i++) {
          if (tableData[i].isSelection == "1") {
            tableData[i].selectionStatus = true
          } else {
            tableData[i].selectionStatus = false
          }
          if(tableData[i].isPublish == "1") {
            tableData[i].publishStatus = true
          } else {
            tableData[i].publishStatus = false
          }
        }
        console.log("tableData", tableData)
        this.tableData = tableData;
        this.currentPage = response.data.current;
        this.pageSize = response.data.size;
        this.total = response.data.total;
      });
    },
    getFormObject: function() {
      let formObject = {
        uid: null,
        name: null,
        summary: null,
        clickCount: 0,
        isSelection: this.problemSelectionDefault,
        openComment: this.openDefault,
        tagLevel: parseInt(this.tagLevelDefault),
        isJumpExternalUrl: parseInt(this.jumpExternalDefault),
        url: "",
        icon: "",
        sort: 0
      };
      return formObject;
    },
    handleFind: function() {
      this.currentPage = 1
      this.tagList();
    },
    handleAdd: function() {
      this.title = "增加标签"
      this.dialogFormVisible = true;
      this.form = this.getFormObject();
      this.isEditForm = false;
    },
    handleEdit: function(row) {
      this.title = "编辑标签";
      this.dialogFormVisible = true;
      this.isEditForm = true;
      this.form = row;
      this.form.isJumpExternalUrl = parseInt(row.isJumpExternalUrl)
    },
    handleDelete: function(row) {
      let that = this;
      this.$confirm("此操作将把标签删除, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          let params = [];
          params.push(row);
          deleteBatchProblemTag(params).then(response => {
            if(response.code == this.$ECode.SUCCESS) {
              this.$commonUtil.message.success(response.message)
            } else {
              this.$commonUtil.message.error(response.message)
            }
            that.tagList();
          });
        })
        .catch(() => {
          this.$commonUtil.message.info("已取消删除")
        });
    },
    handleDeleteBatch: function() {
      let that = this;
      if(that.multipleSelection.length <= 0 ) {
        this.$commonUtil.message.error("请先选中需要删除的内容")
        return;
      }
      this.$confirm("此操作将把选中的标签删除, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          deleteBatchProblemTag(that.multipleSelection).then(response => {
            if(response.code == this.$ECode.SUCCESS) {
              this.$commonUtil.message.success(response.message)
            } else {
              this.$commonUtil.message.error(response.message)
            }
            that.tagList();
          });
        })
        .catch(() => {
          this.$commonUtil.message.info("已取消删除")
        });
    },
    handleCurrentChange: function(val) {
      this.currentPage = val;
      this.tagList();
    },
    submitForm: function(submitType) {
      // 正常提交
      if(submitType == 0) {
        this.$refs.form.validate((valid) => {
          if(!valid) {
            console.log('校验失败')
            return;
          } else {
            if (this.isEditForm) {
              editProblemTag(this.form).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                  this.$commonUtil.message.success(response.message)
                  this.dialogFormVisible = false;
                  this.tagList();
                } else {
                  this.$commonUtil.message.error(response.message)
                }
              });
            } else {
              addProblemTag(this.form).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                  this.$commonUtil.message.success(response.message)
                  this.dialogFormVisible = false;
                  this.tagList();
                } else {
                  this.$commonUtil.message.error(response.message)
                }
              });
            }
          }
        })
      } else {
        editProblemTag(this.form).then(response => {
          if (response.code == this.$ECode.SUCCESS) {
            this.$commonUtil.message.success(response.message)
            this.dialogFormVisible = false;
            this.tagList();
          } else {
            this.$commonUtil.message.error(response.message)
          }
        });
      }

    },
    // 改变多选
    handleSelectionChange(val) {
      this.multipleSelection = val;
    }
  }
};
</script>
