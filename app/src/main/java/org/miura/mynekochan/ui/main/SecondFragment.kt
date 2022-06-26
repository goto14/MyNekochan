package org.miura.mynekochan.ui.main

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import org.miura.mynekochan.R
import org.miura.mynekochan.data.ImageRepository
import org.miura.mynekochan.data.model.ImageData
import org.miura.mynekochan.databinding.FragmentSecondBinding


class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private lateinit var viewModel: MainViewModel

    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.loadImageData()
        val db = mutableListOf<ImageData>()
        ImageRepository.instance.getImagesFromDB().forEach {
            db.add(it)
        }
        val data = db.map { mapOf("img" to BitmapFactory.decodeByteArray(it.image!!.toByteArray(), 0,it.image?.length?:0)) }
        val adapter = SimpleAdapter(requireContext(),data, R.layout.list_item, arrayOf("img"), intArrayOf(R.id.list_item_image))
        binding.listView.adapter = adapter
    }

    private fun prev() {
        findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}