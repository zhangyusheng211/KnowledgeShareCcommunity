<template>
    <div class="app-container">
        <el-divider content-position="center">
            {{ this.$route.params && this.$route.params.uid ? '修改' : '新增' }}课程信息
        </el-divider>
        <el-form ref="form" :model="form" :label-width="formLabelWidth" :rules="rules">
            <el-row>
                <el-col :span="16">
                    <el-form-item label="标题" prop="title">
                        <el-input v-model="form.title" placeholder="请输入标题" auto-complete="off">
                            <template slot="append">{{ form.en }}</template>
                        </el-input>
                    </el-form-item>
                    <el-row>
                        <el-col :span="8">
                            <el-form-item label="类型" prop="type">
                                <el-select v-model="form.type" placeholder="请选择课程类型">
                                    <el-option
                                        v-for="dict in typeOptions"
                                        :key="dict.dictValue"
                                        :label="dict.dictLabel"
                                        :value="dict.dictValue"
                                    ></el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item label="分类" prop="categoryUid">
                                <el-select v-model="form.categoryUid" placeholder="请选择课程分类">
                                    <el-option
                                        v-for="category in categoryOptions"
                                        :key="category.uid"
                                        :label="category.name"
                                        :value="category.uid"
                                    />
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item label="国家" prop="country">
                                <el-select v-model="form.country" placeholder="请输入国家">
                                    <el-option
                                        v-for="dict in countryOptions"
                                        :key="dict.dictValue"
                                        :label="dict.dictLabel"
                                        :value="dict.dictValue"
                                    ></el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col :span="8">
                            <el-form-item label="语言" prop="lang">
                                <el-select v-model="form.lang" placeholder="语言" size="small">
                                    <el-option
                                        v-for="dict in langOptions"
                                        :key="dict.dictValue"
                                        :label="dict.dictLabel"
                                        :value="dict.dictValue"
                                    ></el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>

                        <el-col :span="8">
                            <el-form-item label="发布时间" prop="publishTime">
                                <el-date-picker clearable size="small"
                                                v-model="form.publishTime"
                                                type="date"
                                                value-format="yyyy-MM-dd"
                                                placeholder="选择发布时间">
                                </el-date-picker>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item label="状态">
                                <el-select v-model="form.status" placeholder="请选择状态" clearable size="small">
                                    <el-option
                                        v-for="dict in statusOptions"
                                        :key="dict.dictValue"
                                        :label="dict.dictLabel"
                                        :value="parseInt(dict.dictValue)"
                                    ></el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col :span="8">
                            <el-form-item label="是否可以评论" prop="openComment">
                                <el-radio-group v-model="form.openComment" size="small">
                                    <el-radio
                                        v-for="dict in commonSwitchOptions"
                                        :key="dict.dictValue"
                                        :label="parseInt(dict.dictValue)"
                                    >{{ dict.dictLabel }}
                                    </el-radio>
                                </el-radio-group>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item label="是否可以下载" prop="openDownload">
                                <el-radio-group v-model="form.openDownload" size="small">
                                    <el-radio
                                        v-for="dict in commonSwitchOptions"
                                        :key="dict.dictValue"
                                        :label="parseInt(dict.dictValue)"
                                    >{{ dict.dictLabel }}
                                    </el-radio>
                                </el-radio-group>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item label="开启密钥模式">
                                <el-radio-group v-model="form.openPassword" size="small">
                                    <el-radio
                                        v-for="dict in commonSwitchOptions"
                                        :key="dict.dictValue"
                                        :label="parseInt(dict.dictValue)"
                                    >{{ dict.dictLabel }}
                                    </el-radio>
                                </el-radio-group>
                            </el-form-item>
                        </el-col>
                    </el-row>

                    <!--            <el-row>-->
                    <!--              <el-col :span="8">-->
                    <!--                <el-form-item label="发布人" prop="publishBy">-->
                    <!--                  {{form.publishUsername}}-->
                    <!--                </el-form-item>-->
                    <!--              </el-col>-->
                    <!--              <el-col :span="7">-->
                    <!--                <el-form-item  label="专属二维码" v-if="form.qrcodePath" prop="qrcodePath">-->
                    <!--                  <el-image-->
                    <!--                    @click="previewPicture(fileUploadHost + form.qrcodePath)"-->
                    <!--                    style="width: 100px; height: 100px"-->
                    <!--                    :src="fileUploadHost + form.qrcodePath"-->
                    <!--                    :fit="fit"></el-image>-->
                    <!--                </el-form-item>-->
                    <!--              </el-col>-->
                    <!--            </el-row>-->

                    <el-form-item v-if="form.openPassword == 1" label="私密密钥" prop="password">
                        <el-input show-password v-model="form.password" minlength="6" maxlength="12" size="small"
                                  placeholder="请输入私密访问时的密钥"/>
                    </el-form-item>

                    <el-row>
                        <el-col :span="6">
                            <el-form-item label="价格" prop="price">
                                <el-input-number v-model="form.price" style="width: 100%" :precision="1" size="small"
                                                 placeholder="请输入价格" :step="0.1"></el-input-number>
                            </el-form-item>
                        </el-col>
                        <el-col :span="6">
                            <el-form-item label="评分" prop="rate">
                                <el-input-number v-model="form.rate" style="width: 100%" :precision="1" size="small"
                                                 placeholder="评分" :step="0.1" :max="10"></el-input-number>
                            </el-form-item>
                        </el-col>
                        <el-col :span="6">
                            <el-form-item label="排序" prop="rate">
                                <el-input-number v-model="form.sort" style="width: 100%" placeholder="排序" :step="1" size="small"
                                                 :max="1000"></el-input-number>
                            </el-form-item>
                        </el-col>
                        <el-col :span="6">
                            <el-form-item prop="tagId" label="标签">
                                <el-select
                                    style="width: 100%"
                                    v-model="tagList"
                                    multiple
                                    size="small"
                                    placeholder="请选择">
                                    <el-option
                                        v-for="tag in tagOptions"
                                        :key="tag.uid"
                                        :label="tag.content"
                                        :value="tag.uid + ``"
                                    />
                                </el-select>
                            </el-form-item>
                        </el-col>
                    </el-row>

                    <el-form-item label="备注" prop="remark">
                        <el-input v-model="form.remark" maxlength="30" size="small" placeholder="备注"/>
                    </el-form-item>
                </el-col>
                <el-col :span="6" :offset="1">
                    <el-form-item label="" class="images-uploader" prop="images">
                        <el-upload
                            class="el-upload"
                            :action="uploadPictureHost"
                            :show-file-list="false"
                            :on-success="handleImagesSuccess"
                            name="filedatas"
                            :before-upload="beforeImagesUpload"
                            :data="otherData"
                            :headers="headers">
                            <img v-if="form.images" :src="form.images" class="images"/>
                            <i v-else class="el-icon-plus images-uploader-icon"></i>
                        </el-upload>


                    </el-form-item>
                </el-col>
            </el-row>

            <el-form-item label="简介" prop="remark">
                <el-input v-model="form.summary" rows="6" type="textarea" placeholder="请输入内容" maxlength="1000"
                          show-word-limit/>
            </el-form-item>

            <el-form-item label="介绍" prop="description">
                <editor v-model="form.description" :height="200"></editor>
            </el-form-item>

            <el-divider content-position="center">视频信息(总长度：{{ formatVideoTime(form.totalVideoLength) }})
            </el-divider>

            <!--添加视频-->
            <el-row :gutter="10" class="mb8">
                <el-col :span="1.5">
                    <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAddMediaVideo">添加视频
                    </el-button>
                </el-col>
                <el-col :span="1.5">
                    <el-button type="danger" icon="el-icon-delete" size="mini" @click="handleDeleteMediaVideo">删除
                    </el-button>
                </el-col>
            </el-row>

            <el-table :data="videoList" :row-class-name="rowIndex" @selection-change="handleVideoSelectionChange"
                      ref="movieVideo">
                <el-table-column type="selection" width="50" align="center"/>
                <el-table-column label="序号" align="center" prop="index" width="50" />
                <el-table-column label="标题" width="200" show-overflow-tooltip prop="title" align="center"/>
                <el-table-column label="文件后缀" width="100" prop="ext" align="center"/>
                <el-table-column label="播放长度" :formatter="formatVideoTimeFormat"  width="100" prop="length"/>
                <el-table-column label="视频大小" :formatter="filesizeFormat"  width="100" prop="filesize" align="center"/>
                <el-table-column label="视频状态" highlight-current-row align="center"  width="100">
                    <template slot-scope="scope">
                        <dict-tag :options="videoStatusOptions" :value="scope.row.videoStatus"/>
                    </template>
                </el-table-column>
                <el-table-column label="存储类型" align="center" prop="storageType" width="100">
                    <template slot-scope="scope">
                        <dict-tag :options="mediaVideoStorageTypeOptions" :value="scope.row.storageType"/>
                    </template>
                </el-table-column>
                <el-table-column label="创建时间" prop="createTime" width="200" align="center" />
                <el-table-column label="备注" prop="remark" width="200" align="center" />
                <el-table-column label="源文件" show-overflow-tooltip width="300" prop="url" align="center"/>
                <el-table-column label="M3U8文件" show-overflow-tooltip width="300" prop="m3u8Url" align="center"/>
                <el-table-column label="操作" align="center" class-name="small-padding fixed-width" fixed="right" min-width="147">
                    <template slot-scope="scope">
                        <el-button
                            type="text"
                            size="small"
                            icon="el-icon-view"
                            @click="previewVideo(scope.row.url)"
                        >预览
                        </el-button>
                        <el-button
                            type="text"
                            size="small"
                            icon="el-icon-edit"
                            @click="handleUpdate(scope.row)"
                        >编辑
                        </el-button>
                        <el-button
                            type="text"
                            size="small"
                            icon="el-icon-refresh"
                            @click="transformVideo(scope.row)"
                        >转换
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <el-divider content-position="center">课程宣传照</el-divider>
            <imageUpload :limit="8" v-model="form.stills"/>

            <el-row>
                <el-col :span="12">
                    <el-divider content-position="center">主讲老师信息</el-divider>
                    <el-row :gutter="10" class="mb8">
                        <el-col :span="1.5">
                            <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAddDirector">
                                添加主讲老师
                            </el-button>
                        </el-col>
                        <el-col :span="1.5">
                            <el-button type="danger" icon="el-icon-delete" size="mini" @click="handleDeleteDirector">
                                删除
                            </el-button>
                        </el-col>
                    </el-row>
                    <el-table :data="directorList" :row-class-name="rowIndex"
                              @selection-change="handleActorSelectionChange" ref="movieDirector">
                        <el-table-column type="selection" width="50" align="center"/>
                        <el-table-column label="序号" align="center" prop="index" width="50"/>
                        <el-table-column label="姓名" prop="name"/>
                        <el-table-column prop="avatar" label="头像" align="center" width="60">
                            <template slot-scope="scope">
                                <el-image class="imagesList" :src="fileUploadHost + scope.row.avatar" lazy/>
                            </template>
                        </el-table-column>
                        <el-table-column label="标签" prop="label" :formatter="actorLabelFormat" width="250"/>
                    </el-table>
                </el-col>
                <el-col :span="12">
                    <el-divider content-position="center">老师信息</el-divider>
                    <el-row :gutter="10" class="mb8">
                        <el-col :span="1.5">
                            <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAddActor">添加老师
                            </el-button>
                        </el-col>
                        <el-col :span="1.5">
                            <el-button type="danger" icon="el-icon-delete" size="mini" @click="handleDeleteActor">删除
                            </el-button>
                        </el-col>
                    </el-row>
                    <el-table :data="actorList" :row-class-name="rowIndex"
                              @selection-change="handleActorSelectionChange" ref="movieActor">
                        <el-table-column type="selection" width="50" align="center"/>
                        <el-table-column label="序号" align="center" prop="index" width="50"/>
                        <el-table-column label="姓名" prop="name"/>
                        <el-table-column prop="avatar" label="头像" align="center" width="60">
                            <template slot-scope="scope">
                                <el-image class="imagesList" :src="fileUploadHost + scope.row.avatar" lazy/>
                            </template>
                        </el-table-column>
                        <el-table-column label="标签" prop="label" :formatter="actorLabelFormat" width="250"/>
                    </el-table>
                </el-col>
            </el-row>
        </el-form>

        <el-divider content-position="center"></el-divider>
        <div slot="footer" class="dialog-footer">
            <el-button type="primary" @click="submitForm">确 定</el-button>
            <el-button type="primary" @click="save">保 存</el-button>
            <el-button @click="cancel">取 消</el-button>
            <el-button @click="reset">重 置</el-button>
        </div>


        <!-- 添加或修改课程管理对话框 -->
        <el-dialog :title="title" :visible.sync="open" width="900px" append-to-body>
            <el-form ref="mediaVideoForm" :model="mediaVideoForm" :rules="videoRules" label-width="80px">
                <el-form-item label="标题" prop="title">
                    <el-input v-model="mediaVideoForm.title" placeholder="请输入标题"/>
                </el-form-item>

                <el-form-item label="url地址" prop="url">
                    <el-input v-model="mediaVideoForm.url" placeholder="请输入url地址"/>
                </el-form-item>

                <el-form-item label="文件后缀" prop="ext">
                    <el-input v-model="mediaVideoForm.ext" placeholder="请输入文件后缀"/>
                </el-form-item>


                <el-form-item label="播放长度" prop="length">
                    <el-input v-model="mediaVideoForm.length" disabled placeholder="请输入播放长度">
                        <template slot="append">{{ formatVideoTime(mediaVideoForm.length) }}</template>
                    </el-input>
                </el-form-item>

                <el-form-item label="文件大小" prop="filesize">
                    <el-input v-model="mediaVideoForm.filesize" disabled placeholder="文件大小">
                        <template slot="append">{{ calculateFileSize(mediaVideoForm.filesize) }}</template>
                    </el-input>
                </el-form-item>
                <el-form-item label="存储类型" prop="storageType">
                    <el-select v-model="mediaVideoForm.storageType" placeholder="请选择存储类型" clearable size="small">
                        <el-option
                            v-for="dict in mediaVideoStorageTypeOptions"
                            :key="dict.dictValue"
                            :label="dict.dictLabel"
                            :value="dict.dictValue"
                        ></el-option>
                    </el-select>
                </el-form-item>

                <el-form-item label="备注" prop="remark">
                    <el-input v-model="mediaVideoForm.remark" placeholder="请输入备注"/>
                </el-form-item>

                <el-form-item label="排序字段" prop="remark">
                    <el-input v-model="mediaVideoForm.sort" placeholder="请输入备注" auto-complete="off" />
                </el-form-item>


                <el-form-item label="上传视频" prop="url" v-if="mediaVideoForm.storageType == 'localStorage'">
                    <el-upload
                        class="upload-demo"
                        drag
                        :action="uploadVideoUrl"
                        :on-success="handleVideoSuccess"
                        :before-upload="beforeVideoUpload"
                        :headers="headers"
                        name="file"
                        :data="otherData"
                        multiple>
                        <i class="el-icon-upload"></i>
                        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
                        <div class="el-upload__tip" slot="tip">只能上传mp4文件，且不超过1G</div>
                    </el-upload>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="submitVideoForm">确 定</el-button>
                <el-button @click="open = false">取 消</el-button>
            </div>
        </el-dialog>

        <!--添加老师模态框-->
        <el-dialog :title="actorTitle" :visible.sync="actorOpen" width="900px" append-to-body>
            <el-form :model="queryActorParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
                <el-form-item label="姓名" prop="name">
                    <el-input
                        v-model="queryActorParams.name"
                        placeholder="请输入姓名"
                        clearable
                        size="small"
                        @keyup.enter.native="getActorList"
                    />
                </el-form-item>
                <el-form-item label="描述" prop="description">
                    <el-input
                        v-model="queryActorParams.description"
                        placeholder="请输入描述"
                        clearable
                        size="small"
                        @keyup.enter.native="getActorList"
                    />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" icon="el-icon-search" size="mini" @click="getActorList">搜索</el-button>
                </el-form-item>
            </el-form>


            <el-table :data="notSelectedActorList" @selection-change="handleActorSelectionChange">
                <el-table-column type="selection" width="55" align="center"/>
                <el-table-column label="主键" align="left" prop="actorId" width="50"/>
                <el-table-column label="姓名" align="center" prop="name" width="150"/>
                <el-table-column prop="avatar" label="头像" align="center" width="200">
                    <template slot-scope="scope">
                        <el-image class="actorImages" :src="fileUploadHost+scope.row.avatar"/>
                    </template>
                </el-table-column>
                <el-table-column label="标签" prop="label" :formatter="actorLabelFormat" width="300"/>
            </el-table>

            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="submitActorForm">确 定</el-button>
                <el-button @click="actorOpen = false">取 消</el-button>
            </div>

            <pagination
                v-show="actorTotal>0"
                :total="actorTotal"
                :page.sync="queryActorParams.currentPage"
                :limit.sync="queryActorParams.pageSize"
                :pageSizes="[4,8,10,20]"
                @pagination="getActorList"
            />
        </el-dialog>


    </div>
</template>

<script>
import {addMediaInfo, editMediaInfo, getMediaInfo, transformMediaInfo} from "@/api/mediaInfo";
import {listTag} from "@/api/mediaTag";
import {listCategory} from "@/api/mediaCategory";
import {notSelectedActorList} from "@/api/mediaActor";
import {getListByDictTypeList} from "@/api/sysDictData"
import {getToken} from "@/utils/auth";
import {getSystemConfig} from "@/api/systemConfig";

export default {
    name: "MediaDetail",
    components: {},
    data() {
        return {
            systemConfig: {},
            uploadPictureHost: process.env.PICTURE_API + '/file/pictures',
            fileUploadHost: process.env.FILE_API,
            headers: {
                Authorization: getToken(),
            },
            uploadVideoUrl: process.env.PICTURE_API + '/file/uploadVideo',
            otherData: {
                source: 'picture',
                userUid: 'uid00000000000000000000000000000000',
                adminUid: 'uid00000000000000000000000000000000',
                projectName: 'blog',
                sortName: 'admin',
                token: getToken()
            },
            // 遮罩层
            loading: true,
            fileLoading: true,
            // 选中数组
            ids: [],
            // 子表选中数据
            selectedMediaVideo: [],
            // 非单个禁用
            single: true,
            // 非多个禁用
            multiple: true,
            // 显示搜索条件
            showSearch: true,
            // 总条数
            total: 0,
            // 课程管理表格数据
            movieList: [],
            // 课程视频表格数据
            videoList: [],
            actorTitle: "",
            actorOpen: false,
            movieActorType: "",
            // 老师信息
            actorList: [],
            // 导演信息
            directorList: [],
            actorTotal: 0,
            queryActorParams: {
                currentPage: 1,
                pageSize: 4,
                name: null,
                avatar: null,
                description: null,
                awards: null,
                label: null,
                notSelectedIdsList: []
            },
            // 弹出层标题
            title: "",
            // 是否显示弹出层
            open: false,
            //课程国家字典
            countryOptions: [],
            // 状态字典
            statusOptions: [],
            // 课程类型
            typeOptions: [],
            // 视频状态字典
            videoStatusOptions: [],
            // 视频存储类型字典
            mediaVideoStorageTypeOptions: [],
            //字典是否
            sysYesNoOptions: [],
            // 开关字典
            commonSwitchOptions: [],
            langOptions: [],
            //标签字典
            tagOptions: [],
            //分类字典
            categoryOptions: [],
            //老师标签
            actorLabelOptions: [],
            tagList: [],
            notSelectedActorList: [],
            selectedActorList: [],
            formLabelWidth: "110px",
            lineLabelWidth: "110px",//一行的间隔数
            // 表单参数
            form: {},
            images: [],
            photoVisible: false,
            //课程视频信息Form
            mediaVideoForm: {
            },
            fileList: [], //  表格数据-文件列表
            //网盘数据
            pageData: {
                currentPage: 1,
                pageCount: 10,
                total: 0
            },
            selectionFile: [], // 勾选的文件
            selectFilePath: '', //  移动文件路径
            operationFile: {}, // 当前操作行
            filePath: '/',
            batchOperate: false, //  批量操作模式
            fileTableOpen: false,//网盘视频模态框
            // 表单校验
            rules: {
                title: [
                    {required: true, message: "标题不能为空", trigger: "blur"}
                ],
                categoryUid: [
                    {required: true, message: "分类不能为空", trigger: "blur"}
                ],
                tagId: [
                    {required: true, message: "标签不能为空", trigger: "blur"}
                ],
                description: [
                    {required: true, message: "简介不能为空", trigger: "blur"}
                ],
                password: [
                    {required: true, message: "秘钥不能为空", trigger: "blur"},
                    {min: 6, max: 12, message: '秘钥长度必须介于 6 和 12 之间', trigger: 'blur'}
                ]
            },
            // 表单校验
            videoRules: {
                title: [
                    {required: true, message: "标题不能为空", trigger: "blur"}
                ],
                filesize: [
                    {required: true, message: "视频文件没有上传", trigger: "blur"}
                ],
                sort: [
                    {required: true, message: '排序字段不能为空', trigger: 'blur'},
                    {pattern: /^[0-9]\d*$/, message: '排序字段只能为自然数'},
                ]
            }

        };
    },
    mounted() {
        this.init();
    },
    created() {
        // 查询字典
        this.getDictList();
        getSystemConfig().then(response => {
            if (response.code == this.$ECode.SUCCESS) {
                if (response.data) {
                    this.systemConfig = response.data;
                    this.fileUploadHost = this.systemConfig.localPictureBaseUrl
                }
            }
        });
    },
    watch: {
        "$route": {
            handler(route) {
                if (route.path !== '/media/info') {
                    const that = this;
                    that.init();
                }
            }
        },
        "form.categoryUid": {
            handler(categoryUid) {
                this.setCategoryName(categoryUid)
            }
        }
    },
    computed: {},
    methods: {
        /**
         * 字典查询
         */
        getDictList() {
            // 获取所有的分类
            listCategory({status: 1, currentPage: 1, pageSize: 100}).then(response => {
                this.categoryOptions = response.data.records;
            });
            // 获取所有标签
            listTag({status: 1, currentPage: 1, pageSize: 100}).then(response => {
                this.tagOptions = response.data.records;
            });
            /* const dictTypeList =  ['movie_country', 'movie_status', 'movie_type', 'sys_yes_no', 'actor_label', 'video_status', 'common_switch', 'lang'];
             getListByDictTypeList(dictTypeList).then(response => {
               this.countryOptions = response.data.movie_country.list;
               this.statusOptions = response.data.movie_status.list;
               this.typeOptions = response.data.movie_type.list;
               this.sysYesNoOptions = response.data.sys_yes_no.list;
               this.actorLabelOptions = response.data.actor_label.list;
               this.videoStatusOptions = response.data.video_status.list;
               this.commonSwitchOptions = response.data.common_switch.list;
               this.langOptions = response.data.lang.list;

             });*/

            const dictTypeList = ['media_country', 'sys_publish_status', 'media_type', 'sys_normal_disable', 'lang', 'media_video_storage_type', 'sys_video_status'];
            getListByDictTypeList(dictTypeList).then(response => {
                this.countryOptions = response.data.media_country.list;
                this.statusOptions = response.data.sys_publish_status.list;
                this.typeOptions = response.data.media_type.list;
                this.commonSwitchOptions = response.data.sys_normal_disable.list;
                this.langOptions = response.data.lang.list;
                this.mediaVideoStorageTypeOptions = response.data.media_video_storage_type.list;
                this.videoStatusOptions = response.data.sys_video_status.list;
            });

        },
        init() {
            const uid = this.$route.params && this.$route.params.uid;
            this.tagList = [];
            if (!uid) {
                this.reset();
            } else {
                this.getDetail(uid);
            }
        },
        getDetail(uid) {
            let params = {}
            params.uid = uid
            getMediaInfo(params).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    this.form = response.data;
                    this.videoList = response.data.videoList;
                    this.actorList = response.data.actorList;
                    this.directorList = response.data.directorList;
                    let that = this;
                    let dbTagList = []
                    that.tagList = []
                    if (that.form.tagId) {
                        dbTagList = that.form.tagId.split(",");
                    }
                    for (let index = 0; index < dbTagList.length; index++) {
                        if (dbTagList[index] != null && dbTagList[index] !== "") {
                            that.tagList.push(dbTagList[index]);
                        }
                    }
                } else {
                    this.$message.error(response.message)
                }

            });
        },
        calculateFileSize(size) {
            return this.$commonUtil.calculateFileSize(size)
        },
        // 取消按钮
        cancel() {
            this.$router.push({
                path: "/media/info",
                query: {}
            });
        },
        // 表单重置
        reset() {
            // 导出遮罩层
            this.exportLoading = false;
            this.videoList = [];
            this.actorList = [];
            this.directorList = [];
            this.tagList = [];
            this.resetForm("form");
            this.form = {
                uid: undefined,
                images: undefined,
                title: undefined,
                type: undefined,
                categoryUid: undefined,
                categoryName: undefined,
                country: undefined,
                tagId: undefined,
                description: undefined,
                publishBy: undefined,
                publishTime: undefined,
                status: 1,
                delFlag: null,
                summary: undefined,
                remark: undefined,
                clickCount: 0,
                commentCount: 0,
                followCount: 0,
                collectionCount: 0,
                supportCount: 0,
                opposeCount: 0,
                shareCount: 0,
                openComment: 1,
                openDownload: 1,
                price: 0,
                rate: 0,
                qrcodePath: undefined,
                openPassword: 0,
                password: undefined,
                stills: undefined,
                totalVideoLength: 0
            }
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

        /** 新增按钮操作 */
        handleAdd() {
            this.reset();
            this.open = true;
            this.title = "添加课程管理";
        },
        /** 修改按钮操作 */
        handleUpdate(row) {
            this.title = "修改课程管理";
            this.mediaVideoForm = row;
            this.open = true;
        },
        async submitForm() {
            const flag = await this.save();
            if (flag) {
                this.cancel();
            }
        },
        /** 保存按钮 */
        async save() {
            return new Promise((resolve, reject) => {
                let that = this;
                that.form.tagId = that.tagList.join(",");
                this.$refs["form"].validate(valid => {
                    if (valid) {
                        this.form.videoList = this.videoList;
                        this.form.actorList = this.actorList;
                        this.form.directorList = this.directorList;
                        if (this.form.uid != null) {
                            editMediaInfo(this.form).then(response => {
                                this.$commonUtil.message.success("修改成功");
                                this.getDetail(this.form.uid);
                            });
                        } else {
                            addMediaInfo(this.form).then(response => {
                                this.form = response.data;
                                this.$commonUtil.message.success("新增成功");
                                this.$router.replace({
                                    path: "/media/info/detail/" + this.form.uid
                                });
                            });
                        }
                        return resolve(true);
                    } else {
                        return resolve(false);
                    }
                });
            })

        },
        /** 删除按钮操作 */
        handleDelete(row) {
            const uids = row.uid || this.ids;
            this.$confirm('是否确认删除课程管理编号为"' + uids + '"的数据项?', "警告", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            }).then(function () {
                return delMovie(uids);
            }).then(() => {
                this.getList();
                this.$commonUtil.message.success("删除成功");
            })
        },
        /** 课程视频序号 */
        rowIndex({row, rowIndex}) {
            row.index = rowIndex + 1;
        },
        /** 课程视频添加按钮操作 */
        handleAddMediaVideo() {
            this.mediaVideoForm = {
                url: undefined,
                title: undefined,
                ext: undefined,
                filesize: 0,
                length: 0,
                status: 0,
                fileUid: undefined,
                storageType: 'localStorage',
                sort: 0,
            };
            this.title = "添加课程视频";
            this.open = true;
        },
        submitVideoForm() {
            this.$refs['mediaVideoForm'].validate(valid => {
                if (valid) {
                    const uid = this.mediaVideoForm.uid;
                    if (!uid) {
                        this.videoList.push(this.mediaVideoForm);
                    }
                    this.open = false;
                    this.mediaVideoForm = {};
                }
            });
            this.save();
        },
        /** 课程视频删除按钮操作 */
        handleDeleteMediaVideo(row) {
            if (this.selectedMediaVideo.length === 0) {
                this.$alert("请先选择要删除的课程视频数据", "提示", {confirmButtonText: "确定"});
            } else {
                for (let index = 0; index < this.selectedMediaVideo.length; index++) {
                    if (this.selectedMediaVideo[index] != null && this.selectedMediaVideo[index] !== "") {
                        this.videoList.splice(this.videoList.findIndex(item => item === this.selectedMediaVideo[index]), 1);
                    }
                }
                this.save();
            }
        },

        handleActorSelectionChange(selection) {
            this.selectedActorList = selection;
        },
        /** 单选框选中数据 */
        handleVideoSelectionChange(selection) {
            this.selectedMediaVideo = selection;
            this.single = selection.length !== 1;
        },
        submitActorForm() {
            if (!this.selectedActorList || this.selectedActorList.length === 0) {
                this.$commonUtil.message.error("请选择要添加的老师/导演！");
            }
            for (let index = 0; index < this.selectedActorList.length; index++) {
                if (this.selectedActorList[index] != null && this.selectedActorList[index] !== "") {
                    const data = this.selectedActorList[index];
                    const map = {
                        actorUid: data.uid,
                        mediaInfoUid: this.form.uid,
                        type: this.movieActorType,
                        avatar: data.avatar
                    }
                    if (this.movieActorType === 'actor') {
                        this.actorList.push(map);
                    } else {
                        this.directorList.push(map);
                    }
                }
            }
            this.save();
            this.actorOpen = false;
        },
        getActorList() {
            this.loading = true;
            let ids = [];
            if (this.actorList.length > 0 && this.movieActorType === 'actor') {
                ids = this.actorList.map(item => item.actorUid)
            } else if (this.directorList.length > 0 && this.movieActorType === 'director') {
                ids = this.directorList.map(item => item.actorUid)
            }
            this.queryActorParams.notSelectedIdsList = ids
            notSelectedActorList(this.queryActorParams).then(response => {
                this.notSelectedActorList = response.data.records;
                this.actorTotal = response.data.total;
                this.loading = false;
            });
        },
        // 添加导演
        handleAddDirector() {
            this.selectedActorList = [];
            this.movieActorType = "director";
            this.getActorList();
            this.actorTitle = "添加导演";
            this.actorOpen = true;

        },
        // 删除导演
        handleDeleteDirector() {
            if (!this.selectedActorList || this.selectedActorList.length === 0) {
                this.$commonUtil.message.error("请选择要删除的导演！");
                return;
            }
            for (let index = 0; index < this.selectedActorList.length; index++) {
                if (this.selectedActorList[index] != null && this.selectedActorList[index] !== "") {
                    this.directorList.splice(this.directorList.findIndex(item => item === this.selectedActorList[index]), 1);
                }
            }
            this.save();
        },

        // 添加老师
        handleAddActor() {
            this.selectedActorList = [];
            this.movieActorType = "actor";
            this.getActorList();
            this.actorTitle = "添加老师";
            this.actorOpen = true;
        },
        //删除老师
        handleDeleteActor() {
            if (!this.selectedActorList || this.selectedActorList.length === 0) {
                this.$commonUtil.message.error("请选择要删除的老师！");
                return;
            }
            for (let index = 0; index < this.selectedActorList.length; index++) {
                if (this.selectedActorList[index] != null && this.selectedActorList[index] !== "") {
                    this.actorList.splice(this.actorList.findIndex(item => item === this.selectedActorList[index]), 1);
                }
            }
            this.save();
        },
        // 状态字典翻译
        statusFormat(row, column) {
            return this.selectDictLabel(this.statusOptions, row.status);
        },
        //标签类型字典翻译
        actorLabelFormat(row, column) {
            if (!row.label) return '';
            return this.selectDictLabels(this.actorLabelOptions, row.label);
        },

        handleImagesSuccess(res, file) {
            if (res.code === this.$ECode.SUCCESS) {
                this.form.images = res.data[0].pictureUrl;
                this.$commonUtil.message.success("上传成功！");
            } else {
                this.$commonUtil.message.error("上传失败！");
            }
        },
        beforeImagesUpload(file) {
            const isImages = (file.type === 'image/jpeg') || (file.type === 'image/png');
            const isLt10M = file.size / 1024 / 1024 < 10;
            if (!isImages) {
                this.$commonUtil.message.error('上传头像图片只能是 JPG 格式 和 PNG 格式!');
            }
            if (!isLt10M) {
                this.$commonUtil.message.error('上传头像图片大小不能超过 10MB!');
            }
            return isImages && isLt10M;
        },
        // 视频上传成功
        handleVideoSuccess(res, file) {
            if (res.code === this.$ECode.SUCCESS) {
                const resData = res.data[0];
                this.mediaVideoForm = {
                    title: resData.fileOldName,
                    url: resData.pictureUrl,
                    ext: resData.picExpandedName,
                    filesize: resData.fileSize,
                    length: 0,
                    status: 0,
                    storageType: 'localStorage',
                    fileUid: resData.uid,
                    sort: 0
                };
                this.$commonUtil.message.success("上传成功！");
            } else {
                this.$commonUtil.message.error(res.message);
            }
        },
        beforeVideoUpload(file) {
            const isVideo = file.type === 'video/mp4';
            const isLt1G = file.size / 1024 / 1024 / 1024 < 1;
            if (!isVideo) {
                this.$commonUtil.message.error('上传视频文件只能是 video格式!');
            }
            if (!isLt1G) {
                this.$commonUtil.message.error('上传视频文件大小不能超过 1GB!');
            }
            return isVideo && isLt1G;
        },
        formatVideoTimeFormat(row, column) {
            return this.formatVideoTime(row.length);
        },
        formatVideoTime(timestamp) {
            if (!timestamp) {
                return '00:00'
            }
            let timeMs = timestamp / 1000;
            let hours = parseInt(timeMs / 3600);
            let mini = parseInt(timeMs % 3600 / 60);
            let second = Math.ceil(timeMs % 60);
            if (hours > 0) {
                return hours + ':' + mini + ':' + second;
            } else {
                return mini + ':' + second;
            }
        },
        filesizeFormat(row, column) {
            return this.calculateFileSize(row.filesize);
        },
        /**
         * 表格勾选框事件 | 保存被勾选的文件
         * @param {object[]} selection 被勾选的文件数组
         */
        setSelectionFile(selection) {
            this.selectionFile = selection
        },
        // 赋值分类名称 冗余数据
        setCategoryName(categoryUid) {
            if (!categoryUid || this.categoryOptions.length === 0) {
                return
            }
            const categoryOptions = this.categoryOptions;
            for (let index = 0; index < categoryOptions.length; index++) {
                const category = categoryOptions[index];
                if (category != null && category.uid == categoryUid) {
                    this.form.categoryName = category.name;
                    break;
                }
            }
        },
        // 回显数据字典
        selectDictLabel(datas, value) {
            var actions = [];
            Object.keys(datas).some((key) => {
                if (datas[key].dictValue == ('' + value)) {
                    actions.push(datas[key].dictLabel);
                    return true;
                }
            })
            return actions.join('');
        },

        // 回显数据字典（字符串数组）
        selectDictLabels(datas, value, separator) {
            let actions = [];
            const currentSeparator = undefined === separator ? "," : separator;
            const temp = value.split(currentSeparator);
            Object.keys(value.split(currentSeparator)).some((val) => {
                Object.keys(datas).some((key) => {
                    if (datas[key].dictValue == ('' + temp[val])) {
                        actions.push(datas[key].dictLabel + currentSeparator);
                    }
                })
            })
            return actions.join('').substring(0, actions.join('').length - 1);
        },

        previewVideo(url) {
            window.open(url);
        },
        // 转化视频
        transformVideo(row) {
            let params = {}
            params.videoUid = row.uid
            transformMediaInfo(params).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    this.getDetail(this.$route.params.uid);
                    this.$commonUtil.message.success(response.message);
                } else {
                    this.$commonUtil.message.error(response.message);
                }
            });
        },
    }
};
</script>
<style scoped>
.images-uploader .el-upload {
    border: 2px dashed #d9d9d9;
    border-radius: 1px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    width: 200px;
    height: 100px;
}

.images-uploader .el-upload:hover {
    border-color: #409EFF;
}

.images-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 200px;
    height: 100px;
    line-height: 100px;
    text-align: center;
}

.images {
    left: 10px;
    width: 234px;
    height: 332.6px;
    display: block;
}

.actorImages {
    width: 100px;
    height: 144px;
    display: block;
}

</style>

<style lang="stylus" scoped>
@import '~@/assets/styles/varibles.styl';

.pagination-wrapper {
    position: relative;
    border-top: 1px solid $BorderBase;
    height: 44px;
    line-height: 44px;
    text-align: center;

    .current-page-count {
        position: absolute;
        left: 16px;
        height: 32px;
        line-height: 32px;
        font-size: 13px;
        color: $RegularText;
    }
}
</style>
