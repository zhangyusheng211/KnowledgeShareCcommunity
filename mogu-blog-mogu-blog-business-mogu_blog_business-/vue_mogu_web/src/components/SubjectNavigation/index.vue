<template>
  <div class="book-directory-comp beautify-scrollbar-warp book-directory" cur-index="-1">
    <div  style="padding: 0px 15px;">
      <div>
          <el-button style="width: 100%" type="primary" @click="goSubject">【专栏】{{subject.subjectName}} </el-button>
      </div>
    </div>
    <div  class="section-list">
      <a  class="section" v-for="(subjectItem, index) in dataList" v-if="subjectItem.blog" :href="subjectItem.blog.type == 0 ? VUE_MOGU_WEB + '/info/'+subjectItem.blog.oid  + '?subject=' + subjectItem.subjectUid : subjectItem.blog.outsideLink" :class="subjectItem.blog.uid == entityUid ? 'route-active': ''" >
        <div  class="left">
          <div  class="index">{{index + 1}}</div>
        </div>
        <div  class="center">
          <div  class="main-line">
            <div  class="title">
              <!--标题-->
              <span  class="title-text">{{subjectItem.blog.title}}</span>
            </div>
            <div  class="right">

              <div class="tryRead" v-if="subjectItem.tryRead == '1'">试读</div>

              <!--部分访问权限属于加锁状态-->
              <div  class="lock" v-else-if="elementsContains(subject.visitAuth, ['5', '6', '10', '11', '12', '13'])">
                <i class="el-icon-lock"></i>
              </div>

              <div  class="label" v-else>
                免费
              </div>
              <div  class="lock" v-else>
                <i class="el-icon-lock"></i>
              </div>

            </div>
          </div>
          <div class="sub-line">
            <span >学习时长: {{calculateReadingTime(subjectItem.blog.charCount)}}分钟</span>
          </div>
        </div>
      </a>

    </div>
  </div>

</template>

<script>
export default {
  name: "index",
  props: ['dataList', 'entityUid', 'subject'],
  data() {
    return {
      VUE_MOGU_WEB: process.env.VUE_MOGU_WEB,
    }
  },
  created() {
    let that = this
    setTimeout(() => {
      that.scrollTo("route-active")
    }, 500);

  },
  computed: {

  },
  mounted() {
    let that = this
    setTimeout(() => {
      that.scrollTo("route-active")
    }, 500);
  },
  methods: {
    // 将输入值分割成数组
    elementsContains(visitAuth, elements) {
      if (!visitAuth) {
        return false
      }
      let visitAuthList = visitAuth.split(',').map(element => element.trim().toLowerCase());
      let contains = elements.some(element => visitAuthList.includes(element));
      return contains
    },
    goSubject() {
      this.$router.push("/subject/" + this.subject.uid)
    },
    // 统计阅读事件
    calculateReadingTime(numOfChars) {
        const WORDS_PER_MINUTE = 400;
        const words = numOfChars / 2; // 假设一个单词平均长度为2个字符
        const minutes = words / WORDS_PER_MINUTE;
        return Math.ceil(minutes); // 返回向上取整后的阅读时间（单位：分钟）
    },
scrollTo(className) {
  this.$nextTick(() => { // 等待 DOM 渲染完成
    const el = document.querySelector(`.${className}`);
    if(el) { // 如果元素存在，则滚动到目标位置
      el.scrollIntoView({ block: 'center', behavior: 'auto' });
    }
  });
},
  }

}
</script>

<style scoped>


.book-directory-comp {
  color: #fff;
  padding: 18px 0
}

.book-directory-comp .section-list {
  width: 320px
}

.book-directory-comp .section-list .section {
  position: relative;
  display: flex;
  justify-content: flex-start;
  transition: all .2s;
  padding: 9px 16px;
  cursor: pointer
}

.book-directory-comp .section-list .section:hover {
  background-color: #f7f8fa;
}

.book-directory-comp .section-list .section.unfinished {
  cursor: not-allowed
}

.book-directory-comp .section-list .section.route-active .center .main-line .title,.book-directory-comp .section-list .section.route-active .left .index {
  color: #1e80ff;
}

.book-directory-comp .section-list .section.route-active .center .main-line .title .icon-camera path,.book-directory-comp .section-list .section.route-active .left .index .icon-camera path {
  fill: currentColor
}

.book-directory-comp .section-list .section.route-active:after {
  content: "";
  position: absolute;
  width: 4px;
  height: 24px;
  left: 0;
  top: 9px;
  background: #1e80ff;
  border-radius: 0 8px 8px 0
}

.book-directory-comp .section-list .section .left .index {
  font-weight: 600;
  font-size: 16px;
  line-height: 24px;
  color: #8a919f;
  padding: 0 6px;
  min-width: 26px;
  text-align: center
}

.book-directory-comp .section-list .section .center {
  flex-grow: 1
}

.book-directory-comp .section-list .section .center .main-line {
  font-size: 15px;
  line-height: 24px;
  display: flex
}

.book-directory-comp .section-list .section .center .main-line .title {
  font-size: 0;
  flex: 1;
  color: #252933;
}

.book-directory-comp .section-list .section .center .main-line .title .icon-camera {
  vertical-align: middle;
  display: inline-block;
  margin-right: 6px
}

.book-directory-comp .section-list .section .center .main-line .title .icon-camera path {
  fill: #8a919f;
}

.book-directory-comp .section-list .section .center .main-line .title .title-text {
  vertical-align: bottom;
  font-size: 16px;
  line-height: 24px
}

.book-directory-comp .section-list .section .center .main-line .right {
  margin-left: 15px
}

.book-directory-comp .section-list .section .center .main-line .right .lock {
  width: 40px;
  text-align: center
}

.book-directory-comp .section-list .section .center .main-line .right .lock path {
  fill: #c2c8d1;
}

.book-directory-comp .section-list .section .center .main-line .right .label {
  height: 24px;
  background: #f8f8f8;
  line-height: 24px;
  border-radius: 12px;
  padding: 0 8px;
  color: #83b4e5;
  font-size: 12px;
  white-space: nowrap
}

.book-directory-comp .section-list .section .center .main-line .right .tryRead {
  height: 24px;
  background: #f8f8f8;
  line-height: 24px;
  border-radius: 12px;
  padding: 0 8px;
  color: #67c23a;
  font-size: 12px;
  white-space: nowrap
}

.book-directory-comp .section-list .section .center .sub-line {
  display: flex;
  align-items: center;
  margin-top: 6px;
  font-size: 13px;
  color: #8a919f;
  line-height: 24px
}

.book-directory-comp .section-list .section .center .sub-line .label {
  background: #eaf2ff;
  border-radius: 2px;
  line-height: 20px;
  padding: 0 6px;
  color: #1e80ff;
  margin-right: 12px;
  font-size: 12px;
  min-width: 40px;
  white-space: nowrap;
  flex-shrink: 0
}


.beautify-scrollbar-warp {
  overflow-y: hidden;
  overflow-x: hidden;
  height: calc(100% - 200px);
}

.beautify-scrollbar-warp:hover {
  overflow-y: scroll;
  overflow-x: hidden;
}

@media (max-width: 720px) {
  .book-directory-comp {
    overflow-y:auto;
    overflow-x: hidden
  }

  .book-directory-comp .section:hover {
    background-color: transparent
  }
}

@-webkit-keyframes skeleton-keyframes-data-v-6fbc2a2e {
  0% {
    background-position: 0 0
  }

  to {
    background-position: 480px 0
  }
}

@keyframes skeleton-keyframes-data-v-6fbc2a2e {
  0% {
    background-position: 0 0
  }

  to {
    background-position: 480px 0
  }
}

.book-progress {
  position: absolute;
  left: 20px;
  top: calc(100% + 20px)
}

.book-progress ul li {
  margin-bottom: 5px;
  width: 6px;
  height: 6px;
  background-color: #ccc;
  border-radius: 50%
}

.book-progress ul li.active {
  background-color: #aaa
}

.video-label {
  font-size: 12px;
  display: inline-block;
  background: rgba(0,0,0,.6);
  padding: 2px 6px;
  color: #fff;
  font-weight: 500;
  position: absolute;
  right: 8px;
  bottom: 8px;
  border-radius: 2px;
  z-index: 10
}

@-webkit-keyframes skeleton-keyframes-data-v-077cd020 {
  0% {
    background-position: 0 0
  }

  to {
    background-position: 480px 0
  }
}

@keyframes skeleton-keyframes-data-v-077cd020 {
  0% {
    background-position: 0 0
  }

  to {
    background-position: 480px 0
  }
}

.book-thumb{
  position: relative;
  overflow: hidden;
  background-color: rgba(228,230,235,0.5);
  border-radius: 4px
}

.book-thumb .book-thumb-img {
  display: block;
  height: 100%;
  width: 100%
}

.book-summary .book-summary-inner .book-directory {
  box-sizing: border-box;
  -webkit-overflow-scrolling: touch;
  height: calc(100% - 180px);
}
</style>
