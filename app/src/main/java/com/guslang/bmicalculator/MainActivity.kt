package com.guslang.bmicalculator

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    lateinit var mAdView : AdView
    private lateinit var mInterstitialAd: InterstitialAd
    val lottoImageStartId = R.drawable.ball_01

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 키/몸무게 포멧
        val format = DecimalFormat("###.##")
        val formatted: String = format.format(0.0)
        heightEditText.setText(formatted)
        weightEditText.setText(formatted)

        //admob 초기화
        //MobileAds.initialize(this) {}

        //firebase-admob 초기화
        MobileAds.initialize(this, getString(R.string.admob_app_id))
        //firebase-admob 배너
        try {
            mAdView = findViewById(R.id.adView)
            val adRequest = AdRequest.Builder().build()
            mAdView.loadAd(adRequest)
        } catch (e:Exception){
            Log.d("admob", "The adMob banner wasn't loaded yet. ${e.toString()}")
        }

        //firebase-admob 전면 광고
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = getString(R.string.Interstitial_ad_unit_id)
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        //end

        // 이전에 입력한 값을 읽어오기
        loadData()
//        field = findViewById(R.id.heightEditText) as EditText

        // BMI 계산
        resultButton.setOnClickListener {
            loadResult()
        }

        // 키 입력 후 몸무게 필드로 포커스 이동
        heightEditText.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                weightEditText.requestFocus()
//                weightEditText.selectAll()
                true
            } else
                false
        }
        // 로또 번호 랜덤 추출 6개
        val result:List<Int> = ArrayList(LottoNumberMaker.getShuffleLottoNumbers())
        updateLottoBallImage(result.sortedBy { it })

        mInterstitialAd.adListener = object: AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                // Code to be executed when an ad request fails.
                Log.d("TAG", "The interstitial wasn't loaded yet. $errorCode")
            }

            override fun onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            override fun onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
                mInterstitialAd.loadAd(AdRequest.Builder().build())
            }
        }
    }

    //뒤로가기 연속 클릭 대기 시간
    var mBackWait:Long = 0
    override fun onBackPressed() {
        // 뒤로가기 버튼 클릭
        if(System.currentTimeMillis() - mBackWait >=2000 ) {
            mBackWait = System.currentTimeMillis()
            toast("뒤로가기 버튼을 한번 더 누르면 종료됩니다.")
//            longToast("뒤로가기 버튼을 한번 더 누르면 종료됩니다.")
        } else {
            // admob 전면 광고
//            if (mInterstitialAd.isLoaded) {
//                mInterstitialAd.show()
//            } else {
//                Log.d("TAG", "The interstitial wasn't loaded yet.")
//            }
            finish() //액티비티 종료
        }
    }

    private fun loadResult() {
        var heightval:String? = heightEditText.text.toString()
        heightval = heightval ?:"0"
        if (heightval.isEmpty())  heightval = "0"
        if (heightval.toDouble() <= 0L)
            longToast("Enter your height")
        var weightval:String? = weightEditText.text.toString()
        weightval = weightval ?:"0"
        if (weightval.isEmpty()) weightval = "0"
        if (weightval.toDouble() <= 0L)
            longToast("Enter your height")

        if (heightval.toDouble() > 0 && weightval.toDouble() > 0 ){
            startActivity<ResultActivity>(
                "weight" to weightval,//weightEditText.text.toString(),
                "height" to heightval //heightEditText.text.toString()
            )
            saveData(heightval, weightval)
        }
    }

    private fun loadData () {
        try {
            val pref: SharedPreferences = getSharedPreferences("hwData", MODE_PRIVATE)
            val height = pref.getString("KEY_HEIGHT", "0")
            val weight = pref.getString("KEY_WEIGHT", "0")

            if (height != "0" && weight != "0") {
                heightEditText.setText(height)
                weightEditText.setText(weight)
            }
        } catch (e: ClassCastException){
            Log.d("loadData", "ClassCastException : $e")
        }
    }

    private fun saveData(height: String, weight: String) {
        val pref: SharedPreferences = getSharedPreferences("hwData", MODE_PRIVATE)
        val editor = pref.edit()

        editor.putString("KEY_HEIGHT", height)
              .putString("KEY_WEIGHT", weight)
              .apply()
    }

    /**
     * 결과에 따라 로또 공 이미지를 업데이트한다.
     */
    fun updateLottoBallImage(result: List<Int>) {
        // 결과의 사이즈가 6개 미만인경우 에러가 발생할 수 있으므로 바로 리턴한다.
        if (result.size < 6) return

        // ball_01 이미지 부터 순서대로 이미지 아이디가 있기 때문에
        // ball_01 아이디에 결과값 -1 을 하면 목표하는 이미지가 된다
        // ex) result[0] 이 2번 공인 경우 ball_01 에서 하나뒤에 이미지가 된다.
        imageView01.setImageResource(lottoImageStartId + (result[0] - 1))
        imageView02.setImageResource(lottoImageStartId + (result[1] - 1))
        imageView03.setImageResource(lottoImageStartId + (result[2] - 1))
        imageView04.setImageResource(lottoImageStartId + (result[3] - 1))
        imageView05.setImageResource(lottoImageStartId + (result[4] - 1))
        imageView06.setImageResource(lottoImageStartId + (result[5] - 1))
    }
}

class LoadAdError {

}
