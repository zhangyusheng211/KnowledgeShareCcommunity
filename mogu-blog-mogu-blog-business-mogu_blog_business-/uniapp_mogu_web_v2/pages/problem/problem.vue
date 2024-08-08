<template>
	<view class="template-circle tn-safe-area-inset-bottom problemTabs">
		<!-- 顶部自定义导航 -->
		<!-- <tn-nav-bar fixed alpha customBack>
      <view slot="back" class='tn-custom-nav-bar__back'
        @click="goBack">
        <text class='icon tn-icon-left'></text>
        <text class='icon tn-icon-home-capsule-fill'></text>
      </view>
    </tn-nav-bar> -->

		<!-- <view :style="{paddingTop: vuex_custom_bar_height + 'px'}"></view> -->

		<view class="top-backgroup">
			<image src='https://oos.moguit.cn/image/problemBackground.png' mode='widthFix' class='backgroud-image'>
			</image>
		</view>

		<view class="tnwave waveAnimation">
			<view class="waveWrapperInner bgTop">
				<view class="wave waveTop" style="background-image: url('https://oos.moguit.cn/image/wave-2.png')">
				</view>
			</view>
			<view class="waveWrapperInner bgMiddle">
				<view class="wave waveMiddle" style="background-image: url('https://oos.moguit.cn/image/wave-2.png')">
				</view>
			</view>
			<view class="waveWrapperInner bgBottom">
				<view class="wave waveBottom" style="background-image: url('https://oos.moguit.cn/image/wave-1.png')">
				</view>
			</view>
		</view>

		<view class="about__wrap">
			<!-- 头像用户信息 -->
			<view class="user-info__container tn-flex tn-flex-direction-column tn-flex-col-center tn-flex-row-center">
				<view class="tn-flex tn-flex-row-around tn-margin-top-sm tn-color-white" style="align-items: center;">
					<view
						class="justify-content-item tn-text-center tn-margin-xs tn-radius bg-flex-shadow tn-shadow-blur tn-text-bold">
						<view>{{userProblemRecords.notMasteryProblemCount}}
						</view>
						<view>错误题数</view>
					</view>
					<view
						class="justify-content-item tn-padding-xl tn-text-center tn-margin-xs tn-radius bg-flex-shadow tn-shadow-blur">
						<tn-circle-progress
							:percent="(userProblemRecords.notMasteryProblemCount + userProblemRecords.masteryProblemCount) * 100 / (userProblemRecords.problemCount + 1) ">
							<view class="tn-color-white tn-text-bold" style="text-align: center;">
								{{((userProblemRecords.notMasteryProblemCount + userProblemRecords.masteryProblemCount) * 100 / userProblemRecords.problemCount).toFixed(2)}}%
								<br />
								刷题率
							</view>
						</tn-circle-progress>

					</view>

					<view
						class="justify-content-item  tn-color-white tn-text-center tn-margin-xs tn-radius bg-flex-shadow tn-shadow-blur  tn-text-bold">
						<view>{{userProblemRecords.masteryProblemCount }}</view>
						<view>正确题数</view>
					</view>
				</view>
			</view>

			<!-- 方式1 start-->
			<view class="tn-flex tn-message-fixed noticeTabs tn-margin-top-sm" v-if="vuex_custom_mini == '1'">
				<view class="tn-flex-1 tn-padding-sm tn-margin-xs tn-radius" @click="changeNoticeType('1')">
					<view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
						<view
							class="icon1__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-shadow-blur tn-bg-orange tn-color-white">
							<view class="tn-icon-topics-fill">
							</view>
						</view>
						<view class="tn-color-black tn-text-center">
							<text class="tn-text-ellipsis">智能练习</text>
						</view>
					</view>
				</view>
				<view class="tn-flex-1 tn-padding-sm tn-margin-xs tn-radius" @click="changeNoticeType('2')">
					<view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
						<view
							class="icon1__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-shadow-blur tn-bg-red tn-color-white">
							<view class="tn-icon-like-fill">
							</view>
						</view>
						<view class="tn-color-black tn-text-center">
							<text class="tn-text-ellipsis">错题集</text>
						</view>
					</view>
				</view>
				<view class="tn-flex-1 tn-padding-sm tn-margin-xs tn-radius" @click="changeNoticeType('3')">
					<view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
						<view
							class="icon1__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-shadow-blur tn-bg-cyan tn-color-white">
							<view class="tn-icon-praise-fill">
							</view>
						</view>
						<view class="tn-color-black tn-text-center">
							<text class="tn-text-ellipsis">收藏夹</text>
						</view>
					</view>
				</view>
				<view class="tn-flex-1 tn-padding-sm tn-margin-xs tn-radius" @click="changeNoticeType('6')">
					<view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
						<view
							class="icon1__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-shadow-blur tn-bg-orangeyellow tn-color-white">
							<view class="tn-icon-star-fill">
							</view>
						</view>
						<view class="tn-color-black tn-text-center">
							<text class="tn-text-ellipsis">笔记本</text>
						</view>
					</view>
				</view>
			</view>
			<!-- 方式1 end-->

			<view class="tn-flex tn-flex-row-between  tn-margin ">
				<view class="justify-content-item tn-text-bold">
					<text class="tn-text-df tn-color-black">试题分类</text>
				</view>
				<view class="justify-content-item tn-text-df tn-color-grey" @click="selectTag">
					<text class="tn-padding-xs">筛选</text>
					<text class="tn-icon-filter"></text>
				</view>
			</view>

			<!-- 更多信息-->
			<scroll-view :style="{height: `${swiperHeight}px`}" scroll-y @scrolltolower="loadMore">
				<view class="about-shadow tn-margin-top-sm" v-for="tagItem in tagList" :key="tagItem.uid"
					style="margin-left: 10px; margin-right: 10px;"
					@click="tn('/pages/problem/detail?tagUid=' + tagItem.uid)">
					<tn-list-cell :hover="true" :unlined="true" :radius="true" :fontSize="30">
						<view class="tn-flex tn-flex-col-center">
							<view
								class="icon1__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-cool-bg-color-5 tn-color-white">
								<view class="tn-icon-fire"></view>
							</view>
							<view class="tn-margin-left-sm tn-flex-1">
								<view>{{tagItem.summary?tagItem.summary:tagItem.name}}</view>
								<view class="tn-text-sm">共{{tagItem.problemCount}}题</view>
							</view>
							<view class="tn-margin-left-sm tn-text-xl tn-icon-sword"></view>
						</view>
					</tn-list-cell>
				</view>

			</scroll-view>

			<!-- 			<view @click="loadMore" style="height: 50px;">
				<tn-load-more class="tn-margin-top" :status="loadStatus" :loadText="loadText"></tn-load-more>
			</view> -->
		</view>

		<!-- 回到首页悬浮按钮-->
		<!-- <nav-index-button></nav-index-button> -->

		<!-- popup -->
		<tn-popup v-model="showPopup" :marginTop="vuex_custom_bar_height" length="92%" :mode="mode"
			:backgroundColor="backgroundColor" :width="width" :height="height" :borderRadius="borderRadius"
			:closeBtn="closeBtn" :closeBtnIcon="closeBtnIcon" :closeBtnPosition="closeBtnPosition"
			:maskCloseable="maskCloseable" @close="closedPopup">
			<view class="popup-content" :class="{'popup-content--center': mode === 'center'}"
				style="margin: 20px; overflow-y: scroll;">

				<view class="tn-flex tn-flex-row-between tn-flex-col-center" style="padding: 20rpx; width: 98%;">
					<tn-button backgroundColor="#24f083" @click="selectTagUidList = []"
						fontColor="#FFFFFF">清空筛选</tn-button>
					<tn-button backgroundColor="#01BEFF" @click="submitSearch" fontColor="#FFFFFF">提交搜索</tn-button>
				</view>

				<view v-for="tagItem in allTagList" :key="tagItem.uid">
					<view class="tn-text-bold tn-text-xl tn-text-center tn-margin-sm">{{tagItem.name}}</view>
					<tn-checkbox-group :size="46" :iconSize="40" v-model="selectTagUidList">
						<tn-checkbox :name="childrenItem.uid" v-for="childrenItem in tagItem.children"
							:key="childrenItem.uid">{{childrenItem.name}}</tn-checkbox>
					</tn-checkbox-group>

					<!-- 					<tn-checkbox-group :size="46" :iconSize="40">
						<tn-checkbox :name="childrenItem.name">{{childrenItem.name}}</tn-checkbox
							v-for="childrenItem in tagItem.children" :key="childrenItem.uid">
					</tn-checkbox-group> -->
				</view>


			</view>
		</tn-popup>

	</view>
</template>

<script>
	import template_page_mixin from '@/libs/mixin/template_page_mixin.js'
	import NavIndexButton from '@/libs/components/nav-index-button.vue'
	import {
		getProblemTagList,
		getAllProblemTagList,
		getUserProblemRecords,
	} from '../../api/problem.js'
	export default {
		name: 'about-demo-1',
		mixins: [template_page_mixin],
		components: {
			NavIndexButton
		},
		data() {
			return {
				userInfo: {},
				isLogin: {},
				showPopup: false,
				mode: 'bottom',
				backgroundColor: '',
				width: '',
				height: '',
				borderRadius: 0,
				closeBtn: true,
				closeBtnIcon: 'close',
				closeBtnPosition: 'top-right',
				maskCloseable: true,
				allTagList: [],
				tagList: [],
				pageSize: 10,
				currentPage: 1,
				total: 0,
				userProblemRecords: {},
				selectTagUidList: [],
				loadStatus: "loadmore",
				swiperHeight: 0,
				swiperTop: 0,
				loadText: {
					loadmore: '点击加载更多',
					loading: '快速加载中...',
					nomore: '已经没有了啊'
				}
			}
		},
		onLaunch() {

		},
		created() {
			console.log("展示触发钩子函数1")
			this.$nextTick(() => {
				this.updateSwiperInfo()
			})
			this.getUserProblemRecordsMethod()
			this.getProblemAllTagMethod()
			this.getProblemTagListMethod()
		},
		mounted() {
			console.log("展示触发钩子函数2")

		},
		onReady() {
			console.log("展示触发钩子函数3")
			this.$nextTick(() => {
				this.updateSwiperInfo()
			})
		},
		onShow() {
			console.log("展示触发钩子函数4")
			this.getUserProblemRecordsMethod()
		},
		methods: {
			// 计算可滑动区域的高度信息
			updateSwiperInfo() {
				// 获取可滑动菜单的信息
				this._tGetRect('.problemTabs').then(res => {
					if (!res) {
						setTimeout(() => {
							this.updateSwiperInfo()
						}, 10)
						return
					}
					const systemInfo = uni.getSystemInfoSync()
					this.swiperTop = res.bottom - this.vuex_custom_bar_height
					this.swiperHeight = systemInfo.safeArea.height - res.bottom + systemInfo.statusBarHeight - 20
					console.log("可滑动区域高度", this.swiperHeight)
				})
			},
			initData() {
				this.getUserProblemRecordsMethod()
				this.getProblemAllTagMethod()
				this.getProblemTagListMethod()
			},
			changeNoticeType(noticeType) {
				this.$message.error("功能正在建设中")
			},
			submitSearch() {
				// 提交搜索
				console.log("提交搜索", this.selectTagUidList)
				this.currentPage = 1
				this.tagList = []
				this.getProblemTagListMethod()
				this.closedPopup()
			},
			loadMore() {
				console.log("加载更多")
				if (this.tagList.length >= this.total) {
					this.loadStatus = "nomore";
					return;
				}
				this.currentPage = this.currentPage + 1;
				this.getProblemTagListMethod();
			},
			onShow() {
				// 页面显示时执行的代码
				console.log('页面显示');
			},
			// 关闭Popup
			closedPopup() {
				this.showPopup = false
			},
			selectTag() {
				this.showPopup = true
			},
			// 跳转
			tn(e) {
				console.log("切换URL:", e)
				uni.navigateTo({
					url: e,
				});
			},
			getProblemAllTagMethod() {
				let that = this
				getAllProblemTagList().then((res) => {
					console.log("获取所有标签", res.data)
					if (that.$ECode.SUCCESS == res.code) {
						this.allTagList = res.data
					} else {
						that.$message.error(res.message)
					}
				})
			},
			getProblemTagListMethod() {
				let that = this
				let tagParams = {}
				tagParams.tagLevel = 2
				tagParams.pageSize = this.pageSize;
				tagParams.currentPage = this.currentPage;
				tagParams.tagUidList = this.selectTagUidList
				tagParams.problemCountGe = 1 // 标签数问题数大于等于1
				getProblemTagList(tagParams).then((res) => {
					if (that.$ECode.SUCCESS == res.code) {
						this.total = res.data.total;
						this.currentPage = res.data.current;
						let list = res.data.records
						list = that.tagList.concat(list);
						that.tagList = list
					} else {
						that.$message.error(res.message)
					}
					console.log("获取标签列表", that.tagList)
				})
			},
			getUserProblemRecordsMethod() {
				// 获取用户刷题记录数
				let that = this
				getUserProblemRecords().then((res) => {
					console.log("获取用户刷题记录数", res.data)
					if (that.$ECode.SUCCESS == res.code) {
						this.userProblemRecords = res.data
					} else {
						that.$message.error(res.message)
					}
				})
			},

		}
	}
</script>

<style lang="scss" scoped>
	// @import '@/static/css/templatePage/custom_nav_bar.scss';
	.template-about {}

	/* 顶部背景图 start */
	.top-backgroup {
		height: 450rpx;
		z-index: -1;

		.backgroud-image {
			width: 100%;
			height: 300rpx;
			// z-index: -1;
		}
	}

	/* 顶部背景图 end */

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


	/* 图标容器1 start */
	.icon1 {
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
				width: 90rpx;
				height: 90rpx;
				font-size: 60rpx;
				border-radius: 50%;
				margin-bottom: 18rpx;
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

	.tnwave {
		overflow: hidden;
		position: absolute;
		z-index: 9999;
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

	/* 页面 start*/
	.about-shadow {
		border-radius: 15rpx;
		box-shadow: 0rpx 0rpx 50rpx 0rpx rgba(0, 0, 0, 0.07);
	}

	.about {

		&__wrap {
			position: relative;
			z-index: 1;
			// margin: 20rpx 30rpx;
			margin-top: -330rpx;
		}
	}


	/* 图标容器1 end */
</style>