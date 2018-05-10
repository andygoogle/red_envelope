package com.zzhl.wechat.dto;

import java.util.Map;

/**
 * 发送模板消息
 */
public class WechatSendTemplateMessage {
    private String touser;      // 接收者（用户）的 openid
    private String template_id;     // 所需下发的模板消息的id
    private String page;        // 点击模板卡片后的跳转页面，仅限本小程序内的页面。支持带参数,（示例index?foo=bar）。该字段不填则模板无跳转。
    private String form_id;     // 表单提交场景下，为 submit 事件带上的 formId；支付场景下，为本次支付的 prepay_id
    private Map<String, Keyword> data;        // 模板内容，不填则下发空模板
    private String emphasis_keyword;    // 模板需要放大的关键词，不填则默认无放大

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getForm_id() {
        return form_id;
    }

    public Map<String, Keyword> getData() {
        return data;
    }

    public void setData(Map<String, Keyword> data) {
        this.data = data;
    }

    public void setForm_id(String form_id) {
        this.form_id = form_id;
    }

    public String getEmphasis_keyword() {
        return emphasis_keyword;
    }

    public void setEmphasis_keyword(String emphasis_keyword) {
        this.emphasis_keyword = emphasis_keyword;
    }

    @Override
    public String toString() {
        return "WechatSendTemplateMessage{" +
                "touser='" + touser + '\'' +
                ", template_id='" + template_id + '\'' +
                ", page='" + page + '\'' +
                ", form_id='" + form_id + '\'' +
                ", data='" + data + '\'' +
                ", emphasis_keyword='" + emphasis_keyword + '\'' +
                '}';
    }

    public class Keyword {
        private String value;
        private String color;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }
}
