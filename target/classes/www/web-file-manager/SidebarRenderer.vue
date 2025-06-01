<template>
    <div class="SidebarRenderer base-300 shadow-xl px-2 py-2 text-sm" style="width: 15%; min-width: 20rem; min-height: 100%; height: fit-content;" v-if="getFiles().length >= 1">
        <div class="font-bold">Quick Actions</div>
        <div class="SectionDivider divider"></div>
        <div class="SectionTitle">
            <Icon>info</Icon>
            <a class="font-bold pl-1">Info</a>
        </div>
        <div class="SectionContent" style="font-size: 0.9em">
            <div class="BigIcon text-center">
                <Icon style="font-size: 10em;" filled="true">draft</Icon>
            </div>

            <div class="font-bold">Properties</div>
            <div class="pl-2">
                Paths: 
                <li v-for="File of getFiles()" v-bind:key="File.PATH">{{ File.PATH }}</li>
            </div>
            
            <div class="pl-2" v-if="getFiles().length == 1 && firstFileMeta">
                Changed: {{ new Date(firstFileMeta.LAST_CHANGED).toLocaleString() }}
            </div>

            <div class="pl-2" v-if="getFiles().length == 1 && firstFileMeta">
                Size: {{ humanFileSizeLong(firstFileMeta.SIZE) }}
            </div>
        </div>

        <div class="SectionDivider divider"></div>
        <div class="SectionTitle" v-if="getFiles().length == 1 && firstFileMeta">
            <Icon>history</Icon>
            <a class="font-bold pl-1">History</a>
        </div>
        <div class="SectionContent" style="font-size: 0.9em" v-if="getFiles().length == 1 && firstFileMeta && firstFileMeta.HISTORY">
            <HistoryEntryRenderer v-for="entry of firstFileMeta.HISTORY" :key="entry.TIME" :entry="entry"></HistoryEntryRenderer>
        </div>
    </div>
</template>

<script>
import Icon from './Icon.vue';
import HistoryEntryRenderer from './HistoryEntryRenderer.vue';
import * as Utils from './Utils.js';

export default {
    components: { Icon, HistoryEntryRenderer },
    data: () => ({ firstFileMeta: null }),
    props: ["bus"],
    created() {
        this.bus.onValue("SELECTION.files", async() => {
            if(this.getFiles().length != 1) return;
            this.firstFileMeta = await this.getFiles()[0].getMeta();
        });
    },
    methods: {
        getFiles() {
            return this.bus.get('SELECTION.files');
        },
        humanFileSizeLong: Utils.humanFileSizeLong
    }
}
</script>