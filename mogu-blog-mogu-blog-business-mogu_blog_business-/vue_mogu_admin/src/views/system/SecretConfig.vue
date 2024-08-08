<template>
    <div>
        <secretConfig secret-type="10"></secretConfig>
    </div>
</template>

<script>
import secretConfig from "./secretConfig/secretConfig.vue";
export default {
    name: "thirdSecretConfig",
    data() {
        return {

        }
    },
    components: {
        secretConfig,
    },
    methods: {
        linkList: function () {

        }
    }
}
</script>

<style scoped>

</style>
