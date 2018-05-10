var net = require('../../utils/net.js')
var app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    userInfo: {},
    balance: '',
    fee: null,
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    
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
                balance: res.resultData.balance
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

  allWithdrawals: function () {
    var that = this;
    that.setData({
      fee: that.data.balance 
    })
  },

  /*表单提交 */
  formSubmit: function (e) {
    var that = this;
    if (that.data.fee < 1) {
      wx.showModal({
        title: '提示',
        content: '提现金额必须大于1元',
        showCancel: false,
        confirmText: '确定'
      })
      return;
    }
    var balance = parseFloat(that.data.balance) 
    var fee = parseFloat(that.data.fee)
    if (fee > balance) {
      wx.showModal({
        title: '提示',
        content: '账号余额不足',
        showCancel: false,
        confirmText: '确定'
      })
      return;
    }
    wx.showLoading({
      title: '请稍后',
      mask: true
    })
    net.post(
      app.globalData.serverUrl + '/withdrawals',
      {
        'uid': that.data.userInfo.id,
        'fee': that.data.fee
      },
      function (res) {
        console.log('[' + app.getNowFormatDate() + '] pay end. result = ', res);
        if (res.resultCode != 1000) {
          wx.showModal({
            title: '提示',
            content: res.resultMessage,
            showCancel: false,
            confirmText: '我知道了'
          })
        } else {
          that.setData({
            balance: res.resultData.balance
          })
          wx.showModal({
            title: '提示',
            content: '提现成功，请稍后查看钱包余额',
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

  getFee: function (e) {
    var that = this;
    that.setData({
      fee: e.detail.value,//将input至与data中的inputValue绑定
    })
  },

  home: function () {
    wx.switchTab({
      url: '../home/home',
    })
  }
})