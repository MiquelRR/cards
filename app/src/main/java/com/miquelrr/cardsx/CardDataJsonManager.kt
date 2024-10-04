package com.miquelrr.cardsx

class CardDataLoader {
    fun loadCardsFromJson(json: String): List<CardData> {
        val jsonString = applicationContext.assets.open("cards.json").bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(jsonString)
        val cardDataList = mutableListOf<CardData>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val cardData = CardData(
                jsonObject.getString("imageUrl"),
                jsonObject.getString("title"),
                jsonObject.getString("description"),
                jsonObject.getString("externalLink"),
                jsonObject.getInt("cardViewType")
            )
            cardDataList.add(cardData)
        }
        return cardDataList

     }
}