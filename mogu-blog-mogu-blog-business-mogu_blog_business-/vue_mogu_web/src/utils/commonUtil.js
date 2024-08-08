import {Message} from 'element-ui'
// import {showdown} from 'showdown'
// import {TurndownService} from 'turndown'

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

/**
 * 操作动作
 * @type {{Comment: string, Collect: string, Pay: string, Praise: string, Tread: string}}
 */
const EAction = {
  // 点赞
  Praise: "Praise",
  // 点踩
  Tread: "Tread",
  // 收藏
  Collect: "Collect",
  // 评论
  Comment: "Comment",
  // 支付
  Pay: "Pay",
}

const errorCode = {
  '401': 'token无效或过期,请重新登录',
  '403': '当前操作没有权限',
  '404': '访问资源不存在',
  '500': '服务器打瞌睡了',
  '501': '服务器打瞌睡了',
  '503': '服务器打瞌睡了',
  'default': '服务器打瞌睡了'
};

/**
 * 全局配置文件
 */
const SysConf = {
  // 默认头像
  defaultAvatar: "https://oos.moguit.cn/image/favicon.png",
  // 默认背景图
  defaultBackgroundImages: [
    "http://picture.moguit.cn//blog/admin/png/2021/12/4/1638581345820.png",
    "http://picture.moguit.cn//blog/admin/png/2021/12/4/1638581344560.png",
    "http://picture.moguit.cn//blog/admin/png/2021/12/4/1638581348162.png",
    "http://picture.moguit.cn//blog/admin/png/2021/12/4/1638581347581.png",
  ],
  // 默认支付图
  defaultPayBackgroundImages: "https://oos.moguit.cn/image/pay_background.png",
  // 专题默认背景图
  defaultSubjectSortImages: [
    'https://image.moguit.cn/subject1.jpg',
    'https://image.moguit.cn/subject2.jpg'
  ]
};

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
    let str = '';
    for (let i = 0; i < tags.length; i++) {
      str += tags[i] + ',';
    }
    return str.substr(0, str.length - 1);
  },
  // 设置meta信息
  setMetaInfo: (title, content, description) => {
    document.title = title;
    document.querySelector('meta[name="keywords"]').setAttribute('content', content);
    document.querySelector('meta[name="description"]').setAttribute('content', description);
    document.querySelector('meta[property="og:title"]').setAttribute('content', title);
    document.querySelector('meta[property="og:description"]').setAttribute('content', description);
    document.querySelector('meta[name="twitter:title"]').setAttribute('content', title);
    document.querySelector('meta[name="twitter:description"]').setAttribute('content', description);
  },
  // 图片居中调整
  replaceImg: (str) => {
    str = str.replace(/(<img[^>]*>)|(<img[^>]*><\/img>)/g, function (match, capture) {
      if (match.indexOf('style') < 0) {
        // 没有style 就添加一个
        return match.replace(/<\s*img/, '<img style=""');
      } else {
        // 有style 就不做处理 直接返回
        return match;
      }
    });
    str = str.replace(/<img[^>]*>/gi, function (match, capture) {
      return match.replace(/style\s*?=\s*?([‘"])[\s\S]*?\1/ig, 'style="max-height:700px;margin:0 auto; display:block;"') // 替换style
    });
    return str;
  },
  // 图片居中调整
  replaceMinImg: (str) => {
    let containChatImg = false
    str = str.replace(/(<img[^>]*>)|(<img[^>]*><\/img>)/g, function (match, capture) {
      // 包含了聊天表情，不替换
      if (match.indexOf('chat-msg-img') >= 0 || match.indexOf('chat-msg-emoji') >= 0) {
        containChatImg = true
        return match
      }
      if (match.indexOf('style') < 0) {
        // 没有style 就添加一个
        return match.replace(/<\s*img/, '<img style=""');
      } else {
        // 有style 就不做处理 直接返回
        return match
      }
    })

    if (containChatImg) {
      return str
    }

    str = str.replace(/<img[^>]*>/gi, function (match, capture) {
      return match.replace(/style\s*?=\s*?([‘"])[\s\S]*?\1/ig, 'style="max-height: 300px; display:block;"') // 替换style
    })
    return str
  },
  // iframe居中调整
  replaceMinIframe: (str) => {
    str = str.replace(/(<iframe[^>]*>)|(<iframe[^>]*><\/iframe>)/g, function (match, capture) {
      if (match.indexOf('style') < 0) {
        // 没有style 就添加一个
        return match.replace(/<\s*iframe/, '<iframe style=""');
      } else {
        // 有style 就不做处理 直接返回
        return match;
      }
    });
    str = str.replace(/<iframe[^>]*>/gi, function (match, capture) {
      return match.replace(/style\s*?=\s*?([‘"])[\s\S]*?\1/ig, 'style="width: 740px; height: 400px; display:block;"') // 替换style
    });
    return str;
  },
  // 移除html标签信息
  removeHtmlTags(value) {
    const div = document.createElement('div');
    div.innerHTML = value;
    return div.textContent || div.innerText || '';
  },
  getValueCount(value) {
    let text = this.removeHtmlTags(value);
    if (text) {
      return text.length;
    }
    return 0;
  },
  /**
   * 删除URL中指定search参数,会将参数值一起删除
   * @param {string} url 地址字符串
   * @param {array} aParam 要删除的参数key数组，如['name','age']
   * @return {string} 返回新URL字符串
   */
  ridUrlParam: (url, params) => {
    for (let index = 0; index < params.length; index++) {
      let item = params[index];
      let fromIndex = url.indexOf(item + "="); //必须加=号，避免参数值中包含item字符串
      if (fromIndex !== -1) {
        // 通过url特殊符号，计算出=号后面的的字符数，用于生成replace正则
        let startIndex = url.indexOf("=", fromIndex);
        let endIndex = url.indexOf("&", fromIndex);
        let hashIndex = url.indexOf("#", fromIndex);

        let reg = "";
        if (endIndex !== -1) {
          // 后面还有search参数的情况
          let num = endIndex - startIndex;
          reg = new RegExp(item + "=.{" + num + "}");
          url = url.replace(reg, "");
        } else if (hashIndex !== -1) {
          // 有hash参数的情况
          let num = hashIndex - startIndex - 1;
          reg = new RegExp("&?" + item + "=.{" + num + "}");
          url = url.replace(reg, "");
        } else {
          // search参数在最后或只有一个参数的情况
          reg = new RegExp("&?" + item + "=.+");
          url = url.replace(reg, "");
        }
      }
    }
    let noSearchParam = url.indexOf("=");
    if (noSearchParam === -1) {
      url = url.replace(/\?/, ""); // 如果已经没有参数，删除？号
    }
    return url;
  },

  /**
   * 字符串转标签
   * @param str
   * @returns {Array}
   */
  stringToTags: str => {
    if (str !== null && str !== '') {
      return str.split(',')
    } else {
      return []
    }
  },
  // 切割字符串
  splitStr: (str, flagCount) => {
    if (str == null || str == '') {
      return ""
    } else if (str.length > flagCount) {
      return str.substring(0, flagCount) + "..."
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
    let converter = new showdown.Converter();
    return converter.makeHtml(text);
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
          node.firstChild.textContent + options.fence
        )
      }
    })

    // 提取数学公式进行转换
    turndownService.addRule('multiplemath', {
      filter(node, options) {
        return node.classList.contains('vditor-math')
      },
      replacement(content, node, options) {
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

  // 将光标移动到最后
  moveCursorToEnd(inputBox, isEnd) {
    let targetIndex = isEnd ? inputBox.children.length - 1 : 0;
    let targetElement = inputBox.children[targetIndex];
    if (!targetElement || targetElement.nodeType === Node.TEXT_NODE) {
      const newElement = document.createElement('span');
      if (isEnd) {
        inputBox.appendChild(newElement);
      } else {
        inputBox.insertBefore(newElement, inputBox.firstChild);
      }
      targetElement = newElement;
    }
    const range = document.createRange();
    let selection = window.getSelection();
    range.selectNodeContents(targetElement);
    range[isEnd ? 'setEnd' : 'setStart'](targetElement, targetElement.childNodes.length);
    range.collapse(isEnd);
    selection.removeAllRanges();
    selection.addRange(range);
    console.log("选区列表：", selection);
  },

  // 转化资源
  convertResource(resource) {
    let product = {
      images: SysConf.defaultPayBackgroundImages,
      chargeType: resource.chargeType,
      title: resource.name,
      summary: resource.summary,
      price: resource.price,
      discountPrice: resource.discountPrice,
      resourceType: "RESOURCE",
      resourceUid: resource.uid,
      payType: resource.payType, // 支付方式，积分支付或现金支付
      payOrderCount: resource.payOrderCount ? resource.payOrderCount : 0,
      betweenDiscount: false,
    }
    // 如果不是折扣价，那么就要保持 price 和 discountPrice 价格一致
    if (product.chargeType != 3) {
      product.discountPrice = product.price
    }
    // 格式化时间
    let now = new Date().getTime() / 1000
    let discountStartTime = 0
    let discountEndTime = 0
    if (resource.discountStartTime) {
      discountStartTime = new Date(resource.discountStartTime).getTime() / 1000
      product.discountStartTime = discountStartTime + ' '
    }
    if (resource.discountEndTime) {
      discountEndTime = new Date(resource.discountEndTime).getTime() / 1000
      product.discountEndTime = new Date(resource.discountEndTime).getTime() / 1000 + ' '
    }
    // 判断是否处于折扣
    if (now > discountStartTime && now < discountEndTime) {
      product.betweenDiscount = true
    }

    console.log("转化后的数据", product)
    return product
  },
  // 转化文章
  convertArticle(entity) {
    let product = {
      images: entity.photoList&&entity.photoList.length > 0? entity.photoList[0]: SysConf.defaultPayBackgroundImages,
      chargeType: 2, // 默认付费
      title: entity.title,
      summary: entity.summary.substring(0, 40),
      price: entity.price,
      discountPrice: entity.discountPrice,
      resourceType: "BLOG",
      resourceUid: entity.uid,
      payType: entity.payType, // 支付方式，积分支付或现金支付
      payOrderCount: entity.payOrderCount ? entity.payOrderCount : 0, //
      betweenDiscount: false,
    }
    // 如果不是折扣价，那么就要保持 price 和 discountPrice 价格一致
    if (entity.chargeType != 3) {
      product.discountPrice = product.price
    }
    // 格式化时间
    let now = new Date().getTime() / 1000
    let discountStartTime = 0
    let discountEndTime = 0
    if (entity.discountStartTime) {
      discountStartTime = new Date(entity.discountStartTime).getTime() / 1000
      product.discountStartTime = discountStartTime + ' '
    }
    if (entity.discountEndTime) {
      discountEndTime = new Date(entity.discountEndTime).getTime() / 1000
      product.discountEndTime = new Date(entity.discountEndTime).getTime() / 1000 + ' '
    }
    // 判断是否处于折扣
    if (now > discountStartTime && now < discountEndTime) {
      product.betweenDiscount = true
    }
    console.log("转化后的数据", product)
    return product
  },
  // 转化专栏
  convertSubject(entity) {
    let product = {
      images: entity.photoList&&entity.photoList.length > 0? entity.photoList[0]: SysConf.defaultPayBackgroundImages,
      chargeType: 2, // 默认付费
      title: entity.subjectName,
      summary: entity.summary.substring(0, 40),
      price: entity.price,
      discountPrice: entity.discountPrice,
      resourceType: "SUBJECT",
      resourceUid: entity.uid,
      payType: entity.payType, // 支付方式，积分支付或现金支付
      payOrderCount: entity.payOrderCount ? entity.payOrderCount : 0, //
      betweenDiscount: false,
    }
    // 如果不是折扣价，那么就要保持 price 和 discountPrice 价格一致
    if (entity.chargeType != 3) {
      product.discountPrice = product.price
    }
    // 格式化时间
    let now = new Date().getTime() / 1000
    let discountStartTime = 0
    let discountEndTime = 0
    if (entity.discountStartTime) {
      discountStartTime = new Date(entity.discountStartTime).getTime() / 1000
      product.discountStartTime = discountStartTime + ' '
    }
    // 判断折扣时间
    if (entity.discountEndTime) {
      discountEndTime = new Date(entity.discountEndTime).getTime() / 1000
      product.discountEndTime = new Date(entity.discountEndTime).getTime() / 1000 + ' '
    }
    // 判断是否处于折扣
    if (now > discountStartTime && now < discountEndTime) {
      product.betweenDiscount = true
    }
    console.log("转化后的数据", product)
    return product
  },

  // 转化会员商品
  convertVip(entity) {
    let product = {
      images: SysConf.defaultPayBackgroundImages,
      chargeType: 2, // 默认付费
      title: entity.name,
      summary: entity.summary.substring(0, 40),
      price: entity.price,
      discountPrice: entity.discountPrice,
      resourceType: "VIP",
      resourceUid: entity.uid,
      payType: entity.payType, // 支付方式，积分支付或现金支付
      payOrderCount: entity.payOrderCount ? entity.payOrderCount : 0, // 支付的订单数
      betweenDiscount: false,
    }
    // 如果不是折扣价，那么就要保持 price 和 discountPrice 价格一致
    if (entity.chargeType !== 3) {
      product.discountPrice = product.price
    }
    // 格式化时间
    let now = new Date().getTime() / 1000
    let discountStartTime = 0
    let discountEndTime = 0
    if (entity.discountStartTime) {
      discountStartTime = new Date(entity.discountStartTime).getTime() / 1000
      product.discountStartTime = discountStartTime + ' '
    }
    // 判断折扣时间
    if (entity.discountEndTime) {
      discountEndTime = new Date(entity.discountEndTime).getTime() / 1000
      product.discountEndTime = new Date(entity.discountEndTime).getTime() / 1000 + ' '
    }
    // 判断是否处于折扣时间内
    if (now > discountStartTime && now < discountEndTime) {
      product.betweenDiscount = true
    }
    return product
  },

  // 转化成付费商品
  convertProduct(entity) {
    let product = {
      images: entity.coverPhotoUrl? entity.coverPhotoUrl: SysConf.defaultPayBackgroundImages,
      chargeType: 2, // 默认付费
      title: entity.name,
      summary: entity.summary.substring(0, 40),
      price: entity.price,
      discountPrice: entity.discountPrice,
      resourceType: "PRODUCT",
      resourceUid: entity.uid,
      payType: entity.payType, // 支付方式，积分支付或现金支付
      payOrderCount: entity.payOrderCount ? entity.payOrderCount : 0, // 支付的订单数
      betweenDiscount: false,
    }
    // 如果不是折扣价，那么就要保持 price 和 discountPrice 价格一致
    if (entity.chargeType !== 3) {
      product.discountPrice = product.price
    }
    // 格式化时间
    let now = new Date().getTime() / 1000
    let discountStartTime = 0
    let discountEndTime = 0
    if (entity.discountStartTime) {
      discountStartTime = new Date(entity.discountStartTime).getTime() / 1000
      product.discountStartTime = discountStartTime + ' '
    }
    // 判断折扣时间
    if (entity.discountEndTime) {
      discountEndTime = new Date(entity.discountEndTime).getTime() / 1000
      product.discountEndTime = new Date(entity.discountEndTime).getTime() / 1000 + ' '
    }
    // 判断是否处于折扣时间内
    if (now > discountStartTime && now < discountEndTime) {
      product.betweenDiscount = true
    }
    return product
  },

  // 校验商品是否在生效周期
  checkDiscount(entity) {
    // 格式化时间
    let now = new Date().getTime() / 1000
    let discountStartTime = 0
    let discountEndTime = 0
    if (entity.discountStartTime) {
      discountStartTime = new Date(entity.discountStartTime).getTime() / 1000
    }
    // 判断折扣时间
    if (entity.discountEndTime) {
      discountEndTime = new Date(entity.discountEndTime).getTime() / 1000
    }
    // 判断是否处于折扣时间内
    if (now > discountStartTime && now < discountEndTime) {
      return true
    }
    return false
  },

  // InitUserAuthCode 初始化权限Code
  InitUserAuthCode(authCodeJson) {
    // 初始化定义权限
    let authCodeMap = {
      'BLOG_AUDIT': false,
      'UPLOAD_BLOG': false,
      'ADD_COMMENT': false,
      'ADD_BLOG': false,
      'ADD_QUESTION': false,
      'ADD_MOMENT': false,
      'ADD_PROBLEM': false,
      'PUBLIC_CHAT': false,
      'PRIVATE_CHAT': false,
    };
    // 定义默认的权限
    if (!authCodeJson) {
      authCodeMap['ADD_COMMENT'] = true
      authCodeMap['ADD_BLOG'] = true
      authCodeMap['ADD_QUESTION'] = true
      authCodeMap['ADD_MOMENT'] = true
      authCodeMap['ADD_PROBLEM'] = true
      authCodeMap['PUBLIC_CHAT'] = true
      authCodeMap['PRIVATE_CHAT'] = true
      return authCodeMap;
    }

    let authCodeList = JSON.parse(authCodeJson);
    authCodeList.forEach(function (item) {
      authCodeMap[item] = true;
    });

    return authCodeMap;
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
  // url追加参数
  urlAppendParams: (url, key, value) => {
    let formatUrl = new URL(url);

    let params = new URLSearchParams(formatUrl.search);
    params.append(key, value);

    formatUrl.search = params.toString();
    return formatUrl.toString()
  },
  // 设置URL参数
  setUrlParams: (key, value) => {
    const currentUrl = window.location.href;
    // 拆分 URL 和查询参数
    const urlParts = currentUrl.split('?');
    const queryString = urlParts[1] || '';
    // 将查询参数字符串解析为对象
    const params = Object.fromEntries(new URLSearchParams(queryString));
    // 修改参数值
    params[key] = value;
    // 将对象转换回查询参数字符串
    const newQueryString = new URLSearchParams(params).toString();
    // 构建新的 URL
    const newUrl = `${urlParts[0]}?${newQueryString}`;
    // 替换url
    return newUrl.toString();
  },

  /**
   * 通用提示信息
   * @type {{success: message.success, warning: message.warning, error: message.error, info: message.info}}
   */
  message: {
    success: function (message) {
      Message({
        showClose: true,
        message: message || '成功',
        type: 'success'
      })
    },
    warning: function (message) {
      Message({
        showClose: true,
        message: message || '警告',
        type: 'warning'
      })
    },
    info: function (message) {
      Message({
        showClose: true,
        message: message || '提示'
      })
    },
    error: function (message) {
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
  EAction,
  errorCode,
  SysConf,
  FUNCTIONS
}
