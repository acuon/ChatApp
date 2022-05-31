package dev.acuon.chatapp.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import dev.acuon.chatapp.R
import dev.acuon.chatapp.ui.fragments.Login

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences("currentUser", MODE_PRIVATE)
        mAuth = FirebaseAuth.getInstance()
        checkLogin()
    }
    private fun checkLogin() {
        if (sharedPreferences == null) {
            sharedPreferences = getSharedPreferences("currentUser", MODE_PRIVATE)
        }
        val email: String = sharedPreferences.getString("email", "")!!
        val password: String = sharedPreferences.getString("password", "")!!
        if (email != null && email != "" && password != null && password != "") {
            login(email, password)
        } else {
            openFragment(Login())
        }
    }
    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(
            R.id.frameLayout_for_Fragments,
            fragment
        ).commit()
    }
    private fun login(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this@MainActivity) { task ->
                if (task.isSuccessful) {
                    goToHomepage()
                } else {
                    openFragment(Login())
                }
            }
    }
    private fun goToHomepage() {
        val intent = Intent(this, HomePage::class.java)
        finish()
        startActivity(intent)
    }

}