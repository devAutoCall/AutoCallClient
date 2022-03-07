package com.app.autocaller

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.app.autocaller.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    lateinit var mCh: EditText
    private lateinit var mHandler: Handler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mHandler = Handler()
        viewModel = MainViewModel(this)
        mCh = findViewById(R.id.textView2)
        viewModel.setRetrofitInit()
        viewModel.checkSelfPermission()
    }

    override fun onResume() {
        Log.d("onResume","MainActivity onResume")
        super.onResume()
    }

    //권한에 대한 응답이 있을때 작동하는 함수
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //권한을 허용 했을 경우
        if (requestCode == 1) {
            val length = permissions.size
            var isAllGrated = true
            for (i in 0 until length) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    isAllGrated = false
                }
            }
            if (isAllGrated) {
                viewModel.setPhoneNumber()
            }
        }
    }

}