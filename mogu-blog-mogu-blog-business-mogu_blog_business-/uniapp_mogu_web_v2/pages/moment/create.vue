<template>
	<view class="template-edit tn-safe-area-inset-bottom">
		<!-- 顶部自定义导航 -->
		<tn-nav-bar fixed alpha customBack>
			<view slot="back" class='tn-custom-nav-bar__back' @click="goBack">
				<text class='icon tn-icon-left'></text>
				<text class='icon tn-icon-home-capsule-fill'></text>
			</view>
		</tn-nav-bar>

		<view class="tn-safe-area-inset-bottom" :style="{paddingTop: vuex_custom_bar_height + 'px'}">

			<view class="tn-flex tn-flex-row-between tn-flex-col-center tn-padding-top tn-margin">
				<view class="tn-flex justify-content-item">
					<view class="tn-bg-black tn-color-white tn-text-center"
						style="border-radius: 100rpx;margin-right: 8rpx;width: 45rpx;height: 45rpx;line-height: 45rpx;">
						<text class="tn-icon-topics" style="font-size: 30rpx;"></text>
					</view>
					<view class="tn-text-lg tn-padding-right-xs tn-text-bold">想说点什么</view>
				</view>
				<view class="justify-content-item tn-text-df tn-color-grey">
					<text class="tn-padding-xs">500字内</text>
					<text class="tn-icon-keyboard-circle"></text>
				</view>
			</view>

			<!-- <view class="tn-margin tn-bg-gray--light" style="border-radius: 10rpx;padding: 20rpx 30rpx;">
      	<input placeholder="写下一句简短的标题" name="input" placeholder-style="color:#AAAAAA" ></input>
      </view> -->
			<view class="tn-margin tn-bg-gray--light tn-padding" style="border-radius: 10rpx;">
				<textarea v-model="inputContent" maxlength="500" placeholder="说点什么 , 万一火了呢"
					placeholder-style="color:#AAAAAA"></textarea>
			</view>

			<view class="tn-flex tn-flex-row-between tn-flex-col-center tn-padding-top-xl tn-margin">
				<view class="tn-flex justify-content-item">
					<view class="tn-bg-black tn-color-white tn-text-center"
						style="border-radius: 100rpx;margin-right: 8rpx;width: 45rpx;height: 45rpx;line-height: 45rpx;">
						<text class="tn-icon-image" style="font-size: 30rpx;"></text>
					</view>
					<view class="tn-text-lg tn-padding-right-xs tn-text-bold">发点什么图咧</view>
				</view>
				<view class="justify-content-item tn-text-df tn-color-grey" @tap="clear">
					<text class="tn-padding-xs">清空上传</text>
					<text class="tn-icon-delete"></text>
				</view>
			</view>


			<view class="tn-margin-left tn-padding-top-xs">
				<tn-image-upload-drag ref="imageUpload" name="filedatas" :action="action" :width="236" :height="236"
					:formData="formData" :header="header" :disabled="disabled" :autoUpload="autoUpload"
					:maxCount="maxCount" :showUploadList="showUploadList" :showProgress="showProgress"
					:deleteable="deleteable" :customBtn="customBtn" @sort-list="onSortList" @on-uploaded="onUploaded" />

			</view>

			<view class="tn-flex tn-flex-row-between tn-flex-col-center tn-padding-top-xl tn-margin">
				<view class="tn-flex justify-content-item">
					<view class="tn-bg-black tn-color-white tn-text-center"
						style="border-radius: 100rpx;margin-right: 8rpx;width: 45rpx;height: 45rpx;line-height: 45rpx;">
						<text class="tn-icon-tag" style="font-size: 30rpx;"></text>
					</view>
					<view class="tn-text-lg tn-padding-right-xs tn-text-bold">话题标签</view>
				</view>
				<view class="justify-content-item tn-text-df tn-color-grey">
					<text class="tn-padding-xs">选择</text>
					<text class="tn-icon-right"></text>
				</view>
			</view>

			<view class="tn-tag-content tn-margin tn-text-justify tn-padding-bottom">

				<tn-checkbox-group @change="checkboxGroupChange" style="margin: 0 auto;" :max="3">
					<tn-checkbox v-for="(item, index) in momentTopicList" :key="index" :name="item.uid"
						style="float: none;">
						<view class="tn-tag-content__item tn-margin-right tn-round tn-text-sm tn-text-bold"
							:class="[`tn-bg-${colorList[index%9]}--light tn-color-${colorList[index%9]}`]">
							<text class="tn-tag-content__item--prefix">#</text> {{ item.content }}
						</view>
					</tn-checkbox>
				</tn-checkbox-group>

			</view>

			<!-- 悬浮按钮-->
			<view class="tn-flex tn-footerfixed">
				<view class="tn-flex-1 justify-content-item tn-margin-sm tn-text-center">
					<tn-button backgroundColor="#00FFC6" padding="40rpx 0" width="60%" shadow fontBold
						@click="submitUserMoment">
						<!-- <text class="tn-icon-light tn-padding-right-xs tn-color-black"></text> -->
						<text class="tn-color-black">发 布</text>
						<text class="tn-icon-camera tn-padding-left-xs tn-color-black"></text>
					</tn-button>
				</view>
			</view>

		</view>

		<view class='tn-tabbar-height'></view>

	</view>
</template>

<script>
	import template_page_mixin from '@/libs/mixin/template_page_mixin.js'
	import {
		addUserMoment,
		getUserMomentTopicList
	} from '../../api/moment';
	import {
		tokenUtil
	} from '../../utils/token.js'
	import {
		appConfig
	} from '../../config/config.js'
	export default {
		name: 'TemplateEdit',
		mixins: [template_page_mixin],
		data() {
			return {
				colorList: ['red', 'cyan', 'blue', 'green', 'orange', 'purplered', 'purple', 'brown', 'yellowgreen',
					'grey', 'orangered'
				],
				action: appConfig.GATEWAY_API + '/mogu-picture/file/pictures',
				header: {
					Authorization: tokenUtil.get(),
					ContentType: 'application/json'
				},
				formData: {
					source: 'picture',
					userUid: 'uid00000000000000000000000000000000',
					adminUid: 'uid00000000000000000000000000000000',
					projectName: 'blog',
					sortName: 'moment',
					token: tokenUtil.get()
				},
				showUploadList: true,
				customBtn: false,
				autoUpload: true,
				showProgress: false,
				deleteable: true,
				customStyle: false,
				maxCount: 3,
				disabled: false,
				inputContent: "",
				pictureList: [],
				momentTopicMap: {},
				momentTopicList: [],
				topicValue: [],
			}
		},
		onLoad() {

		},
		created() {
			// 判断是否登录
			this.userMomentTopicList()
		},
		methods: {
			userMomentTopicList: function() {
				let params = {}
				params.currentPage = 1;
				params.pageSize = 10;
				getUserMomentTopicList(params).then(response => {
					if (response.code == this.$ECode.SUCCESS) {
						let momentTopicList = response.data.records;
						let momentTopicMap = {}
						for (let i = 0; i < momentTopicList.length; i++) {
							momentTopicMap[momentTopicList[i].uid] = momentTopicList[i]
						}
						this.momentTopicMap = momentTopicMap
						this.momentTopicList = momentTopicList
					}
				});
			},
			checkboxGroupChange(e) {
				console.log("单选", e)
				this.topicValue = e
			},
			// 提交用户动态
			submitUserMoment() {
				console.log("提交表单", this.form)
				if (this.inputContent == "") {
					this.$message.error("内容不能为空")
					return
				}
				// 开始提交动态
				let photoList = this.pictureList
				console.log("获取的图片列表", this.pictureList)
				let fileUids = "";
				for (let i = 0; i < photoList.length; i++) {
					fileUids += photoList[i].uid + ","
				}

				let topicUids = "";
				if (this.topicValue.length > 3) {
					this.$message.error("话题选择不能超过3个")
					return
				}
				let topicList = this.topicValue
				for (let i = 0; i < topicList.length; i++) {
					topicUids += topicList[i] + ","
				}
				let params = {}
				params.content = this.inputContent
				params.fileUids = fileUids
				params.topicUids = topicUids
				addUserMoment(params).then(response => {
					if (response.code == this.$ECode.SUCCESS) {
						this.$message.success(response.message);
						// 发表成功，清空内容
						// this.clearUserMoment()
						// 跳转到动态页面
						// this.tn("/pages/index?index=1")
					} else {
						this.$message.error(response.message);
					}
				})
			},
			// 跳转
			tn(e) {
				uni.navigateTo({
					url: e,
				});
			},
			// 手动上传文件
			upload() {
				// 提交图片资源
				console.log("提交")
				this.$refs.imageUpload.upload()
			},
			onUploaded(lists, name) {
				console.log("图片批量上传成功", lists)
				for (let a = 0; a < lists.length; a++) {
					let item = lists[a]
					if (item.error) {
						continue
					}
					if (item.response.code != "success") {
						continue
					}
					let pictureList = item.response.data
					if (pictureList.length == 0) {
						continue
					}
					this.pictureList.push(pictureList[0])
				}
				console.log("图片列表处理成功", this.pictureList)
			},
			// 手动清空列表
			clear() {
				this.$refs.imageUpload.clear()
			},
			// 图片拖拽重新排序
			onSortList(list) {
				console.log(list);
			},
		}
	}
</script>

<style lang="scss" scoped>
	.template-edit {}

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

	/* 底部悬浮按钮 start*/
	.tn-tabbar-height {
		min-height: 100rpx;
		height: calc(120rpx + env(safe-area-inset-bottom) / 2);
	}

	.tn-footerfixed {
		position: fixed;
		width: 100%;
		bottom: calc(30rpx + env(safe-area-inset-bottom));
		z-index: 1024;
		box-shadow: 0 1rpx 6rpx rgba(0, 0, 0, 0);

	}

	/* 底部悬浮按钮 end*/

	/* 标签内容 start*/
	.tn-tag-content {
		&__item {
			display: inline-block;
			line-height: 45rpx;
			padding: 10rpx 30rpx;
			margin: 20rpx 20rpx 5rpx 0rpx;

			&--prefix {
				padding-right: 10rpx;
			}
		}
	}

	/* 标签内容 end*/
</style>