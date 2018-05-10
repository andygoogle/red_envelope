package com.zzhl.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzhl.annotation.RequestLockable;
import com.zzhl.config.Constant;
import com.zzhl.config.RedEnvelopeConfig;
import com.zzhl.domain.mapper.UserExtraMapper;
import com.zzhl.domain.mapper.WechatUserMapper;
import com.zzhl.domain.pojo.UserExtra;
import com.zzhl.domain.pojo.WechatUser;
import com.zzhl.dto.requst.LoginRequestDTO;
import com.zzhl.exception.CodeException;
import com.zzhl.exception.ErrorCode;
import com.zzhl.util.AESUtils;
import com.zzhl.util.DateUtil;
import com.zzhl.util.NumberUtils;
import com.zzhl.wechat.WechatApi;
import com.zzhl.wechat.dto.WechatGetSessionKey;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;

@Service
public class WechatUserService {
    private static final Logger logger = LoggerFactory.getLogger(WechatUserService.class);

    @Autowired
    private WechatUserMapper wechatUserMapper;
    @Autowired
    private UserExtraMapper userExtraMapper;

    @Autowired
    private WechatApi wechatApi;
    @Autowired
    private RedisService redisService;
    @Autowired
    private RedEnvelopeConfig redEnvelopeConfig;

    public WechatUser login(LoginRequestDTO request) {
        if (request == null || StringUtils.isEmpty(request.getLoginCode()) || StringUtils.isEmpty(request.getSignature()) || StringUtils.isEmpty(request.getEncryptedData())) {
            throw new CodeException(ErrorCode.PARAMS_ERROR);
        }

        WechatUser wechatUser = getWechatUserInfo(request.getLoginCode(), request.getSignature(), request.getRawData(), request.getEncryptedData(), request.getIv());
        if (wechatUser == null) {
            logger.error("获取微信用户信息失败");
        }

        saveWechatUser(wechatUser);

        return wechatUser;
    }

    public WechatUser getUser(Integer uid) {
        return wechatUserMapper.selectByPrimaryKey(uid);
    }

    public UserExtra getUserExtra(Integer uid) {
        return userExtraMapper.selectByPrimaryKey(uid);
    }

    public Integer updateUserExtra(UserExtra userExtra) {
        return userExtraMapper.updateByPrimaryKeySelective(userExtra);
    }

    /**
     * 更新用户余额
     *
     * @param uid
     * @param amount
     * @return 返回剩余金额
     */
    @RequestLockable(key = "uid")
    public Integer updateUserBalance(Integer uid, Integer amount) {
        UserExtra userExtra = getUserExtra(uid);
        if (userExtra == null){
            throw new CodeException(ErrorCode.PARAMS_ERROR.getState(), "用户不存在");
        }
        int reaminBalance = userExtra.getBalance() + amount;
        if (reaminBalance < 0) {
            throw new CodeException(ErrorCode.ERROR.getState(), "余额不足");
        }
        userExtraMapper.updateBalance(uid, amount, System.currentTimeMillis());
        return reaminBalance;
    }

    @Transactional
    public void saveWechatUser(WechatUser wechatUser) {
        if (NumberUtils.isValidId(wechatUser.getId())) {
            wechatUser.setLoginCount(wechatUser.getLoginCount() + 1);
            wechatUser.setLastLoginTime(System.currentTimeMillis());
            wechatUser.setModifyTime(System.currentTimeMillis());
            wechatUserMapper.updateByPrimaryKeySelective(wechatUser);
        } else {
            wechatUser.setLoginCount(1);
            wechatUser.setLastLoginTime(System.currentTimeMillis());
            wechatUser.setCreateTime(System.currentTimeMillis());
            wechatUser.setModifyTime(System.currentTimeMillis());
            wechatUserMapper.insertSelective(wechatUser);

            // 保存用户扩展信息
            UserExtra userExtra = new UserExtra();
            userExtra.setUid(wechatUser.getId());
            userExtra.setBalance(0);
            userExtra.setCreateTime(wechatUser.getCreateTime());
            userExtra.setModifyTime(wechatUser.getModifyTime());
            userExtraMapper.insertSelective(userExtra);
        }
    }

    /**
     * 根据微信登录生成的code，获取session_key,并根据session_key解密用户数据
     */
    private WechatUser getWechatUserInfo(String loginCode, String signature, String rawData, String encryptedData, String iv) {
        WechatUser wechatUser = null;
        WechatGetSessionKey wechatGetSessionKey = wechatApi.jscode2session(loginCode);
        if (wechatGetSessionKey == null || StringUtils.isEmpty(wechatGetSessionKey.getSession_key())) {
            logger.error("获取微信session_key失败 response = {}", wechatGetSessionKey == null ? "wechatGetSessionKey is null" : wechatGetSessionKey.toString());
            throw new CodeException(ErrorCode.THIRD_RETURN_ERROR.getState(), "获取微信session_key失败");
        }

        if(!signature.equals(AESUtils.getSha1(rawData + wechatGetSessionKey.getSession_key()))){
            throw new CodeException(ErrorCode.THIRD_RETURN_ERROR.getState(), "签名认证失败");
        }

        byte[] resultByte  = null;
        try {
            resultByte = AESUtils.decrypt(Base64.decodeBase64(encryptedData),
                    Base64.decodeBase64(wechatGetSessionKey.getSession_key()),
                    Base64.decodeBase64(iv));
            if(null != resultByte && resultByte.length > 0) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(resultByte);
                String openId = node.get("openId") == null ? null : node.get("openId").toString().replaceAll("\"", "");
                if (!StringUtils.isEmpty(openId)) {
                    wechatUser = wechatUserMapper.selectByOpenId(openId);
                }
                if (wechatUser == null) {
                    wechatUser = new WechatUser();
                }
                wechatUser.setOpenId(openId);
                wechatUser.setUnionId(node.get("unionId") == null ? null : node.get("unionId").toString().replaceAll("\"", ""));
                wechatUser.setNickname(node.get("nickName") == null ? null : node.get("nickName").toString().replaceAll("\"", ""));
                wechatUser.setSex(node.get("gender") == null ? null : node.get("gender").toString().replaceAll("\"", ""));
                wechatUser.setCity(node.get("city") == null ? null : node.get("city").toString().replaceAll("\"", ""));
                wechatUser.setProvince(node.get("province") == null ? null : node.get("province").toString().replaceAll("\"", ""));
                wechatUser.setCountry(node.get("country") == null ? null : node.get("country").toString().replaceAll("\"", ""));
                wechatUser.setAvatarUrl(node.get("avatarUrl") == null ? null : node.get("avatarUrl").toString().replaceAll("\"", ""));
                wechatUser.setType(Constant.USER_TYPE_WECHAT);
            } else {
                // 解密失败
                logger.error("微信用户数据解密失败 rawData = {}", rawData);
            }
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return wechatUser;
    }

    public byte[] getWxacode(String param, String page) {
        String accessToken = wechatApi.getAccessToken();
        if (StringUtils.isEmpty(accessToken)) {
            throw new CodeException(ErrorCode.THIRD_RETURN_ERROR.getState(), "获取微信access_token失败");
        }
        return wechatApi.getWxacodeUnlimit(accessToken, page, param);
    }

    /**
     * 用户日志，uv记录
     *
     * @param uid
     */
    public void logUV(Integer uid) {
        WechatUser wechatUser = wechatUserMapper.selectByPrimaryKey(uid);
        if (wechatUser != null) {
            WechatUser updateUser = new WechatUser();
            updateUser.setId(wechatUser.getId());
            updateUser.setLoginCount(wechatUser.getLoginCount() + 1);
            updateUser.setLastLoginTime(System.currentTimeMillis());
            wechatUserMapper.updateByPrimaryKeySelective(updateUser);
        }

        // 增加UV
        saveUV();
    }

    /**
     * 获取当日访问uv
     * @return
     */
    public Long getUV() {
        String key = "UV-" + DateUtil.getNow("yyyyMMdd");
        Object uv = redisService.get(key);
        if (uv == null) {
            return 0L;
        }
        if (uv instanceof Integer) {
            return ((Integer) uv).longValue();
        }
        if (uv instanceof Long) {
            return ((Long) uv).longValue();
        }
        return 0L;
    }

    /**
     * 保存当日UV
     * @return
     */
    public Long saveUV() {
        String key = "UV-" + DateUtil.getNow("yyyyMMdd");
        return redisService.incr(key, 1L);
    }

}
