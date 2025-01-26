package com.panco.multichoice.repositories

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import androidx.annotation.RequiresApi
import com.panco.multichoice.models.Game
import java.time.Instant

class GameRepository (private val db: SQLiteDatabase) {
    companion object {
        private const val TABLE_NAME = "game"
        private const val COLUMN_ID = "game_id"
        private const val COLUMN_DATE_STARTED = "date_started"
        private const val COLUMN_PLAYER_ID = "player_id"
        private const val COLUMN_SCORE = "score"
    }

    fun addGameForPlayer(playerId: Int): Long {
        val values = ContentValues().apply {
            put(COLUMN_PLAYER_ID, playerId)
            put(COLUMN_DATE_STARTED, System.currentTimeMillis())
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun getGameById(id: Int): Game? {
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_DATE_STARTED, COLUMN_PLAYER_ID, COLUMN_SCORE),
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            val game = Game(
                gameId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                dateStarted = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE_STARTED)),
                playerId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLAYER_ID)),
                score = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE))
            )
            cursor.close()
            return game
        } else {
            cursor.close()
            return null
        }
    }

    // Update game score
    fun updateGameScore(gameId: Int, score: Int): Int {
        val values = ContentValues().apply {
            put(COLUMN_SCORE, score)
        }
        return db.update(
            TABLE_NAME, values,
            "$COLUMN_ID = ?",
            arrayOf(gameId.toString())
        )
    }

    // Delete
    fun deleteGame(id: Int): Int {
        return db.delete(
            TABLE_NAME,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
    }

    fun getAllGamesByPlayerId(playerId: Int): List<Game> {
        val games = mutableListOf<Game>()
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_DATE_STARTED, COLUMN_PLAYER_ID, COLUMN_SCORE),
            "$COLUMN_PLAYER_ID = ?",
            arrayOf(playerId.toString()),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            do {
                val game = Game(
                    gameId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    dateStarted = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE_STARTED)),
                    playerId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLAYER_ID)),
                    score = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE))
                )
                games.add(game)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return games
    }


}