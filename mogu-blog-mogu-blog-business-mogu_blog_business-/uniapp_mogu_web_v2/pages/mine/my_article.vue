<template>
	<view class="template-screen tn-safe-area-inset-bottom">

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


		<!-- 不建议写时间，因为写了时间，你就要经常更新文章了鸭-->
		<view class="tn-margin-bottom-lg">
			<block v-for="(item, index) in newBlogData" :key="index">
				<view class="article-shadow tn-margin">
					<view class="tn-flex">
						<view class="tn-margin-sm tn-padding-top-xs" style="width: 100%">
							<view class="tn-text-lg tn-text-bold clamp-text-1 tn-text-justify"
								@click="tn('/pages/article/details?oid=' + item.oid)">
								<text class="">{{ item.title }}</text>
							</view>
							<view class="tn-padding-top-xs tn-flex" style="height: 122rpx"
								@click="tn('/pages/article/details?oid=' + item.oid)">


								<text v-if="item.photoList && item.photoList[0]"
									class="tn-text-df tn-color-gray clamp-text-2 tn-text-justify tn-flex-8">
									{{ item.summary.replace(/[\r\n]+/g, " ") }}
								</text>
								<text v-if="!item.photoList || !item.photoList[0]"
									class="tn-text-df tn-color-gray clamp-text-2 tn-text-justify">
									{{ item.summary.replace(/[\r\n]+/g, " ") }}
								</text>

								<view v-if="item.photoList && item.photoList[0]"
									class="image-pic tn-margin-sm tn-flex-4" style="height: 110rpx;"
									:style="'background-image:url(' + item.photoList[0] + ')'">
								</view>

							</view>
							<view v-if="vuex_custom_mini == '1'"
								class="tn-flex tn-flex-row-between tn-flex-col-between tn-margin-top-sm"
								style="align-items: center;">
								<!-- 作者信息 -->
								<view
									style="display: inline-block; width: 60rpx;height: 60rpx; background-size: cover; border-radius: 50%;"
									:style="'background-image:url(' + item.user.photoUrl + ')'"
									@click="tn('mine/user_center?uid=' + item.user.uid)">
									<view
										style="margin-left: 70rpx; margin-top: 15rpx; width: 500rpx; font-size: 14px; color: #AAAAAA;">
										{{item.user.nickName}}
									</view>
								</view>

								<view class="justify-content-item tn-color-gray tn-text-center"
									style="padding-top: 5rpx">
									<text class="tn-icon-footprint tn-text-lg" style="padding-right: 5rpx"></text>
									<text class="tn-text-df" style="padding-right: 5rpx">{{ item.clickCount }}</text>
									<text class="tn-icon-message tn-text-lg" style="padding-right: 5rpx"></text>
									<text class="tn-padding-right tn-text-df"
										style="padding-right: 5rpx">{{ item.commentCount }}</text>
								</view>
							</view>
						</view>


					</view>
				</view>


				<!-- 边距间隔 -->
				<view class="tn-strip-bottom"></view>
			</block>
			<view @click="loadMore" style="height: 50px;">
				<tn-load-more class="tn-margin-top" :status="loadStatus" :loadText="loadText"></tn-load-more>
			</view>
		</view>

	</view>
</template>

<script>
	import {
		getNewBlog,
		getBlogByLevel,
	} from '../../api/index';
	import {
		getMeBlogList
	} from '../../api/create_blog.js'
	import template_page_mixin from '@/libs/mixin/template_page_mixin.js'
	export default {
		name: 'Index',
		data() {
			return {
				cardCur: 0,
				current: 0,
				noticeList: [],
				cardCur2: 0,
				keyword: '',
				newBlogData: [], // 最新博客
				isEnd: false, //是否到底底部了
				loading: false, //是否正在加载
				total: 0,
				currentPage: 1,
				pageSize: 5,
				blogSortUid: null,
				orderByDescColumn: this.vue_customer == "1" ? "clicl_count" : "", // 降序字段
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
					},
					{
						name: '最多点赞'
					},
					{
						name: '最多收藏'
					}
				],
				loadStatus: "loadmore",
				loadText: {
					loadmore: '点击加载更多',
					loading: '快速加载中...',
					nomore: '已经没有了啊'
				}
			};
		},
		mixins: [template_page_mixin],
		mounted() {
			this.getLoginUserInfo()
		},
		created() {
			this.getLoginUserInfo()
			this.tabChange(0)
		},
		methods: {
			notImpl() {
				this.$message.error("功能正在建设中")
			},
			getLoginUserInfo() {
				let that = this
				uni.getStorage({
					key: 'userInfo',
					success: function(res) {
						console.log("加载用户信息", res.data)
						that.userInfo = res.data
						that.isLogin = true
					}
				});

			},
			// tab选项卡切换
			tabChange(index) {
				console.log('切换选项', index);
				this.isEnd = false;
				this.current = index
				this.currentPage = 1;
				this.blogSortUid = '';
				this.newBlogData = []
				switch (index) {
					case 0: {
						this.orderByDescColumn = 'create_time';
						this.blogSortUid = '';
						this.getBlogList();
					}
					break;
				case 1: {
					this.orderByDescColumn = 'click_count';
					this.getBlogList();
				}
				break;
				case 2: {
					this.orderByDescColumn = 'comment_count';
					this.getBlogList();
				}
				break;
				case 3: {
					this.orderByDescColumn = 'praise_count';
					this.getBlogList();
				}
				break;
				case 3: {
					this.orderByDescColumn = 'collect_count';
					this.getBlogList();
				}
				break;
				default: {
					this.orderByDescColumn = 'create_time';
					let blogSort = this.hotBlogSortData[index];
					this.blogSortUid = blogSort.uid;

					this.getBlogList();
				}
				}
			},
			// cardSwiper
			cardSwiper(e) {
				this.cardCur = e.detail.current;
			},
			// resume
			resume(e) {
				this.cardCur2 = e.detail.current;
			},
			// 跳转
			tn(e) {
				uni.navigateTo({
					url: e
				});
			},
			getHtmlPlainText(html_str) {
				//提取字符串中的文字
				let re = new RegExp('<[^<>]+>', 'g')
				let text = html_str.replace(re, '')
				//或
				//var text = html_str.replace(/<[^<>]+>/g, "");
				return text
			},
			loadMore() {
				console.log("加载更多")
				if (this.newBlogData.length >= this.total) {
					this.loadStatus = "nomore";
					return;
				}
				this.currentPage = this.currentPage + 1;
				this.getBlogList();
			},
			getBlogList() {
				let params = {};
				params.currentPage = this.currentPage;
				params.pageSize = this.pageSize;
				params.orderByDescColumn = this.orderByDescColumn
				this.loadStatus = "loading";
				getMeBlogList(params).then((res) => {
					console.log('得到的最新博客', res);
					if (res.code == this.$ECode.SUCCESS) {
						var newData = this.newBlogData.concat(res.data.records);
						this.newBlogData = newData;
						this.total = res.data.total;
						this.currentPage = res.data.current;
					}
					//全部加载完毕
					if (this.newBlogData.length >= this.total) {
						this.loadStatus = "nomore";
					} else {
						this.loadStatus = "loadmore";
					}
				});
			},
		}
	};
</script>

<style lang="scss" scoped>
	.template-index {
		max-height: 100vh;
	}

	/* 间隔线 start*/
	.tn-strip-bottom {
		width: 100%;
		border-bottom: 20rpx solid rgba(241, 241, 241, 0.8);
	}

	.tn-tabbar-height {
		min-height: 100rpx;
		height: calc(120rpx + env(safe-area-inset-bottom) / 2);
	}

	/* 资讯主图 start*/
	.image-article {
		border-radius: 8rpx;
		border: 1rpx solid #f8f7f8;
		width: 200rpx;
		height: 200rpx;
		position: relative;
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

	/* 轮播视觉差 start */
	.card-swiper {
		height: 340rpx !important;
	}

	.card-swiper swiper-item {
		width: 750rpx !important;
		left: 0rpx;
		box-sizing: border-box;
		// padding: 0rpx 30rpx 90rpx 30rpx;
		overflow: initial;
	}

	.card-swiper swiper-item .swiper-item {
		width: 100%;
		display: block;
		height: 100%;
		transform: scale(1);
		transition: all 0.2s ease-in 0s;
		overflow: hidden;
	}

	.card-swiper swiper-item.cur .swiper-item {
		transform: none;
		transition: all 0.2s ease-in 0s;
	}

	.card-swiper swiper-item .swiper-item-text {
		margin-top: -260rpx;
		text-align: center;
		width: 100%;
		display: block;
		// height: 50%;
		border-radius: 10rpx;
		transform: translate(100rpx, -60rpx) scale(0.9, 0.9);
		transition: all 0.6s ease 0s;
		overflow: hidden;
	}

	.card-swiper swiper-item.cur .swiper-item-text {
		margin-top: -320rpx;
		width: 100%;
		transform: translate(0rpx, 0rpx) scale(0.9, 0.9);
		transition: all 0.6s ease 0s;
	}

	.image-banner {
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.image-banner image {
		width: 100%;
		height: 100%;
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
		background-color: #ffffff;
		opacity: 0.6;
		width: 10rpx;
		height: 10rpx;
		border-radius: 20rpx;
		top: -60rpx;
		margin: 0 8rpx !important;
		position: relative;
	}

	.spot.active {
		opacity: 1;
		width: 30rpx;
		background-color: #ffffff;
	}

	/* 简历推荐 start */
	.card-swiper2 {
		height: 420rpx !important;
		overflow: hidden;
	}

	.card-swiper2 swiper-item {
		width: 680rpx !important;
		left: 30rpx;
		box-sizing: border-box;
		overflow: initial;
		padding: 0 0rpx 40rpx 0;
	}

	.card-swiper2 swiper-item .swiper-item1 {
		width: 100%;
		display: block;
		height: 100%;
		transform: scale(0.97);
		transition: all 0.2s ease-in 0s;
		background-color: #e7fafe;
	}

	.card-swiper2 swiper-item.cur .swiper-item1 {
		transform: none;
		transition: all 0.2s ease-in 0s;
	}

	.card-swiper2 swiper-item .swiper-item2 {
		margin-top: -120rpx;
		width: 100%;
		display: block;
		height: 50%;
		border-radius: 50%;
		transform: translate(480rpx, -150rpx) scale(0.9, 0.9);
		transition: all 0.3s ease 0s;
	}

	.card-swiper2 swiper-item.cur .swiper-item2 {
		margin-top: -180rpx;
		width: 100%;
		transform: translate(510rpx, -150rpx) scale(0.9, 0.9);
		transition: all 0.3s ease 0s;
	}

	.card-swiper2 swiper-item .swiper-item-text {
		margin-top: -210rpx;
		width: 100%;
		display: block;
		height: 100%;
		border-radius: 10rpx;
		transform: translate(30rpx, -80rpx) scale(0.8, 0.8);
		transition: all 0.6s ease 0s;
		overflow: hidden;
	}

	.card-swiper2 swiper-item.cur .swiper-item-text {
		margin-top: -270rpx;
		width: 100%;
		transform: translate(20rpx, 0rpx) scale(0.9, 0.9);
		transition: all 0.6s ease 0s;
	}

	/* 底部tabbar假阴影 start*/
	.bg-tabbar-shadow {
		background-image: repeating-linear-gradient(to top, rgba(0, 0, 0, 0.1) 10rpx, #ffffff, #ffffff);
		position: fixed;
		bottom: 0;
		height: 450rpx;
		width: 100vw;
		z-index: -1;
	}

	/* 图标容器12 start */
	.tn-three {
		position: absolute;
		top: 50%;
		right: 50%;
		bottom: 50%;
		left: 50%;
		transform: translate(-38rpx, -20rpx) rotateX(20deg) rotateY(10deg) rotateZ(-20deg);
		text-shadow: -1rpx 2rpx 0 #f0f0f0, -2rpx 4rpx 0 #f0f0f0, -10rpx 20rpx 30rpx rgba(0, 0, 0, 0.2);
	}

	.icon12 {
		&__item {
			width: 30%;
			background-color: #ffffff;
			border-radius: 10rpx;
			padding: 30rpx;
			margin: 20rpx 10rpx;
			transform: scale(1);
			transition: transform 0.3s linear;
			transform-origin: center center;

			&--icon {
				width: 100rpx;
				height: 100rpx;
				font-size: 60rpx;
				border-radius: 50%;
				margin-bottom: 18rpx;
				position: relative;
				z-index: 1;

				&::after {
					content: ' ';
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
					background-image: url(https://tnuiimage.tnkjapp.com/cool_bg_image/icon_bg6.png);
				}
			}
		}
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
				// color: #AAAAAA;
			}
		}
	}

	.logo-image {
		width: 65rpx;
		height: 65rpx;
		position: relative;
	}

	.logo-pic {
		background-size: cover;
		background-repeat: no-repeat;
		// background-attachment:fixed;
		background-position: top;
		border-radius: 50%;
	}

	/* 自定义导航栏内容 end */

	/* 热门图片 start*/
	.image-tuniao1 {
		padding: 164rpx 0rpx;
		font-size: 40rpx;
		font-weight: 300;
		position: relative;
	}

	.image-tuniao2 {
		padding: 75rpx 0rpx;
		font-size: 40rpx;
		font-weight: 300;
		position: relative;
	}

	.image-tuniao3 {
		padding: 90rpx 0rpx;
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

	/* 胶囊banner*/
	.image-capsule {
		padding: 100rpx 0rpx;
		font-size: 40rpx;
		font-weight: 300;
		position: relative;
	}

	.image-piccapsule {
		background-size: cover;
		background-repeat: no-repeat;
		// background-attachment:fixed;
		background-position: top;
		border-radius: 20rpx 20rpx 0 0;
	}

	/* 标题 start */
	.nav_title {
		-webkit-background-clip: text;
		color: transparent;

		&--wrap {
			position: relative;
			display: flex;
			height: 120rpx;
			font-size: 46rpx;
			align-items: center;
			justify-content: center;
			font-weight: bold;
			background-image: url(https://tnuiimage.tnkjapp.com/title_bg/title00.png);
			background-size: cover;
		}
	}

	/* 标题 end */

	/* 业务展示 start */
	.tn-info {
		&__container {
			margin-top: 10rpx;
			margin-bottom: 50rpx;
		}

		&__item {
			width: 47.7%;
			margin: 15rpx 0rpx 30rpx 0rpx;
			padding: 40rpx 30rpx;
			border-radius: 10rpx;

			position: relative;
			z-index: 1;

			&::after {
				content: ' ';
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
				background-image: url(https://tnuiimage.tnkjapp.com/cool_bg_image/2.png);
			}

			&__left {
				&--icon {
					width: 80rpx;
					height: 80rpx;
					border-radius: 50%;
					font-size: 40rpx;
					margin-right: 20rpx;
					position: relative;
					z-index: 1;

					&::after {
						content: ' ';
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

				&__content {
					font-size: 25rpx;

					&--data {
						color: rgba(255, 255, 255, 0.5);
						margin-top: 5rpx;
						// font-weight: bold;
					}
				}
			}

			&__right {
				&--icon {
					position: absolute;
					right: 0rpx;
					top: 50rpx;
					font-size: 100rpx;
					width: 108rpx;
					height: 108rpx;
					text-align: center;
					line-height: 60rpx;
					opacity: 0.15;
				}
			}

			&__bottom {
				box-shadow: 0rpx 0rpx 30rpx 0rpx rgba(0, 0, 0, 0.12);
				border-radius: 0 0 10rpx 10rpx;
				position: absolute;
				width: 85%;
				line-height: 15rpx;
				left: 50%;
				bottom: -15rpx;
				transform: translateX(-50%);
				z-index: -1;
				text-align: center;
			}
		}
	}

	/* 业务展示 end */

	/* 底部tabbar start*/
	.footerfixed {
		position: fixed;
		width: 100%;
		bottom: 0;
		z-index: 999;
		background-color: #ffffff;
		box-shadow: 0rpx 0rpx 30rpx 0rpx rgba(0, 0, 0, 0.07);
	}

	.tabbar {
		display: flex;
		align-items: center;
		min-height: 110rpx;
		justify-content: space-between;
		padding: 0;
		height: calc(110rpx + env(safe-area-inset-bottom) / 2);
		padding-bottom: calc(env(safe-area-inset-bottom) / 2);
	}

	.tabbar .action {
		font-size: 22rpx;
		position: relative;
		flex: 1;
		text-align: center;
		padding: 0;
		display: block;
		height: auto;
		line-height: 1;
		margin: 0;
		overflow: initial;
	}

	.bar-center {
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

	.tabbar .action .bar-icon {
		width: 100rpx;
		position: relative;
		display: block;
		height: auto;
		margin: 0 auto 10rpx;
		text-align: center;
		font-size: 42rpx;
		// line-height: 50rpx;
	}

	.tabbar .action .bar-icon image {
		width: 50rpx;
		height: 50rpx;
		display: inline-block;
	}

	.tabbar .action .bar-circle {
		position: relative;
		display: block;
		margin: 0rpx auto 0rpx;
		text-align: center;
		font-size: 52rpx;
		line-height: 90rpx;
		// background-color: #01BEFF;
		width: 100rpx !important;
		height: 100rpx !important;
		overflow: hidden;
		// border-radius: 50%;
		// box-shadow: 0px 10px 30px rgba(70,23,129, 0.12),
		//   0px -8px 40px rgba(255, 255, 255, 1),
		//   inset 0px -10px 10px rgba(70,23,129, 0.05),
		//   inset 0px 10px 20px rgba(255, 255, 255, 1);
		// box-shadow: 0rpx 0rpx 20rpx 0rpx rgba(1, 190, 255, 0.5);
	}

	.tabbar .action .bar-circle image {
		width: 100rpx;
		height: 100rpx;
		display: inline-block;
		margin: 0rpx auto 0rpx;
	}

	/* 流星+悬浮 */
	.nav-index-button {
		animation: suspension 3s ease-in-out infinite;
		z-index: 999999;

		&__content {
			position: absolute;
			width: 100rpx;
			height: 100rpx;
			top: 50%;
			left: 50%;
			transform: translate(-50%, -50%);

			&--icon {
				width: 100rpx;
				height: 100rpx;
				font-size: 60rpx;
				border-radius: 50%;
				margin-bottom: 18rpx;
				position: relative;
				z-index: 1;
				transform: scale(0.85);

				&::after {
					content: ' ';
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
					// background-image: url(https://tnuiimage.tnkjapp.com/cool_bg_image/icon_bg6.png);
				}
			}
		}

		&__meteor {
			position: absolute;
			top: 50%;
			left: 50%;
			width: 100rpx;
			height: 100rpx;
			transform-style: preserve-3d;
			transform: translate(-50%, -50%) rotateY(75deg) rotateZ(10deg);

			&__wrapper {
				width: 100rpx;
				height: 100rpx;
				transform-style: preserve-3d;
				animation: spin 20s linear infinite;
			}

			&__item {
				position: absolute;
				width: 100rpx;
				height: 100rpx;
				border-radius: 1000rpx;
				left: 0;
				top: 0;

				&--pic {
					display: block;
					width: 100%;
					height: 100%;
					background: url(https://tnuiimage.tnkjapp.com/cool_bg_image/arc1.png) no-repeat center center;
					background-size: 100% 100%;
					animation: arc 4s linear infinite;
				}
			}
		}
	}

	@keyframes suspension {

		0%,
		100% {
			transform: translateY(0);
		}

		50% {
			transform: translateY(-0.6rem);
		}
	}

	@keyframes spin {
		0% {
			transform: rotateX(0deg);
		}

		100% {
			transform: rotateX(-360deg);
		}
	}

	@keyframes arc {
		to {
			transform: rotate(360deg);
		}
	}
</style>
