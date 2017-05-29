package com.github.insanusmokrassar.AbstractDatabaseORM.drivers.jdbc

import com.github.insanusmokrassar.AbstractDatabaseORM.core.drivers.tables.interfaces.TableDriver
import com.github.insanusmokrassar.AbstractDatabaseORM.core.drivers.tables.interfaces.TableProvider
import java.sql.Connection
import kotlin.reflect.KClass

class JDBCTableDriver(private val connection: Connection) : TableDriver {
    override fun <M : Any, O : M> getTableProvider(modelClass: KClass<M>, operationsClass: KClass<in O>): TableProvider<M, O> {
        return JDBCTableProvider(modelClass, operationsClass, connection)
    }
}