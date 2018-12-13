package com.asaddour.autoroomdao.generators

import androidx.room.OnConflictStrategy
import com.asaddour.autoroomdao.helpers.RoomAnnotationClassName
import com.asaddour.autoroomdao.helpers.listType
import com.asaddour.autoroomdao.helpers.parameterizedBy
import com.asaddour.autoroomdao.helpers.singleType
import com.asaddour.autoroomdao.models.AutoDaoParams
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.asClassName

////////////////////////////////////////////////////////////////////////////////////////////////////
// Native room annotations (Blocking)
////////////////////////////////////////////////////////////////////////////////////////////////////

private fun generateOnConflictStrategyAsString(onInsertConflictStrategy: Int) =
        when (onInsertConflictStrategy) {
            OnConflictStrategy.REPLACE -> "OnConflictStrategy.REPLACE"
            OnConflictStrategy.ROLLBACK -> "OnConflictStrategy.ROLLBACK"
            OnConflictStrategy.ABORT -> "OnConflictStrategy.ABORT"
            OnConflictStrategy.FAIL -> "OnConflictStrategy.FAIL"
            OnConflictStrategy.IGNORE -> "OnConflictStrategy.IGNORE"
            else -> "OnConflictStrategy.ABORT"
        }

internal fun insertObjBlocking(params: AutoDaoParams) = FunSpec
        .builder("addBlocking")
        .addAnnotation(AnnotationSpec
                .builder(RoomAnnotationClassName.insert())
                .addMember("onConflict = ${generateOnConflictStrategyAsString(params.onInsertConflictStrategy)}")
                .build()
        )
        .addParameter("obj", params.entityType)
        .addModifiers(KModifier.ABSTRACT)
        .returns(Long::class)
        .build()

internal fun insertObjsBlocking(params: AutoDaoParams) = FunSpec
        .builder("addBlocking")
        .addAnnotation(AnnotationSpec
                .builder(RoomAnnotationClassName.insert())
                .addMember("onConflict = ${generateOnConflictStrategyAsString(params.onInsertConflictStrategy)}")
                .build()
        )
        .addParameter("obj", params.entityType, KModifier.VARARG)
        .addModifiers(KModifier.ABSTRACT)
        .returns(listType.parameterizedBy(Long::class.asClassName()))
        .build()

internal fun updateObjBlocking(params: AutoDaoParams) = FunSpec
        .builder("updateBlocking")
        .addAnnotation(AnnotationSpec
                .builder(RoomAnnotationClassName.update())
                .addMember("onConflict = ${generateOnConflictStrategyAsString(params.onUpdateConflictStrategy)}")
                .build()
        )
        .addParameter("obj", params.entityType)
        .addModifiers(KModifier.ABSTRACT)
        .returns(Int::class)
        .build()

internal fun deleteObjBlocking(params: AutoDaoParams) = FunSpec
        .builder("deleteBlocking")
        .addAnnotation(RoomAnnotationClassName.delete())
        .addParameter("obj", params.entityType)
        .addModifiers(KModifier.ABSTRACT)
        .returns(Int::class)
        .build()

internal fun deleteObjsBlocking(params: AutoDaoParams) = FunSpec
        .builder("deleteBlocking")
        .addAnnotation(RoomAnnotationClassName.delete())
        .addParameter("obj", params.entityType, KModifier.VARARG)
        .addModifiers(KModifier.ABSTRACT)
        .returns(Int::class)
        .build()

internal fun deleteAllBlocking(params: AutoDaoParams) = FunSpec
        .builder("deleteAllBlocking")
        .addAnnotation(RoomAnnotationClassName.query(
                "\"DELETE FROM ${params.tableName}\""
        ))
        .addModifiers(KModifier.ABSTRACT)
        .returns(Int::class)
        .build()


////////////////////////////////////////////////////////////////////////////////////////////////////
// Native room annotations (Rx)
////////////////////////////////////////////////////////////////////////////////////////////////////

internal fun insertObjRx(params: AutoDaoParams) = FunSpec
        .builder("add")
        .addParameter("obj", params.entityType)
        .addParameter(autoThreadParam)
        .concatIoThreadStatementWith("return Single.fromCallable({ addBlocking(obj) })")
        .returns(singleType.parameterizedBy(Long::class.asClassName()))
        .build()


internal fun insertObjsRx(params: AutoDaoParams) = FunSpec
        .builder("add")
        .addParameter("obj", params.entityType, KModifier.VARARG)
        .addParameter(autoThreadParam)
        .concatIoThreadStatementWith("return Single.fromCallable({ addBlocking(*obj)})")
        .returns(singleType.parameterizedBy(listType.parameterizedBy(Long::class.asClassName())))
        .build()

internal fun updateObjRx(params: AutoDaoParams) = FunSpec
        .builder("update")
        .addParameter("obj", params.entityType)
        .addParameter(autoThreadParam)
        .concatIoThreadStatementWith("return Single.fromCallable({ updateBlocking(obj) })")
        .returns(singleType.parameterizedBy(Int::class.asClassName()))
        .build()

internal fun deleteObjRx(params: AutoDaoParams) = FunSpec
        .builder("delete")
        .addParameter("obj", params.entityType)
        .addParameter(autoThreadParam)
        .concatIoThreadStatementWith("return Single.fromCallable({ deleteBlocking(obj) })")
        .returns(singleType.parameterizedBy(Int::class.asClassName()))
        .build()

internal fun deleteObjsRx(params: AutoDaoParams) = FunSpec
        .builder("delete")
        .addParameter("obj", params.entityType, KModifier.VARARG)
        .addParameter(autoThreadParam)
        .concatIoThreadStatementWith("return Single.fromCallable({ deleteBlocking(*obj) })")
        .returns(singleType.parameterizedBy(Int::class.asClassName()))
        .build()

internal fun deleteAllRx() = FunSpec
        .builder("deleteAll")
        .addParameter(autoThreadParam)
        .concatIoThreadStatementWith("return Single.fromCallable({ deleteAllBlocking() })")
        .returns(singleType.parameterizedBy(Int::class.asClassName()))
        .build()