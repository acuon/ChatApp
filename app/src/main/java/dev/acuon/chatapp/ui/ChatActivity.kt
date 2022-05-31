package dev.acuon.chatapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import dev.acuon.chatapp.databinding.ActivityChatBinding
import dev.acuon.chatapp.model.Message
import dev.acuon.chatapp.ui.adapter.MessageAdapter

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference

    var senderRoom: String? = null
    var receiverRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val name = intent.getStringExtra("name");
        val receiverUid = intent.getStringExtra("uid")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        mDbRef = FirebaseDatabase.getInstance().reference
        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)
        binding.apply {
            receiverName.text = name
            sendBtn.setOnClickListener {
                val chat = etChatMessage.text.toString()
                if (chat.isNotEmpty()) {
                    val message = Message(chat, senderUid)
                    mDbRef.child("chats").child(senderRoom!!).child("message").push()
                        .setValue(message).addOnSuccessListener {
                            mDbRef.child("chats").child(receiverRoom!!).child("message").push()
                                .setValue(message)
                        }
                    refresh()
                    etChatMessage.setText("")
                }
            }
            rcvChats.apply {
                adapter = messageAdapter
                val linearLayoutManager = LinearLayoutManager(this@ChatActivity)
                linearLayoutManager.stackFromEnd = true
                layoutManager = linearLayoutManager
            }
        }

        mDbRef.child("chats").child(senderRoom!!).child("message")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
    private fun refresh() {
        val position = binding.rcvChats.adapter?.itemCount!!
        binding.rcvChats.smoothScrollToPosition(position)
        messageAdapter.notifyDataSetChanged();
    }
}