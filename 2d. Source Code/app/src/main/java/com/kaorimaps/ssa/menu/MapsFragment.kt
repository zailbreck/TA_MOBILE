package com.kaorimaps.ssa.menu

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.kaorimaps.ssa.R
import com.kaorimaps.ssa.config.MapLog
import khttp.get
import kotlinx.android.synthetic.main.fragment_maps.*
import java.util.*
import kotlin.collections.ArrayList


class MapsFragment : Fragment(),
    OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener{

    lateinit var mMapView: MapView
    lateinit var googleMap: GoogleMap
    lateinit var marker: Marker
    lateinit var mapLogList : ArrayList<MapLog>
    lateinit var geocoder: Geocoder
    lateinit var currentLocation: Location
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var curLat = 0.0
    var curLng = 0.0
    var curZoom = 13f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_maps, container, false)

        //Init Fragment
        activity?.title = arguments?.getString("fragmentName")
        view.setBackgroundColor(Color.WHITE)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
            requireActivity()
        )

        //Init Map
        setupMap(view, savedInstanceState)
        return view
    }

    private fun setupMap(view: View, savedInstanceState: Bundle?){
        mMapView = view.findViewById(R.id.map)
        mMapView.onCreate(savedInstanceState)
        mMapView.onResume()

        try {
            MapsInitializer.initialize(requireActivity().applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mMapView.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }

    private fun bitMapFromVector(vectorResID: Int):BitmapDescriptor {
        val vectorDrawable=ContextCompat.getDrawable(requireContext(), vectorResID)
        vectorDrawable?.setTint(ContextCompat.getColor(requireContext(), R.color.colorAccent_500))
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap=Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas=Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun initData(){
        mapLogList = ArrayList<MapLog>()

        arguments?.getParcelableArrayList<MapLog>("MapList")?.forEach {
            var ic: String = if(it.icon == ""){
                "ic_pin"
            }else{
                it.icon.toString()
            }
            mapLogList.add(MapLog(it.lat, it.lng, it.rad, it.title, ic))
            Log.d("latlng", "${it.lat} | ${it.lng}")
        }
    }

    private fun getAddressName(lat: Double, lng: Double) : String {
        geocoder = Geocoder(requireContext(), Locale.getDefault())
        val address: List<Address>
        val returnAddress: Address
        val strReturnAddress: StringBuilder
        var strAdd = ""

        try{
            address = geocoder.getFromLocation(lat, lng, 1)
            if(address != null){
                returnAddress = address[0]
                strReturnAddress = StringBuilder("")
                for (i in 0..returnAddress.maxAddressLineIndex){
                    strReturnAddress.append(returnAddress.getAddressLine(i)).append("\n")
                }

                strAdd = if(strReturnAddress.toString() != "") strReturnAddress.toString() else "Alamat Tidak Diketahui"
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
        return strAdd
    }

    private fun addPlace(mMap: GoogleMap, lat: Double, lng: Double, rad: Double, title: String, addr: String, icon: String) {
        googleMap = mMap
        val pN : String = context?.packageName ?: icon
        val resId  : Int = resources.getIdentifier(icon, "drawable", pN)
        marker = googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(lat, lng))
                .title(title)
                .snippet(addr)
                .icon(bitMapFromVector(resId))
        )
        googleMap.addCircle(
            CircleOptions()
                .center(LatLng(lat, lng))
                .radius(rad)
                .strokeColor(Color.GREEN)
                .clickable(true)
                .strokeWidth(5.0f)
        )
    }

    private fun getURL(from: LatLng, to: LatLng) : String {
        val str = "http://router.project-osrm.org/trip/v1/car/${from.longitude},${from.latitude};${to.longitude},${to.latitude}?source=first&destination=last&geometries=geojson&roundtrip=false"
        Log.d("thisurl", str)
        return str
    }

    @SuppressLint("MissingPermission")
    private fun inflateMap(mMap: GoogleMap){
        googleMap = mMap


        //Maps Configuration
        googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN



        //Add Point To Mapping
        mapLogList.forEach{
            val x = addPlace(googleMap, it.lat, it.lng, 10.0, it.title.toString(), getAddressName(it.lat, it.lng), it.icon.toString())

        }

        //Add My Location To Mapping
        val userActive = arguments?.getString("userLogin").toString()
        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener {
            if(it != null){
                val lat = it.latitude
                val lng = it.longitude
                addPlace(googleMap, lat, lng, 10.0, userActive, getAddressName(lat, lng), "ic_people")
                curLat = lat
                curLng = lng
            }
        }

        //Camera Position Initialize
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    3.653223,
                    98.673296
                ), 11.5f
            )
        )

        with(googleMap.uiSettings){
            isMapToolbarEnabled = false
        }
    }

    override fun onMapReady(mMap: GoogleMap) {
        googleMap = mMap
        googleMap.clear()
        initData()
        inflateMap(googleMap)
        setupUI(googleMap)

    }


    private fun setupUI(mMap: GoogleMap){
        googleMap = mMap
        fab.setOnClickListener {
            if(curLat != 0.0 && curLng != 0.0){
                if(curZoom > 18f){
                    curZoom = 13f
                } else {
                    googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(curLat, curLng),
                            curZoom
                        )
                    )
                    curZoom += 1.0f
                }
            }else{
                Toast.makeText(context, "Lokasi Anda Tidak Ditemukan!", Toast.LENGTH_SHORT).show()
            }
        }
//        googleMap.setOnMarkerClickListener {
//            getRute(mMap, it.position, it.title)
//            true
//        }
        googleMap.setOnInfoWindowClickListener(this)

    }

    private fun getRute(mMap: GoogleMap, dest: LatLng, dtitle: String){
        googleMap = mMap
        googleMap.clear()
        inflateMap(googleMap)
        getPoint(dest, dtitle)
    }

    private fun getPoint(to: LatLng, str: String){
        val tujuan = LatLng(to.latitude, to.longitude)

        val myPosition = LatLng(curLat, curLng)

        try{
            val url = getURL(myPosition, tujuan)
            val y = get(url)
            val arrTrip = y.jsonObject
                .getJSONArray("trips")
                .getJSONObject(0)
                .getJSONObject("geometry")
                .getJSONArray("coordinates")

            val arr : MutableList<LatLng> = ArrayList()

            for(i in 0 until arrTrip.length()){
                var rArr = arrTrip[i].toString().split(",")

                val lat : Double = rArr[1].dropLast(1).toDouble()
                val lng : Double = rArr[0].drop(1).toDouble()
                arr.add(LatLng(lat, lng))
            }

            googleMap.addPolyline(
                PolylineOptions()
                    .addAll(arr)
                    .width(10f)
                    .color(Color.RED)
                    .geodesic(true)
            )
            Toast.makeText(context, "Anda Memilih Tujuan $str", Toast.LENGTH_SHORT).show()
        }catch (e: Exception){
            Toast.makeText(context, "Terjadi kesalahan!\nSilahkan Ulangi!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onInfoWindowClick(p0: Marker) {
        getRute(googleMap, p0.position, p0.title)
    }

}