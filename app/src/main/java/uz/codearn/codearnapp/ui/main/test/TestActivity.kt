package uz.codearn.codearnapp.ui.main.test

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import uz.codearn.codearnapp.R
import uz.codearn.codearnapp.databinding.ActivityTestBinding
import uz.codearn.codearnapp.model.Question
import uz.codearn.codearnapp.ui.main.finishtest.FinishTestActivity

class TestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTestBinding
    private val viewModel: TestViewModel by lazy {
        val selectedProgrammingLanguage = intent.getStringExtra("PROGRAMMING_LANG")!!
        val viewModelFactory = TestViewModelFactory(selectedProgrammingLanguage, application)
        ViewModelProvider(this, viewModelFactory).get(TestViewModel::class.java)
    }
    private val programmingLang: String by lazy {
        intent.getStringExtra("PROGRAMMING_LANG")!!
    }
    private lateinit var currentQuestion: Question
    private var currentQuestionInd: Int = 0
    private lateinit var allQuestions: List<Question>
    private var correctAnswers = 0
    private val questionsRef = Firebase.firestore.collection("questions")
    private var optionsClickable = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_test)
        binding.lifecycleOwner = this
        supportActionBar?.hide()
        binding.nextFab.isEnabled = true

        viewModel.questions.observe(this, { questionList ->
            questionList?.let {
                if (questionList.isNotEmpty()) {
                    allQuestions = it.shuffled()
                    setUpQuestion()
                }
            }
        })

        binding.nextFab.setOnClickListener {
            binding.nextFab.isEnabled = false
            nextQuestion()
        }
    }

    private fun setUpQuestion() {
        binding.nextFab.isEnabled = false
        optionsClickable = true
        currentQuestion = allQuestions[currentQuestionInd]
        binding.questionText.text = currentQuestion.questionText
        val adapter = OptionsListAdapter(currentQuestion.questionOptions)
        binding.optionsList.adapter = adapter
        adapter.onItemClick = { position: Int ->
            if (optionsClickable) {
                binding.nextFab.isEnabled = true
                binding.progressIndicator.getChildAt(currentQuestionInd).apply {
                    setBackgroundColor(Color.parseColor("#00ACEE"))
                }
                optionsClickable = false
                if (currentQuestion.correctAnswer == position) {
                    correctAnswers++
                    val viewHolder = binding.optionsList.findViewHolderForAdapterPosition(position)
                    val selectedOptionView = viewHolder?.itemView
                    selectedOptionView?.setBackgroundColor(Color.parseColor("#144CAF50"))

                } else {
                    val viewHolder =
                        binding.optionsList.findViewHolderForAdapterPosition(currentQuestion.correctAnswer)
                    val correctOptionView = viewHolder?.itemView
                    correctOptionView?.setBackgroundColor(Color.parseColor("#144CAF50"))
                    val wrongViewHolder =
                        binding.optionsList.findViewHolderForAdapterPosition(position)
                    val wrongSelectedOptionView = wrongViewHolder?.itemView
                    wrongSelectedOptionView?.setBackgroundColor(Color.parseColor("#14F44336"))
                }
            }
        }
    }

    private fun nextQuestion() {
        val animateRight0 = animateToRight(0)
        val animateRight1 = animateToRight(1)
        val animateRight2 = animateToRight(2)
        val animateFadeOut0 = animateFadeOut(0)
        val animateFadeOut1 = animateFadeOut(1)
        val animateFadeOut2 = animateFadeOut(2)
        val animatorSet = AnimatorSet().apply {
            play(animateRight0).with(animateFadeOut0).before(animateRight1)
            play(animateRight1).with(animateFadeOut1)
            play(animateRight2).with(animateFadeOut2).after(animateFadeOut1)
            start()
        }

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                currentQuestionInd++
                if (currentQuestionInd == 10) {
                    val intent = Intent(this@TestActivity, FinishTestActivity::class.java)
                    intent.putExtra("CORRECT_ANSWERS", correctAnswers)
                    intent.putExtra("PROGRAMMING_LANGUAGE", programmingLang)
                    startActivity(intent)
                    finish()
                } else {
                    setUpQuestion()
                }
            }
        })
    }

    private fun animateToRight(position: Int): ObjectAnimator {
        val optionView = binding.optionsList.findViewHolderForAdapterPosition(position)?.itemView
        return ObjectAnimator.ofFloat(optionView, "translationX", 700f).apply {
            interpolator = AccelerateInterpolator()
            duration = 300
        }
    }

    private fun animateFadeOut(position: Int): ObjectAnimator {
        val optionView = binding.optionsList.findViewHolderForAdapterPosition(position)?.itemView
        return ObjectAnimator.ofFloat(optionView, "alpha", 1f, 0f).apply {
            duration = 300
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Tugatish")
            .setMessage("Testni tugatishni xohlaysizmi?")
            .setPositiveButton("Ha") { _, _ ->
                finish()
            }
            .setNegativeButton("Yo'q", null)
            .show()
    }
}