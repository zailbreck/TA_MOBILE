package com.kaorimaps.ssa

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View

import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.getInstance
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kaorimaps.ssa.config.*
import com.kaorimaps.ssa.config.cDrawer.ClickListener
import com.kaorimaps.ssa.config.cDrawer.NavigationItemModel
import com.kaorimaps.ssa.config.cDrawer.NavigationRVAdapter
import com.kaorimaps.ssa.config.cDrawer.RecyclerTouchListener
import com.kaorimaps.ssa.menu.*
import khttp.post
import kotlinx.android.synthetic.main.activity_home.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import java.net.SocketTimeoutException
import java.net.URL
import java.nio.charset.Charset


class HomeActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    private lateinit var adapter: NavigationRVAdapter
    lateinit var mAuth: FirebaseAuth
    lateinit var userActive: String
    lateinit var mapLogList : ArrayList<MapLog>
    lateinit var NewsLogList : ArrayList<NewsLog>
    lateinit var DestLogList : ArrayList<DestLog>
    lateinit var InfoLogList : ArrayList<InfoLog>
    var errorResult = ""

    private var items = arrayListOf(
        NavigationItemModel(R.drawable.ic_menu_map, "Peta"),
        NavigationItemModel(R.drawable.ic_menu_info, "Informasi"),
        NavigationItemModel(R.drawable.ic_menu_paper, "Berita & Acara"),
        NavigationItemModel(R.drawable.ic_menu_track, "Destinasi"),
        NavigationItemModel(R.drawable.ic_menu_profile, "Akun")
    )

    private fun isJSONValid(str: String) : Boolean{
        try{
            JSONObject(str)
        }catch (e: JSONException){
            try{
                JSONArray(str)
            }catch (ex: JSONException){
                return false
            }
        }
        return true
    }

    private fun getData(){
        NewsLogList = ArrayList()
        DestLogList = ArrayList()
        mapLogList = ArrayList()
        InfoLogList = ArrayList()


        // Get Data From Firebase
        try{
            val str = URL("https://m-ssa-ee17c-default-rtdb.firebaseio.com/building/MapLoc.json").readText(
                Charset.forName(
                    "UTF-8"
                )
            )
            if(isJSONValid(str)){
                val gson = Gson()
                val arrayTutorialType = object : TypeToken<Array<MapLog>>() {}.type
                var tutorials: Array<MapLog> = gson.fromJson(str, arrayTutorialType)
                tutorials.forEach {
                    mapLogList.add(MapLog(it.lat, it.lng, it.rad, it.title, it.icon))
                }
            }else{
                errorResult = "DataFailed"
            }
        }catch (e: SocketTimeoutException){
            Log.d("getdata", "RTO")
            errorResult = "RTO"
        }

        //News Load Data
        try {
            for(i in 1..2){ //Get 2 Page (20 Berita Terbaru)
                val x = post(
                    "http://medantourism.pemkomedan.go.id/app/api/news",
                    data = mapOf("page" to i.toString())
                )
                if(isJSONValid(x.text)){
                    val serializeX : JSONArray = x.jsonObject.getJSONArray("items")
                    for(j in 0 until serializeX.length()){
                        val od = serializeX.getJSONObject(j)
                        val raw = od.getString("image")
                        val uri : String = if(raw != ""){
                            raw.replace(" ", "%20")
                        }else{
                            "https://cdn3.iconfinder.com/data/icons/map-and-geo-location/30/map-pin-area-multiple-512.png"
                        }
                        NewsLogList.add(
                            NewsLog(
                                od.getString("id"), od.getString("title"), html2text(
                                    od.getString(
                                        "summary"
                                    )
                                ), html2text(od.getString("content")), od.getString("date"), uri
                            )
                        )
                    }
                }else{
                    errorResult = "DataFailed"
                }
            }
        }catch (e: SocketTimeoutException){
            Log.d("getdata", "RTO")
            errorResult = "RTO"
        }

        //Events Load Data
        try{
            val y = post(
                "http://medantourism.pemkomedan.go.id/app/api/events",
                data = mapOf("page" to "1")
            )
            if(isJSONValid(y.text)){
                val serializeY : JSONArray = y.jsonObject.getJSONArray("items")
                for(k in 0 until serializeY.length()){
                    val od = serializeY.getJSONObject(k)
                    val raw = od.getString("image")
                    val uri : String = if(raw != ""){
                        raw.replace(" ", "%20")
                    }else{
                        "https://cdn3.iconfinder.com/data/icons/map-and-geo-location/30/map-pin-area-multiple-512.png"
                    }
                    NewsLogList.add(
                        NewsLog(
                            od.getString("id"), od.getString("title"), html2text(
                                od.getString(
                                    "summary"
                                )
                            ), html2text(od.getString("content")), "", uri
                        )
                    )
                }
            }else{
                errorResult = "DataFailed"
            }
        }catch (e: SocketTimeoutException){
            Log.d("getdata", "RTO")
            errorResult = "RTO"
        }

        // Destinasi Load Data
        try{
            for(i in 1..2){ //Get 2 Page
                val x = post(
                    "http://medantourism.pemkomedan.go.id/app/api/destinations", data = mapOf(
                        "page" to i.toString(),
                        "category_id" to 3,
                        "latitude" to 3.595196,
                        "longitude" to 98.672226
                    )
                )
                if(isJSONValid(x.text)){
                    val serializeZ : JSONArray = x.jsonObject.getJSONArray("items")
                    for(j in 0 until serializeZ.length()){
                        val od = serializeZ.getJSONObject(j)
                        mapLogList.add(
                            MapLog(
                                od.getDouble("latitude"),
                                od.getDouble("longitude"),
                                25.0,
                                od.getString(
                                    "name"
                                ),
                                "ic_pin"
                            )
                        )
                        val raw = od.getString("image")
                        val uri : String = if(raw != ""){
                            raw.replace(" ", "%20")
                        }else{
                            "https://cdn3.iconfinder.com/data/icons/map-and-geo-location/30/map-pin-area-multiple-512.png"
                        }
                        DestLogList.add(
                            DestLog(
                                od.getString("id"),
                                od.getString("name"),
                                html2text(
                                    od.getString(
                                        "summary"
                                    )
                                ),
                                html2text(od.getString("content")),
                                od.getString("address"),
                                od.getString(
                                    "distance"
                                ),
                                od.getDouble("latitude"),
                                od.getDouble("longitude"),
                                uri
                            )
                        )
                    }
                }else{
                    errorResult = "DataFailed"
                }
            }
        }catch (e: SocketTimeoutException){
            Log.d("getdata", "RTO")
            errorResult = "RTO"
        }

        //Profile load Data
        try{
            val x = post("http://medantourism.pemkomedan.go.id/app/api/profile")
            if(isJSONValid(x.text)){
                val serializeZ : JSONArray = x.jsonObject.getJSONArray("items")
                for(j in 0 until serializeZ.length()){
                    val od = serializeZ.getJSONObject(j)
                    InfoLogList.add(
                        InfoLog(
                            od.getString("id"), od.getString("title"), html2text(
                                od.getString(
                                    "content"
                                )
                            )
                        )
                    )
                }
            }else{
                errorResult = "DataFailed"
            }
        }catch (e: SocketTimeoutException){
            Log.d("getdata", "RTO")
            errorResult = "RTO"
        }

    }

    override fun onStart() {
        super.onStart()
        getUserInfo()
    }

    private fun getUserInfo(){
        mAuth.let {
            userActive = mAuth.currentUser?.displayName.toString()
            userLoginName.text = "Selamat Datang, $userActive"
        }
    }

    private fun openFragment(title: String, fragment: Fragment) {
        val bundle = Bundle()
        bundle.putString("fragmentName", title)
        bundle.putString("errResult", errorResult)
        bundle.putString("userLogin", userActive)
        bundle.putParcelableArrayList("MapList", ArrayList(mapLogList))
        bundle.putParcelableArrayList("NewsList", ArrayList(NewsLogList))
        bundle.putParcelableArrayList("DestList", ArrayList(DestLogList))
        bundle.putParcelableArrayList("InfoList", ArrayList(InfoLogList))
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction()
                .replace(R.id.activity_main_content_id, fragment).commit()
    }

    private fun setupDrawer(){
        drawerLayout = findViewById(R.id.drawer_layout)


        // Set the toolbar
        setSupportActionBar(activity_main_toolbar)

        // Setup Recyclerview's Layout
        navigation_rv.layoutManager = LinearLayoutManager(this)
        navigation_rv.setHasFixedSize(true)

        // Add Item Touch Listener
        navigation_rv.addOnItemTouchListener(RecyclerTouchListener(this, object : ClickListener {
            override fun onClick(view: View, position: Int) {
                when (position) {
                    0 -> {
                        openFragment("PETA", MapsFragment())
                    }
                    1 -> {
                        openFragment("INFORMASI", InfoFragment())
                    }
                    2 -> {
                        openFragment("BERITA", NewsFragment())
                    }
                    3 -> {
                        openFragment("DESTINASI", DestFragment())
                    }
                    4 -> {
                        openFragment("AKUN", AccountFragment())
                    }

                }
                updateAdapter(position)
                // Don't highlight the 'Profile' and 'Like us on Facebook' item row
//                if (position != 5 ) {
//                    updateAdapter(position)
//                }

                Handler(Looper.getMainLooper()).postDelayed({
                    drawerLayout.closeDrawer(GravityCompat.START)
                }, 100)
            }
        }))

        // Update Adapter with item data and highlight the default menu item ('Home' Fragment)
        updateAdapter(0)


        // Home Fragment


        val toggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            drawerLayout,
            activity_main_toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {
            override fun onDrawerClosed(drawerView: View) {
                // Triggered once the drawer closes
                super.onDrawerClosed(drawerView)
                try {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                } catch (e: Exception) {
                    e.stackTrace
                }
            }

            override fun onDrawerOpened(drawerView: View) {
                // Triggered once the drawer opens
                super.onDrawerOpened(drawerView)
                try {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                } catch (e: Exception) {
                    e.stackTrace
                }
            }
        }
        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()

        // Set Header Image
        navigation_header_img.setImageResource(R.drawable.ic_logo)

        // Set background of Drawer
        navigation_layout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))

    }

    private fun setupFirebase(){
        //Init Firebase
        mAuth = getInstance()

        userActive = mAuth.currentUser?.displayName.toString()
        //Check Login Session if not Kill
        mAuth.addAuthStateListener {
            if(mAuth.currentUser == null){
                this.finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        getData()
        setupFirebase()
        setupDrawer()

        openFragment("PETA", MapsFragment())
    }

    private fun html2text(str: String): String {
        return Jsoup.parse(str).text()
    }

    private fun updateAdapter(highlightItemPos: Int) {
        adapter = NavigationRVAdapter(items, highlightItemPos)
        navigation_rv.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            // Checking for fragment count on back stack
            if (supportFragmentManager.backStackEntryCount > 0) {
                // Go to the previous fragment
                supportFragmentManager.popBackStack()
            } else {
                super.onBackPressed()
            }
        }
    }
}