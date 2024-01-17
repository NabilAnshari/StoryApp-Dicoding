package com.nabil.submission1_appstory.View

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.nabil.submission1_appstory.Local.Outcome
import com.nabil.submission1_appstory.Model.ViewModelFactory
import com.nabil.submission1_appstory.R
import com.nabil.submission1_appstory.databinding.ActivityMapsBinding
import com.nabil.submission1_appstory.Component.vectorToBitmap
import com.nabil.submission1_appstory.Model.MainViewModel

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapsViewModel: MapsViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val mainViewModel by viewModels<MainViewModel>{
        ViewModelFactory.findInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupView()
    }

    private fun setupView() {
        val factory: ViewModelFactory = ViewModelFactory.findInstance(this)
        mapsViewModel = ViewModelProvider(this, factory)[MapsViewModel::class.java]
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        setMarker()
        getMyExactLocation()
    }
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyExactLocation()
            }
        }

    private fun getMyExactLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) {
                    myMarker(loc)
                } else {
                    Toast.makeText(
                        this@MapsActivity,
                        resources.getString(R.string.not_found),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun myMarker(location: Location) {
        val startLocation = LatLng(location.latitude, location.longitude)
        mMap.addMarker(
            MarkerOptions()
                .position(startLocation)
                .icon(vectorToBitmap(R.drawable.ic_mylocation,
                    Color.parseColor("#2D3D4F"), this))
                .title(getString(R.string.lokasi))
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 5f))
    }

    private fun setMarker() {
        val token = takeToken()
        if (token != null) {
            mapsViewModel.gainStories("Bearer $token").observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Outcome.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is Outcome.Success -> {
                            binding.progressBar.visibility = View.GONE
                            result.data.listStory.map {
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(LatLng(it.lat, it.lon))
                                        .title(it.name)
                                        .icon(
                                            BitmapDescriptorFactory.defaultMarker(
                                                BitmapDescriptorFactory.HUE_CYAN
                                            )
                                        )
                                        .snippet(it.description)
                                )
                            }
                        }

                        is Outcome.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                this,
                                "Failure : " + result.error,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun takeToken(): String?{
        return mainViewModel.gainPreference(this).value
    }
}