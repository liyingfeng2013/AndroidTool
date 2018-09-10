package com.lyf.tool.recyclerview.pinned

import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.View

/**
 * 顶部固定
 * Created by LYF on 2018/9/10.
 */
class PinnedItemDecoration : RecyclerView.ItemDecoration() {
    private val cache = SparseArray<View>()
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        if (parent.adapter !is PinnedAdapter || parent.childCount <= 0) return
        val adapter = parent.adapter as PinnedAdapter
        val firstAdapterPosition = parent.getChildAdapterPosition(parent.getChildAt(0))
        val pinnedHeaderPosition = getPinnedHeaderViewPosition(firstAdapterPosition, adapter)
        if (pinnedHeaderPosition != -1) {
            //要固定的view
            var pinnedHeaderView: View? = cache.get(pinnedHeaderPosition)
            if (pinnedHeaderView == null) {
                val pinnedHeaderViewHolder = adapter.onCreateViewHolder(parent, adapter.getItemViewType(pinnedHeaderPosition))
                adapter.onBindViewHolder(pinnedHeaderViewHolder, pinnedHeaderPosition)
                pinnedHeaderView = pinnedHeaderViewHolder.itemView
                cache.put(pinnedHeaderPosition, pinnedHeaderView)
            }
            ensurePinnedHeaderViewLayout(pinnedHeaderView, parent)
            var sectionPinOffset = 0
            for (index in 0 until parent.childCount) {
                if (adapter.isPinnedPosition(parent.getChildAdapterPosition(parent.getChildAt(index)))) {
                    val sectionView = parent.getChildAt(index)
                    val sectionTop = sectionView.top
                    val pinViewHeight = pinnedHeaderView.height
                    if (sectionTop in 1..(pinViewHeight - 1)) {
                        sectionPinOffset = sectionTop - pinViewHeight
                    }
                }
            }
            val saveCount = c.save()
            c.translate(0f, sectionPinOffset.toFloat())
            c.clipRect(0, 0, parent.width, pinnedHeaderView.measuredHeight)
            pinnedHeaderView.draw(c)
            c.restoreToCount(saveCount)
        }
    }

    /**
     * 根据第一个可见的adapter的位置去获取临近的一个要固定的position的位置
     *
     * @param adapterFirstVisible 第一个可见的adapter的位置
     * @return -1：未找到 >=0 找到位置
     */
    private fun <T : RecyclerView.ViewHolder> getPinnedHeaderViewPosition(adapterFirstVisible: Int, adapter: PinnedAdapter<T>): Int {
        for (index in adapterFirstVisible downTo 0) {
            if (adapter.isPinnedPosition(index)) {
                return index
            }
        }
        return -1
    }

    private fun ensurePinnedHeaderViewLayout(pinView: View, recyclerView: RecyclerView) {
        if (pinView.isLayoutRequested) {
            //用的是RecyclerView的宽度测量，和RecyclerView的宽度一样
            val layoutParams = pinView.layoutParams as? RecyclerView.LayoutParams
                    ?: throw NullPointerException("PinnedHeaderItemDecoration")
            val widthSpec = View.MeasureSpec.makeMeasureSpec(
                    recyclerView.measuredWidth - layoutParams.leftMargin - layoutParams.rightMargin, View.MeasureSpec.EXACTLY)
            val heightSpec = if (layoutParams.height > 0) {
                View.MeasureSpec.makeMeasureSpec(layoutParams.height, View.MeasureSpec.EXACTLY)
            } else {
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            }
            pinView.measure(widthSpec, heightSpec)
            pinView.layout(0, 0, pinView.measuredWidth, pinView.measuredHeight)
        }
    }
}