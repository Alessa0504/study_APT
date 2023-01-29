package com.example.apt.compiler.activity.method

import com.example.apt.compiler.activity.ActivityClass
import com.example.apt.compiler.activity.utils.camelToUnderline
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

/**
 * @Description: 生成字段field常量类
 * @author zouji
 * @date 2023/1/29
 */
class ConstantBuilder(private val activityClass: ActivityClass) {
    fun build(typeBuilder: TypeSpec.Builder) {
        activityClass.fields.forEach { field ->
            typeBuilder.addField(
                // 创建字段: public static final REQUIRED_NAME = "name"   public static final REQUIRED_AGE = "age"
                FieldSpec.builder(
                    String::class.java,
                    field.prefix + field.name.camelToUnderline().uppercase(),
                    Modifier.PUBLIC,
                    Modifier.STATIC,
                    Modifier.FINAL
                ).initializer("\$S", field.name).build()  //"\$S"表示String
            )
        }
    }
}