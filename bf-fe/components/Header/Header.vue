<template>
  <header class="header">
    <nav class="header__nav">
      <div class="nav__content">
        <NuxtLink to="/" class="nav__content-logo">
          <span class="logo-icon">🍎</span> BringFood
        </NuxtLink>
        <div class="nav__content-links">
          <NuxtLink to="/" class="links__link">Home</NuxtLink>
          <NuxtLink to="/bring/bring" class="links__link">Bring Food</NuxtLink>
          <NuxtLink to="/take/take" class="links__link">Take Food</NuxtLink>
        </div>
      </div>
      <div class="header__actions">
        <button class="header__user-button" @click="onUserClick">
          <span class="header__user-icon">👤</span>
        </button>
      </div>
    </nav>
  </header>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import { useAuthModal } from '~/composables/useAuthModal'

export default defineComponent({
  setup() {
    const { openModal } = useAuthModal()
    
    const onUserClick = () => {
      openModal()
    }
    
    return { onUserClick }
  }
})
</script>

<style scoped lang="scss">
@import "./Header.scss";
</style>
