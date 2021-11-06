package spiridonov.smart_house

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import spiridonov.smart_house.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val msp = this.getSharedPreferences("AppMemory", Context.MODE_PRIVATE)
        var name = ""
        if (msp.contains(KEY_LOGIN)) name = msp.getString(KEY_LOGIN, "").toString()
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
        val mAuth = FirebaseAuth.getInstance()
        var firebaseUser = mAuth.currentUser
        if (firebaseUser == null || name == "") {
            val mintent = Intent(this, SignInActivity::class.java)
            startActivityForResult(mintent, 1)
        } else Toast.makeText(
            this, "Good morning, $name",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            recreate()
        }
    }

    companion object {
        private const val KEY_LOGIN = "login"
    }
}