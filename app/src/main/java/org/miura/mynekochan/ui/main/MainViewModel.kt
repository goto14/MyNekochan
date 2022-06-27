package org.miura.mynekochan.ui.main

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.miura.mynekochan.data.ImageRepository
import org.miura.mynekochan.data.model.ImageData
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random
import kotlin.random.nextInt

class MainViewModel : ViewModel() {
    lateinit var images: List<ImageData>
    private val queue = LinkedList<ImageData>()
    val removedQueue = LinkedList<ImageData>()
    val img1 = MutableLiveData<ImageData>()
    val img2 = MutableLiveData<ImageData>()
    var target:Bitmap? = null
    var isAuto = false
    var turn = 1

    init {
        initData()
    }

    fun initQueue() {
        loadImageData()
        queue.addAll(images.filter { (!it.deleted) && (!it.flag) })
        removedQueue.addAll(images.filter { it.deleted })
        if(queue.size <= 1) {
            nextTurn()
        }
    }

    fun nextQueue() {
        if(queue.size <= 1) {
            nextTurn()
        }
        img1.postValue(queue.pop())
        img2.postValue(queue.pop())
    }
    fun nextTurn() {
        val newImg = images.filter { (!it.deleted) }.toMutableList()
        newImg.forEach { it.flag = true }
        while (newImg.size < 16) {
            val parents = newImg.filter { it.flag }.take(2)
            val nb = newBitmap(parents[0].image!!, parents[1].image!!)
            parents.forEach { it.flag =  false }
            newImg.addAll(nb.map {bitmap ->  ImageData().also { it.image = bitmap } })
        }
        newImg.forEach {
            it.flag = false
            it.deleted = false
        }
        turn++
        ImageRepository.instance.saveTurn(turn)
        ImageRepository.instance.saveImage(newImg)
        queue.clear()
        removedQueue.clear()
        initQueue()
    }

    private fun newBitmap(src1:Bitmap, src2:Bitmap):List<Bitmap> {
        val ret1 = src1.copy(Bitmap.Config.ARGB_8888, true)
        val ret2 = src2.copy(Bitmap.Config.ARGB_8888, true)
        val mutableSrc2 = src2.copy(Bitmap.Config.ARGB_8888,true)
        val r = Random(Date().time)
        val h = src1.height
        val w = mutableSrc2.width
        for(i in (0 until h)) {
            for(j in (0 until w)) {
                var r1 = src1.getPixel(i,j)
                var r2 = mutableSrc2.getPixel(i,j)
                if(r.nextInt(0..1) == 0) {
                    // swap処理
                    val tmp = r1
                    r1 = r2
                    r2 = tmp
                }
                val dice = r.nextInt(0 until 256)
                if(dice == 0) {
//                    r2 = r2 xor  0xFFFFFFFF.toInt()
                } else if(dice in (8 until 16)) {
                    val flg = r.nextInt(0..1)
                    val targetColor = r.nextInt(0..2)
                    val list = mutableListOf(Color.red(r2), Color.green(r2), Color.blue(r2))
                    if(flg == 0) {
                        list[targetColor] = max(0, list[targetColor]-16)
                    } else {
                        list[targetColor] = min(255, list[targetColor]+16)
                    }
                    r2 = Color.rgb(list[0], list[1], list[2])
                    for(k in i..Math.min(ret1.height-1, i+2)) {
                        for(l in (j..Math.min(ret1.width-1, j+2))) {
                            mutableSrc2.setPixel(k,l,r2)
                        }
                    }
                }
                ret1.setPixel(i,j,r1)
                ret2.setPixel(i,j,r2)
            }
        }
        return listOf(ret1,ret2)
    }

    private fun initData() {
        if (!ImageRepository.instance.hasDataInDb()) {
            ImageRepository.instance.initImageData()
        }
    }

    fun loadImageData() {

        val tmp = ImageRepository.instance.getImagesFromDB().toMutableList()
        val ret = mutableListOf<ImageData>()
        val r = Random(Date().time)
        while (tmp.isNotEmpty()) {
            val idx = r.nextInt(0..tmp.lastIndex)
            ret.add(tmp[idx])
            tmp.removeAt(idx)
        }
        images = ret
        target = ImageRepository.instance.getTargetData()
        turn = ImageRepository.instance.getTurn()
    }

    fun autoDiff() {
        for(i in 0..10000) {
            if(!isAuto) {
                break
            }
            if(img1.value == null) {
                nextTurn()
            }
            val s1 = getScore(img1.value!!.image!!)
            val s2 = getScore(img2.value!!.image!!)
            if(s1 > s2) {
                img1.value!!.flag = true
                img2.value!!.deleted = true
                removedQueue.push(img2.value!!)
            } else {
                img2.value!!.flag = true
                img1.value!!.deleted = true
                removedQueue.push(img1.value!!)
            }
            android.os.Handler(Looper.getMainLooper()).post {
                nextQueue()
            }
        }
    }
    private fun getScore(diff:Bitmap):Long {
        var score = 256L*3*diff.height*diff.width
        for(i in (0 until diff.height)) {
            for(j in (0 until diff.width)) {
                val c1 = diff.getColor(i,j)
                val c2 = target!!.getColor(i,j)
                score -= abs(c1.red()-c2.red()).toInt() + abs(c1.green()-c2.green()).toInt() + abs(c1.blue()-c2.blue()).toInt()
            }
        }
        return score
    }
}