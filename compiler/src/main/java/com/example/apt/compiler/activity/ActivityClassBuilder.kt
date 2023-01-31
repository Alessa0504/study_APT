package com.example.apt.compiler.activity

import com.example.apt.compiler.activity.method.*
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import com.squareup.kotlinpoet.FileSpec
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier
import javax.tools.StandardLocation

/**
 * @Description: 生成文件
 * @author zouji
 * @date 2023/1/29
 */
class ActivityClassBuilder(private val activityClass: ActivityClass) {

    companion object {
        const val POSIX = "Builder"
        const val METHOD_NAME = "start"
        const val METHOD_NAME_NO_OPTIONAL = METHOD_NAME + "WithoutOptional"  //1个optional都没有的方法
        const val METHOD_NAME_FOR_OPTIONAL = METHOD_NAME + "WithOptional"
        const val METHOD_NAME_FOR_OPTIONALS = METHOD_NAME + "WithOptionals"
    }

    /**
     * 创建class文件
     * @param filer
     */
    fun build(filer: Filer) {  // Filer用于创建文件
        if (activityClass.isAbstract) return   //抽象类

        // java类
        val typeBuilder = TypeSpec.classBuilder(activityClass.simpleName + POSIX)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)  //生成xxActivityBuilder类，可访问且不能被继承

        ConstantBuilder(activityClass).build(typeBuilder)   //build生成常量
        StartMethodBuilder(activityClass).build(typeBuilder)     //build生成方法
        SaveStateMethodBuilder(activityClass).build(typeBuilder)
        InjectMethodBuilder(activityClass).build(typeBuilder)

        // kt类
        if (activityClass.isKotlin) {
            val fileBuilder = FileSpec.builder(activityClass.packageName, activityClass.simpleName + POSIX)
            StartKotlinFunBuilder(activityClass).build(fileBuilder)
            writeKotlinToFile(filer, fileBuilder.build())
        }

        writeJavaToFile(filer, typeBuilder.build())
    }

    /**
     * 生成java文件
     * @param filer
     * @param typeSpec
     */
    private fun writeJavaToFile(filer: Filer, typeSpec: TypeSpec) {
        try {
            val file = JavaFile.builder(activityClass.packageName, typeSpec).build()  //生成java代码
            file.writeTo(filer)   //写入文件
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 生成kotlin文件
     * @param filer
     * @param fileSpec
     */
    private fun writeKotlinToFile(filer: Filer, fileSpec: FileSpec) {
        try {
            // 创建空白文件: .kt文件用createResource，否则其他方法是生成.java文件
            val fileObject = filer.createResource(
                StandardLocation.SOURCE_OUTPUT,
                activityClass.packageName,
                fileSpec.name + ".kt"
            )
            // 写入文件
            fileObject.openWriter().also {
                fileSpec.writeTo(it)
            }.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}