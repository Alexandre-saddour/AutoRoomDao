package com.asaddour.autoroomdao.generators

import com.asaddour.autoroomdao.helpers.*
import com.asaddour.autoroomdao.models.AutoDaoParams
import com.squareup.kotlinpoet.*
import io.reactivex.schedulers.Schedulers


internal fun daoFunctions(params: AutoDaoParams) = listOf(

        //
        // Native room annotations (Blocking)
        //

        insertObjBlocking(params),
        insertObjsBlocking(params),
        updateObjBlocking(params),
        deleteObjBlocking(params),
        deleteObjsBlocking(params),
        deleteAllBlocking(params),

        //
        // Native room annotations (Rx)
        //
        insertObjRx(params),
        insertObjsRx(params),
        updateObjRx(params),
        deleteObjRx(params),
        deleteObjsRx(params),
        deleteAllRx(),

        //
        // AutoRoomDao: @Query annotations
        //

        *queries(params).toTypedArray()
)


//
// helpers for auto threading.
//

internal val autoThreadParam = ParameterSpec
        .builder("autoThread", Boolean::class)
        .defaultValue("true")
        .build()

internal fun FunSpec.Builder.concatIoThreadStatementWith(statement: String, vararg parameters: Any) = apply {
    addStatement(statement + ".let {\n" +
            "            when (autoThread) {\n" +
            "                true -> it.subscribeOn(%T.io()).observeOn(AndroidSchedulers.mainThread())\n" +
            "                false -> it\n" +
            "            }\n" +
            "        }", *parameters, Schedulers::class)

}


////////////////////////////////////////////////////////////////////////////////////////////////////
// AutoRoomDao: @Query annotations
////////////////////////////////////////////////////////////////////////////////////////////////////

private fun generateGetAll(params: AutoDaoParams): List<FunSpec> {
    return if (params.generateOnlyDefaultRxReturnType) {
        when (params.defaultRxReturnType) {
            singleType -> listOf(
                    getAllSingle_(params),
                    getAllLimitSingle_(params),
                    getAllSingle(params)
            )
            maybeType -> listOf(
                    getAllMaybe_(params),
                    getAllLimitMaybe_(params),
                    getAllMaybe(params)
            )
            flowableType -> listOf(
                    getAllFlowable_(params),
                    getAllLimitFlowable_(params),
                    getAllFlowable(params)
            )
            else -> throw IllegalArgumentException(
                    "Unsupported type ${params.defaultRxReturnType} for defaultRxReturnType"
            )

        }
    } else {
        //
        // Generate all
        //
        listOf(
                getAllSingle_(params),
                getAllMaybe_(params),
                getAllFlowable_(params),
                getAllLimitSingle_(params),
                getAllLimitMaybe_(params),
                getAllLimitFlowable_(params),
                getAllSingle(params),
                getAllMaybe(params),
                getAllFlowable(params)
        )
    }
}

private fun generateGetAllOrderBy(params: AutoDaoParams): List<FunSpec> {
    val listOfSingleFunctions = params.attributes.flatMap {
        listOf(
                getAllOrderByAttrAscSingle_(params.tableName, params.entityType, it),
                getAllOrderByAttrDescSingle_(params.tableName, params.entityType, it),
                getAllOrderByAttrAscLimitSingle_(params.tableName, params.entityType, it),
                getAllOrderByAttrDescLimitSingle_(params.tableName, params.entityType, it),
                getAllOrderedByAttrAsSingle(params, it)
        )
    }

    val listOfMaybeFunctions = params.attributes.flatMap {
        listOf(
                getAllOrderByAttrAscMaybe_(params.tableName, params.entityType, it),
                getAllOrderByAttrDescMaybe_(params.tableName, params.entityType, it),
                getAllOrderByAttrAscLimitMaybe_(params.tableName, params.entityType, it),
                getAllOrderByAttrDescLimitMaybe_(params.tableName, params.entityType, it),
                getAllOrderedByAttrAsMaybe(params, it)
        )
    }

    val listOfFlowableFunctions = params.attributes.flatMap {
        listOf(
                getAllOrderByAttrAscFlowable_(params.tableName, params.entityType, it),
                getAllOrderByAttrDescFlowable_(params.tableName, params.entityType, it),
                getAllOrderByAttrAscLimitFlowable_(params.tableName, params.entityType, it),
                getAllOrderByAttrDescLimitFlowable_(params.tableName, params.entityType, it),
                getAllOrderedByAttrAsFlowable(params, it)
        )
    }

    return if (params.generateOrderBy) {
        if (params.generateOnlyDefaultRxReturnType) {
            when (params.defaultRxReturnType) {
                singleType -> listOfSingleFunctions
                maybeType -> listOfMaybeFunctions
                flowableType -> listOfFlowableFunctions
                else -> throw IllegalArgumentException(
                        "Unsupported type ${params.defaultRxReturnType} for defaultRxReturnType"
                )
            }
        } else {
            // Generate all
            listOfSingleFunctions + listOfMaybeFunctions + listOfFlowableFunctions
        }
    } else {
        emptyList()
    }
}

private fun generateGetByAttr(params: AutoDaoParams): List<FunSpec> {
    return params.attributes.flatMap { attrToGet ->
        listOf(
                getByAttrsAsSingle_(params.tableName, params.entityType, attrToGet),
                getByAttrsAsMaybe_(params.tableName, params.entityType, attrToGet),
                getByAttrsAsFlowable_(params.tableName, params.entityType, attrToGet),
                getByAttrsLimitAsSingle_(params.tableName, params.entityType, attrToGet),
                getByAttrsLimitAsMaybe_(params.tableName, params.entityType, attrToGet),
                getByAttrsLimitAsFlowable_(params.tableName, params.entityType, attrToGet),
                getByAttrsAsSingle(params, attrToGet),
                getByAttrsAsMaybe(params, attrToGet),
                getByAttrsAsFlowable(params, attrToGet)
        )
    }
}

private fun generateGetByAttrOrderByAttr(params: AutoDaoParams): List<FunSpec> {
    val orderByAttrSingle = params.attributes.flatMap { attrToGet ->
        params.attributes.flatMap { orderByAttr ->
            //                    getByAttrsOrderByAttrAscAsSingle(params, attrToGet, orderByAttr)
            listOf(
                    getByAttrsOrderedByAttrAscSingle_(params.tableName, params.entityType, attrToGet, orderByAttr),
                    getByAttrsOrderedByAttrDescSingle_(params.tableName, params.entityType, attrToGet, orderByAttr),
                    getByAttrsOrderByAttrAscLimitSingle_(params.tableName, params.entityType, attrToGet, orderByAttr),
                    getByAttrsOrderByAttrDescLimitSingle_(params.tableName, params.entityType, attrToGet, orderByAttr),
                    getByAttrsOrderedByAsSingle(params, attrToGet, orderByAttr)
            )
        }
    }

    val orderByAttrMaybe= params.attributes.flatMap { attrToGet ->
        params.attributes.flatMap { orderByAttr ->
            //                    getByAttrsOrderByAttrAscAsSingle(params, attrToGet, orderByAttr)
            listOf(
                    getByAttrsOrderedByAttrAscMaybe_(params.tableName, params.entityType, attrToGet, orderByAttr),
                    getByAttrsOrderedByAttrDescMaybe_(params.tableName, params.entityType, attrToGet, orderByAttr),
                    getByAttrsOrderByAttrAscLimitMaybe_(params.tableName, params.entityType, attrToGet, orderByAttr),
                    getByAttrsOrderByAttrDescLimitMaybe_(params.tableName, params.entityType, attrToGet, orderByAttr),
                    getByAttrsOrderedByAsMaybe(params, attrToGet, orderByAttr)
            )
        }
    }
    val orderByAttrFlowable = params.attributes.flatMap { attrToGet ->
        params.attributes.flatMap { orderByAttr ->
            //                    getByAttrsOrderByAttrAscAsSingle(params, attrToGet, orderByAttr)
            listOf(
                    getByAttrsOrderedByAttrAscFlowable_(params.tableName, params.entityType, attrToGet, orderByAttr),
                    getByAttrsOrderedByAttrDescFlowable_(params.tableName, params.entityType, attrToGet, orderByAttr),
                    getByAttrsOrderByAttrAscLimitFlowable_(params.tableName, params.entityType, attrToGet, orderByAttr),
                    getByAttrsOrderByAttrDescLimitFlowable_(params.tableName, params.entityType, attrToGet, orderByAttr),
                    getByAttrsOrderedByAsFlowable(params, attrToGet, orderByAttr)
            )
        }
    }

    return if (params.generateOrderBy) {
        if (params.generateOnlyDefaultRxReturnType) {
            when (params.defaultRxReturnType) {
                singleType -> orderByAttrSingle
                maybeType -> orderByAttrMaybe
                flowableType -> orderByAttrFlowable
                else -> throw IllegalArgumentException(
                        "Unsupported type ${params.defaultRxReturnType} for defaultRxReturnType"
                )
            }
        } else {
            // Generate all
            orderByAttrSingle + orderByAttrMaybe + orderByAttrFlowable
        }
    } else {
        emptyList()
    }
}


private fun queries(params: AutoDaoParams): List<FunSpec> {
    return generateGetAll(params) +
            generateGetAllOrderBy(params) +
            generateGetByAttr(params) +
            generateGetByAttrOrderByAttr(params)
}


////////////////////////////////////////////////////////////////////////////////////////////////////
// Get All
////////////////////////////////////////////////////////////////////////////////////////////////////

private fun getAll_(functionName: String,
                    returnType: ParameterizedTypeName,
                    params: AutoDaoParams) = FunSpec
        .builder(functionName)
        .addAnnotation(RoomAnnotationClassName.query(
                "\"SELECT * FROM ${params.tableName}\""
        ))
        .addModifiers(KModifier.PROTECTED, KModifier.ABSTRACT)
        .returns(returnType)
        .build()

private fun getAllLimit_(functionName: String,
                         returnType: ParameterizedTypeName,
                         params: AutoDaoParams) = FunSpec
        .builder(functionName)
        .addAnnotation(RoomAnnotationClassName.query(
                "\"SELECT * FROM ${params.tableName} LIMIT :limit\""
        ))
        .addParameter("limit", Int::class)
        .addModifiers(KModifier.PROTECTED, KModifier.ABSTRACT)
        .returns(returnType)
        .build()

private fun getAllSingle_(params: AutoDaoParams) =
        getAll_("getAllAsSingle_",
                singleType.parameterizedBy(listType.parameterizedBy(params.entityType)),
                params
        )

private fun getAllMaybe_(params: AutoDaoParams) =
        getAll_("getAllAsMaybe_",
                maybeType.parameterizedBy(listType.parameterizedBy(params.entityType)),
                params
        )

private fun getAllFlowable_(params: AutoDaoParams) =
        getAll_("getAllAsFlowable_",
                flowableType.parameterizedBy(listType.parameterizedBy(params.entityType)),
                params
        )

private fun getAllLimitSingle_(params: AutoDaoParams) = getAllLimit_(
        "getAllAsSingle_",
        singleType.parameterizedBy(listType.parameterizedBy(params.entityType)),
        params
)

private fun getAllLimitMaybe_(params: AutoDaoParams) = getAllLimit_(
        "getAllAsMaybe_",
        maybeType.parameterizedBy(listType.parameterizedBy(params.entityType)),
        params
)

private fun getAllLimitFlowable_(params: AutoDaoParams) = getAllLimit_(
        "getAllAsFlowable_",
        flowableType.parameterizedBy(listType.parameterizedBy(params.entityType)),
        params
)

private fun getAll(
        publicFunctionName: String,
        privateFunctionName: String,
        returnType: ParameterizedTypeName,
        addAutoThreadParam: Boolean = true
): FunSpec {

    val funSpec = FunSpec
            .builder(publicFunctionName)
            .addParameter(ParameterSpec
                    .builder("limit", Int::class)
                    .defaultValue("0")
                    .build())
            .returns(returnType)

    val statement = "" +
            "return when (limit) {\n" +
            "        0 -> ${privateFunctionName}_()\n" +
            "        else -> ${privateFunctionName}_(limit)\n" +
            "    }" +
            ""
    if (addAutoThreadParam) {
        funSpec.addParameter(autoThreadParam).concatIoThreadStatementWith(statement)
    } else {
        funSpec.addStatement(statement)
    }
    return funSpec.build()
}

private fun getAllSingle(params: AutoDaoParams) = getAll(
        publicFunctionName =
        if (params.defaultRxReturnType == singleType) "getAll"
        else "getAllAsSingle",
        privateFunctionName = "getAllAsSingle",
        returnType = singleType.parameterizedBy(listType.parameterizedBy(params.entityType))
)

private fun getAllMaybe(params: AutoDaoParams) = getAll(
        publicFunctionName =
        if (params.defaultRxReturnType == maybeType) "getAll"
        else "getAllAsMaybe",
        privateFunctionName = "getAllAsMaybe",
        returnType = maybeType.parameterizedBy(listType.parameterizedBy(params.entityType))
)

private fun getAllFlowable(params: AutoDaoParams) = getAll(
        publicFunctionName =
        if (params.defaultRxReturnType == flowableType) "getAll"
        else "getAllAsFlowable",
        privateFunctionName = "getAllAsFlowable",
        returnType = flowableType.parameterizedBy(listType.parameterizedBy(params.entityType)),
        addAutoThreadParam = false
)

////////////////////////////////////////////////////////////////////////////////////////////////////
// Get All order by attr
////////////////////////////////////////////////////////////////////////////////////////////////////


private fun getAllOrderByAttr_(
        functionName: String,
        returnType: ParameterizedTypeName,
        tableName: String,
        order: String,
        attr: AutoDaoParams.Attr
) = FunSpec
        .builder(functionName)
        .addAnnotation(RoomAnnotationClassName.query(
                "\"SELECT * FROM $tableName ORDER BY ${attr.collumnName} $order\""
        ))
        .addModifiers(KModifier.PROTECTED, KModifier.ABSTRACT)
        .returns(returnType)
        .build()


private fun getAllOrderByAttrSingle_(
        tableName: String,
        order: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr
) = getAllOrderByAttr_(
        functionName = "getAllOrderBy${attr.name.capitalize()}AsSingle${order.toLowerCase().capitalize()}_",
        returnType = singleType.parameterizedBy(listType.parameterizedBy(modelType)),
        tableName = tableName,
        order = order,
        attr = attr
)

private fun getAllOrderByAttrMaybe_(
        tableName: String,
        order: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr
) = getAllOrderByAttr_(
        functionName = "getAllOrderBy${attr.name.capitalize()}AsMaybe${order.toLowerCase().capitalize()}_",
        returnType = maybeType.parameterizedBy(listType.parameterizedBy(modelType)),
        tableName = tableName,
        order = order,
        attr = attr
)

private fun getAllOrderByAttrFlowable_(
        tableName: String,
        order: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr
) = getAllOrderByAttr_(
        functionName = "getAllOrderBy${attr.name.capitalize()}AsFlowable${order.toLowerCase().capitalize()}_",
        returnType = flowableType.parameterizedBy(listType.parameterizedBy(modelType)),
        tableName = tableName,
        order = order,
        attr = attr
)

private fun getAllOrderByAttrLimit_(
        functionName: String,
        returnType: ParameterizedTypeName,
        tableName: String,
        order: String,
        attr: AutoDaoParams.Attr
) = FunSpec
        .builder(functionName)
        .addParameter("limit", Int::class)
        .addAnnotation(RoomAnnotationClassName.query(
                "\"SELECT * FROM $tableName ORDER BY ${attr.collumnName} $order LIMIT :limit\""
        ))
        .addModifiers(KModifier.PROTECTED, KModifier.ABSTRACT)
        .returns(returnType)
        .build()

private fun getAllOrderByAttrLimitSingle_(
        tableName: String,
        order: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr
) =
        getAllOrderByAttrLimit_(
                functionName = "getAllOrderBy${attr.name.capitalize()}AsSingle${order.toLowerCase().capitalize()}_",
                returnType = singleType.parameterizedBy(listType.parameterizedBy(modelType)),
                tableName = tableName,
                order = order,
                attr = attr
        )

private fun getAllOrderByAttrLimitMaybe_(
        tableName: String,
        order: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr
) =
        getAllOrderByAttrLimit_(
                functionName = "getAllOrderBy${attr.name.capitalize()}AsMaybe${order.toLowerCase().capitalize()}_",
                returnType = maybeType.parameterizedBy(listType.parameterizedBy(modelType)),
                tableName = tableName,
                order = order,
                attr = attr
        )

private fun getAllOrderByAttrLimitFlowable_(
        tableName: String,
        order: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr
) =
        getAllOrderByAttrLimit_(
                functionName = "getAllOrderBy${attr.name.capitalize()}AsFlowable${order.toLowerCase().capitalize()}_",
                returnType = flowableType.parameterizedBy(listType.parameterizedBy(modelType)),
                order = order,
                tableName = tableName,
                attr = attr
        )

private fun getAllOrderByAttrAscSingle_(tableName: String, modelType: TypeName, attr: AutoDaoParams.Attr) =
        getAllOrderByAttrSingle_(
                order = "ASC",
                tableName = tableName,
                modelType = modelType,
                attr = attr
        )

private fun getAllOrderByAttrDescSingle_(tableName: String, modelType: TypeName, attr: AutoDaoParams.Attr) =
        getAllOrderByAttrSingle_(
                order = "DESC",
                tableName = tableName,
                modelType = modelType,
                attr = attr
        )

private fun getAllOrderByAttrAscMaybe_(tableName: String, modelType: TypeName, attr: AutoDaoParams.Attr) =
        getAllOrderByAttrMaybe_(
                order = "ASC",
                tableName = tableName,
                modelType = modelType,
                attr = attr
        )

private fun getAllOrderByAttrDescMaybe_(tableName: String, modelType: TypeName, attr: AutoDaoParams.Attr) =
        getAllOrderByAttrMaybe_(
                order = "DESC",
                tableName = tableName,
                modelType = modelType,
                attr = attr
        )

private fun getAllOrderByAttrAscFlowable_(tableName: String, modelType: TypeName, attr: AutoDaoParams.Attr) =
        getAllOrderByAttrFlowable_(
                order = "ASC",
                tableName = tableName,
                modelType = modelType,
                attr = attr
        )

private fun getAllOrderByAttrDescFlowable_(tableName: String, modelType: TypeName, attr: AutoDaoParams.Attr) =
        getAllOrderByAttrFlowable_(
                order = "DESC",
                tableName = tableName,
                modelType = modelType,
                attr = attr
        )

private fun getAllOrderByAttrAscLimitSingle_(tableName: String, modelType: TypeName, attr: AutoDaoParams.Attr) =
        getAllOrderByAttrLimitSingle_(
                order = "ASC",
                tableName = tableName,
                modelType = modelType,
                attr = attr
        )

private fun getAllOrderByAttrDescLimitSingle_(tableName: String, modelType: TypeName, attr: AutoDaoParams.Attr) =
        getAllOrderByAttrLimitSingle_(
                order = "DESC",
                tableName = tableName,
                modelType = modelType,
                attr = attr
        )

private fun getAllOrderByAttrAscLimitMaybe_(tableName: String, modelType: TypeName, attr: AutoDaoParams.Attr) =
        getAllOrderByAttrLimitMaybe_(
                order = "ASC",
                tableName = tableName,
                modelType = modelType,
                attr = attr
        )

private fun getAllOrderByAttrDescLimitMaybe_(tableName: String, modelType: TypeName, attr: AutoDaoParams.Attr) =
        getAllOrderByAttrLimitMaybe_(
                order = "DESC",
                tableName = tableName,
                modelType = modelType,
                attr = attr
        )

private fun getAllOrderByAttrAscLimitFlowable_(tableName: String, modelType: TypeName, attr: AutoDaoParams.Attr) =
        getAllOrderByAttrLimitFlowable_(
                order = "ASC",
                tableName = tableName,
                modelType = modelType,
                attr = attr
        )

private fun getAllOrderByAttrDescLimitFlowable_(tableName: String, modelType: TypeName, attr: AutoDaoParams.Attr) =
        getAllOrderByAttrLimitFlowable_(
                order = "DESC",
                tableName = tableName,
                modelType = modelType,
                attr = attr
        )

private fun getAllOrderedByAttr(
        publicFunctionName: String,
        privateFunctionName: String,
        returnType: ParameterizedTypeName
) = FunSpec
        .builder(publicFunctionName)
        .addParameter(ParameterSpec
                .builder("limit", Int::class)
                .defaultValue("0")
                .build())
        .addParameter(ParameterSpec
                .builder("order", orderType)
                .defaultValue("Order.ASC")
                .build())
        .addParameter(autoThreadParam)
        .concatIoThreadStatementWith("" +
                "return if (limit == 0){\n" +
                "            when (order){\n" +
                "                Order.ASC -> ${privateFunctionName}Asc_()\n" +
                "                Order.DESC -> ${privateFunctionName}Desc_()\n" +
                "            }\n" +
                "        }\n" +
                "        else {\n" +
                "            when (order){\n" +
                "                Order.ASC -> ${privateFunctionName}Asc_(limit)\n" +
                "                Order.DESC -> ${privateFunctionName}Desc_(limit)\n" +
                "            }\n" +
                "        }" +
                "")
        .returns(returnType)
        .build()

private fun getAllOrderedByAttrAsSingle(params: AutoDaoParams, attr: AutoDaoParams.Attr) =
        getAllOrderedByAttr(
                publicFunctionName = when (singleType) {
                    params.defaultRxReturnType -> "getAllOrderedBy${attr.name.capitalize()}"
                    else -> "getAllOrderedBy${attr.name.capitalize()}AsSingle"
                },
                privateFunctionName = "getAllOrderBy${attr.name.capitalize()}AsSingle",
                returnType = singleType.parameterizedBy(listType.parameterizedBy(params.entityType))
        )

private fun getAllOrderedByAttrAsMaybe(params: AutoDaoParams, attr: AutoDaoParams.Attr) =
        getAllOrderedByAttr(
                publicFunctionName = when (maybeType) {
                    params.defaultRxReturnType -> "getAllOrderedBy${attr.name.capitalize()}"
                    else -> "getAllOrderedBy${attr.name.capitalize()}AsMaybe"
                },
                privateFunctionName = "getAllOrderBy${attr.name.capitalize()}AsMaybe",
                returnType = maybeType.parameterizedBy(listType.parameterizedBy(params.entityType))
        )

private fun getAllOrderedByAttrAsFlowable(params: AutoDaoParams, attr: AutoDaoParams.Attr) =
        getAllOrderedByAttr(
                publicFunctionName = when (flowableType) {
                    params.defaultRxReturnType -> "getAllOrderedBy${attr.name.capitalize()}"
                    else -> "getAllOrderedBy${attr.name.capitalize()}AsFlowable"
                },
                privateFunctionName = "getAllOrderBy${attr.name.capitalize()}AsFlowable",
                returnType = flowableType.parameterizedBy(listType.parameterizedBy(params.entityType))
        )


////////////////////////////////////////////////////////////////////////////////////////////////////
// Get By Attr Name order by attr Name
////////////////////////////////////////////////////////////////////////////////////////////////////

private fun getByAttrOrderByName(
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr
) = "getBy${attrToGetBy.name.capitalize()}OrderedBy${attrToOrderBy.name.capitalize()}"

private fun getByAttrsOrderByAttr_(
        functionName: String,
        returnType: ParameterizedTypeName,
        tableName: String,
        order: String,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr
) = FunSpec
        .builder(functionName)
        .addAnnotation(RoomAnnotationClassName.query(
                "\"SELECT * FROM $tableName WHERE ${attrToGetBy.collumnName} IN (:${attrToGetBy.name}s) ORDER BY ${attrToOrderBy.collumnName} $order\""
        ))
        .addParameter("${attrToGetBy.name}s", attrToGetBy.type.javaToKotlinType(), KModifier.VARARG)
        .addModifiers(KModifier.PROTECTED, KModifier.ABSTRACT)
        .returns(returnType)
        .build()

private fun getByAttrsOrderByAttrLimit_(
        functionName: String,
        returnType: ParameterizedTypeName,
        tableName: String,
        order: String,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr
) = FunSpec
        .builder(functionName)
        .addAnnotation(RoomAnnotationClassName.query(
                "\"SELECT * FROM $tableName WHERE ${attrToGetBy.collumnName} IN (:${attrToGetBy.name}s) ORDER BY ${attrToOrderBy.collumnName} $order LIMIT :limit\""
        ))
        .addParameter("${attrToGetBy.name}s", attrToGetBy.type.javaToKotlinType(), KModifier.VARARG)
        .addParameter("limit", Int::class)
        .addModifiers(KModifier.PROTECTED, KModifier.ABSTRACT)
        .returns(returnType)
        .build()

private fun getByAttrsOrderedByAttrSingle_(
        tableName: String,
        order: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr
) = getByAttrsOrderByAttr_(
        functionName = "${getByAttrOrderByName(attrToGetBy, attrToOrderBy)}AsSingle${order.toLowerCase().capitalize()}_",
        returnType = singleType.parameterizedBy(listType.parameterizedBy(modelType)),
        tableName = tableName,
        order = order,
        attrToGetBy = attrToGetBy,
        attrToOrderBy = attrToOrderBy
)

private fun getByAttrsOrderedByAttrMaybe_(
        tableName: String,
        order: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr
) = getByAttrsOrderByAttr_(
        functionName = "${getByAttrOrderByName(attrToGetBy, attrToOrderBy)}AsMaybe${order.toLowerCase().capitalize()}_",
        returnType = maybeType.parameterizedBy(listType.parameterizedBy(modelType)),
        tableName = tableName,
        order = order,
        attrToGetBy = attrToGetBy,
        attrToOrderBy = attrToOrderBy
)

private fun getByAttrsOrderedByAttrFlowable_(
        tableName: String,
        order: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr
) = getByAttrsOrderByAttr_(
        functionName = "${getByAttrOrderByName(attrToGetBy, attrToOrderBy)}AsFlowable${order.toLowerCase().capitalize()}_",
        returnType = flowableType.parameterizedBy(listType.parameterizedBy(modelType)),
        tableName = tableName,
        order = order,
        attrToGetBy = attrToGetBy,
        attrToOrderBy = attrToOrderBy
)

private fun getByAttrsOrderedByAttrSingleLimit_(
        tableName: String,
        order: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr
) =
        getByAttrsOrderByAttrLimit_(
                functionName = "${getByAttrOrderByName(attrToGetBy, attrToOrderBy)}AsSingle${order.toLowerCase().capitalize()}_",
                returnType = singleType.parameterizedBy(listType.parameterizedBy(modelType)),
                tableName = tableName,
                order = order,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy
        )

private fun getByAttrsOrderedByAttrMaybeLimit_(
        tableName: String,
        order: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr
) =
        getByAttrsOrderByAttrLimit_(
                functionName = "${getByAttrOrderByName(attrToGetBy, attrToOrderBy)}AsMaybe${order.toLowerCase().capitalize()}_",
                returnType = maybeType.parameterizedBy(listType.parameterizedBy(modelType)),
                tableName = tableName,
                order = order,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy
        )

private fun getByAttrsOrderedByAttrFlowableLimit_(
        tableName: String,
        order: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr
) =
        getByAttrsOrderByAttrLimit_(
                functionName = "${getByAttrOrderByName(attrToGetBy, attrToOrderBy)}AsFlowable${order.toLowerCase().capitalize()}_",
                returnType = flowableType.parameterizedBy(listType.parameterizedBy(modelType)),
                tableName = tableName,
                order = order,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy
        )

private fun getByAttrsOrderedByAttrAscSingle_(
        tableName: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr
) =
        getByAttrsOrderedByAttrSingle_(
                order = "ASC",
                tableName = tableName,
                modelType = modelType,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy
        )

private fun getByAttrsOrderedByAttrDescSingle_(
        tableName: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr
) =
        getByAttrsOrderedByAttrSingle_(
                order = "DESC",
                tableName = tableName,
                modelType = modelType,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy
        )

private fun getByAttrsOrderedByAttrAscMaybe_(
        tableName: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr
) =
        getByAttrsOrderedByAttrMaybe_(
                order = "ASC",
                tableName = tableName,
                modelType = modelType,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy
        )

private fun getByAttrsOrderedByAttrDescMaybe_(
        tableName: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr
) =
        getByAttrsOrderedByAttrMaybe_(
                order = "DESC",
                tableName = tableName,
                modelType = modelType,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy
        )

private fun getByAttrsOrderedByAttrAscFlowable_(
        tableName: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr
) =
        getByAttrsOrderedByAttrFlowable_(
                order = "ASC",
                tableName = tableName,
                modelType = modelType,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy
        )

private fun getByAttrsOrderedByAttrDescFlowable_(
        tableName: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr
) =
        getByAttrsOrderedByAttrFlowable_(
                order = "DESC",
                tableName = tableName,
                modelType = modelType,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy
        )


private fun getByAttrsOrderByAttrAscLimitSingle_(
        tableName: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr
) =
        getByAttrsOrderedByAttrSingleLimit_(
                order = "ASC",
                tableName = tableName,
                modelType = modelType,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy
        )

private fun getByAttrsOrderByAttrDescLimitSingle_(
        tableName: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr
) =
        getByAttrsOrderedByAttrSingleLimit_(
                order = "DESC",
                tableName = tableName,
                modelType = modelType,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy
        )

private fun getByAttrsOrderByAttrAscLimitMaybe_(
        tableName: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr
) =
        getByAttrsOrderedByAttrMaybeLimit_(
                order = "ASC",
                tableName = tableName,
                modelType = modelType,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy
        )

private fun getByAttrsOrderByAttrDescLimitMaybe_(
        tableName: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr
) =
        getByAttrsOrderedByAttrMaybeLimit_(
                order = "DESC",
                tableName = tableName,
                modelType = modelType,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy
        )

private fun getByAttrsOrderByAttrAscLimitFlowable_(
        tableName: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr
) =
        getByAttrsOrderedByAttrFlowableLimit_(
                order = "ASC",
                tableName = tableName,
                modelType = modelType,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy
        )

private fun getByAttrsOrderByAttrDescLimitFlowable_(
        tableName: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr
) =
        getByAttrsOrderedByAttrFlowableLimit_(
                order = "DESC",
                tableName = tableName,
                modelType = modelType,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy
        )

private fun getByAttrsOrderedByAttr(
        publicFunctionName: String,
        privateFunctionName: String,
        returnType: ParameterizedTypeName,
        attrToGetBy: AutoDaoParams.Attr
) = FunSpec
        .builder(publicFunctionName)
        .addParameter("${attrToGetBy.name}s", attrToGetBy.type.javaToKotlinType(), KModifier.VARARG)
        .addParameter(ParameterSpec
                .builder("limit", Int::class)
                .defaultValue("0")
                .build())
        .addParameter(ParameterSpec
                .builder("order", orderType)
                .defaultValue("Order.ASC")
                .build())
        .addParameter(autoThreadParam)
        .concatIoThreadStatementWith("" +
                "return if (limit == 0){\n" +
                "            when (order){\n" +
                "                Order.ASC -> ${privateFunctionName}Asc_(*${attrToGetBy.name}s)\n" +
                "                Order.DESC -> ${privateFunctionName}Desc_(*${attrToGetBy.name}s)\n" +
                "            }\n" +
                "        }\n" +
                "        else {\n" +
                "            when (order){\n" +
                "                Order.ASC -> ${privateFunctionName}Asc_(*${attrToGetBy.name}s, limit = limit)\n" +
                "                Order.DESC -> ${privateFunctionName}Desc_(*${attrToGetBy.name}s, limit = limit)\n" +
                "            }\n" +
                "        }" +
                "")
        .returns(returnType)
        .build()

private fun getByAttrsOrderedByAsSingle(params: AutoDaoParams,
                               attrToGetBy: AutoDaoParams.Attr,
                               attrToOrderBy: AutoDaoParams.Attr
) = getByAttrsOrderedByAttr(
        publicFunctionName = when (singleType) {
            params.defaultRxReturnType -> getByAttrOrderByName(attrToGetBy, attrToOrderBy)
            else -> "${getByAttrOrderByName(attrToGetBy, attrToOrderBy)}AsSingle"
        },
        privateFunctionName = "${getByAttrOrderByName(attrToGetBy, attrToOrderBy)}AsSingle",
        returnType = singleType.parameterizedBy(listType.parameterizedBy(params.entityType)),
        attrToGetBy = attrToGetBy
)

private fun getByAttrsOrderedByAsMaybe(params: AutoDaoParams,
                               attrToGetBy: AutoDaoParams.Attr,
                               attrToOrderBy: AutoDaoParams.Attr
) = getByAttrsOrderedByAttr(
        publicFunctionName = when (singleType) {
            params.defaultRxReturnType -> getByAttrOrderByName(attrToGetBy, attrToOrderBy)
            else -> "${getByAttrOrderByName(attrToGetBy, attrToOrderBy)}AsMaybe"
        },
        privateFunctionName = "${getByAttrOrderByName(attrToGetBy, attrToOrderBy)}AsMaybe",
        returnType = maybeType.parameterizedBy(listType.parameterizedBy(params.entityType)),
        attrToGetBy = attrToGetBy
)

private fun getByAttrsOrderedByAsFlowable(params: AutoDaoParams,
                               attrToGetBy: AutoDaoParams.Attr,
                               attrToOrderBy: AutoDaoParams.Attr
) = getByAttrsOrderedByAttr(
        publicFunctionName = when (singleType) {
            params.defaultRxReturnType -> getByAttrOrderByName(attrToGetBy, attrToOrderBy)
            else -> "${getByAttrOrderByName(attrToGetBy, attrToOrderBy)}AsFlowable"
        },
        privateFunctionName = "${getByAttrOrderByName(attrToGetBy, attrToOrderBy)}AsFlowable",
        returnType = flowableType.parameterizedBy(listType.parameterizedBy(params.entityType)),
        attrToGetBy = attrToGetBy
)


////////////////////////////////////////////////////////////////////////////////////////////////////
// Get By Attr Name
////////////////////////////////////////////////////////////////////////////////////////////////////

private fun getByAttrName(attr: AutoDaoParams.Attr) = "getBy${attr.name.capitalize()}"

private fun getByAttrs_(
        functionName: String,
        returnType: ParameterizedTypeName,
        tableName: String,
        attr: AutoDaoParams.Attr
) = FunSpec
        .builder(functionName)
        .addAnnotation(RoomAnnotationClassName.query(
                "\"SELECT * FROM $tableName WHERE ${attr.collumnName} IN (:${attr.name}s)\""
        ))
        .addParameter("${attr.name}s", attr.type.javaToKotlinType(), KModifier.VARARG)
        .addModifiers(KModifier.PROTECTED, KModifier.ABSTRACT)
        .returns(returnType)
        .build()

private fun getByAttrsAsSingle_(tableName: String, modelType: TypeName, attr: AutoDaoParams.Attr) =
        getByAttrs_(
                "${getByAttrName(attr)}AsSingle_",
                singleType.parameterizedBy(listType.parameterizedBy(modelType)),
                tableName,
                attr
        )

private fun getByAttrsAsMaybe_(tableName: String, modelType: TypeName, attr: AutoDaoParams.Attr) =
        getByAttrs_(
                "${getByAttrName(attr)}AsMaybe_",
                maybeType.parameterizedBy(listType.parameterizedBy(modelType)),
                tableName,
                attr
        )

private fun getByAttrsAsFlowable_(tableName: String, modelType: TypeName, attr: AutoDaoParams.Attr) =
        getByAttrs_(
                "${getByAttrName(attr)}AsFlowable_",
                flowableType.parameterizedBy(listType.parameterizedBy(modelType)),
                tableName,
                attr
        )

private fun getByAttrsLimit_(
        functionName: String,
        returnType: ParameterizedTypeName,
        tableName: String,
        attr: AutoDaoParams.Attr
) = FunSpec
        .builder(functionName)
        .addAnnotation(RoomAnnotationClassName.query(
                "\"SELECT * FROM $tableName WHERE ${attr.collumnName} IN (:${attr.name}s) LIMIT :limit\""
        ))
        .addParameter("${attr.name}s", attr.type.javaToKotlinType(), KModifier.VARARG)
        .addParameter("limit", Int::class)
        .addModifiers(KModifier.PROTECTED, KModifier.ABSTRACT)
        .returns(returnType)
        .build()

private fun getByAttrsLimitAsSingle_(tableName: String, modelType: TypeName, attr: AutoDaoParams.Attr) =
        getByAttrsLimit_(
                "${getByAttrName(attr)}AsSingle_",
                singleType.parameterizedBy(listType.parameterizedBy(modelType)),
                tableName,
                attr
        )

private fun getByAttrsLimitAsMaybe_(tableName: String, modelType: TypeName, attr: AutoDaoParams.Attr) =
        getByAttrsLimit_(
                "${getByAttrName(attr)}AsMaybe_",
                maybeType.parameterizedBy(listType.parameterizedBy(modelType)),
                tableName,
                attr
        )

private fun getByAttrsLimitAsFlowable_(tableName: String, modelType: TypeName, attr: AutoDaoParams.Attr) =
        getByAttrsLimit_(
                "${getByAttrName(attr)}AsFlowable_",
                flowableType.parameterizedBy(listType.parameterizedBy(modelType)),
                tableName,
                attr
        )

private fun getByAttrs(
        publicFunctionName: String,
        privateFunctionName: String,
        returnType: ParameterizedTypeName,
        attr: AutoDaoParams.Attr,
        addAutoThreadParam: Boolean = true
): FunSpec {
    val funSpec = FunSpec.builder(publicFunctionName)
            .addParameter("${attr.name}s", attr.type.javaToKotlinType(), KModifier.VARARG)
            .addParameter(ParameterSpec
                    .builder("limit", Int::class)
                    .defaultValue("0")
                    .build())
            .returns(returnType)
    val statement = "" +
            "    return when (limit){\n" +
            "            0 -> ${privateFunctionName}_(*${attr.name}s)\n" +
            "            else -> ${privateFunctionName}_(*${attr.name}s, limit = limit)\n" +
            "        }" +
            ""
    if (addAutoThreadParam) {
        funSpec.addParameter(autoThreadParam).concatIoThreadStatementWith(statement)
    } else {
        funSpec.addStatement(statement)
    }
    return funSpec.build()
}

private fun getByAttrsAsSingle(params: AutoDaoParams,
                               attr: AutoDaoParams.Attr) = getByAttrs(
        publicFunctionName = when (singleType) {
            params.defaultRxReturnType -> getByAttrName(attr)
            else -> "${getByAttrName(attr)}AsSingle"
        },
        privateFunctionName = "${getByAttrName(attr)}AsSingle",
        returnType = singleType.parameterizedBy(listType.parameterizedBy(params.entityType)),
        attr = attr
)

private fun getByAttrsAsMaybe(params: AutoDaoParams,
                              attr: AutoDaoParams.Attr) = getByAttrs(
        publicFunctionName = when (maybeType) {
            params.defaultRxReturnType -> getByAttrName(attr)
            else -> "${getByAttrName(attr)}AsMaybe"
        },
        privateFunctionName = "${getByAttrName(attr)}AsMaybe",
        returnType = maybeType.parameterizedBy(listType.parameterizedBy(params.entityType)),
        attr = attr
)

private fun getByAttrsAsFlowable(params: AutoDaoParams,
                                 attr: AutoDaoParams.Attr) = getByAttrs(
        publicFunctionName = when (flowableType) {
            params.defaultRxReturnType -> getByAttrName(attr)
            else -> "${getByAttrName(attr)}AsFlowable"
        },
        privateFunctionName = "${getByAttrName(attr)}AsFlowable",
        returnType = flowableType.parameterizedBy(listType.parameterizedBy(params.entityType)),
        attr = attr,
        addAutoThreadParam = false
)