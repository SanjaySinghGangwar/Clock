package com.sanjaysgangwar.clock.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.sanjaysgangwar.clock.R
import com.sanjaysgangwar.clock.databinding.AllAppsBinding
import com.sanjaysgangwar.clock.mViewHolder.AppsViewHolder
import com.sanjaysgangwar.clock.modelClass.AppSharePreference
import com.sanjaysgangwar.clock.modelClass.AppsModelClass
import com.sanjaysgangwar.clock.utils.NetworkUtil.isOnline
import com.sanjaysgangwar.clock.utils.mUtils.showToast
import com.squareup.picasso.Picasso


class AllApps : AppCompatActivity() {

    private lateinit var bind: AllAppsBinding
    private lateinit var myRef: DatabaseReference
    lateinit var database: FirebaseDatabase
    var sharedPreferences: AppSharePreference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = AllAppsBinding.inflate(layoutInflater)
        setContentView(bind.root)

        initAllComponents()
        //phoneListener()
    }

    private fun initAllComponents() {
        sharedPreferences = AppSharePreference(this)
        database = FirebaseDatabase.getInstance()
        myRef = database.getReference("Common")
                .child("apps")
        bind.recycler.layoutManager = GridLayoutManager(applicationContext, 2)
    }

    override fun onStart() {
        super.onStart()
        if (isOnline(applicationContext)) {
            showToast(applicationContext, "Please Wait ")
            initRecycler()
        } else {
            onBackPressed()
            showToast(applicationContext, "No Internet Connection")
        }
    }

    private fun initRecycler() {
        val option: FirebaseRecyclerOptions<AppsModelClass> =
                FirebaseRecyclerOptions.Builder<AppsModelClass>()
                        .setQuery(myRef.orderByChild("name"), AppsModelClass::class.java)
                        .build()
        val recyclerAdapter =
                object : FirebaseRecyclerAdapter<AppsModelClass, AppsViewHolder>(option) {
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppsViewHolder {
                        val view =
                                LayoutInflater.from(applicationContext)
                                        .inflate(R.layout.show_all_apps, parent, false)
                        return AppsViewHolder(view)
                    }

                    override fun onBindViewHolder(
                            holder: AppsViewHolder,
                            position: Int,
                            model: AppsModelClass
                    ) {
                        holder.name.text = model.name
                        holder.quote.text = model.quote
                        Picasso.get()
                                .load(model.image)
                                .into(holder.image)
                        holder.card.setOnClickListener { click ->
                            operationToPerform(model.link)

                        }
                    }


                }

        bind.recycler.adapter = recyclerAdapter
        recyclerAdapter.startListening()
    }

    private fun operationToPerform(link: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(link)
        startActivity(i)
    }


}