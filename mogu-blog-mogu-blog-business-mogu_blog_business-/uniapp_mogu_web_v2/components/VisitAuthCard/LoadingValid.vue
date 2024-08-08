<template>
	<view>
		<tn-popup v-model="dialogVisible" mode="bottom">
			<view style="text-align: center">
				<view v-html="webConfig.loadingValidText"></view>
				<view style="margin-top: 10px">
					<image style="width: 300px" :src="webConfig.loadingValidPhoto"></el-image>
				</view>
				<view style="margin-top: 10px">
					<tn-input v-model="validCode" placeholder="请输入验证码(6位)" border="true" :autoHeight="autoHeight" />
					<!-- <input placeholder="请输入验证码(6位)" style="width: 200px" v-model="validCode"></el-input> -->
				</view>
				<view style="margin-top: 10px; margin-bottom: 30px;">
					<tn-button backgroundColor="tn-bg-blue" fontColor="tn-color-white"
						@click="submitCode">提交验证</tn-button>
				</view>
			</view>
		</tn-popup>
	</view>
</template>

<script>
	import {
		checkValidCode
	} from "../../api/user";
	import {
		getWebConfig
	} from '../../api/about.js'
	export default {
		name: "index",
		data() {
			return {
				validCode: "",
				webConfig: {},
				dialogSize: "35%",
				dialogVisible: true,
			}
		},
		watch: {

		},
		created() {
			this.getWebConfigMethod()
		},
		mounted() {

		},
		methods: {
			submitCode: function() {
				let validCode = this.validCode
				if (validCode.length != 6) {
					this.$message.error("验证码长度必须为6位")
					return
				}
				let params = {}
				params.validCode = validCode
				checkValidCode(params).then(response => {
					if (response.code == this.$ECode.SUCCESS) {
						this.$message.success("校验成功")
						this.$emit("loadingValidCallback", 1)
					} else {
						this.$message.error(response.message)
					}
				})
			},
			getWebConfigMethod() {
				let params = {}
				getWebConfig(params).then(response => {
					if (response.code == this.$ECode.SUCCESS) {
						this.webConfig = response.data
					} else {
						this.$message.error(response.message)
					}
				})
			},
			beforeClose() {
				console.log("关闭前")
				this.$emit("loadingValidCallback", 0)
				done()
			},
		}
	}
</script>

<style>
</style>