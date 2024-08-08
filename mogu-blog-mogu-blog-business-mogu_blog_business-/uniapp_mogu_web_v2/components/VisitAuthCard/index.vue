<template>
	<view style="width: 100%; height: 210px; background: rgba(0, 0, 0, 0.03); border-radius: 8px; margin: 0 auto;"
		v-if="!entity.visitAuthSuccess">
		<span class="lockText"><i class="el-icon-lock"></i>
			该文章<span v-if="entity.loadingArea == 1">部分</span>内容已隐藏</span>

		<view style="width: 95%; height: 150px; background: #FFF; margin: 10px auto; border-radius: 8px;">

			<span v-if="visitAuthList.length == 1">
				<tn-tag class="pay-tag tn-margin-right" margin="5rpx" backgroundColor="#FF8331" shape="radius"
					v-for="item in visitAuthDictList" v-if="visitAuthList[0] == item.dictValue" :key="item.uid"
					fontColor="#FFFFFF">{{ item.dictLabel }}</tn-tag>
			</span>

			<tn-tag v-else class="pay-tag tn-margin-right" margin="5rpx" backgroundColor="#FF8331" shape="radius"
				fontColor="#FFFFFF">限制阅读</tn-tag>


			<span class="img-badge" v-if="visitAuthList && visitAuthList.includes('6')">已售
				{{entity.payOrderCount}}</span>

			<view class="payBody" v-if="visitAuthList && visitAuthList.includes('6')">
				<span>
					<i class="el-icon-coin"></i>
					<span class="priceFont">
						<span v-if="entity.payType == 1">
							{{ entity.price }}
						</span>
						<span v-else>
							{{ entity.price / 100 }}
						</span>
					</span>
					<span v-if="entity.payType == 1">
						积分
					</span>
					<span v-else>
						元
					</span>
				</span>
				<tn-button style="width: 180rpx; float: right;" backgroundColor="tn-bg-blue" fontColor="tn-color-white"
					@click="actionNow('6')">{{ getVisitAuthName('6')}}</tn-button>
			</view>

			<view class="pay-doc" v-if="entity.visitAuthExtraVo.visitAuthText">
				{{ entity.visitAuthExtraVo.visitAuthText }}
			</view>

			<view class="pay-doc" v-else>
				<view v-for="item in visitAuthDictList" v-if="visitAuthList.includes(item.dictValue)"
					:key="item.uid + item.uid">
					{{ item.remark }}
				</view>
			</view>

			<view style=" text-align: center; margin-top: 15px;" v-if="entity.visitAuth != 6">

				<tn-button v-for="visitAuth in visitAuthList" :key="visitAuth"
					style="width: 120rpx; margin-right: 5rpx;" backgroundColor="tn-bg-blue" fontColor="tn-color-white"
					@click="actionNow(visitAuth)">{{ getVisitAuthName(visitAuth) }}</tn-button>
			</view>

		</view>
		<PasswordInput :entity="entity" :resourceType="resourceTypeValue" v-if="showPasswordInput"
			@loadingValidCallback="loadingValidCallback">
		</PasswordInput>
		<LoadingValid v-if="showLoadingValid" @loadingValidCallback="loadingValidCallback"></LoadingValid>
	</view>
</template>

<script>
	import {
		getListByDictTypeList,
	} from '../../api/sysDictData.js';
	import template_page_mixin from '@/libs/mixin/template_page_mixin.js'
	import mpHtml from 'mp-html/dist/uni-app/components/mp-html/mp-html'
	import LoadingValid from './LoadingValid.vue'
	import PasswordInput from './PasswordInput.vue'
	export default {
		name: 'VisitAuthCard',
		props: ['entity', "resourceType"],
		data() {
			return {
				visitAuthName: "",
				visitAuthDictList: [],
				showPayWindow: false,
				productVO: {},
				showLoadingValid: false,
				showPasswordInput: false,
				resourceTypeValue: this.resourceType,
				isLogin: false,
				userInfo: {},
				// 访问权限列表
				visitAuthList: [],
			}
		},
		watch: {
			'entity': function(event, oldFlag) {
				if (this.entity) {
					this.visitAuthList = this.entity.visitAuth.split(',')
				}
			},
		},
		created() {
			let that = this
			uni.getStorage({
				key: 'userInfo',
				success: function(res) {
					console.log("加载用户信息", res.data)
					that.userInfo = res.data
					that.isLogin = true
				}
			});
			this.getDictList()
		},
		components: {
			LoadingValid,
			PasswordInput,
		},
		methods: {
			/**
			 * 字典查询
			 */
			getDictList: function() {
				let dictTypeList = ['sys_visit_auth']
				getListByDictTypeList(dictTypeList).then(response => {
					if (response.code == this.$ECode.SUCCESS) {
						let dictMap = response.data;
						this.visitAuthDictList = dictMap.sys_visit_auth.list
					}
				});
			},
			getVisitAuthName(visitAuth) {
				console.log("visitAuth", visitAuth)
				switch (visitAuth) {
					case "1": {
						return "立即查看"
					}
					case "2": {
						return "立即登录"
					}
					case "3": {
						return "立即评论"
					}
					case "4": {
						return "立即验证"
					}
					case "5": {
						return "成为会员"
					}
					case "6": {
						return "立即购买"
					}
					case "7": {
						return "立即点赞"
					}
					case "8": {
						return "立即收藏"
					}
					case "9": {
						return "立即关注"
					}
					case "10": {
						return "输入密码"
					}
					case "11": {
						return "仅作者可见"
					}
					case "12": {
						return "联系作者"
					}
					case "13": {
						return "联系作者"
					}
					case "14": {
						return "订阅专栏"
					}
					default: {
						return "无法访问"
					}
				}
			},
			setLoginMessage(randomInt) {
				// 弹出登录框
				console.log("暂未登录", randomInt)
				this.$message.error("请先完成登录")
			},
			actionNow(visitAuth) {
				let isLogin = this.isLogin
				switch (visitAuth) {
					case "1": {
						this.$emit("refresh", "")
						return
					}
					case "2": {
						// 弹出登录框
						this.setLoginMessage(Math.random())
						return
					}
					case "3": {
						// 弹出评论框
						if (!isLogin) {
							this.setLoginMessage(Math.random())
							return
						}
						this.$message.info("请先到内容下方，完成评论后查看")
						return
					}
					case "4": {
						// 弹出人机验证框
						if (!isLogin) {
							this.setLoginMessage(Math.random())
							return
						}
						this.showLoadingValid = true
						return
					}
					case "5": {
						// 弹出验证框
						if (!isLogin) {
							this.setLoginMessage(Math.random())
							return
						}
						this.$message.info("该内容为会员专属内容，请联系管理员成为会员后查看")
						return
					}
					case "6": {

						if (!isLogin) {
							this.setLoginMessage(Math.random())
							return
						}
						this.$message.info("小程序暂不支持支付，请在PC网站下单后查看")

						// // 弹出支付框
						// let resourceType = this.resourceTypeValue
						// let entity = this.entity
						// switch (resourceType) {
						// 	case "BLOG": {
						// 		// 如果是在文章详情页进行访问控制
						// 		// 判断文章是否携带了专栏，如果归属某个专栏，那么就需要去购买专栏
						// 		if (entity.subject) {
						// 			this.resourceTypeValue = "SUBJECT"
						// 			this.productVO = this.$commonUtil.convertSubject(entity.subject)
						// 		} else {
						// 			this.resourceTypeValue = "BLOG"
						// 			this.productVO = this.$commonUtil.convertArticle(this.entity)
						// 		}
						// 	}
						// 	break;
						// 	case "SUBJECT": {
						// 		this.productVO = this.$commonUtil.convertSubject(this.entity)
						// 	}
						// 	break;
						// }
						// this.showPayWindow = true
						return
					}
					case "7": {
						// 弹出点赞提示
						if (!isLogin) {
							this.setLoginMessage(Math.random())
							return
						}
						this.$message.info("在内容下方，点击点赞按钮")
						return
					}
					case "8": {
						// 弹出点赞提示
						if (!isLogin) {
							this.setLoginMessage(Math.random())
							return
						}
						this.$message.info("在内容下方，点击收藏按钮")
						return
					}
					case "9": {
						// 弹出登录提示
						if (!isLogin) {
							this.setLoginMessage(Math.random())
							return
						}
						this.$message.info("在内容上方，点击作者完成关注")
						return
					}
					case "10": {
						// 弹出登录提示
						if (!isLogin) {
							this.setLoginMessage(Math.random())
							return
						}
						this.showPasswordInput = true
						return
					}
					case "11": {
						// 弹出登录提示
						if (!isLogin) {
							this.setLoginMessage(Math.random())
							return
						}
						this.$message.info("仅文章作者才有权限阅读")
						return
					}
					case "12": {
						// 弹出登录提示
						if (!isLogin) {
							this.setLoginMessage(Math.random())
							return
						}
						this.$message.info("该文章仅开放给指定标签用户")
						return
					}
					case "13": {
						// 弹出登录提示
						if (!isLogin) {
							this.setLoginMessage(Math.random())
							return
						}
						this.$message.info("该文章仅开放给指定用户")
						return
					}

				}
			},
			// 输入密码回调
			passwordCallback(type) {
				this.showPasswordInput = false
				this.resourceTypeValue = this.resourceType
				// 支付成功，刷新页面
				if (type == 1) {
					this.$emit("refresh", "")
				}
			},
			payCallback(paySuccess) {
				this.showPayWindow = false
				this.resourceTypeValue = this.resourceType
				// 支付成功，刷新页面
				if (paySuccess) {
					this.$emit("refresh", "")
				}
			},
			// 加载校验成功
			loadingValidCallback(type) {
				this.showLoadingValid = false
				this.resourceTypeValue = this.resourceType
				if (type == 1) {
					this.$emit("refresh", "")
				}
			},
		},
	}
</script>

<style scoped>
	.lockText {
		display: inline-block;
		font: normal normal normal 14px/1 FontAwesome;
		text-rendering: auto;
		-webkit-font-smoothing: antialiased;
		margin-top: 10px;
		margin-left: 10px;
		color: #999;
	}

	.pay-tag {
		font-size: 13px;
		/* padding: 3px 10px; */
		right: auto;
		width: auto;
		/* top: 10px; */
		background: linear-gradient(135deg, #ff74cd 10%, #ec7d0b 100%);
		color: #fff;
		/* border-radius: 8px 0 8px 0; */
		line-height: 1.4;
		z-index: 1;
	}

	.img-badge {
		float: right;
		right: 0;
		border-radius: 50px 0 0 50px;
		text-shadow: none;
		box-shadow: 0 1px 5px rgba(0, 0, 0, .2);
		z-index: 1;
		padding: 0.2em 0.6em;
		background: linear-gradient(135deg, #60e464 10%, #5cb85b 100%);
		color: #fff;
		font-size: 12px;
	}

	.payBody {
		height: 45px;
		margin-top: 20px;
		margin-left: 10px;
	}

	.pay-doc {
		border-top: 1px solid rgba(0, 0, 0, 0.03);
		margin-top: 10px;
		padding-top: 10px;
		white-space: pre-wrap;
		color: #999;
		font-size: 13px;
		text-align: center;
	}

	.priceFont {
		font-size: 30px;
	}
</style>