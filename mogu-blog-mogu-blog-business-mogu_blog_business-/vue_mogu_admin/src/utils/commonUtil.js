import { Message } from 'element-ui'
// import {showdown} from 'showdown'
// import {TurndownService} from 'turndown'
import showdownKatex from 'showdown-katex'

/** **********************************************************/
/**
 *  全局状态码
 */
const ECode = {
  // 默认页大小
  SUCCESS: "success",
  // 默认页码
  ERROR: "error",
}

const errorCode = {
    '401': 'token无效或过期,请重新登录',
    '403': '当前操作没有权限',
    '404': '访问资源不存在',
    'default': '系统未知错误，请反馈给管理员'
}

/**
 * 全局配置文件
 */
const SysConf = {
  defaultAvatar: "https://oos.moguit.cn/image/favicon.png", // 默认头像
}

/** **********************************************************/

/**
 * 通用工具类
 */
const FUNCTIONS = {

  /**
   * 标签转字符串
   * @param tags
   * @returns {string}
   */
  tagsToString: tags => {
    let str = ''
    for (let i = 0; i < tags.length; i++) {
      str += tags[i] + ','
    }
    return str.substr(0, str.length - 1)
  },
  // 切割字符串
  splitStr: (str, flagCount) => {
    if (str == null || str == '') {
      return ""
    } else if(str.length > flagCount) {
      return str.substring(0, flagCount) + "..."
    } else {
      return str
    }
  },
  /**
   * 切割字符串
   * @param str
   * @param count
   * @returns {string|*}
   */
  strSubstring: (str, count) => {
    if (str == null || str == '') {
      return ""
    } else if(str.length > count) {
      return str.substring(0, count) + "..."
    } else {
      return str
    }
  },
  /**
   * 复制文字到剪切板
   * @param text
   */
  copyText: text => {
    const oInput = document.createElement('input')
    oInput.value = text
    document.body.appendChild(oInput)
    oInput.select() // 选择对象
    document.execCommand('Copy') // 执行浏览器复制命令
    oInput.className = 'oInput'
    oInput.style.display = 'none'
  },
  /**
   * 将Markdown转成Html
   * @param text
   */
  markdownToHtml: text => {
    let converter = new showdown.Converter({
      tables: true,
      extensions: [
        showdownKatex({
          // maybe you want katex to throwOnError
          throwOnError: true,
          // disable displayMode
          displayMode: false,
          // change errorColor to blue
          errorColor: '#1500ff',
        }),
      ],
    });
    let html = converter.makeHtml(text)
    return html;
  },
  /**
   * 将Html转成Markdown
   * @param text
   */
  htmlToMarkdown: text => {
    let turndownService = new TurndownService()

    // 用于提取代码语言
    turndownService.addRule('CodeBlock', {
      filter: function (node, options) {
        return (
          node.nodeName === 'PRE' &&
          node.firstChild &&
          node.firstChild.nodeName === 'CODE'
        )
      },
      replacement: function (content, node, options) {
        let className = node.firstChild.getAttribute('class') || ''
        let language = (className.match(/language-(\S+)/) || [null, ''])[1]
        return (
          '\n\n' + options.fence + language + '\n' +
          node.firstChild.textContent +options.fence
        )
      }
    })

    // 提取数学公式进行转换
    turndownService.addRule('multiplemath', {
      filter (node, options) {
        return node.classList.contains('vditor-math')
      },
      replacement (content, node, options) {
        console.log("中间内容", node.firstChild.textContent)
        return `$$ \n${node.firstChild.textContent}\n $$`
      }
    })

    let turndownPluginGfm = require('turndown-plugin-gfm')
    let gfm = turndownPluginGfm.gfm
    let tables = turndownPluginGfm.tables
    let strikethrough = turndownPluginGfm.strikethrough
    turndownService.use(gfm)
    turndownService.use([tables, strikethrough])

    return turndownService.turndown(text)
  },
  // 图片居中调整
  replaceMinImg: (str) => {
    str = str.replace(/(<img[^>]*>)|(<img[^>]*><\/img>)/g, function(match, capture){
      if(match.indexOf('style') < 0 ){
        // 没有style 就添加一个
        return match.replace(/<\s*img/, '<img style=""');
      } else {
        // 有style 就不做处理 直接返回
        return match
      }
    })
    str = str.replace(/<img[^>]*>/gi, function (match, capture) {
      return match.replace(/style\s*?=\s*?([‘"])[\s\S]*?\1/ig, 'style="height: 200px; display:block;"') // 替换style
    })
    return str
  },
  // iframe居中调整
  replaceMinIframe: (str) => {
    str = str.replace(/(<iframe[^>]*>)|(<iframe[^>]*><\/iframe>)/g, function(match, capture){
      if(match.indexOf('style') < 0 ){
        // 没有style 就添加一个
        return match.replace(/<\s*iframe/, '<iframe style=""');
      } else {
        // 有style 就不做处理 直接返回
        return match
      }
    })
    str = str.replace(/<iframe[^>]*>/gi, function (match, capture) {
      return match.replace(/style\s*?=\s*?([‘"])[\s\S]*?\1/ig, 'style="width: 500px;  display:block;"') // 替换style
    })
    return str
  },
  /**
   * 将Html转成Markdown文件
   * @param title：标题
   * @param text：正文
   */
  htmlToMarkdownFile: (title, text) => {

    title = title || "默认标题"

    let turndownService = new TurndownService()

    let markdown = turndownService.turndown(text)

    //创建一个blob对象,file的一种
    let blob = new Blob([markdown])

    let link = document.createElement('a')

    link.href = window.URL.createObjectURL(blob)

    //配置下载的文件名
    link.download = title + '.md'

    link.click()
  },
  deepClone(obj,hash=new WeakMap()){
    if(obj==null) return obj;   //如果是null 或者undefined 我就不进行拷贝操作
    if(obj instanceof Date) return new Date(obj);
    if(obj instanceof RegExp) return new RegExp(obj);
    //可能是对象 或者普通的值 如果是函数的话，是不需要深拷贝的  因为函数是用来调用的，不需要再拷贝一个新的函数
    if(typeof obj!=='object') return obj;
    // 是对象的话就要进行深拷贝 [] {} Object.prototype.toString.call(obj)==[object Array]?[]:{}
    if(hash.get(obj)) return hash.get(obj);

    let cloneObj=new obj.constructor;
    hash.set(obj,cloneObj);
    for(let key in obj){
      if(obj.hasOwnProperty(key)){
        //实现一个递归拷贝
        cloneObj[key]= this.deepClone(obj[key],hash);
      }
    }
    return cloneObj;
  },

    // 切割字符串
  splitString: function(str, maxLength) {
    if (str.length && str.length > maxLength) {
        return str.slice(0, maxLength - 1) + '...'; // 截取到最大长度减1，然后添加省略号
    }
    return str;
   },

    /**
     * 格式化文件大小
     * @param {number} size 文件大小
     * @returns {string} 文件大小（带单位）
     */
    calculateFileSize: function (size) {
        const B = 1024
        const KB = Math.pow(1024, 2)
        const MB = Math.pow(1024, 3)
        const GB = Math.pow(1024, 4)
        if (!size) {
            return '_'
        } else if (size < KB) {
            return (size / B).toFixed(0) + 'KB'
        } else if (size < MB) {
            return (size / KB).toFixed(1) + 'MB'
        } else if (size < GB) {
            return (size / MB).toFixed(2) + 'GB'
        } else {
            return (size / GB).toFixed(3) + 'TB'
        }
    },
  /**
   * 通用提示信息
   * @type {{success: message.success, warning: message.warning, error: message.error, info: message.info}}
   */
  message: {
    success: function(message) {
      Message({
        showClose: true,
        message: message || '成功',
        type: 'success'
      })
    },
    warning: function(message) {
      Message({
        showClose: true,
        message: message || '警告',
        type: 'warning'
      })
    },
    info: function(message) {
      Message({
        showClose: true,
        message: message || '提示'
      })
    },
    error: function(message) {
      Message({
        showClose: true,
        message: message || '异常',
        type: 'error'
      })
    }
  }
}

export default {
  ECode,
  errorCode,
  SysConf,
  FUNCTIONS
}
