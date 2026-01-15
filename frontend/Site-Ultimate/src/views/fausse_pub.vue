<script setup>
import { RouterLink } from "vue-router";
import chien_prankeur from "@/assets/img/chien_prankeur.png";
import { onMounted, onUnmounted, ref, reactive } from "vue";
import chien_prankeur_audio from "@/assets/sound/chien_prankeur.mp3";
import confetti from 'canvas-confetti';
import PUB from "@/components/PUB.vue";

let audio = null;
let confettiInterval = null;
let myConfetti = null;

const floatingDogs = ref([]);
const staticPubs = ref([]); // Renommé pour la clarté
const mainContainerRef = ref(null);
const confettiRef = ref(null);

const buttonPos = reactive({ top: 'auto', left: 'auto', position: 'static' });

// Génération des chiens (inchangé)
const generateDogs = () => {
  for (let i = 0; i < 20; i++) {
    floatingDogs.value.push({
      id: i,
      left: Math.random() * 100 + 'vw',
      top: Math.random() * 120 + 'vh',
      size: Math.random() * 150 + 50 + 'px',
      duration: Math.random() * 8 + 3 + 's',
      delay: Math.random() * 5 + 's',
      rotationDir: Math.random() > 0.5 ? 1 : -1
    });
  }
};

// SIMPLIFICATION ICI : Juste 10 pubs posées aléatoirement
const generatePubs = () => {
  staticPubs.value = [];
  for (let i = 0; i < 10; i++) {
    staticPubs.value.push({
      id: i,
      // Position simple
      left: Math.random() * 90 + 'vw',
      top: Math.random() * 90 + 'vh',
      // Une petite rotation fixe pour le style
      rotation: (Math.random() * 40 - 20) + 'deg'
    });
  }
};

const moveButton = () => {
  if (!mainContainerRef.value) return;
  const containerRect = mainContainerRef.value.getBoundingClientRect();
  const newTop = Math.random() * (containerRect.height - 100);
  const newLeft = Math.random() * (containerRect.width - 250);

  buttonPos.position = 'absolute';
  buttonPos.top = `${newTop}px`;
  buttonPos.left = `${newLeft}px`;
};

const streamConfetti = () => {
  if (!myConfetti) return;
  const duration = 15 * 1000;
  const animationEnd = Date.now() + duration;
  let skew = 1;

  function randomInRange(min, max) { return Math.random() * (max - min) + min; }

  (function frame() {
    const timeLeft = animationEnd - Date.now();
    const ticks = Math.max(200, 500 * (timeLeft / duration));
    skew = Math.max(0.8, skew - 0.001);

    myConfetti({
      particleCount: 3,
      startVelocity: 0,
      ticks: ticks,
      origin: { x: Math.random(), y: (Math.random() * skew) - 0.2 },
      colors: ['#ff4757', '#2ed573', '#1e90ff', '#ffa502'],
      shapes: ['circle', 'square'],
      gravity: randomInRange(0.4, 0.8),
      scalar: randomInRange(0.8, 1.5),
      drift: randomInRange(-0.4, 0.4)
    });

    if (timeLeft > 0) requestAnimationFrame(frame);
  }());
};


onMounted(() => {
  audio = new Audio(chien_prankeur_audio);
  audio.volume = 0.4;
  audio.loop = true;
  audio.play().catch(() => {});

  if (confettiRef.value) {
    myConfetti = confetti.create(confettiRef.value, { resize: true, useWorker: true });
  }

  generateDogs();
  generatePubs();
  streamConfetti();
  confettiInterval = setInterval(streamConfetti, 10000);
});

onUnmounted(() => {
  if (audio) { audio.pause(); audio.currentTime = 0; }
  if (confettiInterval) clearInterval(confettiInterval);
  if (myConfetti) myConfetti.reset();
});
</script>

<template>
  <div class="prank-container">

    <div
        v-for="dog in floatingDogs"
        :key="'dog-'+dog.id"
        class="floating-dog"
        :style="{
          left: dog.left,
          top: dog.top,
          width: dog.size,
          animationDuration: dog.duration,
          animationDelay: dog.delay,
          '--rot-dir': dog.rotationDir
        }"
    >
      <img :src="chien_prankeur" alt="dog floating">
    </div>

    <div
        v-for="pub in staticPubs"
        :key="'pub-'+pub.id"
        class="background-pub"
        :style="{
          left: pub.left,
          top: pub.top,
          transform: `rotate(${pub.rotation})`
        }"
    >
      <PUB />
    </div>


    <main ref="mainContainerRef">
      <div class="emoji spinning">
        <img :src="chien_prankeur">
      </div>
      <h1>FAUSSE PUB</h1>
      <p>On t'a bien eu ! 🤡</p>

      <RouterLink
          to="/"
          class="btn-retour runaway-btn"
          :style="buttonPos"
          @mouseover="moveButton"
      >
        Retourner à la sécurité
      </RouterLink>
    </main>
  </div>
  <canvas ref="confettiRef" class="confetti-overlay"></canvas>
</template>

<style scoped>
.confetti-overlay {
  position: fixed;
  top: 0; left: 0; width: 100%; height: 100%;
  pointer-events: none;
  z-index: 10000;
}

.prank-container {
  position: fixed;
  top: 0; left: 0; width: 100%; height: 100%;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
  z-index: 9999;
}

main {
  position: relative;
  z-index: 10;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 50px;
}

.emoji.spinning {
  font-size: 6rem;
  animation: spin 20s linear infinite;
}

.emoji img { max-width: 100%; height: auto; }

h1 {
  font-size: 8rem;
  font-weight: 1000;
  white-space: nowrap;
  color: #ff4757;
  margin: 0;
  letter-spacing: 10px;
}

p {
  font-size: 2rem;
  font-weight: bold;
  color: #333;
  margin-top: 1rem;
  margin-bottom: 4rem;
}

.btn-retour {
  background-color: #ff4757;
  color: white;
  font-size: 1.5rem;
  font-weight: bold;
  padding: 1rem 2.5rem;
  border: none;
  border-radius: 50px;
  text-decoration: none;
  box-shadow: 0 5px 15px rgba(255, 71, 87, 0.4);
  white-space: nowrap;
  transition: all 0.3s ease;
}
.btn-retour:hover {
  background-color: #e84118;
  box-shadow: 0 8px 25px rgba(255, 71, 87, 0.6);
}

.floating-dog {
  position: absolute;
  opacity: 0.4;
  pointer-events: none;
  animation: floatUp linear infinite;
  z-index: 1;
}
.floating-dog img { width: 100%; height: auto; animation: spin 4s linear infinite; }

.background-pub {
  position: absolute;
  z-index: 2;
  opacity: 0.9;
  pointer-events: none;
}




@keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }

@keyframes floatUp {
  0% { transform: translateY(100vh) scale(0.5) rotate(0deg); opacity: 0;}
  20% { opacity: 0.6;}
  80% { opacity: 0.6;}
  100% { transform: translateY(-100vh) scale(1.2) rotate(360deg); opacity: 0; }
}
</style>