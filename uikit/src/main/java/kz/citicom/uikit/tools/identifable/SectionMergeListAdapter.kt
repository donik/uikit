package kz.arnapp.utils.identifable

import android.content.Context
import android.view.View
import android.view.ViewGroup
import kz.citicom.uikit.tools.identifable.InsertItems
import kz.citicom.uikit.tools.identifable.SectionIdentifiable
import kz.citicom.uikit.tools.identifable.UpdateItems
import kz.citicom.uikit.tools.identifable.mergeListsStableWithUpdates
import kz.citicom.uikit.tools.recycler.Helper.OrientationHelper
import kz.citicom.uikit.tools.recycler.Helper.RecyclerListView
import kz.citicom.uikit.tools.recycler.LinearLayoutManager
import kz.citicom.uikit.tools.recycler.RecyclerView
import kz.citicom.uikit.views.EmptyTextProgressView
import java.lang.Exception

object SectionRecycler {
    fun <T : SectionMergeListAdapterIdentifiable<R>, R> getRecycler(
        context: Context,
        adapter: SectionMergeListAdapter<T, R>,
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
            it.setFastScrollVisible(true)
            it.setHasFixedSize(true)
            it.setSectionsType(2)
            it.emptyView = emptyView
            it.adapter = adapter
            it.setOnItemClickListener { _, position ->
                val item = adapter.getItem(position) as? T ?: return@setOnItemClickListener
                item.itemClickAction(interaction)
            }
            it.setOnItemLongClickListener { _, position ->
                val item = adapter.getItem(position) as? T ?: return@setOnItemLongClickListener false
                return@setOnItemLongClickListener item.itemLongClickAction(interaction)
            }
            scrollListener?.let { scrollListener ->
                it.setOnScrollListener(scrollListener)
            }

            return@let it
        }
    }
}

abstract class SectionMergeListAdapterIdentifiable<R> : SectionIdentifiable {
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

    abstract fun onBindViewHolder(holder: RecyclerView.ViewHolder?, first: Boolean = false, last: Boolean = false, update: Boolean = false)
    abstract fun createViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder
    abstract fun getSectionHeaderView(view: View?): View
}

class SectionMergeListAdapter<T : SectionMergeListAdapterIdentifiable<R>, R> : RecyclerListView.SectionsAdapter() {
    private var items = arrayListOf<T>()

    fun getItems(predicate: (T) -> Boolean): List<T> {
        return this.items.filter(predicate)
    }

    override fun getLetter(position: Int): String {
        return this.items[position].getSectionIdentifier()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return this.items.firstOrNull()?.createViewHolder(parent, viewType) ?: throw Exception("List is empty")
    }

    override fun getCountForSection(section: Int): Int {
        val values = this.recyclerValues()
        val key = values.keys.toTypedArray()[section]
        return values[key]?.count() ?: 0
    }

    override fun getItem(section: Int, position: Int): Any? {
        val values = this.recyclerValues()
        val key = values.keys.toTypedArray()[section]

        return values[key]?.get(position)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int, payloads: MutableList<Any?>?) {
        if(payloads?.isNotEmpty() == true) {
            val section = getSectionForPosition(position)
            val row = getPositionInSectionForPosition(position)
            val values = this.recyclerValues()
            val key = values.keys.toTypedArray()[section]
            val lastItemPosition = (values[key]?.count() ?: 0) - 1

            values[key]?.get(row)?.onBindViewHolder(holder, row == 0, lastItemPosition == row, true)
        }else {
            super.onBindViewHolder(holder,position, payloads);
        }
    }

    override fun onBindViewHolder(section: Int, position: Int, holder: RecyclerView.ViewHolder?) {
        val values = this.recyclerValues()
        val key = values.keys.toTypedArray()[section]
        val lastItemPosition = (values[key]?.count() ?: 0) - 1
        values[key]?.get(position)?.onBindViewHolder(holder, position == 0, lastItemPosition == position)
    }

    override fun getSectionHeaderView(section: Int, view: View?): View {
        val values = this.recyclerValues()
        val key = values.keys.toTypedArray()[section]
        return values[key]?.firstOrNull()?.getSectionHeaderView(view) ?: throw Exception("Header view is null")
    }

    override fun getItemViewType(section: Int, position: Int): Int {
        val values = this.recyclerValues()
        val key = values.keys.toTypedArray()[section]

        return values[key]?.get(position)?.getItemViewType() ?: 0
    }

    override fun isEnabled(section: Int, row: Int): Boolean {
        val values = this.recyclerValues()
        val key = values.keys.toTypedArray()[section]
        return values[key]?.get(row)?.isEnabled() ?: false
    }

    override fun getSectionCount(): Int {
        return this.recyclerValues().keys.size
    }

    override fun getPositionForScrollProgress(progress: Float): Int {
        return (itemCount * progress).toInt()
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
            this.notifyItemChanged(update.index, update.item)
        }

        if (this.items.count() != this.itemCount) {
            this.notifyDataSetChanged()
        }
    }

    private fun recyclerValues(): Map<String, List<T>> {
        return this.items.groupBy { it.getSectionIdentifier() }
    }

}