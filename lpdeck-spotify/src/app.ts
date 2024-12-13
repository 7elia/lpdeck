class ReconnectableWebSocket {
    readonly url: string;
    readonly retryInterval: number;
    private reconnectLoopId: number;
    socket: WebSocket | null;

    constructor() {
        this.url = "ws://127.0.0.1:7542";
        this.retryInterval = 5000;
        this.reconnectLoopId = -1;
        this.socket = null;

        Spicetify.Player.addEventListener("onplaypause", () => this.sendPlayerData());
        Spicetify.Player.addEventListener("songchange", () => this.sendPlayerData());
        Spicetify.Player.addEventListener("onprogress", () => this.sendPlayerData());

        setInterval(() => {
            if (!Spicetify.Player.isPlaying()) {
                this.sendPlayerData();
            }
        }, 1000);
    }

    connect() {
        console.log(`Attempting to connect to ${this.url}`);
        this.socket = new WebSocket(this.url);

        this.socket.onopen = () => {
            console.log("WebSocket connection established.");
            this.socket?.send(JSON.stringify({
                target: "spotify",
                type: "target"
            }));
            this.sendPlayerData();
            if (this.reconnectLoopId !== -1) {
                clearInterval(this.reconnectLoopId);
                this.reconnectLoopId = -1;
            }
        };
        this.socket.onmessage = (msg: MessageEvent<string>) => {
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

    handleCommand(command: string) {
        console.log("Got command: " + command);

        switch (command) {
            case "toggle_play":
                Spicetify.Player.togglePlay();
                break;
            case "previous":
                if (Spicetify.Player.getProgress() > 5 * 1000) {
                    Spicetify.Player.seek(0);
                } else {
                    Spicetify.Player.back();
                }
                break;
            case "next":
                Spicetify.Player.next();
                break;
            case "toggle_shuffle":
                Spicetify.Player.toggleShuffle();
                break;
            case "toggle_repeat":
                Spicetify.Player.setRepeat(Spicetify.Player.getRepeat() === 2 ? 1 : 2);
                break;
        }

        this.sendPlayerData();
    }

    sendPlayerData() {
        if (!this.socket || this.socket?.readyState !== WebSocket.OPEN) {
            return;
        }
        this.socket?.send(JSON.stringify({
            target: "spotify",
            type: "sync",
            data: {
                playing: Spicetify.Player.isPlaying(),
                shuffle: Spicetify.Player.getShuffle(),
                repeat: Spicetify.Player.getRepeat() === 2
            }
        }));
    }
}

async function main() {
    new ReconnectableWebSocket().connect();
}

export default main;
