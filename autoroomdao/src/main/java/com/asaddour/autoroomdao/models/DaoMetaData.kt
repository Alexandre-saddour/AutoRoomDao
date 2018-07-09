package com.asaddour.autoroomdao.models

import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement

//
// Meta data of the dao annotated with @AutoDao
//
internal data class DaoMetaData(val className: String,
                                val packageName: String
) {
    companion object {
        fun from(element: TypeElement, processingEnv: ProcessingEnvironment): DaoMetaData {
            return DaoMetaData(
                    element.simpleName.toString(),
                    processingEnv.elementUtils.getPackageOf(element).toString()
            )
        }
    }
}