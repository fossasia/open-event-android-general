package org.fossasia.openevent.general.event.similarevent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import org.fossasia.openevent.general.common.EventClickListener
import org.fossasia.openevent.general.common.EventsDiffCallback
import org.fossasia.openevent.general.common.FavoriteFabClickListener
import org.fossasia.openevent.general.databinding.ItemCardSimilarEventsBinding
import org.fossasia.openevent.general.event.Event

class SimilarEventsListAdapter : ListAdapter<Event, SimilarEventViewHolder>(EventsDiffCallback()) {

    var onEventClick: EventClickListener? = null
    var onFavFabClick: FavoriteFabClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarEventViewHolder {
        val binding = ItemCardSimilarEventsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SimilarEventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SimilarEventViewHolder, position: Int) {
        val event = getItem(position)
        holder.apply {
            bind(event, position)
            eventClickListener = onEventClick
            favFabClickListener = onFavFabClick
        }
    }

    fun clear() {
        this.submitList(emptyList())
    }
}

interface EventHashTagClickListener {
    fun onClick(hashTagValue: String)
}
