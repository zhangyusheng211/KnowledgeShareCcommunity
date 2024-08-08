<template>

  <div>
    <div class="pagebg sh"></div>
    <div class="container">
      <h1 class="t_nav">
        <a href="/" class="n1">网站首页</a>
        <a href="/" class="n2">赞助</a>
      </h1>

      <div class="goodsPrice">


        <div class="versionTitle">
          <div>系统版本选择</div>
          <div class="line"></div>
        </div>

        <div>
          <el-row type="flex" class="row-bg" justify="center">
            <el-col :xs="24" :span="6">
              <div class="wrap wrap1 pull-left">
                <div class="top">
                  <div class="iSfree">{{versionList.config.free.title}}</div>
                  <div class="infor line1">{{versionList.config.free.summary}}</div>
                  <div class="money">￥<span class="num">{{versionList.config.free.price}}</span></div>
                  <div class="bnt set_4_button1 raised hoverable qqcstao" @click="goGitee">
                    <div class="anim"></div>
                    <span>我要获取</span>
                  </div>
                </div>

                <ul>
                  <li v-for="(value, key)  in versionList['free']">
                    <div class="name line1">{{ key }}</div>
                    <span class="iconfont" v-if="value" style="color: #0E9A00; font-weight: bold; margin-top: -12px">&#xe664;</span>
                    <span class="iconfont" v-else
                          style="color: red; font-weight: bold; margin-top: -12px">&#xe8e7;</span>
                  </li>
                </ul>
              </div>
            </el-col>

            <el-col :xs="24" :span="6">
              <div class="wrap wrap2 pull-left ">
                <div class="top">
                  <div class="iSfree">{{versionList.config.study.title}}</div>
                  <div class="infor line1">{{versionList.config.study.summary}}</div>
                  <div class="money">￥<span class="num">{{versionList.config.study.price}}</span></div>
                  <div class="bnt set_4_button1 raised hoverable qqcstao" @click="getWechat('社区学习')">
                    <div class="anim"></div>
                    <span>我要获取</span>
                  </div>
                </div>
                <ul>
                  <li v-for="(value, key)  in versionList['study']">
                    <div class="name line1">{{ key }}</div>
                    <span class="iconfont" v-if="value" style="color: #0E9A00; font-weight: bold; margin-top: -12px">&#xe664;</span>
                    <span class="iconfont" v-else
                          style="color: red; font-weight: bold; margin-top: -12px">&#xe8e7;</span>
                  </li>
                </ul>
              </div>
            </el-col>

            <el-col :xs="24" :span="6">
              <div class="wrap wrap3 pull-left ">
                <div class="top">
                  <div class="iSfree">{{versionList.config.business.title}}</div>
                  <div class="infor line1">{{versionList.config.business.summary}}</div>
                  <div class="money">￥<span class="num">{{versionList.config.business.price}}</span></div>
                  <div class="bnt set_4_button1 raised hoverable qqcstao" @click="getWechat('社区商用')">
                    <div class="anim"></div>
                    <span>我要获取</span>
                  </div>
                </div>

                <ul>
                  <li v-for="(value, key)  in versionList['business']">
                    <div class="name line1">{{ key }}</div>
                    <span class="iconfont" v-if="value" style="color: #0E9A00; font-weight: bold; margin-top: -12px">&#xe664;</span>
                    <span class="iconfont" v-else
                          style="color: red; font-weight: bold; margin-top: -12px">&#xe8e7;</span>
                  </li>
                </ul>
              </div>
            </el-col>

            <el-col :xs="24" :span="6">
              <div class="wrap wrap4 pull-right">
                <div class="top">
                  <div class="iSfree">{{versionList.config.interview.title}}</div>
                  <div class="infor line1">{{versionList.config.interview.summary}}</div>
                  <div class="money">￥<span class="num">{{versionList.config.interview.price}}</span></div>
                  <div class="bnt set_4_button1 raised hoverable qqcstao" @click="getWechat('社区定制')">
                    <div class="anim"></div>
                    <span>我要获取</span>
                  </div>
                </div>

                <ul>
                  <li v-for="(value, key)  in versionList['interview']">
                    <div class="name line1">{{ key }}</div>
                    <span class="iconfont" v-if="value" style="color: #0E9A00; font-weight: bold; margin-top: -12px">&#xe664;</span>
                    <span class="iconfont" v-else
                          style="color: red; font-weight: bold; margin-top: -12px">&#xe8e7;</span>
                  </li>
                </ul>

              </div>
            </el-col>
          </el-row>
          <div class="clearfix"></div>
        </div>
      </div>
    </div>

    <el-dialog
      title="咨询购买"
      :visible.sync="dialogVisible"
      width="30%">
      <div style="text-align: center;">
        <el-image style="width: 300px;"
                  src="https://picture.moguit.cn//blog/admin/png/2023/5/28/1685284861994.png"></el-image>
        <div>请联系官方微信咨询购买，备注【{{ remark }}】</div>
      </div>


    </el-dialog>

  </div>


</template>

<script>
import {getVersionParams} from "../api";

export default {
  name: "version",
  data() {
    return {
      dialogVisible: false,
      remark: "社区购买",
      versionList: {}
    }
  },
  created() {
    this.getVersionParamsMethod()
  },
  methods: {
    getVersionParamsMethod() {
      getVersionParams().then(res => {
        if (res.code == this.$ECode.SUCCESS) {
          this.versionList = JSON.parse(res.data)
        }
          console.log("获取版本参数", JSON.parse(res.data))
      })
    },
    goGitee() {
      window.open(this.versionList.giteeUrl, '_blank');
    },
    // 获取微信
    getWechat(remark) {
      this.remark = remark
      this.dialogVisible = true
    },
  },
}
</script>

<style scoped>

.versionTitle {
  font-size: 32px;
  color: #282828;
  text-align: center;
  margin: 30px 0 30px 0;
}

.versionTitle .line {
  width: 40px;
  height: 4px;
  background-color: #007af6;
  margin: 20px auto 0 auto;
}

.goodsPrice {
  margin-bottom: 100px;
}

.goodsPrice .TiTle {
  margin: 50px 0 65px 0;
}

.goodsPrice .wrap {
  width: 280px;
}

.goodsPrice .wrap .top {
  width: 100%;
  height: 340px;
  background-color: #409eff;
  color: #FFFFFF;
  text-align: center;
  padding-top: 38px;
  position: relative;
}

.goodsPrice .wrap .top .label {
  position: absolute;
  right: 20px;
  top: 0;
  width: 42px;
  height: 48px;
}

.goodsPrice .wrap.wrap1 .top {
  color: #FFFFFF;
}

.goodsPrice .wrap.wrap2 .top {
  background-color: #eb2a2a;
}

.goodsPrice .wrap.wrap3 .top {
  background-color: #333333;
}

.goodsPrice .wrap.wrap4 .top {
  background-color: #ff881c;
}

.goodsPrice .wrap .top .iSfree {
  font-size: 32px;
}

.goodsPrice .wrap .top .infor {
  font-size: 16px;
  margin-top: 15px;
}

.goodsPrice .wrap.wrap1 .top .infor {
  color: #FFFFFF;
}

.goodsPrice .wrap .top .money {
  font-size: 36px;
  /*font-weight: lighter;*/
  margin-top: 10px;
}

.goodsPrice .wrap .top .money .num {
  font-size: 60px;
}

.goodsPrice .wrap.wrap3 .top .money .iconfont {
  color: #eb2a2a;
  font-size: 50px;
}

.goodsPrice .wrap .top .bnt {
  font-size: 18px;
  color: #000000;
  width: 180px;
  height: 48px;
  background-color: #FFFFFF;
  margin: 22px auto 0 auto;
  text-align: center;
  line-height: 48px;
  cursor: pointer;
  border-radius: 1px;
}

.goodsPrice .wrap.wrap2 .top .bnt {
  background-color: #FFFFFF;
}

.goodsPrice .wrap.wrap3 .top .bnt {
  background-color: #fff;
  color: #333333;
}

.goodsPrice .wrap.wrap4 .top .bnt {
  background-color: #fff;
  color: #000000;
}

.goodsPrice .wrap ul {
  padding: 25px;
  border: 1px solid #409eff;
  /*height: 444px;*/
}

.goodsPrice .wrap3 ul {
  padding: 25px;
  border: 1px solid #33393d;
  /*height: 444px;*/
}


.goodsPrice .wrap.wrap2 ul {
  border-color: #eb2a2a;
}

.goodsPrice .wrap.wrap4 ul {
  border-color: #eb8c36;
}

.goodsPrice .wrap ul li {
  margin-bottom: 16px;
  position: relative;
}

.goodsPrice .wrap ul li .name {
  font-size: 16px;
  color: #282828;
  width: 210px;
}

.goodsPrice .wrap ul li .iconfont {
  /*color: #d81e06;*/
  position: absolute;
  right: 0;
  top: 50%;
  font-size: 17px;
  margin-top: -8.5px;
}

.goodsPrice .wrap ul li .iconfont.icondui {
  /*color: #17b356;*/
  right: -4px;
  font-size: 15px;
}

.goodsPrice .pull-left {
  margin-right: 24px;
}
</style>
