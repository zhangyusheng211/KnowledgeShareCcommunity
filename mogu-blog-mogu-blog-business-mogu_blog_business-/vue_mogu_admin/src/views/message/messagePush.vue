<template>
    <div class="app-container">
        <!-- 查询和其他操作 -->
        <div class="filter-container" style="margin: 10px 0 10px 0;" v-permission="'/messagePush/getList'">
            <el-input
                clearable
                @keyup.enter.native="handleFind"
                class="filter-item"
                style="width: 200px;"
                v-model="keyword"
                size="small"
                placeholder="请输入关键字名"
            ></el-input>
            <el-button class="filter-item" type="primary" size="small" icon="el-icon-search" @click="handleFind" v-permission="'/messagePush/getList'">查找</el-button>
            <el-button class="filter-item" type="primary" size="small" @click="handleAdd" icon="el-icon-edit" v-permission="'/messagePush/add'">添加模板</el-button>
            <el-button class="filter-item" type="danger" size="small" @click="handleDeleteBatch" icon="el-icon-delete" v-permission="'/messagePush/deleteBatch'">删除选中</el-button>
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

            <el-table-column label="模板标题" width="200" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.title }}</span>
                </template>
            </el-table-column>

            <el-table-column label="推送内容" width="350" align="center">
                <template slot-scope="scope">
                    <span v-html="scope.row.content"></span>
                </template>
            </el-table-column>

            <el-table-column
                :sort-by="['pushMethod']"
                label="触达方式"
                width="200"
                align="center"
                prop="pushMethod"
                sortable="custom"
            >
                <template slot-scope="scope">
                    <span v-for="pushMethod in scope.row.pushMethodList">
                        <el-tag
                            v-for="item in pushMethodDictList"
                            v-if="pushMethod == item.dictValue"
                            :key="item.uid"
                            :type="item.listClass"
                            style="margin-right: 5px; margin-top: 5px;"
                        >{{ item.dictLabel }}
                    </el-tag>
                    </span>

                </template>
            </el-table-column>

            <el-table-column
                :sort-by="['pushArea']"
                label="触达范围"
                width="100"
                align="center"
                prop="pushArea"
                sortable="custom"
            >
                <template slot-scope="scope">
                    <el-tag
                        v-for="item in pushAreaDictList"
                        v-if="scope.row.pushArea == item.dictValue"
                        :key="item.uid"
                        :type="item.listClass"
                    >{{ item.dictLabel }}
                    </el-tag
                    >
                </template>
            </el-table-column>

            <el-table-column
                :sort-by="['pushStatus']"
                label="触达状态"
                width="100"
                align="center"
                prop="pushStatus"
                sortable="custom"
            >
                <template slot-scope="scope">
                    <el-tag
                        v-for="item in pushStatusDictList"
                        v-if="scope.row.pushStatus == item.dictValue"
                        :key="item.uid"
                        :type="item.listClass"
                    >{{ item.dictLabel }}
                    </el-tag
                    >
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
                    <el-button @click="handlePush(scope.row)" type="success" size="mini" v-permission="'/messagePush/push'">推送</el-button>
                    <el-button @click="handleEdit(scope.row)" type="primary" size="mini" v-permission="'/messagePush/edit'">编辑</el-button>
                    <el-button @click="handleDelete(scope.row)" type="danger" size="mini" v-permission="'/messagePush/deleteBatch'">删除</el-button>
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
                <el-form-item label="推送标题" :label-width="formLabelWidth" prop="title">
                    <el-input v-model="form.title" auto-complete="off"></el-input>
                </el-form-item>

                <el-form-item
                    :label-width="formLabelWidth"
                    label="触达方式"
                    prop="level"
                >
<!--                    <el-select-->
<!--                        v-model="form.pushMethod"-->
<!--                        size="small"-->
<!--                        placeholder="请选择"-->
<!--                        style="width: 180px"-->
<!--                    >-->
<!--                        <el-option-->
<!--                            v-for="item in pushMethodDictList"-->
<!--                            :key="item.uid"-->
<!--                            :label="item.dictLabel"-->
<!--                            :value="parseInt(item.dictValue)"-->
<!--                        />-->
<!--                    </el-select>-->

<!--                    <el-checkbox-group v-model="form.pushMethodList">-->
<!--                        <el-checkbox label="复选框 A"></el-checkbox>-->
<!--                        <el-checkbox label="复选框 B"></el-checkbox>-->
<!--                        <el-checkbox label="复选框 C"></el-checkbox>-->
<!--                        <el-checkbox label="禁用"></el-checkbox>-->
<!--                        <el-checkbox label="选中且禁用"></el-checkbox>-->
<!--                    </el-checkbox-group>-->

                    <el-checkbox-group v-model="form.pushMethodList">
                        <el-checkbox v-for="item in pushMethodDictList" :key="item.uid" :label="item.dictValue">{{item.dictLabel}}</el-checkbox>
                    </el-checkbox-group>

                </el-form-item>

                <el-form-item
                    :label-width="formLabelWidth"
                    label="触达范围"
                    prop="level"
                >
                    <el-select
                        v-model="form.pushArea"
                        size="small"
                        placeholder="请选择"
                        style="width: 180px"
                    >
                        <el-option
                            v-for="item in pushAreaDictList"
                            :key="item.uid"
                            :label="item.dictLabel"
                            :value="parseInt(item.dictValue)"
                        />
                    </el-select>
                </el-form-item>

                <el-form-item
                    :label-width="formLabelWidth"
                    label="用户标签"
                    prop="level"
                    v-if="form.pushArea == 3"
                >
                    <el-select
                        v-model="form.userTag"
                        size="small"
                        placeholder="请选择"
                        style="width: 180px"
                    >
                        <el-option
                            v-for="item in userTagDictList"
                            :key="item.uid"
                            :label="item.dictLabel"
                            :value="parseInt(item.dictValue)"
                        />
                    </el-select>
                </el-form-item>

                <el-form-item
                    :label-width="formLabelWidth"
                    label="圈选用户"
                    prop="level"
                    v-if="form.pushArea == 2"
                >
                    <el-select
                        v-model="form.userUidList"
                        :remote-method="userRemoteMethod"
                        :loading="loading"
                        multiple
                        filterable
                        clearable
                        remote
                        reserve-keyword
                        placeholder="输入名称搜索更多"
                        style="width: 300px"
                        size="small"
                    >
                        <el-option
                            v-for="item in userOptions"
                            :key="item.uid"
                            :label="item.nickName"
                            :value="item.uid"
                        />
                    </el-select>
                </el-form-item>


                <el-form-item :label-width="formLabelWidth" label="触达内容" prop="content">
                    <CKEditor
                        ref="editor"
                        :content="form.content"
                        :height="400"
                    />
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
    getMessagePushList,
    addMessagePush,
    editMessagePush,
    pushMessagePush,
    deleteBatchMessagePush,
} from "@/api/messagePush";
import {getUserList} from "@/api/user";
import {getListByDictTypeList} from '@/api/sysDictData'
import CKEditor from '@/components/CKEditor'
export default {
    data() {
        return {
            multipleSelection: [], //多选，用于批量删除
            tableData: [],
            keyword: "",
            currentPage: 1,
            pageSize: 10,
            total: 0, //总数量
            title: "推送模板",
            dialogFormVisible: false, //控制弹出框
            formLabelWidth: "120px",
            isEditForm: false,
            orderByDescColumn: "",
            orderByAscColumn: "",
            loading: false, // 搜索框加载状态
            form: {
                uid: null,
                content: "",
                sortName: "",
                pushMethodList: [],
            },
            rules: {
                sortName: [
                    {required: true, message: '推送名称不能为空', trigger: 'blur'},
                    {min: 1, max: 10, message: '长度在1到10个字符'},
                ],
                sort: [
                    {required: true, message: '排序字段不能为空', trigger: 'blur'},
                    {pattern: /^[0-9]\d*$/, message: '排序字段只能为自然数'},
                ]
            },
            pushStatusDictList: [],
            pushMethodDictList: [],
            pushAreaDictList:   [],
            userTagDictList:    [],
            userOptions: [], // 用户搜索候选项
        };
    },
    components: {
        CKEditor,
    },
    created() {
        this.getDictList()
        this.messagePushList();
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
            this.messagePushList()
        },
        /**
         * 字典查询
         */
        getDictList: function () {
            let dictTypeList =  ['sys_push_status', 'sys_push_method', 'sys_push_area', 'sys_user_tag']
            getListByDictTypeList(dictTypeList).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    let dictMap = response.data;
                    this.pushStatusDictList = dictMap.sys_push_status.list
                    this.pushMethodDictList = dictMap.sys_push_method.list
                    this.pushAreaDictList = dictMap.sys_push_area.list
                    this.userTagDictList = dictMap.sys_user_tag.list
                }
            });
        },
        // 标签远程搜索函数
        userRemoteMethod: function (query) {
            if (query !== '') {
                this.queryUserList(query, null)
            } else {
                this.tagOptions = []
            }
        },
        queryUserList(query, userUidList) {
            const params = {}
            params.keyword = query
            params.userUidList = userUidList
            if (userUidList) {
                params.pageSize = userUidList.length
            } else {
                params.pageSize = 10
            }
            params.currentPage = 1
            getUserList(params).then(response => {
                console.log("getUserList", response);
                if (response.code == this.$ECode.SUCCESS) {
                    this.userOptions = response.data.records;
                }
            });
        },
        messagePushList: function() {
            let params = {};
            params.keyword = this.keyword;
            params.currentPage = this.currentPage;
            params.pageSize = this.pageSize;
            params.orderByDescColumn = this.orderByDescColumn
            params.orderByAscColumn = this.orderByAscColumn
            let array = []
            let userUidList = []
            getMessagePushList(params).then(response => {
                let tableData = response.data.records;
                for (let a = 0; a < tableData.length; a++) {
                    let data = tableData[a]
                    data.pushMethodList = JSON.parse(data.pushMethod)
                    if (data.pushUserUidList) {
                        data.userUidList = JSON.parse(data.pushUserUidList)
                        userUidList = userUidList.concat(data.userUidList)
                    }
                    array.push(data)
                }
                this.tableData = array
                this.currentPage = response.data.current;
                this.pageSize = response.data.size;
                this.total = response.data.total;

                if (userUidList.length > 0) {
                    this.queryUserList("", userUidList);
                }
            });


        },
        getFormObject: function() {
            let formObject = {
                uid: null,
                content: null,
                sortName: null,
                sort: 0,
                pushMethodList: [],
            };
            return formObject;
        },
        handleFind: function() {
            this.currentPage = 1
            this.messagePushList();
        },
        handleAdd: function() {
            this.title = "新增推送模板"
            this.dialogFormVisible = true;
            this.form = this.getFormObject();
            this.isEditForm = false;
        },
        handleEdit: function(row) {
            this.title = "编辑推送模板"
            this.dialogFormVisible = true;
            this.isEditForm = true;
            this.form = row;
        },
        handleDelete: function(row) {
            let that = this;
            this.$confirm("此操作将把推送删除, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    let params = [];
                    params.push(row);
                    deleteBatchMessagePush(params).then(response => {
                        if(response.code == this.$ECode.SUCCESS) {
                            this.$commonUtil.message.success(response.message)
                        } else {
                            this.$commonUtil.message.error(response.message)
                        }
                        that.messagePushList();
                    });
                })
                .catch(() => {
                    this.$commonUtil.message.info("已取消删除")
                });
        },
        handlePush: function(row) {
            let that = this;
            this.$confirm("此操作将会向用户推送消息, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    pushMessagePush(row).then(response => {
                        if(response.code == this.$ECode.SUCCESS) {
                            this.$commonUtil.message.success(response.message)
                        } else {
                            this.$commonUtil.message.error(response.message)
                        }
                        that.messagePushList();
                    });
                })
                .catch(() => {
                    this.$commonUtil.message.info("已取消删除")
                });
        },
        handleDeleteBatch: function() {
            let that = this;
            if(that.multipleSelection.length <= 0 ) {
                this.$commonUtil.message.error("请先选中需要删除的内容!")
                return;
            }
            this.$confirm("此操作将把选中的推送删除, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    deleteBatchMessagePush(that.multipleSelection).then(response => {
                        if(response.code == this.$ECode.SUCCESS) {
                            this.$commonUtil.message.success(response.message)
                        } else {
                            this.$commonUtil.message.error(response.message)
                        }
                        that.messagePushList();
                    });
                })
                .catch(() => {
                    this.$commonUtil.message.info("已取消删除！")
                });
        },
        handleCurrentChange: function(val) {
            this.currentPage = val;
            this.messagePushList();
        },
        submitForm: function() {
            this.$refs.form.validate((valid) => {
                if(!valid) {
                    console.log('校验失败')
                    return;
                } else {
                    this.form.content = this.$refs.editor.getData()
                    this.form.pushMethod = JSON.stringify(this.form.pushMethodList)
                    this.form.pushUserUidList = JSON.stringify(this.form.userUidList)
                    if (this.isEditForm) {
                        editMessagePush(this.form).then(response => {
                            console.log(response);
                            if (response.code == this.$ECode.SUCCESS) {
                                this.$commonUtil.message.success(response.message)
                                this.dialogFormVisible = false;
                                this.messagePushList();
                            } else {
                                this.$commonUtil.message.error(response.message)
                            }
                        });
                    } else {
                        addMessagePush(this.form).then(response => {
                            console.log(response);
                            if (response.code == this.$ECode.SUCCESS) {
                                this.$commonUtil.message.success(response.message)
                                this.dialogFormVisible = false;
                                this.messagePushList();
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
