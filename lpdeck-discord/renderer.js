class ReconnectableWebSocket {
    constructor() {
        this.url = "ws://127.0.0.1:7542";
        this.retryInterval = 5000;
        this.reconnectLoopId = -1;
        this.socket = null;
    }

    connect() {
        console.log(`Attempting to connect to ${this.url}`);
        this.socket = new WebSocket(this.url);

        this.socket.onopen = () => {
            console.log("WebSocket connection established.");
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

    handleCommand(command) {
        console.log("Got command: " + command);

        // switch (command) {
        // }

        this.sendAppData();
    }

    sendAppData() {
        if (!this.socket || this.socket?.readyState !== WebSocket.OPEN) {
            return;
        }
        this.socket?.send(JSON.stringify({
            
        }));
    }
}

Vencord.Plugins.plugins.ConsoleShortcuts.start();

new ReconnectableWebSocket().connect();
