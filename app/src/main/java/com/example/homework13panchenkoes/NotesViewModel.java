package com.example.homework13panchenkoes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

// ViewModel: хранит состояние (список заметок) и вызывает репозиторий
public class NotesViewModel extends AndroidViewModel {

    private final NotesRepository repository;

    private final MutableLiveData<List<Note>> notesLiveData = new MutableLiveData<>(new ArrayList<>());

    public NotesViewModel(@NonNull Application application) {
        super(application);

        repository = new NotesRepository(application);

        List<Note> loaded = repository.loadNotes();

        if (loaded.isEmpty()) {
            loaded = createDemoNotes();
            repository.saveNotes(loaded);
        }

        notesLiveData.setValue(loaded);
    }

    public LiveData<List<Note>> getNotes() {
        return notesLiveData;
    }

    public int addNote(String title, String text) {
        List<Note> current = new ArrayList<>(getCurrentNotes());
        current.add(new Note(title, text));
        applyNewList(current);
        return current.size() - 1;
    }

    public void updateNote(int position, String title, String text) {
        List<Note> current = new ArrayList<>(getCurrentNotes());
        if (position < 0 || position >= current.size()) return;

        current.set(position, new Note(title, text));
        applyNewList(current);
    }

    public Note deleteNote(int position) {
        List<Note> current = new ArrayList<>(getCurrentNotes());
        if (position < 0 || position >= current.size()) return null;

        Note removed = current.remove(position);
        applyNewList(current);
        return removed;
    }

    private List<Note> getCurrentNotes() {
        List<Note> value = notesLiveData.getValue();
        return value != null ? value : new ArrayList<>();
    }

    private void applyNewList(List<Note> newList) {
        notesLiveData.setValue(newList);
        repository.saveNotes(newList);
    }

    private List<Note> createDemoNotes() {
        List<Note> demo = new ArrayList<>();
        demo.add(new Note("Первая заметка", "Это пример заметки для проверки списка."));
        demo.add(new Note("Идея для проекта", "Сделать своё приложение заметок с синхронизацией."));
        return demo;
    }
}
