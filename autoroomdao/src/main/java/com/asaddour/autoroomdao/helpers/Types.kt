package com.asaddour.autoroomdao.helpers

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import javax.lang.model.element.Element
import kotlin.reflect.jvm.internal.impl.name.FqName
import kotlin.reflect.jvm.internal.impl.platform.JavaToKotlinClassMap

fun Element.javaToKotlinType(): TypeName =
        asType().asTypeName().javaToKotlinType()

fun TypeName.javaToKotlinType(): TypeName {
    val className =
            JavaToKotlinClassMap.INSTANCE.mapJavaToKotlin(FqName(toString()))
                    ?.asSingleFqName()?.asString()

    return if (className == null) {
        this
    } else {
        ClassName.bestGuess(className)
    }
}



internal val stringType = ClassName("kotlin", "String")
internal val orderType = ClassName("com.parokit.autoroom", "Order")
internal val listType = ClassName("kotlin.collections", "List")
internal val singleType = ClassName("io.reactivex", "Single")
internal val maybeType = ClassName("io.reactivex", "Maybe")
internal val flowableType = ClassName("io.reactivex", "Flowable")

// Soon to be removed: kotlinpoet will release it.
internal fun ClassName.parameterizedBy(vararg typeArguments: TypeName) =
        ParameterizedTypeName.get(this, *typeArguments)