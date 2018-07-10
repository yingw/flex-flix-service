package cn.wilmar.example;

import lombok.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Date;

@SpringBootApplication
public class FluxFlixServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FluxFlixServiceApplication.class, args);
    }
}

@Component
class DataAppInitializr {
    private final MovieRepository movieRepository;

    DataAppInitializr(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void run(ApplicationReadyEvent event) {
        movieRepository
                .deleteAll()
                .thenMany(
                        Flux.just("A", "B").flatMap(
                                name -> movieRepository.save(new Movie(name))
                        )
                ).subscribe(null, null, () -> movieRepository.findAll().subscribe(System.out::println));
    }
}

@Service
class FluxFlixService {
    private final MovieRepository movieRepository;

    FluxFlixService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }
}

// ReactiveMongoRepository
interface MovieRepository extends ReactiveMongoRepository<Movie, String> {

}

@Document // mongodb
@Data
@NoArgsConstructor
@RequiredArgsConstructor
class Movie {
    private String id; // mongodb's PK using String.
    @NonNull
    private String title;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class MovieEvent {
    private String movieId;
    private Date date;
}