package com.smusic.data.allSongs

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.smusic.model.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AllSongsLocalDataSourceImpl @Inject constructor(@ApplicationContext private val context:Context) : AllSongsLocalDataSource {
    override  fun getLocalSongs(): List<Song> {
            val songList = ArrayList<Song>()
            val songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
            val songCursor = context.contentResolver?.query(
                songUri,
                null,
                selection,
                null,
                null
            )
            if (songCursor != null && songCursor.moveToFirst()) {
                val sArtWorkUri = Uri.parse("content://media/external/audio/albumart")
                val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
                val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
                val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                val songData = songCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
                val albumColumn = songCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                while (songCursor.moveToNext()) {
                    val currentId = songCursor.getLong(songId)
                    val currentTitle = songCursor.getString(songTitle)
                    val currentArtist = songCursor.getString(songArtist)
                    val currentData = songCursor.getString(songData)
                    val currentDate = songCursor.getLong(dateIndex)
                    val songAlbum = songCursor.getLong(albumColumn)
                    val uri = ContentUris.withAppendedId(sArtWorkUri, songAlbum)
                    songList.add(
                        Song(
                            currentId,
                            currentTitle,
                            currentArtist,
                            currentData,
                            currentDate,
                            uri
                        )
                    )
                }
            }
            songCursor?.close()
            return songList

    }
}