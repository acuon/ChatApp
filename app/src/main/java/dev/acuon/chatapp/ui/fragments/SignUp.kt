package dev.acuon.chatapp.ui.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dev.acuon.chatapp.R
import dev.acuon.chatapp.databinding.FragmentSignUpBinding
import dev.acuon.chatapp.model.User
import dev.acuon.chatapp.ui.HomePage
import dev.acuon.chatapp.utils.Constants
import dev.acuon.chatapp.utils.toast
import java.util.regex.Pattern

class SignUp : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()

        sharedPreferences = activity?.applicationContext!!.getSharedPreferences(
            Constants.SHARED_PREFERENCE_KEY,
            AppCompatActivity.MODE_PRIVATE
        )

        binding.apply {
            signUpLoginBtn.setOnClickListener {
                openFragment(Login())
            }
            signUpBtn.setOnClickListener {
                if (emptyCheck()) {
                    signUpLayout.alpha = 0.5f
                    progressbar.visibility = View.VISIBLE
                    val name = signUpName.text.toString()
                    val email = signUpEmail.text.toString()
                    val password = signUpPassword.text.toString()
                    signUp(name, email, password)
                }
            }
            showHideBtn.setOnClickListener {
                if (signUpPassword.transformationMethod
                        .equals(PasswordTransformationMethod.getInstance())
                ) {
                    signUpPassword.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    showHideBtn.text = Constants.HIDE
                } else {
                    signUpPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                    showHideBtn.text = Constants.SHOW
                }
            }
        }
    }

    private fun signUp(name: String, email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    binding.progressbar.visibility = View.GONE
                    // code for if login is successful
                    //adding user to database
                    addUserToDatabase(name, email, mAuth.uid!!)
                    saveToSharedPreference(email, password)
                    // i.e jumping to HomePage
                    val intent = Intent(requireContext(), HomePage::class.java)
                    activity?.finish()
                    startActivity(intent)
                    toast(requireContext(), "Sign-Up Successful")
                } else {
                    binding.apply {
                        signUpLayout.alpha = 1f
                        progressbar.visibility = View.GONE
                    }
                    // If sign in fails, display a message to the user.
                    toast(requireContext(), "Some Error Occurred")
                }
            }
    }


    private fun addUserToDatabase(name: String, email: String, uid: String) {
        mDbRef = FirebaseDatabase.getInstance().reference
        val user = User(name, email, uid)
        mDbRef.child("users").child(uid).setValue(user)
    }

    private fun saveToSharedPreference(email: String?, password: String?) {
        if (sharedPreferences == null)
            sharedPreferences = activity?.applicationContext!!.getSharedPreferences(
                Constants.SHARED_PREFERENCE_KEY,
                AppCompatActivity.MODE_PRIVATE
            )

        sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putString("email", email)
        sharedPreferencesEditor.putString("password", password)
        sharedPreferencesEditor.commit();
    }

    private fun emptyCheck(): Boolean {
        var emailCheck = false
        var nameCheck = false
        var passwordCheck = false
        binding.apply {
            val email = signUpEmail.text.toString()
            var name = signUpName.text.toString()
            val password = signUpPassword.text.toString()
            if (email.isEmpty()) {
                signUpEmail.error = "Enter the email address"
            } else {
                if (isValidEmail(email)) {
                    emailCheck = true
                } else {
                    signUpEmail.error = "Enter valid email address"
                }
            }
            when {
                name.isEmpty() -> {
                    signUpName.error = "Name cannot be empty"
                }
                name.length < 3 -> {
                    signUpName.error = "Name length should be greater than 3"
                }
                else -> {
                    nameCheck = true
                }
            }
            when {
                password.length >= 6 -> {
                    passwordCheck = true
                }
                password.isEmpty() -> {
                    signUpPassword.error = "Password cannot be empty"
                }
                else -> {
                    signUpPassword.error = "Password length cannot be less then 6"
                }
            }
        }
        return emailCheck && nameCheck && passwordCheck
    }

    private fun isValidEmail(email: CharSequence): Boolean {
        var isValid = true
        val pattern = Pattern.compile(Constants.EMAIL_EXPRESSION, Pattern.CASE_INSENSITIVE)
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
}