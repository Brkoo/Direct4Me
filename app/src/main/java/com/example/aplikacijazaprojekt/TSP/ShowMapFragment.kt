package com.example.aplikacijazaprojekt.TSP

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.aplikacijazaprojekt.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class ShowMapFragment : Fragment() {

    private var selectedLocations: MutableList<Location> = mutableListOf()
    private lateinit var googleMap: GoogleMap
    private lateinit var tour: List<Int>
    var thisContext: Context? = null
    private val callback = OnMapReadyCallback { googleMap ->

        this.googleMap = googleMap
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        drawTourOnMap(tour, googleMap)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thisContext = container?.context
        val view = inflater.inflate(R.layout.fragment_show_map, container, false)
        val bundle = arguments
        selectedLocations = bundle?.getSerializable("selectedLocations") as MutableList<Location>
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        println("Selected locations: ${selectedLocations.size}")

        tour = computeTSPUsingGA(selectedLocations)


    }

    private fun computeTSPUsingGA(selectedLocations: MutableList<Location>): List<Int> {
        lateinit var finalIndexes: List<Int>
        for (i in 0..40) {
            //val eilTsp = thisContext?.let { TSP(it, selectedLocations, 1000) }
            val eilTsp = TSP(thisContext!!, selectedLocations, 1000)
            val ga = GA(100, 0.8, 0.1)
            //System.out.println("dvojno je naredilo");
            // val bestPath: TourData? = ga.execute(eilTsp)
            //get list of indexes from ga.execute which will return list of integers which are indexes of locations
            val indexesOfCities = ga.execute(eilTsp)
            // val indexesOfCities: List<Int>? = eilTsp?.let { ga.execute(it) }
            finalIndexes = indexesOfCities!!
        }
        return finalIndexes
    }


    fun showToast(toast: String?) {

        Toast.makeText(this.context, toast, Toast.LENGTH_SHORT).show()

    }

    private fun drawTourOnMap(tour: List<Int>, googleMap: GoogleMap) {


        for (i in 0..tour.size - 1) {

            //location = selecteLoacation wehre its index is equal to element
            // get index of loaction in selectedLoaction where its index is equal to element

            val location = selectedLocations.indexOf(selectedLocations.find { it.index == tour[i] })
            val city = selectedLocations[location]
            val latLng = LatLng(city.latitude, city.longitude)
            googleMap.addMarker(MarkerOptions().position(latLng).title(city.city + " " + i))


            if(i < tour.size - 1) {
                val label = "Tour $i"
                val nextCityIndex =
                    selectedLocations.indexOf(selectedLocations.find { it.index == tour[i + 1] })
                val nextCity = selectedLocations[nextCityIndex]
                val nextLatLng = LatLng(nextCity.latitude, nextCity.longitude)
                val polylineOptions = PolylineOptions().add(latLng, nextLatLng)

                googleMap.addPolyline(polylineOptions)

            }



            // Iterate through the tour and add markers for each city
            /*
            tour.forEachIndexed { index, cityIndex ->
                val city = selectedLocations[cityIndex]
                val latLng = LatLng(city.latitude, city.longitude)
                val markerOptions = MarkerOptions().position(latLng).title(city.city)
                googleMap.addMarker(markerOptions)
    */
            // If this is not the last city in the tour, draw a line to the next city
            /* if (index < tour.size - 1) {
                 val nextCity = selectedLocations[tour[index + 1]]
                 val nextLatLng = LatLng(nextCity.latitude, nextCity.longitude)
                 val polylineOptions = PolylineOptions().add(latLng, nextLatLng)
                 googleMap.addPolyline(polylineOptions)
             }


         }
     }
    }
             */
        }

       // val city = selectedLocations[tour.size - 1]

        val cityindex = selectedLocations.indexOf(selectedLocations.find { it.index == tour[tour.size - 1] })
        val city = selectedLocations[cityindex]
        val latLng = LatLng(city.latitude, city.longitude)
        val nextCity =
            selectedLocations[0]

        val nextLatLng = LatLng(nextCity.latitude, nextCity.longitude)
        val polylineOptions = PolylineOptions().add(latLng, nextLatLng)

        googleMap.addPolyline(polylineOptions)
    }
}