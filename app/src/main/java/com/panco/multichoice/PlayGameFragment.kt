package com.panco.multichoice

import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.panco.multichoice.databinding.FragmentPlayGameBinding
import com.panco.multichoice.models.Question
import com.panco.multichoice.models.Questionnaire
import com.panco.multichoice.repositories.QuestionRepository


class PlayGameFragment : Fragment() {
    private var _binding: FragmentPlayGameBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: SQLiteDatabase
    private val DB_NAME = "quiz-db"
    private val QUESTIONS_SIZE: Int = 10
    private val isDebugMode: Boolean = true //for local testing reasons keep it true

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

        val questionRepository = QuestionRepository(db)
        val questions: List<Question> = questionRepository.getQuestionsByIds(questionRepository.getRandomQuestionIds(QUESTIONS_SIZE))
        if (isDebugMode) {
            println("\n\n\n\n\n---------------------------\n") //TODO delete this
            for (question in questions) {
                println(question.id)
                println(question.text)
                question.answers.forEach {
                    println("${it.id} | ${it.text} | ${it.isCorrect} | ${it.questionId}")
                }
            }
        }

        val questionnaire = Questionnaire(questions)
        val currentPosition: Int= 0
        val q: Question  = questionnaire.questions[currentPosition]
        binding.tvQuestion.text = q.text
        binding.tvOptionOne.text = q.answers[0].text
        binding.tvOptionTwo.text = q.answers[1].text
        binding.tvOptionThree.text = q.answers[2].text
        binding.tvOptionFour.text = q.answers[3].text

        binding.tvOptionOne.setOnClickListener {
            selectOptionView(binding.tvOptionOne, 1)
        }
        binding.tvOptionTwo.setOnClickListener {
            selectOptionView(binding.tvOptionTwo, 2)
        }
        binding.tvOptionThree.setOnClickListener {
            selectOptionView(binding.tvOptionThree, 3)
        }
        binding.tvOptionFour.setOnClickListener {
            selectOptionView(binding.tvOptionFour, 4)
        }
        return view
    }


    /** Resets the options back to the default style
     */
    private fun resetOptionsStyling() {
        val options = ArrayList<TextView>()
        options.add(0, binding.tvOptionOne)
        options.add(1, binding.tvOptionTwo)
        options.add(2, binding.tvOptionThree)
        options.add(3, binding.tvOptionFour)

        options.forEach{ option ->
            println("Option $option")
            option.setTextColor(Color.parseColor("#7A8089"))
            option.typeface = Typeface.DEFAULT
            option.background = view?.let { ContextCompat.getDrawable(it.context, R.drawable.option_border_bg_default) }
        }
    }

    private fun selectOptionView(tv: TextView, selectedOption: Int) {
        resetOptionsStyling()
        tv.setTextColor(Color.parseColor("#363A43"))
        tv.setTypeface(tv.typeface, Typeface.BOLD)
        tv.background = view?.let { ContextCompat.getDrawable(it.context, R.drawable.option_border_bg_selected) }
    }

}