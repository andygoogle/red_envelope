package com.zzhl.controller;

import com.zzhl.config.RedEnvelopeConfig;
import com.zzhl.service.WechatUserService;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
@RequestMapping("file")
public class FileController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private WechatUserService wechatUserService;
    @Autowired
    private RedEnvelopeConfig redEnvelopeConfig;

    /**
     * 根据文件名获取文件
     */
    @RequestMapping(value = "/name/{param}")
    public void getWxacode(@PathVariable("param") String param, HttpServletResponse response) {
        if (StringUtils.isEmpty(param)) {
            throw new RuntimeException("参数错误");
        }
        response.setHeader("content-type", "application/octet-stream");
        response.setContentType("application/octet-stream");
        OutputStream out = null;
        try {
            param = new String(Base64.decodeBase64(param));
            out = response.getOutputStream();
            InputStream inputStream = new FileInputStream(new File(redEnvelopeConfig.getFilePath() + param));
            byte[] buffer = new byte[1024];
            int len = 0;
            while((len = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
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

    /**
     * 获取分享卡片的小程序码
     */
    @RequestMapping(value = "/wxacode/{numberId}")
    public void getWxacode(@PathVariable("numberId") Long numberId, HttpServletResponse response) {
        if (numberId == null || numberId <= 0) {
            throw new RuntimeException("参数错误");
        }
        downloadWxacode(response, String.valueOf(numberId), "pages/hbxq/hbxq");
    }

    private void downloadWxacode(HttpServletResponse response, String param, String page) {
        response.setHeader("content-type", "application/octet-stream");
        response.setContentType("application/octet-stream");
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            out.write(wechatUserService.getWxacode(param, page));
            out.flush();
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
