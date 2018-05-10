var net = require('../../utils/net.js');
var util = require('../../utils/util.js');
const app = getApp()
Page({
  data: {
    userInfo: {},
    zje: '',
    zgs: '',
    list0: [],
    page0: 1,
    szje: '',
    szgs: '',
    list1: [],
    page1: 1,
    pageSize: 20,
    winWidth: 0,
    winHeight: 0,
    // tab切换  
    currentTab: 0,
  },
  onLoad: function () {
    var that = this;
    app.getUserInfo(function (userInfo) {
      if (userInfo != null) {
        that.setData({
          userInfo: userInfo
        })
        //我发出的
        that.mySend();
      }
    });

    wx.getSystemInfo({
      success: function (res) {
        that.setData({
          winWidth: res.windowWidth,
          winHeight: res.windowHeight
        });
      }
    });
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {
    var that = this;
    if (that.data.currentTab == 0) {
      that.data.page0 = 1;
      that.data.list0 = [];
      that.mySend();
    } else {
      that.data.page1 = 1;
      that.data.list1 = [];
      that.myRecieve();
    }
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
    var that = this;
    if (that.data.currentTab == 0) {
      that.data.page0++;
      that.mySend();
    } else {
      that.data.page1++;
      that.myRecieve();
    }
  },

  /** 
   * 滑动切换tab 
   */
  bindChange: function (e) {
    var that = this;
    that.setData({ currentTab: e.detail.current });
  },
  /** 
   * 点击tab切换 
   */
  swichNav: function (e) {
    var that = this;
    var selectTab = e.target.dataset.current;
    if (this.data.currentTab == selectTab) {
      return false;
    } else {
      that.setData({
        currentTab: selectTab
      })
      if (selectTab == 0) {
        if (that.data.list0 == null || that.data.list0.length == 0) {
          that.mySend();
        }
      } else {
        if (that.data.list1 == null || that.data.list1.length == 0) {
          that.myRecieve();
        }
      }
    }
  },

  /**
   * 我发出的
   */
  mySend: function () {
    var that = this;
    wx.showToast({
      icon: 'loading',
      title: '加载中',
      mask: true
    })
    net.post(
      app.globalData.serverUrl + '/mySend',
      {
        'uid': that.data.userInfo.id,
        'pageNumber': that.data.page0,
        'pageSize': that.data.pageSize
      },
      function (res) {
        if (res.resultCode == 1000) {
          var datas = res.resultData.redEnvelopes
          //遍历转换时间格式
          for (var i = 0; i < datas.length; i++) {
            datas[i].createTime = util.formatTime(datas[i].createTime, 'M月D日 h:m')
          }
          var list = that.data.list0;
          list = list.concat(res.resultData.redEnvelopes);
          that.setData({
            zje: res.resultData.fee,
            zgs: res.resultData.number,
            list0: list
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
      function () {
        wx.hideToast();
      }
    )
  },

  /**
   * 我收到的
   */
  myRecieve: function () {
    var that = this;
    wx.showToast({
      icon: 'loading',
      title: '加载中',
      mask: true
    })
    net.post(
      app.globalData.serverUrl + '/myReceive',
      {
        'uid': that.data.userInfo.id,
        'pageNumber': that.data.page1,
        'pageSize': that.data.pageSize
      },
      function (res) {
        if (res.resultCode == 1000) {
          var datas = res.resultData.redEnvelopes
          //遍历转换时间格式
          for (var i = 0; i < datas.length; i++) {
            datas[i].createTime = util.formatTime(datas[i].createTime, 'M月D日 h:m')
          }
          var list = that.data.list1;
          list = list.concat(res.resultData.redEnvelopes);
          that.setData({
            szje: res.resultData.fee,
            szgs: res.resultData.number,
            list1: list,
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
  },

  /**
   * 进入红包详情页
   */
  jumpHbxq: function (e) {
    var id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: '../hbxq/hbxq?hbid=' + id
    })
  },

  problem: function () {
    wx.switchTab({
      url: '../problem/problem'
    })
  }
})  