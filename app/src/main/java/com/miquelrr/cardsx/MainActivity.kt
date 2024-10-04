package com.miquelrr.cardsx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity :AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var cardDataList: MutableList<CardData>
    private lateinit var deckCards: MutableList<CardData>

    private lateinit var adapter: MyAdapter
    private var isAddIcon = true
    private lateinit var cardDataJsonManager: CardDataJsonManager

    fun createGridLayoutManager(recyclerView: RecyclerView, columnWidth: Int): GridLayoutManager {
        val totalWidth = recyclerView.width // Ancho total del RecyclerView
        val columns = (totalWidth / columnWidth).coerceAtLeast(1)
        val layoutManager = GridLayoutManager(recyclerView.context, columns)
        return layoutManager
    }
    lateinit var fab: FloatingActionButton

    fun updateFabIcon() {
        if (adapter.getSelectedCard() != null) {
            fab.setImageResource(R.drawable.ic_remove)
            isAddIcon = false
        } else {
            fab.setImageResource(R.drawable.ic_add)
            isAddIcon = true
        }
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        cardDataList = savedInstanceState.getParcelableArrayList<CardData>("cardDataList")?.toMutableList() ?: mutableListOf()
        deckCards = savedInstanceState.getParcelableArrayList<CardData>("deckCards")?.toMutableList()?: mutableListOf()
        // ... resto del código para actualizar la UI
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        cardDataJsonManager = CardDataJsonManager(applicationContext)

        if (savedInstanceState != null){
            cardDataList = savedInstanceState.getParcelableArrayList<CardData>("cardDataList")?.toMutableList() ?: mutableListOf()
            deckCards = savedInstanceState.getParcelableArrayList<CardData>("deckCards")?.toMutableList() ?: mutableListOf()
        } else {
            cardDataList = cardDataJsonManager.loadCardsFromJson().toMutableList()
            deckCards = cardDataJsonManager.loadCardsFromJson("deck.json").toMutableList()
        }



        recyclerView = findViewById(R.id.my_recycler_view)
        adapter = MyAdapter(cardDataList)
        val selectedIndex = adapter.getSelectedCard()
        fab=findViewById(R.id.fab)
        updateFabIcon()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                // El ancho ya está disponible, actualiza el LayoutManager
                recyclerView.layoutManager = createGridLayoutManager(recyclerView, 800)
            }
        })
        fab.setOnClickListener {
            if (isAddIcon) { // Comprobar si el icono es ic_add
                if (deckCards.isNotEmpty()) {
                    val cardToMove = deckCards.removeAt(0)
                    cardDataList.add(cardToMove)
                    cardToMove.isSelected=false
                    adapter.notifyItemInserted(cardDataList.size - 1)
                    updateFabIcon()
                }
            } else {
                val selectedIndex : Int? = adapter.getSelectedCard()
                if (selectedIndex !=null) {
                    val cardToMove = cardDataList.removeAt(selectedIndex)
                    deckCards.add(cardToMove)
                    adapter.notifyItemRemoved(selectedIndex)
                    adapter.setSelectedCardNull()
                    updateFabIcon()
                }
            }
        }


        val selectedIndexObserver = Observer<Int?> { selectedIndex ->
            if (selectedIndex != null) {
                fab.setImageResource(R.drawable.ic_remove)
            } else {
                fab.setImageResource(R.drawable.ic_add)
            }
        }

    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("cardDataList", ArrayList(cardDataList))
        outState.putParcelableArrayList("deckCards", ArrayList(deckCards))
    }

    override fun onStop() {
        super.onStop()
        cardDataJsonManager.saveCardDataToJson(cardDataList)
        cardDataJsonManager.saveCardDataToJson(deckCards, "deck.json")
    }
}