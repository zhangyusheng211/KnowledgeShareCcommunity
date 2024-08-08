<script>
    import _ from 'lodash';
    import {mapGetters, mapMutations, mapState} from 'vuex';

    export default {
      computed: {
        ...mapState([
          // 'user',
          'filterKey',
        ]),
      },
      data() {
        return {
          user: this.$store.state.chat.user
        }
      },
      created() {
        console.log("加载user", this.loginUser)
      },
      methods: {
        ...mapMutations([
            'setFilterKey',
        ]),
        debounceOnKeyup: _.debounce(function (e) {
            this.setFilterKey(e.target.value);
        }, 150),
      }
    };
</script>

<template>
    <div class="card">
      <img class="userAvatar" width="40" height="40" :alt="user.name" :src="user.img">
      <p class="name">{{user.name}}</p>
      <input class="search" type="text" placeholder="search user..." @keyup="debounceOnKeyup">
    </div>
</template>

<style scoped lang="less">
    img {
      display: inline-block;
    }
    .card {
        padding: 12px;
        border-bottom: solid 1px #24272C;

        footer {
            margin-top: 10px;
        }

        .userAvatar, .name {
            vertical-align: middle;
        }
        .userAvatar {
            border-radius: 2px;
        }
        .name {
            display: inline-block;
            margin: 0 0 0 15px;
            font-size: 16px;
        }
        .search {
            padding: 0 10px;
            width: 100%;
            font-size: 12px;
            color: #fff;
            height: 30px;
            line-height: 30px;
            border: solid 1px #3a3a3a;
            border-radius: 4px;
            outline: none;
            background-color: #26292E;
        }
    }
</style>
