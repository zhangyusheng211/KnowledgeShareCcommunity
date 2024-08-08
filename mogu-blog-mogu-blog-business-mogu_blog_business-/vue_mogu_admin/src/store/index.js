import Vue from 'vue'
import Vuex from 'vuex'
import app from './modules/app'
import tagsView from './modules/tagsView'
import user from './modules/user'
import getters from './getters'
import networkDisk from "./modules/networkDisk";
import message from './modules/message'
import wxUserTags from './modules/wxUserTags'
import wxAccount from './modules/wxAccount'

Vue.use(Vuex)

const store = new Vuex.Store({
  modules: {
    app,
    tagsView,
    user,
    networkDisk,
    message,
    wxUserTags,
    wxAccount
  },
  getters
})

export default store
