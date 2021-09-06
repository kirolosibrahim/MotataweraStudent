package com.kmk.motatawera.student.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.firestore.FirebaseFirestore;
import com.kmk.motatawera.student.R;
import com.kmk.motatawera.student.databinding.ActivityStartQuizBinding;
import com.kmk.motatawera.student.model.QuestionsModel;
import com.kmk.motatawera.student.storage.SharedPrefManager;
import com.kmk.motatawera.student.ui.main.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class StartQuizActivity extends AppCompatActivity {

    List<QuestionsModel> list;

    private String currentUserId;
    FirebaseFirestore db;

    private String quizId;
    private boolean isQuizFinish = true;

    public int counter;
    //UI Elements

    ActivityStartQuizBinding binding;
    private boolean canAnswer = false;
    private int currentQuestion;
    private int index = 1;
    private int correctAnswers = 0;
    private int wrongAnswers = 0;
    private String quizTitlee, CorrectAnswer;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_start_quiz);

        db = FirebaseFirestore.getInstance();

        list  = new ArrayList<>();
        quizId = getIntent().getStringExtra("id");
        quizTitlee = getIntent().getStringExtra("titel");
        currentUserId = SharedPrefManager.getInstance().getUser(this).getId();

        db.collection("quiz")
                .document(quizId).collection("Questions")
                .get()
                .addOnCompleteListener(task -> {
                    list = task.getResult().toObjects(QuestionsModel.class);
                    Collections.shuffle(list);
                    timer();
                    onClick();
                    loadUI();
                });

    }

    private void loadUI() {
        //Quiz Data Loaded, Load the UI

        binding.quizTitle.setText(quizTitlee);
        binding.quizQuestion.setText(R.string.load_firs_question);

        //Load First Question
        loadQuestion(1);
    }

    private void loadQuestion(int questNum) {
        //Set Question Number
        binding.quizQuestionNumber.setText(questNum + "");

        //Load Question Text
        binding.quizQuestion.setText(list.get(questNum - 1).getQuestion());

        //Load Options
        binding.rbA1.setText(list.get(questNum - 1).getAnswer1());
        binding.rbA2.setText(list.get(questNum - 1).getAnswer2());
        binding.rbA3.setText(list.get(questNum - 1).getAnswer3());
        binding.rbA4.setText(list.get(questNum - 1).getAnswer4());
        CorrectAnswer = list.get(questNum - 1).getCorrectAnswer();
        //   Toast.makeText(this, CorrectAnswer, Toast.LENGTH_SHORT).show();
        //Question Loaded, Set Can Answer
        canAnswer = true;
        currentQuestion = questNum;

        //Start Question Timer
        //startTimer(questNum);
    }


    private void submitResults() {

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("questions_number", list.size());
        resultMap.put("correct", correctAnswers);
        resultMap.put("wrong", wrongAnswers);
        resultMap.put("quiz_id", quizId);
        resultMap.put("student_id", currentUserId);
        resultMap.put("quizFinished", isQuizFinish);

        db.collection("quiz").document(quizId).collection("score").add(resultMap).addOnCompleteListener(task -> {

            String id = task.getResult().getId();
            db.collection("quiz").document(quizId).collection("score").document(id).update("id", id);



        });

    }

    private void onClick() {

        binding.quizNextBtn.setOnClickListener(v -> {


            if (currentQuestion == list.size()) {
                finish();
                checkradiobtn();
                Toast.makeText(StartQuizActivity.this, R.string.exam_finish, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(StartQuizActivity.this, MainActivity.class);
                startActivity(i);
                countDownTimer.cancel();


                submitResults();

            } else {

                checkradiobtn();
                loadQuestion(currentQuestion + 1);
                countDownTimer.cancel();
                countDownTimer.start();

            }
        });
    }

    private void checkradiobtn() {


        if (binding.rbA1.isChecked()) {
            if (binding.rbA1.getText().equals(CorrectAnswer)) {
                //   Toast.makeText(this, "correct Answer", Toast.LENGTH_SHORT).show();

                correctAnswers++;
                //  Toast.makeText(this, String.valueOf(correctAnswers), Toast.LENGTH_SHORT).show();
            } else {
                // Toast.makeText(this, "Not Correct Answer", Toast.LENGTH_SHORT).show();
                wrongAnswers++;
                // Toast.makeText(this, String.valueOf(wrongAnswers), Toast.LENGTH_SHORT).show();

            }
        }
        if (binding.rbA2.isChecked()) {
            if (binding.rbA2.getText().equals(CorrectAnswer)) {
                // Toast.makeText(this, "correct Answer", Toast.LENGTH_SHORT).show();
                correctAnswers++;
                //  Toast.makeText(this, String.valueOf(correctAnswers), Toast.LENGTH_SHORT).show();
            } else {
                // Toast.makeText(this, "Not Correct Answer", Toast.LENGTH_SHORT).show();
                wrongAnswers++;
                // Toast.makeText(this, String.valueOf(wrongAnswers), Toast.LENGTH_SHORT).show();

            }
        }
        if (binding.rbA3.isChecked()) {
            if (binding.rbA3.getText().equals(CorrectAnswer)) {
                //  Toast.makeText(this, "correct Answer", Toast.LENGTH_SHORT).show();
                correctAnswers++;
                //  Toast.makeText(this, String.valueOf(correctAnswers), Toast.LENGTH_SHORT).show();
            } else {
                // Toast.makeText(this, "Not Correct Answer", Toast.LENGTH_SHORT).show();
                wrongAnswers++;
                //  Toast.makeText(this, String.valueOf(wrongAnswers), Toast.LENGTH_SHORT).show();

            }
        }
        if (binding.rbA4.isChecked()) {
            if (binding.rbA4.getText().equals(CorrectAnswer)) {
                // Toast.makeText(this, "correct Answer", Toast.LENGTH_SHORT).show();
                correctAnswers++;
                //Toast.makeText(this, String.valueOf(correctAnswers), Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(this, "Not Correct Answer", Toast.LENGTH_SHORT).show();
                wrongAnswers++;
                //  Toast.makeText(this, String.valueOf(wrongAnswers), Toast.LENGTH_SHORT).show();

            }
        }
        binding.rbA1.setChecked(true);
        if (currentQuestion == (list.size() - 1)) {
            binding.quizNextBtn.setText(R.string.finish_exam);
        }


    }

    private void timer() {
        countDownTimer = new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                binding.txtCount.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {


                if (currentQuestion == list.size()) {
                    checkradiobtn();
                    Toast.makeText(StartQuizActivity.this, R.string.exam_finish, Toast.LENGTH_SHORT).show();
                    countDownTimer.cancel();
                    finish();
                    Intent i = new Intent(StartQuizActivity.this, MainActivity.class);
                    startActivity(i);
                    submitResults();

                } else {
                    checkradiobtn();
                    loadQuestion(currentQuestion + 1);
                    countDownTimer.cancel();
                    countDownTimer.start();
                }

            }
        }.start();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        if (correctAnswers == 0) {
            wrongAnswers = list.size();
            finish();
            Intent i = new Intent(StartQuizActivity.this, MainActivity.class);
            startActivity(i);
            submitResults();

        } else {

            wrongAnswers = (list.size() - correctAnswers);
            finish();
            Intent i = new Intent(StartQuizActivity.this, MainActivity.class);
            startActivity(i);
            submitResults();

        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        finish();
        Intent i = new Intent(StartQuizActivity.this, MainActivity.class);
        startActivity(i);
        submitResults();
    }

}