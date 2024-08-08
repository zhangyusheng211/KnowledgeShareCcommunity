<template>
    <div class="app-container">
        <!-- 查询和其他操作 -->
        <div class="filter-container" style="margin: 10px 0 10px 0;" v-permission="'/subjectItem/sortByCreateTime'">
            <el-button
                class="filter-item"
                type="info"
                @click="handleSortByCreateTime(true)"
                icon="el-icon-document"
                size="small"
            >按创建时间倒排
            </el-button>

            <el-button
                class="filter-item"
                type="info"
                @click="handleSortByCreateTime(false)"
                icon="el-icon-document"
                size="small"
            >按创建时间正排
            </el-button>

            <el-button class="filter-item" type="danger" @click="handleDeleteBatch" size="small" icon="el-icon-delete"
                       v-permission="'/blog/deleteBatch'">删除选中
            </el-button>
        </div>

        <aside>
            在博客管理添加勾选博客添加专题，通过拖拽实现专题内列表的排序; <br>
            当专栏设置了访问限制，专栏下所有的文章都会遵循该限制，除非标记试读进行豁免；
        </aside>

        <el-table ref="dragTable" :data="list" @selection-change="handleSelectionChange" row-key="id" border fit
                  highlight-current-row style="width: 100%">
            <el-table-column type="selection"></el-table-column>
            <el-table-column label="标题图" width="180px" align="center">
                <template slot-scope="{row}">
                    <el-image :preview-src-list="row.blog.photoList" v-if="row.blog.photoList"
                              :src="row.blog.photoList[0]"
                              style="width: 130px;height: 70px;"
                    ></el-image>
                </template>
            </el-table-column>

            <el-table-column width="250px" label="标题" align="center">
                <template slot-scope="{row}">
                    <el-tooltip @click.native="onClick(row)" class="item" effect="dark" :content="row.blog.title"
                                placement="top">
                        <span style="cursor:pointer;">{{ strSubstring(row.blog.title, 20) }}</span>
                    </el-tooltip>
                </template>
            </el-table-column>

            <el-table-column width="150px" label="作者" align="center">
                <template slot-scope="{row}">
<!--                    <UserAvatar :user="row.blog.user"></UserAvatar>-->
                    <span>{{ row.blog.author }}</span>
                </template>
            </el-table-column>

            <el-table-column width="100px" label="是否原创" align="center">
                <template slot-scope="{row}">
                    <el-tag v-if="row.blog.isOriginal==1" type="success">原创</el-tag>
                    <el-tag v-if="row.blog.isOriginal==0" type="info">转载</el-tag>
                </template>
            </el-table-column>

            <el-table-column width="100px" label="分类" align="center">
                <template slot-scope="{row}">
                    <span>{{ row.blog.blogSort.sortName }}</span>
                </template>
            </el-table-column>

            <el-table-column width="200px" label="标签" align="center">
                <template slot-scope="{row}">
                    <template>
                        <el-tag
                            style="margin-left: 3px"
                            type="warning"
                            v-if="item"
                            :key="index"
                            v-for="(item, index) in row.blog.tagList"
                        >{{ item.content }}
                        </el-tag>
                    </template>
                </template>
            </el-table-column>

            <el-table-column
                label="开启试读"
                width="120"
                align="center"
                prop="tryRead"
                sortable="custom"
            >
                <template slot-scope="scope">
                    <template>
                        <el-switch
                            v-model="scope.row.tryReadStatus"
                            active-color="#F5DEB3"
                            @change="handChange(scope.row)"
                        />
                    </template>
                </template>
            </el-table-column>

            <el-table-column label="排序" width="100" align="center">
                <template slot-scope="scope">
                    <el-tag type="warning">{{ scope.row.sort }}</el-tag>
                </template>
            </el-table-column>

            <el-table-column width="100px" label="操作" align="center">
                <template slot-scope="{row}">
                    <el-button @click="handleDelete(row)" type="danger" size="small"
                               v-permission="'/subjectItem/delete'">删除
                    </el-button>
                </template>
            </el-table-column>

            <el-table-column align="center" label="拖拽排序" width="80">
                <template slot-scope="{}">
                    <svg-icon class="drag-handler" icon-class="drag"/>
                </template>
            </el-table-column>
        </el-table>
    </div>
</template>

<script>
import {deleteBatchSubjectItem, editSubjectItem, getSubjectItemList, sortByCreateTime} from '@/api/subjectItem'
import Sortable from 'sortablejs'
import UserAvatar from "../../components/UserAvatar"

export default {
    name: 'DragTable',
    filters: {
        statusFilter(status) {
            const statusMap = {
                published: 'success',
                draft: 'info',
                deleted: 'danger'
            }
            return statusMap[status]
        }
    },
    components: {
        UserAvatar
    },
    data() {
        return {
            list: [],
            total: null,
            listLoading: true,
            multipleSelection: [], //多选，用于批量删除
            BLOG_WEB_URL: process.env.BLOG_WEB_URL,
            subjectUid: "",
            listQuery: {
                page: 1,
                limit: 10
            },
            sortable: null,
            oldList: [],
            newList: []
        }
    },
    created() {
        //传递过来的pictureSordUid
        this.subjectUid = this.$route.query.subjectUid;
        this.getList()
    },
    methods: {
        // 根据创建时间对专题进行排序
        handleSortByCreateTime: function (isDesc) {
            if (this.list.length == 0) {
                this.$commonUtil.message.error("没有专题元素，无法进行排序")
                return
            }

            this.$confirm(
                "此操作将根据博客创建时间对所有的专题元素进行升序&降序排列, 是否继续?",
                "提示",
                {
                    confirmButtonText: "确定",
                    cancelButtonText: "取消",
                    type: "warning"
                }
            )
                .then(() => {
                    let params = new URLSearchParams()
                    params.append('subjectUid', this.subjectUid)
                    params.append('isDesc', isDesc)
                    sortByCreateTime(params).then(response => {
                        if (response.code == this.$ECode.SUCCESS) {
                            this.$commonUtil.message.success(response.message)
                            this.getList();
                        } else {
                            this.$commonUtil.message.error(response.message)
                        }
                    });
                })
                .catch(() => {
                    this.$commonUtil.message.info("已取消批量排序")
                });
        },
        handChange(row) {
            if (row.tryReadStatus) {
                row.tryRead = '1'
            } else {
                row.tryRead = '0'
            }

            let list = this.list
            let subjectList = []
            for (let a = list.length - 1; a >= 0; a--) {
                let params = {}
                params.uid = list[a].uid
                params.blogUid = list[a].blogUid
                params.subjectUid = list[a].subjectUid
                params.sort = list.length - a
                if (list[a].uid === row.uid) {
                    params.tryRead = row.tryRead
                }
                subjectList.push(params)
            }
            editSubjectItem(subjectList).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    this.$commonUtil.message.success(response.message)
                    this.$router.go(0);
                }
            })

        },
        getList() {
            // TODO 这里暂时没有做成分页而是全部显示，考虑到分页后不太好拖拽
            var params = {};
            params.subjectUid = this.subjectUid;
            params.pageSize = 100;
            params.currentPage = 1;
            getSubjectItemList(params).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    let tableData = response.data.records
                    for (let i = 0; i < tableData.length; i++) {
                        if (tableData[i].tryRead == 1) {
                            tableData[i].tryReadStatus = true
                        } else {
                            tableData[i].tryReadStatus = false
                        }
                    }
                    this.list = tableData
                    this.total = response.total
                    this.$nextTick(() => {
                        this.setSort()
                    })
                }
            })
        },
        strSubstring(str, count) {
            return this.$commonUtil.splitStr(str, count)
        },
        handleDelete: function (row) {
            this.$confirm("此操作将把博客移除该专辑, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    var params = {};
                    params.uid = row.uid;
                    let subjectItemList = [params]
                    deleteBatchSubjectItem(subjectItemList).then(response => {
                        if (response.code == this.$ECode.SUCCESS) {
                            this.$commonUtil.message.success(response.message)
                        } else {
                            this.$commonUtil.message.error(response.message)
                        }
                        this.getList();
                    });
                })
                .catch(() => {
                    this.$commonUtil.message.info("已取消删除")
                });
        },
        handleDeleteBatch: function (row) {
            if (this.multipleSelection.length <= 0) {
                this.$commonUtil.message.error("请先选中需要删除的专题元素")
                return;
            }
            this.$confirm("此操作将把选中专题元素删除, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    deleteBatchSubjectItem(this.multipleSelection).then(response => {
                        if (response.code == this.$ECode.SUCCESS) {
                            this.$commonUtil.message.success(response.message)
                            this.getList();
                        } else {
                            this.$commonUtil.message.error(response.message)
                        }
                    });
                })
                .catch(() => {
                    this.$commonUtil.message.info("已取消删除")
                });
        },

        // 跳转到该博客详情
        onClick: function (row) {
            window.open(this.BLOG_WEB_URL + "/#/info?blogOid=" + row.blog.oid);
        },
        // 改变多选
        handleSelectionChange(val) {
            this.multipleSelection = val;
        },
        setSort() {
            const el = this.$refs.dragTable.$el.querySelectorAll('.el-table__body-wrapper > table > tbody')[0]
            this.sortable = Sortable.create(el, {
                ghostClass: 'sortable-ghost', // Class name for the drop placeholder,
                setData: function (dataTransfer) {
                    dataTransfer.setData('Text', '')
                },
                onEnd: evt => {
                    let list = this.list
                    const targetRow = list.splice(evt.oldIndex, 1)[0]
                    list.splice(evt.newIndex, 0, targetRow)
                    let subjectList = []
                    for (let a = list.length - 1; a >= 0; a--) {
                        let params = {}
                        params.uid = list[a].uid
                        params.blogUid = list[a].blogUid
                        params.subjectUid = list[a].subjectUid
                        params.sort = list.length - a
                        subjectList.push(params)
                    }
                    editSubjectItem(subjectList).then(response => {
                        if (response.code == this.$ECode.SUCCESS) {
                            this.$commonUtil.message.success(response.message)
                            this.$router.go(0);
                        }
                    })
                }
            })
        }
    }
}
</script>

<style lang="scss">
.sortable-ghost {
    opacity: .8;
    color: #fff !important;
    background: #42b983 !important;
}

aside {
    background: #eef1f6;
    padding: 8px 24px;
    margin-bottom: 20px;
    border-radius: 2px;
    display: block;
    line-height: 32px;
    font-size: 16px;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen, Ubuntu, Cantarell, "Fira Sans", "Droid Sans", "Helvetica Neue", sans-serif;
    color: #2c3e50;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;

    a {
        color: #337ab7;
        cursor: pointer;

        &:hover {
            color: rgb(32, 160, 255);
        }
    }
}
</style>

<style scoped>
.icon-star {
    margin-right: 2px;
}

.drag-handler {
    width: 20px;
    height: 20px;
    cursor: pointer;
}

.show-d {
    margin-top: 15px;
}
</style>
