package dev.acuon.chatapp.ui.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import dev.acuon.chatapp.R
import dev.acuon.chatapp.databinding.FragmentLoginBinding
import dev.acuon.chatapp.ui.HomePage
import java.util.regex.Pattern


class Login : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var mAuth: FirebaseAuth
    private val expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        sharedPreferences = activity?.applicationContext!!.getSharedPreferences("currentUser", AppCompatActivity.MODE_PRIVATE)

        binding.apply {
            loginSignUpBtn.setOnClickListener {
                openFragment(SignUp())
            }
            loginBtn.setOnClickListener {
                if (emptyCheck()) {
                    loginLayout.alpha = 0.5f
                    progressbar.visibility = View.VISIBLE
                    val email = loginEmail.text.toString()
                    val password = loginPassword.text.toString()
                    login(email, password)
                }
            }
            showHideBtn.setOnClickListener {
                if (loginPassword.transformationMethod
                        .equals(PasswordTransformationMethod.getInstance())
                ) {
                    loginPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    showHideBtn.text = "Hide"
                } else {
                    loginPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                    showHideBtn.text = "Show"
                }
            }
        }
    }

    //logic for login
    private fun login(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // code for logging in user
                    binding.progressbar.visibility = View.GONE
                    saveToSharedPreference(email, password)
                    val intent = Intent(requireContext(), HomePage::class.java)
                    activity?.finish()
                    startActivity(intent)
                } else {
                    binding.apply {
                        loginLayout.alpha = 1f
                        progressbar.visibility = View.GONE
                    }
                    toast("User does not exist")
                }
            }
    }

    private fun saveToSharedPreference(email: String?, password: String?) {
        if (sharedPreferences == null)
            sharedPreferences = activity?.applicationContext!!.getSharedPreferences("currentUser", AppCompatActivity.MODE_PRIVATE)

        sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putString("email", email)
        sharedPreferencesEditor.putString("password", password)
        sharedPreferencesEditor.commit();
    }

    private fun emptyCheck(): Boolean {
        var emailCheck = false
        var passwordCheck = false
        binding.apply {
            val email = loginEmail.text.toString()
            val password = loginPassword.text.toString()
            if (email.isEmpty()) {
                loginEmail.error = "Enter the email address"
            } else {
                if (isValidEmail(email)) {
                    emailCheck = true
                } else {
                    loginEmail.error = "Enter valid email address"
                }
            }
            when {
                password.length >= 6 -> {
                    passwordCheck = true
                }
                password.isEmpty() -> {
                    loginPassword.error = "Password cannot be empty"
                }
                else -> {
                    loginPassword.error = "Password length cannot be less then 6"
                }
            }
        }
        return emailCheck && passwordCheck
    }

    private fun isValidEmail(email: CharSequence): Boolean {
        var isValid = true
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        if (!matcher.matches()) {
            isValid = false
        }
        return isValid
    }

    private fun openFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(
            R.id.frameLayout_for_Fragments,
            fragment
        ).commit()
    }
    private fun toast(str: String) {
        Toast.makeText(requireContext(), str, Toast.LENGTH_SHORT).show()
    }

}