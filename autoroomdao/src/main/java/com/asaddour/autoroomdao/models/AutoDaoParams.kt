package com.asaddour.autoroomdao.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import com.asaddour.autoroomdao.annotations.AutoDao
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import sun.rmi.runtime.Log
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeMirror
import kotlin.reflect.KClass

//
// The configuration of the dao to generate
//
internal data class AutoDaoParams(
        val tableName: String,
        val entityType: TypeName,
        val onInsertConflictStrategy: Int,
        val onUpdateConflictStrategy: Int,
        val defaultRxReturnType: TypeName,
        val generateOnlyDefaultRxReturnType: Boolean,
        val generateOrderBy: Boolean,
        val attributes: List<Attr>
) {
    companion object {
        fun from(autoDao: AutoDao, processingEnv: ProcessingEnvironment): AutoDaoParams {
            val entityClassMirror = try {
                // This will fail we don't care we want typeMirror
                // https://area-51.blog/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
                autoDao.entityClass as TypeMirror
            } catch (mte: MirroredTypeException) {
                mte.typeMirror
            }

            val defaultRxReturnType = try {
                // This will fail we don't care we want typeMirror
                // https://area-51.blog/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
                autoDao.defaultRxReturnType as TypeMirror
            } catch (mte: MirroredTypeException) {
                mte.typeMirror
            }

            val entityElement = processingEnv.typeUtils.asElement(entityClassMirror)
            val attributes = entityElement.enclosedElements
                    .filter { element -> keepNonStaticField(element) }
                    .flatMap { element ->
                        val isEmbedded = element.getAnnotation(Embedded::class.java) != null
                        when (isEmbedded) {
                            true -> {
                                processingEnv.elementUtils
                                        .getTypeElement(element.asType().toString())
                                        .enclosedElements
                                        .filter {
                                            keepNonStaticField(it)
                                        }
                            }
                            else -> listOf(element)
                        }
                    }
                    .map { element ->
                        val attributName = element.simpleName.toString()
                        val columnName = element.getAnnotation(ColumnInfo::class.java)?.name
                                ?: attributName
                        Attr(columnName, attributName, element.asType().asTypeName())
                    }

            val tableName = run {
                val annotation = entityElement.getAnnotation(Entity::class.java)
                annotation?.tableName
                        ?: throw IllegalStateException("cannot find tableName argument in annotation $annotation for class $entityClassMirror")
            }

            return AutoDaoParams(
                    tableName = tableName,
                    entityType = entityClassMirror.asTypeName(),
                    onInsertConflictStrategy = autoDao.onInsertConflictStrategy,
                    onUpdateConflictStrategy = autoDao.onUpdateConflictStrategy,
                    defaultRxReturnType = defaultRxReturnType.asTypeName(),
                    generateOnlyDefaultRxReturnType = autoDao.generateOnlyDefaultRxReturnType,
                    generateOrderBy = autoDao.generateOrderBy,
                    attributes = attributes
            )
        }

        private fun keepNonStaticField(element: Element) = when {
            element.kind == ElementKind.FIELD -> {
                val ignoreAnnotation = element.getAnnotation(Ignore::class.java)
                ignoreAnnotation == null && !element.modifiers.contains(Modifier.STATIC)
            }
            else -> false
        }

    }

    internal data class Attr(val collumnName: String, val name: String, val type: TypeName)
}

