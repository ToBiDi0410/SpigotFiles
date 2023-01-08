<template>
    <div class="FileEntryRenderer p-2 hover:bg-gray-100" :class="{ 'bg-red-200 hover:bg-red-300' : !data.exists }" v-if="data.exists" style="min-width: 32rem;">
        <div class="flex flex-row items-center">
            <div class="w-16"><Icon>{{ getIcon() }}</Icon></div>
            <div class="flex-grow">
                <div @click="this.$emit('nameClicked')" style="width: fit-content;">{{ displayName }}</div>
            </div>
            <div>{{ new Date(this.data.lastUpdated).toLocaleString() }}</div>
            <div class="pl-8 w-1/12">{{ window.humanFileSize(this.data.size) }}</div>
        </div>
    </div>
</template>

<script>
import Icon from './Icon.vue';
import FileTransactionRender from './FileTransactionRenderer.vue';

export default {
    components: { FileTransactionRender, Icon },
    emits: ["nameClicked"],
    props: ["data"],
    data: () => ({ displayName: "" }),
    created() {
        this.data.transactions = this.data.transactions.reverse();
        this.displayName = this.data.PATH.replace(/^.*[\\\/]/, '');
    },
    methods: {
        getIcon() {
            if(this.data.mimeType == "FOLDER") return "folder";
            if(this.data.mimeType == "application/json") return "data_object";
            if(this.data.mimeType == "application/java-archive") return "settings";
            if(this.data.mimeType == "application/octet-stream" && this.data.PATH.endsWith("yml")) return "notes";
            if(this.data.mimeType == "text/plain" && this.data.PATH.endsWith("yml")) return "notes";
            return 'draft';
        }
    }
}
</script>