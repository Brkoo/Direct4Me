package com.example.aplikacijazaprojekt.TSP

import com.example.aplikacijazaprojekt.TSP.CityData
import com.example.aplikacijazaprojekt.TSP.TourData
import okhttp3.internal.platform.Platform

class GA(
    var popSize: Int, //crossover probability
    var cr: Double, //mutation probability
    var pm: Double
) {

    var population: ArrayList<TourData>? = null
    var offspring: ArrayList<TourData>? = null
    fun execute(problem: TSP): List<Int>? {
        population = ArrayList()
        offspring = ArrayList()
        var best: TourData? = null
        //tourDrawer.drawCities(problem.getCities());
        for (i in 0 until popSize) {
            val newTour = problem.generateTour()
            //System.out.println(i + " " + newTour.getDistance());
            problem.evaluate(newTour)
            //System.out.println(i + " " + newTour.getDistance());
            population!!.add(newTour)

            //TODO shrani najboljšega (best)
            if (best == null) {
                best = newTour.clone()
            }
            for (tour in population!!) {
                if (tour.distance < best!!.distance) {
                    best = tour.clone() // tu sem dal clone
                }
            }
            // System.out.println(i + " Najboljši: " + best.getDistance());
            //offspring.add(best.clone()); // tu sem zbrisal clone //FIXME poglej ce mores dat offspring
            //System.out.println(offspring.size());
        }
        var iterator = 0
        while (problem.numberOfEvaluations < problem.maxEvaluations) {
            //System.out.println("Število ocen: " + problem.getNumberOfEvaluations());
            //System.out.println(offspring.size());
            //elitizem - poišči najboljšega in ga dodaj v offspring in obvezno uporabi clone()
            while (offspring!!.size < popSize) {
                //System.out.println(offspring!!.size)
                val parent1 = tournamentSelection()
                var parent2 = tournamentSelection()
                //TODO preveri, da starša nista enaka
                while (parent1 == parent2) {
                    parent2 = tournamentSelection()
                }
                if (RandomUtils.nextDouble() < cr) {
                    // assert parent1 != null;
                    val children = pmx(parent1, parent2)
                    offspring!!.add(children[0])
                    if (offspring!!.size < popSize) offspring!!.add(children[1])
                } else {
                    //assert parent1 != null;
                    offspring!!.add(parent1.clone())
                    if (offspring!!.size < popSize) {
                        //  assert parent2 != null;
                        offspring!!.add(parent2.clone())
                    }
                }
            }
            for (off in offspring!!) {
                if (RandomUtils.nextDouble() < pm) {
                    swapMutation(off)
                }
            }

            //TODO ovrednoti populacijo in shrani najboljšega (best)
            //implementacijo lahko naredimo bolj učinkovito tako, da overdnotimo samo tiste, ki so se spremenili (mutirani in križani potomci)
            population = ArrayList(offspring)
            for (tour in population!!) {
                // assert best != null;
                problem.evaluate(tour)
                if (tour.distance < best!!.distance) {
                    best = tour.clone() // tu sem dal clone
                    //tourDrawer.draw2(problem, best);
                }
            }

            iterator++
            offspring!!.clear()
        }
        //System.out.println("Najboljši: " + best.getDistance());

            return best?.getOrder()

    }

    private fun swapMutation(off: TourData) {

        //izvedi mutacijo
        val index1: Int = RandomUtils.nextInt(off.dimension)
        var index2: Int = RandomUtils.nextInt(off.dimension)
        while (index1 == index2) {
            index2 = RandomUtils.nextInt(off.dimension)
        }
        val temp = off.path[index1]!!
        off.path[index1] = off.path[index2]
        off.path[index2] = temp
    }

    private fun pmx(parent1: TourData, parent2: TourData): Array<TourData> {

        //izvedi pmx križanje, da ustvariš dva potomca
        var index1: Int = RandomUtils.nextInt(parent1.path.size)
        var index2: Int = RandomUtils.nextInt(parent2.path.size)
        while (index1 > index2 || index1 == index2) {
            index2 = RandomUtils.nextInt(parent2.path.size)
        }
        val child1 = parent1.clone()
        val child2 = parent2.clone()
        val map1: MutableMap<CityData, CityData> = HashMap()
        val map2: MutableMap<CityData, CityData> = HashMap()
        for (i in index1 until index2) {
            child1.path[i] = parent2.path[i]
            map1[parent2.path[i]!!] = parent1.path[i]!!
            child2.path[i] = parent1.path[i]
            map2[parent1.path[i]!!] = parent2.path[i]!!
        }

        //Spremenimo prvi del
        for (i in 0 until index1) {
            while (map1.containsKey(child1.path[i])) child1.setCity(
                i,
                map1[child1.path[i]]
            )
            while (map2.containsKey(child2.path[i])) child2.setCity(
                i,
                map2[child2.path[i]]
            )
        }
        //Spremenimo drugi del
        for (i in index2 until child1.path.size) {
            while (map1.containsKey(child1.path[i])) child1.setCity(
                i,
                map1[child1.path[i]]
            )
            while (map2.containsKey(child2.path[i])) child2.setCity(
                i,
                map2[child2.path[i]]
            )
        }
        return arrayOf(child1, child2)
    }

    private fun containsCity(tour: TourData, city: CityData, start: Int, end: Int): Boolean {
        for (i in start until end) {
            if (tour.path[i] == city) {
                return true
            }
        }
        return false
    }

    private fun getCity(fromTour: TourData, toTour: TourData, city: CityData, start: Int, end: Int): CityData? {
        for (i in 0 until fromTour.path.size) {
            if (fromTour.path[i] == city) {
                return toTour.path[i]
            }
        }
        return null
    }

    private fun tournamentSelection(): TourData {
        var index1: Int = RandomUtils.nextInt(popSize)
        val index2: Int = RandomUtils.nextInt(popSize)
        while (index1 == index2) {
            index1 = RandomUtils.nextInt(popSize)
            // index2 = RandomUtils.nextInt(popSize);
        }
        //select index with best fitness
        return if (population!![index1].distance < population!![index2].distance) {
            population!![index1]
        } else {
            population!![index2]
        }
    }
}
