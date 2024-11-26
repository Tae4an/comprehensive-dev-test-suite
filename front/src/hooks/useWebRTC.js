import { useState, useEffect, useCallback, useRef } from 'react';
import { WebSocketService } from '../services/websocket.service';
import {
    getLocalStream,
    createPeerConnection,
    addStreamToPeerConnection,
    createOffer,
    createAnswer
} from '../utils/webrtc.utils';

export const useWebRTC = (roomId, userId) => {
    const [localStream, setLocalStream] = useState(null);
    const [remoteStreams, setRemoteStreams] = useState(new Map());
    const [isConnected, setIsConnected] = useState(false);
    const [error, setError] = useState(null);

    const mountedRef = useRef(true);
    const peerConnections = useRef(new Map());
    const wsService = useRef(null);
    const initialized = useRef(false);
    const pendingCandidates = useRef(new Map()); // ICE candidate 임시 저장소 추가


    const configuration = {
        iceServers: [{
            urls: 'turn:210.119.34.236:3478',
            username: 'testuser',
            credential: 'testpassword123'
        }]
    };

    const initializePeerConnection = useCallback(async (remoteUserId) => {
        try {
            if (peerConnections.current.has(remoteUserId)) {
                const existingPC = peerConnections.current.get(remoteUserId);
                existingPC.close();
                peerConnections.current.delete(remoteUserId);
            }

            const pc = createPeerConnection(configuration);

            pc.onicecandidate = (event) => {
                if (event.candidate && wsService.current) {
                    wsService.current.sendIceCandidate(event.candidate);
                }
            };

            pc.ontrack = (event) => {
                const [remoteStream] = event.streams;
                setRemoteStreams(prev => new Map(prev.set(remoteUserId, remoteStream)));
            };

            pc.oniceconnectionstatechange = () => {
                console.log(`ICE connection state for ${remoteUserId}:`, pc.iceConnectionState);
                if (pc.iceConnectionState === 'failed' || pc.iceConnectionState === 'disconnected') {
                    console.warn('ICE connection failed or disconnected');
                }
            };

            if (localStream) {
                addStreamToPeerConnection(pc, localStream);
            }

            peerConnections.current.set(remoteUserId, pc);
            return pc;
        } catch (error) {
            console.error('Error initializing peer connection:', error);
            setError(error.message);
            return null;
        }
    }, [localStream, configuration]);

    const handleOffer = useCallback(async (message) => {
        console.log('=== Start handling offer ===');
        console.log('Offer message:', message);

        if (!message.userId || !message.data?.sdp) {
            console.error('Invalid offer message received');
            return;
        }

        // 자신이 보낸 offer는 무시
        if (message.userId === userId) {
            console.log('Ignoring offer from self');
            return;
        }

        try {
            let pc = peerConnections.current.get(message.userId);
            const currentSignalingState = pc?.signalingState;
            console.log('Current signaling state:', currentSignalingState);

            // 기존 연결이 있을 경우 처리
            if (pc) {
                if (pc.signalingState !== 'stable') {
                    console.log('Existing connection not stable, cleaning up...');
                    pc.close();
                    peerConnections.current.delete(message.userId);
                    pc = null;
                }
            }

            // 새로운 연결 생성
            if (!pc) {
                pc = await initializePeerConnection(message.userId);
                if (!pc) {
                    throw new Error('Failed to create peer connection');
                }
            }

            // Remote Description 설정
            if (pc.signalingState === 'stable' || pc.signalingState === 'have-local-offer') {
                console.log('Setting remote description (offer)');
                await pc.setRemoteDescription(new RTCSessionDescription(message.data.sdp));

                // Answer 생성 및 전송
                console.log('Creating and sending answer');
                const answer = await createAnswer(pc);
                await pc.setLocalDescription(answer);

                if (wsService.current) {
                    wsService.current.sendAnswer(answer);
                }

                // 저장된 ICE candidate 처리
                const candidates = pendingCandidates.current.get(message.userId);
                if (candidates) {
                    console.log('Processing pending candidates:', candidates.length);
                    for (const candidate of candidates) {
                        await pc.addIceCandidate(new RTCIceCandidate(candidate));
                    }
                    pendingCandidates.current.delete(message.userId);
                }
            } else {
                console.log('Ignoring offer in state:', pc.signalingState);
            }

        } catch (error) {
            console.error('Error handling offer:', error);
            setError(error.message);

            // 에러 발생 시 연결 정리
            const pc = peerConnections.current.get(message.userId);
            if (pc) {
                pc.close();
                peerConnections.current.delete(message.userId);
            }
        }
    }, [userId, initializePeerConnection]);


    const handleAnswer = useCallback(async (message) => {
        console.log('=== Start handling answer ===');
        console.log('Answer message:', message);

        if (!message.userId || !message.data?.sdp) {
            console.error('Invalid answer message received');
            return;
        }

        if (message.userId === userId) {
            console.log('Ignoring answer from self');
            return;
        }

        try {
            const pc = peerConnections.current.get(message.userId);
            if (!pc) {
                console.error('No peer connection found for:', message.userId);
                return;
            }

            console.log('Current signaling state:', pc.signalingState);

            // have-local-offer 상태에서만 answer를 처리
            if (pc.signalingState === 'have-local-offer') {
                console.log('Setting remote description (answer)');
                await pc.setRemoteDescription(new RTCSessionDescription(message.data.sdp));

                // 저장된 ICE candidate 처리
                const candidates = pendingCandidates.current.get(message.userId);
                if (candidates) {
                    console.log('Processing pending candidates:', candidates.length);
                    for (const candidate of candidates) {
                        await pc.addIceCandidate(new RTCIceCandidate(candidate));
                    }
                    pendingCandidates.current.delete(message.userId);
                }
            } else {
                console.log('Ignoring answer in state:', pc.signalingState);
            }
        } catch (error) {
            console.error('Error handling answer:', error);
            setError(error.message);
        }
    }, [userId]);


    const handleIceCandidate = useCallback(async (message) => {
        console.log('=== Start handling ICE candidate ===');
        console.log('ICE candidate message:', message);

        if (!message.userId || !message.data) {
            console.error('Invalid ICE candidate message received');
            return;
        }
        // 자신이 보낸 candidate는 무시
        if (message.userId === userId) {
            console.log('Ignoring ICE candidate from self');
            return;
        }
        try {
            const pc = peerConnections.current.get(message.userId);
            if (!pc) {
                console.log('No peer connection found, storing candidate');
                if (!pendingCandidates.current.has(message.userId)) {
                    pendingCandidates.current.set(message.userId, []);
                }
                pendingCandidates.current.get(message.userId).push(message.data);
                return;
            }

            if (pc.remoteDescription && pc.remoteDescription.type) {
                await pc.addIceCandidate(new RTCIceCandidate(message.data));
                console.log('ICE candidate added successfully');
            } else {
                console.log('Remote description not set, storing candidate');
                if (!pendingCandidates.current.has(message.userId)) {
                    pendingCandidates.current.set(message.userId, []);
                }
                pendingCandidates.current.get(message.userId).push(message.data);
            }
        } catch (error) {
            console.error('Error handling ICE candidate:', error);
            setError(error.message);
        }
        console.log('=== End handling ICE candidate ===');
    }, [userId]);

    const handleUserJoined = useCallback(async (message) => {
        console.log('=== Start handling user joined ===');
        console.log('User joined message:', message);

        if (!message.userId) {
            console.error('Invalid user joined message received');
            return;
        }

        if (message.userId === userId) {
            console.log('Ignoring join from self');
            return;
        }

        try {
            const pc = await initializePeerConnection(message.userId);
            if (!pc) {
                throw new Error('Failed to create peer connection');
            }

            // 기존 연결이 stable 상태일 때만 offer 생성
            if (pc.signalingState === 'stable') {
                console.log('Creating and sending offer');
                const offer = await createOffer(pc);
                await pc.setLocalDescription(offer);

                if (wsService.current) {
                    wsService.current.sendOffer(offer);
                }
            } else {
                console.log('Not creating offer in state:', pc.signalingState);
            }
        } catch (error) {
            console.error('Error handling user joined:', error);
            setError(error.message);
        }
    }, [initializePeerConnection, userId]);

    const handleUserLeft = useCallback((message) => {
        if (!message.userId) {
            console.error('Invalid user left message received');
            return;
        }

        const pc = peerConnections.current.get(message.userId);
        if (pc) {
            pc.close();
            peerConnections.current.delete(message.userId);
        }

        setRemoteStreams(prev => {
            const newStreams = new Map(prev);
            newStreams.delete(message.userId);
            return newStreams;
        });
    }, []);

    const cleanup = useCallback(() => {
        if (!mountedRef.current) return;

        try {
            if (localStream) {
                localStream.getTracks().forEach(track => track.stop());
            }

            peerConnections.current.forEach(pc => {
                pc.close();
            });
            peerConnections.current.clear();

            if (wsService.current) {
                wsService.current.disconnect();
                wsService.current = null;
            }

            setLocalStream(null);
            setRemoteStreams(new Map());
            setIsConnected(false);
            setError(null);
            initialized.current = false;
        } catch (error) {
            console.error('Cleanup error:', error);
        }
    }, []);

    const initialize = useCallback(async () => {
        if (!mountedRef.current || initialized.current) return;

        try {
            const stream = await getLocalStream();
            if (!mountedRef.current) {
                stream.getTracks().forEach(track => track.stop());
                return;
            }

            setLocalStream(stream);

            if (!wsService.current) {
                wsService.current = new WebSocketService(roomId, userId);

                wsService.current.on('join', handleUserJoined);
                wsService.current.on('offer', handleOffer);
                wsService.current.on('answer', handleAnswer);
                wsService.current.on('ice-candidate', handleIceCandidate);
                wsService.current.on('leave', handleUserLeft);

                await wsService.current.connect();
                initialized.current = true;
                setIsConnected(true);
                setError(null);
            }
        } catch (err) {
            console.error('Error initializing WebRTC:', err);
            setError(err.message);
            cleanup();
        }
    }, [roomId, userId, handleUserJoined, handleOffer, handleAnswer, handleIceCandidate, handleUserLeft, cleanup]);

    useEffect(() => {
        mountedRef.current = true;
        return () => {
            mountedRef.current = false;
            cleanup();
        };
    }, []);

    return {
        localStream,
        remoteStreams,
        isConnected,
        error,
        initialize,
        cleanup
    };
};