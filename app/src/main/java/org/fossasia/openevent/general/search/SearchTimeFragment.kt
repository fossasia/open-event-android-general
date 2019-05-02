package org.fossasia.openevent.general.search

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_search_time.view.anytimeTextView
import kotlinx.android.synthetic.main.fragment_search_time.view.todayTextView
import kotlinx.android.synthetic.main.fragment_search_time.view.tomorrowTextView
import kotlinx.android.synthetic.main.fragment_search_time.view.thisWeekendTextView
import kotlinx.android.synthetic.main.fragment_search_time.view.nextMonthTextView
import kotlinx.android.synthetic.main.fragment_search_time.view.timeTextView
import org.fossasia.openevent.general.R
import org.fossasia.openevent.general.event.EventUtils.getSimpleFormattedDate
import java.util.Calendar
import org.fossasia.openevent.general.utils.Utils.setToolbar
import org.fossasia.openevent.general.utils.extensions.navigateWithBundleTo
import org.koin.androidx.viewmodel.ext.android.viewModel

const val ANYTIME = "Anytime"
const val TODAY = "Today"
const val TOMORROW = "Tomorrow"
const val THIS_WEEKEND = "This weekend"
const val NEXT_MONTH = "In the next month"

class SearchTimeFragment : Fragment() {
    private val safeArgs: SearchTimeFragmentArgs by navArgs()
    private val searchTimeViewModel by viewModel<SearchTimeViewModel>()

    private lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_search_time, container, false)

        setToolbar(activity)
        setHasOptionsMenu(true)
        setCurrentChoice(safeArgs.time)

        val calendar = Calendar.getInstance()

        val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            calendar.add(Calendar.DATE, 1)
            redirectToCaller(getSimpleFormattedDate(calendar.time))
        }

        rootView.anytimeTextView.setOnClickListener {
            redirectToCaller(ANYTIME)
        }

        rootView.todayTextView.setOnClickListener {
            redirectToCaller(TODAY)
        }

        rootView.tomorrowTextView.setOnClickListener {
            redirectToCaller(TOMORROW)
        }

        rootView.thisWeekendTextView.setOnClickListener {
            redirectToCaller(THIS_WEEKEND)
        }

        rootView.nextMonthTextView.setOnClickListener {
            redirectToCaller(NEXT_MONTH)
        }

        rootView.timeTextView.setOnClickListener {
            DatePickerDialog(requireContext(), date, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        return rootView
    }

    private fun redirectToCaller(time: String) {
        searchTimeViewModel.saveTime(time)
        val (destFragId, popUpId) = if (safeArgs.fromFragmentName == SEARCH_FILTER_FRAGMENT)
            R.id.searchFilterFragment to R.id.searchResultsFragment
        else
            R.id.searchFragment to R.id.eventsFragment

        val navOptions = NavOptions.Builder().setPopUpTo(popUpId, false).build()

        val navArgs = if (safeArgs.fromFragmentName == SEARCH_FILTER_FRAGMENT) {
            SearchFilterFragmentArgs(
                query = safeArgs.query
            ).toBundle()
        } else
            null
        navArgs.navigateWithBundleTo(rootView, destFragId, navOptions)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                activity?.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setCurrentChoice(value: String?) {
        when (value) {
            ANYTIME -> rootView.anytimeTextView.setCheckMarkDrawable(R.drawable.ic_checked)
            TODAY -> rootView.todayTextView.setCheckMarkDrawable(R.drawable.ic_checked)
            TOMORROW -> rootView.tomorrowTextView.setCheckMarkDrawable(R.drawable.ic_checked)
            THIS_WEEKEND -> rootView.thisWeekendTextView.setCheckMarkDrawable(R.drawable.ic_checked)
            NEXT_MONTH -> rootView.nextMonthTextView.setCheckMarkDrawable(R.drawable.ic_checked)
            else -> rootView.timeTextView.setCheckMarkDrawable(R.drawable.ic_checked)
        }
    }
}
