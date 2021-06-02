package com.paulstevenme.countries

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.paulstevenme.countries.favoriteFragmentFunctions.FavoriteFragment
import com.paulstevenme.countries.homeFragmentFunctions.HomeFragment
import com.paulstevenme.countries.utils.Helpers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException
import java.util.*

class HomeActivity : AppCompatActivity() {
    val fragment1: Fragment = HomeFragment()
    val fragment2: Fragment = FavoriteFragment()
    val fm = supportFragmentManager
    var active = fragment1
    lateinit  var sideDrawerLayout: DrawerLayout
    lateinit  var navigationView: NavigationView
    var home_toolbar: Toolbar? = null
    val MY_PREFS_NAME = "CountryOfflineStore"

    var DBFlag = false
    var countryOfflineStoreSPEditor: SharedPreferences.Editor? = null

    var pmAndPresidentURL = "https://en.m.wikipedia.org/wiki/List_of_current_heads_of_state_and_government"
    var pmNames = ArrayList<String>()
    var presidentNames = ArrayList<String>()
    var countryNames = ArrayList<String>()

    override fun onResume() {
        super.onResume()
        refreshSecFragment()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        //        Initializing Shared Preferences
        val countryOfflineStoreSP = this.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE)
        DBFlag = countryOfflineStoreSP.getBoolean("DBFlag", false)
        countryOfflineStoreSPEditor = countryOfflineStoreSP.edit()
        sideDrawerLayout = findViewById(R.id.sideDrawerLayout)
        home_toolbar = findViewById(R.id.home_toolbar)
        navigationView = findViewById(R.id.navigationView)

        val toggle = ActionBarDrawerToggle(this,sideDrawerLayout,home_toolbar, R.string.navigation_open,R.string.navigation_close)
        toggle.drawerArrowDrawable.color = Color.WHITE
        sideDrawerLayout.addDrawerListener(toggle)
        toggle.syncState()


//        Drawer Menu Item Click Functions
        navigationView.setNavigationItemSelectedListener { item: MenuItem ->
            val buttonView = item.itemId
            // Handle navigation view item clicks here.
            if (buttonView == R.id.m_about) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("About")
                builder.setMessage("""
                        ★ Know the Details of the Country
                        ★ Search the Country Name and Get Instant Results
                        ★ Like your Country and make it available in Favorites Tab.
                        ★ Give a Try.
                        ★ Enjoy.
                        """.trimIndent())
                builder.setPositiveButton("OK") { dialogInterface: DialogInterface?, i: Int -> }
                val alert = builder.create()
                alert.show()
                val oKButton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                oKButton.setBackgroundColor(ContextCompat.getColor(this, R.color.special_bg_color))
                oKButton.setTextColor(ContextCompat.getColor(this, R.color.white))
                oKButton.setOnClickListener { view: View? -> alert.dismiss() }
            } else if (buttonView == R.id.m_share) {
                try {
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Countries")
                    var shareMessage = "\nLet me recommend you this best Countries application\n\n"
                    shareMessage = """
                ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                
                
                """.trimIndent()
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                    startActivity(Intent.createChooser(shareIntent, "choose one"))
                } catch (e: Exception) {
                    //e.toString();
                }
            } else if (buttonView == R.id.m_version) {
                var versionCode_Name = ""
                val versionBuilder = AlertDialog.Builder(this)
                versionBuilder.setTitle("App Version")
                try {
                    val pInfo = Objects.requireNonNull(packageManager.getPackageInfo(packageName, 0))
                    val verCode = pInfo.versionName
                    versionCode_Name = "Countries App Version: $verCode"
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
                versionBuilder.setMessage(versionCode_Name)
                versionBuilder.setPositiveButton("OK") { dialogInterface: DialogInterface?, i: Int -> }
                val alert = versionBuilder.create()
                alert.show()
                val oKButton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                oKButton.setBackgroundColor(ContextCompat.getColor(this, R.color.special_bg_color))
                oKButton.setTextColor(ContextCompat.getColor(this, R.color.white))
                oKButton.setOnClickListener { view: View? -> alert.dismiss() }
            }

            //close navigation drawer
            sideDrawerLayout.closeDrawer(GravityCompat.START)
            true
        }


//        Bottom Navigation Bar
        val navigation:BottomNavigationView = findViewById(R.id.navigation)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        fm.beginTransaction().add(R.id.main_container, fragment2, "2").hide(fragment2).commit()
        fm.beginTransaction().add(R.id.main_container, fragment1, "1").commit()

        val helpers = Helpers(this)
        try{
            val internet_check = helpers.isInternetConnected()
            if(internet_check){
                try{
                    countryOfflineStoreSPEditor!!.putString("pmNames", "").apply()
                    countryOfflineStoreSPEditor!!.putString("presidentNames", "").apply()
                    countryOfflineStoreSPEditor!!.putString("countryNames", "").apply()
                    presidentPMFetcher();
                }
                catch(e:Exception){

                }
            }
        }
        catch(e:Exception){

        }




    }

    private val mOnNavigationItemSelectedListener =  BottomNavigationView.OnNavigationItemSelectedListener { item: MenuItem ->
        val buttonView = item.itemId
        // Handle navigation view item clicks here.
        if (buttonView == R.id.navigation_home) {
            fm.beginTransaction().hide(active).show(fragment1).commit()
            active = fragment1
            true
        } else if (buttonView == R.id.navigation_favorite) {
            fm.beginTransaction().hide(active).show(fragment2).commit()
            active = fragment2
            true
        }
        false
    }

    private fun presidentPMFetcher() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                try {
                    val document: Document
                    document = Jsoup.connect(pmAndPresidentURL).get()
                    val table = document.select("table")[1] //select the first table.
                    val rows = table.select("tr")
                    for (i in 1 until rows.size) { //first row is the col names so skip it.
                        val row = rows[i]
                        var cols_coun: Elements = row.select("th")
                        var cols: Elements = row.select("td")
                        try {
                            if (cols[1].text().contains("Prime Minister")) {
                                val pm_name = cols[1].text().replace("Prime Minister – ", "").trim { it <= ' ' }
                                pmNames.add(pm_name)
                            } else {
                                pmNames.add("-")
                            }
                        } catch (e: java.lang.Exception) {
                            pmNames.add("-")
                        }
                        try {
                            if (cols[0].text().contains("President")) {
                                val president_name = cols[0].text().replace("President – ", "").replace("[δ]", "").replace("[μ]", "").trim { it <= ' ' }
                                presidentNames.add(president_name)
                            } else {
                                presidentNames.add("-")
                            }
                        } catch (e: java.lang.Exception) {
                            presidentNames.add("-")
                        }
                        countryNames.add(cols_coun.text())
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: java.lang.Exception) {
                    println("error on fetching")
                    println(e)
                }
                withContext(Dispatchers.Main) {
                    try {
                        val gson:Gson = Gson()
                        val pmNamesSet = gson.toJson(pmNames)
                        val presidentNamesSet = gson.toJson(presidentNames)
                        val countryNamesSet = gson.toJson(countryNames)
                        countryOfflineStoreSPEditor!!.putString("pmNames", pmNamesSet).apply()
                        countryOfflineStoreSPEditor!!.putString("presidentNames", presidentNamesSet).apply()
                        countryOfflineStoreSPEditor!!.putString("countryNames", countryNamesSet).apply()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun refreshSecFragment() {
        val fragmentManager: FragmentManager = getSupportFragmentManager()
        val favoriteFragment:FavoriteFragment= fragmentManager.findFragmentByTag("2") as FavoriteFragment;
        favoriteFragment.updateContent()

    }
}