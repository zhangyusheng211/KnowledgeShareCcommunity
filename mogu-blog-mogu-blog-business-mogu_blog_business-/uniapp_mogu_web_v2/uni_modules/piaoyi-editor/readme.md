### piaoyiEditor 富文本编辑器

**使用方法：**

```
<template>
	<view class="richtext">
		<piaoyiEditor :values="values" :maxlength="3000" @changes="saveContens" :readOnly="readOnly" :photoUrl="photoUrl" :api="api" :name="name"/>
		<view class="">
			{{txt}}
		</view>
	</view>
</template>

<script>
	import piaoyiEditor from '@/uni_modules/piaoyi-editor/components/piaoyi-editor/piaoyi-editor.vue';
	export default {
		data() {
			return {
				readOnly: false, //是否只读
				photoUrl: 'http://test.com', //服务器图片域名或者ip
				api: '/upload', //上传图片接口地址
				txt: '',
				name: 'file',
				values: '<div>11111222</div>'
			};
		},
		components: {
			piaoyiEditor
		},
		methods: {
			saveContens(e) {
				this.txt = e.html
			}
		},
		onShareAppMessage(res) {
			if (res.from === 'button') { // 来自页面内分享按钮
				console.log(res.target)
			}
			return {
				title: '多功能富文本编辑器！',
				path: '/pages/editor/editor'
			}
		},
		onShareTimeline(res) {
			if (res.from === 'button') { // 来自页面内分享按钮
				console.log(res.target)
			}
			return {
				title: '多功能富文本编辑器！'
			}
		}
	}
</script>

<style lang="scss">

</style>

```

#### 事件说明
使用上传图片功能时需要注意查看代码里面的uni.uploadFile方法，因为它的返回值取决于自己后端接口的值，所以可以根据实际情况就行更改即可

|   事件名    | 返回值 |      描述      |
| :---------: | :----: | :------------: |
| @saveContens |   {html: html片段, length: html长度}   | 文本框内容回调 |

#### Prop

| 参数名称 | 默认值  | 描述                           |
| -------- | ------------------------------ |
| maxlength| 300  | 输入最大长度 |
| readOnly  | false  | 是否只读 |
| api | 空  | 上传图片接口地址 |
| photoUrl  | 空  | 服务器图片域名或者ip |
| name  | 'file'  | 上传图片接口的key |
| values  | ''  | 富文本编辑器默认值 |

### 注：近期收到使用用户反馈，存在以下两个问题（如有好的建议，期待私信，谢谢）

1、当组件在页面中部或者底部的时候，进入页面，页面会自动滚动到富文本编辑器的区域
属于正常现象；
官网文档有这么一句话：编辑器聚焦时页面会被上推，系统行为以保证编辑区可见；
作者建议这种情况，进入页面初始设置富文本编辑器为只读，然后页面滚动到一定距离的时候取消这个只读；

2、组件粘贴文字出现软键盘闪烁，导致文字粘贴不了
目前暂未发现解决方法，本插件是在官方的editor基础上开发的，这个组件存在这个问题；
经测试长按出现粘贴后，手不松开滑动到粘贴字样上就不会出现闪烁，然后松开手，点击粘贴就可以；

3、有些上传图片接口是需要token的，接口需要token的话可以在组件内搜索uni.uploadFile,加上headers头部参数

### 可接定制化组件开发
### 右侧有本人代表作小程序二维码，可以扫码体验
### 如使用过程中有问题或有一些好的建议，欢迎加QQ群互相学习交流：120594820