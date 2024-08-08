<template>
    <div class="app-container">
        <el-tabs v-model="activeName" type="border-card" @tab-click="handleClick">
            <el-tab-pane v-permission="'/systemConfig/getSystemConfig'" name="one">
                <span slot="label"><i class="el-icon-edit"/> 系统配置</span>
                <el-form style="margin-left: 20px;" label-position="left" label-width="160px">
                    <aside>
                        通过开关选择博客编辑时的文本编辑器，以及文件显示方式<br>
                    </aside>
                    <el-form-item label="封面图片显示优先级">
                        <template slot="label">
                            封面图片显示优先级
                            <el-popover
                                placement="top-start"
                                width="200"
                                trigger="hover"
                                content="用于控制封面图片使用哪个存储类型的图片">
                                <i slot="reference" style="cursor: pointer;margin-left: 2px" class="el-icon-question"/>
                            </el-popover>
                        </template>
                        <el-radio v-for="item in picturePriorityDictList" :key="item.uid" v-model="form.picturePriority"
                                  :label="item.dictValue" border size="medium">{{ item.dictLabel }}
                        </el-radio>
                    </el-form-item>

                    <el-form-item label="详情图片显示优先级">
                        <template slot="label">
                            详情图片显示优先级
                            <el-popover
                                placement="top-start"
                                width="200"
                                trigger="hover"
                                content="用于控制详情图片使用哪个存储类型的图片">
                                <i slot="reference" style="cursor: pointer;margin-left: 2px" class="el-icon-question"/>
                            </el-popover>
                        </template>
                        <el-radio v-for="item in picturePriorityDictList" :key="item.uid"
                                  v-model="form.contentPicturePriority" :label="item.dictValue" border size="medium">
                            {{ item.dictLabel }}
                        </el-radio>
                    </el-form-item>

                    <el-form-item label="文本编辑器">
                        <el-radio v-for="item in editorModalDictList" :key="item.uid" v-model="form.editorModel"
                                  :label="item.dictValue" border size="medium">{{ item.dictLabel }}
                        </el-radio>
                    </el-form-item>

                    <!--当有新的反馈，友链申请时进行通知，首先需要在系统管理处设置接收通知的邮箱 -->
                    <el-form-item label="网站消息邮件通知">
                        <el-radio v-for="item in openDictList" :key="item.uid" v-model="form.startEmailNotification"
                                  :label="item.dictValue" border size="medium">{{ item.dictLabel }}
                        </el-radio>
                    </el-form-item>

                    <!-- 仪表盘弹框通知，在用户登录后台的时候会出现，可以手动关闭 -->
                    <el-form-item label="仪表盘弹框通知">
                        <el-radio v-for="item in openDictList" :key="item.uid" v-model="form.openDashboardNotification"
                                  :label="item.dictValue" border size="medium">{{ item.dictLabel }}
                        </el-radio>
                    </el-form-item>

                    <!-- 用于控制用户是否需要通过邮箱验证，完成认证-->
                    <el-form-item label="注册用户邮件激活">
                        <el-radio v-for="item in openDictList" :key="item.uid" v-model="form.openEmailActivate"
                                  :label="item.dictValue" border size="medium">{{ item.dictLabel }}
                        </el-radio>
                    </el-form-item>

                    <!-- 搜索模式-->
                    <el-form-item>
                        <template slot="label">
                            文章搜索模式
                            <el-popover
                                placement="top-start"
                                width="200"
                                trigger="hover"
                                content="用于控制门户搜索功能使用SQL方式，还是全文检索。开启全文检索需要启动mogu-search项目">
                                <i slot="reference" style="cursor: pointer;margin-left: 2px" class="el-icon-question"/>
                            </el-popover>
                        </template>
                        <el-radio v-for="item in searchModelDictList" :key="item.uid" v-model="form.searchModel"
                                  :label="item.dictValue" border size="medium">{{ item.dictLabel }}
                        </el-radio>
                    </el-form-item>

                    <el-form-item>
                        <el-button v-permission="'/systemConfig/editSystemConfig'" type="primary" @click="submitForm()">
                            保 存
                        </el-button>
                    </el-form-item>

                </el-form>
            </el-tab-pane>

            <el-tab-pane v-permission="'/systemConfig/getSystemConfig'" name="three">
        <span slot="label">
          <i class="el-icon-date"/> 本地文件存储
        </span>
                <el-form
                    ref="form"
                    :model="form"
                    :rules="rules"
                    style="margin-left: 20px;"
                    label-position="left"
                    label-width="140px"
                >
                    <aside>
                        使用IO流将文件存储本地磁盘中<br>
                    </aside>
                    <el-form-item label="本地文件域名" prop="localPictureBaseUrl">
                        <el-input v-model="form.localPictureBaseUrl" auto-complete="new-password" style="width: 400px"/>
                    </el-form-item>

                    <el-form-item label="文件上传本地">
                        <el-radio v-for="item in yesNoDictList" :key="item.uid" v-model="form.uploadLocal"
                                  :label="item.dictValue" border size="medium">{{ item.dictLabel }}
                        </el-radio>
                    </el-form-item>

                    <el-form-item>
                        <el-button v-permission="'/systemConfig/editSystemConfig'" type="primary" @click="submitForm()">
                            保 存
                        </el-button>
                    </el-form-item>

                </el-form>
            </el-tab-pane>

            <el-tab-pane v-permission="'/systemConfig/getSystemConfig'" name="four">
        <span slot="label">
          <i class="el-icon-date"/> 七牛云对象存储
        </span>

                <el-form
                    ref="form"
                    :model="form"
                    :rules="rules"
                    style="margin-left: 20px;"
                    label-position="left"
                    label-width="120px"
                >
                    <aside>
                        使用 <a target="_blank" href="http://www.moguit.cn/info/202">七牛云</a> 构建对象存储服务<br>
                    </aside>

                    <el-form-item label="七牛云文件域名" prop="qiNiuPictureBaseUrl">
                        <el-input v-model="form.qiNiuPictureBaseUrl" auto-complete="new-password" style="width: 400px"/>
                    </el-form-item>

                    <el-form-item label="七牛云公钥">
                        <el-input v-model="form.qiNiuAccessKey" auto-complete="new-password" style="width: 400px"/>
                    </el-form-item>

                    <el-form-item label="七牛云私钥">
                        <el-input v-model="form.qiNiuSecretKey" type="password" auto-complete="new-password"
                                  style="width: 400px"/>
                    </el-form-item>

                    <el-form-item label="上传空间">
                        <el-input v-model="form.qiNiuBucket" style="width: 400px"/>
                    </el-form-item>

                    <el-form-item label="存储区域">
                        <el-select v-model="form.qiNiuArea" placeholder="请选择存储区域" clearable>
                            <el-option
                                v-for="item in areaDictList"
                                :key="item.dictValue"
                                :label="item.dictLabel"
                                :value="item.dictValue"/>
                        </el-select>
                    </el-form-item>

                    <el-form-item label="文件上传七牛云">
                        <el-radio v-for="item in yesNoDictList" :key="item.uid" v-model="form.uploadQiNiu"
                                  :label="item.dictValue" border size="medium">{{ item.dictLabel }}
                        </el-radio>
                    </el-form-item>

                    <el-form-item>
                        <el-button v-permission="'/systemConfig/editSystemConfig'" type="primary" @click="submitForm()">
                            保 存
                        </el-button>
                    </el-form-item>

                </el-form>
            </el-tab-pane>

            <el-tab-pane v-permission="'/systemConfig/getSystemConfig'" name="four2">
        <span slot="label">
          <i class="el-icon-date"/> 阿里云对象存储
        </span>

                <el-form
                    ref="form"
                    :model="form"
                    :rules="rules"
                    style="margin-left: 20px;"
                    label-position="left"
                    label-width="120px"
                >
                    <aside>
                        使用 <a
                        href="https://oss.console.aliyun.com/bucket/oss-cn-beijing/oneblogpicture/overview">阿里云</a>
                        构建对象存储服务<br>
                    </aside>
                    <el-form-item label="访问域名" prop="aliossPictureBaseUrl">
                        <el-input v-model="form.aliossPictureBaseUrl" auto-complete="new-password"
                                  style="width: 400px"/>
                    </el-form-item>
                    <el-form-item label="Bucket名称">
                        <el-input v-model="form.aliyunBucketName" auto-complete="new-password" style="width: 400px"/>
                    </el-form-item>

                    <el-form-item label="Endpoint">
                        <el-input v-model="form.aliyunEndpoint" auto-complete="new-password" style="width: 400px"/>
                    </el-form-item>

                    <el-form-item label="存储目录">
                        <el-input v-model="form.aliyunCatalog" auto-complete="new-password" style="width: 400px"/>
                    </el-form-item>

                    <el-form-item label="AccessKey">
                        <el-input v-model="form.aliyunAccessKey" style="width: 400px"/>
                    </el-form-item>

                    <el-form-item label="AccessSecret">
                        <i class="fa fa-question-circle"
                           title="阿里云API密钥，获取地址：https://ak-console.aliyun.com/#/"/>
                        <el-input v-model="form.aliyunAccessKeySecret" type="password"
                                  placeholder="请输入Access Key Secret" clearable style="width: 400px"/>
                    </el-form-item>

                    <el-form-item label="上传阿里OSS">
                        <el-radio v-for="item in yesNoDictList" :key="item.uid" v-model="form.uploadAlioss"
                                  :label="item.dictValue" border size="medium">{{ item.dictLabel }}
                        </el-radio>
                    </el-form-item>

                    <el-form-item>
                        <el-button v-permission="'/systemConfig/editSystemConfig'" type="primary" @click="submitForm()">
                            保 存
                        </el-button>
                    </el-form-item>

                </el-form>
            </el-tab-pane>

            <el-tab-pane v-permission="'/systemConfig/getSystemConfig'" name="five">
        <span slot="label">
          <i class="el-icon-date"/> Minio对象存储
        </span>

                <el-form
                    ref="form"
                    :model="form"
                    :rules="rules"
                    style="margin-left: 20px;"
                    label-position="left"
                    label-width="120px"
                >

                    <aside>
                        使用 <a target="_blank" href="http://www.moguit.cn/info/278">Minio</a> 构建本地对象存储服务<br>
                    </aside>

                    <el-form-item label="Minio访问域名" prop="localPictureBaseUrl">
                        <el-input v-model="form.minioPictureBaseUrl" auto-complete="new-password" style="width: 400px"/>
                    </el-form-item>

                    <el-form-item label="Minio连接地址" prop="qiNiuPictureBaseUrl">
                        <el-input v-model="form.minioEndPoint" auto-complete="new-password" style="width: 400px"/>
                    </el-form-item>

                    <el-form-item label="Minio公钥">
                        <el-input v-model="form.minioAccessKey" auto-complete="new-password" style="width: 400px"/>
                    </el-form-item>

                    <el-form-item label="Minio私钥">
                        <el-input v-model="form.minioSecretKey" type="password" auto-complete="new-password"
                                  style="width: 400px"/>
                    </el-form-item>

                    <el-form-item label="Minio上传空间">
                        <el-input v-model="form.minioBucket" style="width: 400px"/>
                    </el-form-item>

                    <el-form-item label="文件上传Minio">
                        <el-radio v-for="item in yesNoDictList" :key="item.uid" v-model="form.uploadMinio"
                                  :label="item.dictValue" border size="medium">{{ item.dictLabel }}
                        </el-radio>
                    </el-form-item>

                    <el-form-item>
                        <el-button v-permission="'/systemConfig/editSystemConfig'" type="primary" @click="submitForm()">
                            保 存
                        </el-button>
                    </el-form-item>
                </el-form>
            </el-tab-pane>

            <el-tab-pane v-permission="'/systemConfig/getSystemConfig'" name="six">
                <span slot="label"><i class="el-icon-edit"/> 邮箱配置</span>
                <el-form style="margin-left: 20px;" label-position="left" label-width="80px">

                    <aside>
                        邮箱配置主要用于配置网站消息的接收<br>
                        例如：友链申请、网站评论、网站反馈等，可以在系统配置处选择是否开启邮件通知<br>
                    </aside>

                    <el-form-item label="邮箱" prop="email">
                        <el-input v-model="form.email" style="width: 400px"/>
                    </el-form-item>

                    <el-form-item label="密码" prop="newPwd1">
                        <el-input v-model="form.emailPassword" type="password" style="width: 400px"/>
                    </el-form-item>

                    <el-form-item label="用户名" prop="newPwd2">
                        <el-input v-model="form.emailUserName" style="width: 400px"/>
                    </el-form-item>

                    <el-form-item label="SMTP地址" prop="newPwd2">
                        <el-input v-model="form.smtpAddress" style="width: 400px"/>
                    </el-form-item>

                    <el-form-item label="SMTP端口" prop="newPwd2">
                        <el-input v-model="form.smtpPort" style="width: 400px"/>
                    </el-form-item>

                    <el-form-item>
                        <el-button v-permission="'/systemConfig/editSystemConfig'" type="primary" @click="submitForm()">
                            保 存
                        </el-button>
                    </el-form-item>
                </el-form>
            </el-tab-pane>

            <el-tab-pane v-permission="'/systemConfig/cleanRedisByKey'" name="seven">
                <span slot="label"><i class="el-icon-edit"/> Redis管理</span>
                <el-form style="margin-left: 20px;" label-position="left" label-width="120px">

                    <aside>
                        Redis管理主要用于清空一些缓存数据<br>
                        用户首次部署时，可以使用清空全部，把Redis中的缓存一键清空<br>
                    </aside>

                    <el-form-item label="全部">
                        <el-row>
                            <el-col :span="6">
                                <el-button type="danger" size="small" @click="cleanRedis('ALL')">清空全部</el-button>
                            </el-col>
                        </el-row>
                    </el-form-item>

                    <el-form-item label="博客相关">
                        <el-row>
                            <el-col :span="3">
                                <el-button type="primary" size="small" @click="cleanRedis('BLOG_CLICK')">清空点击量
                                </el-button>
                            </el-col>

                            <el-col :span="3">
                                <el-button type="success" size="small" @click="cleanRedis('BLOG_PRAISE')">清空点赞量
                                </el-button>
                            </el-col>

                            <el-col :span="3">
                                <el-button type="info" size="small" @click="cleanRedis('BLOG_LEVEL')">清空推荐博客
                                </el-button>
                            </el-col>

                            <el-col :span="3">
                                <el-button type="warning" size="small" @click="cleanRedis('HOT_BLOG')">清空热门博客
                                </el-button>
                            </el-col>

                            <el-col :span="3">
                                <el-button type="danger" size="small" @click="cleanRedis('NEW_BLOG')">清空最新博客
                                </el-button>
                            </el-col>
                        </el-row>
                    </el-form-item>

                    <el-form-item label="分类和归档相关">
                        <el-row>
                            <el-col :span="3">
                                <el-button type="primary" size="small" @click="cleanRedis('MONTH_SET')">清空分类日期
                                </el-button>
                            </el-col>

                            <el-col :span="3">
                                <el-button type="success" size="small" @click="cleanRedis('BLOG_SORT_BY_MONTH')">
                                    清空分类数据
                                </el-button>
                            </el-col>

                            <el-col :span="3">
                                <el-button type="info" size="small" @click="cleanRedis('BLOG_SORT_CLICK')">
                                    清空分类点击量
                                </el-button>
                            </el-col>

                            <el-col :span="3">
                                <el-button type="warning" size="small" @click="cleanRedis('TAG_CLICK')">清空标签点击量
                                </el-button>
                            </el-col>
                        </el-row>
                    </el-form-item>

                    <el-form-item label="系统相关">
                        <el-row>
                            <el-col :span="3">
                                <el-button type="primary" size="small" @click="cleanRedis('REDIS_DICT_TYPE')">清空字典
                                </el-button>
                            </el-col>

                            <el-col :span="3">
                                <el-button type="success" size="small" @click="cleanRedis('ADMIN_VISIT_MENU')">
                                    清空角色访问菜单
                                </el-button>
                            </el-col>

                            <el-col :span="3">
                                <el-button type="info" size="small" @click="cleanRedis('userToken')">清空用户Token
                                </el-button>
                            </el-col>

                            <el-col :span="3">
                                <el-button type="warning" size="small" @click="cleanRedis('REQUEST_LIMIT')">
                                    清空接口请求限制
                                </el-button>
                            </el-col>
                        </el-row>
                    </el-form-item>
                </el-form>
            </el-tab-pane>

            <el-tab-pane label="仪表盘通知" name="third">
                <span slot="label"><i class="el-icon-edit"/> 仪表盘通知</span>
                <div class="editor-container">
                    <CKEditor v-if="form.editorModel == '0'" ref="editor" :content="form.dashboardNotification"
                              :height="500"/>
                    <Mavon v-if="form.editorModel == '1'" ref="editor" :height="660" style="margin-top: 12px"/>
                </div>
                <div style="margin-top: 5px; margin-left: 10px;">
                    <el-button v-permission="'/system/editMe'" type="primary" @click="submitForm()">保 存</el-button>
                </div>
            </el-tab-pane>

        </el-tabs>

    </div>
</template>

<script>
import {cleanRedisByKey, editSystemConfig, getSystemConfig} from '@/api/systemConfig'
import {getListByDictTypeList} from '@/api/sysDictData'

import CKEditor from '@/components/CKEditor'
import MarkdownEditor from '@/components/MarkdownEditor'
import Mavon from '@/components/Mavon'

export default {
    components: {
        CKEditor,
        MarkdownEditor,
        Mavon,
    },
    data() {
        return {
            form: {},
            index: '0', // 当前激活页
            activeName: 'one',
            areaDictList: [], // 存储区域字典
            yesNoDictList: [], // 是否字典
            openDictList: [], // 开启关闭字典
            picturePriorityDictList: [], // 图片显示优先级字典
            editorModalDictList: [], // 文本编辑器字典列表
            searchModelDictList: [], // 搜索模式字典列表
            loadingInstance: null, // loading对象
            rules: {
                localPictureBaseUrl: [
                    {pattern: /^((https|http|ftp|rtsp|mms)?:\/\/)[^\s]+/, message: '请输入正确的域名'}
                ],
                qiNiuPictureBaseUrl: [
                    {pattern: /^((https|http|ftp|rtsp|mms)?:\/\/)[^\s]+/, message: '请输入正确的域名'}
                ],
                email: [
                    {pattern: /\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/, message: '请输入正确的邮箱'}
                ]
            }
        }
    },
    watch: {},
    created() {
        // 获取字典
        this.getDictList()
        // 获取系统配置
        this.getSystemConfigList()
    },
    methods: {
        /**
         * 字典查询
         */
        getDictList: function () {
            const dictTypeList = ['sys_search_model', 'sys_yes_no', 'sys_picture_priority', 'sys_storage_region', 'sys_normal_disable', 'sys_editor_modal']

            getListByDictTypeList(dictTypeList).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    const dictMap = response.data
                    this.areaDictList = dictMap.sys_storage_region.list
                    this.yesNoDictList = dictMap.sys_yes_no.list
                    this.openDictList = dictMap.sys_normal_disable.list
                    this.picturePriorityDictList = dictMap.sys_picture_priority.list
                    this.editorModalDictList = dictMap.sys_editor_modal.list
                    this.searchModelDictList = dictMap.sys_search_model.list
                }
            })
        },
        handleClick(tab, event) {
            this.index = tab.index
            // 设置富文本内容
            if (this.form.dashboardNotification) {
                this.$refs.editor.setData(this.form.dashboardNotification)
            }
        },
        getSystemConfigList: function () {
            getSystemConfig().then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    if (response.data) {
                        this.form = response.data
                    }
                }
            })
        },
        cleanRedis: function (key) {
            const params = []
            params.push(key)
            cleanRedisByKey(params).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                    this.$commonUtil.message.success(response.message)
                } else {
                    this.$commonUtil.message.error(response.message)
                }
            })
        },
        submitForm: function () {
            this.$refs.form.validate((valid) => {
                if (!valid) {
                    console.log('校验出错')
                } else {
                    // 获取文本编辑器中的内容【只有在切换到仪表盘通知的时候，才需要获取】
                    debugger
                    if (this.index == '7') {
                        this.form.dashboardNotification = this.$refs.editor.getData()
                    }
                    editSystemConfig(this.form).then(res => {
                        if (res.code == this.$ECode.SUCCESS) {
                            this.$commonUtil.message.success(res.message)
                        } else {
                            this.$commonUtil.message.error(res.message)
                        }
                    })
                }
            })
        }

    }
}
</script>

<style lang="scss">
aside {
    background: #eef1f6;
    padding: 8px 24px;
    margin-bottom: 20px;
    border-radius: 2px;
    display: block;
    line-height: 32px;
    font-size: 16px;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen, Ubuntu, Cantarell, "Fira Sans", "Droid Sans", "Helvetica Neue", sans-serif;
    color: #2c3e50;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;

    a {
        color: #337ab7;
        cursor: pointer;

        &:hover {
            color: rgb(32, 160, 255);
        }
    }
}
</style>

