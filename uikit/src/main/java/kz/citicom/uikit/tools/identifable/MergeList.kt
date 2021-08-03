package kz.citicom.uikit.tools.identifable

interface Identifiable : Comparable<Identifiable> {
    fun getStableID(): Number
}

interface SectionIdentifiable : Identifiable {
    fun getSectionIdentifier(): String
}

data class InsertItems<T : Identifiable>(var index: Int, var item: T, var previousIndex: Int?)
data class UpdateItems<T : Identifiable>(var index: Int, var item: T, var previousIndex: Int)
data class MergeListResults<T : Identifiable>(val removeIndices: List<Int>, var insertItems: List<InsertItems<T>>, var updatedIndices: List<UpdateItems<T>>)

data class SectionRemoveItem(val section: Int, val index: Int) : Comparable<SectionRemoveItem> {
    override fun compareTo(other: SectionRemoveItem): Int {
        return when {
            this.section == other.section && this.index == other.index -> 0
            this.section <= other.section -> {
                if (this.section == other.section) {
                    if (this.index < other.index) {
                        -1
                    } else {
                        1
                    }
                } else {
                    -1
                }
            }
            else -> 1
        }
    }

}

data class SectionInsertItems<T : Identifiable>(val section: Int, var index: Int, var item: T)
data class SectionUpdateItems<T : Identifiable>(val section: Int, var index: Int, var item: T)
data class SectionMergeListResults<T : Identifiable>(
    val removeIndices: List<SectionRemoveItem>,
    var insertItems: List<SectionInsertItems<T>>,
    var updatedIndices: List<SectionUpdateItems<T>>
)

fun <T : SectionIdentifiable> mergeSectionList(leftList: List<T>, rightList: List<T>, allUpdated: Boolean = false): SectionMergeListResults<T> {
    val leftSection = leftList.groupBy { it.getSectionIdentifier() }
    val leftSectionKeys = leftSection.map { it.key }
    val rightSection = rightList.groupBy { it.getSectionIdentifier() }
    val rightSectionKeys = rightSection.map { it.key }


    val result = mergeListsStableWithUpdates(leftList, rightList)
    val removeItems = arrayListOf<SectionRemoveItem>()
    val insertItems = arrayListOf<SectionInsertItems<T>>()
    val updateItems = arrayListOf<SectionUpdateItems<T>>()

    for (removeIndex in result.removeIndices) {
        if (removeIndex < leftList.count()) {
            val item = leftList[removeIndex]
            val sectionIdentifier = item.getSectionIdentifier()
            val sectionIndex = leftSectionKeys.indexOf(sectionIdentifier)
            val position = leftSection[sectionIdentifier]?.indexOf(item) ?: continue
            removeItems.add(SectionRemoveItem(sectionIndex, position))
        }
    }

    for (insertItem in result.insertItems) {
        if (insertItem.index < rightList.count()) {
            val item = rightList[insertItem.index]
            val sectionIdentifier = item.getSectionIdentifier()
            val sectionIndex = rightSectionKeys.indexOf(sectionIdentifier)
            val position = rightSection[sectionIdentifier]?.indexOf(item) ?: continue
            insertItems.add(SectionInsertItems(sectionIndex, position, insertItem.item))
        }
    }

    for (updateItem in result.updatedIndices) {
        if (updateItem.index < rightList.count()) {
            val item = rightList[updateItem.index]
            val sectionIdentifier = item.getSectionIdentifier()
            val sectionIndex = rightSectionKeys.indexOf(sectionIdentifier)
            val position = rightSection[sectionIdentifier]?.indexOf(item) ?: continue
            updateItems.add(SectionUpdateItems(sectionIndex, position, item))
        }
    }

    return SectionMergeListResults(removeItems, insertItems, updateItems)
}

fun <T : Identifiable> mergeListsStableWithUpdates(leftList: List<T>, rightList: List<T>, allUpdated: Boolean = false): MergeListResults<T> {
    val removeIndices: ArrayList<Int> = arrayListOf()
    val insertItems: ArrayList<InsertItems<T>> = arrayListOf()
    val updatedIndices: ArrayList<UpdateItems<T>> = arrayListOf()

    val currentList = ArrayList(leftList)
    val previousIndices: HashMap<Number, Int> = hashMapOf()

    for ((i, left) in leftList.withIndex()) {
        previousIndices[left.getStableID()] = i
    }

    var i = 0
    var j = 0
    while (true) {
        val left: T? = if (i < currentList.count()) {
            currentList[i]
        } else {
            null
        }
        val right: T? = if (j < rightList.count()) {
            rightList[j]
        } else {
            null
        }

        if (left != null && right != null) {
            if (left.getStableID() == right.getStableID() && (left != right || allUpdated)) {
                updatedIndices.add(UpdateItems(i, right, previousIndices[left.getStableID()]!!))
                i += 1
                j += 1
            } else {
                if (left == right && !allUpdated) {
                    i += 1
                    j += 1
                } else if (left < right) {
                    removeIndices.add(i)
                    i += 1
                } else {
                    j += 1
                }
            }
        } else if (left != null) {
            removeIndices.add(i)
            i += 1
        } else if (right != null) {
            j += 1
        } else {
            break
        }
    }

    for (index in removeIndices.reversed()) {
        currentList.removeAt(index)

        for (i in 0 until updatedIndices.count()) {
            if (updatedIndices[i].index >= index) {
                updatedIndices[i].index -= 1
            }
        }
    }

    i = 0
    j = 0
    var k = 0

    while (true) {
        val left: T?

        if (k < updatedIndices.count() && updatedIndices[k].index < i) {
            k += 1
        }

        if (k < updatedIndices.count()) {
            left = if (updatedIndices[k].index == i) {
                updatedIndices[k].item
            } else {
                if (i < currentList.count()) {
                    currentList[i]
                } else {
                    null
                }
            }
        } else {
            left = if (i < currentList.count()) {
                currentList[i]
            } else {
                null
            }
        }

        val right: T? = if (j < rightList.count()) {
            rightList[j]
        } else {
            null
        }

        if (left != null && right != null) {
            when {
                left == right -> {
                    i += 1
                    j += 1
                }
                left > right -> {
                    val previousIndex = previousIndices[right.getStableID()]
                    insertItems.add(InsertItems(i, right, previousIndex))
                    currentList.add(i, right)
                    if (k < updatedIndices.count()) {
                        for (l in k until updatedIndices.count()) {
                            updatedIndices[l] = UpdateItems(
                                updatedIndices[l].index + 1,
                                updatedIndices[l].item,
                                updatedIndices[l].previousIndex
                            )
                        }
                    }

                    i += 1
                    j += 1
                }
                else -> {
                    i += 1
                }
            }
        } else if (left != null) {
            i += 1
        } else if (right != null) {
            val previousIndex = previousIndices[right.getStableID()]
            insertItems.add(InsertItems(i, right, previousIndex))
            currentList.add(i, right)

            if (k < updatedIndices.count()) {
                for (l in k until updatedIndices.count()) {
                    updatedIndices[l] = UpdateItems(
                        updatedIndices[l].index + 1,
                        updatedIndices[l].item,
                        updatedIndices[l].previousIndex
                    )
                }
            }

            i += 1
            j += 1
        } else {
            break
        }
    }

    for ((index, item, _) in updatedIndices) {
        currentList[index] = item
    }

    return MergeListResults(removeIndices, insertItems, updatedIndices)
}