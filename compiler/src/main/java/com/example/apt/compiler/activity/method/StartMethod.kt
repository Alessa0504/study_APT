package com.example.apt.compiler.activity.method

import com.example.apt.compiler.activity.ActivityClass
import com.example.apt.compiler.activity.entity.Field
import com.example.apt.compiler.prebuilt.ACTIVITY_BUILDER
import com.example.apt.compiler.prebuilt.CONTEXT
import com.example.apt.compiler.prebuilt.INTENT
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

/**
 * @Description:
 * @author zouji
 * @date 2023/1/29
 */
class StartMethod(private val activityClass: ActivityClass, private val name: String) {
    private val fields = ArrayList<Field>()
    private var isStaticMethod = true  //是否静态方法

    fun staticMethod(staticMethod: Boolean): StartMethod {
        this.isStaticMethod = staticMethod
        return this
    }

    fun addAllFields(fields: List<Field>) {
        this.fields += fields   //运算符重载, +=是addAll
    }

    fun addFields(field: Field) {
        this.fields += field  //+-是add
    }

    fun copy(name: String) = StartMethod(activityClass, name).also {
        it.fields.addAll(fields)
    }

    /**
     * build后生成
     * @param typeBuilder
     */
    fun build(typeBuilder: TypeSpec.Builder) {
        val methodBuilder = MethodSpec.methodBuilder(name)   //生成方法
            .addModifiers(Modifier.PUBLIC)   //public方法
            .returns(TypeName.VOID)  //返回void
            .addParameter(CONTEXT.java, "context")   //参数
            .addStatement("\$T intent = new \$T(context, \$T.class)", INTENT.java, INTENT.java, activityClass.typeElement)   // $T会处理导包, kt中用\对$转义

        fields.forEach { field ->
            val name = field.name
            methodBuilder.addParameter(field.asJavaTypeName(), name)
                // $S会把name加""，$L直接是name的值，如 intent.putExtra("age", age)
                .addStatement("intent.putExtra(\$S, \$L)", name, name)
        }

        if (isStaticMethod) {
            methodBuilder.addModifiers(Modifier.STATIC)
        } else {
            methodBuilder.addStatement("fillIntent(intent)")
        }

        methodBuilder.addStatement("\$T.INSTANCE.startActivity(context, intent)", ACTIVITY_BUILDER.java)
        typeBuilder.addMethod(methodBuilder.build())
    }
}