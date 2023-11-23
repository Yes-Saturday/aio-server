package com.zhaizq.aio.system.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhaizq.aio.system.mapper.SystemUserMapper;
import com.zhaizq.aio.system.mapper.entity.SystemUser;
import org.springframework.stereotype.Service;

@Service
public class SystemUserService extends ServiceImpl<SystemUserMapper, SystemUser> {
}
