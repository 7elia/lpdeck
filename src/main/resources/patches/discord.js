class ReconnectableWebSocket {
    constructor() {
        this.url = "ws://127.0.0.1:7542";
        this.retryInterval = 5000;
        this.reconnectLoopId = -1;
        this.socket = null;

        this.prevVoiceState = null;

        this.ApplicationStreamingSettingsStore = findStore("ApplicationStreamingSettingsStore");
        this.StreamRTCConnectionStore = findStore("StreamRTCConnectionStore");
        this.StreamerModeStore = findStore("StreamerModeStore");
        this.MediaEngineStore = findStore("MediaEngineStore");
        this.VoiceStateStore = findStore("VoiceStateStore");
        this.ChannelStore = findStore("ChannelStore");

        FluxDispatcher.subscribe("VOICE_STATE_UPDATES", async e => {
            for (const state of e.voiceStates) {
                if (state.userId === meId) {
                    await this.persistVoiceState(state);
                    this.prevVoiceState = state;
                    await this.sendAppData();
                    return;
                }
            }
        });
        FluxDispatcher.subscribe("AUDIO_SET_NOISE_CANCELLATION", () => this.sendAppData());
        FluxDispatcher.subscribe("AUDIO_SET_NOISE_SUPPRESSION", () => this.sendAppData());
        FluxDispatcher.subscribe("STREAMER_MODE_UPDATE", () => this.sendAppData());
    }

    connect() {
        console.log(`Attempting to connect to ${this.url}`);
        this.socket = new WebSocket(this.url);

        this.socket.onopen = () => {
            console.log("WebSocket connection established.");
            this.socket.send(JSON.stringify({
                target: "discord",
                type: "target"
            }));
            this.sendAppData();
            if (this.reconnectLoopId !== -1) {
                clearInterval(this.reconnectLoopId);
                this.reconnectLoopId = -1;
            }
        };
        this.socket.onmessage = msg => {
            this.handleCommand(msg.data.toLowerCase());
        };
        this.socket.onclose = () => {
            console.warn("WebSocket closed. Reconnecting...");
            if (this.reconnectLoopId === -1) {
                this.reconnectLoopId = setInterval(() => this.connect(), this.retryInterval);
            }
        };
        this.socket.onerror = () => {
            console.error("WebSocket error. Reconnecting...");
            this.socket?.close();
        };
    }

    async persistVoiceState(newState) {
        if (!this.prevVoiceState) {
            return;
        }
        if (!newState.channelId || newState.channelId === this.prevVoiceState.channelId) {
            return;
        }
        if (!newState.guildId || newState.guildId !== this.prevVoiceState.guildId) {
            return;
        }

        if (this.prevVoiceState.selfStream && !newState.selfStream) {
            this.toggleScreenshare();
        }
        if (this.prevVoiceState.selfVideo && !newState.selfVideo) {
            this.toggleCamera();
        }
    }

    async toggleScreenshare() {
        const voiceState = this.VoiceStateStore.getVoiceStateForUser(meId);

        if (!voiceState) {
            return;
        }
        
        if (voiceState.selfStream) {
            for (const streamKey of this.StreamRTCConnectionStore.getAllActiveStreamKeys()) {
                if (!streamKey.includes(meId)) {
                    continue;
                }

                FluxDispatcher.dispatch({
                    type: "STREAM_CLOSE",
                    canShowFeedback: true,
                    streamKey
                });

                FluxDispatcher.dispatch({
                    type: "STREAM_STOP",
                    streamKey,
                    appContext: "app"
                });

                FluxDispatcher.dispatch({
                    type: "STREAM_DELETE",
                    reason: "user_requested",
                    streamKey,
                    unavailable: undefined
                });
            }
            return;
        }

        const connectedChannel = this.ChannelStore.getChannel(voiceState.channelId);
        const channelType = connectedChannel.type;

        const source = (await this.MediaEngineStore
            .getMediaEngine()
            .getScreenPreviews(176, 99))
            .filter(v => v.name === "Screen 1")[0].id;
        const streamOptions = this.ApplicationStreamingSettingsStore.getState();

        FluxDispatcher.dispatch({
            type: "MEDIA_ENGINE_SET_GO_LIVE_SOURCE",
            settings: {
                desktopSettings: {
                    sourceId: source,
                    sound: streamOptions.soundshareEnabled
                },
                qualityOptions: {
                    preset: streamOptions.preset,
                    resolution: streamOptions.resolution,
                    frameRate: streamOptions.fps
                },
                context: "stream"
            }
        });

        FluxDispatcher.dispatch({
            type: "STREAM_START",
            streamType: channelType === 2 ? "guild" : "call",
            guildId: channelType === 2 ? connectedChannel.guild_id : null,
            channelId: voiceState.channelId,
            appContext: "app",
            audioSourceId: null,
            pid: null,
            previewDisabled: false,
            sound: true,
            sourceId: source,
            sourceName: "Screen 1"
        });
    }

    async toggleCamera() {
        const voiceState = this.VoiceStateStore.getVoiceStateForUser(meId);

        if (!voiceState) {
            return;
        }
        
        if (!voiceState.selfVideo)  {
            let camera;
            const devices = this.MediaEngineStore.getVideoDevices();
            for (const key in devices) {
                const value = devices[key];
                if (value.index === 0) {
                    camera = value;
                }
            }
            if (!camera) {
                return;
            }

            FluxDispatcher.dispatch({
                type: "MEDIA_ENGINE_SET_VIDEO_DEVICE",
                id: camera.id
            });
        }
        
        FluxDispatcher.dispatch({
            type: "MEDIA_ENGINE_SET_VIDEO_ENABLED",
            enabled: !voiceState.selfVideo
        });
    }

    async handleCommand(command) {
        console.log("Got command: " + command);

        switch (command) {
            case "disconnect":
                findByProps("selectVoiceChannel", "selectChannel").disconnect();
                break;
            case "toggle_deafen":
                FluxDispatcher.dispatch({
                    type: "AUDIO_TOGGLE_SELF_DEAF",
                    context: "default",
                    syncRemote: true
                });
                break;
            case "toggle_krisp":
                const modeOptions = this.MediaEngineStore.getModeOptions();

                FluxDispatcher.dispatch({
                    type: "AUDIO_SET_NOISE_CANCELLATION",
                    enabled: !modeOptions.vadUseKrisp,
                    location: { section: "Noise Cancellation Popout" }
                });
                FluxDispatcher.dispatch({
                    type: "AUDIO_SET_NOISE_SUPPRESSION",
                    enabled: modeOptions.vadUseKrisp,
                    location: { section: "Noise Cancellation Popout" }
                });

                FluxDispatcher.dispatch({
                    type: "AUDIO_SET_MODE",
                    context: "default",
                    mode: this.MediaEngineStore.getMode(),
                    options: {
                        ...modeOptions,
                        vadUseKrisp: !modeOptions.vadUseKrisp
                    }
                });

                break;
            case "toggle_screenshare":
                await this.toggleScreenshare();
                break;
            case "toggle_camera":
                await this.toggleCamera();
                break;
            case "toggle_streamer_mode":
                FluxDispatcher.dispatch({
                    type: "STREAMER_MODE_UPDATE",
                    key: "enabled",
                    value: !this.StreamerModeStore.enabled
                });
                break;
            case "restart":
                (window.VesktopNative ? VesktopNative : DiscordNative).app.relaunch();
                break;
        }

        await this.sendAppData();
    }

    async sendAppData() {
        if (!this.socket || this.socket?.readyState !== WebSocket.OPEN) {
            return;
        }
        const voiceState = this.VoiceStateStore.getVoiceStateForUser(meId);
        const modeOptions = this.MediaEngineStore.getModeOptions();
        this.socket?.send(JSON.stringify({
            target: "discord",
            type: "sync",
            data: {
                connected: !!voiceState,
                deafened: !voiceState ? false : voiceState.selfDeaf,
                krisp: modeOptions.vadUseKrisp,
                screensharing: !voiceState ? false : voiceState.selfStream,
                camera: !voiceState ? false : voiceState.selfVideo,
                streamer_mode: this.StreamerModeStore.enabled
            }
        }));
    }
}

setTimeout(() => {
    Vencord.Plugins.plugins.ConsoleShortcuts.start();
    new ReconnectableWebSocket().connect();
}, 5000);
