<!-- 抽奖页面 -->
<template>
	<view class="template-activity tn-safe-area-inset-bottom"
		style="background-image: url('https://gd-hbimg.huaban.com/c48d0b2714837fd98334e6dfddb059ef7c31b34511ceb6-gCTAlT_fw1200webp'); background-size: cover; background-repeat: no-repeat;background-position: center center;">
		<!-- 顶部自定义导航 -->
		<!-- <tn-nav-bar fixed alpha customBack>
      <view slot="back" class='tn-custom-nav-bar__back'>
        <text class='icon tn-icon-notice'></text>
        <text class='icon tn-icon-caring'></text>
      </view>
    </tn-nav-bar> -->

		<!-- 		<view class="top-backgroup">
			<image src='https://tnuiimage.tnkjapp.com/index_bg/tuniao2.jpg' mode='widthFix' class='backgroud-image'>
			</image>
		</view>
 -->

		<!-- 顶部自定义导航 -->
		<tn-nav-bar fixed alpha customBack>
			<view slot="back" class='tn-custom-nav-bar__back' @click="goBack">
				<text class='icon tn-icon-left'></text>
				<text class='icon tn-icon-home-capsule-fill'></text>
			</view>
		</tn-nav-bar>

		<view :style="{paddingTop: vuex_custom_bar_height + 'px'}">
			<view class="tn-text-bold tn-text-xxl tn-text-center tn-color-black">{{activityItem.title}}</view>
		</view>

		<view>
			<LuckyWheel ref="myLucky" width="600rpx" :blocks="blocks" :prizes="prizes" :buttons="buttons"
				:defaultStyle="defaultStyle" @start="startCallBack" @end="endCallBack" />
		</view>

		<view>
			<view class="tn-text-center tn-text-md" style="display: inline-block; width: 50%;">
				<!-- 今日剩余次数: {{luckyRule.dayLuckyCount - nowLuckyCount}}次 -->
				每日可抽奖次数: {{luckyRule.dayLuckyCount }}次
			</view>
			<view class="tn-text-center tn-text-md" style="display: inline-block; width: 50%;">
				当前积分：{{credits}}
			</view>
		</view>

		<view class="tn-text-bold tn-text-md tn-text-center tn-color-black" style="margin-top: 20rpx;">奖品清单</view>


		<tn-scroll-list :indicatorWidth="100" :indicatorBarWidth="30" indicatorColor="#D6F4FA"
			indicatorActiveColor="#27A1BA">
			<view class="tn-flex tn-margin-sm">
				<block v-for="(luckyAwardItem, index) in activityItem.luckyAwardItemList" :key="index">
					<view class="tn-flex-1 tn-padding-sm tn-margin-xs tn-radius">
						<view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
							<view
								class="icon3__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-shadow-blur">
								<image :src="luckyAwardItem.awardProduct.photoUrl" style="width: 90rpx; height: 90rpx">
								</image>
							</view>
							<view class="tn-color-black tn-text-sm tn-text-center">
								<text
									class="tn-text-ellipsis">{{ getLuckyItemName(luckyAwardItem, luckyAwardItem.awardProduct)}}</text>
							</view>
							<view class="tn-color-black tn-text-sm tn-text-center">
								<text class="tn-text-ellipsis">{{ luckyAwardItem.total}}份</text>
							</view>
						</view>
					</view>
				</block>
			</view>
		</tn-scroll-list>

		<view class="tn-text-bold tn-text-md tn-text-center tn-color-black">最近中奖记录</view>


		<view class="tn-margin-top-lg tn-margin-bottom"
			style="overflow-y: scroll; min-height: 200rpx; backdrop-filter: blur(10px); background-color: rgba(255, 255, 255, 0.5);">
			<view class="tn-flex tn-flex-row-between tn-strip-bottom-min tn-padding"
				v-for="(item,index) in luckyRecordList" :key="index">

				<view class="blogger__author tn-flex tn-flex-row-between tn-flex-col-center">
					<view class="justify__author__info" @click="tn('/circlePages/blogger_other')">
						<view class="tn-flex tn-flex-row-center">
							<view class="tn-flex tn-flex-row-center tn-flex-col-center" v-if="item.user">
								<view class="">
									<tn-avatar class="" shape="circle" :src="item.user.photoUrl" size="sm">
									</tn-avatar>
								</view>
								<view class="tn-padding-right tn-text-ellipsis">
									<view class="tn-padding-right tn-padding-left-sm tn-text-bold tn-text-sm">
										{{ item.user.nickName }}
									</view>
									<view
										class="tn-padding-right tn-padding-left-sm tn-padding-top-xs tn-color-gray tn-text-sm">
										{{ item.createTime }}
									</view>
								</view>
							</view>
						</view>
					</view>
					<view class="justify-content-item">
						<text class="tn-color-brown">
							获得{{getLuckyItemName(item.luckyAwardItem, item.awardProduct)}}</text>
					</view>
				</view>
			</view>
		</view>


		<view class='tn-tabbar-height'></view>

	</view>
</template>

<script>
	import LuckyWheel from '../../components/@lucky-canvas/uni/lucky-wheel'
	import {
		getActivityList,
		lucky,
		getLuckyRecordList
	} from "../../api/lucky";
	import {
		getCurrentUserCredits
	} from '../../api/user.js'
	import template_page_mixin from '@/libs/mixin/template_page_mixin.js'
	export default {
		name: 'Discovery',
		mixins: [template_page_mixin],
		data() {
			return {
				cardCur: 0,
				swiperList: [{
					id: 0,
					type: 'image',
					title: 'BUG再多',
					name: '也别忘了',
					text: '我无限续杯',
					url: 'https://tnuiimage.tnkjapp.com/shop/cup1.jpg',
				}],
				activityItem: {},
				luckyRule: {},
				sponsorList: [],
				myLuckyRecordList: [],
				luckyRecordList: [],
				luckyAwardProduct: {},
				credits: 0,
				pageSize: 10,
				currentPage: 1,
				total: 0,
				blocks: [{
					padding: '13px',
					background: '#56a1e8'
				}],
				defaultStyle: {
					fontColor: 'blue',
					fontSize: '14px',
					position: 'static'
				},
				prizes: [],
				buttons: [{
					radius: '45px',
					background: '#ffdea0',
					imgs: [{
						src: 'https://oos.moguit.cn/image/pointer2.png',
						width: '100%',
						top: '-255%'
					}],
					style: {
						cursor: 'pointer'
					}
				}],
				screenHeight: "",
				nowLuckyCount: 0,
				userInfo: {},
				isLogin: false,
			}
		},
		components: {
			LuckyWheel
		},
		created() {
			let that = this
			uni.getSystemInfo({
				success: function(res) {
					that.screenHeight = res.screenHeight + "rpx"
					console.log("屏幕高度", res.screenHeight); // 屏幕高度
				}
			});
			uni.getStorage({
				key: 'userInfo',
				success: function(res) {
					console.log("加载用户信息", res.data)
					that.userInfo = res.data
					that.isLogin = true
					that.getMyLuckyRecordListMethod()
				}
			});
			this.getActivityListMethod()
			this.getUserCredits(true)
		},
		methods: {
			// cardSwiper
			cardSwiper(e) {
				this.cardCur = e.detail.current
			},
			// 跳转
			tn(e) {
				uni.navigateTo({
					url: e,
				});
			},
			// 点击抽奖按钮触发回调
			startCallBack() {
				if (!this.isLogin) {
					this.$message.error("请先登录")
					setTimeout(() => {
						uni.navigateTo({
							url: '/pages/index?index=4'
						})
					}, 1000)
					return
				}
				// 使用定时器来模拟请求接口
				let params = {}
				params.uid = this.activityItem.uid
				lucky(params).then(response => {
					if (response.code == this.$ECode.SUCCESS) {
						// 启动转盘抽奖
						this.$refs.myLucky.play()
						console.log("抽奖结果", response)
						let luckyUid = response.data
						// 遍历奖励，获得奖项的下标
						let prizes = this.prizes
						let luckyIndex = -1
						console.log("遍历抽奖项", prizes)
						prizes.forEach((item, index) => {
							if (item.uid == luckyUid) {
								luckyIndex = index
								this.luckyAwardProduct = item
							}
						})
						if (luckyIndex < 0) {
							uni.showToast({
								title: "抽奖服务异常，请联系管理员",
								icon: 'none',
								duration: 2000,
								mask: true,
								position: 'bottom'
							});
							return
						}
						setTimeout(() => {
							this.$refs.myLucky.stop(luckyIndex)
							this.timeOutLoading()
						}, 2000)

					} else {
						uni.showToast({
							title: response.message,
							icon: 'none',
							duration: 2000,
							mask: true,
							position: 'bottom'
						});
					}
				})
			},
			// 抽奖结束触发回调
			endCallBack(prize) {
				// 奖品详情
				uni.showToast({
					title: `恭喜你获得 ${prize.title}`,
					icon: 'none',
					duration: 2000,
					mask: true,
					position: 'bottom'
				});
			},
			timeOutLoading() {
				setTimeout(() => {
					this.getUserCredits(true)
					this.currentPage = 1
					this.getMyLuckyRecordListMethod()
					this.getLuckyRecordListMethod()
				}, 3500)
			},
			getActivityListMethod() {
				let params = {}
				params.pageSize = 1
				params.currentPage = 1
				getActivityList(params).then(resp => {
					if (resp.code == this.$ECode.SUCCESS) {
						let data = resp.data.records
						if (data.length == 0) {
							uni.showToast({
								title: `后台暂未配置抽奖活动`,
								icon: 'none',
								duration: 2000,
								mask: true,
								position: 'bottom'
							});
							return
						}
						let activityItem = data[0]
						if (activityItem.sponsorList) {
							this.sponsorList = JSON.parse(activityItem.sponsorList)
						}
						this.luckyRule = activityItem.luckyRule
						this.activityItem = activityItem
						this.getLuckyRecordListMethod()
						// 获取活动所有
						let luckyAwardItemList = activityItem.luckyAwardItemList
						let prizes = []
						luckyAwardItemList.forEach((item, index) => {
							let name = this.getLuckyItemName(item, item.awardProduct);
							prizes.push({
								uid: item.uid,
								title: name,
								openWindow: item.openWindow,
								photoUrl: item.awardProduct.photoUrl,
								background: index % 2 ? '#ecf3fc' : '#b7dff8',
								fonts: [{
									text: name,
									fontSize: 14,
									top: '10%'
								}],
								imgs: [{
									src: item.awardProduct.photoUrl,
									width: '30%',
									top: '35%'
								}]
							})
						})
						this.prizes = prizes
					}
				})
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
			// 获取我的抽奖记录
			getMyLuckyRecordListMethod() {
				let userInfo = this.userInfo
				if (!userInfo) {
					this.nowLuckyCount = 0
					return
				}

				// 获取最近的抽奖记录
				let params = {}
				params.luckyActivityUid = this.activityItem.uid
				params.pageSize = this.pageSize
				params.currentPage = this.currentPage
				params.userUid = userInfo.uid
				params.luckyStatus = 1
				getLuckyRecordList(params).then(response => {
					if (response.code == this.$ECode.SUCCESS) {
						console.log("查询我的中奖记录", response)
						let myLuckyRecordList = response.data.records
						let count = 0
						myLuckyRecordList.forEach(item => {
							let isToday = this.isToday(item.createTime)
							if (isToday) {
								count += 1
							}
						})
						console.log("今天抽奖次数", count)
						this.nowLuckyCount = count
						// this.total = response.data.total;
						// this.pageSize = response.data.size;
						// this.currentPage = response.data.current;
					}
				})
			},
			isToday(date) {
				var today = new Date();
				today.setHours(0, 0, 0, 0);
				var time = new Date(date).getTime();
				time -= today.getTime();
				return time < 0;
			},
			getLuckyRecordListMethod() {
				// 获取最近的抽奖记录
				let params = {}
				params.luckyActivityUid = this.activityItem.uid
				params.pageSize = 10
				params.currentPage = 1
				params.luckyStatus = 1
				getLuckyRecordList(params).then(response => {
					console.log("获取最近的抽奖记录", response)
					if (response.code == this.$ECode.SUCCESS) {
						this.luckyRecordList = response.data.records
					}

				})
			},
			getUserCredits(refresh) {
				let params = {}
				params.refresh = refresh
				getCurrentUserCredits(params).then(response => {
					if (response.code == this.$ECode.SUCCESS) {
						this.credits = response.data
					}
				})
			},
		}
	}
</script>

<style lang="scss" scoped>
	.template-activity {
		max-height: 100vh;
	}

	.tn-tabbar-height {
		min-height: 120rpx;
		height: calc(140rpx + env(safe-area-inset-bottom) / 2);
	}

	/* 胶囊*/
	.tn-custom-nav-bar__back {
		width: 100%;
		height: 100%;
		position: relative;
		display: flex;
		justify-content: space-evenly;
		align-items: center;
		box-sizing: border-box;
		background-color: rgba(0, 0, 0, 0.15);
		border-radius: 1000rpx;
		border: 1rpx solid rgba(255, 255, 255, 0.5);
		color: #FFFFFF;
		font-size: 18px;

		.icon {
			display: block;
			flex: 1;
			margin: auto;
			text-align: center;
		}

		&:before {
			content: " ";
			width: 1rpx;
			height: 110%;
			position: absolute;
			top: 22.5%;
			left: 0;
			right: 0;
			margin: auto;
			transform: scale(0.5);
			transform-origin: 0 0;
			pointer-events: none;
			box-sizing: border-box;
			opacity: 0.7;
			background-color: #FFFFFF;
		}
	}

	/* .tnphone-white-min 细边框*/
	.tnphone-white-min {
		width: 380rpx;
		height: 800rpx;
		border-radius: 40rpx;
		background: #E9E5F3;
		padding: 7rpx;
		display: table;
		color: #333;
		box-sizing: border-box;
		box-shadow: 0rpx 10rpx 50rpx 0rpx rgba(0, 0, 0, 0.15);
		margin: 70rpx auto;
		cursor: default;
		position: relative
	}

	.tnphone-white-min .skin {
		width: 100%;
		height: 100%;
		border-radius: 40rpx;
		background: #E9E5F3;
		padding: 10rpx;
	}

	.tnphone-white-min .screen {
		width: 100%;
		height: 100%;
		border-radius: 30rpx;
		background: #E9E5F3;
		position: relative;
		overflow: hidden
	}

	.tnphone-white-min .head {
		width: 100%;
		height: 90rpx;
		text-align: center;
		position: absolute;
		padding: 45rpx 15rpx 10rpx 15rpx;
	}

	.tnphone-white-min .peak {
		left: 22%;
		width: 56%;
		height: 27rpx;
		margin: -2rpx auto 0rpx;
		border-radius: 0 0 20rpx 20rpx;
		background: #E9E5F3;
		position: absolute
	}

	.tnphone-white-min .sound {
		width: 48rpx;
		height: 6rpx;
		border-radius: 15rpx;
		background: #555;
		position: absolute;
		left: 50%;
		top: 50%;
		margin-left: -24rpx;
		margin-top: -10rpx;
		box-shadow: 0rpx 4rpx 4rpx 0rpx #444 inset
	}

	.tnphone-white-min .lens {
		width: 6rpx;
		height: 6rpx;
		border-radius: 50%;
		background: #2c5487;
		position: absolute;
		left: 50%;
		top: 50%;
		margin-left: 34rpx;
		margin-top: -10rpx
	}

	.tnphone-white-min .talk {
		width: 50%;
		height: 6rpx;
		border-radius: 15rpx;
		background: rgba(0, 0, 0, .3);
		position: absolute;
		bottom: 8rpx;
		left: 50%;
		margin-left: -25%
	}

	.tnphone-white-min .area-l,
	.tnphone-white-min .area-r {
		width: 70rpx;
		height: 16rpx;
		position: absolute;
		top: 6rpx
	}

	.tnphone-white-min .area-l {
		left: 0;
		text-align: center;
		font-size: 12rpx;
		line-height: 22rpx;
		text-indent: 10rpx;
		font-weight: 600;
		padding-left: 20rpx;
	}

	.tnphone-white-min .area-r {
		right: 0;
		text-align: center;
		font-size: 12rpx;
		line-height: 22rpx;
		text-indent: 10rpx;
		font-weight: 600;
		padding-right: 20rpx;
	}

	.tnphone-white-min .fa-feed {
		float: left;
		font-size: 12rpx !important;
		transform: rotate(-45deg);
		margin-top: 4rpx;
		margin-right: 8rpx
	}

	.tnphone-white-min .fa-battery-full {
		float: left;
		font-size: 12rpx !important;
		margin-top: 6rpx
	}

	.tnphone-white-min .fa-chevron-left {
		float: left;
		margin-top: 4rpx
	}

	.tnphone-white-min .fa-cog {
		float: right;
		margin-top: 4rpx
	}

	.tnphone-white-min .btn01 {
		width: 3rpx;
		height: 28rpx;
		border-radius: 3rpx 0 0 3rpx;
		background: #222;
		position: absolute;
		top: 105rpx;
		left: -3rpx
	}

	.tnphone-white-min .btn02 {
		width: 3rpx;
		height: 54rpx;
		border-radius: 3rpx 0 0 3rpx;
		background: #222;
		position: absolute;
		top: 160rpx;
		left: -3rpx
	}

	.tnphone-white-min .btn03 {
		width: 3rpx;
		height: 54rpx;
		border-radius: 3rpx 0 0 3rpx;
		background: #222;
		position: absolute;
		top: 230rpx;
		left: -3rpx
	}

	.tnphone-white-min .btn04 {
		width: 3rpx;
		height: 86rpx;
		border-radius: 0 3rpx 3rpx 0;
		background: #222;
		position: absolute;
		top: 180rpx;
		right: -3rpx
	}


	/* 顶部背景图 start */
	.top-backgroup {
		height: 450rpx;
		z-index: -1;

		.backgroud-image {
			width: 100%;
			height: 667rpx;
			// z-index: -1;
		}
	}

	/* 顶部背景图 end */

	/* 轮播样机样式 start*/
	.card-swiper {
		height: 830rpx !important;
	}

	.card-swiper swiper-item {
		width: 260rpx !important;
		// left: 170rpx;
		// width: 380rpx !important;
		// left: 185rpx;
		box-sizing: border-box;
		padding: 0rpx 15rpx 90rpx 15rpx;
		overflow: initial;
	}

	.card-swiper swiper-item .swiper-item {
		display: block;
		transform: scale(0.45);
		transition: all 0.2s ease-in 0s;
		overflow: hidden;
	}

	.card-swiper swiper-item.cur .swiper-item {
		transform: scale(0.65);
		transition: all 0.2s ease-in 0s;
	}

	.image-banner {
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.image-banner image {
		width: 100%;
		height: 770rpx;
		// border: 1rpx solid red;
	}

	/* 轮播指示点 start*/
	.indication {
		z-index: 9999;
		width: 100%;
		height: 36rpx;
		position: absolute;
		display: flex;
		flex-direction: row;
		align-items: center;
		justify-content: center;
	}

	.spot {
		background-color: #000;
		opacity: 0;
		width: 10rpx;
		height: 10rpx;
		border-radius: 20rpx;
		margin: 0 8rpx !important;
		top: -80rpx;
		position: relative;
	}

	.spot.active {
		opacity: 0;
		width: 30rpx;
		background-color: #000;
	}

	/* 图标容器4 start */
	.tn-cool-color-icon4 {
		// background-image: -webkit-linear-gradient(135deg, #ED1C24, #FECE12);   16
		// background-image: linear-gradient(135deg, #ED1C24, #FECE12);
		-webkit-background-clip: text;
		background-clip: text;
		-webkit-text-fill-color: transparent;
		text-fill-color: transparent;
	}

	.icon4 {
		&__item {
			width: 30%;
			background-color: #FFFFFF;
			border-radius: 10rpx;
			padding: 30rpx;
			margin: 20rpx 10rpx;
			transform: scale(1);
			transition: transform 0.3s linear;
			transform-origin: center center;

			&--icon {
				width: 110rpx;
				height: 110rpx;
				font-size: 55rpx;
				border-radius: 50%;
				margin-bottom: 18rpx;
				position: relative;
				z-index: 1;
				box-shadow: 0px 10px 30px rgba(70, 23, 129, 0.12),
					0px -8px 40px rgba(255, 255, 255, 1),
					inset 0px -10px 10px rgba(70, 23, 129, 0.05),
					inset 0px 10px 20px rgba(255, 255, 255, 1);
			}
		}
	}

	/* 标题 start */
	.nav_title {
		-webkit-background-clip: text;
		color: transparent;

		&--wrap {
			position: relative;
			display: flex;
			height: 120rpx;
			font-size: 42rpx;
			align-items: center;
			justify-content: center;
			font-weight: bold;
			background-image: url(https://tnuiimage.tnkjapp.com/title_bg/title44.png);
			background-size: cover;
		}
	}

	/* 标题 end */

	/* 组件导航列表 start*/
	.nav-list {
		display: flex;
		flex-wrap: wrap;
		padding: 0rpx 12rpx 0rpx;
		justify-content: space-between;

		/* 列表元素 start */
		.nav-list-item {
			padding: 95rpx 30rpx 5rpx 30rpx;
			border-radius: 12rpx;
			width: 45%;
			margin: 0 18rpx 40rpx;
			background-size: cover;
			background-position: center;
			position: relative;
			z-index: 99;

			/* 元素标题 start */
			.nav-link {
				font-size: 32rpx;
				text-transform: capitalize;
				padding: 0 0 10rpx 0;
				position: relative;

				.title {
					color: #FFFFFF;
					margin-top: 100rpx;
					text-align: center;
				}

				.join {
					color: #FFFFFF;
					margin-top: 20rpx;
					margin-bottom: 40rpx;
					text-align: center;
				}
			}

			/* 元素标题 end */

			/* 元素图标 start */
			.icon {
				font-variant: small-caps;
				position: absolute;
				top: 60rpx;
				right: 50rpx;
				left: 37%;
				width: 90rpx;
				height: 90rpx;
				line-height: 90rpx;
				margin: 0;
				padding: 0;
				display: inline-flex;
				text-align: center;
				justify-content: center;
				vertical-align: middle;
				font-size: 50rpx;
				color: #FFFFFF;
				white-space: nowrap;
				opacity: 0.9;
				background-color: rgba(0, 0, 0, 0.05);
				background-size: cover;
				background-position: 50%;
				border-radius: 5000rpx;

				&::after {
					content: " ";
					position: absolute;
					z-index: -1;
					width: 100%;
					height: 100%;
					left: 0;
					bottom: 0;
					border-radius: inherit;
					opacity: 1;
					transform: scale(1, 1);
					background-size: 100% 100%;
					background-image: url(https://tnuiimage.tnkjapp.com/cool_bg_image/icon_bg2.png);
				}
			}

			/* 元素图标 end */
		}

		/* 列表元素 end */
	}

	/* 组件导航列表 end*/
</style>