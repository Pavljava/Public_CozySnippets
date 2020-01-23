package com.scrappers.notepadpp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.scrappers.notepadpp.MainActivity.importPosition
import java.util.*


class CustomRecycleView(val mainTitle:ArrayList<String>,val subTitle:ArrayList<String>) : RecyclerView.Adapter<CustomRecycleView.onBindClass>() {

    //static object fzlka


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): onBindClass {
        val v=LayoutInflater.from(parent.context).inflate(R.layout.custom_list_xml ,parent,false)

        //List View Animation
        val animation = AnimationUtils.loadAnimation(parent.context, R.anim.fade_in)
        v.startAnimation(animation)

        return onBindClass(v)
    }

    override fun onBindViewHolder(holder: onBindClass, position: Int) {
        holder.bindItems(mainTitle,subTitle,position)

        importPosition=holder.position
    }



    fun getPosition(position: Int) : String{

        return mainTitle.get(position)
    }

    override fun getItemCount(): Int {
        return mainTitle.size
        return subTitle.size
    }


     class onBindClass(itemView: View): RecyclerView.ViewHolder(itemView) {

         fun bindItems(mainTitle:ArrayList<String>,subTitle:ArrayList<String> ,  position :Int) {
             val mainTitleHolder= itemView.findViewById<TextView>(R.id.mainTitleTxt) as TextView
             val subTitleeHolder= itemView.findViewById<TextView>(R.id.subTitleTxt) as TextView
             mainTitleHolder.text = mainTitle[position]
             subTitleeHolder.text = subTitle[position]
//             itemView.setOnClickListener(com.scrappers.notepadpp.MainActivity.clickListner)
         }
     }

}

