package com.example.aplikacijazaprojekt.TSP

import android.content.Context
import java.io.*

class TSP(context: Context,selectedLocations: MutableList<Location>, maxEvaluations: Int) {


    enum class DistanceType {
        EUCLIDEAN, WEIGHTED
    }


    var name: String? = null
    var start: CityData? = null
    var cities: MutableList<CityData> = ArrayList()
    var numberOfCities = 0
    lateinit var weights: Array<DoubleArray>
    var distanceType = DistanceType.EUCLIDEAN
    var numberOfEvaluations: Int
    var maxEvaluations: Int

    fun evaluate(tour: TourData) {
        var distance = 0.0
        distance += calculateDistance(start, tour.path[0])
        for (index in 0 until numberOfCities) {
            distance += if (index + 1 < numberOfCities) calculateDistance(
                tour.path[index],
                tour.path[index + 1]
            ) else calculateDistance(tour.path[index], start)
        }
        //System.out.println("Distance: " + distance);
        tour.distance = distance
        numberOfEvaluations++
    }

    private fun calculateDistance(from: CityData?, to: CityData?): Double {
        //FIXED
        return when (distanceType) {
            DistanceType.EUCLIDEAN -> {

                // Calculate Euclidean distance between two cities
                val xDiff = from!!.x - to!!.x
                val yDiff = from.y - to.y
                Math.sqrt(xDiff * xDiff + yDiff * yDiff) // to je ok niceeeeeeeeeee vsaj nekaj
            }
            DistanceType.WEIGHTED ->                 // Use pre-calculated weight between two cities
                weights[from!!.index - 1][to!!.index - 1] //v weight bova shranjevala od 0 0 kar pomeni da moreva dati -1
            else -> Double.MAX_VALUE
        }
    }

    fun generateTour(): TourData {
        //FIXED
        val tour = TourData(numberOfCities)
        val citiesCopy: MutableList<CityData?> = ArrayList(cities)


        tour.path[0] = start
        citiesCopy.remove(start)
        // System.out.print("Number of cities in generate tour" + numberOfCities);
        println("Citiers copy size: " + citiesCopy.size)

        for (i in 1 until numberOfCities) {
            //System.out.print("Cities copy size : " + citiesCopy.size());
            val index: Int = RandomUtils.nextInt(citiesCopy.size)
            tour.setCity(i, citiesCopy[index])
            citiesCopy.removeAt(index)
        }
        return tour
    }

    @Throws(FileNotFoundException::class)
    private fun loadData(file: File) {
        // System.out.println("load data is called");
        //TODO set starting city, which is always at index 0

        //read file
        val inputStream: InputStream = FileInputStream(file)
        if (inputStream == null) {
            System.err.println("File " + file.getPath().toString() + " not found!")
            return
        }
        val lines: MutableList<String> = ArrayList()
        try {
            BufferedReader(InputStreamReader(inputStream)).use { br ->
                var line: String = br.readLine()
                while (line != null) {
                    lines.add(line)
                    line = br.readLine()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //System.out.println(lines);
        //TODO parse data
        for (i in lines.indices) {
            val line = lines[i].split(" ").toTypedArray() //razdelimo glede na presledek
            if (line[0] == "NAME:") { // ce je prva beseda NAME potem je to ime
                name = line[1]
                //System.out.println("Name: " + name);
            }
            if (line[0] == "DIMENSION:") { // ce je prva beseda DIMENSION potem je to stevilo mest
                numberOfCities = line[1].toInt()
                //System.out.println("Number of cities: " + numberOfCities);
            }
            if (line[0] == "EDGE_WEIGHT_TYPE:") {  // ce je prva beseda EDGE_WEIGHT_TYPE potem je to tip razdalje
                if (line[1] == "EUC_2D") {  // ce je druga beseda EUC_2D potem bomo računali evklidsko razdaljo uporabljali bomo x in y od mesta
                    distanceType = DistanceType.EUCLIDEAN
                    //System.out.print(distanceType);
                }
                if (line[1] == "EXPLICIT") { // ce je explicit potem bomo prebrali razdalje iz matrike
                    distanceType = DistanceType.WEIGHTED
                    //System.out.print(distanceType);
                }
            }
            if (line[0] == "NODE_COORD_SECTION") { // to je, ko imamo EUC_2D
                for (j in i + 1 until lines.size) {
                    val city = lines[j].split(" ").toTypedArray()
                    if (city.size == 3) { // ce je so v eni vrstici TRI vrednosti
                        val c = CityData()
                        c.index = city[0].toInt() // prva vrednost je index
                        c.x = city[1].toDouble() // druga vrednost je x
                        c.y = city[2].toDouble() // tretja vrednost je y
                        cities.add(c)
                        if (cities.size == 1) {
                            start = CityData()
                            start = cities[0]
                        }
                    }

                    /* for(int k = 0 ; k < city.length; k++){
                        System.out.print(city[k] + " ");
                    }
                    System.out.println();

                    */
                }
            }
            if (line[0] == "EDGE_WEIGHT_SECTION") { // i bo vrstia ko imamo to torej 8 vrstica
                weights = Array(numberOfCities) {
                    DoubleArray(
                        numberOfCities
                    )
                }
                for (j in i + 1 until numberOfCities + i + 1) {
                    // j = 9 vrstica
                    //String[] weight = lines.get(j).split(" ");
                    val weight = lines[j].split("\\s+")
                        .toTypedArray() // \s+ je en ali več presledkov //FIXME upam da dela
                    //System.out.println("Weight lenghts = " + weight.length);
                    if (weight.size == numberOfCities) { // ce je v string arrayu enako stevilo kot je mest potem gremo v for loop
                        for (k in 0 until numberOfCities) { //FIXME tu je ena velika napaka sparsa se white space in ga jebes
                            weights[j - i - 1][k] = weight[k].toDouble() // 9 - 8 - 1 = 0
                            //  System.out.print(weights[j-i-1][k] + " ");
                        }
                        //System.out.println();
                    }
                }
            }
            if (line[0] == "DISPLAY_DATA_SECTION") { // to imamo ko je EXPLICIT imamo se na koncu DISPLAY_DATA_SECTION
                for (j in i + 1 until lines.size - 1) {
                    val city = lines[j].split("\\s+").toTypedArray()
                    //System.out.println(city.length);
                    if (city.size == 3) { // ce je so v eni vrstici TRI vrednosti
                        val c = CityData()
                        c.index = city[0].toInt() // prva vrednost je index
                        c.x = city[1].toDouble() // druga vrednost je x
                        c.y = city[2].toDouble() // tretja vrednost je y
                        cities.add(c)
                        if (cities.size == 1) {
                            start = CityData()
                            start = cities[0]
                            println("Start: " + start!!.y)
                        }


                    }
                }

            }
        }
    }


    fun loadData2(context: Context, selectedCities: MutableList<Location>) {
        var inEdgeWeightSection = false
        var inDisplayDataSection = false
        var index = 0
        var cityIndex = 0
        var x = 0.0
        var y = 0.0
        val assetManager = context.assets
        val inputStream = assetManager.open("realni.tsp")
        val bufferedReader = inputStream.bufferedReader()

        numberOfCities = selectedCities.size
        bufferedReader.useLines { lines ->
            lines.forEach { line ->
            when {
                line.startsWith("EDGE_WEIGHT_SECTION") -> inEdgeWeightSection = true

                line.startsWith("DISPLAY_DATA_SECTION") -> inDisplayDataSection = true
                inEdgeWeightSection -> {
                    if(inDisplayDataSection == true){
                        inEdgeWeightSection = false
                    }
                    val weight = line.split(" ") //split line by whitespaces
                    //parint all weight
                    //println(weight.size)
                    if(weight.size == 96) {
                        for (i in 0 until weight.size) {
                            weights[index][i] = weight[i].toDouble()
                        }
                    }
                    index++

                }
                inDisplayDataSection -> {
                    val city = line.split(" ")
                    if (city.size == 3) {
                        val cityIndex = city[0].toInt()
                        val x = city[1].toDouble()
                        val y = city[2].toDouble()

                        if (selectedCities.any{it.index == cityIndex}) {
                            var city: CityData
                            city = CityData()
                            city.index = cityIndex
                            city.x = x
                            city.y = y
                            cities.add(city)

                            println("Dodal sem city ........................")
                        }
                        if (cities.size == 1) {
                            start = CityData()
                            start = cities[0]
                        }

                        }

                        }
                    }
                }
            }
        }



    init {
        //loadData(file)
        val WEIGHT_ARRAY_SIZE = 96
        weights = Array(WEIGHT_ARRAY_SIZE) { DoubleArray(WEIGHT_ARRAY_SIZE) }
        loadData2(context, selectedLocations)
        numberOfEvaluations = 0
        this.maxEvaluations = maxEvaluations
    }
}
