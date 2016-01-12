package wahdan.me.popularmovies.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import wahdan.me.popularmovies.model.MovieModel;


public class MovieDb extends SQLiteOpenHelper {
    public static final int DB_VERSION = 1;
    public static final String DB_NAEM = "MOVIES";
    public static final String TABLE_NAME = "movies";
    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String OVERVIEW = "overview";
    public static final String VOTEAVARAGE = "voteavarage";
    public static final String POSTERPATH = "posterpath";
    public static final String RALEASEDATE = "releasedate";

    public MovieDb(Context context) {
        super(context, DB_NAEM, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MOVIES_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + ID + " INTEGER PRIMARY KEY," + TITLE + " TEXT,"
                + OVERVIEW + " TEXT," + VOTEAVARAGE + " TEXT," + POSTERPATH + " TEXT," + RALEASEDATE + " TEXT" + ")";
        db.execSQL(CREATE_MOVIES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addMovie(MovieModel model) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID, model.getId());
        values.put(TITLE, model.getTitle());
        values.put(OVERVIEW, model.getOverview());
        values.put(VOTEAVARAGE, model.getVoteAverage());
        values.put(RALEASEDATE, model.getReleaseDate());
        values.put(POSTERPATH, model.getPosterPath());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public MovieModel getMovie(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{ID, TITLE, OVERVIEW, VOTEAVARAGE, POSTERPATH, RALEASEDATE}, ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            MovieModel movieModel = new MovieModel();
            movieModel.setId(Integer.parseInt(cursor.getString(0)));
            movieModel.setTitle(cursor.getString(1));
            movieModel.setOverview(cursor.getString(2));
            movieModel.setVoteAverage(Double.parseDouble(cursor.getString(3)));
            movieModel.setPosterPath(cursor.getString(4));
            movieModel.setReleaseDate(cursor.getString(5));
            return movieModel;
        } finally {
            cursor.close();

        }

    }

    public List<MovieModel> getAllMovie() {
        List<MovieModel> movieModels = new ArrayList<MovieModel>();

        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                MovieModel movieModel = new MovieModel();
                movieModel.setId(Integer.parseInt(cursor.getString(0)));
                movieModel.setTitle(cursor.getString(1));
                movieModel.setOverview(cursor.getString(2));
                movieModel.setVoteAverage(Double.parseDouble(cursor.getString(3)));
                movieModel.setPosterPath(cursor.getString(4));
                movieModel.setReleaseDate(cursor.getString(5));
                movieModels.add(movieModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return movieModels;
    }

    public void deleteMovie(MovieModel movieModel) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, ID + " =?", new String[]{String.valueOf(movieModel.getId())});
        db.close();
    }


}
