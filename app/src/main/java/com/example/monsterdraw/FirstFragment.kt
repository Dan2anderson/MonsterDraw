package com.example.monsterdraw

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.monsterdraw.databinding.FragmentFirstBinding
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.fab?.setOnClickListener { view1 ->
            _setBonesState()        }
        _binding?.moveFab?.setOnClickListener { view1 ->
            _setMoveState()        }
        _binding?.clearFab?.setOnClickListener { view1 ->
            _clear()        }

    }

    fun _clear() {
        _binding?.drawingView?.clear()
    }
    fun _setMoveState() {
        _binding?.drawingView?.setMoveState()
    }
    fun _setBonesState() {
        _binding?.drawingView?.setBonesState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}