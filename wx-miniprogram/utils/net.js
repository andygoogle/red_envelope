function post(url, data, successCallback, failCallback, completeCallback) {
    if(typeof(url) === 'object') { // 兼容json格式的参数
        opt = url;
        url = opt.url;
        data = opt.data;
        successCallback = opt.success;
        failCallback = opt.fail;
        completeCallback = opt.complete;
    }
    var app = getApp();
    wx.request({
      url: url,
      data: data,
      method: 'POST',
      header: {
        'Content-Type': 'application/json'
      },
      success: function(res) {
        console.log('[' + app.getNowFormatDate() + ']url: ' + url + ', state=' + res.data.resultCode);
        successCallback && successCallback.call(null, res.data);
      },
      fail: function(err) {
        failCallback && failCallback.call(err);
      },
      complete: function(data) {
        completeCallback && completeCallback.call(data);
      }
    });
}

module.exports = {
    post: post
};