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
			<view class="tn-text-bold tn-text-lg tn-padding-left">{{detail.title}}</view>

			<!-- 图文信息 -->
			<block>
				<view class="blogger__item">
					<!--用户头像详情页-->
					<UserInfoDetail :userDetail="detail.user"></UserInfoDetail>
					<view>
						<view style="display: inline-block;"
							class="blogger__desc__label tn-margin-right tn-bg-gray--light tn-round tn-text-sm tn-text-bold">
							<text class="blogger__desc__label--prefix">#</text>
							<text class="tn-text-df">{{ detail.blogSort.sortName }}</text>
						</view>
						<view style="display: inline-block;" v-for="(item, index) in detail.tagList" :key="index"
							class="blogger__desc__label tn-margin-right tn-bg-gray--light tn-round tn-text-sm tn-text-bold">
							<text class="blogger__desc__label--prefix">#</text>
							<text class="tn-text-df">{{ item.content }}</text>
						</view>
					</view>
					<view
						class="blogger__desc tn-margin-top-sm tn-margin-bottom-sm tn-text-justify tn-flex-col-center tn-flex-row-left">
						<!-- 不用限制长度了，因为发布的时候限制长度了-->
						<!-- 						<text v-if="!detail.label || detail.label.length < 4"
							class="blogger__desc__content tn-flex-1 tn-text-justify tn-text-df"
							v-html="detail.content">{{ detail.content }}</text> -->


						<tn-read-more ref="readMore" v-if="!detail.visitAuthSuccess && detail.loadingArea == '1'"
							openText="完成下方校验, 解锁本篇文章" openIcon="lock" @open="openCheck">
							<rich-text :nodes="detail.content"></rich-text>
						</tn-read-more>

						<!--展示正文-->
						<mp-html v-if="detail.visitAuthSuccess" :content="detail.content"></mp-html>

					</view>

					<!--展示限制区域-->
					<VisitAuthCard v-if="!detail.visitAuthSuccess" :entity="detail" resourceType="BLOG"
						@refresh="getArticleDetail">
					</VisitAuthCard>



					<view class="tn-flex tn-flex-row-between tn-flex-col-center tn-margin-top-xs">
						<view class="justify-content-item tn-flex tn-flex-col-center">
							<view style="margin-right: 10rpx;">
								<tn-avatar-group :lists="viewUserPhotoList" size="sm"></tn-avatar-group>
							</view>
							<text class="tn-color-gray">{{ viewUserPhotoList.length }}人</text>
						</view>
						<view class="justify-content-item tn-color-gray tn-text-center" v-if="vuex_custom_mini == '1'">
							<view class="">
								<text class="blogger__count-icon tn-icon-footprint"></text>
								<text class="tn-padding-right">{{ detail.clickCount}}</text>
								<text class="blogger__count-icon tn-icon-message"></text>
								<text class="tn-padding-right">{{ detail.commentCount }}</text>
								<text class="blogger__count-icon tn-icon-like"></text>
								<text class="">{{ detail.praiseInfo ? detail.praiseInfo.praiseCount:0 }}</text>
							</view>
						</view>
					</view>
				</view>

				<!-- 边距间隔 -->
				<!-- <view class="tn-strip-bottom" v-if="index != content.length - 1"></view> -->
			</block>

			<!-- 关注和收藏-->
			<PraiseCollect :resourceUid="detail.uid" resourceType="1" @praiseCallback="praiseCallback"
				:collectCountValue="detail.collectInfo.collectCount" :isCollectValue="detail.collectInfo.isCollect"
				:praiseCountValue="detail.praiseInfo.praiseCount" :isPraiseValue="detail.praiseInfo.isPraise">
			</PraiseCollect>

		</view>

		<CommentList ref="commentList" v-if="targetID" :maxReplyLevel="commentInfo.maxReplyLevel" :targetId="targetID"
			:author-uid="detail.userUid" :tableName="commentInfo.source"></CommentList>

		<CommentBox class="tabbar footerfixed" v-if="targetID" :targetId="targetID" :tableName="commentInfo.source"
			@callback="commentCallback"></CommentBox>

	</view>
</template>

<script>
	import {
		getBlogByUid,
	} from '../../api/blogContent.js';
	import mpHtml from 'mp-html/dist/uni-app/components/mp-html/mp-html'
	import template_page_mixin from '@/libs/mixin/template_page_mixin.js'
	import CommentList from '../../components/Comment/comment_list.vue'
	import CommentBox from '../../components/Comment/comment_box.vue'
	import UserInfoDetail from '../../components/UserInfoDetail/index.vue'
	import PraiseCollect from '../../components/PraiseCollect/index.vue'
	import VisitAuthCard from '../../components/VisitAuthCard/index.vue'
	export default {
		name: 'TemplateDetails',
		mixins: [template_page_mixin],
		data() {
			return {
				blogOid: null,
				detail: {},
				viewUserPhotoList: [],
				markdownContent: "",
				currentPage: 1,
				pageSize: 10,
				commentInfo: {
					// 评论来源： MESSAGE_BOARD，ABOUT，BLOG_INFO 等 代表来自某些页面的评论
					source: "BLOG_INFO",
					maxReplyLevel: 4, // 最大回复深度
				},
				targetID: null,
			}
		},
		components: {
			mpHtml,
			CommentList,
			CommentBox,
			UserInfoDetail,
			PraiseCollect,
			VisitAuthCard,
		},
		watch: {

		},
		onReady() {


		},
		onLoad(option) {
			console.log("获取跳转的信息", option)
			this.blogOid = option.oid
			this.getArticleDetail()
		},
		// 仅在微信小程序中有效
		onShareTimeline: function() {
			return {
				title: this.detail.title,
				path: '/pages/article/details?oid=' + this.blogOid,
			}
		},
		// 适用于所有平台
		onShareAppMessage: function(res) {
			if (res.from === 'button') {
				// 来自按钮的分享
			}
			return {
				title: this.detail.title,
				path: '/pages/article/details?oid=' + this.blogOid,
			}
		},
		methods: {
			praiseCallback() {
				this.getArticleDetail()
			},
			commentCallback() {
				console.log("收到回调")
				this.$refs.commentList.refresh()
			},
			openCheck(index) {
				this.getArticleDetail()
			},
			getArticleDetail() {
				let that = this
				let params = {};
				params.oid = this.blogOid
				getBlogByUid(params).then((res) => {
					console.log('获取文章详情', res);
					if (res.code == this.$ECode.SUCCESS) {
						this.detail = res.data
						this.targetID = res.data.uid

						let praiseUserList = res.data.praiseUserList
						if (praiseUserList) {
							let photoList = []
							praiseUserList.forEach(function(item) {
								let photo = {}
								photo.src = item.photoUrl
								photoList.push(photo)
							});
							this.viewUserPhotoList = photoList
						}
					}
				});
			},
			// 跳转
			tn(e) {
				uni.navigateTo({
					url: e,
				});
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

	/* 文章内容 start*/
	.blogger {
		&__item {
			padding: 30rpx;
		}

		&__author {
			&__btn {
				margin-right: -12rpx;
				padding: 0 20rpx;
			}
		}

		&__desc {
			line-height: 55rpx;

			&__label {
				padding: 0 20rpx;
				margin: 0rpx 18rpx 0 0;

				&--prefix {
					color: #00FFC8;
					padding-right: 10rpx;
				}
			}

			&__content {}
		}

		&__content {
			margin-top: 18rpx;
			padding-right: 18rpx;

			&__data {
				line-height: 46rpx;
				text-align: justify;
				overflow: hidden;
				transition: all 0.25s ease-in-out;

			}

			&__status {
				margin-top: 10rpx;
				font-size: 26rpx;
				color: #82B2FF;
			}
		}

		&__main-image {
			border-radius: 16rpx;

			&--1 {
				max-width: 80%;
				max-height: 300rpx;
			}

			&--2 {
				max-width: 260rpx;
				max-height: 260rpx;
			}

			&--3 {
				height: 212rpx;
				width: 100%;
			}
		}

		&__count-icon {
			font-size: 40rpx;
			padding-right: 5rpx;
		}

		&__ad {
			width: 100%;
			height: 500rpx;
			transform: translate3d(0px, 0px, 0px) !important;

			::v-deep .uni-swiper-slide-frame {
				transform: translate3d(0px, 0px, 0px) !important;
			}

			.uni-swiper-slide-frame {
				transform: translate3d(0px, 0px, 0px) !important;
			}

			&__item {
				position: absolute;
				width: 100%;
				height: 100%;
				transform-origin: left center;
				transform: translate3d(100%, 0px, 0px) scale(1) !important;
				transition: transform 0.25s ease-in-out;
				z-index: 1;

				&--0 {
					transform: translate3d(0%, 0px, 0px) scale(1) !important;
					z-index: 4;
				}

				&--1 {
					transform: translate3d(13%, 0px, 0px) scale(0.9) !important;
					z-index: 3;
				}

				&--2 {
					transform: translate3d(26%, 0px, 0px) scale(0.8) !important;
					z-index: 2;
				}
			}

			&__content {
				border-radius: 40rpx;
				width: 640rpx;
				height: 500rpx;
				overflow: hidden;
			}

			&__image {
				width: 100%;
				height: 100%;
			}
		}
	}

	/* 文章内容 end*/

	/* 间隔线 start*/
	.tn-strip-bottom {
		width: 100%;
		border-bottom: 20rpx solid rgba(241, 241, 241, 0.8);
	}

	/* 间隔线 end*/
</style>