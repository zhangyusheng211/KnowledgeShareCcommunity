<template>
    <div class="app-container">
        <!-- 查询和其他操作 -->
        <div class="filter-container" style="margin: 10px 0 10px 0;" v-permission="'/luckyRule/getList'">
            <el-input
                clearable
                @keyup.enter.native="handleFind"
                class="filter-item"
                style="width: 200px;"
                v-model="queryParams.luckyActivityUid"
                placeholder="请输入活动UID"
                size="small"
            ></el-input>

            <el-select
                v-model="queryParams.awardProductUid"
                clearable
                placeholder="请选择奖品信息"
                style="width: 200px"
                size="small"
                @keyup.enter.native="handleFind"
            >
                <el-option
                    v-for="item in awardProductList"
                    :key="item.uid"
                    :label="item.name"
                    :value="item.uid"
                />
            </el-select>

            <el-input
                clearable
                @keyup.enter.native="handleFind"
                class="filter-item"
                style="width: 200px;"
                v-model="queryParams.userUid"
                placeholder="请输入用户UID"
                size="small"
            ></el-input>
            <el-button class="filter-item" type="primary" size="small" icon="el-icon-search" @click="handleFind"
                       v-permission="'/luckyRule/getList'">查找
            </el-button>
        </div>

        <el-table :data="tableData"
                  style="width: 100%"
                  @selection-change="handleSelectionChange"
                  @sort-change="changeSort"
                  :default-sort="{prop: 'sort', order: 'descending'}">
            <el-table-column type="selection"></el-table-column>

            <el-table-column label="序号" width="60" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.$index + 1 }}</span>
                </template>
            </el-table-column>

            <el-table-column label="活动名称" width="200" align="center">
                <template slot-scope="scope">
                    <span v-if="scope.row.luckyActivity">{{ scope.row.luckyActivity.title }}</span>
                </template>
            </el-table-column>

            <el-table-column label="作者" width="200" align="center">
                <template slot-scope="scope">
                    <UserAvatar :user="scope.row.user"></UserAvatar>
                </template>
            </el-table-column>

            <el-table-column label="奖品名称" width="200" align="center">
                <template slot-scope="scope">
                    <span v-if="scope.row.awardProduct && scope.row.luckyAwardItem">{{
                            getLuckyItemName(scope.row.luckyAwardItem, scope.row.awardProduct)
                        }}</span>
                </template>
            </el-table-column>

            <el-table-column label="中奖状态" width="200" align="center" prop="maxLuckyCount" sortable="custom"
                             :sort-by="['maxLuckyCount']">
                <template slot-scope="scope">
                    <el-tag type="success" v-if="scope.row.luckyStatus == 1">已中奖</el-tag>
                    <el-tag type="danger" v-if="scope.row.luckyStatus== 0">未中奖</el-tag>
                </template>
            </el-table-column>

            <el-table-column label="创建时间" width="160" align="center" prop="createTime" sortable="custom"
                             :sort-by="['createTime']">
                <template slot-scope="scope">
                    <span>{{ scope.row.createTime }}</span>
                </template>
            </el-table-column>

            <el-table-column label="更新时间" width="160" align="center" prop="updateTime" sortable="custom"
                             :sort-by="['updateTime']">
                <template slot-scope="scope">
                    <span>{{ scope.row.updateTime }}</span>
                </template>
            </el-table-column>

            <el-table-column label="状态" width="100" align="center">
                <template slot-scope="scope">
                    <template v-if="scope.row.status == 1">
                        <span>正常</span>
                    </template>
                    <template v-if="scope.row.status == 2">
                        <span>推荐</span>
                    </template>
                    <template v-if="scope.row.status == 0">
                        <span>已删除</span>
                    </template>
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
    </div>
</template>

<script>
import {getLuckyRecordList} from "@/api/luckyRecord";
import {
    getAwardProductList,
} from "@/api/awardProduct";
import UserAvatar from "../../components/UserAvatar/index.vue";

export default {
    data() {
        return {
            awardProductList: [],
            BLOG_WEB_URL: process.env.BLOG_WEB_URL,
            multipleSelection: [], //多选，用于批量删除
            tableData: [],
            keyword: "",
            currentPage: 1,
            pageSize: 10,
            total: 0, //总数量
            formLabelWidth: "150px",
            isEditForm: false,
            orderByDescColumn: "", // 降序字段
            orderByAscColumn: "", // 升序字段
            form: {
                content: ""
            },
            queryParams: {
                awardProductUid: "",
                luckyActivityUid: "",
                userUid: "",
            },
            rules: {
                name: [
                    {required: true, message: '规则名称不能为空', trigger: 'blur'},
                    {min: 1, max: 25, message: '长度在1到10个字符'},
                ],
                maxLuckyCount: [
                    {required: true, message: '最大抽奖次数不能为空', trigger: 'blur'},
                    {pattern: /^[0-9]\d*$/, message: '最大抽奖次数只能为自然数'},
                ],
                maxAwardCount: [
                    {required: true, message: '最大中奖次数不能为空', trigger: 'blur'},
                    {pattern: /^[0-9]\d*$/, message: '最大中奖次数只能为自然数'},
                ],
            }
        };
    },
    components: {
        UserAvatar,
    },
    created() {
        this.getAwardProductListMethod()
        this.tagList();
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
            this.tagList()
        },
        tagList: function () {
            let params = {};
            params.keyword = this.keyword;
            params.currentPage = this.currentPage;
            params.pageSize = this.pageSize;
            params.userUid = this.queryParams.userUid
            params.luckyActivityUid = this.queryParams.luckyActivityUid
            params.awardProductUid = this.queryParams.awardProductUid
            params.orderByDescColumn = this.orderByDescColumn
            params.orderByAscColumn = this.orderByAscColumn
            getLuckyRecordList(params).then(response => {
                this.tableData = response.data.records;
                this.currentPage = response.data.current;
                this.pageSize = response.data.size;
                this.total = response.data.total;
            });
        },
        getAwardProductListMethod: function() {
            let params = {};
            params.currentPage = 1;
            params.pageSize = 50;
            getAwardProductList(params).then(response => {
                this.awardProductList = response.data.records;
            });
        },
        getLuckyItemName(luckyAwardItem, awardProduct) {
            let name = awardProduct.name
            if (luckyAwardItem.count > 1) {
                let awardType = awardProduct.awardType
                if (awardType != 5) {
                    name = name + "X" + luckyAwardItem.count
                } else {
                    name = name + " ¥" + luckyAwardItem.count / 100
                }
            }
            return name;
        },
        getFormObject: function () {
            var formObject = {
                uid: null,
                content: null,
                clickCount: 0,
                sort: 0
            };
            return formObject;
        },
        // 跳转到用户中心
        goUser: function (item) {
            window.open(
                this.BLOG_WEB_URL + '/userCenter?userUid=' + item.userUid,
                '_blank'
            )
        },
        handleFind: function () {
            this.currentPage = 1
            this.tagList();
        },
        handleCurrentChange: function (val) {
            this.currentPage = val;
            this.tagList();
        },
        // 改变多选
        handleSelectionChange(val) {
            this.multipleSelection = val;
        }
    }
};
</script>
