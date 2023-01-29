package com.example.apt.compiler.activity

import com.example.apt.compiler.activity.method.ConstantBuilder
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * @Description: 生成文件
 * @author zouji
 * @date 2023/1/29
 */
class ActivityClassBuilder(private val activityClass: ActivityClass) {

    companion object {
        const val POSIX = "Builder"
    }

    /**
     * 创建class文件
     * @param filer
     */
    fun build(filer: Filer) {  // Filer用于创建文件
        if (activityClass.isAbstract) return   //抽象类
        val typeBuilder = TypeSpec.classBuilder(activityClass.simpleName + POSIX)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)  //生成xxActivityBuilder类，可访问且不能被继承

        ConstantBuilder(activityClass).build(typeBuilder)

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
}