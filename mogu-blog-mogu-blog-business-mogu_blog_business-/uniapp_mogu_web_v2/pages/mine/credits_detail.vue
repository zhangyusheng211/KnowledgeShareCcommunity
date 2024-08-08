<template>
	<view class="template-integral tn-safe-area-inset-bottom">
		<!-- 顶部自定义导航 -->
		<tn-nav-bar fixed alpha customBack>
			<view slot="back" class='tn-custom-nav-bar__back' @click="goBack">
				<text class='icon tn-icon-left'></text>
				<text class='icon tn-icon-home-capsule-fill'></text>
			</view>
		</tn-nav-bar>

		<view class="top-backgroup">
			<image src='https://tnuiimage.tnkjapp.com/swiper/summer.jpg' mode='widthFix' class='backgroud-image'>
			</image>
		</view>

		<view class="tnwave waveAnimation">
			<view class="waveWrapperInner bgTop">
				<view class="wave waveTop"
					style="background-image: url('https://tnuiimage.tnkjapp.com/wave/wave-2.png')"></view>
			</view>
			<view class="waveWrapperInner bgMiddle">
				<view class="wave waveMiddle"
					style="background-image: url('https://tnuiimage.tnkjapp.com/wave/wave-2.png')"></view>
			</view>
			<view class="waveWrapperInner bgBottom">
				<view class="wave waveBottom"
					style="background-image: url('https://tnuiimage.tnkjapp.com/wave/wave-1.png')"></view>
			</view>
		</view>

		<view class="integral-wrap" :style="{paddingTop: vuex_custom_bar_height + 'px'}">
			<!-- 头像用户信息 -->
			<view class="tn-flex tn-flex-direction-column tn-flex-col-center tn-flex-row-center">

				<view class="tn-text-bold tn-text-xxl tn-color-blue--dark">当前可用积分</view>
				<view class="tn-text-bold tn-text-xxl tn-color-blue tn-padding-top-sm">{{credits}}</view>
			</view>


			<!-- 消息&数据 -->
			<view class="shadow-warp" style="margin-top: 150rpx;background-color: rgba(255,255,255,1);">
				<view class="tn-flex tn-flex-row-around">
					<view class="tn-flex tn-flex-col-center tn-padding-sm tn-margin" @click="tn('/pages/rank/ranking')">
						<view
							class="icon1__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-bg-orangered tn-color-white">
							<view class="tn-icon-sword"></view>
						</view>
						<view class="tn-margin-left-sm tn-flex-1 tn-text-lg tn-text-bold tn-color-orangered">积分排行</view>
					</view>
					<view class="tn-flex tn-flex-col-center tn-padding-sm tn-margin"
						@click="tn('/pages/index?index=2')">
						<view
							class="icon1__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-bg-purplered tn-color-white">
							<view class="tn-icon-gift"></view>
						</view>
						<view class="tn-margin-left-sm tn-flex-1 tn-text-lg tn-text-bold tn-color-purplered">积分抽奖</view>
					</view>
				</view>
			</view>
		</view>

		<view class="tn-margin-top-lg tn-margin-bottom">
			<view class="tn-flex tn-flex-row-between tn-strip-bottom-min tn-padding"
				v-for="(item,index) in creditsLogList" :key="index">
				<view class="justify-content-item">
					<view class="tn-color-gray--dark tn-text-lg">
						{{item.actionName}}
					</view>
					<view class="tn-color-gray tn-padding-top-xs">
						{{item.createTime}}
					</view>
				</view>
				<view class="justify-content-item tn-text-xl tn-padding-top">
					<view class="tn-color-blue--dark" v-if="item.changeCredits >= 0"> +{{item.changeCredits}} </view>
					<view class="tn-color-purplered" v-else> {{item.changeCredits}} </view>
				</view>
			</view>
			<view @click="loadMore" style="height: 50px;">
				<tn-load-more class="tn-margin-top" :status="loadStatus" :loadText="loadText"></tn-load-more>
			</view>
		</view>

	</view>
</template>

<script>
	import template_page_mixin from '@/libs/mixin/template_page_mixin.js'
	import {
		getCreditsListByUser,
	} from '../../api/credits.js'
	import {
		getCurrentUserCredits,
	} from '../../api/user.js'
	export default {
		name: 'TemplateIntegral',
		mixins: [template_page_mixin],
		data() {
			return {
				pageSize: 10,
				currentPage: 1,
				total: 0,
				loadStatus: "loadmore",
				loadText: {
					loadmore: '点击加载更多',
					loading: '快速加载中...',
					nomore: '已经没有了啊'
				},
				creditsLogList: [],
				credits: 0,
			}
		},
		created() {
			this.getCurrentUserCreditsMethod()
			this.getCreditsListByUserMethod()
		},
		methods: {
			// 跳转
			tn(e) {
				uni.navigateTo({
					url: e,
				});
			},
			// 获取用户积分
			getCurrentUserCreditsMethod() {
				getCurrentUserCredits().then((res) => {
					console.log("获取当前用户积分", res)
					if (res.code == this.$ECode.SUCCESS) {
						this.credits = res.data;
					}
				})
			},
			loadMore() {
				console.log("加载更多")
				if (this.creditsLogList.length >= this.total) {
					this.loadStatus = "nomore";
					return;
				}
				this.currentPage = this.currentPage + 1;
				this.getCreditsListByUserMethod();
			},
			// 获取用户积分记录
			getCreditsListByUserMethod() {
				let params = {}
				params.pageSize = this.pageSize
				params.currentPage = this.currentPage
				getCreditsListByUser(params).then((res) => {
					console.log("获取用户积分列表", res)
					if (res.code == this.$ECode.SUCCESS) {

						var creditsLogList = this.creditsLogList.concat(res.data.records);
						this.creditsLogList = creditsLogList;
						this.total = res.data.total;
						//全部加载完毕
						if (this.creditsLogList.length >= this.total) {
							this.loadStatus = "nomore";
						} else {
							this.loadStatus = "loadmore";
						}
					}
				})
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

	/* 顶部背景图 start */
	.top-backgroup {
		height: 450rpx;
		z-index: -1;

		.backgroud-image {
			width: 100%;
			height: 450rpx;
			// z-index: -1;
		}
	}

	/* 顶部背景图 end */

	/* 页面 start*/
	.integral-shadow {
		border-radius: 15rpx;
		box-shadow: 0rpx 0rpx 50rpx 0rpx rgba(0, 0, 0, 0.07);
	}

	.integral-wrap {
		position: relative;
		z-index: 1;
		margin-top: -420rpx;
	}

	/* 页面 end*/

	/* 翘边阴影*/
	.shadow-warp {
		position: relative;
		box-shadow: 0 10rpx 10rpx rgba(0, 0, 0, 0.01);
	}

	.shadow-warp:before,
	.shadow-warp:after {
		position: absolute;
		content: "";
		top: 20rpx;
		bottom: 30rpx;
		left: 20rpx;
		width: 50%;
		box-shadow: 0 30rpx 20rpx rgba(0, 0, 0, 0.2);
		transform: rotate(-3deg);
		z-index: -1;
	}

	.shadow-warp:after {
		right: 20rpx;
		left: auto;
		transform: rotate(3deg);
	}

	/* 图标容器1 start */
	.icon1 {
		&__item {
			// width: 30%;
			background-color: #FFFFFF;
			border-radius: 10rpx;
			padding: 30rpx;
			margin: 20rpx 10rpx;
			transform: scale(1);
			transition: transform 0.3s linear;
			transform-origin: center center;

			&--icon {
				width: 50rpx;
				height: 50rpx;
				font-size: 30rpx;
				border-radius: 50%;
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
					background-image: url(https://tnuiimage.tnkjapp.com/cool_bg_image/icon_bg.png);
				}
			}
		}
	}

	/* 图标容器1 end */

	/* 波浪*/
	@keyframes move_wave {
		0% {
			transform: translateX(0) translateZ(0) scaleY(1)
		}

		50% {
			transform: translateX(-25%) translateZ(0) scaleY(1)
		}

		100% {
			transform: translateX(-50%) translateZ(0) scaleY(1)
		}
	}

	.tnwave {
		overflow: hidden;
		position: absolute;
		z-index: 0;
		height: 200rpx;
		left: 0;
		right: 0;
		top: 290rpx;
		bottom: auto;
	}

	.waveWrapperInner {
		position: absolute;
		width: 100%;
		overflow: hidden;
		height: 100%;
	}

	.wave {
		position: absolute;
		left: 0;
		width: 200%;
		height: 100%;
		background-repeat: repeat no-repeat;
		background-position: 0 bottom;
		transform-origin: center bottom;
	}

	.bgTop {
		opacity: 0.4;
	}

	.waveTop {
		background-size: 50% 45px;
	}

	.waveAnimation .waveTop {
		animation: move_wave 4s linear infinite;
	}

	.bgMiddle {
		opacity: 0.6;
	}

	.waveMiddle {
		background-size: 50% 40px;
	}

	.waveAnimation .waveMiddle {
		animation: move_wave 3.5s linear infinite;
	}

	.bgBottom {
		opacity: 0.95;
	}

	.waveBottom {
		background-size: 50% 35px;
	}

	.waveAnimation .waveBottom {
		animation: move_wave 2s linear infinite;
	}

	/* 波浪*/


	/* 间隔线 start*/
	.tn-strip-bottom-min {
		width: 100%;
		border-bottom: 1rpx solid #F8F9FB;
	}

	.tn-strip-bottom {
		width: 100%;
		border-bottom: 20rpx solid rgba(241, 241, 241, 0.8);
	}

	/* 间隔线 end*/
</style>