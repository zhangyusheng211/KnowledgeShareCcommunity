<template>
    <div class="app-container">
        <!-- 查询和其他操作 -->
        <div class="filter-container" style="margin: 10px 0 10px 0;" v-permission="'/blogSort/getList'">
            <el-input
                clearable
                @keyup.enter.native="handleFind"
                class="filter-item"
                style="width: 200px;"
                v-model="queryParams.keyword"
                size="small"
                placeholder="请输入任务名"
            ></el-input>

            <el-select
                v-model="queryParams.marketName"
                style="width: 140px"
                filterable
                clearable
                remote
                reserve-keyword
                placeholder="任务标识"
                size="small"
                @keyup.enter.native="handleFind"
            >
                <el-option
                    v-for="item in taskSignDictList"
                    :key="item.uid"
                    :label="item.dictLabel"
                    :value="item.dictValue"
                />
            </el-select>

            <el-select
                v-model="queryParams.taskType"
                style="width: 140px"
                filterable
                clearable
                remote
                reserve-keyword
                placeholder="任务类型"
                size="small"
                @keyup.enter.native="handleFind"
            >
                <el-option
                    v-for="item in taskTypeDictList"
                    :key="item.uid"
                    :label="item.dictLabel"
                    :value="item.dictValue"
                />
            </el-select>

            <el-button class="filter-item" type="primary" size="small" icon="el-icon-search" @click="handleFind" v-permission="'/task/getPageList'">查找</el-button>
            <el-button class="filter-item" type="primary" size="small" @click="handleAdd" icon="el-icon-edit" v-permission="'/task/add'">添加任务</el-button>
            <el-button class="filter-item" type="danger" size="small" @click="handleDeleteBatch" icon="el-icon-delete" v-permission="'/task/deleteBatch'">删除选中</el-button>
        </div>

        <el-table :data="tableData"
                  style="width: 100%"
                  @selection-change="handleSelectionChange"
                  @sort-change="changeSort"
                  :default-sort="{prop: 'createTime', order: 'ascending'}">
            <el-table-column type="selection"></el-table-column>
            <el-table-column label="序号" width="60" align="center">
                <template slot-scope="scope">
                    <span>{{scope.$index + 1}}</span>
                </template>
            </el-table-column>

            <el-table-column label="任务名称" width="200" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.name }}</span>
                </template>
            </el-table-column>

            <el-table-column label="任务描述" width="250" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.description }}</span>
                </template>
            </el-table-column>

            <el-table-column label="任务标识" width="100" align="center">
                <template slot-scope="scope">
                    <el-tag
                        v-for="item in taskSignDictList"
                        v-if="scope.row.marketName == item.dictValue"
                        :key="item.uid"
                        :type="item.listClass"
                    >{{ item.dictLabel }}
                    </el-tag>
                </template>
            </el-table-column>

            <el-table-column label="任务icon" width="100" align="center">
                <template slot-scope="scope">
                    <i :class="scope.row.icon" />
                </template>
            </el-table-column>

            <el-table-column label="任务类型" width="150" align="center">
                <template slot-scope="scope">
                    <el-tag
                        v-for="item in taskTypeDictList"
                        v-if="scope.row.limitType == item.dictValue"
                        :key="item.uid"
                        :type="item.listClass"
                    >{{ item.dictLabel }}
                    </el-tag>
                </template>
            </el-table-column>

            <el-table-column
                :sort-by="['isPublish']"
                label="发布状态"
                width="100"
                align="center"
                prop="isPublish"
                sortable="custom"
            >
                <template slot-scope="scope">
                    <el-tag
                        :type="scope.row.isPublish == '1' ? 'success' : 'primary'"
                        disable-transitions
                    >{{ scope.row.isPublish == "1" ? "已发布" : "已下架" }}
                    </el-tag
                    >
                </template>
            </el-table-column>

            <el-table-column label="开始时间" width="160" align="center" prop="startTime" sortable="custom" :sort-orders="['ascending', 'descending']">
                <template slot-scope="scope">
                    <span>{{ scope.row.startTime }}</span>
                </template>
            </el-table-column>

            <el-table-column label="结束时间" width="160" align="center" prop="endTime" sortable="custom" :sort-orders="['ascending', 'descending']">
                <template slot-scope="scope">
                    <span>{{ scope.row.endTime }}</span>
                </template>
            </el-table-column>

            <el-table-column label="排序" width="100" align="center" prop="sort" sortable="custom" :sort-by="['sort']">
                <template slot-scope="scope">
                    <el-tag type="warning">{{ scope.row.sort }}</el-tag>
                </template>
            </el-table-column>

            <el-table-column label="创建时间" width="160" align="center" prop="createTime" sortable="custom" :sort-orders="['ascending', 'descending']">
                <template slot-scope="scope">
                    <span>{{ scope.row.createTime }}</span>
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
                    <el-button @click="handleEdit(scope.row)" type="primary" size="mini" v-permission="'/task/edit'">编辑</el-button>
                    <el-button @click="handleDelete(scope.row)" type="danger" size="mini" v-permission="'/task/deleteBatch'">删除</el-button>
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
                <el-form-item label="任务名称" :label-width="formLabelWidth" prop="name">
                    <el-input v-model="form.name" auto-complete="off"></el-input>
                </el-form-item>

                <el-form-item label="任务描述" :label-width="formLabelWidth" prop="description">
                    <el-input v-model="form.description" auto-complete="off"></el-input>
                </el-form-item>

                <el-form-item label="任务规则" :label-width="formLabelWidth" prop="rule">
                    <CodeEditor ref="rule" :value="form.rule" mode="text/html" style="height: 200px"></CodeEditor>
                </el-form-item>

                <el-form-item label="任务步骤" :label-width="formLabelWidth" prop="stepConfig">
                    <CodeEditor ref="step" :value="form.stepConfig" mode="application/json"></CodeEditor>
                </el-form-item>

                <el-row>
                    <el-col :span="6">
                        <el-form-item
                            :label-width="formLabelWidth"
                            label="任务标识"
                            prop="limitType"
                        >
                            <el-select
                                v-model="form.marketName"
                                size="small"
                                placeholder="是否发布"
                                style="width: 180px"
                            >
                                <el-option
                                    v-for="item in taskSignDictList"
                                    :key="item.uid"
                                    :label="item.dictLabel"
                                    :value="item.dictValue"
                                />
                            </el-select>
                        </el-form-item>
                    </el-col>

                    <el-col :span="6">
                        <el-form-item
                            :label-width="formLabelWidth"
                            label="是否发布"
                            prop="limitType"
                        >
                            <el-select
                                v-model="form.isPublish"
                                size="small"
                                placeholder="是否发布"
                                style="width: 180px"
                            >
                                <el-option
                                    v-for="item in taskPublishDictList"
                                    :key="item.uid"
                                    :label="item.dictLabel"
                                    :value="item.dictValue"
                                />
                            </el-select>
                        </el-form-item>
                    </el-col>

                    <el-col :span="12">
                        <el-form-item
                            :label-width="formLabelWidth"
                            label="任务类型"
                            prop="limitType"
                        >
                            <el-select
                                v-model="form.limitType"
                                size="small"
                                placeholder="请选择任务类型"
                                style="width: 180px"
                            >
                                <el-option
                                    v-for="item in taskTypeDictList"
                                    :key="item.uid"
                                    :label="item.dictLabel"
                                    :value="item.dictValue"
                                />
                            </el-select>

                            <span
                                style="margin-left: 10px"
                                v-for="(item, index) in taskTypeDictList"
                                v-if="form.limitType == item.dictValue"
                                :key="index"
                                hit
                                size="mini"
                                effect="light"
                            >{{ item.remark }}
                    </span
                    >

                        </el-form-item>
                    </el-col>
                </el-row>

                <el-form-item label="图标" :label-width="formLabelWidth" prop="icon">
                    <el-input v-model="form.icon" placeholder="请输入前图标名称">
                        <el-button slot="append" icon="el-icon-setting" @click="openIconsDialog('prefix-icon')">
                            选择
                        </el-button>
                    </el-input>
                </el-form-item>

                <el-form-item v-if="form.limitType == 'CRON'" label="Cron表达式" :label-width="formLabelWidth" prop="limitCron">
                    <el-input v-model="form.limitCron" auto-complete="off"></el-input>
                </el-form-item>


                <el-form-item label="按钮样式" :label-width="formLabelWidth" prop="button">
                    <el-input v-model="form.button" auto-complete="off"></el-input>
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

        <icons-dialog :visible.sync="iconsVisible" :current="form.icon" @select="setIcon" />

    </div>
</template>

<script>
import {
    getTaskConfigList,
    addTaskConfig,
    editTaskConfig,
    deleteBatchTaskConfig
} from "@/api/taskConfig";
import IconsDialog from "../../components/IconsDialog";
import CodeEditor from "../../components/CodeEditor";
import { getListByDictTypeList } from '@/api/sysDictData'
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
            formLabelWidth: "120px",
            isEditForm: false,
            taskActionDictList: [],
            taskTypeDictList: [],
            taskPublishDictList: [],
            taskSignDictList: [],
            taskPublishDefault: null, // 任务发布默认选项
            taskSignDefault: null, // 任务标识默认值
            orderByDescColumn: "",
            orderByAscColumn: "",
            form: {
                uid: null,
                content: "",
                sortName: ""
            },
            queryParams: {},
            rules: {
                name: [
                    {required: true, message: '任务名称不能为空', trigger: 'blur'},
                    {min: 1, max: 20, message: '长度在1到20个字符'},
                ],
                sort: [
                    {required: true, message: '排序字段不能为空', trigger: 'blur'},
                    {pattern: /^[0-9]\d*$/, message: '排序字段只能为自然数'},
                ],
                limitType: [
                    {required: true, message: '任务类型不能为空', trigger: 'blur'}
                ],
                limitCron: [
                    {required: true, message: '定时任务Cron表达式不能为空', trigger: 'blur'}
                ],
                stepConfig: [
                    {required: true, message: '步骤不能为空', trigger: 'blur'},
                    {min: 1, max: 1024, message: '长度在1到1024个字符'},
                ],
            }
        };
    },
    components: {
        IconsDialog,
        CodeEditor,
    },
    created() {
        this.getDictList();
        this.blogSortList();
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
            this.blogSortList()
        },
        /**
         * 字典查询
         */
        getDictList: function() {
            const dictTypeList = ['sys_task_action', 'sys_task_type', 'sys_publish_status', 'sys_task_sign']
            getListByDictTypeList(dictTypeList).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    const dictMap = response.data
                    this.taskActionDictList = dictMap.sys_task_action.list
                    this.taskTypeDictList = dictMap.sys_task_type.list
                    this.taskPublishDictList = dictMap.sys_publish_status.list
                    this.taskSignDictList = dictMap.sys_task_sign.list
                    if (dictMap.sys_publish_status.defaultValue) {
                        this.taskPublishDefault = dictMap.sys_publish_status.defaultValue
                    }
                    if (dictMap.sys_task_sign.defaultValue) {
                        this.taskSignDefault = dictMap.sys_task_sign.defaultValue
                    }
                }
            })
        },
        // 选择图标
        setIcon(val) {
            this.form.icon = val
        },
        openIconsDialog(model) {
            this.iconsVisible = true
            this.currentIconModel = model
        },
        blogSortList: function() {
            let params = {};
            params.name = this.queryParams.keyword;
            params.marketName = this.queryParams.marketName;
            params.limitType = this.queryParams.taskType;
            params.currentPage = this.currentPage;
            params.pageSize = this.pageSize;
            params.orderByDescColumn = this.orderByDescColumn
            params.orderByAscColumn = this.orderByAscColumn
            getTaskConfigList(params).then(response => {
                this.tableData = response.data.records;
                this.currentPage = response.data.current;
                this.pageSize = response.data.size;
                this.total = response.data.total;
            });
        },
        getFormObject: function() {
            let formObject = {
                uid: null,
                name: null,
                description: null,
                isPublish: this.taskPublishDefault,
                marketName: this.taskSignDefault,
                sort: 0
            };
            return formObject;
        },
        handleFind: function() {
            this.currentPage = 1
            this.blogSortList();
        },
        handleAdd: function() {
            this.title = "增加分类"
            this.dialogFormVisible = true;
            this.form = this.getFormObject();
            this.isEditForm = false;
        },
        handleEdit: function(row) {
            this.title = "编辑分类"
            this.dialogFormVisible = true;
            this.isEditForm = true;
            this.form = row;
        },
        handleDelete: function(row) {
            this.multipleSelection = [row]
            this.handleDeleteBatch()
        },
        handleDeleteBatch: function() {
            let that = this;
            if(that.multipleSelection.length <= 0 ) {
                this.$commonUtil.message.error("请先选中需要删除的内容!")
                return;
            }
            let params = []
            let multipleSelection = this.multipleSelection
            multipleSelection.forEach(item => {
                params.push(item.uid)
            })
            this.$confirm("此操作将把选中的任务删除, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    deleteBatchTaskConfig(params).then(response => {
                        if(response.code == this.$ECode.SUCCESS) {
                            this.$commonUtil.message.success(response.message)
                        } else {
                            this.$commonUtil.message.error(response.message)
                        }
                        that.blogSortList();
                    });
                })
                .catch(() => {
                    this.$commonUtil.message.info("已取消删除！")
                });
        },
        handleCurrentChange: function(val) {
            this.currentPage = val;
            this.blogSortList();
        },
        submitForm: function() {
            this.form.rule = this.$refs.rule.getValue()
            this.form.stepConfig = this.$refs.step.getValue()
            this.$refs.form.validate((valid) => {
                if(!valid) {
                    console.log('校验失败')
                    return;
                } else {
                    if (this.isEditForm) {
                        editTaskConfig(this.form).then(response => {
                            console.log(response);
                            if (response.code == this.$ECode.SUCCESS) {
                                this.$commonUtil.message.success(response.message)
                                this.dialogFormVisible = false;
                                this.blogSortList();
                            } else {
                                this.$commonUtil.message.error(response.message)
                            }
                        });
                    } else {
                        addTaskConfig(this.form).then(response => {
                            console.log(response);
                            if (response.code == this.$ECode.SUCCESS) {
                                this.$commonUtil.message.success(response.message)
                                this.dialogFormVisible = false;
                                this.blogSortList();
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
