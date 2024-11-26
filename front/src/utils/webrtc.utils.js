export const getLocalStream = async () => {
    try {
        return await navigator.mediaDevices.getUserMedia({
            video: true,
            audio: true
        });
    } catch (error) {
        console.error('Error accessing media devices:', error);
        throw new Error('카메라/마이크 접근 권한이 필요합니다.');
    }
};

export const createPeerConnection = (configuration) => {
    try {
        const pc = new RTCPeerConnection(configuration);
        return pc;
    } catch (error) {
        console.error('Error creating peer connection:', error);
        throw new Error('P2P 연결 생성에 실패했습니다.');
    }
};

export const addStreamToPeerConnection = (pc, stream) => {
    try {
        stream.getTracks().forEach(track => {
            pc.addTrack(track, stream);
        });
    } catch (error) {
        console.error('Error adding stream to peer connection:', error);
        throw new Error('스트림 추가에 실패했습니다.');
    }
};

export const createOffer = async (peerConnection) => {
    try {
        console.log('Creating offer, current state:', peerConnection.signalingState);
        const offer = await peerConnection.createOffer({
            offerToReceiveAudio: true,
            offerToReceiveVideo: true
        });
        console.log('Offer created');
        return offer;
    } catch (error) {
        console.error('Error creating offer:', error);
        throw error;
    }
};

export const createAnswer = async (peerConnection) => {
    try {
        console.log('Creating answer, current state:', peerConnection.signalingState);
        const answer = await peerConnection.createAnswer({
            offerToReceiveAudio: true,
            offerToReceiveVideo: true
        });
        console.log('Answer created');
        return answer;
    } catch (error) {
        console.error('Error creating answer:', error);
        throw error;
    }
};