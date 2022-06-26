package org.miura.mynekochan.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import org.miura.mynekochan.MyNekochan
import org.miura.mynekochan.R
import org.miura.mynekochan.data.model.ImageData
import java.io.ByteArrayOutputStream

class ImageRepository {
    companion object {
        private var _instance: ImageRepository? = null
        val instance
            get() = run {
                if (_instance == null) {
                    _instance = ImageRepository()
                }
                _instance!!
            }
    }

    fun initImageData() {
        val resList = listOf(
            R.drawable.img_01,
            R.drawable.img_02,
            R.drawable.img_03,
            R.drawable.img_04,
            R.drawable.img_05,
            R.drawable.img_06,
            R.drawable.img_07,
            R.drawable.img_08,
            R.drawable.img_09,
            R.drawable.img_10,
            R.drawable.img_11,
            R.drawable.img_12,
            R.drawable.img_13,
            R.drawable.img_14,
            R.drawable.img_15,
            R.drawable.img_16
        )
        val imageList = resList.map {
            var src = BitmapFactory.decodeResource(MyNekochan.instance.resources, it)
            if (src.height > src.width) {
                val diff = (src.height - src.width) / 2
                src = Bitmap.createBitmap(src, 0, diff, src.width, src.width)
            } else if (src.width > src.height) {
                val diff = (src.width - src.height) / 2
                src = Bitmap.createBitmap(src, diff, 0, src.height, src.height)
            }
            src = Bitmap.createScaledBitmap(src, 200, 200, false)
            ImageData().also {
                val str = ByteArrayOutputStream()
                src.compress(Bitmap.CompressFormat.PNG, 100, str)
                it.image = "3"
                it.name = "testImage:" + it.image!!.length
                it.num = 2
            }
        }

        val realm = getRealm()
        realm.writeBlocking {
            val rec = query<ImageData>().find()
            delete(rec)
        }
        imageList.first().let { src ->
            realm.writeBlocking {
                copyToRealm(ImageData().apply {
                    image = src.image
                    name = src.name
                    num = src.num
                })
            }
        }
        realm.close()
    }

    fun getImagesFromDB(): List<ImageData> {
        val realm = getRealm()
        return realm.query<ImageData>().find()
    }

    fun hasDataInDb(): Boolean {
        val realm = getRealm()
        return realm.query<ImageData>().find().isNotEmpty()
    }

    private fun getRealm(): Realm {
        return MyNekochan.instance.realm
    }


}