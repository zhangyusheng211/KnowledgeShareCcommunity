<template>
    <div class="app-container">

        <div class="filter-container" style="margin: 10px 0 10px 0;" v-permission="'/orderAmountLog/getList'">
            <el-input
                clearable
                @keyup.enter.native="handleFind"
                class="filter-item"
                style="width: 300px;"
                v-model="queryParams.keyword"
                placeholder="输入关键字查询"
                size="small"
            ></el-input>

            <el-button class="filter-item" type="primary" size="small" icon="el-icon-search" @click="handleFind" v-permission="'/orderAmountLog/getList'">查找</el-button>
        </div>

        <!-- 查询和其他操作 -->
        <el-table :data="tableData" style="width: 100%">

            <el-table-column type="selection"></el-table-column>

            <el-table-column label="序号" width="60" align="center">
                <template slot-scope="scope">
                    <span>{{scope.$index + 1}}</span>
                </template>
            </el-table-column>


            <el-table-column label="用户" width="200" align="center">
                <template slot-scope="scope">
                    <UserAvatar :user="scope.row.user"></UserAvatar>
                </template>
            </el-table-column>

            <el-table-column label="动作类型" width="120" align="center">
                <template slot-scope="scope">
                    <el-tag type="primary" >{{scope.row.actionName}}</el-tag>

                </template>
            </el-table-column>

            <el-table-column label="变更积分" width="120" align="center">
                <template slot-scope="scope">
                    <span v-if="scope.row.changeCredits >= 0">+{{ scope.row.changeCredits}}</span>
                    <span v-else>{{ scope.row.changeCredits}}</span>
                </template>
            </el-table-column>

            <el-table-column label="原积分" width="120" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.oldCredits  }}</span>
                </template>
            </el-table-column>

            <el-table-column label="当前积分" width="120" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.newCredits}}</span>
                </template>
            </el-table-column>

            <el-table-column label="创建时间" width="160" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.createTime }}</span>
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


    </div>
</template>

<script>
import {getCreditsLogList} from "../../api/credits";
import {getListByDictTypeList} from "@/api/sysDictData"
import UserAvatar from "../../components/UserAvatar"
export default {
    data() {
        return {
            queryParams: {
                keyword: null,
                businessType: null,
            },
            orderPayTypeDictList: [
                {
                    uid: "1",
                    dictLabel: "订单分账",
                    dictValue: 66,
                },
                {
                    uid: "2",
                    dictLabel: "用户提现",
                    dictValue: 67,
                },
            ],
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
        this.creditsLogList();
    },
    methods: {
        handleFind: function () {
            this.currentPage = 1
            this.pageSize = 10
            this.creditsLogList()
        },
        /**
         * 字典查询
         */
        getDictList: function () {
            let dictTypeList = ['sys_order_status', 'sys_pay_method', 'sys_audit_status', 'sys_withdraw_status']
            getListByDictTypeList(dictTypeList).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    let dictMap = response.data;
                    this.orderStatusDictList = dictMap.sys_order_status.list
                    this.orderPayMethodDictList = dictMap.sys_pay_method.list
                    this.auditStatusDictList = dictMap.sys_audit_status.list;
                    this.withdrawStatusDictList = dictMap.sys_withdraw_status.list;
                }
            });
        },
        creditsLogList: function () {
            let params = {};
            params.currentPage = this.currentPage;
            params.pageSize = this.pageSize;
            params.keyword = this.queryParams.keyword
            getCreditsLogList(params).then(response => {
                console.log("获取积分流水记录", response)
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
            this.creditsLogList();
        },
    }

};
</script>
