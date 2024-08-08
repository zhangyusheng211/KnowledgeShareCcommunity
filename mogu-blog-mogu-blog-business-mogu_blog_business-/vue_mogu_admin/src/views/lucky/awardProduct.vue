<template>
    <div class="app-container">
        <!-- 查询和其他操作 -->
        <div class="filter-container" style="margin: 10px 0 10px 0;" v-permission="'/awardProduct/getList'">
            <el-input
                clearable
                @keyup.enter.native="handleFind"
                class="filter-item"
                style="width: 200px;"
                v-model="keyword"
                placeholder="请输入奖品名"
                size="small"
            ></el-input>
            <el-button class="filter-item" type="primary" size="small" icon="el-icon-search" @click="handleFind" v-permission="'/awardProduct/getList'">查找</el-button>
            <el-button class="filter-item" type="primary" size="small" @click="handleAdd" icon="el-icon-edit" v-permission="'/awardProduct/add'">添加奖品</el-button>
            <el-button class="filter-item" type="danger" size="small" @click="handleDeleteBatch" icon="el-icon-delete" v-permission="'/awardProduct/deleteBatch'">删除选中</el-button>
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

            <el-table-column label="奖品UID" width="280" align="center">
                <template slot-scope="scope">
                    <span>{{scope.row.uid}}</span>
                </template>
            </el-table-column>

            <el-table-column align="center" label="奖品图" width="150">
                <template slot-scope="scope">
                    <img v-if="scope.row.photoUrl" :src="scope.row.photoUrl"
                         style="width: 100px;height: 100px;"
                    >
                </template>
            </el-table-column>

            <el-table-column label="奖品名称" width="150" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.name }}</span>
                </template>
            </el-table-column>

            <el-table-column
                :sort-by="['awardType']"
                label="奖品类型"
                width="150"
                align="center"
                prop="level"
                sortable="custom"
            >
                <template slot-scope="scope">
                    <el-tag
                        v-for="item in awardTypeDictList"
                        v-if="scope.row.awardType == item.dictValue"
                        :key="item.uid"
                        :type="item.listClass"
                    >{{ item.dictLabel }}
                    </el-tag
                    >
                </template>
            </el-table-column>

            <el-table-column label="库存数量" width="100" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.total }}</span>
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

            <el-table-column label="操作" fixed="right" min-width="230">
                <template slot-scope="scope">
                    <el-button @click="handleEdit(scope.row)" type="primary" size="mini" v-permission="'/awardProduct/edit'">编辑</el-button>
                    <el-button @click="handleDelete(scope.row)" type="danger" size="mini" v-permission="'/awardProduct/deleteBatch'">删除</el-button>
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

                <el-form-item label="奖品图" :label-width="formLabelWidth" prop="fileUid">
                    <div class="imgBody" v-if="form.photoUrl">
                        <i class="el-icon-error inputClass" v-show="icon" @click="deletePhoto()"
                           @mouseover="icon = true"></i>
                        <img @mouseover="icon = true" @mouseout="icon = false" v-bind:src="form.photoUrl"
                             style="display:inline; width: 195px;height: 105px;"/>
                    </div>
                    <div v-else class="uploadImgBody" @click="checkPhoto">
                        <i class="el-icon-plus avatar-uploader-icon"></i>
                    </div>
                </el-form-item>

                <el-form-item label="奖品名称" :label-width="formLabelWidth" prop="title">
                    <el-input v-model="form.name" auto-complete="off"></el-input>
                </el-form-item>

                <el-form-item label="权益类型" :label-width="formLabelWidth" prop="awardType">
                    <el-select
                        v-model="form.awardType"
                        size="small"
                        placeholder="请选择"
                        style="width: 250px"
                    >
                        <el-option
                            v-for="item in awardTypeDictList"
                            :key="item.uid"
                            :label="item.dictLabel"
                            :value="item.dictValue"
                        ></el-option>
                    </el-select>
                </el-form-item>

                <el-form-item label="库存数量" :label-width="formLabelWidth" prop="summary">
                    <el-input v-model="form.total" auto-complete="off"></el-input>
                </el-form-item>

            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitForm">确 定</el-button>
            </div>
        </el-dialog>

        <avatar-cropper
            :key="imagecropperKey"
            :url="url"
            :width="300"
            :height="250"
            @close="close"
            @crop-upload-success="cropSuccess"
            lang-type="zh"
            v-show="imagecropperShow"
            noSquare
            noCircle
        />

    </div>
</template>

<script>
import {
    getAwardProductList,
    addAwardProduct,
    editAwardProduct,
    deleteBatchAwardProduct,
} from "@/api/awardProduct";
import AvatarCropper from '@/components/AvatarCropper'
import {getListByDictTypeList} from '@/api/sysDictData'

export default {
    data() {
        return {
            icon: false, //控制删除图标的显示
            imagecropperShow: false,
            imagecropperKey: 0,
            url: process.env.PICTURE_API + "/file/cropperPicture",
            multipleSelection: [], //多选，用于批量删除
            tableData: [],
            awardTypeDictList: [],
            yesNoDictList: [],
            keyword: "",
            currentPage: 1,
            pageSize: 10,
            total: 0, //总数量
            title: "增加奖品",
            dialogFormVisible: false, //控制弹出框
            formLabelWidth: "150px",
            isEditForm: false,
            orderByDescColumn: "", // 降序字段
            orderByAscColumn: "", // 升序字段
            form: {
                content: ""
            },
            rules: {
                name: [
                    {required: true, message: '奖品名称不能为空', trigger: 'blur'},
                    {min: 1, max: 25, message: '长度在1到255个字符'},
                ],
                awardType: [
                    {required: true, message: '奖品类型不能为空', trigger: 'blur'},
                ],
            }
        };
    },
    created() {
        this.getDictList()
        this.tagList();
    },
    components: {
        AvatarCropper
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
        tagList: function() {
            let params = {};
            params.keyword = this.keyword;
            params.currentPage = this.currentPage;
            params.pageSize = this.pageSize;
            params.orderByDescColumn = this.orderByDescColumn
            params.orderByAscColumn = this.orderByAscColumn
            getAwardProductList(params).then(response => {
                this.tableData = response.data.records;
                this.currentPage = response.data.current;
                this.pageSize = response.data.size;
                this.total = response.data.total;
            });
        },
        cropSuccess(resData) {
            this.imagecropperShow = false
            this.imagecropperKey = this.imagecropperKey + 1
            this.form.photoUrl = resData.url
            this.form.fileUid = resData.uid
        },
        close() {
            this.imagecropperShow = false
        },
        //弹出选择图片框
        checkPhoto() {
            this.imagecropperShow = true
        },
        deletePhoto: function () {
            this.form.photoUrl = "";
            this.form.fileUid = "";
            this.icon = false;
        },
        getFormObject: function() {
            let formObject = {
                uid: null,
                content: null,
                clickCount: 0,
                sort: 0,
                openWindow: 0,
            };
            return formObject;
        },
        handleFind: function() {
            this.currentPage = 1
            this.tagList();
        },
        handleAdd: function() {
            this.title = "增加奖品"
            this.dialogFormVisible = true;
            this.form = this.getFormObject();
            this.isEditForm = false;
        },
        handleEdit: function(row) {
            this.title = "编辑奖品";
            this.dialogFormVisible = true;
            this.isEditForm = true;
            this.form = row;
        },
        handleDelete: function(row) {
            let that = this;
            this.$confirm("此操作将把奖品删除, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    let params = [];
                    params.push(row);
                    deleteBatchAwardProduct(params).then(response => {
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
        /**
         * 字典查询
         */
        getDictList: function () {
            const dictTypeList = [
                'sys_equity_type',
                'sys_yes_no'
            ]
            getListByDictTypeList(dictTypeList).then((response) => {
                if (response.code == this.$ECode.SUCCESS) {
                    const dictMap = response.data
                    this.awardTypeDictList = dictMap.sys_equity_type.list
                    this.yesNoDictList = dictMap.sys_yes_no.list
                }
            })
        },
        handleDeleteBatch: function() {
            let that = this;
            if(that.multipleSelection.length <= 0 ) {
                this.$commonUtil.message.error("请先选中需要删除的内容")
                return;
            }
            this.$confirm("此操作将把选中的奖品删除, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    deleteBatchAwardProduct(that.multipleSelection).then(response => {
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
        submitForm: function() {
            this.$refs.form.validate((valid) => {
                if(!valid) {
                    console.log('校验失败')
                    return;
                } else {
                    if (this.isEditForm) {
                        editAwardProduct(this.form).then(response => {
                            if (response.code == this.$ECode.SUCCESS) {
                                this.$commonUtil.message.success(response.message)
                                this.dialogFormVisible = false;
                                this.tagList();
                            } else {
                                this.$commonUtil.message.error(response.message)
                            }
                        });
                    } else {
                        addAwardProduct(this.form).then(response => {
                            console.log(response);
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
        },
        // 改变多选
        handleSelectionChange(val) {
            this.multipleSelection = val;
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
