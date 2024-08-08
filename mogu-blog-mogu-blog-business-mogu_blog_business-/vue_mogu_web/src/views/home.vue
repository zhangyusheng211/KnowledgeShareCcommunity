<template>
  <div>
    <html>
    <body>
    <head>
      <meta charset="utf-8">
      <title>{{info.title?info.title:defaultConfig.title}}</title>
      <meta name="keywords" :content="info.keyword?info.keyword:defaultConfig.meta.keywords">
      <meta name="description" :content="info.summary?info.summary:defaultConfig.meta.description">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>

    <header
      :class="isVisible?'header-navigation slideDown':'header-navigation slideUp'"
      id="header"
      :style="{height: '60px'}"
    >
      <nav>
        <div class="logo">
          <el-image v-if="info.logoPhoto" style="width: 35px;height: 35px; vertical-align: middle; margin-top: -6px;"
                    :src="info.logoPhoto"></el-image>
          <router-link to="/">
            <a href="javascript:void(0);" v-if="info.name">{{ info.name }}</a>
          </router-link>
        </div>

        <h2 id="mnavh" @click="openHead" :class="showHead?'open':''">
          <span class="navicon"></span>
        </h2>

        <ul id="starlist" :style="showHead?'display: block':''">
          <li v-for="webNavbar in webNavbarList" :key="webNavbar.uid" @click="clickWebNavbar(webNavbar)">
            <!--判断是否有下拉菜单-->
            <span v-if="webNavbar.childWebNavbar && webNavbar.childWebNavbar.length > 0">
              <el-dropdown trigger="click">
                <span class="el-dropdown-link"
                      :style="{color: (saveTitle == webNavbar.url || saveTitle == webNavbar.name) ? '#007fff': webNavbar.color}">
                  {{ webNavbar.name }}<i class="el-icon-arrow-down el-icon--right"></i>
                </span>
                <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item v-for="childWebNavbar in webNavbar.childWebNavbar" :key="childWebNavbar.uid">
                      <router-link :to="childWebNavbar.url" v-if="childWebNavbar.isJumpExternalUrl == 0">
                        <span @click="openHead" :style="{color: childWebNavbar.color}">{{ childWebNavbar.name }}</span>
                      </router-link>
                      <a v-if="childWebNavbar.isJumpExternalUrl == 1" :href="childWebNavbar.url"
                         :style="{color: childWebNavbar.color}" target="_blank">{{ childWebNavbar.name }}</a>
                    </el-dropdown-item>
                  </el-dropdown-menu>
              </el-dropdown>
          </span>

            <!--没有有下拉菜单-->
            <span v-else>
            <router-link :to="webNavbar.url" v-if="webNavbar.isJumpExternalUrl == 0">
                <span @click="openHead"
                      :style="{color: (saveTitle == webNavbar.url || saveTitle == webNavbar.name) ? '#007fff': webNavbar.color}"
                      :class="[(saveTitle == webNavbar.url || saveTitle == webNavbar.name) ? 'title' : '']">{{
                    webNavbar.name
                  }}</span>
            </router-link>
            <a v-if="webNavbar.isJumpExternalUrl == 1" :href="webNavbar.url" target="_blank"
               :class="[saveTitle == webNavbar.url || saveTitle == webNavbar.name ? 'title' : '']">{{
                webNavbar.name
              }}</a>
          </span>
          </li>
        </ul>

        <div class="searchbox" v-if="componentShowMap.showUserSearch"
             v-bind:class="showCreateBlog||showCreateQuestion?'':'searchboxDefault'"
             @click.stop="()=>{}">
          <div id="search_bar" :class="(showSearch || keyword.length > 0)?'search_bar search_open':'search_bar'">
            <input
              ref="searchInput"
              class="input"
              placeholder="想搜点什么呢.."
              type="text"
              name="keyboard"
              v-model="keyword"
              v-on:keyup.enter="search"
              @click="showHotSearch"
              autocomplete="off"
              @mouseleave="onLeaveTd(false)"
            >
            <p class="search_ico" @click="clickSearchIco" :style="(browserFlag == 1)?'':'top:17px'">
              <span></span>
            </p>
          </div>

          <HotSearch v-if="dialogHotSearchVisible" @chooseitem="chooseitem" @enter="onCardEnterTd"
                     @leave="onLeaveTd"></HotSearch>
        </div>

        <el-dropdown @command="handleCommand" class="create" placement="bottom">
        <span class="el-dropdown-link">
          <el-button v-if="componentShowMap.showCreateButton" type="primary" size="small" icon="el-icon-edit"
                     :disabled="!authCode.ADD_BLOG && !authCode.UPLOAD_BLOG && !authCode.ADD_QUESTION && !authCode.ADD_MOMENT && !authCode.ADD_PROBLEM"
                     class="createBlog">创作</el-button>
        </span>

          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item v-if="showCreateBlog && authCode.ADD_BLOG && componentShowMap.showCreateArticle"
                              command="createBlog">创作文章
            </el-dropdown-item>
            <el-dropdown-item v-if="authCode.UPLOAD_BLOG && componentShowMap.showUploadArticle" command="uploadBlog">
              上传文章
            </el-dropdown-item>
            <el-dropdown-item v-if="authCode.ADD_QUESTION && componentShowMap.showCreateQuestion"
                              command="createQuestion">发起问答
            </el-dropdown-item>
            <el-dropdown-item v-if="authCode.ADD_MOMENT && componentShowMap.showCreateMoment" command="userMoment">
              发表动态
            </el-dropdown-item>
            <el-dropdown-item v-if="authCode.ADD_PROBLEM && componentShowMap.showCreateProblem" command="createProblem">
              发布面经
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>

        <!--VIP会员页面-->
        <!--        <div class="vipIcon"  v-if="componentShowMap.showVipModel">-->
        <!--          <a href="/vip">-->
        <!--            <div class="vip-entry-img"><img src="../../static/images/vip.png"  alt="vip" class="vip-img" data-v-925171a8="">-->
        <!--            </div>-->
        <!--          </a>-->
        <!--        </div>-->


        <el-dropdown @command="handleCommand" class="vipIcon" v-if="componentShowMap.showVipModel" placement="bottom"
                     trigger="hover" style=" border-radius: 10px;">
          <span class="el-dropdown-link">
            <div class="vip-entry-img"><img src="../../static/images/vip.png" alt="vip" class="vip-img"></div>
          </span>

          <el-dropdown-menu slot="dropdown" class="userDropdownMenu">
            <el-dropdown-item command="openVip" class="userDropdownMenuItem" >
              <el-card :body-style="{ padding: '20px' }" style="width: 380px; cursor: default;"  class="vipCardStyle">
                <el-row class="vipCardTitle"
                        style="text-align: center; line-height: 25px;" >
                  <el-row type="flex" justify="space-around" style="text-align: center">
                    <el-col :span="6" style="margin-top: -6px;">
                      <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="50" height="3" viewBox="0 0 50 3" fill="none">
                        <path d="M50 3L50 0L0 2L50 3Z"   fill="url(#linear_fill_11_12)" >
                        </path>
                        <defs>
                          <linearGradient id="linear_fill_11_12" x1="-16.2236328125" y1="3" x2="50" y2="1.5" gradientUnits="userSpaceOnUse">
                            <stop offset="0" stop-color="#5C7CF5" stop-opacity="0.73" />
                            <stop offset="1" stop-color="#5C7CF5"  />
                          </linearGradient>
                        </defs>
                      </svg>

                    </el-col>
                    <el-col :span="12">
                      <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="20.9853515625" height="18.935546875" viewBox="0 0 20.9853515625 18.935546875" fill="none">
                        <path d="M20.9856 0.00835529L13.4662 0L6.01373 9.13011L6.01373 1.58181L6.01373 1.44034L6.01373 0.00835529L0 0.00835529L0 1.58181L1.50495 1.58181L1.50495 18.9364L6.01373 18.9364L6.01373 18.9364L20.9856 0.00835529Z"   fill="#4756E8" >
                        </path>
                      </svg>
                      <span>会员尊享特权</span>
                    </el-col>
                    <el-col :span="6"  style="margin-top: -6px;">
                      <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="50" height="5" viewBox="0 0 50 3" fill="none">
                        <path d="M0 3L0 0L50 2L0 3Z"   fill="url(#linear_fill_11_13)" >
                        </path>
                        <defs>
                          <linearGradient id="linear_fill_11_13" x1="66.2236328125" y1="8" x2="0" y2="4.5" gradientUnits="userSpaceOnUse">
                            <stop offset="0" stop-color="#5C7CF5" stop-opacity="0.73" />
                            <stop offset="1" stop-color="#4253E9"  />
                          </linearGradient>
                        </defs>
                      </svg>
                    </el-col>
                  </el-row>


                </el-row>

                <el-row :span="24" type="flex" justify="space-around">
                  <el-col :span="8" class="vipIconItem">
                    <div class="icon">
                      <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="23" height="18" viewBox="0 0 23 18" fill="none">
                        <path d="M12.9398 4.3773L19.9842 4.3773C20.2717 4.3773 20.4163 4.09094 20.4163 3.80457L20.4163 2.0864C20.4163 1.80004 20.2717 1.51367 19.9842 1.51367L11.645 1.51367C11.2145 1.51367 11.0699 2.0864 11.3574 2.37277L12.6523 4.2333C12.6523 4.3773 12.7952 4.3773 12.9398 4.3773Z"   fill="#4E70F5" >
                        </path>
                        <path d="M20.2735 5.52353L12.9398 5.52353C12.6523 5.52353 12.2201 5.37953 12.0772 4.9508L9.20156 1.22807C9.19367 1.21937 9.18569 1.21078 9.17759 1.2023C9.16947 1.19382 9.16126 1.18545 9.15293 1.17719C9.14458 1.16893 9.13614 1.16078 9.12757 1.15275C9.11902 1.14471 9.11036 1.1368 9.1016 1.129C9.09283 1.1212 9.08395 1.11352 9.07498 1.10595C9.066 1.09839 9.05691 1.09096 9.04773 1.08364C9.03853 1.07632 9.02926 1.06913 9.01987 1.06207C9.01052 1.055 9.00106 1.04806 8.99147 1.04125C8.9819 1.03444 8.97225 1.02776 8.96251 1.0212C8.95276 1.01465 8.94291 1.00823 8.93299 1.00195C8.92307 0.995661 8.91308 0.989508 8.90298 0.98349C8.89291 0.977474 8.88274 0.971593 8.8725 0.965849C8.86224 0.960106 8.85192 0.954501 8.84152 0.949036C8.83113 0.94357 8.82066 0.938245 8.81012 0.933062C8.79956 0.927878 8.78896 0.922837 8.77826 0.91794C8.7676 0.913041 8.75685 0.908288 8.74604 0.903681C8.73522 0.899072 8.72435 0.894609 8.71341 0.890293C8.70248 0.885977 8.6915 0.881809 8.68045 0.87779C8.66942 0.873769 8.65832 0.869898 8.64716 0.866178C8.636 0.862456 8.62481 0.858885 8.61357 0.855466C8.60233 0.852046 8.59105 0.848778 8.5797 0.845662C8.56838 0.842547 8.557 0.839585 8.54558 0.836775C8.53415 0.833966 8.5227 0.831311 8.51123 0.82881C8.49973 0.826308 8.48821 0.823962 8.47666 0.821772C8.46512 0.81958 8.45354 0.817545 8.44193 0.815667C8.43031 0.813787 8.4187 0.812065 8.40703 0.810499C8.3954 0.808933 8.38371 0.807525 8.37201 0.806273C8.36032 0.805022 8.3486 0.803927 8.33688 0.802989C8.32516 0.802053 8.31344 0.801275 8.30172 0.800655C8.28996 0.800034 8.27822 0.799572 8.26647 0.799268C8.2547 0.798964 8.24296 0.798817 8.2312 0.798829C8.21943 0.798842 8.20767 0.799013 8.1959 0.799342L2.87523 0.799342C2.1555 0.799342 1.58203 1.51443 1.58203 2.22952L1.58203 16.2613C1.58203 17.1221 2.15715 17.6947 2.87523 17.6947L20.2735 17.6947C20.9915 17.6947 21.5666 16.9781 21.5666 16.2613L21.5666 6.95533C21.5666 6.09626 20.9915 5.52353 20.2735 5.52353ZM14.2347 13.3993C14.2347 13.8281 13.9471 14.1144 13.515 14.1144L4.60059 14.1144C4.17007 14.1144 3.88087 13.8281 3.88087 13.3977C3.88087 12.969 4.16843 12.6826 4.60059 12.6826L13.6596 12.6826C13.9471 12.6826 14.2347 12.969 14.2347 13.3993ZM17.9714 13.3993C17.9714 13.8281 17.6837 14.1144 17.2533 14.1144L16.6781 14.1144C16.246 14.1144 15.9584 13.8281 15.9584 13.3977C15.9584 12.969 16.246 12.6826 16.6781 12.6826L17.2532 12.6826C17.6837 12.6826 17.973 12.969 17.973 13.3993L17.9714 13.3993Z"   fill="#4E70F5" >
                        </path>
                      </svg>
                    </div>
                    <div class="title">社区资源</div>
                  </el-col>
                  <el-col :span="8" class="vipIconItem">
                    <div class="icon">
                      <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="23" height="23" viewBox="0 0 23 23" fill="none">
                        <path d="M18.0137 0.898438L7.1875 0.898438C6.11836 0.898438 5.25586 2.0125 5.25586 3.10859L17.7172 3.10859L17.7172 19.2176C18.7863 19.2176 19.9543 18.3281 19.9543 17.223L19.9543 2.89297C19.9453 1.78789 19.0828 0.898438 18.0137 0.898438ZM15.1566 4.06094L14.5187 4.06094L14.5187 9.43359L12.7848 7.36718L11.0059 9.43359L11.0059 4.06094L4.29453 4.06094C3.41406 4.06094 2.69531 4.76172 2.69531 5.6332L2.69531 20.1609C2.69531 21.0324 3.41406 21.7422 4.29453 21.7422L15.1566 21.7422C16.0371 21.7422 16.7559 21.0324 16.7559 20.1609L16.7559 5.6332C16.7469 4.76172 16.0371 4.06094 15.1566 4.06094ZM14.5187 17.798C14.5187 17.8879 14.4469 17.9598 14.357 17.9598L5.09414 17.9598C5.0043 17.9598 4.93242 17.8879 4.93242 17.798L4.93242 16.8547C4.93242 16.7648 5.0043 16.693 5.09414 16.693L14.357 16.693C14.4469 16.693 14.5187 16.7648 14.5187 16.8547L14.5187 17.798ZM14.5187 15.2644C14.5187 15.3543 14.4469 15.4262 14.357 15.4262L5.09414 15.4262C5.0043 15.4262 4.93242 15.3543 4.93242 15.2644L4.93242 14.3211C4.93242 14.2312 5.0043 14.1594 5.09414 14.1594L14.357 14.1594C14.4469 14.1594 14.5187 14.2312 14.5187 14.3211L14.5187 15.2644ZM14.5187 12.7398C14.5187 12.8297 14.4469 12.9016 14.357 12.9016L5.09414 12.9016C5.0043 12.9016 4.93242 12.8297 4.93242 12.7398L4.93242 11.7965C4.93242 11.7066 5.0043 11.6348 5.09414 11.6348L14.357 11.6348C14.4469 11.6348 14.5187 11.7066 14.5187 11.7965L14.5187 12.7398Z"   fill="#4E70F5" >
                        </path>
                      </svg>
                    </div>
                    <div class="title">社区题库</div>
                  </el-col>
                  <el-col :span="8" class="vipIconItem">
                    <div class="icon">
                      <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="23" height="23" viewBox="0 0 23 23" fill="none">
                        <path d="M1.06263 8.8015L1.06263 12.4477L0.183706 13.4539L1.87115 14.9168L3.41339 13.4539L2.48575 12.4213L2.48575 9.4048C7.47194 11.4388 8.82191 12.0709 9.92746 12.5644C11.0333 13.058 11.8298 13.0555 12.9265 12.6466C14.0233 12.2379 18.9957 10.3526 21.5881 9.12514C23.3181 8.30594 23.4295 7.7868 21.5584 7.09168C19.1167 6.17386 15.3292 4.70413 13.1899 3.90205C11.9234 3.39501 11.254 3.11838 10.0915 3.69653C8.01597 4.54445 3.52176 6.30512 1.16361 7.26833C-0.889252 8.1548 0.490883 8.44634 1.06263 8.80147L1.06263 8.8015ZM9.68699 14.077C8.48129 13.5846 6.85553 12.7707 5.08277 12.0194L5.08277 18.0522C5.08277 18.0522 7.37554 20.4651 11.4059 20.4651C15.7464 20.4651 18.091 18.0522 18.091 18.0522L18.091 12.4213C16.7227 12.9727 15.1863 13.4472 13.3269 14.077C12.1809 14.4803 10.7267 14.6196 9.68699 14.077L9.68699 14.077Z"   fill="#4E70F5" >
                        </path>
                      </svg>
                    </div>
                    <div class="title">社区专栏</div>
                  </el-col>
                </el-row>

                <el-row class="loginSummary" :span="24" type="flex" justify="space-around">
                  <el-col :span="8" class="vipIconItem">
                    <div class="icon">
                      <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="23" height="23" viewBox="0 0 23 23" fill="none">
                        <path d="M17.751 8.64775L13.2791 8.64775L16.163 2.44629C16.3068 2.01279 15.8755 1.43555 15.442 1.43555L9.67408 1.43555C9.38658 1.43555 9.09684 1.5793 8.95309 1.86904L4.62487 13.4072C4.48112 13.9845 4.91237 14.4157 5.34587 14.4157L10.3951 14.4157L8.95309 20.9069L8.95309 21.0507C8.95309 21.3382 9.24059 21.6279 9.53033 21.6279C9.67408 21.6279 9.96383 21.4842 9.96383 21.3404L18.3283 9.79999C18.6158 9.22275 18.3283 8.64775 17.751 8.64775Z"   fill="#4E70F5" >
                        </path>
                      </svg>
                    </div>
                    <div class="title">高优响应</div>
                  </el-col>
                  <el-col :span="8" class="vipIconItem">
                    <div class="icon">
                      <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="21" height="21" viewBox="0 0 21 21" fill="none">
                        <path d="M19.6689 13.1248L18.3577 13.1248L18.3577 15.7497L15.7353 13.1248L7.86813 13.1248C7.14395 13.1248 6.55693 12.5372 6.55693 11.8123L6.55693 2.62497C6.55693 1.90009 7.14395 1.3125 7.86813 1.3125L19.6688 1.3125C20.393 1.3125 20.9801 1.90009 20.9801 2.62497L20.9801 11.8123C20.9801 12.5372 20.393 13.1248 19.6688 13.1248L19.6689 13.1248ZM9.83494 6.56238C9.47292 6.56238 9.17933 6.85623 9.17933 7.21863C9.17933 7.58102 9.4729 7.87488 9.83494 7.87488C10.197 7.87488 10.4905 7.58102 10.4905 7.21863C10.4905 6.85623 10.1969 6.56238 9.83494 6.56238ZM13.7685 6.56238C13.4065 6.56238 13.1129 6.85623 13.1129 7.21863C13.1129 7.58102 13.4065 7.87488 13.7685 7.87488C14.1306 7.87488 14.4241 7.58102 14.4241 7.21863C14.4241 6.85623 14.1305 6.56238 13.7685 6.56238ZM17.7021 6.56238C17.3401 6.56238 17.0465 6.85623 17.0465 7.21863C17.0465 7.58102 17.3401 7.87488 17.7021 7.87488C18.0641 7.87488 18.3577 7.58102 18.3577 7.21863C18.3577 6.85623 18.0641 6.56238 17.7021 6.56238ZM14.4241 14.4372L14.4241 17.0622C14.4241 17.7871 13.8371 18.3746 13.1129 18.3746L5.24575 18.3746L2.62335 20.9995L2.62335 18.3746L1.31217 18.3746C0.587991 18.3746 0.000976562 17.7871 0.000976562 17.0622L0.000976562 7.87486C0.000976562 7.14997 0.587991 6.56238 1.31217 6.56238L5.24575 6.56238L5.24575 13.1248C5.24575 13.8496 5.83277 14.4372 6.55693 14.4372L14.4241 14.4372L14.4241 14.4372Z"   fill="#4E70F5" >
                        </path>
                      </svg>

                    </div>
                    <div class="title">1V1交流</div>
                  </el-col>
                  <el-col :span="8" class="vipIconItem">
                    <div class="icon">
                      <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="23" height="23" viewBox="0 0 23 23" fill="none">
                        <path d="M17.0546 17.7675C17.2616 16.997 17.5951 16.7095 17.4341 13.5125C22.9425 8.05 22.9885 0.483 22.9885 0.023L23 0.0115L22.9885 0.0115L22.9885 0L22.9771 0.0115C22.5171 0.0115 14.9501 0.0575 9.4876 5.5545C6.2906 5.3935 5.9916 5.727 5.2326 5.934C3.84111 6.325 0.391111 10.0165 0.0576107 11.3045C-0.275888 12.5925 1.09261 12.788 1.58711 12.834C2.08161 12.88 4.76111 12.581 4.99111 12.949C4.99111 12.949 5.4166 13.2365 5.4396 14.03C5.4626 14.812 5.8306 15.893 6.4056 16.5715L6.4171 16.583L6.4286 16.5945C7.1071 17.1695 8.1881 17.5375 8.9701 17.5605C9.7636 17.5835 10.0511 18.009 10.0511 18.009C10.4076 18.239 10.1201 20.9185 10.1661 21.413C10.2121 21.9075 10.4191 23.2875 11.6956 22.9425C12.9836 22.6089 16.6751 19.159 17.0546 17.7675ZM13.2596 9.74049C12.2016 8.717 12.1671 7.015 13.2021 5.957C14.2256 4.899 15.9276 4.8645 16.9856 5.8995L17.0431 5.957C18.0666 7.015 18.0436 8.717 16.9856 9.74049C15.9506 10.741 14.3061 10.741 13.2596 9.74049ZM7.0611 18.7105C6.2216 18.5955 5.6236 18.2275 5.1981 17.779C4.74961 17.342 4.39311 16.744 4.26661 15.916C4.14011 15.18 3.72611 14.835 3.25461 14.904C2.78311 14.9615 1.24211 16.652 1.13861 17.2385C1.03511 17.8365 1.90911 17.549 2.35761 17.963C2.80611 18.3655 2.30011 19.5845 1.96661 20.2285C1.79411 20.562 1.70211 20.9185 1.85161 21.114C2.04711 21.275 2.40361 21.1715 2.73711 20.999C3.39261 20.6655 4.60011 20.171 5.0026 20.6195C5.4051 21.068 5.12911 21.9305 5.7271 21.8385C6.3251 21.735 8.0041 20.194 8.0616 19.7225C8.1306 19.2625 7.7971 18.837 7.0611 18.7105Z"   fill="#4E70F5" >
                        </path>
                      </svg>

                    </div>
                    <div class="title">社区源码</div>
                  </el-col>
                </el-row>

                <el-row>
                  <button class="vipButton" style="width: 100%; margin-top: 20px; cursor: pointer;">开通会员
                  </button>
                </el-row>
              </el-card>
            </el-dropdown-item>
          </el-dropdown-menu>

        </el-dropdown>


        <el-dropdown @command="handleCommand" class="notice" v-if="componentShowMap.showUserNotice" placement="bottom">
          <span class="el-dropdown-link">
              <i class="el-icon-bell">
                <el-badge is-dot style="margin-top: -25px;" class="item" :value="userReceiveCommentCount"
                          :hidden="(userReceiveCommentCount + userReceiveWatchCount + userReceivePraiseCount + userReceiveSystemCount + userReceiveMessageCount + userReceiveCollectCount) == 0"></el-badge>
              </i>
          </span>

          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="comment">
              评论
              <el-badge is-dot :value="userReceiveCommentCount" class="item"
                        :hidden="userReceiveCommentCount == 0"></el-badge>
            </el-dropdown-item>
            <el-dropdown-item command="watch">
              关注
              <el-badge is-dot :value="userReceiveWatchCount" class="item"
                        :hidden="userReceiveWatchCount == 0"></el-badge>
            </el-dropdown-item>
            <el-dropdown-item command="praise">
              点赞
              <el-badge is-dot :value="userReceivePraiseCount" class="item"
                        :hidden="userReceivePraiseCount == 0"></el-badge>
            </el-dropdown-item>
            <el-dropdown-item command="collect">
              收藏
              <el-badge is-dot :value="userReceiveCollectCount" class="item"
                        :hidden="userReceiveCollectCount == 0"></el-badge>
            </el-dropdown-item>
            <el-dropdown-item command="system">
              系统通知
              <el-badge is-dot :value="userReceiveSystemCount" class="item"
                        :hidden="userReceiveSystemCount == 0"></el-badge>
            </el-dropdown-item>
            <el-dropdown-item command="message" v-if="componentShowMap.showChatRoom">
              私信
              <el-badge is-dot :value="userReceiveMessageCount" class="item"
                        :hidden="userReceiveMessageCount == 0"></el-badge>
            </el-dropdown-item>
          </el-dropdown-menu>

        </el-dropdown>

        <el-dropdown @command="handleCommand" class="userInfoAvatar" trigger="hover" placement="bottom">
        <span class="el-dropdown-link">
            <img v-if="!isLogin" src="../../static/images/defaultAvatar.png">
            <img :class="userInfo.userTag > 0 ?'vip-color':''" v-if="isLogin&&userInfo.photoUrl!=undefined"
                 :src="userInfo.photoUrl" onerror="onerror=null;src=defaultAvatar">
            <img v-if="isLogin&&userInfo.photoUrl==undefined"
                 :src="defaultAvatar">
            <span class="vip-text-home pointer" v-if="userInfo.userTag > 0">v</span>
        </span>

          <el-dropdown-menu slot="dropdown" class="userDropdownMenu">

            <el-dropdown-item command="login" v-show="!isLogin" class="userDropdownMenuItem">
              <el-card :body-style="{ padding: '20px' }" style="width: 280px" class="cardStyle" v-if="!isLogin">
                <el-row class="loginText">
                  <span>登录网站，可立即获得以下权益：</span>
                </el-row>
                <el-row class="loginSummary" :span="24">
                  <el-col :span="12">
                    <span class="iconfont">&#xe606;</span>&nbsp;收藏有用内容
                  </el-col>
                  <el-col :span="12">
                    <span class="iconfont">&#xe606;</span>&nbsp;阅读优质专栏
                  </el-col>
                </el-row>
                <el-row class="loginSummary" :span="24">
                  <el-col :span="12">
                    <span class="iconfont">&#xe606;</span>&nbsp;进行评论互动
                  </el-col>
                  <el-col :span="12">
                    <span class="iconfont">&#xe606;</span>&nbsp;提升成长等级
                  </el-col>
                </el-row>
                <el-row>
                  <el-button type="primary" style="width: 100%; margin-top: 20px;">立即登录</el-button>
                </el-row>
              </el-card>

            </el-dropdown-item>
            <el-dropdown-item command="goUserCenter" v-show="isLogin">我的主页</el-dropdown-item>
            <el-dropdown-item command="goUserInfo" v-show="isLogin">个人中心</el-dropdown-item>
            <el-dropdown-item command="logout" v-show="isLogin">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>


      </nav>
    </header>

    <WebNotice></WebNotice>

    <!--登录框-->
    <LoginBox v-if="showLogin" @closeLoginBox="closeLoginBox"></LoginBox>

    <!--勋章-->
    <Medal ref="medal"></Medal>

    <el-drawer
      :append-to-body="dialogFormVisible"
      :modal-append-to-body="false"
      @close="closePersonCenter"
      :show-close="true"
      :visible.sync="drawer"
      :size="drawerSize"
      :with-header="false">

      <el-tabs type="border-card" tab-position="left" v-model="activeName"
               style="margin-top: 0px; height: 94%; overflow-y: scroll" @tab-click="handleClick">
        <el-tab-pane label="个人中心" name="个人中心">
          <span slot="label"><i class="el-icon-user-solid"></i> 个人中心</span>
          <el-form label-position="left" :model="userInfo" label-width="100px" :rules="rules" ref="userInfo">
            <el-form-item label="头像" :label-width="labelWidth">

              <div class="imgBody" v-if="userInfo.photoUrl">
                <i class="el-icon-error inputClass" v-show="icon" @click="deletePhoto('user')"
                   @mouseover="icon = true"></i>
                <img @mouseover="icon = true" @mouseout="icon = false" v-bind:src="userInfo.photoUrl"/>
              </div>

              <div v-else class="uploadImgBody" @click="checkPhoto">
                <i class="el-icon-plus avatar-uploader-icon"></i>
              </div>
            </el-form-item>

            <el-form-item label="昵称" :label-width="labelWidth" prop="nickName">
              <el-input v-model="userInfo.nickName" style="width: 100%"></el-input>
            </el-form-item>


            <el-form-item label="性别" :label-width="labelWidth">
              <el-radio v-for="gender in genderDictList" :key="gender.uid" v-model="userInfo.gender"
                        :label="gender.dictValue" border size="medium">{{ gender.dictLabel }}
              </el-radio>
            </el-form-item>

            <el-form-item label="钱包" :label-width="labelWidth" prop="occupation">
              <span style="font-weight: bold">{{ userAmount / 100 }}</span><span> 元</span>
              <el-button v-if="userAmount > 0" style="margin-left: 10px" type="warning" size="mini"
                         @click="withdrawAmount">提现
              </el-button>
            </el-form-item>

            <!--          <el-form-item label="生日" :label-width="labelWidth">-->
            <!--            <el-date-picker-->
            <!--              v-model="userInfo.birthday"-->
            <!--              type="date"-->
            <!--              placeholder="选择日期">-->
            <!--            </el-date-picker>-->
            <!--          </el-form-item>-->

            <el-form-item label="邮件通知" :label-width="labelWidth">
              <el-radio v-for="item in yesNoDictList" :key="item.uid" v-model="userInfo.startEmailNotification"
                        :label="parseInt(item.dictValue)" border size="medium">{{ item.dictLabel }}
              </el-radio>
            </el-form-item>

            <el-form-item label="编辑器" :label-width="labelWidth">
              <el-radio v-for="item in editorModalDictList" :key="item.uid" v-model="userInfo.editorModel"
                        :label="item.dictValue" border size="medium">{{ item.dictLabel }}
              </el-radio>
            </el-form-item>

            <el-form-item label="邮箱" :label-width="labelWidth" prop="email">
              <el-input v-model="userInfo.email" style="width: 100%"></el-input>
            </el-form-item>

            <!--          <el-form-item label="QQ号" :label-width="labelWidth" prop="qqNumber">-->
            <!--            <el-input v-model="userInfo.qqNumber" style="width: 100%"></el-input>-->
            <!--          </el-form-item>-->

            <el-form-item label="职业" :label-width="labelWidth" prop="occupation">
              <el-input v-model="userInfo.occupation" style="width: 100%"></el-input>
            </el-form-item>

            <el-form-item label="简介" :label-width="labelWidth" prop="summary">
              <el-input
                type="textarea"
                :autosize="{ minRows: 3, maxRows: 5}"
                placeholder="请输入内容"
                style="width: 100%"
                v-model="userInfo.summary">
              </el-input>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="submitForm('editUser')">保 存</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>


        <el-tab-pane label="账号绑定" name="账号绑定">
          <span slot="label"><i class="el-icon-s-flag"></i> 账号绑定</span>
          <div :style="{'height': minHeight - 65 + 'px'}" style="width: 100%; overflow:auto;">

            <el-table :data="accountList"
                      style="width: 100%">
              <el-table-column label="序号" width="50" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.$index + 1 }}</span>
                </template>
              </el-table-column>

              <el-table-column label="渠道" width="100" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.sourceDesc }}</span>
                </template>
              </el-table-column>

              <el-table-column label="用户" width="150" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.nickName }}</span>
                </template>
              </el-table-column>

              <el-table-column label="绑定时间" width="160" align="center">
                <template slot-scope="scope">
                  <span>{{ scope.row.bindTime }}</span>
                </template>
              </el-table-column>
            </el-table>

            <div style="margin-top: 5px; margin-bottom: 5px;">你还可以绑定以下第三方帐号进行登录</div>
            <el-tooltip content="码云" placement="bottom" v-if="!loginType.gitee">
              <el-button type="danger" circle @click="goBind('gitee')" :disabled="loginType.gitee">
                <span class="iconfont">&#xe602;</span>
              </el-button>
            </el-tooltip>

            <el-tooltip content="Github" placement="bottom" v-if="!loginType.github">
              <el-button type="info" circle @click="goBind('github')" :disabled="loginType.github">
                <span class="iconfont">&#xe64a;</span>
              </el-button>
            </el-tooltip>

            <el-tooltip content="QQ" placement="bottom" v-if="!loginType.qq">
              <el-button type="primary" circle @click="goBind('qq')" :disabled="loginType.qq">
                <span class="iconfont">&#xe601;</span>
              </el-button>
            </el-tooltip>

            <el-tooltip content="微信公众号" placement="bottom" v-if="!loginType.wechat">
              <el-button type="success" circle @click="goBind('wechat')" :disabled="loginType.wechat">
                <span class="iconfont">&#xe60b;</span>
              </el-button>
            </el-tooltip>

            <el-tooltip content="微信小程序" placement="bottom" v-if="!loginType.mini">
              <el-button type="success" circle @click="goBind('mini')" :disabled="loginType.mini">
                <span class="iconfont">&#xe668;</span>
              </el-button>
            </el-tooltip>

            <el-tooltip content="微信" placement="bottom" v-if="!loginType.personWechat">
              <el-button type="success" circle @click="goBind('person_wechat')" :disabled="loginType.personWechat">
                <span class="iconfont">&#xe66f;</span>
              </el-button>
            </el-tooltip>

          </div>
        </el-tab-pane>

        <el-tab-pane label="我的评论" name="我的评论">
          <span slot="label"><i class="el-icon-message-solid"></i> 我的评论</span>
          <div :style="{'height': minHeight - 65 + 'px'}" style="width: 100%; overflow:auto;">
            <el-timeline>
              <el-timeline-item v-for="comment in commentList" :key="comment.uid"
                                :timestamp="timeAgo(comment.createTime)" placement="top">
                <el-card>
                  <div class="commentList">
                <span class="left p1">
                  <img v-if="comment.user" :src="comment.user.photoUrl ? comment.user.photoUrl:defaultAvatar"
                       onerror="onerror=null;src=defaultAvatar"/>
                  <img v-else :src="defaultAvatar"/>
                </span>

                    <span class="right p1">
                  <div class="rightTop">
                    <el-link class="userName" :underline="false">{{ comment.user.nickName }}</el-link>
                    <el-tag style="cursor: pointer;" @click.native="goSource(comment)">{{ comment.sourceName }}</el-tag>
                  </div>

                  <div class="rightCenter ck-content" v-highlight v-html="$xss(comment.content, options)"></div>
                </span>
                  </div>
                </el-card>
              </el-timeline-item>

              <span v-if="isEnd && commentList.length == 0">
                  <el-empty description="空空如也" image=""></el-empty>
            </span>

            </el-timeline>

            <div class="isEnd">
              <div class="loadContent" @click="loadMore('comment')" v-if="!isEnd&&!loading">查看更多</div>
              <div class="lds-css ng-scope" v-if="!isEnd&&loading">
                <div style="width:100%;height:100%" class="lds-facebook">
                  <div></div>
                  <div></div>
                  <div></div>
                </div>
              </div>
              <span v-if="isEnd &&  commentList.length > 0 ">我也是有底线的~</span>
            </div>

          </div>
        </el-tab-pane>

        <el-tab-pane label="我的回复" name="我的回复">
        <span slot="label">
          <i class="el-icon-s-promotion"></i> 我的回复
        </span>
          <div :style="{'height': minHeight - 65 + 'px'}" style="width: 100%; overflow:auto">
            <el-timeline>
              <el-timeline-item v-for="reply in replyList" :key="reply.uid" :timestamp="timeAgo(reply.createTime)"
                                placement="top">
                <el-card>
                  <div class="commentList">
                  <span class="left p1">
                    <img v-if="reply.user" :src="reply.user.photoUrl ? reply.user.photoUrl:defaultAvatar"
                         onerror="onerror=null;src=defaultAvatar"/>
                    <img v-else :src="defaultAvatar"/>
                  </span>

                    <span class="right p1">

                      <div class="rightTop">
                        <el-link class="userName" :underline="false">{{ reply.user.nickName }}</el-link>
                        <el-tag style="cursor: pointer;" @click.native="goSource(reply)">{{ reply.sourceName }}</el-tag>
                      </div>

                      <div class="rightCenter ck-content" v-highlight v-html="$xss(reply.content, options)"></div>
                  </span>
                  </div>
                </el-card>
              </el-timeline-item>

              <span v-if="isEnd && replyList.length == 0">
                  <el-empty description="空空如也" image=""></el-empty>
            </span>

            </el-timeline>

            <div class="isEnd">
              <div class="loadContent" @click="loadMore('reply')" v-if="!isEnd&&!loading">查看更多</div>
              <div class="lds-css ng-scope" v-if="!isEnd&&loading">
                <div style="width:100%;height:100%" class="lds-facebook">
                  <div></div>
                  <div></div>
                  <div></div>
                </div>
              </div>
              <span v-if="isEnd  &&  replyList.length > 0 ">我也是有底线的~</span>
            </div>

          </div>
        </el-tab-pane>

        <el-tab-pane v-if="showCreateBlog" label="我的文章" name="我的文章">
          <span slot="label"><i class="el-icon-s-order"></i> 我的文章</span>

          <div class="filter-container" style="margin: 10px 0 10px 0;">
            <el-form :inline="true" style="margin-bottom: 8px;">
              <el-input
                @input="getBlogList(true)"
                size="mini"
                clearable
                class="filter-item"
                style="width: 120px;"
                v-model="queryParams.keyword"
                placeholder="请输入博客名"
              ></el-input>

              <el-select @change="getBlogList(true)" size="mini" v-model="queryParams.publishKeyword" clearable
                         placeholder="是否发布" style="width:100px">
                <el-option
                  v-for="item in blogPublishDictList"
                  :key="item.uid"
                  :label="item.dictLabel"
                  :value="item.dictValue"
                ></el-option>
              </el-select>

              <el-select @change="getBlogList(true)" size="mini" v-model="queryParams.originalKeyword" clearable
                         placeholder="是否原创" style="width:110px">
                <el-option
                  v-for="item in blogOriginalDictList"
                  :key="item.uid"
                  :label="item.dictLabel"
                  :value="item.dictValue"
                ></el-option>
              </el-select>

              <el-select @change="getBlogList(true)" size="mini" v-model="queryParams.auditStatusKeyword" clearable
                         placeholder="审核状态" style="width:110px">
                <el-option
                  v-for="item in auditStatusDictList"
                  :key="item.uid"
                  :label="item.dictLabel"
                  :value="item.dictValue"
                ></el-option>
              </el-select>
            </el-form>

          </div>

          <div :style="{'height': minHeight - 65 + 'px'}" style="width: 100%; overflow:auto;">
            <el-timeline>
              <div
                v-for="item in userBlogList"
                :key="item.uid"
                class="myArticle"
              >
                <el-timeline-item :timestamp="item.createTime" placement="top">
                  <el-card>

                    <el-row :gutter="24">
                      <el-col :span="(item.photoList && item.photoList.length > 0)?12:0">
                          <span class="blogpic" @click="goToInfo(item)">
                            <a href="javascript:void(0);" title>
                              <img v-if="item.photoList && item.photoList.length > 0 " v-lazy="item.photoList[0]"
                                   :key="item.photoList[0]" alt>
                            </a>
                          </span>
                      </el-col>

                      <el-col :span="(item.photoList && item.photoList.length > 0)?12:24">
                        <div style="height: 70px;">
                          <p class="blogtext" style="font-weight: bold; cursor: pointer;" @click="goToInfo(item)">
                            {{ item.title }}</p>
                        </div>
                      </el-col>

                    </el-row>

                    <div class="bloginfo">
                      <ul>
                        <li class="author">
                          <span class="iconfont">&#xe60f;</span>
                          <a href="javascript:void(0);" @click="goToAuthor(item.author)">{{ item.author }}</a>
                        </li>
                        <li class="lmname" v-if="item.blogSort">
                          <span class="iconfont">&#xe603;</span>
                          <a
                            href="javascript:void(0);"
                            @click="goToList(item.blogSort.uid)"
                          >{{ item.blogSort.sortName }}</a>
                        </li>
                        <li class="view">
                          <span class="iconfont">&#xe8c7;</span>
                          <span>{{ item.clickCount }}</span>
                        </li>
                        <li class="like">
                          <span class="iconfont">&#xe663;</span>
                          {{ item.collectCount }}
                        </li>
                      </ul>
                    </div>

                    <div style="margin-top: 5px; float: left">
                      <el-tag v-if="item.isPublish==1" type="success" style="font-size: 12px">已上架</el-tag>
                      <el-tag v-if="item.isPublish==0" type="info" style="font-size: 12px">未发布</el-tag>

                      <el-tag v-if="item.auditStatus==0" type="info" style="font-size: 12px">待审批</el-tag>
                      <el-tag v-if="item.auditStatus==1" content="预览" type="danger" style="font-size: 12px">
                        <el-tooltip :disabled="item.length <= 10" class="item" placement="top">
                          <div slot="content">{{ item.rejectReason }}</div>
                          <div>审核未通过</div>
                        </el-tooltip>
                      </el-tag>
                      <el-tag v-if="item.auditStatus==2" type="success" style="font-size: 12px">审核已通过</el-tag>
                    </div>

                    <div class="operation">
                      <el-row :gutter="24">
                        <el-tooltip v-if="item.isPublish == '0' && item.auditStatus == '2'" class="item" effect="dark"
                                    content="发布" placement="top">
                          <el-button circle type="success" size="small" icon="el-icon-document-checked"
                                     @click="handlePublish(item)"></el-button>
                        </el-tooltip>

                        <el-tooltip v-if="item.isPublish== '1' && item.auditStatus == '2'" class="item" effect="dark"
                                    content="下架" placement="top">
                          <el-button circle type="danger" size="small" icon="el-icon-document-delete"
                                     @click="handlePublish(item)"></el-button>
                        </el-tooltip>

                        <el-tooltip
                          v-if="(item.auditStatus == '0'  && item.isPublish == '0') || (item.auditStatus == '1')"
                          class="item" effect="dark" content="提交审核" placement="top">
                          <el-button circle type="success" size="small" icon="el-icon-user"
                                     @click="handleAudit(item)"></el-button>
                        </el-tooltip>

                        <el-tooltip class="item" effect="dark" content="编辑" placement="top">
                          <el-button circle type="primary" size="small" icon="el-icon-edit"
                                     @click="createBlog(true, item)"></el-button>
                        </el-tooltip>
                        <el-tooltip class="item" effect="dark" content="预览" placement="top">
                          <el-button circle type="info" size="small" icon="el-icon-search"
                                     @click="goToInfo(item)"></el-button>
                        </el-tooltip>
                        <el-tooltip v-if="item.auditStatus== '2' && item.isPublish == '1'" class="item" effect="dark"
                                    content="导出Markdown" placement="top">
                          <el-button circle type="warning" size="small" icon="el-icon-download"
                                     @click="handleDownload(item, 'blog')"></el-button>
                        </el-tooltip>
                        <el-tooltip class="item" effect="dark" content="删除" placement="top">
                          <el-button circle type="danger" size="small" icon="el-icon-delete"
                                     @click="handleDeleteBlog(item)"></el-button>
                        </el-tooltip>
                      </el-row>
                    </div>

                  </el-card>
                </el-timeline-item>

              </div>
              <span v-if="isEnd && userBlogList.length == 0">
                <el-empty description="空空如也，快去创作吧~" image=""></el-empty>
              </span>

            </el-timeline>

            <div class="isEnd">
              <div class="loadContent" @click="loadMore('blog')" v-if="!isEnd&&!loading">查看更多</div>
              <div class="lds-css ng-scope" v-if="!isEnd&&loading">
                <div style="width:100%;height:100%" class="lds-facebook">
                  <div></div>
                  <div></div>
                  <div></div>
                </div>
              </div>
              <span v-if="isEnd  &&  userBlogList.length > 0 ">我也是有底线的~</span>
            </div>

          </div>
        </el-tab-pane>

        <el-tab-pane v-if="showCreateQuestion" label="我的问答" name="我的问答">
          <span slot="label"><i class="el-icon-s-claim"></i> 我的问答</span>

          <div class="filter-container" style="margin: 10px 0 10px 0;">
            <el-form :inline="true" style="margin-bottom: 8px;">
              <el-input
                @input="getUserQuestionList(true)"
                size="mini"
                clearable
                class="filter-item"
                style="width: 130px;"
                v-model="queryParams.keyword"
                placeholder="请输入问答名称"
              ></el-input>

              <el-select @change="getUserQuestionList(true)" size="mini" v-model="queryParams.publishKeyword" clearable
                         placeholder="是否发布" style="width:100px">
                <el-option
                  v-for="item in blogPublishDictList"
                  :key="item.uid"
                  :label="item.dictLabel"
                  :value="item.dictValue"
                ></el-option>
              </el-select>

            </el-form>

          </div>

          <div :style="{'height': minHeight - 65 + 'px'}" style="width: 100%; overflow:auto;">
            <el-timeline>
              <div
                v-for="item in userQuestionList"
                :key="item.uid"
                class="myArticle"
              >
                <el-timeline-item :timestamp="item.createTime" placement="top">
                  <el-card>

                    <el-row :gutter="24">
                      <el-col :span="24">
                        <div style="height: 70px;">
                          <p class="blogtext" style="font-weight: bold; cursor: pointer;"
                             @click="goToQuestionInfo(item)">{{ item.title }}</p>
                        </div>
                      </el-col>

                    </el-row>

                    <div class="bloginfo">
                      <ul>
                        <li class="author">
                          <span class="iconfont">&#xe60f;</span>
                          <a href="javascript:void(0);">{{ item.user.nickName }}</a>
                        </li>
                        <li class="lmname" v-if="item.blogSort">
                          <span class="iconfont">&#xe603;</span>
                          <a
                            href="javascript:void(0);"
                            @click="goToList(item.blogSort.uid)"
                          >{{ item.blogSort.sortName }}</a>
                        </li>
                        <li class="view">
                          <span class="iconfont">&#xe8c7;</span>
                          <span>{{ item.clickCount }}</span>
                        </li>
                        <li class="like">
                          <span class="iconfont">&#xe663;</span>
                          {{ item.collectCount }}
                        </li>
                      </ul>
                    </div>

                    <div style="margin-top: 5px; float: left">
                      <el-tag v-if="item.isPublish==1" type="success" style="font-size: 12px">已上架</el-tag>
                      <el-tag v-if="item.isPublish==0" type="info" style="font-size: 12px">未发布</el-tag>
                    </div>

                    <div class="operation">
                      <el-row :gutter="24">
                        <el-tooltip class="item" effect="dark" content="编辑" placement="top">
                          <el-button circle type="primary" size="small" icon="el-icon-edit"
                                     @click="createQuestion(true, item)"></el-button>
                        </el-tooltip>
                        <el-tooltip class="item" effect="dark" content="预览" placement="top">
                          <el-button circle type="info" size="small" icon="el-icon-search"
                                     @click="goToQuestionInfo(item)"></el-button>
                        </el-tooltip>
                        <el-tooltip v-if="item.isPublish == '1'" class="item" effect="dark" content="导出Markdown"
                                    placement="top">
                          <el-button circle type="warning" size="small" icon="el-icon-download"
                                     @click="handleDownload(item, 'question')"></el-button>
                        </el-tooltip>
                        <el-tooltip class="item" effect="dark" content="删除" placement="top">
                          <el-button circle type="danger" size="small" icon="el-icon-delete"
                                     @click="handleDeleteQuestion(item)"></el-button>
                        </el-tooltip>
                      </el-row>
                    </div>

                  </el-card>
                </el-timeline-item>
              </div>

              <span v-if="isEnd && userQuestionList.length == 0">
                <el-empty description="空空如也，快去创作吧~" image=""></el-empty>
              </span>
            </el-timeline>

            <div class="isEnd">
              <div class="loadContent" @click="loadMore('question')" v-if="!isEnd&&!loading">查看更多</div>
              <div class="lds-css ng-scope" v-if="!isEnd&&loading">
                <div style="width:100%;height:100%" class="lds-facebook">
                  <div></div>
                  <div></div>
                  <div></div>
                </div>
              </div>
              <span v-if="isEnd && userQuestionList.length > 0">我也是有底线的~</span>
            </div>

          </div>
        </el-tab-pane>

        <el-tab-pane label="我的收藏" name="我的收藏">
          <span slot="label"><i class="el-icon-s-order"></i> 我的收藏</span>
          <div :style="{'height': minHeight - 65 + 'px'}" style="width: 100%; overflow:auto;">
            <el-timeline>
              <div
                v-for="item in collectList"
                :key="item.uid"
                class="myArticle"
              >
                <el-timeline-item :timestamp="item.createTime" placement="top">
                  <el-card>

                    <el-row :gutter="24">
                      <el-col :span="24">
                        <div style="height: 30px;">
                          <p v-if="item.collectType == '1' && item.blog" class="blogtext"
                             style="font-weight: bold; cursor: pointer;" @click="goToInfo(item.blog)">
                            {{ item.blog.title }}</p>
                          <p v-if="item.collectType == '2' && item.question" class="blogtext"
                             style="font-weight: bold; cursor: pointer;" @click="goToQuestionInfo(item.question)">
                            {{ item.question.title }}</p>
                          <p v-if="item.collectType == '4' && item.userMoment" class="blogtext"
                             style="font-weight: bold; cursor: pointer;" @click="goToUserMoment(item.userMoment)">
                            用户动态</p>
                          <p v-if="item.collectType == '6' && item.problem" class="blogtext"
                             style="font-weight: bold; cursor: pointer;" @click="goToProblem(item.problem)">
                            {{ item.problem.title }}</p>
                          <p v-if="item.collectType == '11' && item.resource" class="blogtext"
                             style="font-weight: bold; cursor: pointer;" @click="goToResource(item.resource)">
                            {{ item.resource.name }}</p>
                        </div>
                      </el-col>

                    </el-row>

                    <div v-if="item.collectType == '1' && item.blog" class="rightCenter ck-content"
                         style="min-height: 50px; color: #8F8F8F" v-highlight
                         v-html="$xss(item.blog.summary, options)"></div>
                    <div v-if="item.collectType == '2' && item.question" class="rightCenter ck-content"
                         style="min-height: 50px; color: #8F8F8F" v-highlight
                         v-html="$xss(item.question.summary, options)"></div>
                    <div v-if="item.collectType == '4' && item.userMoment" class="rightCenter ck-content"
                         style="min-height: 50px; color: #8F8F8F" v-highlight
                         v-html="$xss(item.userMoment.content, options)"></div>
                    <div v-if="item.collectType == '6' && item.problem" class="rightCenter ck-content"
                         style="min-height: 50px; color: #8F8F8F" v-highlight
                         v-html="$xss(item.problem.summary, options)"></div>
                    <div v-if="item.collectType == '11' && item.resource" class="rightCenter ck-content"
                         style="min-height: 50px; color: #8F8F8F" v-highlight
                         v-html="$xss(item.resource.summary, options)"></div>
                    <div class="bloginfo">
                      <ul>
                        <li class="author pointer">
                          <el-avatar :class="item.user.userTag > 0 ?'vip-color':''" v-if="item.user"
                                     @click.native="getUserCenter(item)" fit="fill" size="medium"
                                     :src="item.user.photoUrl"></el-avatar>
                          <el-avatar v-else fit="fill" size="small" src="https://empty">
                            <img :src="defaultAvatar"/>
                          </el-avatar>

                        </li>
                        <li class="lmname" style="margin-top: 10px; margin-left: -10px">
                          <a href="javascript:void(0);" v-if="item.user"
                             @click.native="getUserCenter(item)">{{ item.user.nickName }}</a>
                        </li>
                        <li v-if="item.collectType != '4'" class="view" style="margin-top: 10px; margin-left: -5px">
                          <span class="iconfont">&#xe8c7;</span>
                          <span v-if="item.collectType == '1' && item.blog">{{ item.blog.clickCount }}</span>
                          <span v-if="item.collectType == '2' && item.question ">{{ item.question.clickCount }}</span>
                          <span v-if="item.collectType == '6' && item.problem ">{{ item.problem.clickCount }}</span>
                          <span v-if="item.collectType == '11' && item.resource">{{ item.resource.clickCount }}</span>
                        </li>
                        <li v-if="item.collectType != '4'" class="like" style="margin-top: 10px; margin-left: -5px">
                          <span class="iconfont">&#xe663;</span>
                          <span v-if="item.collectType == '1' && item.blog">{{ item.blog.collectCount }}</span>
                          <span v-if="item.collectType == '2' && item.question">{{ item.question.collectCount }}</span>
                          <span v-if="item.collectType == '6' && item.problem">{{ item.problem.collectCount }}</span>
                          <span v-if="item.collectType == '11' && item.resource">{{ item.resource.collectCount }}</span>
                        </li>

                        <li style="margin-top: 10px; margin-left: -5px">
                          <span v-if="item.collectType == '1'  && item.blog">
                            <el-tag type="primary" style="font-size: 12px">博客</el-tag>
                            <el-tag v-if="item.blog.isPublish==1" type="success" style="font-size: 12px">已上架</el-tag>
                            <el-tag v-if="item.blog.isPublish==0" type="info" style="font-size: 12px">未发布</el-tag>
                          </span>
                          <span v-if="item.collectType == '2'  && item.question">
                            <el-tag type="warning" style="font-size: 12px">问答</el-tag>
                            <el-tag v-if="item.question.isPublish==1" type="success"
                                    style="font-size: 12px">已上架</el-tag>
                            <el-tag v-if="item.question.isPublish==0" type="info"
                                    style="font-size: 12px">未发布</el-tag>
                          </span>
                          <span v-if="item.collectType == '4'  && item.userMoment">
                            <el-tag type="warning" style="font-size: 12px">动态</el-tag>
                            <el-tag v-if="item.userMoment.isPublish==1" type="success"
                                    style="font-size: 12px">已上架</el-tag>
                            <el-tag v-if="item.userMoment.isPublish==0" type="info"
                                    style="font-size: 12px">未发布</el-tag>
                          </span>
                          <span v-if="item.collectType == '6'  && item.problem">
                            <el-tag type="warning" style="font-size: 12px">问题</el-tag>
                            <el-tag v-if="item.problem.isPublish==1" type="success"
                                    style="font-size: 12px">已上架</el-tag>
                            <el-tag v-if="item.problem.isPublish==0" type="info" style="font-size: 12px">未发布</el-tag>
                          </span>
                          <span v-if="item.collectType == '11'  && item.resource">
                            <el-tag type="warning" style="font-size: 12px">资源</el-tag>
                            <el-tag v-if="item.resource.isPay" type="success"
                                    style="font-size: 12px">已购买</el-tag>
                            <el-tag v-if="!item.resource.isPay" type="info" style="font-size: 12px">未购买</el-tag>
                          </span>
                        </li>

                        <li style="margin-top: 5px; margin-left: -3px">
                          <el-tooltip class="item" effect="dark" content="取消收藏" placement="top">
                            <el-button circle type="danger" size="small" icon="el-icon-delete"
                                       @click="cancelCollect(item.bizUid, item.collectType)"></el-button>
                          </el-tooltip>
                        </li>
                      </ul>
                    </div>

                  </el-card>
                </el-timeline-item>
              </div>

              <span v-if="isEnd && collectList.length == 0">
                <el-empty description="空空如也，快去收藏内容吧~" image=""></el-empty>
              </span>
            </el-timeline>

            <div class="isEnd">
              <div class="loadContent" @click="loadMore('collect')" v-if="!isEnd&&!loading">查看更多</div>
              <div class="lds-css ng-scope" v-if="!isEnd&&loading">
                <div style="width:100%;height:100%" class="lds-facebook">
                  <div></div>
                  <div></div>
                  <div></div>
                </div>
              </div>
              <span v-if="isEnd && collectList.length > 0">我也是有底线的~</span>
            </div>

          </div>
        </el-tab-pane>

        <!--      <el-tab-pane label="我的点赞" name="4">-->
        <!--        <span slot="label"><i class="el-icon-star-on"></i> 我的点赞</span>-->
        <!--        <div style="width: 100%; height: 840px;overflow:auto">-->
        <!--          <el-timeline>-->
        <!--            <el-timeline-item v-for="praise in praiseList" :key="praise.uid" :timestamp="timeAgo(praise.createTime)" placement="top">-->
        <!--              <el-card>-->
        <!--                <span>点赞</span><el-tag type="warning" style="cursor: pointer" v-if="praise.blog" @click.native="goToInfo(praise.blog)">{{praise.blog.title}}</el-tag>-->
        <!--              </el-card>-->
        <!--            </el-timeline-item>-->

        <!--            <span v-if="isEnd && praiseList.length == 0">-->
        <!--                <el-empty description="空空如也" image=""></el-empty>-->
        <!--              </span>-->


        <!--          </el-timeline>-->

        <!--          <div class="isEnd">-->
        <!--            <div class="loadContent" @click="loadMore('praise')" v-if="!isEnd&&!loading">查看更多</div>-->
        <!--            <div class="lds-css ng-scope" v-if="!isEnd&&loading">-->
        <!--              <div style="width:100%;height:100%" class="lds-facebook">-->
        <!--                <div></div>-->
        <!--                <div></div>-->
        <!--                <div></div>-->
        <!--              </div>-->
        <!--            </div>-->
        <!--            <span v-if="isEnd && praiseList.length > 0">我也是有底线的~</span>-->
        <!--          </div>-->

        <!--        </div>-->
        <!--      </el-tab-pane>-->

        <el-tab-pane label="积分明细" name="积分明细">
          <span slot="label"><i class="el-icon-coin"></i> 积分明细</span>
          <div :style="{'height': minHeight - 65 + 'px'}" style="width: 100%; overflow:auto">
            <el-timeline>
              <el-timeline-item v-for="creditLog in creditsLogList" :key="creditLog.uid"
                                :timestamp="timeAgo(creditLog.createTime)" placement="top">
                <el-card>
                  <span>来源</span>
                  <el-tag type="primary" style="cursor: pointer">{{ creditLog.actionName }}</el-tag>
                  <el-tag v-if="creditLog.changeCredits >=0" type="success" style="cursor: pointer">
                    +{{ creditLog.changeCredits }}
                  </el-tag>
                  <el-tag v-else-if="creditLog.changeCredits < 0" type="danger" style="cursor: pointer">
                    {{ creditLog.changeCredits }}
                  </el-tag>
                  <span>积分</span>
                </el-card>
              </el-timeline-item>

              <span v-if="isEnd && creditsLogList.length == 0">
                <el-empty description="空空如也" image=""></el-empty>
              </span>


            </el-timeline>

            <div class="isEnd">
              <div class="loadContent" @click="loadMore('credits')" v-if="!isEnd&&!loading">查看更多</div>
              <div class="lds-css ng-scope" v-if="!isEnd&&loading">
                <div style="width:100%;height:100%" class="lds-facebook">
                  <div></div>
                  <div></div>
                  <div></div>
                </div>
              </div>
              <span v-if="isEnd && creditsLogList.length > 0">我也是有底线的~</span>
            </div>

          </div>

        </el-tab-pane>

        <el-tab-pane label="我的勋章" name="我的勋章">
          <span slot="label"><i class="el-icon-medal-1"></i> 我的勋章</span>

          <el-menu default-active="1" class="el-menu-demo" mode="horizontal" @select="selectMedalType">
            <el-menu-item v-for="medalType in medalTypeDictList" :index="medalType.dictValue" :key="medalType.uid">
              {{ medalType.dictLabel }}
            </el-menu-item>
          </el-menu>

          <div :style="{'height': minHeight - 65 + 'px'}" style="  width: 100%; overflow-y: scroll; margin-top: 10px;">
            <el-timeline>
              <el-timeline-item v-for="medalClassify in medalList" :key="medalClassify.uid"
                                v-if="medalClassify.medalList && medalClassify.medalList.length > 0"
                                :timestamp="medalClassify.name" placement="top">

                <el-row :gutter="22">
                  <el-col :xs="10" :sm="7" :md="7" :lg="6" :xl="6" v-for="(medal, index) in medalClassify.medalList"
                          :key="index">

                    <el-tooltip class="item" effect="dark" :content="timeFormat(medal.gainTime)"
                                placement="top-end">
                      <el-card :body-style="{ padding: '0px'}" style="margin-top: 5px;">

                        <img :class="medal.gainTime ? '': 'gainImg'" style="width: 100%; vertical-align: middle;"
                             v-lazy="medal.fileUrl">
                        <div style="padding: 14px;">
                          <div style="color: #17233f; font-weight: 600;">{{ medal.name }}</div>
                          <div style="color: #9399a6; font-weight: 500;">{{ medal.summary }}</div>
                        </div>
                      </el-card>
                    </el-tooltip>

                  </el-col>
                </el-row>

              </el-timeline-item>
            </el-timeline>
          </div>

        </el-tab-pane>

        <el-tab-pane label="我的反馈" name="我的反馈">
          <span slot="label"><i class="el-icon-phone"></i> 我的反馈</span>

          <el-collapse v-model="activeNames">
            <el-collapse-item title="反馈须知" name="1">
              <div>如果您对本站有什么想法，可以在这里进行反馈</div>
              <div>或者加入我们的QQ群进行交流</div>
            </el-collapse-item>
          </el-collapse>

          <el-divider></el-divider>

          <div style="width: 100%; height: 450px;overflow:auto">
            <el-timeline>
              <el-timeline-item v-for="feedbackItem in feedbackList" :key="feedbackItem.uid"
                                :timestamp="timeAgo(feedbackItem.createTime)" placement="top">
                <el-card class="feedbackCard">
                  <div class="item">
                  <span class="title">
                    标题:
                  </span>
                    <span class="content">
                    {{ feedbackItem.title }}
                  </span>
                  </div>

                  <div class="item">
                  <span class="title">
                    内容:
                  </span>
                    <span class="content">
                    {{ feedbackItem.content }}
                  </span>
                  </div>

                  <div class="item">
                  <span class="title">
                    反馈状态:
                  </span>
                    <span class="content">
                    <el-tag v-for="item in feedbackDictList" :key="item.uid" :type="item.listClass"
                            v-if="feedbackItem.feedbackStatus == item.dictValue">{{ item.dictLabel }}</el-tag>
                  </span>
                  </div>

                  <div class="item">
                  <span class="title">
                    回复:
                  </span>
                    <span class="content">
                    {{ feedbackItem.reply }}
                  </span>
                  </div>
                </el-card>
              </el-timeline-item>

              <span v-if="isEnd && feedbackList.length == 0">
              <el-empty description="空空如也" image=""></el-empty>
            </span>
            </el-timeline>
          </div>

          <el-divider></el-divider>

          <el-form label-position="left" :model="userInfo" label-width="100px">
            <el-form-item label="标题" :label-width="labelWidth">
              <el-input v-model="feedback.title" style="width: 100%"></el-input>
            </el-form-item>

            <el-form-item label="内容" :label-width="labelWidth">
              <el-input
                type="textarea"
                :autosize="{ minRows: 2, maxRows: 4}"
                placeholder="请输入反馈内容"
                v-model="feedback.content">
              </el-input>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="submitForm('feedback')">提 交</el-button>
            </el-form-item>
          </el-form>

        </el-tab-pane>

        <el-tab-pane label="申请友链" name="申请友链">
          <span slot="label"><i class="el-icon-share"></i> 申请友链</span>

          <el-form label-position="left" :model="blogLink" label-width="100px" ref="blogLink" :rules="linkRules">
            <el-collapse v-model="activeNames">
              <el-collapse-item title="友链申请需知" name="1">
                <span v-html="info.linkApplyTemplate">{{ info.linkApplyTemplate }}</span>
              </el-collapse-item>
            </el-collapse>

            <el-divider></el-divider>

            <el-form-item label="网站图标">
              <div class="imgBody" v-if="blogLink.photoList">
                <i class="el-icon-error inputClass" v-show="icon" @click="deletePhoto('link')"
                   @mouseover="icon = true"></i>
                <img @mouseover="icon = true" @mouseout="icon = false" v-bind:src="blogLink.photoList[0]"/>
              </div>

              <div v-else class="uploadImgBody" @click="checkPhoto">
                <i class="el-icon-plus avatar-uploader-icon"></i>
              </div>
            </el-form-item>

            <el-form-item label="网站名称" :label-width="labelWidth" prop="title">
              <el-input v-model="blogLink.title" style="width: 100%"></el-input>
            </el-form-item>

            <el-form-item label="网站简介" :label-width="labelWidth" prop="summary">
              <el-input v-model="blogLink.summary" style="width: 100%"></el-input>
            </el-form-item>

            <el-form-item label="网站地址" :label-width="labelWidth" prop="url">
              <el-input v-model="blogLink.url" style="width: 100%"></el-input>
            </el-form-item>

            <el-form-item label="站长邮箱" :label-width="labelWidth" prop="email">
              <el-input v-model="blogLink.email" placeholder="用于申请通过邮件通知" style="width: 100%"></el-input>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="submitForm('replyBlogLink')">申 请</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="修改密码" name="修改密码">
          <span slot="label"><i class="el-icon-s-tools"></i> 修改密码</span>
          <el-collapse v-model="activeNames">
            <el-collapse-item title="修改密码须知" name="1">
              <div>此修改密码功能仅适用于账号和密码登录</div>
              <div>对于第三方登录的账号，无法进行密码修改</div>
            </el-collapse-item>
          </el-collapse>
          <el-form ref="userInfoForm" label-position="left" :model="userInfo" label-width="100px"
                   :rules="userInfoRules">
            <el-form-item label="旧密码" :label-width="labelWidth" prop="oldPwd">
              <el-input type="password" v-model="userInfo.oldPwd" style="width: 100%"></el-input>
            </el-form-item>

            <el-form-item label="新密码" :label-width="labelWidth" prop="newPwd">
              <el-input type="password" v-model="userInfo.newPwd" style="width: 100%"></el-input>
            </el-form-item>

            <el-form-item label="重复密码" :label-width="labelWidth" prop="newPwd2">
              <el-input type="password" v-model="userInfo.newPwd2" style="width: 100%"></el-input>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="submitForm('changePwd')">提 交</el-button>
            </el-form-item>
          </el-form>

        </el-tab-pane>

      </el-tabs>
    </el-drawer>

    <!--头像裁剪-->
    <avatar-cropper
      v-show="imagecropperShow"
      :key="imagecropperKey"
      :width="300"
      :height="300"
      :url="url"
      lang-type="zh"
      @close="close"
      @crop-upload-success="cropSuccess"
    />

    <div :style="{'min-height': minHeight + 'px'}" style="padding-top: 20px">
      <router-view/>
    </div>

    <footer>
      <p v-html="info.recordNum"></p>
    </footer>

    <!--创建博客-->
    <CreateBlog v-if="dialogFormVisible" :visible="dialogFormVisible" :isEdit="isEdit"
                :editorModel="userInfo.editorModel" :formData="formData" @beforeClose="beforeClose"></CreateBlog>

    <!--本地上传文章-->
    <UploadBlog v-if="dialogUploadBlogVisible" :visible="dialogUploadBlogVisible"
                @beforeClose="beforeClose"></UploadBlog>

    <!--创建问答-->
    <CreateQuestion v-if="questionDialogFormVisible" :visible="questionDialogFormVisible"
                    :editorModel="userInfo.editorModel" :isEdit="isEdit" :formData="questionFormData"
                    @beforeClose="questionBeforeClose"></CreateQuestion>

    <!--发表面经-->
    <CreateProblem v-if="dialogProblemFormVisible" :visible="dialogProblemFormVisible"
                   :editorModel="userInfo.editorModel" :isEdit="isEdit"
                   :formData="problemFormData" @beforeClose="problemBeforeClose"></CreateProblem>

    <!--  <ChatRoom  style="margin-top: 30px"></ChatRoom>-->

    <!-- 公众号账号绑定 -->
    <el-dialog
      title="公众号账号绑定"
      :visible.sync="wechatBindDialogVisible"
      close-on-click-modal
      :width="drawerSize"
    >

      <div style="text-align: center">
        <el-image :src=" webConfig.wechatPhoto" style="width: 200px; height: 200px"></el-image>
        <el-row>
          <span style="font-size: 14px; color: #303133">扫码关注公众号，回复验证码完成绑定</span>
        </el-row>
        <el-row>
          <span style="font-size: 14px; color: #303133">绑定验证码：</span>
          <span style="color: red; font-weight: bold">{{ bindInfo.bindKey }}</span>
          <i class="el-icon-refresh" @click="refreshBindKey('wechat')" style="cursor: pointer"></i>
        </el-row>
      </div>
    </el-dialog>

    <!-- 小程序账号绑定 -->
    <el-dialog
      title="小程序账号绑定"
      :visible.sync="miniBindDialogVisible"
      close-on-click-modal
      :width="drawerSize"
    >
      <div style="text-align: center">
        <el-image :src=" webConfig.miniPhoto" style="width: 200px; height: 200px"></el-image>
        <el-row>
          <span style="font-size: 14px; color: #303133">扫码打开小程序，回复验证码完成绑定</span>
        </el-row>
        <el-row>
          <span style="font-size: 14px; color: #303133">绑定验证码：</span>
          <span style="color: red; font-weight: bold">{{ bindInfo.bindKey }}</span>
          <i class="el-icon-refresh" @click="refreshBindKey('mini')" style="cursor: pointer"></i>
        </el-row>
      </div>

    </el-dialog>

    <!-- 微信账号绑定 -->
    <el-dialog
      title="微信账号绑定"
      :visible.sync="personWechatBindDialogVisible"
      close-on-click-modal
      :width="drawerSize"
    >
      <div style="text-align: center">
        <div id="login_container" style="text-align: center"></div>
      </div>

    </el-dialog>

    <!--提现操作-->
    <el-dialog
      title="请填写提现信息"
      :visible.sync="drawDialogVisible"
      width="30%"
      center
    >
      <div style="text-align: center">
        <el-form :rules="withdrawRules" :model="withdrawForm" ref="withdrawForm">

          <el-form-item
            label="微信账号"
            :label-width="lineLabelWidth"
            prop="account"
          >
            <el-input
              v-model="withdrawForm.account"
              maxlength="50"
              placeholder="请输入微信账号"
              clearable
            ></el-input>
          </el-form-item>

          <el-form-item label="收款码" :label-width="lineLabelWidth" prop="fileUid">
            <div class="imgBody" v-if="withdrawForm.photoUrl">
              <i class="el-icon-error inputClass" v-show="icon" @click="deletePhoto('withdraw')"
                 @mouseover="icon = true"></i>
              <img @mouseover="icon = true" @mouseout="icon = false" v-bind:src="withdrawForm.photoUrl"/>
            </div>

            <div v-else class="uploadImgBody" @click="checkPhoto">
              <i class="el-icon-plus avatar-uploader-icon"></i>
            </div>
          </el-form-item>

          <el-form-item
            label="提现金额(元)"
            :label-width="lineLabelWidth"
            prop="amount"
          >
            <el-input-number v-model="withdrawForm.amount" :precision="2" :step="0.01" :min="0" :max="userAmount/100"
                             style="float: left"></el-input-number>

          </el-form-item>

        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="drawDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitWithdraw">确 定</el-button>
      </span>
    </el-dialog>

    <div>
      <!--聊天-->
      <el-tooltip class="item" effect="light" content="聊天室" placement="left-end"
                  v-if="componentShowMap.showChatRoom">
        <a
          href="javascript:void(0);"
          class="cd-chat cd-is-visible"
          @click="goChat"
        >
          <span style="font-size: 24px; color: #3299BB;" class="iconfont">&#xe89c;</span>
          <el-badge is-dot :value="userReceiveMessageCount" style="float: right; right: 6px;" class="item"
                    :hidden="userReceiveMessageCount == 0"></el-badge>
        </a>
      </el-tooltip>

      <!--暗黑模式-->
      <el-tooltip class="item" effect="light" content="暗黑模式" placement="left-end"
                  v-if="componentShowMap.showBlackModel">
        <a
          v-if="darkModel == 0"
          href="javascript:void(0);"
          @click="changeDarkModel(1)"
          class="cd-dark cd-is-visible"
        >
          <span style="font-size: 26px; color: #EDC951" class="iconfont">&#xe6dd;</span>
        </a>
        <a
          v-else
          href="javascript:void(0);"
          @click="changeDarkModel(0)"
          class="cd-dark cd-is-visible"
        >
          <span style="font-size: 26px" class="iconfont">&#xe60d;</span>
        </a>
      </el-tooltip>


      <!--顶部模式-->
      <a
        href="javascript:void(0);"
        @click="returnTop"
        :class="isCdTopVisible?'cd-top cd-is-visible':'cd-top'"
      >
        <span style="font-size: 26px;" class="iconfont">&#xe6fb;</span>
      </a>
    </div>

    </body>
    </html>
  </div>
</template>

<script>
import CreateBlog from '../components/CreateBlog'
import CreateQuestion from '../components/CreateQuestion'
import UploadBlog from '../components/UploadBlog'
import CreateProblem from '../components/CreateProblem'
import HotSearch from "../components/HotSearch"

import AvatarCropper from '@/components/AvatarCropper'
import {getWebConfig, getWebNavbar} from "../api/index";
import {delCookie, getCookie, setCookie} from "@/utils/cookieUtils";
import CodeCopy from "@/components/CodeCopy";
import {addWithdraw, getOrderAmountLogList, getRecentWithdraw} from "../api/pay";
import {getWeChatLoginUrl} from "@/api/user";
import config from '../../conf.json'
import {
  addFeedback,
  authVerify,
  deleteUserAccessToken,
  editUser,
  getAccountBindList,
  getFeedbackList,
  login,
  replyBlogLink,
  updateUserPwd,
} from "../api/user";
import {getCommentListByUser, getPraiseListByUser, getReplyListByUser} from "../api/comment";
import {getUserReceiveNoticeCount, readUserReceiveNoticeCount} from "../api/notice"
import LoginBox from "../components/LoginBox";
import Medal from "../components/Medal";
import WebNotice from "../components/WebNotice";
import {getListByDictTypeList} from "@/api/sysDictData"
import {deleteQuestion, getMeQuestionList} from "@/api/question"
import {deleteBlog, editBlog, publishBlog} from "@/api/createBlog"
import {mapMutations} from 'vuex';
import {timeAgo} from "../utils/webUtils";
import {getSearchModel} from "@/api/search";
import {getCreditsListByUser} from "../api/creditsLog";
import {deleteCollect, getUserCollectList} from "../api/collect";
import {getBindKey, loginCheck} from "../api/wechat";
import {getMeBlogList} from "../api/createBlog";
import {addHotSearch} from "../api/hotSearch";
import {downloadBlog} from "../api/blogContent";
import {getUserMedalList} from "../api/medal";
import {getCurrentUserAmount} from "../api/about";
import Qrcode from 'vue-qrcode-component'


export default {
  name: "index",
  components: {
    Medal,
    LoginBox,
    AvatarCropper,
    CreateBlog,
    CreateQuestion,
    UploadBlog,
    CreateProblem,
    HotSearch,
    WebNotice,
    Qrcode,
  },
  data() {
    return {
      // xss白名单配置
      options: {
        whiteList: {
          div: ['class'],
          br: ['class'],
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
          img: ['class', 'src', 'alt', 'width', 'height', 'style'],
          iframe: ['class', 'src', 'style', 'scrolling', 'border', 'frameborder', 'allowfullscreen'],
          hr: [],
          video: ['class', 'src', 'controls'],
          source: ['src', 'type']
        }
      },
      defaultConfig: {}, // 全局默认配置
      drawDialogVisible: false,
      lineLabelWidth: "120px", //一行的间隔数
      withdrawForm: {
        userName: "",
        account: ""
      },
      userAmount: 0, // 用户金额
      webConfig: {},
      bindInfo: {},
      wechatBindDialogVisible: false, // 微信公众号绑定
      miniBindDialogVisible: false, // 小程序绑定
      personWechatBindDialogVisible: false, // 个人微信绑定
      qrcode: null,
      minHeight: window.screen.height - 273,
      queryParams: {
        keyword: "",
        tagKeyword: "", //标签搜索
        sortKeyword: "", //分类搜索
        publishKeyword: "", // 发布 搜索
        originalKeyword: "", // 原创 搜索
        openCommentKeyword: "", // 开启评论
        auditStatusKeyword: "", // 审核状态
      }, // 搜索条件
      // 第三方账号绑定类别
      loginType: {
        password: true,
        gitee: true,
        github: true,
        qq: true,
        wechat: true,
        mini: true,
        personWechat: true,
      },
      total: 0, //总数量
      isEnd: false, //是否到底底部了
      loading: false, //是否正在加载
      pageSize: 10,
      currentPage: 0,
      dialogFormVisible: false, // 控制创建博客的弹出
      questionDialogFormVisible: false, // 控制发表问题的弹出
      dialogUploadBlogVisible: false, // 控制上传博客的弹出
      dialogProblemFormVisible: false, // 控制创建面经的弹出
      searchModel: 0, //搜索模式 0:SQL搜索、1:ES搜索、2:Solr搜索
      activeNames: ['1', '2'], //激活的折叠面板
      activeName: "个人中心", // 激活的标签
      blogOriginalDictList: [], //是否原创字典
      blogPublishDictList: [], //是否发布字典
      yesNoDictList: [], // 是否 字典列表
      genderDictList: [], //性别 字典列表
      feedbackDictList: [], // 反馈 字典列表
      editorModalDictList: [], // 文本编辑器字典列表
      commentReportDictList: [], // 评论举报字典
      auditStatusDictList: [], // 审批状态字典
      medalTypeDictList: [], // 勋章类型字典
      commentReportDictDefault: null, // 评论举报字典默认值
      imagecropperShow: false,
      imagecropperKey: 0,
      url: process.env.PICTURE_API + "/file/cropperPicture",
      webSite: process.env.VUE_MOGU_WEB,
      webNavbarList: [],
      drawer: false,
      info: {},
      saveTitle: "",
      keyword: "",
      showSearch: false, //控制搜索框的弹出
      showHead: false, //控制导航栏的弹出
      showUploadBlog: false, // 显示上传文章
      isCdTopVisible: false,
      isVisible: true, //控制web端导航的隐藏和显示
      isLogin: false,
      showLogin: false, //显示登录框
      userInfo: { // 用户信息
      },
      feedback: {}, //反馈提交
      blogLink: {}, //友链申请
      icon: false, //控制删除图标的显示
      labelWidth: "80px",
      commentList: [], //我的评论
      replyList: [], //我的回复
      praiseList: [], //我的点赞
      feedbackList: [], //我的反馈
      userBlogList: [], // 用户博客列表
      userQuestionList: [], // 用户问答列表
      creditsLogList: [],//积分流水列表
      collectList: [], // 用户收藏列表
      medalList: [], // 勋章列表
      openComment: "0", //是否开启评论
      defaultAvatar: process.env.defaultAvatar, // 默认头像
      drawerSize: "33%",
      userReceiveCommentCount: 0, // 用户收到的评论数
      userReceivePraiseCount: 0, // 用户收到的点赞数
      userReceiveSystemCount: 0, // 用户收到的系统通知数
      userReceiveWatchCount: 0, // 用户收到的关注数
      userReceiveMessageCount: 0, // 用户收到的消息数
      userReceiveCollectCount: 0, // 用户收到的收藏数
      isEdit: false, // 是否编辑博客
      formData: {}, // 表单数据
      questionFormData: {}, // 问答数据
      problemFormData: {}, // 面经数据
      componentShowMap: {}, // 组件展示Map
      showCreateBlog: false, // 是否显示用户创作按钮
      showCreateQuestion: false, // 是否显示创建问答按钮
      showChat: false, // 显示私信按钮
      browserFlag: 1, // 浏览器标志【默认Chrome】
      accountList: [], // 第三方綁定账号列表
      darkModel: 0, //暗黑模式 是否开启
      dialogHotSearchVisible: false,
      leaveTimeout: null, // 离开定时器
      withdrawRules: {
        account: [
          {required: true, message: '收款账号不能为空', trigger: 'blur'},
          {min: 0, max: 255, message: '长度在0到255个字符'},
        ],
        fileUid: [
          {required: true, message: '收款码不能为空', trigger: 'blur'},
        ],
        amount: [
          {required: true, message: '提现金额不能为空', trigger: 'blur'},
        ],
      },
      rules: {
        qqNumber: [
          {pattern: /[1-9]([0-9]{5,11})/, message: '请输入正确的QQ号码'},
        ],
        email: [
          {pattern: /\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/, message: '请输入正确的邮箱'},
        ],
        nickName: [
          {min: 0, max: 20, message: '长度在0到20个字符'},
        ],
        occupation: [
          {min: 0, max: 10, message: '长度在0到10个字符'},
        ],
        summary: [
          {min: 0, max: 200, message: '长度在0到200个字符'},
        ],
      },
      linkRules: {
        title: [
          {required: true, message: '网站名称不能为空', trigger: 'blur'},
          {min: 1, max: 10, message: '长度在1到10个字符'},
        ],
        summary: [
          {required: true, message: '简介不能为空', trigger: 'blur'},
          {min: 1, max: 50, message: '长度在1到50个字符'},
        ],
        url: [
          {required: true, message: '网站地址不能为空', trigger: 'blur'},
          {pattern: /^((https|http|ftp|rtsp|mms)?:\/\/)[^\s]+/, message: '请输入有效的URL'},
        ],
        email: [
          {pattern: /\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/, message: '请输入正确的邮箱'},
        ],
      },
      userInfoRules: {
        oldPwd: [
          {required: true, message: '旧密码不能为空', trigger: 'blur'},
          {min: 5, max: 20, message: '密码长度在5到20个字符'},
        ],
        newPwd: [
          {required: true, message: '新密码不能为空', trigger: 'blur'},
          {min: 5, max: 20, message: '密码长度在5到20个字符'},
        ],
        newPwd2: [
          {required: true, message: '新密码不能为空', trigger: 'blur'},
          {min: 5, max: 20, message: '密码长度在5到20个字符'},
        ]
      },
      actionHandle: {
        "PersonCenter": "个人中心",
        "AccountBind": "账号绑定",
        "MyArticle": "我的文章",
        "MyQuestion": "我的问答",
        "MyCollect": "我的收藏",
        "MyComment": "我的评论",
        "MyReply": "我的回复",
        "CreditsDetail": "积分明细",
        "MyMedal": "我的勋章",
        "MyPraise": "我的点赞",
        "MyFeedback": "我的反馈",
        "ChangePassword": "修改密码",
      },
      webSocket: null, // socket对象
      WS_API: process.env.WS_API, // WebSocket
      authCode: {}, // 用户权限Code
    };
  },
  mounted() {
    let that = this;
    let offset = 300;
    let after = 0;
    window.addEventListener("scroll", function () {
      let scrollTop = document.documentElement.scrollTop; //当前的的位置
      let scrollHeight = document.documentElement.scrollHeight; //最高的位置
      if (scrollTop > offset) {
        that.isCdTopVisible = true;
      } else {
        that.isCdTopVisible = false;
      }
      after = scrollTop;
    });

    // 屏幕自适应
    window.onresize = () => {
      return (() => {
        that.setSize()
      })()
    }
  },

  watch: {
    $route(to, from) {
      this.getCurrentPageTitle()
    },
    '$store.state.user.userInfo': function (newFlag, oldFlag) {
      this.getUserAuthCodeList()
    },

    // 判断登录状态位是否改变（用于控制弹框）
    '$store.state.app.loginMessage': function (newFlag, oldFlag) {
      this.showLogin = true
    },
    // 判断登录状态位是否改变（用于控制弹框）
    '$store.state.app.createQuestionMessage': function (newFlag, oldFlag) {
      this.questionDialogFormVisible = true
    },
    // 判断 发表面经弹出框状态位是否改变(用于控制弹出框)
    '$store.state.app.createProblemMessage': function (newFlag, oldFlag) {
      this.dialogProblemFormVisible = true
    },
    // 判断个人中心是否弹出新内容(用于控制弹出框)
    '$store.state.app.domainEventMessage': function (event, oldFlag) {
      console.log("处理领域事件", event)
      this.domainEventHandler(event)
    },
  },
  metaInfo() {
    return {
      title: this.info.title ? this.info.title : config.title,
      link: [
        {rel: 'icon', href: this.info.logoPhoto}
      ],
      meta: [],
    };
  },
  updated() {
    this.update()
  },
  created() {
    // 字典查询
    this.getDictList()
    this.getToken()
    this.getKeyword()
    this.getCurrentPageTitle()
    this.getWebConfigInfo()
    this.getWebNavbarList()
    this.setSize()
    this.setUserReceiveCommentCount()
    // 获取浏览器类型
    this.getBrowser()
    // 获取搜索模式
    this.getBlogSearchModel()
    // 从url中打开个人中心
    this.getActionByUrl()
    // 初始化专栏
    this.initWebSocket()
    // 初始化暗黑模式
    this.initDarkModel()
    // 设置配置信息
    this.defaultConfig = config
  },
  destroyed() {
    if (this.webSocket) {
      // 离开路由之后断开websocket连接
      this.webSocket.close();
    }
  },
  methods: {
    //拿到vuex中的写的方法
    ...mapMutations(['setUserInfo', 'setLoginState', 'setWebConfigData', 'setNoticeType', 'setCommentReportDict', 'setPaySuccess', 'setTaskFinish', 'setAuthCode']),
    initDarkModel() {
      // 暗黑模式
      let darkModel = getCookie("darkModel")
      if (darkModel && darkModel == "1") {
        this.darkModel = 1
        window.document.documentElement.setAttribute('data-theme', 'dark')
      } else {
        this.darkModel = 0
        window.document.documentElement.setAttribute('data-theme', 'light')
      }
    },
    // 提交提现申请
    submitWithdraw() {
      let params = {}
      params.amount = this.withdrawForm.amount * 100
      params.account = this.withdrawForm.account
      params.fileUid = this.withdrawForm.fileUid
      addWithdraw(params).then(resp => {
        if (resp.code == this.$ECode.SUCCESS) {
          this.$message.success("申请提现成功，请耐心等待管理员审核")
        } else {
          this.$message.error(resp.message)
        }
        this.drawDialogVisible = false
        this.getUserAmountMethod()
      })
    },
    // 获取资金明细
    getOrderAmountLogListMethod(isClean) {
      if (isClean) {
        this.creditsLogList = [];
      }
      let params = {}
      params.pageSize = this.pageSize;
      params.currentPage = this.currentPage;
      this.loading = true
      getOrderAmountLogList(params).then(response => {
        if (response.code == this.$ECode.SUCCESS && response.data.records.length > 0) {
          let orderAmountLogList = response.data.records
          this.orderAmountLogList = this.orderAmountLogList.concat(orderAmountLogList);
          this.total = response.data.total;
          this.pageSize = response.data.size;
          this.currentPage = response.data.current;
          //全部加载完毕
          if (orderAmountLogList.length < this.pageSize) {
            this.isEnd = true;
          }
        } else {
          this.isEnd = true
        }
        this.loading = false
      })
    },
    // 申请提现
    withdrawAmount() {
      if (this.userAmount == 0) {
        this.$message.error("当前钱包金额为0，无法发起提现！")
        return
      }
      this.withdrawForm = this.getWithdrawFormObject()
      this.getRecentWithdrawRecord()
      this.drawDialogVisible = true
    },
    // 获取最近的一次提现记录
    getRecentWithdrawRecord() {
      console.log("获取用户最近抽奖记录")
      getRecentWithdraw().then(response => {
        console.log("获取用户最近抽奖记录", response)
        let data = response.data
        if (data) {
          this.withdrawForm.account = data.account
          this.withdrawForm.photoUrl = data.photoUrl
          this.withdrawForm.fileUid = data.fileUid
        }

      })
    },
    getWithdrawFormObject: function () {
      let withdrawForm = {
        account: "",
        fileUid: "",
        amount: this.userAmount / 100,
      };
      return withdrawForm;
    },
    getUserAmountMethod() {
      getCurrentUserAmount().then(resp => {
        if (resp.code == this.$ECode.SUCCESS) {
          this.userAmount = resp.data
        }
      })
    },
    initWebSocket() {
      if (window.WebSocket) {
        this.webSocket = new WebSocket(
          this.WS_API + `/ws?token=${getCookie("token")}`
        );
        this.webSocket.onmessage = this.onMessage;
        this.webSocket.onopen = this.onOpen;
        this.webSocket.onerror = this.onError;
        this.webSocket.onclose = this.onClose;
        console.log("初始化WebSocket成功")
      } else {
        console.log("您的浏览器不支持websocket");
      }
    },
    onMessage(e) {
      let messageVo = JSON.parse(e.data);
      console.log("接收消息", messageVo);
      // 接收到领域事件
      if (messageVo.messageLevel && messageVo.messageLevel === 40010) {
        console.log("处理领域事件", messageVo);
        switch (messageVo.message) {
          case "MEDAL_AWARD": {
            console.log("勋章奖励")
            // 勋章奖励，刷新
            this.$refs.medal.getUserMedal()
          }
            break;
          case "TASK_FINISH": {
            // 刷新任务列表
            console.log("任务完成")
            this.setTaskFinish(Math.random())
          }
            break;
          case "PAY_SUCCESS": {
            // 赞赏成功
            console.log("赞赏成功")
            this.setPaySuccess(Math.random())
          }
            break;
          case "NOTICE_FLUSH": {
            // 刷新通知
            console.log("刷新通知")
            this.setUserReceiveCommentCount()
          }
            break;
        }
      }

    },
    onOpen() {
      console.log("websocket已连接");
    },
    onError() {
      console.log("websocket连接失败");
    },
    onClose(e) {
      console.log("断开连接", e);
    },
    showHotSearch: function () {
      this.dialogHotSearchVisible = true;
    },
    domainEventHandler(event) {
      switch (event.type) {
        // 任务类领域事件
        case "task": {
          this.taskDomainEventHandler(event)
        }
          break
        case "personCenter": {
          this.showPersonCenter(event.action)
        }
          break
      }
    },
    taskDomainEventHandler(event) {
      switch (event.action) {
        // 写博客
        case "BLOG": {
          this.dialogFormVisible = true
        }
          break;
        // 写问答
        case "QUESTION": {
          this.questionDialogFormVisible = true
        }
          break;
        // 写面经
        case "PROBLEM": {
          this.dialogProblemFormVisible = true
        }
          break;
        // 发表评论
        case "COMMENT": {
          this.$router.push("/messageBoard")
        }
          break;
        // 写动态
        case "MOMENT": {
          this.$router.push("/moment")
        }
          break;
        // 去聊天
        case "CHAT": {
          this.$router.push("/chat")
        }
          break;
        // 去聊天
        case "LUCKY": {
          this.$router.push("/lucky")
        }
          break;
      }
      // 判断是否有消息提示需要处理
      if (event.message) {
        this.$message.success(event.message)
      }
      if (event.router) {
        this.$router.push(event.router)
      }
    },
    chooseitem: function (data) {
      this.keyword = data
      this.search();
      this.onLeaveTd(false)
    },
    // 搜索
    search: function () {
      if (this.keyword == "" || this.keyword.trim() == "") {
        this.$notify.error({
          title: '错误',
          message: "关键字不能为空",
          type: 'error',
          offset: 100
        });
        return;
      }
      addHotSearch(this.keyword).then(result => {
        if (result.code == this.$ECode.SUCCESS) {
        }
      })
      if (this.$route.path == '/userCenter') {
        this.$router.push({
          path: this.$route.fullPath,
          query: {keyword: this.keyword, model: this.searchModel}
        })
      } else {
        this.$router.push({path: "/list", query: {keyword: this.keyword, model: this.searchModel}});
      }
    },
    //获取对应markdown代码块标签
    update() {
      setTimeout(() => {
        document.querySelectorAll('pre').forEach(el => {
          if (el.classList.contains('code-copy-added')) {
            return
          }
          //   https://cn.vuejs.org/v2/api/index.html#Vue-extend
          /* 使用基础 Vue 构造器，创建一个“子类”。参数是一个包含组件选项的对象 */
          let ComponentClass = Vue.extend(CodeCopy)
          let instance = new ComponentClass()
          instance.code = el.innerText
          instance.parent = el
          /* 手动挂载 */
          instance.$mount()
          el.classList.add('code-copy-added')
          el.appendChild(instance.$el)
        })
      }, 100)
    },
    changeDarkModel(darkModel) {
      if (darkModel == 1) {
        console.log("切换暗黑模式")
        window.document.documentElement.setAttribute('data-theme', 'dark')
      } else {
        console.log("切换亮白模式")
        window.document.documentElement.setAttribute('data-theme', 'light')
      }
      // 设置暗黑模式
      setCookie("darkModel", darkModel, 31)
      this.darkModel = darkModel
    },
    refreshBindKey(type) {
      this.goBind(type)
    },

    goBind(source) {
      let that = this
      // 判断是否点击公众号登录
      if (source == "wechat") {
        this.wechatBindDialogVisible = true
        getBindKey().then(response => {
          if (response.code == this.$ECode.SUCCESS) {
            let bindInfo = response.data
            this.bindInfo = bindInfo
            let count = 0
            let interval = setInterval(() => {
              count++
              // 当检查30次未扫码的时候，将二维码失效，重试关闭接口请求
              if (count > 30) {
                this.wechatLoginKey.bindKey = "验证码失效,请刷新"
                clearInterval(interval)
              }
              let params = {}
              params.code = bindInfo.bindKey
              params.ticket = bindInfo.ticket
              loginCheck(params).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                  // 判断url中是否含有token
                  let token = response.data
                  if (token != undefined) {
                    // 设置token七天过期
                    setCookie("token", token, 31)
                    location.replace(this.webSite);
                  }
                }
              });
            }, 3000);
          }
        })
        return
      } else if (source == "mini") {
        this.miniBindDialogVisible = true
        // 如果是小程序的绑定，生成小程序的绑定链接，然后使用微信扫一扫
        getBindKey().then(response => {
          console.log("获取绑定的key", response)
          if (response.code == this.$ECode.SUCCESS) {
            let bindInfo = response.data
            this.bindInfo = bindInfo
            let count = 0
            let interval = setInterval(() => {
              count++
              // 当检查30次未扫码的时候，将二维码失效，重试关闭接口请求
              if (count > 30) {
                this.wechatLoginKey.bindKey = "验证码失效,请刷新"
                clearInterval(interval)
              }
              let params = {}
              params.code = bindInfo.bindKey
              params.ticket = bindInfo.ticket
              loginCheck(params).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                  // 判断url中是否含有token
                  let token = response.data
                  if (token) {
                    // 设置token七天过期
                    setCookie("token", token, 31)
                    location.replace(this.webSite);
                  }
                }
              });
            }, 3000);
          }
        })
        return
      } else if (source == "person_wechat") {
        // 如果是微信登录
        this.personWechatBindDialogVisible = true
        // 生成绑定的URL
        let params = new URLSearchParams()
        params.append("operateType", "bind")
        getWeChatLoginUrl(params).then(response => {
          if (response.code === this.$ECode.SUCCESS) {
            console.log("获取微信的信息", response)
            let data = response.data;
            let obj = new WxLogin({
              // 需要显示内嵌二维码的容器id
              id: 'login_container',
              self_redirect: true,
              // 应用ID
              appid: data.appId,
              // 网页默认即可
              scope: data.scope,
              // 授权成功后回调的url
              redirect_uri: encodeURIComponent(data.redirect_uri),
              // 可设置为简单的随机数加session用来校验
              state: data.state,
              // 二维码的样式，提供"black"、"white"可选。
              style: 'black',
              // 自定义样式链接
              href: 'https://oos.moguit.cn/cdn/wechatLogin.css'
            })
            // 开始定时扫描，判断是否完成登录
            let count = 0
            let interval = setInterval(() => {
              count++
              // 当检查15次未扫码的时候，将二维码失效，重试关闭接口请求
              if (count > 30) {
                that.wechatLoginKey.loginKey = "验证码失效,请刷新"
                clearInterval(interval)
              }
              let params = {}
              params.code = data.loginKey
              params.ticket = data.ticket
              loginCheck(params).then(response => {
                console.log("获取用户状态", response)
                if (response.code == that.$ECode.SUCCESS) {
                  console.log("获取token", response)
                  // 设置token到Cookie中，并刷新页面
                  let token = response.data
                  if (token) {
                    setCookie("token", token, 31)
                    location.reload();
                  }
                }
              });
            }, 3000);

          }
        })

        return
      }


      // 正常的绑定流程
      let params = new URLSearchParams();
      params.append("source", source);
      params.append("type", "bind");
      login(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          window.location.href = response.data.url
        }
      });
    },
    setLoginTypeList: function () {
      // 获取登录方式列表
      let webConfigData = this.$store.state.app.webConfigData
      this.webConfig = webConfigData
      if (webConfigData.loginTypeList != undefined) {
        let loginTypeList = JSON.parse(webConfigData.loginTypeList)
        for (let a = 0; a < loginTypeList.length; a++) {
          switch (loginTypeList[a]) {
            case "1": {
              this.loginType.password = false
            }
              break;
            case "2": {
              this.loginType.gitee = false
            }
              break;
            case "3": {
              this.loginType.github = false
            }
              break;
            case "4": {
              this.loginType.qq = false
            }
              break;
            case "5": {
              this.loginType.wechat = false
            }
              break;
            case "6": {
              this.loginType.mini = false
            }
              break;
            case "7": {
              this.loginType.personWechat = false
            }
              break;
            default: {
              console.log("登录方式设置有误！！", loginTypeList[a])
            }
          }
        }
      }
    },
    clickWebNavbar: function (webNavbar) {
      if (this.info.title && webNavbar.name) {
        document.title = this.info.title + " | " + webNavbar.name
      }
    },
    getBlogSearchModel: function () {
      getSearchModel().then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.searchModel = response.data
        }
      })
    },
    setSize() {
      // 屏幕大于950px的时候，显示侧边栏
      let clientWidth = document.body.clientWidth
      if (clientWidth > 1360) {
        this.drawerSize = "33%";
        this.showSearch = true
      } else if (clientWidth < 1360 && clientWidth > 950) {
        this.drawerSize = "50%";
        this.showSearch = false
      } else if (clientWidth < 950 && clientWidth > 650) {
        this.drawerSize = "70%";
        this.showSearch = false
      } else {
        this.drawerSize = "95%";
        this.showSearch = false
      }
    },
    loadContent: function () {
      this.currentPage = this.currentPage + 1;
    },
    cancelCollect(bizUid, collectType) {
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
      params.bizUid = bizUid
      params.collectType = collectType
      deleteCollect(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.$message.success(response.message)
          this.getCollectList(true)
        } else {
          this.$message.error(response.message)
        }
      })
    },
    // 跳转到个人中心页
    getUserCenter: function (blog) {
      console.log("跳转到用户中心", blog)
      // 判断是后台添加，还是前台投稿
      if (blog.articleSource == 0) {
        let routeData = this.$router.resolve({
          path: "/userCenter",
          query: {adminUid: blog.adminUid}
        });
        window.open(routeData.href, '_blank');
      } else {
        let routeData = this.$router.resolve({
          path: "/userCenter",
          query: {userUid: blog.userUid}
        });
        window.open(routeData.href, '_blank');
      }
    },
    //跳转到文章详情
    goToInfo(blog) {
      console.log("跳转", blog)
      let routeData = this.$router.resolve({
        path: "/info/" + blog.oid
      });
      window.open(routeData.href, '_blank');
    },
    //跳转到问答详情
    goToQuestionInfo(question) {
      console.log("跳转", question)
      let routeData = this.$router.resolve({
        path: "/qInfo/" + question.oid
      });
      window.open(routeData.href, '_blank');
    },
    //跳转到问答详情
    goToUserMoment(userMoment) {
      console.log("跳转", userMoment)
      let routeData = this.$router.resolve({
        path: "/moment",
        query: {uid: userMoment.uid}
      });
      window.open(routeData.href, '_blank');
    },
    //跳转到问答详情
    goToProblem(problem) {
      console.log("跳转", problem)
      let routeData = this.$router.resolve({
        path: "/cInfo/" + problem.oid
      });
      window.open(routeData.href, '_blank');
    },
    //跳转到问答详情
    goToResource(resource) {
      console.log("跳转", resource)
      let routeData = this.$router.resolve({
        path: "/resource/" + resource.uid
      });
      window.open(routeData.href, '_blank');
    },
    // 获取导航栏列表
    getWebNavbarList() {
      let params = {};
      params.isShow = 1
      getWebNavbar(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let webNavbarList = response.data
          let newWebNavbarList = []
          for (let a = 0; a < webNavbarList.length; a++) {
            if (webNavbarList[a].isShow == 1) {
              newWebNavbarList.push(webNavbarList[a])
            }
          }
          this.webNavbarList = newWebNavbarList
        }
      })
    },
    // 跳转到资源详情
    goSource: function (comment) {
      let source = comment.source
      switch (source) {
        case "MESSAGE_BOARD": {
          let routeData = this.$router.resolve({
            path: "/messageBoard"
          });
          window.open(routeData.href, '_blank');
        }
          break;
        case "BLOG_INFO": {
          let routeData = this.$router.resolve({
            path: "/info/" + comment.blogUid,
          });
          window.open(routeData.href, '_blank');
        }
          break;
        case "USER_MOMENT": {
          let routeData = this.$router.resolve({
            path: "/moment?uid=" + comment.blogUid,
          });
          window.open(routeData.href, '_blank');
        }
          break;
        case "RESOURCE_INFO": {
          let routeData = this.$router.resolve({
            path: "/resource/" + comment.blogUid,
          });
          window.open(routeData.href, '_blank');
        }
          break;
        case "ABOUT": {
          let routeData = this.$router.resolve({
            path: "/about"
          });
          window.open(routeData.href, '_blank');
        }
          break;
      }
    },

    // 获取评论列表
    getCommentList: function (isClean) {
      if (isClean) {
        this.commentList = [];
      }
      let params = {}
      params.pageSize = this.pageSize;
      params.currentPage = this.currentPage;
      getCommentListByUser(params).then(response => {
        if (response.code == this.$ECode.SUCCESS && response.data.records.length > 0) {
          let commentList = response.data.records
          this.commentList = this.commentList.concat(commentList);
          this.total = response.data.total;
          this.pageSize = response.data.size;
          this.currentPage = response.data.current;
          //全部加载完毕
          if (commentList.length < this.pageSize) {
            this.isEnd = true;
          }
        } else {
          this.isEnd = true
        }
        this.loading = false
      })
    },

    // 获取评论列表
    getCollectList: function (isClean) {
      if (isClean) {
        this.collectList = [];
      }
      let params = {}
      params.pageSize = this.pageSize;
      params.currentPage = this.currentPage;
      getUserCollectList(params).then(response => {
        if (response.code == this.$ECode.SUCCESS && response.data.records.length > 0) {
          let collectList = response.data.records
          this.collectList = this.collectList.concat(collectList);
          this.total = response.data.total;
          this.pageSize = response.data.size;
          this.currentPage = response.data.current;
          //全部加载完毕
          if (collectList.length < this.pageSize) {
            this.isEnd = true;
          }
        } else {
          this.isEnd = true
        }
        this.loading = false
      })
    },

    // 获取积分列表
    getCreditsList: function (isClean) {
      if (isClean) {
        this.creditsLogList = [];
      }
      let params = {}
      params.pageSize = this.pageSize;
      params.currentPage = this.currentPage;
      this.loading = true
      getCreditsListByUser(params).then(response => {
        if (response.code == this.$ECode.SUCCESS && response.data.records.length > 0) {
          let creditsLogList = response.data.records
          console.log(creditsLogList);
          this.creditsLogList = this.creditsLogList.concat(creditsLogList);
          this.total = response.data.total;
          this.pageSize = response.data.size;
          this.currentPage = response.data.current;
          //全部加载完毕
          if (creditsLogList.length < this.pageSize) {
            this.isEnd = true;
          }
        } else {
          this.isEnd = true
        }
        this.loading = false
      })
    },

    selectMedalType: function (key, keyPath) {
      console.log("选择type", key)
      this.getMedalList(key)
    },
    // 获取勋章列表
    getMedalList: function (type) {
      let params = {}
      params.pageSize = this.pageSize;
      params.currentPage = this.currentPage;
      params.type = type
      this.loading = true
      getUserMedalList(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.medalList = response.data
          //全部加载完毕
          this.isEnd = true
        }
        this.loading = false
      })
    },
    // 获取回复列表
    getReplyList: function (isClean) {
      if (isClean) {
        this.replyList = [];
      }
      let params = {}
      params.pageSize = this.pageSize;
      params.currentPage = this.currentPage;
      this.loading = true
      getReplyListByUser(params).then(response => {
        if (response.code == this.$ECode.SUCCESS && response.data.records.length > 0) {
          let replyList = response.data.records
          this.replyList = this.replyList.concat(replyList);
          this.total = response.data.total;
          this.pageSize = response.data.size;
          this.currentPage = response.data.current;
          //全部加载完毕
          if (replyList.length < this.pageSize) {
            this.isEnd = true;
          }
        } else {
          this.isEnd = true
        }
        this.loading = false
      })
    },

    // 获取反馈列表
    getFeedback: function (isClean) {
      let params = {}
      getFeedbackList(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.feedbackList = response.data.records;
        }
      })
    },

    // 获取点赞列表
    getPraiseList: function (isClean) {
      if (isClean) {
        this.praiseList = [];
      }
      let params = {}
      params.pageSize = this.pageSize;
      params.currentPage = this.currentPage;
      getPraiseListByUser(params).then(response => {
        if (response.code == this.$ECode.SUCCESS && response.data.records.length > 0) {
          let praiseList = response.data.records
          this.praiseList = this.praiseList.concat(praiseList);
          this.total = response.data.total;
          this.pageSize = response.data.size;
          this.currentPage = response.data.current;
          //全部加载完毕
          if (praiseList.length < this.pageSize) {
            this.isEnd = true;
          }
        } else {
          this.isEnd = true
        }
        this.loading = false
      })
    },

    // 获取用户博客列表
    getBlogList: function (isClean) {
      if (isClean) {
        this.userBlogList = [];
      }
      let params = {}
      params.auditStatus = this.queryParams.auditStatusKeyword
      params.isPublish = this.queryParams.publishKeyword
      params.isOriginal = this.queryParams.originalKeyword
      params.keyword = this.queryParams.keyword
      params.pageSize = this.pageSize;
      params.currentPage = this.currentPage;
      getMeBlogList(params).then(response => {
        if (response.code == this.$ECode.SUCCESS && response.data.records.length > 0) {
          let userBlogList = response.data.records
          this.userBlogList = this.userBlogList.concat(userBlogList);
          this.total = response.data.total;
          this.pageSize = response.data.size;
          this.currentPage = response.data.current;
          //全部加载完毕
          if (userBlogList.length < this.pageSize) {
            this.isEnd = true;
          }
        } else {
          this.isEnd = true
        }
        this.loading = false
      })
    },

    // 获取用户问题列表
    getUserQuestionList: function (isClean) {
      if (isClean) {
        this.userQuestionList = [];
      }
      let params = {}
      params.isPublish = this.queryParams.publishKeyword
      params.keyword = this.queryParams.keyword
      params.pageSize = this.pageSize;
      params.currentPage = this.currentPage;
      getMeQuestionList(params).then(response => {
        if (response.code == this.$ECode.SUCCESS && response.data.records.length > 0) {
          let userQuestionList = response.data.records
          this.userQuestionList = this.userQuestionList.concat(userQuestionList);
          this.total = response.data.total;
          this.pageSize = response.data.size;
          this.currentPage = response.data.current;
          //全部加载完毕
          if (userQuestionList.length < this.pageSize) {
            this.isEnd = true;
          }
        } else {
          this.isEnd = true
        }
        this.loading = false

      })
    },

    // 删除博客
    handleDeleteBlog: function (row) {
      let that = this;
      this.$confirm("此操作将把博客删除, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          let params = {};
          params.uid = row.uid;
          deleteBlog(params).then(response => {
            that.$commonUtil.message.success(response.message)
            that.getBlogList(true);
          });
        }).catch(() => {
        that.$commonUtil.message.info("已取消删除")
      });
    },
    handleDownload: function (object, type) {
      if (type == "blog") {
        // 下载博客
        let params = new URLSearchParams()
        params.append("blogUid", object.uid)
        downloadBlog(params).then(response => {
          if (response.code == this.$ECode.SUCCESS) {
            this.$commonUtil.htmlToMarkdownFile(object.title, object.content)
          }
        })

      } else {
        this.$commonUtil.htmlToMarkdownFile(object.title, object.content)
      }
    },
    // 删除问答
    handleDeleteQuestion: function (row) {
      let that = this;
      this.$confirm("此操作将把问答删除, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          let params = {};
          params.uid = row.uid;
          deleteQuestion(params).then(response => {
            that.$commonUtil.message.success(response.message)
            that.getUserQuestionList(true);
          });
        })
        .catch(() => {
          that.$commonUtil.message.info("已取消删除")
        });
    },
    userBindAccountList() {
      getAccountBindList().then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.accountList = response.data
        }
      })
    },

    // 标签选择
    handleClick(tab, event) {
      this.currentPage = 1
      this.queryParams = {
        keyword: "",
        tagKeyword: "", //标签搜索
        sortKeyword: "", //分类搜索
        publishKeyword: "", // 发布 搜索
        originalKeyword: "", // 原创 搜索
        openCommentKeyword: "", // 开启评论
        auditStatusKeyword: "", // 审核状态
      } // 搜索条件
      this.query
      this.isLoading = false
      this.isEnd = false
      this.userQuestionList = []
      this.userBlogList = []
      this.praiseList = []
      this.commentList = []
      this.replyList = []
      switch (tab.label) {
        case "个人中心": {
          console.log("点击个人中心")
          this.getUserAmountMethod()
        }
          break;
        case "账号绑定": {
          console.log("账号绑定")
          this.setLoginTypeList()
          this.userBindAccountList()
        }
          break;
        case "我的文章": {
          this.getBlogList(true)
        }
          break;
        case "我的问答": {
          this.getUserQuestionList(true)
        }
          break;
        case "我的收藏": {
          this.getCollectList(true)
        }
          break;
        case "我的评论": {
          this.getCommentList(true);
        }
          break;
        case "我的回复": {
          this.getReplyList(true);
        }
          break;
        case "积分明细": {
          this.getCreditsList(true);
        }
          break;
        case "我的勋章": {
          this.getMedalList(1);
        }
          break;
        case "我的点赞": {
          this.getPraiseList(true)
        }
          break;
        case "我的反馈": {
          this.getFeedback(true)
        }
          break;
        case "申请友链": {
          console.log("点击申请友链")
        }
          break;
        case "修改密码": {
          console.log("点击修改密码")
        }
          break;
      }
    },

    // 跳转到资源详情
    loadMore: function (type) {
      this.currentPage = this.currentPage + 1;
      switch (type) {
        case "comment": {
          this.getCommentList(false)
        }
          break;
        case "praise": {
          this.getPraiseList(false)
        }
          break;
        case "reply": {
          this.getReplyList(false)
        }
          break;
        case "credits": {
          this.getCreditsList(false)
        }
          break;
        case "feedback": {
          // this.getFeedback()
        }
          break;
        case "blog": {
          this.getBlogList(false)
        }
          break;
        case "question": {
          this.getUserQuestionList(false)
        }
          break;
        case "collect": {
          this.getCollectList(false)
        }
          break;
        case "medal": {
          this.getMedalList(1)
        }
          break;

      }
    },

    //弹出选择图片框
    checkPhoto() {
      this.imagecropperShow = true
    },
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
        return this.timeAgo(gainTime) + ' 获得'
      } else {
        return '暂未获得'
      }
    },

    cropSuccess(resData) {
      console.log("裁剪成功", resData)
      console.log("提现状态", this.drawDialogVisible)
      this.imagecropperShow = false
      this.imagecropperKey = this.imagecropperKey + 1
      if (this.drawDialogVisible) {
        // 判断是提现页面
        this.withdrawForm.photoUrl = resData.url
        this.withdrawForm.fileUid = resData.uid
        console.log("操作的提现页面", this.withdrawForm)
      } else if (this.activeName == "个人中心") {
        // 激活个人中心页面
        this.userInfo.photoUrl = resData.url
        this.userInfo.avatar = resData.uid
      } else if (this.activeName == "申请友链") {
        // 判断是申请友链页面
        let photoList = []
        photoList.push(resData.url);
        this.blogLink.photoList = photoList
        this.blogLink.fileUid = resData.uid
      }
    },

    deletePhoto: function (type) {
      switch (type) {
        case "user": {
          this.userInfo.photoUrl = null;
          this.userInfo.avatar = "";
          this.icon = false;
        }
          break;

        case "link": {
          this.blogLink.photoList = null;
          this.icon = false;
        }
          break;
        case "withdraw": {
          this.withdrawForm.photoUrl = null;
          this.icon = false;
        }
          break;
      }
    },

    onCardEnterTd: function () {
      clearTimeout(this.leaveTimeout)
    },
    //鼠标离开的事件。
    onLeaveTd(nowClean) {
      console.log("触发离开配置")
      let that = this
      if (nowClean) {
        that.dialogHotSearchVisible = false;
      } else {
        this.leaveTimeout = setTimeout(() => {
          that.dialogHotSearchVisible = false;
        }, 500)
      }
    },

    close() {
      this.imagecropperShow = false
    },
    submitForm: function (type) {
      switch (type) {
        case "editUser": {
          this.$refs.userInfo.validate((valid) => {
            if (!valid) {
              console.log("校验失败")
            } else {
              editUser(this.userInfo).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                  this.$message({
                    type: "success",
                    message: response.data
                  })
                } else {
                  this.$message({
                    type: "error",
                    message: response.message
                  })
                }
              });
            }
          })
        }
          ;
          break;

        case "replyBlogLink": {
          this.$refs.blogLink.validate((valid) => {
            if (!valid) {
              console.log("校验失败")
            } else {
              replyBlogLink(this.blogLink).then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                  this.$message({
                    type: "success",
                    message: response.data
                  })
                } else {
                  this.$message({
                    type: "error",
                    message: response.message
                  })
                }
              });
            }
          })
        }
          break;

        case "feedback": {
          let feedback = this.feedback
          if (feedback.title == undefined || feedback.title == "" || feedback.content == undefined || feedback.content == "") {
            this.$message({
              type: "error",
              message: "必填项不能为空"
            })
            return;
          }
          addFeedback(this.feedback).then(response => {
            if (response.code == this.$ECode.SUCCESS) {
              this.$message({
                type: "success",
                message: response.data
              })
              this.feedback = {}
              this.getFeedback()
            } else {
              this.$message({
                type: "error",
                message: response.message
              })
            }
          });
        }
          ;
          break;

        case "changePwd": {
          let newPwd = this.userInfo.newPwd
          let newPwd2 = this.userInfo.newPwd2
          let oldPwd = this.userInfo.oldPwd
          if (newPwd != newPwd2) {
            this.$message({
              type: "error",
              message: "两次密码不一致"
            })
            return
          }
          if (newPwd == oldPwd) {
            this.$message({
              type: "error",
              message: "新旧密码相同"
            })
            return
          }
          let params = new URLSearchParams()
          params.append("oldPwd", oldPwd)
          params.append("newPwd", newPwd)
          updateUserPwd(params).then(response => {
            if (response.code == this.$ECode.SUCCESS) {
              this.$message({
                type: "success",
                message: response.data
              })
            } else {
              this.$message({
                type: "error",
                message: response.message
              })
            }
            // 重置表单
            this.$refs.userInfoForm.resetFields()
          })
        }
          ;
          break;
      }
    },

    /**
     * 字典查询
     */
    getDictList: function () {
      let dictTypeList = ['sys_yes_no', 'sys_user_sex', 'sys_feedback_status', 'sys_editor_modal', 'sys_content_report_type', 'sys_original_status', 'sys_publish_status', 'sys_audit_status', 'sys_normal_disable', 'sys_publish_status', 'sys_medal_type']
      getListByDictTypeList(dictTypeList).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let dictMap = response.data;
          this.genderDictList = dictMap.sys_user_sex.list
          this.yesNoDictList = dictMap.sys_yes_no.list
          this.feedbackDictList = dictMap.sys_feedback_status.list
          this.editorModalDictList = dictMap.sys_editor_modal.list
          this.commentReportDictList = dictMap.sys_content_report_type.list
          this.blogOriginalDictList = dictMap.sys_original_status.list
          this.blogPublishDictList = dictMap.sys_publish_status.list
          this.auditStatusDictList = dictMap.sys_audit_status.list
          this.openDictList = dictMap.sys_normal_disable.list
          this.medalTypeDictList = dictMap.sys_medal_type.list

          if (dictMap.sys_content_report_type.defaultValue) {
            this.commentReportDictDefault = dictMap.sys_content_report_type.defaultValue;
          }
          let commentReportDict = {}
          commentReportDict.list = this.commentReportDictList
          commentReportDict.default = this.commentReportDictDefault
          this.setCommentReportDict(commentReportDict)
        }
      });
    },
    // 关闭个人中心
    closePersonCenter() {
      let url = window.location.href
      let replaceUrl = this.$commonUtil.ridUrlParam(url, ["action"])
      // 替换url不刷新
      history.replaceState(null, null, replaceUrl);
    },
    // 从url中获取个人中心类型
    getActionByUrl() {
      // 根据获取的动作，判断打开哪个页面
      let action = this.getUrllets()["action"];
      // 根据action，判断是否需要打开页面
      if (!action) {
        return
      }
      this.showPersonCenter(action)
    },
    showPersonCenter(action) {
      let actionHandle = this.actionHandle[action]
      if (!actionHandle) {
        return
      }
      // 有内容，首先打开个人中心页
      let tab = {
        "label": actionHandle,
      }
      this.activeName = actionHandle
      // 打开抽屉
      this.drawer = true;
      this.handleClick(tab, null)
    },
    getToken: function () {
      let token = this.getUrllets()["token"];
      // 判断url中是否含有token
      if (token) {
        // 设置token 31天过期
        setCookie("token", token, 31)
        // 如何清空url中某个字段
        let url = window.location.href
        let replaceUrl = this.$commonUtil.ridUrlParam(url, ["token"])
        // 替换url不刷新
        history.replaceState(null, null, replaceUrl);
      } else {
        // 从cookie中获取token
        token = getCookie("token")
      }
      // 从url和cookie中获取token，在去校验
      if (token) {
        authVerify(token).then(response => {
          if (response && response.code == this.$ECode.SUCCESS) {
            this.isLogin = true;
            this.userInfo = response.data;
            // 获取用户标识
            let userTag = this.userInfo.userTag
            if (userTag > 0) {
              this.showUploadBlog = true
            }
            this.setUserInfo(this.userInfo)
          } else {
            this.isLogin = false;
            delCookie("token");
          }
          this.setLoginState(this.isLogin);
          // 获取原页面地址，登录后进行跳转到原页面，如果为空或者就是在首页登录，不进行跳转
          const redirectUrl = getCookie("redirectUrl");
          if (redirectUrl != undefined && redirectUrl != "" && redirectUrl != "/") {
            window.location.href = redirectUrl;
            // 清除缓存的原页面地址
            delCookie("redirectUrl");
            return;
          }
        });

        // 最后在

      } else {
        this.isLogin = false;
        this.setLoginState(this.isLogin);
      }
    },
    /**********文章 发布与下架  *************/
    handlePublish: function (row) {
      let that = this;
      if (row.isPublish == 1) {
        this.$confirm("此操作将下架博客, 是否继续?", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(() => {
          let params = {};
          params.uid = row.uid;
          params.isPublish = 0;
          publishBlog(params).then(response => {
            that.$commonUtil.message.success(response.message)
            that.getBlogList(true)
          });
        }).catch(() => {
          that.$commonUtil.message.info("已取消下架")
        });
      }
      if (row.isPublish == 0) {
        this.$confirm("此操作将发布博客, 是否继续?", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(() => {
          let params = {};
          params.uid = row.uid;
          params.isPublish = 1;
          publishBlog(params).then(response => {
            that.$commonUtil.message.success(response.message)
            that.getBlogList(true)
          });
        }).catch(() => {
          that.$commonUtil.message.info("已取消发布")
        });
      }
    },
    handleAudit: function (row) {
      let params = row;
      params.isPublish = 1;
      editBlog(params).then(response => {
        this.$commonUtil.message.success(response.message)
        this.getBlogList(true)
      });
    },
    /********************** 创作相关 ********************/
    createBlog: function (isEdit, formData) {

      this.isEdit = isEdit
      if (isEdit) {
        this.formData = formData
      }
      if (!this.isLogin) {
        this.showLogin = true;
        return
      }
      /**
       * 修改 已审核过的文章  提示 将会使文章审核状态变更为未审核 进入重新审核流程
       */
      if (isEdit && formData.auditStatus == 2) {
        this.$confirm("修改已审核通过的文章,博客将会被重新审核, 是否继续?", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(() => {
          this.dialogFormVisible = true
        }).catch(() => {
          this.$commonUtil.message.info("已取消修改")
        });
      } else if (isEdit && formData.auditStatus == 1) {
        this.$confirm("请按照审核提示修改文章！重新提交博客后，将会被重新审核，是否继续? ", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(() => {
          this.dialogFormVisible = true
        }).catch(() => {
          this.$commonUtil.message.info("已取消修改")
        });
      } else {
        this.dialogFormVisible = true
      }
    },
    // 关闭模态框回调函数
    beforeClose: function () {
      console.log("更新关闭")
      this.dialogFormVisible = false
      this.dialogUploadBlogVisible = false
      this.drawer = false
      this.$nextTick(() => {
        this.drawer = true

        // 有内容，首先打开个人中心页
        let tab = {
          "label": "我的文章",
        }
        this.activeName = "我的文章"
        this.handleClick(tab, null)
        console.log("点击我的文章")
      })
    },
    /********************** 创作相关结束 ********************/

    /********************** 创作相关 ********************/
    uploadBlog: function () {
      this.dialogUploadBlogVisible = true
    },
    /********************** 创作相关结束 ********************/

    /********************** 创作相关 ********************/
    createQuestion: function (isEdit, formData) {
      this.isEdit = isEdit
      if (isEdit) {
        this.questionFormData = formData
      }
      if (!this.isLogin) {
        this.showLogin = true;
        return
      }
      this.questionDialogFormVisible = true
    },
    // 关闭模态框回调函数
    questionBeforeClose: function () {
      this.questionDialogFormVisible = false
      this.drawer = false
      this.$nextTick(() => {
        this.drawer = true
        let tab = {
          "label": "我的问答",
        }
        this.activeName = "我的问答"
        this.handleClick(tab, null)
      })
    },
    createProblem: function (isEdit, formData) {
      this.isEdit = isEdit
      if (isEdit) {
        this.problemFormData = formData
      }
      if (!this.isLogin) {
        this.showLogin = true;
        return
      }
      this.dialogProblemFormVisible = true
    },
    // 关闭面经模态框回调函数
    problemBeforeClose: function () {
      this.dialogProblemFormVisible = false
      this.drawer = false
      this.$nextTick(() => {
        this.drawer = true
      })
    },
    /********************** 创作相关结束 ********************/

    setUserReceiveCommentCount: function () {
      getUserReceiveNoticeCount().then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.userReceiveCommentCount = response.data.userReceiveCommentCount
          this.userReceivePraiseCount = response.data.userReceivePraiseCount
          this.userReceiveSystemCount = response.data.userReceiveSystemCount
          this.userReceiveWatchCount = response.data.userReceiveWatchCount
          this.userReceiveMessageCount = response.data.userReceiveMessageCount
          this.userReceiveCollectCount = response.data.userReceiveCollectCount
        }
      });
    },
    getKeyword: function () {
      let tempValue = decodeURI(this.getUrllets()["keyword"]);
      if (
        tempValue == null ||
        tempValue == undefined ||
        tempValue == "undefined"
      ) {
      } else {
        this.keyword = tempValue;
      }
    },
    /**
     * 获取当前所在页面的标题
     * @returns {{}}
     */
    getCurrentPageTitle: function () {
      // 判断是否是通过参数传递的title
      let title = this.getUrllets()["title"];
      console.log("获取url", title)
      if (title) {
        // 如果是通过参数传递，那么直接显示
        this.saveTitle = title;
      } else {
        // 从url中获取title
        let test = window.location.href;
        let start = 0;
        let end = test.length;
        for (let i = 0; i < test.length; i++) {
          if (test[i] == "/") {
            start = i;
          }
          if (test[i] == "?" && i > start) {
            end = i;
          }
        }
        let result = test.substring(start, end);
        this.saveTitle = result;
      }
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
        document.title = this.info.title
        this.showCreateBlog = webConfigData.openCreateBlog == "1" ? true : false
        this.showCreateQuestion = webConfigData.openCreateQuestion == "1" ? true : false
        this.showChat = webConfigData.openChat == "1" ? true : false
        this.componentShowMap = webConfigData.componentShowMap
      } else {
        getWebConfig().then(response => {
          if (response.code == this.$ECode.SUCCESS) {
            this.info = response.data;
            // 存储在Vuex中
            this.setWebConfigData(response.data)
            this.openComment = this.info.openComment
            document.title = this.info.title
            this.showCreateBlog = this.info.openCreateBlog == "1" ? true : false
            this.showCreateQuestion = this.info.openCreateQuestion == "1" ? true : false
            this.showChat = this.info.openChat == "1" ? true : false
            this.componentShowMap = this.info.componentShowMap
          }
        });
      }
    },
    // 获取用户权限码
    getUserAuthCodeList() {
      let userInfo = this.$store.state.user.userInfo
      console.log("获取用户信息", userInfo)
      if (userInfo) {
        let authCode = this.$commonUtil.InitUserAuthCode(userInfo.authCodeList)
        this.setAuthCode(authCode)
        this.authCode = authCode
        console.log("初始化用户信息", authCode)
      }
    },
    /**
     * 截取URL中的参数
     * @returns {{}}
     */
    getUrllets: function () {
      let lets = {};
      let url = window.location.href;
      url = decodeURI(url)
      let parts = url.replace(
        /[?&]+([^=&]+)=([^&#]*)/gi,
        function (m, key, value) {
          lets[key] = value;
        }
      );
      return lets;
    },


    clickSearchIco: function () {
      if (this.keyword != "") {
        this.search();
      }
      this.showSearch = !this.showSearch;
      //获取焦点
      this.$refs.searchInput.focus();
    },
    openHead: function () {
      this.showHead = !this.showHead;
    },
    returnTop: function () {
      window.scrollTo(0, 0);
    },
    userLogin: function () {
      this.showLogin = true;
    },
    userLogout: function () {
      deleteUserAccessToken(getCookie("token"));
      delCookie("token");
      let url = window.parent.location.href;
      let haveToken = url.indexOf("?token")
      if (haveToken != -1) {
        let list = url.split("?token");
        this.isLogin = false;
        window.location.href = list[0]
        let userInfo = {};
        this.setUserInfo(userInfo)
      } else {
        window.location.reload()
      }
    },

    // 点击头像触发的动作
    handleCommand(command) {
      switch (command) {
        case "logout" : {
          this.userLogout();
        }
          ;
          break;
        case "login" : {
          this.userLogin();
        }
          ;
          break;
        case "goUserCenter" : {
          this.$router.push({path: "/userCenter", query: {userUid: this.userInfo.uid}});
        }
          break;
        case "goUserInfo" : {
          // 打开抽屉
          this.drawer = true;
          this.getUserAmountMethod()
        }
          break;
        case "createBlog" : {
          // 创作博客
          this.createBlog(false, null)
        }
          break;
        case "createProblem" : {
          // 发布面经
          this.createProblem(false, null)
        }
          break;
        case "createQuestion" : {
          this.createQuestion(false, null)
        }
          break;
        case "uploadBlog" : {
          this.uploadBlog()
        }
          break;
        case "userMoment": {
          this.$router.push({path: "/moment"});
        }
          break;
        case "comment" : {
          if (!this.isLogin) {
            this.showLogin = true;
            return
          }
          // 判断用户是否未读的回复
          if (this.userReceiveCommentCount > 0) {
            // 设置已阅读
            let params = {}
            params.noticeType = "1"
            this.readNotice()
            this.userReceiveCommentCount = 0
          }
          this.setNoticeType("1")
          this.$router.push({path: "/notice", query: {noticeType: "1"}});
        }
          break;
        case "watch" : {
          if (!this.isLogin) {
            this.showLogin = true;
            return
          }
          // 设置已阅读
          let params = {}
          params.noticeType = "2"
          if (this.userReceiveWatchCount > 0) {
            this.readNotice()
            this.userReceiveWatchCount = 0
          }
          this.setNoticeType("2")
          this.$router.push({path: "/notice", query: {noticeType: "2"}});
        }
          break;
        case "praise" : {
          if (!this.isLogin) {
            this.showLogin = true;
            return
          }
          // 设置已阅读
          let params = {}
          params.noticeType = "3"
          if (this.userReceivePraiseCount > 0) {
            this.readNotice()
            this.userReceivePraiseCount = 0
          }
          this.setNoticeType("3")
          this.$router.push({path: "/notice", query: {noticeType: "3"}});
        }
          break;
        case "system" : {
          if (!this.isLogin) {
            this.showLogin = true;
            return
          }
          // 设置已阅读
          let params = {}
          params.noticeType = "4"
          if (this.userReceiveSystemCount > 0) {
            this.readNotice()
            this.userReceiveSystemCount = 0
          }
          this.setNoticeType("4")
          this.$router.push({path: "/notice", query: {noticeType: "4"}});
        }
          break;
        case "collect" : {
          if (!this.isLogin) {
            this.showLogin = true;
            return
          }
          // 设置已阅读
          let params = {}
          params.noticeType = "6"
          if (this.userReceiveCollectCount > 0) {
            this.readNotice()
            this.userReceiveCollectCount = 0
          }
          this.setNoticeType("6")
          this.$router.push({path: "/notice", query: {noticeType: "6"}});
        }
          break;
        case "message" : {
          this.goChat()
        }
          break;
        case "openVip" : {
          this.$router.push({path: "/vip"});
        }
          break;
      }
    },
    goChat() {
      if (!this.isLogin) {
        this.showLogin = true;
        return
      }
      // 设置已阅读
      let params = {}
      params.noticeType = "5"
      if (this.userReceiveMessageCount > 0) {
        this.readNotice()
        this.userReceiveMessageCount = 0
      }
      this.$router.push({path: "/chat"});
    },
    readNotice() {
      readUserReceiveNoticeCount().then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          // 阅读成功
          this.userReceiveSystemCount = 0
        }
      })
    },
    closeLoginBox: function () {
      this.showLogin = false;
    },
    // 获取浏览器类型, 用于兼容处理
    getBrowser() {
      let sBrowser, sUsrAg = navigator.userAgent;
      if (sUsrAg.indexOf("Firefox") > -1) {
        sBrowser = "Mozilla Firefox";
        this.browserFlag = 2;
        // "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:61.0) Gecko/20100101 Firefox/61.0"
      } else if (sUsrAg.indexOf("Chrome") > -1) {
        sBrowser = "Google Chrome or Chromium";
        this.browserFlag = 1;
        // "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/66.0.3359.181 Chrome/66.0.3359.181 Safari/537.36"
      }
    },
  }
};
</script>

<style>
@import "../assets/css/emoji.css";

#login_container iframe {
  height: 450px;
}

.emoji-panel-btn:hover {
  cursor: pointer;
  opacity: 0.8;
}

.emoji-item-common {
  background: url("../assets/img/emoji_sprite.png");
  display: inline-block;
  zoom: 0.3;
}

.emoji-item-common:hover {
  cursor: pointer;
}

.emoji-size-small {
  zoom: 0.3;
}

</style>


<style scoped>
/deep/ .el-timeline-item__timestamp {
  font-size: 14px;
  font-weight: 600;
  color: rgb(23, 35, 63);
}

.userDropdownMenuItem {
  padding: 0 0
}

.userDropdownMenu {
  padding: 0 0;
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

.createBlog {
  position: absolute;
  top: 16px;
  right: -20px;
}

.el-tag {
  height: 25px;
  line-height: 25px;
  margin-left: 6px;
}

#starlist li .title {
  color: #007fff;
}

.searchbox {
  position: absolute;
  right: 300px;
  top: 0;
  display: flex;
  flex-direction: column;
}

.searchboxDefault {
  position: absolute;
  right: 190px;
  top: 0
}

.create {
  width: 35px;
  height: 40px;
  position: absolute;
  right: 200px;
}

.notice {
  width: 35px;
  height: 35px;
  position: absolute;
  font-size: 24px;
  margin-top: 2px;
  right: 90px;
  cursor: pointer;
}

.vipIcon {
  width: 35px;
  position: absolute;
  right: 135px;
  top: 16px;
  height: 30px;
}

.vipIcon img {
  width: 30px;
  height: 30px;
}

.userInfoAvatar {
  width: 35px;
  height: 35px;
  position: absolute;
  right: 40px;
  top: 12px;
}


.userInfoAvatar img {
  width: 35px;
  height: 35px;
  border-radius: 50%;
}


.el-icon-bell:hover {
  color: #00ccff;
}


.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  margin: 0, 0, 0, 10px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}

.imgBody {
  width: 100px;
  height: 100px;
  border: solid 2px #ffffff;
  float: left;
  position: relative;
}

.imgBody img {
  width: 100px;
  height: 100px;
}

.uploadImgBody {
  margin-left: 5px;
  width: 100px;
  height: 100px;
  border: dashed 1px #c0c0c0;
  float: left;
  position: relative;
}

.uploadImgBody :hover {
  border: dashed 1px #00ccff;
}

.inputClass {
  position: absolute;
}

.commentList {
  width: 100%;
  margin: 0 auto;
}

.commentList .p1 {
  float: left;
}

.commentList .left {
  display: inline-block;
  width: 10%;
  height: 100%;
}

.commentList .left img {
  margin: 0 auto;
  width: 100%;
  border-radius: 50%;
}

.commentList .right {
  display: inline-block;
  width: 85%;
  margin-left: 5px;
}

.commentList .rightTop {
  height: 30px;
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
  line-height: 30px;
  min-height: 60px;
}


.feedbackCard .item .title {
  display: inline-block;
  width: 70px;
  margin-bottom: 10px;
  font-size: 14px;
  font-weight: bold;
}

.feedbackCard .item .content {
  display: inline-block;
  width: 240px;
  margin-bottom: 5px;
}

.myArticle .blogpic {
  float: left;
  max-height: 170px;
  margin-right: 20px;
  display: block;
  overflow: hidden;
}

.myArticle .blogpic img {
  width: 100%;
  max-height: 70px;
  -webkit-transition: all 0.6s ease;
  transition: all 0.6s ease;
  margin-bottom: 10px;
}

.myArticle .bloginfo {
  margin-top: 5px;
}

.myArticle .operation {
  float: right;
  margin-top: 3px;
  margin-left: 20px;
  height: 40px;
}

.search_ico2 {
  width: 60px;
  height: 60px;
  display: block;
  position: absolute;
  right: 0;
  top: 15px;
  padding: 0;
  margin: 0;
  line-height: 60px;
  cursor: pointer;
}

@media only screen and (min-width: 1346px) and (max-width: 1432px) {
  .searchbox {
    position: absolute;
    right: 360px;
    top: 0
  }

  .searchboxDefault {
    position: absolute;
    right: 250px;
    top: 0
  }

  .create {
    right: 280px;
  }

  .notice {
    right: 200px;
  }

  .userInfoAvatar {
    right: 150px;
  }
}

@media only screen and (min-width: 1135px) and (max-width: 1345px) {
  .searchbox {
    position: absolute;
    right: 550px;
    top: 0
  }

  .searchboxDefault {
    position: absolute;
    right: 500px;
    top: 0
  }

  .create {
    right: 470px;
  }

  .notice {
    right: 400px;
  }

  .userInfoAvatar {
    right: 350px;
  }
}

@media only screen and (min-width: 649px) and (max-width: 1134px) {
  .searchbox {
    position: absolute;
    right: 150px;
    top: -2px;
  }

  .create {
    right: 50px;
    display: none;
  }

  .notice {
    right: 100px;
    top: -2px;
  }

  .userInfoAvatar {
    width: 35px;
    height: 35px;
    position: absolute;
    right: 45px;
    top: 10px;
  }
}

@media only screen and (max-width: 649px) {
  .searchbox {
    position: absolute;
    right: 75px;
    top: -3px;
  }

  .create {
    right: 50px;
    display: none;
  }

  .notice {
    right: 45px;
    top: -4px;
  }

  .userInfoAvatar {
    width: 35px;
    height: 35px;
    position: absolute;
    right: 10px;
    top: 10px;
  }
}

</style>

<style lang="scss">
.codeContent {
  max-width: 400px;
  margin: 0 auto;
  padding-top: 25vh;
}

.code-copy-added {
  background-color: #282c34;
  color: white;
  padding: 2px 2px;
  margin: 10px 0;
  text-align: left;
  border-radius: 3px;
  position: relative;
}

.code-copy-added:hover .copy-btn {
  opacity: 1;
}

.gainImg {
  -webkit-filter: grayscale(100%);
  -moz-filter: grayscale(100%);
  -o-filter: grayscale(100%);
  filter: grayscale(100%);
}

.vipIconItem {
  text-align: center;
  margin-top: 20px;
}

.vipIconItem .icon {
  line-height: 30px;
}

.vipIconItem .title {
  line-height: 20px;
  font-size: 14px;
  font-weight: 400;
  letter-spacing: 0px;
  color: rgba(51, 51, 51, 1);
  text-align: center;
  vertical-align: top;
}

.thin-stripe {
  background-image: linear-gradient(to right, transparent, #000 10%, #000 90%, transparent);
  background-size: 100% 2px; /* 根据需要调整高度 */
  background-repeat: no-repeat;
  height: 10px; /* 线条高度 */
}

.vipCardTitle {
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 0px;
  line-height: 31.86px;
  color: rgba(60, 81, 234, 1);
  text-align: left;
  vertical-align: top;

}

.vipCardStyle {
  left: 1230px;
  top: 156px;
  width: 400px;
  opacity: 1;
  background: linear-gradient(180deg, rgba(234, 245, 249, 1) 0%, rgba(252, 253, 255, 1) 46.92%, rgba(191, 220, 252, 1) 100%);
}

.vipButton {
  border-radius: 19px;
  background: linear-gradient(180deg, rgba(65, 182, 252, 1) 0%, rgba(106, 177, 255, 1) 40.11%, rgba(177, 211, 250, 1) 100%);
  box-shadow: 3px 3px 4px 1px rgba(204, 231, 252, 0.6), 2px 10px 9px 2px rgba(152, 187, 214, 0.6),inset -6px 0px 5px  rgba(70, 252, 255, 1),inset 6px -3px 7px 2px rgba(70, 252, 255, 1);
  height: 42px;
  border: none;                /* 去除边框 */

  font-size: 20px;
  font-weight: 900;
  letter-spacing: 2px;
  line-height: 18px;
  color: rgba(255, 255, 255, 1);
  text-align: center;
  vertical-align: top;
}
</style>
