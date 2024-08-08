<template>
	<view class="template-details tn-safe-area-inset-bottom">
		<!-- 顶部自定义导航 -->
		<tn-nav-bar fixed alpha customBack>
			<view slot="back" class='tn-custom-nav-bar__back' @click="goBack">
				<text class='icon tn-icon-left'></text>
				<text class='icon tn-icon-home-capsule-fill'></text>
			</view>
		</tn-nav-bar>


		<view class="" :style="{paddingTop: vuex_custom_bar_height + 'px'}">
			<tn-form :model="form" ref="form" :errorType="['message']" labelAlign="center" :labelWidth="labelWidth">
				<tn-form-item label="标题图" prop="photo">
					<tn-image-upload name="filedatas" :action="action" :formData="formData" :header="header"
						:maxCount="1" @on-uploaded="onUploaded"></tn-image-upload>
				</tn-form-item>
				<tn-form-item label="标题" prop="summary">
					<tn-input placeholder="请输入文章标题" v-model="form.title" />
				</tn-form-item>
				<tn-form-item label="分类" prop="blogSortName">
					<tn-input v-model="form.blogSortName" type="select" placeholder="请选择文章分类"
						:selectOpen="selectArticleSort" @click="selectArticleSort = true"></tn-input>
				</tn-form-item>
				<tn-form-item label="标签" prop="tagName">
					<tn-input v-model="form.tagName" type="select" placeholder="请选择文章标签" :selectOpen="selectArticleTag"
						@click="selectArticleTag = true"></tn-input>
				</tn-form-item>

				<piaoyiEditor :values="form.content" :maxlength="40000" @changes="saveContens" :readOnly="readOnly"
					:photoUrl="photoUrl" :api="api" name="filedatas" :formData="formData" :header="header" />
			</tn-form>
		</view>

		<!-- 商品类型select -->
		<tn-select v-model="selectArticleTag" mode="single" :list="articleTagList"
			@confirm="articleTagConfirm"></tn-select>
		<tn-select v-model="selectArticleSort" mode="single" :list="articleSortList"
			@confirm="articleSortConfirm"></tn-select>

		<!--提交按钮-->
		<view class="tn-flex tn-flex-row-right tn-flex-col-center" style="padding: 20rpx; width: 98%;">

			<tn-button backgroundColor="#24f083" @click="submitDraft" fontColor="#FFFFFF"
				style="margin-right: 20rpx;">保存草稿</tn-button>
			<tn-button backgroundColor="#f56c6c" @click="submitForm" fontColor="#FFFFFF">提交审核</tn-button>

		</view>
	</view>
</template>

<script>
	import template_page_mixin from '@/libs/mixin/template_page_mixin.js'
	import {
		addBlog,
		getBlogSortList,
		getBlogTagList
	} from '../../api/create_blog.js'
	import piaoyiEditor from '@/uni_modules/piaoyi-editor/components/piaoyi-editor/piaoyi-editor.vue';
	import {
		tokenUtil
	} from '../../utils/token.js'
	import {
		appConfig
	} from '../../config/config.js'
	export default {
		mixins: [template_page_mixin],
		data() {
			return {
				selectArticleTag: false,
				articleTagList: [],
				articleTagOptionList: [],
				selectArticleSort: false,
				articleSortList: [],
				articleSortOptionList: [],
				labelWidth: 120,
				readOnly: false, //是否只读
				photoUrl: appConfig.GATEWAY_API, //服务器图片域名或者ip
				api: '/mogu-picture/file/pictures', //上传图片接口地址
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
				values: '',
				form: {
					title: null,
					summary: null,
					content: null,
					tagName: null,
					blogSortName: null,
					tagUid: null,
					blogSortUid: null,
					fileUid: null,
					isOriginal: "1", //是否原创
					isPublish: "1",
					author: null, //作者
					articlesPart: null //文章出处
				},
			}
		},
		components: {
			piaoyiEditor,
		},
		created() {
			console.log("初始化")
			this.blogSortList()
			this.tagList()
		},
		methods: {
			saveContens(e) {
				this.form.content = e.html
			},
			articleSortConfirm(event) {
				console.log("确认分类", event)
				// 点击商品类型列选择器
				this.form.blogSortName = `${event[0]['label']}`
				this.form.blogSortUid = `${event[0]['value']}`
			},
			articleTagConfirm(event) {
				console.log("确认标签", event)
				this.form.tagName = `${event[0]['label']}`
				this.form.tagUid = `${event[0]['value']}`
			},
			blogSortList() {
				let blogSortParams = {};
				blogSortParams.pageSize = 100;
				blogSortParams.currentPage = 1;
				getBlogSortList(blogSortParams).then(response => {
					console.log("获取博客分类", response)
					if (response.code == this.$ECode.SUCCESS) {
						let articleSortOptionList = response.data.records;
						this.articleSortOptionList = articleSortOptionList;
						let sortNameList = []
						for (let a = 0; a < articleSortOptionList.length; a++) {
							let sortItem = articleSortOptionList[a]
							let item = {
								label: sortItem.sortName,
								value: sortItem.uid
							}
							sortNameList.push(item)
						}
						this.articleSortList = sortNameList
					}
				});
			},
			tagList() {
				let tagParams = {};
				tagParams.pageSize = 100;
				tagParams.currentPage = 1;
				getBlogTagList(tagParams).then(response => {
					console.log("获取博客标签", response)
					if (response.code == this.$ECode.SUCCESS) {
						let articleTagOptionList = response.data.records;
						this.articleTagOptionList = articleTagOptionList;
						let tagNameList = []
						for (let a = 0; a < articleTagOptionList.length; a++) {
							let tagItem = articleTagOptionList[a]
							let item = {
								label: tagItem.content,
								value: tagItem.uid
							}
							tagNameList.push(item)
						}
						this.articleTagList = tagNameList
					}
				});
			},
			submitDraft() {
				this.form.isPublish = "0"
				this.submit();
			},
			submitForm() {
				this.form.isPublish = "1"
				this.submit();
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
					this.form.fileUid = pictureList[0].uid
				}
			},
			tn(e) {
				uni.navigateTo({
					url: e,
				});
			},
			// 提交文章
			submit() {
				// 开始提交文章
				let that = this
				addBlog(this.form).then(response => {
					if (response.code == this.$ECode.SUCCESS) {
						this.$commonUtil.message.success(response.message)
						setTimeout(() => {
							that.tn("/pages/index?index=0")
						}, 500)
					} else {
						this.$commonUtil.message.error(response.message)
					}
				});
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
</style>