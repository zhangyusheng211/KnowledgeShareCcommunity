<template>
	<view>
		<!-- 压屏窗-->
		<tn-landscape :show="show2" @close="closeLandscape" closePosition="bottom" :closeSize="60">
			<view class="tn-flex" style="margin-bottom: 100rpx;padding-top: 46vh;">
				<view class="tn-flex-1 tn-margin-sm tn-radius" @click="navCreate">
					<view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
						<view
							class="icon9__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-shadow-blur tn-bg-red tn-color-white">
							<view class="tn-icon-edit-form"></view>
						</view>
						<view class="tn-color-white tn-text-sm tn-text-center">
							<text class="tn-text-ellipsis">发表文章</text>
						</view>
					</view>
				</view>
				<view class="tn-flex-1 tn-margin-sm tn-radius" @click="navEdit">
					<view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
						<view
							class="icon9__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-shadow-blur tn-bg-cyan tn-color-white">
							<view class="tn-icon-camera-fill"></view>
						</view>
						<view class="tn-color-white tn-text-sm tn-text-center">
							<text class="tn-text-ellipsis">发布动态</text>
						</view>
					</view>
				</view>
				<view class="tn-flex-1 tn-margin-sm tn-radius" @click="navBuild">
					<view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
						<view
							class="icon9__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-shadow-blur tn-bg-blue tn-color-white">
							<view class="tn-icon-book"></view>
						</view>
						<view class="tn-color-white tn-text-sm tn-text-center">
							<text class="tn-text-ellipsis">发布面经</text>
						</view>
					</view>
				</view>
			</view>
		</tn-landscape>


		<view class="edit tnxuanfu" @tap="showLandscape">
			<view class="bg0 pa">
				<view class="bg1">
					<image src="https://oos.moguit.cn/image/add2.png" class="button-shop shadow"></image>
				</view>
			</view>
			<view class="hx-box pa">
				<view class="pr">
					<view class="hx-k1 pa0">
						<view class="span"></view>
					</view>
					<view class="hx-k2 pa0">
						<view class="span"></view>
					</view>
					<view class="hx-k3 pa0">
						<view class="span"></view>
					</view>
					<view class="hx-k4 pa0">
						<view class="span"></view>
					</view>
					<view class="hx-k5 pa0">
						<view class="span"></view>
					</view>
					<view class="hx-k6 pa0">
						<view class="span"></view>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		name: "creation",
		data() {
			return {
				/* 压屏窗*/
				show2: false,
				maskCloseable: true,
				isLogin: false,
			};
		},
		created() {
			let that = this
			// 判断是否登录
			uni.getStorage({
				key: 'userInfo',
				success: function(res) {

					that.isLogin = true
				}
			});

		},
		methods: {
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
			// 震动跳转
			navEdit(e) {
				if (!this.isLogin) {
					this.$message.error("请先登录")
					setTimeout(() => {
						uni.navigateTo({
							url: '/pages/index?index=4'
						})
					}, 1000)
					return
				}
				// wx.vibrateLong();
				uni.navigateTo({
					url: '/pages/moment/create'
				})
			},
			// 震动跳转
			navCreate(e) {
				if (!this.isLogin) {
					this.$message.error("请先登录")
					setTimeout(() => {
						uni.navigateTo({
							url: '/pages/index?index=4'
						})
					}, 1000)
					return
				}
				// wx.vibrateLong();
				uni.navigateTo({
					url: '/pages/article/create_article'
				})
			},
			// 震动跳转
			navBuild(e) {
				if (!this.isLogin) {
					this.$message.error("请先登录")
					setTimeout(() => {
						uni.navigateTo({
							url: '/pages/index?index=4'
						})
					}, 1000)
					return
				}
				// wx.vibrateLong();
				this.$message.error("功能暂未实现")
			},
		},
	}
</script>


<style lang="scss" scoped>
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