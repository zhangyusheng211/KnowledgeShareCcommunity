<template>
  <div>
    <div class="pagebg sh"></div>
    <div class="container">
      <h1 class="t_nav">
        <a href="/" class="n1">网站首页</a>
        <a href="/" class="n2">搜索</a>
      </h1>

      <el-tabs v-if="showSearchType && showCreateBlog" v-model="activeName" @tab-click="handleClick">
        <el-tab-pane name="-1" v-if="searchModel == 1">
          <span :class="activeName==-1?'tab-pane-active':'tab-pane'" slot="label"><i class="el-icon-collection-tag"></i> <span>综合</span></span>
        </el-tab-pane>
        <el-tab-pane name="0">
          <span :class="activeName==0?'tab-pane-active':'tab-pane'" slot="label"><i class="el-icon-collection-tag"></i> <span>博客</span></span>
        </el-tab-pane>
        <el-tab-pane name="4">
          <span :class="activeName==4?'tab-pane-active':'tab-pane'" slot="label"><i
            class="el-icon-collection"></i> <span>面经</span></span>
        </el-tab-pane>
        <el-tab-pane name="3">
          <span :class="activeName==3?'tab-pane-active':'tab-pane'" slot="label"><i
            class="el-icon-notebook-1"></i> <span>动态</span></span>
        </el-tab-pane>
        <el-tab-pane name="1">
          <span :class="activeName==1?'tab-pane-active':'tab-pane'" slot="label"><i class="el-icon-tickets"></i> <span>问答</span></span>
        </el-tab-pane>
        <el-tab-pane name="2">
          <span :class="activeName==2?'tab-pane-active':'tab-pane'" slot="label"><i
            class="el-icon-user"></i> <span>用户</span></span>
        </el-tab-pane>
        <el-tab-pane name="5">
          <span :class="activeName==5?'tab-pane-active':'tab-pane'" slot="label"><i class="el-icon-document"></i> <span>资源</span></span>
        </el-tab-pane>
      </el-tabs>

      <!--blogsbox begin-->
      <div class="blogsbox commentList">
        <div
          v-for="item in listData"
          :key="item.uid"
          class="blogs"
          data-scroll-reveal="enter bottom over 1s"
        >

          <!--判断返回的结果是否是博客-->
          <el-row v-if="searchModel == 1 && (activeName=='-1' || activeName=='0' ||  activeName=='3' ||  activeName=='4' || activeName=='5' )">

            <h3 class="blogtitle">
              <a v-html="item.title" :href="getResourceUrl(item)" target="_blank">{{ item.title }}</a>
              <span style="float: right; font-weight: 400">
                <el-tag class="pointer" v-if="item.resourceType=='1'" size="mini" effect="plain"
                        type="primary">博客</el-tag>
                <el-tag class="pointer" v-if="item.resourceType=='2'" size="mini" effect="plain"
                        type="warning">问答</el-tag>
                <el-tag class="pointer" v-if="item.resourceType=='3'" size="mini" effect="plain"
                        type="info">课程</el-tag>
                <el-tag class="pointer" v-if="item.resourceType=='4'" size="mini" effect="plain"
                        type="warning">动态</el-tag>
                <el-tag class="pointer" v-if="item.resourceType=='5'" size="mini" effect="plain"
                        type="info">评论</el-tag>
                <el-tag class="pointer" v-if="item.resourceType=='6'" size="mini" effect="plain"
                        type="success">面经</el-tag>
                <el-tag class="pointer" v-if="item.resourceType=='7'" size="mini" effect="plain">用户</el-tag>
                <el-tag class="pointer" v-if="item.resourceType=='11'" size="mini" effect="plain">资源</el-tag>
              </span>
            </h3>

            <el-row>
              <el-col :span="19" :xs="24">
                <p class="blogtext" v-html="item.summary">{{ item.summary }}</p>
              </el-col>
              <el-col :span="5" :xs="0">
                <span class="blogpic">
                  <a :href="getResourceUrl(item)"  target="_blank">
                    <img v-if="item.photoUrl" v-lazy="item.photoUrl" :key="item.photoUrl" alt="">
                  </a>
                </span>
              </el-col>
            </el-row>

            <div class="bloginfo">
              <ul>
                <!-- 搜索内容 -->
                <span v-if="item.user">
                  <li style="padding-right: 6px; text-align: center;cursor: pointer">
                    <span :class="item.user.userTag > 0 ?'vip-avatar':''">
                      <el-avatar :class="item.user.userTag > 0 ?'vip-color':''" v-if="item.user"
                                 @click.native="getUserCenter(item.user)" fit="fill" size="medium"
                                 :src="item.user.photoUrl"></el-avatar>
                      <el-avatar v-else fit="fill" size="small" src="https://empty">
                        <img :src="defaultAvatar"/>
                      </el-avatar>
                      <span style="font-size: 10px; right: -4px; bottom: -1px;" class="vip-text pointer"
                            v-if="item.user.userTag > 0">v</span>
                    </span>
                  </li>

                  <li class="author" style="margin-top: 9px;">
                    <span class="pointer" :class="'lv'+ item.user.userLevel"
                          @click="getUserCenter(item.user)">{{ item.user.nickName }}</span>
                  </li>
                </span>

                <li class="author" v-else style="margin-top: 9px;">
                  <span class="iconfont">&#xe60f;</span>
                  <a href="javascript:void(0);">{{ item.author }}</a>
                </li>

                <li class="lmname" v-if="item.sortName" style="margin-top: 9px;">
                  <span class="iconfont">&#xe603;</span>
                  <a href="javascript:void(0);">{{ item.sortName }}</a>
                </li>

                <li class="lmname" v-if="item.tagName" style="margin-top: 9px;">
                  <span class="iconfont">&#xe603;</span>
                  <a href="javascript:void(0);">{{ item.tagName }}</a>
                </li>

                <li class="createTime" style="margin-top: 9px;">
                  <span class="iconfont">&#xe606;</span>{{ item.createTime }}
                </li>

              </ul>
            </div>
          </el-row>

          <!--判断返回的结果是否是博客-->
          <el-row v-else-if="activeName=='0'">
            <div v-if="item.isVip == '1'" class="el-icon-present" style="float: right;font-size:20px;"></div>
            <h3 class="blogtitle">
              <a v-html="item.title" :href="item.type == 0 ? '/info/'+item.oid : item.outsideLink"
                 target="_blank">{{ item.title }}</a>
            </h3>

            <el-row>
              <el-col :span="19" :xs="24">
                <p class="blogtext" v-html="item.summary">{{ item.summary }}</p>
              </el-col>
              <el-col :span="5" :xs="0">
                <span class="blogpic">
                  <a :href="item.type == 0 ? '/info/'+item.oid : item.outsideLink" target="_blank">
                    <img v-if="item.photoUrl" v-lazy="item.photoUrl" :key="item.photoUrl" alt="">
                  </a>
                </span>
              </el-col>
            </el-row>

            <div class="bloginfo">
              <ul>
                <li class="author">
                  <span class="iconfont">&#xe60f;</span>
                  <a href="javascript:void(0);" @click="getUserCenter(item,'blog')" v-html="getAuthor(item)"></a>
                </li>
                <li class="lmname" v-if="item.blogSortName">
                  <span class="iconfont">&#xe603;</span>
                  <a href="javascript:void(0);" @click="goToList(item.blogSortUid)">{{ item.blogSortName }}</a>
                </li>
                <li class="createTime"><span class="iconfont">&#xe606;</span>{{ item.createTime }}</li>
              </ul>
            </div>
          </el-row>

          <!--判断返回的结果是否是问答-->
          <el-row v-else-if="activeName=='1'" :span="24" class="questionLine">
            <el-col :xs="6" :sm="4">
              <div class="itemNum">
                <el-tag type="success">回答 {{ item.replyCount }}</el-tag>
              </div>
              <div class="itemNum">
                <el-tag type="warning">阅读 {{ item.clickCount }}</el-tag>
              </div>
            </el-col>

            <el-col :xs="18" :sm="20">
              <div class="blogtitle">
                <a :href=" VUE_MOGU_WEB + '/qInfo/'+item.oid" target="_blank" v-html="item.title">{{ item.title }}</a>
                <span v-for="(questionTag, index) in item.questionTagList" style="float: right">
                  <el-tag style="margin-right: 3px" v-if="index%3==0" type="primary">{{ questionTag.name }}</el-tag>
                  <el-tag style="margin-right: 3px" v-if="index%3==1" type="danger">{{ questionTag.name }}</el-tag>
                  <el-tag style="margin-right: 3px" v-if="index%3==2" type="info">{{ questionTag.name }}</el-tag>
                </span>
              </div>


              <div class="bloginfo">
                <ul>

                  <li v-if="item.user" @click="getUserCenter(item,'question')">
                        <span :class="item.user.userTag > 0 ?'vip-avatar':''">
                          <el-avatar :class="item.user.userTag > 0 ?'vip-color':''" size="medium"
                                     v-if="item.user.photoUrl" :src="item.user.photoUrl"></el-avatar>
                          <el-avatar size="small" v-else
                                     src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"></el-avatar>
                          <span style="font-size: 10px; right: -4px; bottom: -1px;" class="vip-text pointer"
                                v-if="item.user.userTag > 0">v</span>
                        </span>
                  </li>

                  <li class="author" style="margin-top: 9px; margin-left: 3px;" v-if="item.user">
                    <a href="javascript:void(0);" @click="getUserCenter(item,'question')"
                       v-html="item.user.nickName">{{ item.user.nickName }}</a>
                  </li>

                  <li class="lmname" v-if="item.blogSort" style="margin-top: 7px">
                    <span class="iconfont">&#xe603;</span>
                    <a
                      href="javascript:void(0);"
                      @click="goToList(item.blogSort.uid)"
                    >{{ item.blogSort.sortName }}</a>
                  </li>

                  <li class="view" style="margin-top: 7px">
                    <span class="iconfont">&#xe8c7;</span>
                    <span>{{ item.clickCount }}</span>
                  </li>

                  <li class="createTime" style="margin-top: 7px">
                    <span class="iconfont">&#xe606;</span>
                    {{ item.createTime }}
                  </li>

                </ul>
              </div>
            </el-col>
          </el-row>

          <!--Andy 判断返回的结果是否是动态圈-->
          <el-row style="min-height: 8px" :gutter="20" v-else-if="activeName=='3'">
            <span class="left p1" :class="item.user.userTag > 0 ?'vip-avatar':''">
              <img :class="item.user.userTag > 0 ?'vip-color':''" v-if="item.user" style="cursor:pointer;"
                   @click="getUserCenter(item.user,'moment')"
                   :src="item.user.photoUrl ? item.user.photoUrl:defaultAvatar"
                   onerror="onerror=null;src=defaultAvatar"/>
              <img v-else :src="defaultAvatar"/>
              <span style="" class="vip-tip vip-text pointer" v-if="item.user.userTag > 0"
                    @click.natice="getUserCenter(item.user)">v</span>
            </span>

            <div class="right p1">
              <div class="rightTop" v-if="item.user">
                <el-link class="userName" :underline="false" v-if="item.user.nickName"
                         @click="getUserCenter(item.user)">{{ item.user.nickName }}
                </el-link>
                <el-tag style="height: 30px; margin-left:5px;" v-for="userTag in userTagDictList" :key="userTag.uid"
                        v-if="item.user.userTag == userTag.dictValue && item.user.userTag != 0"
                        :type="userTag.listClass">{{ userTag.dictLabel }}
                </el-tag>
                <span class="timeAgo">
                  <span class="lv" :class="'lv'+ item.user.userLevel" v-for="userLevel in userLevelDictList"
                        :key="userLevel.uid" v-if="item.user.userLevel == userLevel.dictValue">
                        {{ userLevel.remark }}
                  </span>
                </span>
                <span class="iconfont" v-if="item.user.gender=='1'"
                      style="margin-left: 5px; color: dodgerblue;  font-size: 18px; font-weight: bold;">&#xe641;</span>
                <span class="iconfont" v-if="item.user.gender=='2'"
                      style="margin-left: 5px; color: palevioletred;  font-size: 18px; font-weight: bold;">&#xe643;</span>
                <span class="timeAgo" style="color: #8a919f;"
                      v-if="item.user.occupation">{{ item.user.occupation }}</span>
                <span class="timeAgo" v-if="item.createTime">{{ timeAgo(item.createTime) }}</span>


                <span class="timeAgo" v-else>刚刚</span>
              </div>

              <div class="rightCenter ck-content" style="cursor: pointer;"
                   @click="getResourceUrlByEntity('4', item.uid)" v-highlight
                   v-html="$xss(item.content, options)"></div>

              <div>
                <el-tag
                  style="margin-left: 3px"
                  :key="index"
                  v-for="(userMomentTopic, index) in item.userMomentTopicList"
                  :type="typeList[index%5]"
                  hit
                  size="mini"
                  effect="light"
                >{{ userMomentTopic.content }}
                </el-tag>
              </div>

              <div v-if="item.url">
                <div class="link-container">
                  <div class="zone-link-part">
                    <span class="el-icon-link zone-link-bg"></span>
                    <div class="link-right-part"><span class="title">{{ item.urlInfo }}</span>
                      <a :href="item.url" target="_blank" class="url">{{ item.url }}</a>
                    </div>
                  </div>
                </div>
              </div>

              <div class="demo-image__preview" style="margin-top: 10px;" v-if="item.photoList">
                <el-image
                  v-for="(image, index) in item.photoList"
                  :key="index"
                  fit="contain"
                  style="height: 150px; cursor:zoom-in; margin-right: 5px;"
                  :src="image"
                  :preview-src-list="item.photoList">
                </el-image>
              </div>
            </div>
          </el-row>

          <!--Andy 判断返回的结果是否是用户-->
          <el-row v-else-if="activeName=='2'">
            <span class="left p1" :class="item.userTag > 0 ?'vip-avatar':''">
              <img :class="item.userTag > 0 ?'vip-color':''" v-if="item" style="cursor:pointer;"
                   @click="getUserCenter(item,'moment')" :src="item.photoUrl ? item.photoUrl:defaultAvatar"
                   onerror="onerror=null;src=defaultAvatar"/>
              <img v-else :src="defaultAvatar"/>
              <span style="" class="vip-tip vip-text pointer" v-if="item.userTag > 0"
                    @click.natice="getUserCenter(item,'moment')">v</span>
            </span>

            <div class="right p1">
              <div class="rightTop" v-if="item">
                <el-col :span="16">
                  <el-link class="userName" :underline="false" v-if="item.nickName"
                           @click="getUserCenter(item, 'moment')">{{ item.nickName }}
                  </el-link>
                  <el-tag style="height: 30px; margin-left:5px;" v-for="userTag in userTagDictList" :key="userTag.uid"
                          v-if="item.userTag == userTag.dictValue && item.userTag != 0" :type="userTag.listClass">
                    {{ userTag.dictLabel }}
                  </el-tag>
                  <span class="iconfont" v-if="item.gender=='1'"
                        style="margin-left: 5px; color: dodgerblue;  font-size: 18px; font-weight: bold;">&#xe641;</span>
                  <span class="iconfont" v-if="item.gender=='2'"
                        style="margin-left: 5px; color: palevioletred;  font-size: 18px; font-weight: bold;">&#xe643;</span>
                  <span class="timeAgo" style="color: #8a919f;" v-if="item.occupation">{{ item.occupation }}</span>
                </el-col>

                <!-- 关注 -->
                <el-col :span="8" class="isWatchShow" v-if="item.uid != loginUserInfo.uid">
                  <el-button v-if="item.userWatchStatus == 0" icon="el-icon-sugar" type="danger"
                             @click="watchOtherUser(item.uid)" size="mini" round><span>&nbsp;关&nbsp;注&nbsp;</span>
                  </el-button>
                  <el-button v-if="item.userWatchStatus == 1" icon="el-icon-lightning" type="info"
                             @click="unWatchOtherUser(item.uid)" size="mini" round>取消关注
                  </el-button>
                  <el-button v-if="item.userWatchStatus == 2" icon="el-icon-ship" type="success"
                             @click="unWatchOtherUser(item.uid)" size="mini" round>互相关注
                  </el-button>
                  <el-button icon="el-icon-chat-dot-round" type="primary" @click="goChat(item.uid)" size="mini" round>
                    &nbsp;私&nbsp;信&nbsp;
                  </el-button>
                </el-col>
              </div>

              <br>
              <div class="tip">
                <strong>简介</strong>
                <span v-if="item.summary">
                    {{ item.summary }}
                </span>
                <span v-else>
                    这个小家伙很懒，什么都没有留下
                </span>
              </div>

              <br>
              <span class="pointer">
                <span class="el-icon-coin"></span>
              </span>积分: &nbsp;&nbsp;{{ item.credits }} &nbsp;
              <span class="pointer">
                <span class="el-icon-notebook-1"></span>
              </span>动态: &nbsp;&nbsp;{{ item.userMomentCount }} &nbsp;
              <span class="pointer">
                  <span class="el-icon-document"></span>
              </span>文章:&nbsp;&nbsp;{{ item.blogPublishCount }} &nbsp;&nbsp;
              <span class="pointer">
                  <span class="el-icon-chat-dot-square"></span>
              </span>评论: &nbsp;&nbsp;{{ item.commentPublishCount }} &nbsp;&nbsp;
              <span class="pointer">
                  <span class="el-icon-view"></span>
              </span>关注: &nbsp;&nbsp;{{ item.userWatchCount }} &nbsp;
              <span class="pointer">
                  <span class="iconfont">&#xec7f;</span>
              </span>点赞: &nbsp;&nbsp;{{ item.praiseCount }} &nbsp;
            </div>
          </el-row>

          <!-- 判断返回的结果是否是 面经-->
          <el-row v-else-if="activeName=='4'" class="problemLine">
            <el-col :xs="24" :sm="24">
              <div class="blogtitle" style="font-size: 16px">
                <a v-if="!quickReply" :href=" VUE_MOGU_WEB + '/cInfo/' + item.oid +'?title=面经'"
                   target="_blank">{{ item.title }}</a>
                <a v-else href="javascript:void(0);" @click="showQuickReplyVisible(item, index)">{{ item.title }}</a>
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
                                style="margin-right: 3px" v-if="index%3==1" type="danger">{{ problemTag.name }}</el-tag>
                        <el-tag class="pointer" size="mini" effect="plain" @click="getToProblem(problemTag.uid, '4')"
                                style="margin-right: 3px" v-if="index%3==2" type="info">{{ problemTag.name }}</el-tag>
                      </span>
              </div>

              <div class="bloginfo">
                <ul>
                  <li style=" padding-right: 6px" @click="getUserCenter(item,'question')">
                        <span :class="item.user.userTag > 0 ?'vip-avatar':''">
                          <el-avatar :class="item.user.userTag > 0 ?'vip-color':''" size="medium"
                                     v-if="item.user.photoUrl" :src="item.user.photoUrl"></el-avatar>
                          <el-avatar size="small" v-else
                                     src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"></el-avatar>
                          <span style="font-size: 10px; right: -4px; bottom: -1px;" class="vip-text pointer"
                                v-if="item.user.userTag > 0">v</span>
                        </span>
                  </li>
                  <li class="author" style="margin-top: 9px; margin-left: 3px;"
                      @click="getUserCenter(item, 'question')">
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

          <!--判断返回的结果是否是资源-->
          <el-row v-else-if="activeName=='5'">

          <div v-if="item.isVip == '1'" class="el-icon-present" style="float: right;font-size:20px;"></div>
            <h3 class="blogtitle">
              <a v-html="item.name" :href="'/resource/' + item.uid"
                 target="_blank">{{ item.name }}</a>
            </h3>

            <el-row>
              <el-col :span="19" :xs="24">
                <p class="blogtext" v-html="item.summary">{{ item.summary }}</p>
              </el-col>
              <el-col :span="5" :xs="0">
                <span class="blogpic">
                  <a :href="'/resource/' + item.uid" target="_blank">
                    <img v-if="item.photoUrl" v-lazy="item.photoUrl" :key="item.photoUrl" alt="">
                  </a>
                </span>
              </el-col>
            </el-row>

            <div class="bloginfo">
              <ul>
                <li class="author">
                  <span class="iconfont">&#xe60f;</span>
                  <a href="javascript:void(0);" @click="getUserCenter(item,'blog')" v-html="getAuthor(item)"></a>
                </li>
                <li class="lmname" v-if="item.blogSortName">
                  <span class="iconfont">&#xe603;</span>
                  <a href="javascript:void(0);" @click="goToList(item.blogSortUid)">{{ item.blogSortName }}</a>
                </li>
                <li class="createTime"><span class="iconfont">&#xe606;</span>{{ item.createTime }}</li>
              </ul>
            </div>

          </el-row>

        </div>


        <div class="isEnd">
          <div
            class="loadContent"
            @click="loadContent"
            v-if="!isEnd && !loading && totalPages>0"
          >查看更多
          </div>

          <div class="lds-css ng-scope" v-if="!isEnd && loading">
            <div style="width:100%;height:100%" class="lds-facebook">
              <div></div>
              <div></div>
              <div></div>
            </div>
          </div>

          <span v-if="listData.length >= 0 && isEnd &&!loading && totalPages>0">我也是有底线的~</span>

          <span v-if="totalPages == 0 && !loading">
            <el-empty description="空空如也" image=""></el-empty>
          </span>
        </div>
      </div>

      <!--blogsbox end-->
      <div class="sidebar">
        <!-- 三级推荐 -->
        <ThirdRecommend></ThirdRecommend>

        <!--标签云-->
        <TagCloud></TagCloud>

        <!--四级推荐-->
        <FourthRecommend></FourthRecommend>

        <!--点击排行-->
        <HotBlog></HotBlog>

        <Link></Link>

        <!--关注我们-->
        <FollowUs></FollowUs>
      </div>

    </div>
  </div>
</template>

<script>

import ThirdRecommend from "../components/ThirdRecommend";
import FourthRecommend from "../components/FourthRecommend";
import TagCloud from "../components/TagCloud";
import HotBlog from "../components/HotBlog";
import FollowUs from "../components/FollowUs";
import Collection from "../components/Collection";

import {
  searchAggByES,
  searchBlogByAuthor,
  searchBlogBySort,
  searchBlogBySQL,
  searchBlogByTag,
  searchByUser,
  searchMomentByUser,
  searchProblem,
  searchQuestion,
  searchResource
} from "../api/search";
import {getBlogByUid} from "../api/blogContent";
import {getWebConfig} from "../api";
import {mapMutations} from "vuex";
import {getListByDictTypeList} from "@/api/sysDictData";
import {timeAgo} from "../utils/webUtils";
import Praise from "../components/Praise";
import {addUserWatch, deleteUserWatch} from "../api/about";
import {getProblemQueryList, problemDegree} from "../api/problem";
import {Loading} from "element-ui";

export default {
  name: "list",
  data() {
    return {
      VUE_MOGU_WEB: process.env.VUE_MOGU_WEB,
      defaultAvatar: process.env.defaultAvatar,
      // xss白名单配置
      options: {
        whiteList: {
          a: ['href', 'title', 'target'],
          span: ['class'],
          h1: ['class'],
          h2: ['class'],
          h3: ['class'],
          h4: ['class'],
          pre: [],
          code: ['class'],
          p: ['class']
        }
      },
      selectProblem: {}, // 选中的问题
      quickReplyVisible: false, // 是否显示快问快答
      quickReply: false, // 快问快答
      searchParams: { // 搜索参数
        keyword: "",
        tagValue: "",
        problemDifficulty: "",
        problemType: "",
        isSelection: "",
        hasAnswer: ""
      },
      problemDifficultyDictList: [], // 问题难度字典
      problemTypeDictList: [], // 问题类型字典
      userLevelDictList: [], // 用户等级标签
      resourceSortDictList: [], // 资源分类
      problemDifficultyDefault: null, // 问题难度默认值
      problemTypeDefault: null, // 问题类型默认值
      loginUserInfo: {}, // 当前登录用户的信息
      searchModel: 0, //搜索模式 0:SQL搜索、1:ES搜索、2:Solr搜索
      listData: [], // 查询出的文章
      userTagDictList: [], // 用户标签字典
      typeList: ['warning', 'danger', 'success', 'info', 'primary'],
      keywords: "",
      currentPage: 1,
      totalPages: 0,
      pageSize: 15,
      total: 0, //总数量
      tagUid: "",
      searchlistData: [], //搜索出来的文章
      sortUid: "",
      showCreateBlog: false, // 是否开启创作
      showSearchType: false, // 是否显示按钮
      isEnd: false, //是否到底底部了
      activeName: "0", // 激活页
      loading: false //内容是否正在加载
    };
  },
  components: {
    FourthRecommend,
    ThirdRecommend,
    TagCloud,
    HotBlog,
    FollowUs,
    Collection,
    Praise,
  },
  created() {
    this.keywords = this.$route.query.keyword;
    this.tagUid = this.$route.query.tagUid;
    this.sortUid = this.$route.query.sortUid;
    this.author = this.$route.query.author;

    let model = this.$route.query.model;
    if (model) {
      this.searchModel = model
      if (this.searchModel  == 1) {
        // 只有es搜索时，才激活综合搜索
        this.activeName = "-1"
      }
    }

    let activeName = this.$route.query.active;
    if (activeName) {
      this.activeName = activeName
    }

    if (
      this.keywords == undefined &&
      this.tagUid == undefined &&
      this.sortUid == undefined &&
      this.author == undefined
    ) {
      return;
    }
    this.getWebConfigInfo();
    this.search();
    this.getDictList();
  },
  mounted() {

  },
  watch: {
    $route(to, from) {
      this.keywords = this.$route.query.keyword;
      this.tagUid = this.$route.query.tagUid;
      this.sortUid = this.$route.query.sortUid;
      this.searchlistData = [] // 清空查询出来的博客
      this.currentPage = 1
      this.pageSize = 15
      this.search();
    },
    // 判断用户信息
    '$store.state.user.userInfo': function (newFlag, oldFlag) {
      this.loginUserInfo = this.$store.state.user.userInfo
    }
  },
  methods: {
    ...mapMutations(['setWebConfigData']),
    // 获取资源的跳转URL
    getResourceUrl(aggEsDocument) {
      let resourceType = aggEsDocument.resourceType
      switch (resourceType) {
        case '1': {
          // 博客
          return "/info/" + aggEsDocument.oid
        }
          break;
        case '2': {
          // 问答
          return "/qInfo/" + aggEsDocument.oid + "?title=问答"
        }
          break;
        case '3': {
          // 课程
          return "/video?mediaInfoUid=" + aggEsDocument.uid + "?title=问答"
        }
          break;
        case '4': {
          // 动态
          return "/moment?uid=" + aggEsDocument.id
        }
          break;
        case '6': {
          // 面经
          return "/cInfo/" + aggEsDocument.oid + "?title=面经"
        }
          break;
        case '7': {
          // 个人中心
          return "/userCenter?userUid=" + aggEsDocument.uid
        }
          break;
        case '11': {
          // 资源详情
          return "/resource/" + aggEsDocument.uid
        }
          break;
      }
    },
    // 获取资源的跳转URL
    getResourceUrlByEntity(resourceType, id) {
      switch (resourceType) {
        case '1': {
          window.open(this.VUE_MOGU_WEB + "/info/" + id, "_blank")
        }
          break;
        case '2': {
          window.open(this.VUE_MOGU_WEB + "/qInfo/" + id + "?title=问答", "_blank")
        }
          break;
        case '3': {
          window.open(this.VUE_MOGU_WEB + "/video?mediaInfoUid=" + id + "?title=问答", "_blank")
        }
          break;
        case '4': {
          window.open(this.VUE_MOGU_WEB + "/moment?uid=" + id, "_blank")
        }
          break;
        case '6': {
          window.open(this.VUE_MOGU_WEB + "/cInfo/" + id + "?title=面经", "_blank")
        }
          break;
        case '7': {
          window.open(this.VUE_MOGU_WEB + "/userCenter?userUid=" + id, "_blank")
        }
          break;
        case '11': {
          window.open(this.VUE_MOGU_WEB + "/resource/" + id, "_blank")
        }
          break;
      }
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
      params.keyword = this.searchParams.keyword;
      params.problemDifficulty = this.searchParams.problemDifficulty;
      params.problemType = this.searchParams.problemType;
      if (this.searchParams.isSelection) {
        params.isSelection = "1"
      }
      if (this.searchParams.hasAnswer) {
        params.hasAnswer = "1"
      }

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
          this.$message.error(response.message)
          this.isEnd = true;
        }
        this.loading = false;
        that.loadingInstance.close();
      }, function (err) {
        that.loadingInstance.close();
      });
    },
    // 调到聊天页
    goChat: function (userUid) {
      this.$router.push({path: "/chat", query: {userUid: userUid}});
    },
    showQuickReplyVisible: function (item, index) {
      console.log("选中的问题", index)
      console.log("选中的问题", this.currentPage, this.pageSize)
      this.selectIndex = index + 1
      this.selectProblem = item
      this.quickReplyVisible = true
      // 变成已查看状态
      this.handleProblemDegree('2')
    },
    // 提交掌握记录
    handleProblemDegree(degreeStatus) {
      if (this.selectProblem) {
        let params = {}
        params.problemUid = this.selectProblem.uid
        params.degree = degreeStatus
        problemDegree(params).then(response => {
          if (response.code == this.$ECode.SUCCESS) {
            // 已阅读不弹出
            if (degreeStatus != '2') {
              this.$message.success(response.message)
            }
          } else {
            this.$message.error(response.message)
          }
        })
      } else {
        this.$message.error("未选中问题")
      }
    },
    getToProblem(problem, type) {
      this.currentPage = 1
      this.pageSize = 15
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
    // 关注用户
    watchOtherUser: function (userUid) {
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
      params.toUserUid = userUid;
      addUserWatch(params).then(response => {
        console.log("关注用户", response)
        if (response.code == this.$ECode.SUCCESS) {
          this.$message.success(response.message)
          let that = this;
          that.loading = true;
          if (this.keywords != undefined) {
            this.showSearchType = true
            let params = new URLSearchParams();
            params.append("currentPage", that.currentPage);
            params.append("pageSize", that.pageSize);
            params.append("keywords", that.keywords);

            searchByUser(params).then(response => {
              that.convertSearchDataForUser(that, response)

            });
          }
        } else {
          this.$message.error(response.message)
        }
      })
    },
    // 取消关注用户
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
          let that = this;
          that.loading = true;
          if (this.keywords != undefined) {
            this.showSearchType = true
            let params = new URLSearchParams();
            params.append("currentPage", that.currentPage);
            params.append("pageSize", that.pageSize);
            params.append("keywords", that.keywords);

            searchByUser(params).then(response => {
              that.convertSearchDataForUser(that, response)

            });
          }
        } else {
          this.$message.error(response.message)
        }
      })
    },
    /**
     * 字典查询
     */
    getDictList: function () {
      let dictTypeList = ['sys_user_tag', 'sys_problem_difficulty', 'sys_problem_type', 'sys_user_level', 'sys_resource_sort']
      getListByDictTypeList(dictTypeList).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let dictMap = response.data;
          this.problemDifficultyDictList = dictMap.sys_problem_difficulty.list
          this.problemTypeDictList = dictMap.sys_problem_type.list
          this.userTagDictList = dictMap.sys_user_tag.list
          this.userLevelDictList = dictMap.sys_user_level.list
          this.resourceSortDictList = dictMap.sys_resource_sort.list
          /*this.setUserTag(dictMap.sys_user_tag.list)*/
          if (dictMap.sys_problem_difficulty.defaultValue) {
            this.problemDifficultyDefault = dictMap.sys_problem_difficulty.defaultValue;
          }
          if (dictMap.sys_problem_type.defaultValue) {
            this.problemTypeDefault = dictMap.sys_problem_type.defaultValue;
          }
        }
      });
    },
    //跳转到文章详情
    goToInfo(blog) {
      if (blog.type == "0") {
        let routeData = this.$router.resolve({
          path: "/info",
          query: {blogOid: blog.oid}
        });
        window.open(routeData.href, '_blank');
      } else if (blog.type == "1") {
        let params = new URLSearchParams();
        params.append("uid", blog.uid);
        getBlogByUid(params).then(response => {
          // 记录一下用户点击日志
        });
        window.open(blog.outsideLink, '_blank');
      }
    },
    getAuthor(item) {
      if (item.authorName) {
        return item.authorName;
      }
      return item.author;
    },
    /**
     * 获取网站配置
     */
    getWebConfigInfo: function () {
      let webConfigData = this.$store.state.app.webConfigData
      if (webConfigData.createTime) {
        this.contact = webConfigData;
        this.mailto = "mailto:" + this.contact.email;
        this.openComment = webConfigData.openComment
        console.log("是否开启投稿", webConfigData)
        this.showCreateBlog = webConfigData.openCreateBlog == "1" ? true : false
      } else {
        getWebConfig().then(response => {
          if (response.code == this.$ECode.SUCCESS) {
            this.info = response.data;
            // 存储在Vuex中
            this.setWebConfigData(response.data)
            this.openComment = this.info.openComment
            this.showCreateBlog = this.info.openCreateBlog == "1" ? true : false
          }
        });
      }
    },
    //点击了分类
    goToList(uid) {
      let routeData = this.$router.resolve({
        path: "/list",
        query: {sortUid: uid}
      });
      window.open(routeData.href, '_blank');
    },
    goToAuthor(author) {
      let routeData = this.$router.resolve({
        path: "/list",
        query: {author: author}
      });
      window.open(routeData.href, '_blank');
    },
    // 加载内容
    loadContent: function () {
      this.currentPage = this.currentPage + 1;
      this.search();
    },
    convertSearchData: function (that, response) {
      if (response.code == this.$ECode.SUCCESS) {
        that.isEnd = false;
        //获取总页数
        that.total = response.data.total;
        that.pageSize = response.data.pageSize;
        let listData = [];
        listData = response.data.blogList;
        that.totalPages = response.data.blogList.length;
        // 判断搜索的博客是否有内容
        if (response.data.total <= 0) {
          that.isEnd = true;
          that.loading = false;
          that.totalPages = 0;
          this.listData = []
          return;
        }
        //全部加载完毕
        if (listData.length < that.pageSize) {
          that.isEnd = true;
        }

        listData = that.searchlistData.concat(listData);
        that.searchlistData = listData;
        this.listData = listData;
      } else {
        this.$message.error(response.message)
        that.isEnd = true;
      }
      that.loading = false;
    },
    convertSearchDataForUser: function (that, response) {
      if (response.code == this.$ECode.SUCCESS) {
        that.isEnd = false;
        //获取总页数
        that.total = response.data.total;
        that.pageSize = response.data.pageSize;
        that.currentPage = response.data.currentPage;
        let listData = [];
        listData = response.data.blogList;
        that.totalPages = response.data.blogList.length;
        // 判断搜索的博客是否有内容
        if (response.data.total <= 0) {
          that.isEnd = true;
          that.loading = false;
          that.totalPages = 0;
          this.listData = []
          return;
        }

        //全部加载完毕
        if (listData.length < that.pageSize) {
          that.isEnd = true;
        }

        listData = listData;
        that.searchlistData = listData;
        this.listData = listData;
      } else {
        this.$message.error(response.message)
        that.isEnd = true;
      }
      that.loading = false;
    },
    handleClick(tab, event) {
      console.log("切换table")
      this.currentPage = 1
      this.searchlistData = []
      this.listData = []
      this.search()
    },
    // 跳转到个人中心页
    getUserCenter: function (row, type) {
      console.log("跳转到用户中心", row)
      console.log(">>>>>>>>>>>>>>>>>>>>>type: ", type)
      if (type == 'blog') {
        let routeData = this.$router.resolve({
          path: "/userCenter",
          query: {userUid: row.userUid}
        });
        window.open(routeData.href, '_blank');
      } else if (type == 'moment') {
        let routeData = this.$router.resolve({
          path: "/userCenter",
          query: {userUid: row.uid}
        });
        window.open(routeData.href, '_blank');
      } else if (type == 'question') {
        let routeData = this.$router.resolve({
          path: "/userCenter",
          query: {userUid: row.userUid}
        });
        window.open(routeData.href, '_blank');
      } else {
        let routeData = this.$router.resolve({
          path: "/userCenter",
          query: {userUid: row.uid}
        });
        window.open(routeData.href, '_blank');
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
    search: function () {
      console.log("开始搜索", this.activeName)
      let that = this;
      that.loading = true;
      if (this.keywords != undefined) {
        this.showSearchType = true
        let params = new URLSearchParams();
        params.append("currentPage", that.currentPage);
        params.append("pageSize", that.pageSize);
        params.append("keywords", that.keywords);
        if (that.activeName == -1) {
          console.log("触发综合搜索")
          searchAggByES(params).then(response => {
            console.log("返回综合结果", response)
            that.convertSearchData(that, response)
          });
        } else if (that.activeName == 0) {
          console.log("开始文章搜索")
          // 文章搜索
          if (this.searchModel == 0) {
            searchBlogBySQL(params).then(response => {
              that.convertSearchData(that, response)
            });
          } else if (this.searchModel == 1) {
            // 触发博客搜索
            params.append("resourceType", "BLOG");
            searchAggByES(params).then(response => {
              console.log("触发博客搜索", response)
              that.convertSearchData(that, response)
            });
          } else if (this.searchModel == 2) {
            searchBlogBySolr(params).then(response => {
              that.convertSearchData(that, response)
            });
          }
        } else if (that.activeName == 1) {
          // 问答搜索
          searchQuestion(params).then(response => {
            if (response.code == this.$ECode.SUCCESS) {
              that.isEnd = false;
              //获取总页数
              that.total = response.data.total;
              that.pageSize = response.data.pageSize;
              that.currentPage = response.data.currentPage;
              let listData = [];
              listData = response.data.questionList
              that.totalPages = response.data.questionList.length;

              // 判断搜索的博客是否有内容
              if (response.data.total <= 0) {
                that.isEnd = true;
                that.loading = false;
                this.listData = []
                return;
              }

              //全部加载完毕
              if (listData.length < that.pageSize) {
                that.isEnd = true;
              }

              listData = that.searchlistData.concat(listData);
              that.searchlistData = listData;
              this.listData = listData;
            } else {
              this.$message.error(response.message)
              that.isEnd = true;
            }
            that.loading = false;
          });
        } else if (that.activeName == 2) {
          // 用户搜索
          searchByUser(params).then(response => {
            that.convertSearchData(that, response)
          });
        } else if (that.activeName == 3) {
          if (this.searchModel == 1) {
            // 从ES中搜索
            params.append("resourceType", "MOMENT");
            searchAggByES(params).then(response => {
              console.log("触资源搜索", response)
              that.convertSearchData(that, response)
            });
            return
          }
          // 动态圈搜索
          searchMomentByUser(params).then(response => {
            that.convertSearchData(that, response)
          })
        } else if (that.activeName == 4) {
          if (this.searchModel == 1) {
            // 从ES中搜索
            params.append("resourceType", "PROBLEM");
            searchAggByES(params).then(response => {
              console.log("触资源搜索", response)
              that.convertSearchData(that, response)
            });
            return
          }
          // 从面经中搜索
          searchProblem(params).then(response => {
            that.convertSearchData(that, response)
          })
        } else if (that.activeName == 5) {
          console.log("触发资源搜索")
          if (this.searchModel == 1) {
            // 从ES中搜索
            params.append("resourceType", "RESOURCE");
            searchAggByES(params).then(response => {
              console.log("触资源搜索", response)
              that.convertSearchData(that, response)
            });
            return
          }
          // 从资源中搜索
          searchResource(params).then(response => {
            that.convertSearchData(that, response)
          })
        }
      } else if (this.tagUid != undefined) {
        this.showSearchType = false
        let params = new URLSearchParams();
        params.append("tagUid", that.tagUid);
        params.append("currentPage", that.currentPage);
        params.append("pageSize", that.pageSize);
        searchBlogByTag(params).then(response => {
          if (response.code == this.$ECode.SUCCESS && response.data.records.length > 0) {
            that.isEnd = false;
            //获取总页数
            that.totalPages = response.data.total;
            let listData = response.data.records;
            that.total = response.data.total;
            that.pageSize = response.data.size;
            that.currentPage = response.data.current;

            //全部加载完毕
            if (listData.length < that.pageSize) {
              that.isEnd = true;
            }
            // 设置分类名
            for (let i = 0; i < listData.length; i++) {
              listData[i].blogSort = listData[i].blogSort.sortName;
            }
            listData = that.searchlistData.concat(listData);
            that.searchlistData = listData;
            this.listData = listData;
            that.loading = false;
          } else {
            this.$message.error(response.message)
            that.isEnd = true;
            that.loading = false;
          }
        });
      } else if (this.sortUid != undefined) {
        this.showSearchType = false
        let params = new URLSearchParams();
        params.append("blogSortUid", that.sortUid);
        params.append("currentPage", that.currentPage);
        params.append("pageSize", that.pageSize);
        searchBlogBySort(params).then(response => {
          if (response.code == this.$ECode.SUCCESS && response.data.records.length > 0) {
            that.isEnd = false;
            //获取总页数
            that.totalPages = response.data.total;
            let listData = response.data.records;
            that.total = response.data.total;
            that.pageSize = response.data.size;
            that.currentPage = response.data.current;
            //全部加载完毕
            if (listData.length < that.pageSize) {
              that.isEnd = true;
            }
            for (let i = 0; i < listData.length; i++) {
              listData[i].blogSort = listData[i].blogSort.sortName;
            }
            listData = that.searchlistData.concat(listData);
            that.searchlistData = listData;
            this.listData = listData;
            that.loading = false;
          } else {
            this.$message.error(response.message)
            that.isEnd = true;
            that.loading = false;
          }
        });
      } else if (this.author != undefined) {
        console.log("以作者查询")

        this.showSearchType = true
        let params = new URLSearchParams();
        params.append("author", that.author);
        params.append("searchType", that.activeName);
        params.append("currentPage", that.currentPage);
        params.append("pageSize", that.pageSize);
        searchBlogByAuthor(params).then(response => {
          if (response.code == this.$ECode.SUCCESS && response.data.records.length > 0) {
            that.loading = false;
            that.isEnd = false;
            //获取总页数
            that.totalPages = response.data.total;
            let listData = response.data.records;
            that.total = response.data.total;
            that.pageSize = response.data.size;
            that.currentPage = response.data.current;

            //全部加载完毕
            if (listData.length < that.pageSize) {
              that.isEnd = true;
            }

            // 遍历博客分类信息
            if (that.activeName == "0") {
              for (let i = 0; i < listData.length; i++) {
                if (listData[i].blogSort == undefined) {
                  listData[i].blogSort = "未分类";
                } else {
                  listData[i].blogSort = listData[i].blogSort.sortName;
                }
              }
            }

            listData = that.searchlistData.concat(listData);
            that.searchlistData = listData;
            this.listData = listData;
            that.loading = false;
          } else {
            this.$message.error(response.message)
            that.isEnd = true;
            that.loading = false;
          }
        });
      }
    }
  }
};
</script>

<style>
</style>

<style scoped>
.isWatchShow {
  float: right;
}

.tip {
  padding: 10px;
  margin: 20px auto 15px auto;
  background-color: #ecf8ff;
  border-radius: 4px;
  border-left: 5px solid #50bfff;
  color: #888888;
}

.tip strong {
  color: #38485A;
  font-weight: 400 !important;
  font-size: 13px;
  padding-right: 8px;
}

.vip-tip {
  margin-right: -10px;
  margin-bottom: 1px;
}

.commentList .left img {
  margin: 0 auto;
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.commentList {
  padding-top: 20px;
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

.isEnd {
  float: left;
  width: 100%;
  height: 80px;
  text-align: center;
}

.ng-scope {
  margin: 0 auto;
  width: 18%;
  height: 10%;
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

@keyframes lds-facebook_1 {
  0% {
    top: 0px;
    height: 200px;
  }
  50% {
    top: 80px;
    height: 40px;
  }
  100% {
    top: 80px;
    height: 40px;
  }
}

@-webkit-keyframes lds-facebook_1 {
  0% {
    top: 0px;
    height: 200px;
  }
  50% {
    top: 80px;
    height: 40px;
  }
  100% {
    top: 80px;
    height: 40px;
  }
}

@keyframes lds-facebook_2 {
  0% {
    top: 20px;
    height: 160px;
  }
  50% {
    top: 80px;
    height: 40px;
  }
  100% {
    top: 80px;
    height: 40px;
  }
}

@-webkit-keyframes lds-facebook_2 {
  0% {
    top: 20px;
    height: 160px;
  }
  50% {
    top: 80px;
    height: 40px;
  }
  100% {
    top: 80px;
    height: 40px;
  }
}

@keyframes lds-facebook_3 {
  0% {
    top: 40px;
    height: 120px;
  }
  50% {
    top: 80px;
    height: 40px;
  }
  100% {
    top: 80px;
    height: 40px;
  }
}

@-webkit-keyframes lds-facebook_3 {
  0% {
    top: 40px;
    height: 120px;
  }
  50% {
    top: 80px;
    height: 40px;
  }
  100% {
    top: 80px;
    height: 40px;
  }
}

.lds-facebook {
  position: relative;
}

.lds-facebook div {
  position: absolute;
  width: 20px;
}

.lds-facebook div:nth-child(1) {
  left: 40px;
  background: #1d0e0b;
  -webkit-animation: lds-facebook_1 1s cubic-bezier(0, 0.5, 0.5, 1) infinite;
  animation: lds-facebook_1 1s cubic-bezier(0, 0.5, 0.5, 1) infinite;
  -webkit-animation-delay: -0.2s;
  animation-delay: -0.2s;
}

.lds-facebook div:nth-child(2) {
  left: 90px;
  background: #774023;
  -webkit-animation: lds-facebook_2 1s cubic-bezier(0, 0.5, 0.5, 1) infinite;
  animation: lds-facebook_2 1s cubic-bezier(0, 0.5, 0.5, 1) infinite;
  -webkit-animation-delay: -0.1s;
  animation-delay: -0.1s;
}

.lds-facebook div:nth-child(3) {
  left: 140px;
  background: #d88c51;
  -webkit-animation: lds-facebook_3 1s cubic-bezier(0, 0.5, 0.5, 1) infinite;
  animation: lds-facebook_3 1s cubic-bezier(0, 0.5, 0.5, 1) infinite;
}

.lds-facebook {
  width: 90px !important;
  height: 90px !important;
  -webkit-transform: translate(-45px, -45px) scale(0.45) translate(45px, 45px);
  transform: translate(-45px, -45px) scale(0.45) translate(45px, 45px);
}

.tab-pane {
  font-size: 16px;
  font-weight: bold;
  color: #282828;
}

.tab-pane-active {
  font-size: 16px;
  font-weight: bold;
  color: #00a7eb;
}

.questionLine .itemNum {
  height: 50px;
  width: 100%;
  line-height: 50px;
  justify-content: center;
  margin: 0 auto;
  font-weight: bold;
}
</style>

