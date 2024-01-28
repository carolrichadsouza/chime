package com.example.chimeapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context : Context, val messageList : ArrayList<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val receiveitem = 1
    val sentitem = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if(viewType == 1) {

            val view : View = LayoutInflater.from(context).inflate(R.layout.receive, parent,false)
            return ReceiveViewHolder(view)

        }
        else {
            val view : View = LayoutInflater.from(context).inflate(R.layout.sent, parent,false)
            return SentViewHolder(view)
        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentmessage = messageList[position]

        if(holder.javaClass == SentViewHolder::class.java) {
            val viewHolder = holder as SentViewHolder

            holder.sentmessage.text = currentmessage.message
        }
        else {
            val viewHolder = holder as ReceiveViewHolder
            holder.receivemessage.text = currentmessage.message
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]

        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderID)) {
            return sentitem
        }
        else {
            return receiveitem
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class SentViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val sentmessage = itemView.findViewById<TextView>(R.id.txtsentmessage)

    }

    class ReceiveViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val receivemessage = itemView.findViewById<TextView>(R.id.txtreceivemessage)
    }

}