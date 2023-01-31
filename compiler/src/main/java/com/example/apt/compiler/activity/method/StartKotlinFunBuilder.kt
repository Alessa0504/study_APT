package com.example.apt.compiler.activity.method

import com.example.apt.compiler.activity.ActivityClass
import com.example.apt.compiler.activity.ActivityClassBuilder
import com.example.apt.compiler.activity.entity.OptionalField
import com.example.apt.compiler.prebuilt.ACTIVITY_BUILDER
import com.example.apt.compiler.prebuilt.CONTEXT
import com.example.apt.compiler.prebuilt.INTENT
import com.squareup.kotlinpoet.*

/**
 * @Description: 生成kt的start方法
 * @author zouji
 * @date 2023/1/31
 */
class StartKotlinFunBuilder(private val activityClass: ActivityClass) {

    fun build(fileBuilder: FileSpec.Builder) {   //这里要生成的是kt扩展方法，没有类所以没有type，不用TypeSpec
        val name = ActivityClassBuilder.METHOD_NAME + activityClass.simpleName  //startxxActivity
        val functionBuilder = FunSpec.builder(name)  //kotlinpoet中生成方法
            .receiver(CONTEXT.kotlin)   //receiver表示调用方
            .addModifiers(KModifier.PUBLIC)
            .returns(UNIT)
            .addStatement("val intent = %T(this, %T::class.java)", INTENT.kotlin, activityClass.typeElement)   //java中占位符是$，kt中是%

        activityClass.fields.forEach { field ->
            val name = field.name
            val className = field.asKotlinTypeName()
            if (field is OptionalField) {
                functionBuilder.addParameter(ParameterSpec.builder(name, className).defaultValue("null").build())  //有optional，默认参数null
            } else {
                functionBuilder.addParameter(name, className)  //没有optional，没有默认参数
            }
            functionBuilder.addStatement("intent.putExtra(%S, %L)", name, name)
        }
        functionBuilder.addStatement("%T.INSTANCE.startActivity(this, intent)", ACTIVITY_BUILDER.kotlin)
        fileBuilder.addFunction(functionBuilder.build())
    }
}