package com.github.insanusmokrassar.AutoORM.core.drivers.tables.interfaces

import com.github.insanusmokrassar.AutoORM.core.drivers.tables.abstracts.SearchQueryCompiler

interface TableProvider<M : Any, out O : M> {
    fun insert(what : M) : Boolean
    fun update(than : M, where : SearchQueryCompiler<out Any>) : Boolean
    fun remove(where : SearchQueryCompiler<out Any>) : Boolean
    fun find(where : SearchQueryCompiler<out Any>) : Collection<O>
    fun getEmptyQuery() : SearchQueryCompiler<out Any>
}