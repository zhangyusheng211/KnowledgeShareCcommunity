<template>
    <div>
<!--        <span class="input-toolbar-icon fa fa-picture-o" @click.stop="chooseImg"></span>-->
      <el-tooltip placement="top" effect="light">
        <div slot="content">图片</div>
        <span class="iconfont pointer" style="font-size: 20px;"  @click.stop="chooseImg">&#xe8ba;</span>
      </el-tooltip>

        <input v-show="false" id="fileChoose" ref="fileChoose" @change="validateFile" type="file" accept="image/gif,image/jpeg,image/jpg,image/png">
    </div>
</template>
<script>
import {uploadFile} from "../../../api/chat";
import {getCookie} from "@/utils/cookieUtils";
export default {
    data() {
        return {
          pictureSortUid: null,
          uploadPictureHost: null,
          otherData: {}
        };
    },
    created() {

    },
    methods: {
        chooseImg() {
            this.$refs.fileChoose.dispatchEvent(new MouseEvent("click"));
        },
        validateFile() {
            let _this = this;
            let inputFile = this.$refs.fileChoose.files[0];
            if (inputFile) {
                if (
                    inputFile.type !== "image/jpeg" &&
                    inputFile.type !== "image/jpg" &&
                    inputFile.type !== "image/png" &&
                    inputFile.type !== "image/gif"
                ) {
                  this.$message.error("不是有效的图片文件！")
                    return;
                }

                let formdata = new FormData();
                formdata.append("source", "picture");
                formdata.append("userUid", this.$store.state.user.userInfo.uid);
                formdata.append("adminUid", "uid00000000000000000000000000000000");
                formdata.append("projectName", "blog");
                formdata.append("sortName", "chat");
                formdata.append("token", getCookie('token'));
                formdata.append("filedatas", inputFile);

                uploadFile(formdata).then(res => {
                  document.getElementById('fileChoose').value='';
                  if (res.code == "success") {
                    _this.$emit("sendPic", res.data[0].pictureUrl, res.data[0].uid);
                  } else {

                    this.$message.error(res.message)
                  }
                })
            } else {
                return;
            }
        },
    },
};
</script>
