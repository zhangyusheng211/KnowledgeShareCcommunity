<template>
    <div class="app-container">

        <el-tabs type="border-card">
            <el-tab-pane v-permission="'/webConfig/getWebConfig'">
        <span slot="label">
          <i class="el-icon-date"></i> 网站信息
        </span>

                <el-form
                    style="margin-left: 20px;"
                    label-position="left"
                    :model="form"
                    label-width="120px"
                    ref="from"
                >

                    <el-form-item label="LOGO">
                        <el-upload
                            class="avatar-uploader"
                            name="file"
                            :action="uploadPictureHost"
                            :file-list="fileList"
                            :show-file-list="false"
                            :before-upload="beforeUpload"
                            :on-success="fileSuccess_logo"
                            :data="otherData"
                        >
                            <img v-if="form.logoPhoto" :src="form.logoPhoto" class="avatar uploadImgBody">
                            <div v-else class="uploadImgBody">
                                <i class="el-icon-plus avatar-uploader-icon"></i>
                            </div>
                        </el-upload>
                    </el-form-item>

                    <el-row :gutter="24">
                        <el-col :span="10">
                            <el-form-item label="网站名称" prop="oldPwd">
                                <el-input v-model="form.name" style="width: 400px"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="10">
                            <el-form-item label="标题" prop="newPwd1">
                                <el-input v-model="form.title" style="width: 400px"></el-input>
                            </el-form-item>
                        </el-col>
                    </el-row>

                    <el-row :gutter="24">
                        <el-col :span="10">
                            <el-form-item label="关键字" prop="newPwd2">
                                <el-input v-model="form.keyword" style="width: 400px"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="10">
                            <el-form-item label="描述" prop="newPwd1">
                                <el-input v-model="form.summary" style="width: 400px"></el-input>
                            </el-form-item>
                        </el-col>
                    </el-row>

                    <el-row :gutter="24">
                        <el-col :span="10">
                            <el-form-item label="作者" prop="newPwd2">
                                <el-input v-model="form.author" style="width: 400px"></el-input>
                            </el-form-item>
                        </el-col>

                        <!--            <el-col :span="10">-->
                        <!--              <el-form-item label="备案号" prop="newPwd2">-->
                        <!--                <el-input v-model="form.recordNum" style="width: 400px"></el-input>-->
                        <!--              </el-form-item>-->
                        <!--            </el-col>-->

                    </el-row>

                    <el-row :gutter="24">
                        <el-col :span="10">
                            <el-form-item label="登录方式">
                                <el-checkbox-group v-model="form.loginTypeList">
                                    <el-checkbox label="1" style="margin-left: 10px">账号密码</el-checkbox>
                                    <el-checkbox label="2" style="margin-left: 10px">码云</el-checkbox>
                                    <el-checkbox label="3" style="margin-left: 10px">Github</el-checkbox>
                                    <el-checkbox label="4" style="margin-left: 10px">QQ</el-checkbox>
                                    <el-checkbox label="5" style="margin-left: 10px">微信公众号</el-checkbox>
                                    <el-checkbox label="6" style="margin-left: 10px">微信小程序</el-checkbox>
                                    <el-checkbox label="7" style="margin-left: 10px">微信</el-checkbox>
                                </el-checkbox-group>
                            </el-form-item>
                        </el-col>
                    </el-row>


                    <el-row>
                        <el-form-item label="登录方式展开">
                            <template slot="label">
                                登录方式展开
                                <el-popover
                                    placement="top-start"
                                    width="200"
                                    trigger="hover"
                                    content="用于控制门户登录页是否默认展开更多登录方式">
                                    <i slot="reference" style="cursor: pointer;margin-left: 2px" class="el-icon-question"/>
                                </el-popover>
                            </template>
                            <el-radio v-for="item in yesNoDictList" :key="item.uid" v-model="form.spreadLoginType"
                                      :label="item.dictValue" border size="medium">{{ item.dictLabel }}
                            </el-radio>
                        </el-form-item>
                    </el-row>

                    <el-row :gutter="24">
                        <el-col :span="10">
                            <el-form-item label="登录优先级">
                                <template slot="label">
                                    登录优先级
                                    <el-popover
                                        placement="top-start"
                                        width="200"
                                        trigger="hover"
                                        content="用于控制门户登录页默认展示的登录方式">
                                        <i slot="reference" style="cursor: pointer;margin-left: 2px" class="el-icon-question"/>
                                    </el-popover>
                                </template>
                                <el-select
                                    v-model="form.loginPriority"
                                    style="width: 200px"
                                    clearable
                                    placeholder="请选择登录方式优先级"
                                    size="small"
                                >
                                    <el-option
                                        v-for="item in loginPriorityList"
                                        :key="item.type"
                                        :label="item.name"
                                        :value="item.type"
                                    />
                                </el-select>
                            </el-form-item>
                        </el-col>
                    </el-row>

                    <el-row :gutter="24">
                        <el-col :span="10">
                            <el-form-item label="聊天方式">
                                <template slot="label">
                                    聊天方式
                                    <el-popover
                                        placement="top-start"
                                        width="200"
                                        trigger="hover"
                                        content="用于控制群聊页面用户能发送的内容">
                                        <i slot="reference" style="cursor: pointer;margin-left: 2px" class="el-icon-question"/>
                                    </el-popover>
                                </template>
                                <el-checkbox-group v-model="form.chatTypeList">
                                    <el-checkbox label="1" style="margin-left: 10px">表情</el-checkbox>
                                    <el-checkbox label="2" style="margin-left: 10px">语音</el-checkbox>
                                    <el-checkbox label="3" style="margin-left: 10px">图片</el-checkbox>
                                    <el-checkbox label="4" style="margin-left: 10px">通话</el-checkbox>
                                    <el-checkbox label="5" style="margin-left: 10px">视频</el-checkbox>
                                </el-checkbox-group>
                            </el-form-item>
                        </el-col>
                    </el-row>

                    <el-row :gutter="24">
                        <el-col :span="10">
                            <el-form-item label="现金支付">
                                <el-checkbox-group v-model="form.cashPayMethodList">
                                    <el-checkbox :label="cashPayMethod.dictValue" style="margin-left: 10px" v-for="cashPayMethod in cashPayMethodList" :key="cashPayMethod.uid">{{cashPayMethod.dictLabel}}</el-checkbox>
                                </el-checkbox-group>
                            </el-form-item>
                        </el-col>
                    </el-row>

                    <el-row>
                        <!-- 是否开启注册用户投稿创作 -->
                        <el-form-item label="用户投稿">
                            <el-radio v-for="item in openDictList" :key="item.uid" v-model="form.openCreateBlog"
                                      :label="item.dictValue" border size="medium">{{ item.dictLabel }}
                            </el-radio>
                        </el-form-item>
                    </el-row>

                    <el-row>
                        <!-- 是否开启注册用户问答功能 -->
                        <el-form-item label="用户问答">
                            <el-radio v-for="item in openDictList" :key="item.uid" v-model="form.openCreateQuestion"
                                      :label="item.dictValue" border size="medium">{{ item.dictLabel }}
                            </el-radio>
                        </el-form-item>
                    </el-row>

                    <el-row>
                        <!-- 是否开启注册用户问答功能 -->
                        <el-form-item label="用户聊天">
                            <el-radio v-for="item in openDictList" :key="item.uid" v-model="form.openChat"
                                      :label="item.dictValue" border size="medium">{{ item.dictLabel }}
                            </el-radio>
                        </el-form-item>
                    </el-row>

                    <el-form-item>
                        <el-button type="primary" @click="submitForm()" v-permission="'/webConfig/editWebConfig'">保
                            存
                        </el-button>
                    </el-form-item>
                </el-form>
            </el-tab-pane>

            <el-tab-pane label="版权申明" v-permission="'/webConfig/getWebConfig'">
                <span slot="label"><i class="el-icon-edit"></i> 版权申明</span>
                <div class="editor-container">
                    <el-input
                        type="textarea"
                        :autosize="{ minRows: 10, maxRows: 15}"
                        placeholder="请输入版权申明"
                        v-model="form.recordNum">
                    </el-input>
                </div>
                <div style="margin-top: 5px; margin-left: 10px;">
                    <el-button type="primary" @click="submitForm()" v-permission="'/system/editMe'">保 存</el-button>
                </div>
            </el-tab-pane>

            <el-tab-pane v-permission="'/webConfig/getWebConfig'">
        <span slot="label">
          <i class="el-icon-date"></i> 评论&打赏
        </span>

                <el-form
                    style="margin-left: 20px;"
                    label-position="left"
                    :model="form"
                    label-width="90px"
                    ref="from"
                >
                    <el-row :gutter="24">
                        <el-col :span="4">
                            <el-form-item label="阿里支付">
                                <el-upload
                                    class="avatar-uploader"
                                    name="file"
                                    :action="uploadPictureHost"
                                    :file-list="fileList"
                                    :show-file-list="false"
                                    :before-upload="beforeUpload"
                                    :on-success="fileSuccess_ali"
                                    :data="otherData"
                                >
                                    <img v-if="form.aliPayPhoto" :src="form.aliPayPhoto" class="avatar uploadImgBody">
                                    <div v-else class="uploadImgBody">
                                        <i class="el-icon-plus avatar-uploader-icon"></i>
                                    </div>
                                </el-upload>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item label="微信支付">
                                <el-upload
                                    class="avatar-uploader"
                                    name="file"
                                    :action="uploadPictureHost"
                                    :file-list="fileList"
                                    :show-file-list="false"
                                    :before-upload="beforeUpload"
                                    :on-success="fileSuccess_weixin"
                                    :data="otherData"
                                >
                                    <img v-if="form.weixinPayPhoto" :src="form.weixinPayPhoto"
                                         class="avatar uploadImgBody">
                                    <div v-else class="uploadImgBody">
                                        <i class="el-icon-plus avatar-uploader-icon"></i>
                                    </div>
                                </el-upload>
                            </el-form-item>
                        </el-col>
                    </el-row>

                    <el-row :gutter="24">
                        <el-col :span="6">
                            <el-form-item label="网站评论">
                                <el-radio v-for="item in openDictList" :key="item.uid" v-model="form.openComment"
                                          :label="item.dictValue" border size="medium">{{ item.dictLabel }}
                                </el-radio>
                            </el-form-item>
                        </el-col>
                    </el-row>

                    <el-row :gutter="24">
                        <el-col :span="6">
                            <el-form-item label="网站打赏">
                                <el-radio v-for="item in openDictList" :key="item.uid" v-model="form.openAdmiration"
                                          :label="item.dictValue" border size="medium">{{ item.dictLabel }}
                                </el-radio>
                            </el-form-item>
                        </el-col>
                    </el-row>

<!--                    <el-row :gutter="24">-->
<!--                        <el-col :span="6">-->
<!--                            <el-form-item label="移动端评论">-->
<!--                                <el-radio v-for="item in openDictList" :key="item.uid" v-model="form.openMobileComment"-->
<!--                                          :label="item.dictValue" border size="medium">{{ item.dictLabel }}-->
<!--                                </el-radio>-->
<!--                            </el-form-item>-->
<!--                        </el-col>-->
<!--                    </el-row>-->

<!--                    <el-row :gutter="24">-->
<!--                        <el-col :span="6">-->
<!--                            <el-form-item label="移动端打赏">-->
<!--                                <el-radio v-for="item in openDictList" :key="item.uid"-->
<!--                                          v-model="form.openMobileAdmiration" :label="item.dictValue" border-->
<!--                                          size="medium">{{ item.dictLabel }}-->
<!--                                </el-radio>-->
<!--                            </el-form-item>-->
<!--                        </el-col>-->
<!--                    </el-row>-->

                    <el-form-item>
                        <el-button type="primary" @click="submitForm()" v-permission="'/webConfig/editWebConfig'">保
                            存
                        </el-button>
                    </el-form-item>
                </el-form>

            </el-tab-pane>

            <el-tab-pane v-permission="'/webConfig/getWebConfig'">
        <span slot="label">
          <i class="el-icon-date"></i> 微信相关
        </span>

                <el-form
                    style="margin-left: 20px;"
                    label-position="left"
                    :model="form"
                    label-width="150px"
                    ref="from"
                >

                    <el-row :gutter="24">
                        <el-col>
                            <el-form-item label="公众号登录二维码">
                                <el-upload
                                    class="avatar-uploader"
                                    name="file"
                                    :action="uploadPictureHost"
                                    :file-list="fileList"
                                    :show-file-list="false"
                                    :before-upload="beforeUpload"
                                    :on-success="fileSuccess_wechat"
                                    :data="otherData"
                                >
                                    <img v-if="form.wechatPhoto" :src="form.wechatPhoto" class="avatar uploadImgBody">
                                    <div v-else class="uploadImgBody">
                                        <i class="el-icon-plus avatar-uploader-icon"></i>
                                    </div>
                                </el-upload>
                            </el-form-item>
                        </el-col>
                    </el-row>

                    <el-row :gutter="24">
                        <el-col>
                            <el-form-item label="小程序登录二维码">
                                <template slot="label">
                                    小程序登录二维码
                                    <el-popover
                                        placement="top-start"
                                        width="200"
                                        trigger="hover"
                                        content="用于小程序绑定的时候使用，用户扫码后会自动跳转到审核通过的小程序。">
                                        <i slot="reference" style="cursor: pointer;margin-left: 2px" class="el-icon-question"/>
                                    </el-popover>
                                </template>
                                <el-upload
                                    class="avatar-uploader"
                                    name="file"
                                    :action="uploadPictureHost"
                                    :file-list="fileList"
                                    :show-file-list="false"
                                    :before-upload="beforeUpload"
                                    :on-success="fileSuccess_mini"
                                    :data="otherData"
                                >
                                    <img v-if="form.miniPhoto" :src="form.miniPhoto" class="avatar uploadImgBody">
                                    <div v-else class="uploadImgBody">
                                        <i class="el-icon-plus avatar-uploader-icon"></i>
                                    </div>
                                </el-upload>
                            </el-form-item>
                        </el-col>
                    </el-row>

                    <el-row :gutter="24">
                        <el-col>
                            <el-form-item label="加载校验二维码">
                                <template slot="label">
                                    加载校验二维码
                                    <el-popover
                                        placement="top-start"
                                        width="200"
                                        trigger="hover"
                                        content="博客文章开启验证阅读后，会展示此二维码引导用户扫描微信公众号回复，获取到验证码后继续阅读。">
                                        <i slot="reference" style="cursor: pointer;margin-left: 2px" class="el-icon-question"/>
                                    </el-popover>
                                </template>
                                <el-upload
                                    class="avatar-uploader"
                                    name="file"
                                    :action="uploadPictureHost"
                                    :file-list="fileList"
                                    :show-file-list="false"
                                    :before-upload="beforeUpload"
                                    :on-success="fileSuccess_loading"
                                    :data="otherData"
                                >
                                    <img v-if="form.loadingValidPhoto" :src="form.loadingValidPhoto"
                                         class="avatar uploadImgBody">
                                    <div v-else class="uploadImgBody">
                                        <i class="el-icon-plus avatar-uploader-icon"></i>
                                    </div>
                                </el-upload>
                            </el-form-item>
                        </el-col>
                    </el-row>

                    <el-form-item label="加载校验样式代码">
                        <el-input
                            type="textarea"
                            :autosize="{ minRows: 10, maxRows: 15}"
                            placeholder="请输入加载校验html信息"
                            v-model="form.loadingValidText">
                        </el-input>
                    </el-form-item>

                    <el-form-item label="开启加载校验">
                        <template slot="label">
                            开启加载校验
                            <el-popover
                                placement="top-start"
                                width="200"
                                trigger="hover"
                                content="加载校验主要用于引导用户关注公众号，配合文章验证阅读使用">
                                <i slot="reference" style="cursor: pointer;margin-left: 2px" class="el-icon-question"/>
                            </el-popover>
                        </template>
                        <el-radio v-for="item in openDictList" :key="item.uid" v-model="form.openLoadingValid"
                                  :label="item.dictValue" border size="medium">{{ item.dictLabel }}
                        </el-radio>
                    </el-form-item>

                    <el-row :gutter="24">
                        <el-col :span="6">
                            <el-form-item label="小程序审核完成">
                                <template slot="label">
                                    小程序审核完成
                                    <el-popover
                                        placement="top-start"
                                        width="200"
                                        trigger="hover"
                                        content="资讯类小程序个人无法通过审核，因此增加开关控制；开关关闭说明是小程序处于送审流程，小程序会单独展示一个页面【只针对近期注册的用户展示，其它老用户看到的还是全功能页面】；开关开启后小程序会全功能正常展示">
                                        <i slot="reference" style="cursor: pointer;margin-left: 2px" class="el-icon-question"/>
                                    </el-popover>
                                </template>
                                <el-radio v-for="item in openDictList" :key="item.uid" v-model="form.openMobileComment"
                                          :label="item.dictValue" border size="medium">{{ item.dictLabel }}
                                </el-radio>
                            </el-form-item>
                        </el-col>
                    </el-row>


                    <el-form-item>
                        <el-button type="primary" @click="submitForm()" v-permission="'/webConfig/editWebConfig'">保
                            存
                        </el-button>
                    </el-form-item>
                </el-form>

            </el-tab-pane>

            <el-tab-pane v-permission="'/webConfig/getWebConfig'">
        <span slot="label">
          <i class="el-icon-date"></i> 关注我们
        </span>

                <el-form
                    style="margin-left: 20px;"
                    label-position="left"
                    :model="form"
                    label-width="80px"
                    :rules="rules"
                    ref="form"
                >
                    <el-checkbox-group v-model="form.showList">

                        <el-form-item label="邮箱" prop="email">
                            <el-input v-model="form.email" style="width: 400px"></el-input>
                            <el-checkbox label="1" style="margin-left: 10px">在首页显示</el-checkbox>
                        </el-form-item>

                        <el-form-item label="QQ号" prop="qqNumber">
                            <el-input v-model="form.qqNumber" style="width: 400px"></el-input>
                            <el-checkbox label="2" style="margin-left: 10px">在首页显示</el-checkbox>
                        </el-form-item>

                        <el-form-item label="QQ群" prop="qqGroup">
                            <el-input v-model="form.qqGroup" style="width: 400px"></el-input>
                            <el-checkbox label="3" style="margin-left: 10px">在首页显示</el-checkbox>
                        </el-form-item>

                        <el-form-item label="github" prop="github">
                            <el-input v-model="form.github" style="width: 400px"></el-input>
                            <el-checkbox label="4" style="margin-left: 10px">在首页显示</el-checkbox>
                        </el-form-item>

                        <el-form-item label="Gitee" prop="gitee">
                            <el-input v-model="form.gitee" style="width: 400px"></el-input>
                            <el-checkbox label="5" style="margin-left: 10px">在首页显示</el-checkbox>
                        </el-form-item>

                        <el-form-item label="微信" prop="weChat">
                            <el-input v-model="form.weChat" style="width: 400px"></el-input>
                            <el-checkbox label="6" style="margin-left: 10px">在首页显示</el-checkbox>
                        </el-form-item>

                    </el-checkbox-group>

                    <el-form-item>
                        <el-button type="primary" @click="submitForm()" v-permission="'/webConfig/editWebConfig'">保
                            存
                        </el-button>
                    </el-form-item>

                </el-form>
            </el-tab-pane>

            <el-tab-pane label="友链申请模板" v-permission="'/webConfig/getWebConfig'">
                <span slot="label"><i class="el-icon-edit"></i> 友链申请模板</span>
                <el-input
                    type="textarea"
                    :autosize="{ minRows: 10, maxRows: 15}"
                    placeholder="请输入友链申请的模板信息"
                    v-model="form.linkApplyTemplate">
                </el-input>
                <div style="margin-top: 5px; margin-left: 10px;">
                    <el-button type="primary" @click="submitForm()" v-permission="'/system/editMe'">保 存</el-button>
                </div>
            </el-tab-pane>

            <el-tab-pane label="门户模块" v-permission="'/webConfig/getWebConfig'">
                <span slot="label"><i class="el-icon-edit"></i> 页面模块</span>

                <aside>
                    用于控制门户页面模块展示和隐藏<br>
                </aside>

                <div>
                    <el-checkbox-group v-model="form.componentShowList">
                        <div style="margin-top: 10px">
                            <el-checkbox label="1" style="margin-left: 10px">文章分类</el-checkbox>
                            <el-checkbox label="2" style="margin-left: 10px">用户卡片</el-checkbox>
                            <el-checkbox label="3" style="margin-left: 10px">任务广场</el-checkbox>
                            <el-checkbox label="4" style="margin-left: 10px">轮播图/右侧推荐</el-checkbox>
                        </div>
                        <div style="margin-top: 10px">
                            <el-checkbox label="5" style="margin-left: 10px">用户签到</el-checkbox>
                            <el-checkbox label="6" style="margin-left: 10px">用户排行</el-checkbox>
                            <el-checkbox label="7" style="margin-left: 10px">热门标签</el-checkbox>
                            <el-checkbox label="8" style="margin-left: 10px">特别推荐</el-checkbox>
                        </div>
                        <div style="margin-top: 10px">
                            <el-checkbox label="9" style="margin-left: 10px">推荐文章</el-checkbox>
                            <el-checkbox label="10" style="margin-left: 10px">点击排行</el-checkbox>
                            <el-checkbox label="11" style="margin-left: 10px">关注我们</el-checkbox>
                            <el-checkbox label="12" style="margin-left: 10px">私信/群聊</el-checkbox>
                        </div>
                        <div style="margin-top: 10px">
                            <el-checkbox label="13" style="margin-left: 10px">暗黑模式</el-checkbox>
                            <el-checkbox label="14" style="margin-left: 10px">用户评论</el-checkbox>
                            <el-checkbox label="15" style="margin-left: 10px">友情链接</el-checkbox>
                            <el-checkbox label="16" style="margin-left: 10px">系统通知</el-checkbox>
                            <el-checkbox label="17" style="margin-left: 10px">内容搜索</el-checkbox>
                            <el-checkbox label="30" style="margin-left: 10px">会员模块</el-checkbox>
                        </div>
                        <div style="margin-top: 10px">
                            <el-checkbox label="18" style="margin-left: 10px">创建按钮</el-checkbox>
                            <el-checkbox label="19" style="margin-left: 10px">创建文章</el-checkbox>
                            <el-checkbox label="20" style="margin-left: 10px">上传文章</el-checkbox>
                            <el-checkbox label="21" style="margin-left: 10px">发布问答</el-checkbox>
                            <el-checkbox label="22" style="margin-left: 10px">发布面经</el-checkbox>
                            <el-checkbox label="23" style="margin-left: 10px">发表动态</el-checkbox>
                        </div>

                        <div style="margin-top: 10px">
                            <el-checkbox label="24" style="margin-left: 10px">用户收藏</el-checkbox>
                            <el-checkbox label="25" style="margin-left: 10px">用户点赞</el-checkbox>
                            <el-checkbox label="26" style="margin-left: 10px">用户点踩</el-checkbox>
                            <el-checkbox label="27" style="margin-left: 10px">用户举报</el-checkbox>
                            <el-checkbox label="28" style="margin-left: 10px">删除评论</el-checkbox>
                            <el-checkbox label="29" style="margin-left: 10px">内容勘误</el-checkbox>
                        </div>

                    </el-checkbox-group>
                </div>


                <div style="margin-top: 20px; margin-left: 10px;">
                    <el-button type="primary" @click="submitForm()" v-permission="'/system/editMe'">保 存</el-button>
                </div>
            </el-tab-pane>

        </el-tabs>

    </div>
</template>

<script>
import {getToken} from '@/utils/auth'
import {editWebConfig, getWebConfig} from "@/api/webConfig";
import {Loading} from 'element-ui';
import {getListByDictTypeList} from '@/api/sysDictData'
import {getSystemConfig} from "@/api/systemConfig";
import CKEditor from "@/components/CKEditor";
import MarkdownEditor from "@/components/MarkdownEditor";

export default {
    data() {
        return {
            form: {
                name: "",
                title: "",
                keyword: "",
                summary: "",
                author: "",
                logo: "",
                recordNum: "",
                openComment: "1",
                aliPay: "",
                weixinPay: "",
                aliPayPhoto: "",
                weixinPayPhoto: "",
                showList: [],
                componentShowList: [],
                loginTypeList: [],
            },
            loginPriorityList: [
                {
                    type: "1",
                    name: "账号密码"
                },
                {
                    type: "5",
                    name: "微信公众号"
                },
                {
                    type: "7",
                    name: "微信"
                },
            ],
            systemConfig: {},
            loadingInstance: null, // loading对象
            fileList: [],
            photoVisible: false, //控制图片选择器的显示
            fileIds: "",
            icon: false, //控制删除图标的显示
            otherData: {},
            openDictList: [], //字典
            yesNoDictList: [], // 是否字典
            cashPayMethodList: [], //现金支付方式列表
            isFirstPhotoVisible: true, // 图片选择器是否首次显示【用于懒加载】
            rules: {
                qqNumber: [
                    {pattern: /[1-9]([0-9]{5,11})/, message: '请输入正确的QQ号码'},
                ],
                qqGroup: [
                    {pattern: /-?[1-9]\d*/, message: '请输入正确的QQ群'},
                ],
                gitee: [
                    {pattern: /^((https|http|ftp|rtsp|mms)?:\/\/)[^\s]+/, message: '请输入正确的Gitee地址'},
                ],
                github: [
                    {pattern: /^((https|http|ftp|rtsp|mms)?:\/\/)[^\s]+/, message: '请输入正确的Github地址'},
                ],
                email: [
                    {pattern: /\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/, message: '请输入正确的邮箱'},
                ]
            }
        };
    },
    watch: {
        "form.aliPay": {
            handler(newVal, oldVal) {
                console.log("value change", oldVal, newVal);
            },
            deep: true
        },
        "form.weixinPay": {
            handler(newVal, oldVal) {
                console.log("value change", oldVal, newVal);
            },
            deep: true
        }
    },
    components: {
        CKEditor,
        MarkdownEditor
    },
    created() {
        // 获取配置
        this.getWebConfigFun();

        //图片上传地址
        this.uploadPictureHost = process.env.PICTURE_API + "/file/cropperPicture";
        // 查询字典
        this.getDictList()
        //其它数据
        this.otherData = {
            source: "picture",
            userUid: "uid00000000000000000000000000000000",
            adminUid: "uid00000000000000000000000000000000",
            projectName: "blog",
            sortName: "admin",
            token: getToken()
        };

        // 获取系统配置
        this.getSystemConfigList();
    },
    methods: {
        /**
         * 字典查询
         */
        getDictList: function () {

            const dictTypeList = [
                'sys_normal_disable',
                'sys_yes_no',
                'cash_pay_method',
            ]

            getListByDictTypeList(dictTypeList).then((response) => {
                if (response.code == this.$ECode.SUCCESS) {
                    const dictMap = response.data
                    this.openDictList = dictMap.sys_normal_disable.list
                    this.yesNoDictList = dictMap.sys_yes_no.list
                    this.cashPayMethodList = dictMap.cash_pay_method.list
                }
            })
        },
        getWebConfigFun: function () {
            getWebConfig().then(response => {
                if (response.code === this.$ECode.SUCCESS) {
                    let data = response.data;
                    if (data.showList) {
                        let showList = JSON.parse(data.showList)
                        let loginTypeList = JSON.parse(data.loginTypeList)
                        let chatTypeList = JSON.parse(data.chatTypeList)
                        let cashPayMethodList = JSON.parse(data.cashPayMethodList)
                        let componentShowList = JSON.parse(data.componentShowList)

                        data.showList = showList;
                        data.componentShowList = componentShowList;
                        data.loginTypeList = loginTypeList;
                        data.chatTypeList = chatTypeList;
                        data.cashPayMethodList = cashPayMethodList;
                        this.form = data;
                    } else {
                        data.showList = []
                        data.componentShowList   = []
                        this.form = data;
                    }
                    this.fileIds = this.form.logo;
                }
            });
        },
        // 获取系统配置
        getSystemConfigList: function () {
            getSystemConfig().then(response => {
                if (response.code === this.$ECode.SUCCESS) {
                    this.systemConfig = response.data;
                }
            });
        },
        submitForm: function () {
            let form = this.form;
            form.showList = JSON.stringify(this.form.showList)
            form.componentShowList = JSON.stringify(this.form.componentShowList)
            form.loginTypeList = JSON.stringify(this.form.loginTypeList)
            form.chatTypeList = JSON.stringify(this.form.chatTypeList)
            form.cashPayMethodList = JSON.stringify(this.form.cashPayMethodList)
            console.log("转化一下", form)
            editWebConfig(form).then(response => {
                if ((response.code = "success")) {
                    this.$notify({
                        title: "成功",
                        message: response.message,
                        type: "success"
                    });
                } else {
                    this.$notify.error({
                        title: "警告",
                        message: response.message
                    });
                }
                this.getWebConfigFun();
            });
        },

        beforeUpload: function (file) {
            this.loadingInstance = Loading.service({fullscreen: true, text: '正在努力上传中~'});
        },
        fileSuccess_loading: function (response, file, fileList) {
            if (response.uploaded === 1) {
                this.form.loadingValidFileUid = response.uid;
                this.form.loadingValidPhoto = response.url;
                let tempForm = this.form;
                this.form = {};
                this.form = tempForm;
                this.$commonUtil.message.success("上传成功")
            } else {
                this.$commonUtil.message.success(response.error.message)
            }
            this.loadingInstance.close();
        },
        fileSuccess_ali: function (response, file, fileList) {
            this.form.aliPay = response.uid;
            this.form.aliPayPhoto = response.url;
            let tempForm = this.form;
            this.form = {};
            this.form = tempForm;
            this.$commonUtil.message.success("上传成功")
            this.loadingInstance.close();
        },
        fileSuccess_logo: function (response, file, fileList) {
            this.form.logo = response.uid;
            console.log("上传logo", this.form)
            this.form.logoPhoto = response.url;
            let tempForm = this.form;
            this.form = {};
            this.form = tempForm;
            this.$commonUtil.message.success("上传成功")
            this.loadingInstance.close();
        },
        fileSuccess_weixin: function (response, file, fileList) {
            this.form.weixinPay = response.uid;
            this.form.weixinPayPhoto = response.url;
            let tempForm = this.form;
            this.form = {};
            this.form = tempForm;
            this.$commonUtil.message.success("上传成功")
            this.loadingInstance.close();
        },
        // 微信二维码
        fileSuccess_wechat: function (response, file, fileList) {
            this.form.wechatFileUid = response.uid;
            this.form.wechatPhoto = response.url;
            let tempForm = this.form;
            this.form = {};
            this.form = tempForm;
            this.$commonUtil.message.success("上传成功")
            this.loadingInstance.close();
        },

        fileSuccess_mini: function (response, file, fileList) {
            this.form.miniFileUid = response.uid;
            this.form.miniPhoto = response.url;
            let tempForm = this.form;
            this.form = {};
            this.form = tempForm;
            this.$commonUtil.message.success("上传成功")
            this.loadingInstance.close();
        },


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

img {
    width: 100px;
    height: 100px;
}
</style>

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
