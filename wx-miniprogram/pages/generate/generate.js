// pages/generate/generate.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    userInfo: {},
    hbid: 0,
    imgSrc: '',
    money: '',
    kl: '',
    gs: ''
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    var that =this;
    console.log(options)

    var hbid = parseInt(options.hbid);
    var kl = options.kl;
    var imgSrc = options.imgSrc;
    var money = parseInt(options.money);
    var gs = parseInt(options.gs);
    var hbUserInfo = JSON.parse(options.user);
    if (hbid == undefined || hbid == null || hbid < 0 || hbUserInfo == undefined || hbUserInfo == null) {
      that.jumpHome();
    } else {
      that.setData({
        hbid: hbid,
        userInfo: hbUserInfo,
        imgSrc: imgSrc,
        money: money,
        kl: kl,
        gs: gs  
      });
    }
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
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {
    var that = this;
    var shareUrl = '/pages/hbxq/hbxq?hbid=' + that.data.hbid + '&yhid=' + that.data.userInfo.id + '&fxsj=' + new Date().getTime()
    return {
      title: that.data.kl,
      path: shareUrl,
      success: function (res) {
        console.log('转发成功，res=', res);
      },
      fail: function (res) {
        console.debug("转发失败, res=", res);
      }
    }
  },

  jumpHome: function () {
    wx.navigateTo({
      url: '../home/home'
    })
  }
})