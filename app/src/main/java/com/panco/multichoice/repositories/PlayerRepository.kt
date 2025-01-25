package com.panco.multichoice.repositories

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.panco.multichoice.models.Player

class PlayerRepository(private val db: SQLiteDatabase) {

    companion object {
        private const val TABLE_NAME = "player"
        private const val COLUMN_ID = "player_id"
        private const val  COLUMN_USERNAME = "username"
    }

    fun addPlayer(username: String): Long {
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun getPlayerById(id: Int): Player? {
        val cursor = db.query(TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_USERNAME),
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            val player = Player(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))
            )
            cursor.close()
            return player
        } else {
            cursor.close()
            return null
        }
    }

    fun getPlayerByUsername(username: String): Player? {
        val cursor = db.query(TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_USERNAME),
            "$COLUMN_USERNAME = ?",
            arrayOf(username),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            val player = Player(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))
            )
            cursor.close()
            return player
        } else {
            cursor.close()
            return null
        }
    }


    // Update
    fun updatePlayer(id: Int, newUsername: String): Int {
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, newUsername)
        }
        return db.update(
            TABLE_NAME, values,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )


    }

    // Delete
    fun deletePlayer(id: Int): Int {
        return db.delete(
            TABLE_NAME,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
    }

    // Get all players
    fun getAllPlayers(): List<Player> {
        val players = mutableListOf<Player>()
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val player = Player(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))
                )
                players.add(player)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return players
    }

}