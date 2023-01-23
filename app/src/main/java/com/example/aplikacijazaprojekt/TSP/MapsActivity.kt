package com.example.aplikacijazaprojekt.TSP

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikacijazaprojekt.R
import com.example.aplikacijazaprojekt.databinding.ActivityRecylerViewCitiesBinding
import com.example.pethealthlord.MapsActivityAdapter
import java.io.Serializable

class MapsActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityRecylerViewCitiesBinding
    private var selectedLocations: MutableList<Location> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecylerViewCitiesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data :AllLocations = AllLocations("AllLocations")
        //need to get rvLocation view
        fillData(data);
        binding.rvLocation.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.rvLocation.adapter = MapsActivityAdapter(data, object : MapsActivityAdapter.MyOnClick {
            override fun onClick(p0: View?, position: Int) {

            if(selectedLocations.contains(data.AllLocations[position])) {
                selectedLocations.remove(data.AllLocations[position])
            } else {
                selectedLocations.add(data.AllLocations[position])
            }

            }

        })


        var searchButton: Button = findViewById(R.id.searchButtonMap)
        //start ShowMapFragment and send selected locations
        searchButton.setOnClickListener {

            val bundle = Bundle()
            bundle.putSerializable("selectedLocations", selectedLocations as Serializable)





            val showMapFragment = ShowMapFragment()
            showMapFragment.arguments = bundle

            //start fragment replace framelayot with fragment
           supportFragmentManager.beginTransaction().replace(R.id.frameLayout,showMapFragment).commit()
        }


    }

    //function that will fill data with all locations
    fun fillData(data: AllLocations){
        val assetManager = assets
        val inputStream = assetManager.open("RealniPrimerMesta.txt")
        val inputStreamPosition = assetManager.open("Position.txt")
        val parsedAddresses = mutableListOf<String>()
        val parsedIndexAndPosition = mutableListOf<String>()
        inputStream.bufferedReader().useLines { lines ->
            lines.forEach { line ->
                val parts = line.split(" ")
                val street = parts[0]
                val city = parts[1]
                parsedAddresses.add("$street, $city")
            }
        }
        inputStreamPosition.bufferedReader().useLines { lines ->
            lines.forEach { line ->
                val parts = line.split(" ")
                if(parts.size == 3) {
                    val index = parts[0]
                    val latitude = parts[1]
                    val longitude = parts[2]
                    parsedIndexAndPosition.add("$index, $latitude, $longitude")
                }
                }
        }

        for (i in 0..95)
        {
            val streetAndCity = parsedAddresses[i].split(",")
            val indexAndPosition = parsedIndexAndPosition[i].split(",")
            data.AllLocations.add(Location(indexAndPosition[0].toInt(),streetAndCity[0],streetAndCity[1],indexAndPosition[1].toDouble(),indexAndPosition[2].toDouble()))
        }

    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_recyler_view_cities)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}

