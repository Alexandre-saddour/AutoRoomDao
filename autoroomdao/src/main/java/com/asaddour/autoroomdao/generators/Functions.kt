package com.asaddour.autoroomdao.generators

import com.asaddour.autoroomdao.helpers.*
import com.asaddour.autoroomdao.models.AutoDaoParams
import com.squareup.kotlinpoet.*
import io.reactivex.schedulers.Schedulers


internal fun daoFunctions(params: AutoDaoParams) = listOf(

        //
        // Native room annotations
        //
        *nativeRoomQueries(params).toTypedArray(),

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
    fun singleList() = listOf(
            getAllSingle_(params),
            getAllLimitSingle_(params),
            getAllSingle(params)
    )

    fun maybeList() = listOf(
            getAllMaybe_(params),
            getAllLimitMaybe_(params),
            getAllMaybe(params)
    )

    fun flowableList() = listOf(
            getAllFlowable_(params),
            getAllLimitFlowable_(params),
            getAllFlowable(params)
    )

    fun liveDataList() = listOf(
            getAllLiveData_(params),
            getAllLimitLiveData_(params),
            getAllLiveData(params)
    )
    return if (params.generateOnlyDefaultReturnType) {
        //
        // generate only default type
        when (params.defaultReturnType) {
            singleType -> singleList()
            maybeType -> maybeList()
            flowableType -> flowableList()
            liveDataType -> liveDataList()
            else -> throw IllegalArgumentException(
                    "Unsupported type ${params.defaultReturnType} for defaultRxReturnType"
            )

        }
    } else {
        //
        // Generate all
        val rxQueries = when (params.generateRxQueries) {
            true -> singleList() + maybeList() + flowableList()
            else -> emptyList()
        }
        val liveDataQueries = when (params.generateLiveDataQueries) {
            true -> liveDataList()
            else -> emptyList()
        }
        rxQueries + liveDataQueries

    }
}

private fun generateGetAllOrderBy(params: AutoDaoParams): List<FunSpec> {
    fun singleList() = params.attributes.flatMap {
        listOf(
                getAllOrderByAttrAscSingle_(params.tableName, params.entityType, it, params),
                getAllOrderByAttrDescSingle_(params.tableName, params.entityType, it, params),
                getAllOrderByAttrAscLimitSingle_(params.tableName, params.entityType, it, params),
                getAllOrderByAttrDescLimitSingle_(params.tableName, params.entityType, it, params),
                getAllOrderedByAttrAsSingle(params, it)
        )
    }

    fun maybeList() = params.attributes.flatMap {
        listOf(
                getAllOrderByAttrAscMaybe_(params.tableName, params.entityType, it, params),
                getAllOrderByAttrDescMaybe_(params.tableName, params.entityType, it, params),
                getAllOrderByAttrAscLimitMaybe_(params.tableName, params.entityType, it, params),
                getAllOrderByAttrDescLimitMaybe_(params.tableName, params.entityType, it, params),
                getAllOrderedByAttrAsMaybe(params, it)
        )
    }

    fun flowableList() = params.attributes.flatMap {
        listOf(
                getAllOrderByAttrAscFlowable_(params.tableName, params.entityType, it, params),
                getAllOrderByAttrDescFlowable_(params.tableName, params.entityType, it, params),
                getAllOrderByAttrAscLimitFlowable_(params.tableName, params.entityType, it, params),
                getAllOrderByAttrDescLimitFlowable_(params.tableName, params.entityType, it, params),
                getAllOrderedByAttrAsFlowable(params, it)
        )
    }

    fun liveDataList() = params.attributes.flatMap {
        listOf(
                getAllOrderByAttrAscLiveData_(params.tableName, params.entityType, it, params),
                getAllOrderByAttrDescLiveData_(params.tableName, params.entityType, it, params),
                getAllOrderByAttrAscLimitLiveData_(params.tableName, params.entityType, it, params),
                getAllOrderByAttrDescLimitLiveData_(params.tableName, params.entityType, it, params),
                getAllOrderedByAttrAsLiveData(params, it)
        )
    }

    return if (params.generateOrderBy) {
        if (params.generateOnlyDefaultReturnType) {
            //
            // generate only default type
            when (params.defaultReturnType) {
                singleType -> singleList()
                maybeType -> maybeList()
                flowableType -> flowableList()
                liveDataType -> liveDataList()
                else -> throw IllegalArgumentException(
                        "Unsupported type ${params.defaultReturnType} for defaultRxReturnType"
                )
            }
        } else {
            //
            // Generate all
            val rxQueries = when (params.generateRxQueries) {
                true -> singleList() + maybeList() + flowableList()
                else -> emptyList()
            }
            val liveDataQueries = when (params.generateLiveDataQueries) {
                true -> liveDataList()
                else -> emptyList()
            }
            rxQueries + liveDataQueries
        }
    } else {
        emptyList()
    }
}

private fun generateGetByAttr(params: AutoDaoParams): List<FunSpec> {
    fun singleList() = params.attributes.flatMap { attrToGet ->
        listOf(
                getByAttrsAsSingle_(params.tableName, params.entityType, attrToGet, params),
                getByAttrsLimitAsSingle_(params.tableName, params.entityType, attrToGet, params),
                getByAttrsAsSingle(params, attrToGet)
        )
    }

    fun maybeList() = params.attributes.flatMap { attrToGet ->
        listOf(
                getByAttrsAsMaybe_(params.tableName, params.entityType, attrToGet, params),
                getByAttrsLimitAsMaybe_(params.tableName, params.entityType, attrToGet, params),
                getByAttrsAsMaybe(params, attrToGet)
        )
    }

    fun flowableList() = params.attributes.flatMap { attrToGet ->
        listOf(
                getByAttrsAsFlowable_(params.tableName, params.entityType, attrToGet, params),
                getByAttrsLimitAsFlowable_(params.tableName, params.entityType, attrToGet, params),
                getByAttrsAsFlowable(params, attrToGet)
        )
    }

    fun liveDataList() = params.attributes.flatMap { attrToGet ->
        listOf(
                getByAttrsAsLiveData_(params.tableName, params.entityType, attrToGet, params),
                getByAttrsLimitAsLiveData_(params.tableName, params.entityType, attrToGet, params),
                getByAttrsAsLiveData(params, attrToGet)
        )
    }
    return if (params.generateOnlyDefaultReturnType) {
        when (params.defaultReturnType) {
            singleType -> singleList()
            maybeType -> maybeList()
            flowableType -> flowableList()
            liveDataType -> liveDataList()
            else -> throw IllegalArgumentException(
                    "Unsupported type ${params.defaultReturnType} for defaultRxReturnType"
            )
        }
    } else {
        //
        // Generate all
        val rxQueries = when (params.generateRxQueries) {
            true -> singleList() + maybeList() + flowableList()
            else -> emptyList()
        }
        val liveDataQueries = when (params.generateLiveDataQueries) {
            true -> liveDataList()
            else -> emptyList()
        }
        rxQueries + liveDataQueries
    }
}

private fun generateGetByAttrOrderByAttr(params: AutoDaoParams): List<FunSpec> {
    fun singleList() = params.attributes.flatMap { attrToGet ->
        params.attributes.flatMap { orderByAttr ->
            listOf(
                    getByAttrsOrderedByAttrAscSingle_(params.tableName, params.entityType, attrToGet, orderByAttr, params),
                    getByAttrsOrderedByAttrDescSingle_(params.tableName, params.entityType, attrToGet, orderByAttr, params),
                    getByAttrsOrderByAttrAscLimitSingle_(params.tableName, params.entityType, attrToGet, orderByAttr, params),
                    getByAttrsOrderByAttrDescLimitSingle_(params.tableName, params.entityType, attrToGet, orderByAttr, params),
                    getByAttrsOrderedByAsSingle(params, attrToGet, orderByAttr)
            )
        }
    }

    fun maybeList() = params.attributes.flatMap { attrToGet ->
        params.attributes.flatMap { orderByAttr ->
            listOf(
                    getByAttrsOrderedByAttrAscMaybe_(params.tableName, params.entityType, attrToGet, orderByAttr, params),
                    getByAttrsOrderedByAttrDescMaybe_(params.tableName, params.entityType, attrToGet, orderByAttr, params),
                    getByAttrsOrderByAttrAscLimitMaybe_(params.tableName, params.entityType, attrToGet, orderByAttr, params),
                    getByAttrsOrderByAttrDescLimitMaybe_(params.tableName, params.entityType, attrToGet, orderByAttr, params),
                    getByAttrsOrderedByAsMaybe(params, attrToGet, orderByAttr)
            )
        }
    }

    fun flowableList() = params.attributes.flatMap { attrToGet ->
        params.attributes.flatMap { orderByAttr ->
            listOf(
                    getByAttrsOrderedByAttrAscFlowable_(params.tableName, params.entityType, attrToGet, orderByAttr, params),
                    getByAttrsOrderedByAttrDescFlowable_(params.tableName, params.entityType, attrToGet, orderByAttr, params),
                    getByAttrsOrderByAttrAscLimitFlowable_(params.tableName, params.entityType, attrToGet, orderByAttr, params),
                    getByAttrsOrderByAttrDescLimitFlowable_(params.tableName, params.entityType, attrToGet, orderByAttr, params),
                    getByAttrsOrderedByAsFlowable(params, attrToGet, orderByAttr)
            )
        }
    }

    fun liveDataList() = params.attributes.flatMap { attrToGet ->
        params.attributes.flatMap { orderByAttr ->
            listOf(
                    getByAttrsOrderedByAttrAscLiveData_(params.tableName, params.entityType, attrToGet, orderByAttr, params),
                    getByAttrsOrderedByAttrDescLiveData_(params.tableName, params.entityType, attrToGet, orderByAttr, params),
                    getByAttrsOrderByAttrAscLimitLiveData_(params.tableName, params.entityType, attrToGet, orderByAttr, params),
                    getByAttrsOrderByAttrDescLimitLiveData_(params.tableName, params.entityType, attrToGet, orderByAttr, params),
                    getByAttrsOrderedByAsLiveData(params, attrToGet, orderByAttr)
            )
        }
    }

    return if (params.generateOrderBy) {
        if (params.generateOnlyDefaultReturnType) {
            when (params.defaultReturnType) {
                singleType -> singleList()
                maybeType -> maybeList()
                flowableType -> flowableList()
                liveDataType -> liveDataList()
                else -> throw IllegalArgumentException(
                        "Unsupported type ${params.defaultReturnType} for defaultRxReturnType"
                )
            }
        } else {
            //
            // Generate all
            val rxQueries = when (params.generateRxQueries) {
                true -> singleList() + maybeList() + flowableList()
                else -> emptyList()
            }
            val liveDataQueries = when (params.generateLiveDataQueries) {
                true -> liveDataList()
                else -> emptyList()
            }
            rxQueries + liveDataQueries
        }
    } else {
        emptyList()
    }
}


private fun nativeRoomQueries(params: AutoDaoParams): List<FunSpec> {
    // we dont want to generate insert/update/delete if it contains @Relation
    return when (params.containsRelationAnnotation) {
        true -> emptyList()
        else -> run {
            val blockingQueries = when (params.generateBlockingQueries) {
                true -> listOf(
                        //
                        // Native room annotations (Blocking)
                        //
                        insertObjBlocking(params),
                        insertObjsBlocking(params),
                        updateObjBlocking(params),
                        deleteObjBlocking(params),
                        deleteObjsBlocking(params),
                        deleteAllBlocking(params)
                )
                else -> emptyList()
            }
            val rxQueries = when (params.generateRxQueries) {
                true -> listOf(
                        //
                        // Native room annotations (Rx)
                        //
                        insertObjRx(params),
                        insertObjsRx(params),
                        updateObjRx(params),
                        deleteObjRx(params),
                        deleteObjsRx(params),
                        deleteAllRx()
                )
                else -> emptyList()
            }
            blockingQueries + rxQueries

        }
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
        .apply {
            if (params.containsRelationAnnotation) {
                addAnnotation(RoomAnnotationClassName.transaction())
            }
            addAnnotation(RoomAnnotationClassName.query(
                    "\"SELECT * FROM ${params.tableName}\""
            ))
            addModifiers(KModifier.PROTECTED, KModifier.ABSTRACT)
            returns(returnType)
        }
        .build()

private fun getAllLimit_(functionName: String,
                         returnType: ParameterizedTypeName,
                         params: AutoDaoParams) = FunSpec
        .builder(functionName)
        .apply {
            if (params.containsRelationAnnotation) {
                addAnnotation(RoomAnnotationClassName.transaction())
            }
            addAnnotation(RoomAnnotationClassName.query(
                    "\"SELECT * FROM ${params.tableName} LIMIT :limit\""
            ))
            addParameter("limit", Int::class)
            addModifiers(KModifier.PROTECTED, KModifier.ABSTRACT)
            returns(returnType)
        }
        .build()

private fun getAllLiveData_(params: AutoDaoParams) =
        getAll_("getAllAsLiveData_",
                liveDataType.parameterizedBy(listType.parameterizedBy(params.entityType)),
                params
        )

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

private fun getAllLimitLiveData_(params: AutoDaoParams) = getAllLimit_(
        "getAllAsLiveData_",
        liveDataType.parameterizedBy(listType.parameterizedBy(params.entityType)),
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
        if (params.defaultReturnType == singleType) "getAll"
        else "getAllAsSingle",
        privateFunctionName = "getAllAsSingle",
        returnType = singleType.parameterizedBy(listType.parameterizedBy(params.entityType))
)

private fun getAllMaybe(params: AutoDaoParams) = getAll(
        publicFunctionName =
        if (params.defaultReturnType == maybeType) "getAll"
        else "getAllAsMaybe",
        privateFunctionName = "getAllAsMaybe",
        returnType = maybeType.parameterizedBy(listType.parameterizedBy(params.entityType))
)

private fun getAllFlowable(params: AutoDaoParams) = getAll(
        publicFunctionName =
        if (params.defaultReturnType == flowableType) "getAll"
        else "getAllAsFlowable",
        privateFunctionName = "getAllAsFlowable",
        returnType = flowableType.parameterizedBy(listType.parameterizedBy(params.entityType)),
        addAutoThreadParam = false
)

private fun getAllLiveData(params: AutoDaoParams) = getAll(
        publicFunctionName =
        if (params.defaultReturnType == liveDataType) "getAll"
        else "getAllAsLiveData",
        privateFunctionName = "getAllAsLiveData",
        returnType = liveDataType.parameterizedBy(listType.parameterizedBy(params.entityType)),
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
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) = FunSpec
        .builder(functionName)
        .apply {
            if (params.containsRelationAnnotation) {
                addAnnotation(RoomAnnotationClassName.transaction())
            }
            addAnnotation(RoomAnnotationClassName.query(
                    "\"SELECT * FROM $tableName ORDER BY ${attr.collumnName} $order\""
            ))
            addModifiers(KModifier.PROTECTED, KModifier.ABSTRACT)
            returns(returnType)
        }
        .build()


private fun getAllOrderByAttrSingle_(
        tableName: String,
        order: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) = getAllOrderByAttr_(
        functionName = "getAllOrderBy${attr.name.capitalize()}AsSingle${order.toLowerCase().capitalize()}_",
        returnType = singleType.parameterizedBy(listType.parameterizedBy(modelType)),
        tableName = tableName,
        order = order,
        attr = attr,
        params = params
)

private fun getAllOrderByAttrMaybe_(
        tableName: String,
        order: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) = getAllOrderByAttr_(
        functionName = "getAllOrderBy${attr.name.capitalize()}AsMaybe${order.toLowerCase().capitalize()}_",
        returnType = maybeType.parameterizedBy(listType.parameterizedBy(modelType)),
        tableName = tableName,
        order = order,
        attr = attr,
        params = params
)

private fun getAllOrderByAttrLiveData_(
        tableName: String,
        order: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) = getAllOrderByAttr_(
        functionName = "getAllOrderBy${attr.name.capitalize()}AsLiveData${order.toLowerCase().capitalize()}_",
        returnType = liveDataType.parameterizedBy(listType.parameterizedBy(modelType)),
        tableName = tableName,
        order = order,
        attr = attr,
        params = params
)

private fun getAllOrderByAttrFlowable_(
        tableName: String,
        order: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) = getAllOrderByAttr_(
        functionName = "getAllOrderBy${attr.name.capitalize()}AsFlowable${order.toLowerCase().capitalize()}_",
        returnType = flowableType.parameterizedBy(listType.parameterizedBy(modelType)),
        tableName = tableName,
        order = order,
        attr = attr,
        params = params
)

private fun getAllOrderByAttrLimit_(
        functionName: String,
        returnType: ParameterizedTypeName,
        tableName: String,
        order: String,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) = FunSpec
        .builder(functionName)
        .apply {
            addParameter("limit", Int::class)
            if (params.containsRelationAnnotation) {
                addAnnotation(RoomAnnotationClassName.transaction())
            }
            addAnnotation(RoomAnnotationClassName.query(
                    "\"SELECT * FROM $tableName ORDER BY ${attr.collumnName} $order LIMIT :limit\""
            ))
            addModifiers(KModifier.PROTECTED, KModifier.ABSTRACT)
            returns(returnType)
        }
        .build()

private fun getAllOrderByAttrLimitSingle_(
        tableName: String,
        order: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getAllOrderByAttrLimit_(
                functionName = "getAllOrderBy${attr.name.capitalize()}AsSingle${order.toLowerCase().capitalize()}_",
                returnType = singleType.parameterizedBy(listType.parameterizedBy(modelType)),
                tableName = tableName,
                order = order,
                attr = attr,
                params = params
        )

private fun getAllOrderByAttrLimitMaybe_(
        tableName: String,
        order: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getAllOrderByAttrLimit_(
                functionName = "getAllOrderBy${attr.name.capitalize()}AsMaybe${order.toLowerCase().capitalize()}_",
                returnType = maybeType.parameterizedBy(listType.parameterizedBy(modelType)),
                tableName = tableName,
                order = order,
                attr = attr,
                params = params
        )

private fun getAllOrderByAttrLimitLiveData_(
        tableName: String,
        order: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getAllOrderByAttrLimit_(
                functionName = "getAllOrderBy${attr.name.capitalize()}AsLiveData${order.toLowerCase().capitalize()}_",
                returnType = liveDataType.parameterizedBy(listType.parameterizedBy(modelType)),
                order = order,
                tableName = tableName,
                attr = attr,
                params = params
        )

private fun getAllOrderByAttrLimitFlowable_(
        tableName: String,
        order: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getAllOrderByAttrLimit_(
                functionName = "getAllOrderBy${attr.name.capitalize()}AsFlowable${order.toLowerCase().capitalize()}_",
                returnType = flowableType.parameterizedBy(listType.parameterizedBy(modelType)),
                order = order,
                tableName = tableName,
                attr = attr,
                params = params
        )

private fun getAllOrderByAttrAscSingle_(
        tableName: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getAllOrderByAttrSingle_(
                order = "ASC",
                tableName = tableName,
                modelType = modelType,
                attr = attr,
                params = params
        )

private fun getAllOrderByAttrDescSingle_(
        tableName: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getAllOrderByAttrSingle_(
                order = "DESC",
                tableName = tableName,
                modelType = modelType,
                attr = attr,
                params = params
        )

private fun getAllOrderByAttrAscMaybe_(
        tableName: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getAllOrderByAttrMaybe_(
                order = "ASC",
                tableName = tableName,
                modelType = modelType,
                attr = attr,
                params = params
        )

private fun getAllOrderByAttrDescMaybe_(
        tableName: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getAllOrderByAttrMaybe_(
                order = "DESC",
                tableName = tableName,
                modelType = modelType,
                attr = attr,
                params = params
        )

private fun getAllOrderByAttrAscLiveData_(
        tableName: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getAllOrderByAttrLiveData_(
                order = "ASC",
                tableName = tableName,
                modelType = modelType,
                attr = attr,
                params = params
        )

private fun getAllOrderByAttrDescLiveData_(
        tableName: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getAllOrderByAttrLiveData_(
                order = "DESC",
                tableName = tableName,
                modelType = modelType,
                attr = attr,
                params = params
        )

private fun getAllOrderByAttrAscFlowable_(
        tableName: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getAllOrderByAttrFlowable_(
                order = "ASC",
                tableName = tableName,
                modelType = modelType,
                attr = attr,
                params = params
        )

private fun getAllOrderByAttrDescFlowable_(
        tableName: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getAllOrderByAttrFlowable_(
                order = "DESC",
                tableName = tableName,
                modelType = modelType,
                attr = attr,
                params = params
        )

private fun getAllOrderByAttrAscLimitSingle_(
        tableName: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getAllOrderByAttrLimitSingle_(
                order = "ASC",
                tableName = tableName,
                modelType = modelType,
                attr = attr,
                params = params
        )

private fun getAllOrderByAttrDescLimitSingle_(
        tableName: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getAllOrderByAttrLimitSingle_(
                order = "DESC",
                tableName = tableName,
                modelType = modelType,
                attr = attr,
                params = params
        )

private fun getAllOrderByAttrAscLimitMaybe_(
        tableName: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getAllOrderByAttrLimitMaybe_(
                order = "ASC",
                tableName = tableName,
                modelType = modelType,
                attr = attr,
                params = params
        )

private fun getAllOrderByAttrDescLimitMaybe_(
        tableName: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getAllOrderByAttrLimitMaybe_(
                order = "DESC",
                tableName = tableName,
                modelType = modelType,
                attr = attr,
                params = params
        )

private fun getAllOrderByAttrAscLimitLiveData_(
        tableName: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getAllOrderByAttrLimitLiveData_(
                order = "ASC",
                tableName = tableName,
                modelType = modelType,
                attr = attr,
                params = params
        )

private fun getAllOrderByAttrDescLimitLiveData_(
        tableName: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getAllOrderByAttrLimitLiveData_(
                order = "DESC",
                tableName = tableName,
                modelType = modelType,
                attr = attr,
                params = params
        )

private fun getAllOrderByAttrAscLimitFlowable_(
        tableName: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getAllOrderByAttrLimitFlowable_(
                order = "ASC",
                tableName = tableName,
                modelType = modelType,
                attr = attr,
                params = params
        )

private fun getAllOrderByAttrDescLimitFlowable_(
        tableName: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getAllOrderByAttrLimitFlowable_(
                order = "DESC",
                tableName = tableName,
                modelType = modelType,
                attr = attr,
                params = params
        )

private fun getAllOrderedByAttr(
        publicFunctionName: String,
        privateFunctionName: String,
        returnType: ParameterizedTypeName,
        addAutoThreadParam: Boolean = true
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
        .returns(returnType)
        .apply {
            val statement = "" +
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
                    ""
            if (addAutoThreadParam) {
                addParameter(autoThreadParam).concatIoThreadStatementWith(statement)
            } else {
                addStatement(statement)
            }
        }
        .build()


private fun getAllOrderedByAttrAsSingle(params: AutoDaoParams, attr: AutoDaoParams.Attr) =
        getAllOrderedByAttr(
                publicFunctionName = when (singleType) {
                    params.defaultReturnType -> "getAllOrderedBy${attr.name.capitalize()}"
                    else -> "getAllOrderedBy${attr.name.capitalize()}AsSingle"
                },
                privateFunctionName = "getAllOrderBy${attr.name.capitalize()}AsSingle",
                returnType = singleType.parameterizedBy(listType.parameterizedBy(params.entityType))
        )

private fun getAllOrderedByAttrAsMaybe(params: AutoDaoParams, attr: AutoDaoParams.Attr) =
        getAllOrderedByAttr(
                publicFunctionName = when (maybeType) {
                    params.defaultReturnType -> "getAllOrderedBy${attr.name.capitalize()}"
                    else -> "getAllOrderedBy${attr.name.capitalize()}AsMaybe"
                },
                privateFunctionName = "getAllOrderBy${attr.name.capitalize()}AsMaybe",
                returnType = maybeType.parameterizedBy(listType.parameterizedBy(params.entityType))
        )

private fun getAllOrderedByAttrAsFlowable(params: AutoDaoParams, attr: AutoDaoParams.Attr) =
        getAllOrderedByAttr(
                publicFunctionName = when (flowableType) {
                    params.defaultReturnType -> "getAllOrderedBy${attr.name.capitalize()}"
                    else -> "getAllOrderedBy${attr.name.capitalize()}AsFlowable"
                },
                privateFunctionName = "getAllOrderBy${attr.name.capitalize()}AsFlowable",
                returnType = flowableType.parameterizedBy(listType.parameterizedBy(params.entityType)),
                addAutoThreadParam = false
        )

private fun getAllOrderedByAttrAsLiveData(params: AutoDaoParams, attr: AutoDaoParams.Attr) =
        getAllOrderedByAttr(
                publicFunctionName = when (liveDataType) {
                    params.defaultReturnType -> "getAllOrderedBy${attr.name.capitalize()}"
                    else -> "getAllOrderedBy${attr.name.capitalize()}AsLiveData"
                },
                privateFunctionName = "getAllOrderBy${attr.name.capitalize()}AsLiveData",
                returnType = liveDataType.parameterizedBy(listType.parameterizedBy(params.entityType)),
                addAutoThreadParam = false
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
        attrToOrderBy: AutoDaoParams.Attr,
        params: AutoDaoParams
) = FunSpec
        .builder(functionName)
        .apply {
            if (params.containsRelationAnnotation) {
                addAnnotation(RoomAnnotationClassName.transaction())
            }
            addAnnotation(RoomAnnotationClassName.query(
                    "\"SELECT * FROM $tableName WHERE ${attrToGetBy.collumnName} IN (:${attrToGetBy.name}s) ORDER BY ${attrToOrderBy.collumnName} $order\""
            ))
            addParameter("${attrToGetBy.name}s", attrToGetBy.type.javaToKotlinType(), KModifier.VARARG)
            addModifiers(KModifier.PROTECTED, KModifier.ABSTRACT)
            returns(returnType)
        }
        .build()

private fun getByAttrsOrderByAttrLimit_(
        functionName: String,
        returnType: ParameterizedTypeName,
        tableName: String,
        order: String,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr,
        params: AutoDaoParams
) = FunSpec
        .builder(functionName)
        .apply {
            if (params.containsRelationAnnotation) {
                addAnnotation(RoomAnnotationClassName.transaction())
            }
            addAnnotation(RoomAnnotationClassName.query(
                    "\"SELECT * FROM $tableName WHERE ${attrToGetBy.collumnName} IN (:${attrToGetBy.name}s) ORDER BY ${attrToOrderBy.collumnName} $order LIMIT :limit\""
            ))
            addParameter("${attrToGetBy.name}s", attrToGetBy.type.javaToKotlinType(), KModifier.VARARG)
            addParameter("limit", Int::class)
            addModifiers(KModifier.PROTECTED, KModifier.ABSTRACT)
            returns(returnType)
        }
        .build()

private fun getByAttrsOrderedByAttrSingle_(
        tableName: String,
        order: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr,
        params: AutoDaoParams
) = getByAttrsOrderByAttr_(
        functionName = "${getByAttrOrderByName(attrToGetBy, attrToOrderBy)}AsSingle${order.toLowerCase().capitalize()}_",
        returnType = singleType.parameterizedBy(listType.parameterizedBy(modelType)),
        tableName = tableName,
        order = order,
        attrToGetBy = attrToGetBy,
        attrToOrderBy = attrToOrderBy,
        params = params
)

private fun getByAttrsOrderedByAttrMaybe_(
        tableName: String,
        order: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr,
        params: AutoDaoParams
) = getByAttrsOrderByAttr_(
        functionName = "${getByAttrOrderByName(attrToGetBy, attrToOrderBy)}AsMaybe${order.toLowerCase().capitalize()}_",
        returnType = maybeType.parameterizedBy(listType.parameterizedBy(modelType)),
        tableName = tableName,
        order = order,
        attrToGetBy = attrToGetBy,
        attrToOrderBy = attrToOrderBy,
        params = params
)

private fun getByAttrsOrderedByAttrFlowable_(
        tableName: String,
        order: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr,
        params: AutoDaoParams
) = getByAttrsOrderByAttr_(
        functionName = "${getByAttrOrderByName(attrToGetBy, attrToOrderBy)}AsFlowable${order.toLowerCase().capitalize()}_",
        returnType = flowableType.parameterizedBy(listType.parameterizedBy(modelType)),
        tableName = tableName,
        order = order,
        attrToGetBy = attrToGetBy,
        attrToOrderBy = attrToOrderBy,
        params = params
)

private fun getByAttrsOrderedByAttrLiveData_(
        tableName: String,
        order: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr,
        params: AutoDaoParams
) = getByAttrsOrderByAttr_(
        functionName = "${getByAttrOrderByName(attrToGetBy, attrToOrderBy)}AsLiveData${order.toLowerCase().capitalize()}_",
        returnType = liveDataType.parameterizedBy(listType.parameterizedBy(modelType)),
        tableName = tableName,
        order = order,
        attrToGetBy = attrToGetBy,
        attrToOrderBy = attrToOrderBy,
        params = params
)

private fun getByAttrsOrderedByAttrSingleLimit_(
        tableName: String,
        order: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getByAttrsOrderByAttrLimit_(
                functionName = "${getByAttrOrderByName(attrToGetBy, attrToOrderBy)}AsSingle${order.toLowerCase().capitalize()}_",
                returnType = singleType.parameterizedBy(listType.parameterizedBy(modelType)),
                tableName = tableName,
                order = order,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy,
                params = params
        )

private fun getByAttrsOrderedByAttrMaybeLimit_(
        tableName: String,
        order: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getByAttrsOrderByAttrLimit_(
                functionName = "${getByAttrOrderByName(attrToGetBy, attrToOrderBy)}AsMaybe${order.toLowerCase().capitalize()}_",
                returnType = maybeType.parameterizedBy(listType.parameterizedBy(modelType)),
                tableName = tableName,
                order = order,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy,
                params = params
        )

private fun getByAttrsOrderedByAttrFlowableLimit_(
        tableName: String,
        order: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getByAttrsOrderByAttrLimit_(
                functionName = "${getByAttrOrderByName(attrToGetBy, attrToOrderBy)}AsFlowable${order.toLowerCase().capitalize()}_",
                returnType = flowableType.parameterizedBy(listType.parameterizedBy(modelType)),
                tableName = tableName,
                order = order,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy,
                params = params
        )

private fun getByAttrsOrderedByAttrLiveDataLimit_(
        tableName: String,
        order: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getByAttrsOrderByAttrLimit_(
                functionName = "${getByAttrOrderByName(attrToGetBy, attrToOrderBy)}AsLiveData${order.toLowerCase().capitalize()}_",
                returnType = liveDataType.parameterizedBy(listType.parameterizedBy(modelType)),
                tableName = tableName,
                order = order,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy,
                params = params
        )

private fun getByAttrsOrderedByAttrAscSingle_(
        tableName: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getByAttrsOrderedByAttrSingle_(
                order = "ASC",
                tableName = tableName,
                modelType = modelType,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy,
                params = params
        )

private fun getByAttrsOrderedByAttrDescSingle_(
        tableName: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getByAttrsOrderedByAttrSingle_(
                order = "DESC",
                tableName = tableName,
                modelType = modelType,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy,
                params = params
        )

private fun getByAttrsOrderedByAttrAscMaybe_(
        tableName: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getByAttrsOrderedByAttrMaybe_(
                order = "ASC",
                tableName = tableName,
                modelType = modelType,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy,
                params = params
        )

private fun getByAttrsOrderedByAttrDescMaybe_(
        tableName: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getByAttrsOrderedByAttrMaybe_(
                order = "DESC",
                tableName = tableName,
                modelType = modelType,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy,
                params = params
        )

private fun getByAttrsOrderedByAttrAscFlowable_(
        tableName: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getByAttrsOrderedByAttrFlowable_(
                order = "ASC",
                tableName = tableName,
                modelType = modelType,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy,
                params = params
        )

private fun getByAttrsOrderedByAttrDescFlowable_(
        tableName: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getByAttrsOrderedByAttrFlowable_(
                order = "DESC",
                tableName = tableName,
                modelType = modelType,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy,
                params = params
        )

private fun getByAttrsOrderedByAttrAscLiveData_(
        tableName: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getByAttrsOrderedByAttrLiveData_(
                order = "ASC",
                tableName = tableName,
                modelType = modelType,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy,
                params = params
        )

private fun getByAttrsOrderedByAttrDescLiveData_(
        tableName: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getByAttrsOrderedByAttrLiveData_(
                order = "DESC",
                tableName = tableName,
                modelType = modelType,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy,
                params = params
        )


private fun getByAttrsOrderByAttrAscLimitSingle_(
        tableName: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getByAttrsOrderedByAttrSingleLimit_(
                order = "ASC",
                tableName = tableName,
                modelType = modelType,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy,
                params = params
        )

private fun getByAttrsOrderByAttrDescLimitSingle_(
        tableName: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getByAttrsOrderedByAttrSingleLimit_(
                order = "DESC",
                tableName = tableName,
                modelType = modelType,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy,
                params = params
        )

private fun getByAttrsOrderByAttrAscLimitMaybe_(
        tableName: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getByAttrsOrderedByAttrMaybeLimit_(
                order = "ASC",
                tableName = tableName,
                modelType = modelType,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy,
                params = params
        )

private fun getByAttrsOrderByAttrDescLimitMaybe_(
        tableName: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getByAttrsOrderedByAttrMaybeLimit_(
                order = "DESC",
                tableName = tableName,
                modelType = modelType,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy,
                params = params
        )

private fun getByAttrsOrderByAttrAscLimitFlowable_(
        tableName: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getByAttrsOrderedByAttrFlowableLimit_(
                order = "ASC",
                tableName = tableName,
                modelType = modelType,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy,
                params = params
        )

private fun getByAttrsOrderByAttrDescLimitFlowable_(
        tableName: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getByAttrsOrderedByAttrFlowableLimit_(
                order = "DESC",
                tableName = tableName,
                modelType = modelType,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy,
                params = params
        )

private fun getByAttrsOrderByAttrAscLimitLiveData_(
        tableName: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getByAttrsOrderedByAttrLiveDataLimit_(
                order = "ASC",
                tableName = tableName,
                modelType = modelType,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy,
                params = params
        )

private fun getByAttrsOrderByAttrDescLimitLiveData_(
        tableName: String,
        modelType: TypeName,
        attrToGetBy: AutoDaoParams.Attr,
        attrToOrderBy: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getByAttrsOrderedByAttrLiveDataLimit_(
                order = "DESC",
                tableName = tableName,
                modelType = modelType,
                attrToGetBy = attrToGetBy,
                attrToOrderBy = attrToOrderBy,
                params = params
        )

private fun getByAttrsOrderedByAttr(
        publicFunctionName: String,
        privateFunctionName: String,
        returnType: ParameterizedTypeName,
        attrToGetBy: AutoDaoParams.Attr,
        addAutoThreadParam: Boolean = true
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
        .apply {
            val statement = "" +
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
                    ""
            if (addAutoThreadParam) {
                addParameter(autoThreadParam).concatIoThreadStatementWith(statement)
            } else {
                addStatement(statement)
            }
        }
        .returns(returnType)
        .build()

private fun getByAttrsOrderedByAsSingle(params: AutoDaoParams,
                                        attrToGetBy: AutoDaoParams.Attr,
                                        attrToOrderBy: AutoDaoParams.Attr

) = getByAttrsOrderedByAttr(
        publicFunctionName = when (singleType) {
            params.defaultReturnType -> getByAttrOrderByName(attrToGetBy, attrToOrderBy)
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
        publicFunctionName = when (maybeType) {
            params.defaultReturnType -> getByAttrOrderByName(attrToGetBy, attrToOrderBy)
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
        publicFunctionName = when (flowableType) {
            params.defaultReturnType -> getByAttrOrderByName(attrToGetBy, attrToOrderBy)
            else -> "${getByAttrOrderByName(attrToGetBy, attrToOrderBy)}AsFlowable"
        },
        privateFunctionName = "${getByAttrOrderByName(attrToGetBy, attrToOrderBy)}AsFlowable",
        returnType = flowableType.parameterizedBy(listType.parameterizedBy(params.entityType)),
        attrToGetBy = attrToGetBy,
        addAutoThreadParam = false
)

private fun getByAttrsOrderedByAsLiveData(params: AutoDaoParams,
                                          attrToGetBy: AutoDaoParams.Attr,
                                          attrToOrderBy: AutoDaoParams.Attr
) = getByAttrsOrderedByAttr(
        publicFunctionName = when (liveDataType) {
            params.defaultReturnType -> getByAttrOrderByName(attrToGetBy, attrToOrderBy)
            else -> "${getByAttrOrderByName(attrToGetBy, attrToOrderBy)}AsLiveData"
        },
        privateFunctionName = "${getByAttrOrderByName(attrToGetBy, attrToOrderBy)}AsLiveData",
        returnType = liveDataType.parameterizedBy(listType.parameterizedBy(params.entityType)),
        attrToGetBy = attrToGetBy,
        addAutoThreadParam = false
)


////////////////////////////////////////////////////////////////////////////////////////////////////
// Get By Attr Name
////////////////////////////////////////////////////////////////////////////////////////////////////

private fun getByAttrName(attr: AutoDaoParams.Attr) = "getBy${attr.name.capitalize()}"

private fun getByAttrs_(
        functionName: String,
        returnType: ParameterizedTypeName,
        tableName: String,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) = FunSpec
        .builder(functionName)
        .apply {
            if (params.containsRelationAnnotation) {
                addAnnotation(RoomAnnotationClassName.transaction())
            }
            addAnnotation(RoomAnnotationClassName.query(
                    "\"SELECT * FROM $tableName WHERE ${attr.collumnName} IN (:${attr.name}s)\""
            ))
            addParameter("${attr.name}s", attr.type.javaToKotlinType(), KModifier.VARARG)
            addModifiers(KModifier.PROTECTED, KModifier.ABSTRACT)
            returns(returnType)
        }
        .build()

private fun getByAttrsAsSingle_(
        tableName: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getByAttrs_(
                "${getByAttrName(attr)}AsSingle_",
                singleType.parameterizedBy(listType.parameterizedBy(modelType)),
                tableName,
                attr,
                params
        )

private fun getByAttrsAsMaybe_(
        tableName: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getByAttrs_(
                "${getByAttrName(attr)}AsMaybe_",
                maybeType.parameterizedBy(listType.parameterizedBy(modelType)),
                tableName,
                attr,
                params
        )

private fun getByAttrsAsFlowable_(
        tableName: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getByAttrs_(
                "${getByAttrName(attr)}AsFlowable_",
                flowableType.parameterizedBy(listType.parameterizedBy(modelType)),
                tableName,
                attr,
                params
        )

private fun getByAttrsAsLiveData_(
        tableName: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getByAttrs_(
                "${getByAttrName(attr)}AsLiveData_",
                liveDataType.parameterizedBy(listType.parameterizedBy(modelType)),
                tableName,
                attr,
                params
        )

private fun getByAttrsLimit_(
        functionName: String,
        returnType: ParameterizedTypeName,
        tableName: String,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) = FunSpec
        .builder(functionName)
        .apply {
            if (params.containsRelationAnnotation) {
                addAnnotation(RoomAnnotationClassName.transaction())
            }
            addAnnotation(RoomAnnotationClassName.query(
                    "\"SELECT * FROM $tableName WHERE ${attr.collumnName} IN (:${attr.name}s) LIMIT :limit\""
            ))
            addParameter("${attr.name}s", attr.type.javaToKotlinType(), KModifier.VARARG)
            addParameter("limit", Int::class)
            addModifiers(KModifier.PROTECTED, KModifier.ABSTRACT)
            returns(returnType)
        }
        .build()

private fun getByAttrsLimitAsSingle_(
        tableName: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getByAttrsLimit_(
                "${getByAttrName(attr)}AsSingle_",
                singleType.parameterizedBy(listType.parameterizedBy(modelType)),
                tableName,
                attr,
                params
        )

private fun getByAttrsLimitAsMaybe_(
        tableName: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getByAttrsLimit_(
                "${getByAttrName(attr)}AsMaybe_",
                maybeType.parameterizedBy(listType.parameterizedBy(modelType)),
                tableName,
                attr,
                params
        )

private fun getByAttrsLimitAsFlowable_(
        tableName: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getByAttrsLimit_(
                "${getByAttrName(attr)}AsFlowable_",
                flowableType.parameterizedBy(listType.parameterizedBy(modelType)),
                tableName,
                attr,
                params
        )

private fun getByAttrsLimitAsLiveData_(
        tableName: String,
        modelType: TypeName,
        attr: AutoDaoParams.Attr,
        params: AutoDaoParams
) =
        getByAttrsLimit_(
                "${getByAttrName(attr)}AsLiveData_",
                liveDataType.parameterizedBy(listType.parameterizedBy(modelType)),
                tableName,
                attr,
                params
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
            params.defaultReturnType -> getByAttrName(attr)
            else -> "${getByAttrName(attr)}AsSingle"
        },
        privateFunctionName = "${getByAttrName(attr)}AsSingle",
        returnType = singleType.parameterizedBy(listType.parameterizedBy(params.entityType)),
        attr = attr
)

private fun getByAttrsAsMaybe(params: AutoDaoParams,
                              attr: AutoDaoParams.Attr) = getByAttrs(
        publicFunctionName = when (maybeType) {
            params.defaultReturnType -> getByAttrName(attr)
            else -> "${getByAttrName(attr)}AsMaybe"
        },
        privateFunctionName = "${getByAttrName(attr)}AsMaybe",
        returnType = maybeType.parameterizedBy(listType.parameterizedBy(params.entityType)),
        attr = attr
)

private fun getByAttrsAsFlowable(params: AutoDaoParams,
                                 attr: AutoDaoParams.Attr) = getByAttrs(
        publicFunctionName = when (flowableType) {
            params.defaultReturnType -> getByAttrName(attr)
            else -> "${getByAttrName(attr)}AsFlowable"
        },
        privateFunctionName = "${getByAttrName(attr)}AsFlowable",
        returnType = flowableType.parameterizedBy(listType.parameterizedBy(params.entityType)),
        attr = attr,
        addAutoThreadParam = false
)

private fun getByAttrsAsLiveData(params: AutoDaoParams,
                                 attr: AutoDaoParams.Attr) = getByAttrs(
        publicFunctionName = when (liveDataType) {
            params.defaultReturnType -> getByAttrName(attr)
            else -> "${getByAttrName(attr)}AsLiveData"
        },
        privateFunctionName = "${getByAttrName(attr)}AsLiveData",
        returnType = liveDataType.parameterizedBy(listType.parameterizedBy(params.entityType)),
        attr = attr,
        addAutoThreadParam = false
)