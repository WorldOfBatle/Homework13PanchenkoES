package com.example.homework13panchenkoes;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.homework13panchenkoes.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NoteAdapter adapter;
    private final List<Note> notes = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Инициализируем список данных
        initNotes();

        // Настраиваем RecyclerView
        binding.recyclerNotes.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new NoteAdapter(notes);
        binding.recyclerNotes.setAdapter(adapter);

        // Decorator — добавляем разделители между карточками
        DividerItemDecoration decoration =
                new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        binding.recyclerNotes.addItemDecoration(decoration);
    }

    // Заполняем список тестовыми заметками
    private void initNotes() {
        notes.add(new Note("Купить продукты", "Молоко, хлеб, яйца, сыр"));
        notes.add(new Note("ДЗ по Android", "Доделать калькулятор и приложение с заметками"));
        notes.add(new Note("Идеи для проекта", "Придумать полезное приложение для курса"));
        notes.add(new Note("Позвонить", "Напомнить про встречу в субботу"));
    }
}
