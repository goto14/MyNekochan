package org.miura.mynekochan.ui.main

import androidx.lifecycle.ViewModel
import org.miura.mynekochan.data.ImageRepository
import org.miura.mynekochan.data.model.ImageData

class MainViewModel : ViewModel() {
    lateinit var images: List<ImageData>

    init {
        initData()
    }

    private fun initData() {
        if (!ImageRepository.instance.hasDataInDb()) {
            ImageRepository.instance.initImageData()
        }
    }

    fun loadImageData() {
        images = ImageRepository.instance.getImagesFromDB()
    }
}