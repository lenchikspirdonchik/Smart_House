package spiridonov.smart_house.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import spiridonov.smart_house.MainActivity
import spiridonov.smart_house.databinding.FragmentHomeBinding
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import java.util.*
import org.eazegraph.lib.models.ValueLinePoint
import org.eazegraph.lib.models.ValueLineSeries




class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val host = "ec2-176-34-168-83.eu-west-1.compute.amazonaws.com"
    private val database = "d8uq0o35eq55kl"
    private val port = 5432
    private val user = "zftenjusikiyjk"
    private val pass = "7369c86979b2cbf92b10879ec08ba1ca99394ea761c0462a4baf24d3a2225685"
    private var url = "jdbc:postgresql://%s:%d/%s"
    private lateinit var spinner: Spinner
    private var sensors = arrayListOf<Array<String>>()
    var allrooms = arrayListOf<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        spinner = binding.spinnerCategory
        val msp = requireActivity().getSharedPreferences("AppMemory", Context.MODE_PRIVATE)
        var name = ""
        if (msp.contains(MainActivity.KEY_LOGIN)) name =
            msp.getString(MainActivity.KEY_LOGIN, "").toString()
        url = String.format(url, host, port, database)


        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                itemSelected: View, selectedItemPosition: Int, selectedId: Long
            ) {
                if (sensors[0][0] == "Sensor0")
                    readSQLDB(name = name, sensor = sensors[0][1].toInt())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        if (name != "")
            getCategoryFromDB(name = name)



        return root
    }

    private fun getCategoryFromDB(name: String) {
        val database = FirebaseDatabase.getInstance().reference
        val reference = database.child(name).child("rooms")
        reference.get().addOnSuccessListener {
            for (postSnapshot in it.children) {
                allrooms.add(postSnapshot.key.toString())
                for (post in postSnapshot.children) {
                    val buffArray = arrayOf(post.key.toString(), post.value.toString())
                    sensors.add(buffArray)
                }
            }
            val adaptermain: ArrayAdapter<String> =
                ArrayAdapter<String>(
                    requireActivity(),
                    spiridonov.smart_house.R.layout.support_simple_spinner_dropdown_item,
                    allrooms
                )
            adaptermain.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adaptermain

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun dateToCalendar(date: Date): Calendar {
        val calendar = Calendar.getInstance()
        calendar.clear(Calendar.ZONE_OFFSET)
        calendar.time = date
        return calendar
    }

    private fun readSQLDB(name: String, sensor: Int) {
        val handler = Handler()
        val dateFromDB = arrayListOf<Array<String>>()
        val thread = Thread {
            try {
                Class.forName("org.postgresql.Driver")
                val connection = DriverManager.getConnection(url, user, pass)
                val st: Statement = connection.createStatement()
                val rs: ResultSet =
                    st.executeQuery("select * from $name WHERE sensor = $sensor ORDER BY date;")
                while (rs.next()) {
                    val date = rs.getTimestamp("date")
                    val temperature = rs.getFloat("temperature")
                    val humidity = rs.getFloat("humidity")
                    val calendar = dateToCalendar(date)
                    val day = calendar[Calendar.DAY_OF_MONTH]
                    val month = calendar[Calendar.MONTH]
                    val year = calendar[Calendar.YEAR]
                    val hour = calendar[Calendar.HOUR]
                    val min = calendar[Calendar.MINUTE]
                    val buffArray = arrayOf(day.toString(), month.toString(),hour.toString(), min.toString(), temperature.toString(), humidity.toString())
                    Log.d("DB",  "$day.$month.$year $hour:$min: Температура: $temperature; Влажность: $humidity\n")
                    dateFromDB.add(buffArray)
                }
            } catch (e: Exception) {
                Log.d("DB", e.toString())
            }
            handler.post{
                val mCubicValueLineChartHumidity= binding.cubiclinechartHumidity
                val series = ValueLineSeries()
                series.color = Color.parseColor("#52BF25")
                val mCubicValueLineChartTemperature= binding.cubiclinechartTemperature
                val seriestemp = ValueLineSeries()
                seriestemp.color = -0xa9480f

                for (date in dateFromDB){
                    var data = "${date[2]}:${date[3]}"
                    series.addPoint(ValueLinePoint(data, date[5].toFloat()))
                    data = "${date[2]}:${date[3]}"
                    seriestemp.addPoint(ValueLinePoint(data, date[4].toFloat()))
                }
                mCubicValueLineChartHumidity.addSeries(series)
                mCubicValueLineChartHumidity.startAnimation()
                mCubicValueLineChartTemperature.addSeries(seriestemp)
                mCubicValueLineChartTemperature.startAnimation()
            }
        }
        thread.start()
    }
}