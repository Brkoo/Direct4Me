package com.example.pethealthlord

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikacijazaprojekt.R
import com.example.aplikacijazaprojekt.TSP.AllLocations

import timber.log.Timber

class MapsActivityAdapter (private val data: AllLocations/*, val items: ArrayList<Pet>*/,
                           private val OnClickObject: MapsActivityAdapter.MyOnClick) :
    RecyclerView.Adapter<MapsActivityAdapter.MyView>() {
    private val selected = BooleanArray(data.AllLocations.size)

    interface MyOnClick {

        fun onClick(p0: View?, position: Int){

        }

    }

    class MyView(view: View) : RecyclerView.ViewHolder(view) {
        var City: TextView
        var Street: TextView



        init {
            City = view
                .findViewById<TextView>(R.id.textViewCity)
            Street = view.findViewById<TextView>(R.id.textViewStreet)


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyView {
        val itemView: View = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.card_view_design,
                parent,
                false
            )

        return MyView(itemView)
    }



    override fun getItemCount(): Int {
        return data.AllLocations.size
    }

    override fun onBindViewHolder(holder: MyView, position: Int) {
        val item = data.AllLocations[position]

        val imageView = holder.itemView.findViewById<ImageView>(R.id.IsCitySelectedImage)

        imageView.visibility = if(selected[position]) View.VISIBLE else View.INVISIBLE
        holder.City.text = item.street
        holder.Street.text = item.city
        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                OnClickObject.onClick(p0, holder.bindingAdapterPosition)
                selected[holder.bindingAdapterPosition] = !selected[holder.bindingAdapterPosition]
                notifyDataSetChanged()
            }
        })
        //Loading Image into view
        //Picasso.get().load(listData).placeholder(R.mipmap.ic_launcher).into(holder.imageView)

    }

}
