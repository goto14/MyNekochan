package org.miura.mynekochan.data

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.miura.mynekochan.MyNekochan
import org.miura.mynekochan.R
import org.miura.mynekochan.data.model.ImageData
import org.miura.mynekochan.toByteArray
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
            val src = clipImage(BitmapFactory.decodeResource(MyNekochan.instance.resources, it))
            ImageData().also { img ->
                img.image = src
            }
        }
        val target = clipImage(
            BitmapFactory.decodeResource(
                MyNekochan.instance.resources,
                R.drawable.target
            )
        )
        val db = getWritableDatabase()
        imageList.forEach {
            val value = ContentValues()
            it.image?.toByteArray()?.let { ba ->
                value.put("image", ba)
            }
            value.put("id", UUID.randomUUID().toString())
            value.put("flag", if (it.flag) 1 else 0)
            db.insertOrThrow("ImageData", null, value)
        }
        val targetValues = ContentValues()
        targetValues.put("image", target.toByteArray())
        db.insertOrThrow("Target", null, targetValues)

        db.execSQL("delete from Preference")
        db.execSQL("insert into Preference(turn) values (1)")
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

    fun deleteImage(id: String) {
        getWritableDatabase().execSQL("delete from ImageData where id = '$id'")
    }

    private fun getWritableDatabase(): SQLiteDatabase {
        return MyNekochan.instance.wDb
    }

    private fun getReadableDatabase(): SQLiteDatabase {
        return MyNekochan.instance.rDb
    }

    fun saveImage(data: List<ImageData>) {
        val db = getWritableDatabase()
        db.execSQL("delete from ImageData")
        data.forEach {
            val values = ContentValues()
            values.put("id", it.id.ifEmpty { UUID.randomUUID().toString() })
            it.image?.let { img ->
                values.put("image", img.toByteArray())
            }
            values.put("flag", if (it.flag) 1 else 0)
            values.put("deleted", if (it.deleted) 1 else 0)
            db.insertOrThrow("ImageData", null, values)
        }
    }

    private fun clipImage(src: Bitmap): Bitmap {
        var ret = src
        if (src.height > src.width) {
            val y = (src.height - src.width) / 2
            ret = Bitmap.createBitmap(src, 0, y, src.width, src.width)
        } else if (src.width > src.height) {
            val x = (src.width - src.height) / 2
            ret = Bitmap.createBitmap(src, x, 0, src.height, src.height)
        }
        return Bitmap.createScaledBitmap(ret, 64, 64, false)
    }

    fun getTargetData():Bitmap {
        val cur = getReadableDatabase().rawQuery("select image from Target", null)
        cur.moveToFirst()
        val ba = cur.getBlob(0)
        val bmp = BitmapFactory.decodeByteArray(ba, 0, ba.size)
        cur.close()
        return bmp
    }

    fun getTurn():Int {
        val cur = getReadableDatabase().rawQuery("select turn from Preference", null)
        cur.moveToFirst()
        val cnt = cur.getInt(0)
        cur.close()
        return cnt
    }

    fun saveTurn(turn:Int) {
        val db = getWritableDatabase()
        db.execSQL("delete from Preference")
        val values = ContentValues()
        values.put("turn", turn)
        db.insertOrThrow("Preference", null, values)
    }
}