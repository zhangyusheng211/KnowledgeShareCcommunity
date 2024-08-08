<template>
<div id="table" class="app-container calendar-list-container">
	    <!-- 查询和其他操作 -->
	    <div class="filter-container" style="margin: 10px 0 10px 0;">
	      <el-input clearable class="filter-item" style="width: 200px;" v-model="keyword" placeholder="请输入关键字" size="small"></el-input>
	      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFind" size="small" v-permission="'/userMomentTopic/getList'">查找</el-button>
	      <el-button class="filter-item" type="primary" @click="handleAdd" icon="el-icon-edit" size="small" v-permission="'/userMomentTopic/add'">添加</el-button>
	    </div>

    <el-table :data="tableData"  style="width: 100%">
      <el-table-column type="selection"></el-table-column>

      <el-table-column label="序号" width="60" align="center">
	      <template slot-scope="scope">
	        <span >{{scope.$index + 1}}</span>
	      </template>
	    </el-table-column>

	   	<el-table-column label="话题图" width="160" align="center">
	      <template slot-scope="scope">
	      	<img  v-if="scope.row.photoUrl" :src="scope.row.photoUrl" style="width: 130px;height: 70px;"/>
	      </template>
	    </el-table-column>

	    <el-table-column label="话题名称" width="160" align="center">
	      <template slot-scope="scope">
	        <span>{{ scope.row.content }}</span>
	      </template>
	    </el-table-column>

      <el-table-column label="排序" width="100" align="center">
        <template slot-scope="scope">
          <el-tag type="warning">{{ scope.row.sort }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="是否发布" width="150" align="center">
        <template slot-scope="scope">
          <el-tag v-for="item in publishDictList" :key="item.uid" v-if="scope.row.isPublish == item.dictValue" :type="item.listClass">{{item.dictLabel}}</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="创建时间" width="160" align="center">
	      <template slot-scope="scope">
	        <span >{{ scope.row.createTime }}</span>
	      </template>
	    </el-table-column>

      <el-table-column label="更新时间" width="160" align="center">
        <template slot-scope="scope">
          <span >{{ scope.row.updateTime }}</span>
        </template>
      </el-table-column>

	   	<el-table-column label="状态" width="100" align="center">
	   	  <template slot-scope="scope">
		   	  <template v-if="scope.row.status == 1">
		        <span>正常</span>
		      </template>
		      <template v-if="scope.row.status == 2">
		        <span>推荐</span>
		      </template>
		      <template v-if="scope.row.status == 0">
		        <span>已删除</span>
		      </template>
	   	  </template>
	    </el-table-column>

	    <el-table-column label="操作" fixed="right" min-width="315">
	      <template slot-scope="scope" >
	      	<el-button @click="handleEdit(scope.row)" type="primary" size="mini" v-permission="'/userMomentTopic/edit'">编辑</el-button>
	        <el-button @click="handleDelete(scope.row)" type="danger" size="mini" v-permission="'/userMomentTopic/delete'">删除</el-button>
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
          :total="total">
        </el-pagination>
    </div>

	  <!-- 添加或修改对话框 -->
		<el-dialog :title="title" :visible.sync="dialogFormVisible">
		  <el-form :model="form" :rules="rules" ref="form">

				<el-form-item label="话题图" :label-width="formLabelWidth">
	    		<div class="imgBody" v-if="form.photoList">
	    		  	<i class="el-icon-error inputClass" v-show="icon" @click="deletePhoto()" @mouseover="icon = true"></i>
	    			<img @mouseover="icon = true" @mouseout="icon = false" v-bind:src="form.photoList[0]" style="display:inline; width: 195px;height: 105px;"/>
	    		</div>
	    		<div v-else class="uploadImgBody" @click="checkPhoto">
 		 			<i class="el-icon-plus avatar-uploader-icon"></i>
		    	</div>
		    </el-form-item>

		    <el-form-item label="话题名称" :label-width="formLabelWidth" prop="content">
		      <el-input v-model="form.content" auto-complete="off"></el-input>
		    </el-form-item>

        <el-form-item label="是否发布" :label-width="formLabelWidth" prop="isPublish">
          <el-radio-group v-model="form.isPublish" size="small">
            <el-radio v-for="item in publishDictList" :key="item.uid" :label="item.dictValue" border>{{item.dictLabel}}</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="排序" :label-width="formLabelWidth" prop="sort">
          <el-input v-model="form.sort" auto-complete="off"></el-input>
        </el-form-item>
		  </el-form>
		  <div slot="footer" class="dialog-footer">
		    <el-button @click="dialogFormVisible = false">取 消</el-button>
		    <el-button type="primary" @click="submitForm">确 定</el-button>
		  </div>
		</el-dialog>

		<CheckPhoto v-if="!isFirstPhotoVisible" @choose_data="getChooseData" @cancelModel="cancelModel" :photoVisible="photoVisible" :photos="photoList" :files="fileIds" :limit="1"></CheckPhoto>

  </div>
</template>

<script>
import {
  getUserMomentTopicList,
  addUserMomentTopic,
  editUserMomentTopic
} from "@/api/userMomentTopic";
import {getListByDictTypeList} from "@/api/sysDictData"
import CheckPhoto from "../../components/CheckPhoto";

export default {
  components: {
    CheckPhoto
  },
  created() {
    this.getDictList();
    this.userMomentTopicList()
  },
  data() {
    return {
      tableData: [],
      form: {
        uid: null,
        name: null,
        fileUid: null
      },
      loading: true,
      dialogVisible: false, //控制增加框和修改框的显示
      currentPage: 1,
      total: null,
      pageSize: 10,
      keyword: "",
      title: "增加分类",
      formLabelWidth: "120px", //弹框的label边框
      dialogFormVisible: false,
      isEditForm: false,
      photoVisible: false, //控制图片选择器的显示
      photoList: [],
      publishDictList: [], // 是否发布字典
      publishDefault: null, // 是否发布默认值
      fileIds: "",
      icon: false, //控制删除图标的显示
      isFirstPhotoVisible: true, // 图片选择器是否首次显示【用于懒加载】
      rules: {
        content: [
          {required: true, message: '话题名称不能为空', trigger: 'blur'},
          {min: 1, max: 20, message: '长度在1到20个字符'},
        ],
        isPublish: [
          {required: true, message: '发布字段不能为空', trigger: 'blur'}
        ],
        sort: [
          {required: true, message: '排序字段不能为空', trigger: 'blur'},
          {pattern: /^[0-9]\d*$/, message: '排序字段只能为自然数'},
        ]
      }
    };
  },
  methods: {
    userMomentTopicList: function() {
      let params = {};
      params.keyword = this.keyword
      params.pageSize =  this.pageSize
      params.currentPage = this.currentPage
      getUserMomentTopicList(params).then(response => {
        this.tableData = response.data.records;
        this.currentPage = response.data.current;
        this.pageSize = response.data.size;
        this.total = response.data.total;
      });
    },
    /**
     * 字典查询
     */
    getDictList: function () {
      let dictTypeList =  ['sys_publish_status']
      getListByDictTypeList(dictTypeList).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let dictMap = response.data;
          this.publishDictList = dictMap.sys_publish_status.list
          if(dictMap.sys_publish_status.defaultValue) {
            this.publishDefault = dictMap.sys_publish_status.defaultValue
          }
        }
      });
    },
    handleFind: function() {
      this.currentPage = 1
      this.userMomentTopicList();
    },
    getFormObject: function() {
      let formObject = {
        uid: null,
        content: null,
        fileUid: null,
        sort: 0,
        isPublish: this.publishDefault,
      };
      return formObject;
    },
    //弹出选择图片框
    checkPhoto: function() {
      this.photoList = [];
      this.fileIds = "";
      this.photoVisible = true;
      this.isFirstPhotoVisible = false
    },
    getChooseData(data) {
      let that = this;
      this.photoVisible = false;
      this.photoList = data.photoList;
      this.fileIds = data.fileIds;
      let fileId = this.fileIds.replace(",", "");
      if (this.photoList.length >= 1) {
        this.form.fileUid = fileId;
        this.form.photoList = this.photoList;
      }
    },
    //关闭模态框
    cancelModel() {
      this.photoVisible = false;
    },
    deletePhoto: function() {
      this.form.photoList = null;
      this.form.fileUid = "";
    },
    //改变页码
    handleCurrentChange(val) {
      let that = this;
      console.log(`当前页: ${val}`);
      //改变当前所指向的页数
      this.currentPage = val;
      this.userMomentTopicList();
    },
    //点击新增
    handleAdd: function() {
      this.title = "增加分类"
      this.dialogFormVisible = true;
      this.form = this.getFormObject();
      this.isEditForm = false;
    },
    //点击编辑
    handleEdit: function(row) {
      this.title = "编辑分类"
      this.dialogFormVisible = true;
      this.isEditForm = true;
      this.form = row;
      console.log(row)
    },
    submitForm: function() {
      this.$refs.form.validate((valid) => {
        if(!valid) {
          console.log("校验出错")
        } else {
          if (this.isEditForm) {
            editUserMomentTopic(this.form).then(response => {
              this.$commonUtil.message.success(response.message)
              this.dialogFormVisible = false;
              this.userMomentTopicList();
            });
          } else {
            addUserMomentTopic(this.form).then(response => {
              if (response.code == this.$ECode.SUCCESS) {
                this.$commonUtil.message.success(response.message)
              } else {
                this.$commonUtil.message.error(response.message)
              }
              this.dialogFormVisible = false;
              this.userMomentTopicList();
            });
          }
        }
      })
    }
  }
};
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
  width: 195px;
  height: 105px;
  line-height: 105px;
  text-align: center;
}
.imgBody {
  width: 195px;
  height: 105px;
  border: solid 2px #ffffff;
  float: left;
  position: relative;
}
.uploadImgBody {
  margin-left: 5px;
  width: 195px;
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
