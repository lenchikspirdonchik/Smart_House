package spiridonov.smart_house

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import spiridonov.smart_house.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val msp = this.getSharedPreferences("AppMemory", Context.MODE_PRIVATE)
        var name = ""
        if (msp.contains(KEY_LOGIN)) name = msp.getString(KEY_LOGIN, "").toString()
        val mAuth = FirebaseAuth.getInstance()
        var firebaseUser = mAuth.currentUser
        if (firebaseUser == null || name == "") {
            val mintent = Intent(this, SignInActivity::class.java)
            startActivityForResult(mintent, 1)
        }

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            recreate()
        }
    }

    companion object {
        const val KEY_LOGIN = "login"
    }
}