package com.zzhl.controller;

import com.zzhl.domain.pojo.RedEnvelopeReceive;
import com.zzhl.dto.BaseResult;
import com.zzhl.dto.requst.*;
import com.zzhl.dto.response.*;
import com.zzhl.exception.CodeException;
import com.zzhl.exception.ErrorCode;
import com.zzhl.service.RedEnvelopeService;
import com.zzhl.service.WechatUserService;
import com.zzhl.util.JSONUtil;
import com.zzhl.util.NumberUtils;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;


@Controller
@RequestMapping
public class RedEnvelopeController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(RedEnvelopeController.class);

	@Autowired
	private WechatUserService wechatUserService;
	@Autowired
	private RedEnvelopeService redEnvelopeService;

	/**
	 * 获取红包模板
	 */
	@RequestMapping(value = "/templates")
	public @ResponseBody BaseResult<Object> templates(@RequestBody Page page) {
		return new BaseResult<>(redEnvelopeService.listTemplate(page == null ? new Page() : page));
	}

	/**
	 * 获取红包模板
	 */
	@RequestMapping(value = "/detail")
	public @ResponseBody BaseResult<Object> detail(@RequestBody DetailRequestDTO requestDTO) {
		if (requestDTO == null || !NumberUtils.isValidId(requestDTO.getUid()) || !NumberUtils.isValidId(requestDTO.getRedEnvelopeId())) {
			throw new CodeException(ErrorCode.PARAMS_ERROR);
		}
		return new BaseResult<>(redEnvelopeService.getDetail(requestDTO.getRedEnvelopeId(), requestDTO.getUid(), 0, 100));
	}

	/**
	 * 红包发送：第一步，进入红包发送页面，获取发送费率等信息
	 */
	@RequestMapping(value = "/send/step1/{uid}")
	public @ResponseBody BaseResult<SendStep1ResponseDTO> sendStep1(@PathVariable Integer uid) {
		if (!NumberUtils.isValidId(uid)) {
			throw new CodeException(ErrorCode.PARAMS_ERROR);
		}
		return new BaseResult<>(redEnvelopeService.sendStep1(uid));
	}

	/**
	 * 红包发送：第二步，提交红包信息
	 */
	@RequestMapping(value = "/send")
	public @ResponseBody BaseResult<SendResponseDTO> send(@RequestBody SendRequestDTO requestDTO) {
		if (requestDTO == null) {
			throw new CodeException(ErrorCode.PARAMS_ERROR);
		}
		return new BaseResult<>(redEnvelopeService.send(requestDTO.getUid(), requestDTO.getTemplateId(), requestDTO.getCommandText(), requestDTO.getFee(), requestDTO.getNumber()));
	}

	/**
	 * 领红包
	 */
	@RequestMapping(value = "/receive")
	public @ResponseBody BaseResult<ReceiveResponseDTO> receive(@RequestBody ReceiveRequestDTO requestDTO) {
		if (requestDTO == null || !NumberUtils.isValidId(requestDTO.getUid()) || !NumberUtils.isValidId(requestDTO.getRedEnvelopeId())) {
			throw new CodeException(ErrorCode.PARAMS_ERROR);
		}

		RedEnvelopeReceive redEnvelopeReceive = redEnvelopeService.receiveRedEnvelope(requestDTO.getRedEnvelopeId(), requestDTO.getUid());
		if (redEnvelopeReceive != null) {
			throw new CodeException(ErrorCode.ERROR);
		}

		ReceiveResponseDTO responseDTO = new ReceiveResponseDTO();
		responseDTO.setRedEnvelopeId(redEnvelopeReceive.getRedEnvelopeId());
		responseDTO.setFee(String.format("%.2f", (double) redEnvelopeReceive.getFee() / 100));
		return new BaseResult<>(responseDTO);
	}

	@RequestMapping(value = "/mySend")
	public @ResponseBody BaseResult<MyRedEnvelopeResponseDTO> mySend(@RequestBody MyRedEvnelopeRequestDTO requestDTO) {
		if (requestDTO == null || !NumberUtils.isValidId(requestDTO.getUid())) {
			throw new CodeException(ErrorCode.PARAMS_ERROR);
		}
		return new BaseResult<>(redEnvelopeService.mySend(requestDTO.getUid(), requestDTO.getOffset(), requestDTO.getLimit()));
	}

	@RequestMapping(value = "/myReceive")
	public @ResponseBody BaseResult<MyRedEnvelopeResponseDTO> myReceive(@RequestBody MyRedEvnelopeRequestDTO requestDTO) {
		if (requestDTO == null || !NumberUtils.isValidId(requestDTO.getUid())) {
			throw new CodeException(ErrorCode.PARAMS_ERROR);
		}
		return new BaseResult<>(redEnvelopeService.myReceive(requestDTO.getUid(), requestDTO.getOffset(), requestDTO.getLimit()));
	}

	@RequestMapping(value = "/withdrawals")
	public @ResponseBody BaseResult<WithdrawalsResponseDTO> withdrawals(@RequestBody WithdrawalsRequestDTO requestDTO) {
		if (requestDTO == null || !NumberUtils.isValidId(requestDTO.getUid()) || requestDTO.getFee() == null) {
			throw new CodeException(ErrorCode.PARAMS_ERROR);
		}
		return new BaseResult<>(redEnvelopeService.withdrawals(requestDTO.getUid(), requestDTO.getFee()));
	}

	/**
	 * 获取分享红包二维码
	 */
	@RequestMapping(value = "/share/code/{param}")
	public void getShareCode(@PathVariable("param") String param, HttpServletResponse response) {
		if (StringUtils.isEmpty(param)) {
			throw new CodeException(ErrorCode.PARAMS_ERROR);
		}
		response.setHeader("content-type", "application/octet-stream");
		response.setContentType("application/octet-stream");
		OutputStream out = null;
		try {
			param = new String(Base64.decodeBase64(param));
			Integer hbid = Integer.valueOf(JSONUtil.getValue("hbid", param).toString());
			if (!NumberUtils.isValidId(hbid)) {
				throw new CodeException(ErrorCode.PARAMS_ERROR);
			}

			out = response.getOutputStream();
			out.write(wechatUserService.getWxacode(String.valueOf(hbid), "pages/hbxq/hbxq"));
			out.flush();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
