package com.ariefrahman.myrealtimeapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(private val list: List<User>, val itemClicked: OnItemClicked) :
    RecyclerView.Adapter<UserAdapter.UserAdapterViewHolder>() {
    inner class UserAdapterViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(data: User) {
            view.apply {
                val tvName = findViewById<TextView>(R.id.tvName)
                val tvNim = findViewById<TextView>(R.id.tvNim)
                val btnEdit = findViewById<AppCompatImageButton>(R.id.btnEdit)
                val btnDelete = findViewById<AppCompatImageButton>(R.id.btnDelete)

                tvName.text = data.name
                tvNim.text = data.nim

                /**
                 * handling button edit clicked
                 */
                btnEdit.setOnClickListener {
                    itemClicked.editClicked(data)
                }

                /**
                 * handling button delete clicked
                 */
                btnDelete.setOnClickListener {
                    itemClicked.deleteClicked(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserAdapter.UserAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserAdapter.UserAdapterViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size
}

interface OnItemClicked {
    fun editClicked(data: User)

    fun deleteClicked(data: User)
}