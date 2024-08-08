<template>
  <main data-v-4b9f9746="" class="container main-container">
    <div data-v-19928490="" data-v-4b9f9746="" class="badge-wall">
      <div data-v-19928490="" data-v-4b9f9746="" class="badge-header">
        <div data-v-3f96a75a="" data-v-19928490="" class="badge-header" data-v-4b9f9746="">
          <div data-v-3f96a75a="" class="user-info">
            <div data-v-3f96a75a="" class="userAvatar-box">
              <img data-v-248050e4="" data-v-47508ed8="" data-v-3f96a75a="" :src="userInfo.photoUrl" alt="" class="lazy userAvatar userAvatar" loading="lazy">
            </div>
            <div data-v-3f96a75a="" class="user-name">
						<span data-v-3f96a75a="" class="badge-username">
							{{userInfo.nickName}}
						</span>
              <!---->

<!--              <span data-v-3f96a75a="" class="no-wear">-->
<!--							<svg data-v-3f96a75a="" width="12" height="12" viewBox="0 0 12 12" fill="none" xmlns="http://www.w3.org/2000/svg" class="no-wear-icon">-->
<!--								<path data-v-3f96a75a="" d="M5.15 0.837158C5.67598 0.533482 6.32402 0.533482 6.85 0.837158L10.0462 2.68246C10.5721 2.98613 10.8962 3.54735 10.8962 4.1547V7.8453C10.8962 8.45265 10.5721 9.01387 10.0462 9.31754L6.85 11.1628C6.32402 11.4665 5.67598 11.4665 5.15 11.1628L1.95385 9.31754C1.42787 9.01387 1.10385 8.45265 1.10385 7.8453V4.1547C1.10385 3.54735 1.42787 2.98613 1.95385 2.68246L5.15 0.837158Z" stroke="white" stroke-width="0.6"></path>-->
<!--								<path data-v-3f96a75a="" d="M5.52447 2.46352C5.67415 2.00287 6.32585 2.00287 6.47553 2.46353L7.01031 4.10942C7.07725 4.31543 7.26923 4.45492 7.48584 4.45492H9.21644C9.7008 4.45492 9.90219 5.07472 9.51033 5.35942L8.11025 6.37664C7.93501 6.50397 7.86168 6.72965 7.92861 6.93566L8.4634 8.58156C8.61307 9.04222 8.08583 9.42528 7.69398 9.14058L6.29389 8.12336C6.11865 7.99603 5.88135 7.99603 5.70611 8.12336L4.30602 9.14058C3.91417 9.42528 3.38693 9.04222 3.5366 8.58156L4.07139 6.93566C4.13832 6.72965 4.06499 6.50397 3.88975 6.37664L2.48967 5.35942C2.09781 5.07472 2.2992 4.45492 2.78356 4.45492H4.51416C4.73077 4.45492 4.92275 4.31543 4.98969 4.10942L5.52447 2.46352Z" fill="white"></path>-->
<!--							</svg>-->
<!--							<span data-v-3f96a75a="" class="no-wear-text">未佩戴</span>-->
<!--						</span>-->
            </div>
            <div data-v-3f96a75a="" class="obtain-count">
              已解锁 {{userInfo.userMedalCount}} 枚勋章
            </div>
          </div>
        </div>
      </div>

      <div data-v-19928490="" data-v-4b9f9746="" class="badge-list">
        <div data-v-41f9e5a5="" data-v-19928490="" class="badge-list-wrap" data-v-4b9f9746="">
          <ul data-v-41f9e5a5="" class="series">
            <li data-v-41f9e5a5="" class="serie" v-for="medalClassify in medalClassifyList" :key="medalClassify.uid">
              <div data-v-41f9e5a5="" class="serie-title">
                <img data-v-41f9e5a5="" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAGCAYAAACFIR03AAAACXBIWXMAACE4AAAhOAFFljFgAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAGnSURBVHgBbVNLdsIwDBwpOK+PRRflAt33Ej0al+yqGy7Arp9XIFZHloxDqR9BijSWRp8I9qbPwPzxhXLaouwK5QllEsyXCUUN81RRhHKhVNqE+CopgY1L4XutlMSYNl/3t1hGO2g3C7uFv1S/6zbedxu6TwYWoc+SfpeJLYo3yOEJhh3wSMsnn4fCK45cIDNuz4XPIsS7tJB+alcmQLVprCX8lTgWDLOBdV+/YxkvsvI+NRYlLCIs1mTP5WGsK7p/4cs7nH8723MPA5wmYirk5MQrcRv4DxMD3pCn7pwlidRgZo1IJBO9JQiVkeeGqERxXlQvGBlXIoIElHUTrXtO4HUHOR5p+RlB+wTOCpuDjWS3muRayJRJtQ4fO22KMQHLhBXXHkq10BV/jhPtdovqe6c76R4liwnswf9yBGy3fTvqMopx8r5wvj8crbUVWlZJp+y6P7RXxXUCvWcrsqZpq1hNDXfFOPEgK/jvtFW6xu0rZDPu4Jy/8YNuK+THV8hJO72mLwOrPqlk4xOQ2rq63nHf/WZV5Jqkni1efRpRh6P7MJrdRo2/AqrHkeBWUykAAAAASUVORK5CYII=" alt="" class="series-left">
                <div data-v-41f9e5a5="" class="title">{{medalClassify.name}}</div>
                <img data-v-41f9e5a5="" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAGCAYAAACFIR03AAAACXBIWXMAACE4AAAhOAFFljFgAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAGgSURBVHgBbVNBbtwwDBzSyuaQHgr0XLTn/qfvaf7T9yRA7rkmlyBA1rLFDEmtLS8iQCJFkWMOScvPf7aIoBpQAcyC0GfqVdIWdst72EM3btnfDu9GnI6pjXZFXZG28PdY2m3CrNSNetP8rq4pp4k2ypWy0LZU1HqDejqjfvuO+emZ3/pBfIKCgIdFk5u3y3a6tNixKKxdxfpSCf+MUaAhI1pLqa4rdWKRGFZNe+nxRSG2DnvpWO8EvYW9vKTfvWP15O2Yb1hkyMk2D6YvcngDSZgOIG3A7MmLv6v2uMa7Y5DQxOs04JWSLZdpyKmkfndHsFP6/n6D3D8ETpTbpcmRyZHanrJYt5J8APSKR+ewryNJP1avGKT1ikODxOa3+OZxg5zFUM7Ax8dOQs499hfwt0P0XMbiY+uA9cR0T9w2nyHVNphUdhjt1Y/FcjfJbnVWBt3jygB58qPGyOC2YOuRzamVV9j/P471xQxfkhoJ+Zg4kXblY5deDSPXNppJbCTHi1y64YTiZY0pyApfZeREzqy+lfyO/wP8ebFwhPAI+QRgPNl+sExIwgAAAABJRU5ErkJggg==" alt="" class="series-right">
              </div>
              <ul data-v-41f9e5a5="" class="badge-icon-list">
                <li v-for="medal in medalClassify.medalList" :key="medal.uid" data-v-41f9e5a5="" class="badge-icon-item">
                  <el-tooltip class="item" effect="dark" :content="timeFormat(medal.gainTime) + timeGainFormat(medal.gainProbability)"
                              placement="top-end">
                  <div data-v-41f9e5a5="" class="badge-item-icon">
                    <img style="cursor: pointer" :class="medal.gainTime ? '': 'gainImg'" data-v-41f9e5a5="" v-lazy="medal.fileUrl" alt="" class="not-obtain">
                  </div>
                  </el-tooltip>
                  <div data-v-41f9e5a5="" class="badge-desc">{{medal.name}}</div>
                  <div data-v-41f9e5a5="" class="obtain-date">
                    {{medal.summary}}
                  </div>
                </li>
              </ul>
            </li>

          </ul>
        </div>
      </div>
    </div>
  </main>
</template>

<script>
import {getUserMedalList} from "../api/medal";
import {timeAgo} from "../utils/webUtils";
import {getUserByUid} from "../api/about";
import {recorderBehavior} from "../api";
export default {
  name: "medalDetail",
  data() {
    return {
      medalClassifyList: [], // 勋章列表
      userUid: "",
      userInfo: {},
    }
  },
  created() {
    this.userUid = this.$route.params.userUid;
    this.getUserMedalListMethod()
    this.getUserInfo()
  },
  methods: {
    /**
     * dateTimeStamp是一个时间毫秒，注意时间戳是秒的形式，在这个毫秒的基础上除以1000，就是十位数的时间戳。13位数的都是时间毫秒。
     * @param dateTimeStamp
     * @returns {string}
     */
    timeAgo(dateTimeStamp) {
      return timeAgo(dateTimeStamp)
    },
    timeFormat(gainTime) {
      if (gainTime) {
        return this.timeAgo(gainTime) + '获得'
      } else {
        return '暂未获得'
      }
    },
    timeGainFormat(gainProbability) {
      if (gainProbability) {
          return '[约'+ gainProbability +'用户获取]'
      } else {
        return '[约0%用户获取]'
      }
    },
    getUserInfo() {
      let params = new URLSearchParams()
      params.append("userUid", this.userUid)
      getUserByUid(params).then(response=> {
        if(response.code == this.$ECode.SUCCESS) {
          this.userInfo = response.data
          console.log("获取到的用户信息", this.userInfo)
        } else {
          this.$message.error(response.message)
        }
      })
    },
    getUserMedalListMethod() {
      let params = {}
      params.userUid = this.userUid
      getUserMedalList(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.medalClassifyList = response.data
        }
        console.log(response)
      })
    },
  },
}
</script>

<style scoped>
.badge-wall-wrap .main-container {
  min-height: 100vh;
  background-color: #fff;
  border-radius: 2px;
}
.container {
  position: relative;
  margin: 0 auto;
  width: 100%;
  max-width: 960px;
  margin-top: 80px;
}

.badge-header {
  width: 100%;
  height: 196px;
  position: relative;
  background: url(//oos.moguit.cn/image/badge-header-2.png);
  background-size: cover;
  background-repeat: no-repeat;
}

.badge-header .user-info {
  width: 100%;
  position: relative;
  top: 40px;
}

.badge-header .user-info .userAvatar-box {
  margin: 0 auto;
  width: 64px;
  height: 64px;
}
.badge-header .user-info .userAvatar-box .userAvatar{
  cursor: pointer;
  width: 100%;
  height: 100%;
  border-radius: 50%;
}

.userAvatar {
  display: inline-block;
  position: relative;
  background-position: 50%;
  background-size: cover;
  background-repeat: no-repeat;
}

.badge-header .user-info .user-name {
  text-align: center;
  font-family: PingFang SC;
  font-style: normal;
  font-weight: 600;
  font-size: 18px;
  color: #fff;
  margin-top: 8px;
  height: 30px;
  line-height: 30px;
  margin-right: 4px;
}

.badge-header .user-info .user-name .badge-username {
  display: inline-block;
  vertical-align: middle;
  cursor: pointer;
}

.badge-header .user-info .user-name {
  text-align: center;
  font-family: PingFang SC;
  font-style: normal;
  font-weight: 600;
  font-size: 18px;
  color: #fff;
  margin-top: 8px;
  height: 30px;
  line-height: 30px;
  margin-right: 4px;
}

.badge-header .user-info .user-name .no-wear {
  box-sizing: border-box;
  display: inline-block;
  background: rgba(0,0,0,.2);
  border-radius: 10px;
  font-size: 12px;
  font-weight: 400;
  line-height: 20px;
  text-align: center;
  color: white;
  vertical-align: middle;
  padding: 0 10px;
}

.badge-header .user-info .obtain-count{
  height: 24px;
  line-height: 24px;
  text-align: center;
  font-style: normal;
  font-weight: 400;
  font-size: 14px;
  color: #e4e6eb;
}

.badge-wall-wrap .badge-wall .badge-list{
  padding: 0 26px;
  margin-bottom: 32px;
}

.serie .serie-title {
  height: 32px;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  margin-bottom: 24px;
  margin-top: 24px;
}

.serie .serie-title .title {
  font-family: PingFang SC;
  font-style: normal;
  font-weight: 600;
  font-size: 20px;
  color: #1d2129;
  margin: 0 16px;
}

.serie .serie-title .series-left .serie .serie-title .series-right {
  width: 16px;
  height: 2px;
}

.serie .badge-icon-list {
  display: grid;
  justify-content: space-between;
  grid-template-columns: repeat(auto-fill,150px);
  grid-gap: 24px 40px;
  flex-wrap: wrap;
}

.serie .badge-icon-list .badge-icon-item .badge-desc {
  margin-top: 4px;
  text-align: center;
  color: #1d2129;
  font-size: 16px;
  line-height: 28px;
}

.serie .badge-icon-list .badge-icon-item .obtain-date {
  font-size: 14px;
  text-align: center;
  color: #86909c;
  height: 24px;
  line-height: 24px;
}

.badge-item-icon img {
  width: 100%;
  height: 100%;
  border-style: none;
}

.gainImg {
  -webkit-filter: grayscale(100%);
  -moz-filter: grayscale(100%);
  -o-filter: grayscale(100%);
  filter: grayscale(100%);
}

@media only screen and (max-width: 649px) {
  .serie .badge-icon-list{
    grid-template-columns: repeat(auto-fill, 186px);
  }
}
</style>
