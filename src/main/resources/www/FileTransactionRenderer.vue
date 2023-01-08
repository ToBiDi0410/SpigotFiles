<template>
    <div class="FileTransactionRenderer border-l-4 p-2 my-2 hover:bg-gray-200 text-sm" :class="getColorClass()" @click="expanded = !expanded">
        <div v-if="data.type == 'CREATE'">
            <Icon>add</Icon> The file was indexed for the first time
        </div>

        <div v-if="data.type == 'DELETE'">
            <Icon>delete</Icon> The file was deleted
        </div>

        <div v-if="data.type == 'RENAME'">
            <Icon>label</Icon> The file was renamed to '<i>{{ data.additionalData.split(";;;")[1] }}</i>'
        </div>

        <div v-if="data.type == 'MOVE'">
            <Icon>trending_flat</Icon> The file was moved
        </div>

        <div v-if="data.type == 'COPY_TO'">
            <Icon>file_copy</Icon> The file was copied to another file
        </div>

        <div v-if="data.type == 'COPY_FROM'">
            <Icon>content_paste</Icon> The file was overwritten with another file's content
        </div>

        <div v-if="data.type == 'ADD'">
            <Icon>upload</Icon> The file was uploaded
        </div>

        <div v-if="data.type == 'UPDATE'">
            <Icon>upload</Icon> The file was updated
        </div>

        <div class="expandedInfo px-2 pt-2" v-if="expanded" @click.stop>
            <div v-if="data.type == 'RENAME'">
                <Icon>history</Icon> <b>From:</b> <i>{{ data.additionalData.split(";;;")[0] }}</i>
            </div>

            <div v-if="data.type == 'MOVE'">
                <Icon>arrow_upward</Icon> <b>From:</b> <i>{{ data.additionalData.split(";;;")[0] }}</i><br>
                <Icon>arrow_downward</Icon> <b>To:</b> <i>{{ data.additionalData.split(";;;")[1] }}</i>
            </div>

            <div v-if="data.type == 'COPY_TO'">
                <Icon>arrow_downward</Icon> <b>Target:</b> <i>{{ data.additionalData.split(";;;")[0] }}</i>
            </div>

            <div v-if="data.type == 'COPY_FROM'">
                <Icon>draft</Icon> <b>Source:</b> <i>{{ data.additionalData.split(";;;")[0] }}</i>
            </div>

            <div v-if="username && username != '???'"><Icon>person</Icon> <b>User:</b> {{ username }}</div>
            <div><Icon>calendar_month</Icon> <b>Date:</b> {{ new Date(data.date).toLocaleString() }}</div>
        </div>
    </div>
</template>

<script>
import Icon from './Icon.vue';

export default {
    components: { Icon },
    props: ["data"],
    data: () => ({ expanded: false, username: "..." }),
    async created() {
        this.username = await this.getUser();
    },
    methods: {
        getColorClass() {
            const res = {
                'border-red-500' : this.data.type == 'DELETE',
                'border-green-500' : this.data.type == 'CREATE',
                'border-pink-400' : this.data.type == 'ADD',
                'border-yellow-500' : this.data.type == 'UPDATE',
                'border-yellow-400' : this.data.type == 'MOVE' || this.data.type == 'RENAME',
                'border-blue-400' : this.data.type == 'COPY_TO' || this.data.type == 'COPY_FROM',
            }
            return res;
        },
        async getUser() {
            if(!window.userCache) window.userCache = {};
            if(!window.userCache[this.data.user]) {
                const req = await fetch(window.apiURL + "username?id=" + encodeURIComponent(this.data.user), { cache: 'reload' });
                window.userCache[this.data.user] = await req.text();
            }

            return window.userCache[this.data.user]; 
        }
    },
    watch: {
        data: function(newVal, oldVal) {
            this.expanded = false;
        }
    }
}
</script>