package net.gotev.recycleradapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import java.lang.reflect.InvocationTargetException

/**
 * Abstract class to extend to create ViewHolders.
 * @author Aleksandar Gotev
 * @param <T> ViewHolder subclass
</T> */
abstract class AdapterItem<T : RecyclerAdapterViewHolder>(private val model: Any) : Comparable<AdapterItem<*>> {

    /**
     * Returns the identifier for this adapter item. Used in diffing operations.
     *
     * By overriding this, you don't need to override equals and hashCode, which are already
     * implemented for you. You should only override [hasToBeReplacedBy] method if you want to
     * further control if to replace an item with another one when their IDs matches.
     *
     * For example, if your adapter item model represents a person with those fields:
     * - uniqueId: String
     * - name: String
     * - surname: String
     *
     * what you have to do is:
     *
     * return javaClass.name + uniqueId
     *
     * If not overrided, by default it will return a combination of javaClass.name with hashcode
     * of the model instance passed in AdapterItem class constructor.
     *
     * javaClass.name (Kotlin) is needed to avoid collisions with other adapter items representing
     * the same model.
     */
    open fun diffingId(): String = model.javaClass.name + model.hashCode().toString()

    /**
     * Returns the layout ID for this item
     * @return layout ID
     */
    @Deprecated(
        message = "getLayoutId is deprecated. Use getView(parent: ViewGroup)",
        replaceWith = ReplaceWith("override fun getView(parent: ViewGroup): View = parent.inflating(yourLayoutId)"),
        level = DeprecationLevel.WARNING
    )
    open fun getLayoutId(): Int = 0

    fun ViewGroup.inflating(@LayoutRes layoutId: Int): View =
        LayoutInflater.from(context).inflate(layoutId, this, false)

    /**
     * Returns the view for this item
     * @param parent the parent ViewGroup, which is the current root, from which you can infer context
     */
    open fun getView(parent: ViewGroup): View = parent.inflating(getLayoutId())

    /**
     * Gets called for every item when the [RecyclerAdapter.filter] method gets called.
     * @param searchTerm term to search for
     * @return true if the items matches the search term, false otherwise
     */
    open fun onFilter(searchTerm: String): Boolean = true

    /**
     * Gets called when you perform [RecyclerAdapter.syncWithItems], specifically when
     * an item in the new list equals to this one (according to [AdapterItem.equals]
     * implementation). In this case, the item has to decide whether or not it should be replaced
     * by the new one. Generally this is useful when for example you have a person identified
     * uniquely by ID (equals returns true if two items have the same ID), but you want to update
     * the item only if the rest of the data has been changed.
     * If you return false, the item will remain unchanged. If you return true, the item will be
     * replaced by the new one, and RecyclerAdapter's notifyItemChanged method will be
     * called to update the binding.
     * @param newItem item in the new list whose [AdapterItem.equals] returns the
     * same value as this item
     * @return true to replace this item with the new item, false otherwise
     */
    open fun hasToBeReplacedBy(newItem: AdapterItem<*>): Boolean = true

    /**
     * Creates a new ViewHolder instance, by inferring the ViewHolder type from the generic passed
     * to this class
     * @param view View to be passed to the ViewHolder
     * @return ViewHolder
     * @throws NoSuchMethodException if no matching constructor are found in the ViewHolder subclass
     * @throws InstantiationException if an error happens during instantiation of the ViewHolder subclass
     * @throws InvocationTargetException if an error happens during a method invocation of the ViewHolder subclass
     * @throws IllegalAccessException if a method, field or class has been declared with insufficient access control modifiers
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(NoSuchMethodException::class,
        InstantiationException::class,
        InvocationTargetException::class,
        IllegalAccessException::class)
    private fun getViewHolder(view: View): T {

        // analyze all the public classes and interfaces that are members of the class represented
        // by this Class object and search for the first RecyclerAdapterViewHolder
        // implementation. This should also work if RecyclerAdapterViewHolder subclass
        // hierarchy is present, as the first one should be the last of the subclasses
        for (cl in javaClass.classes) {
            if (RecyclerAdapterViewHolder::class.java.isAssignableFrom(cl)) {
                return (cl as Class<T>).getConstructor(View::class.java).newInstance(view)
            }
        }

        throw RuntimeException("${javaClass.simpleName} - No ViewHolder implementation found! " +
            "Please check that all your ViewHolder implementations are: 'public static' and " +
            "not private or protected, otherwise reflection will not work!")

    }

    fun createItemViewHolder(parent: ViewGroup): RecyclerAdapterViewHolder {
        try {
            return getView(parent).let(::getViewHolder)
        } catch (exc: Throwable) {
            val message = when (exc) {
                is NoSuchMethodException -> "You should declare a constructor like this in your ViewHolder:\n" +
                    "public RecyclerAdapterViewHolder(View itemView, RecyclerAdapterNotifier adapter)"
                is IllegalAccessException -> "Your ViewHolder class in ${javaClass.name} should be public!"
                else -> ""
            }

            throw RuntimeException(
                "${this::class.java.simpleName} - onCreateViewHolder error. $message",
                exc
            )
        }
    }

    /**
     * Bind the current item with the view
     * @param firstTime true if it's the first time this item is being bound
     * @param holder ViewHolder on which to bind data
     */
    abstract fun bind(firstTime: Boolean, holder: T)

    override fun compareTo(other: AdapterItem<*>) = 0

    override fun hashCode() = diffingId().hashCode()

    override fun equals(other: Any?) = hashCode() == other.hashCode()
}
