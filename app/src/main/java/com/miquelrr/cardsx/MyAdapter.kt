package com.miquelrr.cardsx

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView

class MyAdapter(private var cardDataList: List<CardData>) :
    RecyclerView.Adapter<MyAdapter.CardViewHolder>() {

    private var selectedCardIndex: Int? = null

    companion object {
        const val CARD_A = 0
        const val CARD_B = 1
        const val CARD_C = 2
    }

    fun getSelectedCard(): Int? {
        return selectedCardIndex
    }

    fun setSelectedCardNull(){
        selectedCardIndex = null
        notifyDataSetChanged()
    }

    // Un solo ViewHolder para todos los tipos de tarjeta
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
            else -> throw IllegalArgumentException("Tipo de vista inválido")
        }
        val itemView = layoutInflater.inflate(layoutModel, parent, false)
        return CardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val cardData = cardDataList[position]
        Glide.with(holder.itemView.context)
            .load(cardData.imageUrl)
            .into(holder.imageView)
        holder.titleTextView.text = cardData.title
        holder.descriptionTextView.text = cardData.description
        holder.button.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(cardData.externalLink))
            holder.itemView.context.startActivity(intent) }
        // ... (Configura el botóny otros elementos)
        val cardView = holder.itemView as MaterialCardView // Asumiendo que la vista es un MaterialCardView
        if (cardData.isSelected) {
            cardView.cardElevation = 8.0F // Elevación alta
        } else {
            cardView.cardElevation = 0.0F // Elevación baja
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
}