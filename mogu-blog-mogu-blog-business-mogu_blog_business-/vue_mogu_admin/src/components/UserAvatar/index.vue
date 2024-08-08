<template>
    <span v-if="user"  style="cursor: pointer; display: flex; align-items: center; justify-content: center;" @click="goUserCenter(user)">
            <el-avatar  size="small" v-if="user && user.photoUrl" :src="user.photoUrl"></el-avatar>
            <span style="font-size: 14px; margin-left: 2px;">{{user.nickName}}</span>
    </span>
</template>

<script>
export default {
    name: "index",
    props: ['user'],
    data() {
        return {
            BLOG_WEB_URL: process.env.BLOG_WEB_URL,
        }
    },
    methods: {
        goUserCenter(user) {
            window.open( this.BLOG_WEB_URL + '/userCenter?userUid=' + user.uid, '_blank');
        },
    }
}
</script>

<style scoped>

</style>
