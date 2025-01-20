package com.panco.multichoice

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.panco.multichoice.databinding.FragmentAboutBinding
import com.panco.multichoice.databinding.FragmentPlayGameBinding
import com.panco.multichoice.models.Question
import com.panco.multichoice.models.Questionnaire
import com.panco.multichoice.repositories.QuestionRepository
import com.panco.multichoice.viewModels.GameViewModel


class PlayGameFragment : Fragment() {
    private var _binding: FragmentPlayGameBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: SQLiteDatabase
    private val DB_NAME = "quiz-db"
    private val QUESTIONS_SIZE: Int = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        _binding = FragmentPlayGameBinding.inflate(inflater, container, false)
        val view = binding.root

        val username = PlayGameFragmentArgs.fromBundle(requireArguments()).username //get the username

        //Copy database
        DatabaseUtils.copyDatabase(requireContext(), DB_NAME)
        //Open connection
        db = SQLiteDatabase.openDatabase(
            requireContext().getDatabasePath(DB_NAME).path,
            null,
            SQLiteDatabase.OPEN_READWRITE
        )

        val repo = QuestionRepository(db)
        val questions: List<Question> = repo.getQuestionsByIds(repo.getRandomQuestionIds(QUESTIONS_SIZE))
        println("\n\n\n\n\n---------------------------\n") //TODO delete this
        for (question in questions) {
            println(question.id)
            println(question.text)
            question.answers.forEach {
                println("${it.id} | ${it.text} | ${it.isCorrect} | ${it.questionId}")
            }
        }

        val questionnaire = Questionnaire(questions)


        return view
    }

}