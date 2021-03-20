package uz.codearn.codearnapp.ui.main.finishtest

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import uz.codearn.codearnapp.R
import uz.codearn.codearnapp.databinding.ActivityFinishTestBinding
import uz.codearn.codearnapp.model.User
import uz.codearn.codearnapp.ui.main.test.TestActivity
import kotlin.math.absoluteValue

class FinishTestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFinishTestBinding
    private val user = Firebase.auth.currentUser!!
    private val usersRef = Firebase.firestore.collection("users")
    private lateinit var programmingLang: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_finish_test)
        supportActionBar?.hide()
        val correctAnswers = intent.getIntExtra("CORRECT_ANSWERS", 0)
        programmingLang = intent.getStringExtra("PROGRAMMING_LANGUAGE")!!

        initResultsPieChart(correctAnswers)
        initCongratulationsText(correctAnswers)
        initEarnedScoreUpdateScore(correctAnswers)
        initRetryButton()
        initHomeButton()
    }

    private fun initRetryButton() {
        binding.tryAgainBtn.setOnClickListener {
            val intent = Intent(this, TestActivity::class.java)
            intent.putExtra("PROGRAMMING_LANG", programmingLang)
            startActivity(intent)
            finish()
        }
    }

    private fun initHomeButton() {
        binding.goToHome.setOnClickListener {
            finish()
        }
    }

    private fun initEarnedScoreUpdateScore(correctAnswers: Int) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val earnedScore = correctAnswers * 10
                binding.earnedScore.text = earnedScore.toString()
                val oldData = usersRef.whereEqualTo("userId", user.uid).get().await().documents[0]
                val user = oldData.toObject<User>()!!
                usersRef.document(oldData.id).update(mapOf("score" to (user.score + earnedScore)))
                    .await()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@FinishTestActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

    private fun initCongratulationsText(correctAnswers: Int) {
        binding.congratulationsText.text = getString(R.string.congratulations_text, correctAnswers)
    }

    private fun initResultsPieChart(correctAnswers: Int) {
        val pieVisitors = ArrayList<PieEntry>()
        pieVisitors.add(PieEntry(correctAnswers.toFloat(), "To'gri"))
        pieVisitors.add(PieEntry((10 - correctAnswers).absoluteValue.toFloat(), "Noto'g'ri"))
        val pieDataSet = PieDataSet(pieVisitors, "Natijalar")
        pieDataSet.setColors(Color.parseColor("#00b894"), Color.parseColor("#ff7675"))
        pieDataSet.valueTextColor = Color.parseColor("#ffffff")
        pieDataSet.valueTextSize = 16f
        pieDataSet.valueTypeface = Typeface.DEFAULT_BOLD
        pieDataSet.valueFormatter = MyFormatter()
        val pieData = PieData(pieDataSet)
        binding.resultsPieChart.apply {
            data = pieData
            setCenterTextSize(18f)
            setCenterTextTypeface(Typeface.DEFAULT_BOLD)
            description.isEnabled = false
            setCenterTextColor(Color.parseColor("#99FFFFFF"))
            legend.isEnabled = false
            setHoleColor(Color.parseColor("#14FFFFFF"))
            setEntryLabelColor(Color.parseColor("#ffffff"))
            setEntryLabelTypeface(Typeface.DEFAULT_BOLD)
            holeRadius = 40f
            centerText = "RESULTS!"
            dragDecelerationFrictionCoef = 0.59f
            transparentCircleRadius = 45f
            animateY(1500, com.github.mikephil.charting.animation.Easing.EaseInOutCubic)
        }
    }

    private class MyFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return "" + value.toInt()
        }
    }
}