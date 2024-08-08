<template>
	<view class="template-circle tn-safe-area-inset-bottom">

		<!-- 顶部自定义导航 -->
		<!-- 		<tn-nav-bar fixed alpha customBack>
			<view slot="back" class='tn-custom-nav-bar__back' @click="goBack">
				<text class='icon tn-icon-left'></text>
				<text class='icon tn-icon-home-capsule-fill'></text>
			</view>
		</tn-nav-bar> -->

		<!-- 顶部自定义导航 -->
		<tn-nav-bar :isBack="false" :bottomShadow="false" backgroundColor="none">
			<view class="custom-nav tn-flex tn-flex-col-center tn-flex-row-left">
				<!-- 个人圈子，默认显示自己头像，当有消息互动的时候，显示别人的头像，并且有一个红点点 -->
				<view v-if="isLogin" class="custom-nav__back" @click="tn('mine/user_center?uid=' + userInfo.uid)">
					<view class="logo-pic tn-shadow-blur" :style="{ backgroundImage: `url(${userInfo.photoUrl})` }">
						<view class="logo-image">
							<!-- 							<tn-badge backgroundColor="#E72F8C" :dot="true" :radius="16" :absolute="true"
								:translateCenter="false"></tn-badge> -->
						</view>
					</view>
				</view>

				<view v-if="!isLogin" class="custom-nav__back" @click="tn('/pages/index?index=4&action=login')">
					<view class="logo-pic tn-shadow-blur"
						style="background-image: url('https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png')">
					</view>
				</view>

				<view class="tn-margin-top tn-margin-left"
					style="text-shadow:  1rpx 0 0 #FFF, 0 1rpx 0 #FFF, -1rpx 0 0 #FFF , 0 -1rpx 0 #FFF;">
					<tn-tabs :list="scrollList" :current="topCurrent" @change="topChange" activeColor="#000"
						:bold="true" :fontSize="36"></tn-tabs>
				</view>
			</view>

		</tn-nav-bar>


		<view class="tabs-fixed tn-bg-white" :style="{paddingTop: vuex_custom_bar_height + 'px'}">
			<view class="tn-flex tn-flex-col-between tn-flex-col-center tn-padding-top-sm">
				<view class="justify-content-item" style="width: 87vw; overflow: hidden">
					<tn-tabs :list="sortData" :current="current" :isScroll="true" activeColor="#000" :bold="true"
						:fontSize="32" :badgeOffset="[20, 50]" @change="tabChange" backgroundColor="#FFFFFF"
						:height="70"></tn-tabs>
				</view>
				<view class="justify-content-item" style="width: 13vw; overflow: hidden"
					@click="tn('/homePages/xxxxx')">
					<view class="tn-color-black tn-round"
						style="font-size: 45rpx; margin-top: -5rpx; margin-left: 15rpx">
						<text class="tn-icon-level tn-padding-xs"></text>
					</view>
				</view>
			</view>
		</view>

		<!-- 发现 -->
		<view class="" style="background-color: #f1f1f1;" :style="{ minHeight: '1200rpx;'}">

			<!-- 图文 -->
			<view class="tn-padding-bottom-lg tn-padding-top-sm">
				<block v-for="(item,index) in dataList" :key="index" class="">
					<view class="tn-blogger-content__wrap tn-radius"
						@click="tn('/pages/subject/subjectDetail?uid=' + item.uid)"
						style="margin: 0 auto; width: 90%; border-radius: 19rpx; background: #FFFFFF;">

						<view class="tn-margin-top-sm" style="height: 320rpx; position: relative">
							<!-- 方式一 -->
							<image
								style="width: 100%; height: 320rpx;  border-top-left-radius: 18rpx; border-top-right-radius: 18rpx;"
								class="tn-blogger-content__main-image tn-blogger-content__main-image--1 tn-margin-bottom"
								v-if="item.photoList && item.photoList[0]" :src="item.photoList[0]" mode="aspectFill">
							</image>
							<image
								style="width: 100%; height: 320rpx;  border-top-left-radius: 18rpx; border-top-right-radius: 18rpx;"
								class="tn-blogger-content__main-image tn-blogger-content__main-image--1 tn-margin-bottom"
								v-else src="https://oos.moguit.cn/image/default_black.jpg" mode="aspectFill">
							</image>
							<view v-if="item.openPictureTitle === 1" class="carousel-title">
								<text class="tn-blogger-content__label__desc tn-text-bold"
									style="position: absolute; left: 50%; top: 50%; transform: translate(-50%, -50%); color: #FFFFFF; font-size: 22px;">{{ item.subjectName }}</text>
							</view>

						</view>
						<view class="tn-blogger-content__label tn-text-center tn-margin-top-sm">
							<text
								class="tn-blogger-content__label__desc tn-text-lg tn-text-bold">{{ item.subjectName }}</text>
						</view>
						<view class="tn-text-center tn-margin-top-sm tn-padding-bottom-sm">
							<!--展示精选标签-->
							<tn-tag margin="5rpx" class="tn-margin-right" backgroundColor="#e6a23c" shape="radius"
								fontColor="#FFFFFF" v-if="item.isSelection == '1'">精选</tn-tag>

							<tn-tag margin="5rpx" v-if="item.subjectSort"
								:backgroundColor="colorList[item.subjectSort.sort % colorList.length]" shape="radius"
								:plain="true"
								:fontColor="colorList[item.subjectSort.sort % colorList.length]">{{item.subjectSort.name}}</tn-tag>

							<!--区分付费和免费-->
							<tn-tag v-for="tagItem in visitAuthDictList" :key="tagItem.uid"
								v-if="parseInt(tagItem.dictValue) === item.visitAuth" margin="5rpx"
								style="margin-left: 10rpx;" :plain="true"
								:backgroundColor="colorMap[tagItem.listClass]? colorMap[tagItem.listClass]:'#01BEFF'"
								shape="radius"
								:fontColor="colorMap[tagItem.listClass]? colorMap[tagItem.listClass]:'#01BEFF'">{{ [5, 6, 10, 11, 12, 13].includes(item.visitAuth)?tagItem.dictLabel:'免费'}}</tn-tag>
						</view>


					</view>

					<!-- 边距间隔 -->
					<view style="width: 100%;border-bottom: 20rpx solid #f1f1f1;"></view>

				</block>
			</view>

			<view @click="loadMore" style="height: 50px;">
				<tn-load-more class="tn-margin-top" :status="loadStatus" :loadText="loadText"></tn-load-more>
			</view>
			<view class='tn-tabbar-height'></view>
		</view>
	</view>
</template>

<script>
	import {
		getUserMomentList,
		getUserMomentTopicList,
		addUserMoment
	} from '../../api/moment';
	import {
		getSubjectSortList,
		getSubjectList,
	} from '../../api/subject';
	import {
		getListByDictTypeList,
	} from '../../api/sysDictData.js';
	import template_page_mixin from '@/libs/mixin/template_page_mixin.js'
	export default {
		name: 'CirclePage',
		data() {
			return {
				topCurrent: 0,
				current: 0,
				// 内容默认隐藏显示的高度
				contentHideShowHeight: 0,
				adAutoplay: false,
				/* 压屏窗*/
				show2: false,
				maskCloseable: true,

				dataList: [], // 最新博客
				isEnd: false, //是否到底底部了
				loading: false, //是否正在加载
				total: 0,
				currentPage: 1,
				pageSize: 14,
				loadStatus: "loadmore",
				loadText: {
					loadmore: '点击加载更多',
					loading: '快速加载中...',
					nomore: '已经没有了啊'
				},
				orderByDescColumn: 'sort',
				isSelection: null,
				subjectSortUid: null,
				isLogin: false,
				userInfo: {},
				scrollList: [{
						name: '推荐'
					},
					{
						name: '最新'
					},
					{
						name: '最热'
					}
				],
				sortData: [{
						name: '全部专栏'
					},
					{
						name: '推荐专栏'
					},
				],
				visitAuthDictList: [], // 访问权限
				colorList: ["#409EFF", "#909399", "#67C23A", "#E6A23C", "#F56C6C"],
				colorMap: {
					"primary": "#409EFF",
					"info": "#909399",
					"success": "#67C23A",
					"warning": "#E6A23C",
					"danger": "#F56C6C",
				},
				windowHeight: 0,
			}
		},
		mixins: [template_page_mixin],
		components: {},
		onLoad() {
			this.contentHideShowHeight = uni.upx2px(56) * 3
		},
		onReady() {
			// this.$nextTick(() => {
			//   this.getContentRectInfo()
			// })
		},
		onShow() {
			this.adAutoplay = true
		},
		onHide() {
			this.adAutoplay = false
		},
		created() {
			let that = this
			uni.getStorage({
				key: 'userInfo',
				success: function(res) {
					console.log("加载用户信息", res.data)
					that.userInfo = res.data
					that.isLogin = true
				}
			});
			this.getSubjectListMethod();
			this.getSubjectSortList()
			this.getDictList()
		},
		methods: {
			// 获取系统信息
			getSystemInfo() {
				const systemInfo = uni.getSystemInfoSync()
				if (!systemInfo) {
					setTimeout(() => {
						this.getSystemInfo()
					}, 50)
					return
				}
				this.windowHeight = systemInfo.safeArea.height
			},
			// cardSwiper
			cardSwiper(e) {},
			// 跳转
			tn(e) {
				uni.navigateTo({
					url: e,
				});
			},
			// 震动跳转
			navEdit(e) {
				wx.vibrateLong();
				uni.navigateTo({
					url: '/circlePages/edit'
				})
			},
			// 震动跳转
			navCreate(e) {
				wx.vibrateLong();
				uni.navigateTo({
					url: '/circlePages/create'
				})
			},
			// 震动跳转
			navBuild(e) {
				wx.vibrateLong();
				uni.navigateTo({
					url: '/circlePages/build'
				})
			},

			// 弹出压屏窗
			showLandscape() {
				this.openLandscape()
			},
			// 打开压屏窗
			openLandscape() {
				// wx.vibrateLong();
				this.show2 = true
			},
			// 关闭压屏窗
			closeLandscape() {
				this.show2 = false
			},
			loadMore() {
				console.log("加载更多")
				if (this.dataList.length < this.pageSize || this.dataList.length == this.total) {
					this.loadStatus = "nomore";
					return;
				}
				this.currentPage = this.currentPage + 1;
				this.getSubjectListMethod();
			},
			// tab选项卡切换
			tabChange(index) {
				this.isEnd = false;
				// 参数初始化
				this.current = index
				this.currentPage = 1;
				this.isSelection = null
				this.subjectSortUid = null
				this.dataList = []
				switch (index) {
					case 0: {

					}
					break;
					case 1: {
						this.isSelection = 1
					}
					break;
					default: {
						let subjectSort = this.sortData[index]
						if (subjectSort != null) {
							this.subjectSortUid = subjectSort.uid
						}
						console.log("查询对应的专栏分类", this.subjectSortUid)
					}
				}
				this.getSubjectListMethod()
			},
			// tab选项卡切换
			topChange(index) {
				this.isEnd = false;
				// 参数初始化
				this.topCurrent = index
				this.currentPage = 1;
				this.isSelection = null
				this.subjectSortUid = null
				this.dataList = []
				switch (index) {
					case 0: {
						this.sort = 'sort';
					}
					break;
					case 1: {
						this.orderByDescColumn = 'create_time';
					}
					break;
					case 2: {
						this.orderByDescColumn = 'click_count';
					}
					break;
					default: {
						this.orderByDescColumn = 'create_time';
					}
				}
				this.getSubjectListMethod()
			},
			/**
			 * 字典查询
			 */
			getDictList: function() {
				let dictTypeList = ['sys_visit_auth']
				getListByDictTypeList(dictTypeList).then(response => {
					if (response.code == this.$ECode.SUCCESS) {
						let dictMap = response.data;
						this.visitAuthDictList = dictMap.sys_visit_auth.list
					}
				});
			},
			getSubjectListMethod() {
				let params = {};
				params.currentPage = this.currentPage;
				params.pageSize = this.pageSize;
				params.orderByDescColumn = this.orderByDescColumn
				params.subjectSortUid = this.subjectSortUid
				params.isSelection = this.isSelection
				this.loadStatus = "loadmore"
				getSubjectList(params).then((res) => {
					console.log('获取专栏详情', res);
					if (res.code == this.$ECode.SUCCESS) {
						let dataList = this.dataList.concat(res.data.records);
						this.dataList = dataList;
						this.total = res.data.total;
						this.currentPage = res.data.current;
						this.pageSize = res.data.size;
					}
					//全部加载完毕
					if (this.dataList.length >= this.total) {
						this.isEnd = true;
					} else {
						this.isEnd = false;
					}
					this.loading = false;

					if (this.dataList.length < this.pageSize || this.dataList.length == this.total) {
						this.loadStatus = "nomore";
						return;
					}
				});
			},
			getSubjectSortList() {
				let params = {};
				params.currentPage = 1;
				params.pageSize = 1000;
				getSubjectSortList(params).then((res) => {
					console.log('获取分类列表', res);
					if (res.code == this.$ECode.SUCCESS) {
						let sortData = this.sortData.concat(res.data.records);
						this.sortData = sortData;
					}
				});
			},
		}
	}
</script>

<style lang="scss" scoped>
	.carousel-title {
		cursor: pointer;
		position: absolute;
		z-index: 10;
		bottom: 110rpx;
		height: 110rpx;
		width: 100%;
		line-height: 40rpx;
		text-align: center;
		background: rgba(0, 0, 0, 0.3);
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


	.template-circle {
		max-height: 100vh;
	}

	.tn-tabbar-height {
		min-height: 120rpx;
		height: calc(140rpx + env(safe-area-inset-bottom) / 2);
	}

	/* 自定义导航栏内容 start */
	.custom-nav {
		height: 100%;

		&__back {
			margin: auto 5rpx;
			font-size: 40rpx;
			margin-right: 10rpx;
			margin-left: 30rpx;
			flex-basis: 5%;
		}

		&__search {
			flex-basis: 60%;
			width: 100%;
			height: 100%;

			&__box {
				width: 100%;
				height: 70%;
				padding: 10rpx 0;
				margin: 0 30rpx;
				border-radius: 60rpx 60rpx 0 60rpx;
				font-size: 24rpx;
			}

			&__icon {
				padding-right: 10rpx;
				margin-left: 20rpx;
				font-size: 30rpx;
			}

			&__text {
				color: #AAAAAA;
			}
		}
	}

	.logo-image {
		width: 60rpx;
		height: 60rpx;
		position: relative;
		margin-top: -15rpx;
	}

	.logo-pic {
		background-size: cover;
		background-repeat: no-repeat;
		// background-attachment:fixed;
		background-position: top;
		border-radius: 50%;
	}

	/* 自定义导航栏内容 end */
	/* 博主头像 start*/
	.image-circle {
		// padding: 95rpx;
		width: 190rpx;
		height: 190rpx;
		font-size: 40rpx;
		font-weight: 300;
		position: relative;
	}

	.image-pic {
		background-size: cover;
		background-repeat: no-repeat;
		// background-attachment:fixed;
		background-position: top;
		border-radius: 10rpx;
	}


	/* 文章内容 start*/
	.blogger {
		&__item {
			padding: 30rpx;
		}

		&__author {
			&__btn {
				margin-right: -12rpx;
				opacity: 0.5;
			}
		}

		&__desc {
			line-height: 55rpx;

			&__label {
				padding: 0 20rpx;
				margin: 0rpx 18rpx 0 0;

				&--prefix {
					color: #00FFC8;
					padding-right: 10rpx;
				}
			}

			&__content {}
		}

		&__content {
			margin-top: 18rpx;
			padding-right: 18rpx;

			&__data {
				line-height: 46rpx;
				text-align: justify;
				overflow: hidden;
				transition: all 0.25s ease-in-out;

			}

			&__status {
				margin-top: 10rpx;
				font-size: 26rpx;
				color: #82B2FF;
			}
		}

		&__main-image {
			border-radius: 16rpx;

			&--1 {
				max-width: 80%;
				max-height: 300rpx;
			}

			&--2 {
				max-width: 260rpx;
				max-height: 260rpx;
			}

			&--3 {
				height: 212rpx;
				width: 100%;
			}
		}

		&__count-icon {
			font-size: 40rpx;
			padding-right: 5rpx;
		}

		&__ad {
			width: 100%;
			height: 500rpx;
			transform: translate3d(0px, 0px, 0px) !important;

			::v-deep .uni-swiper-slide-frame {
				transform: translate3d(0px, 0px, 0px) !important;
			}

			.uni-swiper-slide-frame {
				transform: translate3d(0px, 0px, 0px) !important;
			}

			&__item {
				position: absolute;
				width: 100%;
				height: 100%;
				transform-origin: left center;
				transform: translate3d(100%, 0px, 0px) scale(1) !important;
				transition: transform 0.25s ease-in-out;
				z-index: 1;

				&--0 {
					transform: translate3d(0%, 0px, 0px) scale(1) !important;
					z-index: 4;
				}

				&--1 {
					transform: translate3d(13%, 0px, 0px) scale(0.9) !important;
					z-index: 3;
				}

				&--2 {
					transform: translate3d(26%, 0px, 0px) scale(0.8) !important;
					z-index: 2;
				}
			}

			&__content {
				border-radius: 40rpx;
				width: 640rpx;
				height: 500rpx;
				overflow: hidden;
			}

			&__image {
				width: 100%;
				height: 100%;
			}
		}
	}

	/* 文章内容 end*/

	/* 间隔线 start*/
	.tn-strip-bottom {
		width: 100%;
		border-bottom: 20rpx solid rgba(241, 241, 241, 0.8);
	}

	/* 间隔线 end*/

	/* 广告内容 start */
	.ad-image {
		width: 80rpx;
		height: 80rpx;
		position: relative;
	}

	.ad-pic {
		background-size: cover;
		background-repeat: no-repeat;
		// background-attachment:fixed;
		background-position: top;
		border-radius: 20%;
	}

	/* 自定义导航栏内容 end */


	/* 全屏轮播  start*/
	.card-swiper {
		height: 100vh !important;
	}

	.card-swiper swiper-item {
		width: 750rpx !important;
		left: 0rpx;
		box-sizing: border-box;
		overflow: initial;
	}

	.card-swiper swiper-item .swiper-item {
		width: 100%;
		display: block;
		height: 100vh;
		border-radius: 0rpx;
		transform: scale(1);
		transition: all 0.2s ease-in 0s;
		overflow: hidden;
	}

	.card-swiper swiper-item.cur .swiper-item {
		transform: none;
		transition: all 0.2s ease-in 0s;
	}

	.card-swiper swiper-item .swiper-item-png {
		margin-top: -50vh;
		width: 100%;
		display: block;
		border-radius: 0rpx;
		transform: translate(1040rpx, 20rpx) scale(0.5, 0.5);
		transition: all 0.6s ease 0s;
		// overflow: hidden;
	}

	.card-swiper swiper-item.cur .swiper-item-png {
		margin-top: -100vh;
		width: 900rpx;
		transform: translate(-80rpx, 0rpx) scale(1, 1);
		transition: all 0.6s ease 0s;
	}

	.image-banner {
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.image-banner image {
		width: 100%;
	}

	/* 轮播指示点 start*/
	.indication {
		z-index: 9999;
		width: 100%;
		height: 36rpx;
		position: fixed;
		// display:flex;
		display: block;
		flex-direction: row;
		align-items: center;
		justify-content: center;
	}

	.spot {
		background-color: #000;
		opacity: 0.3;
		width: 10rpx;
		height: 10rpx;
		border-radius: 20rpx;
		margin: 20rpx 0 !important;
		left: 95vw;
		top: -60vh;
		position: relative;
	}

	.spot.active {
		opacity: 0.6;
		height: 30rpx;
		background-color: #000;
	}

	/* 资讯主图 start*/
	.image-article {
		border-radius: 8rpx;
		border: 1rpx solid #F8F7F8;
		width: 200rpx;
		height: 200rpx;
		position: relative;
	}

	.image-pic {
		background-size: cover;
		background-repeat: no-repeat;
		// background-attachment:fixed;
		background-position: top;
		border-radius: 10rpx;
	}

	.article-shadow {
		border-radius: 15rpx;
		box-shadow: 0rpx 0rpx 50rpx 0rpx rgba(0, 0, 0, 0.07);
	}

	/* 文字截取*/
	.clamp-text-1 {
		-webkit-line-clamp: 1;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		text-overflow: ellipsis;
		overflow: hidden;
	}

	.clamp-text-2 {
		-webkit-line-clamp: 2;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		text-overflow: ellipsis;
		overflow: hidden;
	}

	/* 标签内容 start*/
	.tn-tag-content {
		&__item {
			display: inline-block;
			line-height: 35rpx;
			padding: 5rpx 25rpx;

			&--prefix {
				padding-right: 10rpx;
			}
		}
	}


	/* 图标容器9 start */
	.icon9 {
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
				font-size: 65rpx;
				border-radius: 50%;
				margin: 20rpx 40rpx;
				position: relative;
				z-index: 1;

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
					background-image: url(https://tnuiimage.tnkjapp.com/cool_bg_image/icon_bg5.png);
				}
			}
		}
	}


	/* 悬浮 */
	.tnxuanfu {
		animation: suspension 3s ease-in-out infinite;
	}

	@keyframes suspension {

		0%,
		100% {
			transform: translateY(0);
		}

		50% {
			transform: translateY(-0.8rem);
		}
	}

	/* 悬浮按钮 */
	.button-shop {
		width: 90rpx;
		height: 90rpx;
		display: flex;
		flex-direction: row;
		position: fixed;
		/* bottom:200rpx;
      right: 20rpx; */
		left: 5rpx;
		top: 5rpx;
		z-index: 1001;
		border-radius: 100px;
		opacity: 0.9;
	}


	/* 按钮 */
	.edit {
		bottom: 300rpx;
		right: 75rpx;
		position: fixed;
		z-index: 9999;
	}


	.pa,
	.pa0 {
		position: absolute
	}

	.pa0 {
		left: 0;
		top: 0
	}


	.bg0 {
		width: 100rpx;
		height: 100rpx;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -50%);
	}

	.bg1 {
		width: 100%;
		height: 100%;
	}




	.hx-box {
		top: 50%;
		left: 50%;
		width: 100rpx;
		height: 100rpx;
		transform-style: preserve-3d;
		transform: translate(-50%, -50%) rotateY(75deg) rotateZ(10deg);
	}

	.hx-box .pr {
		width: 100rpx;
		height: 100rpx;
		transform-style: preserve-3d;
		animation: hxz 20s linear infinite;
	}

	@keyframes hxz {
		0% {
			transform: rotateX(0deg);
		}

		100% {
			transform: rotateX(-360deg);
		}
	}



	.hx-box .pr .pa0 {
		width: 100rpx;
		height: 100rpx;
		/* border: 4px solid #5ec0ff; */
		border-radius: 1000px;
	}

	.hx-box .pr .pa0 .span {
		display: block;
		width: 100%;
		height: 100%;
		background: url(https://tnuiimage.tnkjapp.com/cool_bg_image/arc4.png) no-repeat center center;
		background-size: 100% 100%;
		animation: hx 4s linear infinite;
	}

	@keyframes hx {
		to {
			transform: rotate(360deg);
		}
	}

	.hx-k1 {
		transform: rotateX(-60deg) rotateZ(-60deg)
	}

	.hx-k2 {
		transform: rotateX(-30deg) rotateZ(-30deg)
	}

	.hx-k3 {
		transform: rotateX(0deg) rotateZ(0deg)
	}

	.hx-k4 {
		transform: rotateX(30deg) rotateZ(30deg)
	}

	.hx-k5 {
		transform: rotateX(60deg) rotateZ(60deg)
	}

	.hx-k6 {
		transform: rotateX(90deg) rotateZ(90deg)
	}
</style>