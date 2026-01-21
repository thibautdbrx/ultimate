package org.ultimateam.apiultimate.model;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Représente une compétition de type "Championnat".
 *
 * Il s'agit d'une spécialisation de {@link Competition} identifiée par la
 * valeur de discrimination {@code "CHAMPIONNAT"} dans la hiérarchie des
 * compétitions. Les champs et la logique métier communs sont définis dans
 * la classe parente {@link Competition} ; cette classe sert principalement
 * de marqueur pour différencier les types de compétition.
 *
 * Un constructeur sans argument est fourni par Lombok via {@code @NoArgsConstructor}.
 * Si vous souhaitez documenter explicitement des constructeurs ou méthodes
 * spécifiques, ajoutez-les ici avec une Javadoc au-dessus de la méthode.
 */
@Entity
@DiscriminatorValue("CHAMPIONNAT")
@NoArgsConstructor
@Getter
@Setter
public class Championnat extends Competition{
    /*
    public Championnat(Genre genre, Format format, LocalDate date_debut, LocalDate date_fin, String nomCompetition, String descriptionCompetition) {
        super(genre, format, date_debut, date_fin,nomCompetition, descriptionCompetition);
    }*/
}
