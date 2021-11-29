package com.ariefrahman.myrealtimeapp

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase

fun showEditDialog(context: Context, user: User, db: FirebaseDatabase) {
    val dialog = Dialog(context)
    dialog.setCancelable(false)
    dialog.setContentView(R.layout.layout_edit_user)
    dialog.window?.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )

    val etName = dialog.findViewById(R.id.etEditName) as EditText
    val etNim = dialog.findViewById(R.id.etEditNim) as EditText

    etName.setText(user.name)
    etNim.setText(user.nim)

    val editBtn = dialog.findViewById(R.id.btnEdit) as Button
    val cancelBtn = dialog.findViewById(R.id.btnCancel) as Button

    editBtn.setOnClickListener {
        if (etName.text.isNotEmpty() && etNim.text.isNotEmpty()) {
            val updatedData = mapOf<String, String>(
                "name" to etName.text.toString().trim(),
                "nim" to etNim.text.toString()
            )

            /**
             * Update data to firebase
             */
            db.getReference("user").child(user.id).updateChildren(updatedData)
                .addOnSuccessListener {
                    dialog.dismiss()
                    Toast.makeText(context, "Data berhasil diupdate", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                Toast.makeText(context, "Data berhasil diupdate", Toast.LENGTH_SHORT).show()
            }
        }
    }
    cancelBtn.setOnClickListener { dialog.dismiss() }
    dialog.show()
}