var net = require('utils/net.js')
App({
  globalData: {
    userInfo: null,
    deviceInfo: null,
    // serverUrl: 'https://www.mapoer.net/redEnvelope',
    serverUrl: 'http://192.168.6.101:9080'
  },

  onLaunch: function () {
    var that = this;    
    var wxInfo = wx.getSystemInfoSync();
    if ('1.5.4' > wxInfo.SDKVersion) {
      wx.showModal({
        content: '当前微信版本过低，无法使用该功能，请升级到最新微信版本后重试。'
      });
    }
    // 打开次数统计
    that.getUserInfo(function (userInfo) {
      if (userInfo != null) {
        net.post(
          that.globalData.serverUrl + '/wechat/log/uv/' + that.globalData.userInfo.id, null, null, null, null
        );
      }
    });
  },

  onShow: function () {
    
  },

  login: function (cb) {
    var that = this;
    wx.getUserInfo({
      withCredentials: true,
      success: function (res) {
        console.log('获取用户信息成功 user = ', res);
        var userInfo = res;
        wx.login({
          success: function (res) {
            if (res.code) {
              net.post(
                that.globalData.serverUrl + '/wechat/login',
                {
                  'loginCode': res.code,
                  'signature': userInfo.signature,
                  'rawData': userInfo.rawData,
                  'encryptedData': userInfo.encryptedData,
                  'iv': userInfo.iv
                },
                function (res) {
                  if (res.resultCode == 1000) {
                    // 保存用户数据
                    that.globalData.userInfo = res.resultData;
                    wx.setStorageSync('userInfo', that.globalData.userInfo);
                    typeof cb == "function" && cb(that.globalData.userInfo);
                  } else {
                    wx.showToast({
                      title: res.errormsg,
                    });
                  }
                },
                function (res) {
                  wx.showModal({
                    content: '请求服务失败',
                    showCancel: false
                  });
                }, null
              );
            } else {
              console.log('获取用户登录态失败！', res.errMsg)
              typeof cb == "function" && cb(null);
            }
          }, 
          fail: function () {
            console.log('获取用户登录态失败！', res.errMsg)
            typeof cb == "function" && cb(null);
          }
        });   
      },
      fail: function (res) {
        console.log('获取用户信息失败！res: ', res);
        wx.getSetting({
          success: (res) => {
            if (!res.authSetting["scope.userInfo"]) {
              that.noAuthorizeToast()
            }
          }
        })
        typeof cb == "function" && cb(null);
      }
    });
  },

  getUserInfo: function (cb) {
    var that = this;
    if (that.globalData.userInfo) {
      typeof cb == "function" && cb(that.globalData.userInfo)
    } else {
      //调用登录接口
      wx.checkSession({
        success: function () {
          console.log('用户登录态尚未过期');
          try {
            var value = wx.getStorageSync('userInfo')
            if (value) {
              that.globalData.userInfo = value;
              typeof cb == "function" && cb(that.globalData.userInfo)
            } else {
              that.login(cb);
            }
          } catch (e) {
            that.login(cb);
          }
        },
        fail: function () {
          that.login(cb);
        }
      });
    }
  },

  getDeviceInfo: function (cb) {
    var that = this;
    if (that.globalData.deviceInfo) {
      typeof cb == "function" && cb(that.globalData.deviceInfo)
    } else {
      wx.getSystemInfo({
        success: function (res) {
          console.log(res);
          that.globalData.deviceInfo = res;
          typeof cb == "function" && cb(that.globalData.deviceInfo)
        },
        fail: function (res) {
          console.log(res);
        }
      });
    }
  },

  noAuthorizeToast: function () {
    wx.showModal({
      title: '温馨提示',
      content: '小程序需要获取用户信息权限才能正常使用',
      showCancel: false,
      success: function (res) {
        wx.openSetting({
          success: (res) => {
            // res.authSetting = {
            //   'scope.userInfo': true
            // }
            var pages = getCurrentPages();
            var currentPage = pages[pages.length - 1]
            currentPage.onReady();
          }
        })
      }
    })
  },

  jumpIndex: function () {
    wx.navigateTo({
      url: '../index/index'
    })
  },

  jumpAuthorization: function () {
    wx.navigateTo({
      url: '../index/authorization'
    })
  },

  dateFormat: function (fmt, date) {
    var o = {
      "M+": date.getMonth() + 1,                 //月份   
      "d+": date.getDate(),                    //日   
      "h+": date.getHours(),                   //小时   
      "m+": date.getMinutes(),                 //分   
      "s+": date.getSeconds(),                 //秒   
      "q+": Math.floor((date.getMonth() + 3) / 3), //季度   
      "S": date.getMilliseconds()             //毫秒   
    };
    if (/(y+)/.test(fmt))
      fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
      if (new RegExp("(" + k + ")").test(fmt))
        fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
  },

  getNowFormatDate: function () {
    return this.dateFormat('yyyy-MM-dd hh:mm:ss', new Date())
  }

})