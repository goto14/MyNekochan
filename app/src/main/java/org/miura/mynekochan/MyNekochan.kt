package org.miura.mynekochan

import android.app.Application
import com.google.android.gms.tasks.Task
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import org.miura.mynekochan.data.model.ImageData

class MyNekochan: Application() {
    companion object {
        lateinit var instance:MyNekochan
    }
    private lateinit var _realm:Realm
    val realm:Realm get() {
        if(_realm == null || _realm.isClosed()) {
            openRealm()
        }
        return _realm
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        openRealm()
    }

    fun openRealm() {
        val config = RealmConfiguration.Builder(schema = setOf(ImageData::class)).build()
        _realm = Realm.open(config)
    }
    fun closeRealm() {
        _realm?.let { it.close() }
    }
}