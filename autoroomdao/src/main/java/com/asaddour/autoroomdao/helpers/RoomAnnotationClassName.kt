package com.asaddour.autoroomdao.helpers

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName

//
// ClassName factory for Room annotations
//
internal object RoomAnnotationClassName {
    internal fun dao() = create("Dao")
    internal fun insert() = create("Insert")
    internal fun update() = create("Update")
    internal fun delete() = create("Delete")
    internal fun database() = create("Database")
    internal fun query(format: String, vararg args: Any) = AnnotationSpec
            .builder(create("Query"))
            .addMember(format, args)
            .build()

    internal fun typeConverters() = create("TypeConverters")
    private fun create(annotationName: String) = ClassName("androidx.room", annotationName)
}