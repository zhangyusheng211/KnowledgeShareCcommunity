<template>
    <div class="app-container">
        <!-- 查询和其他操作 -->
        <div class="filter-container" style="margin: 10px 0 10px 0;" v-permission="'/link/getList'">
            <el-input
                clearable
                @keyup.enter.native="handleFind"
                class="filter-item"
                style="width: 200px;"
                v-model="keyword"
                placeholder="请输入配置名"
                size="small"
            ></el-input>

            <el-select v-model="linkStatusKeyword" clearable placeholder="是否发布" style="width:140px" size="small">
                <el-option
                    v-for="item in isPublishDictList"
                    :key="item.uid"
                    :label="item.dictLabel"
                    :value="item.dictValue"
                ></el-option>
            </el-select>

            <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFind" size="small"
                       v-permission="'/secretConfig/getList'">查找
            </el-button>
            <el-button class="filter-item" type="primary" @click="handleAdd" icon="el-icon-edit" size="small"
                       v-permission="'/secretConfig/add'">添加配置
            </el-button>
        </div>

        <el-table :data="tableData"
                  style="width: 100%"
                  @sort-change="changeSort"
                  :default-sort="{prop: 'sort', order: 'descending'}">
            <el-table-column type="selection"></el-table-column>

            <el-table-column label="序号" width="60" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.$index + 1 }}</span>
                </template>
            </el-table-column>

<!--            <el-table-column label="网站图标" width="80" align="center">-->
<!--                <template slot-scope="scope">-->
<!--                    <el-image-->
<!--                        v-if="scope.row.photoList"-->
<!--                        :src="scope.row.photoList[0]"-->
<!--                        style="width: 50px;height:50px;"-->
<!--                        :preview-src-list="scope.row.photoList"-->
<!--                    ></el-image>-->
<!--                </template>-->
<!--            </el-table-column>-->

            <el-table-column
                label="密钥类型"
                width="150"
                align="center"
                prop="level"
            >
                <template slot-scope="scope">
                    <el-tag
                        v-for="item in subSecretTypeDictList"
                        v-if="scope.row.subSecretType == item.dictValue"
                        :key="item.uid"
                        :type="item.listClass"
                    >
                        {{ item.dictLabel }}
                    </el-tag>
                </template>
            </el-table-column>

            <el-table-column label="业务ID(AppID)" width="200" align="center">
                <template slot-scope="scope">
                    <span>{{ setMask(scope.row.bizId) }}</span>
                </template>
            </el-table-column>

            <el-table-column label="业务密钥(Secret)" width="200" align="center">
                <template slot-scope="scope">
                    <span>{{ setMask(scope.row.bizSecret) }}</span>
                </template>
            </el-table-column>

            <el-table-column v-if="!['3'].includes(secretType)" label="业务令牌(Key)" width="200" align="center">
                <template slot-scope="scope">
                    <span>{{ setMask(scope.row.bizKey) }}</span>
                </template>
            </el-table-column>

            <el-table-column :label="secretType == '1'?'请求URL':'回调URL'" width="200" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.requestUrl }}</span>
                </template>
            </el-table-column>

            <el-table-column label="发布状态" width="100" align="center" prop="linkStatus" sortable="custom"
                             :sort-by="['isPublish']">
                <template slot-scope="scope">
                    <template>
                        <el-tag v-for="item in isPublishDictList" :key="item.uid" :type="item.listClass"
                                v-if="scope.row.isPublish == item.dictValue">{{ item.dictLabel }}
                        </el-tag>
                    </template>
                </template>
            </el-table-column>

            <el-table-column label="备注" width="200" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.remark }}</span>
                </template>
            </el-table-column>

            <el-table-column label="排序" width="100" align="center" prop="sort" sortable="custom"
                             :sort-orders="['ascending', 'descending']">
                <template slot-scope="scope">
                    <el-tag type="warning">{{ scope.row.sort }}</el-tag>
                </template>
            </el-table-column>

            <el-table-column label="创建时间" width="160" align="center" prop="createTime" sortable="custom"
                             :sort-by="['createTime']">
                <template slot-scope="scope">
                    <span>{{ scope.row.createTime }}</span>
                </template>
            </el-table-column>

            <el-table-column label="操作" fixed="right" min-width="240">
                <template slot-scope="scope">
                    <el-button @click="handleEdit(scope.row)" type="primary" size="mini" v-permission="'/link/edit'">
                        编辑
                    </el-button>
                    <el-button @click="handleDelete(scope.row)" type="danger" size="mini" v-permission="'/link/delete'">
                        删除
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

        <!-- 添加或修改对话框 -->
        <el-dialog :title="title" :visible.sync="dialogFormVisible">
            <el-form :model="form" :rules="rules" ref="form">

                <el-form-item label="密钥类型" :label-width="formLabelWidth" prop="subSecretType">
                    <el-select v-model="form.subSecretType" size="small" placeholder="请选择密钥类型" style="width:200px">
                        <el-option
                            v-for="item in subSecretTypeDictList"
                            :key="item.uid"
                            :label="item.dictLabel"
                            :value="item.dictValue"
                        ></el-option>
                    </el-select>
                </el-form-item>

                <el-form-item label="业务ID(AppID)" :label-width="formLabelWidth" prop="bizId">
                    <el-input v-model="form.bizId" auto-complete="off"></el-input>
                </el-form-item>

                <el-form-item label="业务密钥(Secret)" :label-width="formLabelWidth" prop="secretKey">
                    <el-input v-model="form.bizSecret" auto-complete="off"></el-input>
                </el-form-item>

                <!--第三方登录不需要令牌-->
                <el-form-item v-if="!['3'].includes(secretType)" label="业务令牌(Key)" :label-width="formLabelWidth" prop="secretKey">
                    <el-input v-model="form.bizKey" auto-complete="off"></el-input>
                </el-form-item>

                <el-form-item :label="secretType == '1'?'请求URL':'回调URL'" :label-width="formLabelWidth" prop="requestUrl">
                    <el-input v-model="form.requestUrl" auto-complete="off"></el-input>
                </el-form-item>

                <el-form-item label="扩展配置" :label-width="formLabelWidth" prop="extra">
                    <el-input v-model="form.extra" auto-complete="off"></el-input>
                </el-form-item>

                <el-form-item label="备注" :label-width="formLabelWidth" prop="remark">
                    <el-input v-model="form.remark" auto-complete="off"></el-input>
                </el-form-item>

                <el-form-item label="是否发布" :label-width="formLabelWidth" prop="isPublish">
                    <el-select v-model="form.isPublish" size="small" placeholder="请选择" style="width:200px">
                        <el-option
                            v-for="item in isPublishDictList"
                            :key="item.uid"
                            :label="item.dictLabel"
                            :value="parseInt(item.dictValue)"
                        ></el-option>
                    </el-select>
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

        <avatar-cropper
            v-show="imagecropperShow"
            :key="imagecropperKey"
            :width="300"
            :height="300"
            :url="url"
            lang-type="zh"
            @close="close"
            @crop-upload-success="cropSuccess"
        />
    </div>
</template>

<script>
import {addSecretConfig, deleteBatchSecretConfig, editSecretConfig, getSecretConfigList} from "@/api/secretConfig";
import {getListByDictTypeList} from "@/api/sysDictData"
import AvatarCropper from '@/components/AvatarCropper'

export default {
    props: ['secretType'],
    data() {
        return {
            tableData: [],
            keyword: "",
            linkStatusKeyword: null, //配置状态查询
            currentPage: 1,
            pageSize: 10,
            total: 0, //总数量
            title: "添加密钥",
            subSecretTypeDictList: [], // 密钥子类型字典
            dialogFormVisible: false, //控制弹出框
            isPublishDictList: [], // 是否发布状态字典
            storeDictList: [], // 存储类型字段
            languageModelTypeDictList: [], // 语言大模型字典
            thirdLoginDictList: [], // 第三方登录字典
            thirdSecretDictList: [], // 第三方密钥配置
            isPublishDefault: null, // 配置状态默认值
            formLabelWidth: "130px",
            isEditForm: false,
            imagecropperShow: false, // 是否显示截图框
            imagecropperKey: 0,
            url: process.env.PICTURE_API + "/file/cropperPicture",
            photoVisible: false, //控制图片选择器的显示
            photoList: [],
            fileIds: "",
            icon: false, //控制删除图标的显示
            orderByDescColumn: "", // 降序字段
            orderByAscColumn: "", // 升序字段
            form: {
                uid: null,
                content: "",
                clickCount: 0
            },
            rules: {
                title: [
                    {required: true, message: '配置名称不能为空', trigger: 'blur'},
                    {min: 1, max: 20, message: '长度在1到20个字符'},
                ],
                url: [
                    {required: true, message: 'URL不能为空', trigger: 'blur'},
                    {pattern: /^((https|http|ftp|rtsp|mms)?:\/\/)[^\s]+/, message: '请输入有效的URL'},
                ],
                isPublish: [
                    {required: true, message: '是否发布不能为空', trigger: 'blur'},
                ],
                email: [
                    {pattern: /\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/, message: '请输入正确的邮箱'},
                ],
                linkStatus: [
                    {required: true, message: '配置状态不能为空', trigger: 'blur'}
                ],
                sort: [
                    {required: true, message: '排序字段不能为空', trigger: 'blur'},
                    {pattern: /^[0-9]\d*$/, message: '排序字段只能为自然数'},
                ]
            },
        };
    },
    components: {
        AvatarCropper
    },
    created() {
        // 字典查询
        this.getDictList()
        this.secretConfigList();
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
            this.secretConfigList()
        },
        setMask(content) {
            if (!content) {
                return content
            }
            let masked = '';
            let length = content.length;
            let frontLength, endLength;
            let middleLenght = length/2;

            // 如果原始内容的长度小于或等于4，直接返回原始内容
            if (middleLenght <= 3) {
                return content;
            }
            frontLength = middleLenght * 0.7;
            endLength = middleLenght * 0.7;

            for (let i = 0; i < length; i++) {
                if (i < frontLength || i >= length - endLength) {
                    masked += content[i];
                } else {
                    masked += '*';
                }
            }
            return masked;
        },
        secretConfigList: function () {
            let params = {};
            params.keyword = this.keyword;
            params.secretType = this.secretType
            params.currentPage = this.currentPage;
            params.pageSize = this.pageSize;
            params.orderByDescColumn = this.orderByDescColumn
            params.orderByAscColumn = this.orderByAscColumn
            getSecretConfigList(params).then(response => {
                this.tableData = response.data.records;
                this.currentPage = response.data.current;
                this.pageSize = response.data.size;
                this.total = response.data.total;
            });
        },
        getFormObject: function () {
            let formObject = {
                uid: null,
                title: null,
                summary: null,
                url: null,
                clickCount: 0,
                sort: 0,
                isPublish: this.isPublishDefault
            };
            return formObject;
        },
        // 通过密钥类型，获取子密钥类型
        getSubSecretTypeBySecretType() {
            let secretType = this.secretType
            if (!secretType) {
                return
            }
            switch (secretType) {
                case "1": {
                    this.subSecretTypeDictList = this.languageModelTypeDictList
                } break;
                case "2": {
                    this.subSecretTypeDictList = this.cashPayMethodDictList
                } break;
                case "3": {
                    this.subSecretTypeDictList = this.thirdLoginDictList
                } break;
                case "4": {
                    this.subSecretTypeDictList = this.storeDictList
                } break;
                case "10": {
                    this.subSecretTypeDictList = this.thirdSecretDictList
                } break;
            }
            console.log("获取密钥类型", this.subSecretTypeDictList, secretType)
        },
        /**
         * 字典查询
         */
        getDictList: function () {
            let dictTypeList = ['sys_publish_status', 'sys_secret_type', 'sys_picture_priority', 'language_model_type', 'cash_pay_method', 'sys_account_source', 'third_secret_type']
            getListByDictTypeList(dictTypeList).then(response => {
                if (response.code === this.$ECode.SUCCESS) {
                    let dictMap = response.data;
                    this.isPublishDictList = dictMap.sys_publish_status.list
                    this.cashPayMethodDictList = dictMap.cash_pay_method.list
                    this.storeDictList = dictMap.sys_picture_priority.list
                    this.languageModelTypeDictList = dictMap.language_model_type.list
                    this.thirdLoginDictList = dictMap.sys_account_source.list
                    this.thirdSecretDictList = dictMap.third_secret_type.list
                    if (dictMap.sys_publish_status.defaultValue) {
                        this.isPublishDefault = parseInt(dictMap.sys_publish_status.defaultValue);
                    }
                    // 获取子密钥类型
                    this.getSubSecretTypeBySecretType()
                }
            });
        },
        cropSuccess(resData) {
            this.imagecropperShow = false
            this.imagecropperKey = this.imagecropperKey + 1
            let photoList = []
            photoList.push(resData.url);
            this.form.photoList = photoList;
            this.form.fileUid = resData.uid
        },
        //弹出选择图片框
        checkPhoto() {
            this.imagecropperShow = true
        },
        close() {
            this.imagecropperShow = false
        },
        deletePhoto: function () {
            this.form.photoList = null;
            this.form.fileUid = "";
            this.icon = false;
        },
        handleFind: function () {
            this.currentPage = 1
            this.secretConfigList();
        },
        handleAdd: function () {
            this.title = "增加配置"
            this.dialogFormVisible = true;
            this.form = this.getFormObject();
            this.isEditForm = false;
        },
        handleEdit: function (row) {
            this.title=  "编辑配置";
            this.dialogFormVisible = true;
            this.isEditForm = true;
            this.form = row;
        },
        handleDelete: function (row) {
            let that = this;
            this.$confirm("此操作将把密钥配置删除, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    let params = {};
                    params.uid = row.uid;
                    deleteBatchSecretConfig(params).then(response => {
                        if (response.code === this.$ECode.SUCCESS) {
                            this.$commonUtil.message.success(response.message)
                            that.secretConfigList();
                        } else {
                            this.$commonUtil.message.error(response.message)
                        }
                    });
                })
                .catch(() => {
                    this.$commonUtil.message.info("已取消删除")
                });
        },
        handleCurrentChange: function (val) {
            this.currentPage = val;
            this.secretConfigList();
        },
        submitForm: function () {
            this.form.secretType = this.secretType
            this.$refs.form.validate((valid) => {
                if (!valid) {
                    console.log("校验失败")
                } else {
                    if (this.isEditForm) {
                        editSecretConfig(this.form).then(response => {
                            if (response.code == this.$ECode.SUCCESS) {
                                this.$commonUtil.message.success(response.message)
                                this.dialogFormVisible = false;
                                this.secretConfigList();
                            } else {
                                this.$commonUtil.message.error(response.message)
                            }
                        });
                    } else {
                        addSecretConfig(this.form).then(response => {
                            if (response.code == this.$ECode.SUCCESS) {
                                this.$commonUtil.message.success(response.message)
                                this.dialogFormVisible = false;
                                this.secretConfigList();
                            } else {
                                this.$commonUtil.message.error(response.message)
                            }
                        });
                    }
                }
            })

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
    width: 100px;
    height: 100px;
    line-height: 100px;
    text-align: center;
}

.imgBody {
    width: 100px;
    height: 100px;
    border: solid 2px #ffffff;
    float: left;
    position: relative;
}

.uploadImgBody {
    margin-left: 5px;
    width: 100px;
    height: 100px;
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

.img {
    width: 100%;
    height: 100%;
}

img {
    width: 100px;
    height: 100px;
}
</style>
