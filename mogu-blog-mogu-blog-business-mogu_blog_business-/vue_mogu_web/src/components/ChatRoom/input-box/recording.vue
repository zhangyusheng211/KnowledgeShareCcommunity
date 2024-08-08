<template>
    <div class="audio-wrapper">
<!--        <span v-if="!recording" class="input-toolbar-icon fa fa-microphone" @click.stop="startRecord"></span>-->
<!--        <span v-if="recording" class="input-toolbar-icon fa fa-microphone-slash" @click.stop="endRecord"></span>-->

      <el-tooltip placement="top" effect="light">
        <div slot="content">语音</div>
        <span v-if="!recording" @click.stop="startRecord" class="iconfont  pointer" style="font-size: 20px;">&#xe687;</span>
        <span v-if="recording" @click.stop="startRecord" class="iconfont  pointer" style="font-size: 20px; color: #0f74a8">&#xe687;</span>
      </el-tooltip>


        <div class="audio-picker" v-show="recording">
            <div id="siri-modern"></div>
            <span style="color: #fff">正在录音: {{seconds}}s</span>
        </div>
    </div>
</template>
<script>

import "../../../../static/js/audio-animation";
import Recorderx, { ENCODE_TYPE } from "recorderx";
const rc = new Recorderx();
export default {
    data() {
        return {
            recording: false,
            siriWave: null,
            seconds: 0,
            interval: null,
            voiceStartTime: null,
            voiceEndTime: null,
        };
    },
    methods: {
        startRecord() {
            rc.start().then(() => {
                this.voiceStartTime = new Date();
                this.recording = true;
                if (!this.siriWave) {
                    this.siriWave = new SiriWave({
                        container: document.getElementById("siri-modern"),
                        width: 400,
                        height: 60,
                        // style: "ios9",
                    })
                }
                this.interval = setInterval(() => {
                    this.seconds += 1;
                }, 1000, 1000);
            }).catch(error => {
                alert('无法获取到麦克风');
            });
        },
        endRecord() {
            let _this = this;
            _this.recording = false;
            clearInterval(this.interval);
            // 状态为1表示正在录制
            if (rc.state == 1) {
                rc.pause();
                _this.voiceEndTime = new Date();
                // 加载音频
                let wav = rc.getRecord({
                    encodeTo: ENCODE_TYPE.WAV,
                    compressible: true,
                });
                // 时长(秒)
                let duration = Math.ceil((_this.voiceEndTime - _this.voiceStartTime));
                // 清空计时字段
                _this.seconds = 0;
                if (duration > 60 * 1000) {
                    _this.$message.error('最多只能发送60秒以内的语音');
                    rc.clear();
                    return;
                }
                if (duration < 1000) {
                    _this.$message.error('录音时间太短');
                    rc.clear();
                    return;
                }

                var formdata = new FormData();
                var name = _this.getMath()+this.getMath()+this.getMath()+'.wav'
                formdata.append("file", wav, name);
                _this.axios({
                    url: "/upload/file",
                    method: "post",
                    data: formdata,
                }).then(res => {
                    rc.clear();
                    _this.sendAudio(res.data, duration);
                });
            }
        },
        //生成随机数
        getMath() {
            return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
        },
        sendAudio(url, duration) {
            this.$emit("sendAudio", url, duration);
        },
    },
};
</script>
<style scoped lang="less">
.audio-wrapper {
    position: relative;
    display: inline-block;
    -ms-flex-item-align: center;
    align-self: center;
}
.audio-picker {
    display: flex;
    flex-wrap: wrap;
    width: 400px;
    list-style: none;
    bottom: 40px;
    left: -40px;
    user-select: none;
}
.audio-picker {
    background: #b4b4b4;
    box-shadow: #ccc 1px 1px 7px;
    border-radius: 5px;
    padding: 10px;

    display: flex;
    position: absolute;

    &.left {
        right: 0;
    }

    &.middle {
        left: 50%;
        transform: translateX(-50%);
    }
}
</style>
