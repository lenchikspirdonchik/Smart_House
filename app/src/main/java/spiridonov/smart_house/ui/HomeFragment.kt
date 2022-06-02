package spiridonov.smart_house.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import org.eazegraph.lib.models.ValueLinePoint
import org.eazegraph.lib.models.ValueLineSeries
import spiridonov.smart_house.MainActivity
import spiridonov.smart_house.databinding.FragmentHomeBinding
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import java.text.SimpleDateFormat
import java.util.*


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
    private lateinit var spinnerDate: Spinner
    private var sensors = arrayListOf<Array<String>>()
    private var allrooms = arrayListOf<String>()
    private var today = 0
    var isFanWorking = false
    private var isLightOn = false
    private var selected = ""
    private var whenTurnOnFan = 45.2F
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        spinner = binding.spinnerCategory
        spinnerDate = binding.spinnerDay
        val msp = requireActivity().getSharedPreferences("AppMemory", Context.MODE_PRIVATE)
        var name = ""
        if (msp.contains(MainActivity.KEY_LOGIN)) name =
            msp.getString(MainActivity.KEY_LOGIN, "").toString()
        url = String.format(url, host, port, database)


        spinnerDate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                itemSelected: View, selectedItemPosition: Int, selectedId: Long
            ) {
                today = selectedItemPosition
                if (sensors.isNotEmpty())
                    selectFromSpinner(name = name)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                itemSelected: View, selectedItemPosition: Int, selectedId: Long
            ) {
                selected = allrooms[selectedItemPosition]
                sensors.clear()

                val database = FirebaseDatabase.getInstance().reference
                val reference = database.child(name).child("rooms").child(selected)
                reference.get().addOnSuccessListener {
                    for (post in it.children) {
                        val buffArray = arrayOf(post.key.toString(), post.value.toString())

                        sensors.add(buffArray)
                    }
                    selectFromSpinner(name = name)
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        if (name != "")
            getCategoryFromDB(name = name)

        binding.whenTurnOnFantxt.setOnClickListener {
            val databasee = FirebaseDatabase.getInstance().reference
            val number: Float = binding.whenTurnOnFantxt.text.toString().toFloat()
            val reference =
                databasee.child(name).child("rooms").child(selected).child("whenTurnOnFan")
            reference.setValue(number)
            Toast.makeText(
                requireContext(),
                "Порог успешно выставлен",
                Toast.LENGTH_LONG
            ).show()
        }

        binding.whenTurnOnFanbtn.setOnClickListener {
            val databasee = FirebaseDatabase.getInstance().reference
            val number: Float = binding.whenTurnOnFantxt.text.toString().toFloat()

            val reference =
                databasee.child(name).child("rooms").child(selected).child("whenTurnOnFan")
            reference.setValue(number).addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    "Порог успешно выставлен",
                    Toast.LENGTH_LONG
                ).show()
            }.addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Произошла ошибка",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.fanbtn.setOnClickListener {
            val databasee = FirebaseDatabase.getInstance().reference
            val reference =
                databasee.child(name).child("rooms").child(selected).child("isFanWorkRoot")
            if (isFanWorking) {
                binding.fantxt.text = "Вентилятор отключён"
                binding.fanbtn.text = "Включить вентилятор"
                reference.setValue(false)
                isFanWorking = false
                Toast.makeText(
                    requireContext(),
                    "Вентилятор выключится в течение 10 минут",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                binding.fantxt.text = "Вентилятор работает"
                binding.fanbtn.text = "Выключить вентилятор"
                isFanWorking = true
                reference.setValue(true)
                Toast.makeText(
                    requireContext(),
                    "Вентилятор включится в течение 10 минут",
                    Toast.LENGTH_LONG
                ).show()

            }
        }


        binding.lightbtn.setOnClickListener {
            val databasee = FirebaseDatabase.getInstance().reference
            val reference =
                databasee.child(name).child("rooms").child(selected).child("isLightOn")
            if (isLightOn) {

                binding.lighttxt.text = "Свет отключён"
                binding.lightbtn.text = "Включить свет"
                reference.setValue(false)
                isLightOn = false
                Toast.makeText(
                    requireContext(),
                    "Выключение",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                binding.lighttxt.text = "Свет включён"
                binding.lightbtn.text = "Выключить свет"
                isLightOn = true
                reference.setValue(true)
                Toast.makeText(
                    requireContext(),
                    "Включение",
                    Toast.LENGTH_LONG
                ).show()
            }

        }

        return root
    }

    private fun selectFromSpinner(name: String) {
        val humidityLayout = binding.humidityManagement
        val ventilationLayout = binding.FanManagement
        val lightLayout = binding.LightManagement
        humidityLayout.visibility = View.GONE
        ventilationLayout.visibility = View.GONE
        lightLayout.visibility = View.GONE
        for (i in 0..sensors.lastIndex) {
            when {
                sensors[i][0] == "Sensor0" -> {
                    humidityLayout.visibility = View.VISIBLE
                    val oneDay = 1000 * 60 * 60 * 24
                    val mydate = Date(System.currentTimeMillis() - oneDay * today)
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                    val whatDayGet: String = dateFormat.format(mydate)
                    readSQLDB(name = name, sensor = sensors[0][1].toInt(), whatDayGet = whatDayGet)
                }
                sensors[i][0] == "Sensor1" -> {
                    ventilationLayout.visibility = View.VISIBLE
                    fanWorking()
                }
                sensors[i][0] == "Sensor2" -> {
                    lightLayout.visibility = View.VISIBLE
                    lightWorking()
                }
            }
        }

    }

    private fun getCategoryFromDB(name: String) {
        val database = FirebaseDatabase.getInstance().reference
        val reference = database.child(name).child("rooms")
        reference.get().addOnSuccessListener {
            for (postSnapshot in it.children) {
                allrooms.add(postSnapshot.key.toString())

            }
            selected = allrooms[0]
            val adaptermain: ArrayAdapter<String> =
                ArrayAdapter<String>(
                    requireActivity(),
                    spiridonov.smart_house.R.layout.support_simple_spinner_dropdown_item,
                    allrooms
                )
            adaptermain.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adaptermain


            val adapterdate: ArrayAdapter<String> =
                ArrayAdapter<String>(
                    requireActivity(),
                    spiridonov.smart_house.R.layout.support_simple_spinner_dropdown_item,
                    arrayOf("Сегодня", "Вчера", "Позавчера")
                )
            adapterdate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerDate.adapter = adapterdate


        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fanWorking() {
        for (item in sensors) {
            if (item[0] == "isFanWork")
                isFanWorking = item[1] == "true"
            if (item[0] == "whenTurnOnFan")
                whenTurnOnFan = item[1].toFloat()
        }
        if (isFanWorking) {
            binding.fantxt.text = "Вентилятор работает"
            binding.fanbtn.text = "Выключить вентилятор"
        } else {
            binding.fantxt.text = "Вентилятор отключён"
            binding.fanbtn.text = "Включить вентилятор"
        }
        binding.whenTurnOnFantxt.setText(whenTurnOnFan.toString())
    }

    private fun lightWorking() {
        for (item in sensors) {
            if (item[0] == "isLightOn")
                isLightOn = item[1] == "true"
        }
        if (isLightOn) {
            binding.lighttxt.text = "Свет включён"
            binding.lightbtn.text = "Выключить свет"
        } else {
            binding.lighttxt.text = "Свет отключён"
            binding.lightbtn.text = "Включить свет"
        }
    }

    private fun dateToCalendar(date: Date): Calendar {
        val calendar = Calendar.getInstance()
        calendar.clear(Calendar.ZONE_OFFSET)
        calendar.time = date
        return calendar
    }

    private fun readSQLDB(name: String, sensor: Int, whatDayGet: String) {
        val dateFromDB = arrayListOf<Array<String>>()
        val thread = Thread {
            try {
                Class.forName("org.postgresql.Driver")
                val connection = DriverManager.getConnection(url, user, pass)
                val st: Statement = connection.createStatement()
                val rs: ResultSet =
                    st.executeQuery("select * from $name WHERE sensor = $sensor AND (CAST(date AS DATE) = CAST('$whatDayGet' AS DATE)) ORDER BY date;")
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
                    val buffArray = arrayOf(
                        day.toString(),
                        month.toString(),
                        hour.toString(),
                        min.toString(),
                        temperature.toString(),
                        humidity.toString()
                    )
                    dateFromDB.add(buffArray)
                }
            } catch (e: Exception) {
                Log.d("DB", e.toString())
            }
            Handler(Looper.getMainLooper()).post {
                buildCharts(dateFromDB = dateFromDB)
            }
        }
        thread.start()
    }

    private fun buildCharts(dateFromDB: ArrayList<Array<String>>) {
        val mCubicValueLineChartHumidity = binding.cubiclinechartHumidity
        val series = ValueLineSeries()
        series.color = Color.parseColor("#52BF25")
        val mCubicValueLineChartTemperature = binding.cubiclinechartTemperature
        val seriestemp = ValueLineSeries()
        seriestemp.color = -0xa9480f
        mCubicValueLineChartHumidity.clearChart()
        mCubicValueLineChartTemperature.clearChart()

        for (date in dateFromDB) {
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