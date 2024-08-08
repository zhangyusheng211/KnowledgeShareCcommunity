<template>
    <div class="app-container">
        <div class="filter-container" style="margin: 10px 0 10px 0;" v-permission="'/blogSort/getList'">
            <el-input
                clearable
                @keyup.enter.native="handleFind"
                class="filter-item"
                style="width: 200px;"
                v-model="keyword"
                size="small"
                placeholder="请输入分类名"
            ></el-input>
            <el-button class="filter-item" type="primary" icon="el-icon-search" size="small" @click="handleFind"
                       v-permission="'/blogSort/getList'">查找
            </el-button>
            <el-button class="filter-item" type="primary" @click="handleAdd" size="small" icon="el-icon-edit"
                       v-permission="'/blogSort/add'">添加分类
            </el-button>
            <el-button class="filter-item" type="danger" @click="handleDeleteBatch" size="small" icon="el-icon-delete"
                       v-permission="'/blogSort/deleteBatch'">删除选中
            </el-button>
        </div>

        <el-table :data="tableData"
                  style="width: 100%"
                  @selection-change="handleSelectionChange"
                  @sort-change="changeSort"
                  :default-sort="{prop: 'createTime', order: 'ascending'}">
            <el-table-column type="selection"></el-table-column>
            <el-table-column label="序号" width="60" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.$index + 1 }}</span>
                </template>
            </el-table-column>


            <el-table-column label="举报用户" width="150" align="center">
                <template slot-scope="scope">
                    <UserAvatar :user="scope.row.user"></UserAvatar>
                </template>
            </el-table-column>

            <el-table-column label="举报内容" width="300" align="center">
                <template slot-scope="scope">
                    <span v-html="scope.row.reportContent">{{ scope.row.reportContent }}</span>
                </template>
            </el-table-column>

            <el-table-column label="举报类型" width="100" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.reportType }}</span>
                </template>
            </el-table-column>

            <el-table-column label="举报类型" width="200" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.violationType }}</span>
                </template>
            </el-table-column>

            <el-table-column label="被举报用户" width="150" align="center">
                <template slot-scope="scope">
                    <UserAvatar :user="scope.row.reportUser"></UserAvatar>
                </template>
            </el-table-column>

            <el-table-column label="举报详情" width="200" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.content }}</span>
                </template>
            </el-table-column>

            <el-table-column label="创建时间" width="160" align="center" prop="createTime" sortable="custom"
                             :sort-orders="['ascending', 'descending']">
                <template slot-scope="scope">
                    <span>{{ scope.row.createTime }}</span>
                </template>
            </el-table-column>

            <el-table-column label="进度" width="100" align="center">
                <template slot-scope="scope">
                    <template v-if="scope.row.progress == 0">
                        <el-tag type="info">未处理</el-tag>
                    </template>
                    <template v-if="scope.row.progress == 1">
                        <el-tag type="warning">已查看</el-tag>
                    </template>
                    <template v-if="scope.row.progress == 2">
                        <el-tag type="success">已处理</el-tag>
                    </template>
                </template>
            </el-table-column>

            <el-table-column label="操作" fixed="right" min-width="230">
                <template slot-scope="scope">
                    <el-button @click="handleAudit(scope.row)" type="primary" size="mini"
                               v-permission="'/blogSort/edit'">处理
                    </el-button>
                    <el-button @click="handleDelete(scope.row)" type="danger" size="mini"
                               v-permission="'/blogSort/delete'">删除
                    </el-button>
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

        <el-dialog
            title="请选择处理结果"
            :visible.sync="auditDialogVisible"
            width="40%"
            center>
            <div style="text-align: center">
                <el-form>
                    <el-form-item label="内容" :label-width="formLabelWidth" prop="auditStatus">
            <span v-html="auditForm.violationContent">
              {{ auditForm.violationContent }}
            </span>
                    </el-form-item>
                    <el-form-item label="处理状态" :label-width="formLabelWidth" prop="auditStatus">
                        <el-radio-group v-model="auditForm.progress" size="small">
                            <el-radio :label="1" border>暂不处理</el-radio>
                            <el-radio :label="2" border>下架处理</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-form>
            </div>
            <span slot="footer" class="dialog-footer">
        <el-button @click="auditDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitAudit">确 定</el-button>
      </span>
        </el-dialog>

    </div>
</template>

<script>
import {deleteBatch, getReportList, reportHandle,} from "@/api/report";
import UserAvatar from "../../components/UserAvatar"
export default {
    data() {
        return {
            iconsVisible: false, // 是否显示icon选择器
            multipleSelection: [], //多选，用于批量删除
            tableData: [],
            keyword: "",
            currentPage: 1,
            pageSize: 10,
            total: 0, //总数量
            title: "增加分类",
            dialogFormVisible: false, //控制弹出框
            auditDialogVisible: false, // 处理窗口弹出框
            formLabelWidth: "120px",
            isEditForm: false,
            orderByDescColumn: "",
            orderByAscColumn: "",
            auditForm: {},
            form: {
                uid: null,
                content: "",
                sortName: ""
            },
            rules: {
                sortName: [
                    {required: true, message: '分类名称不能为空', trigger: 'blur'},
                    {min: 1, max: 10, message: '长度在1到10个字符'},
                ],
                sort: [
                    {required: true, message: '排序字段不能为空', trigger: 'blur'},
                    {pattern: /^[0-9]\d*$/, message: '排序字段只能为自然数'},
                ]
            }
        };
    },
    components: {
        UserAvatar,
    },
    created() {
        this.reportList()
    },
    methods: {
        // 从后台获取数据,重新排序
        changeSort(val) {
            // 根据当前排序重新获取后台数据,一般后台会需要一个排序的参数
            if (val.order == "ascending") {
                this.orderByAscColumn = val.prop
                this.orderByDescColumn = ""
            } else {
                this.orderByAscColumn = ""
                this.orderByDescColumn = val.prop
            }
            this.reportList()
        },
        reportList: function () {
            let params = {};
            params.keyword = this.keyword;
            params.currentPage = this.currentPage;
            params.pageSize = this.pageSize;
            params.orderByDescColumn = this.orderByDescColumn
            params.orderByAscColumn = this.orderByAscColumn
            getReportList(params).then(response => {
                this.tableData = response.data.records;
                this.currentPage = response.data.current;
                this.pageSize = response.data.size;
                this.total = response.data.total;
            });
        },
        getFormObject: function () {
            let formObject = {
                uid: null,
                content: null,
                sortName: null,
                sort: 0
            };
            return formObject;
        },
        handleFind: function () {
            this.currentPage = 1
            this.reportList()
        },
        handleAdd: function () {
            this.title = "增加分类"
            this.dialogFormVisible = true;
            this.form = this.getFormObject();
            this.isEditForm = false;
        },
        getAuditsFormObject: function () {
            let auditForm = {
                uid: "",
                violationContent: "", // 处理内容
                reportType: "",
                progress: 2, // 暂不处理
            }
            return auditForm
        },
        handleAudit: function (row) {
            this.auditForm = this.getAuditsFormObject();
            this.auditForm.uid = row.uid
            this.auditForm.reportContentUid = row.reportContentUid
            this.auditForm.reportType = row.reportType
            this.auditForm.violationContent = row.violationContent
            this.auditDialogVisible = true
        },
        submitAudit: function () {
            let params = {}
            console.log("提交审批", this.auditForm)
            params.reportUid = this.auditForm.uid
            params.progress = this.auditForm.progress
            params.reportType = this.auditForm.reportType
            reportHandle(params).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    this.$commonUtil.message.success(response.message)
                    this.auditDialogVisible = false
                    this.reportList();
                } else {
                    this.$commonUtil.message.error(response.message)
                }
            });
        },
        handleDelete: function (row) {
            this.multipleSelection = [row.uid]
            this.handleDeleteBatch()
        },
        handleDeleteBatch: function () {
            let that = this;
            if (that.multipleSelection.length <= 0) {
                this.$commonUtil.message.error("请先选中需要删除的内容!")
                return;
            }
            this.$confirm("此操作将把选中的分类删除, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    deleteBatch(that.multipleSelection).then(response => {
                        if (response.code == this.$ECode.SUCCESS) {
                            this.$commonUtil.message.success(response.message)
                        } else {
                            this.$commonUtil.message.error(response.message)
                        }
                        this.reportList()
                    });
                })
                .catch(() => {
                    this.$commonUtil.message.info("已取消删除！")
                });
        },
        handleCurrentChange: function (val) {
            this.currentPage = val;
            this.reportList();
        },
        // 改变多选
        handleSelectionChange(val) {
            this.multipleSelection = val;
        }
    }
}
</script>

<style scoped>

</style>
