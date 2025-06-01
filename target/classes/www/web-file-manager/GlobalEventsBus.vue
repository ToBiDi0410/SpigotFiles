<script>
export default {
    lastUpdates: {},
    modifyLocks: [],
    busCache: {},
    eventListeners: [],
    on: function(name, callback) {
        this.eventListeners.push({ name: name, callback: callback });
    },
    emit: function(name, value) {
        if(name != "VALUECHANGE") console.log("[BUS] New Event '" + name + "' with Payload", value);
        for(const eventListener of this.eventListeners) {
            if(eventListener) {
                try {
                    if(eventListener.name == name) {
                        eventListener.callback(value);
                    }
                } catch (err) {
                    console.warn("[BUS] Failed to emit Event '" + name + "' on EventListener:", eventListener);
                }
            }
        }
    },

    /* Values */
    onValue(name, callback, once) {
        var index = this.eventListeners.push({
            name: "VALUECHANGE",
            callback: (val) => {
                if(val.name == name) callback(val.value, val.old);
                if(once) delete this.eventListeners[index];
            }
        });
    },

    get: function(name) {
        const val = this.busCache[name];
        return val;
    },
    getText: function(name, leaveNull) {
        const val = this.get(name);
        if(val == null | val == undefined) return leaveNull ? '' : '?';
        if(typeof val == 'boolean') return val ? "Yes" : "No";
        if(val instanceof Array) return "Array (" + val.length + " items)";
        if(val instanceof Text || val instanceof String) return '"' + val + '"';
        if(val instanceof Object) return typeof val;
        return val;
    },
    set: function(name, value) {
        if(!this.canModify(name)) return;
        if(this.is(name, value)) return;
        const old = this.busCache[name];
        this.busCache[name] = value == undefined ? null : value;
        //console.log("[BUS] New Value for Property '" + name + "':", this.get(name));
        this.lastUpdates[name] = Date.now();
        this.emit("VALUECHANGE", { name: name, value: value, old: old });
    },
    remove: function(name) {
        this.set(name, null);
    },
    applyObject: function(rootkey, value) {
        for(const key of Object.keys(value)) this.set(rootkey + "." + key, value[key]);
    },
    getObject: function(rootkey) {
        var obj = {};
        for(const key of Object.keys(this.busCache)) {
            if(key.startsWith(rootkey)) {
                var subKey = key.replace(rootkey, "");
                if(typeof this.busCache[key] == 'object') obj[subKey] = this.getObject(key);
                else obj[subKey] = this.busCache[key];
            }
        }
        return obj;
    },

    /* Checks */
    has: function(name) {
        return this.busCache[name] !== undefined;
    },
    is: function(name, value) {
        return this.get(name) == value;
    },

    /* Operator */
    toggle: function(name) {
        this.set(name, !this.get(name))
    },
    sum: function(name, value) {
        this.set(name, this.get(name) + value);
    },

    /* Modification Toggles */
    disableModify(name) {
        console.log("[BUS] Disallowed Modification of Property: " + name);
        this.modifyLocks.push(name);
    },
    enableModify(name) {
        console.log("[BUS] Allowed Modification of Property: " + name);
        this.modifyLocks = this.modifyLocks.filter(a => a != name);
    },
    canModify(name) {
        return !this.modifyLocks.includes(name);
    },

    /* Last Updated */
    lastUpdated(name) {
        return this.lastUpdates[name] || -1;
    },
    lastUpdatesErrorSummary() {
        var keysChangedRecently = [];
        for(const key of Object.keys(this.lastUpdates)) {
            if(this.lastUpdated("pb.error") - this.lastUpdated(key) < 2500) keysChangedRecently.push(key);
        }
        return keysChangedRecently;
    },
    reset() {
        this.lastUpdates = {};
        this.modifyLocks = [];
        this.busCache = {};
        this.eventListeners = [];
    }
}
</script>