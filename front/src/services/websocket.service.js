import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

export class WebSocketService {
    constructor(roomId, userId) {
        this.roomId = roomId;
        this.userId = userId;
        this.client = null;
        this.handlers = new Map();
        this.isConnecting = false;
        this.isConnected = false;
        this.subscription = null;
    }

    on(type, handler) {
        if (typeof handler !== 'function') {
            throw new Error('Handler must be a function');
        }
        this.handlers.set(type, handler);
    }

    off(type) {
        this.handlers.delete(type);
    }

    async connect() {
        if (this.isConnecting || this.isConnected) {
            console.log('Already connected or connecting');
            return;
        }

        this.isConnecting = true;

        return new Promise((resolve, reject) => {
            try {
                if (this.client) {
                    this.disconnect();
                }

                this.client = new Client({
                    webSocketFactory: () => new SockJS('http://localhost:8080/ws/signaling'),
                    connectHeaders: {
                        roomId: this.roomId,
                        userId: this.userId
                    },
                    debug: function (str) {
                        console.log('STOMP: ' + str);
                    },
                    reconnectDelay: 5000,
                    heartbeatIncoming: 25000,
                    heartbeatOutgoing: 25000,
                    connectionTimeout: 30000
                });

                let connectTimeoutId = setTimeout(() => {
                    if (!this.isConnected) {
                        this.disconnect();
                        reject(new Error('Connection timeout'));
                    }
                }, 30000);

                this.client.onConnect = () => {
                    clearTimeout(connectTimeoutId);

                    if (!this.client || this.isConnected) {
                        return;
                    }

                    console.log('WebSocket Connected');
                    this.isConnected = true;
                    this.isConnecting = false;

                    if (!this.subscription) {
                        this.subscription = this.client.subscribe(
                            `/topic/room/${this.roomId}`,
                            message => {
                                try {
                                    console.log('Received message:', message.body); // 수신된 메시지 내용 출력
                                    const payload = JSON.parse(message.body);
                                    console.log('Parsed payload:', payload); // 파싱된 페이로드 출력
                                    console.log('Message type:', payload.type); // 메시지 타입 출력

                                    const handler = this.handlers.get(payload.type);
                                    if (handler) {
                                        console.log('Handler found for type:', payload.type); // 핸들러 존재 여부 확인
                                        handler(payload);
                                    } else {
                                        console.log('No handler found for type:', payload.type); // 핸들러 없음
                                    }
                                } catch (error) {
                                    console.error('Message handling error:', error);
                                    console.error('Raw message:', message); // 원본 메시지 출력
                                }
                            },
                            { id: `sub-${this.roomId}` }
                        );

                        this.client.publish({
                            destination: `/app/join/${this.roomId}`,
                            headers: {
                                'content-type': 'application/json'
                            },
                            body: JSON.stringify({
                                type: 'join',
                                roomId: this.roomId,
                                userId: this.userId,
                                data: null
                            })
                        });
                    }

                    resolve();
                };

                this.client.onStompError = (frame) => {
                    clearTimeout(connectTimeoutId);
                    console.error('STOMP Error:', frame);
                    this.handleDisconnect();
                    reject(new Error(frame.body));
                };

                this.client.onWebSocketError = (error) => {
                    clearTimeout(connectTimeoutId);
                    console.error('WebSocket Error:', error);
                    this.handleDisconnect();
                    reject(error);
                };

                this.client.onDisconnect = () => {
                    console.log('STOMP Disconnected');
                    this.handleDisconnect();
                };

                this.client.activate();
            } catch (error) {
                this.handleDisconnect();
                reject(error);
            }
        });
    }

    handleDisconnect() {
        this.isConnected = false;
        this.isConnecting = false;

        if (this.subscription) {
            try {
                this.subscription.unsubscribe();
            } catch (error) {
                console.error('Error unsubscribing:', error);
            }
            this.subscription = null;
        }

        if (this.client) {
            try {
                this.client.deactivate();
            } catch (error) {
                console.error('Error during deactivation:', error);
            }
            this.client = null;
        }
    }

    disconnect() {
        if (this.subscription) {
            try {
                this.subscription.unsubscribe();
            } catch (error) {
                console.error('Error unsubscribing:', error);
            }
            this.subscription = null;
        }

        if (this.client?.connected) {
            try {
                this.client.publish({
                    destination: `/app/signal/${this.roomId}`,
                    headers: {
                        'content-type': 'application/json'
                    },
                    body: JSON.stringify({
                        type: 'leave',
                        roomId: this.roomId,
                        userId: this.userId,
                        data: null
                    })
                });

                this.client.deactivate();
            } catch (error) {
                console.error('Disconnect error:', error);
            }
        }
        this.client = null;
        this.handlers.clear();
        this.isConnecting = false;
        this.isConnected = false;
    }

    sendMessage(message) {
        if (this.client?.connected) {
            this.client.publish({
                destination: `/app/signal/${this.roomId}`,
                headers: {
                    'content-type': 'application/json'
                },
                body: JSON.stringify(message)
            });
        } else {
            console.warn('WebSocket is not connected');
        }
    }

    sendOffer(sdp) {
        this.sendMessage({
            type: 'offer',
            roomId: this.roomId,
            userId: this.userId,
            data: { sdp }
        });
    }

    sendAnswer(sdp) {
        this.sendMessage({
            type: 'answer',
            roomId: this.roomId,
            userId: this.userId,
            data: { sdp }
        });
    }

    sendIceCandidate(candidate) {
        this.sendMessage({
            type: 'ice-candidate',
            roomId: this.roomId,
            userId: this.userId,
            data: candidate
        });
    }

    leaveRoom() {
        this.sendMessage({
            type: 'leave',
            roomId: this.roomId,
            userId: this.userId,
            data: null
        });
    }
}