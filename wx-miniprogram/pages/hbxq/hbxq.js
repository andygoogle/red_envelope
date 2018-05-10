var app = getApp();
var net = require('../../utils/net.js');
var util = require('../../utils/util.js');
Page({

  /**
   * 页面的初始数据
   */
  data: {
    userInfo: {},
    hbid: 0,
    yhid: 0,
    fxsj: 0,
    lqje: 0,
    lqzt: 0,
    hbUserInfo: {},
    hbje: 0,
    lqgs: 0,
    gs: 0,
    lqjl: [],
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    console.log(options)
    var that = this;
    // 红包详情页面有三个来源打开。
    // 1. 我的记录页面进入，此时传值有hbid
    // 2. 详情页面分享后打开，此时传值有hbid, yhid, fxsj
    // 3. 扫描小程序码打开，此时options.scene不为空，内容格式为 scene="hbid:yhid:fxsj"
    var hbid = null, yhid = null, fxsj = null;
    if (options.hbid != undefined) {
      hbid = options.hbid;
      yhid = options.yhid;
      fxsj = options.fxsj;
    } else {
      if (options.scene != undefined) {
        var dataArray = decodeURIComponent(scene).split(':');
        if (dataArray != null && dataArray.length == 3) {
          hbid = dataArray[0];
          yhid = dataArray[1];
          fxsj = dataArray[2];
        }
      }
    }
    if (hbid == null) {
      console.error('红包详情数据无法获取');
      that.home();
    }
    that.data.hbid = hbid;
    that.data.yhid = yhid;
    that.data.fxsj = fxsj;
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {
    var that = this
    wx.showToast({
      title: '加载中',
      icon: 'loading',
      mask: true
    })
    app.getUserInfo(function (userInfo) {
      if (userInfo != null) {
        // 加载更新页面数据
        net.post(
          app.globalData.serverUrl + '/detail',
          {
            'uid': userInfo.id,
            'redEnvelopeId': that.data.hbid
          },
          function (res) {
            if (res.resultCode == 1000) {
              console.log(res)
              var data = res.resultData;
              //遍历转换时间格式
              if (data.redEnvelopes != null) {
                for (var i = 0; i < data.redEnvelopes.length; i++) {
                  data.redEnvelopes[i].createTime = util.formatTime(data.redEnvelopes[i].createTime, 'M月D日 h:m')
                }
              }
              that.setData({
                userInfo: userInfo,
                lqje: data.myFee,
                lqzt: data.receiveStatus,
                hbUserInfo: data.redEnvelopeUser,
                hbje: data.redEnvelope.fee,
                lqgs: data.redEnvelope.receiveNumber,
                gs: data.redEnvelope.number,
                lqjl: data.redEnvelopes
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
          null,
          function (res) {
            wx.hideToast();
          }
        )
      }
    });
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
  
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {
  
  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {
  
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {
  
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
  
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {
  
  },

  home: function () {
    wx.navigateTo({
      url: '../home/home'
    })
  },
})