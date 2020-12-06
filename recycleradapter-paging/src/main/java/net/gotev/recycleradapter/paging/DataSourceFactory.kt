package net.gotev.recycleradapter.paging

import androidx.paging.DataSource
import net.gotev.recycleradapter.AdapterItem

internal class DataSourceFactory<T>(
    private val dataSource: () -> DataSource<*, AdapterItem<*>>
) : DataSource.Factory<T, AdapterItem<*>>() {

    @Suppress("UNCHECKED_CAST")
    override fun create(): DataSource<T, AdapterItem<*>> = dataSource() as DataSource<T, AdapterItem<*>>
}
