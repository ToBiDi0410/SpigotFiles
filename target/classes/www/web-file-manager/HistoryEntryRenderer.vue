<template>
    <div class="HistoryEntryRendererrelative py-2 flex flex-col" @click="expanded = !expanded">
        <div class="Basic flex flex-row items-center">
            <div class="w-1.5 h-8 rounded shadow" :class="getChipClass()"></div>
            <div class="pl-2 flex-grow align-middle h-fit font-semibold">{{ entry.TEXT }}</div>
        </div>

        <div class="Details pl-4" v-if="expanded">
            <div class="DetailField w-full flex flex-row align-middle" v-for="field of Object.entries(entry.FIELDS)" :key="field[0]">
                <div><Icon class="text-base">info</Icon><b>{{ field[0] }}</b>:</div>
                <div class="pl-2 flex-grow">{{ field[1] }}</div>
            </div>
        </div>
    </div>
</template>

<script>
import Icon from './Icon.vue';

export default {
    components: { Icon },
    data: () => ({ expanded: false }),
    props: ["entry"],
    methods: {
        getChipClass() {
            return {
                'bg-error/60': this.entry.COLOR == 'error',
                'bg-warning/60': this.entry.COLOR == 'warn',
                'bg-info/60': this.entry.COLOR == 'info',
                'bg-success/60': this.entry.COLOR == 'success',
                'bg-dark/60': this.entry.COLOR == 'dark',
                'bg-primary/60': this.entry.COLOR == 'primary',
                'bg-secondary/60': this.entry.COLOR == 'secondary',
            };
        }
    }
}
</script>