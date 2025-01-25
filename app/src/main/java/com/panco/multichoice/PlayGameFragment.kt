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
import com.panco.multichoice.models.Game
import com.panco.multichoice.models.Player
import com.panco.multichoice.models.Question
import com.panco.multichoice.models.Questionnaire
import com.panco.multichoice.repositories.GameRepository
import com.panco.multichoice.repositories.PlayerRepository
import com.panco.multichoice.repositories.QuestionRepository
import com.panco.multichoice.utils.ToolBarHelper


class PlayGameFragment : Fragment() {
    private var _binding: FragmentPlayGameBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: SQLiteDatabase
    private val DB_NAME = "quiz-db"
    private val QUESTIONS_SIZE: Int = 10
    private val isDebugMode: Boolean = false //for local testing reasons keep it true
    private var selectedOption: Int = 0
    private lateinit var questionnaire: Questionnaire
    private lateinit var currentQuestion: Question
    private lateinit var currentAnswer: Answer
    private var currentPosition: Int = 1
    private var judged: Boolean = false
    private var playerScore: Int = 0
    private var isFinished = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayGameBinding.inflate(inflater, container, false)
        val view = binding.root
        ToolBarHelper.setToolBarTitle(this, "Ερώτηση: $currentPosition")


        //Copy database
        DatabaseUtils.copyDatabase(requireContext(), DB_NAME)
        //Open connection
        db = SQLiteDatabase.openDatabase(
            requireContext().getDatabasePath(DB_NAME).path,
            null,
            SQLiteDatabase.OPEN_READWRITE
        )

        val questionRepository = QuestionRepository(db)
        val playerRepo = PlayerRepository(db)
        val gameRepo = GameRepository(db)

        val username = PlayGameFragmentArgs.fromBundle(requireArguments()).username //get the username
        val player: Player? = playerRepo.getPlayerByUsername(username)
        var playerId: Int = -1
        if (player == null) {
            println("Going to add a new player with username: ${username}")
            playerId = playerRepo.addPlayer(username).toInt()
        } else {
            println("Will use existing player")
        }

        var gameId: Long = -1L
        if (playerId != -1) {
            //create game if player exists
            gameId = gameRepo.addGameForPlayer(playerId)
        }

        //Load game from db
        val game: Game? = gameRepo.getGameById(gameId.toInt())
        if (game == null) {
            Toast.makeText(view.context, "Αποτυχία έναρξης παιχνιδιού", Toast.LENGTH_SHORT).show()
            throw RuntimeException("Could not create game")
        }

        if (isDebugMode) {
            if (player != null) {
                val games: List<Game> = gameRepo.getAllGamesByPlayerId(player.id)
                games.forEach { it ->
                    println("gameId:${it.gameId} --playerId: ${it.playerId} ---dateStarted: ${it.dateStarted} --score: ${it.score}")
                }
            }
        }

        val questions: List<Question> = questionRepository.getQuestionsByIds(
            questionRepository.getRandomQuestionIds(QUESTIONS_SIZE)
        )
        questionnaire = Questionnaire(questions)

        if (isDebugMode) {
            printAll(questions)
        }

        loadNextQuestion(currentPosition, questionnaire)

        // set up the listeners of the options
        setOnClickListenersForOptions()

        binding.btnSubmit.setOnClickListener {
            if (selectedOption == 0) {
                Toast.makeText(
                    view.context,
                    "Παρακαλώ επιλέξτε κάποια απάντηση",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (questionnaire.questions.size == currentPosition) {
                    if (isFinished) {
                        gameRepo.updateGameScore(game.gameId, playerScore)
                    } else {
                        judgeAnswersVisually()
                        updateScore()
                        isFinished = true
                        Toast.makeText(view.context, "Τέλος Παιχνιδιού", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    if (!judged) {
                        judgeAnswersVisually()
                        updateScore()
                        binding.btnSubmit.text = "ΕΠΟΜΕΝΗ ΕΡΩΤΗΣΗ"
                    } else {
                        ++currentPosition
                        selectedOption = 0
                        judged = false
                        resetOptionsStyling()
                        loadNextQuestion(currentPosition, questionnaire)
                        updateProgressBar(currentPosition)
                    }
                }
            }

        }
        return view
    }

    private fun setOnClickListenersForOptions() {
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
    }

    private fun printAll(questions: List<Question>) {
        for (question in questions) {
            println(question.id)
            println(question.text)
            question.answers.forEach {
                println("${it.id} | ${it.text} | ${it.isCorrect} | ${it.questionId}")
            }
        }
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
//        if (currentPosition == questionnaire.questions.size) {
//            binding.btnSubmit.text = "ΤΕΛΟΣ"
//        } else{
        binding.btnSubmit.text = "ΥΠΟΒΟΛΗ"
//        }
    }

    /** Resets the options back to the default style */
    private fun resetOptionsStyling() {
        val options = ArrayList<TextView>()
        options.add(0, binding.tvOptionOne)
        options.add(1, binding.tvOptionTwo)
        options.add(2, binding.tvOptionThree)
        options.add(3, binding.tvOptionFour)

        options.forEach { option ->
            option.setTextColor(Color.parseColor("#7A8089"))
            option.typeface = Typeface.DEFAULT
            option.background = view?.let {
                ContextCompat.getDrawable(
                    it.context,
                    R.drawable.option_border_bg_default
                )
            }
        }
    }

    private fun selectOptionView(tv: TextView, selection: Int) {
        if (judged) { //User can select an answer ONLY IF the question has not been judged
            return
        }
        resetOptionsStyling()
        tv.setTextColor(Color.parseColor("#363A43"))
        tv.setTypeface(tv.typeface, Typeface.BOLD)
        tv.background = view?.let {
            ContextCompat.getDrawable(
                it.context,
                R.drawable.option_border_bg_selected
            )
        }
        selectedOption = selection
        currentAnswer = currentQuestion.answers[selection - 1]
    }

    /**
     * show to the user if the answer was correct and if not which is the correct answer
     */
    private fun judgeAnswersVisually() {
        if (currentAnswer.isCorrect) {
            markAnswerByCorrectNess(selectedOption, R.drawable.option_border_bg_correct)
        } else {
            markAnswerByCorrectNess(selectedOption, R.drawable.option_border_bg_wrong)
            for (i in currentQuestion.answers.indices) {
                if (currentQuestion.answers[i].isCorrect) {
                    println("Correct is: $i")
                    markAnswerByCorrectNess(i + 1, R.drawable.option_border_bg_correct)
                }
            }
        }
        judged = true
    }

    private fun updateScore() {
        if (currentAnswer.isCorrect) {
            ++playerScore
        }
    }

    /**
     * answerNo: the number of the answer to paint
     * drawbleView: the background to apply to the answer
     */
    private fun markAnswerByCorrectNess(answerNo: Int, drawableView: Int) {
        when (answerNo) {
            1 -> {
                binding.tvOptionOne.background =
                    view?.let { ContextCompat.getDrawable(it.context, drawableView) }
            }

            2 -> {
                binding.tvOptionTwo.background =
                    view?.let { ContextCompat.getDrawable(it.context, drawableView) }
            }

            3 -> {
                binding.tvOptionThree.background =
                    view?.let { ContextCompat.getDrawable(it.context, drawableView) }
            }

            4 -> {
                binding.tvOptionFour.background =
                    view?.let { ContextCompat.getDrawable(it.context, drawableView) }
            }
        }
    }

//   https://www.youtube.com/watch?v=b21fiIyOW4A&t=1s&ab_channel=tutorialsEU

}