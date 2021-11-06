package com.example.testing_google_map

import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.testing_google_map.databinding.ActivityMapsBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.Marker
import com.maps.route.extensions.drawMarker
import com.maps.route.extensions.drawRouteOnMap
import com.maps.route.extensions.getTravelEstimations
import com.maps.route.extensions.moveCameraOnMap
import java.security.AccessController.getContext


class MapsActivity : AppCompatActivity(), OnMapReadyCallback,GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var locationCallback: LocationCallback
    // 2
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        // 3
        private const val REQUEST_CHECK_SETTINGS = 2
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled
        mMap.setOnMarkerClickListener(this)
        setUpMap()
//        val Bandung = LatLng(-6.914744, 107.609810)
//        val Jogja = LatLng(-7.797068, 110.370529)
//        mMap.addMarker(MarkerOptions().position(Bandung).title("Marker1"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(Bandung))
//        mMap.addMarker(MarkerOptions().position(Jogja).title("Marker2"))
        // Add a marker in Sydney and move the camera
//        mMap.run {
//            //if you want to move the map on specific location
//            moveCameraOnMap(latLng = Bandung)
//
//            drawRouteOnMap(
//                getString(R.string.google_maps_key),
//                source = Bandung,
//                destination = Jogja,
//                context = this@MapsActivity
//            )
//
//        }
        val source = LatLng(31.490127, 74.316971) //starting point (LatLng)
        val destination = LatLng(31.474316, 74.316112) // ending point (LatLng)

        //if you only want the Estimates (Distance & Time of arrival)
        getTravelEstimations(
            mapsApiKey = getString(R.string.google_maps_key),
            source = source,
            destination = destination
        ) { estimates ->
            estimates?.let {
                //Google Estimated time of arrival
                Log.d(TAG, "ETA: ${it.duration?.text}, ${it.duration?.value}")
                //Google suggested path distance
                Log.d(TAG, "Distance: ${it.distance?.text}, ${it.distance?.text}")

            } ?: Log.e(TAG, "Nothing found")
        }


        mMap?.run {
            //if you want to move the map on specific location
            moveCameraOnMap(latLng = source)

            //if you want to drop a marker of maps, call it
            drawMarker(location = source, context = this@MapsActivity, title = "test marker")

            //if you only want to draw a route on maps
            //Called the drawRouteOnMap extension to draw the polyline/route on google maps
            drawRouteOnMap(
                getString(R.string.google_maps_key),
                source = source,
                destination = destination,
                context = applicationContext
            )

            //if you only want to draw a route on maps and also need the ETAs
            //Called the drawRouteOnMap extension to draw the polyline/route on google maps
            drawRouteOnMap(
                getString(R.string.google_maps_key),
                source = source,
                destination = destination,
                context = applicationContext
            ) { estimates ->
                estimates?.let {
                    //Google Estimated time of arrival
                    Log.d(TAG, "ETA: ${it.duration?.text}, ${it.duration?.value}")
                    //Google suggested path distance
                    Log.d(TAG, "Distance: ${it.distance?.text}, ${it.distance?.text}")

                } ?: Log.e(TAG, "Nothing found")
            }
        }

    }

    override fun onMarkerClick(p0: Marker) = false


    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        mMap.isMyLocationEnabled = true


        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }

        }

    }


}