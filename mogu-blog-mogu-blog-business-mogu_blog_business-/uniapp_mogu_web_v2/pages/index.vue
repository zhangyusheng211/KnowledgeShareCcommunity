<template>
	<view>
		<view v-if="vuex_custom_mini == '1'">
			<view v-if="tabberPageLoadFlag[0]" :style="{display: currentIndex === 0 ? '' : 'none'}">
				<scroll-view class="custom-tabbar-page" scroll-y enable-back-to-top>
					<home ref="home"></home>
				</scroll-view>
			</view>
			<view v-if="tabberPageLoadFlag[1]" :style="{display: currentIndex === 1 ? '' : 'none'}">
				<scroll-view class="custom-tabbar-page" scroll-y enable-back-to-top
					@scrolltolower="tabbarPageScrollLower">
					<Moment ref="moment"></Moment>
				</scroll-view>
			</view>
			<view v-if="tabberPageLoadFlag[2]" :style="{display: currentIndex === 2 ? '' : 'none'}">
				<scroll-view class="custom-tabbar-page" scroll-y enable-back-to-top
					@scrolltolower="tabbarPageScrollLower" refresher="onPullDownRefresh">
					<problem ref="problem"></problem>
				</scroll-view>
			</view>
			<view v-if="tabberPageLoadFlag[3]" :style="{display: currentIndex === 3 ? '' : 'none'}">
				<scroll-view class="custom-tabbar-page" scroll-y enable-back-to-top
					@scrolltolower="tabbarPageScrollLower">
					<preferred ref="subject"></preferred>
				</scroll-view>
			</view>
			<view v-if="tabberPageLoadFlag[4]" :style="{display: currentIndex === 4 ? '' : 'none'}">
				<scroll-view class="custom-tabbar-page" scroll-y enable-back-to-top
					@scrolltolower="tabbarPageScrollLower">
					<mine ref="mine"></mine>
				</scroll-view>
			</view>

			<tn-tabbar v-model="currentIndex" :list="tabbarList" activeColor="#838383" inactiveColor="#AAAAAA"
				activeIconColor="#FBBD12" :animation="true" :safeAreaInsetBottom="true" @change="switchTabbar">
			</tn-tabbar>
		</view>

		<view v-if="vuex_custom_mini == '0'">
			<view v-if="tabberPageLoadFlag[0]" :style="{display: currentIndex === 0 ? '' : 'none'}">
				<scroll-view class="custom-tabbar-page" scroll-y enable-back-to-top
					@scrolltolower="tabbarPageScrollLower">
					<problem ref="problem"></problem>
				</scroll-view>
			</view>
			<view v-if="tabberPageLoadFlag[1]" :style="{display: currentIndex === 1 ? '' : 'none'}">
				<scroll-view class="custom-tabbar-page" scroll-y enable-back-to-top
					@scrolltolower="tabbarPageScrollLower">
					<mine ref="mine"></mine>
				</scroll-view>
			</view>

			<tn-tabbar v-model="currentIndex" :list="tabbarList2" activeColor="#838383" inactiveColor="#AAAAAA"
				activeIconColor="#FBBD12" :animation="true" :safeAreaInsetBottom="true" @change="switchTabbar">
			</tn-tabbar>
		</view>

	</view>
</template>

<script>
	import Home from './home/home.vue'
	import Media from './media/media.vue'
	import Moment from './moment/moment.vue'
	import Problem from './problem/problem.vue'
	import Preferred from './subject/subject.vue'
	import Mine from './mine/mine.vue'
	import {
		authVerify,
	} from '../api/user.js'
	import {
		tokenUtil
	} from '../utils/token.js'
	import {
		getWebConfig
	} from '../api/about.js'

	export default {
		components: {
			Home,
			Media,
			Moment,
			Problem,
			Preferred,
			Mine
		},
		watch: {
			$route(to, from) {
				console.log("路由变化", to)
				let index = to.query.index
				if (index) {
					window.location.reload()
					// this._switchTabbarPage(index)
				}
			},
		},
		data() {
			return {
				// 底部tabbar菜单数据
				tabbarList: [{
						title: '首页',
						activeIcon: 'home-fill',
						inactiveIcon: 'home'
					},
					{
						title: '动态',
						activeIcon: '/static/tabbar/circle_tnnew.png',
						inactiveIcon: '/static/tabbar/circle_tn.png'
					},
					{
						title: '刷题',
						activeIcon: 'level-fill',
						inactiveIcon: 'level',
						activeIconColor: '#FFFFFF',
						inactiveIconColor: '#FFFFFF',
						iconSize: 50,
						out: true
					},
					{
						title: '专栏',
						activeIcon: 'bookmark-fill',
						inactiveIcon: 'bookmark',
					},
					{
						title: '我的',
						activeIcon: 'my-fill',
						inactiveIcon: 'my'
					},
				],
				tabbarList2: [{
						title: '刷题',
						activeIcon: 'level-fill',
						inactiveIcon: 'level',
						activeIconColor: '#FFFFFF',
						inactiveIconColor: '#FFFFFF',
						iconSize: 50,
						out: true
					},

					{
						title: '我的',
						activeIcon: 'my-fill',
						inactiveIcon: 'my'
					},
				],
				// tabbar当前被选中的序号
				currentIndex: 0,
				// 自定义底栏对应页面的加载情况
				tabberPageLoadFlag: [],
			}
		},
		onLoad(options) {
			const index = Number(options.index || 0)
			// 根据底部tabbar菜单列表设置对应页面的加载情况
			this.tabberPageLoadFlag = this.tabbarList.map((item, tabbar_index) => {
				return index === tabbar_index
			})
			this.switchTabbar(index)
			this.$forceUpdate()
			// 判断是否是H5，从url中获取token参数
			// #ifdef H5
			let token = this.getUrllets()["token"];
			console.log("判断是否获取到了token", token)
			// 如果携带了token，尝试获取信息
			if (token) {
				this.getUserInfo(token)
			}
			// #endif
		},
		methods: {
			getUrllets: function() {
				let lets = {};
				let url = window.location.href;
				url = decodeURI(url)
				let parts = url.replace(
					/[?&]+([^=&]+)=([^&#]*)/gi,
					function(m, key, value) {
						lets[key] = value;
					}
				);
				return lets;
			},
			// 跳转
			tn(e) {
				console.log("切换URL:", e)
				uni.navigateTo({
					url: e,
				});
			},
			getUserInfo(token) {
				let that = this
				authVerify(token).then((res) => {
					console.log("根据token获取用户信息", res)
					if (res.code == that.$ECode.SUCCESS) {
						console.log("设置token成功", token)
						// 设置token
						tokenUtil.set(token)
						// 设置用户信息
						uni.setStorageSync("userInfo", res.data)
						that.userInfo = res.data
						that.isLogin = true

						// 处理完成后，跳转页面
						let currentUrl = window.location.href;
						// 提取目标网站的域名
						let targetUrl = currentUrl.split("?")[0];
						// 替换当前 URL 为目标网站的根目录
						window.location.replace(targetUrl);
					} else {
						uni.showToast({
							icon: "none",
							title: res.message,
						})
					}
				})
			},
			// 切换导航
			switchTabbar(index) {
				this._switchTabbarPage(index)
				if (index !== 1) {
					// this.$refs?.moment?.stopAllVideo()
				}
			},
			onPullDownRefresh() {

			},
			// 导航页面滚动到底部
			tabbarPageScrollLower(e) {

			},

			// 切换导航页面
			_switchTabbarPage(index) {
				const selectPageFlag = this.tabberPageLoadFlag[index]
				if (selectPageFlag === undefined) {
					return
				}
				if (selectPageFlag === false) {
					this.tabberPageLoadFlag[index] = true
				}
				this.currentIndex = index
				console.log("刷题页更新数据", this.currentIndex)
				if (this.currentIndex === 0) {
					this.$refs?.home?.initData()
				} else if (this.currentIndex === 1) {
					this.$refs?.moment?.initMomentList()
				} else if (this.currentIndex === 2) {
					// 查询问题
					this.$refs?.problem?.getUserProblemRecordsMethod()
				} else if (this.currentIndex === 3) {
					// 查询专栏
					this.$refs?.subject?.getSubjectListMethod()
				} else if (this.currentIndex === 4) {
					// 查询我的
					this.$refs?.mine?.initData()
				}
			}
		}
	}
</script>

<style lang="scss" scoped>
</style>