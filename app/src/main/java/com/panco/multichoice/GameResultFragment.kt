package com.panco.multichoice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.panco.multichoice.databinding.FragmentGameIntroBinding
import com.panco.multichoice.databinding.FragmentGameResultBinding
import com.panco.multichoice.utils.ToolBarHelper

class GameResultFragment : Fragment() {
    private var _binding: FragmentGameResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGameResultBinding.inflate(inflater, container, false)
        val view = binding.root
        ToolBarHelper.setToolBarTitle(this, "Αποτελέσματα")

        var result = GameResultFragmentArgs.fromBundle(requireArguments()).score //get the score

        return view
    }

    companion object {


    }
}