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
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.panco.multichoice.databinding.FragmentPlayGameBinding
import com.panco.multichoice.models.Answer
import com.panco.multichoice.models.Question
import com.panco.multichoice.models.Questionnaire
import com.panco.multichoice.repositories.QuestionRepository
import com.panco.multichoice.utils.ToolBarHelper


class PlayGameFragment : Fragment() {
    private var _binding: FragmentPlayGameBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: SQLiteDatabase
    private val DB_NAME = "quiz-db"
    private val QUESTIONS_SIZE: Int = 10
    private val isDebugMode: Boolean = true //for local testing reasons keep it true
    private var selectedOption: Int = 0
    private lateinit var questionnaire: Questionnaire
    private lateinit var currentQuestion: Question
    private lateinit var currentAnswer: Answer
    var currentPosition: Int = 1
    var judged: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        _binding = FragmentPlayGameBinding.inflate(inflater, container, false)
        val view = binding.root
        ToolBarHelper.setToolBarTitle(this, "Ερώτηση: $currentPosition")

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
            for (question in questions) {
                println(question.id)
                println(question.text)
                question.answers.forEach {
                    println("${it.id} | ${it.text} | ${it.isCorrect} | ${it.questionId}")
                }
            }
        }

        questionnaire = Questionnaire(questions)

        loadNextQuestion(currentPosition, questionnaire)
        // val q: Question  = questionnaire.questions[currentPosition]


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
        binding.btnSubmit.setOnClickListener {
            if (selectedOption == 0) {
                Toast.makeText(view.context, "Παρακαλώ επιλέξτε κάποια απάντηση", Toast.LENGTH_SHORT).show()
            } else {
                if (questionnaire.questions.size == currentPosition) {
                    judgeAnswers()
                    Toast.makeText(view.context, "Τέλος Παιχνιδιού", Toast.LENGTH_SHORT).show()
                } else {
                    if (!judged) {
                        judgeAnswers()
                        binding.btnSubmit.text = "ΕΠΟΜΕΝΗ ΕΡΩΤΗΣΗ"
                    } else {
                        ++currentPosition
                        selectedOption = 0
                        judged = false
                        resetOptionsStyling()
                        loadNextQuestion(currentPosition, questionnaire)
                        updateProgressBar(currentPosition)
                        //todo send the number of the correct answers
                    }
                }
            }

        }
        return view
    }

    private fun updateProgressBar(position: Int) {
        binding.progressBar.progress = position
        binding.tvProgressRatio.text = "$position/${binding.progressBar.max}"
    }

    private fun loadNextQuestion(currentQuestionNo: Int, questionnaire: Questionnaire) {
        currentQuestion = questionnaire.questions[currentQuestionNo - 1]
        binding.tvQuestion.text = currentQuestion.text
        binding.tvOptionOne.text = currentQuestion.answers[0].text
        binding.tvOptionTwo.text = currentQuestion.answers[1].text
        binding.tvOptionThree.text = currentQuestion.answers[2].text
        binding.tvOptionFour.text = currentQuestion.answers[3].text
        ToolBarHelper.setToolBarTitle(this, "Ερώτηση: $currentQuestionNo")
        if (currentPosition == questionnaire.questions.size) {
            binding.btnSubmit.text = "ΤΕΛΟΣ"
        } else{
            binding.btnSubmit.text = "ΥΠΟΒΟΛΗ"
        }
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
            option.setTextColor(Color.parseColor("#7A8089"))
            option.typeface = Typeface.DEFAULT
            option.background = view?.let { ContextCompat.getDrawable(it.context, R.drawable.option_border_bg_default) }
        }
    }

    private fun selectOptionView(tv: TextView, selection: Int) {
        if (judged) { //User can select an answer ONLY IF the question has not been judged
            return
        }
        resetOptionsStyling()
        tv.setTextColor(Color.parseColor("#363A43"))
        tv.setTypeface(tv.typeface, Typeface.BOLD)
        tv.background = view?.let { ContextCompat.getDrawable(it.context, R.drawable.option_border_bg_selected) }
        selectedOption = selection
        currentAnswer = currentQuestion.answers[selection -1]
    }

    private fun judgeAnswers() {
        if (currentAnswer.isCorrect) {
            markAnswerByCorrectNess(selectedOption, R.drawable.option_border_bg_correct)
        } else {
            markAnswerByCorrectNess(selectedOption, R.drawable.option_border_bg_wrong)
            for (i in currentQuestion.answers.indices) {
                println("\n\n\n\n")
                if (currentQuestion.answers[i].isCorrect) {
                    println("Correct is: $i")
                    markAnswerByCorrectNess(i + 1, R.drawable.option_border_bg_correct)
                }
            }
        }
        judged = true
    }


    /**
     * answerNo: the number of the answer to paint
     * drawbleView: the background to apply to the answer
     */
    private fun markAnswerByCorrectNess(answerNo:Int, drawableView: Int) {
        when(answerNo) {
            1 -> {
                binding.tvOptionOne.background = view?.let { ContextCompat.getDrawable(it.context, drawableView) }
            }
            2 -> {
                binding.tvOptionTwo.background = view?.let { ContextCompat.getDrawable(it.context, drawableView) }
            }
            3 -> {
                binding.tvOptionThree.background = view?.let { ContextCompat.getDrawable(it.context, drawableView) }
            }
            4 -> {
                binding.tvOptionFour.background = view?.let { ContextCompat.getDrawable(it.context, drawableView) }
            }
        }
    }

//   https://www.youtube.com/watch?v=b21fiIyOW4A&t=1s&ab_channel=tutorialsEU

}