package net.gotev.recycleradapter.ext

import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapter.RecyclerAdapter
import java.util.ArrayList

/**
 * @author Aleksandar Gotev
 */

typealias AdapterItems = ArrayList<AdapterItem<*>>

fun createRecyclerAdapterWith(vararg items: AdapterItem<*>?): RecyclerAdapter {
    return RecyclerAdapter().apply {
        submitList(items.filterNotNull())
    }
}

fun createRecyclerAdapterWith(list: List<AdapterItem<*>?>?): RecyclerAdapter {
    return RecyclerAdapter().apply {
        submitList(list?.filterNotNull() ?: emptyList())
    }
}

fun adapterItems(vararg items: AdapterItem<*>?): AdapterItems {
    return ArrayList(listOfNotNull(*items))
}

inline fun <T> Iterable<T>.mapItems(transform: (T) -> AdapterItem<*>?): Array<AdapterItem<*>> {
    return mapNotNull(transform).toTypedArray()
}

inline fun <K, V> Map<out K, V>.mapToManyAdapterItems(transform: (Map.Entry<K, V>) -> List<AdapterItem<*>>): Array<AdapterItem<*>> {
    return map(transform).flatten().toTypedArray()
}

inline fun <T> Array<T>.mapToManyAdapterItems(transform: (T) -> List<AdapterItem<*>>): Array<AdapterItem<*>> {
    return map(transform).flatten().toTypedArray()
}

inline fun <T> Iterable<T>.mapToManyAdapterItems(transform: (T) -> List<AdapterItem<*>>): Array<AdapterItem<*>> {
    return map(transform).flatten().toTypedArray()
}

inline fun <T> Iterable<T>.createRecyclerAdapterByMapping(transform: (T) -> AdapterItem<*>?): RecyclerAdapter {
    return RecyclerAdapter().apply { submitList(mapToAdapterItems(transform)) }
}

inline fun <T> Iterable<T>.mapToAdapterItems(transform: (T) -> AdapterItem<*>?): AdapterItems {
    return ArrayList(mapNotNull(transform))
}

inline fun <T> Array<T>.mapToAdapterItems(transform: (T) -> AdapterItem<*>?): AdapterItems {
    return ArrayList(mapNotNull(transform))
}

inline fun <T, reified R> T?.mapToManyOrEmpty(block: (T) -> Array<R>): Array<R> {
    if (this == null) return emptyArray()
    return block(this)
}

inline fun <T, reified R> T?.applyOrEmpty(block: (T) -> R): Array<R> {
    if (this == null) return emptyArray()
    return arrayOf(block(this))
}

inline fun <T, reified R> Array<T>.mapEachOneToMany(transform: T.() -> List<R>): Array<R> {
    return map(transform).flatten().toTypedArray()
}

inline fun <T, reified R> Iterable<T>.mapEachOneToMany(transform: T.() -> List<R>): Array<R> {
    return map(transform).flatten().toTypedArray()
}

inline fun <T, reified R> Iterable<T>.mapEachOne(transform: T.() -> R): Array<R> {
    return map(transform).toTypedArray()
}

inline fun <reified T : Any> section(
    header: T? = null,
    items: List<T?>? = null,
    footer: T? = null
): Array<T> {
    val itemsArray = items?.mapNotNull { it }?.toTypedArray() ?: return emptyArray()

    return when {
        header != null && footer != null -> arrayOf(header, *itemsArray, footer)
        header != null && footer == null -> arrayOf(header, *itemsArray)
        header == null && footer != null -> arrayOf(*itemsArray, footer)
        else -> itemsArray
    }
}

interface RecyclerAdapterProvider {
    val recyclerAdapter: RecyclerAdapter

    fun AdapterItems.render() {
        recyclerAdapter.submitList(this)
    }

    fun render(vararg items: AdapterItem<*>?) {
        recyclerAdapter.submitList(items.filterNotNull())
    }

    fun render(list: List<AdapterItem<*>?>?) {
        recyclerAdapter.submitList(list?.filterNotNull() ?: emptyList())
    }
}
