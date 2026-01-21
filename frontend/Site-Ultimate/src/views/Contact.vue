<script setup>
import ContactCard from '@/components/card/Contact_Card.vue'
import MailIcon from '@/assets/icons/email.svg'

const contacts = [
  { name: "Loup Gicquel", email: "e45497u@etu.univ-lorraine.fr", role:"Frontend"},
  { name: "Thibaut Debreux", email: "thibaut.debreux7@etu.univ-lorraine.fr", role:"Backend" },
  { name: "Florent Becker", email: "becker367u@etu.univ-lorraine.fr", role:"Backend" },
  { name: "Corentin Caudron", email: "corentin.caudron1@etu.univ-lorraine.fr", role:"Backend" },
  { name: "Valentine Thirion", email: "valentine.thirion6@etu.univ-lorraine.fr", role:"Backend" },
  { name: "Simeon Joseph", email: "simeon.joseph-schenfeigel5@etu.univ-lorraine.fr", role:"Frontend" },
]
</script>

<template>
  <main class="Contact">
    <div class="contact_section">
      <h2 class="titre_contact">Nos contacts</h2>
        <div class="cards_info">
          <ContactCard
              v-for="c in contacts"
              :key="c.email"
              :name="c.name"
              :email="c.email"
              :icon="MailIcon"
              color1="#dbeafe"
              color2="#155dfc"
              :role="c.role"
          />
        </div>
    </div>

    <div class="contact_section">
      <h2 class="titre_contact">Notre localisation</h2>
      <div class="map-container">
        <iframe
            src="https://www.google.com/maps?q=Polytech+Nancy&t=&z=15&ie=UTF8&iwloc=&output=embed"
            width="100%"
            height="400"
            style="border:0;"
            loading="lazy">
        </iframe>

      </div>
    </div>
  </main>
</template>

<style scoped>
.titre_contact {
  text-align: center;
  font-size: 2rem;
  margin-bottom: 2rem;
}

.contact_section {
  display: flex;
  flex-direction: column;
  margin-bottom: 3rem;
}

.cards_info {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 2rem;
}

.map-container {
  width: 100%;
  display: flex;
  justify-content: center;
}

.Contact {
  padding: 2rem;
}
</style>
