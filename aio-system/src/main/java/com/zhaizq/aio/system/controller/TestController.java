package com.zhaizq.aio.system.controller;

import com.zhaizq.aio.common.CacheMap;
import com.zhaizq.aio.common.annotation.JsonParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController extends BaseController {

    @RequestMapping("/test/cache/create")
    public Object cacheCreate(@JsonParam int size) {
        for (int i = 0; i < size; i++) {
            CacheMap<String, Object> cacheMap = new CacheMap<>();
            cacheMap.put("", "", 10000000000L);
        }

        return success();
    }

    @RequestMapping("/test/cache/gc")
    public Object cacheGC() {
        System.gc();
        return success();
    }
}
