<template>
    <div class="app-container">
        <!-- 查询和其他操作 -->
        <div class="filter-container" style="margin: 10px 0 10px 0;" v-permission="'/vipConfig/getList'">
            <el-input
                clearable
                @keyup.enter.native="handleFind"
                class="filter-item"
                style="width: 200px;"
                v-model="keyword"
                placeholder="请输入会员名"
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
                       v-permission="'/secretConfig/add'">添加会员
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

            <el-table-column label="会员名称" width="200" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.name }}</span>
                </template>
            </el-table-column>

            <el-table-column label="会员简介" width="200" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.summary }}</span>
                </template>
            </el-table-column>

            <el-table-column label="会员价格" width="200" align="center">
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

            <el-table-column label="生效天数" width="100" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.effectDay }}</span>
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

            <el-table-column label="授予用户标签" width="150" align="center" prop="linkStatus" sortable="custom"
                             :sort-by="['userTag']">
                <template slot-scope="scope">
                    <template>
                        <el-tag v-for="item in userTagDictList" :key="item.uid" :type="item.listClass"
                                v-if="scope.row.userTag == item.dictValue">{{ item.dictLabel }}
                        </el-tag>
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
                    <el-button @click="handleEdit(scope.row)" type="primary" size="mini" v-permission="'/vipConfig/edit'">
                        编辑
                    </el-button>
                    <el-button @click="handleDelete(scope.row)" type="danger" size="mini" v-permission="'/vipConfig/deleteBatch'">
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
                <el-form-item label="会员名称" :label-width="formLabelWidth" prop="name">
                    <el-input v-model="form.name" auto-complete="off"></el-input>
                </el-form-item>

                <el-form-item label="会员简介" :label-width="formLabelWidth" prop="summary">
                    <el-input v-model="form.summary" auto-complete="off"></el-input>
                </el-form-item>

                <el-form-item label="会员生效天数" :label-width="formLabelWidth" prop="effectDay">
                    <el-input v-model="form.effectDay" auto-complete="off"></el-input>
                </el-form-item>

                <el-form-item label="授予用户标签" :label-width="formLabelWidth" prop="payType">
                    <template slot="label">
                        用户标签
                        <el-popover
                            placement="top-start"
                            width="250"
                            trigger="hover">
                            <div>
                                <div>用户购买会员后，会自动授予该用户标签</div>
                            </div>
                            <i slot="reference" style="cursor: pointer;margin-left: 2px" class="el-icon-question"/>
                        </el-popover>
                    </template>
                    <el-select
                        v-model="form.userTag"
                        size="small"
                        placeholder="请选择"
                        style="width: 200px"
                    >
                        <el-option
                            v-for="item in userTagDictList"
                            :key="item.uid"
                            :label="item.dictLabel"
                            :value="parseInt(item.dictValue)"
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
                    <el-col :span="12">
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
                    <el-col :span="12">
                        <el-form-item label="折扣时间" :label-width="formLabelWidth" prop="discountTimeList"
                                      v-if="form.chargeType == 3">
                            <el-date-picker
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


                <el-row>
                    <el-col :span="12">
                        <el-form-item label="价格(分)"  :label-width="formLabelWidth" prop="price">
                            <template slot="label">
                                价格(分)
                                <el-popover
                                    placement="top-start"
                                    width="250"
                                    trigger="hover">
                                    <div>
                                        <div>积分支付时，设置的是多少积分</div>
                                        <div>现金支付时: 设置的是现金（单位分）</div>
                                    </div>
                                    <i slot="reference" style="cursor: pointer;margin-left: 2px" class="el-icon-question"/>
                                </el-popover>
                            </template>
                            <el-input v-model="form.price" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>

                    <el-col :span="12" v-if="form.chargeType === 3">
                        <el-form-item
                            :label-width="formLabelWidth"
                            label="折扣价(分)"
                            prop="discountPrice"
                        >
                            <el-input
                                v-model="form.discountPrice"
                                auto-complete="off"
                            />
                        </el-form-item>
                    </el-col>
                </el-row>


                <el-form-item label="扩展配置" :label-width="formLabelWidth" prop="extra">
                    <template slot="label">
                        扩展配置
                        <el-popover
                            placement="top-start"
                            width="250"
                            trigger="hover">
                            <div>
                                <div>用于控制会员页面模块的配色</div>
                            </div>
                            <i slot="reference" style="cursor: pointer;margin-left: 2px" class="el-icon-question"/>
                        </el-popover>
                    </template>
                    <el-input v-model="form.extra" auto-complete="off"  type="textarea" :rows="3"></el-input>
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
    </div>
</template>

<script>
import {addVipConfig, deleteBatchVipConfig, editVipConfig, getVipConfigList} from "@/api/vipConfig";
import {getListByDictTypeList} from "@/api/sysDictData"

export default {
    props: ['secretType'],
    data() {
        return {
            tableData: [],
            keyword: "",
            linkStatusKeyword: null, //会员状态查询
            currentPage: 1,
            pageSize: 10,
            total: 0, //总数量
            title: "添加密钥",
            subSecretTypeDictList: [], // 密钥子类型字典
            dialogFormVisible: false, //控制弹出框
            isPublishDictList: [], // 是否发布状态字典
            payTypeDictList: [], // 收费模式字典
            chargeTypeDictList: [], // 收费模式
            userTagDictList:[], // 用户标签
            payTypeDefault: null, // 收费模式默认值
            isPublishDefault: null, // 会员状态默认值
            chargeTypeDefaultValue: null,
            formLabelWidth: "130px",
            isEditForm: false,
            orderByDescColumn: "", // 降序字段
            orderByAscColumn: "", // 升序字段
            form: {},
            discountTimeList: null,
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
                    {required: true, message: '会员名称不能为空', trigger: 'blur'},
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
                    {required: true, message: '会员生效天数字段不能为空', trigger: 'blur'},
                ],
                payType: [
                    {required: true, message: '支付类型字段不能为空', trigger: 'blur'},
                ],
                chargeType: [
                    {required: true, message: '收费模式字段不能为空', trigger: 'blur'},
                ],
                sort: [
                    {required: true, message: '排序字段不能为空', trigger: 'blur'},
                    {pattern: /^[0-9]\d*$/, message: '排序字段只能为自然数'},
                ]
            },
        };
    },
    components: {

    },
    created() {
        // 字典查询
        this.getDictList()
        this.vipConfigList();
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
            this.vipConfigList()
        },

        vipConfigList: function () {
            let params = {};
            params.keyword = this.keyword;
            params.secretType = this.secretType
            params.currentPage = this.currentPage;
            params.pageSize = this.pageSize;
            params.orderByDescColumn = this.orderByDescColumn
            params.orderByAscColumn = this.orderByAscColumn
            getVipConfigList(params).then(response => {
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
                discountPrice: 0,
                price: 0,
                sort: 0,
                isPublish: this.isPublishDefault
            };
            return formObject;
        },
        /**
         * 字典查询
         */
        getDictList: function () {
            let dictTypeList = ['sys_publish_status', 'sys_charge_type','sys_pay_type', 'sys_user_tag']
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
            this.vipConfigList();
        },
        handleAdd: function () {
            this.title = "增加会员"
            this.dialogFormVisible = true;
            this.form = this.getFormObject();
            this.isEditForm = false;
        },
        handleEdit: function (row) {
            this.title=  "编辑会员";
            this.dialogFormVisible = true;
            this.isEditForm = true;
            if (row.discountStartTime && row.discountEndTime) {
                this.discountTimeList = [row.discountStartTime, row.discountEndTime]
            }
            this.form = row;
        },
        handleDelete: function (row) {
            let that = this;
            this.$confirm("此操作将把密钥会员删除, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    let params = {};
                    params.uid = row.uid;
                    deleteBatchVipConfig(params).then(response => {
                        if (response.code === this.$ECode.SUCCESS) {
                            this.$commonUtil.message.success(response.message)
                            that.vipConfigList();
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
            this.vipConfigList();
        },
        submitForm: function () {
            this.form.secretType = this.secretType
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
                        editVipConfig(this.form).then(response => {
                            if (response.code == this.$ECode.SUCCESS) {
                                this.$commonUtil.message.success(response.message)
                                this.dialogFormVisible = false;
                                this.vipConfigList();
                            } else {
                                this.$commonUtil.message.error(response.message)
                            }
                        });
                    } else {
                        addVipConfig(this.form).then(response => {
                            if (response.code == this.$ECode.SUCCESS) {
                                this.$commonUtil.message.success(response.message)
                                this.dialogFormVisible = false;
                                this.vipConfigList();
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
