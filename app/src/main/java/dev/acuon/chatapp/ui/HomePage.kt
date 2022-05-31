package dev.acuon.chatapp.ui

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import dev.acuon.chatapp.R
import dev.acuon.chatapp.databinding.ActivityHomePageBinding
import dev.acuon.chatapp.databinding.UserDetailsBinding
import dev.acuon.chatapp.model.User
import dev.acuon.chatapp.ui.adapter.UserClickListener
import dev.acuon.chatapp.ui.adapter.UsersAdapter

class HomePage : AppCompatActivity(), UserClickListener {
    private lateinit var binding: ActivityHomePageBinding
    private lateinit var usersList: ArrayList<User>
    private lateinit var userAdapter: UsersAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressbar.visibility = View.VISIBLE
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference
        usersList = ArrayList()
        userAdapter = UsersAdapter(this@HomePage, usersList, this@HomePage)
        binding.apply {
            rcvUsers.apply {
                layoutManager = LinearLayoutManager(this@HomePage)
                adapter = userAdapter
            }
        }
        mDbRef.child("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                usersList.clear()
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)
                    if(mAuth.currentUser?.uid != currentUser?.uid) {
                        usersList.add(currentUser!!)
                    }
                }
                userAdapter.notifyDataSetChanged()
                binding.progressbar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                binding.apply {
                    progressbar.visibility = View.GONE
                }
            }

        })
    }

    override fun onClick(position: Int) {
        Toast.makeText(
            this@HomePage,
            usersList[position].name.toString() + " clicked",
            Toast.LENGTH_SHORT
        ).show()

        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("name", usersList[position].name)
        intent.putExtra("uid", usersList[position].uid)
        startActivity(intent)
//        val view = UserDetailsBinding.inflate(layoutInflater)
//        val alertDialog = AlertDialog.Builder(this@HomePage).create()
//        alertDialog.setCancelable(false)
//        view.apply {
//            userName.text = usersList[position].name
//            userEmail.text = usersList[position].email
//            userUid.text = usersList[position].uid
//            cancel.setOnClickListener {
//                alertDialog.cancel()
//            }
//        }
//        alertDialog.setView(view.root)
//        alertDialog.show()
    }
}