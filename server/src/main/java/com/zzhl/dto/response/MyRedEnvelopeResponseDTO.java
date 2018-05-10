package com.zzhl.dto.response;

import com.zzhl.domain.pojo.MyRedEnvelope;

import java.util.List;

public class MyRedEnvelopeResponseDTO {
	private String fee;
	private String number;
	
	private List<MyRedEnvelope> redEnvelopes;

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public List<MyRedEnvelope> getRedEnvelopes() {
		return redEnvelopes;
	}

	public void setRedEnvelopes(List<MyRedEnvelope> redEnvelopes) {
		this.redEnvelopes = redEnvelopes;
	}
}
