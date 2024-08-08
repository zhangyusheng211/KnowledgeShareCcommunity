<template>
	<view class="blogger__author tn-flex tn-flex-row-between tn-flex-col-center tn-margin-sm">
		<view class="justify__author__info">
			<view class="tn-flex tn-flex-row-center">
				<view class="tn-flex tn-flex-row-center tn-flex-col-center">
					<view @click="tn('/pages/mine/user_center?uid=' + userDetail.uid)">
						<tn-avatar class="" shape="circle" :src="userDetail.photoUrl" size="small">
						</tn-avatar>
					</view>
					<view @click="tn('/pages/mine/user_center?uid=' + userDetail.uid)"
						class="tn-padding-right tn-text-ellipsis" :style="{width: (windowWidth - 150) + 'px'}">
						<view class="tn-padding-right tn-padding-left-sm tn-text-bold tn-text-sm">
							{{ userDetail.nickName }}
						</view>
						<view class="tn-padding-right tn-padding-left-sm tn-padding-top-xs tn-color-gray tn-text-sm">
							{{ userDetail.summary?userDetail.summary:'这家伙很懒，什么也没留下' }}
						</view>
					</view>

					<view v-if="userWatchStatus == 0" @click="watchUser"
						class="blogger__author__btn justify-content-item tn-flex-col-center tn-flex-row-center">
						<text class="tn-round tn-text-df tn-text-bold tn-color-white"
							style="padding: 10rpx 24rpx; font-size: 12px; background-color: #f56c6c;">关注</text>
					</view>
					<view v-if="userWatchStatus == 1" @click="watchUser"
						class="blogger__author__btn justify-content-item tn-flex-col-center tn-flex-row-center">
						<text class="tn-round tn-text-df tn-text-bold tn-color-white"
							style="padding: 10rpx 24rpx; font-size: 12px; background-color: #909399;">取关</text>
					</view>
					<view v-if="userWatchStatus == 2" @click="watchUser"
						class="blogger__author__btn justify-content-item tn-flex-col-center tn-flex-row-center">
						<text class=" tn-round tn-text-df tn-text-bold tn-color-white"
							style="padding: 10rpx 24rpx; font-size: 12px; background-color: #67c23a;">互关</text>
					</view>
				</view>
			</view>
		</view>
	</view>

</template>

<script>
	import {
		checkCurrentUserWatch,
		addUserWatch,
		deleteUserWatch,
	} from '../../api/about.js'
	export default {
		name: 'UserInfoDetail',
		props: {
			// 用户信息
			userDetail: {
				type: Object,
				default: {},
			},
			// 开启关注
			openWatch: {
				type: Boolean,
				default: false
			},
		},
		data() {
			return {
				userInfo: {},
				isLogin: false,
				userWatchStatus: 0,
				windowWidth: uni.getSystemInfoSync().windowWidth,
			}
		},
		watch: {
			// 判断用户信息
			'userDetail': function(newFlag, oldFlag) {
				this.getCheckCurrentUserWatch()
			},
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
					that.getCheckCurrentUserWatch()
				}
			});
			console.log("用户信息", this.userDetail)
		},
		mounted() {

		},
		methods: {
			// 获取用户关注的状态
			getCheckCurrentUserWatch: function() {
				console.log("获取用户关注状态")
				let isLogin = this.isLogin
				if (!isLogin || !this.userDetail || !this.userDetail.uid) {
					return
				}
				let params = {}
				params.toUserUid = this.userDetail.uid
				checkCurrentUserWatch(params).then(response => {
					console.log("获取用户关注状态", response)
					if (response.code === this.$ECode.SUCCESS) {
						this.userWatchStatus = response.data
					}
				})
			},
			// 跳转
			tn(e) {
				uni.navigateTo({
					url: e
				});
			},
			watchUser() {
				let userWatchStatus = this.userWatchStatus
				if (userWatchStatus == 0) {
					console.log("关注用户")
					this.watchOtherUser()
				} else {
					console.log("取消关注用户")
					this.unWatchOtherUser()
				}
			},
			// 关注用户
			watchOtherUser: function() {
				let isLogin = this.isLogin
				if (!isLogin || !this.userDetail || !this.userDetail.uid) {
					this.$message.error("登录后才可以关注哦")
					return;
				}
				let params = {}
				params.toUserUid = this.userDetail.uid
				addUserWatch(params).then(response => {
					console.log("关注用户", response)
					if (response.code == this.$ECode.SUCCESS) {
						this.$message.success(response.message)
						this.getCheckCurrentUserWatch()
					} else {
						this.$message.error(response.message)
					}
				})
			},

			// 取消关注用户
			unWatchOtherUser: function() {
				let isLogin = this.isLogin
				if (!isLogin || !this.userDetail || !this.userDetail.uid) {
					this.$message.error("登录后才可以关注哦")
					return;
				}

				let params = {}
				params.toUserUid = this.userDetail.uid
				deleteUserWatch(params).then(response => {
					if (response.code == this.$ECode.SUCCESS) {
						this.$message.success(response.message)
						this.getCheckCurrentUserWatch()
					} else {
						this.$message.error(response.message)
					}
				})
			},

		},
	}
</script>

<style>
</style>