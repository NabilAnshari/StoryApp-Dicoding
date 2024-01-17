package com.nabil.submission1_appstory.utils

import com.nabil.submission1_appstory.Data.ListStory

object DataDummy {
    fun generateDummyQuoteResponse(): List<ListStory> {
        val items: MutableList<ListStory> = arrayListOf()
        for (i in 0..100) {
            val story = ListStory(
                photoUrl = "1",
                createdAt = "",
                name = "Name $i",
                description = "Description $i",
                id = "id_$i",
                lat = i.toDouble() * 10,
                lon = i.toDouble() * 10,
            )
            items.add(story)
        }
        return items
    }
}