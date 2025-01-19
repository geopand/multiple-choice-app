package com.panco.multichoice

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.panco.multichoice.databinding.FragmentAboutBinding
import com.panco.multichoice.databinding.FragmentPlayGameBinding
import com.panco.multichoice.models.Questionnaire
import com.panco.multichoice.viewModels.GameViewModel


class PlayGameFragment : Fragment() {
    private var _binding: FragmentPlayGameBinding? = null
    private val binding get() = _binding!!
    private val gameViewModel: GameViewModel by activityViewModels()
    private var receivedData: Questionnaire? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        _binding = FragmentPlayGameBinding.inflate(inflater, container, false)
        val view = binding.root

        var questions: Questionnaire? = null
        gameViewModel.sharedData.observe(viewLifecycleOwner){  data ->
            receivedData = data
           Log.d("target", "received data is: $data")
            receivedData?.questions?.forEach { q ->
                println(q.text)
            }
        }


        return view
    }

}