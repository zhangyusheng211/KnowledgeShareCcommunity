<template>
	<view class="template-circle tn-safe-area-inset-bottom">

		<!-- 顶部自定义导航 -->
		<tn-nav-bar fixed alpha customBack>
			<view slot="back" class='tn-custom-nav-bar__back' @click="goBack">
				<text class='icon tn-icon-left'></text>
				<text class='icon tn-icon-home-capsule-fill'></text>
			</view>
		</tn-nav-bar>


		<view class="tabs-fixed tn-bg-white" :style="{paddingTop: vuex_custom_bar_height + 'px'}">
			<view class="tn-flex tn-flex-col-between tn-flex-col-center tn-padding-top-sm">
				<view class="justify-content-item" style="width: 87vw; overflow: hidden">
					<tn-tabs :list="hotBlogSortData" :current="current" :isScroll="true" activeColor="#000" :bold="true"
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
		<view class="">
			<view class="tn-flex tn-flex-direction-column tn-margin-top-sm tn-margin-bottom">
				<!-- 边距间隔 -->
				<view class="tn-strip-bottom"></view>

				<!-- 图文信息 -->
				<block v-for="(item,index) in dataList" :key="index">
					<view class="blogger__item">
						<view class="blogger__author tn-flex tn-flex-row-between tn-flex-col-center">
							<view class="justify__author__info" @click="tn('mine/user_center?uid=' + item.user.uid)">
								<view class="tn-flex tn-flex-row-center">
									<view class="tn-flex tn-flex-row-center tn-flex-col-center">
										<view class="">
											<tn-avatar class="" shape="circle" :src="item.user.photoUrl" size="lg">
											</tn-avatar>
										</view>
										<view class="tn-padding-right tn-text-ellipsis">
											<view class="tn-padding-right tn-padding-left-sm tn-text-bold tn-text-lg">
												{{ item.user.nickName }}
											</view>
											<view
												class="tn-padding-right tn-padding-left-sm tn-padding-top-xs tn-color-gray">
												{{ item.createTime }}
											</view>
										</view>
									</view>
								</view>
							</view>
							<view
								class="blogger__author__btn justify-content-item tn-flex-col-center tn-flex-row-center">
								<text class="tn-icon-more-vertical tn-color-gray tn-text-bold tn-text-xxl"></text>
							</view>
						</view>

						<view
							class="blogger__desc tn-margin-top-sm tn-margin-bottom-sm tn-text-justify tn-flex-col-center tn-flex-row-left"
							@click="tn('moment/details?uid=' + item.uid)">
							<view v-for="(label_item,label_index) in item.userMomentTopicList" :key="label_index"
								class="blogger__desc__label tn-float-left tn-margin-right tn-bg-gray--light tn-round tn-text-sm tn-text-bold">
								<text class="blogger__desc__label--prefix">#</text>
								<text class="tn-text-df">{{ label_item.content }}</text>
							</view>

							<!-- 不用限制长度了，因为发布的时候限制长度了-->

							<view class="blogger__desc__content tn-flex-1 tn-text-justify tn-text-df"
								v-html="item.content">
								{{ item.content }}
							</view>
						</view>

						<block v-if="item.photoList">
							<view v-if="[1,2,4].indexOf(item.photoList.length) != -1" class="tn-padding-top-xs"
								@click="tn('moment/details?uid=' + item.uid)">
								<image v-for="(image_item,image_index) in item.photoList" :key="image_index"
									class="blogger__main-image" :class="{
                    'blogger__main-image--1 tn-margin-bottom-sm': item.photoList.length === 1,
                    'blogger__main-image--2 tn-margin-right-sm tn-margin-bottom-sm': item.photoList.length === 2 || item.photoList.length === 4
                  }" :src="image_item" mode="aspectFill"></image>
							</view>

							<view v-else class="tn-padding-top-xs" @click="tn('moment/details?uid=' + item.uid)">
								<tn-grid hoverClass="none" :col="3">
									<block v-for="(image_item,image_index) in item.photoList" :key="image_index">
										<!-- #ifndef MP-WEIXIN -->
										<tn-grid-item style="width: 30%;margin: 10rpx;">
											<image class="blogger__main-image blogger__main-image--3" :src="image_item"
												mode="aspectFill"></image>
										</tn-grid-item>
										<!-- #endif-->
										<!-- #ifdef MP-WEIXIN -->
										<tn-grid-item style="width: 30%;margin: 10rpx;">
											<image class="blogger__main-image blogger__main-image--3" :src="image_item"
												mode="aspectFill"></image>
										</tn-grid-item>
										<!-- #endif-->
									</block>
								</tn-grid>
							</view>
						</block>

						<view class="tn-flex tn-flex-row-between tn-flex-col-center tn-margin-top-xs">
							<view class="justify-content-item tn-color-gray tn-text-center">
								<view class="">
									<text class="blogger__count-icon tn-icon-footprint"></text>
									<text class="tn-padding-right">{{ item.clickCount }}</text>
									<text class="blogger__count-icon tn-icon-message"></text>
									<text class="tn-padding-right">{{ item.commentCount }}</text>
									<text class="blogger__count-icon tn-icon-like"></text>
									<text class="">{{ item.clickCount }}</text>
								</view>
							</view>
							<!-- 							<view class="justify-content-item tn-flex tn-flex-col-center">
								<view style="margin-right: 10rpx;margin-left: 20rpx;">
									<tn-avatar-group :lists="item.viewUser.latestUserAvatar" size="sm">
									</tn-avatar-group>
								</view>
								<text class="tn-color-gray">{{ item.viewUser.viewUserCount }}人</text>
							</view> -->
						</view>
					</view>

					<!-- 边距间隔 -->
					<view class="tn-strip-bottom" v-if="index != dataList.length - 1"></view>
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
	import template_page_mixin from '@/libs/mixin/template_page_mixin.js'
	export default {
		name: 'CirclePage',
		data() {
			return {
				cardCur: 0,
				swiperList: [{
					id: 0,
					type: 'image',
					url: 'https://tnuiimage.tnkjapp.com/swiper/fullbg1.jpg',
					pngurl: 'https://tnuiimage.tnkjapp.com/swiper/full1.png',
					mp4: 'https://tnuiimage.tnkjapp.com/new/111.mp4'
				}, {
					id: 1,
					type: 'image',
					url: 'https://tnuiimage.tnkjapp.com/swiper/fullbg2.jpg',
					pngurl: 'https://tnuiimage.tnkjapp.com/swiper/full2.png',
					mp4: 'https://tnuiimage.tnkjapp.com/new/22.mp4'
				}, {
					id: 2,
					type: 'image',
					url: 'https://tnuiimage.tnkjapp.com/swiper/fullbg1.jpg',
					pngurl: 'https://tnuiimage.tnkjapp.com/swiper/full3.png',
					mp4: 'https://tnuiimage.tnkjapp.com/new/33.mp4'
				}, {
					id: 3,
					type: 'image',
					url: 'https://tnuiimage.tnkjapp.com/swiper/fullbg2.jpg',
					pngurl: 'https://tnuiimage.tnkjapp.com/swiper/full4.png',
					mp4: 'https://tnuiimage.tnkjapp.com/new/111.mp4'
				}],
				current: 0,
				scrollList: [{
						name: '最新'
					},
					{
						name: '关注'
					},
					{
						name: '最热'
					}
				],
				latestUserAvatar: [{
						src: 'https://tnuiimage.tnkjapp.com/blogger/avatar_1.jpeg'
					},
					{
						src: 'https://tnuiimage.tnkjapp.com/blogger/avatar_2.jpeg'
					},
					{
						src: 'https://tnuiimage.tnkjapp.com/blogger/avatar_3.jpeg'
					},
					{
						src: 'https://tnuiimage.tnkjapp.com/blogger/avatar_4.jpeg'
					},
				],
				// 内容默认隐藏显示的高度
				contentHideShowHeight: 0,
				adList: [{
						image: 'https://tnuiimage.tnkjapp.com/swiper/ad1.jpg'
					},
					{
						image: 'https://tnuiimage.tnkjapp.com/swiper/ad2.jpg'
					},
					{
						image: 'https://tnuiimage.tnkjapp.com/swiper/ad3.jpg'
					},
					{
						image: 'https://tnuiimage.tnkjapp.com/swiper/ad4.jpg'
					},
					{
						image: 'https://tnuiimage.tnkjapp.com/swiper/ad5.jpg'
					}
				],
				adAutoplay: false,
				/* 压屏窗*/
				show2: false,
				maskCloseable: true,

				dataList: [], // 最新博客
				isEnd: false, //是否到底底部了
				loading: false, //是否正在加载
				total: 0,
				currentPage: 1,
				pageSize: 20,
				loadStatus: "loadmore",
				loadText: {
					loadmore: '点击加载更多',
					loading: '快速加载中...',
					nomore: '已经没有了啊'
				},
				orderByDescColumn: 'create_time',
				isLogin: false,
				userInfo: {},
				hotBlogSortData: [{
						name: '最新'
					},
					{
						name: '最热'
					},
					{
						name: '最多评论'
					}
				],
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
					that.getMomentList()
				}
			});


		},
		methods: {
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
				if (this.dataList.length >= this.total) {
					this.loadStatus = "nomore";
					return;
				}
				this.currentPage = this.currentPage + 1;
				this.getMomentList();
			},
			// tab选项卡切换
			tabChange(index) {
				this.isEnd = false;
				this.current = index
				this.currentPage = 1;
				this.dataList = []
				switch (index) {
					case 0: {
						this.orderByDescColumn = 'create_time';
					}
					break;
				case 1: {
					this.orderByDescColumn = 'click_count';
				}
				break;
				case 2: {
					this.orderByDescColumn = 'comment_count';
				}
				break;
				default: {
					this.orderByDescColumn = 'create_time';
				}
				}
				this.getMomentList()
			},
			getMomentList() {
				let params = {};
				params.currentPage = this.currentPage;
				params.pageSize = this.pageSize;
				params.orderBy = this.orderBy
				params.userUid = this.userInfo.uid
				params.orderByDescColumn = this.orderByDescColumn
				this.loading = true;
				getUserMomentList(params).then((res) => {
					console.log('获取用户动态', res);
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
				});
			},
		}
	}
</script>

<style lang="scss" scoped>
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
