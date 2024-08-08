<template>
    <div></div>
</template>

<script>
import {getSystemConfig} from '@/api/systemConfig'

export default {
    name: "index",
    data() {
        return {
            systemConfig: {},
        }
    },
    // 用于初始化数据的组件
    created() {
        // 初始化想要的数据
        console.log(">>>>>>>>开始初始化后台项目数据>>>>>>>>")
        this.InitSystemConfig()
    },
    methods: {
        // 初始化系统配置
        InitSystemConfig() {
            getSystemConfig().then((response) => {
                if (response.code == this.$ECode.SUCCESS) {
                    if (response.data) {
                        console.log(">>>>>>>>初始化系统配置成功>>>>>>>>")
                        this.$store.commit('setSystemConfig', response.data)
                    }
                }
            })
        },
        // 初始化数据字典
        InitDataDictList() {
            // 先查询出所有的数据字典
            const dictTypeList = [
                'sys_visit_auth',
                'sys_pay_type'
            ]
            getListByDictTypeList(dictTypeList).then((response) => {
                console.log("查询数据字典", response)
                if (response.code == this.$ECode.SUCCESS) {
                    const dictMap = response.data
                    this.visitAuthDictList = dictMap.sys_visit_auth.list
                    this.payTypeDictList = dictMap.sys_pay_type.list
                    if (dictMap.sys_visit_auth.defaultValue) {
                        this.visitAuthDefault = dictMap.sys_visit_auth.defaultValue
                    }
                    if (dictMap.sys_pay_type.defaultValue) {
                        this.payTypeDefaultValue = dictMap.sys_pay_type.defaultValue
                    }
                }
            })
        },
    }
}
</script>

<style scoped>

</style>
