<template>
    <div class="app-container">
        <!-- 查询和其他操作 -->
        <div class="filter-container" style="margin: 10px 0 10px 0;">
            <el-input clearable class="filter-item" style="width: 200px;" v-model="queryParams.message" size="small"
                      placeholder="请输入评论内容"></el-input>
            <el-input clearable class="filter-item" style="width: 150px;" v-model="queryParams.fromUserNickName"
                      size="small" placeholder="评论人昵称"></el-input>
            <el-input clearable class="filter-item" style="width: 150px;" v-model="queryParams.userUid" size="small"
                      placeholder="评论人id"></el-input>
            <el-input clearable class="filter-item" style="width: 150px;" v-model="queryParams.toUserUid" size="small"
                      placeholder="接收人id"></el-input>

            <el-select v-model="queryParams.messageLevel" clearable placeholder="消息等级" size="small"
                       style="margin-left:5px;width:110px">
                <el-option
                    v-for="item in messageLevelDictList"
                    :key="item.uid"
                    :label="item.dictLabel"
                    :value="item.dictValue"
                ></el-option>
            </el-select>

            <el-select v-model="queryParams.messageCategory" clearable placeholder="消息类别" size="small"
                       style="margin-left:5px;width:110px">
                <el-option
                    v-for="item in messageCategoryDictList"
                    :key="item.uid"
                    :label="item.dictLabel"
                    :value="item.dictValue"
                ></el-option>
            </el-select>

            <el-select v-model="queryParams.messageType" clearable placeholder="消息类型" size="small"
                       style="margin-left:5px;width:110px">
                <el-option
                    v-for="item in messageTypeDictList"
                    :key="item.uid"
                    :label="item.dictLabel"
                    :value="item.dictValue"
                ></el-option>
            </el-select>

            <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFind" size="small"
                       v-permission="'/comment/getList'">查找
            </el-button>
            <el-button class="filter-item" type="danger" @click="handleDeleteBatch" icon="el-icon-delete" size="small"
                       v-permission="'/comment/deleteBatch'">删除选中
            </el-button>
        </div>

        <el-table :data="tableData" style="width: 100%" @selection-change="handleSelectionChange">

            <el-table-column type="selection"></el-table-column>

            <el-table-column label="序号" width="60" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.$index + 1 }}</span>
                </template>
            </el-table-column>

            <el-table-column label="用户" width="200" align="center">
                <template slot-scope="scope">
                    <UserAvatar :user="scope.row.fromUser"></UserAvatar>
                </template>
            </el-table-column>

            <el-table-column label="类别" width="120" align="center" prop="level" sortable="custom"
                             :sort-by="['level']">
                <template slot-scope="scope">
                    <el-tag v-for="item in messageCategoryDictList" :key="item.uid"
                            v-if="scope.row.category == item.dictValue" :type="item.listClass">{{ item.dictLabel }}
                    </el-tag>
                </template>
            </el-table-column>

            <el-table-column label="类型" width="120" align="center" prop="level" sortable="custom"
                             :sort-by="['level']">
                <template slot-scope="scope">
                    <el-tag v-for="item in messageTypeDictList" :key="item.uid"
                            v-if="scope.row.messageType == item.dictValue" :type="item.listClass">{{ item.dictLabel }}
                    </el-tag>
                </template>
            </el-table-column>

            <el-table-column label="接收人" width="200" align="center">
                <template slot-scope="scope">
                    <UserAvatar :user="scope.row.toUser"></UserAvatar>
                </template>
            </el-table-column>

            <el-table-column label="等级" width="120" align="center" prop="level" sortable="custom"
                             :sort-by="['level']">
                <template slot-scope="scope">
                    <el-tag v-for="item in messageLevelDictList" :key="item.uid"
                            v-if="scope.row.messageLevel == item.dictValue" :type="item.listClass">{{ item.dictLabel }}
                    </el-tag>
                </template>
            </el-table-column>


            <el-table-column label="内容" width="400" align="center">
                <template slot-scope="scope">
<!--                    <img style="max-height: 100px" v-if="scope.row.category == 20003" :src="scope.row.message"/>-->
                    <span  class="messageInfo" v-html="$xss(scope.row.message, options)"></span>

                </template>
            </el-table-column>

            <el-table-column label="创建时间" width="160" align="center">
                <template slot-scope="scope">
                    <span>{{ scope.row.createTime }}</span>
                </template>
            </el-table-column>

            <el-table-column label="操作" fixed="right" min-width="150">
                <template slot-scope="scope">
                    <!--          <el-button @click="handleDetail(scope.row)" type="success" size="small">详情</el-button>-->
                    <el-button @click="handleDelete(scope.row)" type="danger" size="mini"
                               v-permission="'/comment/delete'">删除
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
                :total="total">
            </el-pagination>
        </div>


    </div>
</template>

<script>
import {deleteBatchImMessage, getImMessageList} from "@/api/chat";
import {getListByDictTypeList} from "@/api/sysDictData"
import UserAvatar from "../../components/UserAvatar"

export default {
    data() {
        return {
            // xss白名单配置
            options: {
                whiteList: {
                    a: ['href', 'title', 'target', 'class'],
                    span: ['class'],
                    p: ['class'],
                    h1: ['class'],
                    h2: ['class'],
                    h3: ['class'],
                    h4: ['class'],
                    pre: ['class'],
                    code: ['class'],
                    div: ['class'],
                    img: ['src', 'alt', 'class', 'style', 'cursor'],
                    ul: ['class'],
                    li: ['class'],
                    br: [],
                }
            },
            queryParams: {
                fromUserNickName: null, // 评论人昵称
                message: null, // 聊天内容
                userUid: null, // 评论人uid
                toUserUid: null, // 被评论人uid
                messageType: null, //用户名
                messageLevel: null, //类型
                messageCategory: null, //来源
            }, //查询参数
            multipleSelection: [],  //多选，用于批量删除
            BLOG_WEB_URL: process.env.BLOG_WEB_URL,
            tableData: [],
            keyword: "",
            currentPage: 1,
            pageSize: 10,
            total: 0, //总数量
            title: "增加友链",
            dialogFormVisible: false, //控制弹出框
            formLabelWidth: '120px',
            isEditForm: false,
            commentTypeDictList: [], //评论类型字典
            commentSourceDictList: [], //评论来源字典
            messageLevelDictList: [], //消息等级字典
            messageTypeDictList: [], //消息类型字典
            messageCategoryDictList: [], // 消息列表
            commentTypeDefaultValue: null, //评论类型默认值
            defaultAvatar: process.env.defaultAvatar, // 默认头像
            orderByDescColumn: '',
            orderByAscColumn: 'send_time',
        };
    },
    created() {
        // 获取字典
        this.getDictList()
        // 获取评论
        this.messageList();
    },
    components: {
        UserAvatar,
    },
    methods: {
        // 跳转到用户中心
        goUser: function (user) {
            console.log("go user", user)
            this.$router.push({path: "/user/user", query: {nickName: user.nickName}});
        },
        // 跳转到该博客详情
        onClick: function (row) {
            console.log("点击跳转", row)
            window.open(this.BLOG_WEB_URL + "/info/" + row.oid);
        },
        // 跳转到前端页面
        goPage: function (type, blog) {
            switch (type) {
                case 'MESSAGE_BOARD': {
                    window.open(this.BLOG_WEB_URL + "/messageBoard")
                }
                    ;
                    break;
                case 'ABOUT': {
                    window.open(this.BLOG_WEB_URL + "/about")
                }
                    ;
                    break;
                case 'BLOG_INFO': {
                    window.open(this.BLOG_WEB_URL + "/info/" + blog.oid);
                }
                    ;
                    break;
            }
        },
        // 跳转到个人中心页
        getUserCenter: function (userUid) {
            window.open(this.BLOG_WEB_URL + '/userCenter?userUid=' + userUid, '_blank');
        },
        /**
         * 字典查询
         */
        getDictList: function () {
            let dictTypeList = ['sys_message_level', 'sys_message_type', 'sys_message_category']
            getListByDictTypeList(dictTypeList).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    let dictMap = response.data;
                    this.messageLevelDictList = dictMap.sys_message_level.list
                    this.messageTypeDictList = dictMap.sys_message_type.list
                    this.messageCategoryDictList = dictMap.sys_message_category.list
                }
            });
        },
        messageList: function () {
            let params = {}
            params.message = this.queryParams.message
            params.fromUserNickName = this.queryParams.fromUserNickName
            params.fromUserId = this.queryParams.userUid
            params.toUserId = this.queryParams.toUserUid
            params.messageType = this.queryParams.messageType
            params.messageLevel = this.queryParams.messageLevel
            params.category = this.queryParams.messageCategory
            params.currentPage = this.currentPage
            params.pageSize = this.pageSize
            params.orderByDescColumn = this.orderByDescColumn

            getImMessageList(params).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    this.tableData = response.data.records;
                    this.currentPage = response.data.current;
                    this.pageSize = response.data.size;
                    this.total = response.data.total;
                }
            });
        },
        subStr(str, index) {
            if (str == null || str == undefined) {
                return "";
            }
            if (str.length < index) {
                return str;
            } else {
                return str.substring(0, index) + ".."
            }
        },
        handleFind: function () {
            this.currentPage = 1
            this.messageList();
        },
        handleDetail: function (row) {
            console.log("点击了回复");
            this.$alert(this.$xss(row.content, this.options), '消息详情', {
                dangerouslyUseHTMLString: true
            });
        },
        handleDelete: function (row) {
            this.$confirm("此操作将把该评论删除, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    let params = {}
                    params.uid = row.uid
                    let messageList = [params]
                    deleteBatchImMessage(messageList).then(response => {
                        this.$commonUtil.message.success(response.message)
                        this.messageList();
                    })
                })
                .catch(() => {
                    this.$commonUtil.info("已取消删除")
                });
        },
        handleDeleteBatch: function () {
            let that = this;
            if (that.multipleSelection.length <= 0) {
                this.$message({
                    type: "error",
                    message: "请先选中需要删除的内容！"
                });
                return;
            }
            this.$confirm("此操作将把选中的评论删除, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    deleteBatchImMessage(that.multipleSelection).then(response => {
                        if (response.code == that.$ECode.SUCCESS) {
                            that.$commonUtil.message.success(response.message)
                        } else {
                            that.$commonUtil.message.error(response.message)
                        }
                        that.messageList();
                    });
                })
                .catch(() => {
                    that.$commonUtil.info("已取消删除")
                });
        },
        handleCurrentChange: function (val) {
            this.currentPage = val;
            this.messageList();
        },
        // 改变多选
        handleSelectionChange(val) {
            this.multipleSelection = val;
        }

    }
};
</script>
<style>
@import "../../assets/css/emoji.css";

.emoji-item-common {
    background: url("../../assets/img/emoji_sprite.png");
    display: inline-block;
}

.emoji-item-common:hover {
    cursor: pointer;
}

.emoji-size-small {
    zoom: 0.3;
    margin: 5px;
    vertical-align: middle;
}

.messageInfo img {
    max-height: 30px;
}
</style>

