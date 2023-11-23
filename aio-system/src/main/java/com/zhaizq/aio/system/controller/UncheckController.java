package com.zhaizq.aio.system.controller;

import com.zhaizq.aio.common.BusinessException;
import com.zhaizq.aio.common.CacheMap;
import com.zhaizq.aio.common.annotation.JsonParam;
import com.zhaizq.aio.common.annotation.Uncheck;
import com.zhaizq.aio.common.utils.DigestUtil;
import com.zhaizq.aio.common.utils.RsaUtil;
import com.zhaizq.aio.common.utils.StringUtil;
import com.zhaizq.aio.system.mapper.entity.SystemUser;
import com.zhaizq.aio.system.service.SystemUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Uncheck
@RestController
@RequestMapping("/system/uncheck")
public class UncheckController extends BaseController {
    @Autowired
    private SystemUserService systemUserService;

    @RequestMapping("/publicKey")
    public Result<String> publicKey(@JsonParam String username) {
        RsaUtil.Keys<String> keys = RsaUtil.genKeyPairString();
        CacheMap.DEFAULT.put("SYSTEM_LOGIN:KEY-" + username, keys, Duration.ofSeconds(30).toMillis());
        return success(keys.getPublicKey());
    }

    @RequestMapping("/login")
    public Result<?> login(@JsonParam String username, @JsonParam String password) {
        Integer times = (Integer) CacheMap.DEFAULT.getOrDefault("SYSTEM_LOGIN:LOCK-" + username, 1);
        if (++times > 5) throw new BusinessException("账户已被锁定10分钟");
        CacheMap.DEFAULT.put("SYSTEM_LOGIN:LOCK-" + username, times, Duration.ofMinutes(10).toMillis());

        RsaUtil.Keys<String> keys = (RsaUtil.Keys<String>) CacheMap.DEFAULT.get("SYSTEM_LOGIN:KEY-" + username);
        if (keys == null) throw new BusinessException("系统错误: 密钥过期");

        SystemUser user = systemUserService.lambdaQuery().eq(SystemUser::getUsername, username).one();
        if (user == null) throw new BusinessException("用户名或密码错误");

        password = RsaUtil.decryptByPrivateKey(password, keys.getPrivateKey());
        password = DigestUtil.sha256AsHex(password + user.getSalt());
        if (!StringUtil.equals(user.getPassword(), password))
            throw new BusinessException("用户名或密码错误");

        CacheMap.DEFAULT.remove("SYSTEM_LOGIN:LOCK-" + username);
        String token = StringUtil.uuid(); // TODO login
        String secret = StringUtil.uuid();

        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        data.put("secret", secret);
        return success(data);
    }

    @RequestMapping("/logout")
    public Object logout() {
        return success();
    }
}