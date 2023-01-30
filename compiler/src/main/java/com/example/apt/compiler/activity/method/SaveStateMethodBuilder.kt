package com.example.apt.compiler.activity.method

import com.example.apt.compiler.activity.ActivityClass
import com.example.apt.compiler.prebuilt.ACTIVITY
import com.example.apt.compiler.prebuilt.BUNDLE
import com.example.apt.compiler.prebuilt.INTENT
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

/**
 * @Description:
 * @author zouji
 * @date 2023/1/30
 */
class SaveStateMethodBuilder(private val activityClass: ActivityClass) {
    fun build(typeBuilder: TypeSpec.Builder) {
        val methodBuilder = MethodSpec.methodBuilder("saveState")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(TypeName.VOID)
            .addParameter(ACTIVITY.java, "instance")
            .addParameter(BUNDLE.java, "outState")
            .beginControlFlow("if(instance instanceof \$T)", activityClass.typeElement)
            .addStatement("\$T typedInstance = (\$T)instance", activityClass.typeElement, activityClass.typeElement)
            .addStatement("\$T intent = new \$T()", INTENT.java, INTENT.java)
        activityClass.fields.forEach { field ->
            val name = field.name
            if (field.isPrivate) {
                methodBuilder.addStatement("intent.putExtra(\$S, typedInstance.get\$L())", name, name.capitalize())
            } else {
                methodBuilder.addStatement("intent.putExtra(\$S, typedInstance.\$L)", name, name)
            }
        }
        methodBuilder.addStatement("outState.putAll(intent.getExtras())").endControlFlow()
        typeBuilder.addMethod(methodBuilder.build())
    }
}