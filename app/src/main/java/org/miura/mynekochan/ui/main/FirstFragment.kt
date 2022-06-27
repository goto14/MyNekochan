package org.miura.mynekochan.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.miura.mynekochan.R
import org.miura.mynekochan.R.id.action_FirstFragment_to_SecondFragment
import org.miura.mynekochan.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(action_FirstFragment_to_SecondFragment)
        }
        binding.img01.setOnClickListener { _ ->
            viewModel.img1.value?.flag = true
            viewModel.img2.value?.deleted = true
            viewModel.img2.value?.let {
                viewModel.removedQueue.push(it)
            }
            nextQueue()
        }
        binding.img02.setOnClickListener { _ ->
            viewModel.img2.value?.flag = true
            viewModel.img1.value?.deleted = true
            viewModel.img1.value?.let {
                viewModel.removedQueue.push(it)
            }
            nextQueue()
        }
        binding.fab.setOnClickListener {
            if(viewModel.isAuto) {
                viewModel.isAuto = false
                binding.fab.setImageResource(R.drawable.ic_baseline_fast_forward_24)
            } else {
                GlobalScope.launch{
                    viewModel.isAuto = true
                    viewModel.autoDiff()
                }
                binding.fab.setImageResource(R.drawable.ic_baseline_pause_24)
            }
        }
        if (viewModel.img1.value == null) {
            viewModel.initQueue()
            nextQueue()
        } else {
            viewModel.img1.value?.image?.let { binding.img01.setImageBitmap(it) }
            viewModel.img2.value?.image?.let { binding.img02.setImageBitmap(it) }
            binding.turnLabel.text = getString(R.string.turn_label).format(viewModel.turn.toString())
        }
        viewModel.img1.observe(viewLifecycleOwner) { img ->
            img?.image?.let {
                binding.img01.setImageBitmap(it)
                binding.turnLabel.text = getString(R.string.turn_label).format(viewModel.turn.toString())
            }
        }
        viewModel.img2.observe(viewLifecycleOwner) { img ->
            img?.image?.let {
                binding.img02.setImageBitmap(it)
                binding.turnLabel.text = getString(R.string.turn_label).format(viewModel.turn.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun nextQueue() {
        viewModel.nextQueue()
    }

}