@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")
package com.example.apt.compiler.activity.entity

import com.bennyhuo.aptutils.types.asTypeMirror
import com.example.apt.annotations.Optional
import com.squareup.kotlinpoet.TypeName
import com.sun.tools.javac.code.Symbol
import javax.lang.model.type.TypeKind

/**
 * @Description: 标注的Optional字段
 * @author zouji
 * @date 2023/1/29
 */
class OptionalField(symbol: Symbol.VarSymbol) : Field(symbol) {
    var defaultValue: Any? = null
        private set   // defaultValue不能从外部设值，从注解来

    override val prefix = "OPTIONAL_"

    init {
        val optional = symbol.getAnnotation(Optional::class.java)
        defaultValue = when (symbol.type.kind) {   // 根据标注的属性是什么类型设置注解默认值
            TypeKind.BOOLEAN ->  optional.booleanValue
            TypeKind.CHAR -> "'${optional.charValue}'"
            TypeKind.BYTE -> "(byte) ${optional.byteValue}"   //在这里强转
            TypeKind.SHORT -> "(short) ${optional.shortValue}"
            TypeKind.INT -> optional.intValue
            TypeKind.LONG -> "${optional.longValue}L"
            TypeKind.FLOAT -> "${optional.floatValue}f"
            TypeKind.DOUBLE -> optional.doubleValue
            else -> if (symbol.type == String::class.java.asTypeMirror()) {
                """"${optional.stringValue}""""  //注意字面量的引号
            } else {
                null
            }
        }
    }

    override fun asKotlinTypeName(): TypeName {
        return super.asKotlinTypeName().asNullable()  //由于父类调用的asKotlinTypeName() 没有null类型对应，这里覆写转为可空?类型
    }

    override fun compareTo(other: Field): Int {
        return if (other is OptionalField) {
            super.compareTo(other)   // 同为OptionalField就比较
        } else {
            1   // 对方是RequiredField就Required排前面
        }
    }
}