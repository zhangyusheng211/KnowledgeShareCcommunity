<template>
    <div class="app-container">
        <!-- 查询和其他操作 -->
        <div class="filter-container" style="margin: 10px 0 10px 0;" v-permission="'/product/getList'">
            <el-input
                clearable
                @keyup.enter.native="handleFind"
                class="filter-item"
                style="width: 200px;"
                v-model="queryParams.keyword"
                placeholder="请输入关键字"
                size="small"
            ></el-input>

            <el-select v-model="queryParams.isPublish" clearable placeholder="是否发布" style="width:140px"
                       size="small">
                <el-option
                    v-for="item in isPublishDictList"
                    :key="item.uid"
                    :label="item.dictLabel"
                    :value="item.dictValue"
                ></el-option>
            </el-select>

            <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFind" size="small"
                       v-permission="'/product/getList'">查找
            </el-button>
            <el-button class="filter-item" type="primary" @click="handleAdd" icon="el-icon-edit" size="small"
                       v-permission="'/product/add'">添加商品
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

            <el-table-column align="center" label="封面图" width="240">
                <template slot-scope="scope">
                    <el-image v-if="scope.row.coverPhotoUrl" :src="scope.row.coverPhotoUrl"
                              :preview-src-list="[scope.row.coverPhotoUrl]"
                              style="width: 184px; height: 80px;"
                    ></el-image>
                </template>
            </el-table-column>

            <el-table-column label="商品名称" width="200" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.name }}</span>
                </template>
            </el-table-column>

            <el-table-column label="商品简介" width="200" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.summary }}</span>
                </template>
            </el-table-column>

            <el-table-column label="商品分类" width="100" align="center" prop="linkStatus" sortable="custom"
                             :sort-by="['categoryUid']">
                <template slot-scope="scope">
                    <template>
                        <el-tag v-for="item in categoryList" :key="item.uid" type="danger"
                                v-if="scope.row.categoryUid == item.uid">{{ item.name }}
                        </el-tag>
                    </template>
                </template>
            </el-table-column>

            <el-table-column label="商品价格" width="200" align="center">
                <template slot-scope="scope">
                    <span v-if="scope.row.payType === 1">
                                                {{ scope.row.price }} 积分
                        <span v-if="scope.row.chargeType == 3">({{ scope.row.discountPrice }}积分)</span>

                    </span>
                    <span v-if="scope.row.payType === 2">
                        ¥ {{ scope.row.price / 100 }}
                        <span v-if="scope.row.chargeType == 3">(¥{{ scope.row.discountPrice / 100 }})</span>
                    </span>

                </template>
            </el-table-column>

            <el-table-column label="支付类型" width="100" align="center" prop="linkStatus" sortable="custom"
                             :sort-by="['payType']">
                <template slot-scope="scope">
                    <template>
                        <el-tag v-for="item in payTypeDictList" :key="item.uid" :type="item.listClass"
                                v-if="scope.row.payType == item.dictValue">{{ item.dictLabel }}
                        </el-tag>
                    </template>
                </template>
            </el-table-column>

            <el-table-column label="收费模式" width="100" align="center" prop="linkStatus" sortable="custom"
                             :sort-by="['chargeType']">
                <template slot-scope="scope">
                    <template>
                        <el-tag v-for="item in chargeTypeDictList" :key="item.uid" :type="item.listClass"
                                v-if="scope.row.chargeType == item.dictValue">{{ item.dictLabel }}
                        </el-tag>
                    </template>
                </template>
            </el-table-column>

            <el-table-column label="库存数量" width="100" align="center" prop="linkStatus" sortable="custom"
                             :sort-by="['stockCount']">
                <template slot-scope="scope">
                    <template>
                        <el-tag v-if="scope.row.stockCount === -1" type="success"> 无上限</el-tag>
                        <el-tag v-else-if="scope.row.stockCount === 0" type="danger"> {{ scope.row.stockCount }}
                        </el-tag>
                        <el-tag v-else type="success"> {{ scope.row.stockCount }}</el-tag>
                    </template>
                </template>
            </el-table-column>

            <el-table-column label="邮寄运费（分）" width="200" align="center" prop="linkStatus" sortable="custom"
                             :sort-by="['freight']">
                <template slot-scope="scope">
                    <template>
                        <el-tag v-if="scope.row.freight === -1" type="success"> 无需邮寄</el-tag>
                        <el-tag v-else-if="scope.row.freight === 0" type="danger"> 包邮</el-tag>
                        <el-tag v-else type="warning">
                            <span v-if="scope.row.payType === 1">{{ scope.row.freight }} 积分</span>
                            <span v-if="scope.row.payType === 2">¥ {{ scope.row.freight / 100 }} </span>
                        </el-tag>
                    </template>
                </template>
            </el-table-column>

            <el-table-column label="配置" width="200" align="center" prop="isSelection" sortable="custom">
                <template slot-scope="scope">
                    <template>
                        <div style="margin-top: 5px; color: #555555">
                            <span>精选：</span>
                            <el-switch v-model="scope.row.selectionStatus" active-color="#F5DEB3"
                                       @change="handChange(scope.row)"></el-switch>
                        </div>
                        <div style="margin-top: 5px; color: #555555">
                            <span>置顶：</span>
                            <el-switch v-model="scope.row.topStatus" active-color="#F5DEB3"
                                       @change="handChange(scope.row)"></el-switch>
                        </div>
                        <div style="margin-top: 5px; color: #555555">
                            <span>发布：</span>
                            <el-switch v-model="scope.row.publishStatus" active-color="#F5DEB3"
                                       @change="handChange(scope.row)"></el-switch>
                        </div>
                    </template>
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
                    <el-button @click="handleEdit(scope.row)" type="primary" size="mini" v-permission="'/product/edit'">
                        编辑
                    </el-button>
                    <el-button @click="handleDelete(scope.row)" type="danger" size="mini"
                               v-permission="'/product/deleteBatch'">
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
        <el-dialog :title="title" :visible.sync="dialogFormVisible" fullscreen>
            <el-form :model="form" :rules="rules" ref="form">

                <el-row>
                    <el-col :span="8">
                        <el-form-item label="封面图" :label-width="formLabelWidth" prop="fileUid">
                            <div class="imgBody" v-if="form.coverPhotoUrl">
                                <i class="el-icon-error inputClass"  @click="deletePhoto(1)"></i>
                                <img  v-bind:src="form.coverPhotoUrl"
                                      style="display:inline; width: 195px;height: 105px;"/>
                            </div>
                            <div v-else class="uploadImgBody" @click="checkPhoto(1)">
                                <i class="el-icon-plus avatar-uploader-icon"></i>
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="16">
                        <el-form-item label="轮播图" :label-width="formLabelWidth" prop="slidesFileUidList">
                            <div class="uploadImgBody" @click="checkPhoto(2)">
                                <i class="el-icon-plus avatar-uploader-icon"></i>
                            </div>
                            <div v-if="form.slidesPhotoUrlList && form.slidesPhotoUrlList.length > 0" >
                                <div class="imgBody" v-for="(item, index) in  form.slidesPhotoUrlList">
                                    <i class="el-icon-error inputClass"  @click="deletePhoto(2)"></i>
                                    <img  v-bind:src="item"
                                          style="display:inline; width: 195px;height: 105px;"/>
                                </div>
                            </div>


                        </el-form-item>
                    </el-col>
                </el-row>


                <el-form-item label="商品名称" :label-width="formLabelWidth" prop="name">
                    <el-input v-model="form.name" auto-complete="off"></el-input>
                </el-form-item>

                <el-form-item label="商品简介" :label-width="formLabelWidth" prop="summary">
                    <el-input v-model="form.summary" auto-complete="off"></el-input>
                </el-form-item>

                <el-form-item label="商品分类" :label-width="formLabelWidth" prop="isPublish">
                    <el-select v-model="form.categoryUid" size="small" placeholder="请选择" style="width:200px">
                        <el-option
                            v-for="item in categoryList"
                            :key="item.uid"
                            :label="item.name"
                            :value="item.uid"
                        ></el-option>
                    </el-select>
                </el-form-item>

                <el-form-item label="支付类型" :label-width="formLabelWidth" prop="payType">
                    <el-select
                        v-model="form.payType"
                        size="small"
                        placeholder="请选择"
                        style="width: 200px"
                    >
                        <el-option
                            v-for="item in payTypeDictList"
                            :key="item.uid"
                            :label="item.dictLabel"
                            :value="parseInt(item.dictValue)"
                        ></el-option>
                    </el-select>
                </el-form-item>

                <!--1. 收费模式和金额，仅付费使用时才使用-->
                <el-row>
                    <el-col :span="8">
                        <el-form-item label="收费模式" :label-width="formLabelWidth" prop="payType">
                            <el-select
                                v-model="form.chargeType"
                                size="small"
                                placeholder="请选择"
                                style="width: 200px"
                            >
                                <el-option
                                    v-for="item in chargeTypeDictList"
                                    :key="item.uid"
                                    :label="item.dictLabel"
                                    :value="parseInt(item.dictValue)"
                                ></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="16">
                        <el-form-item label="折扣时间" :label-width="formLabelWidth" prop="discountTimeList"
                                      v-if="form.chargeType == 3">
                            <el-date-picker
                                size="small"
                                v-model="discountTimeList"
                                type="datetimerange"
                                align="right"
                                unlink-panels
                                range-separator="至"
                                start-placeholder="开始日期"
                                end-placeholder="结束日期"
                                value-format="yyyy-MM-dd HH:mm:ss"
                                :picker-options="pickerOptions">
                            </el-date-picker>
                        </el-form-item>
                    </el-col>
                </el-row>


                <el-row v-if="form.chargeType !== 1">
                    <el-col :span="12">
                        <el-form-item label="价格(分)" :label-width="formLabelWidth" prop="price">
                            <template slot="label">
                                价格(分)
                                <el-popover
                                    placement="top-start"
                                    width="300"
                                    trigger="hover">
                                    <div>
                                        <div>积分支付时，设置的是多少积分</div>
                                        <div>现金支付时: 设置的是现金（单位分）</div>
                                    </div>
                                    <i slot="reference" style="cursor: pointer;margin-left: 2px"
                                       class="el-icon-question"/>
                                </el-popover>
                            </template>
                            <el-input size="small" v-model="form.price" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>

                    <el-col :span="12" v-if="form.chargeType === 3">
                        <el-form-item
                            :label-width="formLabelWidth"
                            label="折扣价(分)"
                            prop="discountPrice"
                        >
                            <el-input
                                size="small"
                                v-model="form.discountPrice"
                                auto-complete="off"
                            />
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row>
                    <el-col :span="6">
                        <el-form-item
                            :label-width="formLabelWidth"
                            label="库存数量"
                            prop="stockCount"
                        >
                            <template slot="label">
                                库存数量(分)
                                <el-popover
                                    placement="top-start"
                                    width="250"
                                    trigger="hover">
                                    <div>
                                        <div>-1表示无库存上限</div>
                                    </div>
                                    <i slot="reference" style="cursor: pointer;margin-left: 2px" class="el-icon-question"/>
                                </el-popover>
                            </template>
                            <el-input
                                size="small"
                                v-model="form.stockCount"
                                auto-complete="off"
                            />
                        </el-form-item>
                    </el-col>
                    <el-col :span="6">
                        <el-form-item label="邮寄运费(分)" :label-width="formLabelWidth" prop="freight">
                            <template slot="label">
                                邮寄运费(分)
                                <el-popover
                                    placement="top-start"
                                    width="300"
                                    trigger="hover">
                                    <div>
                                        <div>积分支付时，设置的是多少积分</div>
                                        <div>现金支付时: 设置的是现金（单位分）</div>
                                        <div>目前暂不支持积分和现金混合支付</div>
                                        <div>-1表示无需邮寄，0 表示包邮</div>
                                    </div>
                                    <i slot="reference" style="cursor: pointer;margin-left: 2px" class="el-icon-question"/>
                                </el-popover>
                            </template>
                            <el-input size="small" v-model="form.freight" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>

                    <el-col :span="6">
                        <el-form-item label="是否发布" :label-width="formLabelWidth" prop="isPublish">
                            <el-select v-model="form.isPublish" size="small" placeholder="请选择" style="width:100px">
                                <el-option
                                    v-for="item in isPublishDictList"
                                    :key="item.uid"
                                    :label="item.dictLabel"
                                    :value="parseInt(item.dictValue)"
                                ></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="6">
                        <el-form-item label="排序" :label-width="formLabelWidth" prop="sort">
                            <el-input v-model="form.sort" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-form-item label="备注" :label-width="formLabelWidth" prop="remark">
                    <el-input size="small" v-model="form.remark" auto-complete="off"></el-input>
                </el-form-item>

                <el-form-item label="商品描述" :label-width="formLabelWidth" prop="content">
                    <CKEditor v-if="systemConfig.editorModel == '0'" ref="editor" :content="form.content"
                               :height="300"></CKEditor>
                    <Mavon v-if="systemConfig.editorModel == '1'" :content="form.content" ref="editor"
                           :height="300"></Mavon>
                </el-form-item>

            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitForm">确 定</el-button>
            </div>
        </el-dialog>

        <avatar-cropper
            :key="imagecropperKey"
            :url="url"
            :width="740"
            :height="320"
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
import {addProduct, deleteBatchProduct, editProduct, getProductList} from "@/api/product";
import {getCommonCategoryList} from "@/api/commonCategory";
import {getListByDictTypeList} from "@/api/sysDictData"
import AvatarCropper from '@/components/AvatarCropper'
import {getSystemConfig} from "@/api/systemConfig";
import CKEditor from "../../components/CKEditor";
import Mavon from "../../components/Mavon";
export default {
    props: ['secretType'],
    data() {
        return {
            tableData: [],
            categoryList: [],
            keyword: "",
            queryParams: {
                keyword: "",
                isPublish: null,
            },
            systemConfig: {},
            photoType: 1, // 图片上传类型
            currentPage: 1,
            pageSize: 10,
            total: 0, //总数量
            title: "添加商品",
            dialogFormVisible: false, //控制弹出框
            isPublishDictList: [], // 是否发布状态字典
            payTypeDictList: [], // 收费模式字典
            chargeTypeDictList: [], // 收费模式
            userTagDictList: [], // 用户标签
            payTypeDefault: null, // 收费模式默认值
            isPublishDefault: null, // 商品状态默认值
            chargeTypeDefaultValue: null,
            formLabelWidth: "130px",
            isEditForm: false,
            orderByDescColumn: "", // 降序字段
            orderByAscColumn: "", // 升序字段
            form: {},
            discountTimeList: null,
            imagecropperShow: false,
            imagecropperKey: 0,
            url: process.env.PICTURE_API + "/file/cropperPicture",
            pickerOptions: {
                shortcuts: [
                    {
                        text: '一周',
                        onClick(picker) {
                            const start = new Date();
                            const end = new Date();
                            end.setTime(start.getTime() + 3600 * 1000 * 24 * 7);
                            picker.$emit('pick', [start, end]);
                        }
                    },
                    {
                        text: '一个月',
                        onClick(picker) {
                            const start = new Date();
                            const end = new Date();
                            end.setTime(start.getTime() + 3600 * 1000 * 24 * 30);
                            picker.$emit('pick', [start, end]);
                        }
                    },
                    {
                        text: '三个月',
                        onClick(picker) {
                            const start = new Date();
                            const end = new Date();
                            end.setTime(start.getTime() + 3600 * 1000 * 24 * 90);
                            picker.$emit('pick', [start, end]);
                        }
                    },
                    {
                        text: '一年',
                        onClick(picker) {
                            const start = new Date();
                            const end = new Date();
                            end.setTime(start.getTime() + 3600 * 1000 * 24 * 365);
                            picker.$emit('pick', [start, end]);
                        }
                    }

                ]
            },
            rules: {
                name: [
                    {required: true, message: '商品名称不能为空', trigger: 'blur'},
                    {min: 1, max: 20, message: '长度在1到20个字符'},
                ],
                extra: [
                    {required: true, message: '扩展配置不能为空', trigger: 'blur'}
                ],
                isPublish: [
                    {required: true, message: '是否发布不能为空', trigger: 'blur'},
                ],
                price: [
                    {required: true, message: '单价不能为空', trigger: 'blur'},
                    {pattern: /^[0-9]\d*$/, message: '单价只能为自然数'},
                ],
                effectDay: [
                    {required: true, message: '商品生效天数字段不能为空', trigger: 'blur'},
                ],
                payType: [
                    {required: true, message: '支付类型字段不能为空', trigger: 'blur'},
                ],
                chargeType: [
                    {required: true, message: '收费模式字段不能为空', trigger: 'blur'},
                ],
                freight: [
                    {required: true, message: '邮寄运费字段不能为空', trigger: 'blur'},
                    {
                        pattern: /^-?1$|^[0-9]\d*$/,
                        message: '库存数量字段只能为-1, 0或自然数【-1 表示无需邮寄，0 表示免邮】'
                    },
                ],
                stockCount: [
                    {required: true, message: '库存数量字段不能为空', trigger: 'blur'},
                    {pattern: /^-?1$|^[0-9]\d*$/, message: '库存数量字段只能为-1, 0或自然数【-1 无库存上限】'},
                ],
                sort: [
                    {required: true, message: '排序字段不能为空', trigger: 'blur'},
                    {pattern: /^[0-9]\d*$/, message: '排序字段只能为自然数'},
                ]
            },
        };
    },
    components: {
        AvatarCropper,
        CKEditor,
        Mavon,
    },
    created() {
        // 字典查询
        this.getDictList()
        this.productList();
        this.getProductCategoryList()
        this.getSystemConfigMethod()
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
            this.productList()
        },
        // 获取系统配置
        getSystemConfigMethod: function () {
            getSystemConfig().then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    if (response.data) {
                        this.systemConfig = response.data;
                    }
                }
            });
        },
        cropSuccess(resData) {
            this.imagecropperShow = false
            this.imagecropperKey = this.imagecropperKey + 1
            let photoType = this.photoType
            switch (photoType) {
                case 1: {
                    this.form.coverPhotoUrl = resData.url;
                    this.form.coverFileUid = resData.uid
                } break;
                case 2: {
                    console.log("上传轮播图回调", this.form.slidesPhotoUrlList)
                    if (this.form.slidesPhotoUrlList) {
                        this.form.slidesPhotoUrlList.push(resData.url);
                        this.form.slidesFileUidList +=  resData.uid + ","
                        console.log("插入数据", this.form.slidesPhotoUrlList)
                    } else {
                        this.form.slidesPhotoUrlList = [resData.url];
                        this.form.slidesFileUidList = resData.uid + ","
                    }

                } break;
            }
        },
        close() {
            this.imagecropperShow = false
        },
        deletePhoto: function (type) {
            console.log("开始删除图片", type)
            if (type === 1) {
                this.form.coverPhotoUrl = "";
                this.form.coverFileUid = "";
            } else {
                this.form.slidesFileUidList = ""
                this.form.slidesPhotoUrlList = []
            }
        },
        //弹出选择图片框
        checkPhoto(type) {
            this.photoType = type
            this.imagecropperShow = true
        },
        productList: function () {
            let params = {};
            params.keyword = this.keyword;
            params.secretType = this.secretType
            params.currentPage = this.currentPage;
            params.pageSize = this.pageSize;
            params.orderByDescColumn = this.orderByDescColumn
            params.orderByAscColumn = this.orderByAscColumn
            getProductList(params).then(response => {
                let tableData = response.data.records;
                for (let i = 0; i < tableData.length; i++) {
                    if (tableData[i].isSelection === 1) {
                        tableData[i].selectionStatus = true
                    } else {
                        tableData[i].selectionStatus = false
                    }
                    if (tableData[i].isTop === 1) {
                        tableData[i].topStatus = true
                    } else {
                        tableData[i].topStatus = false
                    }
                    if (tableData[i].isPublish == "1") {
                        tableData[i].publishStatus = true
                    } else {
                        tableData[i].publishStatus = false
                    }
                }
                this.tableData = tableData;
                this.currentPage = response.data.current;
                this.pageSize = response.data.size;
                this.total = response.data.total;
            });
        },
        getProductCategoryList: function () {
            let params = {};
            // 业务类型1代表商品分类
            params.bizType = "1"
            params.currentPage = 1;
            params.pageSize = 1000;
            params.orderByDescColumn = "sort"
            getCommonCategoryList(params).then(response => {
                if (response.code === this.$ECode.SUCCESS) {
                    this.categoryList = response.data.records;
                }
            });
        },
        getFormObject: function () {
            let formObject = {
                uid: null,
                title: null,
                summary: null,
                discountPrice: 0,
                price: 0,
                sort: 0,
                stockCount: -1,
                freight: -1,
                isTop: 0,
                isSelection: 0,
                isPublish: this.isPublishDefault
            };
            return formObject;
        },
        /**
         * 字典查询
         */
        getDictList: function () {
            let dictTypeList = ['sys_publish_status', 'sys_charge_type', 'sys_pay_type', 'sys_user_tag']
            getListByDictTypeList(dictTypeList).then(response => {
                if (response.code === this.$ECode.SUCCESS) {
                    let dictMap = response.data;
                    this.isPublishDictList = dictMap.sys_publish_status.list
                    this.chargeTypeDictList = dictMap.sys_charge_type.list
                    this.payTypeDictList = dictMap.sys_pay_type.list
                    this.userTagDictList = dictMap.sys_user_tag.list

                    if (dictMap.sys_publish_status.defaultValue) {
                        this.isPublishDefault = parseInt(dictMap.sys_publish_status.defaultValue);
                    }
                    if (dictMap.sys_charge_type.defaultValue) {
                        this.chargeTypeDefaultValue = dictMap.sys_charge_type.defaultValue
                    }
                    if (dictMap.sys_pay_type.defaultValue) {
                        this.payTypeDefault = parseInt(dictMap.sys_pay_type.defaultValue);
                    }
                }
            });
        },

        handleFind: function () {
            this.currentPage = 1
            this.productList();
        },
        handleAdd: function () {
            this.title = "增加商品"
            this.dialogFormVisible = true;
            this.form = this.getFormObject();
            this.isEditForm = false;
        },
        handleEdit: function (row) {
            let that = this
            this.title = "编辑商品";
            this.dialogFormVisible = true;
            this.isEditForm = true;
            if (row.discountStartTime && row.discountEndTime) {
                this.discountTimeList = [row.discountStartTime, row.discountEndTime]
            }
            this.$nextTick(() => {
                //DOM现在更新了
                that.$refs.editor.setData(that.form.content); //设置富文本内容
            });

            this.form = row;
        },
        handleDelete: function (row) {
            let that = this;
            this.$confirm("此操作将把密钥商品删除, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    let params = {};
                    params.uid = row.uid;
                    deleteBatchProduct(params).then(response => {
                        if (response.code === this.$ECode.SUCCESS) {
                            this.$commonUtil.message.success(response.message)
                            that.productList();
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
            this.productList();
        },
        handChange(row) {
            if (row.selectionStatus) {
                row.isSelection = 1
            } else {
                row.isSelection = 0
            }
            if (row.topStatus) {
                row.isTop = 1
            } else {
                row.isTop = 0
            }
            this.form = row;
            this.submitForm(1)
        },
        submitForm: function (submitType) {
            if (submitType === 1) {
                editProduct(this.form).then(response => {
                    if (response.code == this.$ECode.SUCCESS) {
                        this.$commonUtil.message.success(response.message)
                        this.dialogFormVisible = false;
                        this.productList();
                    } else {
                        this.$commonUtil.message.error(response.message)
                    }
                });
            } else {
                this.form.content = this.$refs.editor.getData(); //获取内容
                let discountTimeList = this.discountTimeList
                if (discountTimeList && discountTimeList.length > 0) {
                    this.form.discountStartTime = discountTimeList[0]
                    this.form.discountEndTime = discountTimeList[1]
                }
                this.$refs.form.validate((valid) => {
                    if (!valid) {
                        console.log("校验失败")
                    } else {
                        if (this.isEditForm) {
                            editProduct(this.form).then(response => {
                                if (response.code == this.$ECode.SUCCESS) {
                                    this.$commonUtil.message.success(response.message)
                                    this.dialogFormVisible = false;
                                    this.productList();
                                } else {
                                    this.$commonUtil.message.error(response.message)
                                }
                            });
                        } else {
                            addProduct(this.form).then(response => {
                                if (response.code == this.$ECode.SUCCESS) {
                                    this.$commonUtil.message.success(response.message)
                                    this.dialogFormVisible = false;
                                    this.productList();
                                } else {
                                    this.$commonUtil.message.error(response.message)
                                }
                            });
                        }
                    }
                })
            }
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
    line-height: 100px;
    text-align: center;
}

.imgBody {
    width: 195px;
    height: 105px;
    border: solid 2px #ffffff;
    float: left;
    margin-left: 5px;
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

.img {
    width: 100%;
    height: 100%;
}

img {
    width: 100px;
    height: 100px;
}
</style>
