<template>
    <div class="ContextMenu absolute" :style="'left: ' + position.x + 'px; top: ' + position.y + 'px;'" @click.stop v-if="position">
        <div class="card bg-base-100 shadow-xl w-52">
            <div class="card-body relative" style="--padding-card: 1rem;">
                <slot></slot>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    props: ["position"]
}
</script>