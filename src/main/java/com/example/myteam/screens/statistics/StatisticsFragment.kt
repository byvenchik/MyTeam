package com.example.myteam.screens.statistics

import android.view.View
import androidx.fragment.app.Fragment
import com.example.myteam.R
import com.example.myteam.database.*
import com.example.myteam.utilits.*
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStyle
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.android.synthetic.main.fragment_main.*

//Главный фрагмент
class StatisticsFragment : Fragment(R.layout.fragment_main) {

    private var mRefStatistics = REF_DATABASE_ROOT.child(NODE_STATISTICS).child(USER.id)

    private var mCountComplited: Int = 0
    private var mNotCountComplited: Int = 0
    private var mCountDecline: Int = 0
    private var mCountGetComplited: Int = 0
    private var mNoGetCountComplited: Int = 0
    private var mGetDeclineDecline: Int = 0
    lateinit var barList: ArrayList<BarEntry>
    lateinit var barDataSet: BarDataSet
    lateinit var barData: BarData

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Cтатистика"
        APP_ACTIVITY.mAppDrawer.enableDrawer()  //Его надо включать после входа
        hideKeyboard()
        initFields()
        initStatistics()
    }

    private fun initFields() {
        statistics_user_photo.downloadAndSetImage(USER.photoUrl)
        statistics_fullname.text = USER.fullname
    }

    private fun initStatistics() {
        mRefStatistics.child(CHILD_PROGRESS_TASK).get().addOnSuccessListener {
            if (it.value != null) {
                count_progress.text = it.value.toString()
            }
        }
        mRefStatistics.child(CHILD_CONTROL_TASK).get().addOnSuccessListener {
            if (it.value != null) {
                count_control.text = it.value.toString()
            }
        }
        mRefStatistics.child(CHILD_COMPLETED_TASK).get().addOnSuccessListener {
            var valueCompleteTask = ""
            if (it.value != null) {
                count_complete_task.text = it.value.toString()
                valueCompleteTask = it.value.toString()
                newvalueCompleteTask(valueCompleteTask)
            } else valueCompleteTask = "0"
            newvalueCompleteTask(valueCompleteTask)
        }
        mRefStatistics.child(CHILD_UNFULFILLED_TASK).get().addOnSuccessListener {
            var valueNotComplitedTask = ""
            if (it.value != null) {
                count_not_complete_task.text = it.value.toString()
                valueNotComplitedTask = it.value.toString()
                newvalueNotComplitedTask(valueNotComplitedTask)
            } else valueNotComplitedTask = "0"
            newvalueNotComplitedTask(valueNotComplitedTask)
        }

        mRefStatistics.child(CHILD_DECLINE_TASK).get().addOnSuccessListener {
            var valueNotDeclineTask = ""
            if (it.value != null) {
                valueNotDeclineTask = it.value.toString()
                newvalueNotDeclineTask(valueNotDeclineTask)
                count_decline_task.text = it.value.toString()
            } else valueNotDeclineTask = "0"
            newvalueNotDeclineTask(valueNotDeclineTask)
        }
        mRefStatistics.child(CHILD_GET_COMPLETED_TASK).get().addOnSuccessListener {
            var valueGetCompletedTask = ""
            if (it.value != null) {
                count_get_complete_task.text = it.value.toString()
                valueGetCompletedTask = it.value.toString()
                newvalueGetCompletedTask(valueGetCompletedTask)
            } else valueGetCompletedTask = "0"
            newvalueGetCompletedTask(valueGetCompletedTask)
        }
        mRefStatistics.child(CHILD_GET_UNFULFILLED_TASK).get().addOnSuccessListener {
            var valueNotGetCompleteTask = ""
            if (it.value != null) {
                count_not_get_complete_task.text = it.value.toString()
                valueNotGetCompleteTask = it.value.toString()
                newvalueNotGetCompleteTask(valueNotGetCompleteTask)
            } else valueNotGetCompleteTask = "0"
            newvalueNotGetCompleteTask(valueNotGetCompleteTask)
        }
        mRefStatistics.child(CHILD_GET_DECLINE_TASK).get().addOnSuccessListener {
            var valueGetDeclineTask = ""
            if (it.value != null) {
                count_decline_get_task.text = it.value.toString()
                valueGetDeclineTask = it.value.toString()
                newvalueGetDeclineTask(valueGetDeclineTask)
            } else valueGetDeclineTask = "0"
            newvalueGetDeclineTask(valueGetDeclineTask)
        }

        statisticsChart()

        btn_show_chart.setOnClickListener {
            statisticsChart()
            btn_show_chart.visibility = View.GONE
            aa_chart_view.visibility = View.VISIBLE
        }
    }

    private fun newvalueGetCompletedTask(valueGetCompletedTask: String) {
        val newValue = valueGetCompletedTask.toString().toInt()
        mCountGetComplited = newValue
    }

    private fun newvalueGetDeclineTask(valueGetDeclineTask: String) {
        val newValue = valueGetDeclineTask.toString().toInt()
        mGetDeclineDecline = newValue
    }

    private fun newvalueNotGetCompleteTask(valueNotGetCompleteTask: String) {
        val newValue = valueNotGetCompleteTask.toString().toInt()
        mNoGetCountComplited = newValue
    }

    private fun newvalueNotDeclineTask(valueNotDeclineTask: String) {
        val newValue = valueNotDeclineTask.toString().toInt()
        mCountDecline = newValue
    }

    private fun newvalueNotComplitedTask(valueNotComplitedTask: String) {
        val newValue = valueNotComplitedTask.toString().toInt()
        mNotCountComplited = newValue
    }

    private fun newvalueCompleteTask(valueCompleteTask: String) {
        val newValue = valueCompleteTask.toString().toInt()
        mCountComplited = newValue
    }


    private fun statisticsChart(
    ) {
        val a = mCountComplited
        val b = mNotCountComplited

        val c = mCountGetComplited
        val d = mNoGetCountComplited

        val f = mCountDecline
        val g = mGetDeclineDecline

        val aaChartView = APP_ACTIVITY.findViewById<AAChartView>(R.id.aa_chart_view)
        val aaChartModel: AAChartModel = AAChartModel()
            .chartType(AAChartType.Bar)
            .title("График вашей статистики").titleStyle(AAStyle.Companion.style("#74E0B8",15f))
            .backgroundColor("#FFFFFFFF")
            .dataLabelsEnabled(true)
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("Выполненные задачи")
                        .data(arrayOf(a)),
                    AASeriesElement()
                        .name("Невыполненные задачи")
                        .data(arrayOf(b)),
                    AASeriesElement()
                        .name("Выполненные отправленные задачи")
                        .data(arrayOf(c)),
                    AASeriesElement()
                        .name("Невыполненные отправленные задачи")
                        .data(arrayOf(d)),
                    AASeriesElement()
                        .name("Отказы от задач")
                        .data(arrayOf(f)),
                    AASeriesElement()
                        .name("Отказы от ваших задач")
                        .data(arrayOf(g))
                )
            )
        aaChartView.aa_drawChartWithChartModel(aaChartModel)
        aaChartView.aa_refreshChartWithChartModel(aaChartModel)
    }
}






