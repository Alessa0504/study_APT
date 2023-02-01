package com.example.apt.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zouji
 * @Description: 必要字段注解@Required
 * @date 2023/1/28
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface Required  {

}
