package com.miquelrr.cardsx

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager

class AutoFitGridLayoutManager(context: Context) : GridLayoutManager(context, 1) {

    private var columnWidth = 300 // Ancho deseado para cada columna en píxeles

    init {
        spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val totalWidth = width // Ancho total del RecyclerView
                val columns = (totalWidth / columnWidth).coerceAtLeast(1) // Calcula el número de columnas
                return if (columns > 1) 1 else columns // Devuelve 1 si hay más de una columna, sino el número de columnas
            }
        }
    }
}