/**
 * WebUtil常用的一些工具类
 */

export function formatData(arr) {
  const params = new URLSearchParams()
  for (var key in arr) {
    params.append(key, arr[key])
  }
  return params
}

export function formatDataToJson(arr) {
  var params = "["
  for (var key in arr) {
    params = params + key + ":" + arr[key] + ","
  }
  params += "]"
  return params
}

export function formatDataToForm(arr) {
  const params = new FormData()
  for (var key in arr) {
    console.log('"' + key + '"', arr[key])
    params.append('"' + key + '"', arr[key])
  }
  return params
}

export function timeAgo(dateTimeStamp) {

    try {
        let result = "";
        let minute = 1000 * 60;      //把分，时，天，周，半个月，一个月用毫秒表示
        let hour = minute * 60;
        let day = hour * 24;
        let week = day * 7;
        let month = day * 30;
        let year = day * 365;
        let now = new Date().getTime();   //获取当前时间毫秒
        dateTimeStamp = dateTimeStamp.substring(0, 18);
        //必须把日期'-'转为'/'
        dateTimeStamp = dateTimeStamp.replace(/-/g, '/');

        let timestamp = new Date(dateTimeStamp).getTime();

        let diffValue = now - timestamp;//时间差

        if (diffValue < 0) {
            return result;
        }
        let minC = diffValue / minute;  //计算时间差的分，时，天，周，月
        let hourC = diffValue / hour;
        let dayC = diffValue / day;
        let weekC = diffValue / week;
        let monthC = diffValue / month;
        let yearC = diffValue / year;

        minC = parseInt(minC)
        hourC = parseInt(hourC)
        dayC = parseInt(dayC)
        weekC = parseInt(weekC)
        monthC = parseInt(monthC)
        yearC = parseInt(yearC)

        if (monthC > 12) {
            result = " " + parseInt(yearC) + "年前"
        } else if (monthC >= 1 && monthC < 12) {
            result = " " + parseInt(monthC) + "月前"
        } else if (weekC >= 1 && weekC <= 4) {
            result = " " + parseInt(weekC) + "周前"
        } else if (dayC >= 1 && dayC <= 6) {
            result = " " + parseInt(dayC) + "天前"
        } else if (hourC >= 1 && hourC <= 23) {
            result = " " + parseInt(hourC) + "小时前"
        } else if (minC >= 1 && minC <= 59) {
            result = " " + parseInt(minC) + "分钟前"
        } else if (diffValue >= 0 && diffValue <= minute) {
            result = "刚刚"
        }
        return result;
    } catch (e) {
        return "刚刚"
    }
}
