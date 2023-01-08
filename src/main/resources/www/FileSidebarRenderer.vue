<template>
    <div class="FileSidebarRenderer p-2 slide">
        <div class="text-lg font-bold">File Information</div>
        
        <div class="text-sm"><Icon>folder</Icon> <a class="font-bold">Path:</a> {{ data.PATH }}</div>
        <div class="text-sm"><Icon>list</Icon> <a class="font-bold">Indexed:</a> {{ new Date(data.firstIndex).toLocaleString() }}</div>
        <div class="text-sm"><Icon>album</Icon> <a class="font-bold">Size:</a> {{ window.humanFileSizeLong(data.size) }}</div>
        <div class="text-sm"><Icon>search</Icon> <a class="font-bold">Type:</a> {{ data.mimeType }}</div>

        <div class="text-lg font-bold py-2">History</div>
        <FileTransactionRender v-for="transaction of data.transactions" v-bind:key="transaction.ID" :data="transaction"></FileTransactionRender>
    </div>
</template>

<script>
import FileTransactionRender from './FileTransactionRenderer.vue';
import Icon from './Icon.vue';

export default {
    props: ["data"],
    components: { FileTransactionRender, Icon }
}
</script>