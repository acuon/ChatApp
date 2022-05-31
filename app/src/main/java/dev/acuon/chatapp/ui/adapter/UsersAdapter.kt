package dev.acuon.chatapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.acuon.chatapp.R
import dev.acuon.chatapp.model.User

class UsersAdapter(private val context: Context, private val list: ArrayList<User>, private val clickListener: UserClickListener) :
    RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.users_layout, parent, false)
        return ViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = list[position]
        holder.setData(user)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View, private val clickListener: UserClickListener) : RecyclerView.ViewHolder(itemView) {
        fun setData(user: User) {
            val userName = itemView.findViewById<TextView>(R.id.user_name)
            userName.text = user.name
            itemView.setOnClickListener {
                clickListener.onClick(adapterPosition)
            }
        }
    }
}