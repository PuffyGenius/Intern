package com.example.geoloca

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var tvLongitude: TextView
    private lateinit var tvLatitude: TextView




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this)
        tvLatitude= findViewById(R.id.tv_latitude)
        tvLongitude= findViewById(R.id.tv_longitude)
        getCurrentLocation()


    }
    private fun getCurrentLocation(){
        if(checkPermission()){

            if(isLocationEnabled()){
                //lat and long
                //add permission check
                if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!=
                    PackageManager.PERMISSION_GRANTED) {
                    requestPermissions()
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this)
                {
                        task-> var location:Location? = task.result
                    if(location == null)
                    {
                        Toast.makeText(this,"No Location Received", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(this,"Location Get!!!", Toast.LENGTH_SHORT).show()

                        tvLatitude.text = location.latitude.toString()
                        tvLongitude.text = location.longitude.toString()
                    }
                }
            }
            else{
                Toast.makeText(this,"Turn on Location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intent)
            }
        }
        else{
            //request
            requestPermissions()
        }
    }
    companion object{
        private const val PERMISSION_REQUEST_ACCESS_LOCATION=100
    }

    private fun checkPermission():Boolean{
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            return true
        }
        return false
    }

    private fun requestPermissions()
    {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_ACCESS_LOCATION)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_REQUEST_ACCESS_LOCATION)
        {
            if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(applicationContext,"Granted", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            }
            else {
                Toast.makeText(applicationContext,"Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun isLocationEnabled():Boolean{
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

}
