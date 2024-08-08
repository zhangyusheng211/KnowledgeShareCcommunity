import {getCookie, setCookie} from "../../utils/cookieUtils";
export default {
    namespaced: true,
    state: {
        ACCOUNT_TYPES:{
            1:'订阅号',
            2:'服务号'
        },
        accountList:[],
        selectedAppid:''
    },
    mutations: {
      updateAccountList (state, list) {
        state.accountList = list
        if(!list.length)return
        if(!state.selectedAppid){
          let appidCookie  = getCookie('appid')
          let selectedAppid = appidCookie?appidCookie:list[0].appid
          this.commit('wxAccount/selectAccount', selectedAppid)
        }
      },
      selectAccount (state, appid) {
        setCookie('appid', appid)
        let oldAppid = state.selectedAppid
        state.selectedAppid = appid
        if(oldAppid){
          //切换账号时刷新网页
          location.reload();
        }
      },
    }
  }
