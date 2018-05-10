// pages/problem/problem.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    list: [
      {
        'id': '红包怎么玩？',
        'hidden': true,
        'opacity': 1,
        'con': "首先挑选红包样式，接着添加祝福语，然后生成红包发给好友或群就可以了。"
      },
      {
        'id': '生成的红包如何转发给好友？',
        'hidden': true,
        'opacity': 1,
        'con': "在生成红包的页面点击“分享给好友或群”，就可以转发给好友了。"
      },
      {
        'id': '自己发的红包在哪里查看？',
        'hidden': true,
        'opacity': 1,
        'con': "在首页下方点击“我的红包”，即可查看自己发出的红包。"
      },
      {
        'id': '发红包会收取服务费吗？',
        'hidden': true,
        'opacity': 1,
        'con': "发红包会收取2%的手续费。"
      },
      {
        'id': '未领取的金额怎么处理？',
        'hidden': true,
        'opacity': 1,
        'con': "未领取的金额24小时后返还到小程序余额，可直接体现。"
      },
      {
        'id': '如何提现到微信钱包？',
        'hidden': true,
        'opacity': 1,
        'con': "在首页底部点击“余额提现”就可以进行提现。单次提现金额不得少于1元，提现1-3个工作日到账。"
      }
    ]
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    wx.hideShareMenu()
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

  /**
   * 用户点击联系客服时触发
   */
  doSend: function () {
    console.log('客服按钮别触发');
  },

  hiddenBtn: function (e) {
    var that = this;
    // 获取事件绑定的当前组件
    var index = e.currentTarget.dataset.index;
    // 获取list中hidden的值
    // 隐藏或显示内容

    if (that.data.list[index].hidden = !that.data.list[index].hidden) {
      that.data.list[index].opacity = 1;
    } else {
      that.data.list[index].opacity = 0.7;
    }
    that.setData({
      list: that.data.list
    })
  },
})