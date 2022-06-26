package org.miura.mynekochan.data.model

import io.realm.kotlin.types.RealmObject

open class ImageData :RealmObject {
    var image:String = ""
    var name :String = ""
    var flag:Boolean  = false
    var num:Int = 0
}