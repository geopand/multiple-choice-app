package com.panco.multichoice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.panco.multichoice.databinding.FragmentGameIntroBinding
import com.panco.multichoice.utils.ToolBarHelper

class GameIntroFragment : Fragment() {
    private var _binding: FragmentGameIntroBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        _binding = FragmentGameIntroBinding.inflate(inflater, container, false)
        val view = binding.root
        ToolBarHelper.setToolBarTitle(this, "Έναρξη")

        //Navigate to PlayGame fragment and passing the username IF GIVEN
        val btnStart = view.findViewById<Button>(R.id.btnStartGame)
        val etEnterUserName = view.findViewById<AppCompatEditText>(R.id.etUserName)
        btnStart.setOnClickListener {
            val username = etEnterUserName.text.toString()
            if (username.isEmpty()) {
                Toast.makeText(view.context, "Παρακαλώ εισάγετε ένα όνομα χρήστη", Toast.LENGTH_SHORT).show()
            } else {
                val action = GameIntroFragmentDirections.actionGameIntroFragmentToPlayGameFragment(username)
                view.findNavController().navigate(action)
            }
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}