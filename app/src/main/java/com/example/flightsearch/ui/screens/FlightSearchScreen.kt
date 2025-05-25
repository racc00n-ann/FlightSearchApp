package com.example.flightsearch.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.flightsearch.data.database.entity.Airport
import com.example.flightsearch.data.database.entity.Favorite
import com.example.flightsearch.data.model.Flight
import com.example.flightsearch.ui.viewmodels.FlightSearchUiState
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.size

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchScreen(
    uiState: FlightSearchUiState,
    onSearchQueryChange: (String) -> Unit,
    onAirportSelected: (Airport) -> Unit,
    onToggleFavorite: (Flight) -> Unit,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Flight Search")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = onSearchQueryChange,
                onClearSearch = onClearSearch,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                uiState.selectedAirport != null -> {
                    FlightsList(
                        departureAirport = uiState.selectedAirport,
                        flights = uiState.flights,
                        onToggleFavorite = onToggleFavorite,
                        favorites = uiState.favorites
                    )
                }
                uiState.searchQuery.isNotEmpty() -> {
                    AirportSuggestions(
                        airports = uiState.suggestedAirports,
                        onAirportSelected = onAirportSelected
                    )
                }
                else -> {
                    FavoritesList(
                        favorites = uiState.favorites,
                        onRemoveFavorite = { favorite ->
                            onToggleFavorite(
                                Flight(
                                    departureCode = favorite.departure_code,
                                    departureName = "",
                                    destinationCode = favorite.destination_code,
                                    destinationName = ""
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Enter airport name or code") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClearSearch) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = Color.Red
                    )
                }
            }
        },
        shape = RoundedCornerShape(20.dp),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(20.dp))
    )
}

@Composable
fun AirportSuggestions(
    airports: List<Airport>,
    onAirportSelected: (Airport) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Suggestions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(airports) { airport ->
                AirportItem(
                    airport = airport,
                    onClick = { onAirportSelected(airport) }
                )
            }
        }
    }
}

@Composable
fun AirportItem(
    airport: Airport,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Place,
            contentDescription = null,
            modifier = Modifier.padding(end = 16.dp)
        )
        Column {
            Text(
                text = airport.name,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = airport.iata_code,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun FlightsList(
    departureAirport: Airport,
    flights: List<Flight>,
    onToggleFavorite: (Flight) -> Unit,
    favorites: List<Favorite>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Flights from ${departureAirport.name} (${departureAirport.iata_code})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(flights) { flight ->
                val isFavorite = favorites.any {
                    it.departure_code == flight.departureCode &&
                            it.destination_code == flight.destinationCode
                }

                FlightItem(
                    flight = flight,
                    isFavorite = isFavorite,
                    onToggleFavorite = { onToggleFavorite(flight) }
                )

            }
        }
    }
}

@Composable
fun FlightItem(
    flight: Flight,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                   Column {
                        Text(
                            text = "DEPART",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                       Row(verticalAlignment = Alignment.Top) {
                           Text(
                               text = flight.departureCode,
                               style = MaterialTheme.typography.bodyLarge,
                               fontWeight = FontWeight.Bold
                           )
                           Text( text = "   ")
                           Text(
                               text = flight.departureName,
                               style = MaterialTheme.typography.bodyMedium,
                               color = MaterialTheme.colorScheme.onSurfaceVariant
                           )
                       }
                    }

                   Spacer(modifier = Modifier.height(12.dp))
                   Column {
                       Text(
                           text = "ARRIVE",
                           style = MaterialTheme.typography.labelMedium,
                           color = MaterialTheme.colorScheme.onSurfaceVariant
                       )
                       Row(verticalAlignment = Alignment.Top) {
                           Text(
                               text = flight.destinationCode,
                               style = MaterialTheme.typography.bodyLarge,
                               fontWeight = FontWeight.Bold
                           )
                           Text( text = "   ")
                           Text(
                               text = flight.destinationName,
                               style = MaterialTheme.typography.bodyMedium,
                               color = MaterialTheme.colorScheme.onSurfaceVariant
                           )
                       }

                   }
            }

            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarOutline,
                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (isFavorite)
                        Color(0xFFFFC107)
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun FavoritesList(
    favorites: List<Favorite>,
    onRemoveFavorite: (Favorite) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Favorite flights",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No favorites yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn {
                items(favorites) { favorite ->
                    FavoriteItem(
                        favorite = favorite,
                        onRemoveFavorite = { onRemoveFavorite(favorite) }
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteItem(
    favorite: Favorite,
    onRemoveFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "DEPART",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = favorite.departure_code,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ARRIVE",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = favorite.destination_code,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            IconButton(
                onClick = onRemoveFavorite,
                modifier = Modifier.size(50.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Remove from favorites",
                    tint = Color(0xFFFFC107)
                )
            }
        }
    }
}
