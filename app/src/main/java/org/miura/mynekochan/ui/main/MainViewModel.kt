package org.miura.mynekochan.ui.main

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.miura.mynekochan.MyNekochan
import org.miura.mynekochan.R
import org.miura.mynekochan.data.ImageRepository
import org.miura.mynekochan.data.model.ImageData

class MainViewModel : ViewModel() {
    lateinit var images:List<ImageData>
    init {
        initData()
    }
    fun initData() {
//        if(!ImageRepository.instance.hasDataInDb()) {
//            ImageRepository.instance.initImageData()
//        }
            ImageRepository.instance.initImageData()
    }
    fun loadImageData() {
        images = ImageRepository.instance.getImagesFromDB()
    }
}