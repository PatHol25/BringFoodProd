<template>
  <div class="main">
    <section class="main__hero">
      <h1>Take food with you and get payed</h1>
      <p>Connect with your community to help them out and get extra money for it</p>
      <div class="hero__actions">
        <NuxtLink to="/bring/bring" class="actions-btn btn-primary">I can take food with me</NuxtLink>
        <NuxtLink to="/take/take" class="actions-btn btn-secondary">Bring me food with you</NuxtLink>
      </div>
    </section>

    <section class="main__features">
      <div class="feature-card">
        <h3>Shopper 1</h3>
        <p>Before I go to a Store i always look in the app and accept a shopping list, it is really convinient and is nice additional money</p>
      </div>
      <div class="feature-card">
        <h3>Costumer 1</h3>
        <p>It is so simple and saves ma a lot of time, always worth the little extra costs</p>
      </div>
      <div class="feature-card">
        <h3>Shopper 2</h3>
        <p>Sometimes i dont't even need anything from the shops, I see it more as a side hustle to earn extra cash!</p>
      </div>
    </section>
  </div>
</template>

<script lang="ts">
//TODO: make button component with these styles
import {defineComponent} from 'vue'

export default defineComponent({
  setup() {


    return {}
  }
})
</script>

<style scoped>
@import "styles/main.scss";
</style>