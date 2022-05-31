package dev.acuon.chatapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import dev.acuon.chatapp.R
import dev.acuon.chatapp.model.Message

class MessageAdapter(private val context: Context, private val messageList: ArrayList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVED = 1
    val ITEM_SENT = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            val view =
                LayoutInflater.from(context).inflate(R.layout.sent_message_layout, parent, false)
            return SentMessageViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(context).inflate(R.layout.received_message_layout, parent, false)
            return ReceivedMessageViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)) {
            return ITEM_RECEIVED
        } else {
            return ITEM_SENT
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.javaClass == SentMessageViewHolder::class.java) {
            // sent message view holder
            val viewHolder = holder as SentMessageViewHolder
            viewHolder.setData(messageList[position])
        } else {
            // received message view holder
            val viewHolder = holder as ReceivedMessageViewHolder
            viewHolder.setData(messageList[position])
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(message: Message) {
            val sentMessage = itemView.findViewById<TextView>(R.id.sent_message)
            sentMessage.text = message.message
        }
    }

    class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(message: Message) {
            val receivedMessage = itemView.findViewById<TextView>(R.id.received_message)
            receivedMessage.text = message.message
        }
    }

}