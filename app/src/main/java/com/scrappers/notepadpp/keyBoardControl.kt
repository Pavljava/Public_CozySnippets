package com.scrappers.notepadpp

import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.scrappers.notepadpp.EditPaneActivity.ed
import kotlinx.android.synthetic.main.fragment_key_board_control.*


class keyBoardControl : Fragment() {

    lateinit var btn1:Button
    lateinit var btn2:Button
    lateinit var btn3:Button
     var x:Int =2

     var tracker : Int = 1


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view :View = inflater.inflate(R.layout.fragment_key_board_control, container, false)

        btn1= view.findViewById(R.id.tabBtn)
        btn2=view.findViewById(R.id.toRight)
        btn3=view.findViewById(R.id.toLeft)



        btn1.setOnClickListener {view ->
            try {
                val value = ed.getText().toString()
                val b = StringBuilder(value)
                ed.setText(b.append("\t"))
                ed.setSelection(ed.selectionStart +x)
                ed.setSelection(ed.selectionStart +x)
                x+=2

            } catch (error: Exception) {
                error.printStackTrace()
            }

        }

        btn2.setOnClickListener {view ->
            try {
                ed.setSelection(ed.selectionStart+tracker)
            }catch (error:java.lang.Exception){
                error.printStackTrace()
            }
        }

        btn3.setOnClickListener {view ->
            try{
                ed.setSelection(ed.selectionStart-tracker)
            }catch(error:Exception){
                error.printStackTrace()
            }
        }


        return view
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}
