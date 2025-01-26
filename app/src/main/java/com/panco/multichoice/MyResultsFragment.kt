package com.panco.multichoice

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.panco.multichoice.adapters.TableAdapter
import com.panco.multichoice.databinding.FragmentMyResultsBinding
import com.panco.multichoice.models.Game
import com.panco.multichoice.repositories.GameRepository
import com.panco.multichoice.utils.ToolBarHelper


class MyResultsFragment : Fragment() {
    private var _binding: FragmentMyResultsBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: SQLiteDatabase
    private val DB_NAME = "quiz-db"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        _binding = FragmentMyResultsBinding.inflate(inflater, container, false)
        val view = binding.root
        ToolBarHelper.setToolBarTitle(this, "Το Ιστορικό μου")

        val playerId = MyResultsFragmentArgs.fromBundle(requireArguments()).playerId

        db = SQLiteDatabase.openDatabase(
            requireContext().getDatabasePath(DB_NAME).path,
            null,
            SQLiteDatabase.OPEN_READWRITE
        )

        val gameRepo = GameRepository(db)
        val games: List<Game> = gameRepo.getAllGamesByPlayerId(playerId)
        val customAdapter = TableAdapter(games)

        val recyclerView: RecyclerView =  view.findViewById(R.id.myresults_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.adapter = customAdapter

        return view
    }

}