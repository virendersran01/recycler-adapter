package net.gotev.recycleradapterdemo.adapteritems

import android.view.View
import androidx.appcompat.widget.SwitchCompat
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_selectable.*
import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapter.RecyclerAdapterViewHolder
import net.gotev.recycleradapterdemo.R

open class SelectableItem(val label: String, private val group: String) :
    AdapterItem<SelectableItem.Holder>(label) {

    override fun getLayoutId() = R.layout.item_selectable

    override fun bind(firstTime: Boolean, holder: Holder) {
        holder.toggleField.apply {
            text = label
        }
    }

    class Holder(itemView: View) : RecyclerAdapterViewHolder(itemView), LayoutContainer {

        override val containerView: View?
            get() = itemView

        internal val toggleField: SwitchCompat by lazy { toggle }

        init {
            toggleField.setOnClickListener {
                //setSelected()
            }
        }
    }
}
