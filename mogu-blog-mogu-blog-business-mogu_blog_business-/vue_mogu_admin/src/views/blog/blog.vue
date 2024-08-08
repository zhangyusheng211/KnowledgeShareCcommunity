<template>
    <div class="app-container">
        <!-- 查询和其他操作 -->
        <div
            v-permission="'/blog/getList'"
            class="filter-container"
            style="margin: 10px 0 10px 0"
        >
            <el-form
                v-show="showSearch"
                :inline="true"
                label-width="68px"
                style="margin-bottom: 8px"
            >
                <el-input
                    v-model="queryParams.keyword"
                    clearable
                    class="filter-item"
                    style="width: 150px"
                    size="small"
                    placeholder="请输入博客名"
                    @keyup.enter.native="handleFind"
                />

                <el-select
                    v-model="queryParams.sortKeyword"
                    :remote-method="sortRemoteMethod"
                    :loading="loading"
                    style="width: 140px"
                    filterable
                    clearable
                    remote
                    reserve-keyword
                    placeholder="请输入分类名"
                    size="small"
                    @keyup.enter.native="handleFind"
                >
                    <el-option
                        v-for="item in sortOptions"
                        :key="item.uid"
                        :label="item.sortName"
                        :value="item.uid"
                    />
                </el-select>

                <el-select
                    v-model="queryParams.tagKeyword"
                    :remote-method="tagRemoteMethod"
                    :loading="loading"
                    filterable
                    clearable
                    remote
                    reserve-keyword
                    placeholder="请输入标签名"
                    style="width: 140px"
                    size="small"
                    @keyup.enter.native="handleFind"
                >
                    <el-option
                        v-for="item in tagOptions"
                        :key="item.uid"
                        :label="item.content"
                        :value="item.uid"
                    />
                </el-select>

                <el-select
                    v-model="queryParams.levelKeyword"
                    clearable
                    placeholder="推荐等级"
                    style="width: 110px"
                    size="small"
                >
                    <el-option
                        v-for="item in blogLevelDictList"
                        :key="item.uid"
                        :label="item.dictLabel"
                        :value="item.dictValue"
                    />
                </el-select>

                <el-select
                    v-model="queryParams.publishKeyword"
                    clearable
                    placeholder="是否发布"
                    style="width: 110px"
                    size="small"
                >
                    <el-option
                        v-for="item in blogPublishDictList"
                        :key="item.uid"
                        :label="item.dictLabel"
                        :value="item.dictValue"
                    />
                </el-select>

                <el-select
                    v-model="queryParams.originalKeyword"
                    clearable
                    placeholder="是否原创"
                    style="width: 110px"
                    size="small"
                >
                    <el-option
                        v-for="item in blogOriginalDictList"
                        :key="item.uid"
                        :label="item.dictLabel"
                        :value="item.dictValue"
                    />
                </el-select>

                <el-select
                    v-model="queryParams.typeKeyword"
                    clearable
                    placeholder="文章类型"
                    style="width: 110px"
                    size="small"
                >
                    <el-option
                        v-for="item in blogTypeDictList"
                        :key="item.uid"
                        :label="item.dictLabel"
                        :value="item.dictValue"
                    />
                </el-select>

                <el-select
                    v-model="queryParams.openCommentKeyword"
                    clearable
                    placeholder="开启评论"
                    style="width: 110px"
                    size="small"
                >
                    <el-option
                        v-for="item in openDictList"
                        :key="item.uid"
                        :label="item.dictLabel"
                        :value="item.dictValue"
                    />
                </el-select>

                <el-select
                    v-model="queryParams.visitAuthKeyword"
                    clearable
                    placeholder="访问权限"
                    style="width: 110px"
                    size="small"
                >
                    <el-option
                        v-for="item in visitAuthDictList"
                        :key="item.uid"
                        :label="item.dictLabel"
                        :value="item.dictValue"
                    />
                </el-select>

                <el-select
                    v-model="queryParams.onlySubjectShowKeyword"
                    clearable
                    placeholder="仅专栏可见"
                    style="width: 130px"
                    size="small"
                >
                    <el-option
                        v-for="item in yesNoDictList"
                        :key="item.uid"
                        :label="item.dictLabel"
                        :value="item.dictValue"
                    />
                </el-select>

                <el-select
                    v-model="queryParams.auditStatusKeyword"
                    clearable
                    placeholder="审核状态"
                    style="width: 110px"
                    size="small"
                >
                    <el-option
                        v-for="item in auditStatusDictList"
                        :key="item.uid"
                        :label="item.dictLabel"
                        :value="item.dictValue"
                    />
                </el-select>

                <!--                <el-select-->
                <!--                    v-model="queryParams.contributeKeyword"-->
                <!--                    clearable-->
                <!--                    placeholder="投稿方式"-->
                <!--                    style="width: 110px"-->
                <!--                    size="small"-->
                <!--                >-->
                <!--                    <el-option-->
                <!--                        v-for="item in articleDictList"-->
                <!--                        :key="item.uid"-->
                <!--                        :label="item.dictLabel"-->
                <!--                        :value="item.dictValue"-->
                <!--                    />-->
                <!--                </el-select>-->

                <!--                <el-select-->
                <!--                    v-model="queryParams.containsSubjectKeyword"-->
                <!--                    clearable-->
                <!--                    placeholder="包含专栏"-->
                <!--                    style="width: 110px"-->
                <!--                    size="small"-->
                <!--                >-->
                <!--                    <el-option-->
                <!--                        v-for="item in yesNoDictList"-->
                <!--                        :key="item.uid"-->
                <!--                        :label="item.dictLabel"-->
                <!--                        :value="item.dictValue"-->
                <!--                    />-->
                <!--                </el-select>-->

                <el-button
                    v-permission="'/blog/getList'"
                    style="margin-left: 10px"
                    class="filter-item"
                    type="primary"
                    icon="el-icon-search"
                    @click="handleFind"
                    size="small"
                >查找
                </el-button>
            </el-form>

            <el-row :gutter="10" style="margin-top: 10px">
                <el-col :span="1.5">
                    <el-button
                        v-permission="'/blog/add'"
                        class="filter-item"
                        type="primary"
                        icon="el-icon-edit"
                        size="small"
                        @click="handleAdd"
                    >添加博客
                    </el-button>
                </el-col>

                <el-col :span="1.5">
                    <el-button
                        v-permission="'/blog/uploadLocalBlog'"
                        class="filter-item"
                        type="warning"
                        icon="el-icon-star-on"
                        @click="handleUpload"
                        size="small"
                    >本地上传
                    </el-button>
                </el-col>

                <el-col :span="1.5">
                    <el-button
                        v-permission="'/blog/downloadBatch'"
                        class="filter-item"
                        type="warning"
                        icon="el-icon-s-flag"
                        @click="handleDownload"
                        size="small"
                    >导出选中
                    </el-button>
                </el-col>

                <el-col :span="1.5">
                    <el-button
                        v-permission="'/blog/downloadBatch'"
                        class="filter-item"
                        type="info"
                        icon="el-icon-folder-opened"
                        @click="handleSubject"
                        size="small"
                    >添加专栏
                    </el-button>
                </el-col>

                <el-col :span="1.5">
                    <el-button
                        v-permission="'/blog/pushBatch'"
                        class="filter-item"
                        type="info"
                        icon="el-icon-folder-opened"
                        @click="handlePushBatch"
                        size="small"
                    >百度SEO推送
                    </el-button>
                </el-col>

                <el-col :span="1.5">
                    <el-button
                        v-permission="'/blog/deleteBatch'"
                        class="filter-item"
                        type="danger"
                        icon="el-icon-delete"
                        @click="handleDeleteBatch"
                        size="small"
                    >删除选中
                    </el-button>
                </el-col>
                <right-toolbar
                    :show-search.sync="showSearch"
                    @queryTable="resetBlogList"
                />
            </el-row>
        </div>

        <el-table
            ref="articleTable"
            :data="tableData"
            :default-sort="{ prop: 'createTime', order: 'descending' }"
            style="width: 100%"
            @selection-change="handleSelectionChange"
            @sort-change="changeSort"
        >
            <el-table-column type="selection"/>

            <el-table-column label="序号" width="60" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.$index + 1 }}</span>
                </template>
            </el-table-column>

            <el-table-column label="标题图" width="140" align="center">
                <template slot-scope="scope">
                    <img
                        v-if="scope.row.photoList && scope.row.photoList[0]"
                        :src="scope.row.photoList[0]"
                        style="width: 130px; height: 70px"
                    />
                </template>
            </el-table-column>

            <el-table-column label="标题" width="160" align="center">
                <template slot-scope="scope">
          <span style="cursor: pointer" @click="onClick(scope.row)">{{
                  scope.row.title
              }}</span>
                </template>
            </el-table-column>

            <el-table-column label="作者" width="200" align="center">
                <template slot-scope="scope">
                    <UserAvatar :user="scope.row.user"></UserAvatar>
                </template>
            </el-table-column>

            <el-table-column label="分类" width="100" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.blogSort.sortName }}</span>
                </template>
            </el-table-column>

            <el-table-column label="标签" width="150" align="center">
                <template slot-scope="scope">
                    <template>
                        <el-tag
                            v-for="(item, index) in scope.row.tagList"
                            v-if="item"
                            :key="index"
                            :type="getTypeClass(item.sort)"
                            style="margin-left: 3px"
                            hit
                            size="mini"
                            effect="light"
                        >{{ item.content }}
                        </el-tag>
                    </template>
                </template>
            </el-table-column>

            <el-table-column
                :sort-by="['isOriginal']"
                label="是否原创"
                width="100"
                align="center"
                prop="isOriginal"
                sortable="custom"
            >
                <template slot-scope="scope">
                    <el-tag v-if="scope.row.isOriginal == 1" type="success">原创</el-tag>
                    <el-tag v-if="scope.row.isOriginal == 0" type="info">转载</el-tag>
                </template>
            </el-table-column>

            <el-table-column
                :sort-by="['level']"
                label="推荐等级"
                width="100"
                align="center"
                prop="level"
                sortable="custom"
            >
                <template slot-scope="scope">
                    <el-tag
                        v-for="item in blogLevelDictList"
                        v-if="scope.row.level == item.dictValue"
                        :key="item.uid"
                        :type="item.listClass"
                    >{{ item.dictLabel }}
                    </el-tag>
                </template>
            </el-table-column>

            <el-table-column
                :sort-by="['auditStatus']"
                label="审批状态"
                width="100"
                align="center"
                prop="level"
                sortable="custom"
            >
                <template slot-scope="scope">
                    <el-tag
                        v-for="item in auditStatusDictList"
                        v-if="scope.row.auditStatus == item.dictValue"
                        :key="item.uid"
                        :type="item.listClass"
                    >
                        <el-tooltip
                            v-if="scope.row.auditStatus == 1"
                            class="item"
                            placement="top"
                        >
                            <div slot="content">{{ scope.row.rejectReason }}</div>
                            <div>{{ item.dictLabel }}</div>
                        </el-tooltip>
                        <span v-else>
              {{ item.dictLabel }}
            </span>
                    </el-tag>
                </template>
            </el-table-column>

            <el-table-column
                :sort-by="['clickCount']"
                label="点击数"
                width="90"
                align="center"
                prop="clickCount"
                sortable="custom"
            >
                <template slot-scope="scope">
                    <span>{{ scope.row.clickCount }}</span>
                </template>
            </el-table-column>

            <el-table-column
                :sort-by="['isOnlySubjectShow']"
                label="仅专栏可见"
                width="120"
                align="center"
                prop="isOnlySubjectShow"
                sortable="custom"
            >
                <template slot-scope="scope">
                    <template>
                        <el-switch
                            v-model="scope.row.isOnlySubjectShowStatus"
                            active-color="#F5DEB3"
                            @change="handChangeBlog(scope.row)"
                        />
                    </template>
                </template>
            </el-table-column>

            <el-table-column
                :sort-by="['openComment']"
                label="开启评论"
                width="100"
                align="center"
                prop="openComment"
                sortable="custom"
            >
                <template slot-scope="scope">
                    <template>
                        <el-switch
                            v-model="scope.row.openCommentStatus"
                            active-color="#F5DEB3"
                            @change="handChangeBlog(scope.row)"
                        />
                    </template>
                </template>
            </el-table-column>

            <el-table-column
                :sort-by="['visitAuth']"
                label="访问权限"
                width="100"
                align="center"
                prop="level"
                sortable="custom"
            >
                <template slot-scope="scope">
                    <span></span>
                    <el-tag
                        v-for="item in visitAuthDictList"
                        v-if="scope.row.visitAuth.split(',').includes(item.dictValue)"
                        :key="item.uid"
                        :type="item.listClass"
                        style="margin-right: 2px; margin-top: 2px"
                    >{{ item.dictLabel }}
                    </el-tag>
                </template>
            </el-table-column>

            <el-table-column
                :sort-by="['isTop']"
                label="置顶"
                width="80"
                align="center"
                prop="isTop"
                sortable="custom"
            >
                <template slot-scope="scope">
                    <template>
                        <el-switch
                            v-model="scope.row.openTopStatus"
                            active-color="#F5DEB3"
                            @change="handChangeBlog(scope.row)"
                        />
                    </template>
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
                    </el-tag>
                </template>
            </el-table-column>

            <!--            <el-table-column-->
            <!--                :sort-by="['articleStatus']"-->
            <!--                label="文章来源"-->
            <!--                width="100"-->
            <!--                align="center"-->
            <!--                prop="questionStatus"-->
            <!--                sortable="custom"-->
            <!--            >-->
            <!--                <template slot-scope="scope">-->
            <!--                    <template>-->
            <!--                        <el-tag-->
            <!--                            v-for="item in articleDictList"-->
            <!--                            v-if="scope.row.articleSource == item.dictValue"-->
            <!--                            :key="item.uid"-->
            <!--                            :type="item.listClass"-->
            <!--                        >{{ item.dictLabel }}-->
            <!--                        </el-tag-->
            <!--                        >-->
            <!--                    </template>-->
            <!--                </template>-->
            <!--            </el-table-column>-->

            <el-table-column
                :sort-orders="['ascending', 'descending']"
                label="创建时间"
                width="160"
                align="center"
                prop="createTime"
                sortable="custom"
            >
                <template slot-scope="scope">
                    <span>{{ scope.row.createTime }}</span>
                </template>
            </el-table-column>

            <el-table-column label="排序" width="50" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.sort }}</span>
                </template>
            </el-table-column>

            <el-table-column label="所属专栏" width="150" align="center">
                <template slot-scope="scope">
                    <template>
                        <el-tag
                            @click.native="goSubjectDetail(item)"
                            v-for="(item, index) in scope.row.subjectList"
                            v-if="item"
                            :key="index"
                            :type="getTypeClass(item.sort)"
                            style="margin-left: 3px; cursor: pointer"
                            hit
                            size="mini"
                            effect="light"
                        >{{ item.subjectName }}
                        </el-tag>
                    </template>
                </template>
            </el-table-column>

            <el-table-column label="操作" fixed="right" min-width="147">
                <template slot-scope="scope">
                    <!-- 发布状态为未发布 且已通过审核  可以选择发布 -->
                    <el-tooltip content="发布" placement="top-end" effect="light">
                        <el-button
                            v-permission="'/blog/publish'"
                            v-if="scope.row.isPublish == 0 && scope.row.auditStatus == 2"
                            type="success"
                            size="mini"
                            icon="el-icon-document-checked"
                            circle
                            @click="handlePublish(scope.row)"
                        />
                    </el-tooltip>

                    <!-- 发布状态为已发布 可以选择下架 当管理员下架文章时文章的审核状态会被变更为未审核 -->
                    <el-tooltip content="下架" placement="top-end" effect="light">
                        <el-button
                            v-permission="'/blog/publish'"
                            v-if="
                scope.row.isPublish == 1 &&
                scope.row.auditStatus != 0 &&
                scope.row.auditStatus != 1
              "
                            type="info"
                            size="mini"
                            icon="el-icon-document-delete"
                            circle
                            @click="handlePublish(scope.row)"
                        />
                    </el-tooltip>

                    <!-- 审核状态为未审核/审核不通过 发布状态为已发布   -->
                    <!--  因目前用户修改文章后审核状态都会变为未审核 审核不通过的条件会很少  只是作为审核错误的修改项        -->
                    <!-- 后台上传的，支持再次审批-->
                    <el-tooltip content="审核" placement="top-end" effect="light">
                        <el-button
                            v-permission="'/blog/audit'"
                            v-if="
                (scope.row.auditStatus == 0 || scope.row.auditStatus == 1) &&
                (scope.row.isPublish == 1 || scope.row.articleSource == 0)
              "
                            type="success"
                            size="mini"
                            icon="el-icon-user"
                            circle
                            @click="handleAudit(scope.row)"
                        />
                    </el-tooltip>

                    <!-- 修改 -->
                    <el-tooltip content="编辑" placement="top-end" effect="light">
                        <el-button
                            v-permission="'/blog/edit'"
                            type="primary"
                            size="mini"
                            icon="el-icon-edit"
                            circle
                            @click="handleEdit(scope.row)"
                        />
                    </el-tooltip>

                    <!-- 删除 -->
                    <el-tooltip content="编辑" placement="top-end" effect="light">
                        <el-button
                            v-permission="'/blog/delete'"
                            type="danger"
                            size="mini"
                            icon="el-icon-delete"
                            circle
                            @click="handleDelete(scope.row)"
                        />
                    </el-tooltip>
                </template>
            </el-table-column>
        </el-table>

        <!--分页-->
        <div class="block">
            <el-pagination
                :current-page.sync="currentPage"
                :page-size="pageSize"
                :total="total"
                layout="total, prev, pager, next, jumper"
                @current-change="handleCurrentChange"
            />
        </div>

        <!-- 添加或修改对话框 -->
        <el-dialog
            :title="title"
            v-if="dialogFormVisible"
            :visible.sync="dialogFormVisible"
            :before-close="closeDialog"
            fullscreen
        >
            <el-form ref="form" :model="form" :rules="rules">
                <el-row>
                    <el-col :xs="24" :sm="16">
                        <el-form-item
                            :label-width="formLabelWidth"
                            label="标题"
                            prop="title"
                        >
                            <el-input
                                size="small"
                                v-model="form.title"
                                auto-complete="off"
                                @input="contentChange"
                            />
                        </el-form-item>

                        <el-form-item
                            :label-width="formLabelWidth"
                            label="简介"
                            prop="summary"
                        >
                            <el-input
                                size="small"
                                v-model="form.summary"
                                auto-complete="off"
                                @input="contentChange"
                            />
                        </el-form-item>
                    </el-col>

                    <el-col :xs="24" :sm="8">
                        <el-form-item :label-width="formLabelWidth" label="标题图">
                            <div v-if="form.photoList" class="imgBody">
                                <i
                                    v-show="icon"
                                    class="el-icon-error inputClass"
                                    @click="deletePhoto()"
                                    @mouseover="icon = true"
                                />
                                <img
                                    :src="form.photoList[0]"
                                    style="display: inline; width: 195px; height: 105px"
                                    @mouseover="icon = true"
                                    @mouseout="icon = false"
                                />
                            </div>
                            <div v-else class="uploadImgBody" @click="checkPhoto">
                                <i class="el-icon-plus avatar-uploader-icon"/>
                            </div>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row>
                    <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
                        <el-form-item
                            :label-width="formLabelWidth"
                            label="分类"
                            prop="blogSortUid"
                        >
                            <el-select
                                v-model="form.blogSortUid"
                                size="small"
                                placeholder="请选择"
                                style="width: 150px"
                                clearable
                                filterable
                                @input="contentChange"
                            >
                                <el-option
                                    v-for="item in blogSortData"
                                    :key="item.uid"
                                    :label="item.sortName"
                                    :value="item.uid"
                                />
                            </el-select>
                        </el-form-item>
                    </el-col>

                    <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
                        <el-form-item label="标签" label-width="80px" prop="tagUid">
                            <el-select
                                v-model="tagValue"
                                multiple
                                size="small"
                                placeholder="请选择"
                                style="width: 210px"
                                filterable
                                clearable
                                @input="contentChange"
                            >
                                <el-option
                                    v-for="item in tagData"
                                    :key="item.uid"
                                    :label="item.content"
                                    :value="item.uid"
                                />
                            </el-select>
                        </el-form-item>
                    </el-col>

                    <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
                        <el-form-item
                            :label-width="maxLineLabelWidth"
                            label="推荐等级"
                            prop="level"
                        >
                            <el-select
                                v-model="form.level"
                                size="small"
                                placeholder="请选择"
                                style="width: 180px"
                            >
                                <el-option
                                    v-for="item in blogLevelDictList"
                                    :key="item.uid"
                                    :label="item.dictLabel"
                                    :value="parseInt(item.dictValue)"
                                />
                            </el-select>
                        </el-form-item>
                    </el-col>

                    <!--                    <el-col  :xs="24" :sm="12" :md="8" :lg="6" :xl="4">-->
                    <!--                        <el-form-item-->
                    <!--                            label-width="130px"-->
                    <!--                            prop="visitAuth"-->
                    <!--                        >-->

                    <!--                            <template slot="label">-->
                    <!--                                访问权限-->
                    <!--                                <el-popover-->
                    <!--                                    placement="top-start"-->
                    <!--                                    width="400"-->
                    <!--                                    trigger="hover">-->
                    <!--                                    <div>-->
                    <!--                                        <div>用于控制文章在满足限定条件后才可以查看全文</div>-->
                    <!--                                        <div v-for="item in visitAuthDictList" :key="item.uid + item.uid">-->
                    <!--                                            <span>{{item.dictLabel}}:</span>-->
                    <!--                                            <span>{{item.remark}}</span>-->
                    <!--                                        </div>-->

                    <!--                                    </div>-->

                    <!--                                    <i slot="reference" style="cursor: pointer;margin-left: 2px" class="el-icon-question"/>-->
                    <!--                                </el-popover>-->
                    <!--                            </template>-->

                    <!--                            <el-select-->
                    <!--                                v-model="form.visitAuth"-->
                    <!--                                size="small"-->
                    <!--                                placeholder="请选择"-->
                    <!--                                style="width: 180px"-->
                    <!--                            >-->
                    <!--                                <el-option-->
                    <!--                                    v-for="item in visitAuthDictList"-->
                    <!--                                    :key="item.uid"-->
                    <!--                                    :label="item.dictLabel"-->
                    <!--                                    :value="parseInt(item.dictValue)"-->
                    <!--                                />-->
                    <!--                            </el-select>-->
                    <!--                        </el-form-item>-->
                    <!--                    </el-col>-->

                    <!--                    <el-col  :xs="24" :sm="12" :md="8" :lg="6" :xl="4" v-if="form.visitAuth != 1" >-->
                    <!--                        <el-form-item :label-width="formLabelWidth" prop="loadingArea">-->

                    <!--                            <template slot="label">-->
                    <!--                                限制区域-->
                    <!--                                <el-popover-->
                    <!--                                    placement="top-start"-->
                    <!--                                    width="400"-->
                    <!--                                    trigger="hover">-->
                    <!--                                    <div>-->
                    <!--                                        <div>用于控制不满足访问权限时，内容可以展示的范围</div>-->
                    <!--                                        <div v-for="item in loadingAreaDictList" :key="item.uid + item.uid">-->
                    <!--                                            <span>{{item.dictLabel}}:</span>-->
                    <!--                                            <span>{{item.remark}}</span>-->
                    <!--                                        </div>-->
                    <!--                                    </div>-->
                    <!--                                    <i slot="reference" style="cursor: pointer;margin-left: 2px" class="el-icon-question"/>-->
                    <!--                                </el-popover>-->
                    <!--                            </template>-->

                    <!--                            <el-select-->
                    <!--                                v-model="form.loadingArea"-->
                    <!--                                size="small"-->
                    <!--                                placeholder="请选择"-->
                    <!--                                style="width: 250px"-->
                    <!--                            >-->
                    <!--                                <el-option-->
                    <!--                                    v-for="item in loadingAreaDictList"-->
                    <!--                                    :key="item.uid"-->
                    <!--                                    :label="item.dictLabel"-->
                    <!--                                    :value="parseInt(item.dictValue)"-->
                    <!--                                ></el-option>-->
                    <!--                            </el-select>-->
                    <!--                        </el-form-item>-->
                    <!--                    </el-col>-->

                    <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
                        <el-form-item
                            :label-width="formLabelWidth"
                            label="所属专栏"
                            prop="subjectUid"
                        >
                            <template slot="label">
                                所属专栏
                                <el-popover placement="top-start" width="400" trigger="hover">
                                    <div>
                                        <div>将文章添加到专栏后，文章访问权限将和专栏保持一致</div>
                                    </div>
                                    <i
                                        slot="reference"
                                        style="cursor: pointer; margin-left: 2px"
                                        class="el-icon-question"
                                    />
                                </el-popover>
                            </template>
                            <el-select
                                v-model="subjectValue"
                                size="small"
                                placeholder="请选择专栏"
                                style="width: 240px"
                                @input="contentChange"
                                clearable
                                filterable
                                multiple
                                :multiple-limit="3"
                            >
                                <el-option
                                    v-for="item in subjectData"
                                    :key="item.uid"
                                    :label="item.subjectName"
                                    :value="item.uid"
                                />
                            </el-select>
                        </el-form-item>
                    </el-col>

                    <!--                    <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4" v-if="form.visitAuth == 6">-->
                    <!--                        <el-form-item label="收费模式" :label-width="formLabelWidth" prop="payType">-->
                    <!--                            <el-select-->
                    <!--                                v-model="form.payType"-->
                    <!--                                size="small"-->
                    <!--                                placeholder="请选择"-->
                    <!--                                style="width: 250px"-->
                    <!--                            >-->
                    <!--                                <el-option-->
                    <!--                                    v-for="item in payTypeDictList"-->
                    <!--                                    :key="item.uid"-->
                    <!--                                    :label="item.dictLabel"-->
                    <!--                                    :value="parseInt(item.dictValue)"-->
                    <!--                                ></el-option>-->
                    <!--                            </el-select>-->
                    <!--                        </el-form-item>-->
                    <!--                    </el-col>-->

                    <!--                    <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4" v-if="form.visitAuth == 6">-->
                    <!--                        <el-form-item label="价格(分)" :label-width="formLabelWidth" prop="price">-->
                    <!--                            <template slot="label">-->
                    <!--                                价格(分)-->
                    <!--                                <el-popover-->
                    <!--                                    placement="top-start"-->
                    <!--                                    width="250"-->
                    <!--                                    trigger="hover">-->
                    <!--                                    <div>-->
                    <!--                                        <div>积分支付时，设置的是多少积分</div>-->
                    <!--                                        <div>现金支付时: 设置的是现金（单位分）</div>-->
                    <!--                                    </div>-->

                    <!--                                    <i slot="reference" style="cursor: pointer;margin-left: 2px" class="el-icon-question"/>-->
                    <!--                                </el-popover>-->
                    <!--                            </template>-->

                    <!--                            <el-input v-model="form.price" auto-complete="off"></el-input>-->
                    <!--                        </el-form-item>-->
                    <!--                    </el-col>-->
                </el-row>

                <el-row>

                    <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
                        <el-form-item
                            :label-width="formLabelWidth"
                            label="是否原创"
                            prop="isOriginal"
                        >
                            <el-radio-group v-model="form.isOriginal" size="small">
                                <el-radio
                                    v-for="item in blogOriginalDictList"
                                    :key="item.uid"
                                    :label="item.dictValue"
                                    border
                                >{{ item.dictLabel }}
                                </el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>

                    <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
                        <el-form-item
                            :label-width="formLabelWidth"
                            label="文章类型"
                            prop="openComment"
                        >
                            <el-radio
                                v-for="item in blogTypeDictList"
                                :key="item.uid"
                                v-model="form.type"
                                :label="item.dictValue"
                                border
                                size="small"
                            >{{ item.dictLabel }}
                            </el-radio>
                        </el-form-item>
                    </el-col>

                    <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
                        <el-form-item
                            :label-width="formLabelWidth"
                            label="文章评论"
                            prop="openComment"
                        >
                            <el-radio
                                v-for="item in openDictList"
                                :key="item.uid"
                                v-model="form.openComment"
                                :label="item.dictValue"
                                border
                                size="small"
                            >{{ item.dictLabel }}
                            </el-radio>
                        </el-form-item>
                    </el-col>

                    <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
                        <el-form-item
                            :label-width="formLabelWidth"
                            label="仅专栏可见"
                            prop="isOnlySubjectShow"
                        >
                            <el-radio
                                v-for="item in yesNoDictList"
                                :key="item.uid"
                                v-model="form.isOnlySubjectShow"
                                :label="parseInt(item.dictValue)"
                                border
                                size="small"
                            >{{ item.dictLabel }}
                            </el-radio>
                        </el-form-item>
                    </el-col>

                    <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
                        <el-form-item
                            :label-width="formLabelWidth"
                            label="排序"
                            prop="sort"
                        >
                            <el-input-number
                                v-model="form.sort"
                                :step="1"
                                step-strictly
                            ></el-input-number>
                        </el-form-item>
                    </el-col>

                    <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
                        <el-form-item
                            :label-width="formLabelWidth"
                            label="文章复制"
                            prop="isCopy"
                        >
                            <template slot="label">
                                文章复制
                                <el-popover placement="top-start" width="250" trigger="hover">
                                    <div>
                                        <div>关闭文章复制后，用户将无法复制文章内容</div>
                                    </div>
                                    <i
                                        slot="reference"
                                        style="cursor: pointer; margin-left: 2px"
                                        class="el-icon-question"
                                    />
                                </el-popover>
                            </template>
                            <el-radio
                                v-for="item in isCopyDictList"
                                :key="item.uid"
                                v-model="form.isCopy"
                                :label="parseInt(item.dictValue)"
                                border
                                size="small"
                            >{{ item.dictLabel }}
                            </el-radio>
                        </el-form-item>
                    </el-col>

                </el-row>

                <el-form-item
                    v-if="form.isOriginal == 0"
                    :label-width="formLabelWidth"
                    label="作者"
                    prop="author"
                >
                    <el-input v-model="form.author" size="small" auto-complete="off"/>
                </el-form-item>

                <el-form-item
                    v-if="form.isOriginal == 0"
                    :label-width="formLabelWidth"
                    label="文章出处"
                >
                    <el-input
                        v-model="form.articlesPart"
                        size="small"
                        auto-complete="off"
                    />
                </el-form-item>

                <el-form-item
                    v-if="form.type == 1"
                    :label-width="formLabelWidth"
                    label="外链"
                    prop="outsideLink"
                >
                    <el-input
                        v-model="form.outsideLink"
                        size="small"
                        auto-complete="off"
                    />
                </el-form-item>

                <!--访问权限扩展，过滤掉 14:订阅可见【该枚举仅可在专栏中设置】 -->
                <VisitAuth
                    ref="visitAuthRef"
                    :showLoadingArea="true"
                    :formLabelWidth="formLabelWidth"
                    :visitAuth="form.visitAuth"
                    :visitAuthExtra="form.visitAuthExtraVo"
                    :excludeVisitAuth="[14]"
                ></VisitAuth>

                <el-form-item :label-width="formLabelWidth" label="内容" prop="content">
                    <CKEditor
                        v-if="systemConfig.editorModel == '0'"
                        ref="editor"
                        :content="form.content"
                        :height="400"
                        @contentChange="contentChange"
                    />
                    <Mavon
                        v-if="systemConfig.editorModel == '1'"
                        ref="editor"
                        :content="form.content"
                        :height="465"
                        @contentChange="contentChange"
                    />
                </el-form-item>

                <el-form-item style="float: right; margin-right: 20px">
                    <el-button @click="dialogFormVisible = false">取 消</el-button>
                    <el-button type="primary" @click="submitForm(0)">确 定</el-button>
                </el-form-item>
            </el-form>
        </el-dialog>

        <el-dialog :visible.sync="localUploadVisible" title="本地博客上传">
            <div class="tipBox">
                <div class="tip">导入须知</div>
                <div class="tipItem">
                    1）如果你的Markdown文档里面的图片是本地，需要选择本地图片，然后提交到图片服务器
                </div>
                <div class="tipItem">
                    2）含有本地图片一定需要提前上传图片，否者会出现图片无法替换的问题
                </div>
                <div class="tipItem">
                    3）如果你的Markdown文档里面的图片不是本地，直接选择博客文件上传即可
                </div>
                <div class="tipItem">
                    4）目前支持Markdown文件批量上传，步骤是先提交所有图片，在提交全部的博客文件
                </div>
                <div class="tipItem">
                    5）因为网络或者服务器性能等不可抗拒的原因，因此不推荐一次上传太多
                </div>
            </div>

            <el-upload
                ref="uploadPicture"
                :data="otherData"
                :action="uploadPictureHost"
                :auto-upload="false"
                class="upload-demo2"
                name="filedatas"
                multiple
            >
                <el-button slot="trigger" size="small" type="primary"
                >选取本地图片
                </el-button>
                <el-button
                    style="margin-left: 10px"
                    size="small"
                    type="success"
                    @click="submitPictureUpload"
                >提交到图片服务器
                </el-button>
            </el-upload>

            <el-upload
                ref="uploadFile"
                :headers="importHeaders"
                :action="uploadAdminHost"
                :auto-upload="false"
                class="upload-demo"
                name="filedatas"
                multiple
            >
                <el-button slot="trigger" size="small" type="primary"
                >选取博客文件
                </el-button>
                <el-button
                    style="margin-left: 10px"
                    size="small"
                    type="success"
                    @click="submitUpload"
                >提交到服务器
                </el-button>
            </el-upload>
        </el-dialog>

        <CheckPhoto
            v-if="!isFirstPhotoVisible"
            :photo-visible="photoVisible"
            :photos="photoList"
            :files="fileIds"
            :limit="1"
            @choose_data="getChooseData"
            @cancelModel="cancelModel"
        />

        <SubjectSelect
            v-if="!isFirstSubjectVisible"
            :subject-visible="subjectVisible"
            @cancelModel="cancelSubjectSelect"
            @selectData="getSelectData"
        />

        <el-dialog
            :visible.sync="auditDialogVisible"
            title="请选择审批结果"
            width="30%"
            center
        >
            <div style="text-align: center">
                <el-form>
                    <el-form-item
                        :label-width="lineLabelWidth"
                        label="审批状态"
                        prop="auditStatus"
                    >
                        <el-radio-group v-model="auditForm.auditStatus" size="small">
                            <el-radio
                                v-for="item in auditStatusDictList"
                                v-if="item.dictValue != 0"
                                :key="item.uid"
                                :label="item.dictValue"
                                border
                            >{{ item.dictLabel }}
                            </el-radio>
                        </el-radio-group>
                    </el-form-item>

                    <el-form-item
                        v-if="auditForm.auditStatus == 1"
                        :label-width="lineLabelWidth"
                        label="拒绝原因"
                        prop="rejectReason"
                    >
                        <el-input
                            v-model="auditForm.rejectReason"
                            maxlength="50"
                            placeholder="拒绝的原因"
                            clearable
                        />
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
import {
    addBlog,
    auditBlog,
    deleteBatchBlog,
    deleteBlog,
    editBlog,
    getBlogList,
    publishBlog,
    pushBatchBlog
} from '@/api/blog'
import {getSystemConfig} from '@/api/systemConfig'
import {getTagList} from '@/api/tag'
import {getBlogSortList} from '@/api/blogSort'
import {getToken} from '@/utils/auth'
import {getCookie} from '@/utils/cookieUtils'
import {getListByDictTypeList} from '@/api/sysDictData'
import {addSubjectItemList} from '@/api/subjectItem'
import {getSubjectList} from "@/api/subject";
import UserAvatar from "../../components/UserAvatar"
import CheckPhoto from '../../components/CheckPhoto'
import CKEditor from '../../components/CKEditor'
import MarkdownEditor from '../../components/MarkdownEditor'
import Mavon from '../../components/Mavon'
import SubjectSelect from '../../components/SubjectSelect'
import {Loading} from 'element-ui'
import VisitAuth from '@/components/VisitAuth'

const querystring = require('querystring')
export default {
    components: {
        CheckPhoto,
        CKEditor,
        MarkdownEditor,
        Mavon,
        SubjectSelect,
        UserAvatar,
        VisitAuth,
    },
    data() {
        return {
            uploadPictureHost: process.env.PICTURE_API + '/file/pictures',
            uploadAdminHost: process.env.ADMIN_API + '/blog/uploadLocalBlog',
            importHeaders: {
                Authorization: getToken()
            },
            otherData: {
                source: 'picture',
                userUid: 'uid00000000000000000000000000000000',
                adminUid: 'uid00000000000000000000000000000000',
                projectName: 'blog',
                sortName: 'admin',
                token: getToken()
            },
            queryParams: {
                keyword: '',
                visitAuthKeyword: null, // 访问权限
                tagKeyword: '', // 标签搜索
                sortKeyword: '', // 分类搜索
                levelKeyword: '', // 等级搜索
                publishKeyword: '', // 发布 搜索
                originalKeyword: '', // 原创 搜索
                typeKeyword: '', // 文章类型
                openCommentKeyword: '', // 开启评论
                contributeKeyword: '', // 投稿方式
                openLoadingValidKeyword: '', // 开启校验
                auditStatusKeyword: '', // 审核状态
                onlySubjectShowKeyword: '', // 仅专栏可见
                containsSubjectKeyword: '', // 包含专栏
            }, // 搜索条件
            typeList: ['warning', 'danger', 'success', 'info', 'primary'],
            showSearch: null, // 显示搜索条件
            pictureList: [], // 上传的图片列表
            BLOG_WEB_URL: process.env.BLOG_WEB_URL,
            multipleSelection: [], // 多选，用于批量删除
            tagOptions: [], // 标签候选框
            sortOptions: [], // 分类候选框
            loading: false, // 搜索框加载状态
            uploadLoading: null, // 文件上传loading
            CKEditorData: null,
            tableData: [], // 博客数据
            tagData: [], // 标签数据
            tagValue: [], // 保存选中标签id(编辑时)
            subjectValue: [], // 保存选中的专题id(编辑时)
            blogSortData: [],
            subjectData: [], // 专栏列表
            currentPage: 1,
            pageSize: 10,
            total: 0, // 总数量
            title: '增加博客',
            dialogFormVisible: false, // 控制弹出框
            subjectVisible: false, // 是否显示专题
            isFirstSubjectVisible: true, // 专题选择器是否首次显示【用于懒加载】
            formLabelWidth: '120px',
            lineLabelWidth: '120px', // 一行的间隔数
            maxLineLabelWidth: '100px',
            isEditForm: false,
            photoVisible: false, // 控制图片选择器的显示
            isFirstPhotoVisible: true, // 图片选择器是否首次显示【用于懒加载】
            photoList: [],
            fileIds: '',
            icon: false, // 控制删除图标的显示
            interval: null, // 定义触发器
            isChange: false, // 表单内容是否改变
            changeCount: 0, // 改变计数器
            blogOriginalDictList: [], // 是否原创字典
            blogPublishDictList: [], // 是否发布字典
            blogLevelDictList: [], // 博客推荐等级字典
            openDictList: [], // 是否启动字典
            blogTypeDictList: [], // 文章类型字典
            articleDictList: [], // 文章来源字典
            auditStatusDictList: [], // 审批状态字典
            yesNoDictList: [], // 是否字典
            visitAuthDictList: [], // 访问权限字典
            isCopyDictList: [], // 是否支持复制字典
            payTypeDictList: [], // 收费模式字典
            loadingAreaDictList: [], // 加载区域字段
            blogOriginalDefault: null, // 博客原创默认值
            blogLevelDefault: null, // 博客等级默认值
            blogPublishDefault: null, // 博客发布默认值
            openDefault: null, // 是否开启评论默认值
            blogTypeDefault: null, // 文章类型默认值
            auditStatusDefault: null, // 审批状态默认值
            visitAuthDefault: null, // 访问权限默认值
            payTypeDefaultValue: null,
            loadingAreaDefaultValue: null, // 加载区域默认值
            yesNoDefault: null, // 是否默认值
            isCopyDefault: null, // 是否可拷贝默认值
            fileList: [],
            localUploadVisible: false,
            systemConfig: {}, // 系统配置
            orderByDescColumn: '', // 降序字段
            orderByAscColumn: '', // 升序字段
            auditDialogVisible: false, // 审批结果
            form: {
                uid: null,
                title: null,
                summary: null,
                content: null,
                tagUid: null,
                fileUid: null,
                isOriginal: null, // 是否原创
                isPublish: null,
                author: null, // 作者
                clickCount: 0,
                articlesPart: null // 文章出处
            },
            auditForm: {},
            rules: {
                title: [{required: true, message: '标题不能为空', trigger: 'blur'}],
                summary: [
                    {max: 200, message: '简介不能超过200个字符', trigger: 'blur'}
                ],
                blogSortUid: [
                    {required: true, message: '分类不能为空', trigger: 'blur'}
                ],
                tagUid: [{required: true, message: '标签不能为空', trigger: 'blur'}],
                level: [
                    {required: true, message: '推荐等级不能为空', trigger: 'blur'},
                    {pattern: /^[0-9]\d*$/, message: '推荐等级只能为自然数'}
                ],
                isPublish: [
                    {required: true, message: '发布字段不能为空', trigger: 'blur'},
                    {pattern: /^[0-9]\d*$/, message: '发布字段只能为自然数'}
                ],
                isOriginal: [
                    {required: true, message: '原创字段不能为空', trigger: 'blur'},
                    {pattern: /^[0-9]\d*$/, message: '原创字段只能为自然数'}
                ],
                openComment: [
                    {required: true, message: '网站评论不能为空', trigger: 'blur'},
                    {pattern: /^[0-9]\d*$/, message: '网站评论只能为自然数'}
                ],
                content: [{required: true, message: '内容不能为空', trigger: 'blur'}],
                outsideLink: [
                    {required: true, message: '外链地址不能为空', trigger: 'blur'},
                    {
                        pattern: /^((https|http|ftp|rtsp|mms)?:\/\/)[^\s]+/,
                        message: '请输入有效的URL'
                    }
                ],
                visitAuth: [
                    {required: true, message: '访问权限不能为空', trigger: 'blur'},
                ],
                price: [
                    {required: true, message: '价格不能为空', trigger: 'blur'},
                    {pattern: /^[0-9]\d*$/, message: '价格只能为自然数'}
                ],
                payType: [
                    {required: true, message: '支付方式不能为空', trigger: 'blur'},
                    {pattern: /^[0-9]\d*$/, message: '支付方式只能为自然数'}
                ],
                loadingArea: [
                    {required: true, message: '限制区域不能为空', trigger: 'blur'},
                    {pattern: /^[0-9]\d*$/, message: '限制区域只能为自然数'}
                ],
            }
        }
    },
    created() {
        // 从dashboard传递过来的 tagUid 以及 blogSortUid
        const tempTag = this.$route.query.tag
        const tempBlogSort = this.$route.query.blogSort

        if (tempTag != undefined) {
            this.tagRemoteMethod(tempTag.name)
            this.queryParams.tagKeyword = tempTag.tagUid
        }
        if (tempBlogSort != undefined) {
            this.sortRemoteMethod(tempBlogSort.name)
            this.queryParams.sortKeyword = tempBlogSort.blogSortUid
        }
        // 判断是否需要展开条件查询
        this.getShowSearch()

        // 获取系统配置
        this.getSystemConfigList()

        // 获取字典
        this.getDictList()

        // 获取标签列表
        this.tagList()
        // 获取博客分类
        this.blogSortList()
        // 获取博客列表
        this.blogList()
        // 获取专栏列表
        this.subjectList()
    },
    methods: {
        // 从后台获取数据,重新排序
        changeSort(val) {
            // 根据当前排序重新获取后台数据,一般后台会需要一个排序的参数
            if (val.order == 'ascending') {
                this.orderByAscColumn = val.prop
                this.orderByDescColumn = ''
            } else {
                this.orderByAscColumn = ''
                this.orderByDescColumn = val.prop
            }
            this.blogList()
        },
        openLoading() {
            this.uploadLoading = Loading.service({
                lock: true,
                text: '正在努力上传中……'
            })
        },
        getTypeClass(sort) {
            return this.typeList[sort % 5]
        },
        closeLoading() {
            this.uploadLoading.close()
        },
        // 跳转到个人中心页
        getUserCenter: function (blog) {
            // 判断是后台添加，还是前台投稿
            if (blog.articleSource == 0) {
                window.open(
                    this.BLOG_WEB_URL + '/userCenter?adminUid=' + blog.adminUid,
                    '_blank'
                )
            } else {
                window.open(
                    this.BLOG_WEB_URL + '/userCenter?userUid=' + blog.userUid,
                    '_blank'
                )
            }
        },
        // 判断是否需要展开条件查询
        getShowSearch: function () {
            const showSearch = getCookie('showSearch')
            if (showSearch == 'false') {
                // 此时的hasAuth是true
                this.showSearch = false
            } else {
                this.showSearch = true
            }
        },
        tagList: function () {
            const tagParams = {}
            tagParams.pageSize = 500
            tagParams.currentPage = 1
            getTagList(tagParams).then((response) => {
                this.tagData = response.data.records
                this.tagOptions = response.data.records
            })
        },
        blogSortList: function () {
            const blogSortParams = {}
            blogSortParams.pageSize = 500
            blogSortParams.currentPage = 1
            getBlogSortList(blogSortParams).then((response) => {
                if (response.code == this.$ECode.SUCCESS) {
                    this.blogSortData = response.data.records
                    this.sortOptions = response.data.records
                }
            })
        },
        resetBlogList: function () {
            this.queryParams = {}
            this.blogList()
        },
        blogList: function () {
            const params = {}
            params.keyword = this.queryParams.keyword
            params.blogSortUid = this.queryParams.sortKeyword
            params.tagUid = this.queryParams.tagKeyword
            params.levelKeyword = this.queryParams.levelKeyword
            params.isPublish = this.queryParams.publishKeyword
            params.isOriginal = this.queryParams.originalKeyword
            params.openComment = this.queryParams.openCommentKeyword
            params.openLoadingValid = this.queryParams.openLoadingValidKeyword
            params.auditStatus = this.queryParams.auditStatusKeyword
            params.articleSource = this.queryParams.contributeKeyword
            params.isOnlySubjectShow = this.queryParams.onlySubjectShowKeyword
            params.visitAuth = this.queryParams.visitAuthKeyword
            params.type = this.queryParams.typeKeyword
            params.containsSubject = this.queryParams.containsSubjectKeyword
            params.currentPage = this.currentPage
            params.pageSize = this.pageSize
            params.orderByDescColumn = this.orderByDescColumn
            params.orderByAscColumn = this.orderByAscColumn
            getBlogList(params).then((response) => {
                if (response.code == this.$ECode.SUCCESS) {
                    const tableData = response.data.records
                    this.currentPage = response.data.current
                    this.pageSize = response.data.size
                    this.total = response.data.total
                    for (let i = 0; i < tableData.length; i++) {
                        if (tableData[i].openComment == 1) {
                            tableData[i].openCommentStatus = true
                        } else {
                            tableData[i].openCommentStatus = false
                        }
                        if (tableData[i].openLoadingValid == 1) {
                            tableData[i].openLoadingValidStatus = true
                        } else {
                            tableData[i].openLoadingValidStatus = false
                        }
                        if (tableData[i].isOnlySubjectShow == 1) {
                            tableData[i].isOnlySubjectShowStatus = true
                        } else {
                            tableData[i].isOnlySubjectShowStatus = false
                        }

                        if (tableData[i].isVip == 1) {
                            tableData[i].openVipStatus = true
                        } else {
                            tableData[i].openVipStatus = false
                        }
                        if (tableData[i].isTop == 1) {
                            tableData[i].openTopStatus = true
                        } else {
                            tableData[i].openTopStatus = false
                        }
                        if (tableData[i].isPublish == 1) {
                            tableData[i].publishStatus = true
                        } else {
                            tableData[i].publishStatus = false
                        }
                    }
                    this.tableData = tableData
                }
            })
        },
        /**
         * 字典查询
         */
        getDictList: function () {
            const dictTypeList = [
                'sys_recommend_level',
                'sys_original_status',
                'sys_publish_status',
                'sys_normal_disable',
                'sys_blog_type',
                'sys_article_source',
                'sys_yes_no',
                'sys_audit_status',
                'sys_visit_auth',
                'sys_pay_type',
                'sys_loading_area',
                'sys_blog_iscopy',
            ]

            getListByDictTypeList(dictTypeList).then((response) => {
                if (response.code == this.$ECode.SUCCESS) {
                    const dictMap = response.data
                    this.blogOriginalDictList = dictMap.sys_original_status.list
                    this.blogPublishDictList = dictMap.sys_publish_status.list
                    this.blogLevelDictList = dictMap.sys_recommend_level.list
                    this.openDictList = dictMap.sys_normal_disable.list
                    this.blogTypeDictList = dictMap.sys_blog_type.list
                    this.articleDictList = dictMap.sys_article_source.list
                    this.auditStatusDictList = dictMap.sys_audit_status.list
                    this.yesNoDictList = dictMap.sys_yes_no.list
                    this.isCopyDictList = dictMap.sys_blog_iscopy.list
                    this.visitAuthDictList = dictMap.sys_visit_auth.list
                    this.payTypeDictList = dictMap.sys_pay_type.list
                    this.loadingAreaDictList = dictMap.sys_loading_area.list

                    if (dictMap.sys_original_status.defaultValue) {
                        this.blogOriginalDefault = dictMap.sys_original_status.defaultValue
                    }
                    if (dictMap.sys_publish_status.defaultValue) {
                        this.blogPublishDefault = dictMap.sys_publish_status.defaultValue
                    }
                    if (dictMap.sys_recommend_level.defaultValue) {
                        this.blogLevelDefault = dictMap.sys_recommend_level.defaultValue
                    }
                    if (dictMap.sys_normal_disable.defaultValue) {
                        this.openDefault = dictMap.sys_normal_disable.defaultValue
                    }
                    if (dictMap.sys_blog_type.defaultValue) {
                        this.blogTypeDefault = dictMap.sys_blog_type.defaultValue
                    }
                    if (dictMap.sys_audit_status.defaultValue) {
                        this.auditStatusDefault = dictMap.sys_audit_status.defaultValue
                    }
                    if (dictMap.sys_yes_no.defaultValue) {
                        this.yesNosDefault = dictMap.sys_yes_no.defaultValue
                    }
                    if (dictMap.sys_blog_iscopy.defaultValue) {
                        this.isCopyDefault = dictMap.sys_blog_iscopy.defaultValue
                    }

                    if (dictMap.sys_visit_auth.defaultValue) {
                        this.visitAuthDefault = dictMap.sys_visit_auth.defaultValue
                    }
                    if (dictMap.sys_pay_type.defaultValue) {
                        this.payTypeDefaultValue = dictMap.sys_pay_type.defaultValue
                    }
                    if (dictMap.sys_loading_area.defaultValue) {
                        this.loadingAreaDefaultValue = dictMap.sys_loading_area.defaultValue
                    }
                }
            })
        },
        // 获取专栏列表
        subjectList: function () {
            let params = {};
            params.currentPage = 1;
            params.pageSize = 100;
            getSubjectList(params).then(response => {
                this.subjectData = response.data.records;
            });
        },
        getAuditFormObject: function () {
            const auditForm = {
                uid: '',
                auditStatus: this.auditStatusDefault, // 审批状态默认值
                rejectReason: null // 审批拒绝原因
            }
            return auditForm
        },
        getFormObject: function () {
            const formObject = {
                uid: null,
                title: null,
                summary: null,
                content: null,
                tagUid: null,
                fileUid: null,
                isOriginal: this.blogOriginalDefault, // 是否原创
                isPublish: this.blogOriginalDefault, // 是否发布
                type: this.blogTypeDefault, // 文章类型
                author: null, // 作者
                level: parseInt(this.blogLevelDefault), // 推荐等级，默认是正常
                openComment: this.openDefault, // 是否启动评论
                openLoadingValid: this.openDefault, // 是否开启校验
                articlesPart: null, // 文章出处，默认蘑菇博客
                visitAuth: this.visitAuthDefault, // 推荐等级，默认是正常
                isVip: "0", // 是否是VIP文章
                isOnlySubjectShow: 0, // 是否仅专栏可见
                isCopy: parseInt(this.isCopyDefault), // 是否支持复制
                sort: 0,
                loadingArea: parseInt(this.loadingAreaDefaultValue),
            }
            return formObject
        },
        // 跳转到该博客详情
        onClick: function (row) {
            window.open(this.BLOG_WEB_URL + '/info/' + row.oid)
        },
        // 标签远程搜索函数
        tagRemoteMethod: function (query) {
            if (query !== '') {
                const params = {}
                params.keyword = query
                params.pageSize = 10
                params.currentPage = 1
                getTagList(params).then((response) => {
                    this.tagOptions = response.data.records
                })
            } else {
                this.tagOptions = []
            }
        },
        // 分类远程搜索函数
        sortRemoteMethod: function (query) {
            if (query !== '') {
                const params = {}
                params.keyword = query
                params.pageSize = 10
                params.currentPage = 1
                getBlogSortList(params).then((response) => {
                    this.sortOptions = response.data.records
                })
            } else {
                this.sortOptions = []
            }
        },
        // 获取系统配置
        getSystemConfigList: function () {
            getSystemConfig().then((response) => {
                if (response.code == this.$ECode.SUCCESS) {
                    if (response.data) {
                        this.systemConfig = response.data
                    }
                }
            })
        },
        getChooseData(data) {
            this.photoVisible = false
            this.photoList = data.photoList
            this.fileIds = data.fileIds
            const fileId = this.fileIds.replace(',', '')
            if (this.photoList.length >= 1) {
                this.form.fileUid = fileId
                this.form.photoList = this.photoList
            }
            this.contentChange()
        },
        // 关闭模态框
        cancelModel() {
            this.photoVisible = false
        },
        deletePhoto: function () {
            this.form.photoList = null
            this.form.fileUid = ''
        },
        checkPhoto() {
            this.photoList = []
            this.fileIds = ''
            this.photoVisible = true
            this.isFirstPhotoVisible = false
        },
        goSubjectDetail(subject) {
            this.$router.push({
                path: "/blog/subjectItem",
                query: {subjectUid: subject.uid}
            });
        },
        // 添加至专题
        handleSubject() {
            if (this.multipleSelection.length <= 0) {
                this.$commonUtil.message.error('请先选中需要添加到专题的博客!')
                return
            }
            this.subjectVisible = true
            this.isFirstSubjectVisible = false
        },
        getSelectData(subjectUid) {
            this.cancelSubjectSelect()
            // 选中的博客
            const multipleSelection = this.multipleSelection
            const subjectItemList = []
            for (let a = 0; a < multipleSelection.length; a++) {
                const params = {}
                params.subjectUid = subjectUid[0]
                params.blogUid = multipleSelection[a].uid
                subjectItemList.push(params)
            }
            addSubjectItemList(subjectItemList).then((response) => {
                if (response.code == this.$ECode.SUCCESS) {
                    this.$commonUtil.message.success(response.message)
                    // 清空选中列表
                    this.multipleSelection = []
                    this.$refs.articleTable.clearSelection()
                    this.blogList()
                } else {
                    this.$commonUtil.message.error(response.message)
                }
            })
        },
        cancelSubjectSelect: function () {
            this.subjectVisible = false
        },
        // 关闭窗口
        closeDialog(done) {
            if (this.isChange) {
                this.$confirm('是否关闭博客编辑窗口', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                })
                    .then(() => {
                        this.isChange = false
                        this.changeCount = 0
                        done()
                    })
                    .catch(() => {
                        this.$commonUtil.message.info('已取消')
                    })
            } else {
                this.isChange = false
                this.changeCount = 0
                done()
            }
        },
        handleFind: function () {
            this.currentPage = 1
            this.blogList()
        },
        handleAdd: function () {
            this.title = '增加博客'
            const that = this
            let tempForm = null
            if (localStorage.getItem('form')) {
                tempForm = JSON.parse(localStorage.getItem('form'))
            }
            if (tempForm != null && tempForm.title != null && tempForm.title != '') {
                this.$confirm('还有上次未完成的博客编辑，是否继续编辑?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                })
                    .then(() => {
                        that.dialogFormVisible = true
                        that.tagValue = []
                        that.form = JSON.parse(localStorage.getItem('form'))
                        const tagValue = that.form.tagUid.split(',')
                        for (let a = 0; a < tagValue.length; a++) {
                            if (tagValue[a] != null && tagValue[a] != '') {
                                that.tagValue.push(tagValue[a])
                            }
                        }

                        that.subjectValue = []
                        const subjectValue = that.form.subjectList
                        for (let a = 0; a < subjectValue.length; a++) {
                            if (subjectValue[a] != null) {
                                that.subjectValue.push(subjectValue[a].uid)
                            }
                        }

                        if (that.form.uid) {
                            that.title = '编辑博客'
                            that.isEditForm = true
                        } else {
                            that.title = '新增博客'
                            that.isEditForm = false
                        }
                    })
                    .catch(() => {
                        that.dialogFormVisible = true
                        that.form = that.getFormObject()
                        that.$nextTick(() => {
                            // DOM现在更新了
                            that.$refs.editor.initData() // 设置富文本内容
                        })
                        that.tagValue = []
                        that.isEditForm = false
                        that.title = '新增博客'
                        localStorage.setItem('form', "")
                    })
            } else {
                that.dialogFormVisible = true
                that.form = this.getFormObject()
                that.$nextTick(() => {
                    // 初始化内容
                    that.$refs.editor.initData()
                })
                that.tagValue = []
                that.isEditForm = false
            }
        },
        handlePushBatch: function (row) {
            const that = this
            if (that.multipleSelection.length <= 0) {
                that.$commonUtil.message.error('请先选中需要推送到百度收录的博客')
                return
            }
            this.$confirm('             此操作将把选中博客推送到百度收录, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
                .then(() => {
                    pushBatchBlog(that.multipleSelection).then((response) => {
                        if (response.success) {
                            that.$commonUtil.message.success(
                                '推送成功' +
                                response.success +
                                '条，您今日还可推送：' +
                                response.remain +
                                '条'
                            )
                        } else {
                            that.$commonUtil.message.error('推送失败:' + response)
                        }
                    })
                })
                .catch(() => {
                    that.$commonUtil.message.info('已取消推送')
                })
        },
        handleUpload: function () {
            this.localUploadVisible = true
        },
        // 文件上传
        submitUpload() {
            const {uploadFiles, action} = this.$refs.uploadFile
            const data = {}
            data.pictureList = JSON.stringify(this.pictureList)
            this.openLoading()
            this.uploadFiles({
                uploadFiles,
                data,
                action,
                success: (response) => {
                    const res = JSON.parse(response)
                    if (res.code == this.$ECode.SUCCESS) {
                        this.$commonUtil.message.success(res.message)
                        // 获取博客列表
                        this.blogList()
                    } else {
                        this.$commonUtil.message.error(res.message)
                    }
                    this.localUploadVisible = false
                    this.closeLoading()
                    // 上传成功后，将里面的内容删除
                    this.$refs.uploadFile.clearFiles()
                    this.$refs.uploadPicture.clearFiles()
                },
                error: (error) => {
                    this.closeLoading()
                    console.log('失败了', error)
                }
            })
        },
        // 图片上传
        submitPictureUpload() {
            const {uploadFiles, action, data} = this.$refs.uploadPicture
            this.openLoading()
            this.uploadFiles({
                uploadFiles,
                data,
                action,
                success: (response) => {
                    const res = JSON.parse(response)
                    if (res.code == this.$ECode.SUCCESS) {
                        this.$commonUtil.message.success('图片上传成功')
                        const pictureList = res.data
                        const list = []
                        for (let a = 0; a < pictureList.length; a++) {
                            const picture = {}
                            picture.uid = pictureList[a].uid
                            picture.fileOldName = pictureList[a].fileOldName
                            picture.picUrl = pictureList[a].picUrl
                            picture.qiNiuUrl = pictureList[a].qiNiuUrl
                            picture.minioUrl = pictureList[a].minioUrl
                            picture.aliOssUrl = pictureList[a].aliOssUrl
                            list.push(picture)
                        }
                        this.pictureList = list
                    } else {
                        this.$commonUtil.message.error('图片上传失败')
                    }
                    this.closeLoading()
                },
                error: (error) => {
                    this.closeLoading()
                    this.$commonUtil.message.error('图片上传失败')
                    console.log('失败了', error)
                }
            })
        },
        /**
         * 自定义上传文件
         * @param fileList 文件列表
         * @param data 上传时附带的额外参数
         * @param url 上传的URL地址
         * @param success 成功回调
         * @param error 失败回调
         */
        uploadFiles({uploadFiles, headers, data, action, success, error}) {
            const form = new FormData()
            // 文件对象
            uploadFiles.map((file) => form.append('filedatas', file.raw))
            // 附件参数
            for (const key in data) {
                form.append(key, data[key])
            }
            const xhr = new XMLHttpRequest()
            // 异步请求
            xhr.open('post', action, true)
            // 设置请求头
            xhr.setRequestHeader('Authorization', getToken())
            xhr.onreadystatechange = function () {
                if (xhr.readyState == 4) {
                    if ((xhr.status >= 200 && xhr.status < 300) || xhr.status == 304) {
                        success && success(xhr.responseText)
                    } else {
                        error && error(xhr.status)
                    }
                }
            }
            xhr.send(form)
        },
        handleDownload: function () {
            if (this.multipleSelection.length <= 0) {
                this.$commonUtil.message.error('请先选中需要导出的博客')
                return
            }

            const blogList = this.multipleSelection
            for (let a = 0; a < blogList.length; a++) {
                this.$commonUtil.htmlToMarkdownFile(
                    blogList[a].title,
                    blogList[a].content
                )
            }
        },
        // 内容改变，触发监听
        contentChange: function () {
            const that = this
            if (that.changeCount > 1) {
                that.isChange = true
                that.form.content = that.$refs.editor.getData() // 获取CKEditor中的内容
                that.form.tagUid = that.tagValue.join(',')
                // 设置内容
                let subjectValue = that.subjectValue
                that.form.subjectList = []
                for (let i = 0; i < subjectValue.length; i++) {
                    let subject = {}
                    subject.uid = that.subjectValue[i]
                    that.form.subjectList.push(subject)
                }
                // 将内容设置到 WebStorage中
                localStorage.setItem('form', JSON.stringify(that.form))
            }
            this.changeCount = this.changeCount + 1
        },
        handleEdit: function (row) {
            const that = this
            let tempForm = null
            if (localStorage.getItem('form')) {
                tempForm = JSON.parse(localStorage.getItem('form'))
            }
            if (tempForm != null && tempForm.title != null && tempForm.title != '') {
                this.$confirm('还有上次未完成的博客编辑，是否继续编辑?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                })
                    .then(() => {
                        that.dialogFormVisible = true
                        that.tagValue = []
                        that.form = JSON.parse(localStorage.getItem.get('form'))
                        const tagValue = that.form.tagUid.split(',')
                        for (let a = 0; a < tagValue.length; a++) {
                            if (tagValue[a] != null && tagValue[a] != '') {
                                that.tagValue.push(tagValue[a])
                            }
                        }

                        that.subjectValue = []
                        const subjectValue = that.form.subjectList
                        for (let a = 0; a < subjectValue.length; a++) {
                            if (subjectValue[a] != null) {
                                that.subjectValue.push(tagValue[a].uid)
                            }
                        }

                        if (that.form.uid) {
                            that.title = '编辑博客'
                            that.isEditForm = true
                        } else {
                            that.title = '新增博客'
                            that.isEditForm = false
                        }
                    })
                    .catch(() => {
                        this.title = '编辑博客'
                        this.form = row
                        this.$nextTick(() => {
                            // DOM现在更新了
                            that.$refs.editor.setData(that.form.content) // 设置富文本内容
                        })
                        that.tagValue = []
                        if (row.tagList) {
                            const json = row.tagList
                            for (let i = 0, l = json.length; i < l; i++) {
                                if (json[i] != null) {
                                    that.tagValue.push(json[i]['uid'])
                                }
                            }
                        }

                        // 设置专栏信息
                        that.subjectValue = []
                        if (row.subjectList) {
                            const json = row.subjectList
                            for (let i = 0, l = json.length; i < l; i++) {
                                if (json[i] != null) {
                                    that.subjectValue.push(json[i]['uid'])
                                }
                            }
                        }
                        that.dialogFormVisible = true
                        that.isEditForm = true
                        localStorage.setItem('form', "")
                    })
            } else {
                this.title = '编辑博客'
                this.form = row
                setTimeout(() => {
                    that.$refs.editor.setData(that.form.content) // 设置富文本内容
                }, 100);
                that.tagValue = []
                if (row.tagList) {
                    const json = row.tagList
                    for (let i = 0, l = json.length; i < l; i++) {
                        if (json[i] != null) {
                            that.tagValue.push(json[i]['uid'])
                        }
                    }
                }
                // 设置专栏信息
                that.subjectValue = []
                if (row.subjectList) {
                    const json = row.subjectList
                    for (let i = 0, l = json.length; i < l; i++) {
                        if (json[i] != null) {
                            that.subjectValue.push(json[i]['uid'])
                        }
                    }
                }
                console.log("编辑内容", row)
                that.dialogFormVisible = true
                that.isEditForm = true
            }
        },
        handChangeBlog(row) {
            this.title = '编辑博客'
            const that = this
            if (row.openCommentStatus) {
                row.openComment = '1'
            } else {
                row.openComment = '0'
            }
            if (row.publishStatus) {
                row.isPublish = '1'
            } else {
                row.isPublish = '0'
            }
            if (row.openLoadingValidStatus) {
                row.openLoadingValid = '1'
            } else {
                row.openLoadingValid = '0'
            }
            if (row.isOnlySubjectShowStatus) {
                row.isOnlySubjectShow = '1'
            } else {
                row.isOnlySubjectShow = '0'
            }
            if (row.openVipStatus) {
                row.isVip = '1'
            } else {
                row.isVip = '0'
            }
            if (row.openTopStatus) {
                row.isTop = 1
            } else {
                row.isTop = 0
            }
            this.form = row
            that.isEditForm = true
            this.submitForm(1)
        },
        handleDelete: function (row) {
            const that = this
            this.$confirm('此操作将把博客删除, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
                .then(() => {
                    const params = {}
                    params.uid = row.uid
                    deleteBlog(params).then((response) => {
                        that.$commonUtil.message.success(response.message)
                        that.blogList()
                    })
                })
                .catch(() => {
                    that.$commonUtil.message.info('已取消删除')
                })
        },
        handlePublish: function (row) {
            const that = this
            if (row.isPublish == 1) {
                this.$confirm('此操作将下架博客, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                })
                    .then(() => {
                        const params = {}
                        params.uid = row.uid
                        params.isPublish = 0
                        publishBlog(params).then((response) => {
                            that.$commonUtil.message.success(response.message)
                            that.blogList()
                        })
                    })
                    .catch(() => {
                        that.$commonUtil.message.info('已取消删除')
                    })
            }
            if (row.isPublish == 0) {
                this.$confirm('此操作将发布博客, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                })
                    .then(() => {
                        const params = {}
                        params.uid = row.uid
                        params.isPublish = 1
                        publishBlog(params).then((response) => {
                            that.$commonUtil.message.success(response.message)
                            that.blogList()
                        })
                    })
                    .catch(() => {
                        that.$commonUtil.message.info('已取消删除')
                    })
            }
        },
        handleAudit: function (row) {
            this.auditForm = this.getAuditFormObject()
            this.auditForm.uid = row.uid
            this.auditDialogVisible = true
        },
        submitAudit: function () {
            const params = {}
            params.uid = this.auditForm.uid
            params.auditStatus = this.auditForm.auditStatus
            params.rejectReason = this.auditForm.rejectReason
            auditBlog(params).then((response) => {
                if (response.code == this.$ECode.SUCCESS) {
                    this.$commonUtil.message.success(response.message)
                    this.auditDialogVisible = false
                    this.blogList()
                } else {
                    this.$commonUtil.message.error(response.message)
                }
            })
        },
        handleDeleteBatch: function (row) {
            const that = this
            if (that.multipleSelection.length <= 0) {
                that.$commonUtil.message.error('请先选中需要删除的博客')
                return
            }
            this.$confirm('此操作将把选中博客删除, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
                .then(() => {
                    deleteBatchBlog(that.multipleSelection).then((response) => {
                        if (response.code == this.$ECode.SUCCESS) {
                            that.$commonUtil.message.success(response.message)
                            that.blogList()
                        } else {
                            that.$commonUtil.message.error(response.message)
                        }
                    })
                })
                .catch(() => {
                    that.$commonUtil.message.info('已取消删除')
                })
        },
        handleCurrentChange: function (val) {
            this.currentPage = val
            this.blogList()
        },
        submitForm: function (submitType) {
            // 正常方式提交
            if (submitType == 0) {
                this.form.content = this.$refs.editor.getData() // 获取CKEditor中的内容
                this.form.tagUid = this.tagValue.join(',')

                let subjectList = []
                let subjectValue = this.subjectValue
                for (let i = 0; i < subjectValue.length; i++) {
                    let subjectParams = {}
                    subjectParams.uid = subjectValue[i]
                    subjectList.push(subjectParams)
                }
                this.form.subjectList = subjectList

                let visitAuthInfo = this.$refs.visitAuthRef.getVisitAuthInfo()
                this.form.visitAuth = visitAuthInfo.visitAuth
                this.form.visitAuthExtraVo = visitAuthInfo.visitAuthExtra
                // 从中解析出收费模式和价格
                if (visitAuthInfo.visitAuthExtra) {
                    this.form.payType = visitAuthInfo.visitAuthExtra.payType
                    this.form.price = visitAuthInfo.visitAuthExtra.price
                    this.form.loadingArea = visitAuthInfo.visitAuthExtra.loadingArea
                }

                this.$refs.form.validate((valid) => {
                    if (!valid) {
                        console.log('校验出错')
                    } else {
                        if (this.isEditForm) {
                            editBlog(this.form).then((response) => {
                                if (response.code == this.$ECode.SUCCESS) {
                                    this.$commonUtil.message.success(response.message)
                                    // 清空LocalStorage中的内容
                                    localStorage.setItem('form', "")
                                    this.dialogFormVisible = false
                                    this.blogList()
                                } else {
                                    this.$commonUtil.message.error(response.message)
                                }
                            })
                        } else {
                            addBlog(this.form).then((response) => {
                                if (response.code == this.$ECode.SUCCESS) {
                                    this.$commonUtil.message.success(response.message)
                                    // 清空cookie中的内容
                                    localStorage.setItem('form', "")
                                    this.dialogFormVisible = false
                                    this.blogList()
                                } else {
                                    this.$commonUtil.message.error(response.message)
                                }
                            })
                        }
                    }
                })
            } else {
                // 未打开编辑框的方式提交
                editBlog(this.form).then((response) => {
                    if (response.code == this.$ECode.SUCCESS) {
                        this.$commonUtil.message.success(response.message)
                        // 清空LocalStorage中的内容
                        localStorage.setItem('form', "");
                        this.dialogFormVisible = false
                        this.blogList()
                    } else {
                        this.$commonUtil.message.error(response.message)
                    }
                })
            }
        },
        // 改变多选
        handleSelectionChange(val) {
            this.multipleSelection = val
        }
    }
}
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

.el-dialog__body {
    padding-top: 10px;
    padding-bottom: 0px;
}

.el-dialog {
    min-height: 400px;
}

.el-upload__tip {
    margin-top: 10px;
    margin-left: 10px;
    color: #3e999f;
}

.upload-demo {
    margin-top: 50px;
}

.tipBox {
    margin-bottom: 30px;
}

.tip {
    font-size: 14px;
    font-weight: bold;
    color: #808080;
}

.tipItem {
    line-height: 22px;
    color: #a9a9a9;
}
</style>
