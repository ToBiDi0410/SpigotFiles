<template>
    <div class="Icon inline align-middle pr-1 select-none" :class="{ 'material-symbols-outlined' : !filled, 'material-symbols-outlined material-symbols-outlined-filled' : filled }">
        <slot></slot>
    </div>
</template>

<script>
export default {
    props: ["filled"]
}
</script>

<style>
.material-symbols-outlined {
  font-variation-settings:
  'FILL' 0,
  'wght' 400,
  'GRAD' 0,
  'opsz' 48
}

.material-symbols-outlined-filled {
  font-variation-settings:
  'FILL' 1,
  'wght' 400,
  'GRAD' 0,
  'opsz' 48
}
</style>