<template>
	<view class="template-search tn-safe-area-inset-bottom">

		<view class="tn-navbg" :style="{height: vuex_custom_bar_height + 'px'}">
			<!-- 顶部自定义导航 -->
			<tn-nav-bar fixed alpha customBack>
				<view slot="back" class='tn-custom-nav-bar__back' @click="goBack">
					<text class='icon tn-icon-left-arrow'></text>
				</view>
			</tn-nav-bar>
		</view>

		<!--    <tn-nav-bar fixed alpha customBack>
      <view slot="back" class='tn-custom-nav-bar__back'
        @click="goBack">
        <text class='icon tn-icon-left'></text>
        <text class='icon tn-icon-home-capsule-fill'></text>
      </view>
    </tn-nav-bar> -->

		<view class="tn-search-fixed">
			<view class="tn-flex tn-flex-row-between tn-flex-col-center tn-margin"
				:style="{marginTop: vuex_custom_bar_height + 'px'}">
				<view class="justify-content-item align-content-item">
					<view class="tn-bg-gray--light tn-flex tn-flex-col-center"
						style="border-radius: 100rpx;padding: 10rpx 20rpx 10rpx 20rpx;width: 100%;">
						<text
							class="tn-icon-search justify-content-item tn-padding-right-xs tn-color-gray tn-text-lg"></text>
						<input v-model="keywords" class="justify-content-item" placeholder="想搜点什么咧" name="input"
							placeholder-style="color:#AAAAAA"></input>
					</view>
				</view>

				<view class="align-content-item">
					<view class="justify-content-item tn-text-center">
						<tn-button backgroundColor="#82B2FF" shape="round" padding="20rpx 20rpx" width="150rpx" shadow
							fontBold @tap="" @click="search">
							<text class="tn-color-white">搜 索</text>
						</tn-button>
					</view>
				</view>
			</view>
		</view>

		<view class="" style="margin-top: 160rpx;" :style="{paddingTop: vuex_custom_bar_height + 'px'}">
			<view class="tn-flex tn-flex-row-between tn-margin">
				<view class="justify-content-item tn-text-bold">
					<text class="tn-text-df tn-color-black">热门搜索</text>
				</view>
				<!-- 				<view class="justify-content-item tn-text-df tn-color-grey">
					<text class="tn-padding-xs">删除</text>
					<text class="tn-icon-delete"></text>
				</view> -->
			</view>
		</view>

		<view class="">
			<view class="tn-tag-search tn-margin tn-text-justify">
				<view v-for="(item, index) in hotSearchList" :key="index" @click="hotSearchClick(item.content)"
					class="tn-tag-search__item tn-margin-right tn-round tn-text-sm tn-bg-gray--light tn-color-gray">
					<text class="tn-tag-search__item--prefix">#</text> {{ item.content }}
				</view>
			</view>
		</view>

		<view class="tn-flex tn-flex-row-between tn-padding-top-xl tn-margin tn-padding-bottom">
			<view class="justify-content-item tn-text-bold">
				<text class="tn-text-df tn-color-black">搜索结果</text>
			</view>
			<view class="justify-content-item tn-text-df tn-color-grey">
				<text class="tn-padding-xs">筛选</text>
				<text class="tn-icon-filter"></text>
			</view>
		</view>


		<!-- 不建议写时间，因为写了时间，你就要经常更新文章了鸭-->
		<view class="tn-margin-bottom-lg">
			<block v-for="(item, index) in listData" :key="index">
				<view class="article-shadow tn-margin" @click="goDetail(item)">
					<view class="tn-flex">
						<view class="tn-margin-sm tn-padding-top-xs" style="width: 100%;">
							<view class="tn-text-lg tn-text-bold clamp-text-1 tn-text-justify">
								<view class="" v-html="item.title">{{ item.title }}</view>
							</view>
							<view class="tn-padding-top-xs" style="min-height: 90rpx;">
								<view class="tn-text-df tn-color-gray clamp-text-2 tn-text-justify"
									v-html="item.summary">
									{{ item.summary }}
								</view>
							</view>
							<view class="tn-flex tn-flex-row-between tn-flex-col-between">
								<view style="transform: translate(0rpx,6rpx);"
									class="justify-content-item tn-tag-content__item tn-margin-right-xs tn-round tn-text-sm tn-text-bold tn-bg-blue--light tn-color-blue">
									<text class="tn-tag-content__item--prefix">#</text>
									<text
										v-if="item.resourceType == '1' && searchModel == 0">{{ item.blogSortName }}</text>
									<text v-if="item.resourceType == '1' &&searchModel == 1">{{ item.tagName }}</text>
									<text v-if="item.resourceType == '2'">问答</text>
									<text v-if="item.resourceType == '3'">课程</text>
									<text v-if="item.resourceType == '4'">动态</text>
									<text v-if="item.resourceType == '6'">面经</text>
									<text v-if="item.resourceType == '11'">资源</text>

								</view>
							</view>
						</view>
					</view>
				</view>
			</block>
		</view>

		<view class="tn-margin-top" @click="loadMore" style="height: 50px;">
			<tn-load-more class="tn-margin-top" :status="loadStatus" :loadText="loadText"></tn-load-more>
		</view>

	</view>
</template>

<script>
	import template_page_mixin from '@/libs/mixin/template_page_mixin.js'
	import {
		getHotSearchList,
		getSearchModel,
		searchBlog,
		elasticSearchAgg,
	} from '../../api/search.js'
	export default {
		name: 'TemplateSearch',
		mixins: [template_page_mixin],
		data() {
			return {
				inputValue: '',

				tagList: [{
						color: 'red',
						title: "救救孩子",
					},
					{
						color: 'cyan',
						title: "今天的Bug写了吗",
					},
					{
						color: 'blue',
						title: "北北猪",
					},
					{
						color: 'green',
						title: "捉住那只北北猪",
					},
					{
						color: 'orange',
						title: "祭天叭，产品经理",
					},
					{
						color: 'purple',
						title: "快醒醒，来需求了",
					},
					{
						color: 'brown',
						title: "夏天的第一个Bug",
					}
				],
				content: [],
				hotSearchList: [], // 热门搜索
				listData: [],
				keywords: '',
				currentPage: 1,
				pageSize: 10,
				searchModel: 0, //搜索模式 0:SQL搜索、1:ES搜索
				loadStatus: "loadmore",
				loadText: {
					loadmore: '点击加载更多',
					loading: '快速加载中...',
					nomore: '已经没有了啊'
				}
			}
		},
		created() {
			this.getSearchModelMethod()
			this.getHotSearchListMethod()
		},
		methods: {
			// 跳转
			tn(e) {
				uni.navigateTo({
					url: e,
				});
			},
			getHotSearchListMethod() {
				getHotSearchList().then(result => {
					if (result.code == this.$ECode.SUCCESS) {
						console.log("获取热搜列表0", result)
						this.hotSearchList = result.data;
					}
				})
			},
			getSearchModelMethod() {
				getSearchModel().then(response => {
					if (response.code == this.$ECode.SUCCESS) {
						this.searchModel = response.data
					}
				})
			},
			hotSearchClick(content) {
				this.loadStatus = []
				this.keywords = content
				this.search()
			},
			loadMore() {
				console.log("加载更多")
				if (this.listData.length >= this.total) {
					this.loadStatus = "nomore";
					return;
				}
				this.currentPage = this.currentPage + 1;
				let searchModel = this.searchModel
				if (searchModel == 1) {
					this.elasticSearchAggMethod()
				} else {
					this.searchBlogMethod()
				}
			},
			search() {
				this.loadStatus = []
				this.listData = []
				let searchModel = this.searchModel
				if (searchModel == 1) {
					this.elasticSearchAggMethod()
				} else {
					this.searchBlogMethod()
				}
			},
			goDetail(item) {
				console.log("跳转到详情页", item)
				let resourceType = item.resourceType
				switch (resourceType) {
					// 文章
					case '1': {
						this.tn("/pages/article/details?oid=" + item.oid)
						return
					}
					// 问答 
					case '2': {
						this.$message.success("功能正在建设中")
						return
					}
					// 课程
					case '3': {
						this.tn("/pages/media/mediaDetail?uid=" + item.id)
						return
					}
					// 动态
					case '4': {
						this.tn("/pages/moment/details?uid=" + item.id)
						return
					}
					// 面经
					case '6': {
						this.tn("/pages/problem/detail?oid=" + item.oid)
						return
					}
					// 资源
					case '11': {
						this.tn("/pages/resource/resourceDetail?uid=" + item.id)
						return
					}
				}
			},
			searchBlogMethod() {
				let that = this
				console.log("开始搜索")
				let params = {}
				params.currentPage = this.currentPage
				params.pageSize = this.pageSize
				params.keywords = this.keywords
				this.loadStatus = "loading";
				searchBlog(params).then(response => {
					console.log("返回综合结果", response)
					if (response.code == this.$ECode.SUCCESS) {
						that.isEnd = false;
						//获取总页数
						that.total = response.data.total;
						that.pageSize = response.data.pageSize;
						let listData = [];
						listData = response.data.blogList;
						that.totalPages = response.data.blogList.length;
						// 判断搜索的博客是否有内容
						if (response.data.total <= 0) {
							that.loadStatus = "nomore";
							that.loading = false;
							that.totalPages = 0;
							that.listData = []
							return;
						}
						//全部加载完毕
						if (listData.length < that.pageSize) {
							that.loadStatus = "nomore";
						}
						listData = that.listData.concat(listData);
						that.listData = listData;

						if (this.listData.length >= this.total) {
							this.loadStatus = "nomore";
						} else {
							this.loadStatus = "loadmore";
						}
					} else {
						that.$message.error(response.message)
						that.loadStatus = "nomore";
					}
				});
			},
			elasticSearchAggMethod() {
				let that = this
				console.log("开始搜索")
				let params = {}
				params.currentPage = this.currentPage
				params.pageSize = this.pageSize
				params.keywords = this.keywords
				this.loadStatus = "loading";
				elasticSearchAgg(params).then(response => {
					console.log("返回综合结果", response)
					if (response.code == this.$ECode.SUCCESS) {
						that.isEnd = false;
						//获取总页数
						that.total = response.data.total;
						that.pageSize = response.data.pageSize;
						let listData = [];
						listData = response.data.blogList;
						that.totalPages = response.data.blogList.length;
						// 判断搜索的博客是否有内容
						if (response.data.total <= 0) {
							that.loadStatus = "nomore";
							that.loading = false;
							that.totalPages = 0;
							that.listData = []
							return;
						}
						//全部加载完毕
						if (listData.length < that.pageSize) {
							that.loadStatus = "nomore";
						}
						listData = that.listData.concat(listData);
						that.listData = listData;

						if (this.listData.length >= this.total) {
							this.loadStatus = "nomore";
						} else {
							this.loadStatus = "loadmore";
						}
						console.log("loadStatus", this.loadStatus)
					} else {
						that.$message.error(response.message)
						that.loadStatus = "nomore";
					}
				});
			},
		}
	}
</script>

<style lang="scss" scoped>
	.tn-search-fixed {
		position: fixed;
		top: 50rpx;
		width: 100%;
		transition: all 0.25s ease-out;
		z-index: 1;
	}

	/* 胶囊*/
	.tn-custom-nav-bar__back {
		width: 60%;
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

	}

	/* 顶部渐变*/
	.tn-navbg {
		background: linear-gradient(-120deg, #F15BB5, #9A5CE5, #01BEFF, #00F5D4);
		/* background: linear-gradient(-120deg,  #9A5CE5, #01BEFF, #00F5D4, #43e97b); */
		/* background: linear-gradient(-120deg,#c471f5, #ec008c, #ff4e50,#f9d423); */
		/* background: linear-gradient(-120deg, #0976ea, #c471f5, #f956b6, #ea7e0a); */
		background-size: 500% 500%;
		animation: gradientBG 15s ease infinite;
		position: fixed;
		top: 0;
		width: 100%;
		z-index: 100;
	}

	@keyframes gradientBG {
		0% {
			background-position: 0% 50%;
		}

		50% {
			background-position: 100% 50%;
		}

		100% {
			background-position: 0% 50%;
		}
	}

	/* 搜索标签 start*/
	.tn-tag-search {
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

	/* 标题 start */
	.nav_title {
		-webkit-background-clip: text;
		color: transparent;

		&--wrap {
			position: relative;
			display: flex;
			height: 120rpx;
			font-size: 42rpx;
			align-items: center;
			justify-content: center;
			font-weight: bold;
			background-image: url(https://tnuiimage.tnkjapp.com/title_bg/title00.png);
			background-size: cover;
		}
	}

	/* 标题 end */

	/* 富文本图示意 start */
	.news-img {
		z-index: -1;
		padding-bottom: 40rpx;

		image {
			width: 100%;
			margin: 20rpx 0;
			// height: 3373rpx;
			// z-index: -1;
		}
	}

	/* 资讯主图 start*/
	.image-article {
		border-radius: 8rpx;
		border: 1rpx solid #F8F7F8;
		width: 200rpx;
		height: 200rpx;
		position: relative;
	}

	.image-pic {
		background-size: cover;
		background-repeat: no-repeat;
		// background-attachment:fixed;
		background-position: top;
		border-radius: 10rpx;
	}

	.article-shadow {
		border-radius: 15rpx;
		box-shadow: 0rpx 0rpx 50rpx 0rpx rgba(0, 0, 0, 0.07);
	}

	/* 文字截取*/
	.clamp-text-1 {
		-webkit-line-clamp: 1;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		text-overflow: ellipsis;
		overflow: hidden;
	}

	.clamp-text-2 {
		-webkit-line-clamp: 2;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		text-overflow: ellipsis;
		overflow: hidden;
	}

	/* 标签内容 start*/
	.tn-tag-content {
		&__item {
			display: inline-block;
			line-height: 35rpx;
			padding: 7rpx 25rpx 5rpx 25rpx;

			&--prefix {
				padding-right: 10rpx;
			}
		}
	}

	/* 标签内容 end*/
</style>