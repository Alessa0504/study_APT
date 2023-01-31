@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")
package com.example.apt.compiler.activity.entity

import com.bennyhuo.aptutils.types.asJavaTypeName
import com.bennyhuo.aptutils.types.asKotlinTypeName
import com.sun.tools.javac.code.Symbol

/**
 * @Description: 标注的字段
 * @author zouji
 * @date 2023/1/29
 */
open class Field(private val symbol: Symbol.VarSymbol) : Comparable<Field> {  // symbol表示标注的是字段

    val name = symbol.qualifiedName.toString()

    open val prefix = "REQUIRED_"   //常量前缀

    val isPrivate = symbol.isPrivate  //是否私有

    val isPrimitive = symbol.type.isPrimitive   //是否基本类型

    fun asJavaTypeName() = symbol.type.asJavaTypeName()

    open fun asKotlinTypeName() = symbol.type.asKotlinTypeName()

    override fun compareTo(other: Field): Int {   //比较
        return name.compareTo(other.name)
    }

    override fun toString(): String {
        return "$name:${symbol.type}"
    }
}