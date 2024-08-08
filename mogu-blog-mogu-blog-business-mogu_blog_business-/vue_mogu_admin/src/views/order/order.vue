<template>
    <div class="app-container">

        <!-- 查询和其他操作 -->
        <div class="filter-container" style="margin: 10px 0 10px 0;" v-permission="'/order/getList'">
            <el-input
                clearable
                @keyup.enter.native="handleFind"
                class="filter-item"
                style="width: 280px;"
                v-model="queryParams.keyword"
                placeholder="支持订单ID、用户ID、商品ID查询"
                size="small"
            ></el-input>


            <el-select
                v-model="queryParams.payType"
                clearable
                placeholder="支付类型"
                style="width: 150px"
                size="small"
            >
                <el-option
                    v-for="item in orderPayTypeDictList"
                    :key="item.uid"
                    :label="item.dictLabel"
                    :value="item.dictValue"
                />
            </el-select>

            <el-select
                v-model="queryParams.payMethod"
                clearable
                placeholder="支付方式"
                style="width: 150px"
                size="small"
            >
                <el-option
                    v-for="item in cashPayMethodDictList"
                    :key="item.uid"
                    :label="item.dictLabel"
                    :value="item.dictValue"
                />
            </el-select>

            <el-select
                v-model="queryParams.orderStatus"
                clearable
                placeholder="订单状态"
                style="width: 150px"
                size="small"
            >
                <el-option
                    v-for="item in orderStatusDictList"
                    :key="item.uid"
                    :label="item.dictLabel"
                    :value="item.dictValue"
                />
            </el-select>

            <el-button class="filter-item" type="primary" size="small" icon="el-icon-search" @click="handleFind" v-permission="'/order/getList'">查找</el-button>
        </div>

        <el-table :data="tableData" style="width: 100%">

            <el-table-column type="selection"></el-table-column>

            <el-table-column label="序号" width="60" align="center">
                <template slot-scope="scope">
                    <span>{{scope.$index + 1}}</span>
                </template>
            </el-table-column>

            <el-table-column label="订单号" width="280" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.uid }}</span>
                </template>
            </el-table-column>

            <el-table-column label="用户" width="200" align="center">
                <template slot-scope="scope">
                    <UserAvatar :user="scope.row.user"></UserAvatar>
                </template>
            </el-table-column>

            <el-table-column label="商品名称" width="200" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.title }}</span>
                </template>
            </el-table-column>

            <el-table-column label="资源ID" width="280" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.resourceUid }}</span>
                </template>
            </el-table-column>


            <el-table-column label="支付类型" width="120" align="center">
                <template slot-scope="scope">
                    <el-tag v-for="item in orderPayTypeDictList" :key="item.uid" :type="item.listClass"
                            v-if="scope.row.payType === parseInt(item.dictValue)">{{ item.dictLabel }}
                    </el-tag>
                </template>
            </el-table-column>

            <el-table-column label="订单金额" width="120" align="center">
                <template slot-scope="scope">
                    <span v-if="scope.row.payType == 1">{{ scope.row.price}}积分</span>
                    <span v-if="scope.row.payType == 2">{{ scope.row.price / 100 }}元
                    </span>
                </template>
            </el-table-column>

            <el-table-column label="支付方式" width="150" align="center">
                <template slot-scope="scope">
                    <el-tag v-for="item in cashPayMethodDictList" :key="item.uid" :type="item.listClass"
                            v-if="scope.row.payMethod === parseInt(item.dictValue)">{{ item.dictLabel }}
                    </el-tag>
                </template>
            </el-table-column>


            <el-table-column label="订单状态" width="120" align="center">
                <template slot-scope="scope">
                    <el-tag v-for="item in orderStatusDictList" :key="item.uid" :type="item.listClass"
                            v-if="scope.row.orderStatus === parseInt(item.dictValue)">{{ item.dictLabel }}
                    </el-tag>
                </template>
            </el-table-column>



            <el-table-column label="创建时间" width="160" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.createTime }}</span>
                </template>
            </el-table-column>


            <!--      <el-table-column label="操作" fixed="right" min-width="150">-->
            <!--        <template slot-scope="scope">-->
            <!--          <el-button @click="handleShow(scope.row)" type="primary" size="small">详情</el-button>-->
            <!--        </template>-->
            <!--      </el-table-column>-->

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
import {getOrderList} from "../../api/order";
import {getListByDictTypeList} from "@/api/sysDictData"
import UserAvatar from "../../components/UserAvatar"

export default {
    data() {
        return {
            queryParams: {
                keyword: null,
                payType: null,
                payMethod: null,
                orderStatus: null,
            },
            tableData: [],
            currentPage: 1,
            pageSize: 10,
            total: 0, //总数量
            formLabelWidth: "120px",
            dialogVisible: false,
            orderStatusDictList: [],
            orderPayTypeDictList: [],
            cashPayMethodDictList: [],
            resourceTypeDictList: [], // 资源类型
        };
    },
    components: {
        UserAvatar,
    },
    created() {
        this.getDictList();
        this.orderList();
    },
    methods: {

        /**
         * 字典查询
         */
        getDictList: function () {
            let dictTypeList = ['sys_order_status', 'sys_pay_type', 'cash_pay_method', 'sys_resource_type']
            getListByDictTypeList(dictTypeList).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    console.log("获取字典", response.data)
                    let dictMap = response.data;
                    this.orderStatusDictList = dictMap.sys_order_status.list
                    this.orderPayTypeDictList = dictMap.sys_pay_type.list
                    this.cashPayMethodDictList = dictMap.cash_pay_method.list
                    this.resourceTypeDictList = dictMap.sys_resource_type.list
                }
            });
        },
        handleFind: function () {
            this.currentPage = 1
            this.pageSize = 10
            this.orderList()
        },
        orderList: function () {
            let params = {};
            params.currentPage = this.currentPage;
            params.pageSize = this.pageSize;
            params.keyword = this.queryParams.keyword
            params.payMethod = this.queryParams.payMethod
            params.payType = this.queryParams.payType
            params.orderStatus = this.queryParams.orderStatus
            getOrderList(params).then(response => {
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
