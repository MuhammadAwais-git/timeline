package com.translate.language.timelinemoduleonmap.adapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.translate.language.timelinemoduleonmap.R
import com.translate.language.timelinemoduleonmap.roommodel.RoomHistoryModel

class LocationHistoryAdapter :
    ListAdapter<RoomHistoryModel, LocationHistoryAdapter.ViewHolder>(RoomHistoryModelDiffCallback()) {

    private var onItemClickListener: ((RoomHistoryModel) -> Unit)? = null
    private var selectedDate: String? = null

    fun setSelectedDate(date: String) {
        selectedDate = date
        notifyDataSetChanged()
    }
    fun setOnItemClickListener(listener: (RoomHistoryModel) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_location_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val historyModel = getItem(position)
        holder.bind(historyModel)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardViewLoc: CardView = itemView.findViewById(R.id.cardViewLoc)
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private val txtLocation: TextView = itemView.findViewById(R.id.txtLocation)
        private val txtLatLng: TextView = itemView.findViewById(R.id.txtLatLng)
        private val imgLoc: ImageView = itemView.findViewById(R.id.imgLoc)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val historyModel = getItem(position)
                    onItemClickListener?.invoke(historyModel)
                }
            }

        }

        fun bind(historyModel: RoomHistoryModel) {
            dateTextView.text = historyModel.date
            Log.d("DATEEEEE", "bind: selectedDate $selectedDate")
            Log.d("DATEEEEE", "bind: historyModel.date ${historyModel.date}")
            if (historyModel.date == selectedDate) {
                dateTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                txtLocation.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                txtLatLng.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                imgLoc.setColorFilter(ContextCompat.getColor(itemView.context, R.color.white))
                cardViewLoc.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.green))

            } else {
                dateTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                txtLocation.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                txtLatLng.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                imgLoc.clearColorFilter()
                cardViewLoc.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.white))

            }
        }
    }
}

class RoomHistoryModelDiffCallback : DiffUtil.ItemCallback<RoomHistoryModel>() {
    override fun areItemsTheSame(oldItem: RoomHistoryModel, newItem: RoomHistoryModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RoomHistoryModel, newItem: RoomHistoryModel): Boolean {
        return oldItem == newItem
    }
}
