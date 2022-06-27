package org.miura.mynekochan.data

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.miura.mynekochan.MyNekochan
import org.miura.mynekochan.R
import org.miura.mynekochan.data.model.ImageData
import java.io.ByteArrayOutputStream
import java.util.*

class ImageRepository {
    companion object {
        private var mInstance: ImageRepository? = null
        val instance
            get() = run {
                if (mInstance == null) {
                    mInstance = ImageRepository()
                }
                mInstance!!
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
            src = Bitmap.createScaledBitmap(src, 800, 800, false)
            ImageData().also { img ->
                img.image = src
            }
        }
        val db = getWritableDatabase()
        imageList.forEach {
            val value = ContentValues()
            val str = ByteArrayOutputStream()
            it.image?.compress(Bitmap.CompressFormat.PNG, 100, str)
            value.put("image", str.toByteArray())
            value.put("id", UUID.randomUUID().toString())
            value.put("flag", if (it.flag) 1 else 0)
            db.insertOrThrow("ImageData", null, value)
        }
    }

    fun getImagesFromDB(): List<ImageData> {
        val ret = mutableListOf<ImageData>()
        val sql =
            "select id, image, flag, deleted from ImageData"
        val cursor = getReadableDatabase().rawQuery(sql, null)
        if (cursor.count > 0) {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val img = ImageData()
                img.image = cursor.getBlob(1).let { BitmapFactory.decodeByteArray(it, 0, it.size) }
                img.id = cursor.getString(0)
                img.flag = cursor.getInt(2) == 1
                img.deleted = cursor.getInt(3) == 1
                ret.add(img)
                cursor.moveToNext()
            }
        }
        cursor.close()
        return ret
    }

    fun hasDataInDb(): Boolean {
        return getImagesFromDB().isNotEmpty()
    }

    private fun getWritableDatabase(): SQLiteDatabase {
        val helper = Helper(MyNekochan.instance.applicationContext, "neko", null, 1)
        return helper.writableDatabase
    }

    private fun getReadableDatabase(): SQLiteDatabase {
        val helper = Helper(MyNekochan.instance.applicationContext, "neko", null, 1)
        return helper.readableDatabase
    }

}