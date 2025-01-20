package com.panco.multichoice

import DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.panco.multichoice.databinding.FragmentGameIntroBinding
import com.panco.multichoice.models.Question
import com.panco.multichoice.models.Questionnaire
import com.panco.multichoice.repositories.QuestionRepository
import com.panco.multichoice.viewModels.GameViewModel

class GameIntroFragment : Fragment() {
    private var _binding: FragmentGameIntroBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: SQLiteDatabase
    private val DB_NAME = "quiz-db"
    private val QUESTIONS_SIZE: Int = 10
    private val gameViewModel: GameViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        _binding = FragmentGameIntroBinding.inflate(inflater, container, false)
        val view = binding.root

        //Navigate to PlayGame fragment
        val actionToPlayGame = GameIntroFragmentDirections.actionGameIntroFragmentToPlayGameFragment()
        val btnStart = view.findViewById<Button>(R.id.btnStartGame)
        val etEnterUserName = view.findViewById<AppCompatEditText>(R.id.etUserName)
        btnStart.setOnClickListener {

            if (etEnterUserName.text.toString().isEmpty()) {
                Toast.makeText(view.context, "Παρακαλώ εισάγετε ένα όνομα χρήστη", Toast.LENGTH_SHORT).show()
            } else {
                view.findNavController().navigate(actionToPlayGame)
            }
        }

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
        println("\n\n\n\n\n---------------------------\n")
        for (question in questions) {
            println(question.id)
            println(question.text)
            question.answers.forEach {
                println("${it.id} | ${it.text} | ${it.isCorrect} | ${it.questionId}")
            }
        }

        val questionnaire = Questionnaire(questions)
        gameViewModel.sharedData.value = questionnaire

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}