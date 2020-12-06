package net.gotev.recycleradapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class RecyclerAdapter : ListAdapter<AdapterItem<*>, RecyclerAdapterViewHolder>(diffCallback),
    RecyclerAdapterNotifier {
    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<AdapterItem<*>>() {
            override fun areItemsTheSame(oldItem: AdapterItem<*>, newItem: AdapterItem<*>) =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: AdapterItem<*>, newItem: AdapterItem<*>) =
                !oldItem.hasToBeReplacedBy(oldItem)
        }
    }

    init {
        setHasStableIds(true)
    }

    var emptyItem: AdapterItem<*>? = null

    override fun submitList(list: List<AdapterItem<*>>?) {
        val safeEmptyItem = emptyItem
        super.submitList(
            if (list.isNullOrEmpty() && safeEmptyItem != null)
                listOf(safeEmptyItem)
            else
                list
        )
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

    override fun getAdapterItem(holder: RecyclerAdapterViewHolder): AdapterItem<*>? {
        return getItem(holder.adapterPosition)
    }

    override fun notifyItemChanged(holder: RecyclerAdapterViewHolder) {
        val position = holder.adapterPosition
        getItem(position) ?: return
        notifyItemChanged(position, true)
    }

    override fun getItemViewType(position: Int) =
        adapterItem(position, caller = "getItemViewType").viewType()

    override fun getItemId(position: Int) =
        adapterItem(position, caller = "getItemId").diffingId().hashCode().toLong()

    override fun onViewRecycled(holder: RecyclerAdapterViewHolder) {
        super.onViewRecycled(holder)
        holder.prepareForReuse()
    }
}
