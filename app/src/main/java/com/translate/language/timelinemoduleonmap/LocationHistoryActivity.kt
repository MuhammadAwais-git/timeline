package com.translate.language.timelinemoduleonmap


import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sahana.horizontalcalendar.HorizontalCalendar
import com.sahana.horizontalcalendar.OnDateSelectListener
import com.translate.language.timelinemoduleonmap.adapter.LocationHistoryAdapter
import com.translate.language.timelinemoduleonmap.histroydata.HistoryModelFactory
import com.translate.language.timelinemoduleonmap.histroydata.HistoryRepository
import com.translate.language.timelinemoduleonmap.histroydata.HistoryViewModel
import com.translate.language.timelinemoduleonmap.roomdatabase.LocationRoomDB
import java.text.SimpleDateFormat
import java.util.Locale


class LocationHistoryActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LocationHistoryAdapter
    private lateinit var mViewModel: HistoryViewModel

    private var mHorizontalCalendar: HorizontalCalendar? = null
    private var mDateTextView: TextView? = null
    private var mDate: TextView? = null
    private var imgBack: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_history)

        recyclerView = findViewById(R.id.mRecyclerViewPlaces)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = LocationHistoryAdapter()
        recyclerView.adapter = adapter

        mHorizontalCalendar = findViewById(R.id.horizontalCalendar)
        mDateTextView = findViewById(R.id.mDateTextView)
        mDate = findViewById(R.id.mDate)
        imgBack = findViewById(R.id.mImgBack)
        imgBack?.setOnClickListener {
            onBackPressed()
        }

        mHorizontalCalendar?.setOnDateSelectListener(OnDateSelectListener { dateModel ->
            mHorizontalCalendar?.setNumberOfDates(newNumOfDays)
            mDateTextView?.text =
                if (dateModel != null) dateModel.dayOfWeek + "\n" + dateModel.month + " " + dateModel.year else ""
            mDate?.text = dateModel.day.toString()

            val monthNumber = monthNameToNumber(dateModel.month)
            val formattedDate = if (dateModel != null) {
                String.format("%04d-%02d-%02d", dateModel.year, monthNumber, dateModel.day)
            } else {
                ""
            }

            adapter.setSelectedDate(formattedDate)

        })


        val mainRepository = HistoryRepository(LocationRoomDB.invoke(this))

        mViewModel =
            ViewModelProvider(this, HistoryModelFactory(mainRepository)).get(
                HistoryViewModel::class.java
            )

        mViewModel.getAllData().observe(this) { historyModels ->
            adapter.submitList(historyModels)
        }


        adapter.setOnItemClickListener { RoomHistoryModel ->
            val intent = Intent(this@LocationHistoryActivity, HistoryOnMap::class.java)
            intent.putExtra("history_model", RoomHistoryModel)
            startActivity(intent)
        }
    }

    fun monthNameToNumber(monthName: String): Int {
        val months = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
        return months.indexOf(monthName) + 1
    }
}
