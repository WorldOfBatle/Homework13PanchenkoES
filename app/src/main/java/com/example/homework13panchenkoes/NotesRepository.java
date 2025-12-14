package com.example.homework13panchenkoes;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// Репозиторий: отвечает только за работу с данными (SharedPreferences)
public class NotesRepository {

    private static final String PREFS_NAME = "notes_prefs";
    private static final String KEY_NOTES_JSON = "notes_json";

    private final SharedPreferences prefs;

    public NotesRepository(Context context) {
        // Берём applicationContext, чтобы не держать Activity
        Context appContext = context.getApplicationContext();
        prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public List<Note> loadNotes() {
        String json = prefs.getString(KEY_NOTES_JSON, null);
        List<Note> result = new ArrayList<>();

        if (json == null || json.isEmpty()) {
            return result;
        }

        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String title = obj.optString("title", "");
                String text = obj.optString("text", "");
                result.add(new Note(title, text));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void saveNotes(List<Note> notes) {
        JSONArray array = new JSONArray();

        if (notes != null) {
            for (Note note : notes) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("title", note != null ? note.getTitle() : "");
                    obj.put("text", note != null ? note.getText() : "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                array.put(obj);
            }
        }

        prefs.edit()
                .putString(KEY_NOTES_JSON, array.toString())
                .apply();
    }
}
