<template>

  <div>
    <div class="pagebg sh"></div>
    <div class="container">
      <h1 class="t_nav">
        <a href="/" class="n1">网站首页</a>
        <a href="/" class="n2">抽奖</a>
      </h1>

      <el-row type="flex" justify="center">
        <el-col :span="18" class="luckyContainer">
          <div style="text-align: center; ">
            <span style="font-weight: 600; font-size: 20px; color: #FFF">
              {{activityItem.title}}
            </span>
          </div>

          <LuckyWheel
            ref="LuckyWheel"
            width="500px"
            height="500px"
            style="margin: 0 auto; margin-top: 50px; display: inline-block; cursor: pointer"
            :prizes="prizes"
            :default-style="defaultStyle"
            :blocks="blocks"
            :buttons="buttons"
            @start="startCallBack"
            @end="endCallBack"
          />

          <span style="color: #FFF; position: absolute; right: 10px; user-select: none">当前积分：{{credits}} &nbsp;&nbsp; 每次抽奖消耗{{luckyRule.costCredits}}积分</span>
          <div class="awardProductList">
            <el-row style="width: 300px; margin-top: 20px;">
              <el-col :span="16" style="text-align: center">
                <div style="font-size: 16px; font-weight: 600">奖品名称</div>
                <div v-for="luckyAwardItem in activityItem.luckyAwardItemList" v-if="luckyAwardItem.awardProduct.awardType != '4'" style="font-size: 16px; line-height: 30px;">{{ getLuckyItemName(luckyAwardItem, luckyAwardItem.awardProduct)}}</div>
              </el-col>
              <el-col :span="8" style="text-align: center">
                <div style="font-size: 16px; font-weight: 600">奖品数量</div>
                <div v-for="luckyAwardItem in activityItem.luckyAwardItemList" v-if="luckyAwardItem.awardProduct.awardType != '4'" style="font-size: 16px; line-height: 30px;">{{luckyAwardItem.total}}</div>
              </el-col>
            </el-row>
          </div>
        </el-col>

        <el-col :span="6">
          <div class="luckyRecordList" style="width: 260px; min-height: 600px; padding: 10px">
            <div style="font-size: 16px; font-weight: 600; text-align: center;">最近中奖Top10</div>
            <div v-for="item in luckyRecordList" :key="item.uid">
              <div>
                <el-row style="line-height: 46px">
                    <el-col :span="4">
                      <li style="padding-right: 6px; cursor: pointer"  >
                        <el-avatar v-if="item.user"  fit="fill"   size="medium"  :src="item.user.photoUrl"@click.native="goUserCenter(item.user)"></el-avatar>
                        <el-avatar v-else fit="fill" size="small" src="https://empty">
                          <img :src="defaultAvatar"/>
                        </el-avatar>
                        </li>
                    </el-col>
                    <el-col :span="9">
                      <li  class="author" style="margin-top: -5px; margin-left: 2px; font-size: 14px; overflow: hidden; white-space: nowrap;" @click="goUserCenter(item.user)">
                        <span>{{item.user.nickName}}</span>
                      </li>
                    </el-col>
                    <el-col :span="11">
                      <li  class="author" style="margin-top: -5px; font-size: 14px;">
                        <span>获得 {{getLuckyItemName(item.luckyAwardItem, item.awardProduct)}}</span>
                      </li>
                    </el-col>
                </el-row>
              </div>

            </div>
          </div>

        </el-col>
      </el-row>

      <el-row class="sponsorList">
        <div class="luckyTitle">
          <span>赞助支持</span>
        </div>
        <span style="position: absolute; right: 10px; top: 10px; cursor: pointer; color: #007fff" @click="joinSponsor">活动赞助入口 ></span>
        <div v-if="sponsorList.length > 0" v-for="sponsorItem in sponsorList">
          <a :href="sponsorItem.url" target="_blank">
            <el-row class="sponsorItem">
              <el-col :span="2">
                <el-avatar  fit="fill"  :size="60"  :src="sponsorItem.photoUrl"></el-avatar>
              </el-col>
              <el-col :span="22">
                <div style="display: inline-block; line-height: 60px; font-size: 16px;">{{sponsorItem.content}}</div>
              </el-col>
            </el-row>
          </a>
        </div>
        <div v-else>
          <el-empty description="空空如也"></el-empty>
        </div>
      </el-row>


      <el-row class="myLuckyRecordListCss">
        <div class="luckyTitle">我的中奖记录</div>
        <span style="position: absolute; right: 10px; top: 10px; cursor: pointer;" @click="withdrawProduct">我要兑奖 ></span>
        <div>
          <el-row class="myLuckyRecordItem" v-for="item in myLuckyRecordList" :key="item.uid">
            <el-col :span="2">
              <el-avatar  fit="fill"  :size="60"  :src="item.awardProduct.photoUrl"></el-avatar>
            </el-col>
            <el-col :span="22">
              <div style="display: inline-block; line-height: 60px;">在 {{item.createTime}} 获得 {{getLuckyItemName(item.luckyAwardItem, item.awardProduct)}}</div>
            </el-col>
          </el-row>
          <!--分页-->
          <div class="block" style="text-align: center;">
            <el-pagination
              @current-change="handleCurrentChange"
              :current-page.sync="currentPage"
              :page-size="pageSize"
              layout="total, prev, pager, next, jumper"
              :total="total">
            </el-pagination>
          </div>
        </div>
      </el-row>

      <el-dialog
        title="活动赞助说明"
        :visible.sync="sponsorDialogVisible"
        width="30%"
        center>
        <div class="sponsorTitle">希望您可以提供:</div>
        <div>赞助商品类型不限，最好是程序员相关的电子产品、技术课程、会员码、折扣、云服务、现金红包等任意类型的合规产品。</div>
        <div class="sponsorTitle">我们可以提供给您:</div>
        <div>您的赞助信息会出现在最近一期活动赞助榜单，可以提供您的网站、Logo、店铺等链接，进行流量转换。往期的赞助抽奖活动信息也会一直保留。</div>
        <div class="sponsorTitle">联系方式: 【备注：活动赞助】</div>
        <div>
          <el-image style="width: 200px;" src="https://picture.moguit.cn//blog/admin/png/2023/5/28/1685284861994.png"></el-image>
        </div>

        <span slot="footer" class="dialog-footer">
          <el-button @click="sponsorDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="sponsorDialogVisible = false">确 定</el-button>
        </span>
      </el-dialog>

      <el-dialog
        title="兑奖说明"
        :visible.sync="withdrawDialogVisible"
        width="30%"
        center>
        <div class="sponsorTitle">现金红包兑换:</div>
        <div>若抽到红包奖励，可直接点击头像->个人中心，找到钱包进行提现即可。</div>
        <div class="sponsorTitle">虚拟物品兑换:</div>
        <div>抽取到积分和签到卡，将直接发放给用户账户，请关注站内信通知。</div>
        <div class="sponsorTitle">实物兑换:</div>
        <div>若抽取到实物，可通过微信扫描下方二维码【备注：抽奖兑换】，联系网站作者进行实物兑奖。</div>
        <div style="text-align: center;">
          <el-image style="width: 200px;" src="https://picture.moguit.cn//blog/admin/png/2023/5/28/1685284861994.png"></el-image>
        </div>

        <span slot="footer" class="dialog-footer">
          <el-button @click="withdrawDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="withdrawDialogVisible = false">确 定</el-button>
        </span>
      </el-dialog>

      <LuckyWindow :awardProduct="luckyAwardProduct"></LuckyWindow>

    </div>
  </div>


</template>

<script>
import { LuckyWheel } from '@lucky-canvas/vue'
import {getActivityList, lucky, getLuckyRecordList} from "../api/lucky";
import {mapMutations} from "vuex";
import {getCurrentUserCredits} from "../api/about";
import LuckyWindow from '@/components/LuckyWindow'

export default {
  components: {
    LuckyWheel,
    LuckyWindow,
  },
  data() {
    return {
      activityItem: {},
      luckyRule: {},
      sponsorList: [],
      prizes: [],
      defaultStyle: {
        fontColor: 'blue',
        fontSize: '14px'
      },
      blocks: [
        { padding: '13px', background: '#56a1e8' }
      ],
      showWindow: false,
      sponsorDialogVisible: false,
      withdrawDialogVisible: false,
      myLuckyRecordList: [],
      luckyRecordList: [],
      luckyAwardProduct: {},
      defaultAvatar: process.env.defaultAvatar, // 默认头像
      buttons: [
        // { radius: '50px', background: 'yellow' },
        // { radius: '45px', background: 'red' },
        // { radius: '41px', background: '#000', pointer: true },
        {
          radius: '45px',
          background: '#ffdea0',
          imgs: [
            {
              src:
                'https://oos.moguit.cn/image/pointer2.png',
              width: '100%',
              top: '-255%'
            }
          ],
          style: {
            cursor: 'pointer'
          }
        }
      ],
      BLOG_WEB_URL: process.env.VUE_MOGU_WEB,
      credits: 0,
      pageSize: 5,
      currentPage: 1,
      total: 0,
      defaultConfig: {
        offsetDegree: 30
      },
    }
  },
  watch: {
    // 判断用户信息
    '$store.state.user.isLogin': function (newFlag, oldFlag) {
      this.getMyLuckyRecordListMethod()
    },
  },
  mounted() {
    this.getActivityListMethod()
    this.getMyLuckyRecordListMethod()
    this.getUserCredits(false)
    // this.getActivityListMethod()
  },
  methods: {
    ...mapMutations(['setLoginMessage']),
    goUserCenter(user) {
      console.log("跳转用户详情页", this.BLOG_WEB_URL)
      window.open(this.BLOG_WEB_URL + '/userCenter?userUid=' + user.uid, '_blank');
    },
    //改变页码
    handleCurrentChange(val) {
      this.currentPage = val
      this.getMyLuckyRecordListMethod()
    },
    // 加入赞助
    joinSponsor() {
      this.sponsorDialogVisible = true
    },
    // 兑奖
    withdrawProduct() {
      this.withdrawDialogVisible= true
    },
    getUserCredits (refresh) {
      if (refresh) {
        let isLogin = this.$store.state.user.isLogin
        // 判断是否登录
        if (!isLogin) {
          this.isSignIn = false
          // 未登录，自动弹出登录框
          this.setLoginMessage(Math.random())
          return;
        }
      }
      let params = {}
      params.refresh = refresh
      getCurrentUserCredits(params).then(response => {
        if(response.code == this.$ECode.SUCCESS) {
          this.credits = response.data
        }
      })
    },
    getLuckyRecordListMethod() {
      // 获取最近的抽奖记录
      let params = {}
      params.luckyActivityUid = this.activityItem.uid
      params.pageSize = 10
      params.currentPage = 1
      params.luckyStatus = 1
      getLuckyRecordList(params).then(response => {
        this.luckyRecordList = response.data.records
      })
    },
    // 获取我的抽奖记录
    getMyLuckyRecordListMethod() {
      // 判断用户是否登录
      let isLogin = this.$store.state.user.isLogin
      if (!isLogin) {
        return;
      }
      // 获取最近的抽奖记录
      let params = {}
      params.luckyActivityUid = this.activityItem.uid
      params.pageSize = this.pageSize
      params.currentPage = this.currentPage
      params.userUid = this.$store.state.user.userInfo.uid
      params.luckyStatus = 1
      getLuckyRecordList(params).then(response => {
        console.log("查询我的中奖记录", response)
        this.myLuckyRecordList = response.data.records
        this.total = response.data.total;
        this.pageSize = response.data.size;
        this.currentPage = response.data.current;
      })
    },
    getActivityListMethod() {
      let params = {}
      params.pageSize = 1
      params.currentPage = 1
      getActivityList(params).then(resp => {
        if (resp.code == this.$ECode.SUCCESS) {
          let data = resp.data.records
          if (data.length == 0) {
            this.$message.error("后台暂未配置抽奖活动")
            return
          }
          let activityItem = data[0]
          if (activityItem.sponsorList) {
            this.sponsorList = JSON.parse(activityItem.sponsorList)
          }
          this.luckyRule = activityItem.luckyRule
          this.activityItem = activityItem
          this.getLuckyRecordListMethod()
          // 获取活动所有
          let luckyAwardItemList = activityItem.luckyAwardItemList
          let prizes = []
          luckyAwardItemList.forEach((item, index) => {
            let name = this.getLuckyItemName(item, item.awardProduct);
            prizes.push({
              uid: item.uid,
              title: name,
              openWindow: item.openWindow,
              awardType: item.awardProduct.awardType, // 奖励类型
              photoUrl: item.awardProduct.photoUrl,
              background: index % 2 ? '#ecf3fc' : '#b7dff8',
              fonts: [{ text: name, top: '10%' }],
              imgs: [
                {
                  src: item.awardProduct.photoUrl,
                  width: '30%',
                  top: '35%'
                }
              ]
            })
          })
          this.prizes = prizes
        }
      })
    },
    getLuckyItemName(luckyAwardItem, awardProduct) {
      let name = awardProduct.name
      if (luckyAwardItem.count > 1) {
        let awardType = awardProduct.awardType
        if (awardType != 5) {
          name = name + "X" + luckyAwardItem.count
        } else {
          name = name + " ¥" + luckyAwardItem.count / 100
        }
      }
      return name;
    },
    startCallBack() {
      // 判断用户是否登录
      let isLogin = this.$store.state.user.isLogin
      if (!isLogin) {
        this.$notify.error({
          title: '警告',
          message: '登录后才可以收藏哦~',
          offset: 100
        });
        // 未登录，自动弹出登录框
        this.setLoginMessage(Math.random())
        return;
      }

      let params = {}
      params.uid = this.activityItem.uid
      lucky(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          // 启动转盘抽奖
          this.$refs.LuckyWheel.play()
          console.log("抽奖结果", response)
          let luckyUid = response.data
          // 遍历奖励，获得奖项的下标
          let prizes = this.prizes
          let luckyIndex = -1
          console.log("遍历抽奖项", prizes)
          prizes.forEach((item, index) => {
            if (item.uid == luckyUid) {
              luckyIndex = index
              this.luckyAwardProduct = item
            }
          })
          if (luckyIndex < 0) {
            this.$message.error("抽奖服务异常，请联系管理员")
            return
          }
          setTimeout(() => {
            this.$refs.LuckyWheel.stop(luckyIndex)
            this.timeOutLoading()
          }, 2000)
        } else {
          this.$message.error(response.message)
        }
      })
    },
    timeOutLoading() {
      setTimeout(() => {
        this.getUserCredits(true)
        this.currentPage = 1
        this.getMyLuckyRecordListMethod()
        this.getLuckyRecordListMethod()
      }, 3500)
    },
    endCallBack(prize) {
      console.log("抽奖的奖励", prize)
      if (prize.awardType == "4") {
        this.$message.error(`很遗憾，你获得 ${prize.title}`)
      } else {
        this.$message.success(`恭喜你获得 ${prize.title}`)
      }
    }
  }
}
</script>

<style scoped>
  .luckyContainer {
    border-top-left-radius: 20px;
    border-bottom-left-radius: 20px;
  }
  .awardProductList{
    color: #FFF;
    display: inline-block;
    float: right;
    margin-top: 60px;
    border-radius: 20px;
    background-color: #333;
    backdrop-filter: blur(5px);
    border: 1px #FFF solid;
    user-select: none;
  }
  .luckyRecordList{
    color: #FFF;
    display: inline-block;
    float: right;
    border-top-right-radius: 20px;
    border-bottom-right-radius: 20px;
    background-color: #333;
    backdrop-filter: blur(5px);
    border: 1px #FFF solid;
    user-select: none;
  }

  .luckyContainer{
    padding: 10px;
    background-color: #333;
    backdrop-filter: blur(5px);
  }
  .luckyTitle {
    font-size: 18px;
    font-weight: 600;
    padding: 10px;
  }
  .myLuckyRecordListCss {
    color: #000;
    background-color: #fdfdfd;
    backdrop-filter: blur(5px);
    min-height: 300px;
    margin-top: 10px;
  }
  .sponsorList {
    margin-top: 10px;
    color: #000;
    background-color: #fdfdfd;
    backdrop-filter: blur(5px);
    min-height: 200px;
  }
  .sponsorItem {
    color: #000;
    cursor: pointer;
    padding: 5px;
    margin: 5px;
    border: 1px #cfd9e8 solid;
    border-radius: 20px;
  }
  .sponsorItem:hover {
    border: 1px #95bbf5 solid;
  }
  .myLuckyRecordItem{
    font-size: 14px;
    color: #000;
    padding: 5px;
    margin: 5px;
    border: 1px #cfd9e8 solid;
    border-radius: 20px;
  }
  .myLuckyRecordItem:hover {
    border: 1px #95bbf5 solid;
  }
  .sponsorTitle {
    font-size: 16px;
    font-weight: 600;
    margin-top: 10px;
    margin-bottom: 10px;
  }
</style>

