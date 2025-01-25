package com.panco.multichoice.repositories

import android.database.sqlite.SQLiteDatabase
import com.panco.multichoice.models.Answer
import com.panco.multichoice.models.Question
import java.util.Collections

class QuestionRepository(private val db: SQLiteDatabase) {

    fun getRandomQuestionIds(limit: Int): List<Int> {
        val query = """
           SELECT q_id as questionId
           FROM question
           ORDER BY random()
           limit $limit;
        """
        val cursor = db.rawQuery(query, null)
        val randomQuestionIds = mutableListOf<Int>()
        cursor.use {
            while (it.moveToNext()) {
                val questionId: Int = cursor.getInt(it.getColumnIndexOrThrow("questionId"))
                randomQuestionIds.add(questionId)
            }
        }
        return randomQuestionIds
    }

    // Get all questions
    fun getQuestionsByIds(questionIds: List<Int>): List<Question> {
        if (questionIds.isEmpty()) return emptyList()

        val questions = mutableListOf<Question>()
        val questionIdsPlaceHolders = questionIds.joinToString(",") { "?" }

        //fetch the questions along with their answers
        val joinQuery = """
                SELECT 
                    q.q_id AS questionId,
                    q.q_text AS questionText,
                    a.answer_id AS answerId,
                    a.answer_text AS answerText,
                    a.is_correct as isCorrect
                FROM question as q
                LEFT JOIN answer as a
                ON q.q_id = a.question_id
                where q.q_id IN ($questionIdsPlaceHolders); 
                """

        val cursor = db.rawQuery(joinQuery, questionIds.map { it.toString() }.toTypedArray())
        val idsToQuestions = mutableMapOf<Int, Question>()

        cursor.use {
            while (it.moveToNext()) {
                val questionId: Int = cursor.getInt(it.getColumnIndexOrThrow("questionId"))
                val questionText: String =
                    cursor.getString(it.getColumnIndexOrThrow("questionText"))
                val answerId: Int = cursor.getInt(it.getColumnIndexOrThrow("answerId"))
                val answerText: String = cursor.getString(it.getColumnIndexOrThrow("answerText"))
                val isCorrect: Boolean = cursor.getInt(it.getColumnIndexOrThrow("isCorrect")) == 1
                val question = idsToQuestions.getOrPut(questionId) {
                    Question(questionId, questionText, mutableListOf(), null)
                }

                if (answerId != 0) {
                    val answer = Answer(answerId, questionId, answerText, isCorrect)
                    (question.answers as MutableList<Answer>).add(answer)
                    (question.answers as MutableList<Answer>).shuffle()
                }
            }
            return idsToQuestions.values.toList()
        }
    }


}