package com.miquelrr.cardsx

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.util.Collections

class MainActivity :AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var cardDataList: MutableList<CardData>
    private lateinit var deckCards: MutableList<CardData>
    private lateinit var bottomAppBar: BottomAppBar
    private var selectedCardPosition: Int? = null

    private lateinit var adapter: MyAdapter
    private lateinit var cardDataJsonManager: CardDataJsonManager
    lateinit var snapHelper: LinearSnapHelper

    private var isFinishing = false

    fun createGridLayoutManager(recyclerView: RecyclerView, columnWidth: Int): GridLayoutManager {
        val totalWidth = recyclerView.width // Total wide of RecyclerView
        val columns = (totalWidth / columnWidth).coerceAtLeast(1)
        val layoutManager = GridLayoutManager(recyclerView.context, columns)
        return layoutManager
    }
    lateinit var fab: FloatingActionButton

    fun refreshIcons() {
        selectedCardPosition = adapter.getSelectedCard() ?: -1
        val cardSelected = selectedCardPosition != -1
        bottomAppBar.menu.findItem(R.id.action_up).isVisible=cardSelected && selectedCardPosition!=0
        bottomAppBar.menu.findItem(R.id.action_down).isVisible=cardSelected && selectedCardPosition!=cardDataList.size-1
        bottomAppBar.menu.findItem(R.id.action_remove).isVisible=cardSelected
        bottomAppBar.menu.findItem(R.id.action_exit).isVisible=!cardSelected
        fab.isEnabled=deckCards.isNotEmpty()
    }

    override fun onRestoreInstanceState(savedInstanceState:Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        //Teacher: i know that getParcelableArrayList<T>("cardDataList") is deprecated, but getParcelableArrayList("cardDataList", CardData::class.java) crashes on my android phone
        cardDataList = savedInstanceState.getParcelableArrayList<CardData>("cardDataList")?.toMutableList() ?: mutableListOf()
        deckCards = savedInstanceState.getParcelableArrayList<CardData>("deckCards")?.toMutableList()?: mutableListOf()
        adapter.updateCardDataList(cardDataList)
        focusOnSelected()


    }

    private fun focusOnSelected(){
        selectedCardPosition = adapter.getSelectedCard() ?: -1
        if (selectedCardPosition != -1) {
            recyclerView.postDelayed({ // Retrasar el scroll
                recyclerView.smoothScrollToPosition(selectedCardPosition!!)
            }, 500)
            refreshIcons()

        }
    }

    private fun showUndoSnackbar(removedCard: CardData, position: Int) {
        val snackbar = Snackbar.make(findViewById(R.id.my_recycler_view),
            resources.getString(R.string.remove_advice)+" '"+removedCard.title+"'",
            Snackbar.LENGTH_LONG
        )
        snackbar.setAction(getString(R.string.undo)) {
            cardDataList.add(position, removedCard)
            deckCards.remove(removedCard)
            adapter.updateCardDataList(cardDataList)
            adapter.setSelectedCard(position)
            refreshIcons()
            adapter.notifyItemInserted(position)
        }
        snackbar.anchorView = fab
        snackbar.show()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomAppBar = findViewById<BottomAppBar>(R.id.bottomAppBar)

        cardDataJsonManager = CardDataJsonManager(applicationContext)

        if (savedInstanceState != null){
            //Teacher: i know that getParcelableArrayList<T>("cardDataList") is deprecated, but getParcelableArrayList("cardDataList", CardData::class.java) crashes on my android phone
            cardDataList = savedInstanceState.getParcelableArrayList<CardData>("cardDataList")?.toMutableList() ?: mutableListOf()
            deckCards = savedInstanceState.getParcelableArrayList<CardData>("deckCards")?.toMutableList() ?: mutableListOf()
        } else {
            cardDataList = cardDataJsonManager.loadCardsFromJson().toMutableList()
            deckCards = cardDataJsonManager.loadCardsFromJson("deck.json").toMutableList()
        }

        recyclerView = findViewById(R.id.my_recycler_view)
        adapter = MyAdapter(cardDataList)
        snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)
        adapter.attachToRecyclerView(recyclerView, snapHelper)

        fab=findViewById(R.id.fab)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        // Aplicar delay en la configuración del LayoutManager
        recyclerView.postDelayed( {
            val gridLayoutWidth =resources.getDimensionPixelSize(R.dimen.max_column_size)
            recyclerView.layoutManager = createGridLayoutManager(recyclerView, gridLayoutWidth)
            refreshIcons()
        },300)

        fab.setOnClickListener {

            selectedCardPosition = adapter.getSelectedCard()

                if (deckCards.isNotEmpty()) {
                    if (selectedCardPosition != null) {
                        cardDataList[selectedCardPosition!!].isSelected=false
                    }
                    val cardToMove = deckCards.removeAt(0)
                    cardDataList.add(cardToMove)
                    cardToMove.isSelected=true
                    adapter.setSelectedCard(cardDataList.size-1)
                    adapter.notifyItemInserted(cardDataList.size-1)
                    focusOnSelected()
                    cardDataJsonManager.saveAllCards(cardDataList,deckCards)
                }



        }

        bottomAppBar.replaceMenu(R.menu.bottom_app_bar_menu)

        bottomAppBar.setOnMenuItemClickListener{ menuItem ->
            when (menuItem.itemId) {
                R.id.action_exit -> {
                    AlertDialog.Builder(this)
                        .setTitle(getString(R.string.exit_app))
                        .setMessage(getString(R.string.really_wanna_exit))
                        .setPositiveButton(getString(R.string.yes)) { _, _ ->
                            finish()
                        }
                        .setNegativeButton(getString(R.string.no), null)
                        .show()
                    true
                }
                R.id.action_up -> {
                    val selectedPosition : Int = adapter.getSelectedCard() ?: -1
                    Collections.swap(cardDataList, selectedPosition, selectedPosition - 1)
                    adapter.notifyItemMoved(selectedPosition, selectedPosition - 1)
                    adapter.notifyItemChanged(selectedPosition)
                    adapter.notifyItemChanged(selectedPosition - 1)
                    adapter.setSelectedCard(selectedPosition - 1)
                    true
                }
                R.id.action_down -> {
                    val selectedPosition : Int = adapter.getSelectedCard() ?: -1
                    Collections.swap(cardDataList, selectedPosition, selectedPosition + 1 )
                    adapter.notifyItemMoved(selectedPosition, selectedPosition + 1)
                    adapter.notifyItemChanged(selectedPosition)
                    adapter.notifyItemChanged(selectedPosition + 1)
                    adapter.setSelectedCard(selectedPosition + 1)
                    true
                }
                R.id.action_remove -> {
                    val selectedIndex : Int? = adapter.getSelectedCard()
                    if (selectedIndex !=null) {
                        val cardToMove = cardDataList.removeAt(selectedIndex)
                        deckCards.add(cardToMove)
                        adapter.setSelectedCardNull()
                        adapter.notifyItemRemoved(selectedIndex)
                        refreshIcons()
                        showUndoSnackbar(cardToMove,selectedIndex)
                        cardDataJsonManager.saveAllCards(cardDataList,deckCards)
                    }
                    true
                }
                else -> false
            }
        }

        focusOnSelected()


    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("cardDataList", ArrayList(cardDataList))
        outState.putParcelableArrayList("deckCards", ArrayList(deckCards))
        outState.putInt("selectedCardPosition", adapter.getSelectedCard() ?: -1)
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