package com.example.apt.compiler

import com.bennyhuo.aptutils.AptContext
import com.bennyhuo.aptutils.logger.Logger
import com.example.apt.annotations.Builder
import com.example.apt.annotations.Optional
import com.example.apt.annotations.Required
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

/**
 * @Description:
 * @author zouji
 * @date 2023/1/28
 */
class BuilderProcessor : AbstractProcessor() {
    // 能处理哪些注解
    private val supportedAnnotations =
        setOf(Builder::class.java, Required::class.java, Optional::class.java)

    /**
     * 支持版本：jdk8
     */
    override fun getSupportedSourceVersion() = SourceVersion.RELEASE_8

    /**
     * 支持注解的类型
     */
    override fun getSupportedAnnotationTypes() = supportedAnnotations.map { it.canonicalName }
        .toSet()  //把class转为name(String类型)，最后转为Set<String>()

    /**
     * 初始化
     * @param processingEnv
     */
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        AptContext.init(processingEnv)
    }

    override fun process(annotations: MutableSet<out TypeElement>, env: RoundEnvironment): Boolean {
        // 所有用@Builder标注的集合返回
        env.getElementsAnnotatedWith(Builder::class.java).forEach {
            Logger.warn(it, "BuilderProcessor 自己打印的警告${it.simpleName}")
        }
        return true
    }
}