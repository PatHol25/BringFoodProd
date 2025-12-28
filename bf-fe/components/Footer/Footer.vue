<template>
  <footer class="footer">
    <div class="footer__content">
      <p>&copy; {{ new Date().getFullYear() }} BringFood. Makes your life simpler</p>
    </div>
  </footer>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
export default defineComponent({
  setup() { return {} }
})
</script>

<style scoped lang="scss">
@import "./Footer.scss";
</style>