package sion.my.netmanger2.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import sion.my.netmanger2.enums.NetMode;

/**
 * 作者：梁浩文 , 日期： 2019/11/13.
 * 让代码更加美观
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NetType {
    NetMode nettype() default NetMode.NONE;
}
