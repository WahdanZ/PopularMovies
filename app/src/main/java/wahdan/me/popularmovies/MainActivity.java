package wahdan.me.popularmovies;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import wahdan.me.popularmovies.fragment.DetailsFragment;
import wahdan.me.popularmovies.fragment.MainActivityFragment;
import wahdan.me.popularmovies.helper.OnSortMoviesChange;
import wahdan.me.popularmovies.helper.onMovieSelectionChange;
import wahdan.me.popularmovies.model.MovieModel;

public class MainActivity extends AppCompatActivity implements onMovieSelectionChange, NavigationView.OnNavigationItemSelectedListener {
    NavigationView navigationView;
    DrawerLayout drawer;
    OnSortMoviesChange moviesChange;
    DetailsFragment detailsFragment;
    MainActivityFragment movieFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //    new FetchMovieTask().execute("popularity.des");

        if (findViewById(R.id.fragment_container) != null) {

            // However if we are being restored from a previous state, then we don't
            // need to do anything and should return or we could end up with overlapping Fragments
            if (savedInstanceState != null) {
                return;
            }
            // NearbyPharmaciesFragment nearbyPharmaciesFragment = new NearbyPharmaciesFragment();
            // Create an Instance of Fragment
            MainActivityFragment mainActivityFragment = new MainActivityFragment();
            Bundle bundle = new Bundle();
            bundle.putString("sort", "popularity.desc");
            mainActivityFragment.setArguments(bundle);
            moviesChange = (OnSortMoviesChange) mainActivityFragment;
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mainActivityFragment, "Movies").commit();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnSelectionChanged(MovieModel movieModel) {
        detailsFragment = (DetailsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.details_movie_fragment);
        movieFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.movie_items_fragment);

        if (detailsFragment != null) {
            moviesChange = (OnSortMoviesChange) movieFragment;
            detailsFragment.initView(movieModel);
        } else {

            DetailsFragment newDesriptionFragment = DetailsFragment.newInstance(movieModel);
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            fragmentTransaction.replace(R.id.fragment_container, newDesriptionFragment, "Details");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);

        if (id == R.id.popular) {
            if (fragment != null && fragment.getTag().contains("Details"))
                sortMovies("popularity.desc");
            moviesChange.onChangeSortMovies("popularity.desc");
        }
        if (id == R.id.high_rate) {
            if (fragment != null && fragment.getTag().contains("Details"))
                sortMovies("Vote_average.des");
            moviesChange.onChangeSortMovies("Vote_average.des");
        }
        if (id == R.id.favourite) {
            if (fragment != null && fragment.getTag().contains("Details"))
                sortMovies("fav");
            moviesChange.onChangeSortMovies("fav");
        }
        Log.d("MainActivity", "");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void sortMovies(String sort) {
        Log.d("MainActivity", "Details");
        MainActivityFragment mainActivityFragment = new MainActivityFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("sort", sort);
        mainActivityFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_container, mainActivityFragment, "Movies");
        fragmentTransaction.commit();

    }
}
