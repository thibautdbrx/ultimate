<script setup >
defineProps({
  code: Number,
  temp: Number,
  temp_unit: String,
  wind: Number,
  wind_unit: String
})


// --- METEO : Utilitaire pour traduire le code WMO en icone/texte ---
const getWeatherInfo = (code) => {
  const codes = { //ca vien de la la norme de l'OMM (Organisation Météorologique Mondiale)
    0: { label: "Ciel dégagé", icon: "☀️" },
    1: { label: "Principalement dégagé", icon: "🌤️" },
    2: { label: "Partiellement nuageux", icon: "⛅" },
    3: { label: "Couvert", icon: "☁️" },
    45: { label: "Brouillard", icon: "🌫️" },
    48: { label: "Brouillard givrant", icon: "🌫️" },
    51: { label: "Bruine légère", icon: "🌦️" },
    53: { label: "Bruine modérée", icon: "🌦️" },
    55: { label: "Bruine dense", icon: "🌧️" },
    61: { label: "Pluie faible", icon: "🌧️" },
    63: { label: "Pluie modérée", icon: "🌧️" },
    65: { label: "Pluie forte", icon: "⛈️" },
    71: { label: "Neige faible", icon: "🌨️" },
    73: { label: "Neige modérée", icon: "🌨️" },
    75: { label: "Neige forte", icon: "❄️" },
    95: { label: "Orage", icon: "⚡" },
    96: { label: "Orage avec grêle", icon: "⚡❄️" },
    [-1]: { label: "Météo indisponible", icon: "📡" }
  };
  // Retourne l'info ou une valeur par défaut
  return codes[code] || codes[-1];
};

</script>

<template>
  <div class="weather-card">
    <div class="weather-icon">
      {{ getWeatherInfo(code).icon }}
    </div>
    <div class="weather-info">
      <div class="weather-temp">
        {{ temp }} <span class="unit">{{ temp_unit }}</span>
      </div>
      <div class="weather-desc">
        {{ getWeatherInfo(code).label }}
      </div>
      <div class="weather-wind">
        💨 Vent : {{ wind }} {{ wind_unit}}
      </div>
    </div>
  </div>
</template>

<style scoped>


/* --- METEO  --- */
.weather-card {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  background: linear-gradient(135deg, rgba(122, 12, 167, 0.22) 0%, #ffffff 100%);
  border-radius: 12px;
  padding: 1rem;
  margin-bottom: 1.5rem;
  box-shadow: 0 4px 6px rgba(0,0,0,0.05);
}

.weather-icon {
  font-size: 2.5rem;
}

.weather-info {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.weather-temp {
  font-size: 1.4rem;
  font-weight: 800;
  color: #2c3e50;
}

.unit {
  font-size: 0.9rem;
  font-weight: 400;
  color: #7f8c8d;
}

.weather-desc {
  font-size: 0.9rem;
  font-weight: 600;
  color: #34495e;
  margin-bottom: 0.2rem;
}

.weather-wind {
  font-size: 0.8rem;
  color: #7f8c8d;
}

</style>