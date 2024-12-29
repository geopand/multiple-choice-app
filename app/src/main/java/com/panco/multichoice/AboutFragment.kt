package com.panco.multichoice

import DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.panco.multichoice.databinding.FragmentAboutBinding
import com.panco.multichoice.models.Question
import com.panco.multichoice.repositories.QuestionRepository


class AboutFragment : Fragment() {
    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: SQLiteDatabase
    private val DB_NAME = "quiz-db"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        val view = binding.root

        DatabaseUtils.copyDatabase(requireContext(), DB_NAME)
        db = SQLiteDatabase.openDatabase(
            requireContext().getDatabasePath(DB_NAME).path,
            null,
            SQLiteDatabase.OPEN_READWRITE
        )
        /*
        val repo = QuestionRepository(db)
        val questions: List<Question> = repo.getQuestionsByIds(listOf(1))
        println("\n\n\n\n\n---------------------------\n")
        for (question in  questions) {
            println(question.id)
            println(question.text)
        }
        */
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}