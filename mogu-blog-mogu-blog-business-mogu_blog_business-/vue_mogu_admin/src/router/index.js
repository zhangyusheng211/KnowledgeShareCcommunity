import Vue from 'vue'
import Router from 'vue-router'
/* Layout */
import Layout from '../views/layout/Layout'

// in development-env not use lazy-loading, because lazy-loading too many pages will cause webpack hot update too slow. so only in production use lazy-loading;
// detail: https://panjiachen.github.io/vue-element-admin-site/#/lazy-loading

Vue.use(Router)

/**
 * hidden: true                   if `hidden:true` will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu, whatever its child routes length
 *                                if not set alwaysShow, only more than one route under the children
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noredirect           if `redirect:noredirect` will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    title: 'title'               the name show in submenu and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar,
  }
 **/
export const constantRouterMap = [
    {path: '/login', component: () => import('@/views/login/index'), hidden: true},
    {path: '/404', component: () => import('@/views/404'), hidden: true},
    {path: '/401', component: () => import('@/views/401'), hidden: true},

    {
        path: '/',
        component: Layout,
        redirect: '/dashboard',
        name: '首页',
        children: [{
            path: 'dashboard',
            component: () => import('@/views/dashboard/index'),
            meta: {title: '仪表盘', icon: 'dashboard'}
        }]
    },

    {
        path: '/blog',
        component: Layout,
        redirect: '/blog/blog',
        name: '博客管理',
        meta: {title: '博客管理', icon: 'edit'},
        children: [
            {
                path: 'blog',
                name: '博客管理',
                component: () => import('@/views/blog/blog'),
                meta: {title: '博客管理', icon: 'edit'}
            },
            {
                path: 'blogTag',
                name: '标签管理',
                component: () => import('@/views/blog/blogTag'),
                meta: {title: '标签管理', icon: 'tag'}
            },
            {
                path: 'blogSort',
                name: '分类管理',
                component: () => import('@/views/blog/blogSort'),
                meta: {title: '分类管理', icon: 'sort'}
            },
            {
                path: 'blogRecommend',
                name: '推荐管理',
                component: () => import('@/views/blog/blogRecommend'),
                meta: {title: '推荐管理', icon: 'sort'}
            },
            {
                path: 'collect',
                name: '收藏管理',
                component: () => import('@/views/blog/collect'),
                meta: {title: '收藏管理', icon: 'table'}
            },
            {
                path: 'subject',
                name: '专栏管理',
                component: () => import('@/views/blog/subject'),
                meta: {title: '专栏管理', icon: 'table'}
            },
            {
                path: 'subjectSort',
                name: '专栏分类',
                component: () => import('@/views/blog/subjectSort'),
                meta: {title: '专栏分类', icon: 'table'}
            },
            {
                path: 'subjectItem',
                name: '专栏列表',
                component: () => import('@/views/blog/subjectItem'),
                meta: {title: '专栏列表', icon: 'table'}
            }
        ]
    },

    {
        path: '/authority',
        component: Layout,
        redirect: '/authority/admin',
        name: '权限管理',
        meta: {title: '权限管理', icon: 'authority'},
        children: [
            {
                path: 'admin',
                name: '管理员管理',
                component: () => import('@/views/authority/admin'),
                meta: {title: '管理员管理', icon: 'user'}
            },
            {
                path: 'role',
                name: '角色管理',
                component: () => import('@/views/authority/role'),
                meta: {title: '角色管理', icon: 'peoples'}
            },
            {
                path: 'categoryMenu',
                name: '菜单管理',
                component: () => import('@/views/authority/categoryMenu'),
                meta: {title: '菜单管理', icon: 'authority'}
            },
            {
                path: 'button',
                name: '接口管理',
                component: () => import('@/views/authority/api'),
                meta: {title: '接口管理', icon: 'authority'}
            }
        ]
    },

    {
        path: '/resource',
        component: Layout,
        redirect: '/resource/resourceSort',
        name: '资源管理',
        meta: {title: '资源管理', icon: 'resource'},
        children: [
            {
                path: 'file',
                name: '网盘管理',
                component: () => import('@/views/resource/file/File'),
                meta: {title: '网盘管理', icon: 'table'}
            },
            {
                path: 'resourceSort',
                name: '分类管理',
                component: () => import('@/views/resource/resourceSort'),
                meta: {title: '分类管理', icon: 'table'}
            },
            {
                path: 'resource',
                name: '视频管理',
                component: () => import('@/views/resource/resource'),
                meta: {title: '视频管理', icon: 'table'}
            }
        ]
    },

    {
        path: '/message',
        component: Layout,
        redirect: '/message/comment',
        name: '消息管理',
        meta: {title: '消息管理', icon: 'message1'},
        children: [
            {
                path: 'push',
                name: '消息推送',
                component: () => import('@/views/message/messagePush'),
                meta: {title: '消息推送', icon: 'table'}
            },
            {
                path: 'chat',
                name: '聊天管理',
                component: () => import('@/views/message/chat'),
                meta: {title: '聊天管理', icon: 'table'}
            },
            {
                path: 'room',
                name: '房间管理',
                component: () => import('@/views/message/room'),
                meta: {title: '房间管理', icon: 'table'}
            },
            {
                path: 'comment',
                name: '评论管理',
                component: () => import('@/views/message/comment'),
                meta: {title: '评论管理', icon: 'table'}
            },
            {
                path: 'feedback',
                name: '反馈管理',
                component: () => import('@/views/message/feedback'),
                meta: {title: '反馈管理', icon: 'table'}
            }
        ]
    },

    {
        path: '/spider',
        component: Layout,
        redirect: '/spider/pictureSpider',
        name: '爬虫管理',
        meta: {title: '爬虫管理', icon: 'message1'},
        children: [
            {
                path: 'pictureSpider',
                name: '图片爬取',
                component: () => import('@/views/spider/pictureSpider'),
                meta: {title: '图片爬取', icon: 'table'}
            }
        ]
    },

    {
        path: '/picture',
        component: Layout,
        redirect: '/picture/pictureSort',
        name: '图片管理',
        meta: {title: '图片管理', icon: 'example'},
        children: [
            {
                path: 'pictureSort',
                name: '图片类别管理',
                component: () => import('@/views/picture/pictureSort'),
                meta: {title: '图片类别管理', icon: 'picture'}
            },
            {
                path: 'picture',
                name: '图片管理',
                hidden: true,
                component: () => import('@/views/picture/picture'),
                meta: {title: '图片管理', icon: 'picture'}
            },
            {
                path: 'file',
                name: '素材管理',
                hidden: true,
                component: () => import('@/views/picture/file'),
                meta: {title: '素材管理', icon: 'file'}
            }
        ]
    },

    {
        path: '/user',
        component: Layout,
        redirect: '/user/user',
        name: '用户管理',
        meta: {title: '用户管理', icon: 'user1'},
        children: [
            {
                path: 'user',
                name: '用户管理',
                component: () => import('@/views/user/user'),
                meta: {title: '用户管理', icon: 'table'}
            },
            {
                path: 'visitor',
                name: '游客管理',
                component: () => import('@/views/user/visitor'),
                meta: {title: '游客管理', icon: 'table'}
            }
        ]
    },

    {
        path: '/system',
        component: Layout,
        redirect: '/system/solrIndex',
        name: '系统管理',
        meta: {title: '系统管理', icon: 'system'},
        children: [
            {
                path: 'aboutMe',
                name: '关于我',
                component: () => import('@/views/system/aboutMe'),
                meta: {title: '关于我', icon: 'aboutMe'}
            },
            {
                path: 'blogLink',
                name: '友情链接',
                component: () => import('@/views/system/blogLink'),
                meta: {title: '友情链接', icon: 'blogLink'}
            },
            {
                path: 'sysDictType',
                name: '字典管理',
                component: () => import('@/views/system/SysDictType'),
                meta: {title: '字典管理', icon: 'web'}
            },
            {
                path: 'sysDictData',
                name: '字典数据',
                hidden: true, // 隐藏
                component: () => import('@/views/system/SysDictData'),
                meta: {title: '字典数据', icon: 'web'}
            },
            {
                path: 'webConfig',
                name: '网站配置',
                component: () => import('@/views/system/webConfig'),
                meta: {title: '网站配置', icon: 'web'}
            },
            {
                path: 'systemConfig',
                name: '系统配置',
                component: () => import('@/views/system/systemConfig'),
                meta: {title: '系统配置', icon: 'web'}
            },
            {
                path: 'sysParams',
                name: '参数配置',
                component: () => import('@/views/system/sysParams'),
                meta: {title: '参数配置', icon: 'web'}
            },
            {
                path: 'generalEdit',
                name: '勘误管理',
                component: () => import('@/views/system/generalEdit'),
                meta: {title: '勘误管理', icon: 'table'}
            },
            {
                path: 'report',
                name: '举报管理',
                component: () => import('@/views/system/report'),
                meta: {title: '举报管理', icon: 'web'}
            },
            {
                path: 'payConfig',
                name: '支付配置',
                component: () => import('@/views/system/PayConfig'),
                meta: {title: '支付配置', icon: 'web'}
            },
            {
                path: 'thirdLoginConfig',
                name: '第三方登录',
                component: () => import('@/views/system/ThirdLoginConfig'),
                meta: {title: '第三方登录', icon: 'web'}
            },
            {
                path: 'languageModelConfig',
                name: '语言大模型',
                component: () => import('@/views/system/LanguageModelConfig'),
                meta: {title: '语言大模型', icon: 'web'}
            },
            {
                path: 'secretConfig',
                name: '密钥配置',
                component: () => import('@/views/system/SecretConfig'),
                meta: {title: '密钥配置', icon: 'web'}
            },
            {
                path: 'vipConfig',
                name: '会员配置',
                component: () => import('@/views/system/VipConfig'),
                meta: {title: '会员配置', icon: 'web'}
            },
        ]
    },

    {
        path: '/log',
        component: Layout,
        redirect: '/log/log',
        name: '操作日志',
        meta: {title: '操作日志', icon: 'log'},
        children: [
            {
                path: 'log',
                name: '操作日志',
                component: () => import('@/views/log/log'),
                meta: {title: '操作日志', icon: 'log'}
            },
            {
                path: 'exceptionLog',
                name: '异常日志',
                component: () => import('@/views/log/exceptionLog'),
                meta: {title: '异常日志', icon: 'exception'}
            },
            {
                path: 'webVisit',
                name: '用户日志',
                component: () => import('@/views/log/webVisit'),
                meta: {title: '用户日志', icon: 'user1'}
            }
        ]
    },

    {
        path: '/restapi',
        component: Layout,
        redirect: '/restapi/adminRestApi',
        name: '接口管理',
        meta: {title: '接口管理', icon: 'restapi'},
        children: [
            {
                path: 'adminRestApi',
                name: 'Admin接口',
                component: () => import('@/views/restapi/adminRestApi'),
                meta: {title: 'Admin接口', icon: 'table'}
            },
            {
                path: 'pictureRestApi',
                name: 'Picture接口',
                component: () => import('@/views/restapi/pictureRestApi'),
                meta: {title: 'Picture接口', icon: 'table'}
            },
            {
                path: 'webRestApi',
                name: 'Web接口',
                component: () => import('@/views/restapi/webRestApi'),
                meta: {title: 'Web接口', icon: 'table'}
            },
            {
                path: 'searchRestApi',
                name: 'Search接口',
                component: () => import('@/views/restapi/searchRestApi'),
                meta: {title: 'Search接口', icon: 'table'}
            }
        ]
    },

    {
        path: '/monitor',
        component: Layout,
        redirect: '/monitor/springBootAdmin',
        name: '监控中心',
        meta: {title: '监控中心', icon: 'log'},
        children: [
            {
                path: 'OnlineAdmin',
                name: '在线用户',
                component: () => import('@/views/monitor/OnlineAdmin'),
                meta: {title: '在线用户', icon: 'log'}
            },
            {
                path: 'ServerMonitor',
                name: '服务器监控',
                component: () => import('@/views/monitor/ServerMonitor'),
                meta: {title: '服务器监控', icon: 'exception'}
            },
            {
                path: 'Solr',
                name: 'Solr',
                component: () => import('@/views/monitor/Solr'),
                meta: {title: 'Solr', icon: 'index'}
            },
            {
                path: 'ElasticSearch',
                name: 'ElasticSearch',
                component: () => import('@/views/monitor/ElasticSearch'),
                meta: {title: 'ElasticSearch', icon: 'exception'}
            },
        ]
    },

    {
        path: '/web',
        component: Layout,
        redirect: '/web/webNavbar',
        name: '门户管理',
        meta: {title: '门户管理', icon: 'user1'},
        children: [
            {
                path: 'webNavbar',
                name: '导航栏管理',
                component: () => import('@/views/web/webNavbar'),
                meta: {title: '导航栏管理', icon: 'table'}
            },
            {
                path: 'medal',
                name: '勋章管理',
                component: () => import('@/views/web/medal'),
                meta: {title: '勋章管理', icon: 'table'}
            },
            {
                path: 'medalClassify',
                name: '勋章分类',
                component: () => import('@/views/web/medalClassify'),
                meta: {title: '勋章分类', icon: 'table'}
            },
            {
                path: 'taskConfig',
                name: '任务管理',
                component: () => import('@/views/web/taskConfig'),
                meta: {title: '任务管理', icon: 'web'}
            },
        ]
    },

    {
        path: '/question',
        component: Layout,
        redirect: '/question/question',
        name: '问答管理',
        meta: {title: '问答管理', icon: 'table'},
        children: [
            {
                path: 'question',
                name: '问答管理',
                component: () => import('@/views/question/question'),
                meta: {title: '问答管理', icon: 'table'}
            },
            {
                path: 'questionTag',
                name: '问答标签',
                component: () => import('@/views/question/questionTag'),
                meta: {title: '问答标签', icon: 'table'}
            },
            {
                path: 'questionTemplate',
                name: '问答模板',
                component: () => import('@/views/question/questionTemplate'),
                meta: {title: '问答模板', icon: 'table'}
            }
        ]
    },

    {
        path: '/problem',
        component: Layout,
        redirect: '/problem/problem',
        name: '问题管理',
        meta: {title: '问题管理', icon: 'table'},
        children: [
            {
                path: 'problem',
                name: '问题管理',
                component: () => import('@/views/problem/problem'),
                meta: {title: '问题管理', icon: 'table'}
            },
            {
                path: 'problemTag',
                name: '问题标签',
                component: () => import('@/views/problem/problemTag'),
                meta: {title: '问题标签', icon: 'table'}
            }
        ]
    },

    {
        path: '/test',
        component: Layout,
        redirect: '/test/markdown',
        name: '测试页面',
        meta: {title: '测试页面', icon: 'log'},
        children: [
            {
                path: 'CropperPicture',
                name: 'CropperPicture',
                component: () => import('@/views/test/CropperPicture'),
                meta: {title: '图片裁剪', icon: 'log'}
            },
            {
                path: 'Markdown',
                name: 'Markdown',
                component: () => import('@/views/test/Markdown'),
                meta: {title: 'Markdown', icon: 'exception'}
            },
            {
                path: 'PictureList',
                name: 'PictureList',
                component: () => import('@/views/test/PictureList'),
                meta: {title: '图片列表', icon: 'exception'}
            },
            {
                path: 'FormBuild',
                name: 'FormBuild',
                component: () => import('@/views/test/build'),
                meta: {title: '表单构建', icon: 'exception'}
            }
        ]
    },

    {
        path: '/media',
        component: Layout,
        redirect: '/media/info',
        name: '课程管理',
        meta: {title: '课程管理', icon: 'user1'},
        children: [
            {
                path: 'info',
                name: '课程管理',
                component: () => import('@/views/media/info'),
                meta: {title: '课程管理', icon: 'table'}
            },
            {
                path: 'info/detail/add',
                name: '新增课程',
                component: (resolve) => require(['@/views/media/detail'], resolve),
                meta: {title: '新增课程', icon: 'table'}
            },
            {
                path: 'info/detail/:uid',
                name: 'mediaInfoDetail2',
                component: (resolve) => require(['@/views/media/detail'], resolve),
                meta: {title: '编辑课程', icon: 'table'}
            },
            {
                path: 'tag',
                name: 'mediaTag',
                component: (resolve) => require(['@/views/media/tag'], resolve),
                meta: {title: '课程标签', icon: 'table'}
            },
            {
                path: 'category',
                name: 'mediaCategory',
                component: (resolve) => require(['@/views/media/category'], resolve),
                meta: {title: '课程分类', icon: 'table'}
            },
            {
                path: 'actor',
                name: 'mediaActor',
                component: (resolve) => require(['@/views/media/actor'], resolve),
                meta: {title: '教师管理', icon: 'table'}
            }
        ]
    },

    {
        path: '/moment',
        component: Layout,
        redirect: '/moment/userMoment',
        name: '动态管理',
        meta: {title: '动态管理', icon: 'user1'},
        children: [
            {
                path: 'userMoment',
                name: '动态管理',
                component: () => import('@/views/moment/userMoment'),
                meta: {title: '动态管理', icon: 'table'}
            },
            {
                path: 'userMomentTopic',
                name: '话题管理',
                component: () => import('@/views/moment/userMomentTopic'),
                meta: {title: '话题管理', icon: 'table'}
            }
        ]
    },

    {
        path: '/wechat',
        component: Layout,
        redirect: '/wechat/WxMenu',
        name: '公众号管理',
        meta: {title: '公众号管理', icon: 'user1'},
        children: [
            {
                path: 'WxMenu',
                name: '菜单管理',
                component: () => import('@/views/wechat/wx-menu'),
                meta: {title: '菜单管理', icon: 'table'}
            },
            {
                path: 'WxAssets',
                name: '素材管理',
                component: () => import('@/views/wechat/wx-assets'),
                meta: {title: '素材管理', icon: 'table'}
            },
            {
                path: 'WxMsgReplyRule',
                name: '回复规则',
                component: () => import('@/views/wechat/msg-reply-rule'),
                meta: {title: '回复规则', icon: 'table'}
            },
            {
                path: 'WxMsgTemplate',
                name: '模板消息',
                component: () => import('@/views/wechat/msg-template'),
                meta: {title: '模板消息', icon: 'table'}
            },
            {
                path: 'WxQrcode',
                name: '带参二维码',
                component: () => import('@/views/wechat/wx-qrcode'),
                meta: {title: '带参二维码', icon: 'table'}
            },
            {
                path: 'WxUser',
                name: '粉丝管理',
                component: () => import('@/views/wechat/wx-user'),
                meta: {title: '菜单管理', icon: 'table'}
            },
            {
                path: 'WxMsg',
                name: '消息管理',
                component: () => import('@/views/wechat/wx-msg'),
                meta: {title: '消息管理', icon: 'table'}
            },
            {
                path: 'WxAccount',
                name: '账号管理',
                component: () => import('@/views/wechat/wx-account'),
                meta: {title: '账号管理', icon: 'table'}
            }
        ]
    },

    {
        path: '/order',
        component: Layout,
        redirect: '/order/order',
        name: '商品&订单',
        meta: { title: '商品&订单', icon: 'user' },
        children: [
            {
                path: 'order',
                name: '订单管理',
                component: () => import('@/views/order/order'),
                meta: { title: '订单管理', icon: 'table' }
            },
            {
                path: 'withdraw',
                name: '提现管理',
                component: () => import('@/views/order/withdraw'),
                meta: { title: '提现管理', icon: 'table' }
            },
            {
                path: 'orderAmountLog',
                name: '资金流水',
                component: () => import('@/views/order/orderAmountLog'),
                meta: { title: '资金流水', icon: 'table' }
            },
            {
                path: 'creditsLog',
                name: '积分流水',
                component: () => import('@/views/order/creditsLog'),
                meta: { title: '积分流水', icon: 'table' }
            },
            {
                path: 'product',
                name: '商品管理',
                component: () => import('@/views/order/product'),
                meta: { title: '商品管理', icon: 'table' }
            },
            {
                path: 'productCategory',
                name: '商品分类',
                component: () => import('@/views/order/productCategory'),
                meta: { title: '商品分类', icon: 'table' }
            },
        ]
    },

    {
        path: '/lucky',
        component: Layout,
        redirect: '/lucky/luckyActivity',
        name: '抽奖管理',
        meta: { title: '抽奖管理', icon: 'user' },
        children: [
            {
                path: 'luckyActivity',
                name: '活动管理',
                component: () => import('@/views/lucky/luckyActivity'),
                meta: { title: '活动管理', icon: 'table' }
            },
            {
                path: 'luckyRecord',
                name: '抽奖记录',
                component: () => import('@/views/lucky/luckyRecord'),
                meta: { title: '抽奖管理', icon: 'table' }
            },
            {
                path: 'luckyRule',
                name: '规则管理',
                component: () => import('@/views/lucky/luckyRule'),
                meta: { title: '抽奖规则', icon: 'table' }
            },
            {
                path: 'awardProduct',
                name: '奖品管理',
                component: () => import('@/views/lucky/awardProduct'),
                meta: { title: '抽奖规则', icon: 'table' }
            },
        ]
    },

    {path: '*', redirect: '/404', hidden: true}
]

const router = new Router({
    // mode: 'history', //后端支持可开
    scrollBehavior: () => ({y: 0}),
    routes: constantRouterMap
})

// 全局路由守卫
router.beforeEach((to, from, next) => {
    // to: Route: 即将要进入的目标 路由对象
    // from: Route: 当前导航正要离开的路由
    // next: Function: 一定要调用该方法来 resolve 这个钩子。执行效果依赖 next 方法的调用参数。

    // if (to.path === '/login') {
    //   next()
    // }
    // if (to.path === '/404') {
    //   next()
    // }

    // if (store.getters.menu.sonList) {
    //   let sonList = store.getters.menu.sonList
    //   console.log("我进来了", sonList);

    //   for (let a = 0; a < sonList.length; a++) {
    //     if (to.path === sonList[a].url) {
    //       next()
    //     }
    //   }
    //   next({ path: '/404' })
    // }
    next()
    // store.dispatch('FedLogOut').then(() => {
    //   next({ path: '/' })
    // })

    // else {
    //   store.dispatch('FedLogOut').then(() => {
    //     next({ path: '/' })
    //   })
    // }

    // if (to.path === '/login') {
    //   next({ path: '/' })
    // }

    // store.dispatch('GetMenu').then(response => {
    //   console.log("come");
    //   if(response.code == "success") {
    //     const sonList = response.data.sonList
    //     for (let a = 0; a < sonList.length; a++) {
    //       if (to.path === sonList[a].url) {
    //         next()
    //       }
    //     }
    //   }
    // })
    // next({ path: '/login' })

    // 未登录状态；当路由到nextRoute指定页时，跳转至login
})

export default router
