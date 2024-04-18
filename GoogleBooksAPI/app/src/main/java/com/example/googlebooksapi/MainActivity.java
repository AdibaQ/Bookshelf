package com.example.googlebooksapi;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editTextSearch;
    private Button buttonSearch;
    private RecyclerView recyclerView;
    private BooksAdapter adapter;
    private List<Book> books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextSearch = findViewById(R.id.editTextSearch);
        buttonSearch = findViewById(R.id.buttonSearch);
        recyclerView = findViewById(R.id.recyclerView);

        books = new ArrayList<>();
        adapter = new BooksAdapter(books, new BooksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Book book) {
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = editTextSearch.getText().toString().trim();
                if (!query.isEmpty()) {
                    performSearch(query);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a search query", Toast.LENGTH_SHORT).show();
                }
            }
        });

        editTextSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = editTextSearch.getText().toString().trim();
                if (!query.isEmpty()) {
                    performSearch(query);
                    return true;
                }
            }
            return false;
        });
    }

    private void performSearch(String query) {
       InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);

        String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=" + query + "&maxResults=30&key=AIzaSyCrRgzZqG0_1m1NzfUnGHcKoZ5tVuvVF40";
        new FetchBooksTask().execute(apiUrl);
   }
    private class FetchBooksTask extends AsyncTask<String, Void, List<Book>> {

        @Override
        protected List<Book> doInBackground(String... urls) {
            List<Book> books = new ArrayList<>();
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                String result = stringBuilder.toString();
                JSONObject jsonObject = new JSONObject(result);
                JSONArray itemsArray = jsonObject.getJSONArray("items");
                for (int i = 0; i < itemsArray.length(); i++) {
                    JSONObject bookObject = itemsArray.getJSONObject(i);
                    JSONObject volumeInfo = bookObject.getJSONObject("volumeInfo");
                    String title = volumeInfo.optString("title", "");
                    String bookId = bookObject.optString("id", "");
                    String imageUrl = "https://books.google.com/books/content?id="+ bookId + "&printsec=frontcover&img=1&zoom=1&edge=curl&imgtk=AFLRE725EDjl-apJnj_hFVX8rHsaTip_OyufLDX8qUDetLx3J1kf4RZx45_bAxMEkVr8_p5LpI27DO_G_Tyr4g0fbY7PlsNDpVtVqqgh-rt_GjQjoWv0TmKGsckhZsjGfwfRbRPRVlpS&source=gbs_api";
                    JSONArray authorsArray = volumeInfo.optJSONArray("authors");
                    String authors = "";
                    if (authorsArray != null) {
                        for (int j = 0; j < authorsArray.length(); j++) {
                            authors += authorsArray.getString(j);
                            if (j < authorsArray.length() - 1) {
                                authors += ", ";
                            }
                        }
                    }
                    books.add(new Book(title, authors, imageUrl, bookId));
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return books;
        }

        @Override
        protected void onPostExecute(List<Book> books) {
            super.onPostExecute(books);

            for (Book book : books) {
                Log.d("BookInfo", "Title: " + book.getTitle());
                Log.d("BookInfo", "Authors: " + book.getAuthors());
                Log.d("BookInfo", "ImageUrl: " + book.getImageUrl());
                Log.d("BookInfo", "BookId: " + book.getBookId());
            }
            if (books.isEmpty()) {
                Log.d("BookInfo", "No books found in the response.");
            } else {
                Log.d("BookInfo", "Number of books retrieved: " + books.size());
            }

            adapter.updateData(books);
        }

    }
}
