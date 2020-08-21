package com.guslang.bmicalculator

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {

    lateinit var mAdView : AdView
    private lateinit var mInterstitialAd: InterstitialAd
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //admob 초기화
        //MobileAds.initialize(this) {}

        //firebase-admob 초기화
        MobileAds.initialize(this,"@string/admob_app_id")
        //firebase-admob 배너
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        //firebase-admob 전면 광고
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "@string/Interstitial_ad_unit_id"
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        //end

        // 이전에 입력한 값을 읽어오기
        loadData()

        // 키 editText에 포커스 주기
        heightEditText.requestFocus()

        // BMI 계산
        resultButton.setOnClickListener {

            // admob 전면 광고
            if (mInterstitialAd.isLoaded) {
                mInterstitialAd.show()
            } else {
                Log.d("TAG", "The interstitial wasn't loaded yet.")
            }

            var heightval = heightEditText.text.toString()
            if (heightval.isEmpty())  heightval = "0"
            if (heightval.toInt() <= 0)
                longToast("Enter your height")
            var weightval = weightEditText.text.toString()
            if (weightval.isEmpty()) weightval = "0"
            if (weightval.toInt() <= 0)
                longToast("Enter your height")

            if (heightval.toInt() > 0 && weightval.toInt() > 0 ){
                startActivity<ResultActivity>(
                    "weight" to weightval ,//weightEditText.text.toString(),
                    "height" to heightval //heightEditText.text.toString()
                )
                saveData(heightval.toInt(), weightval.toInt())
            }
        }

    }

    private fun loadData () {
       val pref: SharedPreferences = getSharedPreferences("dico", MODE_PRIVATE)
//        val pref = getPr.getFloat("KEY_HEIGHT", 0f)
//        val weight = pref.getFloat("KEY_WEIGHT", 0f)
        val height = pref.getInt("KEY_HEIGHT", 0)
        val weight = pref.getInt("KEY_WEIGHT", 0)

        if (height != 0 && weight != 0) {
            heightEditText.setText(height.toString())
            weightEditText.setText(weight.toString())
        }
    }
//
    private fun saveData(height: Int, weight: Int) {
        val pref: SharedPreferences = getSharedPreferences("dico", MODE_PRIVATE)
//        val pref = getPreferences(0)
        val editor = pref.edit()

        editor.putInt("KEY_HEIGHT",height)
              .putInt("KEY_WEIGHT",weight)
              .apply()
    }
//
////    override fun writeToParcel(parcel: Parcel, flags: Float) {
////    }
//
//    override fun writeToParcel(p0: Parcel?, p1: Int) {
//        TODO("Not yet implemented")
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    companion object CREATOR : Parcelable.Creator<bmicaculator> {
//        override fun createFromParcel(parcel: Parcel): bmicaculator {
//            return bmicaculator(parcel)
//        }
//
//        override fun newArray(size: Int): Array<bmicaculator?> {
//            return arrayOfNulls(size)
//        }
//    }

}

class LoadAdError {

}
