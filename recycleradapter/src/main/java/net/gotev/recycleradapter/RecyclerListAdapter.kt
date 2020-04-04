package net.gotev.recycleradapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

/**
 * @author Aleksandar Gotev
 */
class RecyclerListAdapter : ListAdapter<AdapterItem<*>, RecyclerAdapterViewHolder>(diffCallback),
    RecyclerAdapterNotifier {
    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<AdapterItem<*>>() {
            override fun areItemsTheSame(oldItem: AdapterItem<*>, newItem: AdapterItem<*>) =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: AdapterItem<*>, newItem: AdapterItem<*>) =
                !oldItem.hasToBeReplacedBy(oldItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapterViewHolder {
        val item = currentList.find { it.viewType() == viewType }
        require(item != null) { "onCreateViewHolder: cannot find a view with viewType $viewType Check the DataSource implementation!" }

        return item.createItemViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerAdapterViewHolder, position: Int) {
        bindItem(holder, position, true)
    }

    override fun onBindViewHolder(
        holder: RecyclerAdapterViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        bindItem(holder, position, payloads.isEmpty())
    }

    private fun bindItem(holder: RecyclerAdapterViewHolder, position: Int, firstTime: Boolean) {
        val item = adapterItem(position, caller = "bindItem")

        holder.setAdapter(this)
        item.castAsIn().bind(firstTime, holder)
    }

    private fun adapterItem(position: Int, caller: String): AdapterItem<*> {
        val item = getItem(position)
        require(item != null) { "$caller: no item found at position $position. Check the DataSource implementation!" }

        return item
    }

    override fun selected(holder: RecyclerAdapterViewHolder) {
        // not supported
    }

    override fun getAdapterItem(holder: RecyclerAdapterViewHolder): AdapterItem<*>? {
        return getItem(holder.adapterPosition)
    }

    override fun notifyItemChanged(holder: RecyclerAdapterViewHolder) {
        // not supported
    }

    override fun getItemViewType(position: Int) =
        adapterItem(position, caller = "getItemViewType").viewType()

    override fun getItemId(position: Int) =
        adapterItem(position, caller = "getItemId").diffingId().hashCode().toLong()
}
