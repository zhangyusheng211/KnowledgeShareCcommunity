<template>
	<view class="tabbar footerfixed dd-glass" v-if="openMobileComment == '1' || openMobileComment == '0'">
		<view class="tn-flex tn-flex-row-between tn-flex-col-center">
			<view class="justify-content-item tn-margin-top">
				<view class="tn-flex tn-flex-row-center tn-flex-col-center">
					<view class="tn-flex tn-flex-row-center tn-flex-col-center tn-padding-right tn-padding-left-sm">
						<view class="avatar-all">
							<view v-if="isLogin" class="tn-shadow-blur"
								:style="{ backgroundImage: `url(${userInfo.photoUrl})` }"
								style="width: 60rpx;height: 60rpx;background-size: cover;">
							</view>
							<view v-else class="tn-shadow-blur"
								style="background-image: url('https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'); width: 60rpx;height: 60rpx;background-size: cover;">
							</view>
						</view>
					</view>


					<view
						class="topic__info__item__input tn-flex tn-flex-direction-row tn-flex-nowrap tn-flex-col-center tn-flex-row-left">
						<!-- 						<view class="topic__info__item__input__left-icon">
							<view class="tn-icon-emoji-good"></view>
						</view> -->
						<view class="topic__info__item__input__content">
							<input style="font-size: 25rpx;" maxlength="1024" placeholder-class="input-placeholder"
								:cursor-spacing="18" v-model="content" placeholder="既然来了,那就留下些什么吧~" />
						</view>
					</view>
				</view>
			</view>
			<view class="justify-content-item tn-flex-row-center tn-flex-col-center tn-margin-top tn-margin-right">
				<view class="topic__info__item__sure">
					<view class="tn-flex-1 justify-content-item tn-text-center">
						<tn-button shape="round" backgroundColor="tn-cool-bg-color-15--reverse" width="100%" shadow
							@click="addCommentMethod">
							<text class="tn-color-white" hover-class="tn-hover" :hover-stay-time="150">
								发 送
							</text>
						</tn-button>
					</view>
				</view>
			</view>
		</view>
	</view>

</template>

<script>
	import {
		addComment,
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
		},
		data() {
			return {
				commentList: [],
				currentPage: 1,
				pageSize: 10,
				content: "",
				userInfo: {},
				isLogin: {},
				openMobileComment: "0", // 是否开启移动端评论，（1：是，0：否）
			}
		},
		created() {
			let that = this
			// 获取用户信息
			uni.getStorage({
				key: 'userInfo',
				success: function(res) {
					console.log("加载用户信息", res.data)
					that.userInfo = res.data
					that.isLogin = true
				}
			});
			this.getWebConfigData()
		},
		methods: {
			getWebConfigData() {
				let params = {}
				getWebConfig(params).then(res => {
					console.log("获取CommonBox网站配置", res)
					if (res.code == this.$ECode.SUCCESS) {
						this.openMobileComment = res.data.openMobileComment
					}
				})
			},
			addCommentMethod() {
				if (!this.isLogin) {
					this.$message.error("请先登录")
					setTimeout(() => {
						uni.navigateTo({
							url: '/pages/index?index=4'
						})
					}, 1000)
					return
				}
				if (this.content == '') {
					return
				}
				let that = this
				let params = {};
				params.source = this.tableName;
				params.userUid = this.userInfo.uid;
				params.content = this.content;
				params.blogUid = this.targetId;
				addComment(params).then(response => {
					if (response.code === that.$ECode.SUCCESS) {
						that.content = ''
						if (response.data.auditStatus === "2") {
							that.$message.success("评论成功！");
							that.$emit("callback", "")
						} else {
							that.$message.warning("提交成功，请耐心等待审核~");
						}
					} else {
						that.$message.error(response.message)
					}
				});
			},
		},
	}
</script>

<style lang="scss" scoped>
	/* 毛玻璃*/
	.dd-glass {
		width: 100%;
		backdrop-filter: blur(20rpx);
		-webkit-backdrop-filter: blur(20rpx);
	}

	.tabbar {
		align-items: center;
		min-height: 130rpx;
		padding: 0;
		height: calc(130rpx + env(safe-area-inset-bottom) / 2);
		padding-bottom: calc(30rpx + env(safe-area-inset-bottom) / 2);
		padding-left: 10rpx;
		padding-right: 10rpx;
	}

	/* 内容*/
	.topic {
		position: relative;
		height: 100%;
		z-index: 1;
		margin-bottom: 120rpx;


		/* 表单信息 start */
		&__info {
			margin: 0 50rpx;
			margin-top: 105rpx;
			padding: 30rpx 51rpx;
			border-radius: 20rpx;
			background-color: rgba(255, 255, 255, 1);
			border: 2rpx solid rgba(255, 255, 255, 0.1);
			box-shadow: 0rpx 10rpx 50rpx 0rpx rgba(0, 3, 72, 0.1);

			&__item {

				&__input {
					width: 440rpx;
					height: 60rpx;
					border: 1rpx solid #C6D1D8;
					border-radius: 39rpx;

					&__left-icon {
						width: 10%;
						font-size: 44rpx;
						margin-left: 20rpx;
						margin-right: 5rpx;
						color: #C6D1D8;
					}

					&__content {
						width: 80%;
						padding-left: 10rpx;

						&--verify-code {
							width: 56%;
						}

						input {
							font-size: 30rpx;
							color: #78909C;
							// letter-spacing: 0.1em;
						}
					}

					&__right-icon {
						width: 10%;
						font-size: 34rpx;
						margin-right: 20rpx;
						color: #78909C;
					}

					&__right-verify-code {
						width: 34%;
						margin-right: 20rpx;
					}
				}

				&__button {
					width: 100%;
					height: 60rpx;
					text-align: center;
					font-size: 31rpx;
					font-weight: bold;
					line-height: 77rpx;
					// text-indent: 1em;
					border-radius: 100rpx;
					color: #FFFFFF;
					background-color: rgba(255, 255, 255, 0.2);
					// border: 2rpx solid #FFFFFF;
				}

				&__sure {
					height: 60rpx;
					width: 140rpx;
				}

			}
		}

		/* 表单信息 end */

		/* 内容 end */

	}

	/* 头像*/
	.avatar-all {
		width: 60rpx;
		height: 60rpx;
		border: 4rpx solid rgba(255, 255, 255, 0.05);
		border-radius: 50%;
		overflow: hidden;
		box-shadow: 0rpx 0rpx 80rpx 0rpx rgba(0, 0, 0, 0.15);
	}

	/* 底部 start*/
	.footerfixed {
		position: fixed;
		width: 100%;
		bottom: 0;
		z-index: 999;
		background-color: rgba(255, 255, 255, 0.5);
		box-shadow: 0rpx 0rpx 30rpx 0rpx rgba(0, 0, 0, 0.07);
	}


	/deep/.input-placeholder {
		font-size: 25rpx;
		color: #C6D1D8;
	}
</style>