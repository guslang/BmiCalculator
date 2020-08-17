package com.guslang.bmicalculator

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {

    lateinit var mAdView : AdView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Google Admob
//        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713")
        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.ad_view)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

//        mAdView.adListener = object: AdListener() {
//            override fun onAdLoaded() {
//                // Code to be executed when an ad finishes loading.
//            }
//
////            override fun onAdFailedToLoad(adError : LoadAdError) {
////                // Code to be executed when an ad request fails.
////            }
//
//            override fun onAdOpened() {
//                // Code to be executed when an ad opens an overlay that
//                // covers the screen.
//            }
//
//            override fun onAdClicked() {
//                // Code to be executed when the user clicks on an ad.
//            }
//
//            override fun onAdLeftApplication() {
//                // Code to be executed when the user has left the app.
//            }
//
//            override fun onAdClosed() {
//                // Code to be executed when the user is about to return
//                // to the app after tapping on an ad.
//            }
//        }
//        val adSize = AdSize(300, 50)

        // 이전에 입력한 값을 읽어오기
        loadData()

        // 키 editText에 포커스 주기
        heightEditText.requestFocus()

        // BMI 계산
        resultButton.setOnClickListener {
//            val intent = Intent(this, ResultActivity::class.java)
//            intent.putExtra("weight",weightEditText.text.toString())
//            intent.putExtra("height",heightEditText.text.toString())
//            startActivity(intent)

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
