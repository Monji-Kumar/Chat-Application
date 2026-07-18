import { Client } from "@stomp/stompjs";

export function createSocketClient() {
  return new Client({
    brokerURL: `ws://${window.location.hostname}/ws`,
    reconnectDelay: 5000,
    debug: (message) => {
      console.log(message);
    }
  });
}