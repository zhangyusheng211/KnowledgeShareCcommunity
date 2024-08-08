<template>
  <div>
    <article>
      <div style="min-height: 1000px">
        <div style="background: no-repeat; background-size: 100% 100%"
             :style="{background: 'url('+ userInfo.backgroundFileUrl +')' }">
          <el-row :gutter="24" type="flex" justify="center"
                  style="background: linear-gradient(90deg, rgba(220,233,242,0.5),hsla(0,0%, 100%,.5),rgba(220,233,242,0.4)); min-height: 45px">
            <el-col @click.native="goToUserCenter('1')" :span="6" style="text-align: center; cursor: pointer">
              <div class="countStyle">
                <countTo :startVal='0' :endVal='userCenterInfo.blogCount' :duration='3000'></countTo>
              </div>
              <div class="countStyle2">文章</div>
            </el-col>

            <el-col @click.native="goToUserCenter('3')" :span="6" style="text-align: center; cursor: pointer">
              <el-row>
                <div class="countStyle">
                  <countTo :startVal='0' :endVal='userCenterInfo.questionCount' :duration='3000'></countTo>
                </div>
                <div class="countStyle2">问答</div>
              </el-row>
            </el-col>

            <el-col @click.native="goToUserCenter('7')" :span="6" style="text-align: center; cursor: pointer">
              <el-row>
                <div class="countStyle">
                  <countTo :startVal='0' :endVal='userCenterInfo.followCount' :duration='3000'></countTo>
                </div>
                <div class="countStyle2">粉丝</div>
              </el-row>
            </el-col>

            <el-col @click.native="goToUserCenter('8')" :span="6" style="text-align: center; cursor: pointer">
              <el-row>
                <div class="countStyle">
                  <countTo :startVal='0' :endVal='userCenterInfo.watchCount' :duration='3000'></countTo>
                </div>
                <div class="countStyle2">关注</div>
              </el-row>
            </el-col>

            <!-- 动态 -->
            <el-col @click.native="goToUserCenter('9')" :span="6" style="text-align: center; cursor: pointer">
              <el-row>
                <div class="countStyle">
                  <countTo :startVal='0' :endVal='userCenterInfo.momentCount' :duration='3000'></countTo>
                </div>
                <div class="countStyle2">动态</div>
              </el-row>
            </el-col>

            <!-- 问题 -->
            <el-col @click.native="goToUserCenter('10')" :span="6" style="text-align: center; cursor: pointer">
              <el-row>
                <div class="countStyle">
                  <countTo :startVal='0' :endVal='userInfo.userMedalCount' :duration='3000'></countTo>
                </div>
                <div class="countStyle2">勋章</div>
              </el-row>
            </el-col>
          </el-row>

          <el-row :gutter="24" class="userInfo">
            <el-col style="text-align: center; margin-top: 10px">
              <span :class="userInfo.userTag > 0 ?'vip-avatar':''">
                <el-avatar :class="userInfo.userTag > 0 ?'vip-color':''" :size="80" v-if="userInfo.photoUrl"
                           :src="userInfo.photoUrl" @error="errorHandler">
                  <img :src="defaultAvatar"/>
                </el-avatar>
                <span style=" right: 3px; bottom: -1px;" class="vip-text pointer" v-if="userInfo.userTag > 0">v</span>
              </span>

              <div class="nickname fontColorSytle">{{ userInfo.nickName }}</div>
              <div class="gender fontColorSytle">

                <span v-if="userInfo.occupation"> {{ userInfo.occupation }}</span>

                <span>
                  <el-tag size="mini" effect="plain"
                          style="margin-left:5px; background-color: rgba(255, 255, 255, 0.9);"
                          v-for="userTag in userTagDictList" :key="userTag.uid"
                          v-if="userInfo.userTag == userTag.dictValue && userInfo.userTag != 0"
                          :type="userTag.listClass">{{ userTag.dictLabel }}</el-tag>
                </span>

                <span>
<!--                   <span class="lv" style="background-color: rgba(255, 255, 255, 0.9);" :class="'lv'+ userInfo.userLevel" v-for="userLevel in userLevelDictList" :key="userLevel.uid" v-if="userInfo.userLevel == userLevel.dictValue">-->
                  <!--                      Lv{{userInfo.userLevel}}-->
                  <!--                   </span>-->
                  <span>
                    <img v-if="userLevelImage" :src="userLevelImage"
                         style="position: absolute; height: 26px; margin-left: 80px; display: inline-block;">
                  </span>

                   <span class="lv" style="background-color: rgba(255, 255, 255, 0.9);"
                         :class="'lv'+ userInfo.userLevel" v-for="userLevel in userLevelDictList" :key="userLevel.uid"
                         v-if="userInfo.userLevel == userLevel.dictValue">
                      {{ userLevel.remark }}
                   </span>
                </span>

                <span class="iconfont" v-if="userInfo.gender=='1'"
                      style="margin-left: 1px; color: dodgerblue;  font-size: 16px;">&#xe641;</span>
                <span class="iconfont" v-if="userInfo.gender=='2'"
                      style="margin-left: 1px; color: palevioletred;  font-size: 16px;">&#xe643;</span>
              </div>
              <div class="summary fontColorSytle" v-if="userInfo.summary">{{ userInfo.summary }}</div>
              <div class="summary fontColorSytle" v-else>这家伙很懒，什么都没有留下</div>

              <el-row style="margin-top: 5px; height: 60px;">
                <span v-if="userInfo.userMedalCount > 0">
                  <span  v-if="index < 10" v-for="(medal, index) in userInfo.userMedalList" :key="medal.uid" style="line-height: 64px;">
                    <el-tooltip class="item" effect="light"
                                :content="medal.summary + '(' + timeFormat(medal.gainTime) + ')'"
                                placement="top">
                      <img @click="goMedalDetail" :src="medal.fileUrl" class="like-user-avatar">
                    </el-tooltip>
                  </span>
                </span>
              </el-row>

              <div class="love" style="position: absolute; bottom: 10px; right: 20px;">
                <div v-if="loginUserInfo.uid != userUid" >
                  <el-button round size="mini" type="danger" v-if="!userInfo.isWatchUser == true" icon="el-icon-view"
                             @click="watchUser()">关注
                  </el-button>
                  <el-button round size="mini" type="info" v-else icon="el-icon-view" @click="unWatchUser()">取消关注</el-button>
                  <el-button round size="mini" type="primary" icon="el-icon-chat-dot-round" @click="sendMessage()">私信</el-button>
                </div>

                <el-tooltip class="item" effect="light" content="更换背景" placement="top-end" v-if="loginUserInfo.uid == userUid">
                  <el-button @click="changeBackground" type="info" icon="el-icon-edit-outline" circle size="mini"></el-button>
                </el-tooltip>

              </div>
            </el-col>

          </el-row>


        </div>

        <el-tabs class="userEvent" v-model="activeName" @tab-click="handleClick" style="margin-top: 5px">
          <el-tab-pane name="1" label="文章">
            <span :class="activeName==1?'tab-pane-active':'tab-pane'" slot="label"><i
              class="el-icon-collection-tag"></i> <span>文章</span></span>

            <el-tabs class="userEvent2" v-model="activeName2" @tab-click="handleClick2">
              <el-tab-pane label="最新文章" name="1"></el-tab-pane>
              <el-tab-pane label="最热文章" name="2"></el-tab-pane>
            </el-tabs>

            <div style="min-height: 773px">
              <div
                v-for="item in listData"
                :key="item.uid"
                class="blogs"
                data-scroll-reveal="enter bottom over 1s"
              >
                <el-row :span="24" class="questionLine">
                  <el-col>
                    <div class="blogtitle">

                      <el-row :gutter="24">
                        <el-col :xs="8" :lg="3">
                          <span class="blogpic">
                            <a :href=" VUE_MOGU_WEB + '/info/'+item.oid" target="_blank">
                              <img v-if="item.photoList && item.photoList.length > 0 " v-lazy="item.photoList[0]"
                                   :key="item.photoList[0]" alt>
                            </a>
                          </span>
                        </el-col>

                        <el-col :xs="16" :lg="12">
                          <a :href=" VUE_MOGU_WEB + '/info/'+item.oid" target="_blank">{{ item.title }}</a>
                        </el-col>

                        <span v-for="(blogTag, index) in item.tagList" style="float: right; margin-right: 10px">
                          <el-tag style="margin-right: 3px" v-if="index%3==0"
                                  type="primary">{{ blogTag.content }}</el-tag>
                          <el-tag style="margin-right: 3px" v-if="index%3==1"
                                  type="danger">{{ blogTag.content }}</el-tag>
                          <el-tag style="margin-right: 3px" v-if="index%3==2" type="info">{{ blogTag.content }}</el-tag>
                        </span>
                      </el-row>

                    </div>

                    <div class="bloginfo">
                      <ul>
                        <li class="author">
                          <span class="iconfont">&#xe60f;</span>
                          <a href="javascript:void(0);" @click="getUserCenter(item, 'blog')">{{ item.author }}</a>
                        </li>

                        <li class="lmname" v-if="item.blogSort">
                          <span class="iconfont">&#xe603;</span>
                          <a
                            href="javascript:void(0);"
                            @click="goToSortList(item.blogSort.uid)"
                          >{{ item.blogSort.sortName }}</a>
                        </li>

                        <li class="view">
                          <span class="iconfont">&#xe8c7;</span>
                          <span>{{ item.clickCount }}</span>
                        </li>

                        <li class="createTime">
                          <span class="iconfont">&#xe606;</span>
                          {{ item.createTime }}
                        </li>

                      </ul>
                    </div>
                  </el-col>
                </el-row>

              </div>

              <div class="isEnd">
                <div class="loadContent" @click="loadContent(1)" v-if="!isEnd&&!loading">查看更多</div>
                <div class="lds-css ng-scope" v-if="!isEnd&&loading">
                  <div style="width:100%;height:100%" class="lds-facebook">
                    <div></div>
                    <div></div>
                    <div></div>
                  </div>
                </div>

                <span v-if="isEnd && listData.length == 0">
                  <el-empty description="空空如也" image=""></el-empty>
                </span>
                <span v-if="isEnd && listData.length > 0">我也是有底线的~</span>
              </div>
            </div>

          </el-tab-pane>
          <el-tab-pane name="2" label="问答">
            <span :class="activeName==2?'tab-pane-active':'tab-pane'" slot="label"><i
              class="el-icon-tickets"></i> <span>问答</span></span>

            <el-tabs class="userEvent2" v-model="activeName2" @tab-click="handleClick2">
              <el-tab-pane label="最新问答" name="1"></el-tab-pane>
              <el-tab-pane label="最热问答" name="2"></el-tab-pane>
            </el-tabs>

            <div style="min-height: 773px">
              <div
                v-for="item in listData"
                :key="item.uid"
                class="blogs"
                v-if="item.user && item.isPublish == 1"
                data-scroll-reveal="enter bottom over 1s"
              >
                <el-row :span="24" class="questionLine">
                  <el-col>
                    <div class="blogtitle">
                      <a :href=" VUE_MOGU_WEB + '/qInfo/'+item.oid" target="_blank">{{ item.title }}</a>
                      <span v-for="(questionTag, index) in item.questionTagList"
                            style="float: right; margin-right: 10px">
                        <el-tag style="margin-right: 3px" v-if="index%3==0"
                                type="primary">{{ questionTag.name }}</el-tag>
                        <el-tag style="margin-right: 3px" v-if="index%3==1"
                                type="danger">{{ questionTag.name }}</el-tag>
                        <el-tag style="margin-right: 3px" v-if="index%3==2" type="info">{{ questionTag.name }}</el-tag>
                      </span>
                    </div>

                    <div class="bloginfo">
                      <ul>
                        <li style=" padding-right: 6px">
                          <el-avatar size="small" v-if="item.user.photoUrl" :src="item.user.photoUrl"></el-avatar>
                          <el-avatar size="small" v-else
                                     src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"></el-avatar>
                        </li>
                        <li class="author">
                          <a href="javascript:void(0);" v-if="item.user"
                             @click="getUserCenter(item, 'question')">{{ item.user.nickName }}</a>
                        </li>

                        <li class="lmname" v-if="item.blogSort">
                          <span class="iconfont">&#xe603;</span>
                          <a
                            href="javascript:void(0);"
                            @click="goToSortList(item.blogSort.uid)"
                          >{{ item.blogSort.sortName }}</a>
                        </li>

                        <li class="view">
                          <span class="iconfont">&#xe8c7;</span>
                          <span>{{ item.clickCount }}</span>
                        </li>

                        <li class="createTime">
                          <span class="iconfont">&#xe606;</span>
                          {{ item.createTime }}
                        </li>
                      </ul>
                    </div>
                  </el-col>
                </el-row>
              </div>

              <div class="isEnd">
                <div class="loadContent" @click="loadContent(2)" v-if="!isEnd&&!loading">查看更多</div>
                <div class="lds-css ng-scope" v-if="!isEnd&&loading">
                  <div style="width:100%;height:100%" class="lds-facebook">
                    <div></div>
                    <div></div>
                    <div></div>
                  </div>
                </div>
                <span v-if="isEnd && listData.length == 0">
                  <el-empty description="空空如也" image=""></el-empty>
                </span>
                <span v-if="isEnd && listData.length > 0">我也是有底线的~</span>
              </div>

            </div>
          </el-tab-pane>

          <!--          <el-tab-pane name="3" label="收藏">-->
          <!--            <span :class="activeName==3?'tab-pane-active':'tab-pane'" slot="label"><i class="el-icon-chat-dot-square"></i> <span>收藏</span></span>-->
          <!--          </el-tab-pane>-->

          <el-tab-pane name="4" label="关注">
            <span :class="activeName==4?'tab-pane-active':'tab-pane'" slot="label"><i
              class="el-icon-chat-dot-square"></i> <span>关注</span></span>
            <el-tabs class="userEvent2" v-model="activeName2" @tab-click="handleClick2">
              <el-tab-pane label="TA的粉丝" name="1"></el-tab-pane>
              <el-tab-pane label="TA关注的人" name="2"></el-tab-pane>
            </el-tabs>

            <div style="min-height: 773px">

              <el-row :gutter="24" v-for="(item, index) in userWatchListData" :key="item.uid" v-if="activeName2 == 1">
                <el-card>
                  <el-col :span="1">{{ index + 1 }}</el-col>
                  <el-col :span="1.5">
                    <el-avatar style="cursor: pointer;" @click.native="getUserCenter(item, 'watch')" size="small"
                               v-if="item.user && item.user.photoUrl" :src="item.user.photoUrl"></el-avatar>
                    <el-avatar style="cursor: pointer;" @click.native="getUserCenter(item, 'watch')" size="small" v-else
                               src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"></el-avatar>
                  </el-col>
                  <el-col :span="16">
                    <span style="cursor: pointer;" @click="getUserCenter(item, 'watch')">
                      {{ item.user.nickName }}
                    </span>
                  </el-col>
                  <el-col :span="3" style="line-height: 50px; margin-top: -12px; float: right;"
                          v-if="item.user && item.user.uid != loginUserInfo.uid">
                    <el-button v-if="item.watchStatus == 0" icon="el-icon-sugar" type="danger"
                               @click="watchOtherUser(item)"><span>&nbsp;关&nbsp;注&nbsp;</span></el-button>
                    <el-button v-if="item.watchStatus == 1" icon="el-icon-lightning" type="info"
                               @click="unWatchOtherUser(item.user.uid)">取消关注
                    </el-button>
                    <el-button v-if="item.watchStatus == 2" icon="el-icon-ship" type="success"
                               @click="unWatchOtherUser(item.user.uid)">互相关注
                    </el-button>
                  </el-col>
                </el-card>
              </el-row>

              <el-row :gutter="24" v-for="(item, index) in userWatchListData" :key="item.uid" v-if="activeName2 == 2">
                <el-card v-if="item.isAdmin=='1'">
                  <el-col :span="1">{{ index + 1 }}</el-col>
                  <el-col :span="1.5">
                    <el-avatar style="cursor: pointer;" @click.native="getUserCenter(item, 'watch')" size="small"
                               v-if="item.admin.photoUrl" :src="item.admin.photoUrl"></el-avatar>
                    <el-avatar style="cursor: pointer;" @click.native="getUserCenter(item, 'watch')" size="small" v-else
                               src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"></el-avatar>
                  </el-col>
                  <el-col :span="16">
                    <span style="cursor: pointer;" @click="getUserCenter(item, 'watch')">
                      {{ item.admin.nickName }}
                    </span>
                  </el-col>
                  <el-col :span="3" style="line-height: 50px; margin-top: -12px; float: right;"
                          v-if="item.admin && item.admin.uid != loginUserInfo.uid">
                    <el-button v-if="item.watchStatus == 0" icon="el-icon-sugar" type="danger"
                               @click="watchOtherUser(item)"><span> 关 注 </span></el-button>
                    <el-button v-if="item.watchStatus == 1" icon="el-icon-lightning" type="info"
                               @click="unWatchOtherUser(item.admin.uid)">取消关注
                    </el-button>
                    <el-button v-if="item.watchStatus == 2" icon="el-icon-ship" type="success"
                               @click="unWatchOtherUser(item.admin.uid)">互相关注
                    </el-button>
                  </el-col>
                </el-card>

                <el-card v-if="item.isAdmin=='0'">
                  <el-col :span="1">{{ index + 1 }}</el-col>
                  <el-col :span="1.5">
                    <el-avatar style="cursor: pointer;" @click.native="getUserCenter(item, 'watch')" size="small"
                               v-if="item.user && item.user.photoUrl" :src="item.user.photoUrl"></el-avatar>
                    <el-avatar style="cursor: pointer;" @click.native="getUserCenter(item, 'watch')" size="small" v-else
                               src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"></el-avatar>
                  </el-col>
                  <el-col :span="16">
                    <span v-if="item.user && item.user.nickName" style="cursor: pointer;"
                          @click="getUserCenter(item, 'watch')">
                      {{ item.user.nickName }}
                    </span>
                  </el-col>
                  <el-col :span="3" style="line-height: 50px; margin-top: -12px; float: right;"
                          v-if="item.user && item.user.uid != loginUserInfo.uid">
                    <el-button v-if="item.watchStatus == 0" icon="el-icon-sugar" type="danger"
                               @click="watchOtherUser(item)"><span> 关 注 </span></el-button>
                    <el-button v-if="item.watchStatus == 1" icon="el-icon-lightning" type="info"
                               @click="unWatchOtherUser(item.user.uid)">取消关注
                    </el-button>
                    <el-button v-if="item.watchStatus == 2" icon="el-icon-ship" type="success"
                               @click="unWatchOtherUser(item.user.uid)">互相关注
                    </el-button>
                  </el-col>

                </el-card>
              </el-row>

              <div class="isEnd" style="margin-top: 5px;">
                <div class="loadContent" @click="loadContent(3)" v-if="!isEnd&&!loading">查看更多</div>
                <div class="lds-css ng-scope" v-if="!isEnd&&loading">
                  <div style="width:100%;height:100%;" class="lds-facebook">
                    <div></div>
                    <div></div>
                    <div></div>
                  </div>
                </div>
                <span v-if="isEnd && userWatchListData.length == 0">
                  <el-empty description="空空如也" image=""></el-empty>
                </span>
                <span v-if="isEnd && userWatchListData.length > 0">我也是有底线的~</span>
              </div>
            </div>
          </el-tab-pane>


          <!-- 动态  -->
          <el-tab-pane name="5" label="动态">
            <span :class="activeName==5?'tab-pane-active':'tab-pane'" slot="label"><i
              class="el-icon-notebook-1"></i> <span>动态</span></span>

            <div style="min-height: 773px">
              <el-card shadow="hover" v-for="userMoment in momentList" :key="userMoment.uid" class="commentList">
                  <span class="left p1" :class="userMoment.user.userTag > 0 ?'vip-avatar':''">
                    <img :class="userMoment.user.userTag > 0 ?'vip-color':''" v-if="userMoment.user"
                         style="cursor:pointer;" @click="getUserCenter(userMoment,'moment')"
                         :src="userMoment.user.photoUrl ? userMoment.user.photoUrl:defaultAvatar"
                         onerror="onerror=null;src=defaultAvatar"/>
                    <img v-else :src="defaultAvatar"/>
                    <span style="font-size: 10px; right: 1px; bottom: -3px;" class="vip-text pointer"
                          v-if="userMoment.user.userTag > 0"
                          @click.natice="getUserCenter(userMoment.user,'moment')">v</span>
                  </span>

                <div class="right p1">
                  <div class="rightTop" v-if="userMoment.user">
                    <el-link class="userName" :underline="false" v-if="userMoment.user.nickName"
                             @click="getUserCenter(userMoment,'moment')">{{ userMoment.user.nickName }}
                    </el-link>
                    <el-tag size="mini" effect="plain" style="height: 20px; margin-left:5px;"
                            v-for="userTag in userTagDictList" :key="userTag.uid"
                            v-if="userMoment.user.userTag == userTag.dictValue && userMoment.user.userTag != 0"
                            :type="userTag.listClass">{{ userTag.dictLabel }}
                    </el-tag>
                    <span class="iconfont" v-if="userMoment.user.gender=='1'"
                          style="margin-left: 5px; color: dodgerblue;  font-size: 18px; font-weight: bold;">&#xe641;</span>
                    <span class="iconfont" v-if="userMoment.user.gender=='2'"
                          style="margin-left: 5px; color: palevioletred;  font-size: 18px; font-weight: bold;">&#xe643;</span>
                    <span class="timeAgo" style="color: #8a919f;"
                          v-if="userMoment.user.occupation">{{ userMoment.user.occupation }}</span>
                    <span class="timeAgo" v-if="userMoment.createTime">{{ timeAgo(userMoment.createTime) }}</span>

                    <span class="timeAgo" v-else>刚刚</span>

                    <el-link class="b1" :underline="false" style="float: right">
                      <Collection style="margin-top: -1px;" class="b1" :bizUid="userMoment.uid" :collectType="'4'"
                                  :collectCountValue="userMoment.collectInfo.collectCount"
                                  :isCollectValue="userMoment.collectInfo.isCollect"
                      ></Collection>
                    </el-link>

                    <el-link class="b1" :underline="false" style="float: right; margin-right: 10px;"
                             @click="report(userMoment)"><span class="iconfont">&#xe65b;</span>举报
                    </el-link>

                  </div>

                  <div class="rightCenter ck-content" v-highlight
                       @click="imageChange"
                       v-html="$xss(replaceImg(userMoment.content), options)"></div>


                  <div v-if="userMoment.url">
                    <div class="link-container">
                      <div class="zone-link-part">
                        <span class="el-icon-link zone-link-bg"></span>
                        <div class="link-right-part"><span class="title">{{ userMoment.urlInfo }}</span>
                          <a :href="userMoment.url" target="_blank" class="url">{{ userMoment.url }}</a>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div class="demo-image__preview" style="margin-top: 10px;" v-if="userMoment.photoList">
                    <el-image
                      v-for="(image, index) in userMoment.photoList"
                      :key="index"
                      fit="contain"
                      style="height: 150px; cursor:zoom-in; margin-right: 5px;"
                      :src="image"
                      :preview-src-list="userMoment.photoList">
                    </el-image>
                  </div>

                  <div class="rightBottom">
                    <div style="display: inline-block;" class="b1">
                      <Praise style="margin-top: -2px;" class="b1"
                              :bizUid="userMoment.uid" :resourceType="'4'"
                              :isPraiseValue="userMoment.praiseInfo.isPraise"
                              :isTreadValue="userMoment.praiseInfo.isTread"
                              :praiseCountValue="userMoment.praiseInfo.praiseCount"
                              :treadCountValue="userMoment.praiseInfo.treadCount"
                      ></Praise>
                    </div>

                  </div>
                </div>
              </el-card>

              <el-empty v-if="!loading && momentList.length == 0" description="空空如也"></el-empty>
              <div v-else class="isEnd" style="margin-top: 5px">
                <span>
                  <div class="loadContent" @click="loadContent(4)" v-if="!isEnd&&!loading">查看更多</div>
                  <div class="lds-css ng-scope" v-if="!isEnd&&loading">
                    <div style="width:100%;height:100%" class="lds-facebook">
                      <div></div>
                      <div></div>
                      <div></div>
                    </div>
                  </div>
                  <span v-if="isEnd">我也是有底线的~</span>
                </span>
              </div>

            </div>
          </el-tab-pane>

          <!-- 问题  -->
          <el-tab-pane name="6" label="问题">
            <span :class="activeName==6?'tab-pane-active':'tab-pane'" slot="label"><i
              class="el-icon-collection"></i><span>问题</span></span>
            <div style="min-height: 773px">

              <div
                v-for="(item, index) in newProblemData"
                :key="item.uid"
                class="blogs"
                style="padding-top: 1px; padding-bottom: 5px"
                v-if="item.user"
                data-scroll-reveal="enter bottom over 1s"
              >
                <el-row :span="24" class="problemLine">

                  <el-col :xs="24" :sm="24">
                    <div class="blogtitle" style="font-size: 16px">

                      <a :href=" VUE_MOGU_WEB + '/cInfo/' + item.oid +'?title=面经'"
                         target="_blank">{{ item.title }}</a>
                      <span style="float: right">
                          <el-tag class="pointer" @click="getToProblem(item, '1')" size="mini"
                                  v-if="item.isSelection == 1" effect="dark" type="warning">精选</el-tag>
                          <el-tag class="pointer" @click="getToProblem(item, '2')" size="mini" effect="plain"
                                  v-for="problemType in problemTypeDictList" :key="problemType.uid"
                                  v-if="item.problemType == problemType.dictValue"
                                  :type="problemType.listClass">{{ problemType.dictLabel }}</el-tag>
                          <el-tag class="pointer" @click="getToProblem(item, '3')" size="mini" effect="plain"
                                  v-for="problemDifficulty in problemDifficultyDictList" :key="problemDifficulty.uid"
                                  v-if="item.problemDifficulty == problemDifficulty.dictValue"
                                  :type="problemDifficulty.listClass">{{ problemDifficulty.dictLabel }}</el-tag>
                        </span>

                    </div>

                    <div>
                        <span v-for="(problemTag, index) in item.problemTagList">
                          <el-tag class="pointer" size="mini" effect="plain" @click="getToProblem(problemTag.uid, '4')"
                                  style="margin-right: 3px" v-if="index%3==0"
                                  type="primary">{{ problemTag.name }}</el-tag>
                          <el-tag class="pointer" size="mini" effect="plain" @click="getToProblem(problemTag.uid, '4')"
                                  style="margin-right: 3px" v-if="index%3==1"
                                  type="danger">{{ problemTag.name }}</el-tag>
                          <el-tag class="pointer" size="mini" effect="plain" @click="getToProblem(problemTag.uid, '4')"
                                  style="margin-right: 3px" v-if="index%3==2" type="info">{{ problemTag.name }}</el-tag>
                        </span>
                    </div>

                    <div class="bloginfo">
                      <ul>
                        <li style=" padding-right: 6px" @click="getUserCenter(item)">
                          <span :class="item.user.userTag > 0 ?'vip-avatar':''">
                            <el-avatar :class="item.user.userTag > 0 ?'vip-color':''" size="medium"
                                       v-if="item.user.photoUrl" :src="item.user.photoUrl"></el-avatar>
                            <el-avatar size="small" v-else
                                       src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"></el-avatar>
                            <span style="font-size: 10px; right: -4px; bottom: -1px;" class="vip-text pointer"
                                  v-if="item.user.userTag > 0">v</span>
                          </span>
                        </li>
                        <li class="author" style="margin-top: 9px; margin-left: 3px;" @click="getUserCenter(item)">
                          <a href="javascript:void(0);">{{ item.user.nickName }}</a>
                        </li>

                        <li class="lmname" style="margin-top: 8px" v-if="item.blogSort">
                          <span class="iconfont">&#xe603;</span>
                          <a
                            href="javascript:void(0);"
                            @click="goToList(item.blogSort.uid)"
                          >{{ item.blogSort.sortName }}</a>
                        </li>

                        <li class="view" style="margin-top: 8px">
                          <span class="iconfont">&#xe8c7;</span>
                          <span>{{ item.clickCount }}</span>
                        </li>

                        <li class="view" style="margin-top: 8px;">
                          <Collection :bizUid="item.uid" :collectType="'6'"
                                      :collectCountValue="item.collectInfo.collectCount"
                                      :isCollectValue="item.collectInfo.isCollect"
                          ></Collection>
                        </li>

                        <li class="view" style="margin-top: 8px;">
                          <Praise :bizUid="item.uid" :resourceType="'6'"
                                  :isPraiseValue="item.praiseInfo.isPraise"
                                  :isTreadValue="item.praiseInfo.isTread"
                                  :praiseCountValue="item.praiseInfo.praiseCount"
                                  :treadCountValue="item.praiseInfo.treadCount"
                          ></Praise>
                        </li>

                        <li class="createTime" style="margin-top: 8px">
                          <span class="iconfont">&#xe606;</span>
                          {{ timeAgo(item.createTime) }}
                        </li>
                      </ul>
                    </div>
                  </el-col>
                </el-row>
              </div>

              <el-empty v-if="!loading && newProblemData.length == 0" description="空空如也"></el-empty>
              <div v-else class="isEnd" style="margin-top: 5px">
                <span>
                  <div class="loadContent" @click="loadContent(5)" v-if="!isEnd&&!loading">查看更多</div>
                  <div class="lds-css ng-scope" v-if="!isEnd&&loading">
                    <div style="width:100%;height:100%" class="lds-facebook">
                      <div></div>
                      <div></div>
                      <div></div>
                    </div>
                  </div>
                  <span v-if="isEnd">我也是有底线的~</span>
                </span>
              </div>

            </div>
          </el-tab-pane>
        </el-tabs>

      </div>
    </article>
    <Report v-if="dialogReportVisible" :visible="dialogReportVisible" :reportInfo="reportInfo"
            @beforeClose="beforeClose"></Report>

    <!--头像裁剪-->
    <avatar-cropper
      v-show="imagecropperShow"
      :key="imagecropperKey"
      :width="1200"
      :height="328"
      :url="url"
      lang-type="zh"
      :noCircle="true"
      :noSquare="true"
      @close="close"
      @crop-upload-success="cropSuccess"
    />
  </div>
</template>

<script>
import countTo from 'vue-count-to';
import {Loading} from "element-ui";
import {
  addUserWatch,
  deleteUserWatch,
  getBlogListByUser,
  getQuestionListByUser,
  getUserByUid,
  getUserCenterByUid,
  getUserWatchList,
  updateCurrentUserBackgroundImage
} from "../api/about";
import AvatarCropper from '@/components/AvatarCropper'
import {mapMutations} from "vuex";
import {getUserMomentList, getUserMomentTopicList} from "@/api/moment";
import {getListByDictTypeList} from "@/api/sysDictData";
import {timeAgo} from "../utils/webUtils";
import Collection from "../components/Collection";
import Praise from "../components/Praise";
import CommentBox from "@/components/CommentBox";
import Report from "@/components/Report"
import CommentList from "@/components/CommentList";
import MomentBox from "@/components/Moment/MomentBox"
import {getProblemQueryList} from "../api/problem";
import {recorderBehavior} from "../api";

export default {
  data() {
    return {
      VUE_MOGU_WEB: process.env.VUE_MOGU_WEB,
      listData: [],
      activeName: "1", // 激活页
      activeName2: "1",
      keyword: "",
      currentPage: 1,
      pageSize: 8,
      total: 0, //总数量
      isEnd: false, //是否到底底部了
      loading: false, //是否正在加载
      orderByDescColumn: "create_time", // 降序字段
      adminUid: "", // 管理员uid
      userUid: "", // 管理员uid
      userInfo: {}, // 用户信息【管理员或者前端用户】
      loginUserInfo: {}, // 当前登录用户的信息
      userCenterInfo: {
        blogCount:0 ,
        questionCount:0 ,
        followCount:0 ,
        watchCount:0 ,
        momentCount:0 ,
        userMedalCount:0
      }, // 用户中心信息
      userWatchListData: [],
      isWatch: true, // 是否是获取关注者
      imagecropperShow: false, //是否显示图片切割
      imagecropperKey: 0,
      userCenterType: 1, // 默认用户中心类型，【1 文章、2 问答、3 关注】
      url: process.env.PICTURE_API + "/file/cropperPicture",
      defaultAvatar: process.env.defaultAvatar,
      userTagDictList: [], // 用户标签字典
      userLevelDictList: [], //用户等级字典
      momentTopicList: [], // 话题
      momentList: [], // 动态
      typeList: ['warning', 'danger', 'success', 'info', 'primary'],
      reportInfo: {}, // 举报信息
      dialogReportVisible: false, // 控制举报弹出
      newProblemData: [], //最新问题
      problemTypeDictList: [], // 问题类型字典
      problemDifficultyDictList: [], // 问题难度字典
      problemDifficultyDefault: null, // 问题难度默认值
      problemTypeDefault: null, // 问题类型默认值
      userLevelImagesDictList: null,
      userLevelImage: null,
      searchParams: { // 搜索参数
        userUid: "",
        keyword: "",
        tagValue: "",
        problemDifficulty: "",
        problemType: "",
        isSelection: "",
        hasAnswer: ""
      },
      // xss白名单配置
      options: {
        whiteList: {
          a: ['href', 'title', 'target'],
          span: ['class'],
          h1: ['class'],
          h2: ['class'],
          h3: ['class'],
          h4: ['class'],
          h5: ['class'],
          h6: ['class'],
          pre: [],
          code: ['class'],
          p: ['class'],
          ol: [],
          blockquote: ['class'],
          ul: ['class'],
          li: ['class'],
          img: ['class', 'src', 'alt', 'style'],
          iframe: ['class', 'src', 'style', 'scrolling', 'border', 'frameborder', 'allowfullscreen'],
          hr: [],
          video: ['class', 'src', 'controls'],
          source: ['src', 'type']
        }
      },
    };
  },
  components: {
    //注册组件
    AvatarCropper,
    countTo,
    CommentList,
    Collection,
    Praise,
    Report,
    CommentBox,
    //注册组件
    MomentBox,
  },
  mounted() {
    this.loginUserInfo = this.$store.state.user.userInfo
  },
  watch: {
    // 判断登录状态位是否改变（用于控制弹框）
    '$store.state.user.userInfo': function (newFlag, oldFlag) {
      this.loginUserInfo = this.$store.state.user.userInfo
    },
    $route(to, from) {
      // 切换选项卡时，永远选择第一个
      this.listData = []
      this.userWatchListData = []
      this.adminUid = this.$route.query.adminUid;
      this.userUid = this.$route.query.userUid;
      // 获取列表
      this.getDataList(this.activeName);
      // 获取用户信息
      this.initUserInfo(false)
    }
  },
  created() {
    this.adminUid = this.$route.query.adminUid;
    this.userUid = this.$route.query.userUid;

    // 获取类型
    let type = this.$route.query.type
    this.goToUserCenter(type)
    this.getDictList()
    this.userMomentTopicList()
    // this.userMomentList()
    // this.problemList(true);
  },
  methods: {
    //拿到vuex中的写的方法
    ...mapMutations(['setLoginMessage', 'setMomentTopicList', 'setMomentList', 'setUserTag']),
    getLvImage(level) {
      this.userLevelImage = this.userLevelImagesDictList[level - 1].dictValue
    },
    /**
     * dateTimeStamp是一个时间毫秒，注意时间戳是秒的形式，在这个毫秒的基础上除以1000，就是十位数的时间戳。13位数的都是时间毫秒。
     * @param dateTimeStamp
     * @returns {string}
     */
    timeFormat(gainTime) {
      if (gainTime) {
        return this.timeAgo(gainTime) + '获得'
      } else {
        return '暂未获得'
      }
    },
    goMedalDetail: function () {
      this.$router.push("/medal/" + this.userUid)
    },
    //最新博客列表
    problemList(isClear) {
      let that = this;
      that.loadingInstance = Loading.service({
        lock: true,
        text: '正在努力加载中……',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      let params = {};
      if (this.searchParams.tagValue) {
        console.log("tagValue", this.searchParams.tagValue)
        let tagUid = this.searchParams.tagValue.join(",");
        params.problemTagUid = tagUid;
      }
      params.userUid = this.searchParams.userUid;
      params.problemDifficulty = this.searchParams.problemDifficulty;
      params.problemType = this.searchParams.problemType;
      params.pageSize = this.pageSize;
      params.currentPage = this.currentPage;
      params.orderByDescColumn = this.orderByDescColumn;
      getProblemQueryList(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.isEnd = false;
          console.log("isClear", isClear)
          let records = response.data.records
          if (isClear) {
            this.newProblemData = records;
          } else {
            this.newProblemData = this.newProblemData.concat(records);
          }
          this.total = response.data.total;
          this.pageSize = response.data.size;
          this.currentPage = response.data.current;
          //全部加载完毕
          if (records.length < this.pageSize) {
            this.isEnd = true;
          }
        } else {
          this.isEnd = true;
        }
        this.loading = false;
        that.loadingInstance.close();
      }, function (err) {
        that.loadingInstance.close();
      });
    },
    getToProblem(problem, type) {
      this.currentPage = 1
      this.pageSize = 15
      this.searchParams.userUid = this.userUid
      this.searchParams.problemType = null
      this.searchParams.problemDifficulty = null
      this.searchParams.isSelection = null
      this.searchParams.tagValue = null
      switch (type) {
        case '1': {
          this.searchParams.isSelection = problem.isSelection
          this.problemList('true')
        }
          break;
        case '2': {
          this.searchParams.problemType = problem.problemType
          this.problemList('true')
        }
          break;
        case '3': {
          this.searchParams.problemDifficulty = problem.problemDifficulty
          this.problemList('true')
        }
          break;
        case '4': {
          let tagArray = new Array()
          tagArray[0] = problem
          this.searchParams.tagValue = tagArray
          this.problemList('true')
        }
          break;
      }
    },
    // 校验是否登录
    validLogin() {
      let userInfo = this.$store.state.user.userInfo
      if (userInfo.userName == undefined) {
        return false;
      } else {
        return true;
      }
    },
    /**
     * dateTimeStamp是一个时间毫秒，注意时间戳是秒的形式，在这个毫秒的基础上除以1000，就是十位数的时间戳。13位数的都是时间毫秒。
     * @param dateTimeStamp
     * @returns {string}
     */
    timeAgo(dateTimeStamp) {
      return timeAgo(dateTimeStamp)
    },
    userMomentList: function () {
      let params = {}
      params.uid = this.momentUid
      params.userUid = this.userUid
      params.orderByDescColumn = this.orderByDescColumn
      params.orderBy = this.orderBy
      params.currentPage = this.currentPage;
      params.pageSize = this.pageSize;
      params.topicUids = this.selectTopicUid
      this.loading = false;
      getUserMomentList(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let momentList = response.data.records;
          let newMomentList = this.momentList.concat(momentList);
          this.setMomentList(newMomentList)
          this.momentList = newMomentList
          this.total = response.data.total;
          this.pageSize = response.data.size;
          this.currentPage = response.data.current;
          //全部加载完毕
          if (momentList.length < this.pageSize) {
            this.isEnd = true;
          }
        }
        this.loading = false;
      });
    },
    userMomentTopicList: function () {
      let params = {}
      params.currentPage = 1;
      params.pageSize = 10;
      getUserMomentTopicList(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.momentTopicList = response.data.records;
        }
      });
    },
    replaceImg: function (text) {
      text = this.$commonUtil.replaceMinImg(text)
      if (document.body.clientWidth > 1000) {
        text = this.$commonUtil.replaceMinIframe(text)
      }
      return text
    },
    imageChange: function (e) {
      //首先需要判断点击的是否是图片
      let type = e.target.localName;
      if (type == "img") {
        this.dialogPictureVisible = true;
        this.dialogImageUrl = e.target.currentSrc;
      }
    },
    report: function (item) {
      let reportInfo = {}
      reportInfo.reportType = "moment" // 评论
      reportInfo.reportUserUid = item.userUid // 被举报人id
      reportInfo.reportContentUid = item.uid
      reportInfo.reportContent = item.content
      this.reportInfo = reportInfo
      this.dialogReportVisible = true
    },
    beforeClose: function () {
      this.dialogReportVisible = false
    },
    /**
     * 字典查询
     */
    getDictList: function () {
      let dictTypeList = ['sys_user_tag', 'sys_problem_difficulty', 'sys_problem_type', 'sys_user_level', 'user_level_images']
      getListByDictTypeList(dictTypeList).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let dictMap = response.data;
          this.problemDifficultyDictList = dictMap.sys_problem_difficulty.list
          this.problemTypeDictList = dictMap.sys_problem_type.list
          this.userTagDictList = dictMap.sys_user_tag.list
          this.userLevelDictList = dictMap.sys_user_level.list
          this.userLevelImagesDictList = dictMap.user_level_images.list
          this.setUserTag(dictMap.sys_user_tag.list)
          if (dictMap.sys_problem_difficulty.defaultValue) {
            this.problemDifficultyDefault = dictMap.sys_problem_difficulty.defaultValue;
          }
          if (dictMap.sys_problem_type.defaultValue) {
            this.problemTypeDefault = dictMap.sys_problem_type.defaultValue;
          }

          // 获取用户信息
          this.initUserInfo(false)
        }
      });
    },
    // 改变背景图
    changeBackground: function () {
      let isLogin = this.$store.state.user.isLogin
      if (!isLogin) {
        this.$notify.error({
          title: '警告',
          message: '登录后才可以评论哦~',
          offset: 100
        });
        // 未登录，自动弹出登录框
        this.setLoginMessage(Math.random())
        return;
      }
      this.imagecropperShow = true
    },
    sendMessage: function () {
      // 发送私信
      this.$router.push({path: "/chat", query: {userUid: this.userUid}});
    },
    goToUserCenter: function (type) {
      console.log("跳转", type)
      let tab = {}
      if (type != undefined) {
        // 获取列表
        switch (type) {
          case "1": {
            this.activeName = "1"
            tab.label = "最新文章"
            tab.name = "1"
          }
            break;
          case "2": {
            this.activeName = "1"
            tab.label = "最热文章"
            tab.name = "2"
          }
            break;
          case "3": {
            this.activeName = "2"
            tab.label = "最新问答"
            tab.name = "1"
          }
            break;
          case "4": {
            this.activeName = "2"
            tab.label = "最热问答"
            tab.name = "2"
          }
            break;
          case "5": {
            this.activeName = "3"
            tab.label = "最新收藏"
            tab.name = "1"
          }
            break;
          case "6": {
            this.activeName = "3"
            tab.label = "最热收藏"
            tab.name = "2"
          }
            break;
          case "7": {
            this.activeName = "4"
            tab.label = "TA的粉丝"
            tab.name = "1"
          }
            break;
          case "8": {
            this.activeName = "4"
            tab.label = "TA关注的人"
            tab.name = "2"
          }
            break;
          case "9": {
            this.activeName = "5"
            tab.label = "动态"
            tab.name = "1"
          }
            break;
          case "10": {
            // this.activeName = "6"
            // tab.label = "问题"
            // tab.name = "1"
            // 跳转到勋章页面
            this.$router.push("/medal/" + this.userUid)

          }
            break;
        }
      } else {
        tab.label = "最新文章"
        tab.name = "1"
      }
      console.log("开始触发", tab)
      this.handleClick2(tab, "")
    },
    // 头像裁剪关闭回调
    close() {
      this.imagecropperShow = false
    },
    cropSuccess(resData) {
      this.imagecropperShow = false
      this.imagecropperKey = this.imagecropperKey + 1
      console.log("裁剪后的数据", resData)
      let array = new Array()
      array.push(resData.url)
      this.userInfo.backgroundFileUid = resData.uid
      this.userInfo.backgroundUrl = resData.url
      // 提交背景图片
      let params = {}
      params.backgroundFileUid = resData.uid
      updateCurrentUserBackgroundImage(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.$commonUtil.message.success(response.message)
          // 重新刷新用户信息
          this.initUserInfo(true)
        } else {
          this.$commonUtil.message.error(response.message)
        }
      })
    },
    // 跳转到个人中心页
    getUserCenter: function (object, type) {
      console.log("跳转到个人中心", object)
      let routeData = this.$router.resolve({
        path: "/userCenter",
        query: {userUid: object.userUid}
      });
      window.open(routeData.href, '_blank');
    },
    //跳转到搜索详情页
    goToSortList(uid) {
      let routeData = this.$router.push({
        path: "/list",
        query: {sortUid: uid}
      });
    },
    //跳转到搜索详情页
    goToTagList(uid) {
      let routeData = this.$router.push({
        path: "/list",
        query: {tagUid: uid}
      });
    },
    initUserInfo: function (refresh) {
      let params = new URLSearchParams()
      if (this.adminUid) {
        params.append("adminUid", this.adminUid)
      }
      if (this.userUid) {
        params.append("userUid", this.userUid)
      }
      params.append("refresh", refresh)
      getUserByUid(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let userInfo = response.data
          if (userInfo.backgroundFileUrl) {

          } else {
            // 随机从背景池中选取一张图
            let defaultBackgroundImages = this.$SysConf.defaultBackgroundImages
            let myDate = new Date();
            let date = myDate.getDate();
            let index = date % defaultBackgroundImages.length
            userInfo.backgroundFileUrl = defaultBackgroundImages[index]

            setTimeout(() => {
              recorderBehavior({
                "otherData": userInfo.nickName + "的个人中心",
                "bizUid": userInfo.uid,
                "bizType": "USER",
                "behavior": "VISIT_PAGE"
              }).then(response => {
              })
            }, 5000)
          }
          this.getLvImage(userInfo.userLevel)
          this.userInfo = userInfo
        } else {
          this.$message.error(response.message)
        }
      })

      getUserCenterByUid(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.userCenterInfo = response.data
        } else {
          this.$message.error(response.message)
        }
      })
    },
    //跳转到文章详情
    goToInfo(uid) {
      let routeData = this.$router.resolve({
        path: "/info",
        query: {blogUid: uid}
      });
      window.open(routeData.href, "_blank");
    },
    errorHandler: function () {
      return true
    },
    // 一级tab点击事件
    handleClick(tab, event) {
      // 切换选项卡时，永远选择第一个
      this.currentPage = 1
      this.isEnd = false
      this.listData = []
      this.userWatchListData = []
      this.momentList = []
      this.newProblemData = []
      this.activeName2 = "1"
      switch (tab.label) {
        case "文章": {
          this.orderByDescColumn = "create_time";
          this.getDataList()
        }
          break;
        case "问答": {
          this.orderByDescColumn = "create_time";
          this.getDataList()
        }
          break;
        case "收藏": {

        }
          break;
        case "关注": {
          this.isWatch = false
          this.getDataList()
        }
          break;
        case "动态": {
          this.orderByDescColumn = "create_time";
          this.getDataList()
        }
          break;
        case "问题": {
          this.orderByDescColumn = "create_time";
          this.getDataList()
        }
          break;
      }

    },
    // 二级tab点击事件
    handleClick2(tab, event) {
      this.activeName2 = tab.name
      this.listData = []
      this.userWatchListData = []
      this.momentList = []
      this.newProblemData = []
      this.currentPage = 1
      switch (tab.label) {
        case "最新文章": {
          this.orderByDescColumn = "create_time";
          this.getDataList()
        }
          break;
        case "最热文章": {
          this.orderByDescColumn = "click_count";
          this.getDataList()
        }
          break;
        case "最新问答": {
          this.orderByDescColumn = "create_time";
          this.getDataList()
        }
          break;
        case "最热问答": {
          this.orderByDescColumn = "click_count";
          this.getDataList()
        }
          break;
        case "TA的粉丝": {
          this.isWatch = false
          this.getDataList()
        }
          break;
        case "TA关注的人": {
          this.isWatch = true
          this.getDataList()
        }
          break;
        case "动态": {
          this.orderByDescColumn = "create_time";
          this.getDataList()
        }
          break;
        case "问题": {
          this.orderByDescColumn = "create_time";
          this.getDataList()
        }
          break;
      }
    },
    //最新文章列表
    getDataList() {
      let that = this;
      console.log("获取类型", this.activeName)
      let type = Number(this.activeName)
      console.log("获取类型", type)
      that.loadingInstance = Loading.service({
        lock: true,
        text: '正在努力加载中……',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      switch (type) {
        //###################### 获取文章列表 ######################
        case 1 : {
          console.log("获取文章列表", type)
          let params = {};
          params.currentPage = this.currentPage;
          params.pageSize = this.pageSize;
          params.orderByDescColumn = this.orderByDescColumn
          params.adminUid = this.adminUid
          params.userUid = this.userUid
          getBlogListByUser(params).then(response => {
            if (response.code == this.$ECode.SUCCESS) {
              console.log("获取的文章列表", response.data)
              let newData = response.data.records;
              let newTempData = that.listData.concat(newData);
              that.listData = newTempData
              that.total = response.data.total;
              that.pageSize = response.data.size;
              that.currentPage = response.data.current;
              //全部加载完毕
              if (newData.length < this.pageSize) {
                this.isEnd = true;
              }
            }
            that.loadingInstance.close();
          }, function (err) {
            that.loadingInstance.close();
          });
        }
          ;
          break
        //###################### 获取文章列表结束 ######################

        //###################### 获取问答列表 #########################
        case 2 : {
          let params = {};
          params.currentPage = this.currentPage;
          params.pageSize = this.pageSize;
          params.orderByDescColumn = this.orderByDescColumn
          params.adminUid = this.adminUid
          params.userUid = this.userUid
          getQuestionListByUser(params).then(response => {
            console.log("获取的问答列表", response.data)
            if (response.code == this.$ECode.SUCCESS) {
              let newData = response.data.records;
              let newTempData = that.listData.concat(newData);
              that.listData = newTempData
              that.total = response.data.total;
              that.pageSize = response.data.size;
              that.currentPage = response.data.current;
              //全部加载完毕
              if (newData.length < this.pageSize) {
                this.isEnd = true;
              }
            }
            that.loadingInstance.close();
          }, function (err) {
            that.loadingInstance.close();
          });
        }
          ;
          break
        //###################### 获取问答列表结束 ######################

        //###################### 获取用户粉丝和关注人 ######################
        case 4 : {
          let params = {}
          let isWatch = this.isWatch
          console.log("isWatch", isWatch)
          if (isWatch) {
            params.userUid = this.adminUid ? this.adminUid : this.userUid
          } else {
            params.toUserUid = this.adminUid ? this.adminUid : this.userUid
          }
          params.pageSize = this.pageSize
          params.currentPage = this.currentPage
          getUserWatchList(params).then(response => {
            if (response.code == this.$ECode.SUCCESS) {
              let newData = response.data.records
              console.log("newData", newData)
              let newTempData = that.userWatchListData.concat(newData);
              this.userWatchListData = newTempData
              //全部加载完毕
              if (newData.length < this.pageSize) {
                this.isEnd = true;
              }
            } else {
              this.$message.error(response.message)
            }

            that.loadingInstance.close();
          }, function (err) {
            that.loadingInstance.close();
          })
        }
          ;
          break
        case 5 : {
          let params = {}
          params.currentPage = this.currentPage;
          params.pageSize = this.pageSize;
          params.orderByDescColumn = this.orderByDescColumn
          params.adminUid = this.adminUid
          params.userUid = this.userUid
          getUserMomentList(params).then(response => {
            if (response.code == this.$ECode.SUCCESS) {
              let newData = response.data.records
              console.log("newData", newData)
              let newTempData = that.momentList.concat(newData);
              this.momentList = newTempData
              //全部加载完毕
              if (newData.length < this.pageSize) {
                this.isEnd = true;
              }
            } else {
              this.$message.error(response.message)
            }

            that.loadingInstance.close();
          }, function (err) {
            that.loadingInstance.close();
          })

        }
          ;
          break
        case 6 : {
          let params = {}
          params.currentPage = this.currentPage;
          params.pageSize = this.pageSize;
          params.orderByDescColumn = this.orderByDescColumn
          params.userUid = this.userUid
          getProblemQueryList(params).then(response => {
            if (response.code == this.$ECode.SUCCESS) {
              this.isEnd = false;
              let records = response.data.records
              let newTempData = that.newProblemData.concat(records);
              this.newProblemData = newTempData;
              this.total = response.data.total;
              this.pageSize = response.data.size;
              this.currentPage = response.data.current;
              //全部加载完毕
              if (records.length < this.pageSize) {
                this.isEnd = true;
              }
            } else {
              this.isEnd = true;
            }
            this.loading = false;
            that.loadingInstance.close();
          }, function (err) {
            that.loadingInstance.close();
          });

        }
          ;
          break
        //###################### 获取粉丝列表结束 ######################
        default: {
          console.log("都未命中")
          that.loadingInstance.close();
        }
      }
    },
    loadContent: function (type) {
      this.loading = false;
      this.currentPage = this.currentPage + 1;
      this.getDataList(type)
    },
    // 关注用户
    watchUser: function () {
      let isLogin = this.$store.state.user.isLogin
      if (!isLogin) {
        this.$notify.error({
          title: '警告',
          message: '登录后才可以关注哦~',
          offset: 100
        });
        // 未登录，自动弹出登录框
        this.setLoginMessage(Math.random())
        return;
      }
      let params = {}
      if (this.adminUid) {
        params.toUserUid = this.adminUid
        params.isAdmin = "1"
      } else if (this.userUid) {
        params.toUserUid = this.userUid
        params.isAdmin = "0"
      }
      addUserWatch(params).then(response => {
        console.log("关注用户", response)
        if (response.code == this.$ECode.SUCCESS) {
          this.$message.success(response.message)
          this.userInfo.isWatchUser = true
        } else {
          this.$message.error(response.message)
        }
      })
    },
    // 关注用户
    watchOtherUser: function (item) {
      let isLogin = this.$store.state.user.isLogin
      if (!isLogin) {
        this.$notify.error({
          title: '警告',
          message: '登录后才可以评论哦~',
          offset: 100
        });
        // 未登录，自动弹出登录框
        this.setLoginMessage(Math.random())
        return;
      }
      console.log("关注用户", item)
      let params = {}
      params.toUserUid = item.userUid
      params.isAdmin = "0"
      addUserWatch(params).then(response => {
        console.log("关注用户", response)
        if (response.code == this.$ECode.SUCCESS) {
          this.$message.success(response.message)
          this.userInfo.isWatchUser = true
          this.userWatchListData = []
          this.currentPage = 1
          this.getDataList(4)
        } else {
          this.$message.error(response.message)
        }
      })
    },
    // 取消关注
    unWatchUser: function () {
      let isLogin = this.$store.state.user.isLogin
      if (!isLogin) {
        this.$notify.error({
          title: '警告',
          message: '登录后才可以评论哦~',
          offset: 100
        });
        // 未登录，自动弹出登录框
        this.setLoginMessage(Math.random())
        return;
      }
      let params = {}
      if (this.adminUid) {
        params.toUserUid = this.adminUid
        params.isAdmin = "1"
      } else if (this.userUid) {
        params.toUserUid = this.userUid
        params.isAdmin = "0"
      }
      deleteUserWatch(params).then(response => {
        console.log("取消关注用户", response)
        if (response.code == this.$ECode.SUCCESS) {
          this.$message.success(response.message)
          this.userInfo.isWatchUser = false
          item.watchStatus = "0"
        } else {
          this.$message.error(response.message)
        }
      })
    },
    // 关注用户
    unWatchOtherUser: function (toUserUid) {
      let isLogin = this.$store.state.user.isLogin
      if (!isLogin) {
        this.$notify.error({
          title: '警告',
          message: '登录后才可以评论哦~',
          offset: 100
        });
        // 未登录，自动弹出登录框
        this.setLoginMessage(Math.random())
        return;
      }
      let params = {}
      params.toUserUid = toUserUid
      deleteUserWatch(params).then(response => {
        console.log("取消关注用户", response)
        if (response.code == this.$ECode.SUCCESS) {
          this.$message.success(response.message)
          this.userWatchListData = []
          this.currentPage = 1
          this.getDataList(4)
        } else {
          this.$message.error(response.message)
        }
      })
    },
  }
};
</script>


<style>
.el-tabs__active-bar {
  display: none;
}

.userEvent .el-tabs__nav {
  width: 100%;
  text-align: center;
}

.userEvent2 .el-tabs__nav {
  width: 0%;
}

.userEvent2 .el-tabs__nav-wrap::after {
  display: none;
}
</style>

<style scoped>

.like-user-avatar {
  height: 40px;
  width: 40px;
  border-radius: 40px;
  display: inline-block;
  margin-left: 2px;
  cursor: pointer;
  transition: all 0.6s;
}

.like-user-avatar:hover {
  transform: scale(1.5); /*transform:变形属性，scale：缩放1.1倍 */
}

.blogs .blogpic {
  width: 100%;
}

.blogs .blogpic img {
  width: 100px;
}

.bloginfo {
  margin-top: 10px;
}

.userInfo .nickname {
  font-size: 24px;
  font-weight: 700;
  line-height: 56px;
}

.userInfo .gender {
  font-size: 14px;
}

.userInfo .summary {
  font-size: 16px;
  padding-top: 16px
}

.userInfo .love {
  margin-top: 3px;
}

.isEnd {
  float: left;
  width: 100%;
  height: 80px;
  text-align: center;
}

.loadContent {
  border-radius: 25px;
  width: 120px;
  height: 30px;
  line-height: 30px;
  font-size: 16px;
  margin: 0 auto;
  color: aliceblue;
  cursor: pointer;
  background: rgba(0, 0, 0, 0.8);
}

.fontColorSytle {
  color: #FFFFFF;
}

.countStyle {
  color: #000;
  font-weight: bold;
}

.countStyle2 {
  color: #000;
}

.comment {
  display: none;
}

.commentList {
  padding-top: 20px;
  width: 100%;
  margin: 0 auto;
}

.commentList .p1 {
  float: left;
}

.commentList .left {
  display: inline-block;
  width: 5%;
  height: 100%;
}

.commentList .left img {
  margin: 0 auto;
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.commentList .right {
  display: inline-block;
  width: 93%;
  margin-left: 5px;
}

.commentList .rightTop {
  height: 30px;
  margin-top: 2px;
}

.commentList .rightTop .userName {
  color: #303133;
  margin-left: 10px;
  font-size: 16px;
  font-weight: bold;
}

.commentList .rightTop .timeAgo {
  color: #909399;
  margin-left: 5px;
  font-size: 12px;
}

.commentList .rightCenter {
  margin-left: 20px;
  min-height: 50px;
  margin-top: 15px;
}

.commentList .rightBottom {
  margin-left: 10px;
  height: 30px;
}

.commentList .rightBottom el-link {

}

.commentList .rightBottom .b1 {
  margin-top: -5px;
  margin-right: 10px;
}
</style>
