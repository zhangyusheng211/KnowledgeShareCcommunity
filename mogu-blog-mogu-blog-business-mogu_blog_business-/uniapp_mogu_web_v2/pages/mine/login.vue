<template>
	<view class="template-login">
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
				<image class="bg" src="https://oos.moguit.cn/mini/background/mine_bg.jpeg" mode="widthFix"></image>
			</view>
			<view class="login__bg login__bg--top">
				<image class="rocket rocket-sussuspension" src="https://tnuiimage.tnkjapp.com/login/1/login_top3.png"
					mode="widthFix"></image>
			</view>

			<!--微信登录-->
			<view style="text-align: center" class="block">
				<view id="login_container" style="text-align: center"></view>
			</view>

			<view class="login__wrapper">
				<!-- 登录/注册切换 -->
				<view
					class="login__mode tn-flex tn-flex-direction-row tn-flex-nowrap tn-flex-col-center tn-flex-row-center">
					<view class="login__mode__item tn-flex-1"
						:class="[{'login__mode__item--active': currentModeIndex === 0}]" @tap.stop="modeSwitch(0)">
						登录
					</view>
					<view class="login__mode__item tn-flex-1"
						:class="[{'login__mode__item--active': currentModeIndex === 1}]" @tap.stop="modeSwitch(1)">
						注册
					</view>

					<!-- 滑块-->
					<view class="login__mode__slider tn-cool-bg-color-15--reverse" :style="[modeSliderStyle]"></view>
				</view>

				<!-- 输入框内容-->
				<view class="login__info tn-flex tn-flex-direction-column tn-flex-col-center tn-flex-row-center">
					<!-- 登录 -->
					<block v-if="currentModeIndex === 0">
						<view
							class="login__info__item__input tn-flex tn-flex-direction-row tn-flex-nowrap tn-flex-col-center tn-flex-row-left">
							<view class="login__info__item__input__left-icon">
								<view class="tn-icon-phone"></view>
							</view>
							<view class="login__info__item__input__content">
								<input maxlength="20" placeholder-class="input-placeholder" placeholder="请输入登录手机号码"
									:disabled="loginType.password" v-model="form.userName" />
							</view>
						</view>

						<view
							class="login__info__item__input tn-flex tn-flex-direction-row tn-flex-nowrap tn-flex-col-center tn-flex-row-left">
							<view class="login__info__item__input__left-icon">
								<view class="tn-icon-lock"></view>
							</view>
							<view class="login__info__item__input__content">
								<input :password="!showPassword" placeholder-class="input-placeholder"
									:disabled="loginType.password" v-model="form.password" placeholder="请输入登录密码" />
							</view>
							<view class="login__info__item__input__right-icon" @click="showPassword = !showPassword">
								<view :class="[showPassword ? 'tn-icon-eye' : 'tn-icon-eye-hide']"></view>
							</view>
						</view>
					</block>

					<!-- 注册 -->
					<block v-if="currentModeIndex === 1">
						<view
							class="login__info__item__input tn-flex tn-flex-direction-row tn-flex-nowrap tn-flex-col-center tn-flex-row-left">
							<view class="login__info__item__input__left-icon">
								<view class="tn-icon-phone"></view>
							</view>
							<view class="login__info__item__input__content">
								<input maxlength="20" placeholder-class="input-placeholder" placeholder="请输入注册手机号码"
									:disabled="loginType.password" v-model="form.userName" />
							</view>
						</view>

						<view
							class="login__info__item__input tn-flex tn-flex-direction-row tn-flex-nowrap tn-flex-col-center tn-flex-row-left">
							<view class="login__info__item__input__left-icon">
								<view class="tn-icon-phone"></view>
							</view>
							<view class="login__info__item__input__content">
								<input maxlength="20" placeholder-class="input-placeholder" placeholder="请输入用户昵称"
									:disabled="loginType.password" v-model="form.nickName" />
							</view>
						</view>

						<view
							class="login__info__item__input tn-flex tn-flex-direction-row tn-flex-nowrap tn-flex-col-center tn-flex-row-left">
							<view class="login__info__item__input__left-icon">
								<view class="tn-icon-lock"></view>
							</view>
							<view class="login__info__item__input__content">
								<input :password="!showPassword" placeholder-class="input-placeholder"
									:disabled="loginType.password" v-model="form.password" placeholder="请输入登录密码" />
							</view>
							<view class="login__info__item__input__right-icon" @click="showPassword = !showPassword">
								<view :class="[showPassword ? 'tn-icon-eye' : 'tn-icon-eye-hide']"></view>
							</view>
						</view>

						<view
							class="login__info__item__input tn-flex tn-flex-direction-row tn-flex-nowrap tn-flex-col-center tn-flex-row-left">
							<view class="login__info__item__input__left-icon">
								<view class="tn-icon-phone"></view>
							</view>
							<view class="login__info__item__input__content">
								<input maxlength="20" placeholder-class="input-placeholder" placeholder="请输入邮箱地址"
									:disabled="loginType.password" v-model="form.email" />
							</view>
						</view>

						<!-- 						<view
							class="login__info__item__input tn-flex tn-flex-direction-row tn-flex-nowrap tn-flex-col-center tn-flex-row-left">
							<view class="login__info__item__input__left-icon">
								<view class="tn-icon-code"></view>
							</view>
							<view
								class="login__info__item__input__content login__info__item__input__content--verify-code">
								<input placeholder-class="input-placeholder" v-model="form.validCode"
									placeholder="输入邮箱验证码" />
							</view>
							<view class="login__info__item__input__right-verify-code" @tap.stop="getCode">
								<tn-button backgroundColor="#01BEFF" fontColor="#FFFFFF" size="sm" padding="5rpx 10rpx"
									width="100%" shape="round">{{ tips }}</tn-button>
							</view>
						</view> -->


					</block>

					<view class="">

					</view>

					<view class="tn-flex login__info__item__button">
						<view class="tn-flex-1 justify-content-item tn-text-center">
							<tn-button v-if="currentModeIndex === 0" shape="round" @click="startLogin"
								:disabled="loginType.password" backgroundColor="tn-cool-bg-color-7--reverse"
								padding="40rpx 0" width="100%" shadow fontBold>
								<text class="tn-color-white" hover-class="tn-hover" :hover-stay-time="150">
									登 录
								</text>
							</tn-button>
							<tn-button v-if="currentModeIndex === 1" shape="round" @click="startRegister"
								:disabled="loginType.password" backgroundColor="tn-cool-bg-color-7--reverse"
								padding="40rpx 0" width="100%" shadow fontBold>
								<text class="tn-color-white" hover-class="tn-hover" :hover-stay-time="150">
									注 册
								</text>
							</tn-button>
						</view>
					</view>

					<!-- <view class="login__info__item__button tn-cool-bg-color-7--reverse" hover-class="tn-hover" :hover-stay-time="150">{{ currentModeIndex === 0 ? '登录' : '注册'}}</view> -->

					<!-- <view v-if="currentModeIndex === 0" class="login__info__item__tips">忘记密码?</view> -->
				</view>

				<view style="text-align: center;" class="tn-margin-top">
					<view style="font-size: 13px; color: #AAAAAA;">登录方式支持:
						<text v-if="!loginType.password" class="tn-margin-left-sm"> 账号密码 </text>
						<text v-if="!loginType.gitee" class="tn-margin-left-sm"> 码云 </text>
						<text v-if="!loginType.github" class="tn-margin-left-sm"> Github </text>
						<text v-if="!loginType.qq" class="tn-margin-left-sm"> QQ </text>
						<text v-if="!loginType.wechat" class="tn-margin-left-sm"> 公众号 </text>
						<text v-if="!loginType.personWechat" class="tn-margin-left-sm"> 微信 </text>
					</view>
				</view>

				<!-- 其他登录方式 -->
				<view class="login__way tn-flex tn-flex-col-center tn-flex-row-center" v-if="currentModeIndex === 0">
					<!-- 					<view class="tn-padding-sm tn-margin-xs">
						<view @click="goAuth('personWechat')"
							class="login__way__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-shadow-blur tn-bg-green tn-color-white">
							<view class="tn-icon-wechat-fill"></view>
						</view>
					</view> -->

					<view class="tn-padding-sm tn-margin-xs" @click="goAuth('gitee')" v-if="!loginType.gitee">
						<view
							class="login__way__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-shadow-blur tn-bg-red tn-color-white">
							<view class="tn-icon-gitee"></view>
						</view>
					</view>
					<view class="tn-padding-sm tn-margin-xs" @click="goAuth('qq')" v-if="!loginType.qq">
						<view
							class="login__way__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-shadow-blur tn-bg-blue tn-color-white">
							<view class="tn-icon-qq"></view>
						</view>
					</view>

					<view class="tn-padding-sm tn-margin-xs" @click="goAuth('github')" v-if="!loginType.github">
						<view
							class="login__way__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-shadow-blur tn-bg-black tn-color-white">
							<view class="tn-icon-github"></view>
						</view>
					</view>
				</view>
			</view>


			<!-- 底部背景图片-->
			<!-- 			<view class="login__bg login__bg--bottom">
				<image src="https://tnuiimage.tnkjapp.com/login/1/login_bottom_bg.jpg" mode="widthFix"></image>
			</view> -->
		</view>

		<!-- 验证码倒计时 -->
		<tn-verification-code ref="code" uniqueKey="login-demo-1" :seconds="60" @change="codeChange">
		</tn-verification-code>

		<wx-user-info-modal v-model="showAuthorizationModal" @updated="updatedUserInfoEvent"></wx-user-info-modal>
	</view>
</template>
<!--微信登录模块-->
<script src="https://res.wx.qq.com/connect/zh_CN/htmledition/js/wxLogin.js"></script>
<script>
	import WxUserInfoModal from '@/uni_modules/tuniaoui-wx-user-info/components/tuniaoui-wx-user-info/tuniaoui-wx-user-info.vue'
	import template_page_mixin from '@/libs/mixin/template_page_mixin.js'
	import {
		appLogin
	} from '../../api/user.js'
	import {
		getWebConfig
	} from '../../api/index.js'
	import {
		oauthLogin,
		localLogin,
		localRegister,
		getUserLoginStatus,
		getWeChatLoginUrl,
		getOauthUrl,
		getBindKey,
		getLoginKey,
		loginCheck,
	} from '../../api/login.js'
	import {
		tokenUtil
	} from '../../utils/token.js'
	export default {
		name: 'templateLogin',
		mixins: [template_page_mixin],
		data() {
			return {
				form: {
					userName: "",
					password: "",
					nickName: "",
					email: "",
					validCode: "",
				},
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
				showAuthorizationModal: false,
				showCodeLogin: false,
				// 登录类别
				loginType: {
					password: true,
					gitee: true,
					github: true,
					qq: true,
					wechat: true,
					personWechat: true,
				},
			}
		},
		components: {
			WxUserInfoModal
		},
		watch: {
			currentModeIndex(value) {
				const sliderWidth = uni.upx2px(476 / 2)
				this.modeSliderStyle.left = `${sliderWidth * value}px`
			}
		},
		created() {
			this.getWebConfigMethod()
		},
		methods: {
			// 跳转
			tn(e) {
				uni.navigateTo({
					url: e,
				});
			},
			startLogin: function() {
				console.log("查询登录状态", this.form.userName, this.form.password)
				if (!this.form.userName || !this.form.password) {
					this.$message.error("必填项不能为空")
					return;
				}
				let params = {};
				params.userName = this.form.userName;
				params.passWord = this.form.password;
				params.isRememberMe = 1;
				localLogin(params).then(response => {
					if (response.code == this.$ECode.SUCCESS) {
						this.$message.success(response.message)
						// 重定向到首页，然后刷新
						// uni.navigateTo({
						// 	url: '/pages/index?index=0&token=' + response.data.token
						// })
						// 设置token
						// tokenUtil.set(response.data.token)
						uni.navigateTo({
							url: '/pages/index?index=0&token=' + response.data.token
						})

					} else {
						this.$message.error(response.message)
					}
				});
			},
			// 获取系统配置
			getWebConfigMethod() {
				getWebConfig().then(response => {
					if (response.code == this.$ECode.SUCCESS) {
						let webConfigData = response.data
						if (webConfigData.loginTypeList != undefined) {
							let loginTypeList = JSON.parse(webConfigData.loginTypeList)
							for (let a = 0; a < loginTypeList.length; a++) {
								switch (loginTypeList[a]) {
									case "1": {
										this.loginType.password = false
									}
									break;
									case "2": {
										this.loginType.gitee = false
									}
									break;
									case "3": {
										this.loginType.github = false
									}
									break;
									case "4": {
										this.loginType.qq = false
									}
									break;
									case "5": {
										// 公众号
										this.loginType.wechat = false
									}
									break;
									case "6": {
										// 个人微信
										this.loginType.personWechat = false
									}
									break;
									default: {
										console.log("登录方式设置有误！！")
									}
								}
							}
						}
					}
				});
			},
			startRegister: function() {
				if (!this.form.userName || !this.form.password || !this.form.nickName || !this.form.email) {
					this.$message.error("必填项不能为空")
					return;
				}

				// 进行必要参数校验
				let passWord = this.form.password;
				let params = {};
				params.userName = this.form.userName;
				params.passWord = this.form.password;
				params.email = this.form.email;
				params.nickName = this.form.nickName
				localRegister(params).then(response => {
					if (response.code == this.$ECode.SUCCESS) {
						this.$message.success(response.message)
						// 打开登录页
						this.currentModeIndex = 0
					} else {
						this.$message.error(response.message)
					}
				});
			},
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
			// 跳转登录方式
			goAuth: function(source) {
				this.showPersonWechatLogin = false

				// 判断是否点击公众号登录
				if (source === "wechat") {
					console.log("点击公众号登录")
					this.getWechatLoginKey()
					return
				}
				if (source === "password") {
					console.log("点击账号密码登录")
					this.showPersonWechatLogin = false
					this.showPasswordLogin = true
					return
				}
				if (source === "personWechat") {
					this.showPersonWechatLogin = true
					console.log("点击微信登录")
					this.getWeChatLoginUrlMethod()
					return
				}
				// 弹窗提示正在登录
				uni.showToast({
					icon: 'none',
					title: '第三方登录中',
					duration: 3000
				})
				oauthLogin("source=" + source + "&type=login&webSiteSource=h5").then(response => {
					if (response.code == this.$ECode.SUCCESS) {
						window.location.href = response.data.url
					}
				});
			},
			// 获取微信登录二维码
			getWeChatLoginUrlMethod() {
				// 获取登录的票券
				getLoginKey().then(response => {
					if (response.code == this.$ECode.SUCCESS) {
						console.log("返回的票券", response.data)
						let code = response.data.loginKey
						let params = {
							"loginKey": code,
							"source": "h5"
						}
						getOauthUrl(params).then(response => {
							if (response.code === this.$ECode.SUCCESS) {
								console.log("获取微信的信息", response)
								let data = response.data;
								window.location.href = data
							}
						})
					}
				})

			},
			// 微信公众号获取登录的密钥
			getWechatLoginKey: function() {
				this.showPasswordLogin = false
				let that = this
				getLoginKey().then(response => {
					if (response.code == this.$ECode.SUCCESS) {
						let loginKey = response.data
						this.wechatLoginKey = loginKey
						let count = 0
						let interval = setInterval(() => {
							count++
							// 当检查15次未扫码的时候，将二维码失效，重试关闭接口请求
							if (count > 30) {
								that.wechatLoginKey.loginKey = "验证码失效,请刷新"
								clearInterval(interval)
							}
							let params = {}
							params.code = loginKey.loginKey
							params.ticket = loginKey.ticket
							loginCheck(params).then(response => {
								console.log("获取用户状态", response)
								if (response.code == that.$ECode.SUCCESS) {
									console.log("获取token", response)
									// 判断url中是否含有token
									let token = response.data
									if (token != undefined) {
										// 根据Cookie获取信息
									}
								}
							});
						}, 3000);

						this.interval = interval
					}
				})
			},
			wechatLogin() {
				uni.login({
					provider: 'weixin',
					success: function(loginRes) {
						// 获取权限Code
						console.log("获取权限Code", loginRes)
						// 登录成功
						uni.getUserInfo({
							provider: 'weixin',
							success: function(info) {
								// 获取用户信息成功, info.authResult保存用户信息
								console.log("获取用户信息成功", info)
							}
						})
					},
					fail: function(err) {
						// 登录授权失败  
						// err.code是错误码
						console.log("获取用户信息失败", err)
					}
				});
				console.log("微信公众号登录")
				// this.showAuthorizationModal = true
			},
			// 获取到的用户信息
			updatedUserInfoEvent(info) {
				console.log('获取到的用户信息', info)
			},
			// 用户登录
			getUserInfo() {
				// #ifdef MP-WEIXIN
				var that = this
				wx.getUserProfile({
					desc: '更新您的个人信息',
					success: (res) => {
						// 成功后进行登录,获取code
						that.wxlogin();
					},
					fail(err) {
						uni.showToast({
							icon: 'none',
							title: '授权失败',
							duration: 1500
						})
					}
				})
				// #endif 
			},

			//已经授权，自动登录 
			async wxlogin() {
				let that = this;
				uni.showLoading({
					title: '登录中...'
				});
				uni.login({
					provider: 'weixin',
					success: function(loginRes) {
						let code = loginRes.code;
						that.wxloginres(code);
					},
				});

			},
			//登录后提交服务器获取进一步信息
			async wxloginres(wxcode) {
				let that = this;
				// debugger   userId=""
				var userId = uni.getStorageSync('userId')

				//A
				Net.request({
					url: API.weChatLogin(),
					data: {
						code: wxcode,
					},
					header: {
						'Content-Type': 'application/x-www-form-urlencoded'
					},
					method: "get",
					dataType: 'json',
					success: function(res) {
						if (res.data.code == 0) {
							Net.request({
								url: API.userAuthorize(),
								data: {
									name: that.name,
									avatar: that.avatar,
									mptype: 'wx',
									code: res.data.data, //wxcode,
									userId: uni.getStorageSync('userId'), //userId,
								},
								header: {
									// 'Content-Type':'application/x-www-form-urlencoded',
									'Content-Type': 'multipart/form-data'
								},
								method: "get", //注意发送post请求
								dataType: 'json',
								success: function(res) {
									if (res.data.code == 0) {
										that.$u.vuex('vuex_user', res.data.data
											.data)
										that.getUser()
										that.getSign()
										uni.showToast({
											icon: 'none',
											mask: true,
											title: '登录成功',
											duration: 1500
										})
										uni.removeStorageSync('userId')
									} else {
										uni.showToast({
											icon: 'none',
											mask: true,
											title: '登录失败',
											duration: 1500
										})
									}
									that.loginShow = false


								},
								fail: function(res) {}
							})

						}

					},
					fail: function(res) {}
				})

			},


			getLoginShow() {
				let that = this
				that.loginShow = true
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

	/* 悬浮 */
	.rocket-sussuspension {
		animation: suspension 3s ease-in-out infinite;
	}

	@keyframes suspension {

		0%,
		100% {
			transform: translate(0, 0);
		}

		50% {
			transform: translate(-0.8rem, 1rem);
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

				.rocket {
					margin: 50rpx 28%;
					width: 400rpx;
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
			margin-top: 103rpx;
			width: 100%;
		}

		/* 切换 start */
		&__mode {
			position: relative;
			margin: 0 auto;
			width: 476rpx;
			height: 77rpx;
			background-color: rgba(255, 255, 255, 0.9);
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
			margin: 0 30rpx;
			margin-top: 105rpx;
			padding: 30rpx 51rpx;
			padding-bottom: 0;
			border-radius: 20rpx;
			background-color: rgba(255, 255, 255, 0.9);
			box-shadow: 0rpx 0rpx 80rpx 0rpx rgba(0, 0, 0, 0.07);

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
					letter-spacing: 0.5em;
				}

				&__tips {
					margin: 30rpx 0;
					color: #AAAAAA;
				}
			}
		}

		/* 登录注册信息 end */

		/* 登录方式切换 start */
		&__way {
			margin: 0 auto;
			margin-top: 30rpx;

			&__item {
				&--icon {
					width: 77rpx;
					height: 77rpx;
					font-size: 50rpx;
					border-radius: 100rpx;
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

		/* 登录方式切换 end */
		/* 内容 end */

	}

	/deep/.input-placeholder {
		font-size: 30rpx;
		color: #C6D1D8;
	}
</style>