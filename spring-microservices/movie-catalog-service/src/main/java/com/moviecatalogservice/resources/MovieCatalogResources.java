package com.moviecatalogservice.resources;

import com.moviecatalogservice.model.CatalogItem;
import com.moviecatalogservice.model.Movie;
import com.moviecatalogservice.model.Rating;
import com.moviecatalogservice.model.UserRating;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResources {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private WebClient.Builder webClientBuilder;

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {
        //restTemplate makes a call to another microservice
        //and returns (an Item with populated fields) a String of the item type we want. It gets the resource and unmasrshalls it to an object
        //first argument: the url to make the call
        //second argument: the Item type we want
        //Movie movie = restTemplate.getForObject("http://localhost:8081/movies/foo", Movie.class);

        //get all rated movies IDs
        UserRating ratings = restTemplate.getForObject("http://movie-rating-service/ratingsdata/users/" + userId, UserRating.class);

        //replace every rating with a catalogItem object
        return ratings.getUserRating().stream().map(rating -> {
                    Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);
                    return new CatalogItem(movie.getName(), "Description", rating.getRating());

        })
        .collect(Collectors.toList());

    }

}
