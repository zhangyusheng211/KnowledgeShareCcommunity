<template>
	<view class="template-blogger2">
		<!-- 顶部自定义导航 -->
		<tn-nav-bar fixed alpha customBack>
			<view slot="back" class='tn-custom-nav-bar__back' @click="goBack">
				<text class='icon tn-icon-left'></text>
				<text class='icon tn-icon-home-capsule-fill'></text>
			</view>
		</tn-nav-bar>

		<!-- 立体头像-->
		<view class='cube'
			style="background-size: cover; width: 100%; height: 100%; background: no-repeat center center;"
			:style="'background-image: url('+ userInfo.backgroundFileUrl +');'">
			<view class="cube__container">
				<view class="cube__container__body">
					<view class="cube__container__body__item cube__container__body__item--front"
						:style="{backgroundImage: `url(${userInfo.photoUrl})`}"></view>
					<view class="cube__container__body__item cube__container__body__item--back"
						:style="{backgroundImage: `url(${userInfo.photoUrl})`}"></view>
					<view class="cube__container__body__item cube__container__body__item--right"
						:style="{backgroundImage: `url(${userInfo.photoUrl})`}"></view>
					<view class="cube__container__body__item cube__container__body__item--left"
						:style="{backgroundImage: `url(${userInfo.photoUrl})`}"></view>
					<view class="cube__container__body__item cube__container__body__item--top"
						:style="{backgroundImage: `url(${userInfo.photoUrl})`}"></view>
					<view class="cube__container__body__item cube__container__body__item--bottom"
						:style="{backgroundImage: `url(${userInfo.photoUrl})`}"></view>
				</view>
			</view>


			<view class='tn-text-center tn-margin-top-lg tn-color-white'>
				<view class="tn-padding tn-text-bold tn-text-lg">{{ userInfo.nickName }}</view>
				<view class="tn-padding-bottom-xl tn-text-lg">{{ userInfo.summary }}</view>
			</view>
		</view>

		<!-- 消息&数据 -->
		<view class="blogger-tips-data">
			<view class="blogger-tips-data__wrap tn-bg-white">
				<!-- <view class="blogger-tips-data__message tn-flex tn-flex-row-center">
          <view class="blogger-tips-data__message__container tn-flex tn-flex-row-center tn-flex-col-center tn-bg-gray--light">
            <view class="blogger-tips-data__message__avatar">
              <tn-avatar
                class=""
                :src="tipsDataMessage.latestMessageUserAvatar"
                size="sm">
              </tn-avatar>
            </view>
            <view class="tn-padding-right tn-padding-left">{{ tipsDataMessage.messageCount }} 条新消息</view>
          </view>
        </view> -->
				<view class="blogger-tips-data__info tn-flex">
					<view class="tn-flex-1 tn-padding-sm tn-margin-xs">
						<view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
							<view class="">
								<view class="tn-text-xxl tn-color-orange">
									{{ $t.number.formatNumberAddPriceUnit(userCenterInfo.blogCount) }}
								</view>
							</view>
							<view class="tn-margin-top-xs tn-color-gray tn-text-df tn-text-center">
								<text class="tn-icon-like"></text>
								<text class="tn-padding-left-xs">文章</text>
							</view>
						</view>
					</view>

					<view class="tn-flex-1 tn-padding-sm tn-margin-xs">
						<view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
							<view class="">
								<view class="tn-text-xxl tn-color-red">
									{{ $t.number.formatNumberAddPriceUnit(userCenterInfo.followCount) }}
								</view>
							</view>
							<view class="tn-margin-top-xs tn-color-gray tn-text-df tn-text-center">
								<text class="tn-icon-vip"></text>
								<text class="tn-padding-left-xs">粉丝</text>
							</view>
						</view>
					</view>

					<view class="tn-flex-1 tn-padding-sm tn-margin-xs">
						<view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
							<view class="">
								<view class="tn-text-xxl tn-color-cyan">
									{{ $t.number.formatNumberAddPriceUnit(userCenterInfo.watchCount) }}
								</view>
							</view>
							<view class="tn-margin-top-xs tn-color-gray tn-text-df tn-text-center">
								<text class="tn-icon-star"></text>
								<text class="tn-padding-left-xs">关注</text>
							</view>
						</view>
					</view>

					<view class="tn-flex-1 tn-padding-sm tn-margin-xs">
						<view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
							<view class="">
								<view class="tn-text-xxl tn-color-cyan">
									{{ $t.number.formatNumberAddPriceUnit(userCenterInfo.momentCount) }}
								</view>
							</view>
							<view class="tn-margin-top-xs tn-color-gray tn-text-df tn-text-center">
								<text class="tn-icon-star"></text>
								<text class="tn-padding-left-xs">动态</text>
							</view>
						</view>
					</view>

					<view class="tn-flex-1 tn-padding-sm tn-margin-xs">
						<view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
							<view class="">
								<view class="tn-text-xxl tn-color-cyan">
									{{ $t.number.formatNumberAddPriceUnit(userInfo.userMedalCount) }}
								</view>
							</view>
							<view class="tn-margin-top-xs tn-color-gray tn-text-df tn-text-center">
								<text class="tn-icon-star"></text>
								<text class="tn-padding-left-xs">勋章</text>
							</view>
						</view>
					</view>

				</view>
			</view>
		</view>

		<!--分类-->
		<view class="tabs-fixed tn-bg-white">
			<view class="tn-flex tn-flex-col-between tn-flex-col-center tn-padding-top-sm">
				<view class="justify-content-item" style="width: 87vw; overflow: hidden">
					<tn-tabs :list="userSortlist" :current="current" :isScroll="true" activeColor="#000" :bold="true"
						:fontSize="32" :badgeOffset="[20, 50]" @change="tabChange" backgroundColor="#FFFFFF"
						:height="70"></tn-tabs>
				</view>
			</view>
		</view>

		<!--文章和问答详情-->
		<view class="tn-margin-bottom-lg" v-if="current == 0 || current == 1 || current == 4">
			<block v-for="(item, index) in listData" :key="index">
				<view class="article-shadow tn-margin">
					<view class="tn-flex">
						<view class="tn-margin-sm tn-padding-top-xs" style="width: 100%">
							<view class="tn-text-lg tn-text-bold clamp-text-1 tn-text-justify"
								@click="tn('article/details?oid=' + item.oid)">
								<text class="">{{ item.title }}</text>
							</view>
							<view class="tn-padding-top-xs" style="min-height: 90rpx"
								@click="tn('article/details?oid=' + item.oid)">
								<text class="tn-text-df tn-color-gray clamp-text-2 tn-text-justify">
									{{ item.summary }}
								</text>
							</view>
							<view class="tn-flex tn-flex-row-between tn-flex-col-between tn-margin-top-sm"
								style="align-items: center;">
								<view
									style="display: inline-block; width: 60rpx;height: 60rpx; background-size: cover; border-radius: 50%;"
									:style="'background-image:url(' + item.user.photoUrl + ')'"
									@click="tn('mine/user_center?uid=' + item.user.uid)">
									<view
										style="margin-left: 70rpx; margin-top: 15rpx; width: 200rpx; font-size: 14px; color: #AAAAAA;">
										{{item.user.nickName}}
									</view>
								</view>


								<view class="justify-content-item tn-color-gray tn-text-center"
									style="padding-top: 5rpx">
									<text class="tn-icon-like-lack tn-text-lg" style="padding-right: 5rpx"></text>
									<text class="tn-text-df">{{ item.clickCount }}</text>
									<text class="tn-icon-fire tn-text-lg" style="padding-right: 5rpx"></text>
									<text class="tn-padding-right tn-text-df">{{ item.collectCount }}</text>
								</view>
							</view>
						</view>

						<view v-if="item.photoList " class="image-pic tn-margin-sm"
							:style="'background-image:url(' + item.photoList[0] + ')'">
							<view class="image-article"></view>
						</view>
					</view>
				</view>
			</block>
		</view>

		<view class="tn-margin-bottom-lg" v-if="current == 2">
			<view class="tn-margin" v-for="(item,index) in listData" :key="index">
				<view class="tn-flex tn-flex-row-between tn-flex-col-center tn-padding website-shadow"
					@click="tn('/preferredPages/shop')">
					<view class="justify-content-item">
						<view class="tn-flex tn-flex-col-center tn-flex-row-left">
							<view class="logo-pic tn-shadow">
								<view class="logo-image">
									<view class="tn-shadow-blur"
										:style="'background-image:url(' + item.user.photoUrl + ')'"
										style="width: 110rpx;height: 110rpx;background-size: cover;">
									</view>
								</view>
							</view>
							<view class="tn-padding-right tn-color-black">
								<view class="tn-padding-right tn-padding-left-sm tn-text-lg tn-text-bold">
									{{item.user.nickName}}
								</view>
								<view class="tn-padding-right tn-padding-top-xs tn-text-ellipsis tn-padding-left-sm">
									<text class="tn-color-purplered tn-text-lg">关注了TA</text>
								</view>
							</view>
						</view>
					</view>
					<view class="justify-content-item tn-flex-row-center">
						<view class="tn-cool-bg-color-15 tn-padding-xs tn-color-white tn-round tn-shadow-blur">
							<text class="tn-padding-left-xs">关注</text>
						</view>
					</view>
				</view>
			</view>
		</view>

		<view class="tn-margin-bottom-lg" v-if="current == 3">
			<block v-for="(item, index) in listData" :key="index">
				<view class="article-shadow tn-margin">
					<view class="tn-flex">
						<view class="tn-margin-sm tn-padding-top-xs" style="width: 100%">
							<view class="tn-text-lg tn-text-bold clamp-text-1 tn-text-justify"
								@click="tn('article/details?oid=' + item.oid)">
								<text class="">动态</text>
							</view>
							<view class="tn-padding-top-xs" style="min-height: 90rpx"
								@click="tn('article/details?oid=' + item.oid)">
								<text class="tn-text-df tn-color-gray clamp-text-2 tn-text-justify">
									{{ item.content }}
								</text>
							</view>
							<view class="tn-flex tn-flex-row-between tn-flex-col-between tn-margin-top-sm"
								style="align-items: center;">
								<view
									style="display: inline-block; width: 60rpx;height: 60rpx; background-size: cover; border-radius: 50%;"
									:style="'background-image:url(' + item.user.photoUrl + ')'"
									@click="tn('mine/user_center?uid=' + item.user.uid)">
									<view
										style="margin-left: 70rpx; margin-top: 15rpx; width: 200rpx; font-size: 14px; color: #AAAAAA;">
										{{item.user.nickName}}
									</view>
								</view>


								<view class="justify-content-item tn-color-gray tn-text-center"
									style="padding-top: 5rpx">
									<text class="tn-icon-like-lack tn-text-lg" style="padding-right: 5rpx"></text>
									<text class="tn-text-df">{{ item.clickCount }}</text>
									<text class="tn-icon-fire tn-text-lg" style="padding-right: 5rpx"></text>
									<text class="tn-padding-right tn-text-df">{{ item.collectCount }}</text>
								</view>
							</view>
						</view>

						<view v-if="item.photoList " class="image-pic tn-margin-sm"
							:style="'background-image:url(' + item.photoList[0] + ')'">
							<view class="image-article"></view>
						</view>
					</view>
				</view>
			</block>
		</view>


		<!-- 悬浮按钮-->
		<view class="tn-flex tn-flex-row-between tn-footerfixed">
			<view class="tn-flex-1 justify-content-item tn-margin-xs tn-text-center">
				<tn-button backgroundColor="#00FFC6" padding="40rpx 0" width="90%" shadow fontBold>
					<text class="tn-icon-add tn-padding-right-xs tn-color-black"></text>
					<text class="tn-color-black">关 注</text>
				</tn-button>
			</view>
			<view class="tn-flex-1 justify-content-item tn-margin-xs tn-text-center">
				<tn-button backgroundColor="#FFF00D" padding="40rpx 0" width="90%" shadow fontBold open-type="share">
					<text class="tn-icon-share-triangle tn-padding-right-xs tn-color-black"></text>
					<text class="tn-color-black">分 享</text>
				</tn-button>
			</view>
		</view>

		<view class='tn-tabbar-height'></view>

	</view>
</template>

<script>
	import template_page_mixin from '@/libs/mixin/template_page_mixin.js'
	import {
		getUserByUid,
		getUserCenterByUid,
		getBlogListByUser,
		getQuestionListByUser,
		getUserWatchList,
	} from '../../api/about.js'
	import {
		getListByDictTypeList,
	} from '../../api/sysDictData.js'
	import {
		getUserMomentList,
	} from '../../api/moment.js'
	import {
		getProblemQueryList,
	} from '../../api/problem.js'
	export default {
		name: 'TemplateBlogger2',
		mixins: [template_page_mixin],
		data() {
			return {
				// 内容默认隐藏显示的高度
				contentHideShowHeight: 0,
				userInfo: {
					avatar: [
						'https://tnuiimage.tnkjapp.com/blogger/blogger_avatar_1.jpeg',
						'https://tnuiimage.tnkjapp.com/blogger/blogger_avatar_2.jpeg',
						'https://tnuiimage.tnkjapp.com/blogger/blogger_avatar_3.jpeg',
					],
					username: '菜的一撇的北北吖',
					desc: '你是不是傻，菜的一撇的北北'
				},
				tipsDataMessage: {
					latestMessageUserAvatar: 'https://tnuiimage.tnkjapp.com/blogger/avatar_2.jpeg',
					messageCount: 3,
					likeCount: 1290,
					hotReviewsCount: 896,
					fansCount: 962,
					focusCount: 86
				},
				content: [{
						userAvatar: 'https://tnuiimage.tnkjapp.com/blogger/blogger_beibei.jpg',
						userName: '菜的一撇的北北吖',
						date: '2021年12月20日',
						label: ['开源', '创意', 'UI框架'],
						desc: '开源可商用组件，助你开发酷炫UI一臂之力',
						content: '基础常用的布局元素，酷炫完善的配色体系，统一可增的图标 icon ，简便调用的功能组件，酷炫的前端页面，吖，编不下去了',
						viewUser: {
							latestUserAvatar: [{
									src: 'https://tnuiimage.tnkjapp.com/blogger/avatar_1.jpeg'
								},
								{
									src: 'https://tnuiimage.tnkjapp.com/blogger/avatar_2.jpeg'
								},
								{
									src: 'https://tnuiimage.tnkjapp.com/blogger/avatar_3.jpeg'
								},
								{
									src: 'https://tnuiimage.tnkjapp.com/blogger/avatar_4.jpeg'
								},
							],
							viewUserCount: 62
						},
						collectionCount: 439,
						commentCount: 46,
						likeCount: 83
					},
					{
						userAvatar: 'https://tnuiimage.tnkjapp.com/blogger/blogger_beibei.jpg',
						userName: '菜的一撇的北北吖',
						date: '2021年12月20日',
						label: ['开源', '创意', 'UI框架'],
						desc: '开源可商用组件，助你开发酷炫UI一臂之力',
						content: '基础常用的布局元素，酷炫完善的配色体系，统一可增的图标 icon ，简便调用的功能组件，酷炫的前端页面，吖，编不下去了',
						mainImage: [
							'https://tnuiimage.tnkjapp.com/blogger/content_1.jpeg'
						],
						viewUser: {
							latestUserAvatar: [{
									src: 'https://tnuiimage.tnkjapp.com/blogger/avatar_1.jpeg'
								},
								{
									src: 'https://tnuiimage.tnkjapp.com/blogger/avatar_3.jpeg'
								},
								{
									src: 'https://tnuiimage.tnkjapp.com/blogger/avatar_2.jpeg'
								},
								{
									src: 'https://tnuiimage.tnkjapp.com/blogger/avatar_4.jpeg'
								},
							],
							viewUserCount: 12
						},
						collectionCount: 902,
						commentCount: 64,
						likeCount: 83
					},
					{
						userAvatar: 'https://tnuiimage.tnkjapp.com/blogger/blogger_beibei.jpg',
						userName: '菜的一撇的北北吖',
						date: '2021年12月20日',
						label: [],
						desc: '',
						content: '',
						mainImage: [
							'https://tnuiimage.tnkjapp.com/shop/computer2.jpg',
							'https://tnuiimage.tnkjapp.com/shop/prototype2.jpg',
						],
						viewUser: {
							latestUserAvatar: [{
									src: 'https://tnuiimage.tnkjapp.com/blogger/avatar_4.jpeg'
								},
								{
									src: 'https://tnuiimage.tnkjapp.com/blogger/avatar_2.jpeg'
								},
								{
									src: 'https://tnuiimage.tnkjapp.com/blogger/avatar_3.jpeg'
								},
								{
									src: 'https://tnuiimage.tnkjapp.com/blogger/avatar_1.jpeg'
								},
							],
							viewUserCount: 56
						},
						collectionCount: 431,
						commentCount: 26,
						likeCount: 84
					},
					{
						userAvatar: 'https://tnuiimage.tnkjapp.com/blogger/blogger_beibei.jpg',
						userName: '菜的一撇的北北吖',
						date: '2021年12月20日',
						label: ['开源', '创意'],
						desc: '开源可商用组件',
						content: '基础常用的布局元素，酷炫完善的配色体系，统一可增的图标 icon ，简便调用的功能组件，酷炫的前端页面，吖，编不下去了 基础常用的布局元素，酷炫完善的配色体系，统一可增的图标 icon ，简便调用的功能组件，酷炫的前端页面，吖，编不下去了',
						mainImage: [
							'https://tnuiimage.tnkjapp.com/swiper/swiper2.jpg',
							'https://tnuiimage.tnkjapp.com/swiper/swiper3.jpg',
							'https://tnuiimage.tnkjapp.com/swiper/swiper4.jpg',
						],
						viewUser: {
							latestUserAvatar: [{
									src: 'https://tnuiimage.tnkjapp.com/blogger/avatar_1.jpeg'
								},
								{
									src: 'https://tnuiimage.tnkjapp.com/blogger/avatar_4.jpeg'
								},
								{
									src: 'https://tnuiimage.tnkjapp.com/blogger/avatar_2.jpeg'
								},
								{
									src: 'https://tnuiimage.tnkjapp.com/blogger/avatar_3.jpeg'
								},
							],
							viewUserCount: 231
						},
						collectionCount: 780,
						commentCount: 89,
						likeCount: 82
					},
					{
						userAvatar: 'https://tnuiimage.tnkjapp.com/blogger/blogger_beibei.jpg',
						userName: '菜的一撇的北北吖',
						date: '2021年12月20日',
						label: ['开源', '链接'],
						desc: 'https://www.yuque.com/tuniao',
						mainImage: [
							'https://tnuiimage.tnkjapp.com/shop/watch1.jpg',
							'https://tnuiimage.tnkjapp.com/shop/watch2.jpg',
							'https://tnuiimage.tnkjapp.com/shop/pillow2.jpg',
							'https://tnuiimage.tnkjapp.com/shop/pillow.jpg',
						],
						viewUser: {
							latestUserAvatar: [{
									src: 'https://tnuiimage.tnkjapp.com/blogger/avatar_2.jpeg'
								},
								{
									src: 'https://tnuiimage.tnkjapp.com/blogger/avatar_1.jpeg'
								},
								{
									src: 'https://tnuiimage.tnkjapp.com/blogger/avatar_4.jpeg'
								},
								{
									src: 'https://tnuiimage.tnkjapp.com/blogger/avatar_3.jpeg'
								},
							],
							viewUserCount: 28
						},
						collectionCount: 432,
						commentCount: 33,
						likeCount: 12
					},
					{
						userAvatar: 'https://tnuiimage.tnkjapp.com/blogger/blogger_beibei.jpg',
						userName: '菜的一撇的北北吖',
						date: '2021年12月20日',
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
						likeCount: 62
					}
				],
				// adSwiperCurrentIndex: 0,
				adList: [{
						image: 'https://tnuiimage.tnkjapp.com/swiper/ad1.jpg'
					},
					{
						image: 'https://tnuiimage.tnkjapp.com/swiper/ad2.jpg'
					},
					{
						image: 'https://tnuiimage.tnkjapp.com/swiper/ad3.jpg'
					},
					{
						image: 'https://tnuiimage.tnkjapp.com/swiper/ad4.jpg'
					},
					{
						image: 'https://tnuiimage.tnkjapp.com/swiper/ad5.jpg'
					}
				],
				adAutoplay: false,
				userUid: "",
				userInfo: {},
				userCenterInfo: {},
				defaultBackgroundImages: [
					"https://picture.moguit.cn//blog/admin/png/2021/12/4/1638581345820.png",
					"https://picture.moguit.cn//blog/admin/png/2021/12/4/1638581344560.png",
					"https://picture.moguit.cn//blog/admin/png/2021/12/4/1638581348162.png",
					"https://picture.moguit.cn//blog/admin/png/2021/12/4/1638581347581.png"
				],
				current: 0,
				scrollList: [{
						name: '发现'
					},
					{
						name: '视频'
					},
					{
						name: '世界',
						count: ''
					}
				],
				userSortlist: [{
						name: '文章',
						type: 1
					},
					{
						name: '问答',
						type: 2
					},
					{
						name: '关注',
						type: 4
					},
					{
						name: '动态',
						type: 5
					},
					{
						name: '问题',
						type: 6
					}
				],
				current: 0,
				listData: [], // 最新博客
				isEnd: false, //是否到底底部了
				loading: false, //是否正在加载
				total: 0,
				currentPage: 1,
				pageSize: 10,
				orderByDescColumn: "create_time", // 降序字段
				userUid: "", // 用户uid
				isWatch: true, // 是否是获取关注者
			}
		},
		computed: {

		},
		onLoad(option) {
			this.initContentData()
			this.contentHideShowHeight = uni.upx2px(56) * 3
			this.userUid = option.uid
			this.getDictList()
			this.getDataList(this.current + 1)
		},
		onReady() {
			this.$nextTick(() => {
				this.getContentRectInfo()
			})
		},
		onShow() {
			this.adAutoplay = true
		},
		onHide() {
			this.adAutoplay = false
		},
		methods: {
			// 跳转
			tn(e) {
				uni.navigateTo({
					url: e,
				});
			},
			// 标签栏值发生改变
			tabChange(index) {
				console.log("切换index", index)
				let sort = this.userSortlist[index]
				this.current = index
				this.currentPage = 1
				this.pageSize = 10
				this.listData = []
				this.getDataList(sort.type)
			},
			/**
			 * 字典查询
			 */
			getDictList: function() {
				let dictTypeList = ['sys_user_tag', 'sys_problem_difficulty', 'sys_problem_type', 'sys_user_level',
					'user_level_images'
				]
				getListByDictTypeList(dictTypeList).then(response => {
					if (response.code == this.$ECode.SUCCESS) {
						let dictMap = response.data;
						this.problemDifficultyDictList = dictMap.sys_problem_difficulty.list
						this.problemTypeDictList = dictMap.sys_problem_type.list
						this.userTagDictList = dictMap.sys_user_tag.list
						this.userLevelDictList = dictMap.sys_user_level.list
						this.userLevelImagesDictList = dictMap.user_level_images.list
						// this.setUserTag(dictMap.sys_user_tag.list)
						if (dictMap.sys_problem_difficulty.defaultValue) {
							this.problemDifficultyDefault = dictMap.sys_problem_difficulty.defaultValue;
						}
						if (dictMap.sys_problem_type.defaultValue) {
							this.problemTypeDefault = dictMap.sys_problem_type.defaultValue;
						}
						// 获取用户信息
						this.getUserInfoMethod(false)
					}
				});
			},
			getLvImage(level) {
				this.userLevelImage = this.userLevelImagesDictList[level - 1].dictValue
			},
			getUserInfoMethod(refresh) {
				let params = {}
				params.userUid = this.userUid
				params.refresh = refresh
				getUserByUid(params).then(response => {
					if (response.code == this.$ECode.SUCCESS) {
						let userInfo = response.data
						if (userInfo.backgroundFileUrl) {

						} else {
							// 随机从背景池中选取一张图
							let defaultBackgroundImages = this.defaultBackgroundImages
							let myDate = new Date();
							let date = myDate.getDate();
							let index = date % defaultBackgroundImages.length
							userInfo.backgroundFileUrl = defaultBackgroundImages[index]
						}
						this.getLvImage(userInfo.userLevel)
						this.userInfo = userInfo
					} else {
						this.$message.error(response.message)
					}
				});

				getUserCenterByUid(params).then(response => {
					if (response.code == this.$ECode.SUCCESS) {
						this.userCenterInfo = response.data
					} else {
						this.$message.error(response.message)
					}
				})

			},
			// 处理内容，给内容添加对应的标识信息
			initContentData() {
				this.content.forEach((item, index) => {
					// 是否需要隐藏内容
					item.hideContent = false
					// 显示所有内容
					item.showAllContent = false
					// 内容容器的实际高度
					item.contentContainerHeight = 0
					// 内容容器是否初始化完成
					item.contentContainerInit = false
					this.$set(this.content, index, item)
				})
			},
			// 获取内容容器的信息
			getContentRectInfo() {
				let contentRect = {}
				const query = uni.createSelectorQuery().in(this)
				// 筛选出存在内容的数据
				this.content.forEach((item, index) => {
					if (item?.content) {
						query.select(`#blogger__content--${index}`).boundingClientRect()
						contentRect[index] = item
					}
				})
				// 获取所有内容的宽高信息
				query.exec(res => {
					if (!res) {
						setTimeout(() => {
							this.getContentRectInfo()
						}, 10)
						return
					}
					// console.log(res);
					res.map((item) => {
						// console.log(item.height, this.contentHideShowHeight);
						// 获取对应的标号
						const id = item.id
						const idIndex = /blogger__content--(\d)/.exec(id)[1]
						let contentItem = this.content[idIndex]
						contentItem.hideContent = item.height > this.contentHideShowHeight
						contentItem.showAllContent = false
						contentItem.contentContainerHeight = item.height
						contentItem.contentContainerInit = true
						this.$set(this.content, idIndex, contentItem)

						// console.log(/blogger__content--(\d)/.exec(id)[1]);
					})
				})
			},

			//最新文章列表
			getDataList(type) {
				let that = this;
				console.log("获取类型", type)
				switch (type) {
					//###################### 获取文章列表 ######################
					case 1: {
						console.log("获取文章列表", type)
						let params = {};
						params.currentPage = this.currentPage;
						params.pageSize = this.pageSize;
						params.orderByDescColumn = this.orderByDescColumn
						params.userUid = this.userUid
						getBlogListByUser(params).then(response => {
							if (response.code == this.$ECode.SUCCESS) {
								console.log("获取的文章列表", response.data)
								let newData = response.data.records;
								let newTempData = that.listData.concat(newData);
								that.listData = newTempData
								that.total = response.data.total;
								that.pageSize = response.data.size;
								that.currentPage = response.data.current;
								//全部加载完毕
								if (newData.length < this.pageSize) {
									this.isEnd = true;
								}
							}

						}, function(err) {

						});
					};
					break
					//###################### 获取文章列表结束 ######################

					//###################### 获取问答列表 #########################
				case 2: {
					let params = {};
					params.currentPage = this.currentPage;
					params.pageSize = this.pageSize;
					params.orderByDescColumn = this.orderByDescColumn
					params.userUid = this.userUid
					getQuestionListByUser(params).then(response => {
						console.log("获取的问答列表", response.data)
						if (response.code == this.$ECode.SUCCESS) {
							let newData = response.data.records;
							let newTempData = that.listData.concat(newData);
							that.listData = newTempData
							that.total = response.data.total;
							that.pageSize = response.data.size;
							that.currentPage = response.data.current;
							//全部加载完毕
							if (newData.length < this.pageSize) {
								this.isEnd = true;
							}
						}

					}, function(err) {

					});
				};
				break
				//###################### 获取问答列表结束 ######################

				//###################### 获取用户粉丝和关注人 ######################
				case 4: {
					let params = {}
					let isWatch = this.isWatch
					console.log("isWatch", isWatch)
					if (isWatch) {
						params.userUid = this.userUid
					} else {
						params.toUserUid = tthis.userUid
					}
					params.pageSize = this.pageSize
					params.currentPage = this.currentPage
					getUserWatchList(params).then(response => {
						if (response.code == this.$ECode.SUCCESS) {
							let newData = response.data.records
							console.log("newData", newData)
							let newTempData = that.listData.concat(newData);
							that.listData = newTempData
							that.total = response.data.total;
							that.pageSize = response.data.size;
							that.currentPage = response.data.current;
							//全部加载完毕
							if (newData.length < this.pageSize) {
								this.isEnd = true;
							}
						} else {
							this.$message.error(response.message)
						}


					}, function(err) {

					})
				};
				break
				case 5: {
					let params = {}
					params.currentPage = this.currentPage;
					params.pageSize = this.pageSize;
					params.orderByDescColumn = this.orderByDescColumn
					params.userUid = this.userUid
					getUserMomentList(params).then(response => {
						if (response.code == this.$ECode.SUCCESS) {
							let newData = response.data.records
							console.log("newData", newData)
							let newTempData = that.listData.concat(newData);
							that.listData = newTempData
							that.total = response.data.total;
							that.pageSize = response.data.size;
							that.currentPage = response.data.current;
							//全部加载完毕
							if (newData.length < this.pageSize) {
								this.isEnd = true;
							}
						} else {
							this.$message.error(response.message)
						}

					}, function(err) {

					})

				};
				break
				case 6: {
					let params = {}
					params.currentPage = this.currentPage;
					params.pageSize = this.pageSize;
					params.orderByDescColumn = this.orderByDescColumn
					params.userUid = this.userUid
					getProblemQueryList(params).then(response => {
						if (response.code == this.$ECode.SUCCESS) {
							this.isEnd = false;
							let newData = response.data.records
							let newTempData = that.listData.concat(newData);
							that.listData = newTempData
							that.total = response.data.total;
							that.pageSize = response.data.size;
							that.currentPage = response.data.current;
							//全部加载完毕
							if (records.length < this.pageSize) {
								this.isEnd = true;
							}
						} else {
							this.isEnd = true;
						}
						this.loading = false;

					}, function(err) {

					});
				};
				break
				//###################### 获取粉丝列表结束 ######################
				default: {
					console.log("都未命中")

				}
				}
			},

		}
	}
</script>

<style lang="scss" scoped>
	.template-blogger2 {}

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

	$cube-size: 120rpx;
	$cube-split: 60rpx;

	/* 立体头像 start*/

	.cube {
		background: #fff;
		background-repeat: no-repeat;
		background-size: cover;
		height: 550rpx;
		display: flex;
		justify-content: center;
		padding-top: 40rpx;
		overflow: hidden;
		position: relative;
		flex-direction: column;
		align-items: center;
		font-weight: 300;

		&__container {
			margin-top: 180rpx;
			position: relative;
			width: $cube-size;
			height: $cube-size;
			-webkit-perspective: 500px;
			perspective: 500px; //透视太大会超过屏幕就不好了吖

			&:before {
				content: '';
				width: $cube-size;
				height: $cube-size;
				position: absolute;
				// top: calc(50% - ($cube-split - 30px));
				// left: calc(50% - $cube-split);
				background-color: #3c6496;
				filter: blur(60px);
				opacity: .8;
			}

			&__body {
				position: absolute;
				width: 100%;
				height: 100%;
				transform-style: preserve-3d;
				transform: translateZ(-75px);
				animation: cubeFrame 35s cubic-bezier(0.36, -0.03, 0.46, 0.95) infinite alternate;
				will-change: transform;

				&__item {
					position: absolute;
					display: block;
					display: flex;
					align-items: center;
					justify-content: center;
					width: $cube-size;
					height: $cube-size;
					font-size: 30px;
					background-repeat: no-repeat;
					background-size: cover;

					&--front {
						transform: rotateY(0deg) translateZ($cube-split);
						background-color: #BEEBFF;
					}

					&--back {
						transform: rotateX(180deg) translateZ($cube-split);
						background-color: #BEEBFF;
					}

					&--right {
						transform: rotateY(90deg) translateZ($cube-split);
						background-color: #BEEBFF;
					}

					&--left {
						transform: rotateY(-90deg) translateZ($cube-split);
						background-color: #BEEBFF;
					}

					&--top {
						transform: rotateX(90deg) translateZ($cube-split);
						background-color: #BEEBFF;
					}

					&--bottom {
						transform: rotateX(-90deg) translateZ($cube-split);
						background-color: #BEEBFF;
					}
				}
			}
		}
	}

	@keyframes cubeFrame {
		10% {
			transform: translateZ(-75px) rotateX(40deg) rotateY(60deg);
		}

		15% {
			transform: translateZ(-75px) rotateX(80deg) rotateY(20deg);
		}

		20% {
			transform: translateZ(-75px) rotateX(-180deg) rotateY(-70deg);
		}

		30% {
			transform: translateZ(-75px) rotateX(90deg) rotateY(180deg);
		}

		40% {
			transform: translateZ(-75px) rotateX(-10deg) rotateY(-140deg);
		}

		45% {
			transform: translateZ(-75px) rotateX(-100deg) rotateY(20deg);
		}

		55% {
			transform: translateZ(-75px) rotateX(-10deg) rotateY(-35deg);
		}

		60% {
			transform: translateZ(-75px) rotateX(180deg) rotateY(360deg);
		}

		70% {
			transform: translateZ(-75px) rotateX(-180deg) rotateY(-360deg);
		}

		80% {
			transform: translateZ(-75px) rotateX(45deg) rotateY(-70deg);
		}

		90% {
			transform: translateZ(-75px) rotateX(-45deg) rotateY(70deg);
		}

		100% {
			transform: translateZ(-75px) rotateX(-360deg) rotateY(360deg);
		}
	}

	/* 立体头像 end*/

	/* 信息提示 start */
	.blogger-tips-data {
		background-color: #F8F7F3;

		&__wrap {
			border-radius: 60rpx 60rpx 0 0;
		}

		&__message {
			padding-top: 60rpx;

			&__container {
				padding: 5rpx;
				border-radius: 100rpx;
			}

			&__avatar {
				margin: 6rpx 0 0 6rpx;
			}
		}

		&__info {
			padding: 40rpx 0 0 0;
		}
	}

	/* 信息提示 end */

	/* 文章内容 start*/
	.blogger {
		&__item {
			padding: 30rpx;
		}

		&__author {
			&__btn {
				margin-right: -12rpx;
				opacity: 0.5;
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

	/* 广告内容 start */
	.ad-image {
		width: 80rpx;
		height: 80rpx;
		position: relative;
	}

	.ad-pic {
		background-size: cover;
		background-repeat: no-repeat;
		// background-attachment:fixed;
		background-position: top;
		border-radius: 20%;
	}

	/* 自定义导航栏内容 end */
</style>
