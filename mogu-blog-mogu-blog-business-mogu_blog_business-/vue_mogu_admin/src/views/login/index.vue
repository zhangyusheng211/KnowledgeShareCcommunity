<template>
  <div>
      <div class="login-container">
          <el-form
              ref="loginForm"
              :model="loginForm"
              :rules="loginRules"
              class="login-form"
              auto-complete="on"
              label-position="left"
          >
              <h3 class="title">{{webSiteInfo.name}}后台管理系统</h3>
              <el-form-item prop="username">
        <span class="svg-container svg-container_login">
          <svg-icon icon-class="user"/>
        </span>
                  <el-input
                      v-model="loginForm.username"
                      ref="userNameInput"
                      name="username"
                      type="text"
                      auto-complete="on"
                      placeholder="username"
                      @keyup.enter.native="handleLogin"
                  />
              </el-form-item>
              <el-form-item prop="password">
                <span class="svg-container">
                  <svg-icon icon-class="password"/>
                </span>
                <el-input
                      :type="pwdType"
                      v-model="loginForm.password"
                      name="password"
                      auto-complete="on"
                      placeholder="password"
                      @keyup.enter.native="handleLogin"
                />
                <span class="show-pwd" @click="showPwd">
          <svg-icon icon-class="eye"/>
        </span>
              </el-form-item>

              <el-checkbox v-model="loginForm.isRememberMe" style="margin:0px 0px 25px 0px;"><span style="color: #eee">七天免登录</span></el-checkbox>

              <span style="color: #eee; font-size: 14px; float: right; cursor: pointer;" @click="forgetPassword">忘记密码？</span>

              <el-form-item>
                  <el-button
                      :loading="loading"
                      type="primary"
                      style="width:100%;"
                      @click.native.prevent="handleLogin"
                  >登 录</el-button>
              </el-form-item>
          </el-form>
          <!--引入粒子特效-->
          <vue-particles
              color="#fff"
              :particleOpacity="0.7"
              :particlesNumber="60"
              shapeType="circle"
              :particleSize="4"
              linesColor="#fff"
              :linesWidth="1"
              :lineLinked="true"
              :lineOpacity="0.4"
              :linesDistance="150"
              :moveSpeed="2"
              :hoverEffect="true"
              hoverMode="grab"
              :clickEffect="true"
              clickMode="push"
              class="lizi"
          >
          </vue-particles>
      </div>

      <!-- 忘记密码弹出框  -->
      <el-dialog title="密码找回" :visible.sync="dialogFormVisible" :modal="false" width="40%">
          <aside>
              将发送一封激活邮件到账号绑定的邮箱中，若未绑定邮箱将无法找回密码<br>
          </aside>
          <el-form :model="form">
              <el-form-item label="登录账号" :label-width="formLabelWidth">
                  <el-input v-model="form.userName" ></el-input>
              </el-form-item>
              <el-form-item label="新密码" :label-width="formLabelWidth">
                  <el-input v-model="form.passWord" type="password"></el-input>
              </el-form-item>
              <el-form-item label="重复密码" :label-width="formLabelWidth">
                  <el-input v-model="form.passWord2" type="password"></el-input>
              </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
              <el-button @click="dialogFormVisible = false">取 消</el-button>
              <el-button type="primary" @click="submitForm">确 定</el-button>
          </div>
      </el-dialog>
  </div>

</template>

<script>
import {getWebSiteInfo, findbackPassword} from '@/api/login'
export default {
  name: "Login",
  metaInfo() {
    return {
      title: this.webSiteInfo.name + "后台管理系统",
      link: [
        { rel: 'icon', href: this.webSiteInfo.logoPhoto }
      ],
      meta: [
        {
          name: "keywords",
          content: this.webSiteInfo.keyword,
        },
        {
          name: "content",
          content: this.webSiteInfo.summary,
        },
        {
          name: "description",
          content: this.webSiteInfo.summary,
        },
        {
          property: "site_name",
          content: this.webSiteInfo.name + "后台管理系统",
        },
      ],
    };
  },
  data() {
    const validateUsername = (rule, value, callback) => {
      callback();
      // if (!isvalidUsername(value)) {
      //   callback(new Error('请输入正确的用户名'))
      // } else {
      //   callback()
      // }
    };
    const validatePass = (rule, value, callback) => {
      if (value.length < 5) {
        callback(new Error("密码不能小于5位"));
      } else {
        callback();
      }
    };
    return {
      loginForm: {
        username: "",
        password: "",
        isRememberMe: false,
      },
      webSiteInfo: {
        name: "",
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", validator: validateUsername }
        ],
        password: [{ required: true, trigger: "blur", validator: validatePass }]
      },
      loading: false,
      pwdType: "password",
      redirect: undefined,
      dialogFormVisible: false,
      form: {
        userName: '',
        passWord: '',
        passWord2: ''
      },
      formLabelWidth: '120px'
    };
  },
  watch: {
    // $route: {
    //   handler: function(route) {
    //     this.redirect = route.query && route.query.redirect;
    //   },
    //   immediate: true
    // }
  },
  mounted() {
    // mounted钩子函数，dom已经渲染完毕，可以直接获取到dom对象进行聚焦
    this.$refs.userNameInput.focus()
  },
  created() {
    // created，dom还未开始渲染，因此需要使用this.$nextTick 将其放置在下一个dom渲染操作时执行
    // this.$refs.userNameInput.focus()
    this.getWebInfo()
  },
  methods: {
    submitForm: function() {
      if (this.form.userName == "" || this.form.passWord == "" || this.form.passWord2 == "") {
          this.$message.error("请完善信息")
          return
      }
      if (this.form.passWord != this.form.passWord2) {
        this.$message.error("两次密码不一致")
        return
      }
      let params = {};
      params.userName = this.form.userName;
      params.passWord = this.form.passWord;
      findbackPassword(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.$message.success(response.data)
            this.dialogFormVisible = false
        } else {
          this.$message.error(response.data)
        }
      })
    },
    forgetPassword: function() {
      this.dialogFormVisible = true;
    },
    getWebInfo: function () {
      getWebSiteInfo().then(response => {
        if(response.code == this.$ECode.SUCCESS) {
          this.webSiteInfo = response.data
        }
      });
    },
    inputFocus: function() {
      this.$nextTick(x => {
        this.$refs.userNameInput.focus()
      })
    },
    showPwd() {
      if (this.pwdType === "password") {
        this.pwdType = "";
      } else {
        this.pwdType = "password";
      }
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true;
          this.$store
            .dispatch("Login", this.loginForm)
            .then(response => {
              if (response.code == this.$ECode.SUCCESS) {
                this.$router.push({ path: this.redirect || "/" });
              } else {
                this.$message.error(response.message);
              }
              this.loading = false;
            })
            .catch(() => {
              this.loading = false;
            });
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    }
  }
};
</script>

<style rel="stylesheet/scss" lang="scss">
$bg: #2d3a4b;
$light_gray: #eee;

/* reset element-ui css */
.login-container {
  .el-input {
    display: inline-block;
    height: 47px;
    width: 85%;
    input {
      background: transparent;
      border: 0px;
      -webkit-appearance: none;
      border-radius: 0px;
      padding: 12px 5px 12px 15px;
      color: $light_gray;
      height: 47px;
      &:-webkit-autofill {
        -webkit-box-shadow: 0 0 0px 1000px $bg inset !important;
        -webkit-text-fill-color: #fff !important;
      }
    }
  }
  .el-form-item {
    border: 1px solid rgba(255, 255, 255, 0.1);
    background: rgba(0, 0, 0, 0.1);
    border-radius: 5px;
    color: #454545;
  }
}
</style>

<style rel="stylesheet/scss" lang="scss" scoped>
$bg: #2d3a4b;
$dark_gray: #889aa4;
$light_gray: #eee;

aside {
    background: #eef1f6;
    padding: 8px 24px;
    margin-bottom: 20px;
    border-radius: 2px;
    display: block;
    line-height: 32px;
    font-size: 16px;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen, Ubuntu, Cantarell, "Fira Sans", "Droid Sans", "Helvetica Neue", sans-serif;
    color: #2c3e50;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;

    a {
        color: #337ab7;
        cursor: pointer;

        &:hover {
            color: rgb(32, 160, 255);
        }
    }
}

.login-container {
  position: fixed;
  height: 100%;
  width: 100%;
  background-color: $bg;
  .login-form {
    position: absolute;
    left: 0;
    right: 0;
    width: 520px;
    max-width: 100%;
    padding: 35px 35px 15px 35px;
    margin: 120px auto;
  }
  .tips {
    font-size: 14px;
    color: #fff;
    margin-bottom: 10px;
    span {
      &:first-of-type {
        margin-right: 16px;
      }
    }
  }
  .svg-container {
    padding: 6px 5px 6px 15px;
    color: $dark_gray;
    vertical-align: middle;
    width: 30px;
    display: inline-block;
    &_login {
      font-size: 20px;
    }
  }
  .title {
    font-size: 26px;
    font-weight: 400;
    color: $light_gray;
    margin: 0px auto 40px auto;
    text-align: center;
    font-weight: bold;
  }
  .show-pwd {
    position: absolute;
    right: 10px;
    top: 7px;
    font-size: 16px;
    color: $dark_gray;
    cursor: pointer;
    user-select: none;
  }
}
</style>
