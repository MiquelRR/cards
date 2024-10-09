package com.miquelrr.cardsx

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView

class MyAdapter(private var cardDataList: List<CardData>) : RecyclerView.Adapter<MyAdapter.CardViewHolder>() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var snapHelper: LinearSnapHelper

    private var selectedCardIndex: Int? = null

    companion object {
        const val CARD_A = 0
        const val CARD_B = 1
        const val CARD_C = 2
    }

    fun getSelectedCard(): Int? {
        for (card in cardDataList) {if (card.isSelected) {selectedCardIndex=cardDataList.indexOf(card);break}}
        return selectedCardIndex
    }

    fun setSelectedCard(position: Int) {
        selectedCardIndex = position
        notifyItemChanged(position)
    }

    fun setSelectedCardNull(){
        selectedCardIndex = null
        notifyDataSetChanged()
    }

    // Only one cardholder for all models of cards
    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.card_a_image)
        val titleTextView: TextView = itemView.findViewById(R.id.card_title)
        val descriptionTextView: TextView = itemView.findViewById(R.id.card_paragraph)
        val button: Button = itemView.findViewById(R.id.card_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val layoutModel = when (viewType) {
            CARD_A -> R.layout.card_a_layout
            CARD_B -> R.layout.card_b_layout
            CARD_C -> R.layout.card_c_layout
            else -> throw IllegalArgumentException("Not implemented layout for value "+viewType)
        }
        val itemView = layoutInflater.inflate(layoutModel, parent, false)

        return CardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val cardData = cardDataList[position]
        Glide.with(holder.itemView.context)
            .load(cardData.imageUrl)
            .override(300, 300)
            .into(holder.imageView)
        holder.titleTextView.text = cardData.title
        holder.descriptionTextView.text = cardData.description
        holder.button.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(cardData.externalLink))
            holder.itemView.context.startActivity(intent) }
        val cardView = holder.itemView as MaterialCardView
        if (cardData.isSelected) {
            //cardView.cardElevation = holder.itemView.context.resources.getDimension(R.dimen.selected_card_elevation)
            cardView.cardElevation = 8.0F
            selectedCardIndex=cardDataList.indexOf(cardData)
        } else {
            //cardView.cardElevation = holder.itemView.context.resources.getDimension(R.dimen.unselected_card_elevation)
            cardView.cardElevation = 0.0F
        }

        holder.itemView.setOnClickListener {
            val sel = !cardData.isSelected
            for (card in cardDataList) {
                if (card.isSelected) {
                    card.isSelected = false
                    notifyItemChanged(cardDataList.indexOf(card))
                }
            }
            cardData.isSelected = sel
            selectedCardIndex = if (sel) position else null
            if (selectedCardIndex != null) {
                val layoutManager = recyclerView.layoutManager
                val viewToSnap = layoutManager?.findViewByPosition(selectedCardIndex!!)
                if (viewToSnap != null) {
                    recyclerView.smoothScrollToPosition(selectedCardIndex!!)
                }
            }
            notifyItemChanged(position)
            (holder.itemView.context as MainActivity).updateFabIcon()
        }
    }
    override fun getItemCount(): Int = cardDataList.size

    override fun getItemViewType(position: Int): Int {
        return cardDataList[position].cardViewType
    }
    fun updateCardDataList(newCardDataList: List<CardData>) {
        cardDataList = newCardDataList
        notifyDataSetChanged()
    }
    fun attachToRecyclerView(recyclerView: RecyclerView, snapHelper: LinearSnapHelper) {
        this.recyclerView = recyclerView
        this.snapHelper = snapHelper
    }
}