package com.asaddour.autoroomdao.models

import androidx.room.*
import com.asaddour.autoroomdao.annotations.AutoDao
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeMirror

//
// The configuration of the dao to generate
//
internal data class AutoDaoParams(
        val tableName: String,
        val entityType: TypeName,
        val containsRelationAnnotation: Boolean,
        val onInsertConflictStrategy: Int,
        val onUpdateConflictStrategy: Int,
        val defaultRxReturnType: TypeName,
        val generateBlockingQueries: Boolean,
        val generateRxQueries: Boolean,
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
            val containsRelationAnnotation = entityElement.enclosedElements.any { element -> hasRelationAnnotation(element) }
            val attributes = entityElement.enclosedElements
                    .filter { element -> keepNonStaticField(element) }
                    .flatMap { element ->
                       fun getElements(element: Element, prefix: String = ""): List<Attr> {
                           val embedded = element.getAnnotation(Embedded::class.java)
                           return when (embedded) {
                               null -> listOf(element.toAttr(prefix))
                               else -> {
                                   processingEnv.elementUtils
                                           .getTypeElement(element.asType().toString())
                                           .enclosedElements
                                           .filter {
                                               keepNonStaticField(it)
                                           }
                                           .flatMap { subElement -> getElements(subElement, prefix + embedded.prefix) }

                               }
                           }
                       }
                        getElements(element)


                    }

            val tableName = run {
                when {
                    autoDao.tableName.isEmpty() -> run {
                        val annotation = entityElement.getAnnotation(Entity::class.java)
                        annotation?.tableName ?: throw IllegalArgumentException("cannot find tableName argument in annotation $annotation for class $entityClassMirror")
                    }
                    else -> autoDao.tableName
                }
            }

            return AutoDaoParams(
                    tableName = tableName,
                    entityType = entityClassMirror.asTypeName(),
                    onInsertConflictStrategy = autoDao.onInsertConflictStrategy,
                    onUpdateConflictStrategy = autoDao.onUpdateConflictStrategy,
                    defaultRxReturnType = defaultRxReturnType.asTypeName(),
                    containsRelationAnnotation = containsRelationAnnotation,
                    generateBlockingQueries = autoDao.generateBlockingQueries,
                    generateRxQueries = autoDao.generateRxQueries,
                    generateOnlyDefaultRxReturnType = autoDao.generateOnlyDefaultRxReturnType,
                    generateOrderBy = autoDao.generateOrderBy,
                    attributes = attributes
            )
        }

        private fun keepNonStaticField(element: Element) = when {
            element.kind == ElementKind.FIELD -> {
                // we want to ignore
                //  - static fields
                //  - @Ignore fields
                //  - @Relation fields
                val ignoreAnnotation = element.getAnnotation(Ignore::class.java)
                val relationAnnotation = element.getAnnotation(Relation::class.java)
                ignoreAnnotation == null && relationAnnotation == null && !element.modifiers.contains(Modifier.STATIC)
            }
            else -> false
        }

        private fun hasRelationAnnotation(element: Element) = when {
            element.kind == ElementKind.FIELD -> element.getAnnotation(Relation::class.java) != null
            else -> false
        }



        private fun Element.toAttr(prefix: String? = null): Attr {
            val attributName = simpleName.toString()
            val columnName = getAnnotation(ColumnInfo::class.java)?.name
                    ?: attributName
            return Attr((prefix ?: "") + columnName, attributName, asType().asTypeName())
        }

    }

    internal data class Attr(val collumnName: String, val name: String, val type: TypeName)
}

