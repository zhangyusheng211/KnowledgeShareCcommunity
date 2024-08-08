<template>
	<view>
		<tn-popup v-model="dialogVisible" mode="bottom">
			<view style="text-align: center">
				<view style="margin-top: 10px">
					<tn-input v-model="validCode" placeholder="请输入访密码" border="true" :autoHeight="autoHeight" />
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
		checkResourceVisitAuth
	} from "../../api/common";
	export default {
		name: "index",
		props: ['entity', "resourceType"],
		data() {
			return {
				validCode: "",
				dialogVisible: true,
			}
		},
		watch: {

		},
		created() {},
		mounted() {

		},
		methods: {
			submitCode: function() {
				let validCode = this.validCode
				if (validCode.length == 0) {
					this.$message.error("密码不能为空")
					return
				}
				let params = {}
				params.password = validCode
				params.resourceType = this.resourceType
				params.resourceUid = this.entity.uid
				params.visitAuth = 10
				checkResourceVisitAuth(params).then(response => {
					if (response.code == this.$ECode.SUCCESS) {
						this.$message.success("校验成功")
						if (response.data) {
							this.$emit("loadingValidCallback", 1)
						} else {
							this.$message.error("校验失败")
						}
					} else {
						this.$message.error(response.message)
					}
				})
			},
			beforeClose() {
				console.log("关闭前")
				this.$emit("passwordCallback", 0)
				done()
			},
		}
	}
</script>

<style>
</style>