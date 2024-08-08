<template>
	<view class="template-message">
		<!-- 顶部自定义导航 -->
		<tn-nav-bar fixed :bottomShadow="false" backTitle=" ">
			<view class="" @click="showModal">
				<text class="tn-text-lg">消息</text>
				<text class="tn-text-xl tn-padding-left-sm tn-icon-group-circle"></text>
			</view>
		</tn-nav-bar>

		<!-- <tn-nav-bar fixed :bottomShadow="false">消息通知</tn-nav-bar> -->

		<!-- 方式1 start-->
		<view class="tn-flex tn-message-fixed noticeTabs" :style="{paddingTop: vuex_custom_bar_height + 'px'}">
			<view class="tn-flex-1 tn-padding-sm tn-margin-xs tn-radius" @click="changeNoticeType('1')">
				<view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
					<view
						class="icon1__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-shadow-blur tn-bg-orange tn-color-white">
						<view class="tn-icon-topics-fill">
						</view>
					</view>
					<view class="tn-color-black tn-text-center">
						<text class="tn-text-ellipsis">评论</text>
					</view>
				</view>
			</view>
			<view class="tn-flex-1 tn-padding-sm tn-margin-xs tn-radius" @click="changeNoticeType('2')">
				<view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
					<view
						class="icon1__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-shadow-blur tn-bg-red tn-color-white">
						<view class="tn-icon-like-fill">
							<!-- 							<tn-badge backgroundColor="#E72F8C" fontColor="#FFFFFF" :absolute="true" :fontSize="16">
								<text>12</text>
							</tn-badge> -->
						</view>
					</view>
					<view class="tn-color-black tn-text-center">
						<text class="tn-text-ellipsis">关注</text>
					</view>
				</view>
			</view>
			<view class="tn-flex-1 tn-padding-sm tn-margin-xs tn-radius" @click="changeNoticeType('3')">
				<view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
					<view
						class="icon1__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-shadow-blur tn-bg-cyan tn-color-white">
						<view class="tn-icon-praise-fill">
							<!-- <tn-badge backgroundColor="#E72F8C" fontColor="#FFFFFF" :absolute="true" :fontSize="16">
                <text>39</text>
              </tn-badge> -->
						</view>
					</view>
					<view class="tn-color-black tn-text-center">
						<text class="tn-text-ellipsis">点赞</text>
					</view>
				</view>
			</view>
			<view class="tn-flex-1 tn-padding-sm tn-margin-xs tn-radius" @click="changeNoticeType('6')">
				<view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
					<view
						class="icon1__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-shadow-blur tn-bg-orangeyellow tn-color-white">
						<view class="tn-icon-star-fill">
							<!-- 							<tn-badge backgroundColor="#E72F8C" fontColor="#FFFFFF" :absolute="true" :fontSize="16">
								<text>99+</text>
							</tn-badge> -->
						</view>
					</view>
					<view class="tn-color-black tn-text-center">
						<text class="tn-text-ellipsis">收藏</text>
					</view>
				</view>
			</view>

			<view class="tn-flex-1 tn-padding-sm tn-margin-xs tn-radius" @click="changeNoticeType('4')">
				<view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
					<view
						class="icon1__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-shadow-blur tn-bg-blue tn-color-white">
						<view class="tn-icon-notice-fill">
							<!-- 							<tn-badge backgroundColor="#E72F8C" fontColor="#FFFFFF" :absolute="true" :fontSize="16">
								<text>99+</text>
							</tn-badge> -->
						</view>
					</view>
					<view class="tn-color-black tn-text-center">
						<text class="tn-text-ellipsis">系统</text>
					</view>
				</view>
			</view>
		</view>
		<!-- 方式1 end-->

		<tn-modal v-model="show1" :custom="true">
			<view class="custom-modal-content">
				<view class="tn-text-center tn-padding-top-sm tn-text-xxl tn-text-bold">提 示</view>
				<view class="tn-text-center tn-padding-top tn-text-lg tn-color-gray">确定将所有通知都清空吗？</view>
				<view class="tn-flex tn-flex-row-between tn-margin-top-xl">
					<view class="tn-flex-1 justify-content-item tn-margin-xs tn-text-center">
						<tn-button backgroundColor="#00FFC6" padding="40rpx 0" width="90%" shadow fontBold shape="round"
							@click="cancelDeleteNotice">
							<text class="tn-color-black">取 消</text>
						</tn-button>
					</view>
					<view class="tn-flex-1 justify-content-item tn-margin-xs tn-text-center">
						<tn-button backgroundColor="#FFF00D" padding="40rpx 0" width="90%" shadow fontBold shape="round"
							@click="batchDeleteNotice">
							<text class="tn-color-black">确 定</text>
						</tn-button>
					</view>
				</view>
			</view>
		</tn-modal>

		<view class="tn-safe-area-inset-bottom tn-margin-bottom-sm" style="margin-top: 260rpx; "
			:style="{paddingTop: vuex_custom_bar_height + 'px'}">
			<tn-swipe-action>
				<scroll-view :style="{height: `${swiperHeight}px`}" scroll-y @scrolltolower="loadMore">
					<tn-swipe-action-item v-for="(item, index) in noticeList" :key="index" :name="item.uid"
						:options="options">
						<view class="tn-flex tn-flex-row-between tn-flex-col-center tn-padding">
							<view class="justify-content-item">
								<view class="tn-flex tn-flex-col-center tn-flex-row-left">
									<view class="logo-pic">
										<view class="logo-image">

											<view class="tn-shadow-blur" v-if="item.noticeType == '1'"
												:style="'background-image:url(' + item.comment.user.photoUrl + ')'"
												style="background-repeat: no-repeat;width: 90rpx;height: 90rpx; background-size: cover;">
											</view>

											<view class="tn-shadow-blur" v-if="item.noticeType == '2'"
												:style="'background-image:url(' + item.userWatch.user.photoUrl + ')'"
												style="background-repeat: no-repeat;width: 90rpx;height: 90rpx; background-size: cover;">
											</view>

											<view class="tn-shadow-blur" v-if="item.noticeType == '3'"
												:style="'background-image:url(' + item.fromUser.photoUrl + ')'"
												style="background-repeat: no-repeat;width: 90rpx;height: 90rpx; background-size: cover;">
											</view>

											<view class="tn-shadow-blur" v-if="item.noticeType == '6'"
												:style="'background-image:url(' + item.fromUser.photoUrl + ')'"
												style="background-repeat: no-repeat;width: 90rpx;height: 90rpx; background-size: cover;">
											</view>

											<view class="tn-shadow-blur" v-if="item.noticeType == '4'"
												:style="'background-image:url(' + webConfig.logoPhoto + ')'"
												style="background-repeat: no-repeat;width: 90rpx;height: 90rpx; background-size: cover;">
											</view>
										</view>
									</view>
									<view class="tn-padding-right tn-color-black">
										<view class="tn-padding-right tn-padding-left-sm tn-text-md tn-text-bold">
											<view v-if="noticeType == '1'">{{item.comment.user.nickName}}</view>
											<view v-if="noticeType == '2'">{{item.userWatch.user.nickName}}</view>
											<view v-if="noticeType == '3'">{{item.fromUser.nickName}}</view>
											<view v-if="noticeType == '6'">{{item.fromUser.nickName}}</view>
											<view v-if="noticeType == '4'">系统</view>

											<view
												style="position: absolute; right: 10rpx;  margin-top: -40rpx; font-size: 14px; font-weight: 500;">
												{{timeAgo(item.createTime)}}
											</view>
										</view>
										<view
											class="tn-padding-right tn-padding-top-xs tn-text-ellipsis tn-padding-left-sm">

											<view v-if="item.businessType == 1"
												@click="tn('/pages/article/details?oid=' + $commonUtil.variableJudge(item, '.comment.blog.oid') )">
												<text class="tn-color-grey">
													评论了你的文章
													<!-- : {{splitText(item.comment.blog.title)}} -->
												</text>
											</view>
											<view v-else-if="item.businessType == 2">
												<text class="tn-color-grey">
													评论了你的问答
													<!-- :{{splitText(item.comment.question.title)}} -->
												</text>
											</view>
											<view v-else-if="item.businessType == 3"
												@click="tn('/pages/article/details?oid=' + $commonUtil.variableJudge(item, '.comment.blog.oid') )">
												<text class="tn-color-grey">在文章
													<!-- :{{splitText(item.comment.blog.title)}} -->
													中，回复了你的评论
												</text>
											</view>
											<view v-else-if="item.businessType == 4">
												<text class="tn-color-grey">
													在问答
													<!-- :{{splitText(item.comment.question.title)}} -->中，回复了你的评论
												</text>
											</view>
											<view v-else-if="item.businessType == 10">
												<text class="tn-color-grey">
													在留言板页面中，回复了你的评论
												</text>
											</view>
											<view v-else-if="item.businessType == 11">
												<text class="tn-color-grey">
													在关于我页面中，回复了你的评论
												</text>
											</view>
											<view v-else-if="item.businessType == 12"
												@click="tn('/pages/moment/details?uid=' + $commonUtil.variableJudge(item, '.comment.blogUid') )">
												<text class="tn-color-grey">
													评论了你的动态
												</text>
											</view>
											<view v-else-if="item.businessType == 13"
												@click="tn('/pages/moment/details?uid=' + $commonUtil.variableJudge(item, '.comment.blogUid') )">
												<text class="tn-color-grey">
													在动态中，回复了你的评论
												</text>
											</view>
											<view v-else-if="item.businessType == 15">
												<text class="tn-color-grey">
													在课程中，回复了你的评论
												</text>
											</view>
											<view v-else-if="item.businessType == 25">
												<text class="tn-color-grey">
													在问题
													<!-- ：{{splitText(item.comment.problem.title)}} -->中，回复了你的评论
												</text>
											</view>
											<view v-else-if="item.businessType == 26">
												<text class="tn-color-grey">
													评论了你的问题
													<!-- ：{{splitText(item.comment.problem.title)}} -->
												</text>
											</view>

											<!--关注相关-->
											<view v-else-if="item.businessType == 5">
												<text class="tn-color-grey">
													关注了你
												</text>
											</view>

											<!--点赞相关-->
											<view v-else-if="item.businessType == 6"
												@click="tn('/pages/article/details?oid=' + $commonUtil.variableJudge(item, '.blog.oid') )">
												<text class="tn-color-grey">
													给你的文章
													<!-- ：{{splitText(item.blog.title)}} -->点赞
												</text>
											</view>

											<view v-else-if="item.businessType == 7">
												<text class="tn-color-grey">
													给你的问答
													<!-- ：{{splitText(item.question.title)}} -->点赞
												</text>
											</view>

											<view v-else-if="item.businessType == 14"
												@click="tn('/pages/moment/details?uid=' + $commonUtil.variableJudge(item, '.userMoment.uid') )">
												<text class="tn-color-grey">
													给你的动态
													<!-- ：{{splitText(item.userMoment.content)}} -->点赞
												</text>
											</view>

											<view v-else-if="item.businessType == 17">
												<text class="tn-color-grey">
													给你的评论
													<!-- ：{{splitText(item.comment.content)}} -->点赞
												</text>
											</view>

											<view v-else-if="item.businessType == 24">
												<text class="tn-color-grey">
													给你的问题
													<!-- ：{{splitText(item.problem.title)}} -->点赞
												</text>
											</view>

											<!--收藏相关-->
											<view v-else-if="item.businessType == 27"
												@click="tn('/pages/article/details?oid=' + $commonUtil.variableJudge(item, '.blog.oid') )">
												<text class="tn-color-grey">
													收藏了你的文章
													<!-- ：{{splitText(item.blog.title)}} -->
												</text>
											</view>
											<view v-else-if="item.businessType == 28">
												<text class="tn-color-grey">
													收藏了你的问答
													<!-- ：{{splitText(item.question.title)}} -->
												</text>
											</view>
											<view v-else-if="item.businessType == 29">
												<text class="tn-color-grey">
													收藏了你的面经
													<!-- ：{{splitText(item.problem.title)}} -->
												</text>
											</view>
											<view v-else-if="item.businessType == 30">
												<text class="tn-color-grey">
													收藏了你的评论
													<!-- ：{{splitText(item.comment.title)}} -->
												</text>
											</view>
											<view v-else-if="item.businessType == 31"
												@click="tn('/pages/moment/details?uid=' + $commonUtil.variableJudge(item, '.userMoment.uid') )">
												<text class="tn-color-grey">
													收藏了你的动态
													<!-- ：{{splitText(item.userMoment.content)}} -->
												</text>
											</view>

											<!--系统通知相关-->
											<view v-else-if="item.businessType == 8">
												<text class="tn-color-grey">
													你关注的 {{splitText(item.blog.user.nickName)}} 发表了新文章
												</text>
											</view>

											<view v-else-if="item.businessType == 22">
												<text class="tn-color-grey">
													你关注的 {{splitText(item.userMoment.user.nickName)}} 发表了新动态
												</text>
											</view>

											<view v-else-if="item.businessType == 60">
												<text class="tn-color-grey">
													在文章中提及到你
												</text>
											</view>

											<view v-else-if="item.businessType == 61">
												<text class="tn-color-grey">
													在问答中提及到你
												</text>
											</view>
											<view v-else-if="item.businessType == 62">
												<text class="tn-color-grey">
													在面经中提及到你
												</text>
											</view>

											<view v-else-if="item.businessType == 63">
												<text class="tn-color-grey">
													在评论中提及到你
												</text>
											</view>

											<view v-else-if="item.businessType == 64">
												<text class="tn-color-grey">
													在动态中提及到你
												</text>
											</view>

											<view v-else-if="item.businessType == 65">
												<text class="tn-color-grey">
													在聊天中提及到你
												</text>
											</view>

											<view v-else>
												<text class="tn-color-grey">
													{{item.content}}
												</text>
											</view>

										</view>
									</view>
								</view>
							</view>
							<!-- 						<view class="justify-content-item">
									<view class="tn-flex tn-flex-row-right">
										<view class="">{{timeAgo(item.createTime)}}</view>
									</view>
									<view class="tn-flex tn-flex-row-right">
										<tn-badge backgroundColor="tn-cool-bg-color-1" fontColor="tn-color-white" :radius="40"
											margin="10rpx 0rpx 10rpx 10rpx"><text>6</text></tn-badge>
									</view>
								</view> -->
						</view>
					</tn-swipe-action-item>
				</scroll-view>
			</tn-swipe-action>
		</view>



	</view>
</template>

<script>
	import template_page_mixin from '@/libs/mixin/template_page_mixin.js'
	import {
		getNoticeList,
		deleteNotice,
		deleteBatchNotice,
	} from '../../api/notice.js'
	import {
		getWebConfig
	} from '../../api/about.js'

	import {
		timeAgo
	} from '../../utils/webUtils.js'
	export default {
		name: 'TemplateMessage',
		mixins: [template_page_mixin],
		data() {
			return {
				show1: false,
				options: [{
					icon: 'delete',
					style: {
						backgroundColor: '#E83A30',
						width: '80rpx',
						height: '80rpx',
						margin: '0 12rpx',
						borderRadius: '100rpx'
					}
				}],
				noticeList: [],
				pageSize: 20,
				currentPage: 1,
				total: 0,
				noticeType: '1',
				webConfig: {},
				swiperHeight: 0,
				loadStatus: "loadmore",
				loadText: {
					loadmore: '点击加载更多',
					loading: '快速加载中...',
					nomore: '已经没有了啊'
				},
			}
		},
		onLoad() {

		},
		created() {
			this.getWebConfigMethod()
			this.getNoticeListMethod()
		},
		onReady() {
			this.$nextTick(() => {
				this.updateSwiperInfo()
			})
		},
		methods: {
			// 跳转
			tn(e) {
				console.log("开始跳转", e)
				uni.navigateTo({
					url: e
				});
			},
			// 跳转详情页
			handleGoDetail(item) {
				console.log("跳转详情页", item)
			},
			// 计算可滑动区域的高度信息
			updateSwiperInfo() {
				// 获取可滑动菜单的信息
				this._tGetRect('.noticeTabs').then(res => {
					if (!res) {
						setTimeout(() => {
							this.updateSwiperInfo()
						}, 10)
						return
					}
					const systemInfo = uni.getSystemInfoSync()
					this.swiperTop = res.bottom - this.vuex_custom_bar_height
					this.swiperHeight = systemInfo.safeArea.height - res.bottom + systemInfo.statusBarHeight
					console.log("可滑动区域高度", this.swiperHeight)
				})
			},
			cancelDeleteNotice() {
				this.show1 = false
			},
			batchDeleteNotice() {
				let noticeList = this.noticeList
				if (noticeList.length == 0) {
					this.$message.error("暂无通知")
					return
				}
				let params = []
				for (let a = 0; a < noticeList.length; a++) {
					let temp = {};
					temp.uid = noticeList[a].uid;
					params.push(temp)
				}
				deleteBatchNotice(params).then(response => {
					if (response.code == this.$ECode.SUCCESS) {
						this.$message.success(response.message)
						this.noticeList = []
						this.getNoticeListMethod()
					} else {
						this.$message.error(response.message)
					}
				});
			},
			handleDelete: function(uid) {
				console.log("删除通知", uid)
				let params = {};
				params.uid = uid;
				deleteNotice(params).then(response => {
					if (response.code == this.$ECode.SUCCESS) {
						this.$message.success(response.message)
						let noticeList = this.noticeList
						noticeList = noticeList.filter(item => item.uid !== uid);
						this.noticeList = noticeList
					} else {
						this.$message.error(response.message)
					}
				});
			},
			// 弹出模态框
			showModal(event) {
				this.openModal()
			},
			// 打开模态框
			openModal() {
				this.show1 = true
			},
			splitText(str) {
				if (str.length < 15) {
					return str
				}
				return str.substring(0, 15) + "."
			},
			changeNoticeType(noticeType) {
				this.noticeList = []
				this.noticeType = noticeType
				this.pageSize = 10
				this.currentPage = 1
				this.getNoticeListMethod()
			},
			getWebConfigMethod() {
				getWebConfig().then(response => {
					console.log("获取网站配置", response)
					if (response.code == this.$ECode.SUCCESS) {
						this.webConfig = response.data
					}
				})
			},
			loadMore() {
				console.log("加载更多")
				if (this.noticeList.length >= this.total) {
					this.loadStatus = "nomore";
					return;
				}
				this.currentPage = this.currentPage + 1;
				this.getNoticeListMethod();
			},
			getNoticeListMethod() {
				let params = {}
				params.pageSize = this.pageSize
				params.currentPage = this.currentPage
				params.noticeType = this.noticeType
				getNoticeList(params).then(response => {
					console.log("获取通知", response)
					if (response.code == this.$ECode.SUCCESS) {
						var noticeList = this.noticeList.concat(response.data.records);
						this.noticeList = noticeList
						this.pageSize = response.data.size
						this.currentPage = response.data.current
						this.total = response.data.total
						//全部加载完毕
						if (noticeList.length >= this.total) {
							this.loadStatus = "nomore";
						} else {
							this.loadStatus = "loadmore";
						}
					}
				})
			},
			timeAgo(dateTime) {
				return timeAgo(dateTime)
			},
		}
	}
</script>

<style lang="scss" scoped>
	.template-message {}

	.tn-message-fixed {
		position: fixed;
		background-color: rgba(255, 255, 255, 1);
		box-shadow: 0rpx 0rpx 30rpx 0rpx rgba(0, 0, 0, 0.07);
		top: 0;
		width: 100%;
		transition: all 0.25s ease-out;
		z-index: 100;
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

	/* 用户头像 start */
	.logo-image {
		width: 90rpx;
		height: 90rpx;
		position: relative;
	}

	.logo-pic {
		background-size: cover;
		background-repeat: no-repeat;
		// background-attachment:fixed;
		background-position: top;
		// box-shadow: 0rpx 0rpx 80rpx 0rpx rgba(0, 0, 0, 0.15);
		border-radius: 50%;
		overflow: hidden;
		// background-color: #FFFFFF;
	}
</style>