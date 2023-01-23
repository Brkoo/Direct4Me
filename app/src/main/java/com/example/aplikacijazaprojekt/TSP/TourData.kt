package com.example.aplikacijazaprojekt.TSP

class TourData {
    var distance: Double
    var dimension: Int
    var path: Array<CityData?>

    constructor(tour: TourData) {
        distance = tour.distance
        dimension = tour.dimension
        path = tour.path.clone()
    }

    constructor(dimension: Int) {
        this.dimension = dimension
        path = arrayOfNulls(dimension)
        distance = Double.MAX_VALUE
    }

    fun clone(): TourData {
        return TourData(this)
    }


    fun setCity(index: Int, city: CityData?) {
        path[index] = city
        distance = Double.MAX_VALUE
    }
    fun getOrder(): List<Int>{
        val order = mutableListOf<Int>()
        for (i in path.indices){
            order.add(path[i]!!.index)
        }
        return order

    }
}