package com.example.homework13panchenkoes;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homework13panchenkoes.databinding.ActivityEditNoteBinding;

public class EditNoteActivity extends AppCompatActivity {

    private ActivityEditNoteBinding binding;

    // Позиция заметки в списке, если редактируем (иначе -1)
    private int editingPosition = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Небольшая анимация появления экрана
        animateScreenEnter();

        // Настраиваем тулбар как ActionBar
        setSupportActionBar(binding.toolbarEdit);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // стрелка «Назад»
        }

        // Проверяем, пришли ли данные для редактирования
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(MainActivity.EXTRA_NOTE_TITLE)) {
            // Режим редактирования
            String title = intent.getStringExtra(MainActivity.EXTRA_NOTE_TITLE);
            String text = intent.getStringExtra(MainActivity.EXTRA_NOTE_TEXT);
            editingPosition = intent.getIntExtra(MainActivity.EXTRA_NOTE_POSITION, -1);

            binding.toolbarEdit.setTitle(getString(R.string.title_edit_note));
            binding.editTitle.setText(title);
            binding.editText.setText(text);
        } else {
            // Режим создания новой заметки
            binding.toolbarEdit.setTitle(getString(R.string.title_new_note));
        }

        // Кнопка «Назад» в тулбаре
        binding.toolbarEdit.setNavigationOnClickListener(v -> finish());

        // Кнопка «Сохранить» (с анимацией)
        binding.btnSave.setOnClickListener(v -> animateSaveAndFinish());
    }

    // Анимация появления: плавно + немного снизу вверх
    private void animateScreenEnter() {
        binding.getRoot().setAlpha(0f);
        binding.getRoot().setTranslationY(dpToPx(18));

        binding.getRoot().animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(260)
                .start();
    }

    // Анимация нажатия на "Сохранить" + лёгкое исчезновение экрана (видно на видео/проверке)
    private void animateSaveAndFinish() {
        binding.btnSave.setEnabled(false);

        binding.btnSave.animate()
                .scaleX(0.96f)
                .scaleY(0.96f)
                .setDuration(110)
                .withEndAction(() -> binding.btnSave.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(110)
                        .withEndAction(() -> binding.getRoot().animate()
                                .alpha(0f)
                                .translationY(dpToPx(-10))
                                .setDuration(160)
                                .withEndAction(this::saveNoteAndFinish)
                                .start())
                        .start())
                .start();
    }

    // Собираем данные и возвращаем их в MainActivity
    private void saveNoteAndFinish() {
        String title = binding.editTitle.getText() != null
                ? binding.editTitle.getText().toString().trim()
                : "";
        String text = binding.editText.getText() != null
                ? binding.editText.getText().toString().trim()
                : "";

        // Если оба поля пустые — просто выходим без результата
        if (title.isEmpty() && text.isEmpty()) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        Intent data = new Intent();
        data.putExtra(MainActivity.EXTRA_NOTE_TITLE, title);
        data.putExtra(MainActivity.EXTRA_NOTE_TEXT, text);
        data.putExtra(MainActivity.EXTRA_NOTE_POSITION, editingPosition);

        setResult(RESULT_OK, data);
        finish();
    }

    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }
}
