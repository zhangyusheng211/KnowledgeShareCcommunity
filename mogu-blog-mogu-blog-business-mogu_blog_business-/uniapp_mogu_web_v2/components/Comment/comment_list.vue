<template>
	<view class="tn-margin" style="padding-bottom: 120rpx;" v-if="openMobileComment == '1' || openMobileComment == '0'">
		<!-- 图标logo/头像 -->
		<view v-for="item in commentList" :key="item.uid">
			<view class="tn-flex tn-flex-row-between tn-flex-col-center tn-margin-top-xl">
				<view class="justify-content-item">
					<view class="tn-flex tn-flex-col-center tn-flex-row-left">
						<view class="logo-pic tn-shadow" @click="tn('/pages/mine/user_center?uid=' + item.user.uid)">
							<view class="logo-image">
								<view class="tn-shadow-blur" :style="{ backgroundImage: `url(${item.user.photoUrl})` }"
									style="width: 60rpx;height: 60rpx;background-size: cover;">
								</view>
							</view>
						</view>
						<view class="tn-padding-right tn-padding-left-sm">
							<view class="tn-padding-right tn-text-df tn-text-bold tn-color-black">
								{{item.user.nickName}}
							</view>
							<view class="tn-padding-right tn-text-ellipsis tn-text-xs tn-color-gray"
								style="padding-top: 5rpx;">
								{{item.createTime}}
							</view>
						</view>
					</view>
				</view>
				<!-- 				<view class="justify-content-item tn-flex-row-center tn-flex-col-center tn-color-gray"
					v-if="item.praiseInfo">
					<view class="tn-text-center">
						<text class="tn-icon-like-lack tn-padding-xs"></text>
					</view>
					<view class="tn-text-center">
						<text class="tn-text-xs">{{item.praiseInfo.praiseCount}}</text>
					</view>
				</view> -->

				<view class="justify-content-item tn-flex-row-center tn-flex-col-center tn-color-gray">
					<view class="tn-text-center">
						<text v-if="userInfo.uid == item.userUid" @click="delComment(item)"
							class="tn-icon-delete tn-padding-xs"></text>
						<text @click="replyComment(item)" class="tn-icon-comment tn-padding-xs"></text>
					</view>
				</view>
			</view>

			<view class="" style="margin: 20rpx 30rpx 30rpx 80rpx;" v-html="item.content">
				{{item.content}}
			</view>

			<view class="tn-bg-gray--light tn-padding-sm" v-if="item.replyList && item.replyList.length > 0"
				v-for="reply in item.replyList" :key="reply.uid"
				style="margin: 20rpx 30rpx 30rpx 80rpx;border-radius: 10rpx;">
				<text class="tn-text-bold tn-padding-right-xs">{{reply.user.nickName}}: </text>
				<view class="tn-color-blue" style="line-height: 40rpx; display: inline-block; margin-right: 10rpx;"
					v-if="reply.toUser" @click="tn('/pages/mine/user_center?uid=' + reply.toUser.uid)">
					@{{reply.toUser.nickName}}
				</view>
				<view style="line-height: 40rpx; display: inline-block;" v-html="reply.content"></view>
				<view class="tn-flex tn-flex-row-between tn-margin-top-xs">
					<view class="justify-content-item tn-text-xs tn-color-gray" style="padding-top: 5rpx;">
						{{reply.createTime}}
					</view>
					<view class="justify-content-item tn-text-xs tn-color-gray">
						<view class="tn-text-center">
							<text v-if="userInfo.uid == item.userUid" class="tn-icon-delete tn-padding-xs"
								@click="delComment(item)"></text>
							<text v-if="openMobileComment == '1'" class="tn-icon-comment tn-padding-xs"
								@click="replyComment(item)"></text>
						</view>
					</view>
				</view>
			</view>
		</view>


		<tn-modal v-model="showReply" :custom="true">
			<view class="custom-modal-content">
				<view class="tn-text-bold tn-text-md tn-padding-sm">评论回复</view>
				<tn-input v-model="replyContent" type="text" :border="true" :showRightIcon="true" />

				<view style="margin-top: 10px;">
					<tn-button backgroundColor="#01BEFF" fontColor="tn-color-white" width="48%" @click="reply">
						回复
					</tn-button>
					<tn-button style="margin-left: 10rpx" fontColor="tn-color-white" backgroundColor="#f56c6c"
						width="48%" @click="cancel">取消
					</tn-button>
				</view>

			</view>
		</tn-modal>

	</view>

</template>

<script>
	import {
		getCommentList,
		addComment,
		deleteComment,
	} from '../../api/comment.js';
	import {
		getWebConfig
	} from '../../api/about.js'
	export default {
		name: 'CommentList',
		props: {
			// 对应的资源ID
			targetId: [Number, String],
			// 对应的资源名称
			tableName: {
				type: String,
				default: '',
			},
			// 控制最大评论层级
			maxReplyLevel: {
				type: Number,
				default: 5,
			},
			// 左边偏移量
			marginLeft: {
				type: Number,
				default: -3,
			},
			// 作者UID
			authorUid: {
				type: String,
				default: '',
			},
		},
		data() {
			return {
				commentList: [],
				currentPage: 1,
				pageSize: 10,
				showReply: false,
				replyContent: '',
				toComment: {},
				isLogin: false,
				userInfo: {},
				openMobileComment: "0", // 是否开启移动端评论，（1：是，0：否）
			}
		},
		created() {
			let that = this
			this.getWebConfigData()
			this.getCommentListMethod()
			uni.getStorage({
				key: 'userInfo',
				success: function(res) {
					that.userInfo = res.data
					that.isLogin = true
				}
			});
		},
		methods: {
			// 跳转
			tn(e) {
				uni.navigateTo({
					url: e,
				});
			},
			refresh() {
				console.log("回调刷新列表")
				this.getCommentListMethod()
			},
			getWebConfigData() {
				let params = {}
				getWebConfig(params).then(res => {
					console.log("获取网站配置", res)
					if (res.code == this.$ECode.SUCCESS) {
						this.openMobileComment = res.data.openMobileComment
					}
				})
			},
			getCommentListMethod() {
				let params = {};
				let tableName = this.tableName
				let targetId = this.targetId
				console.log("参数配置", tableName, targetId)
				// 判断是否是对应详情页的数据
				if (tableName == "BLOG_INFO" && !targetId) {
					return
				}
				if (tableName == "USER_MOMENT" && !targetId) {
					return
				}
				if (tableName == "PROBLEM_INFO" && !targetId) {
					return
				}
				params.blogUid = targetId
				params.source = tableName
				params.currentPage = this.currentPage
				params.pageSize = this.pageSize
				params.openSecondLevel = true
				getCommentList(params).then((res) => {
					console.log("加载评论列表", res)
					if (res.code == this.$ECode.SUCCESS) {
						this.commentList = res.data.records
					}
				})
			},
			// 回复评论
			replyComment(toComment) {
				// 回复评论
				this.showReply = true
				this.toComment = toComment
			},
			reply() {
				if (this.replyContent == '') {
					return
				}
				let toComment = this.toComment
				let params = {};
				params.content = this.replyContent;
				params.blogUid = toComment.blogUid;
				params.toUid = toComment.uid;
				params.toUserUid = toComment.userUid;
				params.source = this.tableName
				addComment(params).then(response => {
					if (response.code == "success") {
						this.$message.success(response.message)
						this.cancel()
						this.refresh()
					} else {
						uni.showToast({
							title: response.data,
							icon: "none"
						})
						this.$message.error(response.message)
					}
				})
			},
			delComment(comment) {
				let params = {};
				params.uid = comment.uid;
				deleteComment(params).then(response => {
					if (response.code == "success") {
						this.$message.success(response.message)
						this.refresh()
					} else {
						uni.showToast({
							title: response.data,
							icon: "none"
						})
						this.$message.error(response.message)
					}
				})
			},
			cancel() {
				this.showReply = false
				this.replyContent = ''
			},
		}
	}
</script>

<style lang="scss" scoped>
	/* 头像 start */
	.logo-image {
		width: 60rpx;
		height: 60rpx;
		position: relative;
	}

	.logo-pic {
		background-size: cover;
		background-repeat: no-repeat;
		// background-attachment:fixed;
		background-position: top;
		box-shadow: 0rpx 0rpx 80rpx 0rpx rgba(0, 0, 0, 0.15);
		border-radius: 50%;
		overflow: hidden;
		// background-color: #FFFFFF;
	}
</style>