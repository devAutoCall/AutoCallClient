package com.app.autocaller.viewmodel

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.app.autocaller.MainActivity
import com.app.autocaller.api.Api
import com.app.autocaller.define.StaticObj
import com.example.dtpapp.models.TokenInfoModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class MainViewModel constructor(
    var mActivity: MainActivity
) {

    var token: String = ""
    var phoneNumber: String = ""
    private val disposeBeg = CompositeDisposable()

    fun intervalCmd() {
        disposeBeg.add(
            Observable
                .interval(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe {
                })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkSelfPermission() {
        val temp = getPermissionStr()
        if (!TextUtils.isEmpty(temp)) {
            // 권한 요청
            ActivityCompat.requestPermissions(
                mActivity,
                temp!!.trim { it <= ' ' }.split(" ").toTypedArray(),
                1
            )
        } else {

            setPhoneNumber()

            StaticObj.MESSAGE = mActivity.mCh.text.toString()
        }
    }

    fun setRetrofitInit() {
        val retrofit = Retrofit.Builder()
            .baseUrl(StaticObj.SV_DOMAIN)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        StaticObj.Api = retrofit.create(Api::class.java)
    }

    private fun insertTokenInfo() {

        if (token == "") {
            Toast.makeText(mActivity, "토큰 값이 없습니다 앱을 완전히 종료하고 다시 시작해주세요", Toast.LENGTH_LONG)
        } else if (phoneNumber == "") {
            Toast.makeText(mActivity, "전화번호 값이 없습니다 앱을 완전히 종료하고 다시 시작해주세요", Toast.LENGTH_LONG)
        }

        var model = TokenInfoModel(phoneNumber, token)
        StaticObj.Api.insertTokenInfo(model).enqueue(object : retrofit2.Callback<Int> {
            override fun onResponse(
                call: Call<Int>,
                response: Response<Int>
            ) {
                Log.d("retrofit", response.body().toString())

            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                Log.d("retrofit", t.toString())
            }
        })
    }

    fun setPhoneNumber() {
        val tm = mActivity.getSystemService(AppCompatActivity.TELEPHONY_SERVICE) as TelephonyManager

        var PhoneNum = ""
        if (ActivityCompat.checkSelfPermission(
                mActivity,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mActivity,
                Manifest.permission.READ_PHONE_NUMBERS
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mActivity,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        PhoneNum = tm.getLine1Number();
        if (PhoneNum.startsWith("+82")) {
            PhoneNum = PhoneNum.replace("+82", "0");
        }


        if (PhoneNum.startsWith("+15")) {
            PhoneNum = PhoneNum.replace("+15", "0");
        }

        PhoneNum = PhoneNum.replace("-", "");

        phoneNumber = PhoneNum

        mActivity.mCh.setText(PhoneNum);


        //mActivity.subscribe(mActivity.mCh.text.toString());
        checkPermisson2()
    }

    private fun checkPermisson2() {
        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                System.out.println("callPhone onPermissionGranted")
                getToken()

            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {

            }
        }

        TedPermission.with(mActivity)
            .setPermissionListener(permissionListener)
            .setDeniedMessage("[설정] 에서 권한을 열어줘야 전화 연결이 가능합니다.")
            .setPermissions(
                Manifest.permission.CALL_PHONE,
                Manifest.permission.INTERNET,
                Manifest.permission.SYSTEM_ALERT_WINDOW
            )
            .check()

    }


    private fun getPermissionStr(): String? {
        var temp = ""
        if (ContextCompat.checkSelfPermission(
                mActivity,
                Manifest.permission.INTERNET
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            temp += Manifest.permission.INTERNET + " "
        }
        if (ContextCompat.checkSelfPermission(
                mActivity,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            temp += Manifest.permission.READ_SMS + " "
        }
        if (ContextCompat.checkSelfPermission(
                mActivity,
                Manifest.permission.READ_PHONE_NUMBERS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            temp += Manifest.permission.READ_PHONE_NUMBERS + " "
        }
        if (ContextCompat.checkSelfPermission(
                mActivity,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            temp += Manifest.permission.READ_PHONE_STATE + " "
        }
        if (ContextCompat.checkSelfPermission(
                mActivity,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            temp += Manifest.permission.CALL_PHONE + " "
        }

        return temp
    }

    fun getToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            token = task.result.toString()
            insertTokenInfo()
            Log.d("token", token!!)
            //Toast.makeText(mActivity, token!!, Toast.LENGTH_SHORT).show()
        })
    }

}