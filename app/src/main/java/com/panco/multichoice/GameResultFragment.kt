package com.panco.multichoice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.panco.multichoice.databinding.FragmentGameResultBinding
import com.panco.multichoice.utils.ToolBarHelper

class GameResultFragment : Fragment() {
    private var _binding: FragmentGameResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGameResultBinding.inflate(inflater, container, false)
        val view = binding.root
        ToolBarHelper.setToolBarTitle(this, "Αποτελέσματα")

        val score = GameResultFragmentArgs.fromBundle(requireArguments()).score //get the score
        val playerId = GameResultFragmentArgs.fromBundle(requireArguments()).playerId
        binding.tvEarnedPoints.text = score.toString()

        binding.btnAcknowledgePoint.setOnClickListener {
            val action =
                GameResultFragmentDirections.actionGameResultFragmentToMyResultsFragment(playerId)
            view.findNavController().navigate(action)
        }
        return view
    }

}