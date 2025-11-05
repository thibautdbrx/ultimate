package org.ultimateam.apiultimate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public abstract class Competition {
    public enum Genre {MIXTE, FEMMME, HOMME};
    public enum Format {V5, v7};

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id_competition;

    private Genre genre;
    private Format format;

    private LocalDateTime date_debut;
    private LocalDateTime date_fin;

    @OneToMany(mappedBy = "id_competition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Match> matchs = new ArrayList<>();



}
