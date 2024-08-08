<template>
    <div>
        <el-row>
            <!--访问权限-->
            <el-col  :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
                <el-form-item
                    :label-width="formLabelWidth"
                    prop="visitAuth"
                >
                    <template slot="label">
                        访问权限
                        <el-popover
                            placement="top-start"
                            width="400"
                            trigger="hover">
                            <div>
                                <div>用于控制文章在满足限定条件后才可以查看全文</div>
                                <div>注意：若文章归属于某个专栏，将会收到专栏的访问权限控制</div>
                                <div v-for="item in visitAuthDictList" :key="item.uid + item.uid" v-if="!excludeVisitAuth.includes(parseInt(item.dictValue))">
                                    <span>{{item.dictLabel}}:</span>
                                    <span>{{item.remark}}</span>
                                </div>
                            </div>
                            <i slot="reference" style="cursor: pointer;margin-left: 2px" class="el-icon-question"/>
                        </el-popover>
                    </template>

                    <el-select
                        v-model="form.visitAuthList"
                        size="small"
                        placeholder="请选择访问权限"
                        style="width: 200px"
                        @change="visitAuthChange"
                        multiple
                        :multiple-limit="5"
                    >
                        <el-option
                            v-for="item in visitAuthDictList"
                            :key="item.uid"
                            :label="item.dictLabel"
                            v-if="!excludeVisitAuth.includes(parseInt(item.dictValue))"
                            :value="item.dictValue"
                        />
                    </el-select>
                </el-form-item>
            </el-col>

            <!--0. 控制访问区域，如果是文章详情可以支持让用户看到部分区域的内容-->
            <!--只有一个选项时，并且该选项是1，不显示该控件，其它情况都显示-->
            <el-col  :xs="24" :sm="12" :md="8" :lg="6" :xl="4" v-if="!(form.visitAuthList.length === 0 || (form.visitAuthList.length === 1 && form.visitAuthList[0] === '1')) && showLoadingArea" >
                <el-form-item :label-width="formLabelWidth" prop="loadingArea">
                    <template slot="label">
                        限制区域
                        <el-popover
                            placement="top-start"
                            width="400"
                            trigger="hover">
                            <div>
                                <div>用于控制不满足访问权限时，内容可以展示的范围【当设置多个时，只需满足一个即可生效】</div>
                                <div v-for="item in loadingAreaDictList" :key="item.uid + item.uid">
                                    <span>{{item.dictLabel}}:</span>
                                    <span>{{item.remark}}</span>
                                </div>
                            </div>
                            <i slot="reference" style="cursor: pointer;margin-left: 2px" class="el-icon-question"/>
                        </el-popover>
                    </template>

                    <el-select
                        v-model="form.visitAuthExtra.loadingArea"
                        size="small"
                        placeholder="请选择访问区域"
                        style="width: 160px"
                    >
                        <el-option
                            v-for="item in loadingAreaDictList"
                            :key="item.uid"
                            :label="item.dictLabel"
                            :value="parseInt(item.dictValue)"
                        ></el-option>
                    </el-select>
                </el-form-item>
            </el-col>



            <el-col  :xs="24" :sm="12" :md="8" :lg="6" :xl="4" v-if="!(form.visitAuthList.length === 0 || (form.visitAuthList.length === 1 && form.visitAuthList[0] === '1')) && showLoadingArea" >
                <el-form-item :label-width="formLabelWidth" prop="visitAuthText">
                    <template slot="label">
                        访问限制文本
                        <el-popover
                            placement="top-start"
                            width="400"
                            trigger="hover">
                            <div>
                                当不满足访问权限时，提示的文案内容【不设置时采用数据字典中配置的提示】
                            </div>
                            <i slot="reference" style="cursor: pointer;margin-left: 2px" class="el-icon-question"/>
                        </el-popover>
                    </template>
                    <el-input size="small" v-model="form.visitAuthExtra.visitAuthText" auto-complete="off"></el-input>
                </el-form-item>
            </el-col>

            <!--1. 收费模式和金额，仅付费使用时才使用-->
            <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4" v-if="form.visitAuthList.includes('6')">
                <el-form-item label="收费模式" :label-width="formLabelWidth" prop="payType">
                    <el-select
                        v-model="form.visitAuthExtra.payType"
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
            </el-col>

            <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4" v-if="form.visitAuthList.includes('6')">
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

                    <el-input size="small" v-model="form.visitAuthExtra.price" auto-complete="off"></el-input>
                </el-form-item>
            </el-col>

            <!-- 2. 输入密码【输入密码能访问】-->
            <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4" v-if="form.visitAuthList.includes('10')">
                <el-form-item label="访问密码"  :label-width="formLabelWidth" prop="password">
                    <el-input size="small" v-model="form.visitAuthExtra.password" auto-complete="off"></el-input>
                </el-form-item>
            </el-col>


            <!-- 3. 查询用户标签【指定标签人群能访问】-->
            <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4" v-if="form.visitAuthList.includes('12')">
                <el-form-item label="用户标签" :label-width="formLabelWidth" prop="userTagList">
                    <el-select
                        v-model="form.visitAuthExtra.userTagList"
                        size="small"
                        placeholder="请选择用户标签"
                        style="width: 250px"
                        multiple
                    >
                        <el-option
                            v-for="item in userTagDictList"
                            :key="item.uid"
                            :label="item.dictLabel"
                            :value="item.dictValue"
                        ></el-option>
                    </el-select>
                </el-form-item>
            </el-col>

            <!-- 13. 指定用户可见【指定用户人群能访问】-->
            <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4" v-if="form.visitAuthList.includes('13')">
                <el-form-item
                    :label-width="formLabelWidth"
                    label="圈选用户"
                    prop="level"
                >
                    <el-select
                        v-model="form.visitAuthExtra.userUidList"
                        :remote-method="userRemoteMethod"
                        :loading="loading"
                        multiple
                        filterable
                        clearable
                        remote
                        reserve-keyword
                        placeholder="输入名称搜索更多"
                        style="width: 300px"
                        size="small"
                    >
                        <el-option
                            v-for="item in userOptions"
                            :key="item.uid"
                            :label="item.nickName"
                            :value="item.uid"
                        />
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>

    </div>
</template>

<script>
import {getListByDictTypeList} from '@/api/sysDictData'
import {getUserList} from "@/api/user";
export default {
    name: "visit_auth",
    props: {
        // 表单Label的宽度
        formLabelWidth: {
            type: String,
            default: "120px",
        },
        // 访问权限 1 公开、2 登录后可见、3 评论后可见、4 验证可见、5 会员可见、6 付费可见、7 点赞阅读、8 收藏阅读、9 关注阅读、10 密码可见、11 仅作者可见, 12 标签用户可见、13 指定用户可见
        visitAuth: {
            type: String,
            default: "",
        },
        // 限制加载区域
        showLoadingArea: {
          type: Boolean,
          default: false
        },
        // 需要排除的访问权限
        excludeVisitAuth: {
          type: Array,
          default: []
        },
        // 访问权限扩展字段
        visitAuthExtra: {
            type: Object,
            default: function() {
                return {
                    password: "", // 访问密码
                    userTagList: null, // 用户标签
                    userUidList: null, // 指定用户可见
                    price: 0, // 价格
                    payType: null, // 收费模式
                    loadingArea: null, // 加载区域
                }
            }
        },
    },
    watch: {

    },
    data() {
        return {
            form: {
                // 访问权限列表
                visitAuthList: [],
                // 访问权限
                visitAuth: this.visitAuth,
                // 访问权限扩展字段
                visitAuthExtra: this.visitAuthExtra
            },
            visitAuthDictList: [], // 访问权限字典
            visitAuthDefault: null, // 访问权限默认值
            payTypeDictList: [], // 收费模式字典
            payTypeDefault: null, // 收费模式默认值
            userTagDictList: [], // 用户标签字典
            loadingAreaDictList: [], // 加载区域字段
            loadingAreaDefaultValue: null, // 加载区域默认值
            userOptions: [],
            loading: false,
        }
    },
    created() {
        // 初始化字典数据
        this.getDictList()
        // 初始化用户数据
        this.initUserData()

        // 初始化数据
        if (this.visitAuth) {
            console.log("this.visitAuth", this.visitAuth)
            const visitAuthList = this.visitAuth.split(',')
            for (let a = 0; a < visitAuthList.length; a++) {
                if (visitAuthList[a] != null && visitAuthList[a] != '') {
                    this.form.visitAuthList.push(visitAuthList[a])
                }
            }
        }
    },
    methods: {

        getFormObject: function () {
            let formObject = {
                password: "", // 访问密码
                userTagList: null, // 用户标签
                userUidList: null, // 指定用户可见
                price: 0, // 价格
                payType: parseInt(this.payTypeDefaultValue), // 收费模式
                loadingArea: parseInt(this.loadingAreaDefaultValue), // 加载区域
                visitAuthText: "",
            };
            return formObject;
        },
        visitAuthChange() {
            this.form.visitAuthExtra = this.getFormObject()
            this.form.visitAuth = this.form.visitAuthList.join(",")
        },

        initUserData() {
            // 校验传递的参数是否有
            let userUidList = this.visitAuthExtra.userUidList
            if (userUidList) {
                this.queryUserList(null, userUidList)
            }
        },
        // 标签远程搜索函数
        userRemoteMethod: function (query) {
            if (query !== '') {
                this.queryUserList(query, null)
            } else {
                this.tagOptions = []
            }
        },
        queryUserList(query, userUidList) {
            this.loading = true
            const params = {}
            params.keyword = query
            params.userUidList = userUidList
            if (userUidList) {
                params.pageSize = userUidList.length
            } else {
                params.pageSize = 10
            }
            params.currentPage = 1
            getUserList(params).then(response => {
                console.log("getUserList", response);
                this.loading = false
                if (response.code === this.$ECode.SUCCESS) {
                    this.userOptions = response.data.records;
                }
            });
        },

        /**
         * 字典查询
         */
        getDictList: function () {
            const dictTypeList = [
                'sys_visit_auth',
                'sys_pay_type',
                'sys_user_tag',
                'sys_loading_area',
            ]
            getListByDictTypeList(dictTypeList).then((response) => {
                console.log("查询数据字典", response)
                if (response.code == this.$ECode.SUCCESS) {
                    const dictMap = response.data
                    this.visitAuthDictList = dictMap.sys_visit_auth.list
                    this.payTypeDictList = dictMap.sys_pay_type.list
                    this.userTagDictList = dictMap.sys_user_tag.list
                    this.loadingAreaDictList = dictMap.sys_loading_area.list
                    if (dictMap.sys_visit_auth.defaultValue) {
                        this.visitAuthDefault = dictMap.sys_visit_auth.defaultValue
                    }
                    if (dictMap.sys_pay_type.defaultValue) {
                        this.payTypeDefaultValue = dictMap.sys_pay_type.defaultValue
                    }
                    if (dictMap.sys_loading_area.defaultValue) {
                        this.loadingAreaDefaultValue = dictMap.sys_loading_area.defaultValue
                    }
                }
            })
        },
        // 获取访问权限信息
        getVisitAuthInfo() {
            console.log("返回表单内容")
            return this.form
        },
    }
}
</script>

<style scoped>

</style>
