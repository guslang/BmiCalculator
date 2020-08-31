package com.guslang.bmicalculator

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.akj.lotto.LottoNumberMaker
import com.google.android.gms.ads.*
import kotlinx.android.synthetic.main.activity_result.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.startActivity
import java.math.BigDecimal
import kotlin.math.pow

class ResultActivity : AppCompatActivity() {
    lateinit var mAdView : AdView
    private lateinit var mInterstitialAd: InterstitialAd
    val lottoImageStartId = R.drawable.ball_01

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        //firebase-admob 초기화
        MobileAds.initialize(this,getString(R.string.admob_app_id))
//        MobileAds.initialize(this)

        //firebase-admob 배너
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        //firebase-admob 전면 광고
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = getString(R.string.Interstitial_ad_unit_id)
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        //end

        // 전달받은 키와 몸무게
        val height = intent.getStringExtra("height").toString().toDouble()
        val weight = intent.getStringExtra("weight").toString().toDouble()

        // BMI 계산
        val bmi = weight / (height / 100.0).pow(2.0)

        // 결과 표시
        when {
            bmi >= 35 -> {
                resultTextView.text = "OBESITY Class 3"  //3단계 비만 고도 비만
                commentTextView.text = "You are heavily overweight. Try to exercise more and diet"
                //비만 3단계입니다. 운동과 식단조절을 병행하세요.
            }
            bmi >= 30 -> {
                resultTextView.text = "OBESITY Class 2"  //2단계 비만
                commentTextView.text = "You are heavily overweight. Try to exercise more and diet"
                //비만 2단계입니다. 운동과 식단조절을 병행하세요.
            }
            bmi >= 25 -> {
                resultTextView.text = "OBESITY Class 1"  //1단계 비만
                commentTextView.text ="You are heavily overweight. Try to exercise more and diet"
                //비만 3단계입니다. 운동과 식단조절을 병행하세요.
            }
            bmi >= 23 -> {
                resultTextView.text = "OVERWEIGHT" //과체중
                commentTextView.text = "You have a higher than normal body weight. Try to exercise more."
                //과체중입니다. 하루 1만보 걸어보는건 어떨까요?
            }
            bmi >= 18.5 -> {
                resultTextView.text = "NORMAL RANGE" //정상
                commentTextView.text = "You have a normal body weight. Great job!"
                // 정상 체중입니다. Good Job!
            }
            else -> {
                resultTextView.text = "UNDERWEIGHT" //저체중
                commentTextView.text = "You have a lower than normal body weight. You can eat a bit more."
                //저체중입니다. 삼시세끼 잘 챙겨 드세요.
            }
        }

        // 이미지 표시
        when {
            bmi >=23 ->
                imageView.setImageResource(
                    R.drawable.ic_baseline_sentiment_very_dissatisfied_24
                )
            bmi >= 18.5 ->
                imageView.setImageResource(
                    R.drawable.ic_baseline_tag_faces_24
                )
            else ->
                imageView.setImageResource(
                    R.drawable.ic_baseline_sentiment_dissatisfied_24)
        }

        longToast("$bmi")
        txtResult.text = bmi.toString().toBigDecimal().setScale(2,BigDecimal.ROUND_HALF_UP).toString()

        btnRecalc.setOnClickListener {
            startActivity<MainActivity>()
        }

        // 로또 번호 랜덤 추출 6개
        val result:List<Int> = ArrayList(LottoNumberMaker.getShuffleLottoNumbers())
        updateLottoBallImage(result.sortedBy { it })

        // 로또 번호 클릭 시 전면 광고
        imageView01.setOnClickListener {
            showInterstitialAd()
        }
        imageView02.setOnClickListener {
            showInterstitialAd()
        }
        imageView03.setOnClickListener {
            showInterstitialAd()
        }
        imageView04.setOnClickListener {
            showInterstitialAd()
        }
        imageView05.setOnClickListener {
            showInterstitialAd()
        }
        imageView06.setOnClickListener {
            showInterstitialAd()
        }


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

    /**
     * 전면 광고 게재하기
     */
    fun showInterstitialAd() {
        // admob 전면 광고
        if (mInterstitialAd.isLoaded) {
            mInterstitialAd.show()
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.")
        }
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