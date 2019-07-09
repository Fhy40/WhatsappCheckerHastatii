package com.example.whatsappcheckerhastatii

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot



class MainActivity : AppCompatActivity() {
    var fusedLocationClient: FusedLocationProviderClient? = null
    val PERMISSION_ID = 42


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val intent = Intent(this,IntentService::class.java)
        val db = FirebaseFirestore.getInstance()
        var check_button = findViewById(R.id.check_button) as Button
        var scoreactual_textview = findViewById(R.id.scoreactual_textview) as TextView
        var home_textview = findViewById(R.id.home_textview) as TextView



        check_button.setOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View): Unit {
                var docRef = db.collection("users").document("whastapp_score")

                startService(intent)

                docRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val score = document.getLong("score").toString()
                            scoreactual_textview.text = score
                            Log.d("Arjun", "DocumentSnapshot data: ${document.getString("name")}")
                        } else {
                            Log.d("Arjun", "No such document")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("Arjun", "get failed with ", exception)
                    }
                if (checkPermission(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    fusedLocationClient?.lastLocation?.
                        addOnSuccessListener { location:Location? ->
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

                docRef = db.collection("users").document("is_home")

                docRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val is_home = document.getBoolean("Home")
                            if(is_home == true){
                                home_textview.text = "Yes"
                            } else{
                                home_textview.text = "No"
                            }
                            Log.d("Arjun", "DocumentSnapshot data: ${document.getBoolean("Home").toString()}")
                        } else {
                            Log.d("Arjun", "No such document")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("Arjun", "get failed with ", exception)
                    }
            }

        })


    }

    private fun checkPermission(vararg perm:String) : Boolean {
        val havePermissions = perm.toList().all {
            ContextCompat.checkSelfPermission(this,it) ==
                    PackageManager.PERMISSION_GRANTED
        }
        if (!havePermissions) {
            if(perm.toList().any {
                    ActivityCompat.
                        shouldShowRequestPermissionRationale(this, it)}
            ) {
                val dialog = AlertDialog.Builder(this)
                    .setTitle("Permission")
                    .setMessage("Permission needed!")
                    .setPositiveButton("OK", {id, v ->
                        ActivityCompat.requestPermissions(
                            this, perm, PERMISSION_ID)
                    })
                    .setNegativeButton("No", {id, v -> })
                    .create()
                dialog.show()
            } else {
                ActivityCompat.requestPermissions(this, perm, PERMISSION_ID)
            }
            return false
        }
        return true
    }
}
