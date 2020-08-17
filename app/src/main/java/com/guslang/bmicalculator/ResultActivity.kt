package com.guslang.bmicalculator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_result.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.startActivity
import java.math.BigDecimal

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // 전달받은 키와 몸무게
        val height = intent.getStringExtra("height").toString().toInt()
        val weight = intent.getStringExtra("weight").toString().toInt()

        // BMI 계산
        val bmi = weight / Math.pow(height / 100.0, 2.0)

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


//        Toast.makeText(this, "$bmi",Toast.LENGTH_SHORT).show()
//        toast("$bmi")
        longToast("$bmi")
        txtResult.text = bmi.toString().toBigDecimal().setScale(2,BigDecimal.ROUND_HALF_UP).toString()

        btnRecalc.setOnClickListener {
            startActivity<MainActivity>()
        }

    }
}