import Vue from 'vue'
import Router from 'vue-router'
import HomeIndex from '@/views/home'

Vue.use(Router)

export const constantRouterMap = [
  {
    path: '/',
    component: HomeIndex,
    children: [
      {path: '/', component: () => import('@/views/index')},
      {path: '/about', component: () => import('@/views/about')},
      {path: '/life', component: () => import('@/views/life')},
      {path: '/list', component: () => import('@/views/list')},
      {path: '/sort', component: () => import('@/views/sort')},
      {path: '/share', component: () => import('@/views/share')},
      {path: '/subject', component: () => import('@/views/special')},
      // { path: '/subject2', component: () => import('@/views/special') },
      {
        path: '/subject/:subjectUid',
        component: () => import('@/views/specialDetail')
      },
      {path: '/time', component: () => import('@/views/time')},
      {path: '/rank', component: () => import('@/views/rank')},
      {
        path: '/info/:blogOid',
        component: () => import('@/views/info')
      },
      {path: '/messageBoard', component: () => import('@/views/messageBoard')},
      {path: '/question', component: () => import('@/views/question')},
      {
        path: '/qInfo/:questionOid',
        component: () => import('@/views/questionInfo')
      },
      {path: '/code', component: () => import('@/views/code')},
      {
        path: '/cInfo/:problemOid',
        component: () => import('@/views/codeInfo')
      },
      {path: '/userCenter', component: () => import('@/views/userCenter')},
      {path: '/notice', component: () => import('@/views/notice')},
      {path: '/moment', component: () => import('@/views/moment')},
      // { path: '/chat2', component: () => import('@/views/chat') },
      {path: '/chat', component: () => import('@/views/chatRoom')},
      {path: '/media', component: () => import('@/views/mediaInfoList')},
      {path: '/video/:videoId?', component: () => import('@/views/mediaVideo')},
      {path: '/hot', component: () => import('@/views/hot')},
      {path: '/resource', component: () => import('@/views/resource')},
      {path: '/resource/:resourceUid', component: () => import('@/views/resourceDetail')},
      {path: '/medal/:userUid', component: () => import('@/views/medal')},
      {path: '/sponsor', component: () => import('@/views/sponsor')},
      {path: '/version', component: () => import('@/views/version')},
      {path: '/lucky', component: () => import('@/views/lucky')},
      {path: '/vip', component: () => import('@/views/vip')},
      {path: '/mall', component: () => import('@/views/mall')},
      {path: '/mall/:productUid', component: () => import('@/views/mallDetail')},
    ]
  },
  {path: '/404', component: () => import('@/views/404')},
  {path: '/403', component: () => import('@/views/403')},
  {path: '/500', component: () => import('@/views/500')},
  {path: '/502', component: () => import('@/views/502')},
  { path: '/success', component: () => import('@/views/success') },
  { path: '/error', component: () => import('@/views/error') },
  {path: '/*', component: () => import('@/views/404')}

]

const router = new Router({
  mode: 'history',
  routes: constantRouterMap
})

// router.beforeEach((to, from, next) => {
//   console.log("获取路径", to, from)
//   console.log("------------")
//   console.log(store.state.app.webNavbarList)
//   next()
// })

export default router
