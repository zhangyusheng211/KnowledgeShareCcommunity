<template xmlns="http://www.w3.org/1999/html">
    <div class="dashboard-editor-container">
        <el-menu class="navbar" mode="horizontal">
            <hamburger
                :toggle-click="toggleSideBar"
                :is-active="sidebar.opened"
                class="hamburger-container"
            />

            <breadcrumb/>

            <div class="right-menu">

                <el-tooltip content="门户页面" effect="dark" placement="bottom">
                    <Website id="website" class="right-menu-item"/>
                </el-tooltip>

                <el-tooltip content="Gitee源码" effect="dark" placement="bottom">
                    <MoGuGit id="mogu-git" class="right-menu-item"/>
                </el-tooltip>

                <el-tooltip content="文档地址" effect="dark" placement="bottom">
                    <MoGuDoc id="mogu-doc" class="right-menu-item"/>
                </el-tooltip>

                <el-tooltip effect="dark" content="全屏" placement="bottom">
                    <screenfull class="screenfull right-menu-item"></screenfull>
                </el-tooltip>

                <el-tooltip effect="dark" content="通知" placement="bottom">
                    <i class="right-menu-item theme-notice el-icon-bell" style="cursor: pointer;" @click="noticeList">
                        <el-badge is-dot style="margin-top: -25px;" class="item" :value="userReceiveNoticeCount"
                                  :hidden="userReceiveNoticeCount == 0"></el-badge>
                    </i>

                </el-tooltip>

                <el-tooltip effect="dark" content="换肤" placement="bottom">
                    <theme-picker class="theme-switch right-menu-item"></theme-picker>
                </el-tooltip>

                <el-dropdown class="avatar-container" trigger="click">
                    <div class="avatar-wrapper">
                        <img :src="avatar+'?imageView2/1/w/80/h/80'" class="user-avatar">
                        <i class="el-icon-caret-bottom"/>
                    </div>
                    <el-dropdown-menu slot="dropdown" class="user-dropdown">
                        <el-dropdown-item>
                            <span style="display:block;" @click="aboutMe">关于我</span>
                        </el-dropdown-item>
                        <el-dropdown-item divided>
                            <span style="display:block;" @click="showLog">更新日志</span>
                        </el-dropdown-item>
                        <el-dropdown-item divided>
                            <span style="display:block;" @click="logout">退出</span>
                        </el-dropdown-item>
                    </el-dropdown-menu>
                </el-dropdown>
            </div>
        </el-menu>
        <el-drawer
            title="通知消息"
            :visible.sync="table"
            direction="rtl"
            size="50%">
            <!--          <el-table :data="gridData">
                          <el-table-column property="createTime" label="创建时间" width="150"></el-table-column>
                          <el-table-column property="businessTypeName" label="通知类型" width="200"></el-table-column>
                          <el-table-column property="content" label="通知内容"></el-table-column>
                      </el-table>-->
            <span>
              <el-tabs v-model="noticeType" :tab-position="tabPosition" style="min-height: 820px;"
                       @tab-click="handleClick">
              <el-tab-pane class="tabPane" name="1">
                  <el-row style="height: 30px; border-bottom: 1px solid #e0e0e0;">
                      <el-col :span="12">
                          <span style="font-size: 16px; font-weight: 600; color: #222226;">系统通知</span>
                      </el-col>
                      <el-col :span="11" style="text-align: right;">
                      <span style="font-size: 16px; cursor: pointer; " @click="handleDeleteBatch">
                        <i class="el-icon-view"></i> 清空所有消息
                      </span>
                      </el-col>
                  </el-row>

                  <div class="commentList" v-for="item in gridData" :key="item.uid">
                      <span class="left p1 " v-if="item.user" :class="item.user.userTag > 0 ?'vip-avatar':''">
                          <img :class="item.user.userTag > 0 ?'vip-color':''" v-if="item.user" style="cursor:pointer;"
                               @click="getUserCenter(item.user)"
                               :src="item.user.photoUrl ? item.user.photoUrl:defaultAvatar"
                               onerror="onerror=null;src=defaultAvatar"/>
                          <img v-else :src="defaultAvatar"/>
                          <span class="vip-text pointer" v-if="item.user.userTag > 0"
                                @click.natice="getUserCenter(item.user)">v</span>
                      </span>

                      <span class="right p1" v-if="item.user">
                      <div class="rightTop">

                        <span v-if="item.user" class="pointer userName"
                              @click="getUserCenter(item)">{{ item.user.nickName }}</span>
                        <span class="timeAgo" style="color: #8a919f;"
                              v-if="item.user.occupation">{{ item.user.occupation }}</span>

                        <span class="timeAgo">
                          <span class="lv" :class="'lv'+ item.user.userLevel" v-for="userLevel in userLevelDictList"
                                :key="userLevel.uid" v-if="item.user.userLevel == userLevel.dictValue">
                                {{ userLevel.remark }}
                          </span>
                        </span>
                        <span class="timeAgo" style="color: #8a919f;"
                              v-if="item.user.userIpPossession">IP属地:{{ item.user.userIpPossession }}</span>


                        <span class="timeAgo">{{ convertTimeAgo(item.createTime) }}</span>
                        <i class="el-icon-delete deleteStyle" @click="handleDelete(item)"></i>
                      </div>

                      <div class="rightCenter">
                        <span class="timeAgo" v-if="item.businessType == 8">
                          提交博客发布审批：
                          <span style="cursor: pointer; color: #555;" @click="routeModule(item)"
                                v-html="$xss(item.content, options)"></span>
                        </span>
                        <span class="timeAgo" v-if="item.businessType == 9">
                          提交问答发布审批：
                          <span style="cursor: pointer; color: #555;" @click="routeModule(item)"
                                v-html="$xss(item.content, options)"></span>
                        </span>
                        <span class="timeAgo" v-if="item.businessType == 22">
                          提交动态发布审批：
                          <span style="cursor: pointer; color: #555;" @click="routeModule(item)"
                                v-html="$xss(item.content, options)"></span>
                        </span>
                        <span class="timeAgo" v-if="item.businessType == 23">
                          提交问题发布审批：
                          <span style="cursor: pointer; color: #555;" @click="routeModule(item)"
                                v-html="$xss(item.content, options)"></span>
                        </span>
                        <span class="timeAgo" v-if="item.businessType == 51">
                          提交友情链接审批：
                          <span style="cursor: pointer; color: #555;" @click="routeModule(item)"
                                v-html="$xss(item.content, options)"></span>
                        </span>
                        <span class="timeAgo" v-if="item.businessType == 52">
                          提交反馈审批：
                          <span style="cursor: pointer; color: #555;" @click="routeModule(item)"
                                v-html="$xss(item.content, options)"></span>
                        </span>
                        <span class="timeAgo" v-if="item.businessType == 53">
                          提交勘误审批：
                          <span style="cursor: pointer; color: #555;" @click="routeModule(item)"
                                v-html="$xss(item.content, options)"></span>
                        </span>
                         <span class="timeAgo" v-if="item.businessType == 67">
                           发起提现审批：
                          <span style="cursor: pointer; color: #555;" @click="routeModule(item)"
                                v-html="$xss(item.content, options)"></span>
                        </span>
                      </div>
                    </span>
                  </div>
                  <span v-if="gridData.length == 0">
                    <el-empty description="空空如也" image=""></el-empty>
                  </span>
              </el-tab-pane>
              </el-tabs>
          </span>
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
        </el-drawer>
    </div>
</template>


<script>
import {mapGetters} from "vuex";
import Breadcrumb from "@/components/Breadcrumb";
import Hamburger from "@/components/Hamburger";
import Screenfull from "@/components/Screenfull";
import ThemePicker from "@/components/ThemePicker";
import MoGuGit from '@/components/MoGu/Git'
import MoGuDoc from '@/components/MoGu/Doc'
import Website from '@/components/MoGu/Website'
import {
    deleteBatchNotice,
    deleteNotice,
    deleteRedisBatchNotice,
    searchAllNotice,
    searchAllNoticeCount
} from "../../../api/notice";
import {getListByDictTypeList} from '@/api/sysDictData'
import {timeAgo} from "../../../utils/webUtils";

export default {
    components: {
        Screenfull,
        Breadcrumb,
        Hamburger,
        ThemePicker,
        MoGuGit,
        MoGuDoc,
        Website
    },
    data() {
        return {
            title: "更新日志",
            activeName: "1",
            gridData: [],
            table: false,
            dialog: false,
            loading: false,
            timer: null,
            VUE_MOGU_WEB: process.env.BLOG_WEB_URL,
            defaultAvatar: process.env.defaultAvatar,
            splitCount: 1024, // 切割的数
            tabPosition: 'left',
            noticeType: "1",
            userReceiveNoticeCount: 0, // 管理员的未读消息
            userLevelDictList: [],
            currentPage: 1,
            pageSize: 10,
            total: 0, //总数量
            // xss白名单配置
            options: {
                whiteList: {
                    a: ['href', 'title', 'target', 'style'],
                    span: ['class', 'style'],
                    p: ['class', 'style'],
                    div: ['class', 'style'],
                    br: ['class', 'style'],
                    code: ['class', 'style'],
                    img: ['src', 'style', 'class']
                }
            },
        }
    },
    created() {
        this.getDictList()
        this.searchAllBlkNotice();
    },
    computed: {
        ...mapGetters(["sidebar", "avatar"])
    },
    methods: {
        handleCurrentChange: function (val) {
            this.currentPage = val;
            this.searchAllBlkNotice();
        },
        routeModule: function (notice) {
            switch (notice.businessType) {
                case 8: {
                    this.$router.push({path: "/blog/blog"});
                }
                    break;
                case 9: {
                    this.$router.push({path: "/question/question"});
                }
                    break;
                case 22: {
                    this.$router.push({path: "/moment/userMoment"});
                }
                    break;
                case 23: {
                    this.$router.push({path: "/problem/problem"});
                }
                    break;
                case 51: {
                    this.$router.push({path: "/system/blogLink"});
                }
                    break;
                case 52: {
                    this.$router.push({path: "/message/feedback"});
                }
                    break;
                case 53: {
                    this.$router.push({path: "/system/generalEdit"});
                }
                    break;
                case 67: {
                    console.log("跳转到提现管理")
                    this.$router.push({path: "/order/withdraw"});
                }
                    break;
            }
        },
        /**
         * 字典查询
         */
        getDictList: function () {
            const dictTypeList = [
                'sys_user_level'
            ]
            getListByDictTypeList(dictTypeList).then((response) => {
                if (response.code == this.$ECode.SUCCESS) {
                    const dictMap = response.data
                    this.userLevelDictList = dictMap.sys_user_level.list
                }
            })
        },
        /**
         * dateTimeStamp是一个时间毫秒，注意时间戳是秒的形式，在这个毫秒的基础上除以1000，就是十位数的时间戳。13位数的都是时间毫秒。
         * @param dateTimeStamp
         * @returns {string}
         */
        convertTimeAgo(dateTimeStamp) {
            return timeAgo(dateTimeStamp)
        },
        handleClick(tab, event) {
            console.log(tab.index)
            this.currentPage = 1
            switch (tab.index) {
                case "0": {
                    this.noticeType = "1"
                }
                    break;
            }
        },
        splitStr: (str, flagCount) => {
            if (str == null || str == '') {
                return ""
            } else if (str.length > flagCount) {
                return str.substring(0, flagCount) + "..."
            } else {
                return str
            }
        },
        searchAllBlkNotice: function () {
            let params = {};
            params.currentPage = this.currentPage;
            params.pageSize = this.pageSize;
            searchAllNotice(params).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    this.gridData = response.data.records
                    this.currentPage = response.data.current;
                    this.pageSize = response.data.size;
                    this.total = response.data.total;
                }
            });
            searchAllNoticeCount().then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    console.log('未读消息数量  ：' + response.data)
                    this.userReceiveNoticeCount = response.data
                }
            })
        },
        handleDelete: function (notice) {
            console.log("删除通知", notice)

            this.$confirm("此操作将把通知删除, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    let params = {};
                    params.uid = notice.uid;
                    deleteNotice(params).then(response => {
                        if (response.code == this.$ECode.SUCCESS) {
                            this.$message.success(response.message)
                            this.searchAllBlkNotice();
                        } else {
                            this.$message.error(response.message)
                        }
                    });
                })
                .catch(() => {
                    this.$commonUtil.message.info("已取消删除")
                });
        },
        handleDeleteBatch: function (notice) {
            console.log("删除全部通知", notice)

            this.$confirm("此操作将把全部通知删除, 是否继续?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning"
            })
                .then(() => {
                    let noticeList = this.gridData
                    if (noticeList.length == 0) {
                        this.$message.error("暂无通知")
                        return
                    }
                    let params = []
                    for (let a = 0; a < noticeList.length; a++) {
                        let temp = {};
                        temp.uid = noticeList[a].uid;
                        params.push(temp)
                    }
                    deleteBatchNotice(params).then(response => {
                        if (response.code == this.$ECode.SUCCESS) {
                            this.$message.success(response.message)
                            this.searchAllBlkNotice();
                        } else {
                            this.$message.error(response.message)
                        }
                    });
                })
                .catch(() => {
                    this.$commonUtil.message.info("已取消删除")
                });
        },
        handleClose(done) {
            if (this.loading) {
                return;
            }
        },
        noticeList: function () {
            this.table = true;
            //未读消息清零
            this.userReceiveNoticeCount = 0;
            //清空redis小红点
            deleteRedisBatchNotice().then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    console.log('删除小红点成功');
                }
            });
        },
        getUserCenter: function (user) {
            window.open(this.VUE_MOGU_WEB + '/userCenter?userUid=' + user.uid, '_blank');
        },
        toggleSideBar() {
            this.$store.dispatch("ToggleSideBar");
        },
        logout() {
            this.$store.dispatch("LogOut").then(() => {
                // 为了重新实例化vue-router对象 避免bug
                location.reload();
            });
        },
        showLog: function () {
            console.log("点击了显示日志");
            window.open("https://vsp58xj3kr.feishu.cn/wiki/JniZwgRkUiv609kOv1ZcxYQJnDh", "_blank")
        },
        aboutMe: function () {
            this.$router.push({path: '/system/aboutMe'})
        }
    }
};
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.navbar {
    height: 50px;
    line-height: 50px;
    border-radius: 0px !important;

    .hamburger-container {
        line-height: 58px;
        height: 50px;
        float: left;
        padding: 0 10px;
    }

    .breadcrumb-container {
        float: left;
    }

    .errLog-container {
        display: inline-block;
        vertical-align: top;
    }

    .right-menu {
        float: right;
        height: 100%;

        &:focus {
            outline: none;
        }

        .right-menu-item {
            display: inline-block;
            margin: 0 8px;
        }

        .screenfull {
            height: 20px;
        }

        .international {
            vertical-align: top;
        }

        .theme-notice {
            vertical-align: 10px;
            font-size: 25px;
        }

        .theme-switch {
            vertical-align: 15px;
        }

        .avatar-container {
            height: 50px;
            margin-right: 30px;

            .avatar-wrapper {
                cursor: pointer;
                margin-top: 5px;
                position: relative;

                .user-avatar {
                    width: 40px;
                    height: 40px;
                    border-radius: 10px;
                }

                .el-icon-caret-bottom {
                    position: absolute;
                    right: -20px;
                    top: 25px;
                    font-size: 12px;
                }
            }
        }
    }
}

.commentList {
    width: 100%;
    margin: 15px 0 0 10px;

}

.commentList .p1 {
    float: left;
}

.commentList .left {
    display: inline-block;
    width: 5%;
    height: 100%;
}

.commentList .left img {
    margin: 0 auto;
    width: 100%;
    border-radius: 50%;
}

.commentList .right {
    display: inline-block;
    width: 93%;
    margin-left: 5px;
    margin-top: 15px;
    border-bottom: 1px solid #e0e0e0;
}

.commentList .rightTop {
    height: 50px;
    margin-top: 2px;
}

.commentList .rightTop .userName {
    margin-left: 10px;
    margin-top: -5px;
    font-size: 16px;
    font-weight: bold;
    cursor: pointer;
}

.commentList .rightTop .timeAgo {
    color: #909399;
    margin-left: 5px;
    font-size: 12px;
}

.commentList .rightCenter {
    margin-left: 10px;
    min-height: 40px;
    margin-top: 0px;
}

.commentList .rightBottom {
    margin-left: 10px;
    height: 30px;
}

.commentList .rightBottom el-link {

}

.commentList .rightBottom .b1 {
    margin-left: 10px;
}

.deleteStyle {
    cursor: pointer;
    margin-right: 10px;
    float: right;
}

.deleteStyle:hover {
    color: #00AAAA;
}

.el-icon-bell:hover {
    color: #00ccff;
}
</style>

