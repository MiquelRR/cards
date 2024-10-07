package com.miquelrr.cardsx

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.annotation.RequiresApi
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

    private var isFinishing = false

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

    override fun onRestoreInstanceState(savedInstanceState:Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        cardDataList = savedInstanceState.getParcelableArrayList<CardData>("cardDataList")?.toMutableList() ?: mutableListOf()
        deckCards = savedInstanceState.getParcelableArrayList<CardData>("deckCards")?.toMutableList()?: mutableListOf()
        adapter.updateCardDataList(cardDataList)

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
                recyclerView.layoutManager = createGridLayoutManager(recyclerView, 700)
            }
        })
        fab.setOnClickListener {
            if (isAddIcon) {
                if (deckCards.isNotEmpty()) {
                    val cardToMove = deckCards.removeAt(0)
                    cardDataList.add(cardToMove)
                    cardToMove.isSelected=false
                    updateFabIcon()
                    adapter.notifyItemInserted(cardDataList.size-1)
                    cardDataJsonManager.saveAllCards(cardDataList,deckCards)
                }
            } else {
                val selectedIndex : Int? = adapter.getSelectedCard()
                if (selectedIndex !=null) {
                    val cardToMove = cardDataList.removeAt(selectedIndex)
                    deckCards.add(cardToMove)
                    adapter.setSelectedCardNull()
                    updateFabIcon()
                    adapter.notifyItemRemoved(selectedIndex)
                    cardDataJsonManager.saveAllCards(cardDataList,deckCards)
                }
            }
            if (savedInstanceState != null){
                Toast.makeText(this, cardDataList[cardDataList.size-1].title, Toast.LENGTH_SHORT).show()
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
    override fun onPause() {
        super.onPause()
        isFinishing = isFinishing()
    }


    override fun onStop() {
        super.onStop()
        cardDataJsonManager.saveAllCards(cardDataList,deckCards)
    }
    override fun onDestroy() {
        super.onDestroy()
        cardDataJsonManager.saveAllCards(cardDataList,deckCards)
    }
}