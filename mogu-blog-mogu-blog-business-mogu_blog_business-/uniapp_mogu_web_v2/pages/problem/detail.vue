<template>
	<view class="template-integral tn-safe-area-inset-bottom">
		<!-- 顶部自定义导航 -->
		<tn-nav-bar fixed alpha customBack>
			<view slot="back" class='tn-custom-nav-bar__back' @click="goBack">
				<text class='icon tn-icon-left'></text>
				<text class='icon tn-icon-home-capsule-fill'></text>
			</view>

			<tn-button :backgroundColor="readModel?'#FFFFFF':'#000000'" @click="switchModel"
				:fontColor="readModel?'#000000':'#FFFFFF'">答题模式</tn-button>
			<tn-button :backgroundColor="!readModel?'#FFFFFF':'#000000'" @click="switchModel"
				:fontColor="!readModel?'#000000':'#FFFFFF'">背题模式</tn-button>

		</tn-nav-bar>


		<view class="integral-wrap" :style="{paddingTop: vuex_custom_bar_height + 'px'}"
			style="padding-left: 20rpx; padding-right: 20rpx;">
			<!-- 			<view style="padding: 20rpx;">
				<tn-tag backgroundColor="#01BEFF" fontColor="#FFFFFF" margin="10rpx 10rpx"
					style="display: inline-block;" v-for="item in problemTypeDictList" :key="item.uid"
					v-if="problemDetail.problemType == parseInt(item.dictValue)">{{item.dictLabel}}</tn-tag>
				<view v-html="problemDetail.content" style="display: inline-block;"></view>
			</view> -->

			<tn-line-progress :percent="(current + 1)*100/total" activeColor="#31E749">
				<view class="tn-color-white" style="float: right;" v-if="(current + 1)*100/total > 20">
					刷题进度:
					{{current+1}}/{{total}}
				</view>
			</tn-line-progress>

			<view>
				<swiper class="swiper" :style="{height: windowsHeight - vuex_custom_bar_height - 100 + 'px'}"
					@change="changePage" :current="current">
					<swiper-item v-for="(problemItem, index) in problemList" :key="problemItem.uid">
						<view class="swiper-item">

							<view style="margin-left: 10px; margin-top: 10px; margin-bottom: 10px;">
								<text style="font-weight: 600 !important;">
									{{problemItem.content.replace(/<[^>]*>/g, '')}}
								</text>
							</view>
							<!--题目类型-->
							<view style=" display: inline-block; align-items: start;">
								<!--问题类型-->
								<tn-tag backgroundColor="#01BEFF" fontColor="#FFFFFF" margin="5rpx 5rpx" shape="radius"
									style="display: inline-block; text-align: center; line-height: 50rpx;"
									v-for="item in problemTypeDictList" :key="item.uid"
									v-if="problemItem.problemType == parseInt(item.dictValue)">{{item.dictLabel}}</tn-tag>
								<!--是否精选-->
								<tn-tag backgroundColor="#e6a23c" fontColor="#FFFFFF" margin="5rpx 5rpx" shape="radius"
									style="display: inline-block; text-align: center; line-height: 50rpx;"
									v-if="problemItem.isSelection == '1'">精选</tn-tag>
								<!--问题难度-->
								<tn-tag backgroundColor="#00aa00" fontColor="#FFFFFF" margin="5rpx 5rpx" shape="radius"
									style="display: inline-block; text-align: center; line-height: 50rpx;"
									v-for="item in problemDifficultyDictList" :key="item.uid"
									v-if="problemItem.problemDifficulty == parseInt(item.dictValue)">{{item.dictLabel}}</tn-tag>

								<tn-tag :backgroundColor="colorList[item.problemCount % 11]"
									:fontColor="colorList[item.problemCount % 11]" :plain="true" margin="5rpx 5rpx"
									shape="radius"
									style="display: inline-block; text-align: center; line-height: 50rpx;"
									v-for="item in problemItem.problemTagList" :key="item.uid">{{item.name}}</tn-tag>
							</view>

							<!--解析-->
							<view style="overflow-y: scroll; padding-bottom: 35px;"
								:style="{height: windowsHeight - vuex_custom_bar_height - 100 + 'px'}">
								<mp-html :content="problemItem.answer"
									v-if="problemItem.showAnswer || showAnswer"></mp-html>
								<view v-else style="text-align: center; margin-top: 400rpx;"
									@click="showAnswerMethod()">
									<tn-button width="150rpx" height="100rpx" :fontSize="100" shape="icon"
										margin="10rpx 10rpx"><text class="tn-icon-eye"></text></tn-button>
									<view>点击查看解析</view>
								</view>
							</view>
						</view>
					</swiper-item>
				</swiper>
			</view>

			<view class="tabbar footerfixed dd-glass">
				<view class="tn-flex tn-flex-row-between tn-flex-col-center" style="padding: 20rpx; width: 98%;">

					<tn-button shape="icon" :fontSize="40" margin="5rpx 5rpx" @click="changeCurrentPage(-1)"><text
							class="tn-icon-left"></text></tn-button>
					<tn-button shape="icon" :fontSize="40" margin="5rpx 5rpx" @click="changeCurrentPage(1)"><text
							class="tn-icon-right"></text></tn-button>

					<tn-button shape="icon" :fontSize="40" @click="cancelUserCollect"
						v-if="(selectProblem.collectInfo &&selectProblem.collectInfo.isCollect) || isCollect"
						fontColor="#e6a23c"><text class="tn-icon-star-fill"></text></tn-button>

					<tn-button shape="icon" :fontSize="40" @click="addUserCollect" v-else><text
							class="tn-icon-star"></text></tn-button>

					<tn-button backgroundColor="#24f083" @click="handleProblemDegree('3')"
						fontColor="#FFFFFF">这题我会</tn-button>
					<tn-button backgroundColor="#f56c6c" @click="handleProblemDegree('1')"
						fontColor="#FFFFFF">有一点难</tn-button>

				</view>
			</view>

		</view>

	</view>
</template>

<script>
	import {
		getProblemQueryList,
		problemDegree,
	} from '../../api/problem.js'
	import {
		addCollect,
		deleteCollect,
	} from '../../api/collect.js'
	import {
		getListByDictTypeList
	} from '../../api/sysDictData.js'
	import mpHtml from 'mp-html/dist/uni-app/components/mp-html/mp-html'
	import template_page_mixin from '@/libs/mixin/template_page_mixin.js'
	export default {
		mixins: [template_page_mixin],
		name: 'ProblemDetail',
		data() {
			return {
				colorList: ['red', 'cyan', 'blue', 'green', 'orange', 'purplered', 'purple', 'brown', 'yellowgreen',
					'grey', 'orangered'
				],
				userInfo: {},
				isLogin: false,
				// 1 背题模式，0 答题模式
				readModel: false,
				problemList: [], // 面经列表
				problemDetail: {},
				problemTypeDictList: [],
				problemDifficultyDictList: [],
				images: [
					'https://picture.moguit.cn//blog/admin/jpg/2020/3/16/1584356881898.jpg',
					'https://picture.moguit.cn//blog/admin/jpg/2022/3/21/1647837418004.jpg',
					'https://picture.moguit.cn//blog/admin/jpg/2020/8/18/1597736009574.jpg'
				],
				duration: 1000,
				autoplay: false,
				current: 0,
				showAnswer: false,
				windowsHeight: 0,
				tagUidList: [],
				problemOid: null,
				currentPage: 1,
				pageSize: 10,
				total: 0,
				isEnd: false,
				isCollect: false,
				selectProblem: {},
			}
		},
		components: {
			mpHtml,
		},
		created() {
			this.getDictList()
			// this.getProblemQueryListMethod()
			this.windowsHeight = uni.getSystemInfoSync().windowHeight

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
		// 仅在微信小程序中有效
		onShareTimeline: function() {
			return {
				title: "问答详情",
				path: '/pages/problem/detail?tagUid=' + this.tagUidList[0],
			}
		},
		methods: {
			addUserCollect() {
				let isLogin = this.isLogin
				if (!isLogin) {
					this.$message.error("请先登录")
					return;
				}
				let params = {}
				params.bizUid = this.selectProblem.uid
				params.collectType = 6
				addCollect(params).then(response => {
					if (response.code == this.$ECode.SUCCESS) {
						this.$message.success("收藏成功")
						this.problemList[this.current].collectInfo.isCollect = true
						this.isCollect = true
					} else {
						this.$message.error(response.message)
					}
				})
			},
			cancelUserCollect() {
				let isLogin = this.isLogin
				if (!isLogin) {
					this.$message.error("请先登录")
					return;
				}
				let params = {}
				params.bizUid = this.selectProblem.uid
				params.collectType = 6
				deleteCollect(params).then(response => {
					if (response.code == this.$ECode.SUCCESS) {
						this.$message.success("取消收藏成功")
						this.isCollect = false
						this.problemList[current].collectInfo.isCollect = false
					} else {
						this.$message.error(response.message)
					}
				})
			},
			onLoad(option) {
				console.log("获取跳转的信息", option)
				if (option.tagUid) {
					this.tagUidList = [option.tagUid]
				}
				if (option.oid) {
					this.problemOid = option.oid
				}

				console.log("获取标签UID", this.tagUidList)
				this.getProblemQueryListMethod()
			},
			switchModel() {
				this.readModel = !this.readModel
				this.showAnswer = this.readModel
			},
			showAnswerMethod() {
				// this.showAnswer = !this.showAnswer
				let index = this.current
				let problemList = this.problemList
				problemList[index].showAnswer = true
				this.problemList = problemList
			},
			changePage(event) {
				let current = event.detail.current
				this.selectProblem = this.problemList[current]
				this.current = current
				if (!this.readModel) {
					this.showAnswer = false
				}
				// 当前页面已经到达总页数的90%的时候，需要进行翻页加载
				if (!this.isEnd && current >= this.problemList.length * 0.8) {
					console.log("触发阈值，进行翻页显示", this.problemList.length * 0.8, current)
					this.currentPage = this.currentPage + 1
					this.getProblemQueryListMethod()
				}
				this.isCollect = this.selectProblem.collectInfo.isCollect
			},
			changeCurrentPage(changePage) {
				let current = this.current
				if (current == 0 && changePage < 0) {
					this.$message.error("当前位置已在第一页")
					return
				}
				if (current == this.problemList.length - 1 && changePage > 0) {
					this.$message.error("已经到了最后一页啦")
					return
				}
				current = current + changePage
				this.selectProblem = this.problemList[current]
				this.current = current
				if (!this.readModel) {
					this.showAnswer = false
				}
				// 当前页面已经到达总页数的90%的时候，需要进行翻页加载
				if (!this.isEnd && current >= this.problemList.length * 0.8) {
					console.log("触发阈值，进行翻页显示", this.problemList.length * 0.8, current)
					this.currentPage = this.currentPage + 1
					this.getProblemQueryListMethod()
				}
			},
			getDictList: function() {
				let dictTypeList = [
					'sys_problem_type', 'sys_problem_difficulty', 'sys_normal_disable', 'sys_selection'
				]
				getListByDictTypeList(dictTypeList).then(response => {
					if (response.code == this.$ECode.SUCCESS) {
						let dictMap = response.data;
						this.problemTypeDictList = dictMap.sys_problem_type.list
						this.problemDifficultyDictList = dictMap.sys_problem_difficulty.list
					}
				});
			},
			// 提交掌握记录
			handleProblemDegree(degreeStatus) {
				let isLogin = this.isLogin
				if (!isLogin) {
					this.$message.error("该功能登录后才可使用")
					return;
				}
				let params = {}
				params.problemUid = this.selectProblem.uid
				params.degree = degreeStatus
				params.title = this.selectProblem.title
				problemDegree(params).then(response => {
					if (response.code == this.$ECode.SUCCESS) {
						// 已阅读不弹出
						if (degreeStatus != '2') {
							this.$message.success(response.message)
						}
						this.showAnswerMethod()
					} else {
						this.$message.error(response.message)
					}
				})
			},
			getProblemQueryListMethod() {
				let that = this
				// 根据获取用户信息
				let params = {}
				params.currentPage = this.currentPage
				params.pageSize = this.pageSize
				params.isShowAnswer = "1"
				if (this.tagUidList.length > 0) {
					params.tagUidList = this.tagUidList
				}
				if (this.problemOid) {
					params.oid = this.problemOid
				}
				getProblemQueryList(params).then((res) => {
					if (that.$ECode.SUCCESS == res.code) {
						let list = res.data.records
						this.total = res.data.total;
						this.currentPage = res.data.current;
						for (var i = 0; i < list.length; i++) {
							list[i].showAnswer = false
						}
						console.log(list.length)
						if (list.length == 0) {
							that.isEnd = true
						}

						list = that.problemList.concat(list);
						that.problemList = list
						console.log("面经列表", that.problemList)
						// 首次进入的时候，需要默认指定
						that.selectProblem = that.problemList[this.current]
						console.log("当前选中的面经", that.selectProblem)
					} else {
						that.$message.success(res.message)
					}
				})
			},
		},
	}
</script>

<style scoped>
	.container {
		width: 100%;
		height: 100%;
	}

	/* 底部 start*/
	.footerfixed {
		position: fixed;
		width: 100%;
		bottom: 50rpx;
		z-index: 999;
		background-color: rgba(255, 255, 255, 0.5);
		box-shadow: 0rpx 0rpx 30rpx 0rpx rgba(0, 0, 0, 0.07);
	}
</style>

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
</style>