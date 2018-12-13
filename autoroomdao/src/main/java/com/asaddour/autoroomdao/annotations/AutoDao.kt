package com.asaddour.autoroomdao.annotations

import androidx.room.OnConflictStrategy
import io.reactivex.Single
import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class AutoDao(
        val entityClass: KClass<*>,
        val onInsertConflictStrategy: Int = OnConflictStrategy.ABORT,
        val onUpdateConflictStrategy: Int = OnConflictStrategy.ABORT,
        val defaultRxReturnType: KClass<*> = Single::class,
        val generateOnlyDefaultRxReturnType: Boolean = false,
        val generateOrderBy: Boolean = true

)