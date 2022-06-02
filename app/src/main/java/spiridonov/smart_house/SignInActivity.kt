package spiridonov.smart_house

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        val mAuth = FirebaseAuth.getInstance()
        val msp = this.getSharedPreferences("AppMemory", Context.MODE_PRIVATE)
        btnLogin.setOnClickListener {
            PbarLogin.visibility = View.VISIBLE
            val signIn = Thread(Runnable {
                mAuth.signInAnonymously()
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d("TAG", "signInAnonymously:success")
                            val login = txtEmail.text.toString()
                            val password = txtPassword.text.toString()
                            val database = FirebaseDatabase.getInstance().reference
                            val reference = database.child(login).child("password")
                            reference.get().addOnSuccessListener {
                                val goodPass = it.value.toString()
                                if (goodPass == password) {
                                    val editor = msp.edit()
                                    editor.putString(KEY_LOGIN, login)
                                    editor.apply()
                                    val mintent = Intent()
                                    setResult(Activity.RESULT_OK, mintent)
                                    finish()
                                }
                            }.addOnFailureListener {
                                Toast.makeText(
                                    baseContext, it.toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                                PbarLogin.visibility = View.INVISIBLE
                                Log.d("Log", "${task.exception}")
                                btnLogin.error = "Invalid data"
                                btnLogin.requestFocus()
                            }
                        } else {
                            Log.w("TAG", "signInAnonymously:failure", task.exception)
                            Toast.makeText(
                                baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                            PbarLogin.visibility = View.INVISIBLE
                            Log.d("Log", "${task.exception}")
                            btnLogin.error = "Invalid data"
                            btnLogin.requestFocus()
                        }
                    }
            })
            signIn.start()

        }
    }

    companion object {
        private const val KEY_LOGIN = "login"
    }
}