package com.zzhl.domain.pojo;

public class RedEnvelope {
    private Integer id;

    private Integer uid;

    private Integer templateId;

    private String commandText;

    private Integer number;

    private Integer fee;

    private Integer serviceFee;

    private Byte isFinish;

    private Integer receiveNumber;

    private Integer receiveFee;

    private Byte status;

    private Long createTime;

    private Long modifyTime;

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

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public String getCommandText() {
        return commandText;
    }

    public void setCommandText(String commandText) {
        this.commandText = commandText == null ? null : commandText.trim();
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getFee() {
        return fee;
    }

    public void setFee(Integer fee) {
        this.fee = fee;
    }

    public Integer getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(Integer serviceFee) {
        this.serviceFee = serviceFee;
    }

    public Byte getIsFinish() {
        return isFinish;
    }

    public void setIsFinish(Byte isFinish) {
        this.isFinish = isFinish;
    }

    public Integer getReceiveNumber() {
        return receiveNumber;
    }

    public void setReceiveNumber(Integer receiveNumber) {
        this.receiveNumber = receiveNumber;
    }

    public Integer getReceiveFee() {
        return receiveFee;
    }

    public void setReceiveFee(Integer receiveFee) {
        this.receiveFee = receiveFee;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }
}