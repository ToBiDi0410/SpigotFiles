<template>
    <tr class="FileRenderer" :class="{ 'bg-accent/20' : isSelected(), 'hover:bg-base-200': !isSelected() }" @mouseenter="isHovering = true" @mouseleave="isHovering = false" @click.left="handleClick(false)"  @click.right="handleClick(true)" @contextmenu="handleClick" draggable="true" @drop="handleDrop" @dragend="handleDragEnd" @dragover="handleDragOver" @dragstart="handleDragStart" v-if="FileMeta && FileMeta.VISIBILITY == true">
        <td class="FileSelectedCheckbox px-2 py-2 w-9 h-9" :class="{ 'opacity-0' : !isHovering }" @click.stop>
            <input type="checkbox" class="checkbox checkbox-sm" :disabled="!isHovering" :value="isSelected()" :checked="isSelected()" @click="toggleChecked" />
        </td>

        <td class="FileIcon px-2">
            <Icon>{{ dertermineIcon() }}</Icon>
        </td>

        <td class="FileName px-2">
            <div>{{ File.NAME }}</div>
        </td>

        <td class="FileSize px-2">
            <div>{{ humanFileSize(FileMeta.SIZE) }}</div>
        </td>

        <td class="FileChanged px-2">
            <div>{{ new Date(FileMeta.LAST_CHANGED).toLocaleString() }}</div>
        </td>
    </tr>
</template>

<script>
import Icon from './Icon.vue';
import * as Utils from './Utils.js';

export default {
    components: { Icon },
    props: ["File", "bus"],
    data: () => ({ FileMeta: null, isHovering: false, isFocused: false, isDragged: false }),
    async created() {
        this.FileMeta = await this.File.getMeta();
        //console.log(this.FileMeta);
        //console.log(this.File.PATH, this.FileMeta.VISIBILITY);
    },
    methods: {
        dertermineIcon() {
            if(this.File.DIRECTORY) {
                return "folder";
            }

            return "draft";
        },

        isSelected() {
            return this.bus.get("SELECTION.files").includes(this.File);
        },


        toggleChecked() {
            if(!this.isSelected()) this.bus.set("SELECTION.files", [...this.bus.get("SELECTION.files"), this.File])
            else this.bus.set("SELECTION.files", this.bus.get("SELECTION.files").filter(a => a != this.File));
        },

        handleClick(right) {
            if(right) return;
            if(this.bus.get("SELECTION.files").length > 1) {
                this.toggleChecked();
                return;
            }

            if(this.isSelected()) this.bus.emit("DISPLAYED.navigate", this.File);
            else this.bus.set("SELECTION.files", [this.File]);
        },

        //Dragging
        handleDragStart(e) {
            this.bus.set("DRAGDROP.dragging", true);
            this.bus.set("DRAGDROP.isPartOfSelection", this.isSelected());
            this.bus.set("DRAGDROP.origin", this.File);
        },

        handleDragEnd() {
            this.bus.set("DRAGDROP.dragging", false);
        },

        handleDrop(e) {
            if(!this.isDropTarget()) return;
            this.bus.set("DRAGDROP.target", this.File);
            this.bus.emit("DRAGDROP.dropped");
        },

        handleDragOver(e) {
            if(!this.isDropTarget()) return;
            e.preventDefault();
        },

        isDropTarget() {
            if(this.bus.get("DRAGDROP.origin") == this.File) return false;
            if(!this.File.DIRECTORY) return false;
            return true;
        },

        humanFileSize: Utils.humanFileSize
    }
}
</script>

<style>
.FileRenderer {
    border-bottom: 1px solid hsl(var(--b2));
}
</style>