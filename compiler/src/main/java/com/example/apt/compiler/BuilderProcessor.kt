@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")
package com.example.apt.compiler

import com.bennyhuo.aptutils.AptContext
import com.bennyhuo.aptutils.logger.Logger
import com.bennyhuo.aptutils.types.isSubTypeOf
import com.example.apt.annotations.Builder
import com.example.apt.annotations.Optional
import com.example.apt.annotations.Required
import com.example.apt.compiler.activity.ActivityClass
import com.example.apt.compiler.activity.entity.Field
import com.example.apt.compiler.activity.entity.OptionalField
import com.sun.tools.javac.code.Symbol.VarSymbol
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

/**
 * @Description: 注解处理器
 * @author zouji
 * @date 2023/1/28
 */
@SupportedAnnotationTypes("com.example.apt.annotations.Builder", "com.example.apt.annotations.Required", "com.example.apt.annotations.Optional")  //也可以通过这种方式替代方法getSupportedAnnotationTypes()
class BuilderProcessor : AbstractProcessor() {
    // 能处理哪些注解
    private val supportedAnnotations =
        setOf(Builder::class.java, Required::class.java, Optional::class.java)

    /**
     * 声明注解处理器支持的java版本，如jdk8，jdk11等
     */
    override fun getSupportedSourceVersion() = processingEnv.sourceVersion   //processingEnv.sourceVersion:获取最新的java版本

    /**
     * 声明注解处理器要识别的注解有哪些
     */
//    override fun getSupportedAnnotationTypes() = supportedAnnotations.map { it.canonicalName }
//        .toSet()  //把class转为name(String类型)，最后转为Set<String>()

    /**
     * 初始化
     * @param processingEnv
     */
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        AptContext.init(processingEnv)
    }

    // 注: 有几个模块依赖了注解处理器 kapt project(':compiler')，就会执行几次，所以每次process的执行都是处理当前模块下的注解
    override fun process(annotations: MutableSet<out TypeElement>, env: RoundEnvironment): Boolean {
        // 定义map，把注解标注的类和字段都存入
        val activityClasses = HashMap<Element, ActivityClass>()
        // 处理类 -所有用@Builder标注的集合返回
        env.getElementsAnnotatedWith(Builder::class.java).filter {
            it.kind.isClass     //过滤注解是标注在class上的
        }.forEach { element ->
            try {
                if (element.asType().isSubTypeOf("android.app.Activity")) {  //如果是Activity的子类
                    activityClasses[element] = ActivityClass(element as TypeElement)
                } else {
                    Logger.error(element, "Unsupported typeElement: ${element.simpleName}")
                }
            } catch (e: Exception) {
                Logger.logParsingError(element, Builder::class.java, e)
            }
        }
        // 处理属性 -@Required
        env.getElementsAnnotatedWith(Required::class.java).filter { it.kind.isField }
            .forEach { element ->
                // @Required标注的element是Activity中的属性，所以element.enclosingElement是Activity的注解(用@Builder标注的，没有标注就为null)
                activityClasses[element.enclosingElement]?.fields?.add(Field(element as VarSymbol))
                    ?: Logger.error(
                        element,
                        "Field $element annotated as Required while ${element.enclosingElement} not annotated."
                    )  //为null找不到

            }

        // 处理属性 -@Optional
        env.getElementsAnnotatedWith(Optional::class.java).filter { it.kind.isField }
            .forEach { element ->
                // @Required标注的element是Activity中的属性，所以element.enclosingElement是Activity的注解(用@Builder标注的，没有标注就为null)
                activityClasses[element.enclosingElement]?.fields?.add(OptionalField(element as VarSymbol))
                    ?: Logger.error(
                        element,
                        "Field $element annotated as Optional while ${element.enclosingElement} not annotated."
                    )  //为null找不到

            }

        activityClasses.values.forEach {
            it.builder.build(AptContext.filer)  //生成xxActivityBuilder.java
        }
        return true
    }
}