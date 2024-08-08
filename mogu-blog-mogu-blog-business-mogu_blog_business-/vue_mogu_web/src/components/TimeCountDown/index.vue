<template>
	<span :endTime="endTime" :endText="endText">
		<slot>
      <span v-html="content"></span>
    </slot>
	</span>
</template>

<script>
export default {
  data(){
    return {
      content: '',
    }
  },
  props:{//接收父组件的数据
    startTime: {type:String,default:''},
    endTime: {type:String,default:''},
    endText: {type:String,default:'活动已结束'},
    startText: {type:String,default:'优惠活动未开始'},
  },
  watch: {//监听时间的变化
    endTime() {
      this.countdowm(this.startTime, this.endTime)
    }
  },
  mounted () {
    console.log("监听时间", this.endTime)
    this.countdowm(this.startTime, this.endTime)
  },
  methods: {
    countdowm(startTimestamp, timestamp){
      let self = this;
      let timer = setInterval(function(){
        let nowTime = new Date();
        let endTime = new Date(timestamp * 1000);
        let startTime = new Date(startTimestamp * 1000);
        let t = endTime.getTime() - nowTime.getTime();
        let t2 = startTime.getTime() - nowTime.getTime();
        if (t2 > 0) {
          clearInterval(timer);
          self.content = self.startText;
          return
        }

        if(t > 0){
          let day = Math.floor(t/86400000);
          let hour=Math.floor((t/3600000)%24);
          let min=Math.floor((t/60000)%60);
          let sec=Math.floor((t/1000)%60);
          hour = hour < 10 ? "0" + hour : hour;
          min = min < 10 ? "0" + min : min;
          sec = sec < 10 ? "0" + sec : sec;
          let format = '';
          if(day > 0){
            format = ` <span class="red">${day}</span> 天 <span class="red">${hour}</span> 小时 <span class="red">${min}</span> 分 <span class="red">${sec}</span> 秒 `;
          }
          if(day <= 0 && hour > 0 ){
            format = `<span class="red">${hour}</span> 小时 <span class="red">${min}</span> 分 <span class="red">${sec}</span> 秒 `;
          }
          if(day <= 0 && hour <= 0){
            format =`<span class="red">${min}</span> 分 <span class="red">${sec}</span> 秒 `;
          }
          self.content = "限时优惠仅剩" + format;
        } else{
          clearInterval(timer);
          self.content = self.endText;
        }
      },1000);
    }
  }
}
</script>

<style>
.red {
  color: red;
  font-weight: 600;
}
</style>
