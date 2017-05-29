package com.github.insanusmokrassar.AbstractDatabaseORM.drivers.jdbc

import com.github.insanusmokrassar.AbstractDatabaseORM.core.drivers.tables.abstracts.AbstractTableProvider
import com.github.insanusmokrassar.AbstractDatabaseORM.core.drivers.tables.interfaces.SearchQueryCompiler
import java.sql.Connection
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class JDBCTableProvider<M : Any, O : M>(
        modelClass: KClass<M>,
        operationsClass: KClass<in O>,
        val connection: Connection)
    : AbstractTableProvider<M, O>(
        modelClass,
        operationsClass) {

    init {
        val checkStatement = connection.prepareStatement("SELECT * FROM information_schema.tables WHERE table_name='${modelClass.simpleName}';")
        val resultSet = checkStatement.executeQuery()
        if (resultSet.next()) {
            TODO()
        } else {
            TODO()
        }
    }

    override fun remove(where: SearchQueryCompiler<out Any>): Boolean {
        if (where is JDBCSearchQueryCompiler) {
            val queryBuilder = StringBuilder().append("DELETE FROM ${modelClass.simpleName}${where.compileQuery()}${where.compilePaging()};")
            val statement = connection.prepareStatement(queryBuilder.toString())
            return statement.execute()
        } else{
            throw IllegalArgumentException("JDBC provider can't handle query compiler of other providers")
        }
    }

    override fun find(where: SearchQueryCompiler<out Any>): Collection<O> {
        if (where is JDBCSearchQueryCompiler) {
            val queryBuilder = StringBuilder().append("SELECT")
            if (where.getFields == null) {
                queryBuilder.append(" * ")
            } else {
                where.getFields!!.forEach {
                    queryBuilder.append(" $it")
                    if (where.getFields!!.indexOf(it) < where.getFields!!.size - 1) {
                        queryBuilder.append(",")
                    }
                }
            }
            queryBuilder.append("${where.compileQuery()}${where.compilePaging()};")

            val resultSet = connection.prepareStatement(queryBuilder.toString()).executeQuery()
            val result = ArrayList<O>()
            while (resultSet.next()) {
                val currentValuesMap = HashMap<KProperty<*>, Any>()
                variablesList.forEach {
                    currentValuesMap.put(it, resultSet.getObject(it.name, it.javaClass))
                }
                result.add(createModelFromValuesMap(currentValuesMap))
            }
            return result
        } else {
            throw IllegalArgumentException("JDBC provider can't handle query compiler of other providers")
        }
    }

    override fun getEmptyQuery(): SearchQueryCompiler<out Any> {
        return JDBCSearchQueryCompiler()
    }

    override fun insert(values: Map<KProperty<*>, Any>): Boolean {
        val queryBuilder = StringBuilder().append("INSERT INTO ${modelClass.simpleName}")
        val fieldsBuilder = StringBuilder()
        val valuesBuilder = StringBuilder()
        val valuesList = values.toList()
        valuesList.forEach {
            fieldsBuilder.append(it.first.name)
            valuesBuilder.append(it.second.toString())
            if (valuesList.indexOf(it) < valuesList.size - 1) {
                fieldsBuilder.append(",")
                valuesBuilder.append(",")
            }
        }
        queryBuilder.append(" ($fieldsBuilder) VALUES ($valuesBuilder);")
        val statement = connection.prepareStatement(queryBuilder.toString())
        return statement.execute()
    }

    override fun update(values: Map<KProperty<*>, Any>, where: SearchQueryCompiler<out Any>): Boolean {
        if (where is JDBCSearchQueryCompiler) {
            val queryBuilder = StringBuilder().append("UPDATE ${modelClass.simpleName} SET ")
            values.forEach {
                queryBuilder.append(" ${it.key.name} = ${it.value}")
            }
            queryBuilder.append("${where.compileQuery()}${where.compilePaging()};")
            val statement = connection.prepareStatement(queryBuilder.toString())
            return statement.execute()
        } else {
            throw IllegalArgumentException("JDBC provider can't handle query compiler of other providers")
        }
    }
}