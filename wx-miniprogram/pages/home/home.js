var net = require('../../utils/net.js')
var app = getApp()
Page({
  data: {
    userInfo: {},
    imgSrc: '../image/HB1-W.jpg',
    templateId: 1,
    modeHid: true,
    muBan: false,
    gs: 0,
    hbje: 0,
    tip: '',
    fwfl: 0.02,
    kl: '',
    mrkl: '新春快乐！'
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    var that = this;
    app.getUserInfo(function (userInfo) {
      if (userInfo != null) {
        that.setData({
          userInfo: userInfo
        });
        net.post(
          app.globalData.serverUrl + '/send/step1/' + userInfo.id, null,
          function (res) {
            if (res.resultCode == 1000) {
              console.log(res.resultData)
              that.setData({
                ye: parseFloat(res.resultData.balance),
                fwfl: parseFloat(res.resultData.serviceFeeRate)
              })
            } else {
              wx.showModal({
                title: '提示',
                content: res.resultMessage,
                showCancel: false,
                confirmText: '我知道了'
              })
            }
          },
          null, null
        )
      }
    });
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {
   
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    var that = this;
    if (this.data.muBan) {
      that.setData({
        muBan: false
      })
      return;
    }
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {
  
  },

  /*表单提交 */
  formSubmit: function (e) {
    var that = this;
    if (that.data.gs == 0 || that.data.hbje == 0) {
      wx.showModal({
        title: '提示',
        content: '数量和赏金不能为空',
        showCancel: false,
        confirmText: '我知道了'
      })
      return;
    }
    if (that.data.hbje / that.data.gs < 0.2) {
      wx.showModal({
        title: '提示',
        content: '平均每人打赏金额不能低于0.2元',
        showCancel: false,
        confirmText: '我知道了'
      })
      return;
    }
    wx.showLoading({
      title: '请稍后',
      mask: true
    })
    var kl = that.data.kl == '' ? that.data.mrkl : that.data.kl;
    net.post(
      app.globalData.serverUrl + '/send',
      {
        'uid': that.data.userInfo.id,
        'templateId': that.data.templateId,
        'commandText': kl,
        'number': that.data.gs,
        'fee': that.data.hbje
      },
      function (res) {
        console.log('[' + app.getNowFormatDate() + '] pay end. result = ', res);
        if (res.resultCode == 1000) {
          var hbid = res.resultData.redEnvelopeId;
          that.generate()
          // wx.requestPayment({
          //   timeStamp: res.resultData.timeStamp,
          //   nonceStr: res.resultData.nonceStr,
          //   package: res.resultData.packagea,
          //   signType: res.resultData.signType,
          //   paySign: res.resultData.paySign,
          //   success: function (res) {
          //     console.log('success' + JSON.stringify(res));
          //     if (hbid != null && hbid > 0) {
          //       wx.navigateTo({
          //         url: '../generate/generate?hbid=' + hbid + '&kl=' + kl + '&user=' + JSON.stringify(that.data.userInfo)
          //       })
          //     }
          //   },
          //   fail: function (res) {
          //     console.log('fail' + JSON.stringify(res));
          //   }
          // })
        } else {
          wx.showModal({
            title: '提示',
            content: res.resultMessage,
            showCancel: false,
            confirmText: '我知道了'
          })
        }
      },
      null,
      function () {
        wx.hideLoading();
      }
    )
    console.log('form发生了submit事件，携带数据为：', e.detail.value)
  },

  /**
   * 获取红包个数 
   */
  getGs: function (e) {
    var that = this;
    if (e.detail.value < 1) {
      that.setData({
        tip: '数量最少1个',
        gs: '',//将input至与data中的inputValue绑定
      });
    } else if (e.detail.value > 10000) {
      that.setData({
        tip: '数量最多10000个',
        gs: '10000'
      });
    } else {
      that.setData({
        gs: e.detail.value,//将input至与data中的inputValue绑定
      })
    }
    setTimeout(function () {
      that.setData({
        tip: '',
      });
    }, 2000);
  },

  /**
   * 获取红包金额，并生成服务费 
   */
  getHbje: function (e) {
    var that = this;
    if (e.detail.value < 1) {
      that.setData({
        tip: '打赏金额不能低于1元',
        hbje: '',
      });
    } else if (e.detail.value > 2000) {
      that.setData({
        tip: '打赏金额不能超过2000元',
        hbje: '2000'
      });
    } else {
      var hbje = parseInt(e.detail.value);
      var xzfje = hbje + hbje * that.data.fwfl;
      if (xzfje > that.data.ye) {
        xzfje = xzfje - that.data.ye;
      } else {
        xzfje = 0;
      }
      that.setData({
        hbje: hbje,
        xzfje: xzfje
      })
    }
    setTimeout(function () {
      that.setData({
        tip: '',
      });
    }, 2000);
  },
  /**
   * 获取口令
   */
  getKl: function (e) {
    var that = this;
    console.log(e.detail.value)
      that.setData({
        kl: e.detail.value
      })
  },
  // 红包放大查看
  fd: function () {
    var that = this;
    that.setData({
      modeHid: false
    })
  },
  modeBind: function () {
    var that = this;
    that.setData({
      modeHid: true
    })
  },
  muban: function () {
    wx.navigateTo({
      url: '../muban/muban',
    })
  },
  generate: function () {
    var that = this
    var kl = that.data.kl == '' ? that.data.mrkl : that.data.kl;
    wx.navigateTo({
      url: '../generate/generate?hbid=11&kl=' + kl + '&gs=' + that.data.gs + '&money=' + that.data.hbje + '&imgSrc=' + that.data.imgSrc + '&user=' + JSON.stringify(that.data.userInfo)
    })
  }
})