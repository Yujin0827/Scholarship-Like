package com.cookandroid.scholarshiplike

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import java.lang.Exception

class AppInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_info)

        val versionName = findViewById<TextView>(R.id.info_versionName)
        val versionCode = findViewById<TextView>(R.id.info_versionCode)

        versionName.text = getVersionName(this)
        versionCode.text = getVersionCode(this)
    }

    // build.gradle에서 versionName 호출
    fun getVersionName(context: Context): String{
        lateinit var versionName: String
        try{
            val pm = context.packageManager.getPackageInfo(context.packageName, 0)
            versionName = pm.versionName
        } catch (e : Exception){}

        return versionName
    }

    // build.gradle에서 versionCode 호출
    fun getVersionCode(context: Context): String{
        lateinit var versionCode: String
        try{
            val pm = context.packageManager.getPackageInfo(context.packageName, 0)
            versionCode = pm.versionCode.toString()
        } catch (e : Exception){}

        return versionCode
    }

}
