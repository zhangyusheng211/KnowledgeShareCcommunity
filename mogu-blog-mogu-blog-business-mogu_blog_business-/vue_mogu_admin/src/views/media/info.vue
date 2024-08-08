<template>
    <div class="app-container">
        <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
            <el-form-item label="标题" prop="title">
                <el-input
                    v-model="queryParams.title"
                    placeholder="请输入标题"
                    clearable
                    size="small"
                    @keyup.enter.native="handleQuery"
                />
            </el-form-item>
            <el-form-item label="课程类型" prop="movieType">
                <el-select v-model="queryParams.type" placeholder="请选择课程类型" clearable size="small">
                    <el-option
                        v-for="dict in typeOptions"
                        :key="dict.dictValue"
                        :label="dict.dictLabel"
                        :value="dict.dictValue"
                    ></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="课程分类" prop="categoryId">
                <el-select v-model="queryParams.categoryId" placeholder="请选择课程分类" @change="handleQuery" clearable
                           size="small">
                    <el-option
                        v-for="category in categoryOptions"
                        :key="category.uid"
                        :label="category.name"
                        :value="category.uid"
                    />
                </el-select>
            </el-form-item>

            <el-form-item label="标签" prop="tagId">
                <el-select
                    v-model="queryParams.tagIdList"
                    placeholder="请选择标签"
                    clearable
                    @change="handleQuery"
                    multiple size="small">
                    <el-option
                        v-for="tag in tagOptions"
                        :key="tag.uid"
                        :label="tag.content"
                        :value="tag.uid"
                    />
                </el-select>
            </el-form-item>

            <el-form-item label="国家" prop="country">
                <el-select v-model="queryParams.country" clearable placeholder="请输入国家">
                    <el-option
                        v-for="dict in countryOptions"
                        :key="dict.dictValue"
                        :label="dict.dictLabel"
                        :value="dict.dictValue"
                    ></el-option>
                </el-select>
            </el-form-item>


            <el-form-item label="状态" prop="status">
                <el-select v-model="queryParams.status" placeholder="请选择状态" clearable size="small">
                    <el-option
                        v-for="dict in statusOptions"
                        :key="dict.dictValue"
                        :label="dict.dictLabel"
                        :value="dict.dictValue"
                    ></el-option>
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
                <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
            </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button
                    type="primary"
                    icon="el-icon-plus"
                    size="mini"
                    @click="handleAdd"
                >添加课程
                </el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button
                    type="success"
                    icon="el-icon-edit"
                    size="mini"
                    :disabled="single"
                    @click="handleUpdate"

                >修改
                </el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button
                    type="danger"
                    icon="el-icon-delete"
                    size="mini"
                    :disabled="multiple"
                    @click="handleDelete"
                >删除
                </el-button>
            </el-col>
            <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="infoList" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55" align="center"/>
            <el-table-column prop="images" label="封面" align="center" width="100">
                <template slot-scope="scope">
                    <el-image :src="scope.row.images" lazy/>
                </template>
            </el-table-column>
            <el-table-column label="标题" align="left" show-overflow-tooltip prop="title" width="200"/>
            <el-table-column label="课程类型" align="center" prop="type" width="100">
                <template slot-scope="scope">
                    <dict-tag :options="typeOptions" :value="scope.row.type"/>
                </template>
            </el-table-column>
            <el-table-column label="课程分类" align="center" prop="categoryName" width="100">
                <template slot-scope="scope">
                    <el-tag v-if="scope.row.categoryName">
                        {{ scope.row.categoryName }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="国家" align="center" prop="country" width="100">
                <template slot-scope="scope">
                    <dict-tag :options="countryOptions" :value="scope.row.country"/>
                </template>
            </el-table-column>
            <el-table-column label="发布人" align="center" prop="publishUsername" width="100"/>
            <el-table-column label="发布时间" align="center" prop="publishTime">
                <template slot-scope="scope">
                    <span>{{ scope.row.publishTime }}</span>
                </template>
            </el-table-column>
            <el-table-column label="状态" align="center" prop="status">
                <template slot-scope="scope">
                    <dict-tag :options="statusOptions" :value="scope.row.status"/>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="250" fixed="right" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                    <el-button
                        size="mini"
                        type="primary"
                        icon="el-icon-edit"
                        @click="handleUpdate(scope.row)"
                    >编辑
                    </el-button>
                    <el-button
                        size="mini"
                        type="danger"
                        icon="el-icon-delete"
                        @click="handleDelete(scope.row)"
                    >删除
                    </el-button>
                </template>
            </el-table-column>
        </el-table>

        <pagination
            v-show="total>0"
            :total="total"
            :page.sync="queryParams.currentPage"
            :limit.sync="queryParams.pageSize"
            @pagination="getList"
        />

    </div>
</template>

<script>
import {delMovieInfo, getMediaInfoList} from "@/api/mediaInfo";
import {getListByDictTypeList} from "@/api/sysDictData"
import {listTag} from "@/api/mediaTag";
import {listCategory} from "@/api/mediaCategory";

export default {
    name: "MediaInfo",
    components: {},
    data() {
        return {
            // 遮罩层
            loading: true,
            // 选中数组
            ids: [],
            // 非单个禁用
            single: true,
            // 非多个禁用
            multiple: true,
            // 显示搜索条件
            showSearch: true,
            // 总条数
            total: 0,
            // 课程管理表格数据
            infoList: [],
            // 弹出层标题
            title: "",
            // 是否显示弹出层
            open: false,
            // 查询参数
            queryParams: {
                currentPage: 1,
                pageSize: 10,
                title: null,
                movieType: null,
                categoryId: null,
                country: null,
                tagId: null,
                description: null,
                publishBy: null,
                publishTime: null,
                status: null,
                openComment: null,
                openDownload: null,
                tagIdList: null
            },
            //课程国家字典
            countryOptions: [],
            //状态字典
            statusOptions: [],
            //课程类型
            typeOptions: [],
            //标签字典
            tagOptions: [],
            //分类字典
            categoryOptions: [],
            // 表单参数
            form: {},

            // 表单校验
            rules: {
                title: [
                    {required: true, message: "标题不能为空", trigger: "blur"}
                ],
            }
        };
    },
    watch: {
        "$route": {
            handler(route) {
                this.getList();
            }
        }
    },
    created() {
        this.getList();
        // 获取字典
        this.getDictList();
    },
    mounted() {
        this.getList();
    },
    methods: {
        handleCurrentChange: function (val) {
            this.queryParams.currentPage = val;
            this.getList();
        },
        /** 查询课程管理列表 */
        getList() {
            this.loading = true;
            getMediaInfoList(this.queryParams).then(response => {
                this.infoList = response.data.records;
                this.total = response.data.total;
                this.loading = false;
            });
        },
        /**
         * 字典查询
         */
        getDictList() {
            let params = {}
            params.status = 1
            params.pageSize = 100
            params.currentPage = 1
            listCategory(params).then(response => {
                this.categoryOptions = response.data.records;
            });
            listTag(params).then(response => {
                this.tagOptions = response.data.records;
            });
            const dictTypeList = ['media_country', 'sys_publish_status', 'media_type'];
            getListByDictTypeList(dictTypeList).then(response => {
                this.countryOptions = response.data.media_country.list;
                this.statusOptions = response.data.sys_publish_status.list;
                this.typeOptions = response.data.media_type.list;
            });
        },
        // 取消按钮
        cancel() {
            this.open = false;
            this.reset();
        },
        /** 搜索按钮操作 */
        handleQuery() {
            this.queryParams.currentPage = 1;
            this.getList();
        },
        /** 重置按钮操作 */
        resetQuery() {
            this.resetForm("queryForm");
            this.handleQuery();
        },
        // 表单重置
        resetForm(refName) {
            if (this.$refs[refName]) {
                this.$refs[refName].resetFields();
            }
        },
        // 多选框选中数据
        handleSelectionChange(selection) {
            this.ids = selection.map(item => item.uid);
            this.single = selection.length !== 1;
            this.multiple = !selection.length;
        },
        /** 新增按钮操作 */
        handleAdd() {
            this.$router.push({path: '/media/info/detail/add'})
            //this.$router.push({path:'/media/info/detail/', query: { uid: uidValue}});
        },
        /** 修改按钮操作 */
        handleUpdate(row) {
            const uidValue = row.uid || this.ids;
            this.$router.push('/media/info/detail/' + uidValue);
            //this.$router.push({path:'/media/info/detail/', query: { uid: uidValue}});
        },
        /** 删除按钮操作 */
        handleDelete(row) {
            let uids = '';
            if (row.uid) {
                uids = [row.uid];
            } else {
                uids = this.ids
            }
            this.$confirm('是否确认删除课程信息管理编号为"' + uids + '"的数据项?', "警告", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            }).then(function () {
                return delMovieInfo(uids);
            }).then(() => {
                this.getList();
                this.$commonUtil.message.success("删除成功");
            })
        }
    }
};
</script>

