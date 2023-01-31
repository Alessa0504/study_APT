package com.example.apt.compiler.activity.method

import com.example.apt.compiler.activity.ActivityClass
import com.example.apt.compiler.activity.ActivityClassBuilder
import com.example.apt.compiler.activity.entity.OptionalField
import com.example.apt.compiler.prebuilt.INTENT
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

/**
 * @Description: 生成java的start方法
 * @author zouji
 * @date 2023/1/29
 */
class StartMethodBuilder(private val activityClass: ActivityClass) {
    fun build(typeBuilder: TypeSpec.Builder) {
        val startMethod = StartMethod(activityClass, ActivityClassBuilder.METHOD_NAME)

        // 分组 -Optional & Required
        val groupedFields = activityClass.fields.groupBy { it is OptionalField }   // it is OptionalField返回的是true
        val requiredFields = groupedFields[false] ?: emptyList()
        val optionalFields = groupedFields[true] ?: emptyList()

        startMethod.addAllFields(requiredFields)
        val startMethodNoOptional = startMethod.copy(ActivityClassBuilder.METHOD_NAME_NO_OPTIONAL)   //copy一个没有optional的方法
        startMethod.addAllFields(optionalFields)
        startMethod.build(typeBuilder)   //生成包含所有fields的(Required+Optional)

        if (optionalFields.isNotEmpty()) {
            startMethodNoOptional.build(typeBuilder)   //生成没有Optional的(只有Required)
        }

        // field<3生成静态start方法；>=3生成非静态方法，在内部填充intent
        if (optionalFields.size < 3) {
            optionalFields.forEach { field ->
                startMethodNoOptional.copy(ActivityClassBuilder.METHOD_NAME_FOR_OPTIONAL + field.name.capitalize())
                    .also { it.addFields(field) }
                    .build(typeBuilder)
            }
        } else {
            val builderName = activityClass.simpleName + ActivityClassBuilder.POSIX
            val fillIntentMethodBuilder = MethodSpec.methodBuilder("fillIntent")
                .addModifiers(Modifier.PRIVATE)
                .addParameter(INTENT.java, "intent")
            val builderClassName = ClassName.get(activityClass.packageName, builderName)   //获取类名
            optionalFields.forEach { field ->
                // FieldSpec用来创建字段
                typeBuilder.addField(FieldSpec.builder(field.asJavaTypeName(), field.name, Modifier.PRIVATE).build())
                typeBuilder.addMethod(MethodSpec.methodBuilder(field.name)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(field.asJavaTypeName(), field.name)
                    .addStatement("this.${field.name} = ${field.name}")   //set方法
                    .addStatement("return this")
                    .returns(builderClassName)   //return类型
                    .build())

                if (field.isPrimitive) {   //基本类型
                    fillIntentMethodBuilder.addStatement("intent.putExtra(\$S, \$L)", field.name, field.name)
                } else {
                    fillIntentMethodBuilder.beginControlFlow("if(\$L != null)", field.name)
                        .addStatement("intent.putExtra(\$S, \$L)", field.name, field.name )
                        .endControlFlow()
                }
            }
            typeBuilder.addMethod(fillIntentMethodBuilder.build())  // 生成fillIntentMethodBuilder方法
            // 生成startWithOptionals方法，由于不是静态方法，内部会调用fillIntent方法
            startMethodNoOptional.copy(ActivityClassBuilder.METHOD_NAME_FOR_OPTIONALS)
                .staticMethod(false)
                .build(typeBuilder)
        }
    }
}