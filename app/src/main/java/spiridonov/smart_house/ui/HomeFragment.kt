package spiridonov.smart_house.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import spiridonov.smart_house.MainActivity
import spiridonov.smart_house.databinding.FragmentHomeBinding
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val textView: TextView = binding.textHome
        val msp = requireActivity().getSharedPreferences("AppMemory", Context.MODE_PRIVATE)
        var name = ""
        if (msp.contains(MainActivity.KEY_LOGIN)) name =
            msp.getString(MainActivity.KEY_LOGIN, "").toString()

        url = String.format(url, host, port, database)
        var text = "Влажность в гостиной:\n"
        val handler = Handler()
        val thread = Thread {
            try {
                Class.forName("org.postgresql.Driver")
                val connection = DriverManager.getConnection(url, user, pass)
                val st: Statement = connection.createStatement()
                val rs: ResultSet = st.executeQuery("select * from $name ORDER BY date DESC")
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
                    text =
                        "$text$day.$month.$year $hour:$min: Температура: $temperature; Влажность: $humidity\n"
                    handler.post {
                        textView.text = text
                    }
                }
            } catch (e: Exception) {
                Log.d("DB", e.toString())
            }
        }
        if (name != "") {
            thread.start()
        }


        return root
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
}