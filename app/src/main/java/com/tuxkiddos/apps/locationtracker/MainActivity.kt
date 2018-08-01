package com.tuxkiddos.apps.locationtracker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.content.res.AppCompatResources
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MainActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private lateinit var context: Context
    private lateinit var mapController: IMapController
    private lateinit var mRotationGestureOverlay: RotationGestureOverlay
    private lateinit var mLocationOverlay: MyLocationNewOverlay

    private val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val PERMISSIONS_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(!arePermissionsEnabled()){
            requestMultiplePermissions()
        }


        context = this
        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)

        mapController = map.controller

        // add zoom buttons and 2 fingure gesture
        map.setBuiltInZoomControls(true)
        map.setMultiTouchControls(true)

        // move map on default view point
        mapController.setZoom(16)
        mapController.setCenter(GeoPoint(27.70362,85.31495))

        // enable rotation gesture
        mRotationGestureOverlay = RotationGestureOverlay(context, map)
        mRotationGestureOverlay.isEnabled = true
        map.overlays.add(mRotationGestureOverlay)

        //add my location overlay
        mLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context),map)
        mLocationOverlay.enableMyLocation()
        map.overlays.add(mLocationOverlay)

        addMarker()

    }

    private fun addMarker() {
        val marker = Marker(map)
        marker.position =GeoPoint(27.70771,85.31405)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "Durbar High School"
        marker.icon = AppCompatResources.getDrawable(context,R.drawable.ic_marker)
        map.overlays.add(marker)
    }

    private fun arePermissionsEnabled(): Boolean{
        for(permission in permissions){
            if(ContextCompat.checkSelfPermission(this, permission)!=PackageManager.PERMISSION_GRANTED)
                return false
        }
        return true
    }
    private fun requestMultiplePermissions() {
        val remainingPermissions = mutableListOf<String>()
        for(permission in this.permissions ){
            if(ContextCompat.checkSelfPermission(this, permission)!= PackageManager.PERMISSION_GRANTED){
                remainingPermissions.add(permission)
            }
        }
        ActivityCompat.requestPermissions(this, remainingPermissions.toTypedArray(),PERMISSIONS_REQUEST_CODE)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                for (i in 0..(permissions.size - 1)) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        if (shouldShowRequestPermissionRationale(permissions[i])) {
                            AlertDialog.Builder(this)
                                    .setMessage("This permission is required to use the app.")
                                    .setPositiveButton("Allow") { _, _ -> requestMultiplePermissions() }
                                    .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                                    .create()
                                    .show()
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}
