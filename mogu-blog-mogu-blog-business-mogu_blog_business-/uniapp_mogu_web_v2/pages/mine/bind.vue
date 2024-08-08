<template>
	<view class="template-safety tn-safe-area-inset-bottom">
		<!-- 顶部自定义导航 -->
		<tn-nav-bar fixed alpha customBack>
			<view slot="back" class='tn-custom-nav-bar__back' @click="goBack">
				<text class='icon tn-icon-left'></text>
				<text class='icon tn-icon-home-capsule-fill'></text>
			</view>
		</tn-nav-bar>

		<view class="login">
			<!-- 顶部背景图片-->
			<view class="login__bg login__bg--top">
				<image class="bg" src="https://tnuiimage.tnkjapp.com/login/2/login-top2.png" mode="widthFix"></image>
			</view>

			<view class="login__wrapper tn-padding">
				<view class="tn-margin-left tn-margin-right tn-text-bold" style="font-size: 50rpx;">
					账号绑定
				</view>
				<view class="tn-margin tn-color-grey--disabled tn-text-lg">
					绑定PC账号，资料共享互通
				</view>

				<!-- 切换 -->
				<!-- 				<view
					class="login__mode tn-flex tn-flex-direction-row tn-flex-nowrap tn-flex-col-center tn-flex-row-center">
					<view class="login__mode__item tn-flex-1"
						:class="[{'login__mode__item--active': currentModeIndex === 0}]" @tap.stop="modeSwitch(0)">
						PC
					</view>
					<view class="login__mode__item tn-flex-1"
						:class="[{'login__mode__item--active': currentModeIndex === 1}]" @tap.stop="modeSwitch(1)">
						小程序
					</view>
					<view class="login__mode__slider tn-cool-bg-color-15--reverse" :style="[modeSliderStyle]"></view>
				</view> -->

				<!-- 输入框内容-->
				<view class="login__info tn-flex tn-flex-direction-column tn-flex-col-center tn-flex-row-center">

					<!-- 昵称 -->
					<block v-if="currentModeIndex === 0">
						<view
							class="login__info__item__input tn-flex tn-flex-direction-row tn-flex-nowrap tn-flex-col-center tn-flex-row-left">
							<view class="login__info__item__input__left-icon">
								<view class="tn-icon-my"></view>
							</view>
							<view class="login__info__item__input__content">
								<input v-model="code" maxlength="20" placeholder-class="input-placeholder"
									placeholder="请输入PC端绑定码" />
							</view>
						</view>
					</block>
					<view class="login__info__item__button tn-bg-blue tn-color-white" hover-class="tn-hover"
						@click="bindCodeMethod" :hover-stay-time="150">{{ currentModeIndex === 0 ? '提交绑定' : '前往PC操作'}}
					</view>
				</view>
			</view>

			<!-- 底部背景图片-->
			<view class="login__bg login__bg--bottom">
				<image src="https://tnuiimage.tnkjapp.com/login/2/login-bottom2.png" mode="widthFix"></image>
			</view>

		</view>

		<tn-modal v-model="showUserInfo" :custom="true">
			<view class="custom-modal-content">

				<view class="text">是否将当前账号关联以下用户</view>

				<view class="blogger__author tn-flex tn-flex-row-between tn-flex-col-center" style="margin-top: 10px;">
					<view class="justify__author__info" @click="tn('/circlePages/blogger_other')">
						<view class="tn-flex tn-flex-row-center">
							<view class="tn-flex tn-flex-row-center tn-flex-col-center">
								<view class="">
									<tn-avatar class="" shape="circle" :src="userInfo.photoUrl" size="lg">
									</tn-avatar>
								</view>
								<view class="tn-padding-right tn-text-ellipsis">
									<view class="tn-padding-right tn-padding-left-sm tn-text-bold tn-text-lg">
										{{ userInfo.nickName }}
									</view>
									<view class="tn-padding-right tn-padding-left-sm tn-padding-top-xs tn-color-gray">
										{{ userInfo.summary }}
									</view>
								</view>
							</view>
						</view>
					</view>
				</view>

				<view style="margin-top: 10px;">
					<tn-button backgroundColor="#01BEFF" fontColor="tn-color-white" width="48%" @click="submitBindCode">
						确定
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
	import template_page_mixin from '@/libs/mixin/template_page_mixin.js'
	import {
		bindCode,
		getUserInfoByBindCode,
	} from '../../api/user.js'
	import {
		tokenUtil
	} from '../../utils/token.js'
	export default {
		name: 'TemplateSafety',
		mixins: [template_page_mixin],
		data() {
			return {
				// 当前选中的模式
				currentModeIndex: 0,
				// 模式选中滑块
				modeSliderStyle: {
					left: 0
				},
				// 是否显示密码
				showPassword: false,
				// 倒计时提示文字
				tips: '获取验证码',
				ticketCode: null,
				code: null,
				showUserInfo: false,
				userInfo: {},
			}
		},
		watch: {
			currentModeIndex(value) {
				const sliderWidth = uni.upx2px(476 / 2)
				this.modeSliderStyle.left = `${sliderWidth * value}px`
			}
		},
		methods: {
			// 切换模式
			modeSwitch(index) {
				this.currentModeIndex = index
				this.showPassword = false
			},
			// 获取验证码
			getCode() {
				if (this.$refs.code.canGetCode) {
					this.$t.message.loading('正在获取验证码')
					setTimeout(() => {
						this.$t.message.closeLoading()
						this.$t.message.toast('验证码已经发送')
						// 通知组件开始计时
						this.$refs.code.start()
					}, 2000)
				} else {
					this.$t.message.toast(this.$refs.code.secNum + '秒后再重试')
				}
			},
			// 获取验证码倒计时被修改
			codeChange(event) {
				this.tips = event
			},
			bindCodeMethod() {
				let that = this
				uni.login({
					provider: 'weixin',
					success: function(loginRes) {
						// 根据获取用户信息
						that.ticketCode = loginRes.code
						that.getUserInfoByBindCodeMethod(loginRes.code)
					},
					fail: function(err) {
						// 登录授权失败
						console.log("获取用户信息失败", err)
					}
				});
			},
			cancel() {
				this.showUserInfo = false
				this.code = ""
			},
			submitBindCode() {
				let that = this
				// 根据获取用户信息
				let params = {}
				params.ticketCode = that.ticketCode
				params.bindCode = that.code
				bindCode(params).then((res) => {
					if (that.$ECode.SUCCESS == res.code) {
						that.$message.success("更新换绑成功，请重新登录账号")
						that.logout()
					} else {
						that.$message.success(res.message)
					}
				})
			},
			getUserInfoByBindCodeMethod(ticketCode) {
				let that = this
				let params = {}
				params.ticketCode = this.ticketCode
				params.bindCode = this.code
				getUserInfoByBindCode(params).then((res) => {
					console.log("获取当前用户", res)
					if (that.$ECode.SUCCESS == res.code) {
						that.userInfo = res.data
						that.showUserInfo = true
					} else {
						that.$message.success(res.message)
					}
				})
			},
			logout() {
				uni.clearStorage("userInfo")
				tokenUtil.clear()
				// 跳转页面
				setTimeout(() => {
					uni.navigateTo({
						url: '/pages/index?index=4'
					})
				}, 1000)
			}
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

	.login {
		position: relative;
		height: 100%;
		z-index: 1;

		/* 背景图片 start */
		&__bg {
			z-index: -1;
			position: fixed;

			&--top {
				top: 0;
				left: 0;
				right: 0;
				width: 100%;

				.bg {
					width: 750rpx;
					will-change: transform;
				}
			}

			&--bottom {
				bottom: -10rpx;
				left: 0;
				right: 0;
				width: 100%;
				// height: 144px;
				// margin-bottom: env(safe-area-inset-bottom);

				image {
					width: 750rpx;
					will-change: transform;
				}
			}
		}

		/* 背景图片 end */

		/* 内容 start */
		&__wrapper {
			margin-top: 300rpx;
			width: 100%;
		}

		/* 切换 start */
		&__mode {
			position: relative;
			margin: 0 auto;
			width: 476rpx;
			height: 77rpx;
			margin-top: 100rpx;
			background-color: rgba(255, 255, 255, 0.1);
			box-shadow: 0rpx 10rpx 50rpx 0rpx rgba(0, 3, 72, 0.1);
			border-radius: 39rpx;

			&__item {
				height: 77rpx;
				width: 100%;
				line-height: 77rpx;
				text-align: center;
				font-size: 31rpx;
				color: #78909C;
				letter-spacing: 1em;
				text-indent: 1em;
				z-index: 2;
				transition: all 0.4s;

				&--active {
					font-weight: bold;
					color: #FFFFFF;
				}
			}

			&__slider {
				position: absolute;
				height: inherit;
				width: calc(476rpx / 2);
				border-radius: inherit;
				box-shadow: 0rpx 18rpx 72rpx 18rpx rgba(0, 195, 255, 0.1);
				z-index: 1;
				transition: all 0.3s cubic-bezier(0.68, -0.55, 0.265, 1.55);
			}
		}

		/* 切换 end */

		/* 登录注册信息 start */
		&__info {
			margin: 80rpx 30rpx 10rpx 30rpx;
			padding-bottom: 0;
			border-radius: 20rpx;

			&__item {

				&__input {
					margin-top: 59rpx;
					width: 100%;
					height: 77rpx;
					border: 1rpx solid #C6D1D8;
					border-radius: 39rpx;

					&__left-icon {
						width: 10%;
						font-size: 44rpx;
						margin-left: 20rpx;
						color: #78909C;
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
						font-size: 44rpx;
						margin-right: 20rpx;
						color: #78909C;
					}

					&__right-verify-code {
						width: 34%;
						margin-right: 20rpx;
					}
				}

				&__button {
					margin-top: 75rpx;
					margin-bottom: 39rpx;
					width: 100%;
					height: 77rpx;
					text-align: center;
					font-size: 31rpx;
					font-weight: bold;
					line-height: 77rpx;
					letter-spacing: 1em;
					text-indent: 1em;
					border-radius: 39rpx;
					box-shadow: 1rpx 10rpx 24rpx 0rpx rgba(60, 129, 254, 0.35);
				}

			}
		}

		/* 登录注册信息 end */

		/* 内容 end */

	}

	/deep/.input-placeholder {
		font-size: 30rpx;
		color: #C6D1D8;
	}
</style>
