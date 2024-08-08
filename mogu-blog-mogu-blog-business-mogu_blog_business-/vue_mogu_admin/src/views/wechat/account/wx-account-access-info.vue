<template>
    <el-dialog title="开发接入信息" :close-on-click-modal="false" :visible.sync="visible">
        <div>
            <aside>
                接入公众号前，请先完成 <a target="_blank" href="http://www.moguit.cn/info/582">域名配置</a> <br>
            </aside>

            <div class="list-item"><span class="label">公众号:</span>{{account.name}}</div>
            <div class="list-item"><span class="label">token:</span>{{account.token}}</div>
            <div class="list-item"><span class="label">aesKey:</span>{{account.aesKey}}</div>
            <div class="list-item">
                <span class="label">接入链接:</span>
                <span v-html="accessUrl"></span>
            </div>

        </div>
    </el-dialog>
</template>

<script>
export default {
    data() {
        return {
            visible: false,
            account: {},
            WECHAT_API: process.env.WECHAT_API,
        }
    },
    computed: {
        accessUrl() {
            let host = location.host;
            if(/^(\d(.\d){3})|localhost/.test(host)){
                host='<span class="text-red">正式域名</span>'
                return location.protocol + '//' + host + '/wx/msg/' + this.account.appid
            }
            return this.WECHAT_API + '/wx/msg/' + this.account.appid
        }
    },
    methods: {
        init(item) {
            this.visible = true
            if (item && item.appid) {
                this.account = item
            }
        },
    }
}
</script>
<style scoped>
.list-item{
    line-height: 30px;
}
.label{
    width: 100px;
    display: inline-block;
    margin-right: 10px;
    font-weight: bold;
    text-align: right;
}
</style>

<style lang="scss">
aside {
    background: #eef1f6;
    padding: 8px 24px;
    margin-bottom: 20px;
    border-radius: 2px;
    display: block;
    line-height: 32px;
    font-size: 16px;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen, Ubuntu, Cantarell, "Fira Sans", "Droid Sans", "Helvetica Neue", sans-serif;
    color: #2c3e50;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;

    a {
        color: #337ab7;
        cursor: pointer;

        &:hover {
            color: rgb(32, 160, 255);
        }
    }
}
</style>
