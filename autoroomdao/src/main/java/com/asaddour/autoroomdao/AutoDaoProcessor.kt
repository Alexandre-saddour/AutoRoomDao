package com.asaddour.autoroomdao

import com.asaddour.autoroomdao.AutoDaoProcessor.Companion.KAPT_KOTLIN_GENERATED_OPTION_NAME
import com.asaddour.autoroomdao.annotations.AutoDao
import com.asaddour.autoroomdao.generators.daoFunctions
import com.asaddour.autoroomdao.helpers.RoomAnnotationClassName
import com.asaddour.autoroomdao.models.AutoDaoParams
import com.asaddour.autoroomdao.models.DaoMetaData
import com.asaddour.autoroomdao.models.ProcessorMetaData
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedOptions
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic


@AutoService(Processor::class)
@SupportedOptions(KAPT_KOTLIN_GENERATED_OPTION_NAME)
class AutoDaoProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(AutoDao::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    override fun process(set: MutableSet<out TypeElement>, roundEnvironment: RoundEnvironment): Boolean {

        generateOrderEnum()

        roundEnvironment.getElementsAnnotatedWith(AutoDao::class.java)
                .forEach { element ->
                    if (element.kind != ElementKind.CLASS) {
                        throw IllegalArgumentException("AutoRoom: AutoDao annotation can only be applied to classes")
                    }

                    ProcessorMetaData(
                            DaoMetaData.from(element as TypeElement, processingEnv),
                            AutoDaoParams.from(element.getAnnotation(AutoDao::class.java), processingEnv)
                    ).let { generateSubDao(it, element) }
                }
        return true
    }

    private fun generateOrderEnum() {
        val autoRoomDaoClassName = "AutoRoomDao"
        val orderEnum = TypeSpec
                .enumBuilder("Order")
                .addEnumConstant("ASC")
                .addEnumConstant("DESC")
                .build()
        generateFile("com.parokit.autoroom", autoRoomDaoClassName, orderEnum)
    }

    private fun generateSubDao(autoDaoContext: ProcessorMetaData, element: TypeElement) {
        with(autoDaoContext) {
            val fileName = "Auto_${daoMetaData.className}"
            val daoClassBuilder = TypeSpec
                    .classBuilder(fileName)
                    .superclass(element.asClassName())
                    .addModifiers(KModifier.ABSTRACT)
                    .addAnnotation(RoomAnnotationClassName.dao())
                    .addFunctions(daoFunctions(autoDaoParams))
            generateFile(daoMetaData.packageName, fileName, daoClassBuilder.build())
        }
    }

    private fun generateFile(packageName: String, fileName: String, daoClass: TypeSpec) {
        val file = FileSpec
                .builder(packageName, fileName)
                .addStaticImport("io.reactivex.rxkotlin", "toFlowable")
                .addStaticImport("io.reactivex.android.schedulers", "AndroidSchedulers")
                .addStaticImport("androidx.room", "OnConflictStrategy")
                .addType(daoClass)
                .build()

        // FIXME: weirdly under kaptKotlin files is not recognized as source file on AS or IntelliJ
        // so as a workaround we generate .kt file in generated/source/kapt/$sourceSetName
        // ref 1: https://github.com/hotchemi/PermissionsDispatcher/issues/320#issuecomment-316175775
        // ref 2: https://github.com/permissions-dispatcher/PermissionsDispatcher/blob/master/processor/src/main/kotlin/permissions/dispatcher/processor/PermissionsProcessor.kt
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]?.replace("kaptKotlin", "kapt")
                ?: run {
                    processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Can't find the target directory for generated Kotlin files.")
                    return
                }
        file.writeTo(File(kaptKotlinGeneratedDir))
    }

}