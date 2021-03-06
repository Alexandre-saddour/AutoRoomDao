package com.asaddour.autoroomdao.annotations

import androidx.room.OnConflictStrategy
import io.reactivex.Single
import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class AutoDao(
        val entityClass: KClass<*>,
        val tableName: String = "",
        val onInsertConflictStrategy: Int = OnConflictStrategy.ABORT,
        val onUpdateConflictStrategy: Int = OnConflictStrategy.ABORT,
        val defaultReturnType: KClass<*> = Single::class,
        val generateBlockingQueries: Boolean = true,
        val generateRxQueries: Boolean = true, // ignored if generateOnlyDefaultReturnType is true
        val generateLiveDataQueries: Boolean = true, // ignored if generateOnlyDefaultReturnType is true
        val generateOnlyDefaultReturnType: Boolean = false,
        val generateOrderBy: Boolean = true

)