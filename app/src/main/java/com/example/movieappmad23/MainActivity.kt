package com.example.movieappmad23

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.movieappmad23.models.Movie
import com.example.movieappmad23.models.getMovies
import com.example.movieappmad23.ui.theme.MovieAppMAD23Theme

class MainActivity : ComponentActivity() {
    private val movieViewModel by viewModels<MovieViewModel>()
    private val tabs = listOf(
        "Home",
        "Favorites"
    )

    var currentTab by mutableStateOf(0)

    // Overrides the onCreate() function, which is called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Sets the content of the activity to the MovieAppMAD23Theme
        setContent {
            MovieAppMAD23Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    // Defines a column layout
                    Column {
                        // Creates a TabRow with a selected tab index, background color, and content color
                        TabRow(
                            selectedTabIndex = currentTab,
                            backgroundColor = MaterialTheme.colors.primary,
                            contentColor = MaterialTheme.colors.onPrimary
                        ) {
                            // Loops through the tabs and creates a Tab for each one
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    text = { Text(title) },
                                    selected = currentTab == index,
                                    onClick = { currentTab = index }
                                )
                            }
                        }

                        // Determines whether to show the MovieList or FavoriteScreen depending on the selected tab
                        if (currentTab == 0) {
                            // Gets a list of movies and displays them in a MovieList
                            val movies = getMovies()
                            MovieList(movies = movies, movieViewModel = movieViewModel)
                        } else {
                            // Displays the user's favorite movies in a FavoriteScreen
                            FavoriteScreen(movieViewModel = movieViewModel)
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun MovieList(movies: List<Movie>, movieViewModel: MovieViewModel) {
    val favoriteMovies = movieViewModel.favoriteMovies
    val isFavorite: (Movie) -> Boolean = { movie -> favoriteMovies.contains(movie) }

    LazyColumn {
        items(movies) { movie ->
            MovieRow(movie, isFavorite(movie)) {
                movieViewModel.toggleFavorite(movie)
            }
        }
    }
}


@Composable
fun MovieRow(movie: Movie, isFavorite: Boolean, onToogleFavorite: (Movie)-> Unit) {

    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(5.dp),
        shape = RoundedCornerShape(corner = CornerSize(15.dp)),
        elevation = 5.dp
    ) {
        Column {
            Box(modifier = Modifier
                .height(150.dp)
                .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.avatar2),
                    contentDescription = "Movie Poster",
                    contentScale = ContentScale.Crop
                )

                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                    contentAlignment = Alignment.TopEnd
                ){
                    Icon(
                        tint = MaterialTheme.colors.secondary,
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Add to favorites",
                        modifier = Modifier.clickable() {
                           onToogleFavorite(movie)
                        }
                    )
                }
            }

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(movie.title, style = MaterialTheme.typography.h6)
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Show details"
                )
            }
        }
    }
}

// Defines a composable function called FavoriteScreen that takes an instance of MovieViewModel as a parameter
@Composable
fun FavoriteScreen(movieViewModel: MovieViewModel) {
    // Retrieves the list of favorite movies from the movieViewModel by accessing the favoriteMovies property
    val favoriteMovies = movieViewModel.favoriteMovies

    // Creates a vertically scrollable list of items using LazyColumn
    LazyColumn {

        // Iterates over the favoriteMovies list and for each movie, it creates a FavoriteMovieRow composable
        items(favoriteMovies) { movie ->
            FavoriteMovieRow(movie = movie, movieViewModel = movieViewModel)
        }
    }
}

@Composable
// Defines a composable function called FavoriteMovieRow that takes a Movie object and an instance of MovieViewModel as parameters
fun FavoriteMovieRow(movie: Movie, movieViewModel: MovieViewModel) {

    // Determines whether the current movie is marked as a favorite by checking if it is present in the favoriteMovies list of movieViewModel
    val isFavorite = movieViewModel.favoriteMovies.contains(movie)

    // Creates a Card composable with the provided modifier, shape, and elevation
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        shape = RoundedCornerShape(corner = CornerSize(15.dp)),
        elevation = 5.dp
    ) {

        // Creates a Row composable to display the movie poster, title, and favorite icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Displays the movie poster using the Image composable, with the provided painter resource, content description, and size modifier
            Image(
                painter = painterResource(id = R.drawable.avatar2),
                contentDescription = "Movie Poster",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .padding(10.dp)
            )

            // Displays the movie title using the Text composable, with the provided text, style, and padding modifier
            Text(
                text = movie.title,
                style = MaterialTheme.typography.h6,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )

            // Displays the favorite icon using the Icon composable, with the provided tint, image vector, content description, and clickable modifier
            Icon(
                tint = MaterialTheme.colors.secondary,
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Add to favorites",
                modifier = Modifier.clickable {
                    movieViewModel.toggleFavorite(movie)
                }
            )
        }
    }
}


// Defines a class called MovieViewModel that extends the ViewModel class
class MovieViewModel : ViewModel() {

    // Defines a private mutable list of favorite movies
    private val _favoriteMovies = mutableStateListOf<Movie>()

    // Defines a public read-only list of favorite movies
    val favoriteMovies: List<Movie> = _favoriteMovies

    // Defines a function to toggle the favorite state of a movie
    fun toggleFavorite(movie: Movie) {
        if (_favoriteMovies.contains(movie)) {
            _favoriteMovies.remove(movie)
        } else {
            _favoriteMovies.add(movie)
        }
    }
}
