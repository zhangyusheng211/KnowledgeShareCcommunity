import {
  SET_CHAT_MESSAGE_EVENT,
  SET_COMMENT_LIST,
  SET_COMMENT_REPORT_DICT,
  SET_CREATE_PROBLEM_MESSAGE,
  SET_CREATE_QUESTION_MESSAGE, SET_DOMAIN_EVENT_MESSAGE,
  SET_LOGIN_MESSAGE,
  SET_MOMENT_LIST,
  SET_MOMENT_TOPIC_LIST,
  SET_NOTICE_TYPE,
  SET_SIGN_IN_MESSAGE,
  SET_USER_TAG,
  SET_WEB_CONFIG_DATA,
  SET_TASK_FINISH,
  SET_PAY_SUCCESS,
  SET_PRAISE_MESSAGE,
} from "./mutation-types";

const app = {
  // 全局状态
  state: {
    // 评论列表
    commentList: [],
    // 动态列表
    momentList: [],
    // 用户标签字典 列表
    userTagDictList: [],
    // WebConfig网站配置
    webConfigData: {},
    // 登录消息，用于控制登录弹框
    loginMessage: "",
    // 创建问答消息，用于控制问答弹框
    createQuestionMessage: "",
    // 创建面经消息，用于控制发表面经弹出框
    createProblemMessage: "",
    // 通知类型
    noticeType: "",
    // 评论举报类型字典
    commentReportDict: {},
    // 话题列表
    momentTopicList: [],
    // 签到消息，用于签到刷新
    signInMessage: "",
    // 聊天消息事件，用于触发@操作、引用等操作
    chatMessageEvent: {},
    domainEventMessage: {}, // 领域事件消息
    taskFinishMessage: "", // 任务完成消息
    paySuccessMessage: "", // 支付成功消息
    praiseMessage: "", // 点赞消息

  },
  // getters是对数据的包装，例如对数据进行拼接，或者过滤
  getters: {
    //类似于计算属性
    // 增加的方法
  },
  // 如果我们需要更改store中的状态，一定要通过mutations来进行操作
  mutations: {

    // 传入自定义参数
    [SET_COMMENT_LIST](state, commentList) {
      state.commentList = commentList
    },

    // 设置动态列表
    [SET_MOMENT_LIST](state, momentList) {
      state.momentList = momentList
    },

    // 传入自定义参数
    [SET_USER_TAG](state, userTagDictList) {
      state.userTagDictList = userTagDictList
    },

    // 设置WebConfig
    [SET_WEB_CONFIG_DATA](state, webConfigData) {
      state.webConfigData = webConfigData
    },

    // 设置消息
    [SET_LOGIN_MESSAGE](state, loginMessage) {
      state.loginMessage = loginMessage
    },

    // 设置消息
    [SET_CREATE_QUESTION_MESSAGE](state, createQuestionMessage) {
      state.createQuestionMessage = createQuestionMessage
    },

    // 设置创建问题消息
    [SET_CREATE_PROBLEM_MESSAGE](state, createProblemMessage) {
      state.createProblemMessage = createProblemMessage
    },

    // 设置个人中心消息
    [SET_DOMAIN_EVENT_MESSAGE](state, domainEventMessage) {
      state.domainEventMessage = domainEventMessage
    },

    // 设置通知消息
    [SET_NOTICE_TYPE](state, noticeType) {
      state.noticeType = noticeType
    },

    [SET_COMMENT_REPORT_DICT](state, commentReportDict) {
      state.commentReportDict = commentReportDict
    },
    [SET_MOMENT_TOPIC_LIST](state, momentTopicList) {
      state.momentTopicList = momentTopicList
    },

    // 设置消息
    [SET_SIGN_IN_MESSAGE](state, signInMessage) {
      state.signInMessage = signInMessage
    },

    // 设置聊天消息事件
    [SET_CHAT_MESSAGE_EVENT](state, chatMessageEvent) {
      state.chatMessageEvent = chatMessageEvent
    },
    // 设置任务完成消息事件
    [SET_TASK_FINISH](state, taskFinishMessage) {
      state.taskFinishMessage = taskFinishMessage
    },
    // 设置支付完成消息事件
    [SET_PAY_SUCCESS](state, paySuccessMessage) {
      state.paySuccessMessage = paySuccessMessage
    },
    // 设置点赞消息
    [SET_PRAISE_MESSAGE](state, setPraiseMessage) {
      state.praiseMessage = setPraiseMessage
    },
  },

  // actions是我们定义的一些操作，正常情况下，我们很少会直接调用mutation方法来改变state
  actions: {}
}
export default app
