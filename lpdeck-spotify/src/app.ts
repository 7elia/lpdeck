class ReconnectableWebSocket {
    url: string;
    retryInterval: number;
    socket: WebSocket | null;

    constructor(url: string, retryInterval: number) {
        this.url = url;
        this.retryInterval = retryInterval;
        this.socket = null;
    }

    connect() {
        console.log(`Attempting to connect to ${this.url}`);
        this.socket = new WebSocket(this.url);

        this.socket.onopen = () => {
            console.log("WebSocket connection established.");
        };
        this.socket.onmessage = (msg: MessageEvent<string>) => {
            this.handleCommand(msg.data.toLowerCase());
        };
        this.socket.onclose = () => {
            console.warn("WebSocket closed. Reconnecting...");
            this.scheduleReconnect();
        };
        this.socket.onerror = () => {
            console.error("WebSocket error. Reconnecting...");
            this.socket?.close();
        };
    }

    scheduleReconnect() {
        setTimeout(() => this.connect(), this.retryInterval);
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
        }
    }
}

async function main() {
    const socket = new ReconnectableWebSocket("ws://127.0.0.1:7542", 5000);
    socket.connect();
}

export default main;
