/**
 * Vuex
 * http://vuex.vuejs.org/zh-cn/intro.html
 */

const now = new Date()
const chat = {
  // 全局状态
  state: {
    // 当前用户
    user: {
      name: '陌溪',
      img: 'http://picture.moguit.cn//blog/admin/jpg/2020/1/31/1580434623431.jpg'
    },
    // 会话列表
    sessions: [
      {
        id: 50,
        user: {
          name: '遇见',
          img: 'http://picture.moguit.cn//blog/admin/jpg/2021/11/29/1638175279455.jpg'
        },
        messages: [
          {
            content: 'hello，我是遇见',
            date: now
          }, {
            content: '快快加入蘑菇',
            date: now
          }
        ]
      },
      {
        id: 55,
        user: {
          name: '你钉钉响了',
          img: 'http://picture.moguit.cn//blog/admin/jpg/2021/12/28/1640683675251.jpg'
        },
        messages: []
      }
    ],
    // 当前选中的会话
    currentSessionId: 1,
    // 过滤出只包含这个key的会话
    filterKey: ''
  },
  mutations: {
    initData(state) {
      let data = localStorage.getItem('vue-chat-session')
      if (data) {
        state.sessions = JSON.parse(data)
      }
    },
    // 发送消息
    sendMessage({sessions, currentSessionId}, content) {
      let session = sessions.find(item => item.id === currentSessionId)
      session.messages.push({
        content: content,
        date: new Date(),
        self: true
      })
    },
    // 选择会话
    selectSession(state, id) {
      state.currentSessionId = id
    },
    // 搜索
    setFilterKey(state, value) {
      state.filterKey = value
    }
  },
  getters: {
    // 当前会话 session
    session: ({sessions, currentSessionId}) => sessions.find(session => session.id === currentSessionId),
    // 过滤后的会话列表
    filteredSessions: ({sessions, filterKey}) => {
      let result = sessions.filter(session => session.user.name.includes(filterKey))
      return result
    }
  }
}

// store.watch(
//     (state) => state.sessions,
//     (val) => {
//         console.log('CHANGE: ', val);
//         localStorage.setItem('vue-chat-session', JSON.stringify(val));
//     },
//     {
//         deep: true
//     }
// );

export default chat
