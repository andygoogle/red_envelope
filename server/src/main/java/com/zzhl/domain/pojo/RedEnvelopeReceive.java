package com.zzhl.domain.pojo;

public class RedEnvelopeReceive {
    private Integer id;

    private Integer uid;

    private Integer redEnvelopeId;

    private Integer fee;

    private Long createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getRedEnvelopeId() {
        return redEnvelopeId;
    }

    public void setRedEnvelopeId(Integer redEnvelopeId) {
        this.redEnvelopeId = redEnvelopeId;
    }

    public Integer getFee() {
        return fee;
    }

    public void setFee(Integer fee) {
        this.fee = fee;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}