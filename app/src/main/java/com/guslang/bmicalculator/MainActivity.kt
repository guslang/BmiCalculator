package com.guslang.bmicalculator

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    lateinit var mAdView : AdView
    private val lottoImageStartId = R.drawable.ball_01
    lateinit var mHeightVal : String
    lateinit var mWeightVal : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val initHeightTranslationY = tvHeight.translationY
//        val initWeightTranslationY = tvWeight.translationY
//        val initHeightTranslationX = tvHeight.translationX
        sbHeight.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mHeightVal = progress.toString()
                tvHeight.text = "Height : " + mHeightVal + "cm"
                //animation
//                val translationDistance = (initHeightTranslationX
//                        + progress * resources.getDimension(R.dimen.text_anim_step) *  -1)
//                tvHeight.animate().translationX(translationDistance)
//
//                if(!fromUser) {
//                    tvHeight.animate().setDuration(500).rotationBy(360f).translationX(initHeightTranslationX)
//                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        //
        sbWeight.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mWeightVal = progress.toString()
                tvWeight.text = "Weight : " + mWeightVal + "kg"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        // 키 1씩 조정
        ivHLeftArrow.setOnClickListener {
            var tmpVal:Int = sbHeight.progress - 1
            if (tmpVal < 90)
                tmpVal = 90
            sbHeight.progress = tmpVal
        }

        ivHRightArrow.setOnClickListener {
            var tmpVal:Int = sbHeight.progress +1
            if (tmpVal > 210)
                tmpVal = 210
            sbHeight.progress = tmpVal
        }

        ivWLeftArrow.setOnClickListener {
            var tmpVal:Int = sbWeight.progress - 1
            if (tmpVal < 10)
                tmpVal = 10
            sbWeight.progress = tmpVal
        }

        ivWRightArrow.setOnClickListener {
            var tmpVal:Int = sbWeight.progress + 1
            if (tmpVal > 150)
                tmpVal = 150
            sbWeight.progress = tmpVal
        }

        //firebase-admob 초기화
        MobileAds.initialize(this,getString(R.string.admob_app_id))
        //firebase-admob 배너
        try {
            mAdView = findViewById(R.id.adView)
            val adRequest = AdRequest.Builder().build()
            mAdView.loadAd(adRequest)
        } catch (e:Exception){
            Log.d("admob", "The adMob banner wasn't loaded yet. ${e.toString()}")
        }

        mAdView.adListener = object: AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.d("admob", "The adMob banner onAdLoaded.")
            }

            override fun onAdFailedToLoad(p0: Int) {
                // Code to be executed when an ad request fails.
                Log.d("admob", "The adMob banner onAdFailedToLoad. ${p0.toString()}")
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.d("admob", "The adMob banner onAdOpened.")
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.d("admob", "The adMob banner onAdClicked.")
            }

            override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.\
                Log.d("admob", "The adMob banner onAdLeftApplication.")
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Log.d("admob", "The adMob banner onAdClosed.")
            }
        }
        // 이전에 입력한 값을 읽어오기
        loadData()
        // BMI 계산
        resultButton.setOnClickListener {
            loadResult()
        }

        // 로또 번호 랜덤 추출 6개
        val result:List<Int> = ArrayList(LottoNumberMaker.getShuffleLottoNumbers())
        updateLottoBallImage(result.sortedBy { it })
    }

    //뒤로가기 연속 클릭 대기 시간
    var mBackWait:Long = 0
    override fun onBackPressed() {
        // 뒤로가기 버튼 클릭
        if(System.currentTimeMillis() - mBackWait >=2000 ) {
            mBackWait = System.currentTimeMillis()
            toast("뒤로가기 버튼을 한번 더 누르면 종료됩니다.")
        } else {
//            finish() //액티비티 종료
            finishAffinity()        //해당 앱의 루트 액티비티를 종료시킨다. (API  16미만은 ActivityCompat.finishAffinity())
            System.runFinalization() //현재 작업중인 쓰레드가 다 종료되면, 종료 시키라는 명령어이다.
            exitProcess(0)    // 현재 액티비티를 종료시킨다.
        }
    }

    private fun loadResult() {
        var heightval:String = mHeightVal
        var weightval:String = mWeightVal
        startActivity<ResultActivity>(
               "weight" to weightval,
               "height" to heightval
        )
        saveData(heightval, weightval)
    }

    private fun loadData () {
        try {
            val pref: SharedPreferences = getSharedPreferences("hwData", MODE_PRIVATE)
            val height = pref.getString("KEY_HEIGHT", "0")
            val weight = pref.getString("KEY_WEIGHT", "0")

            sbHeight.progress = Integer.parseInt(height?:"0")
            sbWeight.progress = Integer.parseInt(weight?:"0")

            tvHeight.text = "Height : " + sbHeight.progress.toString() + "cm"
            tvWeight.text = "Weight : " + sbWeight.progress.toString() + "kg"

        } catch (e: ClassCastException){
            Log.d("loadData", "ClassCastException : $e")
        } catch (e: NumberFormatException){
            Log.d("loadData", "NumberFormatException : $e")
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
    private fun updateLottoBallImage(result: List<Int>) {
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
