package com.heima.reggie.common;

import org.yaml.snakeyaml.events.Event;

//基于ThreadLocal封装工具类，用户保存和获取登录用户ID
public class BaseContext {

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
