<template>
  <Teleport to="body">
    <div v-if="isOpen" class="modal" @click.self="closeModal">
      <div class="modal__container">
        <button class="modal__close" @click="closeModal">&times;</button>

        <div class="modal__tabs">
          <button
              :class="['modal__tab', { 'modal__tab--active': isLogin }]"
              @click="isLogin = true"
          >
            Login
          </button>
          <button
              :class="['modal__tab', { 'modal__tab--active': !isLogin }]"
              @click="isLogin = false"
          >
            Register
          </button>
        </div>

        <form class="modal__form" @submit.prevent="handleSubmit">
          <div v-if="!isLogin" class="modal__field">
            <label class="modal__label">Username</label>
            <input v-model="form.username" type="text" class="modal__input" required />
          </div>

          <div class="modal__field">
            <label class="modal__label">Email</label>
            <input v-model="form.email" type="email" class="modal__input" required />
          </div>

          <div class="modal__field">
            <label class="modal__label">Password</label>
            <input v-model="form.password" type="password" class="modal__input" required />
          </div>

          <div v-if="!isLogin" class="modal__address-section">
            <h3 class="modal__sub-title">Address</h3>
            <div class="modal__row">
              <div class="modal__field">
                <label class="modal__label">Street</label>
                <input v-model="form.address.street" type="text" class="modal__input" required />
              </div>
              <div class="modal__field modal__field--short">
                <label class="modal__label">Nr.</label>
                <input v-model="form.address.houseNumber" type="text" class="modal__input" required />
              </div>
            </div>
            <div class="modal__row">
              <div class="modal__field modal__field--short">
                <label class="modal__label">ZIP</label>
                <input v-model="form.address.zip" type="text" class="modal__input" required />
              </div>
              <div class="modal__field">
                <label class="modal__label">City</label>
                <input v-model="form.address.city" type="text" class="modal__input" required />
              </div>
            </div>
          </div>

          <button type="submit" class="modal__submit">
            {{ isLogin ? 'Login' : 'Create Account' }}
          </button>
        </form>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useAuthModal } from '~/composables/useAuthModal'

const { isOpen, closeModal } = useAuthModal()
const isLogin = ref(true)

const form = reactive({
  username: '',
  email: '',
  password: '',
  address: { zip: '', city: '', street: '', houseNumber: '' }
})

async function handleSubmit () {
  if(isLogin.value ) {
    const todo = await $fetch('http://10.19.21.89:8080/api/auth/login', {
      method: 'POST',
      body: {
        form,
      },
    })
    console.log(todo);
  } else {
    const todo = await $fetch('http://10.19.21.89:8080/api/auth/login', {
      method: 'POST',
      body: {
        form,
      },
    })
    console.log(todo);
  }
}

</script>

<style lang="scss" scoped>
@import "./AuthModal.scss";
</style>