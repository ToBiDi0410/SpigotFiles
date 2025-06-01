<template>
    <div class="Main h-full w-full bg-white text-black">
        <FileManager v-if="user" :FileBackend="backend" :debug="false"></FileManager>
        <Login v-if="!user" @loginCompleted="updateCurrentUser"></Login>
    </div>
</template>

<script>
import FileManager from './web-file-manager/FileManager.vue';
import Login from './Login.vue';
import { SpigotFilesBackend } from './SpigotFilesBackend.mjs';

export default {
    components: { FileManager, Login },
    data: () => ({ user: null, backend: null }),
    async created() {
        this.backend = new SpigotFilesBackend();
        await this.updateCurrentUser();
    },
    methods: {
        async updateCurrentUser() {
            const loginReq = await fetch(window.apiURL + "loginstate", { credentials: 'same-origin' });
            if(loginReq.status == 200) {
                const loginRes = await loginReq.text();
                this.user = { name: loginRes.split(";")[2], id: loginRes.split(";")[1], permissions: loginRes.split(";")[3].split(",") };
                window.user = this.user;
            }
        }
    }
}
</script>