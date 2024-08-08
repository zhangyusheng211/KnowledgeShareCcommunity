<template>
    <div class="app-container">

        <!-- 查询和其他操作 -->
        <div class="filter-container" style="margin: 10px 0 10px 0;" v-permission="'/withdraw/getList'">
            <el-input
                clearable
                @keyup.enter.native="handleFind"
                class="filter-item"
                style="width: 280px;"
                v-model="queryParams.keyword"
                placeholder="支持提现单ID、用户ID、微信账号"
                size="small"
            ></el-input>

            <el-select
                v-model="queryParams.withdrawStatus"
                clearable
                placeholder="提现状态"
                style="width: 150px"
                size="small"
            >
                <el-option
                    v-for="item in withdrawStatusDictList"
                    :key="item.uid"
                    :label="item.dictLabel"
                    :value="item.dictValue"
                />
            </el-select>

            <el-select
                v-model="queryParams.auditStatus"
                clearable
                placeholder="审核状态"
                style="width: 150px"
                size="small"
            >
                <el-option
                    v-for="item in auditStatusDictList"
                    :key="item.uid"
                    :label="item.dictLabel"
                    :value="item.dictValue"
                />
            </el-select>


            <el-button class="filter-item" type="primary" size="small" icon="el-icon-search" @click="handleFind" v-permission="'/withdraw/getList'">查找</el-button>
        </div>

        <el-table :data="tableData" style="width: 100%">

            <el-table-column type="selection"></el-table-column>

            <el-table-column label="序号" width="60" align="center">
                <template slot-scope="scope">
                    <span>{{scope.$index + 1}}</span>
                </template>
            </el-table-column>

            <el-table-column label="提现单ID" width="280" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.uid }}</span>
                </template>
            </el-table-column>

            <el-table-column label="提现人" width="200" align="center">
                <template slot-scope="scope">
                    <UserAvatar :user="scope.row.user"></UserAvatar>
                </template>
            </el-table-column>

            <el-table-column label="微信账号" width="120" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.account }}</span>
                </template>
            </el-table-column>

            <el-table-column label="提现金额（元）" width="120" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.amount / 100 }}</span>
                </template>
            </el-table-column>

            <el-table-column label="收款码" width="150" align="center">
                <template slot-scope="scope">
                    <el-image style="width: 100px" :src="scope.row.photoUrl" :preview-src-list="[scope.row.photoUrl]"></el-image>
                </template>
            </el-table-column>


            <el-table-column label="提现状态" width="100" align="center">
                <template slot-scope="scope">
                    <el-tag
                        v-for="item in withdrawStatusDictList"
                        :key="item.uid"
                        v-if="scope.row.withdrawStatus == parseInt(item.dictValue)"
                        :type="item.listClass"
                    >
                        <span>
                          {{ item.dictLabel }}
                        </span>
                    </el-tag>
                </template>
            </el-table-column>

            <el-table-column label="审核状态" width="100" align="center">
                <template slot-scope="scope">
                    <el-tag
                        v-for="item in auditStatusDictList"
                        :key="item.uid"
                        v-if="parseInt(scope.row.auditStatus) == parseInt(item.dictValue)"
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

            <el-table-column label="创建时间" width="160" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.createTime }}</span>
                </template>
            </el-table-column>


              <el-table-column label="操作" fixed="right" min-width="150">
                <template slot-scope="scope">
                  <el-button @click="handleAudit(scope.row)" type="primary" size="small" v-if="scope.row.withdrawStatus != 3">审批</el-button>
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
                :total="total">
            </el-pagination>
        </div>


        <el-dialog
            title="请选择审批结果"
            :visible.sync="auditDialogVisible"
            width="30%"
            center
        >
            <div style="text-align: center">
                <el-form>
                    <el-form-item
                        label="审批状态"
                        :label-width="lineLabelWidth"
                        prop="auditStatus"
                    >
                        <el-radio-group v-model="auditForm.auditStatus" size="small">
                            <el-radio
                                v-for="item in auditStatusDictList"
                                v-if="item.dictValue != 0"
                                :key="item.uid"
                                :label="item.dictValue"
                                border
                            >{{ item.dictLabel }}</el-radio
                            >
                        </el-radio-group>
                    </el-form-item>

                    <el-form-item
                        label="拒绝原因"
                        :label-width="lineLabelWidth"
                        v-if="auditForm.auditStatus == 1"
                        prop="rejectReason"
                    >
                        <el-input
                            v-model="auditForm.rejectReason"
                            maxlength="50"
                            placeholder="拒绝的原因"
                            clearable
                        ></el-input>
                    </el-form-item>

<!--                    <el-form-item-->
<!--                        label="支付方式"-->
<!--                        :label-width="lineLabelWidth"-->
<!--                        prop="auditStatus"-->
<!--                    >-->
<!--                        <el-radio-group v-model="auditForm.payMethod" size="small">-->
<!--                            <el-radio-->
<!--                                label="1"-->
<!--                                border-->
<!--                            >微信打款</el-radio>-->
<!--                            <el-radio-->
<!--                                key="2"-->
<!--                                label="2"-->
<!--                                border-->
<!--                            >支付宝打款</el-radio>-->
<!--                        </el-radio-group>-->
<!--                    </el-form-item>-->

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
import {auditWithdraw, getWithdrawList} from "../../api/order";
import {getListByDictTypeList} from "@/api/sysDictData"
import UserAvatar from "../../components/UserAvatar"
export default {
    data() {
        return {
            queryParams: {
                keyword: null,
                withdrawStatus: null,
                auditStatus: null,
            },
            tableData: [],
            currentPage: 1,
            pageSize: 10,
            total: 0, //总数量
            formLabelWidth: "120px",
            dialogVisible: false,
            orderStatusDictList: [],
            orderPayMethodDictList: [],
            auditStatusDictList: [], // 审核枚举
            withdrawStatusDictList: [], // 提现状态
            auditStatusDefault: "2",
            lineLabelWidth: "120px", //一行的间隔数
            auditDialogVisible: false,
            auditForm: {},
        };
    },
    components: {
        UserAvatar,
    },
    created() {
        this.getDictList();
        this.withdrawList();
    },
    methods: {
        handleFind: function () {
            this.currentPage = 1
            this.pageSize = 10
            this.withdrawList()
        },
        /**
         * 字典查询
         */
        getDictList: function () {
            let dictTypeList = ['sys_order_status', 'sys_audit_status', 'sys_withdraw_status']
            getListByDictTypeList(dictTypeList).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    let dictMap = response.data;
                    this.orderStatusDictList = dictMap.sys_order_status.list
                    this.auditStatusDictList = dictMap.sys_audit_status.list
                    this.withdrawStatusDictList = dictMap.sys_withdraw_status.list
                }
            });
        },
        getAuditFormObject: function () {
            let auditForm = {
                uid: "",
                auditStatus: this.auditStatusDefault, // 审批状态默认值
                rejectReason: null, // 审批拒绝原因
            };
            return auditForm;
        },
        handleAudit: function (row) {
            this.auditForm = this.getAuditFormObject();
            this.auditForm.uid = row.uid;
            this.auditDialogVisible = true;
        },
        submitAudit: function () {
            let auditForm = this.auditForm
            auditWithdraw(auditForm).then(response => {
                if (response.code === this.$ECode.SUCCESS) {
                    this.$message.success(response.message)
                } else {
                    this.$message.error(response.message)
                }
                this.auditForm = this.getAuditFormObject();
                this.auditDialogVisible = false;
                this.withdrawList()
                console.log("审核结果", response)
            })
        },
        withdrawList: function () {
            let params = {};
            params.currentPage = this.currentPage;
            params.pageSize = this.pageSize;
            params.auditStatus = this.queryParams.auditStatus
            params.withdrawStatus = this.queryParams.withdrawStatus
            params.keyword = this.queryParams.keyword
            getWithdrawList(params).then(response => {
                console.log("获取订单列表", response)
                if (response.code == this.$ECode.SUCCESS) {
                    this.tableData = response.data.records;
                    this.currentPage = response.data.current;
                    this.pageSize = response.data.size;
                    this.total = response.data.total;
                }
            });
        },
        handleCurrentChange: function (val) {
            this.currentPage = val;
            this.orderList();
        },
    }

};
</script>
