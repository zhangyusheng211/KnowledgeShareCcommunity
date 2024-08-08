<template>
  <div>
    <!--  购物车标志  -->
    <div id="wechats" :class="testShopping.length > 0 ? 'wechat-list2': 'wechat-list' " @scroll="scrollChange">
<!--      <div v-if="testShopping.length > 0" id="chooseBtn" class="el-icon-shopping-cart-full" @click="openShopping"></div>-->
      <div id="chooseBtn" class="el-icon-shopping-cart-2 pointer" @click="openShopping">

      </div>
    </div>

    <el-image-viewer
      style="z-index: 2010"
      v-if="showViewer"
      :on-close="closeViewer"
      :url-list="picList" />
    <div class="pagebg sh"></div>
    <div class="container">
      <el-row>
        <el-col :xs="0" :sm="5" :md="5" :lg="5" :xl="5" >
          <div >
            <Sticky :sticky-top="10" >
              <el-menu :default-openeds="openeds"  :style="{'max-height': maxHeight + 'px'}" style="overflow-y: scroll; overflow-x: hidden" :default-active="defaultActive" class="el-menu-vertical-demo" @select="handleSelect" @open="handleOpen" @close="handleClose" :collapse="isCollapse">
              <span v-for="(tag, index) in allTagData" :key="tag.uid">
                <el-submenu v-if="tag.children" :index="tag.uid">
                  <template slot="title">
                    <i :class="tag.icon"></i>
                    <span slot="title">{{ tag.name }}</span>
                  </template>
                  <el-menu-item v-if="tag.children" v-for="child in tag.children" :key="child.uid" :index="child.uid">
                    <i :class="child.icon"></i>
                    <span slot="title">{{ child.name }}</span>
                  </el-menu-item>
                </el-submenu>

                <el-menu-item v-else  :index="tag.uid">
                    <i :class="tag.icon"></i>
                    <span slot="title">{{ tag.name }}</span>
                </el-menu-item>
              </span>
              </el-menu>
            </Sticky>
           </div>
        </el-col>

        <el-col :xs="24" :sm="19" :md="19" :lg="19" :xl="19">

          <el-card class="box-card">
            <el-form ref="form" label-width="80px" size="mini">

              <el-form-item :label-width="lineLabelWidth">
                <span slot="label"><i class="el-icon-collection-tag"></i> 标签</span>
                <el-cascader style="width:400px"
                             @change="handleChange(1)"
                             :options="allTagData"
                             v-model="searchParams.tagValue"
                             :show-all-levels="false"
                             :props="{ multiple: true, checkStrictly: false, expandTrigger:'hover', value: 'uid', label: 'name', expandTrigger: 'hover' }"
                             clearable filterable>
                  <template slot-scope="{ node, data }">
                    <span>{{ data.name }}</span>
                    <span v-if="!node.isLeaf"> ({{ data.children.length }}) </span>
                  </template>
                </el-cascader>

<!--                <el-cascader-->
<!--                  :options="allTagData"-->
<!--                  :props="{ multiple: true, checkStrictly: true, expandTrigger:'hover' }"-->
<!--                  collapse-tags-->
<!--                  clearable></el-cascader>-->

                <right-toolbar :showSearch.sync="showSearch" @queryTable="clear" style="float: right"></right-toolbar>

                <el-tooltip
                  effect="dark"
                  :disabled="authCode.ADD_PROBLEM"
                  content="权限不足，无法操作"
                  placement="top-end"
                >
                  <span style="float: right; margin-right: 10px">
                    <el-button type="success" icon="el-icon-upload"  @click="createProblem"  :disabled="!authCode.ADD_PROBLEM">发布面经</el-button>
                  </span>
                </el-tooltip>

              </el-form-item>

              <el-form-item v-if="showSearch" :label-width="lineLabelWidth">
                <span slot="label"><i class="el-icon-search"></i> 搜索</span>
                <el-input v-model="searchParams.keyword" placeholder="按题目名称搜索" style="width: 320px"@keyup.enter.native="handleChange(2)"></el-input>
                <el-button icon="el-icon-search" type="primary" @click="handleChange(2)" clearable style="margin-left: 5px">搜索</el-button>


              </el-form-item>

              <el-form-item  v-if="showSearch" :label-width="lineLabelWidth">
                <span slot="label"><i class="el-icon-pie-chart"></i> 题型</span>
                <el-radio-group v-model="searchParams.problemType" @change="handleChange(3)">
                  <el-radio-button  v-for="problemType in problemTypeDictList" :key="problemType.uid" :label="problemType.dictValue" border size="medium">{{problemType.dictLabel}}</el-radio-button>
                </el-radio-group>
              </el-form-item>

              <el-form-item  v-if="showSearch" :label-width="lineLabelWidth">
                <span slot="label"><i class="el-icon-data-analysis"></i> 难度</span>
                <el-radio-group v-model="searchParams.problemDifficulty" @change="handleChange(4)">
                  <el-radio-button  v-for="problemDifficulty in problemDifficultyDictList" :key="problemDifficulty.uid" :label="problemDifficulty.dictValue" border size="medium">{{problemDifficulty.dictLabel}}</el-radio-button>
                </el-radio-group>
              </el-form-item>

              <el-form-item  v-if="showSearch" :label-width="lineLabelWidth">
                <span slot="label"><i class="el-icon-open"></i> 模式</span>
                <el-checkbox @input="handleChange(5)" v-model="quickReply" label="快问快答" ></el-checkbox>
                <el-checkbox @input="handleChange(6)" v-model="searchParams.isSelection" label="精选" ></el-checkbox>
                <el-checkbox @input="handleChange(7)" v-model="searchParams.hasAnswer" label="有解" ></el-checkbox>
                <el-button type="warning" icon="el-icon-download" @click="createTest()" style="float: right;">导出试卷</el-button>
              </el-form-item>

            </el-form>
          </el-card>

          <div  style="min-height: 773px; margin-top: 10px" >
            <el-card class="box-card" :body-style="{ padding: '5px' }">
              <el-tabs v-model="activeName" @tab-click="handleClick">
                <el-tab-pane name="0">
                  <span :class="activeName==0?'tab-pane-active':'tab-pane'" slot="label" ><i class="el-icon-collection-tag"></i> <span>随机</span></span>
                </el-tab-pane>
                <el-tab-pane name="1">
                  <span :class="activeName==1?'tab-pane-active':'tab-pane'" slot="label" ><i class="el-icon-collection-tag"></i> <span>最新</span></span>
                </el-tab-pane>
                <el-tab-pane name="2">
                  <span :class="activeName==2?'tab-pane-active':'tab-pane'" slot="label"><i class="el-icon-tickets"></i> <span>最热</span></span>
                </el-tab-pane>
<!--                <el-tab-pane name="3">-->
<!--                  <span :class="activeName==3?'tab-pane-active':'tab-pane'" slot="label"><i class="el-icon-chat-dot-square"></i> <span>最多点赞</span></span>-->
<!--                </el-tab-pane>-->
<!--                <el-tab-pane name="4">-->
<!--                  <span :class="activeName==4?'tab-pane-active':'tab-pane'" slot="label"><i class="el-icon-chat-dot-square"></i> <span>最多收藏</span></span>-->
<!--                </el-tab-pane>-->
<!--                <el-tab-pane name="5">-->
<!--                  <span :class="activeName==5?'tab-pane-active':'tab-pane'" slot="label"><i class="el-icon-chat-dot-square"></i> <span>最多回复</span></span>-->
<!--                </el-tab-pane>-->
                <el-tab-pane name="6">
                  <span :class="activeName==6?'tab-pane-active':'tab-pane'" slot="label"><i class="el-icon-chat-dot-square"></i> <span>最多通过</span></span>
                </el-tab-pane>
                <el-tab-pane name="7">
                  <span :class="activeName==7?'tab-pane-active':'tab-pane'" slot="label"><i class="el-icon-chat-dot-square"></i> <span>最多未通过</span></span>
                </el-tab-pane>
<!--                <el-tab-pane name="8">-->
<!--                  <span :class="activeName==8?'tab-pane-active':'tab-pane'" slot="label"><i class="el-icon-chat-dot-square"></i> <span>最多出现</span></span>-->
<!--                </el-tab-pane>-->

              </el-tabs>
              <div
                v-for="(item, index) in newProblemData"
                :key="item.uid"
                class="blogs"
                style="padding-top: 1px; padding-bottom: 5px"
                data-scroll-reveal="enter bottom over 1s"
              >
                <el-row  :span="24" class="problemLine">

                  <el-col :xs="24" :sm="24" style="position: relative; ">
                    <div class="blogtitle" style="font-size: 16px">

                      <a v-if="!quickReply" :href=" VUE_MOGU_WEB + '/cInfo/' + item.oid +'?title=面经'" target="_blank">{{item.title}}</a>
                      <a v-else href="javascript:void(0);" @click="showQuickReplyVisible(item, index)">{{item.title}}</a>

                      <span style="float: right">
                        <el-tag class="pointer" @click="getToProblem(item, '1')" size="mini" v-if="item.isSelection == 1" effect="dark" type="warning">精选</el-tag>
                        <el-tag class="pointer" @click="getToProblem(item, '2')" size="mini" effect="plain" v-for="problemType in problemTypeDictList" :key="problemType.uid" v-if="item.problemType == problemType.dictValue" :type="problemType.listClass">{{problemType.dictLabel}}</el-tag>
                        <el-tag class="pointer" @click="getToProblem(item, '3')" size="mini" effect="plain" v-for="problemDifficulty in problemDifficultyDictList" :key="problemDifficulty.uid" v-if="item.problemDifficulty == problemDifficulty.dictValue" :type="problemDifficulty.listClass">{{problemDifficulty.dictLabel}}</el-tag>
                      </span>

                    </div>

                    <div v-if="item.isTop == 1"  style="position: absolute; right: -20px; top: -4px; font-size:15px;">
                      <el-tooltip class="item" effect="dark" content="置顶" placement="top">
                        <img style="width: 30px; height: 30px" src="../assets/img/top.png" />
                      </el-tooltip>
                    </div>


                    <div>
                      <span v-for="(problemTag, index) in item.problemTagList">
                        <el-tag class="pointer" size="mini" effect="plain" @click="getToProblem(problemTag.uid, '4')"  style="margin-right: 3px" v-if="index%3==0" type="primary">{{problemTag.name}}</el-tag>
                        <el-tag class="pointer" size="mini" effect="plain" @click="getToProblem(problemTag.uid, '4')" style="margin-right: 3px" v-if="index%3==1" type="danger">{{problemTag.name}}</el-tag>
                        <el-tag class="pointer" size="mini" effect="plain" @click="getToProblem(problemTag.uid, '4')" style="margin-right: 3px" v-if="index%3==2" type="info">{{problemTag.name}}</el-tag>
                      </span>
                    </div>

                    <div class="bloginfo" v-if="item.user">
                      <ul>
                        <div @mouseover="onEnterTd(item.user,index)" @mouseleave="onLeaveTd(false)">
                          <li style=" padding-right: 6px" @click="getUserCenter(item)">
                            <span :id="getTabIndex(index)" :class="item.user.userTag > 0 ?'vip-avatar':''">
                              <el-avatar :class="item.user.userTag > 0 ?'vip-color':''" size="medium" v-if="item.user.photoUrl" :src="item.user.photoUrl"></el-avatar>
                              <el-avatar size="small" v-else src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"></el-avatar>
                              <span style="font-size: 10px; right: -4px; bottom: -1px;" class="vip-text pointer" v-if="item.user.userTag > 0">v</span>
                            </span>
                          </li>
                          <li class="author"  style="margin-top: 9px; margin-left: 3px;" @click="getUserCenter(item)">
                            <span class="pointer" :class="'lv'+ item.user.userLevel">{{item.user.nickName}}</span>
                          </li>
                        </div>


                        <li class="lmname" style="margin-top: 8px" v-if="item.blogSort">
                          <span class="iconfont">&#xe603;</span>
                          <a
                            href="javascript:void(0);"
                            @click="goToList(item.blogSort.uid)"
                          >{{item.blogSort.sortName}}</a>
                        </li>

                        <li class="view" style="margin-top: 8px">
                          <span class="iconfont">&#xe8c7;</span>
                          <span>{{item.clickCount}}</span>
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
                          {{timeAgo(item.createTime)}}
                        </li>
                      </ul>

                      <!-- 加入试卷  -->
                      <span v-if="item.flg">
                        <el-button type="primary" size="mini" plain @click="addTest(item)" style="float: right;">加入试卷</el-button>
                      </span>
                      <span v-if="!item.flg">
                        <el-button type="info" size="mini" plain @click="removeTest(item)" style="float: right;">取消添加</el-button>
                      </span>
                    </div>
                  </el-col>
                </el-row>

              </div>

              <el-empty v-if="!loading && newProblemData.length == 0" description="空空如也"></el-empty>
              <div v-else class="isEnd" style="margin-top: 5px">
                <span>
                  <div class="loadContent" @click="loadContent" v-if="!isEnd&&!loading">查看更多</div>
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
            </el-card>
          </div>

        </el-col>
      </el-row>
    </div>

    <el-dialog
      style="z-index: 1999;"
      v-dialogDrag
      :visible.sync="quickReplyVisible"
      :width="drawerSize"
      top="20px"
      center>

      <span slot="title">
        {{selectProblem.title}}
      </span>

      <div>
        <el-tag size="mini" v-if="selectProblem.isSelection == 1" effect="dark" type="warning">精选</el-tag>
        <el-tag size="mini" effect="plain" v-for="problemType in problemTypeDictList" :key="problemType.uid" v-if="selectProblem.problemType == problemType.dictValue" :type="problemType.listClass">{{problemType.dictLabel}}</el-tag>
        <el-tag size="mini" effect="plain" v-for="problemDifficulty in problemDifficultyDictList" :key="problemDifficulty.uid" v-if="selectProblem.problemDifficulty == problemDifficulty.dictValue" :type="problemDifficulty.listClass">{{problemDifficulty.dictLabel}}</el-tag>

        <span v-for="(problemTag, index) in selectProblem.problemTagList">
          <el-tag size="mini" effect="plain"  style="margin-right: 3px" v-if="index%3==0" type="primary">{{problemTag.name}}</el-tag>
          <el-tag size="mini" effect="plain"  style="margin-right: 3px" v-if="index%3==1" type="danger">{{problemTag.name}}</el-tag>
          <el-tag size="mini" effect="plain"  style="margin-right: 3px" v-if="index%3==2" type="info">{{problemTag.name}}</el-tag>
        </span>

        <span style="display: inline-flex;" >

          <li style="padding-right: 10px;">
            <span class="iconfont">&#xe8c7;</span>
            <span>{{selectProblem.clickCount}}</span>
          </li>

          <li style="padding-right: 10px;">
            <Collection :bizUid="selectProblem.uid" :collectType="'6'"></Collection>
          </li>

          <li style="padding-right: 10px;">
            <Praise :bizUid="selectProblem.uid" :resourceType="'6'"></Praise>
          </li>

          <li style="padding-right: 10px;" v-if="selectProblem.createTime">
            <i class="el-icon-time"></i>
            <span>{{timeAgo(selectProblem.createTime)}}</span>
          </li>
        </span>
      </div>

      <div class="tip-primary">
        <strong>描述</strong>
        <span class="ck-content"  v-highlight v-html="selectProblem.content">
            {{selectProblem.content}}
          </span>
      </div>

      <div class="tip-success" style="max-height: 560px; overflow-y: scroll" v-if="selectProblem.hasAnswer == '1' && showAnswer" @click="imageChange">
        <strong>解析</strong>
        <span class="ck-content"  v-highlight v-if="selectProblem.hasAnswer" v-html="selectProblem.answer">
            {{selectProblem.answer}}
        </span>
        <span v-else v-html="selectProblem.answer">
            暂无解析
        </span>
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button type="default" style="margin-top: 3px" @click="preProblem">上一题</el-button>
        <el-button  type="success" style="margin-top: 3px" @click="handleProblemDegree('3')">已掌握</el-button>
        <el-button  type="danger" style="margin-top: 3px" @click="handleProblemDegree('1')">未掌握</el-button>
        <el-button v-if="showAnswer && selectProblem.hasAnswer=='1'" style="margin-top: 3px" type="info" @click="handleShowAnswer(selectProblem)">隐藏解析</el-button>
        <el-button v-else type="success" style="margin-top: 3px" @click="handleShowAnswer(selectProblem)">显示解析</el-button>
        <el-button type="primary" style="margin-top: 3px" @click="nextProblem">下一题</el-button>
    </span>
    </el-dialog>



    <!--  添加試卷對話框  -->
    <el-dialog
      :title="title"
      :visible.sync="dialogFormVisible"
      :before-close="beforeClose"
      fullscreen
    >

      <el-steps :active="active">
        <el-step title="试卷信息" icon="el-icon-edit"></el-step>
        <el-step title="浏览试卷" icon="el-icon-reading"></el-step>
        <el-step title="导出" icon="el-icon-present"></el-step>
      </el-steps>

      <br>
      <br>
      <br>

      <el-form :model="examForm" :rules="rules" ref="examForm">
        <el-row>
          <el-col :span="16">
            <el-form-item label="名称" :label-width="formLabelWidth" prop="title">
              <el-input v-model="examForm.title" auto-complete="off" @input="contentChange"></el-input>
            </el-form-item>

            <el-form-item label="描述" :label-width="formLabelWidth">
              <el-input v-model="examForm.description" auto-complete="off" @input="contentChange"></el-input>
            </el-form-item>
          </el-col>

        </el-row>

        <el-row>
          <el-col :lg="5" :sm="8" :xs="8">
            <el-form-item label="标签" :label-width="formLabelWidth" prop="tagUid">
              <el-select
                @input="contentChange"
                v-model="tagValue"
                multiple
                :multiple-limit="3"
                size="small"
                placeholder="请选择"
                style="width:250px"
                filterable
              >
                <el-option
                  v-for="item in tagExamData"
                  :key="item.uid"
                  :label="item.name"
                  :value="item.uid"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-col :lg="7" :sm="8" :xs="8">
          <el-form-item label="权限" :label-width="formLabelWidth" prop="isOpen">
            <el-radio-group v-model="examForm.isOpen" size="small">
              <el-radio v-for="item in isOpenDictList" :key="item.uid" :label="item.dictValue" border>{{item.dictLabel}}</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-form>

      <br>
      <br>
      <span slot="footer" class="dialog-footer nextStep">
        <el-button type="primary" @click="nextPage">下一步</el-button>
      </span>
    </el-dialog>

    <!--  试卷浏览弹框  -->
    <el-dialog
      :title="title"
      :visible.sync="dialogExamVisible"
      fullscreen
    >
      <el-steps :active="active"  class="no-print">
        <el-step title="试卷信息" icon="el-icon-edit"></el-step>
        <el-step title="浏览试卷" icon="el-icon-reading"></el-step>
        <el-step title="完成" icon="el-icon-present"></el-step>
      </el-steps>
      <br>

      <div id="padDom">
        <h3 class="topic" v-if="singleChoice.length > 0">单选题</h3>
        <div
          v-for="(item, index) in singleChoice"
          :key="item.uid"
          style="padding-top: 1px; padding-bottom: 5px"
          data-scroll-reveal="enter bottom over 1s"
        >
          <el-row  :span="24" class="problemLine">

            <el-col :xs="24" :sm="24">
              <div class="blogtitle" style="font-size: 16px">
                <span>第{{index+1}}题 :</span>
                <span v-highlight v-html="item.content">
                  {{item.content}}
                </span>
              </div>

              <div class="blogtitle" style="font-size: 16px">
                <span>解析：</span>
                <span v-highlight v-html="item.answer">
                  {{item.answer}}
                </span>
              </div>
              <br>
            </el-col>
          </el-row>
        </div>
        <br>

        <h3 class="topic" v-if="multipleChoice.length > 0">多选题</h3>
        <br>
        <div
          v-for="(item, index) in multipleChoice"
          :key="item.uid"
          style="padding-top: 1px; padding-bottom: 5px"
          data-scroll-reveal="enter bottom over 1s"
        >
          <el-row  :span="24" class="problemLine">

            <el-col :xs="24" :sm="24">
              <div class="blogtitle" style="font-size: 16px">
                <span>第{{index+1}}题 :</span>
                <span v-highlight v-html="item.content">
                  {{item.content}}
                </span>
              </div>

              <div class="blogtitle" style="font-size: 16px">
                <span>解析：</span>
                <span v-html="item.answer">
                {{item.answer}}
                </span>
              </div>
              <br>
            </el-col>
          </el-row>
        </div>
        <br>

        <h3 class="topic" v-if="shortAnswer.length >0">简答题</h3>
        <br>
        <div
          v-for="(item, index) in shortAnswer"
          :key="item.uid"
          style="padding-top: 1px; padding-bottom: 5px"
          data-scroll-reveal="enter bottom over 1s"
        >
          <el-row  :span="24" class="problemLine">

            <el-col :xs="24" :sm="24">
              <div style="font-size: 18px">
                <span>第{{index+1}}题 :</span>
                <span v-html="item.content">
                  {{item.content}}
                </span>
              </div>

              <div style="font-size: 16px">
                <span>解析：</span>
                <span v-html="item.answer">
                {{item.answer}}
                </span>
              </div>
              <br>
            </el-col>
          </el-row>
        </div>
      </div>
      <span slot="footer" class="dialog-footer nextStep">
        <el-button type="primary" @click="dowmLoadExam" class="no-print">下一步</el-button>
      </span>

    </el-dialog>

    <!--  打开购物车  -->
    <el-drawer
      title="购物车"
      :visible.sync="drawer"
      :direction="direction"
      :before-close="handleCloseShopping">
      <div
        v-for="(item, index) in testShopping"
        :key="item.uid"
        class="blogs shoppingcrat"
        style="padding-top: 1px; padding-bottom: 5px"
        v-if="item.user"
        data-scroll-reveal="enter bottom over 1s"
      >
        <el-row  :span="24" class="problemLine">

          <el-col :xs="24" :sm="24">
            <div class="blogtitle" style="font-size: 16px">

              <a v-if="!quickReply" :href=" VUE_MOGU_WEB + '/cInfo/' + item.oid +'?title=面经'" target="_blank">{{item.title}}</a>
              <a v-else href="javascript:void(0);" @click="showQuickReplyVisible(item, index)">{{item.title}}</a>

              <span style="float: right">
                  <el-tag class="pointer" @click="getToProblem(item, '1')" size="mini" v-if="item.isSelection == 1" effect="dark" type="warning">精选</el-tag>
                  <el-tag class="pointer" @click="getToProblem(item, '2')" size="mini" effect="plain" v-for="problemType in problemTypeDictList" :key="problemType.uid" v-if="item.problemType == problemType.dictValue" :type="problemType.listClass">{{problemType.dictLabel}}</el-tag>
                  <el-tag class="pointer" @click="getToProblem(item, '3')" size="mini" effect="plain" v-for="problemDifficulty in problemDifficultyDictList" :key="problemDifficulty.uid" v-if="item.problemDifficulty == problemDifficulty.dictValue" :type="problemDifficulty.listClass">{{problemDifficulty.dictLabel}}</el-tag>
              </span>
            </div>

            <div class="bloginfo">
              <ul>
                <li style=" padding-right: 6px" @click="getUserCenter(item)">
                      <span :class="item.user.userTag > 0 ?'vip-avatar':''">
                        <el-avatar :class="item.user.userTag > 0 ?'vip-color':''" size="medium" v-if="item.user.photoUrl" :src="item.user.photoUrl"></el-avatar>
                        <el-avatar size="small" v-else src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"></el-avatar>
                        <span style="font-size: 10px; right: -4px; bottom: -1px;" class="vip-text pointer" v-if="item.user.userTag > 0">v</span>
                      </span>
                </li>
                <li class="author"  style="margin-top: 9px; margin-left: 3px;" @click="getUserCenter(item)">
                  <span class="pointer" :class="'lv'+ item.user.userLevel">{{item.user.nickName}}</span>
                </li>

                <li class="lmname" style="margin-top: 8px" v-if="item.blogSort">
                  <span class="iconfont">&#xe603;</span>
                  <a
                    href="javascript:void(0);"
                    @click="goToList(item.blogSort.uid)"
                  >{{item.blogSort.sortName}}</a>
                </li>

                <li class="view" style="margin-top: 8px">
                  <span class="iconfont">&#xe8c7;</span>
                  <span>{{item.clickCount}}</span>
                </li>

                <li class="view" style="margin-top: 8px;">
                  <Collection :bizUid="item.uid" :collectType="'6'"></Collection>
                </li>

                <li class="view" style="margin-top: 8px;">
                  <Praise :bizUid="item.uid" :resourceType="'6'"></Praise>
                </li>

                <li class="createTime" style="margin-top: 8px">
                  <span class="iconfont">&#xe606;</span>
                  {{timeAgo(item.createTime)}}
                </li>
              </ul>

              <span v-if="!item.flg">
                <el-button type="info" plain size="mini" @click="removeTest(item)" style="float: right; width: 100px">取消添加</el-button>
              </span>
            </div>
          </el-col>
        </el-row>
      </div>
      <el-button type="warning" icon="el-icon-download" @click="createTest()" style="float: right; margin-right: 10px">导出试卷</el-button>

    </el-drawer>

    <UserCard :usrCard="this.usrCard" v-if="showUsrCard" :coorDinatex="this.coorDinatex" :coorDinatey="this.coorDinatey" @enter="onCardEnterTd" @leave="onLeaveTd"></UserCard>

  </div>
</template>

<script>
import TagCloud from "../components/TagCloud";
import {getProblemList, getProblemTagList, getAllProblemTagList, getProblemQueryList, problemDegree, builderExam} from "../api/problem";
import { Loading } from 'element-ui';
import {mapMutations} from "vuex";
import Collection from "../components/Collection";
import Praise from "../components/Praise";
import {getListByDictTypeList} from "@/api/sysDictData"
import {timeAgo} from "../utils/webUtils";
import ElImageViewer from 'element-ui/packages/image/src/image-viewer.vue'
import Sticky from "@/components/Sticky";
import RightToolbar from "@/components/RightToolbar";
import {getCookie} from "@/utils/cookieUtils";
import {recorderBehavior} from "../api";
import UserCard from "../components/UserCard";
export default {
  name: "index",
  components: {
    //注册组件
    TagCloud,
    Sticky,
    Collection,
    Praise,
    ElImageViewer,
    RightToolbar,
    UserCard,
  },
  data() {
    return {
      problemMap: null, // 问题字典
      showStep: true,
      active: 1,
      drawer: false,
      direction: 'rtl',
      shortAnswer: [],
      multipleChoice: [],
      singleChoice: [],
      title: "试卷生成",
      dialogFormVisible: false,
      dialogExamVisible: false,
      changeCount: 0, // 改变计数器
      isChange: false, // 表单内容是否改变
      tagExamData: [],
      examForm: {},
      formLabelWidth: "120px",
      openeds: [],
      lineLabelWidth: '60px',
      showSearch: false,
      selectProblem: {}, // 选中的问题
      quickReplyVisible: false, // 是否显示快问快答
      quickReply: false, // 快问快答
      searchParams: { // 搜索参数
        keyword:  "",
        tagValue: "",
        problemDifficulty: "",
        problemType: "",
        isSelection: "",
        hasAnswer: ""
      },
      maxHeight: window.screen.height - 300,
      showViewer: false,
      picList: [],
      imageWidth: "60%",
      selectIndex: 0,
      defaultActive: "", //
      allTagData: [], // 全部标签
      loadingInstance: null, // loading对象
      VUE_MOGU_WEB: process.env.VUE_MOGU_WEB,
      newProblemData: [], //最新问题
      hotBlogData: [], //最热问题
      keyword: "",
      currentPage: 1,
      pageSize: 15,
      total: 0, //总数量
      isEnd: false, //是否到底底部了
      loading: false, //是否正在加载
      activeName: "0", // 激活页
      methodType: "newProblem", // 激活方法
      isCollapse: false,
      problemDifficultyDictList: [], // 问题难度字典
      problemTypeDictList: [], // 问题类型字典
      isOpenDictList: [],
      problemDifficultyDefault: null, // 问题难度默认值
      problemTypeDefault: null, // 问题类型默认值
      tagValue: [], //保存选中标签id(编辑时)
      tagData: [], //标签数据
      showAnswer: true, //显示解析
      drawerSize: "40%",
      problemOid: null, // 问答oid
      testData: [], // 添加问题的problemId列表
      testShopping: [],// 购物车问题列表
      rules: {
        title: [
          {required: true, message: '名称不能为空', trigger: 'blur'},
          {min: 2, max: 50, message: '长度在2到50个字符'},
        ],
        description: [
          {required: true, message: '描述不能为空', trigger: 'blur'},
          {min: 2, max: 500, message: '长度在2到500个字符'},
        ],
        tagUid: [
          {required: true, message: '标签不能为空', trigger: 'blur'}
        ],
      },
      usrCard: "",
      coorDinatex: "",
      coorDinatey: "",
      showUsrCard: false,
      currentId:1,
      authCode: {},
    };
  },
  watch: {
    '$store.state.user.authCode': function (newFlag, oldFlag) {
      this.authCode = this.$store.state.user.authCode
    }
  },
  mounted() {
    // 注册scroll事件并监听
    this.loading = false;
    // 获取宽高
    window.onresize = () => {
      return (() => {
        this.resizeWin();
      })();
    };
    this.resizeWin();
    //点击文本就让它自动点击前面的input就能够触发选择。可是因组件阻止了冒泡，暂时想不到好方法来触发。
    //这种比较耗性能，暂时想不到其余的，能实现效果了。
    setInterval(function() {
      document.querySelectorAll(".el-cascader-node__label").forEach(el => {
        el.onclick = function() {
          if (this.previousElementSibling) this.previousElementSibling.click();
        };
      });
    }, 1000);

  },
  created() {
    this.authCode = this.$store.state.user.authCode
    let problemOid = this.$route.query.oid;
    if (problemOid) {
      this.quickReplyVisible = true
      this.problemDetail(0, problemOid)
    }
    let tagUid = this.$route.query.tagUid;
    if (tagUid) {
      let tagArray = new Array()
      tagArray[0] = tagUid
      this.searchParams.tagValue = tagArray
    }

    let problemDifficulty = this.$route.query.problemDifficulty;
    if (problemDifficulty) {
      this.searchParams.problemDifficulty = problemDifficulty
    }

    let problemType = this.$route.query.problemType;
    if (problemType) {
      this.searchParams.problemType = problemType
    }

    let isSelection = this.$route.query.isSelection;
    if (isSelection) {
      this.searchParams.isSelection = isSelection
    }
    // 判断是否显示搜索
    this.getShowSearch()
    // 获取全部标签
    this.getAllList()
    // 获取标签
    this.tagList()
    // 获取字典
    this.getDictList()
    // 获取最新问答
    this.orderByDescColumn = "random"
    this.problemList(true);

    // 5S后埋点信息上报
    setTimeout(()=>{
      recorderBehavior({"otherData": "面经", "behavior": "VISIT_PAGE"}).then(response => {})
    }, 5000)

  },

  methods: {
    //拿到vuex中的写的方法
    ...mapMutations(['setCreateProblemMessage', 'setLoginMessage']),
    getTabIndex: function (index) {
      return "id" + index
    },
    //鼠标进入的事件。
    onEnterTd (item, index) {
      this.showUsrCard = true;
      this.usrCard = item;
      let tagElement = document.getElementById("id"+ index);
      let leftx = tagElement.getBoundingClientRect().left;
      let topx = tagElement.getBoundingClientRect().top;
      this.coorDinatex = leftx;
      this.coorDinatey = topx;
      clearTimeout(this.leaveTimeout)
    },
    onCardEnterTd: function () {
      clearTimeout(this.leaveTimeout)
    },
    //鼠标离开的事件。
    onLeaveTd (nowClean) {
      let that = this
      if (nowClean) {
        that.showUsrCard = false;
      } else {
        this.leaveTimeout = setTimeout(() => {
          that.showUsrCard = false;
        }, 300)
      }
    },
    //打开购物车抽屉
    openShopping(done) {
      if (this.testShopping.length >0){
        this.drawer = true;

      }else {
        this.$alert('购物车空空如也，请添加问题', '购物车', {
          confirmButtonText: '确定',
          callback: action => {
            this.$message({
              type: 'info',
              message: `请添加问题至购物车呦~`
            });
          }
        });
      }

    },
    handleCloseShopping(done) {
      this.$confirm('确认关闭？')
        .then(_ => {
          done();
        })
        .catch(_ => {});
    },
    // 购物车悬浮
    scrollChange () {
      let scw = document.getElementById('wechats').clientWidth
      let sw = document.getElementById('wechats').scrollWidth
      let sl = document.getElementById('wechats').scrollLeft
      if (sw > scw) {
        document.getElementById('chooseBtn').style.marginRight = -sl + 'px'
      } else {
        document.getElementById('chooseBtn').style.marginRight = 0 + 'px'
      }
    },
    nextPage: function () {
      let that = this;
      that.loadingInstance = Loading.service({
        lock: true,
        text: '正在努力加载中……',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      //console.log(this.examForm)
      //显示下一页
      this.dialogFormVisible = false;
      this.dialogExamVisible = true;
      let params = {};
      params.problemUid = this.examForm.problemUid
      //根据选择的问题 进行归类 简答，单选 等
      builderExam(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          //获取的试题分类
          console.log(response.data.singleChoice);
          console.log(response.data.shortAnswer);
          console.log(response.data.multipleChoice);
          this.singleChoice = response.data.singleChoice;
          this.multipleChoice = response.data.multipleChoice;
          this.shortAnswer = response.data.shortAnswer;
        } else {
          this.$message.error(response.message)
        }

        this.loading = false;
        that.loadingInstance.close();
      },function(err){
        that.loadingInstance.close();
      });
    },//导出为pdf
    dowmLoadExam: function () {
        this.active = 3;
        this.showStep = false;
        window.print();
        setTimeout(() => {
          window.location.href= this.VUE_MOGU_WEB + '/code';
        }, 1000)
    },
    // 生成试卷
    createTest: function() {
      if(this.testData == [] || this.testData == ''){
          this.$alert('请选择题目加入试卷', '未添加题目', {
            confirmButtonText: '确定',
            callback: action => {
              this.$message({
                type: 'info',
                message: `请加入试卷后再点击生成试卷`
              });
            }
          });
      }else{
        this.dialogFormVisible = true;
        this.examForm.problemUid = this.testData
      }
    },
    // 关闭时的回调
    beforeClose(done) {
      this.$confirm("是否关闭试卷生成窗口", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          this.isChange = false;
          this.changeCount = 0
          //取消时，开始状态
          this.$emit("beforeClose", "");
          done();
        })
        .catch(() => {
          // this.$commonUtil.message.info("已取消")
        });
    },
    // 内容改变，触发监听
    contentChange: function() {
      console.log("内容改变")
      let that = this;
      if(that.changeCount > 1) {
        that.isChange = true;
        that.examForm.examTagUid = that.tagValue.join(",");
      }
      this.changeCount = this.changeCount + 1;
    },
    ...mapMutations(['setCreateProblemMessage', 'setLoginMessage']),
    createProblem: function () {
      let isLogin = this.$store.state.user.isLogin
      if(!isLogin) {
        this.$notify.error({
          title: '警告',
          message: '登录后才可以发表面经~',
          offset: 100
        });
        // 未登录，自动弹出登录框
        this.setLoginMessage(Math.random())
        return;
      }
      this.setCreateProblemMessage(Math.random())
    },
    handleOpen(key, keyPath) {
      console.log(key, keyPath);
    },
    handleClose(key, keyPath) {
      console.log(key, keyPath);
    },
    // 判断是否需要展开条件查询
    getShowSearch: function () {
      let showSearch = getCookie("showSearch")
      if(showSearch == "false"){
        this.showSearch = false
      } else {
        this.showSearch = true
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
        } break;
        case '2': {
          this.searchParams.problemType = problem.problemType
          this.problemList('true')
        } break;
        case '3': {
          this.searchParams.problemDifficulty = problem.problemDifficulty
          this.problemList('true')
        } break;
        case '4': {
          let tagArray = new Array()
          tagArray[0] = problem
          this.searchParams.tagValue = tagArray
          this.problemList('true')
        } break;
      }
    },
    // 提交掌握记录
    handleProblemDegree(degreeStatus) {
      if (this.selectProblem) {
        let params = {}
        params.problemUid = this.selectProblem.uid
        params.degree = degreeStatus
        params.title = this.selectProblem.title
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
    preProblem: function () {
      this.selectIndex = this.selectIndex - 1
      let currentPage = (this.currentPage - 1 ) * this.pageSize + this.selectIndex
      if (currentPage < 0) {
        this.$message.error("已到搜索条件下的第一题")
        this.selectIndex = 1
      }
      this.problemDetail(currentPage)
    },
    nextProblem: function () {
      this.selectIndex = this.selectIndex + 1
      let currentPage = (this.currentPage - 1 ) * this.pageSize + this.selectIndex
      this.problemDetail(currentPage)
    },
    problemDetail: function (currentPage, problemOid) {
      let that = this;
      that.loadingInstance = Loading.service({
        lock: true,
        text: '正在努力加载中……',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      let params = {};
      if (this.searchParams.tagValue) {
        let tagUid = this.searchParams.tagValue.join(",");
        params.problemTagUid = tagUid;
      }
      if (problemOid) {
        params.oid = problemOid
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
      // 默认查询过滤答案
      params.pageSize = 1;
      params.currentPage = currentPage;
      params.degreeStatus = "1"
      params.orderByDescColumn = this.orderByDescColumn;

      getProblemQueryList(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let newProblemData = response.data.records;
          //全部加载完毕
          if (newProblemData.length > 0) {
            this.selectProblem = newProblemData[0]
            console.log("下一题", this.selectProblem)
          } else {
            this.selectIndex = this.selectIndex - 1
            this.$message.error("已到搜索条件下的最后一题")
          }
        }
        that.loadingInstance.close();
      });
    },
    resizeWin() {
      //当前window 宽
      let centerWidth = document.documentElement.scrollWidth;
      if (centerWidth > 1300) {
        this.drawerSize = "50%"
      } else if(centerWidth > 1000) {
        this.drawerSize = "70%"
      } else if(centerWidth > 600) {
        this.drawerSize = "80%"
      } else {
        this.drawerSize = "100%"
      }
    },
    handleSelect(index ,keyPath) {
      this.currentPage = 1
      this.pageSize = 15
      this.newProblemData = []
      let array = new Array()
      for (let i = 0; i < keyPath.length; i++) {
        array[i] = keyPath[i]
      }
      let tagArray = new Array()
      tagArray[0] = array
      this.searchParams.tagValue = tagArray
      // 埋点上报
      console.log("点击问答标签uid", tagArray[tagArray.length - 1][tagArray[tagArray.length - 1].length - 1])

      let problemTagUid = tagArray[tagArray.length - 1][tagArray[tagArray.length - 1].length - 1]


      let problemTagName = ""
      if (this.problemMap) {
        problemTagName = this.problemMap.get(problemTagUid)
      }

      // 埋点上报
      recorderBehavior({"otherData": problemTagName, "bizUid": problemTagUid, "bizType": "PROBLEM" , "behavior": "PROBLEM_TAG"}).then(response => {})

      this.problemList(true)
    },
    clear: function () {
      this.searchParams = {}
      this.orderByDescColumn = 'create_time'
      this.currentPage = 1
      this.pageSize = 15
      // 获取最新问答
      this.problemList(true);
    },
    timeAgo(dateTimeStamp) {
      return timeAgo(dateTimeStamp)
    },
    showQuickReplyVisible: function (item, index) {
      this.selectIndex = index + 1
      this.selectProblem = item
      this.quickReplyVisible = true
      // 变成已查看状态
      this.handleProblemDegree('2')
    },
    handleShowAnswer: function (item) {
      if(item.hasAnswer == '0') {
        this.$message.error("暂无解析")
        return
      }
      this.showAnswer = !this.showAnswer
    },
    /**
     * 字典查询
     */
    getDictList: function () {
      let dictTypeList =  ['sys_problem_difficulty', 'sys_problem_type', 'sys_isopen_status', 'sys_isopen_status']
      getListByDictTypeList(dictTypeList).then(response => {
        let that = this
        if (response.code == this.$ECode.SUCCESS) {
          let dictMap = response.data;
          this.problemDifficultyDictList = dictMap.sys_problem_difficulty.list
          this.problemTypeDictList = dictMap.sys_problem_type.list
          this.isOpenDictList = dictMap.sys_isopen_status.list
          if(dictMap.sys_problem_difficulty.defaultValue) {
            this.problemDifficultyDefault = dictMap.sys_problem_difficulty.defaultValue;
          }
          if(dictMap.sys_problem_type.defaultValue) {
            this.problemTypeDefault = dictMap.sys_problem_type.defaultValue;
          }

          if(dictMap.sys_isopen_status.defaultValue) {
            this.examForm.isOpen = dictMap.sys_isopen_status.defaultValue
          }

          that.tagValue = [];
          if (this.examForm.tagList) {
            let json = this.examForm.tagList;
            for (let i = 0, l = json.length; i < l; i++) {
              if (json[i] != null) {
                that.tagValue.push(json[i]["uid"]);
              }
            }
          }
        }
      });
    },
    tagList: function() {
      let tagParams = {};
      tagParams.pageSize = 200;
      tagParams.currentPage = 1;
      getProblemTagList(tagParams).then(response => {
        console.log(response.data.records)
        this.tagExamData = response.data.records;
      });
    },
    getAllList: function() {
      let problemMap = new Map();

      getAllProblemTagList().then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let allTagData = response.data;
          if (allTagData.length > 0) {
            this.openeds.push(allTagData[0].uid)
          }

          for (let i = 0; i < allTagData.length; i++) {
            console.log("problem", allTagData[i])
            if (allTagData[i].children) {
              let children = allTagData[i].children
              for (let j = 0; j <children.length; j++) {
                problemMap.set(children[j].uid, children[j].name);
              }
            }
          }
          this.problemMap = problemMap

          this.allTagData = allTagData
        }
      });
    },
    closeViewer() {
      this.showViewer = false
    },
    imageChange: function(e) {
      //首先需要判断点击的是否是图片
      let type = e.target.localName;
      if (type == "img") {
        this.showViewer = true
        this.picList = [e.target.currentSrc]
      }
    },
    handleChange(tag) {
      if (tag == 1) {
        this.orderByDescColumn = ""
        this.activeName = "0"
      }
      this.currentPage = 1
      this.pageSize = 15
      this.newProblemData = []
      this.problemList(true)
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
      if (!this.quickReply) {
        params.isShowAnswer = "0"
      }
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
          let records = response.data.records

          if (isClear){
            this.newProblemData = records;
            for (let i = 0; i < this.newProblemData.length; i++) {
              this.newProblemData[i].flg = true;
            }
          } else {
            this.newProblemData = this.newProblemData.concat(records);
            for (let i = 0; i < this.newProblemData.length; i++) {
              this.newProblemData[i].flg = true;
            }
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
      },function(err){
        that.loadingInstance.close();
      });
    },
    loadContent: function () {
      this.loading = false;
      this.currentPage = this.currentPage + 1;
      this.problemList(false)
    },
    handleClick(tab, event) {
      console.log("事件", tab)
      this.currentPage = 1
      this.pageSize = 15
      this.newProblemData = []
      switch (tab.name) {
        // 默认
        case "0": {
          this.orderByDescColumn = "random"
        } break;
        // 最新
        case "1": {
          this.orderByDescColumn = "create_time"
        } break;
        // 最热
        case "2": {
          this.orderByDescColumn = "click_count"
        } break;
        // 最多点赞
        case "3": {
          this.orderByDescColumn = "start_count"
        } break;
        // 最多收藏
        case "4": {
          this.orderByDescColumn = "collect_time"
        } break;
        // 最多评论
        case "5": {
          this.orderByDescColumn = "comment_count"
        } break;
        // 最多通过
        case "6": {
          this.orderByDescColumn = "pass_count"
        } break;
        // 最多未通过
        case "7": {
          this.orderByDescColumn = "no_pass_count"
        } break;
        // 最多出现
        case "8": {
          this.orderByDescColumn = "visit_count"
        } break;
      }
      console.log("this.orderByDescColumn", this.orderByDescColumn)
      this.problemList(true)
    },
    getUserCenter: function (problem) {
      console.log("跳转到用户中心", problem)
      let routeData = this.$router.resolve({
        path: "/userCenter",
        query: {userUid: problem.userUid}
      });
      window.open(routeData.href, '_blank');
    },
    addTest: function (problem) {
      problem.flg = false;
      this.$forceUpdate();
      this.testData.push(problem.uid)
      this.testShopping.push(problem);
/*      //购物车抖动效果
      document.onclick = function () {
        /!*
        * 抖动：
        * 1. 每次改变一下元素的位置
        * 按照一个中心点进行偏移，假设中心点left原始是1360，那么每次就以left：1360为中心做位置的移动
        * 1350 -> 1370
        * *!/
        var a = true;
        var stopInterval = setInterval(function() {
          /!*
          * 根据a的值，做不同的设置
          * *!/
          // document.getElementsByClassName('wechat-list')[0].style.left = (a? 1354 : 1374) + 'px'
          document.getElementsByClassName('wechat-list')[0].style.left = (a? 95 : 96) + '%'
          a = !a;
        }, 30);
        setTimeout(() => {
          clearInterval(stopInterval)
        }, 200)
      }*/
    },
    removeTest: function (problem) {
      problem.flg = true;
      this.$forceUpdate();
      var testTemp = [];
      var testShoppingTemp = [];
      //试题id 集合
      for (let i = 0; i < this.testData.length; i++) {
        if (this.testData[i] != problem.uid){
          testTemp.push(this.testData[i])
        }
      }//购物车集合
      for (let i = 0; i < this.testShopping.length; i++) {
        if (this.testShopping[i].uid != problem.uid){
          testShoppingTemp.push(this.testShopping[i])
        }
      }
      this.testData = testTemp;
      this.testShopping = testShoppingTemp;
    },

  }
};
</script>

<style scoped>
::-webkit-scrollbar {
  width: 0;
  height: 0;
}
</style>

<style>

@media print {
  .no-print{
    display: none;
  }
}
/*打印页配置*/
@page {
  margin: 60px 10px;
}
.shoppingcrat {
  margin-top: 50px;
}
.el-icon-shopping-cart-2 {
  font-size: 30px;
}
.el-icon-shopping-cart-full {
  font-size: 50px;
  color: #aa88ff;
}
.wechat-list {
  position: fixed;
  bottom: 190px;
  right: 45px;
  overflow: auto;
}

.wechat-list2 {
  position: fixed;
  bottom: 190px;
  right: 45px;
  overflow: auto;
  color: #0db9f0;
}

.nextStep {
  float: left;
}
.tip-info {
  padding: 10px;
  margin: 20px auto 15px auto;
  background-color: #fafcfc;
  border-radius: 4px;
  border-left: 5px solid #3e3f41;
  color: #888888;
}
.tip-info strong {
  color: #38485A;
  font-weight: 400 !important;
  font-size: 13px;
  padding-right: 8px;
}

.tip-primary {
  padding: 10px;
  margin: 20px auto 15px auto;
  background-color: #ecf8ff;
  border-radius: 4px;
  border-left: 5px solid #50bfff;
  color: #888888;
}
.tip-primary strong {
  color: #38485A;
  font-weight: 400 !important;
  font-size: 13px;
  padding-right: 8px;
}

.tip-success {
  padding: 10px;
  margin: 20px auto 15px auto;
  background-color: #e7f6e0;
  border-radius: 4px;
  border-left: 5px solid #67c23a;
  color: #888888;
}
.tip-success strong {
  color: #38485A;
  font-weight: 400 !important;
  font-size: 13px;
  padding-right: 8px;
}

.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 200px;
  min-height: 400px;
}
.el-loading-mask {
  z-index: 2002;
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

.iconfont {
  font-size: 15px;
  margin-right: 2px;
}

.problemLine .itemNum {
  height: 50px;
  width: 100%;
  line-height: 50px;
  justify-content: center;
  margin: 0 auto;
  font-weight: bold;
}
</style>
