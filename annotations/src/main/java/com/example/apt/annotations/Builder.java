package com.example.apt.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zouji
 * @Description:
 * @date 2023/1/28
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Builder {

}
