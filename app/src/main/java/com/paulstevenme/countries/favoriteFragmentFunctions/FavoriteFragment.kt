package com.paulstevenme.countries.favoriteFragmentFunctions

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.paulstevenme.countries.DatabaseClient
import com.paulstevenme.countries.R
import com.paulstevenme.countries.database.entity.Note
import com.paulstevenme.countries.homeFragmentFunctions.HomeRecyclerAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class FavoriteFragment: Fragment() {
    lateinit var fav_no_favorites_linear_layout: LinearLayout
    lateinit var fav_data_linear_layout:LinearLayout
    var fav_rv_country_list: RecyclerView? = null
    val MY_PREFS_NAME = "CountryOfflineStore"
    var countryOfflineStoreSP: SharedPreferences? = null
    var fav_country_list_str: String? = null
    var fav_country_list: ArrayList<String?> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_favorite, container, false)
        val placeHolder = view as ViewGroup

        fav_no_favorites_linear_layout = view.findViewById(R.id.fav_no_favorites_linear_layout)
        fav_data_linear_layout = view.findViewById(R.id.fav_data_linear_layout)
        fav_rv_country_list = view.findViewById(R.id.fav_rv_country_list)


        countryOfflineStoreSP = this.activity?.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE)
        fav_country_list_str = countryOfflineStoreSP?.getString("fav_country_list_str", "")
        if (fav_country_list_str != "") {
            fav_country_list = ArrayList(Arrays.asList(*fav_country_list_str!!.split(",").toTypedArray()))
            //            Remove Spaces in List of Strings
            for (i in fav_country_list.indices) {
                (fav_country_list ).set(i, fav_country_list[i]!!.trim { it <= ' ' })
            }
            getFavFragmentItems()
        } else {
            fav_no_favorites_linear_layout.setVisibility(View.VISIBLE)
            fav_data_linear_layout.setVisibility(View.GONE)
        }


        return placeHolder



    }

    private fun getFavFragmentItems() {
        getFavCountryNamesAndFlags()
    }

    private fun getFavCountryNamesAndFlags() {
        val fav_notes: MutableList<Note> = ArrayList()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                try {
                    for (i in fav_country_list.indices) {
                        val s = fav_country_list[i]
                        val note = DatabaseClient.getInstance(context).noteDatabase.noteDao()
                            .getNoteByTitle(s)
                        fav_notes.add(note)
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: java.lang.Exception) {
                    println("error on fetching")
                    println(e)
                }
                withContext(Dispatchers.Main) {
                    try {
                        fav_no_favorites_linear_layout.visibility = View.GONE
                        fav_data_linear_layout.visibility = View.VISIBLE
                        val adapter = HomeRecyclerAdapter(activity, fav_notes)
                        fav_rv_country_list!!.adapter = adapter
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateContent() {
        countryOfflineStoreSP = this.activity?.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE)
        fav_country_list_str = countryOfflineStoreSP?.getString("fav_country_list_str", "")
        if (fav_country_list_str != "") {
            fav_country_list = ArrayList(Arrays.asList(*fav_country_list_str!!.split(",").toTypedArray()))
            //            Remove Spaces in List of Strings
            for (i in fav_country_list.indices) {
                fav_country_list[i] = fav_country_list[i]?.trim { it <= ' ' }
            }
            getFavFragmentItems()
        } else {
            fav_no_favorites_linear_layout.visibility = View.VISIBLE
            fav_data_linear_layout.visibility = View.GONE
        }
    }
}