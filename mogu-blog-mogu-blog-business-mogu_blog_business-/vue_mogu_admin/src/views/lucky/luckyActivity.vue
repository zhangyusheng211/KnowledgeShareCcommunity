<template>
    <div class="app-container">
        <!-- 查询和其他操作 -->
        <div class="filter-container" style="margin: 10px 0 10px 0;" v-permission="'/luckyActivity/getList'">
            <el-input
                clearable
                @keyup.enter.native="handleFind"
                class="filter-item"
                style="width: 200px;"
                v-model="keyword"
                placeholder="请输入活动名"
                size="small"
            ></el-input>


            <el-button class="filter-item" type="primary" size="small" icon="el-icon-search" @click="handleFind" v-permission="'/luckyActivity/getList'">查找</el-button>
            <el-button class="filter-item" type="primary" size="small" @click="handleAdd" icon="el-icon-edit" v-permission="'/luckyActivity/add'">添加活动</el-button>
            <el-button class="filter-item" type="danger" size="small" @click="handleDeleteBatch" icon="el-icon-delete" v-permission="'/luckyActivity/deleteBatch'">删除选中</el-button>
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

            <el-table-column align="center" label="活动图" width="250">
                <template slot-scope="scope">
                    <img v-if="scope.row.photoUrl" :src="scope.row.photoUrl"
                         style="width: 100px;height: 100px;"
                    >
                </template>
            </el-table-column>


            <el-table-column label="活动名称" width="100" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.title }}</span>
                </template>
            </el-table-column>

            <el-table-column label="活动状态" width="200" align="center" prop="maxLuckyCount" sortable="custom" :sort-by="['maxLuckyCount']">
                <template slot-scope="scope">
                    <span>{{ scope.row.activityStatus }}</span>
                </template>
            </el-table-column>

            <el-table-column label="开始时间" width="160" align="center" prop="createTime" sortable="custom" :sort-by="['createTime']">
                <template slot-scope="scope">
                    <span>{{ scope.row.startTime }}</span>
                </template>
            </el-table-column>

            <el-table-column label="结束时间" width="160" align="center" prop="createTime" sortable="custom" :sort-by="['createTime']">
                <template slot-scope="scope">
                    <span>{{ scope.row.endTime }}</span>
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
                    <el-button @click="handleEdit(scope.row)" type="primary" size="mini" v-permission="'/luckyActivity/edit'">编辑</el-button>
                    <el-button @click="handleDelete(scope.row)" type="danger" size="mini" v-permission="'/luckyActivity/deleteBatch'">删除</el-button>
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
        <el-dialog :title="title" :visible.sync="dialogFormVisible" fullscreen>
            <el-form :model="form" :rules="rules" ref="form">

                <el-form-item label="活动图" :label-width="formLabelWidth" prop="fileUid">
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

                <el-form-item label="活动标题" :label-width="formLabelWidth" prop="title">
                    <el-input v-model="form.title" auto-complete="off"></el-input>
                </el-form-item>

                <el-form-item label="活动描述" :label-width="formLabelWidth" prop="summary">
                    <el-input v-model="form.summary" auto-complete="off"></el-input>
                </el-form-item>

                <!-- 奖项列表 -->
                <div v-for="(item,index) in sponsorList" :key="(index+1)*20">
                    <el-form-item :label-width="formLabelWidth" :label="'赞助商' + (index+1)">
                        <el-row>
                            <el-col :span="6" style="margin-left: 20px">
                                <el-input style="display: inline-block" size="small" v-model="item.photoUrl" placeholder="输入赞助商头像"></el-input>
                            </el-col>

                            <el-col :span="6" style="margin-left: 20px">
                                <el-row>
                                    <el-col :span="4">
                                        <span style="font-size: 14px; color: #606266; font-weight: 600">赞助商链接</span>
                                    </el-col>
                                    <el-col :span="20">
                                        <el-input style="display: inline-block" size="small" v-model="item.url" placeholder="输入赞助商链接"></el-input>
                                    </el-col>
                                </el-row>
                            </el-col>

                            <el-col :span="6" style="margin-left: 20px">
                                <el-row>
                                    <el-col :span="4">
                                        <span style="font-size: 14px; color: #606266; font-weight: 600">赞助内容</span>
                                    </el-col>
                                    <el-col :span="20">
                                        <el-input style="display: inline-block" size="small" v-model="item.content" placeholder="赞助内容"></el-input>
                                    </el-col>
                                </el-row>
                            </el-col>

                            <el-col :span="4" style="margin-left: 20px">
                                <el-tooltip class="item" effect="dark" content="新增" placement="top">
                                    <el-button type="primary" size="small" icon="el-icon-plus" circle @click="sponsorAdd(index)"></el-button>
                                </el-tooltip>
                                <el-tooltip class="item" effect="dark" content="删除" placement="top">
                                    <el-button type="danger" size="small" icon="el-icon-minus" circle @click="sponsorDel(index)"></el-button>
                                </el-tooltip>
                            </el-col>

                        </el-row>
                    </el-form-item>
                </div>

                <el-row>
                    <el-col :span="6">
                        <el-form-item label="抽奖规则" :label-width="formLabelWidth" prop="summary">
                            <el-select
                                v-model="form.luckyRuleUid"
                                size="small"
                                placeholder="请选择抽奖规则"
                                clearable
                                filterable
                                style="width: 100%"
                            >
                                <el-option
                                    v-for="item in luckyRuleList"
                                    :key="item.uid"
                                    :label="item.name"
                                    :value="item.uid"
                                />
                            </el-select>
                        </el-form-item>
                    </el-col>

                    <el-col :span="6">
                        <el-form-item label="活动开始时间" :label-width="formLabelWidth" prop="startTime">
                            <el-date-picker
                                v-model="form.startTime"
                                type="datetime"
                                value-format="yyyy-MM-dd HH:mm:ss"
                                style="width: 100%"
                                placeholder="活动开始时间">
                            </el-date-picker>
                        </el-form-item>
                    </el-col>

                    <el-col :span="6">
                        <el-form-item label="活动结束时间" :label-width="formLabelWidth" prop="endTime">
                            <el-date-picker
                                v-model="form.endTime"
                                type="datetime"
                                value-format="yyyy-MM-dd HH:mm:ss"
                                style="width: 100%"
                                placeholder="活动结束时间">
                            </el-date-picker>
                        </el-form-item>
                    </el-col>
                </el-row>

                <!-- 奖项列表 -->
                <div v-for="(item,index) in luckyAwardList" :key="index">
                    <el-form-item :label-width="formLabelWidth" :label="'奖项' + (index+1)">

                        <el-row>
                            <el-col :span="2">
                                <el-select
                                    v-model="item.awardProductUid"
                                    size="small"
                                    placeholder="请选择奖品"
                                    clearable
                                    filterable
                                    style="width: 100%"
                                >
                                    <el-option
                                        v-for="item in awardProductList"
                                        :key="item.uid"
                                        :label="item.name"
                                        :value="item.uid"
                                    />
                                </el-select>

                            </el-col>
                            <el-col :span="3" style="margin-left: 20px">
                                <el-row>
                                    <el-col :span="6">
                                        <span style="font-size: 14px; color: #606266; font-weight: 600">
                                            <span v-if="item.awardProductUid">
                                               <span v-for="awardProduct in awardProductList" :key="awardProduct.uid" v-if="awardProduct.uid == item.awardProductUid">
                                                    <span v-if="awardProduct.awardType != 5">奖励个数</span>
                                                    <span v-else>金额(分)</span>
                                                </span>
                                            </span>
                                            <span v-else>
                                                <span>奖励个数</span>
                                            </span>
                                        </span>
                                    </el-col>
                                    <el-col :span="18">
                                        <el-input style="display: inline-block" size="small" v-model="item.count" placeholder="中奖发放个数"></el-input>
                                    </el-col>
                                </el-row>
                            </el-col>

                            <el-col :span="3" style="margin-left: 20px">
                                <el-row>
                                    <el-col :span="6">
                                        <span style="font-size: 14px; color: #606266; font-weight: 600">奖品总数</span>
                                    </el-col>
                                    <el-col :span="18">
                                        <el-input style="display: inline-block" size="small" v-model="item.total" placeholder="奖品总数"></el-input>
                                    </el-col>
                                </el-row>
                            </el-col>

                            <el-col :span="3" style="margin-left: 20px">
                                <el-row>
                                    <el-col :span="6">
                                        <span style="font-size: 14px; color: #606266; font-weight: 600">剩余数量</span>
                                    </el-col>
                                    <el-col :span="18">
                                        <el-input style="display: inline-block" size="small" v-model="item.residueTotal" placeholder="剩余数量"></el-input>
                                    </el-col>
                                </el-row>
                            </el-col>

                            <el-col :span="2" style="margin-left: 20px">
                                <el-row>
                                    <el-col :span="8">
                                        <span style="font-size: 14px; color: #606266; font-weight: 600">奖励放回</span>
                                    </el-col>
                                    <el-col :span="16">
                                        <el-select
                                            v-model="item.putBack"
                                            size="small"
                                            placeholder="请选择"
                                        >
                                            <el-option
                                                v-for="dictItem in yesNoDictList"
                                                :key="dictItem.uid"
                                                :label="dictItem.dictLabel"
                                                :value="parseInt(dictItem.dictValue)"
                                            ></el-option>
                                        </el-select>
                                    </el-col>
                                </el-row>
                            </el-col>


                            <el-col :span="2" style="margin-left: 20px">
                                <el-row>
                                    <el-col :span="8">
                                        <span style="font-size: 14px; color: #606266; font-weight: 600">中奖弹窗</span>
                                    </el-col>
                                    <el-col :span="16">
                                        <el-select
                                            v-model="item.openWindow"
                                            size="small"
                                            placeholder="请选择"
                                        >
                                            <el-option
                                                v-for="dictItem in yesNoDictList"
                                                :key="dictItem.uid"
                                                :label="dictItem.dictLabel"
                                                :value="parseInt(dictItem.dictValue)"
                                            ></el-option>
                                        </el-select>
                                    </el-col>
                                </el-row>
                            </el-col>

                            <el-col :span="2" style="margin-left: 20px">
                                <el-row>
                                    <el-col :span="8">
                                        <span style="font-size: 14px; color: #606266; font-weight: 600">重复中奖</span>
                                    </el-col>
                                    <el-col :span="16">
                                        <el-select
                                            v-model="item.allowRepetition"
                                            size="small"
                                            placeholder="请选择"
                                        >
                                            <el-option
                                                v-for="dictItem in allowDictList"
                                                :key="dictItem.uid"
                                                :label="dictItem.dictLabel"
                                                :value="parseInt(dictItem.dictValue)"
                                            ></el-option>
                                        </el-select>
                                    </el-col>
                                </el-row>
                            </el-col>

                            <el-col :span="4" style="margin-left: 20px">
                                <el-tooltip class="item" effect="dark" content="向上新增" placement="top">
                                    <el-button type="primary" size="small" icon="el-icon-arrow-up" circle @click="topAdd(index)"></el-button>
                                </el-tooltip>
                                <el-tooltip class="item" effect="dark" content="向下新增" placement="top">
                                    <el-button type="primary" size="small" icon="el-icon-arrow-down" circle @click="bottomAdd(index)"></el-button>
                                </el-tooltip>
                                <el-tooltip class="item" effect="dark" content="删除" placement="top" v-if="luckyAwardList.length > 2">
                                    <el-button type="danger" size="small" icon="el-icon-minus" circle @click="del(index)"></el-button>
                                </el-tooltip>
                                <el-tag type="success" style="margin-left: 5px;" v-if="item.total > 0">中奖率:{{ getLuckyRate(item.total)}}%</el-tag>
<!--                                <el-tag type="success" style="margin-left: 5px;" v-if="item.total > 0">当前中奖率:{{ getCurrentLuckyRate(item.residueTotal)}}%</el-tag>-->
                            </el-col>

                        </el-row>
                    </el-form-item>
                </div>

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
    getLuckyActivityList,
    addLuckyActivity,
    editLuckyActivity,
    deleteBatchLuckyActivity,
} from "@/api/luckyActivity";
import {
    getAwardProductList,
} from "@/api/awardProduct";
import {
    getLuckyRuleList
} from "@/api/luckyRule";
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
            keyword: "",
            currentPage: 1,
            pageSize: 10,
            total: 0, //总数量
            title: "增加活动",
            dialogFormVisible: false, //控制弹出框
            formLabelWidth: "150px",
            isEditForm: false,
            orderByDescColumn: "", // 降序字段
            orderByAscColumn: "", // 升序字段
            form: {
                content: ""
            },
            sponsorList: [], // 赞助列表
            awardProductList: [],
            luckyRuleList: [],
            luckyAwardList: [],
            yesNoDictList: [],
            allowDictList: [],
            rules: {
                title: [
                    {required: true, message: '规则名称不能为空', trigger: 'blur'},
                    {min: 1, max: 25, message: '长度在1到10个字符'},
                ],
                startTime: [
                    {required: true, message: '活动开始时间不能为空', trigger: 'blur'},
                ],
                endTime: [
                    {required: true, message: '活动结束时间不能为空', trigger: 'blur'},
                ],
            }
        };
    },
    components: {
        AvatarCropper,
    },
    created() {
        this.getDictList()
        this.tagList();
        this.awardProductListMethod()
        this.ruleListMethod()
    },
    methods: {
        sponsorAdd(idx) {
            this.sponsorList.splice(idx +1, 0, {
                "photoUrl": "",
                "url": "",
                "content": ""
            })
        },
        /**
         * 字典查询
         */
        getDictList: function () {
            const dictTypeList = [
                'sys_yes_no',
                'sys_allow_status'
            ]
            getListByDictTypeList(dictTypeList).then((response) => {
                if (response.code == this.$ECode.SUCCESS) {
                    const dictMap = response.data
                    this.yesNoDictList = dictMap.sys_yes_no.list
                    this.allowDictList = dictMap.sys_allow_status.list
                }
            })
        },
        sponsorDel(idx) {
            this.sponsorList.splice(idx,1)
        },
        // 获取中奖率
        getLuckyRate(value) {
            // 遍历拿到所有的项
            let luckyAwardList = this.luckyAwardList
            let total = 0
            luckyAwardList.forEach(item => {
                total += item.total
            })
            if (total == 0) {
                return 100;
            }
            value = value/total * 100
            return value.toFixed(2)
        },
        // 获取当前中奖率
        getCurrentLuckyRate(value) {
            // 遍历拿到所有的项
            let luckyAwardList = this.luckyAwardList
            let total = 0
            luckyAwardList.forEach(item => {
                total += item.residueTotal
            })
            if (total == 0) {
                return 100;
            }
            value = value/total * 100
            return value.toFixed(2)
        },
        // 添加按钮
        bottomAdd(idx){
            this.luckyAwardList.splice(idx +1, 0, {
                awardProductUid: "",
            })
        },
        topAdd(idx){
            this.luckyAwardList.splice(idx, 0, {
                awardProductUid: "",
            })
        },
        del(index){
            if (this.luckyAwardList.length == 2) {
                this.$message.error("至少需要设置两个奖项")
                return
            }
            this.luckyAwardList.splice(index,1)  //然后删除array对应索引的值，实现点击删除按钮，减少input框效果
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
            this.tagList()
        },
        ruleListMethod: function() {
            let params = {};
            params.currentPage = 1;
            params.pageSize = 100;
            getLuckyRuleList(params).then(response => {
                this.luckyRuleList = response.data.records;
            });
        },
        awardProductListMethod: function() {
            let params = {};
            params.currentPage = 1;
            params.pageSize = 100;
            getAwardProductList(params).then(response => {
                this.awardProductList = response.data.records;
            });
        },
        tagList: function() {
            let params = {};
            params.keyword = this.keyword;
            params.currentPage = this.currentPage;
            params.pageSize = this.pageSize;
            params.orderByDescColumn = this.orderByDescColumn
            params.orderByAscColumn = this.orderByAscColumn
            getLuckyActivityList(params).then(response => {
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
                sort: 0
            };
            return formObject;
        },
        handleFind: function() {
            this.currentPage = 1
            this.tagList();
        },
        handleAdd: function() {
            this.title = "增加活动"
            this.dialogFormVisible = true;
            this.form = this.getFormObject();
            this.isEditForm = false;
            this.luckyAwardList = [{
                awardProductUid: "",
            }]
            this.sponsorList = [
                {
                    "photoUrl": "",
                    "url": "",
                    "content": ""
                }
            ]
        },
        handleEdit: function(row) {
            this.title = "编辑活动";
            this.dialogFormVisible = true;
            this.isEditForm = true;
            this.form = row;
            this.luckyAwardList = this.form.luckyAwardItemList
            this.sponsorList = [                {
                "photoUrl": "",
                "url": "",
                "content": ""
            }]
            if (this.form.sponsorList) {
                let sponsorList = JSON.parse(this.form.sponsorList)
                if (sponsorList) {
                    this.sponsorList = sponsorList
                }
            }

        },
        handleDelete: function(row) {
            let that = this;
            this.$confirm("此操作将把活动删除, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {

                    let params = [];
                    params.push(row);
                    deleteBatchLuckyActivity(params).then(response => {
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
            this.$confirm("此操作将把选中的活动删除, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    deleteBatchLuckyActivity(that.multipleSelection).then(response => {
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
            // 获取奖励项
            this.form.luckyAwardItemList = this.luckyAwardList
            if (this.luckyAwardList.length < 2) {
                this.$message.error("至少需要设置两个奖项")
                return
            }
            this.form.sponsorList = JSON.stringify(this.sponsorList)
            this.$refs.form.validate((valid) => {
                if(!valid) {
                    console.log('校验失败')
                    return;
                } else {
                    if (this.isEditForm) {
                        editLuckyActivity(this.form).then(response => {
                            if (response.code == this.$ECode.SUCCESS) {
                                this.$commonUtil.message.success(response.message)
                                this.dialogFormVisible = false;
                                this.tagList();
                            } else {
                                this.$commonUtil.message.error(response.message)
                            }
                        });
                    } else {
                        addLuckyActivity(this.form).then(response => {
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
