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
			<!-- 图文信息 -->
			<block>
				<view class="blogger__item">
					<UserInfoDetail :userDetail="detail.user"></UserInfoDetail>

					<view
						class="blogger__desc tn-margin-top-sm tn-margin-bottom-sm tn-text-justify tn-flex-col-center tn-flex-row-left">
						<view v-for="(label_item,label_index) in detail.userMomentTopicList" :key="label_index"
							class="blogger__desc__label tn-float-left tn-margin-right tn-bg-gray--light tn-round tn-text-sm tn-text-bold">
							<text class="blogger__desc__label--prefix">#</text>
							<text class="tn-text-df">{{ label_item.content }}</text>
						</view>
						<!-- 不用限制长度了，因为发布的时候限制长度了-->
						<view class="blogger__desc__content tn-flex-1 tn-text-justify tn-text-df"
							v-html="detail.content">{{ detail.content }}</text>
						</view>


						<block v-if="detail.photoList">
							<view v-if="[1,2,4].indexOf(detail.photoList.length) != -1" class="tn-padding-top-xs">
								<image v-for="(image_item,image_index) in detail.photoList" :key="image_index"
									class="blogger__main-image" :class="{
                  'blogger__main-image--1 tn-margin-bottom-sm': detail.photoList.length === 1,
                  'blogger__main-image--2 tn-margin-right-sm tn-margin-bottom-sm': detail.photoList.length === 2 || detail.photoList.length === 4
                }" :src="image_item" mode="aspectFill"></image>
							</view>

							<view v-else class="tn-padding-top-xs">
								<tn-grid hoverClass="none" :col="3">
									<block v-for="(image_item,image_index) in detail.photoList" :key="image_index">
										<!-- #ifndef MP-WEIXIN -->
										<tn-grid-item style="width: 30%;margin: 10rpx;">
											<image class="blogger__main-image blogger__main-image--3" :src="image_item"
												mode="aspectFill"></image>
										</tn-grid-item>
										<!-- #endif-->
										<!-- #ifdef MP-WEIXIN -->
										<tn-grid-item style="width: 30%;margin: 10rpx;">
											<image class="blogger__main-image blogger__main-image--3" :src="image_item"
												mode="aspectFill"></image>
										</tn-grid-item>
										<!-- #endif-->
									</block>
								</tn-grid>
							</view>
						</block>

						<view class="tn-flex tn-flex-row-between tn-flex-col-center tn-margin-top-xs">
							<view class="justify-content-item tn-flex tn-flex-col-center">
								<view style="margin-right: 10rpx;">
									<tn-avatar-group :lists="viewUserPhotoList" size="sm"></tn-avatar-group>
								</view>
								<text class="tn-color-gray"
									v-if="viewUserPhotoList && viewUserPhotoList.length > 0 ">{{ viewUserPhotoList.length }}人</text>
							</view>
							<view class="justify-content-item tn-color-gray tn-text-center">
								<view class="">
									<text class="blogger__count-icon tn-icon-footprint"></text>
									<text
										class="tn-padding-right">{{ detail.praiseInfo ? detail.praiseInfo.praiseCount:0}}</text>
									<text class="blogger__count-icon tn-icon-message"></text>
									<text class="tn-padding-right">{{ detail.commentCount }}</text>
									<text class="blogger__count-icon tn-icon-like"></text>
									<text class="">{{ detail.praiseInfo ? detail.praiseInfo.praiseCount:0 }}</text>
								</view>
							</view>
						</view>
					</view>
				</view>
				<!-- 边距间隔 -->
				<!-- <view class="tn-strip-bottom" v-if="index != content.length - 1"></view> -->
			</block>

			<!-- 点赞和收藏-->
			<PraiseCollect :resourceUid="detail.uid" resourceType="4" @praiseCallback="praiseCallback"
				:collectCountValue="detail.collectInfo.collectCount" :isCollectValue="detail.collectInfo.isCollect"
				:praiseCountValue="detail.praiseInfo.praiseCount" :isPraiseValue="detail.praiseInfo.isPraise">
			</PraiseCollect>


		</view>

		<!-- 评论 -->
		<view style="padding-bottom: 120rpx;">
			<CommentList ref="commentList" v-if="momentUid" :maxReplyLevel="commentInfo.maxReplyLevel"
				:targetId="momentUid" :author-uid="detail.userUid" :tableName="commentInfo.source"></CommentList>

			<CommentBox class="tabbar footerfixed" v-if="momentUid" :targetId="momentUid"
				:tableName="commentInfo.source" @callback="commentCallback"></CommentBox>

		</view>

	</view>
</template>

<script>
	import {
		getUserMomentList,
		getUserMomentTopicList,
		addUserMoment
	} from '../../api/moment';
	import UserInfoDetail from '../../components/UserInfoDetail/index.vue'
	import mpHtml from 'mp-html/dist/uni-app/components/mp-html/mp-html'
	import CommentList from '../../components/Comment/comment_list.vue'
	import CommentBox from '../../components/Comment/comment_box.vue'
	import PraiseCollect from '../../components/PraiseCollect/index.vue'
	import template_page_mixin from '@/libs/mixin/template_page_mixin.js'
	export default {
		name: 'TemplateDetails',
		mixins: [template_page_mixin],
		data() {
			return {
				content: [{
						userAvatar: 'https://tnuiimage.tnkjapp.com/blogger/blogger_beibei.jpg',
						userName: '可我会像',
						date: '2022年5月20日',
						label: ['开源', '创意'],
						desc: '开源可商用组件',
						mainImage: [
							'https://tnuiimage.tnkjapp.com/blogger/y11.jpg',
							'https://tnuiimage.tnkjapp.com/blogger/y33.jpg',
							'https://tnuiimage.tnkjapp.com/blogger/y22.jpg',
							'https://tnuiimage.tnkjapp.com/blogger/y44.jpg',
							'https://tnuiimage.tnkjapp.com/blogger/y55.jpg',
						],
						viewUser: {
							latestUserAvatar: [{
									src: 'https://tnuiimage.tnkjapp.com/blogger/avatar_4.jpeg'
								},
								{
									src: 'https://tnuiimage.tnkjapp.com/blogger/avatar_3.jpeg'
								},
								{
									src: 'https://tnuiimage.tnkjapp.com/blogger/avatar_2.jpeg'
								},
								{
									src: 'https://tnuiimage.tnkjapp.com/blogger/avatar_1.jpeg'
								},
							],
							viewUserCount: 65
						},
						collectionCount: 265,
						commentCount: 22,
						likeCount: 102,
					},

				],
				detail: {},
				viewUserPhotoList: [],
				commentInfo: {
					// 评论来源： MESSAGE_BOARD，ABOUT，BLOG_INFO 等 代表来自某些页面的评论
					source: "USER_MOMENT",
					maxReplyLevel: 4, // 最大回复深度
				},
				momentUid: null,
			}
		},
		components: {
			mpHtml,
			CommentList,
			CommentBox,
			UserInfoDetail,
			PraiseCollect,
		},
		watch: {
			detail(newLength, oldLength) {
				console.log("数值变化", newLength, oldLength)
			}
		},
		onShareTimeline: function() {
			return {
				title: this.detail.title,
				path: '/pages/moment/details?uid=' + this.momentUid,
			}
		},
		methods: {
			onLoad(option) {
				this.momentUid = option.uid
				this.getMomentList()
			},
			praiseCallback() {
				this.getMomentList()
			},
			commentCallback() {
				console.log("收到回调")
				this.$refs.commentList.refresh()
			},
			getMomentList() {
				let that = this
				let params = {};
				params.uid = this.momentUid
				params.currentPage = 1;
				params.pageSize = 1;
				this.loading = true;
				getUserMomentList(params).then((res) => {
					console.log('获取用户详情', res);
					if (res.code == this.$ECode.SUCCESS) {
						let dataList = res.data.records;
						if (dataList.length == 0) {
							return
						}
						this.detail = dataList[0]
						let praiseUserList = this.detail.praiseUserList
						if (praiseUserList) {
							let photoList = []
							praiseUserList.forEach(function(item) {
								let photo = {}
								photo.src = item.photoUrl
								photoList.push(photo)
							});
							this.viewUserPhotoList = photoList
							console.log("用户头像", this.viewUserPhotoList)
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

	/* 头像 start */
	.logo-image {
		width: 60rpx;
		height: 60rpx;
		position: relative;
	}

	.logo-pic {
		background-size: cover;
		background-repeat: no-repeat;
		// background-attachment:fixed;
		background-position: top;
		box-shadow: 0rpx 0rpx 80rpx 0rpx rgba(0, 0, 0, 0.15);
		border-radius: 50%;
		overflow: hidden;
		// background-color: #FFFFFF;
	}


	/* 底部 start*/
	.footerfixed {
		position: fixed;
		width: 100%;
		bottom: 0;
		z-index: 999;
		background-color: rgba(255, 255, 255, 0.5);
		box-shadow: 0rpx 0rpx 30rpx 0rpx rgba(0, 0, 0, 0.07);
	}


	/* 内容*/
	.topic {
		position: relative;
		height: 100%;
		z-index: 1;
		margin-bottom: 120rpx;


		/* 表单信息 start */
		&__info {
			margin: 0 50rpx;
			margin-top: 105rpx;
			padding: 30rpx 51rpx;
			border-radius: 20rpx;
			background-color: rgba(255, 255, 255, 1);
			border: 2rpx solid rgba(255, 255, 255, 0.1);
			box-shadow: 0rpx 10rpx 50rpx 0rpx rgba(0, 3, 72, 0.1);

			&__item {

				&__input {
					width: 400rpx;
					height: 60rpx;
					border: 1rpx solid #C6D1D8;
					border-radius: 39rpx;

					&__left-icon {
						width: 10%;
						font-size: 44rpx;
						margin-left: 20rpx;
						margin-right: 5rpx;
						color: #C6D1D8;
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
						font-size: 34rpx;
						margin-right: 20rpx;
						color: #78909C;
					}

					&__right-verify-code {
						width: 34%;
						margin-right: 20rpx;
					}
				}

				&__button {
					width: 100%;
					height: 60rpx;
					text-align: center;
					font-size: 31rpx;
					font-weight: bold;
					line-height: 77rpx;
					// text-indent: 1em;
					border-radius: 100rpx;
					color: #FFFFFF;
					background-color: rgba(255, 255, 255, 0.2);
					// border: 2rpx solid #FFFFFF;
				}

				&__sure {
					height: 60rpx;
					width: 140rpx;
				}

			}
		}

		/* 表单信息 end */

		/* 内容 end */

	}

	/deep/.input-placeholder {
		font-size: 30rpx;
		color: #C6D1D8;
	}
</style>