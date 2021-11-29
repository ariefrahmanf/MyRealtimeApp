package com.ariefrahman.myrealtimeapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class MainActivity : AppCompatActivity(), OnItemClicked {
    private lateinit var etName: EditText
    private lateinit var etNim: EditText
    private lateinit var btnSave: Button
    private lateinit var rvUser: RecyclerView
    private val db by lazy { RealtimeDatabase.instance() }
    private val listUser = mutableListOf<User>()
    private val listKey = mutableListOf<String>()
    private lateinit var adapterUser: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewId()
        btnSave.apply {
            setOnClickListener {
                setDataToDatabase(getUserData())
            }
        }

        getDataFromDatabase()
    }

    /**
     * init view component
     */
    private fun findViewId() {
        etName = findViewById(R.id.etName)
        etNim = findViewById(R.id.etNim)
        btnSave = findViewById(R.id.btnSave)
    }

    /**
     * get user data from view
     */
    private fun getUserData(): User {
        return User(
            id = UUID.randomUUID().toString(),
            name = etName.text.toString().trim(),
            nim = etNim.text.toString()
        )
    }


    /**
     * write data to database
     */
    private fun setDataToDatabase(user: User) {
        if (user.name.isNotEmpty() && user.nim.isNotEmpty()) {
            btnSave.isEnabled = false
            db.getReference("user").child(user.id).setValue(user).addOnSuccessListener {
                etName.setText("")
                etNim.setText("")
                btnSave.isEnabled = true

                Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                btnSave.isEnabled = true

                Toast.makeText(this, "Data gagal disimpan", Toast.LENGTH_SHORT).show()
            }
        }
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        /**
         * Hiding keyboard when save button clicked
         */
        imm.hideSoftInputFromWindow(etName.windowToken, 0)
        imm.hideSoftInputFromWindow(etNim.windowToken, 0)
    }

    /**
     * read data from database
     */
    private fun getDataFromDatabase() {
        val progressBar = findViewById<ProgressBar>(R.id.pbCircular)
        progressBar.visibility = View.VISIBLE

        db.getReference("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                progressBar.visibility = View.GONE
                listKey.clear()
                listUser.clear()

                /**
                 * get key (user id) from user
                 */
                snapshot.children.map {
                    it.key?.let { userId ->
                        listKey.add(userId)
                    }
                }

                /**
                 * get data user based on key (user id)
                 */
                listKey.map {
                    listUser.add(
                        User(
                            id = snapshot.child(it).child("id").value.toString(),
                            name = snapshot.child(it).child("name").value.toString(),
                            nim = snapshot.child(it).child("nim").value.toString()
                        )
                    )
                }

                /**
                 * 1. Initialize adapter for recycler view
                 * 2. Setup recycler view with adapter
                 */
                adapterUser = UserAdapter(listUser.sortedBy { it.name }, this@MainActivity)
                initRecyclerView()
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
                Log.e("DB ERROR", error.message)
            }
        })
    }

    /**
     * setup recycler view
     */
    private fun initRecyclerView() {
        rvUser = findViewById(R.id.rvUser)
        rvUser.apply {
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            adapter = adapterUser
        }
    }

    /**
     * listener on click edit button
     */
    override fun editClicked(data: User) {
        showEditDialog(
            this,
            data,
            db
        )
    }

    /**
     * listener on click delete button
     */
    override fun deleteClicked(data: User) {
        db.getReference("user").child(data.id).removeValue().addOnSuccessListener {
            Toast.makeText(this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Data gagal dihapus", Toast.LENGTH_SHORT).show()
        }
    }
}