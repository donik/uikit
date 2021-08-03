package kz.arnapp.utils.identifable

import android.content.Context
import android.view.ViewGroup
import kz.citicom.uikit.tools.identifable.Identifiable
import kz.citicom.uikit.tools.identifable.InsertItems
import kz.citicom.uikit.tools.identifable.UpdateItems
import kz.citicom.uikit.tools.identifable.mergeListsStableWithUpdates
import kz.citicom.uikit.tools.recycler.Helper.OrientationHelper
import kz.citicom.uikit.tools.recycler.Helper.RecyclerListView
import kz.citicom.uikit.tools.recycler.LinearLayoutManager
import kz.citicom.uikit.tools.recycler.RecyclerView
import kz.citicom.uikit.views.EmptyTextProgressView


object Recycler {
    fun <T : MergeListAdapterIdentifiable<R>, R> getRecycler(
        context: Context,
        adapter: MergeListAdapter<T, R>,
        emptyText: String,
        scrollListener: RecyclerView.OnScrollListener? = null,
        interaction: R? = null
    ): RecyclerListView {
        val emptyView = EmptyTextProgressView(context)
        emptyView.showTextView()
        emptyView.setText(emptyText)

        return RecyclerListView(context).let {
            it.layoutManager = LinearLayoutManager(context, OrientationHelper.VERTICAL, false)
            it.isVerticalScrollBarEnabled = false
            it.emptyView = emptyView
            it.adapter = adapter
            it.setOnItemClickListener { _, position ->
                val item = adapter.getItem(position) as? T ?: return@setOnItemClickListener
                item.itemClickAction(interaction)
            }
            it.setOnItemLongClickListener { _, position ->
                val item = adapter.getItem(position) ?: return@setOnItemLongClickListener false
                return@setOnItemLongClickListener item.itemLongClickAction(interaction)
            }
            scrollListener?.let { scrollListener ->
                it.setOnScrollListener(scrollListener)
            }

            return@let it
        }
    }
}

abstract class MergeListAdapterIdentifiable<R> : Identifiable {
    open fun isEnabled(): Boolean {
        return true
    }

    open fun getItemViewType(): Int {
        return 0
    }

    open fun itemClickAction(interaction: R?) {

    }

    open fun itemLongClickAction(interaction: R?) : Boolean {
        return false
    }

    abstract fun onBindViewHolder(holder: RecyclerView.ViewHolder?, first: Boolean = false, last: Boolean = false)
    abstract fun createViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder
}

class MergeListAdapter<T : MergeListAdapterIdentifiable<R>, R>() : RecyclerListView.SelectionAdapter() {
    private var items: ArrayList<T> = arrayListOf()

    fun getItems(predicate: (T) -> Boolean): List<T> {
        return this.items.filter(predicate)
    }

    fun getItem(position: Int): T? {
        return this.items.getOrNull(position)
    }

    fun isLast(position: Int): Boolean {
        return this.items.size != (position - 1)
    }

    fun setItems(items: List<T>) {
        val results = mergeListsStableWithUpdates(this.items, items)

        this.transaction(results.removeIndices, results.insertItems, results.updatedIndices)
    }

    private fun transaction(
        deleteIndices: List<Int>,
        insertIndicesAndItems: List<InsertItems<T>>,
        updateIndicesAndItems: List<UpdateItems<T>>
    ) {
        val deleteIndexes = deleteIndices.sorted().reversed()
        for (position in deleteIndexes) {
            this.items.removeAt(position)
            this.notifyItemRemoved(position)
        }
        for (insertion in insertIndicesAndItems) {
            this.items.add(insertion.index, insertion.item)
            this.notifyItemInserted(insertion.index)
        }
        for (update in updateIndicesAndItems) {
            this.items[update.index] = update.item
            this.notifyItemChanged(update.index)
        }

        if (this.items.count() != this.itemCount) {
            this.notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val firstItem = this.items.firstOrNull() ?: throw Exception("onCreateViewHolder called when items null")
        return firstItem.createViewHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return this.items.getOrNull(position)?.getItemViewType() ?: 0
    }

    override fun getItemCount(): Int {
        return this.items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        this.items.getOrNull(position)?.onBindViewHolder(holder, position == 0, items.count() - 1 == position)
    }

    override fun isEnabled(holder: RecyclerView.ViewHolder?): Boolean {
        val position = holder?.adapterPosition ?: return false
        return this.items.getOrNull(position)?.isEnabled() ?: false
    }
}