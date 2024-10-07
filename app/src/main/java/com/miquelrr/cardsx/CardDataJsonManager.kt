package com.miquelrr.cardsx

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileWriter

class CardDataJsonManager(private val applicationContext: Context) {

    private val defaultCardsFileName = "cards.json"
    private val defaultDeckFileName = "deck.json"

    private fun copyAssetToFile(fileName: String) {
        val assetManager = applicationContext.assets
        val file = File(applicationContext.filesDir, fileName)

        assetManager.open(fileName).use { inputStream ->
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }
    fun saveAllCards(hand: List<CardData>,deck: List<CardData>){
        saveCardDataToJson(hand, defaultCardsFileName)
        saveCardDataToJson(deck, defaultDeckFileName)
    }

    fun loadCardsFromJson(fileName: String = defaultCardsFileName): List<CardData> {
        val file = File(applicationContext.filesDir, fileName)
        if (!file.exists()) {
            copyAssetToFile(fileName)
        }

        val jsonString = file.readText()
        val jsonArray = JSONArray(jsonString)
        val cardDataList = mutableListOf<CardData>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val cardData = CardData(
                jsonObject.getString("imageUrl"),
                jsonObject.getString("title"),
                jsonObject.getString("description"),
                jsonObject.getString("externalLink"),
                jsonObject.getInt("cardViewType"),
                jsonObject.getBoolean("isSelected")
            )
            cardDataList.add(cardData)
        }
        return cardDataList

     }

    fun saveCardDataToJson(cardDataList: List<CardData>, fileName: String = "cards.json") {
        val jsonArray = JSONArray()
        for (cardData in cardDataList) {
            val jsonObject = JSONObject()
            jsonObject.put("imageUrl", cardData.imageUrl)
            jsonObject.put("title", cardData.title)
            jsonObject.put("description", cardData.description)
            jsonObject.put("externalLink", cardData.externalLink)
            jsonObject.put("cardViewType", cardData.cardViewType)
            jsonObject.put("isSelected", cardData.isSelected)
            jsonArray.put(jsonObject)
        }

        val file = File(applicationContext.filesDir, fileName)
        file.writeText(jsonArray.toString(2))
    }
}