import Vue from 'vue'
import Vuex from 'vuex'
import app from './app'
import user from './user'
import chat from './chat'
import videoPreview from './videoPreview'

// 让vuex生效
Vue.use(Vuex)

const store = new Vuex.Store({

  // 将app和user放在store中
  modules: {
    videoPreview,
    app,
    user,
    chat
  }
})

store.watch(
  (state) => state.sessions,
  (val) => {
    localStorage.setItem('vue-chat-session', JSON.stringify(val))
  },
  {
    deep: true
  }
)

export default store
