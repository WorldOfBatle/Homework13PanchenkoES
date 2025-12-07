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

        // Кнопка «Сохранить»
        binding.btnSave.setOnClickListener(v -> saveNoteAndFinish());
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
}
