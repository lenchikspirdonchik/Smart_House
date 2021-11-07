package spiridonov.smart_house.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import spiridonov.smart_house.MainActivity
import spiridonov.smart_house.databinding.FragmentHomeBinding
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

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

        this.url = String.format(this.url, this.host, this.port, this.database)

      Class.forName("org.postgresql.Driver::class.java")
        val connection: Connection
        try {
            connection = DriverManager.getConnection(url, user, pass)
            textView.text = textView.text.toString() + "\n" + "DriverManager.getConnection(url, user, pass)"
        } catch (e: Exception) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
            textView.text = textView.text.toString() + "\n" + e.toString()
        }
        var text = "Влажность в гостиной:\n"
        val handler = Handler()

        //val thread = Thread {
        try {
            textView.text = textView.text.toString() + "\n" + "try"

            val connection = DriverManager.getConnection(url, user, pass)
            Toast.makeText(context, "if3", Toast.LENGTH_SHORT).show()
            val st: Statement = connection.createStatement()
            Toast.makeText(
                context,
                "select * from $name ORDER BY date DESC",
                Toast.LENGTH_SHORT
            )
                .show()
            val rs: ResultSet = st.executeQuery("select * from $name ORDER BY date DESC")
            while (rs.next()) {
                val date = rs.getDate("date")?.toString()
                val temperature = rs.getInt("temperature")
                val humidity = rs.getInt("humidity")
                text = "$text$date: Температура: $temperature; Влажность: $humidity\n"
                handler.post {
                    textView.text = text
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
            textView.text = textView.text.toString() + "\nerr  "+ e.toString()
        }
        //  }
        ///   if (name != "") {


        // thread.start()
        // }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}