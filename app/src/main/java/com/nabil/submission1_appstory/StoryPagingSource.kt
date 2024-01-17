package com.nabil.submission1_appstory

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nabil.submission1_appstory.Data.GlobalVariabel.token
import com.nabil.submission1_appstory.Data.ListStory
import com.nabil.submission1_appstory.Retro.ApiService

class StoryPagingSource(private val apiService: ApiService) :
    PagingSource<Int, ListStory>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStory> {
        return try {
            val page = params.key ?: 1
            val size = params.loadSize

            val response = apiService.gainStrories(token, page, size)
            val data = response.listStory

            LoadResult.Page(
                data = data,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (data.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStory>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1)?: anchorPage?.nextKey?.minus(1)
        }
    }
}