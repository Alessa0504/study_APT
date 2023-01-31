package com.example.apt.compiler.activity.method

import com.example.apt.compiler.activity.ActivityClass
import com.example.apt.compiler.activity.entity.OptionalField
import com.example.apt.compiler.prebuilt.ACTIVITY
import com.example.apt.compiler.prebuilt.BUNDLE
import com.example.apt.compiler.prebuilt.BUNDLE_UTILS
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

/**
 * @Description:
 * @author zouji
 * @date 2023/1/30
 */
class InjectMethodBuilder(private val activityClass: ActivityClass) {
    fun build(typeBuilder: TypeSpec.Builder) {
        val injectMethodBuilder = MethodSpec.methodBuilder("inject")
            .addParameter(ACTIVITY.java, "instance")
            .addParameter(BUNDLE.java, "savedInstanceState")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(TypeName.VOID)
            .beginControlFlow("if(instance instanceof \$T)", activityClass.typeElement)
            .addStatement("\$T typedInstance = (\$T)instance", activityClass.typeElement, activityClass.typeElement)  //强转
            .addStatement("\$T extras = savedInstanceState == null ? typedInstance.getIntent().getExtras() : savedInstanceState", BUNDLE.java)
            .beginControlFlow("if(extras != null)")

        activityClass.fields.forEach { field ->
            val name = field.name
            val typeName = field.asJavaTypeName().box()   //box(): 如int,long类型需要装箱
            val unboxedTypeName =
            if (typeName.isBoxedPrimitive) {  //是装箱类型
                typeName.unbox()   //拆箱: Integer -> int, Long -> long
            } else {
                typeName
            }
            if (field is OptionalField) {
                 injectMethodBuilder.addStatement("\$T \$LValue = \$T.<\$T>get(extras, \$S, (\$T)\$L)", typeName, name, BUNDLE_UTILS.java, typeName, name, unboxedTypeName, field.defaultValue)
            } else {
                injectMethodBuilder.addStatement("\$T \$LValue = \$T.<\$T>get(extras, \$S)", typeName, name, BUNDLE_UTILS.java, typeName, name )
            }

            if (field.isPrivate) {
                injectMethodBuilder.addStatement("typedInstance.set\$L(\$LValue)", name.capitalize(), name)
            } else {
                injectMethodBuilder.addStatement("typedInstance.\$L = \$LValue", name, name)
            }
        }
        injectMethodBuilder.endControlFlow() .endControlFlow()
        typeBuilder.addMethod(injectMethodBuilder.build())
    }
}