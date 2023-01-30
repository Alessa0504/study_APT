package com.example.apt.compiler.activity

import com.example.apt.compiler.activity.method.ConstantBuilder
import com.example.apt.compiler.activity.method.InjectMethodBuilder
import com.example.apt.compiler.activity.method.SaveStateMethodBuilder
import com.example.apt.compiler.activity.method.StartMethodBuilder
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
        val typeBuilder = TypeSpec.classBuilder(activityClass.simpleName + POSIX)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)  //生成xxActivityBuilder类，可访问且不能被继承

        ConstantBuilder(activityClass).build(typeBuilder)   //build生成常量
        StartMethodBuilder(activityClass).build(typeBuilder)     //build生成方法
        SaveStateMethodBuilder(activityClass).build(typeBuilder)
        InjectMethodBuilder(activityClass).build(typeBuilder)

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