<script setup>
import ImageTournoi from "@/assets/img/img_tournois.jpg"
import ImageChampionnat from "@/assets/img/img_championnat.png"

const props = defineProps({
  competition: {
    type: Object,
    required: true
  }
})

const format_bien_aff = (format) => (format || "").toUpperCase()

// Retourne l'image en fonction du type
function getCompetitionImage(type) {
  if (!type) return ImageTournoi
  if (type.toLowerCase() === 'tournoi') return ImageTournoi
  if (type.toLowerCase() === 'championnat') return ImageChampionnat
  return ImageTournoi
}
</script>

<template>
  <div class="competition-card">
    <img
        :src="getCompetitionImage(competition.typeCompetition)"
        alt="Image compétition"
        class="competition-img"
    />
    <div class="competition-info">
      <h3>{{ competition.nomCompetition }}</h3>
      <p>{{ format_bien_aff(competition.format) + " - " + competition.genre }}</p>
      <p>{{competition.dateDebut.slice(0,7)}} | {{competition.dateFin.slice(0,7)}}</p>
    </div>
  </div>
</template>

<style scoped>
.competition-card {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  overflow: hidden;
  height: 100%; /* Pour que toutes les cartes aient la même hauteur dans la grille */
}
.competition-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 5px 10px rgba(0,0,0,0.15);
}
.competition-img {
  width: 100%;
  height: 120px;
  object-fit: cover;
}
.competition-info {
  padding: 1rem;
  text-align: center;
}
h3 {
  margin: 0;
  font-size: 1.1rem;
  color: #2c3e50;
}
p {
  margin: 0.5rem 0 0;
  color: #666;
  font-size: 0.9rem;
}
</style>