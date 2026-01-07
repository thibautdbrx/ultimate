<script setup>

import { onMounted, ref} from "vue";
import {useRouter} from "vue-router";

const pubProduct = ref(null)
const loadingPub = ref(true)
const router = useRouter()

onMounted(() => {
  fetchPub();
})

async function fetchPub() {
  try {
    const res = await fetch('https://dummyjson.com/products/category/sunglasses');
    const data = await res.json();

    if (data.products && data.products.length > 0) {
      const randomIndex = Math.floor(Math.random() * data.products.length);
      pubProduct.value = data.products[randomIndex];
    }
    loadingPub.value = false;
  } catch (error) {
    console.error("Erreur chargement pub", error);
  }
}
function goto_pub(){
  router.push("/fausse_pub/");
}
</script>

<template>
  <div class="ad-widget" v-if="pubProduct" @click="goto_pub">
    <div class="ad-badge">Sponsor</div>

    <div class="ad-image-container">
      <img :src="pubProduct.thumbnail" :alt="pubProduct.title">
    </div>

    <div class="ad-content">
      <h4 class="ad-title">{{ pubProduct.title }}</h4>
      <p class="ad-desc">{{ pubProduct.description.substring(0, 50) }}...</p>

      <div class="ad-footer">
        <span class="ad-price">{{ pubProduct.price }} €</span>
        <a href="#" class="ad-btn">Voir l'offre</a>
      </div>
    </div>
  </div>

</template>

<style scoped>
.ad-widget {
  width: 300px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
  font-family: sans-serif;
  background: white;
  position: relative;
  box-shadow: 0 4px 6px rgba(0,0,0,0.05);
}

.ad-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  padding: 2px 6px;
  font-size: 0.65rem;
  border-radius: 4px;
  text-transform: uppercase;
}

.ad-image-container {
  width: 100%;
  height: 180px;
  background: #f4f4f4;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ad-image-container img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.ad-content {
  padding: 1rem;
  text-align: left;
}

.ad-title {
  margin: 0 0 0.5rem 0;
  font-size: 1rem;
  color: #333;
  font-weight: 700;
}

.ad-desc {
  font-size: 0.85rem;
  color: #666;
  margin-bottom: 1rem;
  line-height: 1.3;
}

.ad-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}

.ad-price {
  font-size: 1.2rem;
  font-weight: bold;
  color: #2c3e50;
}

.ad-btn {
  background-color: #ff9900;
  color: white;
  text-decoration: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  font-size: 0.9rem;
  font-weight: 500;
  transition: background 0.2s;
}

.ad-btn:hover {
  background-color: #e68a00;
}

</style>