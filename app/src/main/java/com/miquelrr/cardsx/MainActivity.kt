package com.miquelrr.cardsx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity :AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyAdapter // Reemplaza MyAdapter con el nombre de tu adaptador

    fun createGridLayoutManager(recyclerView: RecyclerView, columnWidth: Int): GridLayoutManager {
        val totalWidth = recyclerView.width // Ancho total del RecyclerView
        val columns = (totalWidth / columnWidth).coerceAtLeast(1)
        val layoutManager = GridLayoutManager(recyclerView.context, columns)
        return layoutManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.my_recycler_view)
        adapter = MyAdapter(listOf("uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve", "diez", "once", "doce", "trece", "catorce", "quince", "dieciseis", "diecisiete", "dieciocho", "diecinueve", "veinte", "veintiuno", "veintidós", "veintitrés", "veinticuatro", "veinticinco", "veintiséis", "veintisiete", "veintiocho", "veintinueve", "treinta", "treinta y uno", "treinta y dos")) // Reemplaza dataList con tu lista de datos

        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 1)

        recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                // El ancho ya está disponible, actualiza el LayoutManager
                recyclerView.layoutManager = createGridLayoutManager(recyclerView, 500)
            }
        })
    }
}