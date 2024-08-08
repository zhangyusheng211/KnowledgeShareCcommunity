<template>
	<view class="tn-flex tn-flex-row-between" style="margin: 40rpx 0 60rpx 0;">
		<view class="tn-flex-1 justify-content-item tn-margin-xs tn-text-center">
			<tn-button @click="praiseMethod" backgroundColor="#00FFC6" padding="40rpx 0" width="90%" shadow fontBold>
				<text v-if="isPraise" class="tn-icon-praise-fill tn-padding-right-xs tn-color-black"></text>
				<text v-else class="tn-icon-praise tn-padding-right-xs tn-color-black"></text>

				<text class="tn-color-black" v-if="praiseCount > 0">点 赞({{praiseCount}})</text>
				<text class="tn-color-black" v-else>点 赞</text>
			</tn-button>

		</view>
		<view class="tn-flex-1 justify-content-item tn-margin-xs tn-text-center">
			<tn-button @click="collectMethod" backgroundColor="#FFF00D" padding="40rpx 0" width="90%" shadow fontBold>
				<text v-if="isCollect" class="tn-icon-star-fill tn-padding-right-xs tn-color-black"></text>
				<text v-else class="tn-icon-star tn-padding-right-xs tn-color-black"></text>

				<text class="tn-color-black" v-if="collectCount > 0">收 藏({{collectCount}})</text>
				<text class="tn-color-black" v-else>收 藏</text>
			</tn-button>
		</view>
	</view>

</template>

<script>
	import {
		addPraise,
		cancelPraise,
	} from '../../api/praise.js'
	import {
		addCollect,
		deleteCollect,
	} from '../../api/collect.js'


	export default {
		name: 'UserInfoDetail',
		props: {
			// 业务ID
			resourceUid: {
				type: String,
				default: "",
			},
			resourceType: {
				type: String,
				default: "",
			},
			praiseCountValue: {
				type: Number,
				default: 0
			},
			// 是否点赞
			isPraiseValue: {
				type: Boolean,
				default: false
			},
			// 是否收藏
			isCollectValue: {
				type: Boolean,
				default: false
			},
			collectCountValue: {
				type: Number,
				default: 0
			},
		},
		data() {
			return {
				userInfo: {},
				isLogin: {},
				isPraise: false,
				isTread: false,
				isCollect: false,
				praiseCount: 0,
				treadCount: 0,
				collectCount: 0,
			}
		},
		watch: {
			// 判断用户信息
			'resourceUid': function(newFlag, oldFlag) {
				this.initPraiseInfo()
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
				}
			});

		},
		mounted() {

		},
		methods: {
			initPraiseInfo() {
				this.isPraise = this.isPraiseValue
				this.isCollect = this.isCollectValue
				this.praiseCount = this.praiseCountValue
				this.collectCount = this.collectCountValue
			},
			praiseMethod() {
				let isPraise = this.isPraise
				if (isPraise) {
					this.cancelUserPraise()
				} else {
					this.praise()
				}
			},
			collectMethod() {
				let isCollect = this.isCollect
				if (isCollect) {
					this.cancelUserCollect()
				} else {
					this.addUserCollect()
				}
			},
			praise() {
				if (!this.isLogin) {
					this.$message.error("请先登录")
					setTimeout(() => {
						uni.navigateTo({
							url: '/pages/index?index=4'
						})
					}, 1000)
					return
				}
				let params = {}
				params.resourceUid = this.resourceUid
				params.resourceType = this.resourceType
				addPraise(params).then(response => {
					if (response.code == this.$ECode.SUCCESS) {
						this.$message.success("点赞成功")
						this.isPraise = true
						this.praiseCount = this.praiseCount + 1
						this.$emit("praiseCallback", 1)
					} else {
						this.$message.error(response.message)
					}
				})
			},
			cancelUserPraise() {
				if (!this.isLogin) {
					this.$message.error("请先登录")
					setTimeout(() => {
						uni.navigateTo({
							url: '/pages/index?index=4'
						})
					}, 1000)
					return
				}
				let params = {}
				params.resourceUid = this.resourceUid
				params.resourceType = this.resourceType
				cancelPraise(params).then(response => {
					if (response.code == this.$ECode.SUCCESS) {
						this.$message.success("取消点赞成功")
						this.isPraise = false
						this.praiseCount = this.praiseCount - 1
						this.$emit("praiseCallback", 0)
					} else {
						this.$message.error(response.message)
					}
				})
			},
			addUserCollect() {
				if (!this.isLogin) {
					this.$message.error("请先登录")
					setTimeout(() => {
						uni.navigateTo({
							url: '/pages/index?index=4'
						})
					}, 1000)
					return
				}
				let params = {}
				params.bizUid = this.resourceUid
				params.collectType = this.resourceType
				addCollect(params).then(response => {
					if (response.code == this.$ECode.SUCCESS) {
						this.$message.success("收藏成功")
						this.isCollect = true
						this.collectCount = this.collectCount + 1
					} else {
						this.$message.error(response.message)
					}
				})
			},
			cancelUserCollect() {
				if (!this.isLogin) {
					this.$message.error("请先登录")
					setTimeout(() => {
						uni.navigateTo({
							url: '/pages/index?index=4'
						})
					}, 1000)
					return
				}
				let params = {}
				params.bizUid = this.resourceUid
				params.collectType = this.resourceType
				deleteCollect(params).then(response => {
					if (response.code == this.$ECode.SUCCESS) {
						this.$message.success("取消收藏成功")
						this.isCollect = false
						this.collectCount = this.collectCount - 1
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