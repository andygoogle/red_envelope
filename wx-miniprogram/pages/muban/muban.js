var net = require('../../utils/net.js')
var app = getApp()
var Base64 = require('../../utils/we-base64.js')
Page({

  /**
   * 页面的初始数据
   */
  data: {
    imgsrc: '',
    // list: [
    //   {
    //     'id': '../image/HB1-W.jpg',
    //   },
    //   {
    //     'id': '../image/HB2-W.jpg',
    //   },
    //   {
    //     'id': '../image/HB3-W.jpg',
    //   },
    //   {
    //     'id': '../image/HB4-W.jpg',
    //   }
    // ]
    list: null
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    wx.hideShareMenu()
    var that = this
    net.post(
      app.globalData.serverUrl + '/templates',
      {
        'pageNumber': 1,
        'pageSize': 20
      },
      function (res) {
        if (res.resultCode == 1000) {
          console.log(res.resultData)
          var list = res.resultData
          if (list != undefined && list.length > 0) {
            for (var i = 0; i < list.length; i++) {
              list[i].url = app.globalData.serverUrl + "/file/name/" + Base64.encode('templates/' + list[i].url)
            }
          }
          that.setData({
            list: list
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
  },

  hbxx: function(e) {
    var that = this;
    var pages = getCurrentPages();
    var prevPage = pages[pages.length - 2];  //上一个页面
    prevPage.setData({
      imgSrc: e.currentTarget.dataset.src,
      templateId: e.currentTarget.dataset.index,
      muBan: true
    })
    that.setData({
      imgSrc: e.currentTarget.dataset.src
    })
    wx.switchTab({
      url: '../home/home',
    })
  }
})