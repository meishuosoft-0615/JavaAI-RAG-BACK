package com.meishuosoft.rag.auth.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 系统功能权限注解。
 *
 * <p>用于标记需要指定权限码才能访问的 Controller 方法。</p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {

    /**
     * 前端约定的权限码，例如 kb:manage、document:manage、audit:read。
     */
    String value();
}
