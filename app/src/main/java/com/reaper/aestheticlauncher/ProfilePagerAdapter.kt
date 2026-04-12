package com.reaper.aestheticlauncher

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ProfilePagerAdapter : RecyclerView.Adapter<ProfilePagerAdapter.PageViewHolder>() {

    private var personalApps: List<AppItem> = emptyList()
    private var workApps: List<AppItem> = emptyList()

    fun submitData(personal: List<AppItem>, work: List<AppItem>) {
        personalApps = personal
        workApps = work
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val rv = RecyclerView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            layoutManager = LinearLayoutManager(parent.context)
            overScrollMode = View.OVER_SCROLL_NEVER
        }
        return PageViewHolder(rv)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        val rv = holder.itemView as RecyclerView
        val isWork = position == 1
        val adapter = AuraAppAdapter(if (isWork) workApps else personalApps, isWork)
        rv.adapter = adapter
        
        rv.clearOnScrollListeners()
        val physicsListener = KineticScrollListener()
        rv.addOnScrollListener(physicsListener)
        
        rv.post { physicsListener.applyPhysics(rv) }
    }

    override fun getItemCount() = 2

    class PageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
