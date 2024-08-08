# 部署蘑菇博客vue-mogu-web


## 添加helm chart仓库

``` bash
helm repo add mogu-chart http://47.106.230.203:9090/
helm repo update
```


## 安装蘑菇博客vue-mogu-web
### 编写参数配置文件`vue-mogu-web.yaml`
```yaml
tee vue-mogu-web.yaml<<-EOF
image:
  pullPolicy: Always
env:
  open:
    NODE_ENV: production
    VUE_MOGU_WEB: http://moguit.local.cn
    PICTURE_API: http://picture.mogu.local.com
    WEB_API: http://web.mogu.local.com
    ELASTICSEARCH: http://es.mogu.local.com
ingress:
  enabled: true
  host: moguit.local.cn
EOF
```
### 安装
```bash
helm upgrade --install vue-mogu-web mogu-chart/vue-mogu-web \
    -f vue-mogu-web.yaml \
    --create-namespace \
    --namespace mogu-system
```
使用`——set key=value[key=value]`参数指定每个参数为`helm install`。

### 卸载chart

```bash
helm uninstall vue-mogu-web -n mogu-system
```

### 验证部署
```bash
curl $(kubectl get svc vue-mogu-web -o jsonpath="{.spec.clusterIP}" -n mogu-system)
```
出现以下类似信息即为成功部署

```html
<!DOCTYPE html><html><head><meta charset=utf-8><meta name=viewport content="width=device-width,initial-scale=1"><title>蘑菇云后台管理系统</title><script id=env>window._env_ = {"WEB_API":"http://localhost:8603","FILE_API":"http://192.169.0.56:8600/","RABBIT_MQ_ADMIN":"http://localhost:15672","SENTINEL_ADMIN":"http://localhost:8070/sentinel/","EUREKA_API":"http://localhost:8761","Search_API":"http://localhost:8605","ADMIN_API":"http://localhost:8601","NODE_ENV":"production","Zipkin_Admin":"http://localhost:9411/zipkin/","DRUID_ADMIN":"http://localhost:8601/druid/login.html","SPRING_BOOT_ADMIN":"http://localhost:8606/wallboard","BLOG_WEB_URL":"http://localhost:9527","ELASTIC_SEARCH":"http://localhost:5601","PICTURE_API":"http://localhost:8602","SOLR_API":"http://localhost:8080/solr"};;</script><link rel="shortcut icon" href=/favicon.ico><link href=/static/css/chunk-libs.825f9043.css rel=stylesheet><link href=/static/css/app.cb3f39cc.css rel=stylesheet></head><script src=static/ckeditor/ckeditor.js type=text/javascript></script><link rel=stylesheet href=https://cdn.bootcdn.net/ajax/libs/element-ui/2.13.2/theme-chalk/index.css><script src=https://cdn.bootcdn.net/ajax/libs/vue/2.6.10/vue.js></script><script src=https://cdn.bootcdn.net/ajax/libs/element-ui/2.13.2/index.js></script><script src=https://cdn.bootcdn.net/ajax/libs/showdown/1.9.1/showdown.min.js></script><script src=https://cdn.bootcdn.net/ajax/libs/turndown/6.0.0/turndown.min.js></script><link rel=stylesheet href=https://cdn.jsdelivr.net/npm/vditor/dist/index.css><script src=https://cdn.jsdelivr.net/npm/vditor/dist/index.min.js defer=defer></script><body><div id=app></div><script>!function(e){function c(c){for(var u,f,h=c[0],d=c[1],r=c[2],k=0,i=[];k<h.length;k++)f=h[k],t[f]&&i.push(t[f][0]),t[f]=0;for(u in d)Object.prototype.hasOwnProperty.call(d,u)&&(e[u]=d[u]);for(o&&o(c);i.length;)i.shift()();return a.push.apply(a,r||[]),n()}function n(){for(var e,c=0;c<a.length;c++){for(var n=a[c],u=!0,f=1;f<n.length;f++){var d=n[f];0!==t[d]&&(u=!1)}u&&(a.splice(c--,1),e=h(h.s=n[0]))}return e}var u={},f={runtime:0},t={runtime:0},a=[];function h(c){if(u[c])return u[c].exports;var n=u[c]={i:c,l:!1,exports:{}};return e[c].call(n.exports,n,n.exports,h),n.l=!0,n.exports}h.e=function(e){var c=[];f[e]?c.push(f[e]):0!==f[e]&&{"chunk-40d7":1,"chunk-f32a":1,"chunk-604f":1,"chunk-6403":1,"chunk-7da6":1,"chunk-1c5e":1,"chunk-2698":1,"chunk-32fa":1,"chunk-3e0e":1,"chunk-20f6":1,"chunk-aebf":1,"chunk-f784":1,"chunk-45f5":1,"chunk-53e6":1,"chunk-61d6":1,"chunk-67c7":1,"chunk-7043":1,"chunk-4925":1,"chunk-7ac0":1,"chunk-7f20":1,"chunk-8cd5":1,"chunk-d02d":1,"chunk-f588":1,"chunk-d56d":1,"chunk-fe38":1,"chunk-fe4e":1,"chunk-4a93":1,"chunk-6e20":1,"chunk-34bb":1}[e]&&c.push(f[e]=new Promise(function(c,n){for(var u="static/css/"+({}[e]||e)+"."+{"/0GP":"31d6cfe0","chunk-40d7":"f2ad54d7","chunk-f32a":"ba0cf22c","chunk-604f":"cd9bfc5c","chunk-6403":"e91c81ce","chunk-7da6":"61589334","/yWt":"31d6cfe0","0OiR":"31d6cfe0","0yUa":"31d6cfe0",AJhj:"31d6cfe0","Hm+T":"31d6cfe0",Jwxw:"31d6cfe0",Jz1E:"31d6cfe0",UUnq:"31d6cfe0",Z9zO:"31d6cfe0","chunk-0978":"31d6cfe0","chunk-0a4f":"31d6cfe0","chunk-1055":"31d6cfe0","chunk-18ad":"31d6cfe0","chunk-1c5e":"e892549f","chunk-2698":"dca0f32d","chunk-32fa":"31ad046e","chunk-34ef":"31d6cfe0","chunk-3e0e":"c2152aa9","chunk-20f6":"ee9cd69c","chunk-aebf":"ea8fd6b7","chunk-f784":"c0d615f7","chunk-45a2":"31d6cfe0","chunk-45f5":"f5acd634","chunk-517b":"31d6cfe0","chunk-53e6":"15fd55e2","chunk-5b4d":"31d6cfe0","chunk-61d6":"afaadbcd","chunk-63d0":"31d6cfe0","chunk-67c7":"47c00187","chunk-722e":"31d6cfe0","chunk-0f5f":"31d6cfe0","chunk-7043":"92620dc8","chunk-4925":"69777f4a","chunk-72fa":"31d6cfe0","chunk-7ac0":"96c188ca","chunk-7c1b":"31d6cfe0","chunk-7f20":"6b136368","chunk-8cd5":"66703a64","chunk-99ff":"31d6cfe0","chunk-d02d":"ec377277","chunk-d56c":"31d6cfe0","chunk-f588":"503c15d8","chunk-d56d":"39e18c84","chunk-fe38":"0fbdc06f","chunk-fe4e":"51ec6a10",dCgC:"31d6cfe0",fnnb:"31d6cfe0","chunk-4a93":"32eee369","chunk-6e20":"4515f2ce",iiPH:"31d6cfe0","chunk-34bb":"992b9af9",y8C7:"31d6cfe0"}[e]+".css",f=h.p+u,t=document.getElementsByTagName("link"),a=0;a<t.length;a++){var d=(k=t[a]).getAttribute("data-href")||k.getAttribute("href");if("stylesheet"===k.rel&&(d===u||d===f))return c()}var r=document.getElementsByTagName("style");for(a=0;a<r.length;a++){var k;if((d=(k=r[a]).getAttribute("data-href"))===u||d===f)return c()}var o=document.createElement("link");o.rel="stylesheet",o.type="text/css",o.onload=c,o.onerror=function(c){var u=c&&c.target&&c.target.src||f,t=new Error("Loading CSS chunk "+e+" failed.\n("+u+")");t.request=u,n(t)},o.href=f,document.getElementsByTagName("head")[0].appendChild(o)}).then(function(){f[e]=0}));var n=t[e];if(0!==n)if(n)c.push(n[2]);else{var u=new Promise(function(c,u){n=t[e]=[c,u]});c.push(n[2]=u);var a,d=document.getElementsByTagName("head")[0],r=document.createElement("script");r.charset="utf-8",r.timeout=120,h.nc&&r.setAttribute("nonce",h.nc),r.src=function(e){return h.p+"static/js/"+({}[e]||e)+"."+{"/0GP":"e03ee439","chunk-40d7":"b036ebc6","chunk-f32a":"7e070eab","chunk-604f":"f3618986","chunk-6403":"64a03a9d","chunk-7da6":"107bf5a7","/yWt":"551434fe","0OiR":"d36d731a","0yUa":"c5a7e52b",AJhj:"456fd9f5","Hm+T":"6fc2a978",Jwxw:"7a53ab7e",Jz1E:"90411057",UUnq:"a9db44b6",Z9zO:"0c5c5cd5","chunk-0978":"3b2167d5","chunk-0a4f":"44257b43","chunk-1055":"8961726e","chunk-18ad":"9e9519bd","chunk-1c5e":"c322616c","chunk-2698":"939b657e","chunk-32fa":"c1a2625c","chunk-34ef":"873012ff","chunk-3e0e":"e3e62fb2","chunk-20f6":"f5b723b1","chunk-aebf":"f98d77dd","chunk-f784":"bbcfeac4","chunk-45a2":"060ee956","chunk-45f5":"95e2d132","chunk-517b":"547b3a39","chunk-53e6":"c9b9aad5","chunk-5b4d":"237df214","chunk-61d6":"1af61579","chunk-63d0":"6acda9e7","chunk-67c7":"fd7bc2e1","chunk-722e":"cd44cee8","chunk-0f5f":"22cdf23f","chunk-7043":"0653685f","chunk-4925":"c442b573","chunk-72fa":"774aa9d6","chunk-7ac0":"c1df3f66","chunk-7c1b":"98cab630","chunk-7f20":"02b36254","chunk-8cd5":"a968efa3","chunk-99ff":"2093c77d","chunk-d02d":"281ae079","chunk-d56c":"2d181bce","chunk-f588":"2083f8be","chunk-d56d":"c69bba56","chunk-fe38":"4933fa32","chunk-fe4e":"7c10287c",dCgC:"83f831ab",fnnb:"4768c0c0","chunk-4a93":"c7c84cd1","chunk-6e20":"9fa4082b",iiPH:"4be4d00c","chunk-34bb":"59d01b27",y8C7:"45308312"}[e]+".js"}(e),a=function(c){r.onerror=r.onload=null,clearTimeout(k);var n=t[e];if(0!==n){if(n){var u=c&&("load"===c.type?"missing":c.type),f=c&&c.target&&c.target.src,a=new Error("Loading chunk "+e+" failed.\n("+u+": "+f+")");a.type=u,a.request=f,n[1](a)}t[e]=void 0}};var k=setTimeout(function(){a({type:"timeout",target:r})},12e4);r.onerror=r.onload=a,d.appendChild(r)}return Promise.all(c)},h.m=e,h.c=u,h.d=function(e,c,n){h.o(e,c)||Object.defineProperty(e,c,{enumerable:!0,get:n})},h.r=function(e){"undefined"!=typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})},h.t=function(e,c){if(1&c&&(e=h(e)),8&c)return e;if(4&c&&"object"==typeof e&&e&&e.__esModule)return e;var n=Object.create(null);if(h.r(n),Object.defineProperty(n,"default",{enumerable:!0,value:e}),2&c&&"string"!=typeof e)for(var u in e)h.d(n,u,function(c){return e[c]}.bind(null,u));return n},h.n=function(e){var c=e&&e.__esModule?function(){return e.default}:function(){return e};return h.d(c,"a",c),c},h.o=function(e,c){return Object.prototype.hasOwnProperty.call(e,c)},h.p="/",h.oe=function(e){throw console.error(e),e};var d=window.webpackJsonp=window.webpackJsonp||[],r=d.push.bind(d);d.push=c,d=d.slice();for(var k=0;k<d.length;k++)c(d[k]);var o=r;n()}([]);</script><script src=/static/js/chunk-libs.94acede4.js></script><script src=/static/js/app.eea37e21.js></script></body></html>
```
