package com.example.apt.compiler.activity

import com.bennyhuo.aptutils.types.packageName
import com.bennyhuo.aptutils.types.simpleName
import com.example.apt.compiler.activity.entity.Field
import java.util.*
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

/**
 * @Description: 标注的Activity
 * @author zouji
 * @date 2023/1/29
 */
class ActivityClass(private val typeElement: TypeElement) {   // Type用来标注类，标注的类 类型是TypeElement
    val simpleName = typeElement.simpleName()
    val packageName = typeElement.packageName()
    val fields = TreeSet<Field>()

    val isAbstract = typeElement.modifiers.contains(Modifier.ABSTRACT)  //修饰符是否包含ABSTRACT
    val isKotlin =
        typeElement.getAnnotation(Metadata::class.java) != null //是否是kt类 -根据kapt stub生成的java类有@kotlin.Metadata修饰

    companion object {
        // 反射获取
        val META_DATA =
            Class.forName("kotlin.Metadata") as Class<Annotation>  // META_DATA = Metadata::class.java
    }

    override fun toString(): String {
        return "$packageName.$simpleName[${fields.joinToString()}]"
    }
}