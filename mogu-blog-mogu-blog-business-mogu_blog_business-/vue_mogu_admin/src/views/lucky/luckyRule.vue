<template>
    <div class="app-container">
        <!-- 查询和其他操作 -->
        <div class="filter-container" style="margin: 10px 0 10px 0;" v-permission="'/luckyRule/getList'">
            <el-input
                clearable
                @keyup.enter.native="handleFind"
                class="filter-item"
                style="width: 200px;"
                v-model="keyword"
                placeholder="请输入规则名"
                size="small"
            ></el-input>
            <el-button class="filter-item" type="primary" size="small" icon="el-icon-search" @click="handleFind" v-permission="'/luckyRule/getList'">查找</el-button>
            <el-button class="filter-item" type="primary" size="small" @click="handleAdd" icon="el-icon-edit" v-permission="'/luckyRule/add'">添加规则</el-button>
            <el-button class="filter-item" type="danger" size="small" @click="handleDeleteBatch" icon="el-icon-delete" v-permission="'/luckyRule/deleteBatch'">删除选中</el-button>
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

            <el-table-column label="规则名称" width="100" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.name }}</span>
                </template>
            </el-table-column>

            <el-table-column label="最大参与次数" width="200" align="center" prop="maxLuckyCount" sortable="custom" :sort-by="['maxLuckyCount']">
                <template slot-scope="scope">
                    <span>{{ scope.row.maxLuckyCount }}</span>
                </template>
            </el-table-column>

            <el-table-column label="最大中奖次数" width="200" align="center" prop="maxAwardCount" sortable="custom" :sort-by="['maxAwardCount']">
                <template slot-scope="scope">
                    <span>{{ scope.row.maxAwardCount }}</span>
                </template>
            </el-table-column>

            <el-table-column label="每日可参与次数" width="200" align="center" prop="dayLuckyCount" sortable="custom" :sort-by="['maxLuckyCount']">
                <template slot-scope="scope">
                    <span>{{ scope.row.dayLuckyCount }}</span>
                </template>
            </el-table-column>

            <el-table-column label="抽奖消耗积分" width="200" align="center" prop="maxAwardCount" sortable="custom" :sort-by="['costCredits']">
                <template slot-scope="scope">
                    <span>{{ scope.row.costCredits }}</span>
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
                    <el-button @click="handleEdit(scope.row)" type="primary" size="mini" v-permission="'/luckyRule/edit'">编辑</el-button>
                    <el-button @click="handleDelete(scope.row)" type="danger" size="mini" v-permission="'/luckyRule/delete'">删除</el-button>
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

                <el-form-item label="规则名" :label-width="formLabelWidth" prop="name">
                    <el-input v-model="form.name" auto-complete="off"></el-input>
                </el-form-item>

                <el-form-item label="最大抽奖次数" :label-width="formLabelWidth" prop="maxLuckyCount">
                    <el-input v-model="form.maxLuckyCount" auto-complete="off" placeholder="请输入单个活动最大抽奖次数，-1不限制"></el-input>
                </el-form-item>

                <el-form-item label="最大中奖次数" :label-width="formLabelWidth" prop="maxAwardCount">
                    <el-input v-model="form.maxAwardCount" auto-complete="off" placeholder="请输入单个活动最大中奖次数，-1不限制"></el-input>
                </el-form-item>

                <el-form-item label="每日可抽奖次数" :label-width="formLabelWidth" prop="dayLuckyCount">
                    <el-input v-model="form.dayLuckyCount" auto-complete="off" placeholder="请输入单个活动每日可抽奖次数，-1不限制"></el-input>
                </el-form-item>

                <el-form-item label="抽奖消耗积分" :label-width="formLabelWidth" prop="costCredits">
                    <el-input v-model="form.costCredits" auto-complete="off" placeholder="请输入单个活动抽奖消耗积分，-1不限制"></el-input>
                </el-form-item>


            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitForm">确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import {
    getLuckyRuleList,
    addLuckyRule,
    editLuckyRule,
    deleteBatchLuckyRule,
} from "@/api/luckyRule";
import { formatData } from "@/utils/webUtils";
export default {
    data() {
        return {
            multipleSelection: [], //多选，用于批量删除
            tableData: [],
            keyword: "",
            currentPage: 1,
            pageSize: 10,
            total: 0, //总数量
            title: "增加规则",
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
                    {required: true, message: '规则名称不能为空', trigger: 'blur'},
                    {min: 1, max: 25, message: '长度在1到10个字符'},
                ],
                maxLuckyCount: [
                    {required: true, message: '最大抽奖次数不能为空', trigger: 'blur'},
                    {pattern: /^[0-9]\d*$/, message: '最大抽奖次数只能为自然数'},
                ],
                maxAwardCount: [
                    {required: true, message: '最大中奖次数不能为空', trigger: 'blur'},
                    {pattern: /^[0-9]\d*$/, message: '最大中奖次数只能为自然数'},
                ],
                dayLuckyCount: [
                    {required: true, message: '每日可抽奖次数不能为空', trigger: 'blur'},
                    {pattern: /^[0-9]\d*$/, message: '最大中奖次数只能为自然数'},
                ],
                costCredits: [
                    {required: true, message: '抽奖消耗积分数不能为空', trigger: 'blur'},
                    {pattern: /^[0-9]\d*$/, message: '抽奖消耗积分数只能为自然数'},
                ],
            }
        };
    },
    created() {
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
        tagList: function() {
            var params = {};
            params.keyword = this.keyword;
            params.currentPage = this.currentPage;
            params.pageSize = this.pageSize;
            params.orderByDescColumn = this.orderByDescColumn
            params.orderByAscColumn = this.orderByAscColumn
            getLuckyRuleList(params).then(response => {
                this.tableData = response.data.records;
                this.currentPage = response.data.current;
                this.pageSize = response.data.size;
                this.total = response.data.total;
            });
        },
        getFormObject: function() {
            var formObject = {
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
            this.title = "增加规则"
            this.dialogFormVisible = true;
            this.form = this.getFormObject();
            this.isEditForm = false;
        },
        handleEdit: function(row) {
            this.title = "编辑规则";
            this.dialogFormVisible = true;
            this.isEditForm = true;
            this.form = row;
        },
        handleDelete: function(row) {
            var that = this;
            this.$confirm("此操作将把规则删除, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {

                    var params = [];
                    params.push(row);
                    deleteBatchLuckyRule(params).then(response => {
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
            var that = this;
            var that = this;
            if(that.multipleSelection.length <= 0 ) {
                this.$commonUtil.message.error("请先选中需要删除的内容")
                return;
            }
            this.$confirm("此操作将把选中的规则删除, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    deleteBatchLuckyRule(that.multipleSelection).then(response => {
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
                        editLuckyRule(this.form).then(response => {
                            if (response.code == this.$ECode.SUCCESS) {
                                this.$commonUtil.message.success(response.message)
                                this.dialogFormVisible = false;
                                this.tagList();
                            } else {
                                this.$commonUtil.message.error(response.message)
                            }
                        });
                    } else {
                        addLuckyRule(this.form).then(response => {
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
