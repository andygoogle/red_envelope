package com.zzhl.wechat.dto;

/**
 * 发送客服消息接口
 */
public class WechatSendKfMessage {
    private String touser;  // 用户的openid
    private String msgtype; // 消息类型，文本为text，图文链接为link

//    private String pagepath;    // 小程序的页面路径，跟app.json对齐，支持参数，比如pages/index/index?foo=bar
//    private String thumb_media_id;  // 小程序消息卡片的封面， image类型的media_id，通过新增素材接口上传图片文件获得，建议大小为520*416

    private Text text;    // 文本消息
    private Link link;    // 图文链接
    private Image image;   // 图片消息
    private String miniprogrampage; // 小程序卡片消息

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getMiniprogrampage() {
        return miniprogrampage;
    }

    public void setMiniprogrampage(String miniprogrampage) {
        this.miniprogrampage = miniprogrampage;
    }

    public class Text {
        private String content; // 文本消息内容

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public class Image {
        private String media_id;    // 发送的图片的媒体ID，通过新增素材接口上传图片文件获得

        public String getMedia_id() {
            return media_id;
        }

        public void setMedia_id(String media_id) {
            this.media_id = media_id;
        }
    }

    public class Link {
        private String title;       // 消息标题
        private String description;     // 图文链接消息
        private String url;         // 图文链接消息被点击后跳转的链接
        private String picurl;      // 图文链接消息的图片链接，支持 JPG、PNG 格式，较好的效果为大图 640 X 320，小图 80 X 80

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPicurl() {
            return picurl;
        }

        public void setPicurl(String picurl) {
            this.picurl = picurl;
        }

        private String thumb_url;

        public String getThumb_url() {
            return thumb_url;
        }

        public void setThumb_url(String thumb_url) {
            this.thumb_url = thumb_url;
        }
    }
}
