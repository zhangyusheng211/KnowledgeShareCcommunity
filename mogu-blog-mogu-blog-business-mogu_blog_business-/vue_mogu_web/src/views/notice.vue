<template>
  <div>
    <div class="pagebg sorts"></div>
    <div class="container">
      <h1 class="t_nav">
        <a href="/" class="n1">网站首页</a>
        <a href="javascript:void(0);" class="n2">通知</a>
      </h1>

      <div class="commentBox" style="min-height: 900px">

        <el-tabs  v-model="noticeType" :tab-position="tabPosition"  style="min-height: 820px;" @tab-click="handleClick">

          <!--评论-->
          <el-tab-pane class="tabPane" name="1">
              <span slot="label" class="tabItem"><i class="el-icon-chat-dot-square"></i> 评论</span>
              <el-row style="height: 30px; border-bottom: 1px solid #e0e0e0;">
                <el-col :span="12">
                  <span style="font-size: 16px; font-weight: 600; color: #222226;">评论</span>
                </el-col>
                <el-col :span="12" style="text-align: right;" >
                  <span style="font-size: 16px; cursor: pointer" @click="handleDeleteBatch">
                    <i class="el-icon-view"></i> 清空所有消息
                  </span>
                </el-col>
              </el-row>

              <div class="commentList" v-for="item in notices" v-if="noticeType == '1'" :key="item.uid" >
                <span class="left p1"  v-if="item.comment && item.comment.user">
                  <el-link :href="VUE_MOGU_WEB + '/userCenter?userUid='+item.comment.user.uid" :underline="false">
                    <img v-if="item.comment.user"  :src="item.comment.user.photoUrl ? item.comment.user.photoUrl:defaultAvatar" onerror="onerror=null;src=defaultAvatar" />
                    <img v-else :src="defaultAvatar" />
                  </el-link>
                </span>

                <span class="right p1" v-if="item.comment && item.comment.user">
                  <div class="rightTop">
                    <el-link class="userName" :href="VUE_MOGU_WEB + '/userCenter?userUid='+item.comment.user.uid" :underline="false">{{item.comment.user.nickName}}</el-link>

                    <el-tag size="mini" effect="plain" style="margin-left:5px;" v-for="userTag in userTagDictList" :key="userTag.uid" v-if="item.comment.user.userTag == userTag.dictValue && item.comment.user.userTag != 0" :type="userTag.listClass">{{userTag.dictLabel}}</el-tag>

                    <span class="timeAgo" >
                       <span class="lv" style="background-color: rgba(255, 255, 255, 0.9);" :class="'lv'+ item.comment.user.userLevel" v-for="userLevel in userLevelDictList" :key="userLevel.uid" v-if="item.comment.user.userLevel == userLevel.dictValue">
                          Lv{{item.comment.user.userLevel}}
                       </span>
                    </span>

                    <span class="iconfont" v-if="item.comment.user.gender=='1'" style="margin-left: 5px; color: dodgerblue;  font-size: 18px; font-weight: bold;">&#xe641;</span>
                    <span class="iconfont" v-if="item.comment.user.gender=='2'" style="margin-left: 5px; color: palevioletred;  font-size: 18px; font-weight: bold;">&#xe643;</span>
                    <span class="timeAgo" style="color: #8a919f;" v-if="item.comment.user.userIpPossession">IP属地:{{item.comment.user.userIpPossession}}</span>
                    <span class="timeAgo" v-if="item.createTime">{{timeAgo(item.comment.createTime)}}</span>
                    <span class="timeAgo" v-else>刚刚</span>

                    <span class="timeAgo" v-if="item.businessType == 1">
                      评论了你的文章:
                      <a :href="VUE_MOGU_WEB + '/info/'+item.comment.blog.oid" target="_blank" style="cursor: pointer; color: #555;">{{splitStr(item.comment.blog.title, splitCount)}}</a>
                    </span>

                    <span class="timeAgo" v-if="item.businessType == 2">
                      评论了你的问答:
                      <a :href="VUE_MOGU_WEB + '/qInfo/'+item.comment.question.oid" target="_blank" style="cursor: pointer; color: #555;">{{splitStr(item.comment.question.title, splitCount)}}</a>
                    </span>

                    <span class="timeAgo" v-else-if="item.businessType == 3">
                      在文章
                      <a :href="VUE_MOGU_WEB + '/info/'+item.comment.blog.oid" target="_blank" style="cursor: pointer; color: #555;">{{splitStr(item.comment.blog.title, splitCount)}}</a>
                      中，回复了你的评论
                    </span>

                    <span class="timeAgo" v-else-if="item.businessType == 4">
                      在问答
                      <a :href="VUE_MOGU_WEB + '/qInfo/'+item.comment.question.oid" target="_blank" style="cursor: pointer; color: #555;">{{splitStr(item.comment.question.title, splitCount)}}</a>
                      中，回复了你的评论
                    </span>

                    <span class="timeAgo" v-else-if="item.businessType == 10">
                      <a :href="VUE_MOGU_WEB + '/messageBoard'" target="_blank" style="cursor: pointer; color: #555;">留言板</a>
                      页面，回复了你的评论
                    </span>

                    <span class="timeAgo" v-else-if="item.businessType == 11">
                      <a :href="VUE_MOGU_WEB + '/about'" target="_blank" style="cursor: pointer; color: #555;">关于我</a>
                      页面，回复了你的评论
                    </span>

                    <span class="timeAgo" v-if="item.businessType == 12">
                      评论了你的
                      <a :href="VUE_MOGU_WEB + '/moment?uid=' + item.comment.blogUid" target="_blank" style="cursor: pointer;">动态</a>
                    </span>

                    <span class="timeAgo" v-else-if="item.businessType == 13">
                      在
                      <a :href="VUE_MOGU_WEB + '/moment?uid=' + item.comment.blogUid " target="_blank" style="cursor: pointer; color: #555;">动态</a>
                      中，回复了你的评论
                    </span>

                    <span class="timeAgo" v-else-if="item.businessType == 15">
                      在
                      <a :href="VUE_MOGU_WEB + '/video?mediaInfoUid=' + item.comment.blogUid " target="_blank" style="cursor: pointer; color: #555;">课程</a>
                      中，回复了你的评论
                    </span>

                    <span class="timeAgo" v-else-if="item.businessType == 25">
                      在问题
                      <a :href="VUE_MOGU_WEB + '/cInfo/'+item.comment.problem.oid" target="_blank" style="cursor: pointer; color: #555;">{{splitStr(item.comment.problem.title, splitCount)}}</a>
                      中，回复了你的评论
                    </span>

                    <span class="timeAgo" v-if="item.businessType == 26">
                      评论了你的问题:
                      <a :href="VUE_MOGU_WEB + '/cInfo/'+item.comment.problem.oid" target="_blank" style="cursor: pointer; color: #555;">{{splitStr(item.comment.problem.title, splitCount)}}</a>
                    </span>


                    <i class="el-icon-delete deleteStyle" @click="handleDelete(item)"></i>

                  </div>

                  <div @click="goToCommentInfo(item)" class="rightCenter ck-content" style="color: #0E2231; cursor:pointer;" v-highlight v-html="$xss(item.comment.content, options)"></div>

                  <div style="margin-bottom: 5px; text-align: right;">
                    <el-link class="b1" style="color: #60676a;" :underline="false" @click="replyTo(item.comment)">快捷回复</el-link>
                  </div>

                  <CommentBox class="comment" :userInfo="userInfo" :id="item.comment.uid" :toInfo="toInfo"  :commentInfo="commentInfo"
                              @submit-box="submitBox" @cancel-box="cancelBox"></CommentBox>

                </span>
              </div>
              <span v-if="notices.length == 0">
                <el-empty description="空空如也" image=""></el-empty>
              </span>
          </el-tab-pane>


          <el-tab-pane class="tabPane" name="2">
            <span slot="label" class="tabItem"><i class="el-icon-view"></i> 关注</span>

            <el-row style="height: 30px; border-bottom: 1px solid #e0e0e0;">
              <el-col :span="12">
                <span style="font-size: 16px; font-weight: 600; color: #222226;">关注</span>
              </el-col>
              <el-col :span="12" style="text-align: right;" >
                  <span style="font-size: 16px; cursor: pointer" @click="handleDeleteBatch">
                    <i class="el-icon-view"></i> 清空所有消息
                  </span>
              </el-col>
            </el-row>

            <div class="commentList" v-for="item in notices" v-if="noticeType == '2'" :key="item.uid" >
              <span class="left p1" v-if="item.userWatch && item.userWatch.user">
                <el-link :href="VUE_MOGU_WEB + '/userCenter?userUid='+item.userWatch.user.uid" :underline="false">
                  <img v-if="item.userWatch.user" style="cursor:pointer;"  :src="item.userWatch.user.photoUrl ? item.userWatch.user.photoUrl:defaultAvatar" onerror="onerror=null;src=defaultAvatar" />
                  <img v-else :src="defaultAvatar" />
                </el-link>
              </span>

              <span class="right p1" v-if="item.userWatch && item.userWatch.user">
                <div class="rightTop" v-if="item.userWatch.user">
                  <el-link class="userName" :href="VUE_MOGU_WEB + '/userCenter?userUid='+item.userWatch.user.uid" :underline="false">{{item.userWatch.user.nickName}}</el-link>

                  <el-tag size="mini" effect="plain" style="margin-left:5px;" v-for="userTag in userTagDictList" :key="userTag.uid" v-if="item.userWatch.user.userTag == userTag.dictValue && item.userWatch.user.userTag != 0" :type="userTag.listClass">{{userTag.dictLabel}}</el-tag>

                  <span class="timeAgo" >
                     <span class="lv" style="background-color: rgba(255, 255, 255, 0.9);" :class="'lv'+ item.userWatch.user.userLevel" v-for="userLevel in userLevelDictList" :key="userLevel.uid" v-if="item.userWatch.user.userLevel == userLevel.dictValue">
                        Lv{{item.userWatch.user.userLevel}}
                     </span>
                  </span>

                  <span class="iconfont" v-if="item.userWatch.user.gender=='1'" style="margin-left: 5px; color: dodgerblue;  font-size: 18px; font-weight: bold;">&#xe641;</span>
                  <span class="iconfont" v-if="item.userWatch.user.gender=='2'" style="margin-left: 5px; color: palevioletred;  font-size: 18px; font-weight: bold;">&#xe643;</span>
                  <span class="timeAgo" style="color: #8a919f;" v-if="item.userWatch.user.userIpPossession">IP属地:{{item.userWatch.user.userIpPossession}}</span>
                  <span class="timeAgo" v-if="item.createTime">{{timeAgo(item.createTime)}}</span>
                  <span class="timeAgo" v-else>刚刚</span>

                  <i class="el-icon-delete deleteStyle" @click="handleDelete(item)"></i>

                </div>

                <div class="rightCenter">
                    关注了你
                </div>
              </span>
            </div>
            <span v-if="notices.length == 0">
                  <el-empty description="空空如也" image=""></el-empty>
            </span>
          </el-tab-pane>

          <el-tab-pane class="tabPane" name="3">
            <span slot="label" class="tabItem"><i class="el-icon-position"></i> 点赞</span>
            <el-row style="height: 30px; border-bottom: 1px solid #e0e0e0;">
              <el-col :span="12">
                <span style="font-size: 16px; font-weight: 600; color: #222226;">点赞</span>
              </el-col>
              <el-col :span="12" style="text-align: right;" >
                  <span style="font-size: 16px; cursor: pointer" @click="handleDeleteBatch">
                    <i class="el-icon-view"></i> 清空所有消息
                  </span>
              </el-col>
            </el-row>

            <div class="commentList" v-for="item in notices" v-if="noticeType == '3'" :key="item.uid" >

              <span>
                <span class="left p1" v-if="item.fromUser">
                <el-link :href="VUE_MOGU_WEB + '/userCenter?userUid='+item.fromUser.uid" :underline="false">
                  <img v-if="item.fromUser" style="cursor:pointer;"  :src="item.fromUser.photoUrl ? item.fromUser.photoUrl:defaultAvatar" onerror="onerror=null;src=defaultAvatar" />
                  <img v-else :src="defaultAvatar" />
                </el-link>
              </span>
                <span class="right p1" v-if="item.fromUser">
                <div class="rightTop" v-if="item.fromUser">
                  <el-link class="userName" :href="VUE_MOGU_WEB + '/userCenter?userUid='+item.fromUser.uid" :underline="false">{{item.fromUser.nickName}}</el-link>

                  <el-tag size="mini" effect="plain" style="margin-left:5px;" v-for="userTag in userTagDictList" :key="userTag.uid" v-if="item.fromUser.userTag == userTag.dictValue && item.fromUser.userTag != 0" :type="userTag.listClass">{{userTag.dictLabel}}</el-tag>

                  <span class="timeAgo" >
                    <span class="lv" style="background-color: rgba(255, 255, 255, 0.9);" :class="'lv'+ item.fromUser.userLevel" v-for="userLevel in userLevelDictList" :key="userLevel.uid" v-if="item.fromUser.userLevel == userLevel.dictValue">
                        Lv{{item.fromUser.userLevel}}
                     </span>
                  </span>

                  <span class="iconfont" v-if="item.fromUser.gender=='1'" style="margin-left: 5px; color: dodgerblue;  font-size: 18px; font-weight: bold;">&#xe641;</span>
                  <span class="iconfont" v-if="item.fromUser.gender=='2'" style="margin-left: 5px; color: palevioletred;  font-size: 18px; font-weight: bold;">&#xe643;</span>
                  <span class="timeAgo" style="color: #8a919f;" v-if="item.fromUser.userIpPossession">IP属地:{{item.fromUser.userIpPossession}}</span>
                  <span class="timeAgo" v-if="item.createTime">{{timeAgo(item.createTime)}}</span>
                  <span class="timeAgo" v-else>刚刚</span>
                  <i class="el-icon-delete deleteStyle" @click="handleDelete(item)"></i>
                </div>

                <div class="rightCenter" v-if="item.businessType == 6">
                    给文章
                    <a :href="VUE_MOGU_WEB + '/info/'+item.blog.oid" target="_blank" style="cursor: pointer; color: #555;">{{splitStr(item.blog.title, splitCount)}}</a>
                    点赞
                </div>

                <div class="rightCenter" v-if="item.businessType == 7">
                    给问答
                    <a :href="VUE_MOGU_WEB + '/qInfo/'+item.question.oid" target="_blank" style="cursor: pointer; color: #555;">{{splitStr(item.question.title, splitCount)}}</a>
                    点赞
                </div>

                <div class="rightCenter" v-if="item.businessType == 14">
                    给动态
                    <a :href="VUE_MOGU_WEB + '/moment?uid='+item.userMoment.uid" target="_blank" style="cursor: pointer; color: #555; display: inline-block;" v-html="$xss(item.userMoment.content, options)"></a>
                    点赞
                </div>

                <div class="rightCenter" v-if="item.businessType == 17">
                    给评论
                    <a  target="_blank" style="cursor: pointer; color: #555; display: inline-block;" v-html="$xss(item.comment.content, options)"></a>
                    点赞
                </div>

                <div class="rightCenter" v-if="item.businessType == 24">
                    给问题
                    <a :href="VUE_MOGU_WEB + '/cInfo/'+ item.problem.oid" target="_blank" style="cursor: pointer; color: #555; display: inline-block;" v-html="$xss(item.problem.title, options)"></a>
                    点赞
                </div>

              </span>
              </span>
            </div>

            <span v-if="notices.length == 0">
                  <el-empty description="空空如也" image=""></el-empty>
            </span>
          </el-tab-pane>

          <el-tab-pane class="tabPane" name="6">
            <span slot="label" class="tabItem"><i class="el-icon-collection"></i> 收藏</span>
            <el-row style="height: 30px; border-bottom: 1px solid #e0e0e0;">
              <el-col :span="12">
                <span style="font-size: 16px; font-weight: 600; color: #222226;">收藏</span>
              </el-col>
              <el-col :span="12" style="text-align: right;" >
                  <span style="font-size: 16px; cursor: pointer" @click="handleDeleteBatch">
                    <i class="el-icon-view"></i> 清空所有消息
                  </span>
              </el-col>
            </el-row>

            <div class="commentList" v-for="item in notices" v-if="noticeType == '6'" :key="item.uid" >
              <span>
                <span class="left p1" v-if="item.fromUser">
                <el-link :href="VUE_MOGU_WEB + '/userCenter?userUid='+item.fromUser.uid" :underline="false">
                  <img v-if="item.fromUser" style="cursor:pointer;"  :src="item.fromUser.photoUrl ? item.fromUser.photoUrl:defaultAvatar" onerror="onerror=null;src=defaultAvatar" />
                  <img v-else :src="defaultAvatar" />
                </el-link>
              </span>
                <span class="right p1" v-if="item.fromUser">
                <div class="rightTop" v-if="item.fromUser">
                  <el-link class="userName" :href="VUE_MOGU_WEB + '/userCenter?userUid='+item.fromUser.uid" :underline="false">{{item.fromUser.nickName}}</el-link>

                  <el-tag size="mini" effect="plain" style="margin-left:5px;" v-for="userTag in userTagDictList" :key="userTag.uid" v-if="item.fromUser.userTag == userTag.dictValue && item.fromUser.userTag != 0" :type="userTag.listClass">{{userTag.dictLabel}}</el-tag>

                  <span class="timeAgo" >
                    <span class="lv" style="background-color: rgba(255, 255, 255, 0.9);" :class="'lv'+ item.fromUser.userLevel" v-for="userLevel in userLevelDictList" :key="userLevel.uid" v-if="item.fromUser.userLevel == userLevel.dictValue">
                        Lv{{item.fromUser.userLevel}}
                     </span>
                  </span>

                  <span class="iconfont" v-if="item.fromUser.gender=='1'" style="margin-left: 5px; color: dodgerblue;  font-size: 18px; font-weight: bold;">&#xe641;</span>
                  <span class="iconfont" v-if="item.fromUser.gender=='2'" style="margin-left: 5px; color: palevioletred;  font-size: 18px; font-weight: bold;">&#xe643;</span>
                  <span class="timeAgo" style="color: #8a919f;" v-if="item.fromUser.userIpPossession">IP属地:{{item.fromUser.userIpPossession}}</span>
                  <span class="timeAgo" v-if="item.createTime">{{timeAgo(item.createTime)}}</span>
                  <span class="timeAgo" v-else>刚刚</span>
                  <i class="el-icon-delete deleteStyle" @click="handleDelete(item)"></i>
                </div>

                <div class="rightCenter" v-if="item.businessType == 27 && item.blog">
                    收藏了文章&nbsp;
                    <a :href="VUE_MOGU_WEB + '/info/'+item.blog.oid" target="_blank" style="cursor: pointer; color: #555;">{{splitStr(item.blog.title, splitCount)}}</a>

                </div>

                <div class="rightCenter" v-if="item.businessType == 28 && item.question">
                    收藏了问答&nbsp;
                    <a :href="VUE_MOGU_WEB + '/qInfo/'+item.question.oid" target="_blank" style="cursor: pointer; color: #555;">{{splitStr(item.question.title, splitCount)}}</a>
                </div>

                <div class="rightCenter" v-if="item.businessType == 29 && item.problem">
                    收藏了面经&nbsp;
                    <a :href="VUE_MOGU_WEB + '/cInfo/'+ item.problem.oid" target="_blank" style="cursor: pointer; color: #555; display: inline-block;" v-html="$xss(item.problem.title, options)"></a>
                </div>

                <div class="rightCenter" v-if="item.businessType == 30 && item.comment">
                    收藏了评论&nbsp;
                    <a  style="cursor: pointer; color: #555; display: inline-block;" v-html="$xss(item.comment.content, options)"></a>
                </div>

                <div class="rightCenter" v-if="item.businessType == 31">
                    收藏了动态&nbsp;
                    <a :href="VUE_MOGU_WEB + '/moment?uid='+item.userMoment.uid" target="_blank" style="cursor: pointer; color: #555; display: inline-block;" v-html="$xss(item.userMoment.content, options)"></a>
                </div>

<!--                <div class="rightCenter" v-if="item.businessType == 32">-->
<!--                    收藏了课程&nbsp;-->
<!--                    <a  target="_blank" style="cursor: pointer; color: #555; display: inline-block;" v-html="$xss(item.comment.content, options)"></a>-->
<!--                </div>-->

              </span>
              </span>
            </div>

            <span v-if="notices.length == 0">
                  <el-empty description="空空如也" image=""></el-empty>
            </span>
          </el-tab-pane>

          <el-tab-pane class="tabPane" name="4">
            <span slot="label" class="tabItem"><i class="el-icon-open"></i> 系统</span>

            <el-row style="height: 30px; border-bottom: 1px solid #e0e0e0;">
              <el-col :span="12">
                <span style="font-size: 16px; font-weight: 600; color: #222226;">系统</span>
              </el-col>
              <el-col :span="12" style="text-align: right;" >
                  <span style="font-size: 16px; cursor: pointer" @click="handleDeleteBatch">
                    <i class="el-icon-view"></i> 清空所有消息
                  </span>
              </el-col>
            </el-row>

            <div class="commentList" v-for="item in notices" v-if="noticeType == '4'" :key="item.uid" >
              <span class="left p1">
               <img :src="defaultAvatar" />
              </span>

              <span class="right p1">
                <div class="rightTop">
                  <el-link class="userName" :underline="false">系统</el-link>
                  <span class="timeAgo" v-if="item.createTime">{{timeAgo(item.createTime)}}</span>
                  <span class="timeAgo" v-else>刚刚</span>
                  <i class="el-icon-delete deleteStyle" @click="handleDelete(item)"></i>
                </div>

                <div class="rightCenter">
                    <span v-if="item.businessType == 8">
                      <span v-if="item.blog">
                        你关注的
                        <a v-if="item.blog.articleSource == 1 && item.blog.user" :href="VUE_MOGU_WEB + '/userCenter?userUid='+item.blog.userUid" target="_blank" style="cursor: pointer; color: #6467a3;">{{splitStr(item.blog.user.nickName, splitCount)}}</a>
                        发表了新的博客
                        <a :href="VUE_MOGU_WEB + '/info/'+item.blog.oid" target="_blank" style="cursor: pointer; color: #6467a3;">{{splitStr(item.blog.title, splitCount)}}</a>
                      </span>
                    </span>

                    <span v-else-if="item.businessType == 9">
                      <span v-if="item.question">
                        你关注的
                        <a :href="VUE_MOGU_WEB + '/userCenter?userUid='+item.question.userUid" target="_blank" style="cursor: pointer; color: #6467a3;">{{splitStr(item.question.user.nickName, splitCount)}}</a>
                        发表了新的博客
                        <a :href="VUE_MOGU_WEB + '/qInfo/'+item.question.oid + '?title=问答'" target="_blank" style="cursor: pointer; color: #6467a3;">{{splitStr(item.question.title, splitCount)}}</a>
                      </span>
                    </span>

                    <span v-else-if="item.businessType == 22">
                      <span>
                        <span style="float: left; margin-right: 5px;">
                        你关注的
                        <a :href="VUE_MOGU_WEB + '/userCenter?userUid='+item.userMoment.user.uid" target="_blank" style="cursor: pointer; color: #6467a3;">{{splitStr(item.userMoment.user.nickName, splitCount)}}</a>
                        发表了新的动态
                        </span>
                        <a :href="VUE_MOGU_WEB + '/moment?uid='+item.userMoment.uid" target="_blank" style="cursor: pointer; color: #6467a3; display: inline-block;" v-html="$xss(item.userMoment.content, options)"></a>
                      </span>
                    </span>

                    <span v-else-if="item.businessType === 60">
                      <span>
                        <span style="float: left; margin-right: 5px;">在 <a :href="VUE_MOGU_WEB + '/info/'+item.businessUid" target="_blank" style="cursor: pointer; color: #6467a3;">文章</a>&nbsp;中提及到你</span>
                        <span v-html="$xss(item.content, options)">{{item.content}}</span>
                      </span>
                    </span>

                    <span v-else-if="item.businessType === 61">
                      <span>
                        <span style="float: left; margin-right: 5px;">在&nbsp;问答&nbsp;中提及到你</span>
                        <span v-html="$xss(item.content, options)">{{item.content}}</span>
                      </span>
                    </span>

                    <span v-else-if="item.businessType === 62">
                      <span>
                        <span style="float: left; margin-right: 5px;">在&nbsp;面经&nbsp;中提及到你</span>
                        <span v-html="$xss(item.content, options)">{{item.content}}</span>
                      </span>
                    </span>

                    <span v-else-if="item.businessType === 63">
                      <span>
                        <span style="float: left; margin-right: 5px;">在&nbsp;评论&nbsp;中提及到你</span>
                        <span v-html="$xss(item.content, options)">{{item.content}}</span>
                      </span>
                    </span>

                    <span v-else-if="item.businessType === 64">
                      <span>
                        <span style="float: left; margin-right: 5px;">在 <a :href="VUE_MOGU_WEB + '/moment?uid='+item.businessUid" target="_blank" style="cursor: pointer; color: #6467a3;">动态</a>&nbsp;中提及到你</span>
                        <span v-html="$xss(item.content, options)">{{item.content}}</span>
                      </span>
                    </span>

                    <span v-else-if="item.businessType === 65">
                      <span>
                        <span style="float: left; margin-right: 5px;">在 <a :href="VUE_MOGU_WEB + '/chat'" target="_blank" style="cursor: pointer; color: #6467a3;">聊天</a>&nbsp;中提及到你</span>
                        <span v-html="$xss(item.content, options)">{{item.content}}</span>
                      </span>
                    </span>

                    <span v-else v-html="$xss(item.content, options)">
                      {{item.content}}
                    </span>
                </div>
              </span>
            </div>
            <span v-if="notices.length == 0">
                  <el-empty description="空空如也" image=""></el-empty>
            </span>

          </el-tab-pane>



<!--          <el-tab-pane class="tabPane" name="5">-->
<!--            <span slot="label" class="tabItem"><i class="el-icon-setting"></i> 消息设置</span>-->
<!--          </el-tab-pane>-->

        </el-tabs>

        <!--分页-->
       <el-row style="text-align: center;" v-if="noticeType != 5 && total > pageSize">
         <el-pagination
           @current-change="handleCurrentChange"
           :current-page.sync="currentPage"
           :page-size="pageSize"
           layout="total, prev, pager, next, jumper"
           :total="total"
         ></el-pagination>
       </el-row>

      </div>
    </div>
  </div>
</template>

<script>
import {getNoticeList, deleteNotice, deleteBatchNotice} from "../api/notice";
import {timeAgo} from "../utils/webUtils";
import CommentBox from "@/components/CommentBox";
import {addComment} from "../api/comment";
import {getListByDictTypeList} from "@/api/sysDictData"
export default {
  name: "notice",
  data() {
    return {
      splitCount: 40, // 切割的数
      currentPage: 1,
      pageSize: 8,
      total: 0, //总数量
      tabPosition: 'left',
      VUE_MOGU_WEB: process.env.VUE_MOGU_WEB,
      defaultAvatar: process.env.defaultAvatar,
      // xss白名单配置
      options : {
        whiteList: {
          a: ['href', 'title', 'target', 'style', 'class'],
          span: ['class', 'style'],
          h1: ['class'],
          h2: ['class'],
          h3: ['class'],
          h4: ['class'],
          ol: ['start'],
          pre: [],
          code: ['class'],
          p: ['class'],
          blockquote: ['class'],
          ul: ['class'],
          li: ['class'],
          img: ['class', 'src', 'alt', 'style'],
          // iframe: ['class', 'src', 'style', 'scrolling', 'border', 'frameborder', 'allowfullscreen'],
          hr: [],
          br: [],
          div: [],
          video: ['class', 'src', 'controls'],
          source: ['src', 'type']
        },
      },
      userTagDictList: [],
      userLevelDictList: [], //用户等级字典
      noticeType: "",
      notices: [],
      toInfo: {
        uid: "",
        commentUid: ""
      },
      userInfo: {},
      commentInfo: {},
    };
  },
  components: {
    //注册组件
    CommentBox
  },
  created() {
    this.noticeType = this.$route.query.noticeType
    if(this.noticeType == undefined) {
      this.$message.error("传递通知类型有误")
      return
    }
    this.getDictList()
    this.noticeList()
  },
  watch: {
    $route(to, from) {
      this.noticeList()
    },
    '$store.state.app.noticeType': function (newFlag, oldFlag) {
      this.noticeType = newFlag
      this.noticeList()
    }
  },
  methods: {
    /**
     * 字典查询
     */
    getDictList: function () {
      let dictTypeList =  ['sys_user_tag', 'sys_user_level']
      getListByDictTypeList(dictTypeList).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let dictMap = response.data;
          this.userTagDictList = dictMap.sys_user_tag.list
          this.userLevelDictList = dictMap.sys_user_level.list
        }
      });
    },
    noticeList: function () {
      let params = {}
      params.pageSize = this.pageSize
      params.currentPage = this.currentPage
      params.noticeType = this.noticeType
      getNoticeList(params).then(response => {
        console.log("获取问答列表", response)
        this.notices = response.data.records
        this.pageSize = response.data.size
        this.currentPage = response.data.current
        this.total = response.data.total
      })
    },
    // 校验是否登录
    validLogin() {
      let userInfo = this.$store.state.user.userInfo
      if(userInfo.userName == undefined) {
        return false;
      } else {
        return true;
      }
    },
    replyTo: function (item) {
      console.log("开始回复", item)
      if(!this.validLogin()) {
        this.$notify.error({
          title: '错误',
          message: "登录后才能回复评论哦~",
          offset: 100
        });
        return
      }

      this.showComment = true;
      let userUid = item.userUid;
      let commentUid = item.uid;
      let lists = document.getElementsByClassName("comment");
      for (let i = 0; i < lists.length; i++) {
        lists[i].style.display = 'none';
      }
      document.getElementById(commentUid).style.display = 'block';
      this.toInfo.commentUid = commentUid
      this.toInfo.uid = userUid
      this.commentInfo.source = item.source
      this.commentInfo.blogUid = item.blogUid
    },
    submitBox(e) {
      console.log("添加内容", e)
      let params = {};
      params.userUid = e.userUid;
      params.content = e.content;
      params.blogUid = e.blogUid;
      params.toUid = e.toCommentUid;
      params.toUserUid = e.toUserUid;
      params.source = e.source
      addComment(params).then(response => {
          if (response.code == this.$ECode.SUCCESS) {
            this.$notify({
              title: '成功',
              message: "评论成功",
              type: 'success',
              offset: 100
            });
            let lists = document.getElementsByClassName("comment");
            for (let i = 0; i < lists.length; i++) {
              lists[i].style.display = 'none';
            }

          } else {
            this.$notify.error({
              title: '错误',
              message: "评论失败",
              type: 'success',
              offset: 100
            });
          }
        }
      )
    },
    cancelBox(toCommentUid) {
      console.log("toCommentUid", toCommentUid)
      document.getElementById(toCommentUid).style.display = 'none'
    },
    handleDelete: function (notice) {
      console.log("删除通知", notice)

      this.$confirm("此操作将把通知删除, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          let params = {};
          params.uid = notice.uid;
          deleteNotice(params).then(response => {
            if(response.code == this.$ECode.SUCCESS) {
              this.$message.success(response.message)
              this.noticeList()
            } else {
              this.$message.error(response.message)
            }
          });
        })
        .catch(() => {
          this.$commonUtil.message.info("已取消删除")
        });
    },
    goToCommentInfo: function (notice) {
      console.log("开始跳转", notice)
      let comment = notice.comment
      let businessType = notice.businessType
      switch (businessType) {
        case 1: {
          // 评论了你的文章
          window.open( this.VUE_MOGU_WEB + '/info/' + comment.blog.oid);
        }break;
        case 2: {
          // 评论了你的问答
          window.open( this.VUE_MOGU_WEB + '/qInfo/' + comment.question.oid);
        }break;
        case 3: {
          // 评论了你的评论
          window.open( this.VUE_MOGU_WEB + '/info/' + comment.blog.oid);
        }break;
        case 4: {
          //问答回复
          window.open( this.VUE_MOGU_WEB + '/qInfo/' + comment.question.oid);
        }break;

        case 10: {
          // 留言板回复
          window.open( this.VUE_MOGU_WEB + '/messageBoard');
        }break;
        case 11: {
          // 关于我的页面回复
          window.open( this.VUE_MOGU_WEB + '/about')
        }break;
        case 12: {
          // 动态评论
          window.open( this.VUE_MOGU_WEB + '/moment?uid=' + comment.blogUid);
        }break;
        case 13: {
          //动态回复
          window.open( this.VUE_MOGU_WEB + '/moment?uid=' + comment.blogUid);
        }break;
      }
    },
    splitStr: (str, flagCount) => {
      if (str == null || str == '') {
        return ""
      } else if(str.length > flagCount) {
        return str.substring(0, flagCount) + "..."
      } else {
        return str
      }
    },
    handleDeleteBatch: function (notice) {
      console.log("删除全部通知", notice)

      this.$confirm("此操作将把全部通知删除, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          let noticeList = this.notices
          if(noticeList.length == 0) {
            this.$message.error("暂无通知")
            return
          }
          let params = []
          for (let a=0; a<noticeList.length; a++) {
            let temp = {};
            temp.uid = noticeList[a].uid;
            params.push(temp)
          }
          deleteBatchNotice(params).then(response => {
            if(response.code == this.$ECode.SUCCESS) {
              this.$message.success(response.message)
              this.noticeList()
            } else {
              this.$message.error(response.message)
            }
          });
        })
        .catch(() => {
          this.$commonUtil.message.info("已取消删除")
        });
    },
    /**
     * dateTimeStamp是一个时间毫秒，注意时间戳是秒的形式，在这个毫秒的基础上除以1000，就是十位数的时间戳。13位数的都是时间毫秒。
     * @param dateTimeStamp
     * @returns {string}
     */
    timeAgo(dateTimeStamp) {
      return timeAgo(dateTimeStamp)
    },
    handleClick(tab, event) {
      console.log(tab.name)
      this.currentPage = 1
      switch (tab.name) {
        case "1": {
          this.noticeType = "1"
        } break;
        case "2": {
          this.noticeType = "2"
        } break;
        case "3": {
          this.noticeType = "3"
        } break;
        case "4": {
          this.noticeType = "4"
        } break;
        case "5": {
          this.noticeType = "5"
        } break;
        case "6": {
          this.noticeType = "6"
        } break;
      }
      this.$router.push({ path: "/notice", query: { noticeType: this.noticeType } });
      // this.noticeList()
    },
    handleCurrentChange: function(val) {
      this.currentPage = val;
      this.noticeList();
    },
  }
};
</script>

<style>
  .tabItem {
    text-align: center;
  }
  .el-tabs--left .el-tabs__item.is-left {
    font-size: 16px;
    height: 50px;
    text-align: center;
  }
</style>

<style scoped>
.commentBox {
  width: 1100px;
}
.commentList {
  width: 100%;
  margin: 15px 0 0 10px;

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
  width: 100%;
  border-radius: 50%;
}
.commentList .right {
  display: inline-block;
  width: 93%;
  margin-left: 5px;
  margin-top: 10px;
  border-bottom: 1px solid #e0e0e0;
}
.commentList .rightTop {
  height: 30px;
  margin-top: 2px;
}
.commentList .rightTop .userName {
  color: #303133;
  margin-left: 10px;
  margin-top: -5px;
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
  margin-left: 10px;
}

.deleteStyle {
  cursor: pointer;
  float: right;
}
.deleteStyle:hover {
  color: #00AAAA;
}

.comment {
  display: none;
}

</style>
